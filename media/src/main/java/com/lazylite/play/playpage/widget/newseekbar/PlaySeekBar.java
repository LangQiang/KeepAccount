package com.lazylite.play.playpage.widget.newseekbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.lazylite.media.R;
import com.lazylite.mod.utils.ColorUtils;
import com.lazylite.mod.utils.DeviceInfo;
import com.lazylite.mod.utils.ScreenUtility;

import java.util.ArrayList;

/**
 * @author qyh
 * @date 2022/1/7
 * describe:
 */
public class PlaySeekBar extends View {

    private int viewAllWidth;
    private int viewAllHigh;
    private Paint paint;
    private ArrayList<SpectrumData> myCData;
    private int[] highD;

    private OnSeekChangeListener listener;
    private int mCurrentT = -1;
    private int mCurrentCacheT = -1;
    private int roundAngle;
    private float gapMultiple;
    private int unSelectColor, selectColor, cacheColor;
    private int lastIndex;
    private float lineWidth;
    private boolean initCurrentTFlag;
    // 当前播放歌曲的进度，不是进度条的当前进度
    private int mCurrentProgress = -1;
    private int mCurrentCacheProgress = -1;
//    private Paint mMovePaint;
//    private boolean drawMoveBg;
//    private int bgColor;
//    private int moveLastColor;


    public PlaySeekBar(Context context) {
        this(context, null);
    }

    public PlaySeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlaySeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        @SuppressLint("CustomViewStyleable") TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Media_MusicSeekBar);
        roundAngle = array.getInt(R.styleable.Media_MusicSeekBar_roundAngle, 5);
        gapMultiple = array.getFloat(R.styleable.Media_MusicSeekBar_gapMultiple, 1);
        unSelectColor = array.getColor(R.styleable.Media_MusicSeekBar_unSelectColor, Color.GRAY);
        selectColor = array.getColor(R.styleable.Media_MusicSeekBar_selectColor, Color.WHITE);
        cacheColor = array.getColor(R.styleable.Media_MusicSeekBar_cacheColor, Color.BLUE);
        lineWidth = array.getFloat(R.styleable.Media_MusicSeekBar_lineWidth, ScreenUtility.dip2px(3));
        array.recycle();
        clearItems();
    }


    public void setData(long id) {
        highD = PlaySeekSpeed.getSpeed(id);
        clearItems();
        requestLayout();
    }

    public void setBgColor(int bgColor) {
//        Log.e("qyh", "setBgColor: ====" + bgColor);
//        if (mMovePaint == null) {
//            mMovePaint = new Paint();
//            mMovePaint.setStyle(Paint.Style.FILL);
//        }
//
//        int startColor = ColorUtils.transColorWithAlpha(bgColor, 0f);
//        int endColor = ColorUtils.transColorWithAlpha(bgColor, 0.3f);
//        moveLastColor = ColorUtils.playBarMoveColor(bgColor);
//        LinearGradient linearGradient = new LinearGradient(0, 0, DeviceInfo.WIDTH, 0,
//                new int[]{startColor, endColor},
//                null, Shader.TileMode.REPEAT);
//        mMovePaint.setShader(linearGradient);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float right;
        if (myCData.isEmpty()) {
            return;
        }
        if (paint == null) {
            paint = new Paint();
            paint.setAntiAlias(true);
        }
        if (lastIndex <= 0 && (mCurrentProgress > 0 || mCurrentCacheProgress > 0)) {
            initCurrentTFlag = true;
        }
        for (int i = 0; i < highD.length; i++) {
            right = myCData.get(i).getRight();
            if (right >= viewAllWidth) {
                break;
            }

            if (i <= mCurrentT && mCurrentT > 0) {
                paint.setColor(selectColor);
//                if (drawMoveBg && i == mCurrentT) {
//                    paint.setColor(moveLastColor);
//                }
            } else {
                if (i <= mCurrentCacheT && mCurrentCacheT > 0) {
                    paint.setColor(cacheColor);
                } else {
                    paint.setColor(unSelectColor);
                }
            }

            canvas.drawRoundRect(myCData.get(i).getLeft(), myCData.get(i).getTop(),
                    myCData.get(i).getRight(), myCData.get(i).getBottom(), roundAngle, roundAngle, paint);
            lastIndex = i;
        }

//        if (drawMoveBg) {
//            canvas.drawRect(myCData.get(0).getLeft(), 0,
//                    myCData.get(mCurrentT).getRight(), myCData.get(mCurrentT).getBottom(), mMovePaint);
//        }

        //  解决一秒的误差
        if (initCurrentTFlag) {
            setCurrent(mCurrentProgress);
            setCurrentCache(mCurrentCacheProgress);
            initCurrentTFlag = false;
        }
    }


    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                performClick();
                if (listener != null) {
                    listener.onStartTrackingTouch();
                    float refan = event.getX();
                    if (refan < 0) {
                        refan = 0;
                    } else if (refan > viewAllWidth) {
                        refan = viewAllWidth;
                    }
                    listener.onProgressChanged((int) (refan / viewAllWidth * 100), true);
                }
            case MotionEvent.ACTION_MOVE:
