package com.oushangfeng.cuteanim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by Oubowu on 2016/7/6 16:11.
 */
public class ShineView extends View {

    private Paint mPaint;
    private Bitmap mBitmap;

    private float mBoomRadius;
    private float mBoom1Radius;
    private float mBoom2Radius;

    private float mOffset;

    private float mDiagonalLine;
    private float mMultiDegree;

    private boolean mBoom;
    private boolean mDisappear;

    private float mStrokeWidth;
    private float mScaleX;
    private float mScaleY;
    private PaintFlagsDrawFilter mDrawFilter;

    private ValueAnimator mAnimator1;
    private ValueAnimator mAnimator2;
    private ValueAnimator mAnimator3;
    private AnimatorSet mAnimatorSet;
    //    private Matrix mMatrix;

    public ShineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * @param bitmap
     * @see "http://blog.csdn.net/sjf0115/article/details/8698619"
     */
    private void tintColor(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(mBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(1);
        /*转成#FC9642*/
        float[] colorArray = {0, 0, 0, 0, 255, 0, 0, 0, 0, 150, 0, 0, 0, 0, 66, 0, 0, 0, 1, 0};
        colorMatrix.set(colorArray);
        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorMatrixFilter);
        canvas.drawBitmap(bitmap, 0, 0, paint);

    }


    private void init(Context context) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(Color.parseColor("#FC9642"));
        mPaint.setStrokeWidth(dip2px(context, 1));
        mPaint.setStyle(Paint.Style.STROKE);

        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        Bitmap bitmap = BitmapUtil.getScaleBitmapFromSrc(getContext(), R.mipmap.like, dip2px(getContext(), 250) / 2, dip2px(getContext(), 250) / 2);

        //        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.star);

        //        mMatrix = new Matrix();

        tintColor(bitmap);

        mDiagonalLine = (float) Math.sqrt(Math.pow(mBitmap.getWidth() / 2, 2) + Math.pow(mBitmap.getHeight() / 2, 2));

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

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(dip2px(getContext(), 250), dip2px(getContext(), 250));
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mScaleX > 0) {
            canvas.save();
            canvas.translate(getWidth() / 2, getHeight() / 2);
            canvas.scale(mScaleX, mScaleY);
            canvas.setDrawFilter(mDrawFilter);

            canvas.drawBitmap(mBitmap, -mBitmap.getWidth() / 2, -mBitmap.getHeight() / 2, mPaint);

            //            canvas.translate(getWidth() / 2 - mBitmap.getWidth() * mScaleX / 2, getHeight() / 2 - mBitmap.getHeight() * mScaleY / 2);
            //            mMatrix.setScale(mScaleX, mScaleY);
            //            canvas.drawBitmap(mBitmap, mMatrix, mPaint);

            canvas.restore();
        }

        mPaint.setColor(Color.parseColor("#FC9642"));

        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(0, 0, getWidth(), 0, mPaint);
        canvas.drawLine(0, getHeight(), getWidth(), getHeight(), mPaint);
        canvas.drawLine(0, 0, 0, getHeight(), mPaint);
        canvas.drawLine(getWidth(), 0, getWidth(), getHeight(), mPaint);

        mPaint.setStyle(mDisappear ? Paint.Style.STROKE : Paint.Style.FILL);
        if (mStrokeWidth != 0) {
            mPaint.setStrokeWidth(mStrokeWidth);
            canvas.drawCircle((getWidth() - mBitmap.getWidth()) / 2 + mBitmap.getWidth() / 2, (getHeight() - mBitmap.getHeight()) / 2 + mBitmap.getHeight() / 2,
                    mDisappear ? mBoomRadius - mStrokeWidth / 2 : mBoomRadius, mPaint);
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

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
