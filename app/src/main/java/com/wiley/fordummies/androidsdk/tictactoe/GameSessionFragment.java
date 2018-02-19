package com.wiley.fordummies.androidsdk.tictactoe;

import android.app.AlertDialog;
import android.support.v4.app.FragmentManager;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import static android.content.Context.KEYGUARD_SERVICE;

/**
 * Fragment where user plays Tic-Tac-Toe.
 * <p>
 * Created by adamcchampion on 2017/08/19.
 */

public class GameSessionFragment extends Fragment {
    private static final int ANDROID_TIMEOUT_BASE = 500;
    private static final int ANDROID_TIMEOUT_SEED = 2000;

    private Board mBoard;
    private Game mActiveGame;
    private GameView mGameView;
    int mScorePlayerOne = 0;
    int mScorePlayerTwo = 0;
    String mFirstPlayerName;
    String mSecondPlayerName;
    private boolean mTestMode = false;
    private ViewGroup mContainer;
    private Bundle mSavedInstanceState;

    private static final String SCOREPLAYERONEKEY = "ScorePlayerOne";
    private static final String SCOREPLAYERTWOKEY = "ScorePlayerTwo";
    private static final String GAMEKEY = "Game";
    private final String TAG = getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View v;
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            v = inflater.inflate(R.layout.fragment_gamesession_land, container, false);
        } else {
            v = inflater.inflate(R.layout.fragment_game_session, container, false);
        }
        mContainer = container;
        setRetainInstance(true);

        if (savedInstanceState != null) {
            mScorePlayerOne = savedInstanceState.getInt(SCOREPLAYERONEKEY);
            mScorePlayerTwo = savedInstanceState.getInt(SCOREPLAYERTWOKEY);
        }

        KeyguardManager keyguardManager = (KeyguardManager) getActivity().getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock("GameSession");
        if (lock != null) {
            lock.disableKeyguard();
        }

        setupBoard(v);

        setHasOptionsMenu(true);

        return v;
    }

    private void setupBoard(View v) {
        mBoard = (Board) v.findViewById(R.id.board);
        TextView turnStatusView = (TextView) v.findViewById(R.id.gameInfo);
        TextView scoreView = (TextView) v.findViewById(R.id.scoreboard);
        mActiveGame = new Game();
        GameGrid gameGrid = mActiveGame.getGameGrid();

        mBoard.setGrid(gameGrid);
        mGameView = new GameView();
        mGameView.setGameViewComponents(mBoard, turnStatusView, scoreView);
        this.setPlayers(mActiveGame);
        mGameView.showScores(mActiveGame.getPlayerOneName(), mScorePlayerOne, mActiveGame.getPlayerTwoName(), mScorePlayerTwo);
        mGameView.setGameStatus(mActiveGame.getCurrentPlayerName() + " to play.");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        try {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setSubtitle(getResources().getString(R.string.game));
            }
        } catch (NullPointerException npe) {
            Log.e(TAG, "Could not set subtitle");
        }

        playNewGame();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    public void startSession() {
        Log.d(TAG, "In startSession()");
        mScorePlayerOne = 0;
        mScorePlayerTwo = 0;
    }

    public int getPlayCount() {
        return mActiveGame.getPlayCount();
    }

    private void playNewGame() {
        // If Android is the first player, give it its turn
        if (mActiveGame.getCurrentPlayerName().equals("Android")) scheduleAndroidsTurn();
    }

    private void setPlayers(Game theGame) {

        if (Settings.doesHumanPlayFirst(getActivity())) {
            mFirstPlayerName = Settings.getName(getActivity());
            mSecondPlayerName = "Android";
        } else {
            mFirstPlayerName = "Android";
            mSecondPlayerName = Settings.getName(getActivity());
        }
        theGame.setPlayerNames(mFirstPlayerName, mSecondPlayerName);
    }

    public void scheduleAndroidsTurn() {
        Log.d(TAG, "Thread ID in scheduleAndroidsTurn:" + Thread.currentThread().getId());
        mBoard.disableInput();
        if (!mTestMode) {
            Random randomNumber = new Random();
            Handler handler = new Handler();
            handler.postDelayed(
                    new Runnable() {
                        public void run() {
                            androidTakesATurn();
                        }
                    },
                    ANDROID_TIMEOUT_BASE + randomNumber.nextInt(ANDROID_TIMEOUT_SEED)
            );
        } else {
            androidTakesATurn();
        }
    }

    private void androidTakesATurn() {
        int pickedX, pickedY;
        Log.d(TAG, "Thread ID in androidTakesATurn:" + Thread.currentThread().getId());

        GameGrid gameGrid = mActiveGame.getGameGrid();
        ArrayList<Square> emptyBlocks = gameGrid.getEmptySquares();
        int n = emptyBlocks.size();
        Random r = new Random();
        int randomIndex = r.nextInt(n);
        Square picked = emptyBlocks.get(randomIndex);
        mActiveGame.play(pickedX = picked.getX(), pickedY = picked.getY());
        mGameView.placeSymbol(pickedX, pickedY);
        mBoard.enableInput();
        if (mActiveGame.isActive()) {
            mGameView.setGameStatus(mActiveGame.getCurrentPlayerName() + " to play.");
        } else {
            proceedToFinish();
        }
    }

    public void humanTakesATurn(int x, int y) {/* human's turn */
        Log.d(TAG, "Thread ID in humanTakesATurn:" + Thread.currentThread().getId());
        boolean successfulPlay = mActiveGame.play(x, y);
        if (successfulPlay) {
            mGameView.placeSymbol(x, y); /* Update the display */
            if (mActiveGame.isActive()) {
                mGameView.setGameStatus(mActiveGame.getCurrentPlayerName() + " to play.");
                scheduleAndroidsTurn();
            } else {
                proceedToFinish();
            }
        }
    }

    private void quitGame() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        AbandonGameDialogFragment abandonGameDialogFragment = new AbandonGameDialogFragment();
        abandonGameDialogFragment.show(fm, "abandon_game");
    }

    private void proceedToFinish() {
        String winningPlayerName;
        String alertMessage;
        if (mActiveGame.isWon()) {
            winningPlayerName = mActiveGame.getWinningPlayerName();
            alertMessage = winningPlayerName + " Wins!";
            mGameView.setGameStatus(alertMessage);
            accumulateScores(winningPlayerName);

            mGameView.showScores(mFirstPlayerName, mScorePlayerOne, mSecondPlayerName, mScorePlayerTwo);

        } else if (mActiveGame.isDrawn()) {
            alertMessage = "DRAW!";
            mGameView.setGameStatus(alertMessage);
        } else {
            // Control flow should never reach this block, but if it does, show a default text string.
            alertMessage = "Info";
        }
        new AlertDialog.Builder(getActivity())
                .setTitle(alertMessage)
                .setMessage("Play another game?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        LayoutInflater inflater = LayoutInflater.from(getActivity());
                        if (mContainer != null) {
                            Log.d(TAG, "Calling setupBoard() again");
                            onSaveInstanceState(mSavedInstanceState);
                            getActivity().recreate();
                            onCreateView(inflater, mContainer, mSavedInstanceState);

                            if (mBoard != null) {
                                Symbol blankSymbol = Symbol.SymbolBlankCreate();
                                for (int x = 0; x < GameGrid.SIZE; x++) {
                                    for (int y = 0; y < GameGrid.SIZE; y++) {
                                        mActiveGame.getGameGrid().setValueAtLocation(x, y, blankSymbol);
                                    }
                                }
                            }
                        } else {
                            Log.d(TAG, "Could not restart game. mContainer or mSavedInstanceState were null");
                        }
                        playNewGame();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                })
                .show();

    }

    private void accumulateScores(String winningPlayerName) {
        if (winningPlayerName.equals(mFirstPlayerName))
            mScorePlayerOne++;
        else
            mScorePlayerTwo++;
    }

    public void sendScoresViaEmail() {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                "Look at my AWESOME TicTacToe Score!");
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                mFirstPlayerName + " score is  " + mScorePlayerOne +
                        " and " +
                        mSecondPlayerName + " score is  " + mScorePlayerTwo);
        startActivity(emailIntent);
    }

    public void sendScoresViaSMS() {
        Intent SMSIntent = new Intent(Intent.ACTION_VIEW);
        SMSIntent.putExtra("sms_body",
                "Look at my AWESOME TicTacToe Score!" +
                        mFirstPlayerName + " score is  " + mScorePlayerOne +
                        " and " +
                        mSecondPlayerName + " score is  " + mScorePlayerTwo);
        SMSIntent.setType("vnd.android-dir/mms-sms");
        startActivity(SMSIntent);
    }

    public void callTicTacToeHelp() {
        Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
        String phoneNumber = "842-822-4357"; // TIC TAC HELP
        String uri = "tel:" + phoneNumber.trim();
        phoneIntent.setData(Uri.parse(uri));
        startActivity(phoneIntent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_ingame, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_help:
                startActivity(new Intent(getActivity().getApplicationContext(), HelpActivity.class));
                return true;
            case R.id.menu_exit:
                quitGame();
                return true;
            case R.id.menu_email:
                sendScoresViaEmail();
                return true;
            case R.id.menu_sms:
                sendScoresViaSMS();
                return true;
            case R.id.menu_call:
                callTicTacToeHelp();
                return true;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle instanceState;
        if (outState == null) {
            instanceState = new Bundle();
        }
        else {
            instanceState = outState;
        }
        // Save session score
        instanceState.putInt(SCOREPLAYERONEKEY, mScorePlayerOne);
        instanceState.putInt(SCOREPLAYERTWOKEY, mScorePlayerTwo);
        // Save turn
        instanceState.putString(GAMEKEY, mActiveGame.toString());
        //Save board
    }
}
