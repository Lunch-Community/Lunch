package com.lazytong.launcher.utils;

import java.util.Date;

public class TimeUtil {

    private final static long minute = 60 * 1000;// 1??
    private final static long hour = 60 * minute;// 1??
    private final static long day = 24 * hour;// 1?
    private final static long month = 31 * day;// ?
    private final static long year = 12 * month;// ?

    /**
     * 格式化时间
     *
     * @param date
     * @return
     */
    public static String getTimeFormatText(Date date) {
        if (date == null) {
            return null;
        }
        long diff = new Date().getTime() - date.getTime();
        long r = 0;
        if (diff > year) {
            r = (diff / year);
            return r + "年前";
        }
        if (diff > month) {
            r = (diff / month);
            return r + "个月前";
        }
        if (diff > day) {
            r = (diff / day);
            return r + "天前";
        }
        if (diff > hour) {
            r = (diff / hour);
            return r + "小时前";
        }
        if (diff > minute) {
            r = (diff / minute);
            return r + "分钟前";
        }
        return "刚刚";
    }
}