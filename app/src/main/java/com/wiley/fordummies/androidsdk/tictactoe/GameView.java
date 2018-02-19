package com.wiley.fordummies.androidsdk.tictactoe;

import android.widget.TextView;

public class GameView {
    private Board gameBoard = null;
    private TextView statusView = null;
    private TextView sessionScoresView = null;

    public void setGameViewComponents(Board theBoard, TextView theStatusView, TextView theSessionScoresView) {
        gameBoard = theBoard;
        statusView = theStatusView;
        sessionScoresView = theSessionScoresView;
    }

    public void setGameStatus(String message) {
        statusView.setText(message);
    }

    public void showScores(String player1Name, int player1Score, String player2Name, int player2Score) {
        sessionScoresView.setText(player1Name + ":" + player1Score + "...." + player2Name + ":" + player2Score);
    }

    public void placeSymbol(int x, int y) {
        gameBoard.placeSymbol(x, y);
        gameBoard.invalidateBlock(x, y);
    }
}
