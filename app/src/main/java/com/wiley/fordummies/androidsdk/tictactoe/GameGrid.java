package com.wiley.fordummies.androidsdk.tictactoe;

import android.util.Log;

import java.util.ArrayList;

public class GameGrid {
    public static final int SIZE = 3;
    private Symbol[][] mGrid = null;

    private final String TAG = getClass().getSimpleName();

    GameGrid() {// Constructor. Initializes the mGrid to blanks
        mGrid = new Symbol[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                mGrid[i][j] = Symbol.SymbolBlankCreate();
    }

    public void setValueAtLocation(int x, int y, Symbol value) {
        if (((x >= 0) && (x < SIZE)) && ((y >= 0) && (y < SIZE)))
            mGrid[x][y] = value;
    }

    public Symbol getValueAtLocation(int x, int y) {
        Symbol returnValue = null;
        if (((x >= 0) && (x < SIZE)) && ((y >= 0) && (y < SIZE))) returnValue = mGrid[x][y];
        return returnValue;
    }

    public boolean isRowFilled(int row) {//Entire row has the same symbol
        boolean isFilled = false;
        boolean foundMismatch = false;
        for (int col = 0; (col < SIZE) && (!foundMismatch); col++) {
            if (mGrid[row][0] != mGrid[row][col])
                foundMismatch = true;
        }
        isFilled = (!foundMismatch) && (mGrid[row][0] != Symbol.SymbolBlankCreate());
        return isFilled;
    }

    public boolean isColumnFilled(int column) {//Entire column has the same symbol
        boolean isFilled = false;
        boolean foundMismatch = false;
        for (int row = 0; (row < SIZE) && (!foundMismatch); row++) {
            if (mGrid[0][column] != mGrid[row][column]) foundMismatch = true;
        }
        isFilled = (!foundMismatch) && (mGrid[0][column] != Symbol.SymbolBlankCreate());
        return isFilled;
    }

    public boolean isLeftToRightDiagonalFilled() {//Left diagonal has the same symbol
        boolean isFilled = false;
        boolean foundMismatch = false;
        for (int index = 0; (index < SIZE) && (!foundMismatch); index++) {
            if (mGrid[0][0] != mGrid[index][index]) foundMismatch = true;
        }
        isFilled = (!foundMismatch) && (mGrid[0][0] != Symbol.SymbolBlankCreate());
        return isFilled;
    }

    public boolean isRightToLeftDiagonalFilled() {//Right diagonal has the same symbol
        int foundIndex = -1;
        boolean isFilled = false;
        boolean foundMismatch = false;
        Log.d(TAG,"Entering isRightToLeftDiagonalFilled");
        for (int index = SIZE - 1; (index >= 0) && (!foundMismatch); index--) {
            Log.d(TAG, ">" + mGrid[0][SIZE - 1].toString() + "<   >" + mGrid[index][index].toString() + "<");
            if (mGrid[0][SIZE - 1] != mGrid[SIZE - 1 - index][index]) {
                foundMismatch = true;
                foundIndex = index;
            }
        }
        isFilled = (!foundMismatch) && (mGrid[0][SIZE - 1] != Symbol.SymbolBlankCreate());
        Log.d(TAG,"Leaving isRightToLeftDiagonalFilled" + foundMismatch + "index>" + foundIndex + "<>" + mGrid[0][SIZE - 1].toString() + "<");
        return isFilled;
    }

    public ArrayList<Square> getEmptySquares() {//Get the unfilled squares
        ArrayList<Square> list = new ArrayList<Square>();
        for (int i = 0; i < GameGrid.SIZE; i++) {
            for (int j = 0; j < GameGrid.SIZE; j++) {
                if (mGrid[i][j] == Symbol.SymbolBlankCreate()) {
                    Square b = new Square(i, j);
                    list.add(b);
                }
            }
        }
        return list;
    }
}
