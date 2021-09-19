import java.io.*;
import java.util.*;

public class Table {

    static Map<String,String> typeTracker = new HashMap<>();
    static boolean header = false;

    public static void main(String[] args) throws IOException {

//        for(String st : args){
//            System.out.println(st);
//        }

        boolean print = false;
        boolean sum = false;
        boolean action = false;
        boolean when = false;
        boolean update = false;
        boolean opFile = false;

        if(Objects.equals(args[0], "-header")){
            header = true;
        }

        ArrayList<ArrayList<String>> tokens;
        File opF = null;
        String argName;
        if(header) {
            argName = args[1];
        }else{
            argName = args[0];
        }
        if(Objects.equals(argName, "-print")){
            print = true;
        }else if(Objects.equals(argName, "-sum")){
            sum = true;
        }else if(Objects.equals(argName, "-action")){
            action = true;
        }else if(Objects.equals(argName, "-when")){
            when = true;
        }else if(Objects.equals(argName, "-update")){
            update = true;
        }else {
            opF = new File(args[0]);
            opFile = true;
        }

        File file;
        if(header) {
            file = new File(args[3]);
            if(action){
                if(args.length == 5){
                    file = new File(args[3]);
                } else {
                if (args.length == 4) {
                    file = new File(args[2]);
                }
                }
            }
        }else{
            file = new File(args[2]);
            if(action){
                if(args.length == 4){
                    file = new File(args[2]);
                } else {
                    if (args.length == 3) {
                        file = new File(args[1]);
                    }
                }
            }
        }
        if(opFile){
            file = new File(args[1]);
        }

        BufferedReader br = new BufferedReader(new FileReader(file));
        tokens = tokenizer(br);

        int sizeTest = tokens.get(0).size(); //checking if every row has the same number of cols
        for(ArrayList<String> a : tokens){
            if(a.size() != sizeTest){
                System.out.println("NUM COLS ERROR");
                return;
            }
        }


        TypeSeparator(tokens);

//        String argName;
//        if(header) {
//            argName = args[1];
//        }else{
//            argName = args[0];
//        }
        if(print){
            PrintArg(tokens,args);
        }else if(sum){
            SumArg(tokens,args);
        }else if(action){
            ActionArg(tokens,args);
        }else if(when){
            WhenArg(tokens,args);
        }else if(update){
            UpdateArg(tokens,args);
        }else if(opFile){
            OpFileArg(tokens,args,opF);
        }


//        //DEBUGGING STATEMENT
//
//        for(ArrayList<String> a : tokens){
//            for(String s : a){
//                System.out.print(s + " ");
//            }
//            System.out.print("\n");
//        }




    }

    static void PrintArg(ArrayList<ArrayList<String>> tokens, String[] args) throws IOException {

        String stringCols;
        File file;
        if(header) {
            stringCols = args[2];
            file = new File(args[4]);
        }else{
            stringCols = args[1];
            file = new File(args[3]);
        }
        PrintWriter p = new PrintWriter(file);
        p.print("");
        p.close();
        FileWriter f = new FileWriter(file);

        int numCols = 0;
        for(int i = 0; i < stringCols.length(); i++){
            if(stringCols.charAt(i) == ','){
                numCols++;
            }
        }
        String[] colVals = new String[numCols+1];

        if(stringCols.contains("-")){
            System.out.println("COL INDEX ERROR");
            f.write("COL INDEX ERROR");
            f.close();
            return;
        }

        int currentIndex = 0;
        int currentStart = 0;
        int currentEnd = 0;
        for(int i = 0; i < stringCols.length(); i++){
            if(IsInt(String.valueOf(stringCols.charAt(i)))){
                currentEnd++;
                if(i == stringCols.length()-1){
                    colVals[currentIndex] = stringCols.substring(currentStart,currentEnd);
                }
            }else if(stringCols.charAt(i) != ','){
                System.out.println("COL INDEX ERROR");
                f.write("COL INDEX ERROR");
                f.close();
                return;
            }else if(stringCols.charAt(i) == ','){
                colVals[currentIndex] = stringCols.substring(currentStart,currentEnd);
                currentIndex++;
                currentEnd = currentStart = i+1;
            }
        }


        for(String test : colVals){
            int currentCol = Integer.parseInt(String.valueOf(test));
            if(currentCol > tokens.get(0).size()-1){
                System.out.println("COL INDEX ERROR");
                f.write("COL INDEX ERROR");
                f.close();
                return;
            }
        }

        ArrayList<Integer> sorted = new ArrayList<>();
        for(int i = 0; i < colVals.length; i++){
            sorted.add(Integer.valueOf(colVals[i]));
        }

        Collections.sort(sorted);

        ArrayList<String> currentList;
        int counter = 0;
        for (ArrayList<String> token : tokens) {

            currentList = token;

            for (Integer colVal : sorted) {
                //int currentCol = colVal;
                //System.out.print(currentList.get(currentCol) + " ");
                f.write(currentList.get(colVal) + " ");
            }
            if(counter++ != token.size()){
                //System.out.print("\n");
                f.write("\n");
            }

        }

        f.close();

    }


    static void SumArg(ArrayList<ArrayList<String>> tokens, String[] args) throws IOException {

        String colNum;
        boolean intException = false;
        boolean floatException = false;
        boolean containsInts = false;
        boolean containsFloats = false;
        File file;
        ArrayList<String> headers = null;
        if(header) {
            headers = tokens.get(0);
            colNum = args[2];
            file = new File(args[4]);
            tokens.remove(0);
        }else{
            colNum = args[1];
            file = new File(args[3]);
        }

        PrintWriter p = new PrintWriter(file);
        p.print("");
        p.close();
        FileWriter f = new FileWriter(file);

        if(colNum.contains("-")){
            System.out.println("COL INDEX ERROR");
            f.write("COL INDEX ERROR");
            f.close();
            return;
        }

        if(!IsInt(colNum)){
            System.out.println("COL INDEX ERROR");
            f.write("COL INDEX ERROR");
            f.close();
            return;
        }

        int intColNum = Integer.parseInt(colNum);

        if(intColNum > tokens.get(0).size()-1){
            System.out.println("COL INDEX ERROR");
            f.write("COL INDEX ERROR");
            f.close();
            return;
        }

        int sum = 0;
        float fSum = 0;
        for(ArrayList<String> a : tokens){

            try {
                int currentVal = Integer.parseInt(a.get(intColNum));
                sum = sum + currentVal;
                containsInts = true;

            }catch(NumberFormatException e){
                intException = true;
            }
            if(intException){

                try{
                    float currentVal = Float.parseFloat(a.get(intColNum));
                    fSum = fSum + currentVal;
                    containsFloats = true;

                }catch(NumberFormatException e){
                    floatException = true;
                }

            }

            if(floatException && intException){
                System.out.println("TYPE ERROR");
                f.write("TYPE ERROR");
                f.close();
                return;
            }


        }

        if(headers != null && header){
            tokens.add(0,headers);
        }
        if(containsInts && !containsFloats){
            //System.out.println(String.valueOf(sum));
            f.write(String.valueOf(sum));
            f.close();
        }else if(!containsInts && containsFloats){
            //System.out.println(String.valueOf(fSum));
            f.write(String.valueOf(fSum));
            f.close();
        }else if(containsInts && containsFloats){
            //System.out.println((float)sum + fSum);
            f.write(String.valueOf((float)sum + fSum));
            f.close();
        }


    }


