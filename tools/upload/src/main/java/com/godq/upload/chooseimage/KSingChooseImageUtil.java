package com.godq.upload.chooseimage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.godq.upload.R;
import com.lazylite.mod.App;
import com.lazylite.mod.permission.Permission;
import com.lazylite.mod.permission.SimpleCallback;
import com.lazylite.mod.utils.KwDirs;
import com.lazylite.mod.utils.KwFileUtils;
import com.lazylite.mod.utils.UIHelper;
import com.lazylite.mod.utils.toast.KwToast;

import java.io.File;
import java.util.List;

/**
 * Created by lzf on 2019/4/12 5:06 PM
 */
public class KSingChooseImageUtil {
    public static final int GALLERY_RESULT_CODE = 181;
    public static final int GALLERY_RESULT_CODE_NO_PHOTO = 182;
    private static final int MENU_TAKE_PHONE = 1;
    private static final int MENU_ALBUM = 2;
    private static final int REQUEST_CODE_TAKE_PHOTO = 1;
    private static final int REQUEST_CODE_GALLERY = 2;
    private static final int REQUEST_CODE_CROP_IMG = 3;

    private ChooseImageMenuDialog mMenuDialog = null;
    private Fragment mFragment;
    private Activity mActivity;

    private String mCameraFileName;
    private Callback mCallback;

    private OnPhotoItemClickListener mPhotoItemClickListener;


