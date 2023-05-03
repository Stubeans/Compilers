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

    int tempNum = 0;

    public void main(ArrayList<String> iAST, ArrayList<Integer> iASTdepth, ArrayList<Symbol> isymbolTable) {
        System.out.println("Starting Code Gen...");
        System.out.println();
        AST = iAST;
        ASTdepth = iASTdepth;
        symbolTable = isymbolTable;

        codeGeneration();
        stackGen();
        heapGen();
        if(opCodes.size() > 256 || codePointer > 256) {
            //ERROR
        } else {
            printCode();
        }
    }   

    private void codeGeneration() {
        for(int i = 0 ; i < AST.size(); i++) {
            if(AST.get(i).equals("<Variable Declaration>")) {
                //System.out.println("VarDecl");
                i++;
                i++;
                stack.add(new tempVar("T" + Integer.toString(tempNum) + "XX", AST.get(i).substring(1, 2), Integer.toString(tempNum), scope, findScopeLetter(scope)));
                addToCode("A9");
                addToCode("00");
                addToCode("8D");
                addToCode("T" + Integer.toString(tempNum));
                addToCode("XX");
                tempNum++;
            } else if(AST.get(i).equals("<Assign Statement>")) {
                i++;
                tempVar tempVariable = isInTempTable(stack, AST.get(i).substring(1, 2), scope, findScopeLetter(scope));
                i++;
                addToCode("A9");
                //addToCode("00");
                boolean isntDone = true;
                int wholeNum = 0;
                while(isntDone) {
                    wholeNum = wholeNum + Integer.valueOf(AST.get(i).substring(1, 2));
                    if(AST.get(i + 1).length() > 3) {
                        isntDone = false;
                    } else {
                        i++;
                    }
                }
                addToCode(num2hex(wholeNum));
                addToCode("8D");
                addToCode(tempVariable.temp.substring(0, 2));
                addToCode("XX");

            } else if(AST.get(i).equals("<If Statement>")) {

            } else if(AST.get(i).equals("<While Statement>")) {

            } else if(AST.get(i).equals("<Print Statement>")) {

            } else if(AST.get(i).equals("<Block>")) {
                //System.out.println("Block");
                scope++;
                scopeList.add(scope);
                lastDepth = ASTdepth.get(i);
            } else if(AST.get(i).equals("<endBlock>")) {
                //System.out.println("endBlock");
                scope--;
            }

        }
    }

    private void stackGen() {
        //System.out.println("stackGen");
        addToCode("00");
        for(int i = 0; i < stack.size(); i++) {
            for(int j = 0; j < opCodes.size(); j++) {
                if(opCodes.get(j).equals("T" + Integer.toString(i))) {
                    opCodes.set(j, num2hex(codePointer));
                    j++;
                    opCodes.set(j, "00");
                }
            }
            codePointer++;
        }
        //System.out.println("end stackGen");
    }

    private void heapGen() {

    }

    private void addToCode(String code) {
        opCodes.add(codePointer, code);
        codePointer++;
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

    private String num2hex(int num) {
        String hexx = Integer.toHexString(num);
        hexx = hexx.toUpperCase();
        if(hexx.length() < 2) {
            hexx = "0" + hexx;
        } else if(hexx.length() > 2) {
            hexx = hexx.substring(0, 2);
        }
        return hexx;
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

    private tempVar isInTempTable(ArrayList<tempVar> checkST, String checkName, int iscope, String iScopeLetter) {
        for(int i = iscope; i > -1; i--) {
            for(int j = 0; j < checkST.size(); j++) {
                if(checkST.get(j).var.equals(checkName) && checkST.get(j).scope == i && checkST.get(j).scopeLetter.equals(iScopeLetter) && i == iscope) {
                    return checkST.get(j);
                } else if(checkST.get(j).var.equals(checkName) && checkST.get(j).scope == i && i != iscope) {
                    return checkST.get(j);
                }
            }
        }
        return null;
    }

    private void printCode() {
        boolean isntEnd = true;
        int pointer = 0;
        // while(isntEnd) {
        //     if(opCodes.get(pointer).equals("00")) {
        //         isntEnd = false;
        //         break;
        //     }
        //     System.out.print(opCodes.get(pointer) + " ");
        //     pointer++;
        // }
        for(int i = 0; i < opCodes.size(); i++) {
            System.out.print(opCodes.get(i) + " ");
        }
    }
    
}