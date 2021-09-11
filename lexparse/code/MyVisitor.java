
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

public class MyVisitor extends SimpleLangBaseVisitor<Object>{

    Map<String,String> declared = new HashMap<>();
    Map<String,Boolean> typesUsed = new HashMap<>();
    Map<String,String> varTracker = new HashMap<>();
    HashMap<String, HashMap<String,Integer>> intVarTracker = new HashMap<>();
    Map<String,String> classesAndMethods = new HashMap<>();
    HashMap<String, HashMap<String,String>> charVarTracker = new HashMap<>();
    HashMap<String,Boolean> expressions = new HashMap<>();
    HashMap<String, HashMap<String,Boolean>> boolVarTracker = new HashMap<>();

    boolean containsMain = false;

    @Override public Object visitProgram(SimpleLangParser.ProgramContext ctx) {

        //System.out.println("HI");

        declared.put(ctx.IDENTIFIER().getText(),"program");
        //List<SimpleLangParser.ConstDeclContext> id = ctx.constDecl();
        //for(SimpleLangParser.ConstDeclContext s : id){
            //System.out.println(s);
        //}
        //System.out.println(id);
        return visitChildren(ctx);

    }

    @Override public Object visitConstDecl(SimpleLangParser.ConstDeclContext ctx) {


        List<TerminalNode> list = ctx.IDENTIFIER();
        String type = ctx.type().getText();

        for(TerminalNode n : list){
            //System.out.println(n.getText());

            if(type.equals("int")){
               // intVarTracker.put()
            }else if(type.equals("char")){

            }else if(type.equals("bool")){

            }

            declared.put(n.getText(),"const");

        }

        return visitChildren(ctx);
    }

    @Override public Object visitVarDecl(SimpleLangParser.VarDeclContext ctx) {

        List<TerminalNode> list = ctx.IDENTIFIER();
        String t = ctx.type().getText();
        //HashMap<String,String> stringTempVals = new HashMap<>();
        HashMap<String, String> tempVals = new HashMap<>();
        HashMap<String, Integer> tempValsInt = new HashMap<>();
        HashMap<String, Boolean> tempValsBool = new HashMap<>();
        boolean isChar = Objects.equals(ctx.type().getText(), "char");
        boolean isBool = Objects.equals(ctx.type().getText(), "bool");
        boolean isInt = Objects.equals(ctx.type().getText(), "int");
        if(isChar) {
            HashMap<String, String> isMapEmptpy = charVarTracker.get(ctx.parent.getText());
            if (isMapEmptpy == null) {
                tempVals = new HashMap<>();
            } else {
                tempVals = isMapEmptpy;
            }
        }else if(isBool) {
            HashMap<String, Boolean> isMapEmptpy = boolVarTracker.get(ctx.parent.getText());
            if (isMapEmptpy == null) {
                tempValsBool = new HashMap<>();
            } else {
                tempValsBool = isMapEmptpy;
            }
        }else if(isInt) {
            HashMap<String, Integer> isMapEmptpy = intVarTracker.get(ctx.parent.getText());
            if (isMapEmptpy == null) {
                tempValsInt = new HashMap<>();
            } else {
                tempValsInt = isMapEmptpy;
            }
        }

        if(!typesUsed.containsKey(t) && !Objects.equals(t, "bool") && !Objects.equals(t, "char") && !Objects.equals(t, "int")){

            System.out.println("NAME USE ERROR");
            System.out.println(t);

        }

        for(TerminalNode n : list){

            //System.out.println(n.getText());
            String testString = varTracker.get(n.getText());
            if(Objects.equals(testString, ctx.parent.getText())){
                System.out.println("VAR ERROR");
            }
            if(isChar) {
                tempVals.put(n.getText(), null);
            }else if (isBool){
                tempValsBool.put(n.getText(),null);
            }else if (isInt){
                tempValsInt.put(n.getText(),null);
            }
            //System.out.println(n.getText());
            declared.put(n.getText(),"var");
            varTracker.put(n.getText(),ctx.parent.getText());


        }

        if(isChar) {
            charVarTracker.put(ctx.parent.getText(), tempVals);
        }else if(isBool){
            boolVarTracker.put(ctx.parent.getText(), tempValsBool);
        }else if(isInt){
            intVarTracker.put(ctx.parent.getText(), tempValsInt);
        }

        return visitChildren(ctx);
    }

    @Override public Object visitClassDecl(SimpleLangParser.ClassDeclContext ctx) {

        declared.put(ctx.IDENTIFIER().getText(),"class");
        typesUsed.put(ctx.IDENTIFIER().getText(),true);
        classesAndMethods.put(ctx.getText(),"class");
        return visitChildren(ctx);
    }

    @Override public Object visitEnumDecl(SimpleLangParser.EnumDeclContext ctx) {

        List<TerminalNode> list = ctx.IDENTIFIER();

        for(TerminalNode n : list){
            //System.out.println(n.getText());
            declared.put(n.getText(),"enum");
        }

        return visitChildren(ctx);
    }

