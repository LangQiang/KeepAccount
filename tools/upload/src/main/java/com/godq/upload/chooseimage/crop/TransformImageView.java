package com.godq.upload.chooseimage.crop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;


/**
 * 作者：aprz on 2016/4/1.
 * 邮箱：aprz512@163.com
 */
public class TransformImageView extends androidx.appcompat.widget.AppCompatImageView {

    private static final int RECT_CORNER_POINTS_COORDS = 8;
    private static final int RECT_CENTER_POINT_COORDS = 2;
    private static final int MATRIX_VALUES_COUNT = 9;
    protected final float[] mCurrentImageCorners = new float[RECT_CORNER_POINTS_COORDS];
    protected final float[] mCurrentImageCenter = new float[RECT_CENTER_POINT_COORDS];
    private final String TAG = "TransformImageView";
    private final float[] mMatrixValues = new float[MATRIX_VALUES_COUNT];
    protected Matrix mCurrentImageMatrix = new Matrix();
    protected int mThisWidth, mThisHeight;
    protected TransformImageListener mTransformImageListener;
    private Uri mImageUri;
    private int mMaxBitmapSideLength;
    private float[] mInitialImageCorners;
    private float[] mInitialImageCenter;
    private boolean mBitmapWasLoaded = false;

    public TransformImageView(Context context) {
        this(context, null);
    }

    public TransformImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TransformImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public void setTransformImageListener(TransformImageListener transformImageListener) {
        mTransformImageListener = transformImageListener;
    }

    protected void init() {
        setScaleType(ScaleType.MATRIX);
    }

