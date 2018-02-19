package com.wiley.fordummies.androidsdk.tictactoe;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

public class Board extends View {

    private final GameSessionActivity mGameSessionActivity;    // game context (parent)
    private float mBlockWidth;        // mBlockWidth of one block
    private float mBlockHeight;    // will be same as mBlockWidth;
    private final float mStrokeWidth = 2;
    private final float mLineWidth = 10;
    private GameGrid mGameGrid = null;
    private boolean mIsEnabled = true;

    private Paint mBackgroundPaint, mDarkPaint, mLightPaint, mLinePaint, mDitherPaint;

    static Bitmap sSymX = null, sSymO = null, sSymBlank = null;
    static boolean sDrawablesInitialized = false;

    private final String TAG = getClass().getSimpleName();

    public Board(Context context, AttributeSet attributes) {
        super(context, attributes);
        this.mGameSessionActivity = (GameSessionActivity) context;

        setFocusable(true);
        setFocusableInTouchMode(true);

        // Allocate Paint objects to save memory.
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(getResources().getColor(R.color.white));
        mDarkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDarkPaint.setColor(getResources().getColor(R.color.dark));
        mDarkPaint.setStrokeWidth(mStrokeWidth);
        mLightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLightPaint.setColor(getResources().getColor(R.color.light));
        mLightPaint.setStrokeWidth(mStrokeWidth);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(getResources().getColor(R.color.dark));
        mLinePaint.setStrokeWidth(mLineWidth);
        mLinePaint.setStrokeCap(Cap.ROUND);
        mDitherPaint = new Paint();
        mDitherPaint.setDither(true);
    }

    public void setGrid(GameGrid aGrid) {
        mGameGrid = aGrid;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mBlockWidth = w / 3f;
        mBlockHeight = h / 3f;

        if (w < h) {
            mBlockHeight = mBlockWidth;
        } else {
            mBlockWidth = mBlockHeight;
        }

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float canvasWidth = getWidth();
        float canvasHeight = getHeight();

        if (canvasWidth < canvasHeight) canvasHeight = canvasWidth;
        else canvasWidth = canvasHeight;

        canvas.drawRect(0, 0, canvasWidth, canvasHeight, mBackgroundPaint);

        // Drawing lines
        for (int i = 0; i < GameGrid.SIZE - 1; i++) {
            canvas.drawLine(0, (i + 1) * mBlockHeight, canvasWidth, (i + 1) * mBlockHeight, mDarkPaint);
            canvas.drawLine(0, (i + 1) * mBlockHeight + 1, canvasWidth, (i + 1) * mBlockHeight + 1, mLightPaint);
            canvas.drawLine((i + 1) * mBlockHeight, 0, (i + 1) * mBlockHeight, canvasHeight, mDarkPaint);
            canvas.drawLine((i + 1) * mBlockHeight + 1, 0, (i + 1) * mBlockHeight + 1, canvasHeight, mLightPaint);
        }

        float offsetX = 0;
        float offsetY = 0;
        for (int i = 0; i < GameGrid.SIZE; i++) {
            for (int j = 0; j < GameGrid.SIZE; j++) {
                Bitmap symSelected = getBitmapForSymbol(mGameGrid.getValueAtLocation(i, j));
                offsetX = (int) (((mBlockWidth - symSelected.getWidth()) / 2) + (i * mBlockWidth));
                offsetY = (int) (((mBlockHeight - symSelected.getHeight()) / 2) + (j * mBlockHeight));
                canvas.drawBitmap(symSelected, offsetX, offsetY, mDitherPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!this.mIsEnabled) {
            Log.d(TAG, "Board.onTouchEvent: Board not mIsEnabled");
            return false;
        }

        int posX = 0;
        int posY = 0;
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                Log.d(TAG,"Coordinates: " + x + "," + y);
                if (x > mBlockWidth && x < mBlockWidth * 2) posX = 1;
                if (x > mBlockWidth * 2 && x < mBlockWidth * 3) posX = 2;

                if (y > mBlockHeight && y < mBlockHeight * 2) posY = 1;
                if (y > mBlockHeight * 2 && y < mBlockHeight * 3) posY = 2;

                mGameSessionActivity.humanTakesATurn(posX, posY);
                break;
        }
        return super.onTouchEvent(event);
    }

    protected boolean placeSymbol(int x, int y) {
        Log.d(TAG,"Thread ID in Board.placeSymbol:" + Thread.currentThread().getId());
        invalidateBlock(x, y);
        return true;
    }

    public void invalidateBlock(int x, int y) {
        Rect selBlock = new Rect((int) (x * mBlockWidth), (int) (y * mBlockHeight), (int) ((x + 1) * mBlockWidth), (int) ((y + 1) * mBlockHeight));
        invalidate(selBlock);
    }

    public boolean isInputEnabled() {
        return this.mIsEnabled;
    }

    protected void disableInput() {
        this.mIsEnabled = false;
        Log.d(TAG,"Board.disableInput(): Board not mIsEnabled");
    }

    protected void enableInput() {
        this.mIsEnabled = true;
        Log.d(TAG,"Board.enableInput(): Board mIsEnabled");
    }

    public Bitmap getBitmapForSymbol(Symbol aSymbol) {

        if (!sDrawablesInitialized) {
            try {
                Resources res = getResources();
                sSymX = BitmapFactory.decodeResource(res, R.drawable.x);
                sSymO = BitmapFactory.decodeResource(res, R.drawable.o);
                sSymBlank = BitmapFactory.decodeResource(res, R.drawable.blank);
                sDrawablesInitialized = true;
            } catch (OutOfMemoryError ome) {
                Log.d(TAG, "Ran out of memory decoding bitmaps");
                ome.printStackTrace();
            }
        }


        Bitmap symSelected = sSymBlank;

        if (aSymbol == Symbol.SymbolXCreate())
            symSelected = sSymX;
        else if (aSymbol == Symbol.SymbolOCreate())
            symSelected = sSymO;
        return symSelected;
    }
}

