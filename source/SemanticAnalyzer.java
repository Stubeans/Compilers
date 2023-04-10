package source;

import java.util.ArrayList;

public class SemanticAnalyzer {

    private int prgCounter = 0;
    private int depth = 0;
    private ArrayList<String> AST = new ArrayList<>();
    private ArrayList<Integer> ASTdepth = new ArrayList<>();

    public void main(ArrayList<String> CST) {

        prgCounter++;

        System.out.println();
        debug("STARTING SEMANTIC ANALYSIS ON PROGRAM " + prgCounter + ".");
        System.out.println();

        ASTcreation(CST);
        printAST();

        AST.clear();
        ASTdepth.clear();
    }

    private void ASTcreation(ArrayList<String> CST) {
        for(int i = 0; i < CST.size(); i++) {
            if(CST.get(i).equals("[{]")) {
                AST.add("<Block>");
                ASTdepth.add(depth);
                depth++;
            } else if(CST.get(i).equals("[}]")) {
                depth--;
            } 
        }
    }

    private void printAST() {
        String leading = "";
        for(int i = 0; i < AST.size(); i++) {
            for(int j = 0; j < ASTdepth.get(i); j++) {
                leading = leading + "-";
            }
            System.out.println(leading + AST.get(i));
            leading = "";
        }
    }

    private void debug(String message) {
        System.out.println("SEMANTIC: " + message);
    }
}
