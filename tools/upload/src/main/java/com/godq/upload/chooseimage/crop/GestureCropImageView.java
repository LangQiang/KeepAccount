package com.godq.upload.chooseimage.crop;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * 作者：aprz on 2016/4/1.
 * 邮箱：aprz512@163.com
 */
public class GestureCropImageView extends CropImageView {

    private static final int DOUBLE_TAP_ZOOM_DURATION = 200;

    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureDetector;

    private float mMidPntX, mMidPntY;

    private boolean mIsDoubleTapEnable = true;//是否允许双击缩放
    private boolean mIsScaleEnabled = true;
    private int mDoubleTapScaleSteps = 5;

    private boolean mIsTouchable = false;

    public GestureCropImageView(Context context) {
        this(context, null);
    }

    public GestureCropImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureCropImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        setupGestureListeners();
    }

    private void setupGestureListeners() {
        mGestureDetector = new GestureDetector(getContext(), new GestureListener(), null, true);
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    public void setDoubleTapScaleEanbled(boolean scaleEanbled){
        mIsDoubleTapEnable = scaleEanbled;
    }

    public boolean isScaleEnabled() {
        return mIsScaleEnabled;
    }

    public void setScaleEnabled(boolean scaleEnabled) {
        mIsScaleEnabled = scaleEnabled;
    }

    public int getDoubleTapScaleSteps() {
        return mDoubleTapScaleSteps;
    }

    public void setDoubleTapScaleSteps(int doubleTapScaleSteps) {
        mDoubleTapScaleSteps = doubleTapScaleSteps;
    }

    protected float getDoubleTapTargetScale() {
        return getCurrentScale() * (float) Math.pow(getMaxScale() / getMinScale(), 1.0f / mDoubleTapScaleSteps);
    }

    public Matrix getCurrentImageMatrix() {
        return mCurrentImageMatrix;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mIsTouchable) {
            return false;
        }

        // 图片加载失败时，禁止触摸事件
        if (getViewBitmap() == null) {
            return false;
        }

        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
            cancelAllAnimations();
        }

        if (event.getPointerCount() > 1) {
            mMidPntX = (event.getX(0) + event.getX(1)) / 2;
            mMidPntY = (event.getY(0) + event.getY(1)) / 2;
        }

        mGestureDetector.onTouchEvent(event);

        if (mIsScaleEnabled) {
            mScaleDetector.onTouchEvent(event);
        }

        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            setImageToWrapCropBounds();
        }
        return true;
    }

    public void enableTouch(boolean enable) {
        mIsTouchable = enable;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            postScale(detector.getScaleFactor(), mMidPntX, mMidPntY);
            return true;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mIsDoubleTapEnable) {
                zoomImageToPosition(getDoubleTapTargetScale(), e.getX(), e.getY(),
                        DOUBLE_TAP_ZOOM_DURATION);
            }
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            postTranslate(-distanceX, -distanceY);
            return true;
        }

    }
}
