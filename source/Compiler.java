package source;

import java.util.ArrayList;

public class Compiler {
    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        Parser parser = new Parser();

        //System.out.println(Arrays.toString(args));
        if(args.length != 0) {
            ArrayList<Token> tokenStream = new ArrayList<>();
            tokenStream = lexer.main(args[0]);
            parser.main(tokenStream);
            System.out.println();
        } else {
            System.out.println("No arguments were provided");
        }
    }   

    
}