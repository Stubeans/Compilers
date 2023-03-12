package source;

import java.util.ArrayList;

public class Parser {
    public void main(ArrayList<Token> tokenStream) {
        for(int i = 0; i < tokenStream.size(); i++) {
            System.out.print(tokenStream.get(i).type + " ");
        }
    }
}
