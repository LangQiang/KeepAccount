package com.godq.upload.chooseimage.crop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import java.lang.ref.WeakReference;
import java.util.Arrays;

/**
 * 作者：aprz on 2016/4/1.
 * 邮箱：aprz512@163.com
 */
public class CropImageView extends TransformImageView {

    public static final int DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION = 500;
    public static final float DEFAULT_MAX_SCALE_MULTIPLIER = 10.0f;
    public static final float SOURCE_IMAGE_ASPECT_RATIO = 0f;

    private final RectF mCropRect = new RectF();
    private final Matrix mTempMatrix = new Matrix();
    private RectF mPointCropRect;//设置剪切区域
    private boolean mUseOvalCrop = false;
    private float mTargetAspectRatio;
    private float mMaxScaleMultiplier = DEFAULT_MAX_SCALE_MULTIPLIER;

    private Runnable mWrapCropBoundsRunnable, mZoomImageToPositionRunnable = null;

    private float mMaxScale, mMinScale;
    private int mMaxResultImageSizeX = 700, mMaxResultImageSizeY = 700;//bitmap尺寸最大限制
    private int mPointResultImageSizeX = 0;//手动指定bitmap最终尺寸（会在根据剪切框剪切完后，在此以此尺寸缩放下Bitmap）
    private int mPointResultImageSizeY = 0;//手动指定bitmap最终尺寸（会在根据剪切框剪切完后，在此以此尺寸缩放下Bitmap）
    private long mImageToWrapCropBoundsAnimDuration = DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION;
    private boolean mInitFill = true;  //是否初始加载是填充满剪裁区域，默认是填充，不填充就是缩到里面
    private Paint bmpPaint;

    public CropImageView(Context context) {
        this(context, null);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        bmpPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bmpPaint.setFilterBitmap(true);
    }

    public void setInitScaleFill(boolean isInitFill) {
        mInitFill = isInitFill;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Bitmap bmp = getViewBitmap();
        if (bmp != null && !bmp.isRecycled()) {
            bmp.recycle();
        }
    }

    /**
     * 获取裁剪宽高比
     *
     * @return 宽高比
     */
    public float getTargetAspectRatio() {
        return mTargetAspectRatio;
    }

    /**
     * 设置裁剪宽高比，默认为图片宽高比
     *
     * @param targetAspectRatio 宽高比
     */
    public void setTargetAspectRatio(float targetAspectRatio) {
        final Drawable drawable = getDrawable();
        if (drawable == null) {
            mTargetAspectRatio = targetAspectRatio;
            return;
        }

        if (targetAspectRatio == SOURCE_IMAGE_ASPECT_RATIO) {
            mTargetAspectRatio = drawable.getIntrinsicWidth() / (float) drawable.getIntrinsicHeight();
        } else {
            mTargetAspectRatio = targetAspectRatio;
        }

        setupCropBounds();
        postInvalidate();
    }

    public void setCropRect(RectF rectf){
        mPointCropRect = rectf;
        setupCropBounds();
        postInvalidate();
    }

    /**
     * 是否以椭圆的形式来剪切图片
     *
     * @param useOval true以椭圆的形式剪切区域内的图片；false others
     * */
    public void setCropOvalBitmap(boolean useOval){
        mUseOvalCrop = useOval;
    }

    /**
     * 图片 onLayout 完成之后
     * 初始化一些值
     */
    @Override
    protected void onImageLaidOut() {
        super.onImageLaidOut();
        final Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        float drawableWidth = drawable.getIntrinsicWidth();
        float drawableHeight = drawable.getIntrinsicHeight();

        if (mTargetAspectRatio == SOURCE_IMAGE_ASPECT_RATIO) {
            mTargetAspectRatio = drawableWidth / drawableHeight;
        }

        setupCropBounds();
        // 这个方法重置了矩阵
        setupInitialImagePosition(drawableWidth, drawableHeight);
        setImageMatrix(mCurrentImageMatrix);

        if (mTransformImageListener != null) {
            mTransformImageListener.onLoadCompleteAndLayout();
        }
    }

