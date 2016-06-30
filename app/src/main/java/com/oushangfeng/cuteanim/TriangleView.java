package com.oushangfeng.cuteanim;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Oubowu on 2016/6/30 16:02.
 */
public class TriangleView extends View {

    private Paint mPaint;
    private Path mPath;
    private ValueAnimator mAnimator1;
    private float mDegree;
    private int mLengthOffset;

    private int mShadowHeight;
    private int mHeight;
    private Paint.FontMetrics mFontMetrics;
    private float mMeasureTextWidth;

    public TriangleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TriangleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(Color.parseColor("#ffffff"));
        mPaint.setStrokeWidth(dip2px(context, 1));
        mPaint.setStyle(Paint.Style.FILL);

        mPaint.setTextSize(dip2px(context, 12));
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mFontMetrics = mPaint.getFontMetrics();
        mMeasureTextWidth = mPaint.measureText("Circulatory Triangle");

        mPath = new Path();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(dip2px(getContext(), 250), dip2px(getContext(), 250));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.parseColor("#383D44"));

        int length = getWidth() / 3 + mLengthOffset;

        mHeight = (int) Math.sqrt(length * length - (length / 2) * (length / 2));

        int startX = (getWidth() - length) / 2;

        int startY = getHeight() / 2 + mHeight / 3;

        // 等边三角形重心
        int baryCenterX = startX + length / 2;
        int baryCenterY = startY - mHeight / 3;

        // 三条分割线坐标
        int divideLine1X = startX + length / 4;
        int divideLine1Y = startY - mHeight / 2;

        int divideLine2X = divideLine1X + length / 2;
        int divideLine2Y = divideLine1Y;

        mPath.moveTo(startX, startY);
        mPath.lineTo(startX + length, startY);
        mPath.lineTo(startX + length / 2, startY - mHeight);
        mPath.lineTo(startX, startY);

        mPaint.setColor(Color.parseColor("#ffffff"));
        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawText("Circulatory Triangle", (getWidth() - mMeasureTextWidth) / 2, getHeight() + mFontMetrics.ascent, mPaint);

        canvas.rotate(mDegree, baryCenterX, baryCenterY);

        canvas.drawPath(mPath, mPaint);

        mPath.reset();

        mPaint.setColor(Color.parseColor("#383D44"));

        for (int i = 0; i < 3; i++) {
            canvas.save();
            canvas.rotate(120, baryCenterX, baryCenterY);
            canvas.drawRect(divideLine1X, divideLine1Y - mShadowHeight, divideLine2X, divideLine2Y, mPaint);
        }
        canvas.restore();

        //        canvas.drawLine(divideLine1X, divideLine1Y, divideLine2X, divideLine2Y, mPaint);
        //        canvas.drawLine(divideLine2X, divideLine2Y, divideLine3X, divideLine3Y, mPaint);
        //        canvas.drawLine(divideLine3X, divideLine3Y, divideLine1X, divideLine1Y, mPaint);


        play();

    }

    private void play() {

        if (mAnimator1 == null) {
            mAnimator1 = new ValueAnimator();
            mAnimator1.setInterpolator(new LinearInterpolator());
            mAnimator1.setFloatValues(1, 60);
            mAnimator1.setDuration(3000);
            mAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mDegree = (float) animation.getAnimatedValue();

                    final float fraction = animation.getAnimatedFraction();

                    mLengthOffset = (int) (getWidth() / 3 * fraction);

                    mShadowHeight = (int) (mHeight / 2 * fraction);

                    invalidate();
                }
            });
            mAnimator1.setRepeatCount(ValueAnimator.INFINITE);
            mAnimator1.start();
        }

    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
