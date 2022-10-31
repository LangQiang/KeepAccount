package com.godq.upload.chooseimage;

import static com.godq.upload.chooseimage.KSingGalleryActivity.KEY_GIF;
import static com.godq.upload.chooseimage.KSingGalleryActivity.MAX_PHOTO_SIZE;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.godq.upload.chooseimage.widget.StateView;
import com.lazylite.mod.utils.toast.KwToast;
import com.godq.upload.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * 作者：aprz on 2016/4/11.
 * 邮箱：aprz512@163.com
 */
public class KSingPhotoSelectFragment extends BasePhotoFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    public static final String PARENT_PSRC = "parentPsrc";
    private static final String KEY_SIMPLE = "key_simple";
    private static final String KEY_MULTI = "key_multi";
    private final int HANDLER_REFRESH_LIST_EVENT = 1002;
    private final ArrayList<PhotoInfo> mSelectPhotoList = new ArrayList<>();
    private final Handler mHandler = new WeakHandler(this);
    private TextView mFolderTitle, mChooseCount, mContinue;
    private ImageView mFolderArrow;
    private GridView mPhotoList;
    private ListView mFolderList;
    private LinearLayout mFolderPanel;
    private FrameLayout mPhotoPanel;
    private StateView mTipView;
    private KSingGalleryTitleBar mTitleBar;
    private ArrayList<PhotoFolderInfo> mAllPhotoFolderList;
    private ArrayList<PhotoInfo> mCurPhotoList;
    private FolderListAdapter mFolderListAdapter;
    private PhotoListAdapter mPhotoListAdapter;
    private OnPhotoSelectFinishListener mSelectFinishListener;
    private OnPhotoItemClickListener mItemClickListener;
    private boolean isSimple = false;
    private boolean isMultiSelected = false;
    private boolean isGifSelected = false;          //是否支持gif选择处理逻辑
    private boolean mHasSelectGif = false;         //当前是否已选择gif

    public static KSingPhotoSelectFragment newInstance(String psrc) {
        return newInstance(psrc, false, true, false);
    }

    /**
     * @param simple      是否仅仅作为照片选择器来使用，true是；false不是，选完照片后会将照片上传到服务器上
     * @param multiSelect 是否是多选：true是，false不是
     */
    public static KSingPhotoSelectFragment newInstance(String psrc, boolean simple, boolean multiSelect, boolean canGifSelect) {
        KSingPhotoSelectFragment fragment = new KSingPhotoSelectFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PARENT_PSRC, psrc);
        bundle.putBoolean(KEY_SIMPLE, simple);
        bundle.putBoolean(KEY_MULTI, multiSelect);
        bundle.putBoolean(KEY_GIF, canGifSelect);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setSelectFinishListener(OnPhotoSelectFinishListener selectFinishListener) {
        mSelectFinishListener = selectFinishListener;
    }

    public void setItemClickListener(OnPhotoItemClickListener listener) {
        mItemClickListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (null != args) {
            isSimple = args.getBoolean(KEY_SIMPLE, false);
            isMultiSelected = args.getBoolean(KEY_MULTI, true);
            isGifSelected = args.getBoolean(KEY_GIF, false);
        }
    }

    private void notifyDataSetChanged() {
        if (mPhotoListAdapter != null) {
            mPhotoListAdapter.notifyDataSetChanged();
        }
        if (mFolderListAdapter != null) {
            mFolderListAdapter.notifyDataSetChanged();
        }
    }

    private void setEmptyView() {
        if (mTipView != null) {
            mTipView.showState(-1, getResources().getString(R.string.tools_no_photo), getResources().getString(R.string.tools_take_one_photo), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().setResult(KSingChooseImageUtil.GALLERY_RESULT_CODE_NO_PHOTO);
                    getActivity().finish();
                }
            });
        }
    }

    @Override
    protected String getTitleName() {
        return "所有照片";
    }

    @Override
    protected View onCreateTitleView(LayoutInflater inflater, ViewGroup container) {
        mTitleBar = (KSingGalleryTitleBar) inflater.inflate(R.layout.tools_gallery_normal_titlebar, container, false);
        TextView cancel = mTitleBar.getCancelView();
        cancel.setOnClickListener(this);
        mFolderTitle = mTitleBar.getTitleView();
        mFolderTitle.setText(getTitleName());
        mFolderArrow = mTitleBar.getTitleArrowView();
        mChooseCount = mTitleBar.getCountView();
        mContinue = mTitleBar.getContinueView();
        mContinue.setOnClickListener(this);
        mContinue.setEnabled(false);
        configForSimple();
        return mTitleBar;
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container) {
        View root = inflater.inflate(R.layout.tools_photo_select, container, false);

        initViews(root);
        setListeners();
        setAdapter();
        refreshSelectCount();
        getPhotos();
        return root;
    }

    private void configForSimple() {
        if (!isMultiSelected) {
            mChooseCount.setVisibility(View.INVISIBLE);
            mContinue.setVisibility(View.INVISIBLE);
            mTitleBar.hideCountBg();
        }
    }

    private void initViews(View root) {
        mTipView = root.findViewById(R.id.kw_tip_view);
        mPhotoList = (GridView) root.findViewById(R.id.gv_photo_list);
        mFolderList = (ListView) root.findViewById(R.id.lv_folder_list);
        mFolderPanel = (LinearLayout) root.findViewById(R.id.ll_folder_panel);
        mPhotoPanel = (FrameLayout) root.findViewById(R.id.fl_photo_container);
    }

    private void setListeners() {
        mFolderArrow.setOnClickListener(this);
        mFolderTitle.setOnClickListener(this);
        mFolderList.setOnItemClickListener(this);
        mPhotoList.setOnItemClickListener(this);
    }

    private void setAdapter() {
        mAllPhotoFolderList = new ArrayList<>();
        mFolderListAdapter = new FolderListAdapter(getActivity(), mAllPhotoFolderList);
        mFolderList.setAdapter(mFolderListAdapter);

        mCurPhotoList = new ArrayList<>();
        mPhotoListAdapter = new PhotoListAdapter(getActivity(), mCurPhotoList, mSelectPhotoList, null);
        mPhotoListAdapter.setMultiSelected(isMultiSelected);
        mPhotoList.setAdapter(mPhotoListAdapter);
    }

    /**
     * 刷新已选择图片数量
     */
    private void refreshSelectCount() {
        if (!isMultiSelected) {
            return;
        }
        if (mChooseCount != null) {
            mChooseCount.setText(mSelectPhotoList.size() + "");
        }
        if (mContinue != null) {
            if (mSelectPhotoList.size() > 0) {
                mContinue.setEnabled(true);
            } else {
                mContinue.setEnabled(false);
            }
        }
    }

    /**
     * 获取所有图片信息列表
     */
    private void getPhotos() {
        showProcess("请稍候...");
        setWidgetClickEnable(false);
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mAllPhotoFolderList.clear();
                List<PhotoFolderInfo> allFolderList = PhotoTools.getAllPhotoFolder(getActivity());
                mAllPhotoFolderList.addAll(allFolderList);

                mCurPhotoList.clear();
                if (allFolderList.size() > 0) {
                    if (allFolderList.get(0).getPhotoList() != null) {
                        mCurPhotoList.addAll(allFolderList.get(0).getPhotoList());
                    }
                }

                refreshAdapter();
            }
        });
    }

    /**
     * 控制按钮 列表 是否可以点击
     *
     * @param enable true 可点击 反之不可
     */
    private void setWidgetClickEnable(boolean enable) {
        if (mPhotoList != null) {
            mPhotoList.setEnabled(enable);
        }
        if (mFolderArrow != null) {
            mFolderArrow.setEnabled(enable);
        }
    }

    /**
     * 刷新适配器
     */
    private void refreshAdapter() {
        mHandler.sendEmptyMessageDelayed(HANDLER_REFRESH_LIST_EVENT, 100);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.iv_folder_arrow || id == R.id.tv_folder_title) {
            clickFolderArrow();
            mTitleBar.rotateArrow();
        } else if (id == R.id.tv_cancel) {
            getActivity().finish();
        } else if (id == R.id.tv_continue) {
            clickContinue();
        }
    }

    /**
     * 点击继续，进入图片调整界面
     * 未选择图片时，完成按钮置灰
     */
    private void clickContinue() {
        if (isGifSelected && mHasSelectGif) {
            ArrayList<Uri> resultUris = PhotoTools.transforLocalPathToUri(mSelectPhotoList);
            if (resultUris != null) {
                PhotoTools.saveResultForActivity(getActivity(), resultUris, resultUris.get(0));
                getActivity().finish();
            } else {
                KwToast.show("没有选择图片，不能继续");
            }
        } else {
            if (mSelectFinishListener != null) {
                mSelectFinishListener.onSelectFinish(mSelectPhotoList, isSimple);
            }
//            KSingGalleryActivity activity = (KSingGalleryActivity) getActivity();
//            activity.addCropFragment();
//            activity.showAnotherFragment(this);
            ArrayList<Uri> resultUris = PhotoTools.transforLocalPathToUri(mSelectPhotoList);
            PhotoTools.saveResultForActivity(getActivity(), resultUris, resultUris.get(0));
            getActivity().finish();
        }
    }

    /**
     * 点击文件列表箭头
     */
    private void clickFolderArrow() {
        if (mFolderPanel != null) {
            if (mFolderPanel.getVisibility() == View.VISIBLE) {
                mFolderPanel.setVisibility(View.GONE);
                mPhotoPanel.setVisibility(View.VISIBLE);
            } else {
                mFolderPanel.setVisibility(View.VISIBLE);
                mPhotoPanel.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final int parentId = parent.getId();
        if (parentId == R.id.gv_photo_list) {
            clickPhotoListItem(view, position);
        } else if (parentId == R.id.lv_folder_list) {
            clickFolderListItem(position);
        }
    }

    private void showSelectCountTip() {
        String tip;
        if (isGifSelected) {
            tip = "最多选择" + MAX_PHOTO_SIZE + "张图片哦~";
        } else {
            if (MAX_PHOTO_SIZE != 9) {
                tip = isSimple ? "最多选择100张照片哦~" : "相册最多上传100张照片哦~";
            } else {
                tip = isSimple ? "最多选择9张照片哦~" : "每次最多上传9张照片哦~";
            }
        }
        KwToast.show(tip);
//        KSingDialogUtils.showTipsDialog(getActivity(), tip, "我知道了");
    }

    /**
     * 点击照片项目
     *
     * @param view
     * @param position
     */
    private void clickPhotoListItem(View view, int position) {
        PhotoInfo info = mCurPhotoList.get(position);
        boolean canNext = true;
        if (null != mItemClickListener) {
            canNext = mItemClickListener.onItemClick(info);
        }
        if (!canNext) {
            return;
        }
        if (isGifSelected) {
            if (mHasSelectGif) {
                if (!mSelectPhotoList.contains(info)) {
                    KwToast.show("每次仅能添加一张GIF图");
                    return;
                }
            } else {
                if (mSelectPhotoList.size() > 0 && info.isGif()) {
                    KwToast.show("GIF图不能和图片同时选择");
                    return;
                }
            }
        }
        if (!isMultiSelected) {//不是多选，点击后直接跳转
            mSelectPhotoList.clear();
            mSelectPhotoList.add(info);
            clickContinue();
            return;
        }

        boolean checked = false;
        if (!mSelectPhotoList.contains(info)) {
            if (mSelectPhotoList.size() == MAX_PHOTO_SIZE) {
                showSelectCountTip();
                return;
            } else {
                mSelectPhotoList.add(info);
                checked = true;
                if (info.isGif() && isGifSelected) {
                    mHasSelectGif = true;
                    updateContinueTitle("完成");
                }
            }
        } else {
            try {
                for (Iterator<PhotoInfo> iterator = mSelectPhotoList.iterator(); iterator.hasNext(); ) {
                    PhotoInfo pi = iterator.next();
                    if (pi != null && TextUtils.equals(pi.getPath(), info.getPath())) {
                        if (pi.isGif() && isGifSelected) {
                            mHasSelectGif = false;
                            updateContinueTitle("继续");
                        }
                        iterator.remove();
                        break;
                    }
                }
            } catch (Exception e) {
            }
            checked = false;
        }
        refreshSelectCount();

        PhotoListAdapter.ViewHolder holder = (PhotoListAdapter.ViewHolder) view.getTag();
        if (holder != null) {
            holder.mPhotoStatus.setChecked(checked);
        } else {
            mPhotoListAdapter.notifyDataSetChanged();
        }
    }

    private void updateContinueTitle(String title) {
        if (mContinue != null) {
            mContinue.setText(title);
        }
    }

    /**
     * 点击文件夹列表项目
     *
     * @param position item position
     */
    private void clickFolderListItem(int position) {
        mFolderPanel.setVisibility(View.GONE);
        mPhotoPanel.setVisibility(View.VISIBLE);
        mCurPhotoList.clear();
        PhotoFolderInfo photoFolderInfo = mAllPhotoFolderList.get(position);
        if (photoFolderInfo.getPhotoList() != null) {
            mCurPhotoList.addAll(photoFolderInfo.getPhotoList());
        }
        mPhotoListAdapter.notifyDataSetChanged();
        mFolderTitle.setText(photoFolderInfo.getFolderName());
        if (mCurPhotoList.size() == 0) {
            setEmptyView();
        }
    }

    @Override
    public boolean isNeedSwipeBack() {
        return false;
    }

    public interface OnPhotoSelectFinishListener {
        void onSelectFinish(ArrayList<PhotoInfo> photoInfos, boolean simple);
    }

    public interface OnPhotoItemClickListener {
        //返回true表示可以选择，false表示不可以选择
        boolean onItemClick(PhotoInfo photoInfo);
    }//

    private class WeakHandler extends Handler {

        private WeakReference<Fragment> mFragment;

        public WeakHandler(Fragment fragment) {
            mFragment = new WeakReference<Fragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mFragment.get() == null) {
                return;
            }
            handle(msg);
        }

        public void handle(Message msg) {
            if (msg.what == HANDLER_REFRESH_LIST_EVENT) {
                hideProcess();
                refreshSelectCount();
                notifyDataSetChanged();

                if (mAllPhotoFolderList == null
                        || mAllPhotoFolderList.isEmpty()
                        || mAllPhotoFolderList.get(0).getPhotoList() == null
                        || mAllPhotoFolderList.get(0).getPhotoList().isEmpty()) {
                    setEmptyView();
                }

                setWidgetClickEnable(true);
            }
        }
    }
}

