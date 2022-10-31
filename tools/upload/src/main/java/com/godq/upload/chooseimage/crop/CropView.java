package com.godq.upload.chooseimage.crop;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.godq.upload.R;


/**
 * 作者：aprz on 2016/4/5.
 * 邮箱：aprz512@163.com
 */
public class CropView extends FrameLayout {

    private GestureCropImageView mGestureCropImageView;
    private OverlayView mViewOverlay;

    public CropView(Context context) {
        this(context, null);
    }

    public CropView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.tools_gallery_cropview_layout, this, true);
        mGestureCropImageView = (GestureCropImageView) findViewById(R.id.gesture_cropview);
        mViewOverlay = (OverlayView) findViewById(R.id.overlay);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ucrop_UCropView);
        mViewOverlay.processStyledAttributes(a);
        a.recycle();
    }

    public void setOverlayTip(String tip){
        mViewOverlay.setTipText(tip);
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public GestureCropImageView getCropImageView() {
        return mGestureCropImageView;
    }

    public OverlayView getOverlayView() {
        return mViewOverlay;
    }

}
