package com.atool.apm.dokit.component;

import android.app.Activity;
import android.content.Context;

import com.atool.apm.R;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.lazylite.bridge.router.ServiceImpl;
import com.lazylite.mod.App;

import org.json.JSONObject;


public class PayDialogComponent extends AbstractKit {
    @Override
    public int getIcon() {
        return R.drawable.apm_logcat_icon;
    }

    @Override
    public int getName() {
        return R.string.dokit_pay_dialog;
    }

    @Override
    public void onAppInit(Context context) {

    }

    @Override
    public void onClick(Context context) {
//        Activity activity = App.getMainActivity();
//        if (activity == null) {
//            return;
//        }
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.putOpt("userId", "610000061");
//            jsonObject.putOpt("userName","12345");
//            jsonObject.putOpt("orderId","12206064372900001");
//            jsonObject.putOpt("orderPrice","0.01");
//            jsonObject.putOpt("orderName", "元惜中文标题测试");
//            ((IPayService)ServiceImpl.getInstance().getService(IPayService.class.getName())).showPayDialog(activity, jsonObject.toString());
//        } catch (Exception ignore) {
//
//        }
    }
}
