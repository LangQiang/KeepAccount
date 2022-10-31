package com.godq.upload.chooseimage;

import android.text.format.Time;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
    /**
     * 一秒的毫秒数
     */
    public static final long SECOND = 1000;
    public static final long MINUTE = SECOND * 60;
    public static final long HOUR = MINUTE * 60;
    /**
     * 一天的毫秒数
     */
    public static final long DAY = 1000 * 60 * 60 * 24L;
    public static final long WEEK = DAY * 7;
    public static final long MONTH = DAY * 30;
    public static final long SEASON = MONTH * 3;
    public static final long YEAR = DAY * 365;
    private static Time today = new Time();
    private static Time time = new Time(); //不能加"GMT+8"，按网上加了时间就不对了。

    /**
     * 生成要显示的时间字符串
     *
     * @param milliSecs
     * @return
     */
    public static String toString(long milliSecs) {
        StringBuffer sb = null;
        sb = new StringBuffer();
        long second = milliSecs / 1000;
        long m = second / 60;
        sb.append(m < 10 ? "0" : "").append(m);
        sb.append(":");
        long s = second % 60;
        sb.append(s < 10 ? "0" : "").append(s);
        return sb.toString();
    }

    /**
     * 生成要显示的时间字符串
     * HH:mm:ss
     *
     * @param milliSecs
     * @return
     */
    public static String formatTime(long milliSecs) {
        StringBuffer sb = new StringBuffer();
        long second = milliSecs / 1000;

        long h = second / 3600;
        long m = second % 3600 / 60;
        long s = second % 60;

        sb.append(h < 10 ? "0" : "").append(h);
        sb.append(":");
        sb.append(m < 10 ? "0" : "").append(m);
        sb.append(":");
        sb.append(s < 10 ? "0" : "").append(s);
        return sb.toString();
    }

    /**
     * 生成要显示的时间字符串
     * HH:mm:ss/mm:ss
     *
     * @param milliSecs
     * @return
     */
    public static String formatHourTime(long milliSecs) {
        StringBuffer sb = new StringBuffer();
        long second = milliSecs / 1000;

        long h = second / 3600;
        long m = second % 3600 / 60;
        long s = second % 60;

        if(h > 0){
            sb.append(h < 10 ? "0" : "").append(h);
            sb.append(":");
        }
        sb.append(m < 10 ? "0" : "").append(m);
        sb.append(":");
        sb.append(s < 10 ? "0" : "").append(s);
        return sb.toString();
    }

    public static String format(long time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(time));
    }

    /**
     * 复杂格式化传入的时间值，可按当天只显示时间，昨天显示昨天加时间，前天及以前显示月-日，跨年显示年-月-日
     *
     * @param hasTime：用于标示，除当天外，其它的时间格式中，是否包括时分字符
     * @return
     */
    public static String ExFormatDateTime(long msec, boolean hasTime) {
        today.setToNow();
        time.set(msec);  //%Y%m%dT%H%M
        if (time.year < today.year) { //去年的时间
            return time.format(hasTime ? "%Y-%m-%d %H:%M" : "%Y-%m-%d");
        } else if (time.year == today.year) { //今年时间
            if (time.month < today.month) {  //上一个月以前
                return time.format(hasTime ? "%m-%d %H:%M" : "%m-%d");
            } else if (time.month == today.month) {  //当前月
                if (time.monthDay == today.monthDay) {  //当天时间
                    return time.format("%H:%M");
                } else if ((time.monthDay + 1) == today.monthDay) { //昨天
                    return hasTime ? "昨天 " + time.format("%H:%M") : "昨天";
                } else if ((time.monthDay + 1) < today.monthDay) {
                    return time.format(hasTime ? "%m-%d %H:%M" : "%m-%d");
                } else {
                    return time.format(hasTime ? "%Y-%m-%d %H:%M" : "%Y-%m-%d"); //此为未来时间，统一全显示格式
                }
            } else {
                return time.format(hasTime ? "%Y-%m-%d %H:%M" : "%Y-%m-%d"); //此为未来时间，统一全显示格式
            }
        } else {
            return time.format(hasTime ? "%Y-%m-%d %H:%M" : "%Y-%m-%d");//此为未来时间，统一全显示格式
        }
    }

    /**
     * 复杂格式化传入的时间值2，可按当天只显示时间，昨天显示昨天加时间，前天及以前显示月-日，跨年显示年-月-日，比上一个函数增加钟显示功能了一小时内的分
     *
     * @param hasTime：用于标示，除当天外，其它的时间格式中，是否包括时分字符
     * @return
     */
    public static String ExFormatDateTime2(long msec, boolean hasTime) {
        today.setToNow();
        time.set(msec);  //%Y%m%dT%H%M
        if (time.year < today.year) { //去年的时间
            return time.format(hasTime ? "%Y-%m-%d %H:%M" : "%Y-%m-%d");
        } else if (time.year == today.year) { //今年时间
            if (time.month < today.month) {  //上一个月以前
                return time.format(hasTime ? "%m-%d %H:%M" : "%m-%d");
            } else if (time.month == today.month) {  //当前月
                if (time.monthDay == today.monthDay) {  //当天时间
                    long minSpace = System.currentTimeMillis() - msec;
                    if (minSpace < 3600000) { //如果是一小时以内的时间
                        int minVal = (int) (minSpace / 60000);
                        if (minVal < 1) {
                            return "刚刚";
                        } else {
                            return minVal + "分钟前";
                        }
                    } else {
                        return time.format("%H:%M");
                    }
                } else if ((time.monthDay + 1) == today.monthDay) { //昨天
                    return hasTime ? "昨天 " + time.format("%H:%M") : "昨天";
                } else if ((time.monthDay + 1) < today.monthDay) {
                    return time.format(hasTime ? "%m-%d %H:%M" : "%m-%d");
                } else {
                    return time.format(hasTime ? "%Y-%m-%d %H:%M" : "%Y-%m-%d"); //此为未来时间，统一全显示格式
                }
            } else {
                return time.format(hasTime ? "%Y-%m-%d %H:%M" : "%Y-%m-%d"); //此为未来时间，统一全显示格式
            }
        } else {
            return time.format(hasTime ? "%Y-%m-%d %H:%M" : "%Y-%m-%d");//此为未来时间，统一全显示格式
        }
    }

    /**
     * 复杂格式化传入的时间值2，可按当天只显示时间，昨天显示昨天加时间，前天及以前显示月-日，跨年显示年-月-日，比上一个函数增加钟显示功能了一小时内的分
     *
     * @param msec：毫秒值
     * @return
     */
    public static String ExFormatDateTime3(long msec) {
        today.setToNow();
        time.set(msec);

        if (time.year == today.year
                && time.month == today.month
                && time.monthDay == today.monthDay) {
            long minSpace = System.currentTimeMillis() - msec;
            if (minSpace < 3600000) { //如果是一小时以内的时间
                int minVal = (int) (minSpace / 60000);
                if (minVal < 5) {
                    return "当前在线";
                } else {
                    return minVal + "分钟前在线";
                }
            } else {
                return minSpace / 3600000 + "小时前在线";
            }
        } else {
            return "1天前在线";
        }
    }

    public static String formatPlayTime(int time) {
        if (time <= 0) {
            return "00:00";
        }

        int sec, min;
        sec = (time / 1000) % 60;
        min = time / 60000;

        return String.format(Locale.getDefault(), "%02d:%02d", min, sec);
    }
}
