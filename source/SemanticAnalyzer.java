package source;

import java.util.ArrayList;

public class SemanticAnalyzer {

    private int currentTokenPos;
    private String currentToken;
    ArrayList<Token> thisTokenStream;
    private int prgCounter = 0;
    private boolean isntError;
    private int depth = 0;
    private String currentString = "";
    private int scope = -1;
    private boolean decState = false;

    private ArrayList<String> AST = new ArrayList<>();
    private ArrayList<Integer> ASTdepth = new ArrayList<>();

    private ArrayList<Symbol> symbolTable = new ArrayList<>();

    public void main(ArrayList<Token> tokenStream) {

        depth = 0;
        prgCounter++;
        isntError = true;
        thisTokenStream = tokenStream;
        currentTokenPos = 0;
        currentString = "";
        scope = -1;

        System.out.println();
        debug("STARTING SEMANTIC ANALYSIS ON PROGRAM " + prgCounter + ".");
        System.out.println();

        currentToken = thisTokenStream.get(currentTokenPos).type;
        parse();
        if(isntError) {
            printAST();
            printSymbolTable();
        }

        AST.clear();
        ASTdepth.clear();
        symbolTable.clear();

        //symbolTable.add(new Symbol("0", "type", 0, symbolTable));

    }

    //Begins the parse
    private void parse() {
        //isntError is here to ensure that when an error is found, we don't keep recursively traversing our tokenStream.
        //Or rather, technically, that we don't run the code inside the functions that are being recursively traversed.
        if(isntError) {
            parsePrg();
        }
    }

    //Function names self explanatory
    private void parsePrg() {
        if(isntError) {
            parseBlock();
            if(currentTokenPos+1 != thisTokenStream.size()) {
                currentTokenPos++;
                currentToken = thisTokenStream.get(currentTokenPos).type;
            } else { //End of file code
                System.out.println();
                debug("AST completed successfully");
                System.out.println();
            }
        }
    }

    private void parseBlock() {
        if(isntError) {
            AST.add("<Block>");
            ASTdepth.add(depth);
            depth++;
            scope++;
            debug("parseBlock()");
            currentTokenPos++;
            currentToken = thisTokenStream.get(currentTokenPos).type;
            parseStmtList();
            currentTokenPos++;
            currentToken = thisTokenStream.get(currentTokenPos).type;
            //At the end of every function ( except parsePrg ) decrement the depth counter
            depth--;
            scope--;
        }
    }

    private void parseStmtList() {
        if(isntError) {
            //If the token is something we expect:
            if(currentToken.equals("PRINT_STMT") | currentToken.equals("ID") | currentToken.equals("TYPE_INT") | currentToken.equals("TYPE_STR") | 
            currentToken.equals("TYPE_BOOL") | currentToken.equals("WHILE_STMT") | currentToken.equals("IF_STMT") | currentToken.equals("OPEN_BLOCK")) {
                parseStmt();
                parseStmtList();
            } else { //If there is no token
                //do nothing
            }
        }
    }

    private void parseStmt() {
        if(isntError) {
            //If it's something we expect
            if(currentToken.equals("PRINT_STMT")) {
                parsePrintStmt();
            } else if(currentToken.equals("ID")) {
                parseAssignStmt();
            } else if(currentToken.equals("TYPE_INT") | currentToken.equals("TYPE_STR") | currentToken.equals("TYPE_BOOL")) {
                parseVarDecl();
            } else if(currentToken.equals("WHILE_STMT")) {
                parseWhileStmt();
            } else if(currentToken.equals("IF_STMT")) {
                parseIfStmt();
            } else if(currentToken.equals("OPEN_BLOCK")) {
                parseBlock();
            } else { //Else display an error saying what we got, and what we expected
                isntError = false;
            }
        }
    }

    private void parsePrintStmt() {
        if(isntError) {
            AST.add("<Print Statement>");
            ASTdepth.add(depth);
            depth++;
            debug("parsePrintStatement()");
            currentTokenPos++;
            currentToken = thisTokenStream.get(currentTokenPos).type;
            currentTokenPos++;
            currentToken = thisTokenStream.get(currentTokenPos).type;
            parseExpr();
            currentTokenPos++;
            currentToken = thisTokenStream.get(currentTokenPos).type;
            depth--;
        }
    }

    private void parseAssignStmt() {
        if(isntError) {
            AST.add("<Assign Statement>");
            ASTdepth.add(depth);
            depth++;
            debug("parseAssignmentStatement()");
            parseId();
            currentTokenPos++;
            currentToken = thisTokenStream.get(currentTokenPos).type;
            parseExpr();
            depth--;
        }
    }

    private void parseVarDecl() {
        if(isntError) {
            AST.add("<Variable Declaration>");
            ASTdepth.add(depth);
            depth++;
            debug("parseVariableDeclaration()");
            parseType();
            decState = true;
            parseId();
            // if(symbolTable.get(symbolTable.size()-1)) {

            // }
            depth--;
        }
    }