    static void ActionArg(ArrayList<ArrayList<String>>tokens, String[] args) throws IOException {// TO DO: headers

        boolean containsCols = false;

        HashMap<String, Integer> stringToNode = new HashMap<>();
        File file;
        ArrayList<String> headers = null;
        if(header){
            headers = tokens.get(0);
            tokens.remove(0);
            if(args.length == 5){
                containsCols = true;
            }
        }else{
            if(args.length == 4){
                containsCols = true;
            }
        }

        FileWriter f;
        String stringCols;
        String[] colVals; //keeps track of cols wanted
        if(containsCols){ //get certain cols

            if(header) {
                stringCols = args[2];
                file = new File(args[4]);
            }else{
                stringCols = args[1];
                file = new File(args[3]);
            }

            PrintWriter p = new PrintWriter(file);
            p.print("");
            p.close();
            f = new FileWriter(file);

            int numCols = 0;
            for(int i = 0; i < stringCols.length(); i++){
                if(stringCols.charAt(i) == ','){
                    numCols++;
                }
            }
            colVals = new String[numCols+1];

            if(stringCols.contains("-")){
                System.out.println("COL INDEX ERROR");
                f.write("COL INDEX ERROR");
                f.close();
                return;
            }

            int currentIndex = 0;
            int currentStart = 0;
            int currentEnd = 0;
            for(int i = 0; i < stringCols.length(); i++){
                if(IsInt(String.valueOf(stringCols.charAt(i)))){
                    currentEnd++;
                    if(i == stringCols.length()-1){
                        colVals[currentIndex] = stringCols.substring(currentStart,currentEnd);
                    }
                }else if(stringCols.charAt(i) != ','){
                    System.out.println("COL INDEX ERROR");
                    f.write("COL INDEX ERROR");
                    f.close();
                    return;
                }else if(stringCols.charAt(i) == ','){
                    colVals[currentIndex] = stringCols.substring(currentStart,currentEnd);
                    currentIndex++;
                    currentEnd = currentStart = i+1;
                }
            }
        }else{ //all cols

            if(header){
                file = new File(args[3]);
            }else{
                file = new File(args[2]);
            }

            PrintWriter p = new PrintWriter(file);
            p.print("");
            p.close();
            f = new FileWriter(file);

            int size = tokens.get(0).size();

            colVals = new String[size];

            for(int i = 0; i < size; i++){

                colVals[i] = String.valueOf(i);

            }

        }

        //System.out.println(colVals.length);
        int[] currentArray;
        int currentNodeNum = 0;
        for(int i = 0; i < tokens.size(); i++){
            for(int j = 0; j < tokens.get(0).size(); j++){

                currentArray = new int[2];
                currentArray[0] = i;
                currentArray[1] = j;
                stringToNode.put(Arrays.toString(currentArray),currentNodeNum);
                currentNodeNum++;

            }
        }

        int totalRows = tokens.size();
        ArrayList<Node> nodeTracker = new ArrayList<>();
        for(int i = 0; i < totalRows; i++){

            for(int j = 0; j < tokens.get(0).size(); j++){

                //int currentCol = Integer.parseInt(colVals[j]);

                if(!IsAction(tokens.get(i).get(j))){
                    //System.out.print(tokens.get(i).get(currentCol) + " ");

                }else{ // it is an action
                    //System.out.print(tokens.get(i).get(j) + " ");

                    String inside = tokens.get(i).get(j).replaceAll("[^0-9'\\,']", "");
                    ArrayList<int[]> listOfPoints = new ArrayList<>();

                    if(inside.length() == 3){
                        int[] p = new int[2];
                        p[0] = Integer.parseInt(inside.substring(0,1));
                        p[1] = Integer.parseInt(inside.substring(2,3));
                        listOfPoints.add(p);
                    }else {

                        int firstXVal = 0;
                        int firstYVal = 1;
                        int secondXVal = 2;
                        int secondYVal = 3;
                        while(secondYVal <= inside.length()){

                            int[] p = new int[2];
                            p[0] = Integer.parseInt(inside.substring(firstXVal,firstYVal));
                            p[1] = Integer.parseInt(inside.substring(secondXVal,secondYVal));
                            listOfPoints.add(p);
                            firstXVal = firstXVal+4;
                            firstYVal = firstYVal+4;
                            secondYVal = secondYVal+4;
                            secondXVal = secondXVal+4;

                        }

                    }

                    //ArrayList<Node> nodeTracker = new ArrayList<>();

                    for(int[] list : listOfPoints){

                        int[] arrayKey = new int[2];
                        arrayKey[0] = i;
                        arrayKey[1] = j;
                        int getNodeNum = stringToNode.get(Arrays.toString(arrayKey));
                        int getEndNum = stringToNode.get(Arrays.toString(list));
                        Node n = new Node(getNodeNum,getEndNum);
                        nodeTracker.add(n);

                    }



                }

            }


        }
        Graph g = new Graph(tokens.size()*tokens.get(0).size(),nodeTracker);
        if(!ValidDag(tokens.size()*tokens.get(0).size(), g)){
            System.out.println("DAG ERROR");
            f.write("DAG ERROR");
            f.close();
            return;
        }

        for(int i = 0; i < tokens.size(); i++){

            for(int j = 0; j < colVals.length; j++){

                int currentCol = Integer.parseInt(colVals[j]);

                String currentVal = tokens.get(i).get(currentCol);

                if(IsAction(currentVal)){

                    //String input = currentVal.replaceAll("\\(\\[.*?\\]\\)(\\,)*", "");
                    String input = currentVal .replaceAll("[^a-z]", "");
                    switch (input) {
                        case "sum":

                            //System.out.println("sum");
                            float totalSum = CalculateSum(tokens,currentVal, f);

                            if(totalSum % 1 == 0){
                                //System.out.println((int)totalSum);
                                tokens.get(i).set(currentCol,String.valueOf((int)totalSum));
                            }else{
                                //System.out.println(totalSum);
                                tokens.get(i).set(currentCol,String.valueOf(totalSum));
                            }

                            break;
                        case "avg":

                            float avg = CalculateAvg(tokens,currentVal, f);
                            if(avg % 1 == 0){
                                //System.out.println((int)avg);
                                tokens.get(i).set(currentCol,String.valueOf((int)avg));
                            }else{
                                //System.out.println(avg);
                                tokens.get(i).set(currentCol,String.valueOf(avg));
                            }

                            break;
                        case "toupper":

                            String upperVersion = ToUpper(tokens,currentVal);
                            if(upperVersion == null){
                                f.write("TYPE ERROR");
                                f.close();
                                return;
                            }
                            //System.out.println(upperVersion);
                            tokens.get(i).set(currentCol,upperVersion);


                            break;
                        case "tolower":

                            String lowerVersion = ToLower(tokens,currentVal);
                            if(lowerVersion == null){
                                f.write("TYPE ERROR");
                                f.close();
                                return;
                            }
                            //System.out.println(lowerVersion);
                            tokens.get(i).set(currentCol,lowerVersion);
                            break;
                    }

                }

                //System.out.print(tokens.get(i).get(currentCol) + " ");

            }

            //System.out.print("\n");

        }

        //System.out.println(tokens);
        if(header && headers != null){
            tokens.add(0,headers);
        }
        for(int i = 0; i < tokens.size(); i++){
            for(int j = 0; j < colVals.length; j++){

                int currentVal = Integer.parseInt(colVals[j]);
                f.write(tokens.get(i).get(currentVal) + " ");


            }
            if(i != tokens.size()-1){
                f.write("\n");
            }
        }
        f.close();

    }

