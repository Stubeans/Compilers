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

    public Symbol(String iName, String iType, int iScope, ArrayList<Symbol> symbolTable) {
        name = iName;
        type = iType;
        isInit = false;
        isUsed = false;
        scope = iScope;
        int num = 0;
        for(int i = 0; i < symbolTable.size(); i++) {
            if(symbolTable.get(i).scope == scope) {
                num++;
            }
        }
        scopeLetter = (char)(97 + num) + "";
        num = 0;
    }
}