    /**
     * 根据设定的宽高比（没有设定宽高比则使用图片的宽高比）来设置裁剪的矩形边界
     * 如果设定的宽高比大于控件的宽高比，则将左右两边留出距离裁剪
     * 如果设定的宽高比小于控件的宽高比，则将上下两边留出距离裁剪
     */
    private void setupCropBounds() {
        if (!isUsePointCropRect()) {
            int height = (int) (mThisWidth / mTargetAspectRatio);
            if (height > mThisHeight) {
                int width = (int) (mThisHeight * mTargetAspectRatio);
                int halfDiff = (mThisWidth - width) / 2;
                mCropRect.set(halfDiff, 0, width + halfDiff, mThisHeight);
            } else {
                int halfDiff = (mThisHeight - height) / 2;
                mCropRect.set(0, halfDiff, mThisWidth, height + halfDiff);
            }
        }else {
            mCropRect.set(getPaddingLeft() + mPointCropRect.left, getPaddingTop() + mPointCropRect.top,
                    mPointCropRect.right, mPointCropRect.bottom);
        }
    }

    //是否采用指定剪切框来裁剪，true使用指定剪切框，false不使用
    private boolean isUsePointCropRect() {
        return null != mPointCropRect && !mPointCropRect.isEmpty();
    }

    /**
     * 设置初始图片的位置：
     * 对图片进行缩放，直到宽或则高填满裁剪矩形
     * 最后与裁剪矩形居中对齐
     *
     * @param drawableWidth  图片的宽
     * @param drawableHeight 图片的高
     */
    private void setupInitialImagePosition(float drawableWidth, float drawableHeight) {
        float cropRectWidth = mCropRect.width();
        float cropRectHeight = mCropRect.height();

        float widthScale = cropRectWidth / drawableWidth;
        float heightScale = cropRectHeight / drawableHeight;
        mMinScale = Math.max(widthScale, heightScale);
        mMaxScale = mMinScale * mMaxScaleMultiplier;

        mCurrentImageMatrix.reset();
        float tw = (cropRectWidth - drawableWidth * mMinScale) / 2.0f + mCropRect.left;
        float th = (cropRectHeight - drawableHeight * mMinScale) / 2.0f + mCropRect.top;
        if (isUsePointCropRect()) {//居中填满View
            float wScale = mThisWidth / drawableWidth;
            float hScale = mThisHeight / drawableHeight;
            float minSale = Math.min(wScale, hScale);
            minSale = Math.min(mMaxScale, minSale);//如果超出了最大缩放限制，就赋值为最大缩放值
            if (minSale > mMinScale) {
                mCurrentImageMatrix.postScale(minSale, minSale);
                //计算平移
                float _tw = (mThisWidth - drawableWidth * minSale) / 2.0f;
                float _th = (mThisHeight - drawableHeight * minSale) / 2.0f;
                if (_th > mCropRect.top) {//如果超出了剪切框那么与剪切框对齐（左右方向的不用考虑，因为剪切框一般都是屏幕宽度上居中的）
                    _th = mCropRect.top;
                }
                mCurrentImageMatrix.postTranslate(_tw, _th);
            } else {//如果缩放后不能填满剪切框，那么采用填满剪切框的方式来初始化
                mCurrentImageMatrix.postScale(mMinScale, mMinScale);
                mCurrentImageMatrix.postTranslate(tw, th);
            }
        } else {//对图片进行缩放，直到宽或则高填满裁剪矩形
            mCurrentImageMatrix.postScale(mMinScale, mMinScale);
            mCurrentImageMatrix.postTranslate(tw, th);
        }

        if (mInitFill == false) {  //默认初始化还是要充满屏幕，这里只是让用户操作缩放时，可以缩小到fill模式，松开手还是要填充满剪裁口的
            mMinScale = Math.min(widthScale, heightScale);
            mMaxScale = mMinScale * mMaxScaleMultiplier;
        }
    }

