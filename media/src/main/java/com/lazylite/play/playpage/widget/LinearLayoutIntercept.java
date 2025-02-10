package com.lazylite.play.playpage.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;


public class LinearLayoutIntercept extends LinearLayout {

    private boolean intercept;

    public LinearLayoutIntercept(Context context) {
        super(context);
    }

    public LinearLayoutIntercept(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearLayoutIntercept(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setIntercept(boolean intercept) {
        this.intercept = intercept;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(intercept){
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(intercept){
            return true;
        }
        return super.onTouchEvent(event);
    }
}
