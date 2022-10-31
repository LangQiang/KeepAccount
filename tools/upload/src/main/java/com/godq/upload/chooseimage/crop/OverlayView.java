package com.godq.upload.chooseimage.crop;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.graphics.Region;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.lazylite.mod.utils.ScreenUtility;
import com.godq.upload.R;


/**
 * 作者：aprz on 2016/4/5.
 * 邮箱：aprz512@163.com
 */
public class OverlayView extends View {

    public static final boolean DEFAULT_OVAL_DIMMED_LAYER = false;
    public static final int DEFAULT_CROP_GRID_ROW_COUNT = 2;
    public static final int DEFAULT_CROP_GRID_COLUMN_COUNT = 2;

    private final RectF mCropViewRect = new RectF();
    protected int mThisWidth, mThisHeight;
    private RectF mPointCropRect;//设置剪切区域
    private int mCropGridRowCount, mCropGridColumnCount;
    private float mTargetAspectRatio;
    private float[] mGridPoints = null;
    private boolean mOvalDimmedLayer;
    private boolean mDrawGridPoints = true;//是否绘制分割线
    private int mDimmedColor;
    private Path mCircularPath = new Path();
    private Paint mDimmedStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCropGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCropFramePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTipTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private String mTipText;

    public OverlayView(Context context) {
        this(context, null);
    }

