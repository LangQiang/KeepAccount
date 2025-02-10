package com.lazylite.play.timing;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.lazylite.media.R;
import com.lazylite.mod.config.ConfMgr;
import com.lazylite.mod.widget.KuwoSwitch;
import com.lazylite.mod.widget.dialog.BlurBgDialog;
import com.lazylite.play.PlayHelper;

import java.util.ArrayList;

public class TimingDialog extends BlurBgDialog implements TsSleepCtr.CallBack{

    private Activity activity;

    int lastSetTime = 45;
    int lastSetNum = 5;

    private int[] sleepTime = {10, 20, 30, 45, 60, 90, 120, 150, 180, 240};
    private int[] sleepNum = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    private KuwoSwitch menuTimeCheck, menuNumCheck;
    private RadioButton btnModeOne, btnModeAlways;
    private TextView mMenuSleepTip;

    private ArrayList<RadioButton> menuSets = new ArrayList<>();
    private ArrayList<RadioButton> menuMin = new ArrayList<>();

    /**
     * 记录两次的睡眠模式，取上一次睡眠模式来避免重复操作
     */
    private FixSizeLinkedList<Integer> lastSleepMode = new FixSizeLinkedList<>(2);
    private View view;

    private static final String LAST_SLEEP_TIME = "last_sleep_time";
    private static final String LAST_SLEEP_CHP = "last_sleep_chp";

