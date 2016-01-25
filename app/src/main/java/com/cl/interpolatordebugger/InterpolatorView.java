package com.cl.interpolatordebugger;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnticipateInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cWX221950 on 2016/1/15.
 */
public class InterpolatorView extends View {


    ValueAnimator valueAnimator;
    Paint mCoordinatePaint;
    Paint mCurvePaint;
    Paint mBallPaint;
    int mDia;
    int Padding;

    PointF mOrigin;


    int mCoorDia;

    float mCurrentValueX;
    float mCurrentValueY;

    List<Float> mPathPoint;

    public void setInterpolator(android.animation.TimeInterpolator timeInterpolator) {
        TimeInterpolator = timeInterpolator;
    }

    TimeInterpolator TimeInterpolator;


    public void start()
    {
        if(valueAnimator!=null) {
            valueAnimator.cancel();
            clear();
            valueAnimator.start();
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureDimension(800, widthMeasureSpec);
        int height = measureDimension(800, heightMeasureSpec);
        mDia = Math.min(width, height);
        setMeasuredDimension(width, height);


        mOrigin = new PointF((float) (width - mDia) / 2 + Padding, (float) (height - mDia) / 2 + Padding);
        mCoorDia = mDia - Padding * 2;

        valueAnimator = ValueAnimator.ofFloat(0f, 1f);
//        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(5000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
//                mCurrentValueX = animation.getAnimatedFraction();
                mCurrentValueX = (float) animation.getAnimatedValue();
                mCurrentValueY = TimeInterpolator.getInterpolation(mCurrentValueX);
                updatePath();
                invalidate();

            }
        });

    }

    public int measureDimension(int defaultSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(defaultSize, specSize);
        } else {
            result = defaultSize;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        canvas.save();
        canvas.translate(mOrigin.x, mOrigin.y + mCoorDia);

        DrawCoordinateAxis(canvas, mCoordinatePaint);

//        canvas.drawPath(mPath, mCurvePaint);
        canvas.drawLines(convert2Array(mPathPoint), mCurvePaint);
        canvas.drawCircle(0, toY(mCurrentValueY), 16f,mBallPaint);

        canvas.restore();
    }


    float[] convert2Array(List<Float> list){
        float[] result = new float[list.size()];
        for(int i =0;i<list.size();i++)
        {
            result[i] =list.get(i);
        }
        return result;
    }

    void DrawCoordinateAxis(Canvas canvas, Paint paint) {
        //draw xAxis
        canvas.drawLine(0, 0,toX(1), 0, paint);

        //draw yAxis
        canvas.drawLine(0, 0, 0, toY(1), paint);

        //刻度
        float start = 0.2f;
        for (int i = 1; i <= 5; i++) {
            canvas.drawLine(toX(start * i), toY(0), toX(start * i), toY(0.2f) * 0.1f, paint);
        }

        for (int i = 1; i <= 5; i++) {
            canvas.drawLine(toX(0), toY(start * i), toX(0.2f) * 0.1f, toY(start * i), paint);
        }

    }

    void updatePath() {
        mPathPoint.add(toX(mCurrentValueX));
        mPathPoint.add(toY(mCurrentValueY));
    }


    public InterpolatorView(Context context) {
        super(context);
        init();

    }


    public InterpolatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        mCoordinatePaint = new Paint();
        mCoordinatePaint.setStyle(Paint.Style.STROKE);
        mCoordinatePaint.setColor(Color.BLACK);
        mCoordinatePaint.setStrokeWidth(2f);
        mCoordinatePaint.setTextSize(20f);
        mCoordinatePaint.setAntiAlias(true);
//        Rect rect = new Rect();
//        mCoordinatePaint.getTextBounds("0.0", 0, 2, rect);
//        Padding = Math.max(rect.width(), rect.height());
        Padding =150;

        mCurvePaint = new Paint();
        mCurvePaint.setStyle(Paint.Style.STROKE);
        mCurvePaint.setColor(Color.RED);
        mCurvePaint.setStrokeWidth(4f);
        mCurvePaint.setAntiAlias(true);

        mBallPaint = new Paint();
        mBallPaint.setStyle(Paint.Style.FILL);
        mBallPaint.setColor(Color.BLUE);

        mBallPaint.setAntiAlias(true);



        mPathPoint =new ArrayList<>();
        mPathPoint.add(0f);
        mPathPoint.add(0f);

        setInterpolator(new AnticipateInterpolator());

    }

    public void clear() {
        mPathPoint.clear();
        invalidate();
    }


    public InterpolatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private float toX(float x) {
        return x * mCoorDia;
    }

    private float toY(float y) {
        return - y * mCoorDia;
    }
}
