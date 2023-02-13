package source;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

public class Lexer {
    public void main(String path) {
      String currentString = "";
      String expectedString = "";
        try {
          File myObj = new File(path);
          Scanner myReader = new Scanner(myObj);
          int lineNum = 0;
          boolean isString = false;
          boolean isComment = false;
          while (myReader.hasNextLine()) {
            lineNum++;
            String line = myReader.nextLine();
            for(int i = 0; i < line.length(); i++) {
                currentString = currentString + line.charAt(i);
                //System.out.println(currentString);
                if(currentString.equals("$")) {
                  debug("DEBUG", "EOP [ $ ] found at ", lineNum, i+1);
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
                  debug("DEBUG", "SPACE [  ] found at ", lineNum, i+1);
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
                } else if(currentString.equals("*/")) {
                  debug("DEBUG", "CLOSE_COMMENT [ */ ] found at ", lineNum, i+1);
                  currentString = "";
                  isComment = false;
                } else {
                  for(int j = 0; j < 10; j++) {
                    if(currentString.equals(i + "")) {
                      debug("DEBUG", "DIGIT [ " + j + " ] found at ", lineNum, i+1);
                      currentString = "";
                      break;
                    }
                  }
                  for(int j = 97; j < 123; j++) {
                    if(currentString.equals((char)j + "")) {
                      if(line.length() == i+1) {
                        debug("DEBUG", "CHAR [ " + (char)j + " ] found at ", lineNum, i+1);
                        currentString = "";
                        break;
                      } else if((line.charAt(i+1) + "").equals(" ")) {
                        debug("DEBUG", "CHAR [ " + (char)j + " ] found at ", lineNum, i+1);
                        currentString = "";
                        break;
                      }
                    }
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
