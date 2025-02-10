package com.lazylite.play.timing;

import android.app.Activity;
import android.text.TextUtils;
import android.util.SparseArray;

import com.godq.media_api.media.IPlayObserver;
import com.godq.media_api.media.IPlayController;
import com.godq.media_api.media.PlayStatus;
import com.lazylite.bridge.router.ServiceImpl;
import com.lazylite.media.playctrl.PlayControllerImpl;
import com.lazylite.mod.config.ConfMgr;
import com.lazylite.mod.log.LogMgr;
import com.lazylite.mod.messagemgr.MessageManager;
import com.lazylite.mod.utils.KwDate;
import com.lazylite.mod.utils.KwTimer;
import com.lazylite.mod.widget.CommonCenterDialog;

public final class TsSleepCtr {

    /**
     * 没有模式
     */
    public static final int SLEEP_MODE_NONE = 0;
    /**
     * 定时模式
     */
    public static final int SLEEP_MODE_TIME = 1;
    /**
     * 定集模式
     */
    public static final int SLEEP_MODE_NUM = 2;

    /**
     * 定时间隔
     */
    private final static int TIMER_COUNT = 1000;

    /**
     * 当前定时模式
     */
    private int controlSleepMode = SLEEP_MODE_NONE;
    /**
     * 设定分钟数
     */
    private int setTime = -1;
    /**
     * 设定集数
     */
    private int setNum = -1;

//	/**剩余分钟数*/
//	private int minutesRemaining = 0;
    /**
     * 剩余秒钟数
     */
    private long secondRemaining = 0;

    /**
     * 已播放集数
     */
    private int playedNum = 0;


    /**
     * 关闭app
     */
    private static final int SLEEP_SET = 0x8000099;

    private SparseArray<CallBack> cList = new SparseArray<>();
    private KwTimer mTimer;
    private final IPlayController playController;
    private KwTimer countdownTimer;
    private CommonCenterDialog mDialog;
    private Activity mActivity;

    private static final String SLEEP_CONFIG = "sleep_config";
    private static final String SLEEP_APPLY_MODE = "sleep_apply_mode";


    public static TsSleepCtr getIns() {
        return Inner.INSTANCE;
    }

    private static class Inner {
        private static final TsSleepCtr INSTANCE = new TsSleepCtr();
    }

    public void setActivity(Activity activity){
        mActivity = activity;
    }


    private TsSleepCtr() {
        playController = (IPlayController) ServiceImpl.getInstance().getService(IPlayController.class.getName());
        initConfig();
        MessageManager.getInstance().attachMessage(IPlayObserver.EVENT_ID, new TimingPlayObserver());
    }

    private void initConfig() {
        //初始化 上次保存数据（总是记住模式会有）最好在MainActivity里初始化
        String sleepConfigStr = ConfMgr.getStringValue("", SLEEP_CONFIG, "");
        if (!TextUtils.isEmpty(sleepConfigStr)) {
            cancelSleep();
            String[] sleepConfigs = sleepConfigStr.split(",");
            if (sleepConfigs.length != 2) {
                return;
            }

            controlSleepMode = Integer.parseInt(sleepConfigs[0]);
            int count = Integer.parseInt(sleepConfigs[1]);
            if (isTimeMode()) {
                setTime(count);
            } else if (isNumMode()) {
                setNum(count);
            }
        }
    }

    public void addCallBack(int index, CallBack callback) {
        cList.put(index, callback);
    }

    // 不移除 会泄露  都在主线程操作cList SparseArray线程不安全
    public void removeCallBack(int key) {
        cList.remove(key);
    }

    /**
     * 重置睡眠定时
     */
    private void resetView() {
        int size = cList.size();
        for (int i = 0; i < size; i++) {
            int key = cList.keyAt(i);
            CallBack callBack = cList.get(key);
            if (callBack != null) {
                callBack.resetView();
            }
        }
    }

    /**
     * 更新定时
     */
    private void updateTime(boolean isFirst) {
        int size = cList.size();
        for (int i = 0; i < size; i++) {
            int key = cList.keyAt(i);
            CallBack callBack = cList.get(key);
            if (callBack != null) {
                callBack.updateTime(isFirst);
            }
        }
    }

    /**
     * 更新定集
     */
    private void updateChp(boolean isFirst) {
        int size = cList.size();
        for (int i = 0; i < size; i++) {
            int key = cList.keyAt(i);
            CallBack callBack = cList.get(key);
            if (callBack != null) {
                callBack.updateChp(isFirst);
            }
        }
    }


    private void cancelSleep() {
        controlSleepMode = SLEEP_MODE_NONE;

        setTime = -1;
        setNum = -1;

        secondRemaining = 0;

        playedNum = 0;

        stopTime();
    }


    public boolean isTimeMode() {
        return controlSleepMode == SLEEP_MODE_TIME;
    }

    public boolean isNumMode() {
        return controlSleepMode == SLEEP_MODE_NUM;
    }

    /**
     * 设置记住一次还是总是记住
     *
     * @param isChecked true 总是记住
     */
    public void setAlwaysCheck(boolean isChecked) {
        String config = "";
        int mode = 0;
        if (isChecked) {
            mode = 1;
            if (controlSleepMode != SLEEP_MODE_NONE){
                if (isTimeMode() && setTime > 0) {
                    config = SLEEP_MODE_TIME + "," + setTime;
                } else if (isNumMode() && setNum > 0) {
                    config = SLEEP_MODE_NUM + "," + setNum;
                }
            }
        }
        ConfMgr.setIntValue("", SLEEP_APPLY_MODE, mode, false);
        ConfMgr.setStringValue("", SLEEP_CONFIG, config, false);
    }

