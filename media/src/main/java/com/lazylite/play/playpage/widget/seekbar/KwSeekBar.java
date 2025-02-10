package com.lazylite.play.playpage.widget.seekbar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatSeekBar;

public class KwSeekBar extends AppCompatSeekBar {

	Drawable mThumb;

	public KwSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public KwSeekBar(Context context) {
		super(context);
	}

	public KwSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	@Override
	public void setThumb(Drawable thumb) {
		super.setThumb(thumb);
		mThumb = thumb;
	}
	public Drawable getSeekBarThumb() {
		return mThumb;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			this.requestFocus();
			getParent().requestDisallowInterceptTouchEvent(true);
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			getParent().requestDisallowInterceptTouchEvent(false);
			break;
		}
		super.dispatchTouchEvent(ev);
		return true;
	}

}
