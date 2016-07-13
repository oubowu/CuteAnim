package com.oushangfeng.cuteanim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

/**
 * Created by Oubowu on 2016/7/13 11:55.
 */
public class ShineContainer extends View {

    private float mDiagonalLine;
    private float mBoomRadius;
    private float mBoom1Radius;
    private float mBoom2Radius;
    private float mStrokeWidth;
    private float mMultiDegree;
    private float mScaleX;
    private float mScaleY;

    private Paint mPaint;
    private ViewParent mViewParent;
    private View mTargetView;
    private int mTargetId;
    private int mTargetViewHeight = 0;
    private int mTargetViewWidth = 0;

    private boolean mDisappear;
    private boolean mBoom;
    private float mOffset;

    private ValueAnimator mAnimator1;
    private ValueAnimator mAnimator2;
    private ValueAnimator mAnimator3;
    private AnimatorSet mAnimatorSet;
    private int[] mLocation;
    private boolean mSameLevel;
    private Toolbar mToolbar;

    public ShineContainer(Context context) {
        super(context);
    }

    public ShineContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShineContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ShineContainer);
        mTargetId = ta.getResourceId(R.styleable.ShineContainer_targetId, 0);
        ta.recycle();

        init();

    }

    private void init() {
        if (!isInEditMode()) {
            post(new Runnable() {
                @Override
                public void run() {

                    if (mTargetView == null) {
                        mViewParent = getParent();
                        findTargetView();
                    }

                    if (mTargetView == null) {
                        Log.e("TAG", "can't find target view.");
                    } else {

                        mTargetView.post(new Runnable() {
                            @Override
                            public void run() {

                                mTargetViewHeight = mTargetView.getMeasuredHeight();
                                mTargetViewWidth = mTargetView.getMeasuredWidth();

                                Log.e("TAG", "ShineContainer-97行-run(): " + mTargetViewWidth + ";" + mTargetViewHeight);

                                mTargetViewHeight = mTargetViewWidth = Math.max(mTargetViewHeight, mTargetViewWidth);

                                mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
                                mPaint.setColor(Color.parseColor("#FC9642"));
                                mPaint.setStrokeWidth(dip2px(getContext(), 1));
                                mPaint.setStyle(Paint.Style.STROKE);

                                mDiagonalLine = (float) Math.sqrt(Math.pow(mTargetViewWidth / 2, 2) + Math.pow(mTargetViewHeight / 2, 2));

                                mBoomRadius = mDiagonalLine / 2;

                                mBoom1Radius = mDiagonalLine / 2 / 3;

                                mBoom2Radius = mDiagonalLine / 2 / 5;

                                mStrokeWidth = 0;

                                mMultiDegree = 360.0f / 7;

                                mScaleX = 1;
                                mScaleY = 1;

                                setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        play();
                                    }
                                });

                                mLocation = new int[2];

                                if (mSameLevel) {
                                    mLocation[0] = mTargetView.getLeft();
                                    mLocation[1] = mTargetView.getTop();
                                } else {
                                    mTargetView.getLocationInWindow(mLocation);
                                    final Toolbar toolbar = findToolBar();
                                    if (toolbar != null) {
                                        mLocation[1] -= getStatusBarHeight(getContext()) + toolbar.getMeasuredHeight();
                                    } else {
                                        mLocation[1] -= getStatusBarHeight(getContext());
                                    }
                                }

                                if (mTargetView instanceof TextView) {
                                    mLocation[1] -= mTargetView.getMeasuredHeight();
                                }

                                ShineContainer.this.setX(mLocation[0] - ((int) (mTargetViewWidth * 2.5f) - mTargetView.getMeasuredWidth()) / 2);
                                ShineContainer.this.setY(mLocation[1] - ((int) (mTargetViewHeight * 2.5f) - mTargetView.getMeasuredHeight()) / 2);

                                measure(0, 0);
                                requestLayout();
                            }
                        });

                    }
                }
            });
        }
    }


    private void findTargetView() {
        if (mTargetId != 0 && mViewParent != null && mViewParent instanceof ViewGroup) {
            mTargetView = ((ViewGroup) mViewParent).findViewById(mTargetId);
            if (mTargetView == null) {
                mViewParent = mViewParent.getParent();
                findTargetView();
            } else if (mTargetView.getParent() != null && mTargetView.getParent() == mViewParent) {
                // 目标View和此View同级
                mSameLevel = true;
            }
        }
    }

    private Toolbar findToolBar() {

        if (getContext() instanceof Activity) {

            Activity activity = (Activity) getContext();

            final ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();

            findToolbar(decorView);

        }

        return mToolbar;

    }

    private void findToolbar(ViewGroup decorView) {
        if (mToolbar != null) {
            return;
        }

        for (int i = 0; i < decorView.getChildCount(); i++) {
            final View view = decorView.getChildAt(i);
            if (view instanceof ViewGroup) {
                if (view instanceof Toolbar) {
                    mToolbar = (Toolbar) view;
                } else {
                    findToolbar((ViewGroup) view);
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int) (mTargetViewWidth * 2.5f), (int) (mTargetViewHeight * 2.5f));
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mTargetView == null || mTargetViewHeight == 0 || mTargetViewWidth == 0) {
            return;
        }

        if (mScaleX > 0) {
            mTargetView.setVisibility(VISIBLE);
            mTargetView.setScaleX(mScaleX);
            mTargetView.setScaleY(mScaleY);
        } else {
            mTargetView.setVisibility(INVISIBLE);
        }

        mPaint.setColor(Color.parseColor("#FC9642"));

        mPaint.setStyle(mDisappear ? Paint.Style.STROKE : Paint.Style.FILL);
        if (mStrokeWidth != 0) {
            mPaint.setStrokeWidth(mStrokeWidth);
            canvas.drawCircle((getWidth()) / 2, (getHeight()) / 2, mDisappear ? mBoomRadius - mStrokeWidth / 2 : mBoomRadius, mPaint);
        }

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(dip2px(getContext(), 1));
        if (mBoom) {
            canvas.save();

            canvas.translate(getWidth() / 2, getHeight() / 2);

            for (int i = 0; i < 7; i++) {

                canvas.rotate(i == 0 ? 20 : mMultiDegree);

                canvas.drawCircle(0, -mDiagonalLine * 5 / 6 - mDiagonalLine / 4 - mOffset, mBoom1Radius, mPaint);

            }

            canvas.restore();

            canvas.save();

            canvas.translate(getWidth() / 2, getHeight() / 2);

            for (int i = 0; i < 7; i++) {

                canvas.rotate(i == 0 ? 8 : mMultiDegree);

                mPaint.setColor(Color.parseColor("#F77E47"));

                canvas.drawCircle(0, -mDiagonalLine * 5 / 6 - mOffset, mBoom2Radius, mPaint);
            }

            canvas.restore();
        }

    }

    private void play() {
        if (mAnimatorSet == null) {
            mAnimator1 = new ValueAnimator();
            mAnimator1.setInterpolator(new AccelerateInterpolator());
            mAnimator1.setFloatValues(0, mDiagonalLine / 4, mDiagonalLine / 2);
            mAnimator1.setDuration(200);
            mAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if ((float) animation.getAnimatedValue() <= mDiagonalLine / 4) {
                        mStrokeWidth = dip2px(getContext(), 6);
                        mBoom = false;
                        mDisappear = false;
                        mBoomRadius = mDiagonalLine / 2 + (float) animation.getAnimatedValue();
                    } else {
                        mBoomRadius = mDiagonalLine / 2 + (float) animation.getAnimatedValue();
                        mOffset = ((float) animation.getAnimatedValue() - mDiagonalLine / 4) / 4;
                    }
                    mScaleX = mScaleY = 0;
                    invalidate();
                }
            });
            mAnimator1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mBoom = true;
                    mScaleX = mScaleY = (float) Math.sqrt(0.5);
                    mBoom1Radius = mDiagonalLine / 2 / 3;
                    mBoom2Radius = mDiagonalLine / 2 / 5;
                    invalidate();
                }
            });

            mAnimator2 = new ValueAnimator();
            mAnimator2.setFloatValues(dip2px(getContext(), 6), dip2px(getContext(), 0));
            mAnimator2.setDuration(200);
            mAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mDisappear = true;
                    mStrokeWidth = (float) animation.getAnimatedValue();

                    final float fraction = animation.getAnimatedFraction();

                    mOffset = mDiagonalLine / 4 / 2f + mDiagonalLine / 3.5f * fraction;

                    if (fraction <= 0.5) {
                        mScaleX = mScaleY = 0;
                    } else {
                        mScaleX = mScaleY = (float) (Math.sqrt(0.5) + (Math.sqrt(2.0) - Math.sqrt(0.5)) * (fraction - 0.5) / 0.5);
                    }

                    invalidate();
                }
            });

            mAnimator3 = new ValueAnimator();
            mAnimator3.setInterpolator(new DecelerateInterpolator());
            mAnimator3.setFloatValues((float) Math.sqrt(2.0), (float) Math.sqrt(2.5), 1, (float) Math.sqrt(0.5), 1);
            mAnimator3.setDuration(500);
            mAnimator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    mDisappear = true;

                    mStrokeWidth = 0;

                    mScaleX = mScaleY = (float) animation.getAnimatedValue();

                    final float fraction = animation.getAnimatedFraction();

                    mOffset = (mDiagonalLine / 4 / 2f + mDiagonalLine / 3.5f) + mDiagonalLine / 3.5f * fraction;

                    mBoom1Radius = mDiagonalLine / 2 / 3 - mDiagonalLine / 2 / 3 * fraction;

                    mBoom2Radius = mDiagonalLine / 2 / 5 - mDiagonalLine / 2 / 5 * fraction;

                    invalidate();
                }
            });

            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playSequentially(mAnimator1, mAnimator2, mAnimator3);
            mAnimatorSet.start();

        } else {
            mAnimatorSet.start();
        }
    }

    public void setTargetView(View targetView) {
        mTargetView = targetView;
        init();
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