    public boolean getAlwaysCheckMode() {
        return 1 == ConfMgr.getIntValue("", SLEEP_APPLY_MODE, 0);
    }

    /**
     * 设置多少分钟后退出app
     *
     * @param sleepTime 分钟数
     */
    public void setTime(int sleepTime) {
        cancelSleep();
        controlSleepMode = SLEEP_MODE_TIME;
        setTime = sleepTime;
        secondRemaining = sleepTime * KwDate.T_MS_MINUTE;
        startTimer();
        updateTime(true);
        setAlwaysCheck(getAlwaysCheckMode());
    }

    /**
     * 设置多少集后退出app
     */
    public void setNum(int num) {
        cancelSleep();
        //本集还没播放，点1集后 播放会直接弹退出框
        if (playController != null) {
            if (playController.getPlayStatus() == PlayStatus.STATUS_STOP || playController.getPlayStatus() == PlayStatus.STATUS_INIT) {
                playedNum = -1;
            }
        }
        controlSleepMode = SLEEP_MODE_NUM;
        setNum = num;
        updateChp(true);
        setAlwaysCheck(getAlwaysCheckMode());
    }

    /**
     * 重置所有
     */
    public void reset() {
        cancelSleep();
        resetView();
    }

    void onPlayNewOne() {
        if (isNumMode()) {
            isNumFinish();
        }
    }

    /**
     * 到定集 结束
     */
    private void isNumFinish() {
        if (isNumMode()) {
            playedNum++;
            if (setNum != -1 && playedNum >= setNum) {
                exitApp();
            } else {
                updateChp(false);
            }
        }
    }

    private void isTimeFinish() {
        if (isTimeMode()) {
            secondRemaining = secondRemaining - TIMER_COUNT >= 0 ? secondRemaining - TIMER_COUNT : 0;
            if (secondRemaining <= 0) {
                exitApp();
            } else {
                updateTime(false);
            }
        }
    }


    public void exitApp() {
        if (setTime == -1 && setNum == -1) {
            return;
        }

        LogMgr.e("appexit", "mainsleep");
        reset();
        showTipsDialog();
        countdownTimer = new KwTimer(new KwTimer.Listener() {
            /**
             * 5秒后退出
             */
            private int second = 5;

            @Override
            public void onTimer(KwTimer timer) {
                second--;
                LogMgr.e("appexit", second+"");
                if(mDialog != null) {
                    mDialog.setDescriptionText("软件将会在" + second + "s 后退出,点击取消后取消退出");
                }
                if (second == 0) {
                    try {
                        PlayControllerImpl.getInstance().stop();
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                    } catch (Throwable throwable) {
                        LogMgr.e(throwable.getMessage() + "");
                    }
                }
            }
        });
        countdownTimer.start(1000, 5);
    }

    private void showTipsDialog(){
        if (mActivity == null) {
            return;
        }
        CommonCenterDialog.Builder  dialogBuilder = new CommonCenterDialog.Builder(mActivity);
        mDialog = dialogBuilder.setTitle("睡眠定时提示")
                .setDescription("软件将会在5s 后退出,点击取消后取消退出")
                .setSingleButton().setSingleText("取消")
                .setSingleClickListener(v -> {
                    if (countdownTimer.isRunnig()) {
                        countdownTimer.stop();
                    }
                }).build();
        mDialog.showDialog();
    }

    public void startTimer() {
        if (mTimer == null) {
            mTimer = new KwTimer(new KwTimer.Listener() {
                @Override
                public void onTimer(KwTimer timer) {
                    if (isTimeMode()) {
                        isTimeFinish();
                    } else {
                        stopTime();
                    }
                }
            });
        }
        mTimer.start(1000);
    }

    public void stopTime() {
        if (mTimer != null) {
            mTimer.stop();
        }
    }

    public int getSetNum() {
        return setNum;
    }

    public int getSetTime() {
        return setTime;
    }


    public String getSleepText() {
        if (isTimeMode()) {
            return getTimeStr();
        } else if (isNumMode()) {
            return getNemStr();
        } else {
            return "";
        }
    }

    public String getTimeStr() {
        if (secondRemaining > 0) {
            return formatTime(secondRemaining);
        }
        return "";
    }

    public String getNemStr() {
        int cnt = setNum - playedNum;
        if (playedNum == -1) {
            cnt--;
        }
        String text;
        if (cnt <= 1 && playedNum == -1) {
            text = "本集";
        } else {
            text = cnt + "集";
        }
        return text;
    }

    private String formatTime(long time) {
        StringBuilder formatTime = new StringBuilder();

        long hours = time / (60 * 60 * 1000);
        // 获取分值
        long minutes = (time - hours * (60 * 60 * 1000))
                / (60 * 1000);
        // 获取秒值
        long seconds = (time - hours * (60 * 60 * 1000) - minutes
                * (60 * 1000)) / 1000;
        if (hours < 10) {
            formatTime.append("0" + hours);
        } else
            formatTime.append(hours + "");
        if (minutes < 10) {
            formatTime.append(":0" + minutes);
        } else
            formatTime.append(":" + minutes + "");
        if (seconds < 10) {
            formatTime.append(":0" + seconds);
        } else
            formatTime.append(":" + seconds + "");

        return formatTime.toString();
    }


    public interface CallBack {
        /**
         * 重置睡眠定时
         */
        void resetView();

        /**
         * 更新定时
         * isFirst true 第一次回调   false 之后回调 一般用于只更新倒计时时间
         */
        void updateTime(boolean isFirst);

        /**
         * 更新定集
         */
        void updateChp(boolean isFirst);
    }
}
