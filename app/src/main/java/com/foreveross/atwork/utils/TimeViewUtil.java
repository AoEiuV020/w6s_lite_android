package com.foreveross.atwork.utils;

import android.content.Context;
import android.util.Log;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.BuildConfig;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by lingen on 15/4/3.
 * Description:
 * 对时间显示 做处理
 * <p/>
 * 时间精确至秒，1至9时需要在加零，其他不需加零。
 * 时间分段早上（5点-8点）、上午（9点-12点）、下午（13点-18点）、晚上（19点-23点）、凌晨（00点-5点）。
 * 5.3.1	今年内
 * 当天：时间分段+时间；例如：上午09:10:01。
 * 前一天：昨天。
 * 前两天：周X。
 * 超出本周：月日+时间分段+时间，日期不加零显示，例如：8月9日。
 * 5.3.2	今年之前
 * 年月日+时间，日期不加零显示，例如：2014年8月9日 09:10:01。
 */
public class TimeViewUtil {

    private static final String TAG = TimeViewUtil.class.getSimpleName();

    public static String getWeek(int day) {
        switch (day) {
            case 0:
                return AtworkApplicationLike.baseContext.getResources().getString(R.string.Sun);

            case 1:
                return AtworkApplicationLike.baseContext.getResources().getString(R.string.Mon);

            case 2:
                return AtworkApplicationLike.baseContext.getResources().getString(R.string.Tues);

            case 3:
                return AtworkApplicationLike.baseContext.getResources().getString(R.string.Wed);

            case 4:
                return AtworkApplicationLike.baseContext.getResources().getString(R.string.Thur);

            case 5:
                return AtworkApplicationLike.baseContext.getResources().getString(R.string.Fri);

            case 6:
                return AtworkApplicationLike.baseContext.getResources().getString(R.string.Sat);

            default:
                return StringUtils.EMPTY;
        }
    }

    public static String getMultipartItemViewTime(Context context, long timestamp) {
        long todayTimeInMillis = TimeUtil.getTodayTimeInMillis();

        DateFormat dateFormat;
        if(timestamp >= todayTimeInMillis) {
            dateFormat = new SimpleDateFormat(TimeUtil.TIME_ONLY);

        } else if(timestamp >= TimeUtil.getThisYearTimeInMillis()) {

            dateFormat = new SimpleDateFormat(TimeUtil.MULTIPART_MM_DD);

        } else {
            dateFormat = new SimpleDateFormat(TimeUtil.MULTIPART_YYYY_MM_DD);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);

        return dateFormat.format(calendar.getTime());



    }

    /**
     * 消息汇总使用的时间显示
     * @param context
     * @param timestamp
     * @return
     */
    public static String getNewsSummaryItemViewTime(Context context, long timestamp) {
        long todayTimeInMillis = TimeUtil.getTodayTimeInMillis();

        DateFormat dateFormat;
        if(timestamp >= todayTimeInMillis) {
            dateFormat = new SimpleDateFormat(TimeUtil.TIME_ONLY);

        } else if(timestamp >= TimeUtil.getThisYearTimeInMillis()) {

            dateFormat = new SimpleDateFormat(TimeUtil.NEWSSUMMARY_MM_DD);

        } else {
            dateFormat = new SimpleDateFormat(TimeUtil.NEWSSUMMARY_YYYY_MM_DD);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);

        return dateFormat.format(calendar.getTime());



    }

    public static String getSimpleUserCanViewTime(Context context, long timestamp) {
//        return StringUtils.EMPTY;

        String result = null;
        result = getTodayUserViewTime(context, timestamp);

        if (result == null) {
            result = getYesterdayUserViewTime(context, timestamp, false);
        }

        if (result == null) {
            result = getThisWeekUserViewTime(context, timestamp, false);
        }

        if (result == null) {
            result = getThisYearUserViewTime(context, timestamp, false);
        }

        if (result == null) {
            result = getYearsBeforeUserViewTime(context, timestamp, false);
        }

        if (BuildConfig.DEBUG) {
            Log.d(TAG, result);
        }

        return result;
    }


    public static String getUserCanViewTimeInChatDetail(Context context, long timestamp) {
        String result = null;
        result = getTodayUserViewTimeInChatDetail(context, timestamp);

        if (result == null) {
            result = getYesterdayUserViewTime(context, timestamp, false);
        }

        if (result == null) {
            result = getThisYearUserViewTime(context, timestamp, false);
        }

        if (result == null) {
            result = getYearsBeforeUserViewTime(context, timestamp, false);
        }

        return result;
    }

    public static String getUserCanViewTime(Context context, long timestamp) {

        String result = null;
        result = getTodayUserViewTime(context, timestamp);

        if (result == null) {
            result = getYesterdayUserViewTime(context, timestamp, true);
        }

        if (result == null) {
            result = getThisWeekUserViewTime(context, timestamp, true);
        }

        if (result == null) {
            result = getThisYearUserViewTime(context, timestamp, true);
        }

        if (result == null) {
            result = getYearsBeforeUserViewTime(context, timestamp, true);
        }

        if (BuildConfig.DEBUG) {
            Log.d(TAG, result);
        }

        return result;
    }

