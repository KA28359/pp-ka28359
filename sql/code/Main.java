import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        //System.out.println("args: " + Arrays.toString(args));
        String file = args[0];
        BufferedReader br = new BufferedReader(new FileReader(file));
        ArrayList<ArrayList<String>> tokens = new ArrayList<>();

        String st;
        String currentString = "";
        int counter = 0;
        while ((st = br.readLine()) != null) {
            tokens.add(new ArrayList<>());
            for (int i = 0; i < st.length(); i++) {
                if (!IsWhiteSpace(st.charAt(i))) {

                    String newVal = Character.toString(st.charAt(i));
                    currentString = currentString + newVal;


                }else{
                    while(IsWhiteSpace(st.charAt(i+1)) && i+1 < st.length()-1){ //skipping all white space
                        i++;
                    }
                    tokens.get(counter).add(currentString);
                    //System.out.println(currentString);
                    currentString = "";
                    //clear string

                }
            }
            if(!currentString.equals("")){
                tokens.get(counter).add(currentString);
            }

            //System.out.println(currentString);
            currentString = "";
            counter++;
        }
        tokens.removeIf(ArrayList::isEmpty);
        int size = tokens.get(0).size();
        for(ArrayList<String> l : tokens){
            if(l.size() != size){
                PrintWriter out = new PrintWriter(args[1]);
                out.print("NUM COLS ERROR");
                out.close();
                return;
            }
        }

        ArrayList<ArrayList<String>> cols = new ArrayList<>();

        for(int i = 0; i < tokens.get(0).size();i++) {
            cols.add(new ArrayList<>());
            for (ArrayList<String> a : tokens) {
                cols.get(i).add(a.get(i));
            }
        }

        //System.out.println(cols);
        //System.out.println(tokens);
        String sqlString = "";
        ArrayList<Integer> validCols = new ArrayList<>();
        for(ArrayList<String> a : cols){
            if(a.size() > 1){
                if(IsInt(a.get(1))){
                    sqlString = sqlString + a.get(0) + " INT,";
                    validCols.add(cols.indexOf(a));
                }else if(IsFloat(a.get(1))){
                    sqlString = sqlString + a.get(0) + " DOUBLE,";
                    validCols.add(cols.indexOf(a));
                }else{
                    sqlString = sqlString + a.get(0) + " VARCHAR(100),";
                }
            }else{
                PrintWriter out = new PrintWriter(args[1]);
                out.print("");
                out.close();
                return;
            }
        }
        if (sqlString.length() != 0) {
            sqlString = sqlString.substring(0, sqlString.length() - 1);
            sqlString = "create table t(" + sqlString + ")";
        }
        //System.out.println(sqlString);

        Connection conn = DriverManager.getConnection("jdbc:h2:~/pp","sa","");
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(sqlString);
        for(int i = 1; i < tokens.size(); i++){
            for(String s : tokens.get(i)){
                if(!IsFloat(s) && !IsInt(s)){
                    tokens.get(i).set(tokens.get(i).indexOf(s),"'"+s+"'");
                }
            }
            //System.out.println(tokens.get(i).toString().replace("[", "").replace("]", ""));
            stmt.executeUpdate("insert into t values("+tokens.get(i).toString().replace("[", "").replace("]", "")+")");
        }
//        System.out.println("sum("+cols.get(validCols.get(0)).get(0)+")");
        double currentSum = -1;
        int index = -1;
        for(int i = 0; i < validCols.size(); i++){

            ResultSet rs = stmt.executeQuery("select sum("+cols.get(validCols.get(i)).get(0)+") from t");
            while(rs.next()){
                String test = rs.getString("sum("+cols.get(validCols.get(i)).get(0)+")");
                if(IsInt(test)){
                    int val = rs.getInt("sum("+cols.get(validCols.get(i)).get(0)+")");
                    if (val > currentSum){
                        currentSum = val;
                        index = validCols.get(i);
                    }
                }else{
                    double val = rs.getDouble("sum("+cols.get(validCols.get(i)).get(0)+")");
                    if (val > currentSum){
                        currentSum = val;
                        index = validCols.get(i);
                    }
                }

            }


        }
        if(index == -1){
            PrintWriter out = new PrintWriter(args[1]);
            out.print("");
            out.close();
            stmt.executeUpdate("drop table t");
            conn.close();
            return;
        }
        //System.out.println(cols.get(index));
        stmt.executeUpdate("drop table t");
        conn.close();
        String output = "";
        for(int i = 0; i < cols.get(index).size();i++){
            output = output + cols.get(index).get(i) + "\n";
        }
        if (output.length() != 0) {
            output = output.substring(0, output.length() - 1);
        }
        //System.out.println(output);
        PrintWriter out = new PrintWriter(args[1]);
        out.print(output);
        out.close();
    }

    static boolean IsWhiteSpace(char c){
        return c == ' ' || c == '\t' || c == '\r' || c == '\n';
    }

    static boolean IsInt(String s){ //TRY TO CHANGE TO REGEX

        if(s.charAt(0) == '-' && s.length() == 1)return false;

        if(s.charAt(0) == '-' && s.length() > 1){
            for(int i = 1; i < s.length(); i++){

                if(s.charAt(i) < '0' || s.charAt(i) > '9')return false;

            }
        }else{

            for(int i = 0; i < s.length(); i++){

                if(s.charAt(i) < '0' || s.charAt(i) > '9')return false;

            }

        }
        return true;


    }

    static boolean IsFloat(String s){

        return s.matches("[+-]?[0-9]*(\\.)?[0-9]+");

    }
}
