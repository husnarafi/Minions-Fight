package com.husnarafi.xo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.husnarafi.R;


public class BoardView extends View {

    public static final int mBoardGridWidth = 6;
    private TicTacToeGame mGame;
    private Paint mPaint, paint;

    private Bitmap mHumanBitmap;
    private Bitmap mComputerBitmap;

    private int mBoardColor = Color.LTGRAY;
    private static final int[] gradientColors = {Color.TRANSPARENT, Color.GREEN, Color.YELLOW, Color.GREEN, Color.TRANSPARENT};
    private Shader verticalShader = new LinearGradient(0, 0, 0, getResources().getDimension(R.dimen.grid_width), gradientColors, null, Shader.TileMode.CLAMP);
    private Shader horizontalShader = new LinearGradient(0, 0, getResources().getDimension(R.dimen.grid_height), 0, gradientColors, null, Shader.TileMode.CLAMP);

    public BoardView(Context context) {
        super(context);
        initialize();
    }

    public BoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public void initialize() {

        // ask the bitmap factory not to scale the loaded bitmaps
        // BitmapFactory.Options opts = new BitmapFactory.Options();
        // opts.inScaled = false;

        mHumanBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.x_img);
        mComputerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.o_img);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
    }

    public void setGame(TicTacToeGame game) {
        mGame = game;
    }

    public int getBoardCellWidth() {
        return getWidth() / 3;
    }

    public int getBoardCellHeight() {
        return getHeight() / 3;
    }

    public void setBoardColor(int boardColor) {
        mBoardColor = boardColor;
    }

    public int getBoardColor() {
        return mBoardColor;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int halfGridWidth = mBoardGridWidth / 3;
        mPaint.setShader(verticalShader);
        // Draw the board lines
        canvas.drawRect(getBoardCellWidth() - halfGridWidth, 2,
                getBoardCellWidth() + halfGridWidth, getHeight(), mPaint);
        canvas.drawRect(getBoardCellWidth() * 2 - halfGridWidth, 2,
                getBoardCellWidth() * 2 + halfGridWidth, getHeight(), mPaint);
        mPaint.setShader(horizontalShader);
        canvas.drawRect(0, getBoardCellHeight() - halfGridWidth, getWidth(),
                getBoardCellHeight() + halfGridWidth, mPaint);
        canvas.drawRect(0, getBoardCellHeight() * 2 - halfGridWidth,
                getWidth(), getBoardCellHeight() * 2 + halfGridWidth, mPaint);

        // Draw all the pieces
        for (int i = 0; i < TicTacToeGame.BOARD_SIZE; i++) {
            int col = i % 3;
            int row = i / 3;

            // Define the boundaries of a destination rectangle for the image
            int left = col * getBoardCellWidth() + mBoardGridWidth;
            int top = row * getBoardCellHeight() + mBoardGridWidth;
            int right = left + getBoardCellWidth() - 10;
            int bottom = top + getBoardCellHeight() - mBoardGridWidth - 6;

            if (mGame != null
                    && mGame.getBoardOccupant(i) == TicTacToeGame.HUMAN_PLAYER) {
                canvas.drawBitmap(mHumanBitmap, null, // src
                        new Rect(left, top, right, bottom), // dest
                        paint);

            } else if (mGame != null
                    && mGame.getBoardOccupant(i) == TicTacToeGame.COMPUTER_PLAYER) {
                canvas.drawBitmap(mComputerBitmap, null, // src
                        new Rect(left, top, right, bottom), // dest
                        paint);
            }
        }
    }

}
