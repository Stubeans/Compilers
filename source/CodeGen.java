package source;

import java.util.ArrayList;

public class CodeGen {

    ArrayList<String> AST;
    ArrayList<Integer> ASTdepth;

    ArrayList<Symbol> symbolTable;

    ArrayList<String> opCodes = new ArrayList<>(256);
    // stack contains pointer of it's respective Symbol Table (i)
    ArrayList<tempVar> stack = new ArrayList<>();

    int codePointer = 0;
    int heapPointer = 255;

    private int scope = -1;
    ArrayList<Integer> scopeList = new ArrayList<>();
    int lastDepth = -500;

    public void main(ArrayList<String> iAST, ArrayList<Integer> iASTdepth, ArrayList<Symbol> isymbolTable) {
        System.out.println("Starting Code Gen...");
        System.out.println();
        AST = iAST;
        ASTdepth = iASTdepth;
        symbolTable = isymbolTable;

        codeGeneration();
        stackGen();
        heapGen();
        if(opCodes.size() > 256) {
            //ERROR
        } else {
            //done
        }
    }   

    private void codeGeneration() {
        for(int i = 0 ; i < AST.size(); i++) {
            if(AST.get(i).equals("<Variable Declaration>")) {
                
            } else if(AST.get(i).equals("<Assign Statement>")) {

            } else if(AST.get(i).equals("<If Statement>")) {

            } else if(AST.get(i).equals("<While Statement>")) {

            } else if(AST.get(i).equals("<Print Statement>")) {

            } else if(AST.get(i).equals("<Block>")) {
                scope++;
                scopeList.add(scope);
                lastDepth = ASTdepth.get(i);
            } else if(AST.get(i).equals("<endBlock>")) {
                scope--;
            }

        }
    }

    private void stackGen() {

    }

    private void heapGen() {

    }

    private String findScopeLetter(int inputScope) {
        String scopeLetter = "";
        int num = 0;
        for(int i = 0; i < scopeList.size(); i++) {
            if(scopeList.get(i) == inputScope) {
                num++;
            }
        }
        num--;
        scopeLetter = (char)(97 + num) + "";
        return scopeLetter;
    }

    private Symbol isInTableS(ArrayList<Symbol> checkST, String checkName, int iscope, String iScopeLetter) {
        for(int i = iscope; i > -1; i--) {
            for(int j = 0; j < checkST.size(); j++) {
                if(checkST.get(j).name.equals(checkName) && checkST.get(j).scope == i && checkST.get(j).scopeLetter.equals(iScopeLetter) && i == iscope) {
                    return checkST.get(j);
                } else if(checkST.get(j).name.equals(checkName) && checkST.get(j).scope == i && i != iscope) {
                    return checkST.get(j);
                }
            }
        }
        return null;
    }
    
}