    /**
     * 以裁剪矩形的中心点为坐标，将图片旋转 deltaAngle
     *
     * @param deltaAngle
     */
    public void postRotate(float deltaAngle) {
        postRotate(deltaAngle, mCropRect.centerX(), mCropRect.centerY());
    }

    /**
     * 在现有的基础上对图片进行缩放
     * 只有缩放值在边界范围之内才生效
     *
     * @param deltaScale 缩放大小
     * @param px         缩放中心 x
     * @param py         缩放中心 y
     */
    public void postScale(float deltaScale, float px, float py) {
        if (deltaScale > 1 && getCurrentScale() * deltaScale <= getMaxScale()) {
            super.postScale(deltaScale, px, py);
        } else if (deltaScale < 1 && getCurrentScale() * deltaScale >= getMinScale()) {
            super.postScale(deltaScale, px, py);
        }
    }

    /**
     * 获取最大缩放值
     *
     * @return 图片的缩放值
     */
    public float getMaxScale() {
        return mMaxScale;
    }

    /**
     * 获取最小缩放值
     *
     * @return 图片的缩放值
     */
    public float getMinScale() {
        return mMinScale;
    }

    /**
     * 设置图片缩放 最大值 和 最小值 之间的倍数
     *
     * @param maxScaleMultiplier 倍数
     */
    public void setMaxScaleMultiplier(float maxScaleMultiplier) {
        mMaxScaleMultiplier = maxScaleMultiplier;
    }

    /**
     * 设置图片裁剪后的最大宽度
     *
     * @param maxResultImageSizeX 最大宽度
     */
    public void setMaxResultImageSizeX(int maxResultImageSizeX) {
        if (maxResultImageSizeX <= 0) {
            throw new IllegalArgumentException("invalid maxResultImageSizeX");
        }
        mMaxResultImageSizeX = maxResultImageSizeX;
    }

    /**
     * 设置图片裁剪后的最大高度
     *
     * @param maxResultImageSizeY 最大高度
     */
    public void setMaxResultImageSizeY(int maxResultImageSizeY) {
        if (maxResultImageSizeY <= 0) {
            throw new IllegalArgumentException("invalid maxResultImageSizeY");
        }
        mMaxResultImageSizeY = maxResultImageSizeY;
    }

    /**
     * 设置最终输出图片的宽度
     *
     * @param resultImageSizeX 宽度
     */
    public void setResultImageSizeX(int resultImageSizeX) {
        if (resultImageSizeX <= 0) {
            throw new IllegalArgumentException("invalid resultImageSizeX");
        }
        mPointResultImageSizeX = resultImageSizeX;
    }

    /**
     * 设置最终输出图片的高度
     *
     * @param resultImageSizeY 高度
     */
    public void setResultImageSizeY(int resultImageSizeY) {
        if (resultImageSizeY <= 0) {
            throw new IllegalArgumentException("invalid maxResultImageSizeY");
        }
        mPointResultImageSizeY = resultImageSizeY;
    }

