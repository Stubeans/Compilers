package source;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

public class Lexer {
    public static void main(String[] args) {
        try {
          File myObj = new File("C:/Users/playe/Project/Compilers/source/filename.txt");
          Scanner myReader = new Scanner(myObj);
          while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            for(int i = 0; i < data.length(); i++) {
                System.out.println(data.charAt(i));
            }
            System.out.println(data);
          }
          myReader.close();
        } catch (FileNotFoundException e) {
          System.out.println("An error occurred.");
          e.printStackTrace();
        }
    }
}
