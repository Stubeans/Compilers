package source;

import java.util.ArrayList;

public class Symbol {
    
    public String name;
    public String type;
    //is this initialized?
    public boolean isInit;
    //is this used?
    public boolean isUsed;
    public int scope;
    public String scopeLetter = "";

    public Symbol(String iName, String iType, int iScope) {
        name = iName;
        type = iType;
        isInit = false;
        isUsed = false;
        scope = iScope;
    }
}
