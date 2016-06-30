package com.oushangfeng.cuteanim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by Oubowu on 2016/6/29 17:52.
 */
public class SunRiseView extends View {

    private float mDegree;

    private boolean mBlink;

    private float mRiseOffset = Integer.MIN_VALUE;
    private float mEyesXOffset = 0;
    private int mSunRadius;

    private ValueAnimator mAnimator1;
    private ValueAnimator mAnimator2;
    private ValueAnimator mAnimator3;
    private Paint.FontMetrics mFontMetrics;
    private float mMeasureTextWidth;

    public SunRiseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SunRiseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private Paint mPaint;

    private void init(Context context, AttributeSet attrs) {

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(Color.parseColor("#7a6021"));
        mPaint.setStrokeWidth(dip2px(context, 3));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setTextSize(dip2px(context, 12));
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mFontMetrics = mPaint.getFontMetrics();
        mMeasureTextWidth = mPaint.measureText("SunRise");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(dip2px(getContext(), 250), dip2px(getContext(), 250));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.parseColor("#f5c142"));

        mPaint.setStyle(Paint.Style.STROKE);

        final int lineWidth = getWidth() / 2;

        final int startLineX = (getWidth() - lineWidth) / 2;

        final int startLineY = getHeight() * 5 / 8;

        canvas.drawLine(startLineX, startLineY, startLineX + lineWidth, startLineY, mPaint);

        mSunRadius = lineWidth * 3 / 8;

        if (mRiseOffset == Integer.MIN_VALUE) {
            mRiseOffset = mSunRadius / 4 + dip2px(getContext(), 2.5f) / 2.0f;
            mEyesXOffset = 0;
        }

        canvas.drawCircle(getWidth() / 2, startLineY + mRiseOffset, mSunRadius, mPaint);

        canvas.save();
        canvas.translate(getWidth() / 2, startLineY + mRiseOffset);
        for (int i = 0; i < 8; i++) {
            canvas.rotate(i == 0 ? 0 + mDegree : 45);
            canvas.drawLine(-lineWidth / 2, 0, -lineWidth / 2 + lineWidth / 8 - dip2px(getContext(), 3) / 2 * 4, 0, mPaint);
        }
        canvas.restore();

        mPaint.setStyle(Paint.Style.FILL);

        if (!mBlink) {
            canvas.drawCircle(startLineX + lineWidth / 8 + mSunRadius / 2 + mEyesXOffset, startLineY - mSunRadius / 4 + mRiseOffset, dip2px(getContext(), 2.5f), mPaint);
            canvas.drawCircle(startLineX + lineWidth / 8 + mSunRadius + mEyesXOffset, startLineY - mSunRadius / 4 + mRiseOffset, dip2px(getContext(), 2.5f), mPaint);
        }

        mPaint.setColor(Color.parseColor("#f5c142"));

        canvas.drawRect(0, startLineY + dip2px(getContext(), 3) / 2, getWidth(), getHeight(), mPaint);

        mPaint.setColor(Color.parseColor("#7a6021"));

        canvas.drawText("SunRise", (getWidth() - mMeasureTextWidth) / 2,
                startLineY + ((-mFontMetrics.ascent + mFontMetrics.descent) / 2 - mFontMetrics.descent) + dip2px(getContext(), 3) * 8, mPaint);

        play();

    }

    private void play() {
        if (mAnimator1 == null) {
            mAnimator1 = new ValueAnimator();
            mAnimator1.setInterpolator(new AccelerateInterpolator());
            mAnimator1.setFloatValues(1, 45);
            mAnimator1.setDuration(2000);
            mAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mDegree = (float) animation.getAnimatedValue();
                    final float fraction = animation.getAnimatedFraction();

                    mBlink = !((fraction >= 0 && fraction < 0.2) || (fraction >= 0.25 && fraction < 0.45) || (fraction >= 0.5 && fraction <= 1));

                    if (fraction >= 0.5 && fraction <= 0.75) {
                        mEyesXOffset = (float) (mSunRadius / 2.0f * (fraction - 0.5) * 4);
                    }

                    mRiseOffset = (mSunRadius / 4 + dip2px(getContext(), 2.5f) / 2.0f) * (1 - fraction);

                    invalidate();
                }
            });
            mAnimator1.setStartDelay(1000);
            mAnimator1.start();

            mAnimator2 = new ValueAnimator();
            mAnimator2.setInterpolator(new LinearInterpolator());
            mAnimator2.setFloatValues(1, 45);
            mAnimator2.setDuration(3000);
            mAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mDegree = (float) animation.getAnimatedValue();
                    final float fraction = animation.getAnimatedFraction();

                    mBlink = !((fraction >= 0 && fraction < 0.2) || (fraction >= 0.25 && fraction < 0.45) || (fraction >= 0.5 && fraction <= 1));

                    if (fraction >= 0.5 && fraction <= 0.75) {
                        mEyesXOffset = mSunRadius / 2.0f - (float) (mSunRadius / 2.0f * (fraction - 0.5) * 4);
                    }

                    mRiseOffset = -(mSunRadius / 4 + dip2px(getContext(), 2.5f) / 2.0f) * fraction;

                    invalidate();
                }
            });

            mAnimator3 = new ValueAnimator();
            mAnimator3.setInterpolator(new AccelerateInterpolator());
            mAnimator3.setFloatValues(1, 45);
            mAnimator3.setDuration(1000);
            mAnimator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mDegree = (float) animation.getAnimatedValue() * 2;
                    final float fraction = animation.getAnimatedFraction();

                    mBlink = !((fraction >= 0 && fraction < 0.2) || (fraction >= 0.35 && fraction <= 1));

                    mEyesXOffset = 0;

                    mRiseOffset = -(mSunRadius / 4 + dip2px(getContext(), 2.5f) / 2.0f) + (mSunRadius / 4 + dip2px(getContext(), 2.5f) / 2.0f) * 2 * fraction;

                    invalidate();
                }
            });

            mAnimator1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAnimator2.start();
                }
            });

            mAnimator2.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAnimator3.start();
                }
            });

            mAnimator3.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAnimator1.start();
                }
            });

        }
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
