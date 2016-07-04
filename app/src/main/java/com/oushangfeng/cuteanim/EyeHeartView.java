package com.oushangfeng.cuteanim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by Oubowu on 2016/7/1 12:28.
 */
public class EyeHeartView extends View {

    private Paint mPaint;
    private Path mPath;

    private boolean mBlink;

    private int mEyeWidth;
    private float mEyeHeight = Integer.MIN_VALUE;
    private float mEyeBallScaleY;
    private float mEyeBallMoveX = Integer.MIN_VALUE;
    private float mEyeBallMoveY;
    private float mEyeBallRadius;

    private int mStep = 1;

    private float mCircleRadius = Integer.MIN_VALUE;
    private float mHeartRate;

    private ValueAnimator mAnimator1_1;
    private ValueAnimator mAnimator1_2;
    private ValueAnimator mAnimator1_3;
    private ValueAnimator mAnimator2_1;
    private ValueAnimator mAnimator2_2;
    private ValueAnimator mAnimator2_3;
    private ValueAnimator mAnimator3_1;

    public EyeHeartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EyeHeartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStrokeWidth(dip2px(context, 1));
        mPaint.setStyle(Paint.Style.STROKE);

        mPath = new Path();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(dip2px(getContext(), 250), dip2px(getContext(), 250));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mStep == 1) {
            canvas.drawColor(Color.parseColor("#4D91ED"));

            // 第一阶段：眼球转动然后眨眼
            mEyeWidth = getWidth() / 4;

            // 用于眨眼
            if (mEyeHeight == Integer.MIN_VALUE) {
                mEyeHeight = mEyeWidth / 2;
            }

            // 眼球半径
            mEyeBallRadius = mEyeWidth / 2 / 4;

            int startEyeX = (getWidth() - mEyeWidth) / 2;
            int startEyeY = getHeight() / 2;

            mPaint.setColor(Color.WHITE);
            mPaint.setStyle(Paint.Style.FILL);

            mPath.moveTo(startEyeX, startEyeY);
            mPath.quadTo(startEyeX + mEyeWidth / 2, startEyeY - mEyeHeight, startEyeX + mEyeWidth, startEyeY);
            mPath.moveTo(startEyeX + mEyeWidth, startEyeY);
            mPath.quadTo(startEyeX + mEyeWidth / 2, startEyeY + mEyeHeight, startEyeX, startEyeY);
            canvas.drawPath(mPath, mPaint);
            mPath.reset();

            mPaint.setColor(Color.parseColor("#4D91ED"));
            canvas.save();
            // TODO: 2016/7/1 眼球大小
            mEyeBallScaleY = 1;
            canvas.scale(1, mEyeBallScaleY);
            canvas.translate(getWidth() / 2 - mEyeBallRadius, getHeight() / 2);
            if (mEyeBallMoveX == Integer.MIN_VALUE) {
                mEyeBallMoveX = mEyeBallRadius;
            }
            canvas.drawCircle(mEyeBallMoveX, mEyeBallMoveY, mEyeBallRadius, mPaint);
            canvas.restore();
        } else if (mStep == 2) {

            canvas.drawColor(Color.parseColor("#4D91ED"));

            // 第二阶段
            mPaint.setColor(Color.parseColor("#F75A55"));
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, mCircleRadius, mPaint);

            mPaint.setColor(Color.WHITE);

            // 重置画板
            // 得到屏幕的长宽的一半
            int px = getMeasuredWidth() / 2;
            int py = getMeasuredHeight() / 2;
            // 路径的起始点
            mPath.moveTo(px, py - 5 * mHeartRate);
            // 根据心形函数画图
            for (double i = 0; i <= 2 * Math.PI; i += 0.001) {
                float x = (float) (16 * Math.sin(i) * Math.sin(i) * Math.sin(i));
                float y = (float) (13 * Math.cos(i) - 5 * Math.cos(2 * i) - 2 * Math.cos(3 * i) - Math.cos(4 * i));
                x *= mHeartRate;
                y *= mHeartRate;
                x = px - x;
                y = py - y;
                mPath.lineTo(x, y);
            }
            canvas.drawPath(mPath, mPaint);
            mPath.reset();

        } else {

            canvas.drawColor(Color.parseColor("#F75A55"));


            // 第三阶段
            mPaint.setColor(Color.parseColor("#4D91ED"));
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, mCircleRadius, mPaint);
            mPaint.setColor(Color.WHITE);

            // 眼球半径
            mEyeBallRadius = mEyeWidth / 2 / 4;

            int startEyeX = (getWidth() - mEyeWidth) / 2;
            int startEyeY = getHeight() / 2;

            mPaint.setColor(Color.WHITE);
            mPaint.setStyle(Paint.Style.FILL);

            mPath.moveTo(startEyeX, startEyeY);
            mPath.quadTo(startEyeX + mEyeWidth / 2, startEyeY - mEyeHeight, startEyeX + mEyeWidth, startEyeY);
            mPath.moveTo(startEyeX + mEyeWidth, startEyeY);
            mPath.quadTo(startEyeX + mEyeWidth / 2, startEyeY + mEyeHeight, startEyeX, startEyeY);
            canvas.drawPath(mPath, mPaint);
            mPath.reset();

            mPaint.setColor(Color.parseColor("#4D91ED"));
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, mEyeBallRadius, mPaint);

        }

        play();

    }

    private void play() {

        if (mAnimator1_1 == null) {
            mAnimator1_1 = new ValueAnimator();
            mAnimator1_1.setInterpolator(new AccelerateDecelerateInterpolator());
            mAnimator1_1.setFloatValues(0, 1);
            mAnimator1_1.setDuration(1500);
            mAnimator1_1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    final float fraction = animation.getAnimatedFraction();

                    if (fraction < 0.1) {
                        return;
                    }

                    mEyeBallScaleY = 1 - 0.25f / 0.9f * (fraction - 0.1f);

                    if (fraction >= 0.1 && fraction < 0.4) {
                        mEyeBallMoveX = mEyeBallRadius - 2 * mEyeBallRadius / 0.3f * (fraction - 0.1f);
                        mEyeBallMoveY = -(float) Math.sqrt(mEyeBallRadius * mEyeBallRadius - mEyeBallMoveX * mEyeBallMoveX);
                    } else if (fraction >= 0.4 && fraction < 0.7) {
                        final float newFraction = (fraction - 0.4f) / 0.3f;
                        // 代入贝塞尔公式得到贝塞尔曲线过程的x,y坐标
                        mEyeBallMoveX = getBazierValue(newFraction, -mEyeBallRadius, mEyeBallRadius, mEyeBallRadius * 3);
                        mEyeBallMoveY = -getBazierValue(newFraction, 0, -mEyeBallRadius * 2, 0);
                    } else if (fraction >= 0.7 && fraction <= 1) {
                        mEyeBallMoveX = mEyeBallRadius * 3 - 2 * mEyeBallRadius / 0.3f * (fraction - 0.7f);
                        mEyeBallMoveY = -(float) Math.sqrt(mEyeBallRadius * mEyeBallRadius - (mEyeBallMoveX - 2 * mEyeBallRadius) * (mEyeBallMoveX - 2 * mEyeBallRadius));
                    }

                    invalidate();
                }
            });
            mAnimator1_1.start();
            mStep = 1;

            mAnimator1_2 = new ValueAnimator();
            mAnimator1_2.setInterpolator(new AccelerateInterpolator());
            mAnimator1_2.setFloatValues(mEyeWidth / 2, -mEyeWidth / 2);
            mAnimator1_2.setDuration(250);
            mAnimator1_2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mEyeHeight = (Float) animation.getAnimatedValue();
                    mEyeBallMoveX = mEyeBallRadius;
                    mEyeBallMoveY = 0;
                    invalidate();
                }
            });
            mAnimator1_2.setStartDelay(500);

            mAnimator1_3 = new ValueAnimator();
            mAnimator1_3.setInterpolator(new DecelerateInterpolator());
            mAnimator1_3.setFloatValues(mEyeWidth / 2, 0);
            mAnimator1_3.setDuration(500);
            mAnimator1_3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mEyeHeight = (Float) animation.getAnimatedValue();
                    mEyeBallMoveX = mEyeBallRadius;
                    mEyeBallMoveY = 0;
                    invalidate();
                }
            });
            mAnimator1_3.setStartDelay(500);

            mAnimator2_1 = new ValueAnimator();
            mAnimator2_1.setInterpolator(new AccelerateInterpolator());
            mAnimator2_1.setFloatValues(getWidth() / 16, (float) Math.sqrt(getWidth() * getWidth() + getHeight() * getHeight()) / 2);
            mAnimator2_1.setDuration(500);
            mAnimator2_1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCircleRadius = (Float) animation.getAnimatedValue();
                    mHeartRate = 2 + animation.getAnimatedFraction() * 2;
                    invalidate();
                }
            });

            mAnimator2_2 = new ValueAnimator();
            mAnimator2_2.setInterpolator(new AccelerateDecelerateInterpolator());
            mAnimator2_2.setFloatValues(0, -1, 0, 1, 0);
            mAnimator2_2.setDuration(1000);
            mAnimator2_2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mHeartRate = 4 + (Float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            mAnimator2_2.setStartDelay(500);
            mAnimator2_2.setRepeatCount(1);

            mAnimator2_3 = new ValueAnimator();
            mAnimator2_3.setInterpolator(new AccelerateInterpolator());
            mAnimator2_3.setFloatValues(-4);
            mAnimator2_3.setDuration(500);
            mAnimator2_3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mHeartRate = 4 + (Float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            mAnimator2_3.setStartDelay(500);

            mAnimator3_1 = new ValueAnimator();
            mAnimator3_1.setInterpolator(new AccelerateInterpolator());
            mAnimator3_1.setFloatValues(getWidth() / 16, (float) Math.sqrt(getWidth() * getWidth() + getHeight() * getHeight()) / 2);
            mAnimator3_1.setDuration(500);
            mAnimator3_1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCircleRadius = (Float) animation.getAnimatedValue();
                    mEyeHeight = mEyeWidth / 2 * animation.getAnimatedFraction();
                    invalidate();
                }
            });

            mAnimator1_1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAnimator1_2.start();
                }
            });

            mAnimator1_2.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAnimator1_3.start();
                }
            });

            mAnimator1_3.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAnimator2_1.start();
                    mStep = 2;
                }
            });

            mAnimator2_1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAnimator2_2.start();
                }
            });

            mAnimator2_2.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAnimator2_3.start();
                }
            });

            mAnimator2_3.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAnimator3_1.start();
                    mStep = 3;
                }
            });

            mAnimator3_1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAnimator1_1.start();
                    mStep = 1;
                }
            });

        }

    }

    /**
     * 二阶贝塞尔公式：B(t)=(1-t)^2*P0+2*t*(1-t)*P1+t^2*P2,(t∈[0,1])
     */
    private float getBazierValue(float fraction, float p0, float p1, float p2) {
        return (1 - fraction) * (1 - fraction) * p0 + 2 * fraction * (1 - fraction) * p1 + fraction * fraction * p2;
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
