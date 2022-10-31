package com.godq.upload.chooseimage;

import android.Manifest;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.lazylite.mod.permission.Permission;
import com.lazylite.mod.permission.SimpleCallback;
import com.godq.upload.R;

import java.util.ArrayList;

/**
 * 作者：aprz on 2016/4/15.
 * 邮箱：aprz512@163.com
 */
public class KSingGalleryActivity extends KwFragmentActivity implements KSingPhotoSelectFragment.OnPhotoSelectFinishListener {
    public static final String KEY_SIMPLE = "key_simple";
    public static final String KEY_MULTI = "key_multi";
    public static final String KEY_SCALE = "key_scale";
    public static final String KEY_GIF = "key_gif";
    public static final String KEY_MAXCOUNT = "key_maxcount";
    public static final String KEY_CROP_RECT = "key_crop_rect";
    public static final String KEY_USE_OVAL = "key_use_oval";

    @Nullable
    public static OnPhotoItemClickListener mPhotoItemClickListener;

    public static int MAX_PHOTO_SIZE;
    private final ArrayList<PhotoInfo> mSelectPhotoList = new ArrayList<>();
    private KSingPhotoSelectFragment mSelectFragment;
    private KSingPhotoCropFragment mCropFragment;
    private boolean isSimple = false;//是否仅仅作为相册图片选择功能：true是：false不是，还会同时上传选中的图
    private boolean isMultiSelected = true;
    private boolean canScaleByFinger = true;
    private boolean canGifSelected = false;            //是否支持gif选择，与图片选择互斥。只能选一个gif或多个图片（只对音乐片段有意义）
    private RectF cropRect;//指定剪切区域
    private boolean useOval = false;                    //是否采用圆形剪切

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent galleyIntent = getIntent();
        /*MAX_PHOTO_SIZE = 9;
        MAX_PHOTO_SIZE = Math.min(100 - galleyIntent.getIntExtra("photoCount", 0), MAX_PHOTO_SIZE);*/
        isSimple = galleyIntent.getBooleanExtra(KEY_SIMPLE, false);
        isMultiSelected = galleyIntent.getBooleanExtra(KEY_MULTI, true);
        canScaleByFinger = galleyIntent.getBooleanExtra(KEY_SCALE, true);
        canGifSelected = galleyIntent.getBooleanExtra(KEY_GIF, false);
        MAX_PHOTO_SIZE = galleyIntent.getIntExtra(KEY_MAXCOUNT, 9);  //最多可选图片数据，现可通过intent控制
        cropRect = galleyIntent.getParcelableExtra(KEY_CROP_RECT);
        useOval = galleyIntent.getBooleanExtra(KEY_USE_OVAL, false);

        setContentView(R.layout.tools_gallery_activity);
        Permission.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, new SimpleCallback() {

            @Override
            public void onSuccess(int requestCode) {
                addSelectFragment();
            }

            @Override
            public void onFail(int requestCode, String[] permissions, int[] grantResults) {
                finish();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MAX_PHOTO_SIZE = 9;
        mPhotoItemClickListener = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permission.onRequestPermissionResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Permission.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void addSelectFragment() {
        if (mSelectFragment == null) {
            mSelectFragment = KSingPhotoSelectFragment.newInstance("", isSimple, isMultiSelected, canGifSelected);
            mSelectFragment.setSelectFinishListener(this);
            mSelectFragment.setItemClickListener(new KSingPhotoSelectFragment.OnPhotoItemClickListener() {
                @Override
                public boolean onItemClick(PhotoInfo photoInfo) {
                    if (null != mPhotoItemClickListener) {
                        return mPhotoItemClickListener.onItemClick(photoInfo);
                    }
                    return true;
                }
            });
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.fl_gallery_container, mSelectFragment, "select");
            ft.commitAllowingStateLoss();
        }
    }

    public void addCropFragment() {
        if (mCropFragment == null) {
            mCropFragment = KSingPhotoCropFragment.newInstance("", isSimple, isMultiSelected, canScaleByFinger, canGifSelected, false, useOval, cropRect);
            mCropFragment.setSelectInfos(mSelectPhotoList);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.fl_gallery_container, mCropFragment, "crop");
            ft.commitAllowingStateLoss();
        }
    }

    public void popCropFragment() {
        if (mCropFragment != null && mSelectFragment != null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(mCropFragment);
            ft.show(mSelectFragment);
            ft.commitAllowingStateLoss();
            mCropFragment = null;
        }
    }

    /**
     * 隐藏 curFragment 展示另外一个fragment
     *
     * @param curFragment 当前显示的fragment
     */
    public void showAnotherFragment(Fragment curFragment) {
        if (mCropFragment == null || mSelectFragment == null) {
            return;
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (curFragment instanceof KSingPhotoSelectFragment) {
            ft.hide(mSelectFragment);
            ft.show(mCropFragment);
        } else {
            ft.hide(mCropFragment);
            ft.show(mSelectFragment);
        }
        ft.commitAllowingStateLoss();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                clickBack();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void clickBack() {
        if (mCropFragment != null) {
            popCropFragment();
        } else {
            finish();
        }
    }

    @Override
    public void onSelectFinish(ArrayList<PhotoInfo> photoInfos, boolean simple) {
        mSelectPhotoList.clear();
        mSelectPhotoList.addAll(photoInfos);
        isSimple = simple;
    }
}
