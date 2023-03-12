package source;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.ArrayList;
import java.util.Scanner; // Import the Scanner class to read text files

public class Lexer {
    public ArrayList<Token> main(String path) {
      String currentString = "";
      ArrayList<Token> tokenStream = new ArrayList<>();
        try {
          System.out.println("INFO  LEXER - Lexing program 1...");
          File myObj = new File(path);
          Scanner myReader = new Scanner(myObj);
          int lineNum = 0;
          int errorsFound = 0;
          int prgCounter = 1;
          boolean isString = false;
          boolean isComment = false;
          //WHILE THERE ARE LINES LEFT
          while (myReader.hasNextLine()) {
            lineNum++;
            String line = myReader.nextLine();
            //Empty currentString every new line
            currentString = "";
            //FOR CHAR IN LINE
            for(int i = 0; i < line.length(); i++) {
              //The current element that's being inspected. ( one or many chars stored as a string )
              currentString = currentString + line.charAt(i);
              //System.out.println(currentString); //DEBUG----------------
              //IF COMMENTS ARE NOT ON
              if(isComment == false && isString == false) {
                //If cases that look for keywords/identifiers/etc.
                //If a legitimate case is found, debug Tokens in output, and reset the currentString back to empty
                if(currentString.equals("$")) {
                  debug("DEBUG", "EOP [ $ ] found at ", lineNum, i+1);
                  tokenStream.add(new Token("EOP", lineNum + ":" + (i+1)));
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
                  debug("ERROR", "You forgot to put a $ at the end of your file", lineNum, (i+1));
                } else if(currentString.equals("{")) {
                  debug("DEBUG", "OPEN_BLOCK [ { ] found at ", lineNum, i+1);
                  tokenStream.add(new Token("OPEN_BLOCK", lineNum + ":" + (i+1)));
                  currentString = "";
                } else if(currentString.equals("}")) {
                  debug("DEBUG", "CLOSE_BLOCK [ } ] found at ", lineNum, i+1);
                  tokenStream.add(new Token("CLOSE_BLOCK", lineNum + ":" + (i+1)));
                  currentString = "";
                } else if(currentString.equals("print")) {
                  debug("DEBUG", "PRINT_STMT [ print ] found at ", lineNum, (i+1)-4);
                  tokenStream.add(new Token("PRINT_STMT", lineNum + ":" + (i+1-4)));
                  currentString = "";
                } else if(currentString.equals("(")) {
                  debug("DEBUG", "OPEN_PAREN [ ( ] found at ", lineNum, i+1);
                  tokenStream.add(new Token("OPEN_PAREN", lineNum + ":" + (i+1)));
                  currentString = "";
                } else if(currentString.equals(")")) {
                  debug("DEBUG", "CLOSE_PAREN [ ) ] found at ", lineNum, i+1);
                  tokenStream.add(new Token("CLOSE_PAREN", lineNum + ":" + (i+1)));
                  currentString = "";
                } else if(currentString.equals("=")) {
                  if(line.length() != i + 1) {
                    if(!(line.charAt(i+1) + "").equals("=")) {
                      debug("DEBUG", "ASSIGN_OP [ = ] found at ", lineNum, i+1);
                      tokenStream.add(new Token("ASSIGN_OP", lineNum + ":" + (i+1)));
                      currentString = "";
                    }
                  } else {
                    debug("DEBUG", "ASSIGN_OP [ = ] found at ", lineNum, i+1);
                    tokenStream.add(new Token("ASSIGN_OP", lineNum + ":" + (i+1)));
                    currentString = "";
                  }
                } else if(currentString.equals("while")) {
                  debug("DEBUG", "WHILE_STMT [ while ] found at ", lineNum, (i+1)-4);
                  tokenStream.add(new Token("WHILE_STMT", lineNum + ":" + (i+1-4)));
                  currentString = "";
                } else if(currentString.equals("if")) {
                  debug("DEBUG", "IF_STMT [ if ] found at ", lineNum, (i+1)-1);
                  tokenStream.add(new Token("IF_STMT", lineNum + ":" + (i+1-1)));
                  currentString = "";
                } else if(currentString.equals("" + '"') && isString == false) {
                  debug("DEBUG", "OPEN_STR [ " + '"' + " ] found at ", lineNum, i+1);
                  tokenStream.add(new Token("OPEN_STR", lineNum + ":" + (i+1)));
                  isString = true;
                  currentString = "";
                } else if(currentString.equals("int")) {
                  debug("DEBUG", "TYPE_INT [ int ] found at ", lineNum, (i+1)-2);
                  tokenStream.add(new Token("TYPE_INT", lineNum + ":" + (i+1-2)));
                  currentString = "";
                } else if(currentString.equals("string")) {
                  debug("DEBUG", "TYPE_STR [ string ] found at ", lineNum, (i+1)-5);
                  tokenStream.add(new Token("TYPE_STR", lineNum + ":" + (i+1-5)));
                  currentString = "";
                } else if(currentString.equals("boolean")) {
                  debug("DEBUG", "TYPE_BOOL [ boolean ] found at ", lineNum, (i+1)-6);
                  tokenStream.add(new Token("TYPE_BOOL", lineNum + ":" + (i+1-6)));
                  currentString = "";
                } else if(currentString.equals("char")) {
                  debug("DEBUG", "TYPE_CHAR [ char ] found at ", lineNum, (i+1)-3);
                  tokenStream.add(new Token("TYPE_CHAR", lineNum + ":" + (i+1-3)));
                  currentString = "";
                } else if(currentString.equals(" ")) {
                  if(isString == true) {
                    debug("DEBUG", "SPACE [  ] found at ", lineNum, i+1);
                  }
                  currentString = "";
                } else if(currentString.equals("==")) {
                  debug("DEBUG", "BOOL_OP [ == ] found at ", lineNum, (i+1)-1);
                  tokenStream.add(new Token("BOOL_OP", "==", lineNum + ":" + (i+1-1)));
                  currentString = "";
                } else if(currentString.equals("!=")) {
                  debug("DEBUG", "BOOL_OP [ != ] found at ", lineNum, (i+1)-1);
                  tokenStream.add(new Token("BOOL_OP", "!=", lineNum + ":" + (i+1-1)));
                  currentString = "";
                } else if(currentString.equals("false")) {
                  debug("DEBUG", "BOOL_VAL [ false ] found at ", lineNum, (i+1)-4);
                  tokenStream.add(new Token("BOOL_VAL", "false", lineNum + ":" + (i+1-4)));
                  currentString = "";
                } else if(currentString.equals("true")) {
                  debug("DEBUG", "BOOL_VAL [ true ] found at ", lineNum, (i+1)-3);
                  tokenStream.add(new Token("BOOL_VAL", "true", lineNum + ":" + (i+1-3)));
                  currentString = "";
                } else if(currentString.equals("+")) {
                  debug("DEBUG", "INTOP [ + ] found at ", lineNum, i+1);
                  tokenStream.add(new Token("INTOP", lineNum + ":" + i+1));
                  currentString = "";
                } else if(currentString.equals("/*")) {
                  currentString = "";
                  isComment = true;

                  //FOR THE REST OF THE CASES, IE: Digits, and Chars. They both iterate through loops that look for one of 0-9, or a-z
                } else {
                  boolean isDone = false;

                  for(int j = 0; j < 10; j++) {
                    if(currentString.equals(j + "")) {
                      if(isString == false) {
                        debug("DEBUG", "DIGIT [ " + j + " ] found at ", lineNum, (i+1));
                        tokenStream.add(new Token("DIGIT", j + "", lineNum + ":" + (i+1)));
                        currentString = "";
                        isDone = true;
                        break;
                      } else {
                        debug("ERROR", "DIGITS cannot be in Strings error found at ", lineNum, (i+1));
                        currentString = "";
                        isDone = true;
                        break;
                      }
                    }
                  }
                  if(isDone == false) {
                    for(int j = 97; j < 123; j++) {
                      if(currentString.equals((char)j + "")) {
                        isDone = true;
                        // IF END OF LINE
                        if(line.length() == i+1) {
                          debug("DEBUG", "ID [ " + (char)j + " ] found at ", lineNum, (i+1));
                          tokenStream.add(new Token("ID", (char)j + "", lineNum + ":" + (i+1)));
                          currentString = "";
                          break;
                        // IF IT ISNT THE END OF LINE, LOOK AHEAD
                        } else if((line.charAt(i+1) + "").equals(" ")) {
                          debug("DEBUG", "ID [ " + (char)j + " ] found at ", lineNum, (i+1));
                          tokenStream.add(new Token("ID", (char)j + "", lineNum + ":" + (i+1)));
                          currentString = "";
                          break;
                        //If the next space is NOT a valid char, is ID
                        } else if(!isValidChar(line.charAt(i+1) + "")) {
                          debug("DEBUG", "ID [ " + (char)j + " ] found at ", lineNum, (i+1));
                          tokenStream.add(new Token("ID", (char)j + "", lineNum + ":" + (i+1)));
                          currentString = "";
                          break;
                        }
                        // IF ANY CHAR IS FOUND, isDone SET TO true. 
                        // We do this so that the next if statement doesn't run and count multichar keywords as unrecognized tokens
                      } else if((line.charAt(i) + "").equals((char)j + "")) {
                        isDone = true;
                        if(!isValidChar(line.charAt(i+1) + "")) {
                          debug("DEBUG", "ID [ " + currentString.charAt(0) + " ] found at ", lineNum, ((i+1) - currentString.length() + 1));
                          tokenStream.add(new Token("ID", currentString.charAt(0) + "", lineNum + ":" + ((i+1) - currentString.length() + 1)));
                          i = (i+1) - currentString.length();
                          currentString = "";
                          break;
                        }
                      }
                    }
                  } 
                  // UNRECOGNIZED TOKENS REACH HERE, AND DEBUG AN ERROR
                  if(isDone == false && !(line.charAt(i) + "").equals("/") && !(line.charAt(i) + "").equals("!") && !(line.charAt(i) + "").equals("=")) {
                    debug("ERROR", "Unrecognized Token: " + currentString + " Error found at ", lineNum, (i+1));
                    errorsFound++;
                    currentString = "";
                  }
                }
              //else (IF isComment == true) or (IF isString == true), IE: ARE COMMENTS/STRING ON? YES
              } else {

                if(isComment == true) {
                  //IF WE'VE REACHED THE END AND COMMENTS ARE STILL ON, DEBUG AN ERROR
                  if(!myReader.hasNextLine() && line.length() == i + 1) {
                    debug("ERROR", "You forgot to close your comment!", lineNum, (i+1));
                    errorsFound++;
                  }

                  //IF WE FIND A '*' KEEP LOOKING FOR A '/'
                  if((line.charAt(i) + "").equals("*")) {
                    currentString = "";
                    currentString = currentString + line.charAt(i);
                  //FOUND '*/' END COMMENTS
                  } else if(currentString.equals("*/")) {
                    currentString = "";
                    isComment = false;
                  }
                //If isString is true..
                } else if(isString == true) {
                  //If it's NOT the end of the line
                  if(line.length() != i+1) {
                    if(isValidChar(line.charAt(i) + "")) { //if current char is valid
                      debug("DEBUG", "CHAR_" + Character.toUpperCase(line.charAt(i)) + " found at ", lineNum, (i+1));
                      tokenStream.add(new Token("CHAR_" + Character.toUpperCase(line.charAt(i)), lineNum + ":" + (i+1)));
                    } else if((line.charAt(i) + "").equals("" + '"')) { //if current char isn't 'valid', but is an "
                      debug("DEBUG", "CLOSE_STR [ " + '"' + " ] found at ", lineNum, (i+1));
                      tokenStream.add(new Token("CLOSE_STR", lineNum + ":" + (i+1)));
                      isString = false;
                      currentString = "";
                    } else if((line.charAt(i) + "").equals(" ")) { //If current char isn't 'valid', or an ", but is rather a space
                      debug("DEBUG", "CHAR_SPACE found at ", lineNum, (i+1));
                      tokenStream.add(new Token("CHAR_SPACE", lineNum + ":" + (i+1)));
                    } else { //If it's none of these acceptable inputs, send out an error
                      System.out.println("ERROR LEXER - The character, '" + line.charAt(i) + "' at (" + lineNum + ":" + (i+1) + ") does not belong in a String");
                      errorsFound++;
                    }
                  } else {
                    if((line.charAt(i) + "").equals("" + '"')) { //if current char is an "
                      debug("DEBUG", "CLOSE_STR [ " + '"' + " ] found at ", lineNum, (i+1));
                      isString = false;
                      currentString = "";
                    } else if(isValidChar(line.charAt(i) + "")) { //if current char is valid. Also Gives out an error that the String wasn't ended
                      debug("DEBUG", "CHAR_" + Character.toUpperCase(line.charAt(i)) + " found at ", lineNum, (i+1));
                      tokenStream.add(new Token("CHAR_" + Character.toUpperCase(line.charAt(i)), lineNum + ":" + (i+1)));
                      debug("ERROR", "Strings must be closed on the same line, missing '" + '"' + "' found at ", lineNum, (i+1));
                      errorsFound++;
                      currentString = "";
                      isString = false;
                    } else if((line.charAt(i) + "").equals(" ")) { //If current char isn't 'valid', or an ", but is rather a space
                      debug("DEBUG", "CHAR_SPACE found at ", lineNum, (i+1));
                      tokenStream.add(new Token("CLOSE_STR", lineNum + ":" + (i+1)));
                    } else { //If it's none of these acceptable inputs, send out an error. Also Gives out an error that the String wasn't ended
                      System.out.println("ERROR LEXER - The character, '" + line.charAt(i) + "' at (" + lineNum + ":" + (i+1) + ") does not belong in a String");
                      debug("ERROR", "Strings must be closed on the same line, missing '" + '"' + "' found at ", lineNum, (i+1));
                      errorsFound = errorsFound + 2;
                      currentString = "";
                      isString = false;
                    }
                  }
                }
                
              }
            }
          }
          // close scanner
          myReader.close();
          //Catches an error for unfindable/unknown files
        } catch (FileNotFoundException e) {
          System.out.println("An error occurred.");
          e.printStackTrace();
        }
      return tokenStream;
    }

    //Debug Function
    public void debug(String type, String msg, int line, int spot) {
      System.out.println(type + " Lexer - " + msg + "(" + line + ":" + spot + ")");
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
