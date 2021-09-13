import java.io.*;
import java.util.ArrayList;

public class Table {

    public static void main(String[] args) throws IOException {

        ArrayList<ArrayList<String>> tokens = new ArrayList<>();

        File file = new File("1.txt");

        BufferedReader br = new BufferedReader(new FileReader(file));
        tokens = tokenizer(br);

        

        /*
        //DEBUGGING STATEMENT

        for(ArrayList<String> a : tokens){
            for(String s : a){
                System.out.println(s);
            }
        }
        */



    }

    static boolean IsWhiteSpace(char c){
        return c == ' ' || c == '\t' || c == '\r' || c == '\n';
    }

    static ArrayList<ArrayList<String>> tokenizer(BufferedReader br) throws IOException {

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
                    tokens.get(counter).add(currentString);
                    //System.out.println(currentString);
                    currentString = "";
                    //clear string

                }
            }
            tokens.get(counter).add(currentString);
            //System.out.println(currentString);
            currentString = "";
            counter++;
        }

        return tokens;

    }

}