package com.foreveross.atwork.infrastructure.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dasunsy on 15/9/09.
 */
public class LogUtil {

    private LogUtil() {

    }

    public final static String TAG = "WORKPLUS";

    private final static String START_FLAG = "----------------------- START " + TAG + " -----------------------";


    private final static String END_FLAG = "----------------------- END " + TAG + " -----------------------";

    private static final int CHUNK_SIZE = 4000;


    public static boolean sIsDebug = true;

    public static void v(String text) {
        v(TAG, text);
    }

    public static void d(String text) {
        d(TAG, text);
    }

    public static void i(String text) {
        i(TAG, text);
    }

    public static void w(String text) {
        w(TAG, text);
    }


    public static void e(String text) {
        e(TAG, text);
    }

    public static void e_startTag() {
        e_startTag(TAG);
    }

    public static void e_endTag() {
        e_endTag(TAG);
    }

    public static void e_startTag(String tag) {
        e(tag, " ");
        e(tag, START_FLAG);
        e(tag, " ");
    }

    public static void e_endTag(String tag) {
        e(tag, " ");
        e(tag, END_FLAG);
        e(tag, " ");

    }

    public static void w_startTag() {
        w_startTag(TAG);
    }

    public static void w_endTag() {
        w_endTag(TAG);
    }

    public static void w_startTag(String tag) {
        w(tag, " ");
        w(tag, START_FLAG);
        w(tag, " ");

    }

    public static void w_endTag(String tag) {
        w(tag, " ");
        w(tag, END_FLAG);
        w(tag, " ");
    }

    public static void i_startTag() {
        i_startTag(TAG);
    }

    public static void i_endTag() {
        i_endTag(TAG);
    }

    public static void i_startTag(String tag) {
        i(tag, " ");
        i(tag, START_FLAG);
        i(tag, " ");

    }

    public static void i_endTag(String tag) {
        i(tag, " ");
        i(tag, END_FLAG);
        i(tag, " ");
    }

    public static void d_startTag() {
        d_startTag(TAG);
    }

    public static void d_endTag() {
        d_endTag(TAG);
    }

    public static void d_startTag(String tag) {
        d(tag, " ");
        d(tag, START_FLAG);
        d(tag, " ");

    }

    public static void d_endTag(String tag) {
        d(tag, " ");
        d(tag, END_FLAG);
        d(tag, " ");

    }

    public static void v_startTag() {
        v_startTag(TAG);
    }

    public static void v_endTag() {
        v_endTag(TAG);
    }

    public static void v_startTag(String tag) {
        v(tag, " ");
        v(tag, START_FLAG);
        v(tag, " ");
    }

    public static void v_endTag(String tag) {
        v(tag, " ");
        v(tag, END_FLAG);
        v(tag, " ");
    }


    public static void init(boolean isDebug) {
        sIsDebug = isDebug;
    }

    public static void v(String tag, String text) {
        if (sIsDebug) {
            log(Log.VERBOSE, tag, text);
        }
    }


    public static void d(String tag, String text) {
        if (sIsDebug) {
            log(Log.DEBUG, tag, text);

        }
    }


    public static void i(String tag, String text) {
        if (sIsDebug) {
            log(Log.INFO, tag, text);

        }
    }


    public static void w(String tag, String text) {
        if (sIsDebug) {
            log(Log.WARN, tag, text);
        }
    }

    public static void w(String tag, String text, Throwable t) {
        if (sIsDebug) {
            if (null == text) {
                Log.w(tag, "null", t);
            } else {

                Log.w(tag, text, t);
            }
        }
    }


    public static void e(String tag, String text) {
        if (sIsDebug) {
            log(Log.ERROR, tag, text);
        }
    }

    public static void e(String tag, String text, Throwable t) {
        if (sIsDebug) {
            if (null == text) {
                Log.e(tag, "null", t);
            } else {

                Log.e(tag, text, t);
            }
        }

    }

    private static void log(int level, String tag, String text) {
        if (null == text) {
            text = "null";
        } else {
            text = jsonFormat(text);
        }


        for (int i = 0; i < text.length(); i += CHUNK_SIZE) {
            int count = Math.min(text.length(), CHUNK_SIZE * (i + 1));
            logInternal(level, tag, text.substring(i, count));
        }
    }

    private static void logInternal(int level, String tag, String text) {
        if (Log.VERBOSE == level) {
            Log.v(tag, text);

        } else if (Log.DEBUG == level) {
            Log.d(tag, text);

        } else if (Log.INFO == level) {
            Log.i(tag, text);

        } else if (Log.WARN == level) {
            Log.w(tag, text);

        } else if (Log.ERROR == level) {
            Log.e(tag, text);
        }

    }


    public static String jsonFormat(String text) {
        String message;
        try {
            if (text.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(text);
                message = jsonObject.toString(4);
            } else if (text.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(text);
                message = jsonArray.toString(4);
            } else {
                message = text;
            }
        } catch (JSONException e) {
            message = text;
        }

        return message;
    }
}
