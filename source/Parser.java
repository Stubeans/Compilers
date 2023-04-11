package source;

import java.util.ArrayList;

public class Parser {

    private int currentTokenPos;
    private String currentToken;
    ArrayList<Token> thisTokenStream;
    private int prgCounter = 0;
    private boolean isntError;
    ArrayList<String> CST = new ArrayList<>();
    ArrayList<Integer> CSTdepth = new ArrayList<>();
    int depth;

    SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();

    public void main(ArrayList<Token> tokenStream) {
        //For each tokenstream, reset all the variables (except prgCounter which we increment)
        //Display the current program, and the tokenStream recieved, and then start the recursive descent parser with the tokenStream recieved
        depth = 0;
        prgCounter++;
        isntError = true;
        thisTokenStream = tokenStream;
        currentTokenPos = 0;
        System.out.println();
        debug("Parsing program " + prgCounter + "...");
        System.out.print("Token Stream Received : ( ");
        for(int i = 0; i < thisTokenStream.size(); i++) {
            if(!(thisTokenStream.get(i).type).equals("EOP")) {
                System.out.print(thisTokenStream.get(i).type + ", ");
            } else {
                System.out.print(thisTokenStream.get(i).type + " ");
            }
            
        }
        currentToken = thisTokenStream.get(currentTokenPos).type;
        System.out.println(")");
        parse();
        semanticAnalyzer.main(thisTokenStream);
    }

    //Begins the parse
    private void parse() {
        //isntError is here to ensure that when an error is found, we don't keep recursively traversing our tokenStream.
        //Or rather, technically, that we don't run the code inside the functions that are being recursively traversed.
        if(isntError) {
            debug("parse()");
            parsePrg();
        }
    }

    //Function names self explanatory
    private void parsePrg() {
        if(isntError) {
            //Add the current production to the CST, assign it's depth, then increment the depth counter
            CST.add("<Program>");
            CSTdepth.add(depth);
            depth++;
            debug("parseProgram()");
            parseBlock();
            match("EOP");
        }
    }

    private void parseBlock() {
        if(isntError) {
            CST.add("<Block>");
            CSTdepth.add(depth);
            depth++;
            debug("parseBlock()");
            match("OPEN_BLOCK");
            parseStmtList();
            match("CLOSE_BLOCK");
            //At the end of every function ( except parsePrg ) decrement the depth counter
            depth--;
        }
    }

    private void parseStmtList() {
        if(isntError) {
            CST.add("<Statement List>");
            CSTdepth.add(depth);
            depth++;
            debug("parseStatementList()");
            //If the token is something we expect:
            if(currentToken.equals("PRINT_STMT") | currentToken.equals("ID") | currentToken.equals("TYPE_INT") | currentToken.equals("TYPE_STR") | 
            currentToken.equals("TYPE_BOOL") | currentToken.equals("WHILE_STMT") | currentToken.equals("IF_STMT") | currentToken.equals("OPEN_BLOCK")) {
                parseStmt();
                parseStmtList();
            } else { //If there is no token
                //do nothing
            }
            depth--;
        }
    }

