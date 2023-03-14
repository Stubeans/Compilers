package source;

//import java.util.ArrayList;

public class Compiler {
    public static void main(String[] args) {
        //Insantiate the Lexer
        Lexer lexer = new Lexer();

        //If a file is given, run the lexer with it. Otherwise, error.
        if(args.length != 0) {
            lexer.main(args[0]);
        } else {
            System.out.println("No arguments were provided");
        }
    }   

    
}