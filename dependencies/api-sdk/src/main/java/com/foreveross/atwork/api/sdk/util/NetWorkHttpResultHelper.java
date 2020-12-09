    package com.foreveross.atwork.api.sdk.util;/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 */


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by reyzhang22 on 16/3/25.
 */
public class NetWorkHttpResultHelper  {

    public static String getResultText(String httpResult) {
        String result = "";
        try {
            JSONObject jsonObject = new JSONObject(httpResult);
            result = jsonObject.optString("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int getResultStatus(String httpResult) {
        int status = -1;
        try {
            JSONObject jsonObject = new JSONObject(httpResult);
            status = jsonObject.optInt("status");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    public static String getResultMessage(String httpResult) {
        String message = "";
        try {
            JSONObject jsonObject = new JSONObject(httpResult);
            message = jsonObject.optString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message;
    }
}
