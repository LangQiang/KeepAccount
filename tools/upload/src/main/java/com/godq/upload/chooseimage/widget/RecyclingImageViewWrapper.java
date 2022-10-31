package com.godq.upload.chooseimage.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.lazylite.mod.utils.ScreenUtility;

/**
 * 作者：aprz on 2016/4/19.
 * 邮箱：aprz512@163.com
 * <p/>
 * 纯粹是为了搞一个 imageview 的边框
 */
public class RecyclingImageViewWrapper extends SimpleDraweeView {
    private final Rect mFrameBound = new Rect();
    private final Paint mPaint = new Paint();
    private boolean mShowFrame;
    private DraweeHolder mDraweeHolder;
    private GenericDraweeHierarchy mHierarchy;
    private int specifyColor = 0;
    private int defaultColor = 0;

    public RecyclingImageViewWrapper(Context context) {
        this(context, null);
    }

    public RecyclingImageViewWrapper(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclingImageViewWrapper(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        defaultColor = Color.parseColor("#1672FA");
        setScaleType(ScaleType.CENTER_CROP);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(ScreenUtility.dip2px(3));
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        if (mDraweeHolder == null) {
            mHierarchy = new GenericDraweeHierarchyBuilder(getResources()).build();
            mDraweeHolder = DraweeHolder.create(mHierarchy, getContext());
            mDraweeHolder.getTopLevelDrawable().setCallback(this);
        }
        setAspectRatio(1.0f);
    }

    public void setShowFrame(boolean showFrame) {
        mShowFrame = showFrame;
        invalidate();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDraweeHolder.onDetach();
        mDraweeHolder.getTopLevelDrawable().setCallback(null);
    }

    @Override
    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        mDraweeHolder.onDetach();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mDraweeHolder.onAttach();
    }

    @Override
    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        mDraweeHolder.onAttach();
    }

    public void setImageUri(String uri) {
        final ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(uri)).build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(mDraweeHolder.getController())
                .setImageRequest(imageRequest)
                .build();
        mDraweeHolder.setController(controller);
    }

    @Override
    protected boolean verifyDrawable(Drawable da) {
        if (da == mDraweeHolder.getTopLevelDrawable()) {
            return true;
        }
        return super.verifyDrawable(da);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mFrameBound.set(0, 0, w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Drawable drawable = mDraweeHolder.getHierarchy().getTopLevelDrawable();
        drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        drawable.draw(canvas);
        if (mShowFrame) {
            if (specifyColor != 0) {
                mPaint.setColor(specifyColor);
            } else {
                mPaint.setColor(defaultColor);
            }
            canvas.drawRect(mFrameBound, mPaint);
        }
    }

    public int getSpecifyColor() {
        return specifyColor;
    }

    public void setSpecifyColor(int specifyColor) {
        this.specifyColor = specifyColor;
    }
}
