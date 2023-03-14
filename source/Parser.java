package source;

import java.util.ArrayList;

public class Parser {

    private int currentTokenPos = 0;
    private String currentToken;
    ArrayList<Token> thisTokenStream;
    private int prgCounter = 0;
    private boolean isntError;

    public void main(ArrayList<Token> tokenStream) {
        prgCounter++;
        isntError = true;
        thisTokenStream = tokenStream;
        System.out.println();
        debug("Parsing program " + prgCounter + "...");
        System.out.print("Token Stream : ( ");
        for(int i = 0; i < thisTokenStream.size(); i++) {
            System.out.print(thisTokenStream.get(i).type + " ");
        }
        currentToken = thisTokenStream.get(currentTokenPos).type;
        System.out.println(" )");
        parse();
    }

    private void parse() {
        if(isntError) {
            debug("parse()");
            parsePrg();
        }
    }

    private void parsePrg() {
        if(isntError) {
            debug("parseProgram()");
            parseBlock();
            match("EOP");
        }
    }

    private void parseBlock() {
        if(isntError) {
            debug("parseBlock()");
            match("OPEN_BLOCK");
            parseStmtList();
            match("CLOSE_BLOCK");
        }
    }

    private void parseStmtList() {
        if(isntError) {
            debug("parseStatementList()");
            if(currentToken == "PRINT_STMT" | currentToken == "ID" | currentToken == "TYPE_INT" | currentToken == "TYPE_STR" | currentToken == "TYPE_BOOL" | currentToken == "WHILE_STMT" |
            currentToken == "IF_STMT" | currentToken == "OPEN_BLOCK") {
                parseStmt();
                parseStmtList();
            } else {
                //do nothing
            }
        }
    }

    private void parseStmt() {
        if(isntError) {
            debug("parseStatement()");
            if(currentToken == "PRINT_STMT") {
                parsePrintStmt();
            } else if(currentToken == "ID") {
                parseAssignStmt();
            } else if(currentToken == "TYPE_INT" | currentToken == "TYPE_STR" | currentToken == "TYPE_BOOL") {
                parseVarDecl();
            } else if(currentToken == "WHILE_STMT") {
                parseWhileStmt();
            } else if(currentToken == "IF_STMT") {
                parseIfStmt();
            } else if(currentToken == "OPEN_BLOCK") {
                parseBlock();
            }
        }
    }

    private void parsePrintStmt() {
        if(isntError) {
            debug("parsePrintStatement()");
            match("PRINT_STMT");
            match("OPEN_PAREN");
            parseExpr();
            match("CLOSE_PAREN");
        }
    }

    private void parseAssignStmt() {
        if(isntError) {
            debug("parseAssignmentStatement()");
            parseId();
            match("ASSIGN_OP");
            parseExpr();
        }
    }

    private void parseVarDecl() {
        if(isntError) {
            debug("parseVariableDeclaration()");
            parseType();
            parseId();
        }
    }

    private void parseWhileStmt() {
        if(isntError) {
            debug("parseWhileStatement()");
            match("WHILE_STMT");
            parseBoolExpr();
            parseBlock();
        }
    }

    private void parseIfStmt() {
        if(isntError) {
            debug("parseIfStatement()");
            match("IF_STMT");
            parseBoolExpr();
            parseBlock();
        }
    }
    
    private void parseExpr() {
        if(isntError) {
            debug("parseExpression()");
            if(currentToken == "DIGIT") {
                parseIntExpr();
            } else if(currentToken == "OPEN_STR") {
                parseStringExpr();
            } else if(currentToken == "OPEN_PAREN") {
                parseBoolExpr();
            } else if(currentToken == "ID") {
                parseId();
            }
        }
    }

    private void parseIntExpr() {
        if(isntError) {
            debug("parseIntegerExpression()");
            match("DIGIT");
            if(currentToken == "INTOP") {
                parseIntop();
                parseExpr();
            }
        }
    }

    private void parseStringExpr() {
        if(isntError) {
            debug("parseStringExpression()");
            match("OPEN_STR");
            parseCharList();
            match("CLOSE_STR");
        }
    }

    private void parseBoolExpr() {
        if(isntError) {
            debug("parseBoolExpression()");
            if(currentToken == "OPEN_PAREN") {
                match("OPEN_PAREN");
                parseExpr();
                parseBoolop();
                parseExpr();
                match("CLOSE_PAREN");
            } else {
                parseBoolval();
            }
        }
    }

    private void parseId() {
        if(isntError) {
            debug("parseId()");
            match("ID");
        }
    }

    private void parseCharList() {
        if(isntError) {
            debug("parseCharList()");
            if(currentToken.length() >= 4) {
                if(currentToken.substring(0, 3) == "CHAR") {
                    match("CHAR");
                    parseCharList();
                } else if(currentToken == "_CHAR_SPACE") {
                    match("_CHAR_SPACE");
                    parseCharList();
                } else {
                    //nothing
                }
            } else {
                //nothing
            }
        }
    }

    private void parseType() {
        if(isntError) {
            debug("parseType()");
            if(currentToken == "TYPE_INT") {
                match("TYPE_INT");
            } else if(currentToken == "TYPE_STR") {
                match("TYPE_STR");
            } else if(currentToken == "TYPE_BOOL") {
                match("TYPE_BOOL");
            }
        }
    }

    private void parseChar() {
        if(isntError) {
            debug("parseChar()");
            match("CHAR");
        }
    }

    private void parseSpace() {
        if(isntError) {
            debug("parseSpace()");
            match("_CHAR_SPACE");
        }
    }

    private void parseDigit() {
        if(isntError) {
            debug("parseDigit()");
            match("DIGIT");
        }
    }

    private void parseBoolop() {
        if(isntError) {
            debug("parseBoolOperation()");
            match("BOOL_OP");
        }
    }

    private void parseBoolval() {
        if(isntError) {
            debug("parseBoolValue()");
            match("BOOL_VAL");
        }
    }

    private void parseIntop() {
        if(isntError) {
            debug("parseIntegerOperation()");
            match("INTOP");
        }
    }

    private void match(String expectedToken) {
        if(isntError) {
            if(currentToken.length() >= 4) {
                if(currentToken.substring(0, 3) == "CHAR") {
                    currentToken = currentToken.substring(0, 3);
                }
            }
            if(currentToken == expectedToken) {
                if(expectedToken == "EOP") {
                    debug("Parse completed successfully");
                    System.out.println();
                } else {
                    currentTokenPos++;
                }
                currentToken = thisTokenStream.get(currentTokenPos).type;
            } else {
                debug("ERROR: Found token: " + currentToken + ", Expected token: " + expectedToken);
                debug("Parse failed with 1 error");
                System.out.println();
                System.out.println("CST for program " + prgCounter + ": Skipped due to PARSER error(s).");
                System.out.println();
                isntError = false;
            }
        }
    }

    private void debug(String message) {
        System.out.println("PARSER: " + message);
    }
}