    @Override public Object visitInterfaceDecl(SimpleLangParser.InterfaceDeclContext ctx) {


        declared.put(ctx.IDENTIFIER().getText(),"interface");
        return visitChildren(ctx);
    }

    @Override public Object visitMethodDecl(SimpleLangParser.MethodDeclContext ctx) {

        //System.out.println(ctx.getText());
        declared.put(ctx.IDENTIFIER().getText(),"method");
        classesAndMethods.put(ctx.getText(),"method");
        if(Objects.equals(ctx.IDENTIFIER().getText(), "main")){
            if(Objects.equals(ctx.children.get(0).getText(), "void")){
                if(ctx.formPars() == null){
                    this.containsMain = true;
                }
            }
        }
        return visitChildren(ctx); }

    @Override public Object visitStatement(SimpleLangParser.StatementContext ctx) {

        //System.out.println(ctx.getText());
        //System.out.println(ctx.children.size());
        RuleContext currentCtx = ctx.parent;
        String test = currentCtx.getText();
        while(!Objects.equals(classesAndMethods.get(test), "method") ){  //&& !Objects.equals(classesAndMethods.get(test), "class")
            currentCtx = currentCtx.parent;
            test = currentCtx.getText();

        }

        if(ctx.PRINT() != null) {
            if(ctx.expr() != null){

                if(charVarTracker.get(currentCtx.getText()).containsKey(ctx.expr().getText())){ //it is a string
                    if(charVarTracker.get(currentCtx.getText()).get(ctx.expr().getText()) == null){
                        System.out.println("");
                    }else {
                        System.out.println(charVarTracker.get(currentCtx.getText()).get(ctx.expr().getText()));
                    }

                }else if(boolVarTracker.get(currentCtx.getText()).containsKey(ctx.expr().getText())){

                    if(boolVarTracker.get(currentCtx.getText()).get(ctx.expr().getText()) != null){
                        System.out.println(boolVarTracker.get(currentCtx.getText()).get(ctx.expr().getText()));
                    }

                }else if(intVarTracker.get(currentCtx.getText()).containsKey(ctx.expr().getText())){

                    if(intVarTracker.get(currentCtx.getText()).get(ctx.expr().getText()) != null){
                        System.out.println(intVarTracker.get(currentCtx.getText()).get(ctx.expr().getText()));
                    }

                }

            }
            //System.out.println(ctx.PRINT().getText());
        }
//        if(ctx.designator() != null) {
//            System.out.println(ctx.designator().getText());
//        }
        //List<ParseTree> children = ctx.children;
        //for(ParseTree c : children){

        //    System.out.println(c.getText());

        //}

        return visitChildren(ctx);
    }

    @Override public Object visitDesignatorStatement(SimpleLangParser.DesignatorStatementContext ctx) {

        RuleContext currentCtx = ctx.parent;
        String test = currentCtx.getText();
        while(!Objects.equals(classesAndMethods.get(test), "method") ){  //&& !Objects.equals(classesAndMethods.get(test), "class")
            currentCtx = currentCtx.parent;
            test = currentCtx.getText();

        }
        //System.out.println(currentCtx.getText());


        if(ctx.expr() != null) {
            //System.out.println(ctx.designator().getText() + "=" + ctx.expr().getText());
            if(charVarTracker.containsKey(currentCtx.getText())){
                if(ctx.expr().getText().startsWith("\"")){
                    HashMap<String,String> temp = charVarTracker.get(currentCtx.getText());
                    temp.put(ctx.designator().getText(),ctx.expr().getText().substring(1,ctx.expr().getText().length()-1));
                    //System.out.println(ctx.expr().getText().substring(1,ctx.expr().getText().length()-1));
                }else if(Objects.equals(ctx.expr().getText(), "true") || Objects.equals(ctx.expr().getText(), "false")){
                    HashMap<String,Boolean> temp = boolVarTracker.get(currentCtx.getText());
                    if(Objects.equals(ctx.expr().getText(), "true")){
                        temp.put(ctx.designator().getText(), true);
                    }else{
                        temp.put(ctx.designator().getText(), false);
                    }

                }else try{
                    int number = Integer.parseInt(ctx.expr().getText());
                    HashMap<String,Integer> temp = intVarTracker.get(currentCtx.getText());
                    temp.put(ctx.designator().getText(),number);


                }catch(NumberFormatException ignored){

                }
            }

        }else if(ctx.actPars() != null){
            //System.out.println(ctx.designator().getText() + ctx.actPars().getText() );
        }else if(ctx.PP() != null){
            //System.out.println(ctx.designator().getText() + ctx.PP().getText());
        }else if(ctx.MM() != null){
            //System.out.println(ctx.designator().getText() + ctx.MM().getText() );
        }
        return visitChildren(ctx);
    }

    @Override public Object visitActPars(SimpleLangParser.ActParsContext ctx) { return visitChildren(ctx); }

    @Override public Object visitExpr(SimpleLangParser.ExprContext ctx) {

        expressions.put(ctx.getText(),true);


        return visitChildren(ctx);
    } //ord and chr