    private void parseWhileStmt() {
        if(isntError) {
            AST.add("<While Statement>");
            ASTdepth.add(depth);
            depth++;
            debug("parseWhileStatement()");
            currentTokenPos++;
            currentToken = thisTokenStream.get(currentTokenPos).type;
            parseBoolExpr();
            parseBlock();
            depth--;
        }
    }

    private void parseIfStmt() {
        if(isntError) {
            AST.add("<If Statement>");
            ASTdepth.add(depth);
            depth++;
            debug("parseIfStatement()");
            currentTokenPos++;
            currentToken = thisTokenStream.get(currentTokenPos).type;
            parseBoolExpr();
            parseBlock();
            depth--;
        }
    }
    
    private void parseExpr() {
        if(isntError) {
            if(currentToken.equals("DIGIT")) {
                parseIntExpr();
            } else if(currentToken.equals("OPEN_STR")) {
                parseStringExpr();
            } else if(currentToken.equals("OPEN_PAREN")) {
                parseBoolExpr();
            } else if(currentToken.equals("BOOL_VAL")) {
                parseBoolval();
            } else if(currentToken.equals("ID")) {
                parseId();
            } else {
                isntError = false;
            }
        }
    }

    private void parseIntExpr() {
        if(isntError) {
            parseDigit();
            if(currentToken == "INTOP") {
                parseIntop();
                parseExpr();
            }
        }
    }

    private void parseStringExpr() {
        if(isntError) {
            currentTokenPos++;
            currentToken = thisTokenStream.get(currentTokenPos).type;
            parseCharList();
            currentTokenPos++;
            currentToken = thisTokenStream.get(currentTokenPos).type;
        }
    }

    private void parseBoolExpr() {
        if(isntError) {
            if(currentToken.equals("OPEN_PAREN")) {
                currentTokenPos++;
                currentToken = thisTokenStream.get(currentTokenPos).type;
                parseExpr();
                parseBoolop();
                parseExpr();
                currentTokenPos++;
                currentToken = thisTokenStream.get(currentTokenPos).type;
            } else if(currentToken.equals("BOOL_VAL")){
                parseBoolval();
            } else {
                isntError = false;
            }
        }
    }

    private void parseId() {
        if(isntError) {
            match("ID");

        }
    }

    private void parseCharList() {
        if(isntError) {
            if(currentToken.length() >= 4) {
                if(currentToken.substring(0, 4).equals("CHAR")) {
                    parseChar();
                    parseCharList();
                } else if(currentToken.equals("_CHAR_SPACE")) {
                    parseSpace();
                    parseCharList();
                } else {
                    AST.add("[" + currentString + "]");
                    ASTdepth.add(depth);
                    currentString = "";
                }
            } else {
                //nothing
            }
        }
    }

    private void parseType() {
        if(isntError) {
            if(currentToken.equals("TYPE_INT")) {
                match("TYPE_INT");
            } else if(currentToken.equals("TYPE_STR")) {
                match("TYPE_STR");
            } else if(currentToken.equals("TYPE_BOOL")) {
                match("TYPE_BOOL");
            } else {
                isntError = false;
            }
        }
    }

    private void parseChar() {
        if(isntError) {
            currentString = currentString + thisTokenStream.get(currentTokenPos).val;
            currentTokenPos++;
            currentToken = thisTokenStream.get(currentTokenPos).type;
        }
    }

    private void parseSpace() {
        if(isntError) {
            currentString = currentString + thisTokenStream.get(currentTokenPos).val;
            currentTokenPos++;
            currentToken = thisTokenStream.get(currentTokenPos).type;
        }
    }

    private void parseDigit() {
        if(isntError) {
            match("DIGIT");
        }
    }

    private void parseBoolop() {
        if(isntError) {
            // AST.add("<Boolean Operation>");
            // ASTdepth.add(depth);
            // depth++;
            // debug("parseBoolOperation()");
            currentTokenPos++;
            currentToken = thisTokenStream.get(currentTokenPos).type;
            // depth--;
        }
    }

    private void parseBoolval() {
        if(isntError) {
            match("BOOL_VAL");
        }
    }

    private void parseIntop() {
        if(isntError) {
            // AST.add("<Integer Operation>");
            // ASTdepth.add(depth);
            // depth++;
            // debug("parseIntegerOperation()");
            currentTokenPos++;
            currentToken = thisTokenStream.get(currentTokenPos).type;
            // depth--;
        }
    }

