package source;

import java.util.ArrayList;

public class CodeGen {

    ArrayList<String> AST;
    ArrayList<Integer> ASTdepth;

    ArrayList<Symbol> symbolTable;

    public void main(ArrayList<String> iAST, ArrayList<Integer> iASTdepth, ArrayList<Symbol> isymbolTable) {
        AST = iAST;
        ASTdepth = iASTdepth;
        symbolTable = isymbolTable;

        codeGeneration();
    }   

    private void codeGeneration() {
        
    }

    
}