package com.wiley.fordummies.androidsdk.tictactoe;

public class Square {
    private int mX;
    private int mY;

    public Square(int x, int y, String value) {
        this.mX = x;
        this.mY = y;
    }

    public Square(int x, int y) {
        this.mX = x;
        this.mY = y;
    }

    public int getX() {
        return mX;
    }

    public int getY() {
        return mY;
    }
}
