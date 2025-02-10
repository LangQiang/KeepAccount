package com.lazylite.play.playpage.widget.seekbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lazylite.media.Media;
import com.lazylite.mod.utils.ScreenUtility;


public class PlaySeekBar extends KwSeekBar implements RangeSeekbarCallBack {

    private static final String TAG = "KwRangeSeekBar";
    private static final int MAX_PROGRESS = 1000;                             //
    private static final int COLOR_TAG_YELLO = Color.parseColor("#FFFFFF");
    private static final int COLOR_PROGRESS_DEFAULT = Color.parseColor("#1affffff");
    private static final int COLOR_SECOND_PROGRESS = Color.parseColor("#1affffff");
    private static final int TAG_WIDTH = ScreenUtility.dip2px(Media.getContext(),2);
    private static final int TAG_HEIGHT = ScreenUtility.dip2px(Media.getContext(), 5);
    private static final int PROGRESS_HEIGHT = ScreenUtility.dip2px(Media.getContext(), 4);
    private static final int THUMB_WIDTH = ScreenUtility.dip2px(Media.getContext(), 68);
    private static final int THUMB_HEIGHT = ScreenUtility.dip2px(Media.getContext(), 18);
    private static final String DEFAULT_TIME = "00:00/00:00";

    private Paint mPaint, mPaintTag, mPaintThumb, mPaintText;                    // 画笔
    private int mStartProgress = 0;                            // 开始进度
    private int mEndProgress = 0;                            // 结束进度
    private int available;
    //available 减去滑块
    private int mTrueWidth;
    private int mCenterY;
    private int mHalfHeight;
    private float mTagLeft, mTagRight;
    private String mTime = DEFAULT_TIME;

    private int mSecondProgress;
    private boolean isRangeSeekbar = false;
    private boolean reachedMaxOrMin = false;

    private boolean thumbVisible = true;

    public void setThumbVisible(boolean thumbVisible) {
        this.thumbVisible = thumbVisible;
    }

    public PlaySeekBar(Context context) {
        super(context);
        init();
    }

    public PlaySeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlaySeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(COLOR_TAG_YELLO);
        mPaint.setStrokeWidth(PROGRESS_HEIGHT);

        mPaintTag = new Paint();
        mPaintTag.setAntiAlias(true);
        mPaintTag.setColor(COLOR_TAG_YELLO);
        mPaintTag.setStrokeWidth(TAG_WIDTH);
        mPaintTag.setStrokeCap(Paint.Cap.ROUND);

        mPaintThumb = new Paint();
        mPaintThumb.setAntiAlias(true);
        mPaintThumb.setStyle(Paint.Style.FILL);
        mPaintThumb.setColor(COLOR_TAG_YELLO);