    private void parseStmt() {
        if(isntError) {
            CST.add("<Statement>");
            CSTdepth.add(depth);
            depth++;
            debug("parseStatement()");
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
                debug("ERROR: Found token: " + currentToken + ", Expected statement on line " + thisTokenStream.get(currentTokenPos).pos);
                debug("Parse failed with 1 error");
                System.out.println();
                System.out.println("CST for program " + prgCounter + ": Skipped due to PARSER error(s).");
                System.out.println();
                isntError = false;
            }
            depth--;
        }
    }

    private void parsePrintStmt() {
        if(isntError) {
            CST.add("<Print Statement>");
            CSTdepth.add(depth);
            depth++;
            debug("parsePrintStatement()");
            match("PRINT_STMT");
            match("OPEN_PAREN");
            parseExpr();
            match("CLOSE_PAREN");
            depth--;
        }
    }

    private void parseAssignStmt() {
        if(isntError) {
            CST.add("<Assign Statement>");
            CSTdepth.add(depth);
            depth++;
            debug("parseAssignmentStatement()");
            parseId();
            match("ASSIGN_OP");
            parseExpr();
            depth--;
        }
    }

    private void parseVarDecl() {
        if(isntError) {
            CST.add("<Variable Declaration>");
            CSTdepth.add(depth);
            depth++;
            debug("parseVariableDeclaration()");
            parseType();
            parseId();
            depth--;
        }
    }

    private void parseWhileStmt() {
        if(isntError) {
            CST.add("<While Statement>");
            CSTdepth.add(depth);
            depth++;
            debug("parseWhileStatement()");
            match("WHILE_STMT");
            parseBoolExpr();
            parseBlock();
            depth--;
        }
    }

    private void parseIfStmt() {
        if(isntError) {
            CST.add("<If Statement>");
            CSTdepth.add(depth);
            depth++;
            debug("parseIfStatement()");
            match("IF_STMT");
            parseBoolExpr();
            parseBlock();
            depth--;
        }
    }
    
    private void parseExpr() {
        if(isntError) {
            CST.add("<Expression>");
            CSTdepth.add(depth);
            depth++;
            debug("parseExpression()");
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
                debug("ERROR: Found token: " + currentToken + ", Expected an expression on line " + thisTokenStream.get(currentTokenPos).pos);
                debug("Parse failed with 1 error");
                System.out.println();
                System.out.println("CST for program " + prgCounter + ": Skipped due to PARSER error(s).");
                System.out.println();
                isntError = false;
            }
            depth--;
        }
    }

    private void parseIntExpr() {
        if(isntError) {
            CST.add("<Integer Expression>");
            CSTdepth.add(depth);
            depth++;
            debug("parseIntegerExpression()");
            parseDigit();
            if(currentToken == "INTOP") {
                parseIntop();
                parseExpr();
            }
            depth--;
        }
    }

    private void parseStringExpr() {
        if(isntError) {
            CST.add("<String Expression>");
            CSTdepth.add(depth);
            depth++;
            debug("parseStringExpression()");
            match("OPEN_STR");
            parseCharList();
            match("CLOSE_STR");
            depth--;
        }
    }

    private void parseBoolExpr() {
        if(isntError) {
            CST.add("<Boolean Expression>");
            CSTdepth.add(depth);
            depth++;
            debug("parseBoolExpression()");
            if(currentToken.equals("OPEN_PAREN")) {
                match("OPEN_PAREN");
                parseExpr();
                parseBoolop();
                parseExpr();
                match("CLOSE_PAREN");
            } else if(currentToken.equals("BOOL_VAL")){
                parseBoolval();
            } else {
                debug("ERROR: Found token: " + currentToken + ", Expected boolean expression on line " + thisTokenStream.get(currentTokenPos).pos);
                debug("Parse failed with 1 error");
                System.out.println();
                System.out.println("CST for program " + prgCounter + ": Skipped due to PARSER error(s).");
                System.out.println();
                isntError = false;
            }
            depth--;
        }
    }

    private void parseId() {
        if(isntError) {
            CST.add("<Id>");
            CSTdepth.add(depth);
            depth++;
            debug("parseId()");
            match("ID");
            depth--;
        }
    }

    private void parseCharList() {
        if(isntError) {
            CST.add("<Char List>");
            CSTdepth.add(depth);
            depth++;
            debug("parseCharList()");
            if(currentToken.length() >= 4) {
                if(currentToken.substring(0, 4).equals("CHAR")) {
                    parseChar();
                    parseCharList();
                } else if(currentToken.equals("_CHAR_SPACE")) {
                    parseSpace();
                    parseCharList();
                } else {
                    //nothing
                }
            } else {
                //nothing
            }
            depth--;
        }
    }

    private void parseType() {
        if(isntError) {
            CST.add("<Type>");
            CSTdepth.add(depth);
            depth++;
            debug("parseType()");
            if(currentToken.equals("TYPE_INT")) {
                match("TYPE_INT");
            } else if(currentToken.equals("TYPE_STR")) {
                match("TYPE_STR");
            } else if(currentToken.equals("TYPE_BOOL")) {
                match("TYPE_BOOL");
            } else {
                debug("ERROR: Found token: " + currentToken + ", Expected a type on line " + thisTokenStream.get(currentTokenPos).pos);
                debug("Parse failed with 1 error");
                System.out.println();
                System.out.println("CST for program " + prgCounter + ": Skipped due to PARSER error(s).");
                System.out.println();
                isntError = false;
            }
            depth--;
        }
    }

    private void parseChar() {
        if(isntError) {
            CST.add("<Char>");
            CSTdepth.add(depth);
            depth++;
            debug("parseChar()");
            match("CHAR");
            depth--;
        }
    }

    private void parseSpace() {
        if(isntError) {
            CST.add("<Space>");
            CSTdepth.add(depth);
            depth++;
            debug("parseSpace()");
            match("_CHAR_SPACE");
            depth--;
        }
    }

    private void parseDigit() {
        if(isntError) {
            CST.add("<Digit>");
            CSTdepth.add(depth);
            depth++;
            debug("parseDigit()");
            match("DIGIT");
            depth--;
        }
    }

    private void parseBoolop() {
        if(isntError) {
            CST.add("<Boolean Operation>");
            CSTdepth.add(depth);
            depth++;
            debug("parseBoolOperation()");
            match("BOOL_OP");
            depth--;
        }
    }

    private void parseBoolval() {
        if(isntError) {
            CST.add("<Boolean Value>");
            CSTdepth.add(depth);
            depth++;
            debug("parseBoolValue()");
            match("BOOL_VAL");
            depth--;
        }
    }

    private void parseIntop() {
        if(isntError) {
            CST.add("<Integer Operation>");
            CSTdepth.add(depth);
            depth++;
            debug("parseIntegerOperation()");
            match("INTOP");
            depth--;
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
                CST.add("[" + thisTokenStream.get(currentTokenPos).val + "]");
                CSTdepth.add(depth);
                if(expectedToken.equals("EOP")) {
                    //AND the token is the EOP, end the program and display the CST. Theoretically, if there is an error this code will never be reached.
                    debug("Parse completed successfully");
                    System.out.println();
                    displayCST();
                } else {
                    //Increment the currentTokenPos and set the current token to the token in the stream at currentTokenPos.
                    currentTokenPos++;
                }
                currentToken = thisTokenStream.get(currentTokenPos).type;
            } else {
                debug("ERROR: Found token: " + currentToken + ", Expected token: " + expectedToken + " on line " + thisTokenStream.get(currentTokenPos).pos);
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

    //This code handles displaying the CST. It runs thru each element in the CST, and displays it using it's corresponding Depth value in the CSTdepth array.
    //After the CST is displayed, clear both arrays, and reset the depth back to 0. ( this is why we skip decrementing depth in parsePrg() )
    private void displayCST() {
        System.out.println("CST for program " + prgCounter + "...");
        String leading = "";
        for(int i = 0; i < CST.size(); i++) {
            for(int j = 0; j < CSTdepth.get(i); j++) {
                leading = leading + "-";
            }
            System.out.println(leading + CST.get(i));
            leading = "";
        }
        System.out.println();
        CST.clear();
        CSTdepth.clear();
        depth = 0;
    }
}
