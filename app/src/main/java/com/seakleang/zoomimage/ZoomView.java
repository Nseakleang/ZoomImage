package com.seakleang.zoomimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ZoomView extends android.support.v7.widget.AppCompatImageView {

    private PointF zoomPosition;
    private boolean zooming = false;
    private Matrix matrix;
    private Paint paint,linepaint;
    private Bitmap bitmap;
    private BitmapShader shader;
    private int sizeOfMagnifier = 100;

    private Paint mStartCircle, mEndCircle;
    private Paint mStartPoint, mEndPoint;

    private float mStartCircleX, mStartCircleY, mEndCircleX, mEndCircleY;

    private float mStartCircleRadius = 50f;
    private float mEndCircleRadius = 50f;

    private float mStartPointRadius = 15f;
    private float mEndPointRadius = 15f;

    public ZoomView(Context context) {
        super(context);
        init();
    }

    public ZoomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZoomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        zoomPosition = new PointF(0, 0);
        matrix = new Matrix();
        paint = new Paint();
        linepaint = new Paint();
        linepaint.setColor(Color.WHITE);
        linepaint.setStrokeWidth(2f);

        mStartCircle = new Paint();
        mEndCircle = new Paint();

        mStartCircle.setColor(getResources().getColor(R.color.colorAccent));
        mStartCircle.setStrokeWidth(1f);
        mStartCircle.setStyle(Paint.Style.STROKE);

        mEndCircle.setColor(getResources().getColor(R.color.colorPrimary));
        mEndCircle.setStrokeWidth(1f);
        mEndCircle.setStyle(Paint.Style.STROKE);

        mStartPoint = new Paint();
        mStartPoint.setColor(getResources().getColor(R.color.colorAccent));
        mEndPoint = new Paint();
        mEndPoint.setColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        zoomPosition.x = event.getX();
        zoomPosition.y = event.getY();

        double distanceStart = Math.sqrt((Math.pow(mStartCircleX - event.getX(),2)
                +Math.pow(mStartCircleY - event.getY(),2)));
        double distanceEnd = Math.sqrt((Math.pow(mEndCircleX - event.getX(),2)
                +Math.pow(mEndCircleY - event.getY(),2)));
        if (distanceStart <= mStartCircleRadius) {
            mStartCircleX = zoomPosition.x;
            mStartCircleY = zoomPosition.y;
        }
        else if (distanceEnd <= mEndCircleRadius) {
            mEndCircleX = zoomPosition.x;
            mEndCircleY = zoomPosition.y;
        }

        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                zooming = true;
                this.invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                zooming = false;
                this.invalidate();
                break;

            default:
                break;
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPoints(canvas);
        drawZoom(canvas);
    }

    private void drawZoom(Canvas canvas) {
        if (!zooming)
        {
            buildDrawingCache();
        }
        else
        {

            bitmap = getDrawingCache();
            shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

            paint = new Paint();
            paint.setShader(shader);
            matrix.reset();
//            matrix.postScale(5f, 5f, zoomPosition.x, zoomPosition.y);
            RectF src = new RectF(zoomPosition.x-30, zoomPosition.y-30, zoomPosition.x+30, zoomPosition.y+30);
            RectF dst = new RectF(0, 0, 100, 100);
            matrix.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER);
            matrix.postScale(2f, 2f);
            paint.getShader().setLocalMatrix(matrix);
            canvas.drawCircle(100, 100, sizeOfMagnifier, paint);
            canvas.drawLine(100,75,100,125,linepaint);
            canvas.drawLine(75,100,125,100,linepaint);
        }
    }

    private void drawPoints(Canvas canvas){
        if (mStartCircleX == 0 || mStartCircleY == 0) {
            mStartCircleX = getWidth() / 2 - 100;
            mStartCircleY = getHeight() / 2;
        }if (mEndCircleX == 0 || mEndCircleY == 0) {
            mEndCircleX = getWidth() / 2 + 100;
            mEndCircleY = getHeight() / 2;
        }
        Paint pathPain = new Paint();
        pathPain.setColor(Color.parseColor("#00cca0"));
        pathPain.setAntiAlias(true);
        pathPain.setStrokeWidth(5f);
        pathPain.setStyle(Paint.Style.STROKE);

        canvas.drawLine(mStartCircleX, mStartCircleY, mEndCircleX, mEndCircleY, pathPain);

        canvas.drawCircle(mStartCircleX, mStartCircleY, mStartCircleRadius, mStartCircle);
        canvas.drawCircle(mStartCircleX, mStartCircleY, mStartPointRadius, mStartPoint);

        canvas.drawCircle(mEndCircleX, mEndCircleY, mEndCircleRadius, mEndCircle);
        canvas.drawCircle(mEndCircleX, mEndCircleY, mEndPointRadius, mEndPoint);
    }
}
