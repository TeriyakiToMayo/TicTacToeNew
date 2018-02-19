package com.wiley.fordummies.androidsdk.tictactoe;

public class Symbol {
    private enum MARK {X, O, Blank}

    private MARK value = null;
    private static Symbol sSymbolX = null;
    private static Symbol sSymbolO = null;
    private static Symbol sSymbolBlank = null;

    private Symbol() {/* Empty PRIVATE constructor to enforce Singleton */}

    public static final Symbol SymbolXCreate() {
        if (sSymbolX == null) {
            sSymbolX = new Symbol();
            sSymbolX.value = MARK.X;
        }
        return sSymbolX;
    }

    public static final Symbol SymbolOCreate() {
        if (sSymbolO == null) {
            sSymbolO = new Symbol();
            sSymbolO.value = MARK.O;
        }
        return sSymbolO;
    }

    public static final Symbol SymbolBlankCreate() {
        if (sSymbolBlank == null) {
            sSymbolBlank = new Symbol();
            sSymbolBlank.value = MARK.Blank;
        }
        return sSymbolBlank;
    }

    public String toString() {
        if (value == MARK.X)
            return "X";
        else if (value == MARK.O)
            return "O";
        return "";
    }
}


