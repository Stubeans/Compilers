package source;

import java.util.ArrayList;

public class SemanticAnalyzer {

    private int prgCounter = 0;
    private int depth = 0;
    private int rebound = 0;
    private int reboundDepth = 0;

    private ArrayList<String> AST = new ArrayList<>();
    private ArrayList<Integer> ASTdepth = new ArrayList<>();

    private ArrayList<Symbol> symbolTable = new ArrayList<>();

    public void main(ArrayList<String> CST, ArrayList<Integer> CSTdepth) {

        prgCounter++;

        System.out.println();
        debug("STARTING SEMANTIC ANALYSIS ON PROGRAM " + prgCounter + ".");
        System.out.println();

        ASTcreation(CST, CSTdepth);
        printAST();

        AST.clear();
        ASTdepth.clear();

        //symbolTable.add(new Symbol("0", "type", 0, symbolTable));

    }

    private void ASTcreation(ArrayList<String> CST, ArrayList<Integer> CSTdepth) {
        for(int i = 0; i < CST.size(); i++) {
            //Block
            if(CST.get(i).equals("[{]")) {
                AST.add("<Block>");
                ASTdepth.add(depth);
                depth++;
            //endBlock
            } else if(CST.get(i).equals("[}]")) {
                depth--;
            //VarDecl
            } else if(CST.get(i).equals("<Variable Declaration>")) {
                AST.add("<VarDecl>");
                ASTdepth.add(depth);
                depth++;
                i = i+2;
                AST.add(CST.get(i));
                ASTdepth.add(depth);
                i = i+2;
                AST.add(CST.get(i));
                ASTdepth.add(depth);
                depth--;
            //Assignment
            } else if(CST.get(i).equals("<Assign Statement>")) {
                AST.add("Assign");
                ASTdepth.add(depth);
                depth++;
                i = i+2;
                AST.add(CST.get(i));
                ASTdepth.add(depth);
            //Print
            } else if(CST.get(i).equals("<Print Statement>")) {
                AST.add("Print");
                ASTdepth.add(depth);
                depth++;
            //intOp
            } else if(CST.get(i).equals("<Integer Operation>")) {
                AST.add("Intop");
                ASTdepth.add(depth);
                depth++;
                AST.add(CST.get(i-1));
                ASTdepth.add(depth);
            //boolOp
            } else if(CST.get(i).equals("<Boolean Operation>")) {
                AST.add("Boolop");
                ASTdepth.add(depth);
                depth++;
            //if
            } else if(CST.get(i).equals("<If Statement>")) {

            //while
            } else if(CST.get(i).equals("<While Statement>")) {

            //NON-DEPTH++
            //id
            } else if(CST.get(i).equals("<Id>")) {

            //digit
            } else if(CST.get(i).equals("<Digit>")) {

            //charList
            } else if(CST.get(i).equals("<Char List>")) {

            //boolval
            } else if(CST.get(i).equals("<Boolean Value>")) {

            //int
            } else if(CST.get(i).equals("[int]")) {

            //string
            }  else if(CST.get(i).equals("[string]")) {

            //bool
            } else if(CST.get(i).equals("[boolean]")) {

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
