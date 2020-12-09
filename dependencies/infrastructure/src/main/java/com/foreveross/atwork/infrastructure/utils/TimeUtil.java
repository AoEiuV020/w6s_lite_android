package com.foreveross.atwork.infrastructure.utils;

import android.content.Context;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * Created by lingen on 15/3/23.
 * Description:时间公共处理类
 */
public class TimeUtil {
    private static final String TAG = TimeUtil.class.getSimpleName();

    public static long timeToServer = 0;
    public static long sLastTimeCheck = -1;

    public static final String MULTIPART_YYYY_MM_DD = "yyyy-MM-dd HH:mm";
    public static final String MULTIPART_MM_DD = "MM-dd HH:mm";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String NEWSSUMMARY_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String NEWSSUMMARY_MM_DD = "MM-dd";
    private static final String MM_DD = "MM-dd";
    private static final String MM_DD_YYYY_EN = "MM/dd/yyyy";
    private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    private static final String MM_DD_YYYY_HH_MM_SS_EN = "MM/dd/yyyy HH:mm:ss";
    private static final String CHINESE_Y_M_D = "yyyy年MM月dd日";
    private static final String EN_Y_M_D = "MM/dd/yyyy";
    private static final String CHINESE_Y_M = "yyyy年MM月";
    private static final String EN_Y_M = "MM/yyyy";
    private static final String CHINESE_M_D = "M月d日";
    private static final String EN_M_D = "dd/MM";
    private static final String CHINESE_M_D_TIME = "M月d日 HH:mm";
    private static final String EN_M_D_TIME = "dd/MM HH:mm";
    public static final String TIME_ONLY = "HH:mm";
    public static final String TIME_HHH_MM_SS = "HH:mm:ss";


    /**
     * 时间校准检查偏移量, 如果检查到偏移大于该值, 说明可能用户修改了时间, 需要发心跳校准
     * */
    public static final long TIME_CHECK_OFFSET = 60 * 1000;


    /**
     * 获取时间格式类型1
     * 此类类型 中文格式返回 如 2017-01-01
     * 英文格式返回 如 01/02/2017
     *
     * @return
     */
    public static String getTimeFormat1(Context context) {
        return LanguageUtil.isZhLocal(context) ? YYYY_MM_DD : MM_DD_YYYY_EN;
    }

    /**
     * 获取时间格式类型2
     * 此类类型 中文格式返回 如 2017-01-01 11:11:11
     * 英文格式返回 如 01/02/2017 11:11:11
     *
     * @return
     */
    public static String getTimeFormat2(Context context) {
        return LanguageUtil.isZhLocal(context) ? YYYY_MM_DD_HH_MM_SS : MM_DD_YYYY_HH_MM_SS_EN;
    }

    /**
     * 获取时间格式类型3
     * 此类类型 中文格式返回 如 2017年11月11日
     * 英文格式返回 如 2/22/2017
     *
     * @return
     */
    public static String getTimeFormat3(Context context) {
        return LanguageUtil.isZhLocal(context) ? CHINESE_Y_M_D : EN_Y_M_D;
    }

    /**
     * 获取时间格式类型4
     * 此类类型 中文格式返回 如 2017年11
     * 英文格式返回 如 2/2017
     *
     * @return
     */
    public static String getTimeFormat4(Context context) {
        return LanguageUtil.isZhLocal(context) ? CHINESE_Y_M : EN_Y_M;
    }

    /**
     * 获取时间格式类型5
     * 此类类型 中文格式返回 如 5月6日
     * 英文格式返回 如 6/5
     *
     * @return
     */
    public static String getTimeFormat5(Context context) {
        return LanguageUtil.isZhLocal(context) ? CHINESE_M_D : EN_M_D;
    }

    /**
     * 获取时间格式类型5
     * 此类类型 中文格式返回 如 5月6日 22:22
     * 英文格式返回 如 6/5 22:22
     *
     * @return
     */
    public static String getTimeFormat6(Context context) {
        return LanguageUtil.isZhLocal(context) ? CHINESE_M_D_TIME : EN_M_D_TIME;
    }


    /**
     * 获取当前时间的豪秒数
     *
     * @return
     */
    public static long getCurrentTimeInMillis() {
        return System.currentTimeMillis() - timeToServer;

    }

    public static long getTodayTimeInMillis() {
        return getTodayTimeInMillis(getCurrentTimeInMillis());
    }

    public static long getTodayTimeInMillis(long timestamp) {
        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(timestamp);

        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        //今天0点
        long todayTimestamp = today.getTimeInMillis();
        return todayTimestamp;
    }

    public static long getYesterdayTimeInMillis() {
        Calendar yesterday = Calendar.getInstance();
        yesterday.setTimeInMillis(getCurrentTimeInMillis());

        yesterday.set(Calendar.HOUR_OF_DAY, 0);
        yesterday.set(Calendar.MINUTE, 0);
        yesterday.set(Calendar.SECOND, 0);
        yesterday.set(Calendar.MILLISECOND, 0);

        //昨天0点
        yesterday.add(Calendar.DAY_OF_MONTH, -1);
        long yesterdayTimestamp = yesterday.getTimeInMillis();

        return yesterdayTimestamp;
    }

    public static long getThisWeekTimeInMillis() {
        Calendar thisWeek = Calendar.getInstance();
        thisWeek.setTimeInMillis(getCurrentTimeInMillis());

        thisWeek.set(Calendar.HOUR_OF_DAY, 0);
        thisWeek.set(Calendar.MINUTE, 0);
        thisWeek.set(Calendar.SECOND, 0);
        thisWeek.set(Calendar.MILLISECOND, 0);

        int dayOfWeek = thisWeek.get(Calendar.DAY_OF_WEEK);
        thisWeek.add(Calendar.DAY_OF_MONTH, 1 - dayOfWeek);

        long thisWeekTimeStamp = thisWeek.getTimeInMillis();

        return thisWeekTimeStamp;
    }


