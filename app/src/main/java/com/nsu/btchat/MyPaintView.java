package com.nsu.btchat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

class Scale{
    int color;

    public Scale(int color){
        this.color=color;
    }
}


public class MyPaintView extends View {

    public boolean changed = false;
    static Canvas mCanvas;
    public int color;
    Bitmap mBitmap;
    ArrayList<Scale> arrS;
    Paint mPaint;
    float lastX;
    float lastY;
    static Path mPath = new Path();
    float mCurveEndX;
    float mCurveEndY;
    int mInvalidateExtraBorder = 10;
    static final float TOUCH_TOLERANCE = 8;



    public MyPaintView(Context context) {
        super(context);
        arrS=new ArrayList<Scale>();
        mPaint= new Paint(Paint.ANTI_ALIAS_FLAG);
        init(context);
    }

    public MyPaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(3.0F);
        this.lastX = -1;
        this.lastY = -1;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Bitmap img = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(img);
        canvas.drawColor(Color.WHITE);
        mBitmap = img;
        mCanvas = canvas;
    }

    @Override
    protected void onDraw(Canvas canvas) {
            arrS.add(new Scale(color));


        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }

        for(int j=1; j<arrS.size();j++){
            mPaint.setColor(arrS.get(j).color);

        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                changed = true;
                Rect rect = touchUp(event, false);
                if (rect != null) {
                    invalidate(rect);
                }
                mPath.rewind();

                return true;
            case MotionEvent.ACTION_DOWN:
                rect = touchDown(event);
                if (rect != null) {
                    invalidate(rect);
                }

                return true;
            case MotionEvent.ACTION_MOVE:
                rect = touchMove(event);
                if (rect != null) {
                    invalidate(rect);
                }
                return true;

        }
        return false;
    }

    private Rect touchMove(MotionEvent event) {
        Rect rect=processMove(event);
        return rect;
    }

    private Rect processMove(MotionEvent event) {
        final float x=event.getX();
        final float y=event.getY();

        final float dx=Math.abs(x-lastX);
        final float dy=Math.abs(y-lastY);

        Rect mInvalidateRect=new Rect();

        if(dx>=TOUCH_TOLERANCE || dy>=TOUCH_TOLERANCE){
            final int border=mInvalidateExtraBorder;

            mInvalidateRect.set((int)mCurveEndX-border,(int)mCurveEndY-border,(int)mCurveEndX+border,(int)mCurveEndY+border);

            float cx=mCurveEndX=(x+lastX)/2;
            float cy=mCurveEndY=(y+lastY)/2;

            mPath.quadTo(lastX,lastY,cx,cy);

            mInvalidateRect.union((int)lastX-border,(int)lastY-border,(int)lastX+border,(int)lastY+border);
            mInvalidateRect.union((int)cx-border,(int)cy-border,(int)cx,(int)cy+border);

            lastX=x;
            lastY=y;

            mCanvas.drawPath(mPath,mPaint);

        }

        return mInvalidateRect;
    }

    private Rect touchDown(MotionEvent event) {
        float x=event.getX();
        float y=event.getY();

        lastX=x;
        lastY=y;

        Rect mInvalidateRect=new Rect();
        mPath.moveTo(x,y);

        final int border=mInvalidateExtraBorder;
        mInvalidateRect.set((int)x-border,(int)y-border,(int)x+border,(int)y+border);
        mCurveEndX=x;
        mCurveEndY=y;

        mCanvas.drawPath(mPath,mPaint);
        return mInvalidateRect;
    }

    private Rect touchUp(MotionEvent event, boolean b) {
        Rect rect=processMove(event);
        return rect;
    }
    public static void clear() {
        if (mCanvas != null) {
            mCanvas.drawColor(Color.WHITE);
            mPath.reset();
        }


    }


}