//                drawMoveBg = true;
                mCurrentT = (int) ((event.getX() / viewAllWidth) * lastIndex);
                if (mCurrentT > highD.length) {
                    mCurrentT = highD.length;
                }
                invalidate();
                if (listener != null) {
                    float refan = event.getX();
                    if (refan < 0) {
                        refan = 0;
                    } else if (refan > viewAllWidth) {
                        refan = viewAllWidth;
                    }
                    listener.onProgressChanged((int) (refan / viewAllWidth * 100), true);
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (listener != null) {
                    float refan = event.getX();
                    if (refan < 0) {
                        refan = 0;
                    } else if (refan > viewAllWidth) {
                        refan = viewAllWidth;
                    }
//                    drawMoveBg = false;
                    listener.onStopTrackingTouch((int) (refan / viewAllWidth * 100));
                }
        }
        return super.onTouchEvent(event);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewAllWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewAllHigh = MeasureSpec.getSize(heightMeasureSpec);
        setItems();
    }


    /**
     * 设置每条频谱线的属性
     */
    private void setItems() {
        float lineMinHigh = ((float) viewAllHigh) / getMaxIntArr();
        if (myCData.isEmpty()) {
            for (int i = 0; i < highD.length; i++) {
                float lineStartW = (float) i * (1 + gapMultiple) * lineWidth;
                float lineStartH;
                lineStartH = (float) viewAllHigh - highD[i] * lineMinHigh;
                myCData.add(new SpectrumData(lineStartW, lineStartW + lineWidth, lineStartH, lineStartH + highD[i] * lineMinHigh));
            }
        }
    }

    /**
     * 获取频谱进度条中最长一条
     *
     * @return 最长的长度倍数
     */
    private int getMaxIntArr() {
        int maxSin = 0;
        for (int item : highD) {
            if (item > maxSin) {
                maxSin = item;
            }
        }
        return maxSin;
    }

    /**
     * 清除进度条里的频谱数据
     */
    private void clearItems() {
        if (myCData == null) {
            myCData = new ArrayList<>();
        }
        myCData.clear();
        mCurrentProgress = -1;
        mCurrentCacheProgress = -1;
        lastIndex = 0;
    }


    /**
     * 设置当前进度
     *
     * @param current 进度
     */
    public void setCurrent(int current) {
        mCurrentProgress = current;
        int currentT = getBarProgress(current);
        if (currentT != mCurrentT) {
            invalidate();
            if (listener != null) {
                listener.onProgressChanged(current, false);
            }
            mCurrentT = currentT;
        }
    }

    public void setCurrentCache(int current) {
        mCurrentCacheProgress = current;
        int currentCacheT = getBarProgress(current);
        if (mCurrentCacheT != currentCacheT) {
            invalidate();
            mCurrentCacheT = currentCacheT;
        }
    }

    private int getBarProgress(int current) {
        return lastIndex * current / 100;
    }

    /**
     * 设置监听器,跟seekbar的滑动监听一样
     *
     * @param listener 监听器
     */
    public void setOnSeekBarChangeListener(OnSeekChangeListener listener) {
        this.listener = listener;
    }

    public interface OnSeekChangeListener {

        void onProgressChanged(int progress, boolean touch);

        void onStartTrackingTouch();

        void onStopTrackingTouch(int progress);

    }
}