    public OverlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * http://developer.android.com/intl/zh-cn/guide/topics/graphics/hardware-accel.html
     * 绘制时使用的 clipPath() 等操作直到 API18 才支持硬件加速
     * 所以 11 - 18 应该关闭硬件加速
     */
    protected void init() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2 &&
//                Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
//        }
        mTipText = "拖动和缩放来选取";
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (changed) {
            left = getPaddingLeft();
            top = getPaddingTop();
            right = getWidth() - getPaddingRight();
            bottom = getHeight() - getPaddingBottom();
            mThisWidth = right - left;
            mThisHeight = bottom - top;
            setupCropBounds();
        }
    }

    public void setTargetAspectRatio(float targetAspectRatio) {
        mTargetAspectRatio = targetAspectRatio;
        setupCropBounds();
    }

    public void setTipText(String tipText){
        mTipText = tipText;
    }

    public void setCropRect(RectF rectf){
        mPointCropRect = rectf;
        setupCropBounds();
        postInvalidate();
    }

    public void userOvalDimmedLayer(boolean use){
        mOvalDimmedLayer = use;
        setupCropBounds();
        postInvalidate();
    }

    /**
     * 设置裁剪矩形的裁剪边界
     */
    public void setupCropBounds() {
        if(mOvalDimmedLayer){//如果采用圆形遮罩，那么就不要分割线了
            mDrawGridPoints = false;
        }
        if (null == mPointCropRect || mPointCropRect.isEmpty()) {
            int height = (int) (mThisWidth / mTargetAspectRatio);
            if (height > mThisHeight) {
                int width = (int) (mThisHeight * mTargetAspectRatio);
                int halfDiff = (mThisWidth - width) / 2;
                mCropViewRect.set(getPaddingLeft() + halfDiff, getPaddingTop(),
                        getPaddingLeft() + width + halfDiff, getPaddingTop() + mThisHeight);
            } else {
                int halfDiff = (mThisHeight - height) / 2;
                mCropViewRect.set(getPaddingLeft(), getPaddingTop() + halfDiff,
                        getPaddingLeft() + mThisWidth, getPaddingTop() + height + halfDiff);
            }
        } else {
            mCropViewRect.set(getPaddingLeft() + mPointCropRect.left, getPaddingTop() + mPointCropRect.top,
                    mPointCropRect.right, mPointCropRect.bottom);
        }
        mGridPoints = null;
        mCircularPath.reset();
        mCircularPath.addOval(mCropViewRect, Path.Direction.CW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDimmedLayer(canvas);
        drawCropGrid(canvas);
        drawTipText(canvas);
    }

    /**
     * 绘制提示文字
     *
     * @param canvas 画布
     */
    private void drawTipText(Canvas canvas) {
        if(TextUtils.isEmpty(mTipText)){
            return;
        }
        mTipTextPaint.setTextSize(ScreenUtility.sp2px(getContext(),14));
        mTipTextPaint.setTextAlign(Paint.Align.CENTER);
        mTipTextPaint.setColor(getResources().getColor(R.color.white50));
        Paint.FontMetrics metrics = mTipTextPaint.getFontMetrics();

        float bottomDimHeight = (mThisHeight - (mCropViewRect.bottom - mCropViewRect.top)) / 2;

        float tipTextBaseLine = mCropViewRect.bottom + bottomDimHeight / 2 + (Math.abs(metrics.ascent) - Math.abs(metrics.descent)) / 2;
        canvas.drawText(mTipText, mThisWidth / 2, tipTextBaseLine, mTipTextPaint);
    }

    /**
     * 绘制裁剪区域外的暗色区域
     *
     * @param canvas 画布
     */
    protected void drawDimmedLayer(Canvas canvas) {
        canvas.save();
        if (mOvalDimmedLayer) {
            canvas.clipPath(mCircularPath, Region.Op.DIFFERENCE);
        } else {
            canvas.clipRect(mCropViewRect, Region.Op.DIFFERENCE);
        }
        canvas.drawColor(mDimmedColor);
        canvas.restore();

        if (mOvalDimmedLayer) { // Draw 1px stroke to fix antialias
            canvas.drawOval(mCropViewRect, mDimmedStrokePaint);
        }
    }

    /**
     * 绘制辅助网格线 虚线
     *
     * @param canvas 画布
     */
    protected void drawCropGrid(Canvas canvas) {
        if(!mDrawGridPoints){
            return;
        }
        if (mGridPoints == null && !mCropViewRect.isEmpty()) {

            mGridPoints = new float[(mCropGridRowCount) * 4 + (mCropGridColumnCount) * 4];

            int index = 0;
            for (int i = 0; i < mCropGridRowCount; i++) {
                mGridPoints[index++] = mCropViewRect.left;
                mGridPoints[index++] = (mCropViewRect.height() * (((float) i + 1.0f) / (float) (mCropGridRowCount + 1))) + mCropViewRect.top;
                mGridPoints[index++] = mCropViewRect.right;
                mGridPoints[index++] = (mCropViewRect.height() * (((float) i + 1.0f) / (float) (mCropGridRowCount + 1))) + mCropViewRect.top;
            }

            for (int i = 0; i < mCropGridColumnCount; i++) {
                mGridPoints[index++] = (mCropViewRect.width() * (((float) i + 1.0f) / (float) (mCropGridColumnCount + 1))) + mCropViewRect.left;
                mGridPoints[index++] = mCropViewRect.top;
                mGridPoints[index++] = (mCropViewRect.width() * (((float) i + 1.0f) / (float) (mCropGridColumnCount + 1))) + mCropViewRect.left;
                mGridPoints[index++] = mCropViewRect.bottom;
            }
        }

        if (mGridPoints != null) {
            // 两个点（四个坐标值）画一条线
            PathEffect pathEffect = new DashPathEffect(new float[]{8, 4}, 4);

            mCropGridPaint.setPathEffect(pathEffect);
            Path path = new Path();
            for (int i = 0; i < mGridPoints.length; ) {
                path.moveTo(mGridPoints[i++], mGridPoints[i++]);
                path.lineTo(mGridPoints[i++], mGridPoints[i++]);
            }
            canvas.drawPath(path, mCropGridPaint);
        }

        canvas.drawRect(mCropViewRect, mCropFramePaint);
    }

    protected void processStyledAttributes(TypedArray a) {
        mOvalDimmedLayer = a.getBoolean(R.styleable.ucrop_UCropView_ucrop_oval_dimmed_layer, DEFAULT_OVAL_DIMMED_LAYER);
        mDimmedColor = a.getColor(R.styleable.ucrop_UCropView_ucrop_dimmed_color,
                getResources().getColor(R.color.black80));
        mDimmedStrokePaint.setColor(mDimmedColor);
        mDimmedStrokePaint.setStyle(Paint.Style.STROKE);
        mDimmedStrokePaint.setStrokeWidth(1);
        initCropFrameStyle(a);

        initCropGridStyle(a);
    }

    private void initCropFrameStyle(TypedArray a) {
        mCropFramePaint.setStrokeWidth(1);
        mCropFramePaint.setColor(getResources().getColor(R.color.white20));
        mCropFramePaint.setStyle(Paint.Style.STROKE);
    }

    private void initCropGridStyle(TypedArray a) {
        mCropGridPaint.setStyle(Paint.Style.STROKE);
        mCropGridPaint.setStrokeWidth(1);
        mCropGridPaint.setColor(getResources().getColor(R.color.white40));

        mCropGridRowCount = a.getInt(R.styleable.ucrop_UCropView_ucrop_grid_row_count, DEFAULT_CROP_GRID_ROW_COUNT);
        mCropGridColumnCount = a.getInt(R.styleable.ucrop_UCropView_ucrop_grid_column_count, DEFAULT_CROP_GRID_COLUMN_COUNT);
    }
}
