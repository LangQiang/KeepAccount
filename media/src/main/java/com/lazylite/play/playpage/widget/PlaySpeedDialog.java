package com.lazylite.play.playpage.widget;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lazylite.media.R;
import com.lazylite.media.playctrl.PlayControllerImpl;
import com.lazylite.mod.widget.dialog.BlurBgDialog;
import com.lazylite.play.PlayHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qyh
 * @date 2022/1/10
 * describe:倍速播放
 */
public class PlaySpeedDialog extends BlurBgDialog {

    private FragmentActivity mActivity;
    private List<Float> multipleList = new ArrayList<>();
    private View view;

    public PlaySpeedDialog(FragmentActivity activity) {
        super(activity);
        mActivity = activity;
        initData();
        showBlurBg(true);
    }

    private void initData() {
        multipleList.add(0.5f);
        multipleList.add(0.75f);
        multipleList.add(1f);
        multipleList.add(1.25f);
        multipleList.add(1.5f);
        multipleList.add(2f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(getContext(), R.layout.lrlite_media_multiple_dialog, null);
        setContentView(view);
        initDialog();
    }

    private void initDialog() {
        View dialogClose = view.findViewById(R.id.dialog_close);
        dialogClose.setOnClickListener(v -> {
            cancel();
        });

        RecyclerView listView = view.findViewById(R.id.rv_list);
        listView.setLayoutManager(new LinearLayoutManager(mActivity));
        BaseQuickAdapter adapter = new BaseQuickAdapter<Float, BaseViewHolder>(
                R.layout.lrlite_media_multiple_item, multipleList) {

            @Override
            protected void convert(BaseViewHolder helper, Float item) {
                if (item == 1) {
                    helper.setText(R.id.multiple_tv, item + "倍速·正常");
                } else {
                    helper.setText(R.id.multiple_tv, item + "倍速");
                }
                float speed = PlayControllerImpl.getInstance().getSpeed();
                boolean selected = speed == item;
                //去掉最后的横线
                helper.setGone(R.id.multiple_line, item != 2);

                helper.setGone(R.id.multiple_iv, selected);

            }
        };
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                try {
                    Float speed = (Float) adapter.getItem(position);
                    if (speed == null) {
                        return;
                    }
                    PlayControllerImpl.getInstance().setSpeed(speed);
                    adapter.notifyDataSetChanged();
                    dismiss();
                } catch (Exception ignored) {
                    //防止 不是Float
                }
            }
        });
        listView.setAdapter(adapter);
    }


    public void showDialog() {
        if (!isShowing()) {
            show();
        }
    }

}