    private void match(String expectedToken) {
        if(isntError) {
            //If the token is a CHAR_<A-Z>, just cut it down to CHAR for the match 
            if(currentToken.length() >= 4) {
                if(currentToken.substring(0, 4).equals("CHAR")) {
                    currentToken = currentToken.substring(0, 4);
                }
            }
            //If the token does match,
            if(currentToken.equals(expectedToken)) {
                AST.add("[" + thisTokenStream.get(currentTokenPos).val + "]");
                ASTdepth.add(depth);
                if(expectedToken.equals("EOP")) {
                    //AND the token is the EOP, end the program and display the CST. Theoretically, if there is an error this code will never be reached.
                    debug("Semantic completed successfully");
                    System.out.println();
                } else if(expectedToken.equals("ID")){
                    //If we're not assigning the ID
                    if(decState != true) {
                        if(isInTable(symbolTable, thisTokenStream.get(currentTokenPos).val) != null) {
                            //If it exists below me
                            if(isInTable(symbolTable, thisTokenStream.get(currentTokenPos).val).scope < scope) {
                                isInTable(symbolTable, thisTokenStream.get(currentTokenPos).val).isUsed = true;
                            //If it exists above of me
                            } else if(isInTable(symbolTable, thisTokenStream.get(currentTokenPos).val).scope > scope) {
                                System.out.println("ERROR: The variable " + thisTokenStream.get(currentTokenPos).val + " isn't yet declared!");
                                debug("Semantic failed with 1 error");
                                System.out.println();
                                System.out.println("AST and Symbol Table for program " + prgCounter + ": Skipped due to PARSER error(s).");
                                System.out.println();
                                isntError = false;
                            //If it exists parrallel to me
                            } else {
                                isInTable(symbolTable, thisTokenStream.get(currentTokenPos).val).isUsed = true;
                            }
                        } else {
                            System.out.println("ERROR: The variable " + thisTokenStream.get(currentTokenPos).val + " isn't yet declared!");
                            debug("Semantic failed with 1 error");
                            System.out.println();
                            System.out.println("AST and Symbol Table for program " + prgCounter + ": Skipped due to PARSER error(s).");
                            System.out.println();
                            isntError = false;
                        }
                    //If we ARE assigning the ID
                    } else {
                        if(isInTable(symbolTable, thisTokenStream.get(currentTokenPos).val) != null) {
                            //If it exists below me
                            if(isInTable(symbolTable, thisTokenStream.get(currentTokenPos).val).scope < scope) {
                                symbolTable.add(new Symbol(thisTokenStream.get(currentTokenPos).val, "ID", scope));
                                symbolTable.get(symbolTable.size()-1).isInit = true;
                            //If it exists above of me
                            } else if(isInTable(symbolTable, thisTokenStream.get(currentTokenPos).val).scope > scope) {
                                symbolTable.add(new Symbol(thisTokenStream.get(currentTokenPos).val, "ID", scope));
                                symbolTable.get(symbolTable.size()-1).isInit = true;
                            //If it exists parrallel to me
                            } else {
                                System.out.println("ERROR: The variable " + thisTokenStream.get(currentTokenPos).val + " is already declared in this scope!");
                                debug("Semantic failed with 1 error");
                                System.out.println();
                                System.out.println("AST and Symbol Table for program " + prgCounter + ": Skipped due to PARSER error(s).");
                                System.out.println();
                                isntError = false;
                            }
                        } else {
                            symbolTable.add(new Symbol(thisTokenStream.get(currentTokenPos).val, "ID", scope));
                            symbolTable.get(symbolTable.size()-1).isInit = true;
                        }
                        decState = false;
                    }
                    //Increment the currentTokenPos and set the current token to the token in the stream at currentTokenPos.
                    currentTokenPos++;
                } else {
                    //Increment the currentTokenPos and set the current token to the token in the stream at currentTokenPos.
                    currentTokenPos++;
                }
                currentToken = thisTokenStream.get(currentTokenPos).type;
            } else {
                debug("ERROR: Found token: " + currentToken + ", Expected token: " + expectedToken + " on line " + thisTokenStream.get(currentTokenPos).pos);
                debug("Semantic failed with 1 error");
                System.out.println();
                System.out.println("AST for program " + prgCounter + ": Skipped due to PARSER error(s).");
                System.out.println();
                isntError = false;
            }
        }
    }

    private Symbol isInTable(ArrayList<Symbol> checkST, String checkName) {
        for(int i = 0; i < checkST.size(); i++) {
            if(checkST.get(i).name.equals(checkName)) {
                return checkST.get(i);
            }
        }
        return null;
    }

    private void test() {
        String scopeLetter = "";
        int num = 0;
        for(int i = 0; i < symbolTable.size(); i++) {
            if(symbolTable.get(i).scope == scope) {
                num++;
            }
        }
        scopeLetter = (char)(97 + num) + "";
        num = 0;
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

    private void printSymbolTable() {
        System.out.println("Name | Type | isInit? | isUsed? | Scope");
        for(int i = 0; i < symbolTable.size(); i++) {
            System.out.println(symbolTable.get(i).name + "|" + symbolTable.get(i).type + "|" + symbolTable.get(i).isInit + "|" + symbolTable.get(i).isUsed + "|" + symbolTable.get(i).scope + symbolTable.get(i).scopeLetter);
        }
    }

    private void debug(String message) {
        System.out.println("SEMANTIC: " + message);
    }
}
