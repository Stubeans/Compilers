package source;

public class Token {
    //The type, ie: BOOL_VAL, or ID
    public String type;
    //The character(s) stored inside, ie: "e", "int", or "}"
    public String val;
    //The position of the Token in the file
    public String pos;

    public Token(String inputType, String inputPos) {
        type = inputType;
        val = null;
        pos = inputPos;
    }

    public Token(String inputType, String inputVal, String inputPos) {
        type = inputType;
        val = inputVal;
        pos = inputPos;
    }
}