    /**
     * 裁剪图片
     * 要想实现连续裁剪 还需要先 setUri 再执行这个方法
     *
     * @return 返回裁剪之后的图片
     */
    public Bitmap cropImage() {
        Bitmap viewBitmap = getViewBitmap();
        if (viewBitmap == null || viewBitmap.isRecycled()) {
            return null;
        }

        // 创建一个复制品， 裁剪会创建一个新的 bitmap， 控件本身的bitmap 仍然存在
        // 使用 recycleDrawable 所以图片会自动释放
        Bitmap copy = Bitmap.createBitmap(viewBitmap.getWidth(), viewBitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(copy);
        canvas.drawBitmap(viewBitmap, 0, 0, bmpPaint);
        viewBitmap = copy;

//        LogMgr.e("aprz", "创建bitmap复制品...copy...");

        cancelAllAnimations();
        setImageToWrapCropBounds(false);

        RectF currentImageRect = RectUtils.trapToRect(mCurrentImageCorners);
        if (currentImageRect.isEmpty()) {
            return null;
        }

        float currentScale = getCurrentScale();
        float currentAngle = getCurrentAngle();

        // 生成缩放图片
        if (mMaxResultImageSizeX > 0 && mMaxResultImageSizeY > 0) {
            float cropWidth = mCropRect.width() / currentScale;
            float cropHeight = mCropRect.height() / currentScale;

            if (cropWidth > mMaxResultImageSizeX || cropHeight > mMaxResultImageSizeY) {

                float scaleX = mMaxResultImageSizeX / cropWidth;
                float scaleY = mMaxResultImageSizeY / cropHeight;
                float resizeScale = Math.min(scaleX, scaleY);

                Bitmap resizedBitmap = Bitmap.createScaledBitmap(viewBitmap,
                        (int) (viewBitmap.getWidth() * resizeScale),
                        (int) (viewBitmap.getHeight() * resizeScale), true);
//                LogMgr.e("aprz", "生成缩放图片。。。");
                if (viewBitmap != resizedBitmap) {
                    viewBitmap.recycle();
//                    LogMgr.e("aprz", "回收上一个bitmap");
                }
                viewBitmap = resizedBitmap;

                currentScale /= resizeScale;
            }
        }

        // 生成旋转图片
        if (currentAngle != 0) {
            mTempMatrix.reset();
            // 绕哪个点旋转并没有什么关系，只要生成旋转后的图片即可
            mTempMatrix.setRotate(currentAngle, viewBitmap.getWidth() / 2, viewBitmap.getHeight() / 2);

            Bitmap rotatedBitmap = Bitmap.createBitmap(viewBitmap, 0, 0, viewBitmap.getWidth(), viewBitmap.getHeight(),
                    mTempMatrix, true);
//            LogMgr.e("aprz", "创建旋转图片");
            if (viewBitmap != rotatedBitmap) {
                viewBitmap.recycle();
//                LogMgr.e("aprz", "回收上一个图片");
            }
            viewBitmap = rotatedBitmap;
        }

        // 裁剪最终的图片，不理解的可以画图
        int top = (int) ((mCropRect.top - currentImageRect.top) / currentScale);
        int left = (int) ((mCropRect.left - currentImageRect.left) / currentScale);
        int width = (int) (mCropRect.width() / currentScale);
        int height = (int) (mCropRect.height() / currentScale);

        // 修正缩放图片带来的 1 个像素的误差 而引起的崩溃
        if (top + height > viewBitmap.getHeight()) {
            height = viewBitmap.getHeight() - top;
        }

        if (left + width > viewBitmap.getWidth()) {
            width = viewBitmap.getWidth() - left;
        }

        Bitmap resultBitmap = Bitmap.createBitmap(viewBitmap, left, top, width, height);
//        LogMgr.e("aprz", "创建裁剪图片");
        if (viewBitmap != resultBitmap) {
            viewBitmap.recycle();
//            LogMgr.e("aprz", "回收上一个图片");
        }
        viewBitmap = resultBitmap;

        // 将最终的图片缩放为指定的大小
        Bitmap result = viewBitmap;
        if (mPointResultImageSizeX > 0 && mPointResultImageSizeY > 0) {
            result = Bitmap.createScaledBitmap(viewBitmap, mPointResultImageSizeX, mPointResultImageSizeY, true);
//        LogMgr.e("aprz", "创建制定大小图片");
            if (viewBitmap != result) {
                viewBitmap.recycle();
//            LogMgr.e("aprz", "回收上一个图片");
            }
        }

        //椭圆图
        if(mUseOvalCrop){
            viewBitmap = result;
            Bitmap finalBitmap = getCircleBitmap(viewBitmap);
            if (viewBitmap != finalBitmap) {
                viewBitmap.recycle();
            }
            return finalBitmap;
        }
        return result;
    }

    public Bitmap getCircleBitmap(Bitmap bmp) {
        //获取bmp的宽高 小的一个做为圆的直径r
        int w = bmp.getWidth();
        int h = bmp.getHeight();

        //创建一个paint
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        //新创建一个Bitmap对象newBitmap 宽高都是r
        Bitmap newBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        //创建一个使用newBitmap的Canvas对象
        Canvas canvas = new Canvas(newBitmap);

        //创建一个Path对象，path添加一个圆 圆心半径均是r / 2， Path.Direction.CW顺时针方向
        Path path = new Path();
        path.addOval(new RectF(0, 0, w, h),Path.Direction.CW);
        //canvas绘制裁剪区域
        canvas.clipPath(path);
        //canvas将图画到留下的圆形区域上
        canvas.drawBitmap(bmp, 0, 0, paint);

        return newBitmap;
    }

    /**
     * 取消移动和缩放动画
     */
    public void cancelAllAnimations() {
        removeCallbacks(mWrapCropBoundsRunnable);
        removeCallbacks(mZoomImageToPositionRunnable);
    }

    public void setImageToWrapCropBounds() {
        setImageToWrapCropBounds(true);
    }

    /**
     * 让图片填满裁剪矩形
     *
     * @param animate 填充过程是否有动画
     */
    public void setImageToWrapCropBounds(boolean animate) {
        if (!isImageWrapCropBounds()) {

            /**
             * 先判断裁剪矩形 和 图片矩形的中心重合时
             * 图片矩形范围是否包含裁剪矩形
             */
            float currentX = mCurrentImageCenter[0];
            float currentY = mCurrentImageCenter[1];
            float currentScale = getCurrentScale();

            float deltaX = mCropRect.centerX() - currentX;
            float deltaY = mCropRect.centerY() - currentY;
            float deltaScale = 0;

            mTempMatrix.reset();
            mTempMatrix.setTranslate(deltaX, deltaY);

            final float[] tempCurrentImageCorners = Arrays.copyOf(mCurrentImageCorners, mCurrentImageCorners.length);
            mTempMatrix.mapPoints(tempCurrentImageCorners);

            boolean willImageWrapCropBoundsAfterTranslate = isImageWrapCropBounds(tempCurrentImageCorners);

            if (willImageWrapCropBoundsAfterTranslate) {
                final float[] imageIndents = calculateImageIndents();
                deltaX = -(imageIndents[0] + imageIndents[2]);
                deltaY = -(imageIndents[1] + imageIndents[3]);
            } else {
                /**
                 * 中心点重合后，图片内容仍然无法包含裁剪矩形
                 * 只有放大图片了
                 * 如何计算放大比例？
                 * 和判断包含的思路是一样的
                 * 用计算出来的矩形 的宽和高 / 图片矩形 的宽高
                 */
                RectF tempCropRect = new RectF(mCropRect);
                mTempMatrix.reset();
                mTempMatrix.setRotate(getCurrentAngle());
                // 计算出一个正矩形，这个矩形的范围包含旋转的矩形
                mTempMatrix.mapRect(tempCropRect);

                final float[] currentImageSides = RectUtils.getRectSidesFromCorners(mCurrentImageCorners);

                deltaScale = Math.max(tempCropRect.width() / currentImageSides[0],
                        tempCropRect.height() / currentImageSides[1]);
                // 误差
                // Ugly but there are always couple pixels that want to hide because of all these calculations
                deltaScale *= 1.01;
                deltaScale = deltaScale * currentScale - currentScale;
            }

            if (animate) {
                post(mWrapCropBoundsRunnable = new WrapCropBoundsRunnable(
                        CropImageView.this, mImageToWrapCropBoundsAnimDuration, currentX, currentY, deltaX, deltaY,
                        currentScale, deltaScale, willImageWrapCropBoundsAfterTranslate));
            } else {
                postTranslate(deltaX, deltaY);
                if (!willImageWrapCropBoundsAfterTranslate) {
                    zoomInImage(currentScale + deltaScale, mCropRect.centerX(), mCropRect.centerY());
                }
            }
        }
    }

    /**
     * 图片是否填满了裁剪矩形
     *
     * @return true 填满
     */
    protected boolean isImageWrapCropBounds() {
        return isImageWrapCropBounds(mCurrentImageCorners);
    }

    /**
     * 正矩形的意思是：旋转角度为 0
     * 将包含的矩形旋转正，被包含的矩形旋转相同的角度
     * 计算出一个能容纳被包含矩形的最小正矩形
     * 看包含的矩形 是否 包含计算出来的矩形
     *
     * @param imageCorners 旋转图片的角度
     * @return true 包含
     */
    protected boolean isImageWrapCropBounds(float[] imageCorners) {
        mTempMatrix.reset();
        mTempMatrix.setRotate(-getCurrentAngle());

        float[] unrotatedImageCorners = Arrays.copyOf(imageCorners, imageCorners.length);
        mTempMatrix.mapPoints(unrotatedImageCorners);

        float[] unrotatedCropBoundsCorners = RectUtils.getCornersFromRect(mCropRect);
        mTempMatrix.mapPoints(unrotatedCropBoundsCorners);

        return RectUtils.trapToRect(unrotatedImageCorners).contains(RectUtils.trapToRect(unrotatedCropBoundsCorners));
    }

    /**
     * 计算出 x 和 y 方向的最小平移距离 能让图片矩形包含裁剪矩形
     *
     * @return left top right bottom 表示 图片矩形 - 裁剪矩形 的值
     */
    private float[] calculateImageIndents() {
        mTempMatrix.reset();
        mTempMatrix.setRotate(-getCurrentAngle());

        float[] unrotatedImageCorners = Arrays.copyOf(mCurrentImageCorners, mCurrentImageCorners.length);
        float[] unrotatedCropBoundsCorners = RectUtils.getCornersFromRect(mCropRect);

        mTempMatrix.mapPoints(unrotatedImageCorners);
        mTempMatrix.mapPoints(unrotatedCropBoundsCorners);

        RectF unrotatedImageRect = RectUtils.trapToRect(unrotatedImageCorners);
        RectF unrotatedCropRect = RectUtils.trapToRect(unrotatedCropBoundsCorners);

        float deltaLeft = unrotatedImageRect.left - unrotatedCropRect.left;
        float deltaTop = unrotatedImageRect.top - unrotatedCropRect.top;
        float deltaRight = unrotatedImageRect.right - unrotatedCropRect.right;
        float deltaBottom = unrotatedImageRect.bottom - unrotatedCropRect.bottom;

        float indents[] = new float[4];
        indents[0] = (deltaLeft > 0) ? deltaLeft : 0; //
        indents[1] = (deltaTop > 0) ? deltaTop : 0;
        indents[2] = (deltaRight < 0) ? deltaRight : 0;
        indents[3] = (deltaBottom < 0) ? deltaBottom : 0;

        mTempMatrix.reset();
        mTempMatrix.setRotate(getCurrentAngle());
        mTempMatrix.mapPoints(indents);

        return indents;
    }

    public void zoomInImage(float deltaScale) {
        zoomInImage(deltaScale, mCropRect.centerX(), mCropRect.centerY());
    }

    public void zoomInImage(float scale, float centerX, float centerY) {
        if (scale <= getMaxScale()) {
            postScale(scale / getCurrentScale(), centerX, centerY);
        }
    }

    public void zoomOutImage(float deltaScale) {
        zoomOutImage(deltaScale, mCropRect.centerX(), mCropRect.centerY());
    }

    public void zoomOutImage(float scale, float centerX, float centerY) {
        if (scale >= getMinScale()) {
            postScale(scale / getCurrentScale(), centerX, centerY);
        }
    }

    /**
     * 根据给定的中心点进行缩放
     *
     * @param scale      缩放大小
     * @param centerX    x
     * @param centerY    y
     * @param durationMs 动画执行时间
     */
    protected void zoomImageToPosition(float scale, float centerX, float centerY, long durationMs) {
        if (scale > getMaxScale()) {
            scale = getMaxScale();
        }

        final float oldScale = getCurrentScale();
        final float deltaScale = scale - oldScale;

        post(mZoomImageToPositionRunnable = new ZoomImageToPosition(CropImageView.this,
                durationMs, oldScale, deltaScale, centerX, centerY));
    }

    public interface CropBoundsChangeListener {

        void onCropBoundsChangedRotate(float cropRatio);

    }

    /**
     * 类动画效果 对图片进行移动和缩放
     */
    private static class WrapCropBoundsRunnable implements Runnable {

        private final WeakReference<CropImageView> mCropImageView;

        private final long mDurationMs, mStartTime;
        private final float mOldX, mOldY;
        private final float mCenterDiffX, mCenterDiffY;
        private final float mOldScale;
        private final float mDeltaScale;
        private final boolean mWillBeImageInBoundsAfterTranslate;

        public WrapCropBoundsRunnable(CropImageView cropImageView,
                                      long durationMs,
                                      float oldX, float oldY,
                                      float centerDiffX, float centerDiffY,
                                      float oldScale, float deltaScale,
                                      boolean willBeImageInBoundsAfterTranslate) {

            mCropImageView = new WeakReference<>(cropImageView);

            mDurationMs = durationMs;
            mStartTime = System.currentTimeMillis();
            mOldX = oldX;
            mOldY = oldY;
            mCenterDiffX = centerDiffX;
            mCenterDiffY = centerDiffY;
            mOldScale = oldScale;
            mDeltaScale = deltaScale;
            mWillBeImageInBoundsAfterTranslate = willBeImageInBoundsAfterTranslate;
        }

        @Override
        public void run() {
            CropImageView cropImageView = mCropImageView.get();
            if (cropImageView == null) {
                return;
            }

            long now = System.currentTimeMillis();
            float currentMs = Math.min(mDurationMs, now - mStartTime);

            float newX = CubicEasing.easeOut(currentMs, 0, mCenterDiffX, mDurationMs);
            float newY = CubicEasing.easeOut(currentMs, 0, mCenterDiffY, mDurationMs);
            float newScale = CubicEasing.easeInOut(currentMs, 0, mDeltaScale, mDurationMs);

            if (currentMs < mDurationMs) {
                cropImageView.postTranslate(newX - (cropImageView.mCurrentImageCenter[0] - mOldX), newY - (cropImageView.mCurrentImageCenter[1] - mOldY));
                if (!mWillBeImageInBoundsAfterTranslate) {
                    cropImageView.zoomInImage(mOldScale + newScale, cropImageView.mCropRect.centerX(), cropImageView.mCropRect.centerY());
                }
                if (!cropImageView.isImageWrapCropBounds()) {
                    cropImageView.post(this);
                }
            }
        }
    }

    /**
     * 中心点缩放动画
     */
    private static class ZoomImageToPosition implements Runnable {

        private final WeakReference<CropImageView> mCropImageView;

        private final long mDurationMs, mStartTime;
        private final float mOldScale;
        private final float mDeltaScale;
        private final float mDestX;
        private final float mDestY;

        public ZoomImageToPosition(CropImageView cropImageView,
                                   long durationMs,
                                   float oldScale, float deltaScale,
                                   float destX, float destY) {

            mCropImageView = new WeakReference<>(cropImageView);

            mStartTime = System.currentTimeMillis();
            mDurationMs = durationMs;
            mOldScale = oldScale;
            mDeltaScale = deltaScale;
            mDestX = destX;
            mDestY = destY;
        }

        @Override
        public void run() {
            CropImageView cropImageView = mCropImageView.get();
            if (cropImageView == null) {
                return;
            }

            long now = System.currentTimeMillis();
            float currentMs = Math.min(mDurationMs, now - mStartTime);
            float newScale = CubicEasing.easeInOut(currentMs, 0, mDeltaScale, mDurationMs);

            if (currentMs < mDurationMs) {
                cropImageView.zoomInImage(mOldScale + newScale, mDestX, mDestY);
                cropImageView.post(this);
            } else {
                cropImageView.setImageToWrapCropBounds();
            }
        }

    }
}
