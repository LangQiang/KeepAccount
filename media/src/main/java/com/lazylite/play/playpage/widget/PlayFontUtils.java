package com.lazylite.play.playpage.widget;

import android.content.Context;
import android.graphics.Typeface;

import com.lazylite.mod.App;
import com.lazylite.mod.utils.FontUtils;

/**
 * @author GodQ
 * @date 2024/7/9 21:24
 */
public class PlayFontUtils {

    public static PlayFontUtils fontsUtil;

    private Context mContext;
    private Typeface iconFontTypeface;


    private PlayFontUtils(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static PlayFontUtils getInstance() {
        if (fontsUtil == null) {
            fontsUtil = new PlayFontUtils(App.getInstance());
        }
        return fontsUtil;
    }

    public Typeface getIconFontType(Context context) {
        if (iconFontTypeface == null) {
            try {
                iconFontTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/playiconfont.ttf");
            } catch (Exception e) {

            }
        }
        return iconFontTypeface;
    }
}