    private boolean isCropImageCanScale;//剪切图片时，是否支持双指缩放
    private boolean isOvalCrop;
    private RectF mCropRect;//指定剪切区域
    private final View.OnClickListener mChangeBgMenuClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mMenuDialog != null) {
                UIHelper.safeDismissDialog(mMenuDialog);
            }
            Integer pos = (Integer) v.getTag();
            if (pos == MENU_TAKE_PHONE) {
                takePhoto();
            } else if (pos == MENU_ALBUM) {
                openGallery();
            }
        }
    };

    public void supportCropScale(boolean scale) {
        isCropImageCanScale = scale;
    }

    public void setCropRect(RectF rect) {
        mCropRect = rect;
    }

    public void setUseOvalCrop(boolean useOvalCrop) {
        this.isOvalCrop = useOvalCrop;
    }

    public void setOnPhotoItemClickListener(OnPhotoItemClickListener clickListener) {
        mPhotoItemClickListener = clickListener;
    }

    private Context getContext() {
        if(null != mFragment){
            return mFragment.getContext();
        }
        return mActivity;
    }

    public void show(@NonNull Object fragment, Callback callback) {
        if(fragment instanceof Fragment){
            mFragment = (Fragment) fragment;
        }else if(fragment instanceof Activity){
            mActivity = (Activity) fragment;
        }
        mCallback = callback;
        if (mMenuDialog != null) {
            mMenuDialog.cancel();
        }
        mMenuDialog = new ChooseImageMenuDialog(getContext());
        mMenuDialog.show();
        //
        View menuView = mMenuDialog.getMenuView();
        if (null != menuView) {
            View chooseV = menuView.findViewById(R.id.tv_choose);
            chooseV.setTag(MENU_ALBUM);
            chooseV.setOnClickListener(mChangeBgMenuClick);
            View takePhone = menuView.findViewById(R.id.tv_token);
            takePhone.setTag(MENU_TAKE_PHONE);
            takePhone.setOnClickListener(mChangeBgMenuClick);
            menuView.findViewById(R.id.tv_cancel).setOnClickListener(v -> {
                if (mMenuDialog != null) {
                    mMenuDialog.cancel();
                }
            });
        }
    }

    public void destroy() {
        if (mMenuDialog != null) {
            mMenuDialog.cancel();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_TAKE_PHOTO) {//从相机拍照回来
            Uri uri;
            File file = null;
            if (data != null && data.getData() != null) {
                uri = data.getData();
            } else {
                final String fileName = mCameraFileName;
                file = new File(KwDirs.getDir(KwDirs.TEMP), fileName);
                uri = Uri.fromFile(file);
            }
            if (uri == null || file == null) {
                KwToast.show("相机没有提供图片哦，请换个相机试试");
                return;
            }
            onImageGet(uri.toString());
//            cropImage(REQUEST_CODE_CROP_IMG, file.getAbsolutePath());//开始截取图片
        } else if (resultCode == GALLERY_RESULT_CODE && requestCode == REQUEST_CODE_GALLERY) {//从相册选择回来
            if (null != data) {
                List<Uri> images = data.getParcelableArrayListExtra("data");
                if (null != images && images.size() > 0) {
                    Uri imageUri = images.get(0);
                    onImageGet(imageUri.toString());
                }
            } else {//没有选择任何图片
                //empty
            }
        } else if (resultCode == GALLERY_RESULT_CODE_NO_PHOTO && requestCode == REQUEST_CODE_GALLERY) {
            takePhoto();
        } else if (resultCode == GALLERY_RESULT_CODE && requestCode == REQUEST_CODE_CROP_IMG) {//截取图片回来
            if (null != data) {
                List<Uri> images = data.getParcelableArrayListExtra("data");
                if (null != images && images.size() > 0) {
                    Uri imageUri = images.get(0);
                    onImageGet(imageUri.toString());
                } else {//没有选择任何图片
                    //empty
                }
            }
        }
    }

    private void onImageGet(String imageUri) {
        if (null != mCallback) {
            mCallback.onImageGet(imageUri);
        }
    }

    //打开相机
    private void takePhoto() {
        if(null != mFragment){
            Permission.requestPermissions(mFragment, 1, new String[]{android.Manifest.permission.CAMERA}, new SimpleCallback() {
                @Override
                public void onSuccess(int requestCode) {
                    onCameraPermissionOk();
                }

                @Override
                public void onFail(int requestCode, String[] permissions, int[] grantResults) {
                    onCameraPermissionFail();
                }
            });
        }else {
            Permission.requestPermissions(mActivity, 1, new String[]{android.Manifest.permission.CAMERA}, new SimpleCallback() {
                @Override
                public void onSuccess(int requestCode) {
                    onCameraPermissionOk();
                }

                @Override
                public void onFail(int requestCode, String[] permissions, int[] grantResults) {
                    onCameraPermissionFail();
                }
            });
        }
    }

    private void onCameraPermissionOk(){
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mCameraFileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
        //Uri imageUri = Uri.fromFile(new File(KwDirs.getDir(KwDirs.TEMP), mCameraFileName));
        Uri imageUri = KwFileUtils.getUriForFile(App.getInstance(), new File(KwDirs.getDir(KwDirs.TEMP), mCameraFileName));
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        ResolveInfo reInfo = App.getInstance().getApplicationContext().getPackageManager().resolveActivity(openCameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (reInfo == null) {
            KwToast.show("请先安装相机");
            return;
        }
        if(null != mFragment){
            mFragment.startActivityForResult(openCameraIntent, REQUEST_CODE_TAKE_PHOTO);
        }else {
            mActivity.startActivityForResult(openCameraIntent, REQUEST_CODE_TAKE_PHOTO);
        }

    }

    private void onCameraPermissionFail(){
        KwToast.show("无法获取【相机】权限");
    }

    /**
     * 打开自定义相册
     * <p>
     * 权限检查可以忽略，Manifest.permission.READ_EXTERNAL_STORAGE
     * 没有这个权限，应用无法使用
     */
    private void openGallery() {
        KSingGalleryActivity.mPhotoItemClickListener = mPhotoItemClickListener;
        final Context context = getContext();
        Intent galleryIntent = new Intent(context, KSingGalleryActivity.class);
        galleryIntent.putExtra(KSingGalleryActivity.KEY_MULTI, false);
        galleryIntent.putExtra(KSingGalleryActivity.KEY_SIMPLE, true);
        galleryIntent.putExtra(KSingGalleryActivity.KEY_SCALE, isCropImageCanScale);
        galleryIntent.putExtra(KSingGalleryActivity.KEY_CROP_RECT, mCropRect);
        galleryIntent.putExtra(KSingGalleryActivity.KEY_USE_OVAL, isOvalCrop);
        try {
            if(null != mFragment){
                mFragment.startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY);
            }else {
                mActivity.startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY);
            }
        } catch (Exception e) { //ActivityNotFoundException
            KSingGalleryActivity.mPhotoItemClickListener = null;
        }
    }

    // 截取图片
    private void cropImage(int requestCode, String imagePath) {
        if(null != mFragment){
            CropImageActivity.openForResult(mFragment, requestCode, imagePath, isCropImageCanScale, isOvalCrop, mCropRect);
        } else {
            CropImageActivity.openForResult(mActivity, requestCode, imagePath, isCropImageCanScale, isOvalCrop, mCropRect);
        }
    }


    public interface Callback {
        void onImageGet(String imageUri);
    }

    public interface CallbackV2 extends Callback {
        boolean checkImage(@NonNull PhotoInfo photoInfo);
    }

}
