package com.lazylite.play.templist;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facebook.drawee.view.SimpleDraweeView;
import com.godq.media_api.media.IMediaService;
import com.godq.media_api.media.IPlayController;
import com.lazylite.bridge.router.ServiceImpl;
import com.lazylite.media.R;
import com.godq.media_api.media.bean.BookBean;
import com.godq.media_api.media.bean.ChapterBean;
import com.lazylite.mod.imageloader.fresco.ImageLoaderWapper;
import com.lazylite.mod.imageloader.fresco.config.ImageLoadConfig;
import com.lazylite.mod.utils.ScreenUtility;
import com.lazylite.mod.utils.psrc.PsrcInfo;
import com.lazylite.mod.utils.psrc.PsrcOptional;
import com.lazylite.mod.widget.KwRecyclerLinearLayoutManager;
import com.lazylite.mod.widget.dialog.BlurBgDialog;
import com.lazylite.mod.widget.pulltorefresh.PullToRefreshBase;
import com.lazylite.mod.widget.pulltorefresh.PullToRefreshRecyclerView;
import com.lazylite.mod.widget.textview.IconView;
import com.lazylite.play.PlayHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author DongJr
 * @date 2020/3/11
 * 临时播放列表弹窗
 */
public class TempPlayListDialog extends BlurBgDialog implements View.OnClickListener {

    private TempPlayListAdapter mAdapter;
    private final boolean mIsSkin;

    private TextView mTvTitle;
    private TextView mTvTotal;
    private PullToRefreshRecyclerView mPullToRefreshView;
    private RecyclerView mRecyclerView;
    private IconView mIvSort;
    private TextView mTvSort;
    private PsrcInfo mPsrcInfo;
    private IPlayController playController;
    private SimpleDraweeView simpleDraweeView;

    private final ImageLoadConfig config = new ImageLoadConfig.Builder()
            .roundedCorner(ScreenUtility.dip2px(6))
            .create();

    private TempPlayListDialog(Activity context, boolean isSkin) {
        super(context);
        this.mIsSkin = isSkin;
        IMediaService service = (IMediaService) ServiceImpl.getInstance().getService(IMediaService.class.getName());
        if (service != null) {
            playController = service.getPlayController();
        }

        mPsrcInfo = PsrcOptional.buildRoot("临时列表", PsrcOptional.DEFAULT_POSITION);
        if (isSkin) {
            showBlurBg(true);
        }
    }

    public static void pop(Activity activity, boolean isSkin) {
        if (activity == null) {
            return;
        }
        TempPlayListDialog dialog = new TempPlayListDialog(activity, isSkin);
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(onCreateContentView());
        initData();
    }

    protected View onCreateContentView() {
        View view;
        if (mIsSkin) {
            view = View.inflate(getContext(), R.layout.layout_temp_play_dialog_skin, null);
        } else {
            view = View.inflate(getContext(), R.layout.layout_temp_play_dialog, null);
        }
        simpleDraweeView = view.findViewById(R.id.templist_album_cover_iv);
        mTvTitle = view.findViewById(R.id.tv_title);
        mTvTotal = view.findViewById(R.id.tv_total);
        mPullToRefreshView = view.findViewById(R.id.pull_to_refresh);
        mRecyclerView = mPullToRefreshView.getRefreshableView();
        mPullToRefreshView.setOnRefreshListener(new RefreshListener());
        mTvSort = view.findViewById(R.id.tv_sort);
        mIvSort = view.findViewById(R.id.iv_sort);

        view.findViewById(R.id.ll_head).setOnClickListener(this);
        View tvCancel = view.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(this);
        View llSort = view.findViewById(R.id.ll_sort);
        llSort.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_head) {
            jumpToAlbum();
        } else if (id == R.id.tv_cancel) {
            dismiss();
        } else if (id == R.id.ll_sort) {
            sortList();
        }
    }

    private void sortList() {

        if (playController == null) {
            return;
        }

        BookBean curBook = playController.getCurrentBook();

        if (curBook == null) {
            return;
        }

        curBook.sortType = curBook.sortType == BookBean.SORT_TYPE_ASC ? BookBean.SORT_TYPE_DESC : BookBean.SORT_TYPE_ASC;
        setSortText(curBook.sortType);
        List<ChapterBean> data = mAdapter.getData();
        Collections.reverse(data);
        mAdapter.setNewData(data);
        playController.updateChapterList(data);
        mPullToRefreshView.setMode(PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH);
    }

    private void initData() {
        if (playController == null) {
            return;
        }

        BookBean curBook = playController.getCurrentBook();
        List<ChapterBean> list = playController.getPlayList();

        if (curBook != null) {
            mTvTitle.setVisibility(View.VISIBLE);
            mTvTotal.setVisibility(View.VISIBLE);
            mTvTitle.setText(curBook.mName);
            mTvTotal.setText("(" + curBook.mCount + ")");
            ImageLoaderWapper.getInstance().load(simpleDraweeView, curBook.mImgUrl, config);
            setSortText(curBook.sortType);
        }
        mAdapter = new TempPlayListAdapter(list, mIsSkin, playController);
        LinearLayoutManager manager = new KwRecyclerLinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        AdapterListener adapterListener = new AdapterListener();
        mAdapter.setOnItemClickListener(adapterListener);
        mAdapter.setOnLoadMoreListener(adapterListener);
        mRecyclerView.scrollToPosition(playController.getChaptersPlayIndex());
    }

    private void jumpToAlbum() {
        if (playController == null) {
            return;
        }
        BookBean curBook = playController.getCurrentBook();
        if (curBook != null) {
            dismiss();
        }
    }

    private void setSortText(int sortType) {
        if (sortType == BookBean.SORT_TYPE_DESC) {
            mIvSort.setText(R.string.icon_order_desc);
            mTvSort.setText("倒序");
        } else {
            mIvSort.setText(R.string.icon_order_asc);
            mTvSort.setText("正序");
        }
    }


    private class RefreshListener implements PullToRefreshBase.OnRefreshListener {
        @Override
        public void onRefresh(int curMode) {
            TempPlayListManager.getInstance().refreshMoreData(new OnLoadDataListener() {
                @Override
                public void onLoadSuccess(ChapterList list) {
                    mPullToRefreshView.onRefreshComplete();
                    if (list.isEmpty()) {
                        mPullToRefreshView.setMode(PullToRefreshBase.MODE_NONE);
                    } else {
                        mAdapter.addData(0, list);
                        mRecyclerView.scrollToPosition(0);
                    }
                }

                @Override
                public void onLoadFailed() {
                    mPullToRefreshView.onRefreshComplete();
                }
            });
        }
    }

    private class AdapterListener implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.RequestLoadMoreListener {

        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            if (playController == null) {
                return;
            }
            BookBean book = playController.getCurrentBook();
            List<ChapterBean> chapters = playController.getPlayList();
            List<ChapterBean> list = new ArrayList<>(chapters);
            playController.play(book, list, position, 0);
//            TsPlayUtils.play(book, list, position, 0, mPsrcInfo);
            dismiss();
        }

        @Override
        public void onLoadMoreRequested() {
            TempPlayListManager.getInstance().loadMoreData(new OnLoadDataListener() {
                @Override
                public void onLoadSuccess(ChapterList list) {
                    if (list.isEmpty()) {
                        mAdapter.loadMoreEnd();
                    } else {
                        mAdapter.loadMoreComplete();
                        mAdapter.addData(list);
                    }
                }

                @Override
                public void onLoadFailed() {
                    mAdapter.loadMoreFail();
                }
            });
        }
    }

}
