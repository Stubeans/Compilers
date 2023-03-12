package source;

public class Token {
    public String type;
    public String val;
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
