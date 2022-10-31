package com.godq.upload.chooseimage;

import static com.godq.upload.chooseimage.KSingGalleryActivity.KEY_GIF;
import static com.godq.upload.chooseimage.KSingGalleryActivity.MAX_PHOTO_SIZE;
import static com.godq.upload.chooseimage.KSingPhotoSelectFragment.PARENT_PSRC;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lazylite.mod.utils.DeviceInfo;
import com.lazylite.mod.utils.KwDirs;
import com.lazylite.mod.utils.ScreenUtility;
import com.lazylite.mod.utils.toast.KwToast;
import com.godq.upload.R;
import com.godq.upload.chooseimage.crop.CropView;
import com.godq.upload.chooseimage.crop.Md5FileNameGenerator;
import com.godq.upload.chooseimage.crop.TransformImageView;
import com.godq.upload.chooseimage.widget.RecyclingImageViewWrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 作者：aprz on 2016/4/12.
 * 邮箱：aprz512@163.com
 */
public class KSingPhotoCropFragment extends BasePhotoFragment implements TransformImageView.TransformImageListener, View.OnClickListener {
    public static final String KEY_USE_OVAL = "key_use_oval";
    private static final String KEY_SCALE = "key_scale";
    private static final String KEY_MULTI = "key_multi";
    private static final String KEY_SINGLE = "key_single";
    private static final String KEY_CROP_RECT = "key_crop_rect";
    private final ArrayList<File> mCropFiles = new ArrayList<>();
    private CropView mCropView;
    private LinearLayout mPreviewGallery;
    private View mGifEditLayout;
    private TextView tvEditHint;
    private CheckedTextView cbEditCover;
    private ArrayList<PhotoInfo> mSelectList;
    private final View.OnClickListener mThumbnailItemAddClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mCropView != null) {
                saveAndPopFragment();
            }
        }
    };
    private int mCurPosition = 0;
    private boolean mClickable = false;
    private boolean isMultiSelected = false;//是否支持选择多张图片：true是；false不会，并且不会在底部显示多图菜单
    private boolean canScaleByFinger = true;//是否支持手指缩放图片：true支持；false不支持
    private boolean isSingle = false;
    private boolean isGifSelected = false;      //是否是支持gif选择模式
    private boolean useOval = false;                    //是否采用圆形剪切
    private RectF cropRect;//指定剪切区域
    private int mCoverIndex = mCurPosition;     //保存设置为封面的图片索引
    private final View.OnClickListener coverClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mCoverIndex == mCurPosition && mCurPosition > 0) {
                cbEditCover.setChecked(false);
                mCoverIndex = 0;
            } else {
                mCoverIndex = mCurPosition;
                cbEditCover.setChecked(true);
            }
        }
    };
    private final View.OnClickListener mThumbnailItemClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mCropView != null && isClickable()) {
                Integer cropTag = (Integer) mCropView.getTag();
                Integer viewTag = (Integer) v.getTag(R.id.tools_crop_preview);
                // 点击同一张图片 不重复加载
                if (cropTag.intValue() == viewTag.intValue()) {
                    return;
                }
                // 由于加载图片是异步过程，所以这里的代码有问题，get 返回的可能不是当前的图片的 matrix
                // 所以图片未加载出来之前 不响应点击
                enableClickAndTouch(false);
                saveImageMatrix(cropTag, mCropView.getCropImageView().getCurrentImageMatrix());
                Uri uri = Uri.parse("file://" + mSelectList.get(viewTag).getPath());
                mCropView.getCropImageView().setImageUri(uri);
                mCropView.setTag(viewTag);
                mCurPosition = viewTag;
                cbEditCover.setChecked(mCurPosition == mCoverIndex);
                tvEditHint.setText("滑动图片进行剪裁（" + (mCurPosition + 1) + "/" + MAX_PHOTO_SIZE + "）");
                // 点击之后 需要换背景
                changeImageBackground(viewTag);
            }
        }
    };

    public static KSingPhotoCropFragment newInstance(String psrc) {
        return newInstance(psrc, false, true, true, false, false, false, null);
    }

    /**
     * @param simple      是否仅仅作为照片选择器来使用，true是；false不是，选完照片后会将照片上传到服务器上
     * @param multiSelect 是否是多选：true是，false不是
     * @param isSingle    :true title栏左侧不显示“上一步”，false显示
     */
    public static KSingPhotoCropFragment newInstance(String psrc, boolean simple, boolean multiSelect, boolean canScaleByFinger, boolean canGifSelect, boolean isSingle, boolean useOval, RectF cropRect) {
        KSingPhotoCropFragment fragment = new KSingPhotoCropFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PARENT_PSRC, psrc);
        bundle.putBoolean(KEY_MULTI, multiSelect);
        bundle.putBoolean(KEY_SCALE, canScaleByFinger);
        bundle.putBoolean(KEY_SINGLE, isSingle);
        bundle.putBoolean(KEY_GIF, canGifSelect);
        bundle.putParcelable(KEY_CROP_RECT, cropRect);
        bundle.putBoolean(KEY_USE_OVAL, useOval);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (null != args) {
            isMultiSelected = args.getBoolean(KEY_MULTI, true);
            canScaleByFinger = args.getBoolean(KEY_SCALE, true);
            isSingle = args.getBoolean(KEY_SINGLE, false);
            isGifSelected = args.getBoolean(KEY_GIF, false);
            cropRect = args.getParcelable(KEY_CROP_RECT);
            useOval = args.getBoolean(KEY_USE_OVAL);
        }
    }

    @Override
    protected String getTitleName() {
        return "调整图片";
    }

    @Override
    protected View onCreateTitleView(LayoutInflater inflater, ViewGroup container) {
        KSingGalleryTitleBar titleBar = (KSingGalleryTitleBar) inflater.inflate(R.layout.tools_gallery_normal_titlebar, container, false);

        TextView cancel = titleBar.getCancelView();
        if (isSingle) {
            cancel.setVisibility(View.INVISIBLE);
        } else {
            cancel.setText("上一步");
            cancel.setOnClickListener(this);
        }

        TextView folderTitle = titleBar.getTitleView();
        folderTitle.setText(getTitleName());

        titleBar.getTitleArrowView().setVisibility(View.GONE);

        TextView chooseCount = titleBar.getCountView();
        if (isMultiSelected) {
            int count = 0;
            if (mSelectList != null) {
                count = mSelectList.size();
            }
            chooseCount.setText("" + count);
        } else {
            chooseCount.setVisibility(View.GONE);
            titleBar.hideCountBg();
        }

        TextView tvContinue = titleBar.getContinueView();
        tvContinue.setEnabled(true);
        tvContinue.setText("完成");
        tvContinue.setOnClickListener(this);

        return titleBar;
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container) {
        View root = inflater.inflate(R.layout.tools_gallery_crop_fragment, container, false);
        mPreviewGallery = (LinearLayout) root.findViewById(R.id.ll_preview_container);

        final View previewGalleryParent = root.findViewById(R.id.hs_preview);
        if (isMultiSelected) {
            previewGalleryParent.setVisibility(View.VISIBLE);
            addSelectPhoto(mPreviewGallery);
        } else {
            previewGalleryParent.setVisibility(View.GONE);
        }
        mGifEditLayout = root.findViewById(R.id.cv_titleedit_layout);
        tvEditHint = (TextView) root.findViewById(R.id.tv_edit_hint);
        tvEditHint.setText("滑动图片进行剪裁（" + (mCurPosition + 1) + "/" + MAX_PHOTO_SIZE + "）");
        cbEditCover = (CheckedTextView) root.findViewById(R.id.cb_edit_cover);
        cbEditCover.setOnClickListener(coverClickListener);
        if (isGifSelected) {
            mGifEditLayout.setVisibility(View.VISIBLE);
        }
        mCropView = (CropView) root.findViewById(R.id.cv_crop);
        mCropView.getCropImageView().setMaxResultImageSizeX(DeviceInfo.WIDTH);
        mCropView.getCropImageView().setMaxResultImageSizeY(DeviceInfo.HEIGHT);
        mCropView.setTag(0);
        mCropView.getCropImageView().setTargetAspectRatio(1);
        mCropView.getOverlayView().setTargetAspectRatio(1);
        mCropView.getCropImageView().setCropRect(cropRect);
        mCropView.getOverlayView().setCropRect(cropRect);
        mCropView.getOverlayView().userOvalDimmedLayer(useOval);
        mCropView.getCropImageView().setCropOvalBitmap(useOval);
        mCropView.getCropImageView().setTransformImageListener(this);
        mCropView.getCropImageView().setScaleEnabled(canScaleByFinger);
        mCropView.getCropImageView().setDoubleTapScaleEanbled(canScaleByFinger);
        if (!canScaleByFinger) {
            mCropView.getOverlayView().setTipText("拖动来选取");
        }
        enableClickAndTouch(false);
        mCropView.getCropImageView().setImageUri(Uri.parse("file://" + mSelectList.get(0).getPath()));

        return root;
    }

    /**
     * 将选择的照片添加到容器中
     *
     * @param previewGallery 预览容器
     */
    private void addSelectPhoto(LinearLayout previewGallery) {
        if (mSelectList == null) {
            return;
        }
        int size = mSelectList.size();
        for (int i = 0; i < size; i++) {
            RecyclingImageViewWrapper wrapper = new RecyclingImageViewWrapper(getActivity());

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ScreenUtility.convertDpToPixelInt(getActivity(), 60),
                    ScreenUtility.convertDpToPixelInt(getActivity(), 60));
            layoutParams.leftMargin = ScreenUtility.convertDpToPixelInt(getActivity(), 10);
            wrapper.setLayoutParams(layoutParams);

            wrapper.setTag(R.id.tools_crop_preview, i);
            wrapper.setOnClickListener(mThumbnailItemClickListener);

            if (i == 0) {
                wrapper.setShowFrame(true);
            } else if (i == size - 1) {
                wrapper.setShowFrame(false);
            }
            wrapper.setImageUri("file://" + mSelectList.get(i).getPath());

            previewGallery.addView(wrapper, layoutParams);
        }

        if (size < MAX_PHOTO_SIZE) {
            createAddButton(previewGallery);
        }
    }

    /**
     * 预览画廊的添加按钮
     *
     * @param previewGallery 预览容器
     */
    private void createAddButton(LinearLayout previewGallery) {
        ImageView riv = new ImageView(getActivity());
        riv.setScaleType(ImageView.ScaleType.FIT_XY);
        riv.setOnClickListener(mThumbnailItemAddClickListener);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ScreenUtility.convertDpToPixelInt(getActivity(), 60),
                ScreenUtility.convertDpToPixelInt(getActivity(), 60));
        layoutParams.leftMargin = ScreenUtility.convertDpToPixelInt(getActivity(), 10);
        riv.setLayoutParams(layoutParams);
        riv.setImageDrawable(getResources().getDrawable(R.drawable.tools_gallery_crop_select_photo_btn));

        previewGallery.addView(riv);
    }

    /**
     * 选择图片时的背景变换
     *
     * @param position 点击图片的位置
     */
    private void changeImageBackground(int position) {
        int imageChildCount = 0;
        if (mSelectList.size() < 9) {
            imageChildCount = mPreviewGallery.getChildCount() - 1;
        } else {
            imageChildCount = mPreviewGallery.getChildCount();
        }
        for (int i = 0; i < imageChildCount; i++) {
            RecyclingImageViewWrapper wrapper = (RecyclingImageViewWrapper) mPreviewGallery.getChildAt(i);
            wrapper.setShowFrame(i == position);
        }
    }

    /**
     * 保存图片矩阵
     *
     * @param position 图片位置
     * @param matrix   图片变形矩阵
     */
    private void saveImageMatrix(Integer position, Matrix matrix) {
        if (position >= 0 && position < mSelectList.size()) {
            Matrix temp = new Matrix(matrix);
            mSelectList.get(position).setMatrix(temp);
        }
    }

    /**
     * 设置是否可以响应点击事件 和 cropview 的触摸事件
     * 图片未加载完成时 触摸会出异常
     *
     * @param enable true 可以响应
     */
    private void enableClickAndTouch(boolean enable) {
        mClickable = enable;
        if (mCropView != null) {
            mCropView.getCropImageView().enableTouch(enable);
        }
    }

    /**
     * 是否响应点击事件
     *
     * @return true 响应 反之不响应
     */
    private boolean isClickable() {
        return mClickable;
    }

    @Override
    public void onLoadCompleteAndLayout() {
        if (!isFragmentAlive()) {
            return;
        }
        Integer position = (Integer) mCropView.getTag();
        if (mCropView != null && mCurPosition == position && mSelectList.get(position).getMatrix() != null) {
            mCropView.getCropImageView().restoreMatrix(mSelectList.get(position).getMatrix());
        }
        enableClickAndTouch(true);
    }

    @Override
    public void onLoadFailure(Exception e) {
        if (!isFragmentAlive()) {
            return;
        }
        enableClickAndTouch(true);
    }

    @Override
    public void onClick(View v) {
        if (!isFragmentAlive()) {
            return;
        }
        final int id = v.getId();
        if (id == R.id.tv_cancel) {
            saveAndPopFragment();
        } else if (id == R.id.tv_continue) {
            if (isGifSelected) {
                clickCropFinish();  //剪裁并返回图片列表，但不上传，不是单张
            } else {
                clickSimpleFinish();
            }
        }
    }

    /**
     * 完成裁剪，向外传递结果
     */
    private void clickSimpleFinish() {
        if (!isFragmentAlive()) {
            return;
        }
        saveImageMatrix((Integer) mCropView.getTag(), mCropView.getCropImageView().getCurrentImageMatrix());
        mCropFiles.clear();
        executeTask(0);
    }

    /**
     * 上传图片
     */
    private void clickCropFinish() {
        if (!isFragmentAlive()) {
            return;
        }
        showProcess("正在剪裁图片...");
        setProgressDialogCancelable(false);

        saveImageMatrix((Integer) mCropView.getTag(), mCropView.getCropImageView().getCurrentImageMatrix());

        mCropFiles.clear();
        executeTask(0);
    }

    /**
     * 保存图片矩阵 并 弹出当前 fragment
     */
    private void saveAndPopFragment() {
        if (!isFragmentAlive()) {
            return;
        }
        Activity activity = getActivity();
        if (activity instanceof KSingGalleryActivity) {
            saveImageMatrix((Integer) mCropView.getTag(), mCropView.getCropImageView().getCurrentImageMatrix());
            KSingGalleryActivity kActivity = (KSingGalleryActivity) activity;
            kActivity.popCropFragment();
        } else {
            activity.finish();
        }
    }

    public void setSelectInfos(ArrayList<PhotoInfo> list) {
        mSelectList = list;
    }

    private void fetchNextTask(Integer integer) {
        if (integer < mSelectList.size() - 1) {
            executeTask(++integer);
        } else {
            mCropView.getCropImageView().setTransformImageListener(this);
            if (isGifSelected) {
                cropPhotoAndResult();
            } else {
                Activity cxt;
                if ((cxt = getActivity()) != null) {
                    ArrayList<Uri> uris = PhotoTools.transforCorpPhotoToUri(mCropFiles);
                    PhotoTools.saveResultForActivity(cxt, uris, null);
                    cxt.finish();
                }
            }
        }
    }

    private void cropPhotoAndResult() {
        if (mCropFiles != null && mCropFiles.size() > 0) {
            ArrayList<Uri> uris = PhotoTools.transforCorpPhotoToUri(mCropFiles);
            Uri coverUri = uris.get(mCoverIndex);
            hideProcess();
            PhotoTools.saveResultForActivity(getActivity(), uris, coverUri);
            getActivity().finish();
        }
    }

    private void executeTask(int position) {
        mCropView.getCropImageView().setTransformImageListener(new BitmapTransformImageListener(position));
        Uri uri = Uri.parse("file://" + mSelectList.get(position).getPath());
        mCropView.getCropImageView().setImageUri(uri);
    }

    @Override
    public boolean isNeedSwipeBack() {
        return false;
    }

    //------------------------------ 图片裁剪 --------------------------------//
    class BitmapCropTask extends AsyncTask<Integer, Void, Integer> {

        private Bitmap cropBitmap;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cropBitmap = mCropView.getCropImageView().cropImage();
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            String photoPath = mSelectList.get(params[0]).getPath();
            String fileName = Md5FileNameGenerator.generate(photoPath);
            saveMyBitmap(fileName, cropBitmap);
            return params[0];
        }

        private void saveMyBitmap(String fileName, Bitmap bitmap) {
            if (bitmap == null || bitmap.isRecycled()) {
                return;
            }
            FileOutputStream fOut = null;
            try {
                File f = new File(KwDirs.getDir(KwDirs.TEMP), fileName + ".png");
                if (!f.exists()) {
                    f.createNewFile();
                }
                fOut = new FileOutputStream(f);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                mCropFiles.add(f);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeIoStream(fOut);
                bitmap.recycle();
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            fetchNextTask(integer);
        }
    }

    class BitmapTransformImageListener implements TransformImageView.TransformImageListener {
        private int position;

        public BitmapTransformImageListener(int position) {
            this.position = position;
        }

        @Override
        public void onLoadCompleteAndLayout() {
            Matrix selectMatrix = mSelectList.get(position).getMatrix();
            if (selectMatrix == null) {
                // 按默认的裁剪
                Matrix matrix = new Matrix(mCropView.getCropImageView().getCurrentImageMatrix());
                mSelectList.get(position).setMatrix(matrix);
            } else {
                mCropView.getCropImageView().restoreMatrix(mSelectList.get(position).getMatrix());
            }
            new BitmapCropTask().execute(position);
        }

        @Override
        public void onLoadFailure(Exception e) {
            hideProcess();
            mCropView.getCropImageView().setTransformImageListener(this);
            KwToast.show("图片裁剪失败");
        }
    }
}