    public TimingDialog(Activity context) {
        super(context);
        this.activity = context;
        TsSleepCtr.getIns().setActivity(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lastSetTime = ConfMgr.getIntValue("", LAST_SLEEP_TIME, lastSetTime);
        lastSetNum = ConfMgr.getIntValue("", LAST_SLEEP_CHP, lastSetNum);
        view = View.inflate(getContext(), R.layout.lrlite_media_sleep_dialog,null);
        setContentView(view);
        mMenuSleepTip = (TextView) view.findViewById(R.id.menu_sleep_tip);
        menuTimeCheck = (KuwoSwitch) view.findViewById(R.id.menu_time_check);
        menuNumCheck = (KuwoSwitch) view.findViewById(R.id.menu_num_check);
        btnModeOne = (RadioButton) view.findViewById(R.id.btn_mode_one);
        btnModeAlways = (RadioButton) view.findViewById(R.id.btn_mode_always);
        btnModeOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    TsSleepCtr.getIns().setAlwaysCheck(false);
                }
            }
        });
        btnModeAlways.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    TsSleepCtr.getIns().setAlwaysCheck(true);
                }
            }
        });

        TsSleepCtr.getIns().addCallBack(1, this);

        if (!TsSleepCtr.getIns().getAlwaysCheckMode()) {
            btnModeOne.setChecked(true);
        } else {
            btnModeAlways.setChecked(true);
        }

        initItem(view);

        if (TsSleepCtr.getIns().isNumMode()) {
            lastSleepMode.add(TsSleepCtr.SLEEP_MODE_NUM);
            updateChp(true);
        } else if (TsSleepCtr.getIns().isTimeMode()) {
            lastSleepMode.add(TsSleepCtr.SLEEP_MODE_TIME);
            updateTime(true);
        }else {
            lastSleepMode.add(TsSleepCtr.SLEEP_MODE_NONE);
        }

        initListener();
    }

    @Override
    public void resetView() {
        mMenuSleepTip.setVisibility(View.INVISIBLE);
        if (lastSleepMode.getFirst() == TsSleepCtr.SLEEP_MODE_NUM) {
            updateItmeButton(-1, false);
        }
        if (lastSleepMode.getFirst() == TsSleepCtr.SLEEP_MODE_TIME) {
            updateItmeButton(-1, true);
        }
        setMenuCheck(0);
    }

    /**
     * 定时还是定集播放Switch选择
     *
     * @param checkType 0 全不选中；1 time选中； 2 num选中
     */
    private void setMenuCheck(int checkType) {
        switch (checkType) {
            case 1:
                if (!menuTimeCheck.isChecked()) {
                    menuNumCheck.setChecked(false);
                    menuTimeCheck.setChecked(true);
                }
                break;
            case 2:
                if (!menuNumCheck.isChecked()) {
                    menuTimeCheck.setChecked(false);
                    menuNumCheck.setChecked(true);
                }
                break;
            case 0:
            default:
                if (menuTimeCheck.isChecked()) {
                    menuTimeCheck.setChecked(false);
                }
                if (menuNumCheck.isChecked()) {
                    menuNumCheck.setChecked(false);
                }
        }
    }

    @Override
    public void updateTime(boolean isFirst) {
        if (isFirst) {
            if (lastSleepMode.getFirst() != TsSleepCtr.SLEEP_MODE_TIME){
                updateItmeButton(TsSleepCtr.getIns().getSetNum(), false);
            }
            updateItmeButton(TsSleepCtr.getIns().getSetTime(), true);
            mMenuSleepTip.setVisibility(View.VISIBLE);
            setMenuCheck(1);
        }
        String timeStr = TsSleepCtr.getIns().getTimeStr();
        mMenuSleepTip.setText(String.valueOf(timeStr + "后退出应用"));
    }

    @Override
    public void updateChp(boolean isFirst) {
        if (isFirst) {
            if (lastSleepMode.getFirst() != TsSleepCtr.SLEEP_MODE_NUM){
                updateItmeButton(TsSleepCtr.getIns().getSetTime(), true);
            }
            updateItmeButton(TsSleepCtr.getIns().getSetNum(), false);
            mMenuSleepTip.setVisibility(View.VISIBLE);
            setMenuCheck(2);
        }
        String nemStr = TsSleepCtr.getIns().getNemStr();
        mMenuSleepTip.setText(String.valueOf(nemStr + "后退出应用"));
    }

    private void initItem(View view) {

        for (int i = 0; i < 10; i++) {
            int id = activity.getResources().getIdentifier("menu_sleep_min_" + (i + 1), "id", activity.getPackageName());
            RadioButton minRadioBut = (RadioButton) view.findViewById(id);
            changeFont(minRadioBut, false);
            final int finalI = i;
            minRadioBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSleepTime(sleepTime[finalI]);
                }
            });
            menuMin.add(minRadioBut);
        }


        for (int i = 0; i < 10; i++) {
            int id = activity.getResources().getIdentifier("menu_sets_" + (i + 1), "id", activity.getPackageName());
            RadioButton setRadioBut = (RadioButton) view.findViewById(id);
            changeFont(setRadioBut, false);
            final int finalI = i;
            setRadioBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSleepNum(sleepNum[finalI]);
                }
            });
            menuSets.add(setRadioBut);
        }


    }

    private void changeFont(RadioButton rb, boolean isChecked) {
        if (rb == null) {
            return;
        }
        CharSequence text = rb.getText();
        SpannableString msp = new SpannableString(text);
        int length = text.length();
        //设置字体绝对大小
        msp.setSpan(new AbsoluteSizeSpan(21, true), 0, length - 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new AbsoluteSizeSpan(11, true), length - 2, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 正常和粗体
        msp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, length - 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), length - 2, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //设置前景色
        if (isChecked) {
            msp.setSpan(new ForegroundColorSpan(Color.parseColor("#ffffff")), 0, length - 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            msp.setSpan(new ForegroundColorSpan(Color.parseColor("#ffffff")), length - 2, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            msp.setSpan(new ForegroundColorSpan(Color.parseColor("#ccffffff")), 0, length - 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            msp.setSpan(new ForegroundColorSpan(Color.parseColor("#96ffffff")), length - 2, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        rb.setText(msp);
    }


    public void setSleepTime(int time) {
        lastSleepMode.add(TsSleepCtr.SLEEP_MODE_TIME);
        lastSetTime = time;
        ConfMgr.setIntValue("", LAST_SLEEP_TIME, lastSetTime, false);
        TsSleepCtr.getIns().setTime(lastSetTime);
    }

    public void setSleepNum(int num) {
        lastSleepMode.add(TsSleepCtr.SLEEP_MODE_NUM);
        lastSetNum = num;
        ConfMgr.setIntValue("", LAST_SLEEP_CHP, lastSetNum, false);
        TsSleepCtr.getIns().setNum(lastSetNum);
    }

    private void initListener() {
        menuTimeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    menuNumCheck.setChecked(false);
                    setSleepTime(lastSetTime);
                } else {
                    lastSleepMode.add(TsSleepCtr.SLEEP_MODE_NONE);
                    TsSleepCtr.getIns().reset();
                }
            }
        });
        menuNumCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    menuTimeCheck.setChecked(false);
                    setSleepNum(lastSetNum);
                } else {
                    lastSleepMode.add(TsSleepCtr.SLEEP_MODE_NONE);
                    TsSleepCtr.getIns().reset();
                }
            }
        });
    }

    int curTime = -1;
    int curNum = -1;

    private void updateItmeButton(int value, boolean isTime) {
        ArrayList<RadioButton> rbs;
        if (isTime) {
            if (curTime == value) {
                return;
            }
            rbs = menuMin;
            curTime = value;
        } else {
            if (curNum == value) {
                return;
            }
            rbs = menuSets;
            curNum = value;
        }
        int index = -1;
        if (TsSleepCtr.getIns().isNumMode()) {
            index = getIndex(value, sleepNum);
        } else if (TsSleepCtr.getIns().isTimeMode()) {
            index = getIndex(value, sleepTime);
        }
        int size = rbs.size();
        for (int i = 0; i < size; i++) {
            RadioButton rb = rbs.get(i);
            changeFont(rb, i == index);
            rb.setChecked(i == index);
        }
    }

    private int getIndex(int value, int[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        TsSleepCtr.getIns().removeCallBack(1);
    }
}
