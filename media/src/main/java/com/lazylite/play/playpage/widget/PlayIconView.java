package com.lazylite.play.playpage.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.lazylite.mod.widget.textview.IconView;

/**
 * @author GodQ
 * @date 2024/7/9 21:22
 */
public class PlayIconView extends IconView {

    public PlayIconView(Context context) {
        this(context, null);
    }

    public PlayIconView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayIconView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(PlayFontUtils.getInstance().getIconFontType(context));
    }
}