    static void WhenArg(ArrayList<ArrayList<String>> tokens, String[] args) throws IOException { //headers

        ArrayList<String> values = new ArrayList<>();
        File file;
        String cond;
        ArrayList<String> headers = null;
        if(header){
            headers = tokens.get(0);
            tokens.remove(0);
            cond = args[2];
            file = new File(args[4]);
        }else{
            cond = args[1];
            file = new File(args[3]);
        }

        PrintWriter p = new PrintWriter(file);
        p.print("");
        p.close();
        FileWriter f = new FileWriter(file);

        //String test = cond.replaceAll("['\\(']['\\$']?[a-zA-Z0-9]+(<|==|>|<>)", "");
        String test = cond.replaceAll("['\\(']['\\$']?[a-zA-Z0-9_]+(<|==|>|<>)['\\$']?[a-zA-Z0-9_]+['\\)'](['\\&' | '\\|']*['\\(']['\\$']?[a-zA-Z0-9_]+(<|==|>|<>)['\\$']?[a-zA-Z0-9_]+['\\)'])*", "");
        //String test = cond.replaceAll("\\(\\$?[a-zA-Z0-9]+", "");
        //System.out.println(test);

        if(!test.equals("")){
            System.out.println("COND ERROR");
            f.write("COND ERROR");
            f.close();
            return;
        }

        int startChar = 0;
        int endChar;
        for(int i = 0; i < cond.length(); i++){

            String currentChar = String.valueOf(cond.charAt(i));
            if(currentChar.equals("(")){
                startChar = i+1;
            }else if(currentChar.equals(")")){
                endChar = i-1;
                values.add(cond.substring(startChar,endChar+1));
            }else if(currentChar.equals("&") && String.valueOf(cond.charAt(i+1)).equals("&")){
                values.add(cond.substring(i,i+2));
            }else if(currentChar.equals("|") && String.valueOf(cond.charAt(i+1)).equals("|")){
                values.add(cond.substring(i,i+2));
            }


        }

        if(!values.contains("&&") && !values.contains("||")){

            ArrayList<Integer> validRows = new ArrayList<>();

            DoSolo(tokens, values, validRows, headers);

        }

        while(values.contains("&&") || values.contains("||")){

            int andIndex = -1;
            int orIndex = -1;
            if(values.contains("&&")){
                andIndex = values.indexOf("&&");
            }
            if(values.contains("||")){
                orIndex = values.indexOf("||");
            }

            if(andIndex != -1 && orIndex != -1){

                if(andIndex < orIndex){
                    ArrayList<Integer> validRows = new ArrayList<>();

                    DoAnds(tokens, values, validRows, headers);
                }else if(andIndex > orIndex){
                    ArrayList<Integer> vRows = new ArrayList<>();

                    DoOrs(tokens, values, vRows, headers);
                }

            }else if(andIndex != -1 && orIndex == -1){
                ArrayList<Integer> validRows = new ArrayList<>();

                DoAnds(tokens, values, validRows, headers);

            }else if(orIndex != -1 && andIndex == -1){
                ArrayList<Integer> vRows = new ArrayList<>();

                DoOrs(tokens, values, vRows, headers);

            }



//            if(values.contains("&&")) {
//                ArrayList<Integer> validRows = new ArrayList<>();
//
//                DoAnds(tokens, values, validRows, headers);
//            }
//            if(values.contains("||")) {
//                ArrayList<Integer> vRows = new ArrayList<>();
//
//                DoOrs(tokens, values, vRows, headers);
//            }
        }


        String printRows = values.get(0);
        printRows = printRows.replaceAll("[\\[ \\]]", "");
        ArrayList<Integer> rowsAsInts = new ArrayList<>();
        for(int i = 0; i < printRows.length(); i++){
            if(IsInt(String.valueOf(printRows.charAt(i)))){
                rowsAsInts.add(Integer.parseInt(String.valueOf(printRows.charAt(i))));
            }
        }
        for(int i = 0; i < rowsAsInts.size(); i++){
            for(int j = 0; j < tokens.get(0).size(); j++){

                //System.out.print(tokens.get(rowsAsInts.get(i)).get(j) + " ");
                f.write(tokens.get(rowsAsInts.get(i)).get(j) + " ");

            }
            if(i != rowsAsInts.size()-1){

                //System.out.print("\n");
                f.write("\n");

            }

        }
        f.close();
        tokens.add(0,headers);

    }

    static void UpdateArg(ArrayList<ArrayList<String>> tokens, String[] args) throws IOException { // TO DO: headers

        String rowColVal;
        File file;
        ArrayList<String> headers = null;
        if(header){
            headers = tokens.get(0);
            tokens.remove(0);
            rowColVal = args[2];
            file = new File(args[4]);
        }else{
            rowColVal = args[1];
            file = new File(args[3]);
        }

        PrintWriter p = new PrintWriter(file);
        p.print("");
        p.close();
        FileWriter f = new FileWriter(file);

        int row = -1;
        int col = -1;
        String val = "";
        int startVal = 0;
        boolean rowDone = false;
        boolean colDone = false;
        for(int i = 0; i < rowColVal.length(); i++){

            if(rowColVal.charAt(i) == ','){

                if(!rowDone){
                    row = Integer.parseInt(rowColVal.substring(startVal,i));
                    startVal = i+1;
                    rowDone = true;
                }else if(!colDone){
                    col = Integer.parseInt(rowColVal.substring(startVal,i));
                    val = rowColVal.substring(i+1,rowColVal.length());
                    colDone = true;
                }

            }

        }

//        System.out.println(row);
//        System.out.println(col);
//        System.out.println(val);
        if(row>tokens.size()-1 || col > tokens.get(0).size()-1){
            System.out.println("INDEX ERROR");
            f.write("INDEX ERROR");
            f.close();
            return;
        }

        tokens.get(row).set(col,val);

        tokens.add(0,headers);
        int counter = 0;
        for(ArrayList<String> a : tokens){
            for(String s : a){
                //System.out.print(s + " ");
                f.write(s + " ");
            }
            if(counter++ != a.size()){
                //System.out.print("\n");
                f.write("\n");
            }

        }
        f.close();

    }

    static void OpFileArg(ArrayList<ArrayList<String>> tokens, String[] args, File opF) throws IOException {

        BufferedReader br;
        try
        {

            br = new BufferedReader(new FileReader(opF));
        }
        catch(FileNotFoundException e)
        {
            System.out.println("OTHER ERROR");
            return;
        }

        String st;
        while((st = br.readLine()) != null){

            String[] sArray = st.split(" ");

            String[] newArgs = new String[args.length + sArray.length - 1];


            //int currentIndex = 0;
            for(int i = 0; i < sArray.length; i++){

                newArgs[i] = sArray[i];

            }
            newArgs[newArgs.length-2] = args[1];
            newArgs[newArgs.length-1] = args[2];

            if(Objects.equals(newArgs[0], "-header")){
                header = true;
            }

            if(!header){
                if(Objects.equals(sArray[0], "-print")){
                    PrintArg(tokens,newArgs);
                }else if(Objects.equals(sArray[0], "-sum")){
                    SumArg(tokens,newArgs);
                }else if(Objects.equals(sArray[0], "-action")){
                    ActionArg(tokens,newArgs);
                }else if(Objects.equals(sArray[0], "-when")){
                    WhenArg(tokens,newArgs);
                }else if(Objects.equals(sArray[0], "-update")){
                    UpdateArg(tokens,newArgs);
                }
            }else{
                if(Objects.equals(sArray[1], "-print")){
                    PrintArg(tokens,newArgs);
                }else if(Objects.equals(sArray[1], "-sum")){
                    SumArg(tokens,newArgs);
                }else if(Objects.equals(sArray[1], "-action")){
                    ActionArg(tokens,newArgs);
                }else if(Objects.equals(sArray[1], "-when")){
                    WhenArg(tokens,newArgs);
                }else if(Objects.equals(sArray[1], "-update")){
                    UpdateArg(tokens,newArgs);
                }
            }


           //System.out.println(line);

        }


    }

