package com.lazylite.play;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;

import androidx.palette.graphics.Palette;

import com.enrique.stackblur.NativeBlurProcess;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lazylite.media.R;
import com.lazylite.media.playctrl.PlayControllerImpl;
import com.godq.media_api.media.bean.BookBean;
import com.lazylite.mod.imageloader.fresco.ImageLoaderWapper;
import com.lazylite.mod.imageloader.fresco.listener.SimpleDownloaderListener;
import com.lazylite.mod.widget.BaseFragment;
import com.lazylite.play.playpage.widget.PaletteBlurBgView;

/**
 * @author qyh
 * @date 2022/1/10
 * describe:
 */
public class BasePlayFragment extends BaseFragment {
    private PaletteBlurBgView paletteBlurBgView;
    protected int curPaletteColor;

    protected void setBlurBackground(PaletteBlurBgView blurBgView, SimpleDraweeView ivCover) {
        BookBean currentBook = PlayControllerImpl.getInstance().getCurrentBook();
        if (currentBook == null) {
            return;
        }
        String imgUrl = currentBook.mImgUrl;
        paletteBlurBgView = blurBgView;

        if (TextUtils.isEmpty(imgUrl)) {
            ivCover.setImageResource(R.drawable.base_img_default);
        } else {

            ImageLoaderWapper.getInstance().load(imgUrl,
                    new SimpleDownloaderListener() {
                        @Override
                        public void onSuccess(Bitmap result) {
                            setBlurBackground(result);
                            setPalette(result);
                            ivCover.setImageBitmap(result);
                        }
                    });
        }
    }


    private void setBlurBackground(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap temp = Bitmap.createScaledBitmap(bitmap, w, h, false);
        Bitmap blurBitmap = NativeBlurProcess.blur(temp, 20);
        temp.recycle();
        paletteBlurBgView.setBlurBitmap(blurBitmap);
    }

    private void setPalette(Bitmap result) {
        Palette.from(result).generate((palette) -> {
            assert palette != null;
            Palette.Swatch swatch = palette.getMutedSwatch();
            if (swatch == null) {
                swatch = palette.getVibrantSwatch();
                if (swatch == null) {
                    for (Palette.Swatch s : palette.getSwatches()) {
                        if (s != null) {
                            swatch = s;
                            break;
                        }
                    }
                }
            }
            if (swatch != null) {
                int rgb = swatch.getRgb();
                curPaletteColor = changeNowPlayColor(rgb);
                paletteBlurBgView.setPaletteBg(curPaletteColor);
                onPaletteColor(curPaletteColor);
            }
        });
    }

    public static int changeNowPlayColor(int rgb) {
        float[] hsv = new float[3];
        try {
            Color.colorToHSV(rgb, hsv);
            if (hsv[2] > 0.9 && hsv[1] > 0 && hsv[1] <= 0.1) {
                return Color.parseColor("#ff333333");
            } else {
                return rgb;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return rgb;
    }

    public void onPaletteColor(int curPaletteColor) {

    }
}
