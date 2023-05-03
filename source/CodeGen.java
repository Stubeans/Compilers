package source;

import java.util.ArrayList;

public class CodeGen {

    ArrayList<String> AST;
    ArrayList<Integer> ASTdepth;

    ArrayList<Symbol> symbolTable;

    ArrayList<String> opCodes = new ArrayList<>(256);
    
    ArrayList<tempVar> stack = new ArrayList<>();

    ArrayList<tempVar> jumpTable = new ArrayList<>();

    int codePointer = 0;
    int heapPointer = 255;

    private int scope = -1;
    ArrayList<Integer> scopeList = new ArrayList<>();
    int lastDepth = -500;

    int tempNum = 0;
    int tempBranchNum = 0;

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
            System.out.println("CodeGen: ERROR | Cannot be more than 256 opCodes");
        } else {
            printCode();
        }
    }   

    private void codeGeneration() {
        stack.add(new tempVar("T0XX", "Left", "0", 0, ""));
        tempNum++;
        stack.add(new tempVar("T1XX", "Right", "1", 0, ""));
        tempNum++;
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
                boolean isntDone = true;
                addToCode("A9");
                addToCode(num2hex(Integer.valueOf(AST.get(i).substring(1, 2))));
                addToCode("8D");
                addToCode(tempVariable.temp.substring(0, 2));
                addToCode("XX");
                if(!(AST.get(i + 1).length() > 3)) {
                    i++;
                    while(isntDone) {
                        //If it isn't an ID
                        if(!isValidChar(AST.get(i).substring(1, 2))) {
                            addToCode("A9");
                            addToCode(num2hex(Integer.valueOf(AST.get(i).substring(1, 2))));
                            addToCode("6D");
                            addToCode(tempVariable.temp.substring(0, 2));
                            addToCode("XX");
                            addToCode("8D");
                            addToCode(tempVariable.temp.substring(0, 2));
                            addToCode("XX");
                        //If it is an ID
                        } else {
                            tempVar tempVariable2 = isInTempTable(stack, AST.get(i).substring(1, 2), scope, findScopeLetter(scope));
                            addToCode("AD");
                            addToCode(tempVariable2.temp.substring(0, 2));
                            addToCode("XX");
                            addToCode("6D");
                            addToCode(tempVariable.temp.substring(0, 2));
                            addToCode("XX");
                            addToCode("8D");
                            addToCode(tempVariable.temp.substring(0, 2));
                            addToCode("XX");
                        }
                        if(AST.get(i + 1).length() > 3) {
                            isntDone = false;
                        } else {
                            i++;
                        }
                    }
                }

            } else if(AST.get(i).equals("<If Statement>")) {
                tempVar branchVar = new tempVar("J" + Integer.toString(tempBranchNum), null, null, 0, null);
                i++;
                boolean isntDone = true;
                addToCode("A9");
                addToCode("00");
                addToCode("8D");
                addToCode("T0");
                addToCode("XX");
                while(isntDone) {
                    //If it isn't an ID
                    if(!isValidChar(AST.get(i).substring(1, 2))) {
                        addToCode("A9");
                        addToCode(num2hex(Integer.valueOf(AST.get(i).substring(1, 2))));
                        addToCode("6D");
                        addToCode("T0");
                        addToCode("XX");
                        addToCode("8D");
                        addToCode("T0");
                        addToCode("XX");
                    //If it is an ID
                    } else {
                        tempVar tempVariable = isInTempTable(stack, AST.get(i).substring(1, 2), scope, findScopeLetter(scope));
                        addToCode("AD");
                        addToCode(tempVariable.temp.substring(0, 2));
                        addToCode("XX");
                        addToCode("6D");
                        addToCode("T0");
                        addToCode("XX");
                        addToCode("8D");
                        addToCode("T0");
                        addToCode("XX");
                    }
                    if(AST.get(i + 1).length() > 3 && !AST.get(i + 1).equals("<Boolop>")) {
                        isntDone = false;
                    } else if(AST.get(i + 1).equals("<Boolop>")) {
                        i++;
                        boolean isntDone2 = true;
                        addToCode("A9");
                        addToCode("00");
                        addToCode("8D");
                        addToCode("T1");
                        addToCode("XX");
                        while(isntDone2) {
                            //If it isn't an ID
                            if(!isValidChar(AST.get(i).substring(1, 2))) {
                                addToCode("A9");
                                addToCode(num2hex(Integer.valueOf(AST.get(i).substring(1, 2))));
                                addToCode("6D");
                                addToCode("T1");
                                addToCode("XX");
                                addToCode("8D");
                                addToCode("T1");
                                addToCode("XX");
                            //If it is an ID
                            } else {
                                tempVar tempVariable2 = isInTempTable(stack, AST.get(i).substring(1, 2), scope, findScopeLetter(scope));
                                addToCode("AD");
                                addToCode(tempVariable2.temp.substring(0, 2));
                                addToCode("XX");
                                addToCode("6D");
                                addToCode("T1");
                                addToCode("XX");
                                addToCode("8D");
                                addToCode("T1");
                                addToCode("XX");
                            }
                            if(AST.get(i + 1).length() > 3) {
                                isntDone2 = false;
                            } else {
                                i++;
                            }
                        }
                        isntDone = false;
                    } else {
                        i++;
                    }
                }
                addToCode("AE");
                addToCode("T0");
                addToCode("XX");
                addToCode("EC");
                addToCode("T1");
                addToCode("XX");
                addToCode("D0");
                //Branch this # of bytes if the Left and Right sides don't match
                addToCode(branchVar.temp);
            } else if(AST.get(i).equals("<While Statement>")) {

            } else if(AST.get(i).equals("<Print Statement>")) {
                i++;
                tempVar tempVariable = isInTempTable(stack, AST.get(i).substring(1, 2), scope, findScopeLetter(scope));
                addToCode("A2");
                addToCode("01");
                addToCode("AC");
                addToCode(tempVariable.temp.substring(0, 2));
                addToCode("XX");
                addToCode("FF");
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

    //Helper function to tell if a given char ( in string format ) is valid. valid meaning a-z lowercase.
    private boolean isValidChar(String x) {
        for(int j = 97; j < 123; j++) {
          if(x.equals((char)j + "")) {
            return true;
          }
        }
        return false;
      }
    
}