    static void DoSolo(ArrayList<ArrayList<String>> tokens, ArrayList<String> values, ArrayList<Integer> validRows, ArrayList<String> headers){


            String val;

            val = values.get(0);


            int endingIndex = 0;

            String colIdent;
            String constantVal;

            for(int i = 0; i < val.length(); i++){

                if(val.charAt(i) == '<' || val.charAt(i) == '>' || val.charAt(i) == '='){
                    endingIndex = i;
                    break;
                }

            }

        if(!val.contains("$")){

            boolean dne = false;
            boolean eq = false;
            boolean gt = false;
            boolean lt = false;
            boolean trueSt = false;


            if(val.charAt(endingIndex+1) == '>' || val.charAt(endingIndex+1) == '='){
                if(val.charAt(endingIndex) == '='){
                    eq = true;
                }else{
                    dne = true;
                }
            }else{
                if(val.charAt(endingIndex) == '>'){
                    gt = true;
                }else{
                    lt = true;
                }
            }

            String firstVal = val.substring(0,endingIndex);
            String secondVal;
            if(eq || dne){
                secondVal = val.substring(endingIndex+2,val.length());
            }else{
                secondVal = val.substring(endingIndex+1,val.length());
            }
            if(eq){
                if(firstVal.equals(secondVal)){
                    trueSt = true;
                }

            }else if(dne){
                if(!firstVal.equals(secondVal)){
                    trueSt = true;
                }
            }else if(gt){

                if(IsInt(firstVal)){
                    if(IsInt(secondVal)){
                        if(Integer.parseInt(firstVal) > Integer.parseInt(secondVal)){
                            trueSt = true;
                        }
                    }else if(IsFloat(secondVal)){
                        if(Integer.parseInt(firstVal) > Float.parseFloat(secondVal)){
                            trueSt = true;
                        }
                    }
                }else if(IsFloat(firstVal)){
                    if(IsInt(secondVal)){
                        if(Float.parseFloat(firstVal) > Integer.parseInt(secondVal)){
                            trueSt = true;
                        }
                    }else if(IsFloat(secondVal)){
                        if(Float.parseFloat(firstVal) > Float.parseFloat(secondVal)){
                            trueSt = true;
                        }
                    }
                }
            }else if(lt){

                if(IsInt(firstVal)){
                    if(IsInt(secondVal)){
                        if(Integer.parseInt(firstVal) < Integer.parseInt(secondVal)){
                            trueSt = true;
                        }
                    }else if(IsFloat(secondVal)){
                        if(Integer.parseInt(firstVal) < Float.parseFloat(secondVal)){
                            trueSt = true;
                        }
                    }
                }else if(IsFloat(firstVal)){
                    if(IsInt(secondVal)){
                        if(Float.parseFloat(firstVal) < Integer.parseInt(secondVal)){
                            trueSt = true;
                        }
                    }else if(IsFloat(secondVal)){
                        if(Float.parseFloat(firstVal) < Float.parseFloat(secondVal)){
                            trueSt = true;
                        }
                    }
                }
            }

            if(trueSt){
                for(int i = 0; i < tokens.size(); i++){

                    validRows.add(i);

                }
            }


            values.set(0,validRows.toString());


            return;
        }

            boolean dne = false;
            boolean eq = false;
            boolean gt = false;
            boolean lt = false;
            boolean colOnLeft = false;

            if(val.charAt(0) == '$'){ //first argument is col num or name

                colOnLeft = true;

                colIdent = val.substring(1,endingIndex);
                if(val.charAt(endingIndex+1) == '>' || val.charAt(endingIndex+1) == '='){
                    if(val.charAt(endingIndex) == '='){
                        eq = true;
                    }else{
                        dne = true;
                    }
                    constantVal = val.substring(endingIndex+2,val.length());
                }else{
                    if(val.charAt(endingIndex) == '>'){
                        gt = true;
                    }else{
                        lt = true;
                    }
                    constantVal = val.substring(endingIndex+1,val.length());
                }

            }else{ //first argument is constant
                colOnLeft = false;
                constantVal = val.substring(0,endingIndex);
                if(val.charAt(endingIndex+1) == '>' || val.charAt(endingIndex+1) == '='){
                    if(val.charAt(endingIndex) == '='){
                        eq = true;
                    }else{
                        dne = true;
                    }
                    colIdent = val.substring(endingIndex+3,val.length());
                }else{
                    if(val.charAt(endingIndex) == '>'){
                        gt = true;
                    }else{
                        lt = true;
                    }
                    colIdent = val.substring(endingIndex+2,val.length());
                }
            }

            if(header){

                if(!IsInt(colIdent) && headers.contains(colIdent)){
                    int index = headers.indexOf(colIdent);
                    colIdent = String.valueOf(index);
                }else if(!IsInt(colIdent) && !headers.contains(colIdent)){
                    System.out.println("INDEX ERROR");
                    return;
                }

            }

            //System.out.println(colIdent);
            //System.out.println(constantVal);

            for(int i = 0; i < tokens.size(); i++){

                if(eq) {
                    if (tokens.get(i).get(Integer.parseInt(colIdent)).equals(constantVal)) {
                        //System.out.println(i + " " + colIdent);
                        validRows.add(i);
                    }
                }else if(dne){
                    if (!tokens.get(i).get(Integer.parseInt(colIdent)).equals(constantVal)) {
                        //System.out.println(i + " " + colIdent);
                        validRows.add(i);
                    }
                }else if(gt){

                    if(colOnLeft){//ex $1>5

                        if((IsInt(tokens.get(i).get(Integer.parseInt(colIdent))) || IsFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) &&
                                (IsInt(constantVal)) || IsFloat(constantVal)){

                            if(IsInt(tokens.get(i).get(Integer.parseInt(colIdent)))){ //value in table is int

                                if(IsInt(constantVal)){ //both values are integers
                                    if (Integer.parseInt(constantVal) < Integer.parseInt(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }else if(IsFloat(constantVal)){ //value from terminal is float, value in table is int
                                    if (Float.parseFloat(constantVal) < Integer.parseInt(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }

                            }else if(IsFloat(tokens.get(i).get(Integer.parseInt(colIdent)))){ //value in table is float
                                if(IsInt(constantVal)){ //value in table is float, value in terminal is int
                                    if (Integer.parseInt(constantVal) < Float.parseFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }else if(IsFloat(constantVal)){ //both are floats
                                    if (Float.parseFloat(constantVal) < Float.parseFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }
                            }

                        }

                    }else{//ex 5>$1

                        if((IsInt(tokens.get(i).get(Integer.parseInt(colIdent))) || IsFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) &&
                                (IsInt(constantVal)) || IsFloat(constantVal)){

                            if(IsInt(tokens.get(i).get(Integer.parseInt(colIdent)))){ //value in table is int

                                if(IsInt(constantVal)){ //both values are integers
                                    if (Integer.parseInt(constantVal) > Integer.parseInt(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }else if(IsFloat(constantVal)){ //value from terminal is float, value in table is int
                                    if (Float.parseFloat(constantVal) > Integer.parseInt(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }

                            }else if(IsFloat(tokens.get(i).get(Integer.parseInt(colIdent)))){ //value in table is float
                                if(IsInt(constantVal)){ //value in table is float, value in terminal is int
                                    if (Integer.parseInt(constantVal) > Float.parseFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }else if(IsFloat(constantVal)){ //both are floats
                                    if (Float.parseFloat(constantVal) > Float.parseFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }
                            }

                        }

                    }

                }else if(lt){

                    if(colOnLeft){ //ex $1<5

                        if((IsInt(tokens.get(i).get(Integer.parseInt(colIdent))) || IsFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) &&
                                (IsInt(constantVal)) || IsFloat(constantVal)){

                            if(IsInt(tokens.get(i).get(Integer.parseInt(colIdent)))){ //value in table is int

                                if(IsInt(constantVal)){ //both values are integers
                                    if (Integer.parseInt(constantVal) > Integer.parseInt(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }else if(IsFloat(constantVal)){ //value from terminal is float, value in table is int
                                    if (Float.parseFloat(constantVal) > Integer.parseInt(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }

                            }else if(IsFloat(tokens.get(i).get(Integer.parseInt(colIdent)))){ //value in table is float
                                if(IsInt(constantVal)){ //value in table is float, value in terminal is int
                                    if (Integer.parseInt(constantVal) > Float.parseFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }else if(IsFloat(constantVal)){ //both are floats
                                    if (Float.parseFloat(constantVal) > Float.parseFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }
                            }

                        }

                    }else{//ex 5<$1

                        if((IsInt(tokens.get(i).get(Integer.parseInt(colIdent))) || IsFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) &&
                                (IsInt(constantVal)) || IsFloat(constantVal)){

                            if(IsInt(tokens.get(i).get(Integer.parseInt(colIdent)))){ //value in table is int

                                if(IsInt(constantVal)){ //both values are integers
                                    if (Integer.parseInt(constantVal) < Integer.parseInt(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }else if(IsFloat(constantVal)){ //value from terminal is float, value in table is int
                                    if (Float.parseFloat(constantVal) < Integer.parseInt(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }

                            }else if(IsFloat(tokens.get(i).get(Integer.parseInt(colIdent)))){ //value in table is float
                                if(IsInt(constantVal)){ //value in table is float, value in terminal is int
                                    if (Integer.parseInt(constantVal) < Float.parseFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }else if(IsFloat(constantVal)){ //both are floats
                                    if (Float.parseFloat(constantVal) < Float.parseFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }
                            }

                        }

                    }

                }

//                if(left){
//                    values.set(index-1,validRows.toString());
//                }else if(right){
//                    values.set(index+1,validRows.toString());
//                }

            }

            values.set(0,validRows.toString());




            //currentElem = currentElem+2;


    }


    static void DoAnds(ArrayList<ArrayList<String>> tokens, ArrayList<String> values, ArrayList<Integer> validRows, ArrayList<String> headers){

        while(values.contains("&&")){

            int index = values.indexOf("&&");
            boolean left = false;
            boolean right = false;
            String val;
            if(values.get(index+1).startsWith("[") && values.get(index-1).startsWith("[")){ //the and statement is finished, evaluate it

                String leftSide = values.get(index-1).replaceAll("['\\[''\\]']", "");
                String rightSide = values.get(index+1).replaceAll("['\\[''\\]']", "");

                ArrayList<Integer> leftInt = new ArrayList<>();
                for(int i = 0; i < leftSide.length(); i++){
                    if(IsInt(String.valueOf(leftSide.charAt(i)))){
                        leftInt.add(Integer.parseInt(String.valueOf(leftSide.charAt(i))));
                    }
                }
                ArrayList<Integer> rightInt = new ArrayList<>();
                for(int i = 0; i < rightSide.length(); i++){
                    if(IsInt(String.valueOf(rightSide.charAt(i)))){
                        rightInt.add(Integer.parseInt(String.valueOf(rightSide.charAt(i))));
                    }
                }

                Set<Integer> commonInts = new HashSet<>();
                for (int i = 0; i < leftInt.size(); i++){
                    for(int j = 0; j < rightInt.size(); j++){

                        if(Objects.equals(leftInt.get(i), rightInt.get(j))){
                            commonInts.add(leftInt.get(i));
                        }

                    }
                }

                ArrayList<Integer> andAnswer = new ArrayList<>(commonInts);

                values.set(index,andAnswer.toString());
                values.remove(index-1);
                values.remove(index);

                return;
                //continue;
            }
            if(values.get(index-1).startsWith("[")){//left side done, go to right
                right = true;
                val = values.get(index+1);
            }else{
                left = true;
                val = values.get(index-1);
            }

            //String val = values.get(currentElem);

            int startingIndex = 0;
            int endingIndex = 0;

            String colIdent;
            String constantVal;

            for(int i = 0; i < val.length(); i++){

                if(val.charAt(i) == '<' || val.charAt(i) == '>' || val.charAt(i) == '='){
                    endingIndex = i;
                    break;
                }

            }

            if(!val.contains("$")){

                boolean dne = false;
                boolean eq = false;
                boolean gt = false;
                boolean lt = false;
                boolean trueSt = false;


                if(val.charAt(endingIndex+1) == '>' || val.charAt(endingIndex+1) == '='){
                    if(val.charAt(endingIndex) == '='){
                        eq = true;
                    }else{
                        dne = true;
                    }
                }else{
                    if(val.charAt(endingIndex) == '>'){
                        gt = true;
                    }else{
                        lt = true;
                    }
                }

                String firstVal = val.substring(0,endingIndex);
                String secondVal;
                if(eq || dne){
                    secondVal = val.substring(endingIndex+2,val.length());
                }else{
                    secondVal = val.substring(endingIndex+1,val.length());
                }
                if(eq){
                    if(firstVal.equals(secondVal)){
                        trueSt = true;
                    }

                }else if(dne){
                    if(!firstVal.equals(secondVal)){
                        trueSt = true;
                    }
                }else if(gt){

                    if(IsInt(firstVal)){
                        if(IsInt(secondVal)){
                            if(Integer.parseInt(firstVal) > Integer.parseInt(secondVal)){
                                trueSt = true;
                            }
                        }else if(IsFloat(secondVal)){
                            if(Integer.parseInt(firstVal) > Float.parseFloat(secondVal)){
                                trueSt = true;
                            }
                        }
                    }else if(IsFloat(firstVal)){
                        if(IsInt(secondVal)){
                            if(Float.parseFloat(firstVal) > Integer.parseInt(secondVal)){
                                trueSt = true;
                            }
                        }else if(IsFloat(secondVal)){
                            if(Float.parseFloat(firstVal) > Float.parseFloat(secondVal)){
                                trueSt = true;
                            }
                        }
                    }
                }else if(lt){

                    if(IsInt(firstVal)){
                        if(IsInt(secondVal)){
                            if(Integer.parseInt(firstVal) < Integer.parseInt(secondVal)){
                                trueSt = true;
                            }
                        }else if(IsFloat(secondVal)){
                            if(Integer.parseInt(firstVal) < Float.parseFloat(secondVal)){
                                trueSt = true;
                            }
                        }
                    }else if(IsFloat(firstVal)){
                        if(IsInt(secondVal)){
                            if(Float.parseFloat(firstVal) < Integer.parseInt(secondVal)){
                                trueSt = true;
                            }
                        }else if(IsFloat(secondVal)){
                            if(Float.parseFloat(firstVal) < Float.parseFloat(secondVal)){
                                trueSt = true;
                            }
                        }
                    }
                }

                if(trueSt){
                    for(int i = 0; i < tokens.size(); i++){

                        validRows.add(i);

                    }
                }

                if(left){
                    values.set(index-1,validRows.toString());
                }else if(right){
                    values.set(index+1,validRows.toString());
                }
                validRows = new ArrayList<>();
                return;
                //continue;
            }

            boolean dne = false;
            boolean eq = false;
            boolean gt = false;
            boolean lt = false;
            boolean colOnLeft = false;

            if(val.charAt(0) == '$'){ //first argument is col num or name

                colOnLeft = true;

                colIdent = val.substring(1,endingIndex);
                if(val.charAt(endingIndex+1) == '>' || val.charAt(endingIndex+1) == '='){
                    if(val.charAt(endingIndex) == '='){
                        eq = true;
                    }else{
                        dne = true;
                    }
                    constantVal = val.substring(endingIndex+2,val.length());
                }else{
                    if(val.charAt(endingIndex) == '>'){
                        gt = true;
                    }else{
                        lt = true;
                    }
                    constantVal = val.substring(endingIndex+1,val.length());
                }

            }else{ //first argument is constant
                colOnLeft = false;
                constantVal = val.substring(0,endingIndex);
                if(val.charAt(endingIndex+1) == '>' || val.charAt(endingIndex+1) == '='){
                    if(val.charAt(endingIndex) == '='){
                        eq = true;
                    }else{
                        dne = true;
                    }
                    colIdent = val.substring(endingIndex+3,val.length());
                }else{
                    if(val.charAt(endingIndex) == '>'){
                        gt = true;
                    }else{
                        lt = true;
                    }
                    colIdent = val.substring(endingIndex+2,val.length());
                }
            }

            if(header){
                if(!IsInt(colIdent) && headers.contains(colIdent)){
                    int newIndex = headers.indexOf(colIdent);
                    colIdent = String.valueOf(newIndex);
                }else if(!IsInt(colIdent) && !headers.contains(colIdent)){
                    System.out.println("INDEX ERROR");
                    return;
                }
            }
            //System.out.println(colIdent);
            //System.out.println(constantVal);

            for(int i = 0; i < tokens.size(); i++){

                if(eq) {
                    if (tokens.get(i).get(Integer.parseInt(colIdent)).equals(constantVal)) {
                        //System.out.println(i + " " + colIdent);
                        validRows.add(i);
                    }
                }else if(dne){
                    if (!tokens.get(i).get(Integer.parseInt(colIdent)).equals(constantVal)) {
                        //System.out.println(i + " " + colIdent);
                        validRows.add(i);
                    }
                }else if(gt){

                    if(colOnLeft){//ex $1>5

                        if((IsInt(tokens.get(i).get(Integer.parseInt(colIdent))) || IsFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) &&
                                (IsInt(constantVal)) || IsFloat(constantVal)){

                            if(IsInt(tokens.get(i).get(Integer.parseInt(colIdent)))){ //value in table is int

                                if(IsInt(constantVal)){ //both values are integers
                                    if (Integer.parseInt(constantVal) < Integer.parseInt(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }else if(IsFloat(constantVal)){ //value from terminal is float, value in table is int
                                    if (Float.parseFloat(constantVal) < Integer.parseInt(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }

                            }else if(IsFloat(tokens.get(i).get(Integer.parseInt(colIdent)))){ //value in table is float
                                if(IsInt(constantVal)){ //value in table is float, value in terminal is int
                                    if (Integer.parseInt(constantVal) < Float.parseFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }else if(IsFloat(constantVal)){ //both are floats
                                    if (Float.parseFloat(constantVal) < Float.parseFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }
                            }

                        }

                    }else{//ex 5>$1

                        if((IsInt(tokens.get(i).get(Integer.parseInt(colIdent))) || IsFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) &&
                                (IsInt(constantVal)) || IsFloat(constantVal)){

                            if(IsInt(tokens.get(i).get(Integer.parseInt(colIdent)))){ //value in table is int

                                if(IsInt(constantVal)){ //both values are integers
                                    if (Integer.parseInt(constantVal) > Integer.parseInt(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }else if(IsFloat(constantVal)){ //value from terminal is float, value in table is int
                                    if (Float.parseFloat(constantVal) > Integer.parseInt(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }

                            }else if(IsFloat(tokens.get(i).get(Integer.parseInt(colIdent)))){ //value in table is float
                                if(IsInt(constantVal)){ //value in table is float, value in terminal is int
                                    if (Integer.parseInt(constantVal) > Float.parseFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }else if(IsFloat(constantVal)){ //both are floats
                                    if (Float.parseFloat(constantVal) > Float.parseFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }
                            }

                        }

                    }

                }else if(lt){

                    if(colOnLeft){ //ex $1<5

                        if((IsInt(tokens.get(i).get(Integer.parseInt(colIdent))) || IsFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) &&
                                (IsInt(constantVal)) || IsFloat(constantVal)){

                            if(IsInt(tokens.get(i).get(Integer.parseInt(colIdent)))){ //value in table is int

                                if(IsInt(constantVal)){ //both values are integers
                                    if (Integer.parseInt(constantVal) > Integer.parseInt(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }else if(IsFloat(constantVal)){ //value from terminal is float, value in table is int
                                    if (Float.parseFloat(constantVal) > Integer.parseInt(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }

                            }else if(IsFloat(tokens.get(i).get(Integer.parseInt(colIdent)))){ //value in table is float
                                if(IsInt(constantVal)){ //value in table is float, value in terminal is int
                                    if (Integer.parseInt(constantVal) > Float.parseFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }else if(IsFloat(constantVal)){ //both are floats
                                    if (Float.parseFloat(constantVal) > Float.parseFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }
                            }

                        }

                    }else{//ex 5<$1

                        if((IsInt(tokens.get(i).get(Integer.parseInt(colIdent))) || IsFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) &&
                                (IsInt(constantVal)) || IsFloat(constantVal)){

                            if(IsInt(tokens.get(i).get(Integer.parseInt(colIdent)))){ //value in table is int

                                if(IsInt(constantVal)){ //both values are integers
                                    if (Integer.parseInt(constantVal) < Integer.parseInt(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }else if(IsFloat(constantVal)){ //value from terminal is float, value in table is int
                                    if (Float.parseFloat(constantVal) < Integer.parseInt(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }

                            }else if(IsFloat(tokens.get(i).get(Integer.parseInt(colIdent)))){ //value in table is float
                                if(IsInt(constantVal)){ //value in table is float, value in terminal is int
                                    if (Integer.parseInt(constantVal) < Float.parseFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }else if(IsFloat(constantVal)){ //both are floats
                                    if (Float.parseFloat(constantVal) < Float.parseFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }
                            }

                        }

                    }

                }

//                if(left){
//                    values.set(index-1,validRows.toString());
//                }else if(right){
//                    values.set(index+1,validRows.toString());
//                }

            }
            if(left){
                values.set(index-1,validRows.toString());
            }else if(right){
                values.set(index+1,validRows.toString());
            }
            validRows = new ArrayList<>();

            //currentElem = currentElem+2;

        }

    }

    static void DoOrs(ArrayList<ArrayList<String>> tokens, ArrayList<String> values, ArrayList<Integer> validRows, ArrayList<String> headers){

        while(values.contains("||")){

            int index = values.indexOf("||");
            boolean left = false;
            boolean right = false;
            String val;
            if(values.get(index+1).startsWith("[") && values.get(index-1).startsWith("[")){ //the and statement is finished, evaluate it

                String leftSide = values.get(index-1).replaceAll("['\\[''\\]']", "");
                String rightSide = values.get(index+1).replaceAll("['\\[''\\]']", "");

                ArrayList<Integer> leftInt = new ArrayList<>();
                for(int i = 0; i < leftSide.length(); i++){
                    if(IsInt(String.valueOf(leftSide.charAt(i)))){
                        leftInt.add(Integer.parseInt(String.valueOf(leftSide.charAt(i))));
                    }
                }
                ArrayList<Integer> rightInt = new ArrayList<>();
                for(int i = 0; i < rightSide.length(); i++){
                    if(IsInt(String.valueOf(rightSide.charAt(i)))){
                        rightInt.add(Integer.parseInt(String.valueOf(rightSide.charAt(i))));
                    }
                }

                Set<Integer> allInts = new HashSet<>();
                allInts.addAll(leftInt);
                allInts.addAll(rightInt);

                ArrayList<Integer> orAnswer = new ArrayList<>(allInts);

                values.set(index,orAnswer.toString());
                values.remove(index-1);
                values.remove(index);


                return;
                //continue;
            }
            if(values.get(index-1).startsWith("[")){//left side done, go to right
                right = true;
                val = values.get(index+1);
            }else{
                left = true;
                val = values.get(index-1);
            }

            //String val = values.get(currentElem);

            int startingIndex = 0;
            int endingIndex = 0;

            String colIdent;
            String constantVal;

            for(int i = 0; i < val.length(); i++){

                if(val.charAt(i) == '<' || val.charAt(i) == '>' || val.charAt(i) == '='){
                    endingIndex = i;
                    break;
                }

            }

            if(!val.contains("$")){

                boolean dne = false;
                boolean eq = false;
                boolean gt = false;
                boolean lt = false;
                boolean trueSt = false;


                if(val.charAt(endingIndex+1) == '>' || val.charAt(endingIndex+1) == '='){
                    if(val.charAt(endingIndex) == '='){
                        eq = true;
                    }else{
                        dne = true;
                    }
                }else{
                    if(val.charAt(endingIndex) == '>'){
                        gt = true;
                    }else{
                        lt = true;
                    }
                }

                String firstVal = val.substring(0,endingIndex);
                String secondVal;
                if(eq || dne){
                    secondVal = val.substring(endingIndex+2,val.length());
                }else{
                    secondVal = val.substring(endingIndex+1,val.length());
                }
                if(eq){
                    if(firstVal.equals(secondVal)){
                        trueSt = true;
                    }

                }else if(dne){
                    if(!firstVal.equals(secondVal)){
                        trueSt = true;
                    }
                }else if(gt){

                    if(IsInt(firstVal)){
                        if(IsInt(secondVal)){
                            if(Integer.parseInt(firstVal) > Integer.parseInt(secondVal)){
                                trueSt = true;
                            }
                        }else if(IsFloat(secondVal)){
                            if(Integer.parseInt(firstVal) > Float.parseFloat(secondVal)){
                                trueSt = true;
                            }
                        }
                    }else if(IsFloat(firstVal)){
                        if(IsInt(secondVal)){
                            if(Float.parseFloat(firstVal) > Integer.parseInt(secondVal)){
                                trueSt = true;
                            }
                        }else if(IsFloat(secondVal)){
                            if(Float.parseFloat(firstVal) > Float.parseFloat(secondVal)){
                                trueSt = true;
                            }
                        }
                    }
                }else if(lt){

                    if(IsInt(firstVal)){
                        if(IsInt(secondVal)){
                            if(Integer.parseInt(firstVal) < Integer.parseInt(secondVal)){
                                trueSt = true;
                            }
                        }else if(IsFloat(secondVal)){
                            if(Integer.parseInt(firstVal) < Float.parseFloat(secondVal)){
                                trueSt = true;
                            }
                        }
                    }else if(IsFloat(firstVal)){
                        if(IsInt(secondVal)){
                            if(Float.parseFloat(firstVal) < Integer.parseInt(secondVal)){
                                trueSt = true;
                            }
                        }else if(IsFloat(secondVal)){
                            if(Float.parseFloat(firstVal) < Float.parseFloat(secondVal)){
                                trueSt = true;
                            }
                        }
                    }
                }

                if(trueSt){
                    for(int i = 0; i < tokens.size(); i++){

                        validRows.add(i);

                    }
                }

                if(left){
                    values.set(index-1,validRows.toString());
                }else if(right){
                    values.set(index+1,validRows.toString());
                }
                validRows = new ArrayList<>();
                return;
                //continue;
            }

            boolean dne = false;
            boolean eq = false;
            boolean gt = false;
            boolean lt = false;
            boolean colOnLeft = false;

            if(val.charAt(0) == '$'){ //first argument is col num or name

                colOnLeft = true;

                colIdent = val.substring(1,endingIndex);
                if(val.charAt(endingIndex+1) == '>' || val.charAt(endingIndex+1) == '='){
                    if(val.charAt(endingIndex) == '='){
                        eq = true;
                    }else{
                        dne = true;
                    }
                    constantVal = val.substring(endingIndex+2,val.length());
                }else{
                    if(val.charAt(endingIndex) == '>'){
                        gt = true;
                    }else{
                        lt = true;
                    }
                    constantVal = val.substring(endingIndex+1,val.length());
                }

            }else{ //first argument is constant
                colOnLeft = false;
                constantVal = val.substring(0,endingIndex);
                if(val.charAt(endingIndex+1) == '>' || val.charAt(endingIndex+1) == '='){
                    if(val.charAt(endingIndex) == '='){
                        eq = true;
                    }else{
                        dne = true;
                    }
                    colIdent = val.substring(endingIndex+3,val.length());
                }else{
                    if(val.charAt(endingIndex) == '>'){
                        gt = true;
                    }else{
                        lt = true;
                    }
                    colIdent = val.substring(endingIndex+2,val.length());
                }
            }

            if(header){
                if(!IsInt(colIdent) && headers.contains(colIdent)){
                    int newIndex = headers.indexOf(colIdent);
                    colIdent = String.valueOf(newIndex);
                }else if(!IsInt(colIdent) && !headers.contains(colIdent)){
                    System.out.println("INDEX ERROR");
                    return;
                }
            }

            //System.out.println(colIdent);
            //System.out.println(constantVal);

            for(int i = 0; i < tokens.size(); i++){

                if(eq) {
                    if (tokens.get(i).get(Integer.parseInt(colIdent)).equals(constantVal)) {
                        //System.out.println(i + " " + colIdent);
                        validRows.add(i);
                    }
                }else if(dne){
                    if (!tokens.get(i).get(Integer.parseInt(colIdent)).equals(constantVal)) {
                        //System.out.println(i + " " + colIdent);
                        validRows.add(i);
                    }
                }else if(gt){

                    if(colOnLeft){//ex $1>5

                        if((IsInt(tokens.get(i).get(Integer.parseInt(colIdent))) || IsFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) &&
                                (IsInt(constantVal)) || IsFloat(constantVal)){

                            if(IsInt(tokens.get(i).get(Integer.parseInt(colIdent)))){ //value in table is int

                                if(IsInt(constantVal)){ //both values are integers
                                    if (Integer.parseInt(constantVal) < Integer.parseInt(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }else if(IsFloat(constantVal)){ //value from terminal is float, value in table is int
                                    if (Float.parseFloat(constantVal) < Integer.parseInt(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }

                            }else if(IsFloat(tokens.get(i).get(Integer.parseInt(colIdent)))){ //value in table is float
                                if(IsInt(constantVal)){ //value in table is float, value in terminal is int
                                    if (Integer.parseInt(constantVal) < Float.parseFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }else if(IsFloat(constantVal)){ //both are floats
                                    if (Float.parseFloat(constantVal) < Float.parseFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }
                            }

                        }

                    }else{//ex 5>$1

                        if((IsInt(tokens.get(i).get(Integer.parseInt(colIdent))) || IsFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) &&
                                (IsInt(constantVal)) || IsFloat(constantVal)){

                            if(IsInt(tokens.get(i).get(Integer.parseInt(colIdent)))){ //value in table is int

                                if(IsInt(constantVal)){ //both values are integers
                                    if (Integer.parseInt(constantVal) > Integer.parseInt(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }else if(IsFloat(constantVal)){ //value from terminal is float, value in table is int
                                    if (Float.parseFloat(constantVal) > Integer.parseInt(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }

                            }else if(IsFloat(tokens.get(i).get(Integer.parseInt(colIdent)))){ //value in table is float
                                if(IsInt(constantVal)){ //value in table is float, value in terminal is int
                                    if (Integer.parseInt(constantVal) > Float.parseFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }else if(IsFloat(constantVal)){ //both are floats
                                    if (Float.parseFloat(constantVal) > Float.parseFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }
                            }

                        }

                    }

                }else if(lt){

                    if(colOnLeft){ //ex $1<5

                        if((IsInt(tokens.get(i).get(Integer.parseInt(colIdent))) || IsFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) &&
                                (IsInt(constantVal)) || IsFloat(constantVal)){

                            if(IsInt(tokens.get(i).get(Integer.parseInt(colIdent)))){ //value in table is int

                                if(IsInt(constantVal)){ //both values are integers
                                    if (Integer.parseInt(constantVal) > Integer.parseInt(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }else if(IsFloat(constantVal)){ //value from terminal is float, value in table is int
                                    if (Float.parseFloat(constantVal) > Integer.parseInt(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }

                            }else if(IsFloat(tokens.get(i).get(Integer.parseInt(colIdent)))){ //value in table is float
                                if(IsInt(constantVal)){ //value in table is float, value in terminal is int
                                    if (Integer.parseInt(constantVal) > Float.parseFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }else if(IsFloat(constantVal)){ //both are floats
                                    if (Float.parseFloat(constantVal) > Float.parseFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }
                            }

                        }

                    }else{//ex 5<$1

                        if((IsInt(tokens.get(i).get(Integer.parseInt(colIdent))) || IsFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) &&
                                (IsInt(constantVal)) || IsFloat(constantVal)){

                            if(IsInt(tokens.get(i).get(Integer.parseInt(colIdent)))){ //value in table is int

                                if(IsInt(constantVal)){ //both values are integers
                                    if (Integer.parseInt(constantVal) < Integer.parseInt(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }else if(IsFloat(constantVal)){ //value from terminal is float, value in table is int
                                    if (Float.parseFloat(constantVal) < Integer.parseInt(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }

                            }else if(IsFloat(tokens.get(i).get(Integer.parseInt(colIdent)))){ //value in table is float
                                if(IsInt(constantVal)){ //value in table is float, value in terminal is int
                                    if (Integer.parseInt(constantVal) < Float.parseFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }else if(IsFloat(constantVal)){ //both are floats
                                    if (Float.parseFloat(constantVal) < Float.parseFloat(tokens.get(i).get(Integer.parseInt(colIdent)))) {
                                        //System.out.println(i + " " + colIdent);
                                        validRows.add(i);
                                    }
                                }
                            }

                        }

                    }

                }

//                if(left){
//                    values.set(index-1,validRows.toString());
//                }else if(right){
//                    values.set(index+1,validRows.toString());
//                }

            }
            if(left){
                values.set(index-1,validRows.toString());
            }else if(right){
                values.set(index+1,validRows.toString());
            }
            validRows = new ArrayList<>();

            //currentElem = currentElem+2;

        }



    }

    static float CalculateSum(ArrayList<ArrayList<String>> tokens, String currentVal, FileWriter f) throws IOException {

        if(IsInt(currentVal) || IsFloat(currentVal)){
            return Float.parseFloat(currentVal);
        }

        if(IsAction(currentVal) && currentVal.contains("avg")){
            return CalculateAvg(tokens,currentVal, f);
        }else if(IsAction(currentVal) && currentVal.contains("upper")){
            f.write("TYPE ERROR");
            f.close();
            System.exit(0);
        }else if(IsAction(currentVal) && currentVal.contains("lower")){
            f.write("TYPE ERROR");
            f.close();
            System.exit(0);
        }else if(!IsAction(currentVal)){
            if(!IsInt(currentVal) && !IsFloat(currentVal)){
                f.write("TYPE ERROR");
                f.close();
                System.exit(0);
            }
        }

        String inside = currentVal.replaceAll("[^0-9'\\,']", "");
        ArrayList<int[]> listOfPoints = new ArrayList<>();

        if(inside.length() == 3){
            int[] p = new int[2];
            p[0] = Integer.parseInt(inside.substring(0,1));
            p[1] = Integer.parseInt(inside.substring(2,3));
            listOfPoints.add(p);
        }else {

            int firstXVal = 0;
            int firstYVal = 1;
            int secondXVal = 2;
            int secondYVal = 3;
            while(secondYVal <= inside.length()){

                int[] p = new int[2];
                p[0] = Integer.parseInt(inside.substring(firstXVal,firstYVal));
                p[1] = Integer.parseInt(inside.substring(secondXVal,secondYVal));
                listOfPoints.add(p);
                firstXVal = firstXVal+4;
                firstYVal = firstYVal+4;
                secondYVal = secondYVal+4;
                secondXVal = secondXVal+4;

            }

        }

        float total = 0;
        for(int[] list : listOfPoints){

            float value = CalculateSum(tokens,tokens.get(list[0]).get(list[1]), f);
            total = total + value;

        }

        return total;

    }

    static float CalculateAvg(ArrayList<ArrayList<String>> tokens, String currentVal, FileWriter f) throws IOException {

//        String inside = currentVal.replaceAll("[^0-9]", "");
//        int numVals = inside.length()/2;

        String inside = currentVal.replaceAll("[^0-9'\\,']", "");
        ArrayList<int[]> listOfPoints = new ArrayList<>();
        int numVals = 0;
        if(inside.length() == 3){
            int[] p = new int[2];
            p[0] = Integer.parseInt(inside.substring(0,1));
            p[1] = Integer.parseInt(inside.substring(2,3));
            listOfPoints.add(p);
            numVals++;
        }else {

            int firstXVal = 0;
            int firstYVal = 1;
            int secondXVal = 2;
            int secondYVal = 3;
            while(secondYVal <= inside.length()){
                numVals++;
                int[] p = new int[2];
                p[0] = Integer.parseInt(inside.substring(firstXVal,firstYVal));
                p[1] = Integer.parseInt(inside.substring(secondXVal,secondYVal));
                listOfPoints.add(p);
                firstXVal = firstXVal+4;
                firstYVal = firstYVal+4;
                secondYVal = secondYVal+4;
                secondXVal = secondXVal+4;

            }

        }

        float total = 0;
        for(int[] list : listOfPoints){

            float value = CalculateSum(tokens,tokens.get(list[0]).get(list[1]), f);
            total = total + value;

        }

        //float sum = CalculateSum(tokens,currentVal);

        return total/numVals;

    }

    static String ToUpper(ArrayList<ArrayList<String>> tokens, String currentVal){


        String inside = currentVal.replaceAll("[^0-9'\\,']", "");
        int[] p = new int[2];
        p[0] = Integer.parseInt(inside.substring(0,1));
        p[1] = Integer.parseInt(inside.substring(2,3));

        String value = tokens.get(p[0]).get(p[1]);

        if(IsInt(value) || IsFloat(value)){
            return null;
        }
        //String test = value.replaceAll("\\(\\[.*?\\]\\)(\\,)*", "");
        String test = value.replaceAll("[^a-z]", "");
        if(IsAction(value) && test.equals("toupper")){
            value = ToUpper(tokens, value);
        }else if(IsAction(value) && test.equals("tolower")){
            value = ToLower(tokens, value);
        }

        if(value == null){
            return null;
        }

        return value.toUpperCase();

    }

    static String ToLower(ArrayList<ArrayList<String>> tokens, String currentVal){


        String inside = currentVal.replaceAll("[^0-9'\\,']", "");
        int[] p = new int[2];
        p[0] = Integer.parseInt(inside.substring(0,1));
        p[1] = Integer.parseInt(inside.substring(2,3));

        String value = tokens.get(p[0]).get(p[1]);
        if(IsInt(value) || IsFloat(value)){
            return null;
        }
        String test = value.replaceAll("[^a-z]", "");
        if(IsAction(value) && test.equals("tolower")){
            value = ToLower(tokens, value);
        }else if(IsAction(value) && test.equals("toupper")){
            value = ToUpper(tokens, value);
        }
        if(value == null){
            return null;
        }
        return value.toLowerCase();

    }


    static boolean ValidDag(int size, Graph g){

        boolean[] visited = new boolean[size];
        boolean[] tracker = new boolean[size];

        for(int i = 0; i < size; i++){

            if(ContainsCycle(visited,tracker,i, g)) return false;


        }
        return true;



    }

    static boolean ContainsCycle(boolean[] visited, boolean[] tracker, int currentNode, Graph g){

        if(tracker[currentNode]){
            return true;
        }
        if(visited[currentNode]){
            return false;
        }


        tracker[currentNode] = visited[currentNode] = true;

        ArrayList<Integer> subNodes = g.matrix.get(currentNode);

        for(Integer i : subNodes){

            if(ContainsCycle(visited,tracker,i,g))return true;

        }

        tracker[currentNode] = false;
        return false;

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
                    while(IsWhiteSpace(st.charAt(i+1)) && i+1 < st.length()-1){ //skipping all white space
                        i++;
                    }
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


    static void TypeSeparator(ArrayList<ArrayList<String>> tokens){

        for(ArrayList<String> a : tokens){

            for(String s : a){ //various tests of different types

                if(IsInt(s)){
                    typeTracker.put(s,"int");
                }else if(IsFloat(s)){
                    typeTracker.put(s,"float");
                }else if(IsAction(s)){
                    typeTracker.put(s,"action");
                }else{
                    typeTracker.put(s,"string");
                }

            }

        }

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

    static boolean IsAction(String s){

        //String input = s.replaceAll("\\(\\[.*?\\]\\)(\\,)*", "");
        String input = s .replaceAll("[^a-z]", "");
        return input.equals("sum") || input.equals("avg") || input.equals("toupper") || input.equals("tolower");
    }



}