    public static long getLastMonthTimeInMillis() {
        Calendar thisMonth = Calendar.getInstance();
        thisMonth.setTimeInMillis(getCurrentTimeInMillis());

        thisMonth.setTime(new Date());
        thisMonth.add(Calendar.MONTH, -1);

        long thisMonthTimeStamp = thisMonth.getTimeInMillis();

        return thisMonthTimeStamp;
    }

    public static long getThisYearTimeInMillis() {
        Calendar thisYear = Calendar.getInstance();
        thisYear.setTimeInMillis(getCurrentTimeInMillis());

        thisYear.set(Calendar.MONTH, 0);
        thisYear.set(Calendar.DAY_OF_MONTH, 0);
        thisYear.set(Calendar.HOUR_OF_DAY, 0);
        thisYear.set(Calendar.MINUTE, 0);
        thisYear.set(Calendar.SECOND, 0);
        thisYear.set(Calendar.MILLISECOND, 0);
        thisYear.add(Calendar.DAY_OF_MONTH, 1);

        long thisYearTimeStamp = thisYear.getTimeInMillis();

        return thisYearTimeStamp;
    }

    public static Long calculateBetweenTwoMonth(long date1, long date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Calendar bef = Calendar.getInstance();
        Calendar aft = Calendar.getInstance();
        bef.setTimeInMillis(date1);
        aft.setTimeInMillis(date2);
        int result = aft.get(Calendar.MONTH) - bef.get(Calendar.MONTH);
        int month = (aft.get(Calendar.YEAR) - bef.get(Calendar.YEAR)) * 12;
        return (long) Math.abs(month + result);
    }



    /**
     * 获取一定天数之前的豪秒数
     *
     * @param day
     * @return
     */
    public static long getTimeInMillisDaysBeforeDebug(int day) {
        if(CustomerHelper.isTest(BaseApplicationLike.baseContext)) {
            return TimeUtil.getCurrentTimeInMillis() - day * 60 * 1000L;
        }

        return TimeUtil.getCurrentTimeInMillis() - day * 24 * 60 * 60 * 1000L;
    }

    /**
     * 获取一定天数之前的豪秒数
     *
     * @param day
     * @return
     */
    public static long getTimeInMillisDaysBefore(int day) {

        return TimeUtil.getCurrentTimeInMillis() - day * 24 * 60 * 60 * 1000L;
    }


    public static boolean isOverdueDate(long currentDate, long overdueDate) {
        if (overdueDate <= 0) {
            return false;
        }
        return currentDate > overdueDate;
    }

    /**
     * 获取一定天数之后的豪秒数
     *
     * @param day
     * @return
     */
    public static long getTimeInMillisDaysAfter(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getCurrentTimeInMillis());
        calendar.add(Calendar.DAY_OF_MONTH, (day));
        return calendar.getTimeInMillis();
    }


    /**
     * 返回豪秒的时间数
     *
     * @param millis
     * @return
     */
    public static String getStringForMillis(long millis, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        return dateFormat.format(c.getTime());
    }

    public static String getLinuxDateForDateSting(String dateString) {
        if (TextUtils.isEmpty(dateString)) {
            return "";
        }
        String year = dateString.substring(0, 4);
        String month = dateString.substring(4, 6);
        String day = dateString.substring(6, 8);
        String hour = dateString.substring(8, 10);
        String minute = dateString.substring(10, 12);
        String second = dateString.substring(12, 14);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(year).append("-").append(month).append("-").append(day).append(" ").append(hour).append(":").append(minute);
        return stringBuilder.toString();
    }

    public static String isSameMonth(Context context, long currentTime, String compare) {
        DateFormat dateFormat = new SimpleDateFormat(getTimeFormat4(context));
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(currentTime);
        String formateDate = dateFormat.format(c.getTime());
        if (formateDate.equalsIgnoreCase(compare)) {
            return LanguageUtil.isZhLocal(context) ? "本月" : "This Month";
        }
        return compare;
    }

    public static int compareTo(long lhs, long rhs) {
        Calendar cLhs = Calendar.getInstance();
        Calendar cRhs = Calendar.getInstance();
        cLhs.setTimeInMillis(lhs);
        cRhs.setTimeInMillis(rhs);
        return cLhs.compareTo(cRhs);
    }

    public static int compareToReverted(long lhs, long rhs) {
        return compareTo(rhs, lhs);
    }


    /**
     * 系统时间发生了偏移
     * */
    public static boolean isTimeNeedCheck() {
        long nowTime = System.currentTimeMillis();
        boolean isTimeNeedCheck = -1 != sLastTimeCheck && Math.abs(nowTime - sLastTimeCheck) > TIME_CHECK_OFFSET;
        sLastTimeCheck = nowTime;
        return isTimeNeedCheck;
    }

    public static boolean isSameDate(long date1, long date2) {
        try {
            Calendar d1 = Calendar.getInstance();
            d1.setTimeInMillis(date1);
            Calendar d2 = Calendar.getInstance();
            d2.setTimeInMillis(date2);
            return d1.get(Calendar.YEAR) == d2.get(Calendar.YEAR) && d1.get(Calendar.MONTH) == d2.get(Calendar.MONTH) && d1.get(Calendar.DAY_OF_MONTH) == d2.get(Calendar.DAY_OF_MONTH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static String generateTime(int time) {
        int totalSeconds =  (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

}
