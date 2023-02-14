package source;

import java.util.Arrays;

public class Compiler {
    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        System.out.println(Arrays.toString(args));
        lexer.main(args[0]);
    }   

    
}