    private static String getThisYearUserViewTime(Context context, long timestamp, boolean time) {


        if (timestamp >= TimeUtil.getThisYearTimeInMillis()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);

            DateFormat dateFormat = null;
            if (time) {
                dateFormat = new SimpleDateFormat(TimeUtil.getTimeFormat6(context));
            } else {
                dateFormat = new SimpleDateFormat(TimeUtil.getTimeFormat5(context));
            }
            return dateFormat.format(calendar.getTime());
        }
        return null;
    }

    private static String getYearsBeforeUserViewTime(Context context, long timestamp, boolean time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        DateFormat dateFormat = null;
        if (time) {
            dateFormat = new SimpleDateFormat(TimeUtil.getTimeFormat2(context));
        } else {
            dateFormat = new SimpleDateFormat(TimeUtil.getTimeFormat3(context));
        }
        return dateFormat.format(calendar.getTime());
    }

    /**
     * 返回本周的日期显示
     *
     * @param timestamp
     * @return
     */
    private static String getThisWeekUserViewTime(Context context, long timestamp, boolean time) {

        long thisWeekTimeInMillis = TimeUtil.getThisWeekTimeInMillis();
        if (timestamp >= thisWeekTimeInMillis) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);

            String prefix = getWeek(calendar.get(Calendar.DAY_OF_WEEK) - 1);

            if (time) {
                DateFormat dateFormat = new SimpleDateFormat(TimeUtil.TIME_ONLY);
                return LanguageUtil.isZhLocal(context) ? prefix + " " + dateFormat.format(calendar.getTime()) :  dateFormat.format(calendar.getTime()) + " " + prefix;
            }else{
                return prefix;
            }


        }
        return null;
    }




    /**
     * 前一天：昨天 09:10:01
     *
     * @param timestamp
     * @return
     */
    private static String getYesterdayUserViewTime(Context context, long timestamp, boolean time) {


        long yesterdayTimeInMillis = TimeUtil.getYesterdayTimeInMillis();
        if (timestamp >= yesterdayTimeInMillis) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);

            String prefix = AtworkApplicationLike.baseContext.getResources().getString(R.string.yesterday);
            if (time) {
                DateFormat dateFormat = new SimpleDateFormat(TimeUtil.TIME_ONLY);
                return LanguageUtil.isZhLocal(context) ? prefix + " " + dateFormat.format(calendar.getTime()) : dateFormat.format(calendar.getTime()) + " "  + prefix;
            } else {
                return prefix;
            }
        }
        return null;
    }


    private static String getTodayUserViewTimeInChatDetail(Context context, long timestamp) {

        long todayTimeInMillis = TimeUtil.getTodayTimeInMillis();


        if (timestamp >= todayTimeInMillis) {

            return "今天";

        }

        return null;
    }

    /**
     * 时间精确至秒，1至9时需要在加零，其他不需加零。
     * 时间分段早上（5点-8点）、上午（9点-12点）、下午（13点-18点）、晚上（19点-23点）、凌晨（00点-5点）。
     *
     * @param timestamp
     * @return
     */
    private static String getTodayUserViewTime(Context context, long timestamp) {

        long todayTimeInMillis = TimeUtil.getTodayTimeInMillis();


        if (timestamp >= todayTimeInMillis) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);

            String prefix = null;
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            if (hour >= 0 && hour <= 5) {
                prefix = context.getResources().getString(R.string.early_morning);
            } else if (hour > 5 && hour <= 8) {
                prefix = context.getResources().getString(R.string.morning);
            } else if (hour > 8 && hour <= 12) {
                prefix = context.getResources().getString(R.string.later_morning);
            } else if (hour > 12 && hour <= 18) {
                prefix = context.getResources().getString(R.string.afternoon);
            } else if (hour > 18 && hour < 24) {
                prefix = context.getResources().getString(R.string.evening);
            }

            DateFormat dateFormat = new SimpleDateFormat(TimeUtil.TIME_ONLY);
            return prefix + " " + dateFormat.format(calendar.getTime());

        }

        return null;
    }


    public static String getShowDurationHavingText(long duration) {
        StringBuilder timeShow = new StringBuilder();
        long hour = duration / (1000 * 60 * 60);
        if(0 < hour) {
            timeShow.append(AtworkApplicationLike.getResourceString(R.string.hours_show, hour));
        }

        long minute = (duration / (1000 * 60)) % 60;
        timeShow.append(AtworkApplicationLike.getResourceString(R.string.minutes_show, minute));


        return timeShow.toString();

    }

    public static String getShowDuration(long duration, boolean min1000) {
        if (duration == -1) {
            return "";
        }
        //minimum is 1000 millisecond
        if(min1000 && duration < 1000) {
            duration = 1000;
        }

        StringBuilder timeShow = new StringBuilder();
        long hour = duration / (1000 * 60 * 60);
        if(0 < hour) {
            timeShow.append(hour).append(":");
        }

        long minute = (duration / (1000 * 60)) % 60;
        String minuteShow;
        if(minute < 10) {
            minuteShow = "0" + minute;
        } else {
            minuteShow = minute + "";
        }
        timeShow.append(minuteShow).append(":");


        long second = (duration / (1000)) % 60;

        String secondShow;
        if(second < 10) {
            secondShow = "0" + second;
        } else {
            secondShow = second + "";
        }

        timeShow.append(secondShow);

        return timeShow.toString();
    }
}
