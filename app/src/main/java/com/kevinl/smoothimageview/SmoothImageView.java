package com.kevinl.smoothimageview;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;

/**
 * Author: liuk
 * Created at: 15/12/15
 * @http://blog.csdn.net/zzhou910/article/details/26379795
 */
public class SmoothImageView extends ImageView {

    private static final int STATE_NORMAL = 0;
    private static final int STATE_TRANSFORM_IN = 1;
    private static final int STATE_TRANSFORM_OUT = 2;
    private int mOriginalWidth;
    private int mOriginalHeight;
    private int mOriginalLocationX;
    private int mOriginalLocationY;
    private int mState = STATE_NORMAL;
    private Matrix mSmoothMatrix;
    private Bitmap mBitmap;
    private boolean mTransformStart = false;
    private Transfrom mTransfrom;
    private final int mBgColor = 0xFF000000;
    private int mBgAlpha = 0;
    private Paint mPaint;

    public SmoothImageView(Context context) {
        super(context);
        init();
    }

    public SmoothImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SmoothImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mSmoothMatrix = new Matrix();
        mPaint = new Paint();
        mPaint.setColor(mBgColor);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public void setOriginalInfo(int width, int height, int locationX, int locationY) {
        mOriginalWidth = width;
        mOriginalHeight = height;
        mOriginalLocationX = locationX;
        mOriginalLocationY = locationY;
        mOriginalLocationY = mOriginalLocationY - getStatusBarHeight(getContext());
    }

    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        java.lang.reflect.Field field = null;
        int x = 0;
        int statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
            return statusBarHeight;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    public void transformIn() {
        mState = STATE_TRANSFORM_IN;
        mTransformStart = true;
        invalidate();
    }

    public void transformOut() {
        mState = STATE_TRANSFORM_OUT;
        mTransformStart = true;
        invalidate();
    }

    private class Transfrom {
        float startScale;
        float endScale;
        float scale;
        LocationSizeF startRect;
        LocationSizeF endRect;
        LocationSizeF rect;

