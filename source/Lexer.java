package source;

import java.io.Console;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

public class Lexer {
    public void main(String path) {
      String currentString = "";
      String expectedString = "";
        try {
          System.out.println("INFO  LEXER - Lexing program 1...");
          File myObj = new File(path);
          Scanner myReader = new Scanner(myObj);
          int lineNum = 0;
          int errorsFound = 0;
          int prgCounter = 1;
          boolean isString = false;
          boolean isComment = false;
          while (myReader.hasNextLine()) {
            lineNum++;
            String line = myReader.nextLine();
            for(int i = 0; i < line.length(); i++) {
                currentString = currentString + line.charAt(i);
                //System.out.println(currentString);
                if(isComment == false) {
                  if(currentString.equals("$")) {
                    debug("DEBUG", "EOP [ $ ] found at ", lineNum, i+1);
                    System.out.println("INFO  LEXER - Lex completed with " + errorsFound + " errors");
                    errorsFound = 0;
                    prgCounter++;
                    if(myReader.hasNextLine()) {
                      System.out.println("INFO  LEXER - Lexing program " + prgCounter + "...");
                    }
                    currentString = "";
                  } else if(currentString.equals("{")) {
                    debug("DEBUG", "OPEN_BLOCK [ { ] found at ", lineNum, i+1);
                    currentString = "";
                  } else if(currentString.equals("}")) {
                    debug("DEBUG", "CLOSE_BLOCK [ } ] found at ", lineNum, i+1);
                    currentString = "";
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
                  } else if(currentString.equals("" + '"') && isString == true) {
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
                          if(line.length() == i+1) {
                            debug("DEBUG", "ID [ " + (char)j + " ] found at ", lineNum, i+1);
                            currentString = "";
                            break;
                          } else if((line.charAt(i+1) + "").equals(" ")) {
                            debug("DEBUG", "ID [ " + (char)j + " ] found at ", lineNum, i+1);
                            currentString = "";
                            break;
                          }
                        } else if((line.charAt(i) + "").equals((char)j + "")) {
                          isDone = true;
                        }
                      }
                    } 
                    if(isDone == false && !(line.charAt(i) + "").equals("/")) {
                      debug("ERROR", "Unrecognized Token: " + line.charAt(i) + " Error found at ", lineNum, i+1);
                      errorsFound++;
                      currentString = "";
                    }
                    //System.out.println(line.charAt(i));
                  }
                } else { //if comment == true
                  if((line.charAt(i) + "").equals("*")) {
                    currentString = "";
                    currentString = currentString + line.charAt(i);
                  } else if(currentString.equals("*/")) {
                    debug("DEBUG", "CLOSE_COMMENT [ */ ] found at ", lineNum, i+1);
                    currentString = "";
                    isComment = false;
                  }
                }
            }
          }
          myReader.close();
        } catch (FileNotFoundException e) {
          System.out.println("An error occurred.");
          e.printStackTrace();
        }
    }

    public void debug(String type, String msg, int line, int spot) {
      System.out.println(type + " Lexer - " + msg + "(" + line + ":" + spot + ")");
    }
}
