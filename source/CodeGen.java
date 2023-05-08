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
        if(opCodes.size() > 256 || codePointer > 256 || heapPointer < codePointer) {
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
                //System.out.println(AST.get(i)); -> <VarDecl>
                i++;
                //System.out.println(AST.get(i)); -> [type]
                i++;
                //System.out.println(AST.get(i)); -> [id]
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
                Symbol tempSymbol = isInTableS(symbolTable, AST.get(i).substring(1, 2), scope, findScopeLetter(scope));
                i++;
                boolean isntDone = true;
                //IF ITS AN INT
                if(tempSymbol.type.equals("int")) {
                    addToCode("A9");
                    addToCode(num2hex(Integer.valueOf(AST.get(i).substring(1, 2))));
                    addToCode("8D");
                    addToCode("T0");
                    addToCode("XX");
                    if(!(AST.get(i + 1).length() > 3)) {
                        i++;
                        while(isntDone) {
                            //If it isn't an ID
                            if(!isValidChar(AST.get(i).substring(1, 2))) {
                                addToCode("A9");
                                addToCode(num2hex(Integer.valueOf(AST.get(i).substring(1, 2))));
                                addToCode("6D");
                                addToCode("T0");
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
                                addToCode("T0");
                                addToCode("XX");
                                addToCode("8D");
                                addToCode(tempVariable.temp.substring(0, 2));
                                addToCode("XX");
                            }
                            if(AST.get(i + 1).length() > 3 || (AST.size() - 1) == i) {
                                addToCode("8D");
                                addToCode(tempVariable.temp.substring(0, 2));
                                addToCode("XX");
                                isntDone = false;
                            } else {
                                i++;
                            }
                        }
                    } else {
                        addToCode("8D");
                        addToCode(tempVariable.temp.substring(0, 2));
                        addToCode("XX");
                    }
                //IF ITS A STRING
                } else if(tempSymbol.type.equals("string")) {
                    String inString = AST.get(i).substring(1, AST.get(i).length()-1);
                    addToCode("A9");
                    addToCode("00");
                    addToCode("8D");
                    addToCode(num2hex(heapPointer));
                    heapPointer--;
                    addToCode("00");
                    for(int j = inString.length(); j > 0; j--) {
                        addToCode("A9");
                        addToCode(num2hex((int)inString.charAt(j-1)));
                        addToCode("8D");
                        addToCode(num2hex(heapPointer));
                        heapPointer--;
                        addToCode("00");
                    }
                    addToCode("A9");
                    addToCode(num2hex(heapPointer + 1));
                    addToCode("8D");
                    addToCode(tempVariable.temp.substring(0, 2));
                    addToCode("XX");
                } else if(tempSymbol.type.equals("boolean")) {
                    addToCode("A9");
                    if(AST.get(i).equals("[true]")) {
                        addToCode("01");
                    } else if(AST.get(i).equals("[false]")) {
                        addToCode("02");
                    }
                    addToCode("8D");
                    addToCode(tempVariable.temp.substring(0, 2));
                    addToCode("XX");
                }


            } else if(AST.get(i).equals("<If Statement>")) {
                i++;
                boolean isntDone = true;
                boolean negation = false;
                addToCode("A9");
                addToCode("00");
                addToCode("8D");
                addToCode("T0");
                addToCode("XX");
                while(isntDone) {
                    //If it isn't an ID
                    if(!isValidChar(AST.get(i).substring(1, 2)) || AST.get(i).equals("[true]") || AST.get(i).equals("[false]")) {
                        if(AST.get(i).equals("[true]") || AST.get(i).equals("[false]")) {
                            addToCode("A9");
                            if(AST.get(i).equals("[true]")) {
                                addToCode("01");
                            } else {
                                addToCode("00");
                            }
                            addToCode("6D");
                            addToCode("T0");
                            addToCode("XX");
                            addToCode("8D");
                            addToCode("T0");
                            addToCode("XX");
                        } else {
                            addToCode("A9");
                            addToCode(num2hex(Integer.valueOf(AST.get(i).substring(1, 2))));
                            addToCode("6D");
                            addToCode("T0");
                            addToCode("XX");
                            addToCode("8D");
                            addToCode("T0");
                            addToCode("XX");
                        }
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
                    if(AST.get(i + 1).length() > 3 && !AST.get(i + 1).equals("<==>") && !AST.get(i + 1).equals("<!=>")) {
                        isntDone = false;
                    } else if(AST.get(i + 1).equals("<==>") || AST.get(i + 1).equals("<!=>")) {
                        if(AST.get(i + 1).equals("<!=>")) {
                            negation = true;
                        }
                        i++;
                        i++;
                        boolean isntDone2 = true;
                        addToCode("A9");
                        addToCode("00");
                        addToCode("8D");
                        addToCode("T1");
                        addToCode("XX");
                        while(isntDone2) {
                            //If it isn't an ID
                            if(!isValidChar(AST.get(i).substring(1, 2)) || AST.get(i).equals("[true]") || AST.get(i).equals("[false]")) {
                                if(AST.get(i).equals("[true]") || AST.get(i).equals("[false]")) {
                                    addToCode("A9");
                                    if(AST.get(i).equals("[true]")) {
                                        addToCode("01");
                                    } else {
                                        addToCode("00");
                                    }
                                    addToCode("6D");
                                    addToCode("T1");
                                    addToCode("XX");
                                    addToCode("8D");
                                    addToCode("T1");
                                    addToCode("XX");
                                } else {
                                    addToCode("A9");
                                    addToCode(num2hex(Integer.valueOf(AST.get(i).substring(1, 2))));
                                    addToCode("6D");
                                    addToCode("T1");
                                    addToCode("XX");
                                    addToCode("8D");
                                    addToCode("T1");
                                    addToCode("XX");
                                }
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
                if(negation == true) {
                    addToCode("A9");
                    addToCode("00");
                    addToCode("D0");
                    addToCode("02");
                    addToCode("A9");
                    addToCode("01");
                    addToCode("A2");
                    addToCode("00");
                    addToCode("8D");
                    addToCode("T0");
                    addToCode("XX");
                    addToCode("EC");
                    addToCode("T0");
                    addToCode("XX");
                }
                addToCode("D0");
                tempVar branchVar = new tempVar("J" + Integer.toString(tempBranchNum), Integer.toString(codePointer), null, scope + 1, null);
                tempBranchNum++;
                //Branch this # of bytes if the Left and Right sides don't match
                addToCode(branchVar.temp);
                jumpTable.add(branchVar);
            } else if(AST.get(i).equals("<While Statement>")) {
                i++;
                boolean isntDone = true;
                boolean negation = false;
                int whileBegin = codePointer;
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
                    if(AST.get(i + 1).length() > 3 && !AST.get(i + 1).equals("<==>") && !AST.get(i + 1).equals("<!=>")) {
                        isntDone = false;
                    } else if(AST.get(i + 1).equals("<==>") || AST.get(i + 1).equals("<!=>")) {
                        if(AST.get(i + 1).equals("<!=>")) {
                            negation = true;
                        }
                        i++;
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
                if(negation == true) {
                    addToCode("A9");
                    addToCode("00");
                    addToCode("D0");
                    addToCode("02");
                    addToCode("A9");
                    addToCode("01");
                    addToCode("A2");
                    addToCode("00");
                    addToCode("8D");
                    addToCode("T0");
                    addToCode("XX");
                    addToCode("EC");
                    addToCode("T0");
                    addToCode("XX");
                }
                addToCode("D0");
                tempVar branchVar = new tempVar("J" + Integer.toString(tempBranchNum), Integer.toString(codePointer), null, scope + 1, Integer.toString(whileBegin));
                tempBranchNum++;
                //Branch this # of bytes if the Left and Right sides don't match
                addToCode(branchVar.temp);
                jumpTable.add(branchVar);

                //This goes after the loop. I'm stupid

                //addToCode("A9");
                //addToCode("01");
                //addToCode("8D");
                //addToCode("T0");
                //addToCode("XX");
                //addToCode("A2");
                //addToCode("02");
                //addToCode("EC");
                //addToCode("D0");
                //Branch this # to get back to the beginning of the loop
                //int goBack = (255 - codePointer) + whileBegin;
                //addToCode(num2hex(goBack));
            } else if(AST.get(i).equals("<Print Statement>")) {
                i++;
                tempVar tempVariable = isInTempTable(stack, AST.get(i).substring(1, 2), scope, findScopeLetter(scope));
                Symbol tempSymbol = isInTableS(symbolTable, AST.get(i).substring(1, 2), scope, findScopeLetter(scope));
                addToCode("A2");
                if(tempSymbol.type.equals("int")) {
                    addToCode("01");
                } else if(tempSymbol.type.equals("string")) {
                    addToCode("02");
                } else if(tempSymbol.type.equals("boolean")) {
                    addToCode("01");
                }
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
                for(int j = 0; j < jumpTable.size(); j++) {
                    if(jumpTable.get(j).scope > scope) {
                        //If its an If jump
                        if(jumpTable.get(j).scopeLetter == null) {
                            int spots = codePointer - Integer.valueOf(jumpTable.get(j).var) - 1;
                            jumpTable.get(j).address = Integer.toString(spots);
                            jumpTable.get(j).scope = -500;
                        //If it's a While jump
                        } else {
                            addToCode("A9");
                            addToCode("01");
                            addToCode("8D");
                            addToCode("T0");
                            addToCode("XX");
                            addToCode("A2");
                            addToCode("02");
                            addToCode("EC");
                            addToCode("T0");
                            addToCode("XX");
                            addToCode("D0");
                            //Branch this # to get back to the beginning of the loop
                            int goBack = (255 - codePointer) + Integer.valueOf(jumpTable.get(j).scopeLetter);
                            addToCode(num2hex(goBack));
                            int spots = codePointer - Integer.valueOf(jumpTable.get(j).var) - 1;
                            jumpTable.get(j).address = Integer.toString(spots);
                            jumpTable.get(j).scope = -500;
                        }
                        
                    }
                }
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

        //Also replace jump to values here
        for(int i = 0; i < jumpTable.size(); i++) {
            for(int j = 0; j < opCodes.size(); j++) {
                if(opCodes.get(j).equals("J" + Integer.toString(i))) {
                    opCodes.set(j, num2hex(Integer.valueOf(jumpTable.get(i).address)));
                }
            }
        }
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