        void initStartIn() {
            scale = startScale;
            try {
                rect = (LocationSizeF) startRect.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        void initStartOut() {
            scale = endScale;
            try {
                rect = (LocationSizeF) endRect.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

    }

    private void initTransform() {
        if (getDrawable() == null) {
            return;
        }

        if (getDrawable() instanceof ColorDrawable) return;

        if (mBitmap == null || mBitmap.isRecycled()) {
            if (getDrawable() instanceof BitmapDrawable) {
                mBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
            } else if (getDrawable() instanceof GlideBitmapDrawable) {
                mBitmap = ((GlideBitmapDrawable) getDrawable()).getBitmap();
            } else if (getDrawable() instanceof GifDrawable){
                mBitmap = ((GifDrawable) getDrawable()).getFirstFrame();
            } else {
                return;
            }
        }

        if (mTransfrom != null) {
            return;
        }
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        mTransfrom = new Transfrom();

        float xSScale = mOriginalWidth / ((float) mBitmap.getWidth());
        float ySScale = mOriginalHeight / ((float) mBitmap.getHeight());
        float startScale = xSScale > ySScale ? xSScale : ySScale;
        mTransfrom.startScale = startScale;

        float xEScale = getWidth() / ((float) mBitmap.getWidth());
        float yEScale = getHeight() / ((float) mBitmap.getHeight());
        float endScale = xEScale < yEScale ? xEScale : yEScale;
        mTransfrom.endScale = endScale;

        mTransfrom.startRect = new LocationSizeF();
        mTransfrom.startRect.left = mOriginalLocationX;
        mTransfrom.startRect.top = mOriginalLocationY;
        mTransfrom.startRect.width = mOriginalWidth;
        mTransfrom.startRect.height = mOriginalHeight;

        mTransfrom.endRect = new LocationSizeF();
        float bitmapEndWidth = mBitmap.getWidth() * mTransfrom.endScale;
        float bitmapEndHeight = mBitmap.getHeight() * mTransfrom.endScale;
        mTransfrom.endRect.left = (getWidth() - bitmapEndWidth) / 2;
        mTransfrom.endRect.top = (getHeight() - bitmapEndHeight) / 2;
        mTransfrom.endRect.width = bitmapEndWidth;
        mTransfrom.endRect.height = bitmapEndHeight;

        mTransfrom.rect = new LocationSizeF();
    }

    private class LocationSizeF implements Cloneable {
        float left;
        float top;
        float width;
        float height;

        @Override
        public String toString() {
            return "[left:" + left + " top:" + top + " width:" + width + " height:" + height + "]";
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

    }

    private void getBmpMatrix() {
        if (getDrawable() == null) {
            return;
        }
        if (mTransfrom == null) {
            return;
        }
        if (mBitmap == null || mBitmap.isRecycled()) {
            mBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
        }

        mSmoothMatrix.setScale(mTransfrom.scale, mTransfrom.scale);
        mSmoothMatrix.postTranslate(-(mTransfrom.scale * mBitmap.getWidth() / 2 - mTransfrom.rect.width / 2),
                -(mTransfrom.scale * mBitmap.getHeight() / 2 - mTransfrom.rect.height / 2));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return;
        }

        if (mState == STATE_TRANSFORM_IN || mState == STATE_TRANSFORM_OUT) {
            if (mTransformStart) {
                initTransform();
            }
            if (mTransfrom == null) {
                super.onDraw(canvas);
                return;
            }

            if (mTransformStart) {
                if (mState == STATE_TRANSFORM_IN) {
                    mTransfrom.initStartIn();
                } else {
                    mTransfrom.initStartOut();
                }
            }

            if (mTransformStart) {
                Log.d("SmoothImageView", "mTransfrom.startScale:" + mTransfrom.startScale);
                Log.d("SmoothImageView", "mTransfrom.startScale:" + mTransfrom.endScale);
                Log.d("SmoothImageView", "mTransfrom.scale:" + mTransfrom.scale);
                Log.d("SmoothImageView", "mTransfrom.startRect:" + mTransfrom.startRect.toString());
                Log.d("SmoothImageView", "mTransfrom.endRect:" + mTransfrom.endRect.toString());
                Log.d("SmoothImageView", "mTransfrom.rect:" + mTransfrom.rect.toString());
            }

            mPaint.setAlpha(mBgAlpha);
            canvas.drawPaint(mPaint);

            int saveCount = canvas.getSaveCount();
            canvas.save();

            getBmpMatrix();
            canvas.translate(mTransfrom.rect.left, mTransfrom.rect.top);
            canvas.clipRect(0, 0, mTransfrom.rect.width, mTransfrom.rect.height);
            canvas.concat(mSmoothMatrix);
            getDrawable().draw(canvas);
            canvas.restoreToCount(saveCount);
            if (mTransformStart) {
                mTransformStart = false;
                startTransform(mState);
            }
        } else {
            mPaint.setAlpha(255);
            canvas.drawPaint(mPaint);
            super.onDraw(canvas);
        }
    }

    private void startTransform(final int state) {
        if (mTransfrom == null) {
            return;
        }
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        if (state == STATE_TRANSFORM_IN) {
            PropertyValuesHolder scaleHolder = PropertyValuesHolder.ofFloat("scale", mTransfrom.startScale, mTransfrom.endScale);
            PropertyValuesHolder leftHolder = PropertyValuesHolder.ofFloat("left", mTransfrom.startRect.left, mTransfrom.endRect.left);
            PropertyValuesHolder topHolder = PropertyValuesHolder.ofFloat("top", mTransfrom.startRect.top, mTransfrom.endRect.top);
            PropertyValuesHolder widthHolder = PropertyValuesHolder.ofFloat("width", mTransfrom.startRect.width, mTransfrom.endRect.width);
            PropertyValuesHolder heightHolder = PropertyValuesHolder.ofFloat("height", mTransfrom.startRect.height, mTransfrom.endRect.height);
            PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofInt("alpha", 0, 255);
            valueAnimator.setValues(scaleHolder, leftHolder, topHolder, widthHolder, heightHolder, alphaHolder);
        } else {
            PropertyValuesHolder scaleHolder = PropertyValuesHolder.ofFloat("scale", mTransfrom.endScale, mTransfrom.startScale);
            PropertyValuesHolder leftHolder = PropertyValuesHolder.ofFloat("left", mTransfrom.endRect.left, mTransfrom.startRect.left);
            PropertyValuesHolder topHolder = PropertyValuesHolder.ofFloat("top", mTransfrom.endRect.top, mTransfrom.startRect.top);
            PropertyValuesHolder widthHolder = PropertyValuesHolder.ofFloat("width", mTransfrom.endRect.width, mTransfrom.startRect.width);
            PropertyValuesHolder heightHolder = PropertyValuesHolder.ofFloat("height", mTransfrom.endRect.height, mTransfrom.startRect.height);
            PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofInt("alpha", 255, 0);
            valueAnimator.setValues(scaleHolder, leftHolder, topHolder, widthHolder, heightHolder, alphaHolder);
        }

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public synchronized void onAnimationUpdate(ValueAnimator animation) {
                mTransfrom.scale = (Float) animation.getAnimatedValue("scale");
                mTransfrom.rect.left = (Float) animation.getAnimatedValue("left");
                mTransfrom.rect.top = (Float) animation.getAnimatedValue("top");
                mTransfrom.rect.width = (Float) animation.getAnimatedValue("width");
                mTransfrom.rect.height = (Float) animation.getAnimatedValue("height");
                mBgAlpha = (Integer) animation.getAnimatedValue("alpha");
                invalidate();
                ((Activity) getContext()).getWindow().getDecorView().invalidate();
            }
        });
        valueAnimator.addListener(new ValueAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (state == STATE_TRANSFORM_IN) {
                    mState = STATE_NORMAL;
                }
                if (mTransformListener != null) {
                    mTransformListener.onTransformComplete(state);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        valueAnimator.start();
    }

    public void setOnTransformListener(TransformListener listener) {
        mTransformListener = listener;
    }

    private TransformListener mTransformListener;

    public static interface TransformListener {
        //mode STATE_TRANSFORM_IN 1 ,STATE_TRANSFORM_OUT 2
        void onTransformComplete(int mode);// mode 1
    }

}


