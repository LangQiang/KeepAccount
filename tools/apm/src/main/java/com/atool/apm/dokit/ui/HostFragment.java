package com.atool.apm.dokit.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atool.apm.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lazylite.mod.config.ConfMgr;
import com.lazylite.mod.config.IConfDef;
import com.lazylite.mod.utils.toast.KwToast;
import com.lazylite.mod.widget.BaseFragment;
import com.lazylite.mod.widget.KwTitleBar;

import java.util.ArrayList;
import java.util.List;


public class HostFragment extends BaseFragment {
    private RecyclerView mRvHost;
    private HostAdapter mHostAdapter;
    private List<HostModel> mHostModels;
    private View selectContainer;
    private RecyclerView selectRv;
    private List<SelectModel> hosts = new ArrayList<>();

    private void initHosts() {
        hosts.add(new SelectModel("http://49.232.151.23", "http://150.158.55.208", "测试"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feedback_config_host, container, false);
        mRvHost = (RecyclerView) view.findViewById(R.id.recyclerview_host);
        KwTitleBar titleBar = (KwTitleBar) view.findViewById(R.id.title);
        titleBar.setMainTitle("配置Host");
        titleBar.setRightTextBtn("添加");

        titleBar.setRightListener(new KwTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick() {
                addItem();
            }
        });
        initSelectView(view);
        view.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (writeHostFile(mHostModels)){
                    KwToast.show("保存成功，重启app生效");
                } else {
                    KwToast.show("保存失败");
                }
            }
        });
        view.findViewById(R.id.btn_add_weex).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean containsWeex = false;
                for (HostModel model : mHostModels){
                    if ("wx_cache_host".equals(model.key)){
                        containsWeex = true;
                        break;
                    }
                }
                if (!containsWeex){
                    HostModel model = new HostModel();
                    model.key = "wx_cache_host";
                    model.value = "webapi.kuwo-inc.com";
                    mHostModels.add(model);
                    initAdapter();
                }
            }
        });
        return view;
    }

    private void initSelectView(View view) {
        KwTitleBar selectTitle = view.findViewById(R.id.select_title);
        selectTitle.setMainTitle("快速选择host");
        selectContainer = view.findViewById(R.id.select_container);
        selectRv = view.findViewById(R.id.selectRv);
        initHosts();
        final SelectAdapter selectAdapter = new SelectAdapter(R.layout.download_chapter_del_item, hosts);
        selectAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                hosts.get(position).isSelect = !hosts.get(position).isSelect;
                selectAdapter.notifyDataSetChanged();
            }
        });
        selectRv.setLayoutManager(new LinearLayoutManager(view.getContext()));
        selectRv.setAdapter(selectAdapter);
        view.findViewById(R.id.host_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectContainer.setVisibility(View.VISIBLE);
            }
        });
        view.findViewById(R.id.confirm_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (SelectModel host : hosts) {
                    if (!host.isSelect) {
                        continue;
                    }
                    boolean contains = false;
                    for (HostModel model : mHostModels){
                        if (host.key.equals(model.key)){
                            contains = true;
                            break;
                        }
                    }
                    if (!contains){
                        HostModel model = new HostModel();
                        model.key = host.key;
                        model.value = host.value;
                        mHostModels.add(model);
                        initAdapter();
                    }
                }
                selectContainer.setVisibility(View.GONE);
            }
        });

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectContainer.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    private void initData() {
        mHostModels = readHostFile();
        if (!mHostModels.isEmpty()){
            initAdapter();
        } else {
            mRvHost.setVisibility(View.GONE);
        }
    }

    private void initAdapter(){
        mRvHost.setVisibility(View.VISIBLE);
        if (mHostAdapter == null){
            LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
            mRvHost.setLayoutManager(manager);
            mHostAdapter = new HostAdapter();
            mRvHost.setAdapter(mHostAdapter);
        } else {
            mHostAdapter.notifyDataSetChanged();
        }
    }

    private void addItem() {
        mHostModels.add(new HostModel());
        initAdapter();
    }

    private List<HostModel> readHostFile() {
        List<HostModel> hostModels = new ArrayList<>();
        String entrust_host = ConfMgr.getStringValue(IConfDef.SEC_APP, IConfDef.KEY_ENTRUST_HOST, "");
        if (!TextUtils.isEmpty(entrust_host)) {
            String[] hostKV = entrust_host.split("\\|");
            for (String s : hostKV) {
                String[] split = s.split("#");
                if (split.length == 2) {
                    hostModels.add(new HostModel(split[0], split[1]));
                }
            }
        }
        return hostModels;
    }

    private boolean writeHostFile(List<HostModel> hostModels){
        List<HostModel> notNullList = new ArrayList<>();
        for (HostModel hostModel : hostModels){
            if (!TextUtils.isEmpty(hostModel.key) && !TextUtils.isEmpty(hostModel.value)){
                notNullList.add(hostModel);
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < notNullList.size(); i++) {
            HostModel hostModel = notNullList.get(i);
            if (i != 0) {
                stringBuilder.append("|");
            }
            stringBuilder.append(hostModel.key).append("#").append(hostModel.value).append("|");

        }
        ConfMgr.setStringValue(IConfDef.SEC_APP, IConfDef.KEY_ENTRUST_HOST, stringBuilder.toString(), false);
        return true;
    }

    private class HostAdapter extends RecyclerView.Adapter<HostHolder>{
        @Override
        public HostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).
                    inflate(R.layout.item_feedback_config_host, parent, false);
            return new HostHolder(view);
        }

        @Override
        public void onBindViewHolder(final HostHolder holder, int position) {
            HostModel hostModel = mHostModels.get(position);
            if (!TextUtils.isEmpty(hostModel.key)){
                holder.mEtHost.setText(hostModel.key + " " + hostModel.value);
            } else {
                holder.mEtHost.setText("");
            }
            holder.mIvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteHost(holder.getAdapterPosition());
                }
            });
            holder.mEtHost.setTag(position);
            holder.mEtHost.addTextChangedListener(new EditTextWatcher(){
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    int keyPosition = (int) holder.mEtHost.getTag();
                    if (keyPosition == holder.getAdapterPosition()){
                        String str = s.toString().trim();
                        String[] arr = str.split("\\s+");
                        HostModel model = mHostModels.get(holder.getAdapterPosition());
                        if (arr.length > 1){
                            model.key = arr[0];
                            model.value = arr[1];
                        }else {
                            model.key = "";
                            model.value = "";
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mHostModels.size();
        }

        private void deleteHost(int position){
            mHostModels.remove(position);
            if (writeHostFile(mHostModels)){
                notifyDataSetChanged();
                KwToast.show("删除成功，重启app生效");
            } else {
                KwToast.show("删除失败");
            }
        }

    }

    class HostHolder extends RecyclerView.ViewHolder {
        EditText mEtHost;
        View mIvDelete;


        HostHolder(View itemView) {
            super(itemView);
            mEtHost = (EditText) itemView.findViewById(R.id.et_host);
            mIvDelete = itemView.findViewById(R.id.iv_delete);
        }
    }

    private class EditTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    private class HostModel{

        HostModel(){}

        HostModel(String key, String value){
            this.key = key;
            this.value = value;
        }

        public String key;
        public String value;
    }

    class SelectAdapter extends BaseQuickAdapter<SelectModel, BaseViewHolder> {

        public SelectAdapter(int layoutResId, @Nullable List<SelectModel> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, SelectModel item) {
            helper.setText(R.id.title, item.key + (TextUtils.isEmpty(item.des) ? "" : ("#" + item.des)));
            helper.setText(R.id.tab_tv1, item.value);
            View tv = helper.getView(R.id.select_btn);
            tv.setSelected(item.isSelect);
        }

    }

    class SelectModel {
        public SelectModel(String key, String value, String des) {
            this.key = key;
            this.value = value;
            this.des = des;
        }
        public String key;
        public String value;
        public String des;
        public boolean isSelect;
    }
}