        mPaintText = new Paint();
        mPaintText.setTextSize(ScreenUtility.dip2px(Media.getContext(), 10));
        mPaintText.setColor(Color.parseColor("#c4000000"));
        mPaintText.setAntiAlias(true);
        mPaintText.setStyle(Paint.Style.FILL);
        mPaintText.setTypeface(Typeface.DEFAULT_BOLD);
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = View.MeasureSpec.getMode(heightMeasureSpec);
        if (View.MeasureSpec.AT_MOST == mode || View.MeasureSpec.UNSPECIFIED == mode){
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(THUMB_HEIGHT + PROGRESS_HEIGHT, View.MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //减去滑块的宽度，这样可以保证从头开始，到尾结束。
        available = w - getPaddingLeft() - getPaddingRight();
        mTrueWidth = available - THUMB_WIDTH;
        mCenterY = h / 2;
        mHalfHeight = TAG_HEIGHT / 2;

        mTagLeft = getX(mStartProgress);
        mTagRight = getX(mEndProgress);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (isRangeSeekbar) {
                    int newProgress = getProgress(event.getX());
                    seekTo(newProgress);
                    if (newProgress < mStartProgress || newProgress > mEndProgress) {
                        super.onTouchEvent(event);
                        invalidate();
                        return true;
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        int save = canvas.saveLayer(0,0,getWidth(),getHeight(), null,Canvas.ALL_SAVE_FLAG);
        // 绘制底部默认的半透明的背景
        mPaint.setColor(COLOR_PROGRESS_DEFAULT);
        canvas.drawLine(getPaddingLeft() + PROGRESS_HEIGHT / 2.f, mCenterY, getWidth() - getPaddingRight() - PROGRESS_HEIGHT / 2.f, mCenterY, mPaint);
        if(!thumbVisible){
            return;
        }
        if (isRangeSeekbar) {
            // 绘制二级进度条
            float startX = mTagLeft + PROGRESS_HEIGHT / 2.f;
            float stopX = getX(mSecondProgress) - PROGRESS_HEIGHT / 2.f;
            mPaint.setColor(COLOR_SECOND_PROGRESS);
            canvas.drawLine(startX, mCenterY, Math.max(startX, stopX), mCenterY, mPaint);
        } else {
            float startX = getPaddingLeft() + PROGRESS_HEIGHT / 2.f;
            float stopX = getX(mSecondProgress) - PROGRESS_HEIGHT / 2.f;
            mPaint.setColor(COLOR_SECOND_PROGRESS);
            canvas.drawLine(getPaddingLeft() + PROGRESS_HEIGHT / 2.f, mCenterY, Math.max(startX, stopX), mCenterY, mPaint);
        }
        // 绘制原生的seekbar
//        super.onDraw(canvas);

        if (isRangeSeekbar) {
            int curProgress = getProgress();
            float curX = getX(curProgress);
            // 实时绘制切换的进度，黄色线条
            mPaint.setColor(COLOR_TAG_YELLO);
            if (curProgress >= mStartProgress && curProgress <= mEndProgress) {
                canvas.drawLine(mTagLeft + PROGRESS_HEIGHT / 2.f, mCenterY, curX - PROGRESS_HEIGHT / 2.f, mCenterY, mPaint);
            } else if (curProgress > mEndProgress) {
                canvas.drawLine(mTagLeft + PROGRESS_HEIGHT / 2.f, mCenterY, mTagRight - PROGRESS_HEIGHT / 2.f, mCenterY, mPaint);
            }

            // 两条标记线
            mPaintTag.setColor(COLOR_TAG_YELLO);
            canvas.drawLine(mTagLeft, mCenterY - mHalfHeight, mTagLeft, mCenterY + mHalfHeight, mPaintTag);
            mPaintTag.setColor(COLOR_SECOND_PROGRESS);
            canvas.drawLine(mTagRight, mCenterY - mHalfHeight, mTagRight, mCenterY + mHalfHeight, mPaintTag);

            // 滑块
            drawThumb(canvas);
        } else {
            int curProgress = getProgress();
            float curX = getX(curProgress);
            // 实时绘制切换的进度，黄色线条
            mPaint.setColor(COLOR_TAG_YELLO);
            float stopX = curX - getPaddingLeft() < PROGRESS_HEIGHT ? getPaddingLeft() + PROGRESS_HEIGHT : curX - PROGRESS_HEIGHT / 2.f;
            canvas.drawLine(getPaddingLeft() + PROGRESS_HEIGHT / 2.f, mCenterY, stopX, mCenterY, mPaint);
            // 滑块
            drawThumb(canvas);
        }
        canvas.restoreToCount(save);
    }

    // ----------------------------------  对外接口 -----------------------

    /**
     * 设置时间，在进入页面的时候就要设置
     */
    public void setTime(int startTime, int endTime, int duration) {
        if (endTime > duration || duration <= 0) {
            return;
        }
        if (startTime > endTime) {
            startTime = endTime;
        }

        isRangeSeekbar = true;
        mStartProgress = (int) (startTime * 1.0f / duration * MAX_PROGRESS); // 需要做个转换
        mEndProgress = (int) (endTime * 1.0f / duration * MAX_PROGRESS);
        mSecondProgress = mStartProgress;
        setProgressBg(Color.TRANSPARENT);
        super.setProgress(mStartProgress);
        mTagLeft = getX(mStartProgress);
        mTagRight = getX(mEndProgress);
        invalidate();
    }

    public void resetSeekBar() {
        isRangeSeekbar = false;
        mStartProgress = 0;
        mEndProgress = 0;
        mSecondProgress = 0;
        mTagLeft = 0;
        mTagRight = 0;
        reachedMaxOrMin = false;
        setProgress(0);
        setSecondaryProgress(0);
        invalidate();
//        Drawable d = getResources().getDrawable(R.drawable.play_page_seekbar_style);
//        setProgressDrawable (d);
    }

    @Override
    public void setSecondaryProgress(int progress) {
        mSecondProgress = progress;
        super.setSecondaryProgress(progress);
    }


    @Override
    public void setThumb(Drawable thumb) {
//        super.setThumb(thumb);
//        mThumb = thumb;
//        mThumb.setColorFilter(COLOR_TAG_YELLO, PorterDuff.Mode.SRC_IN);
    }


    // ----------------------------------  控件内部使用的 -----------------------
    // 获取进度对应的横坐标
    private float getX(int progress) {
        int max = getMax();
        if (max <= 0) {
            max = MAX_PROGRESS;
        }
        float scale = progress * 1.0f / max;
        return scale * available + getPaddingLeft();
    }

    private float getThumbX(int progress) {
        int max = getMax();
        if (max <= 0) {
            max = MAX_PROGRESS;
        }
        float scale = progress * 1.0f / max;
        return scale * mTrueWidth + getPaddingLeft();
    }

    private int getProgress(float touchX) {
        float x = touchX - getPaddingLeft();
        if (x < 0) {
            x = 0;
        }
        return (int) (MAX_PROGRESS * 1.0f / available * x);
    }

    private void seekTo(int progress) {
        if (progress < mStartProgress) {
            super.setProgress(mStartProgress);
            reachedMaxOrMin = true;
        } else if (progress > mEndProgress) {
            super.setProgress(mEndProgress);
            reachedMaxOrMin = true;
        } else {
            super.setProgress(progress);
            reachedMaxOrMin = false;
        }
    }

    // 设置进度条背景，为了兼容带范围的和不带范围的两种进度条
    private void setProgressBg(int color) {
        final Drawable d = getProgressDrawable();
        if (d == null) {
            return;
        }
        d.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    private void drawThumb(Canvas canvas) {
        float left = getThumbX(getProgress());
        RectF rectF = new RectF(left, getPaddingTop(), left + THUMB_WIDTH, getPaddingTop() + THUMB_HEIGHT);
        canvas.drawRoundRect(rectF, THUMB_HEIGHT / 2, THUMB_HEIGHT / 2, mPaintThumb);

        Paint.FontMetrics fontMetrics = mPaintText.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;
        int baseLineY = (int) (rectF.centerY() - top / 2 - bottom / 2);
        canvas.drawText(mTime, rectF.left + THUMB_WIDTH / 2, baseLineY, mPaintText);
    }

    public void refreshTime(String time){
        mTime = time;
        invalidate();
    }

    @Override
    public int getCurProgress() {
        return getProgress();
    }

    @Override
    public boolean touchMaxOrMin() {
        return reachedMaxOrMin;
    }
}
