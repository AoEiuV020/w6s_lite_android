package com.foreveross.atwork.cordova.plugin;


import android.util.Base64;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by wuzjie on 2020/1/02.
 */

public class HTMemberPlugin extends CordovaPlugin {

    /**
     * 获取签名信息
     */
    private static final String GET_SIGNATURE = "getSignature";


    @Override
    public boolean execute(String action, String rawArgs, CallbackContext callbackContext) throws JSONException {
        if(GET_SIGNATURE.equals(action)) {

            String token = "3af8809e979b44968052a0762d6de0de";
            //获取当前时间戳
            String timeStamp = System.currentTimeMillis()+"";
            //获取随机数
            String nonce = System.currentTimeMillis()+"";
            //业务系统标识
            String channelId = "caedcaccabbdd41ae910666cc3c81b160";
            String signature = getGetSignature(token, timeStamp, nonce, channelId);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("channel_id", channelId);
            jsonObject.put("signature", signature);
            jsonObject.put("nonce", nonce);
            jsonObject.put("timestamp", timeStamp);
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
            callbackContext.sendPluginResult(pluginResult);
            callbackContext.success();
            return true;
        }

        return false;
    }

    public static String getGetSignature(String token, String timeStamp, String nonce, String channelId) {

        //签名算法中的secret
        String secret = channelId+token;

        String[] array = {token, timeStamp, nonce};
        Arrays.sort(array);
        StringBuffer stringBuffer = new StringBuffer();
        for(int i= 0; i < array.length; i++){
            if(i != 0){
                stringBuffer.append("&");
            }
            stringBuffer.append(array[i]);
        }
        String strArray = stringBuffer.toString();

        String reString = "";
        //hmac算法加密
        try{
            byte[] data = secret.getBytes("UTF-8");
            SecretKey secretKey = new SecretKeySpec(data, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(secretKey);

            byte[] text = strArray.getBytes("UTF-8");
            byte[] textMac = mac.doFinal(text);
            reString = Base64.encodeToString(textMac, Base64.NO_WRAP);
        }catch (Exception e){

        }
        return reString;
    }
}