    @Override public Object visitTerm(SimpleLangParser.TermContext ctx) { return visitChildren(ctx); }

    @Override public Object visitDesignator(SimpleLangParser.DesignatorContext ctx) {


        List<TerminalNode> list = ctx.IDENTIFIER();

        for(TerminalNode n : list){
            //System.out.println(n.getText());
           // declared.put(n.getText(),"designator");

            if(!declared.containsKey(n.getText()) && !n.getText().equals("this") && !expressions.containsKey(n.getText())){

                System.out.println("NAME USE ERROR");
                System.out.println(n.getText());
            }

        }
        return visitChildren(ctx);
    }

    @Override public Object visitFactor(SimpleLangParser.FactorContext ctx) { return visitChildren(ctx); }

    @Override public Object visitCondition(SimpleLangParser.ConditionContext ctx) { return visitChildren(ctx); }

    @Override public Object visitCondTerm(SimpleLangParser.CondTermContext ctx) { return visitChildren(ctx); }

    @Override public Object visitCondFact(SimpleLangParser.CondFactContext ctx) { return visitChildren(ctx); }

    @Override public Object visitInterfaceMethodDecl(SimpleLangParser.InterfaceMethodDeclContext ctx) {

        //System.out.println(ctx.IDENTIFIER().getText());
        declared.put(ctx.IDENTIFIER().getText(),"interfaceMethod");
        return visitChildren(ctx);
    }

    @Override public Object visitFormPars(SimpleLangParser.FormParsContext ctx) {

        List<TerminalNode> list = ctx.IDENTIFIER();
        HashMap<String, String> isMapEmptpy = charVarTracker.get(ctx.parent.getText());
        HashMap<String, String> stringTempVals;
        if(isMapEmptpy == null) {
            stringTempVals = new HashMap<>();
        }else{
            stringTempVals = isMapEmptpy;
        }

        HashMap<String, Integer> isMapEmptpyInt = intVarTracker.get(ctx.parent.getText());
        HashMap<String, Integer> intTempVals;
        if(isMapEmptpyInt == null) {
            intTempVals = new HashMap<>();
        }else{
            intTempVals = isMapEmptpyInt;
        }

        HashMap<String, Boolean> isMapEmptpyBool = boolVarTracker.get(ctx.parent.getText());
        HashMap<String, Boolean> boolTempVals;
        if(isMapEmptpyBool == null) {
            boolTempVals = new HashMap<>();
        }else{
            boolTempVals = isMapEmptpyBool;
        }


        Map<String,String> typeChecker = new HashMap<>();

        int size = ctx.IDENTIFIER().size();

        for(int i = 0; i < size; i++){

            typeChecker.put(ctx.IDENTIFIER(i).getText(),ctx.type(i).getText());

        }

        for(TerminalNode n : list){

            String testString = varTracker.get(n.getText());
            if(Objects.equals(testString, ctx.parent.getText())){
                System.out.println("VAR ERROR");
            }

            if(Objects.equals(typeChecker.get(n.getText()), "char")) {
                stringTempVals.put(n.getText(), null);
            }else if(Objects.equals(typeChecker.get(n.getText()), "int")) {
                intTempVals.put(n.getText(), null);
            }else if(Objects.equals(typeChecker.get(n.getText()), "bool")) {
                boolTempVals.put(n.getText(), null);
            }
            //System.out.println(n.getText());
            varTracker.put(n.getText(),ctx.parent.getText());

            //System.out.println(n.getText());
            declared.put(n.getText(),"form");
        }

        if(!stringTempVals.isEmpty()) {
            charVarTracker.put(ctx.parent.getText(), stringTempVals);
        }else if(!intTempVals.isEmpty()) {
            intVarTracker.put(ctx.parent.getText(), intTempVals);
        }else if(!boolTempVals.isEmpty()) {
            boolVarTracker.put(ctx.parent.getText(), boolTempVals);
        }
        return visitChildren(ctx);
    }

    //@Override public Object visitBoolConst(SimpleLangParser.BoolConstContext ctx) { return visitChildren(ctx); }

    //@Override public Object visitNumConst(SimpleLangParser.NumConstContext ctx) { return visitChildren(ctx); }

    //@Override public Object visitCharConst(SimpleLangParser.CharConstContext ctx) { return visitChildren(ctx); }

    @Override public Object visitType(SimpleLangParser.TypeContext ctx) {

        //System.out.println(ctx.IDENTIFIER().getText());
        if(!typesUsed.containsKey(ctx.IDENTIFIER().getText())){
            typesUsed.put(ctx.IDENTIFIER().getText(),true);
        }


        return visitChildren(ctx);
    }

//    @Override public Object visitIdent(SimpleLangParser.IdentContext ctx) {
//
//        //System.out.println(ctx.getText());
//
//        if(!declared.containsKey(ctx.getText())){
//            System.out.println("NAME USE ERROR");
//        }
//
//        return visitChildren(ctx);
//    }



}
