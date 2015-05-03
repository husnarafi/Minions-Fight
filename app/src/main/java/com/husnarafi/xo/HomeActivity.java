package com.husnarafi.xo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.husnarafi.R;
import com.husnarafi.xo.utility.TextViewWithRobotoFont;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HomeActivity extends Activity {

    // Identify the dialog boxes
    static final int DIALOG_QUIT_ID = 1;
    static final int DIALOG_ABOUT = 2;
    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;
    // Represents the game board
    // Indicates if game is currently over or not
    private boolean mGameOver = false;
    // Whose turn to go first
    private char mGoFirst = TicTacToeGame.HUMAN_PLAYER;

    // Whose turn is it
    private char mTurn = TicTacToeGame.COMPUTER_PLAYER;
    // Listen for touches on the board
    private OnTouchListener mTouchListener = new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {

            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;

            if (!mGameOver && mTurn == TicTacToeGame.HUMAN_PLAYER
                    && setMove(TicTacToeGame.HUMAN_PLAYER, pos)) {

                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    // mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                } else
                    endGame(winner);
            }

            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };
    private int mHumanWins = 0;
    private int mComputerWins = 0;
    private int mTies = 0;
    // Represents the internal state of the game
    private TicTacToeGame mGame;
    // private TextView // mInfoTextView;
    private SharedPreferences mPrefs;
    private boolean mSoundOn;

    @InjectView(R.id.board)
    BoardView mBoardView;

    @InjectView(R.id.layoutPlayerO)
    RelativeLayout playerOView;

    @InjectView(R.id.layoutPlayerX)
    RelativeLayout playerXView;

    @InjectView(R.id.layoutGameTie)
    RelativeLayout gameTieView;

    @InjectView(R.id.player_score)
    TextView mHumanScoreTextView;

    @InjectView(R.id.ai_score)
    TextView mComputerScoreTextView;

    @InjectView(R.id.tie_score)
    TextView mTieScoreTextView;

    @InjectView(R.id.tv_player_name_x)
    TextViewWithRobotoFont mPlayerName;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.inject(this);

        mGame = new TicTacToeGame();
        mBoardView.setGame(mGame);

        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);


        // mInfoTextView = (TextView) findViewById(R.id.information);

        // Restore the scores from the persistent preference data source
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSoundOn = mPrefs.getBoolean(Settings.SOUND_PREFERENCE_KEY, true);
        mPlayerName.setText(mPrefs
                .getString(Settings.PLAYER_NAME_KEY, "Player"));
        mHumanWins = mPrefs.getInt("mHumanWins", 0);
        mComputerWins = mPrefs.getInt("mComputerWins", 0);
        mTies = mPrefs.getInt("mTies", 0);
        mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);

        if (savedInstanceState == null) {
            startNewGame();
        } else {
            // Restore the game's state
            // The same thing can be accomplished with onRestoreInstanceState
            mGame.setBoardState(savedInstanceState.getCharArray("board"));
            mGameOver = savedInstanceState.getBoolean("mGameOver");
            // mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
            mTurn = savedInstanceState.getChar("mTurn");
            mGoFirst = savedInstanceState.getChar("mGoFirst");

            // If it's the computer's turn, the previous turn did not take, so
            // go again
            if (!mGameOver && mTurn == TicTacToeGame.COMPUTER_PLAYER) {
                int move = mGame.getComputerMove();
                setMove(TicTacToeGame.COMPUTER_PLAYER, move);
            }
        }
        if (mTies == 0)
            gameTieView.setVisibility(View.INVISIBLE);
        displayScores();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(),
                R.raw.excited2);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(),
                R.raw.excited1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Save the current score, but not the state of the current game
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("mHumanWins", mHumanWins);
        ed.putInt("mComputerWins", mComputerWins);
        ed.putInt("mTies", mTies);
        ed.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharArray("board", mGame.getBoardState());
        outState.putBoolean("mGameOver", mGameOver);
        // outState.putCharSequence("info", mInfoTextView.getText());
        outState.putChar("mGoFirst", mGoFirst);
        outState.putChar("mTurn", mTurn);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (id) {
            case DIALOG_QUIT_ID:
                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        HomeActivity.this.finish();
                                    }
                                }).setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;
        }

        return dialog;
    }

    // Handles menu item selections
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.reset_scores:
                mHumanWins = 0;
                mComputerWins = 0;
                mTies = 0;
                displayScores();
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // See if Back button was pressed on Settings activity
        if (requestCode == RESULT_CANCELED) {
            // Apply potentially new settings
            mSoundOn = mPrefs.getBoolean(Settings.SOUND_PREFERENCE_KEY, true);

            String goes_first = mPrefs.getString(
                    Settings.GOES_FIRST_PREFERENCE_KEY, "Alternate");
            if (!goes_first.equals("Alternate")) {
                // See if any moves have been made. If not, start a new game
                // which will use the selected setting
                for (int i = 0; i < 8; i++)
                    if (mGame.getBoardOccupant(i) != TicTacToeGame.OPEN_SPOT)
                        return;
                // All spots must be open
                startNewGame();
            }
        }
    }

    // Show the scores
    private void displayScores() {
        mHumanScoreTextView.setText(Integer.toString(mHumanWins));
        mComputerScoreTextView.setText(Integer.toString(mComputerWins));
        mTieScoreTextView.setText(Integer.toString(mTies));
    }

    // Set up the game board.
    private void startNewGame() {

        mGame.clearBoard();
        mBoardView.invalidate(); // Redraw the board

        // Determine who should go first based on settings
        String goesFirst = mPrefs.getString(Settings.GOES_FIRST_PREFERENCE_KEY,
                "Alternate");

        if (goesFirst.equals("Alternate")) {
            // Alternate who goes first
            if (mGoFirst == TicTacToeGame.COMPUTER_PLAYER) {
                mGoFirst = TicTacToeGame.HUMAN_PLAYER;
                mTurn = TicTacToeGame.COMPUTER_PLAYER;
            } else {
                mGoFirst = TicTacToeGame.COMPUTER_PLAYER;
                mTurn = TicTacToeGame.HUMAN_PLAYER;
            }
        } else if (goesFirst.equals("Human"))
            mTurn = TicTacToeGame.HUMAN_PLAYER;
        else
            mTurn = TicTacToeGame.COMPUTER_PLAYER;

        // Start the game
        if (mTurn == TicTacToeGame.COMPUTER_PLAYER) {
            setAlpha(playerXView, 1.0f, 0.5f);
            // mInfoTextView.setText(R.string.first_computer);
            int move = mGame.getComputerMove();
            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
        } else {
            setAlpha(playerOView, 1.0f, 0.5f);
            // mInfoTextView.setText(R.string.first_human);
        }

        mGameOver = false;
    }

    private void setAlpha(RelativeLayout relativeLayout, float fromAlpha,
                          float toAlpha) {
        AlphaAnimation alpha = new AlphaAnimation(fromAlpha, toAlpha);
        alpha.setDuration(0); // Make animation instant
        alpha.setFillAfter(true); // Tell it to persist after the animation ends
        relativeLayout.startAnimation(alpha);
    }

    // Make a move
    private boolean setMove(char player, int location) {

        if (player == TicTacToeGame.COMPUTER_PLAYER) {
            // Make the computer move after a delay of 1 second
            final int loc = location;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    mGame.setMove(TicTacToeGame.COMPUTER_PLAYER, loc);
                    mBoardView.invalidate(); // Redraw the board

                    try {
                        if (mSoundOn)
                            mComputerMediaPlayer.start();
                    } catch (IllegalStateException e) {
                    }
                    ; // Happens if orientation changed before playing

                    int winner = mGame.checkForWinner();
                    if (winner == 0) {
                        setAlpha(playerOView, 1.0f, 0.5f);
                        setAlpha(playerXView, 0.5f, 1.0f);
                        mTurn = TicTacToeGame.HUMAN_PLAYER;
                        // mInfoTextView.setText(R.string.turn_human);
                    } else
                        endGame(winner);
                }
            }, 1000);

            return true;
        } else if (mGame.setMove(TicTacToeGame.HUMAN_PLAYER, location)) {
            mTurn = TicTacToeGame.COMPUTER_PLAYER;
            mBoardView.invalidate(); // Redraw the board
            setAlpha(playerOView, 0.5f, 1.0f);
            setAlpha(playerXView, 1.0f, 0.5f);
            if (mSoundOn)
                mHumanMediaPlayer.start();
            return true;
        }

        return false;
    }

    // Game is over logic
    private void endGame(int winner) {
        if (winner == 1) {
            mTies++;
            if (mTies == 1) {
                Animation animBounce = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                gameTieView.setVisibility(View.VISIBLE);
                gameTieView.startAnimation(animBounce);
            }
            mTieScoreTextView.setText(Integer.toString(mTies));
            // mInfoTextView.setText(R.string.result_tie);
        } else if (winner == 2) {
            mHumanWins++;
            mHumanScoreTextView.setText(Integer.toString(mHumanWins));
            // String defaultMessage = getResources().getString(
            // R.string.result_human_wins);
            // mInfoTextView.setText(mPrefs.getString("victory_message",
            // defaultMessage));
        } else if (winner == 3) {
            mComputerWins++;
            mComputerScoreTextView.setText(Integer.toString(mComputerWins));
            // mInfoTextView.setText(R.string.result_computer_wins);
        }

        mGameOver = true;
        openOptionsMenu();
    }
}