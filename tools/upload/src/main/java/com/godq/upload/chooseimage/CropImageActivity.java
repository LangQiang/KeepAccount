package com.godq.upload.chooseimage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.godq.upload.R;

import java.util.ArrayList;


/**
 * 对外返回的结果：<br/>
 * 1.resultCode = {@link KSingChooseImageUtil#GALLERY_RESULT_CODE}<br/>
 * 2.{@code List<Uri> images = data.getParcelableArrayListExtra("data")};
 * <p/>
 * Created by lizhaofei on 2017/10/16 14:50
 */
public class CropImageActivity extends KwFragmentActivity {
    public static final String KEY_IMAGE_PATH = "key_image_path";
    public static final String KEY_SCALE = "key_scale";
    public static final String KEY_CROP_RECT = "key_crop_rect";
    public static final String KEY_USE_OVAL = "key_use_oval";

    //cropRect 可以为null
    public static void openForResult(Fragment fragment, int requestCode, String imagePath, boolean canScale,boolean useOval, RectF cropRect){
        internalOpenForResult(fragment, requestCode, imagePath, canScale, useOval, cropRect);
    }

    public static void openForResult(Activity fragment, int requestCode, String imagePath, boolean canScale,boolean useOval, RectF cropRect){
        internalOpenForResult(fragment, requestCode, imagePath, canScale, useOval, cropRect);
    }

    //cropRect 可以为null
    private static void internalOpenForResult(Object fragment, int requestCode, String imagePath, boolean canScale,boolean useOval, RectF cropRect){
        if(null == fragment){
            return;
        }
        final Context context;
        if(fragment instanceof Fragment){
            context = ((Fragment) fragment).getContext();
        }else {
            context = (Activity)fragment;
        }
        if(null == context){
            return;
        }
        Intent intent = new Intent();
        intent.setClass(context,CropImageActivity.class);
        intent.putExtra(KEY_SCALE,canScale);
        intent.putExtra(KEY_IMAGE_PATH,imagePath);
        intent.putExtra(KEY_CROP_RECT, cropRect);
        intent.putExtra(KEY_USE_OVAL, useOval);
        if(fragment instanceof Fragment){
            ((Fragment)fragment).startActivityForResult(intent,requestCode);
        }else {
            ((Activity)fragment).startActivityForResult(intent,requestCode);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tools_activity_gallery_cropimage);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag("crop") == null) {
            String path = "";
            boolean canScale = true;
            boolean useOval = false;
            RectF cropRect = null;
            ArrayList<PhotoInfo> selectPhotoList = new ArrayList<>();
            Intent intent = getIntent();
            if (null != intent) {
                path = intent.getStringExtra(KEY_IMAGE_PATH);
                canScale = intent.getBooleanExtra(KEY_SCALE, true);
                cropRect = intent.getParcelableExtra(KEY_CROP_RECT);
                useOval = intent.getBooleanExtra(KEY_USE_OVAL, false);
            }
            if(TextUtils.isEmpty(path)){
                finish();
            }
            PhotoInfo photoInfo = new PhotoInfo();
            photoInfo.setPhotoId(1);
            photoInfo.setPath(path);
            selectPhotoList.add(photoInfo);

            KSingPhotoCropFragment fragment = KSingPhotoCropFragment.newInstance("", true, false, canScale,false,true,useOval,cropRect);
            fragment.setSelectInfos(selectPhotoList);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.fl_container, fragment, "crop");
            ft.commitAllowingStateLoss();
        }
    }
}
