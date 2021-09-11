import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import java.io.IOException;

import static org.antlr.v4.runtime.CharStreams.fromFileName;

public class test {


    public static void main(String[] args) throws IOException {

        String source = args[0];
        CharStream cs =  fromFileName(source);
        SimpleLangLexer lexer = new SimpleLangLexer(cs);
        ErrorListener e = new ErrorListener();
        lexer.removeErrorListeners();
        lexer.addErrorListener(e);
        CommonTokenStream token = new CommonTokenStream(lexer);
        SimpleLangParser parser = new SimpleLangParser(token);
        parser.removeErrorListeners();
        parser.addErrorListener(e);
        ParseTree tree = parser.program();

        MyVisitor visitor = new MyVisitor();
        visitor.visit(tree);
        if(e.error){
            System.out.println("PARSER ERROR");
        }
        if(!visitor.containsMain){
            System.out.println("MAIN ERROR");
        }


    }

}