    /**
     * 限制缩放类型为 MATRIX
     *
     * @param scaleType 缩放类型参数
     */
    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType == ScaleType.MATRIX) {
            super.setScaleType(scaleType);
        } else {
            Log.e(TAG, "Invalid ScaleType. Only ScaleType.MATRIX can be used");
        }
    }

    protected int getMaxBitmapSideLength() {
        if (mMaxBitmapSideLength <= 0) {
            mMaxBitmapSideLength = calculateMaxBitmapSize();
        }

        return mMaxBitmapSideLength;
    }

    public void setMaxBitmapSideLength(int maxBitmapSideLength) {
        this.mMaxBitmapSideLength = maxBitmapSideLength;
    }

    /**
     * 以屏幕宽高的对角线作为最大值
     *
     * @return 图片宽高默认最大值
     */
    @SuppressWarnings("deprecation")
    protected int calculateMaxBitmapSize() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point size = new Point();
        int width, height;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(size);
            width = size.x;
            height = size.y;
        } else {
            width = display.getWidth();
            height = display.getHeight();
        }
        return (int) Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));
    }

    public Uri getImageUri() {
        return mImageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.mImageUri = imageUri;

        int reqLength = getMaxBitmapSideLength();

        BitmapLoadUtils.decodeBitmapInBackground(
                getContext(),
                imageUri,
                reqLength,
                reqLength,
                new BitmapLoadUtils.BitmapLoadCallback() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap) {
                        mBitmapWasLoaded = true;
                        setImageDrawable(new BitmapDrawable(getResources(), bitmap));
                        invalidate();
                        // 强制更新自己 触发回调方法
                        requestLayout();
                    }

                    @Override
                    public void onFailure(Exception bitmapWorkerException) {
                        mBitmapWasLoaded = false;
                        Log.e(TAG, "onFailure: setImageUri", bitmapWorkerException);
                        if (mTransformImageListener != null) {
                            mTransformImageListener.onLoadFailure(bitmapWorkerException);
                        }
                        // 失败也要刷新啊
                        setImageDrawable(null);
                        invalidate();
                        requestLayout();
                    }
                });
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed || mBitmapWasLoaded) {

            if (mBitmapWasLoaded) {
                // 避免其它地方的布局变化 引起 onImageLaidout 的重新调用
                mBitmapWasLoaded = false;
            }

            left = getPaddingLeft();
            top = getPaddingTop();
            right = getWidth() - getPaddingRight();
            bottom = getHeight() - getPaddingBottom();
            mThisWidth = right - left;
            mThisHeight = bottom - top;

            onImageLaidOut();
        }
    }

    /**
     * ImageView layout 完成之后，需要初始化 mInitialImageCorners 和 mInitialImageCenter
     */
    protected void onImageLaidOut() {
        final Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        float w = drawable.getIntrinsicWidth();
        float h = drawable.getIntrinsicHeight();

        Log.d(TAG, String.format("Image size: [%d:%d]", (int) w, (int) h));

        RectF initialImageRect = new RectF(0, 0, w, h);
        mInitialImageCorners = RectUtils.getCornersFromRect(initialImageRect);
        mInitialImageCenter = RectUtils.getCenterFromRect(initialImageRect);
    }

    /**
     * 图片在 x 和 y 方向上分别平移 deltaX 和 deltaY 的距离。
     *
     * @param deltaX x 方向平移距离
     * @param deltaY y 方向平移距离
     */
    public void postTranslate(float deltaX, float deltaY) {
        if (deltaX != 0 || deltaY != 0) {
            mCurrentImageMatrix.postTranslate(deltaX, deltaY);
            setImageMatrix(mCurrentImageMatrix);
        }
    }

    /**
     * 以 px 和 py 为中心，对图片在现有基础上缩放 deltaScale
     *
     * @param deltaScale 缩放大小
     * @param px         缩放中心 x
     * @param py         缩放中心 y
     */
    public void postScale(float deltaScale, float px, float py) {
        if (deltaScale != 0) {
            mCurrentImageMatrix.postScale(deltaScale, deltaScale, px, py);
            setImageMatrix(mCurrentImageMatrix);
        }
    }

    /**
     * 以 px 和 py 为旋转中心，将图片在现有基础上旋转 deltaAngle
     *
     * @param deltaAngle 旋转角度
     * @param px         旋转中心 x
     * @param py         旋转中心 y
     */
    public void postRotate(float deltaAngle, float px, float py) {
        if (deltaAngle != 0) {
            mCurrentImageMatrix.postRotate(deltaAngle, px, py);
            setImageMatrix(mCurrentImageMatrix);
        }
    }

    /**
     * 获取当前图片在原有基础上的缩放值
     *
     * @return 缩放值
     */
    public float getCurrentScale() {
        return getMatrixScale(mCurrentImageMatrix);
    }

    /**
     * 获取矩阵中的缩放值
     * 计算图片的缩放值，因为 x 和 y 的缩放值是一样的
     * 取其中一个就行
     */
    public float getMatrixScale(Matrix matrix) {
        return (float) Math.sqrt(Math.pow(getMatrixValue(matrix, Matrix.MSCALE_X), 2)
                + Math.pow(getMatrixValue(matrix, Matrix.MSKEW_Y), 2));
    }

    /**
     * 获取当前图片在原图基础上的旋转角度
     *
     * @return 旋转角度
     */
    public float getCurrentAngle() {
        return getMatrixAngle(mCurrentImageMatrix);
    }

    /**
     * 根据矩阵，获取旋转角度
     *
     * @param matrix 矩阵
     * @return 旋转角度
     */
    public float getMatrixAngle(Matrix matrix) {
        return (float) -(Math.atan2(getMatrixValue(matrix, Matrix.MSKEW_X),
                getMatrixValue(matrix, Matrix.MSCALE_X)) * (180 / Math.PI));
    }

    /**
     * 设置图片的矩阵
     *
     * @param matrix 图片矩阵
     */
    @Override
    public void setImageMatrix(Matrix matrix) {
        super.setImageMatrix(matrix);
        updateCurrentImagePoints();
    }

    /**
     * 恢复之前的裁剪矩阵
     *
     * @param matrix 矩阵
     */
    public void restoreMatrix(Matrix matrix) {
        if (matrix == null) {
            return;
        }
        mCurrentImageMatrix = matrix;
        setImageMatrix(mCurrentImageMatrix);
    }

    /**
     * 将矩阵转为二维数组，返回对应 index 的值
     *
     * @param matrix     矩阵
     * @param valueIndex 索引
     * @return 对应 index 的值
     */
    protected float getMatrixValue(Matrix matrix, int valueIndex) {
        if (valueIndex >= MATRIX_VALUES_COUNT || valueIndex < 0) {
            throw new IllegalArgumentException("");
        }
        matrix.getValues(mMatrixValues);
        return mMatrixValues[valueIndex];
    }

    /**
     * 更新当前图片矩阵的值（四个顶点 + 一个中心点）
     */
    private void updateCurrentImagePoints() {
        mCurrentImageMatrix.mapPoints(mCurrentImageCorners, mInitialImageCorners);
        mCurrentImageMatrix.mapPoints(mCurrentImageCenter, mInitialImageCenter);
    }

    /**
     * 获取当前 ImageView 设置的图片
     *
     * @return 图片
     */
    public Bitmap getViewBitmap() {
        if (getDrawable() == null || !(getDrawable() instanceof BitmapDrawable)) {
            return null;
        } else {
            return ((BitmapDrawable) getDrawable()).getBitmap();
        }
    }

    public interface TransformImageListener {

        // 这个回调用于image加载并且layout完成后 可以用于恢复上一次的变形矩阵
        void onLoadCompleteAndLayout();

        void onLoadFailure(Exception e);
    }
}
