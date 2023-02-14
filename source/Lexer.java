package source;

import java.io.Console;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

public class Lexer {
    public void main(String path) {
      String currentString = "";
      //never used
      String expectedString = "";
        try {
          System.out.println("INFO  LEXER - Lexing program 1...");
          File myObj = new File(path);
          Scanner myReader = new Scanner(myObj);
          int lineNum = 0;
          int errorsFound = 0;
          int prgCounter = 1;
          int lBrack = 0;
          int rBrack = 0;
          boolean isString = false;
          boolean isComment = false;
          //WHILE THERE ARE LINES LEFT
          while (myReader.hasNextLine()) {
            lineNum++;
            String line = myReader.nextLine();
            //FOR LINES IN FILE
            for(int i = 0; i < line.length(); i++) {
                //The current element that's being inspected. ( one or many chars stored as a string )
                currentString = currentString + line.charAt(i);
                //System.out.println(currentString);
                //IF COMMENTS ARE NOT ON
                if(isComment == false) {
                  //If cases that look for keywords/identifiers/etc.
                  //If a legitimate case is found, debug Tokens in output, and reset the currentString back to empty
                  if(currentString.equals("$")) {
                    //If the number of left brackets don't match the number of left, return an error on the current program
                    if(lBrack != rBrack) {
                      System.out.println("ERROR LEXER - Your brackets are uneven in program " + prgCounter + "!");
                      errorsFound++;
                    }
                    lBrack = 0;
                    rBrack = 0;
                    debug("DEBUG", "EOP [ $ ] found at ", lineNum, i+1);
                    System.out.println("INFO  LEXER - Lex completed with " + errorsFound + " errors");
                    errorsFound = 0;
                    prgCounter++;
                    //If there are more lines,
                    if(myReader.hasNextLine()) {
                      System.out.println("INFO  LEXER - Lexing program " + prgCounter + "...");
                    }
                    currentString = "";
                    // IF END OF FILE AND LAST CHAR IN CURRENT LINE
                    // In this case, it's already after the check for the '$' character, therefore someone did not end the file with a '$'
                  } else if(!myReader.hasNextLine() && line.length() == i + 1) {
                    debug("ERROR", "You forgot to put a $ at the end of your file", lineNum, i+1);
                  } else if(currentString.equals("{")) {
                    debug("DEBUG", "OPEN_BLOCK [ { ] found at ", lineNum, i+1);
                    currentString = "";
                    lBrack++;
                  } else if(currentString.equals("}")) {
                    debug("DEBUG", "CLOSE_BLOCK [ } ] found at ", lineNum, i+1);
                    currentString = "";
                    rBrack++;
                  } else if(currentString.equals("print")) {
                    debug("DEBUG", "PRINT_STMT [ print ] found at ", lineNum, i+1);
                    currentString = "";
                  } else if(currentString.equals("(")) {
                    debug("DEBUG", "OPEN_PAREN [ ( ] found at ", lineNum, i+1);
                    currentString = "";
                  } else if(currentString.equals(")")) {
                    debug("DEBUG", "CLOSE_PAREN [ ) ] found at ", lineNum, i+1);
                    currentString = "";
                  } else if(currentString.equals("=")) {
                    debug("DEBUG", "ASSIGN_OP [ = ] found at ", lineNum, i+1);
                    currentString = "";
                  } else if(currentString.equals("while")) {
                    debug("DEBUG", "WHILE_STMT [ while ] found at ", lineNum, i+1);
                    currentString = "";
                  } else if(currentString.equals("if")) {
                    debug("DEBUG", "IF_STMT [ if ] found at ", lineNum, i+1);
                    currentString = "";
                  } else if(currentString.equals("" + '"') && isString == false) {
                    debug("DEBUG", "OPEN_STR [ " + '"' + " ] found at ", lineNum, i+1);
                    isString = true;
                    currentString = "";
                    //currentString.equals("" + '"') && isString == true
                  } else if((line.charAt(i) + "").equals("" + '"') && isString == true) {
                    debug("DEBUG", "CLOSE_STR [ " + '"' + " ] found at ", lineNum, i+1);
                    isString = false;
                    currentString = "";
                  } else if(currentString.equals("int")) {
                    debug("DEBUG", "TYPE_INT [ int ] found at ", lineNum, i+1);
                    currentString = "";
                  } else if(currentString.equals("string")) {
                    debug("DEBUG", "TYPE_STR [ string ] found at ", lineNum, i+1);
                    currentString = "";
                  } else if(currentString.equals("boolean")) {
                    debug("DEBUG", "TYPE_BOOL [ boolean ] found at ", lineNum, i+1);
                    currentString = "";
                  } else if(currentString.equals("char")) {
                    debug("DEBUG", "TYPE_CHAR [ char ] found at ", lineNum, i+1);
                    currentString = "";
                  } else if(currentString.equals(" ")) {
                    //debug("DEBUG", "SPACE [  ] found at ", lineNum, i+1);
                    currentString = "";
                  } else if(currentString.equals("==")) {
                    debug("DEBUG", "BOOL_OP [ == ] found at ", lineNum, i+1);
                    currentString = "";
                  } else if(currentString.equals("!=")) {
                    debug("DEBUG", "BOOL_OP [ != ] found at ", lineNum, i+1);
                    currentString = "";
                  } else if(currentString.equals("false")) {
                    debug("DEBUG", "BOOL_VAL [ false ] found at ", lineNum, i+1);
                    currentString = "";
                  } else if(currentString.equals("true")) {
                    debug("DEBUG", "BOOL_VAL [ true ] found at ", lineNum, i+1);
                    currentString = "";
                  } else if(currentString.equals("+")) {
                    debug("DEBUG", "INTOP [ + ] found at ", lineNum, i+1);
                    currentString = "";
                  } else if(currentString.equals("/*")) {
                    debug("DEBUG", "OPEN_COMMENT [ /* ] found at ", lineNum, i+1);
                    currentString = "";
                    isComment = true;

                    //FOR THE REST OF THE CASES, IE: Digits, and Chars. They both iterate through loops that look for one of 0-9, or a-z
                  } else {
                    boolean isDone = false;

                    for(int j = 0; j < 10; j++) {
                      if(currentString.equals(i + "")) {
                        debug("DEBUG", "DIGIT [ " + j + " ] found at ", lineNum, i+1);
                        currentString = "";
                        isDone = true;
                        break;
                      }
                    }
                    if(isDone == false) {
                      for(int j = 97; j < 123; j++) {
                        if(currentString.equals((char)j + "")) {
                          isDone = true;
                          // IF END OF LINE
                          if(line.length() == i+1) {
                            debug("DEBUG", "ID [ " + (char)j + " ] found at ", lineNum, i+1);
                            currentString = "";
                            break;
                          // IF IT ISNT THE END OF LINE, LOOK AHEAD
                          } else if((line.charAt(i+1) + "").equals(" ")) {
                            debug("DEBUG", "ID [ " + (char)j + " ] found at ", lineNum, i+1);
                            currentString = "";
                            break;
                          }
                          // IF ANY CHAR IS FOUND, isDone SET TO true. 
                          // We do this so that the next if statement doesn't run and count multichar keywords as unrecognized tokens
                        } else if((line.charAt(i) + "").equals((char)j + "")) {
                          isDone = true;
                        }
                      }
                    } 
                    // UNRECOGNIZED TOKENS REACH HERE, AND DEBUG AN ERROR
                    if(isDone == false && !(line.charAt(i) + "").equals("/")) {
                      debug("ERROR", "Unrecognized Token: " + line.charAt(i) + " Error found at ", lineNum, i+1);
                      errorsFound++;
                      currentString = "";
                    }
                    //System.out.println(line.charAt(i)); //Debug Line
                  }
                //else (IF isComment == true), IE: ARE COMMENTS ON? YES
                } else {

                  //IF WE'VE REACHED THE END AND COMMENTS ARE STILL ON, DEBUG AN ERROR
                  if(!myReader.hasNextLine() && line.length() == i + 1) {
                    debug("ERROR", "You forgot to close your comment!", lineNum, i+1);
                    errorsFound++;
                  }

                  //IF WE FIND A '*' KEEP LOOKING FOR A '/'
                  if((line.charAt(i) + "").equals("*")) {
                    currentString = "";
                    currentString = currentString + line.charAt(i);
                  //FOUND '*/' END COMMENTS
                  } else if(currentString.equals("*/")) {
                    debug("DEBUG", "CLOSE_COMMENT [ */ ] found at ", lineNum, i+1);
                    currentString = "";
                    isComment = false;
                  }
                }
            }
          }
          // close scanner
          myReader.close();
          //Catches an error for unfindable/unkown files
        } catch (FileNotFoundException e) {
          System.out.println("An error occurred.");
          e.printStackTrace();
        }
    }

    //Debug Function
    public void debug(String type, String msg, int line, int spot) {
      System.out.println(type + " Lexer - " + msg + "(" + line + ":" + spot + ")");
    }
}
