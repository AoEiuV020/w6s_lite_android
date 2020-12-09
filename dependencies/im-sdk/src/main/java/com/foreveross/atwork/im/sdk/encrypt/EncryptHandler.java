package com.foreveross.atwork.im.sdk.encrypt;

import android.content.Context;
import android.content.SharedPreferences;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.shared.EndPointInfo;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.EncryptSharedPrefsUtils;
import com.foreveross.atwork.infrastructure.utils.encryption.AES128CBCUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by dasunsy on 2017/7/10.
 */

public class EncryptHandler {


    public static byte[] encrypt(byte[] msgRawBytes) {
        Context context = BaseApplicationLike.baseContext;
        byte[] secret = EndPointInfo.getInstance().getCurrentEndpointInfo(context).mSecret.getBytes();

        MessageDigest digest;
        byte[] keyByte = new byte[0];
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(secret);
            keyByte = digest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return AES128CBCUtil.encrypt(keyByte, msgRawBytes);
    }

    public static byte[] decrypt(byte[] msgEncryptByte) {
        Context context = BaseApplicationLike.baseContext;
        byte[] secret = EndPointInfo.getInstance().getCurrentEndpointInfo(context).mSecret.getBytes();

        MessageDigest digest;
        byte[] keyByte = new byte[0];
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(secret);
            keyByte = digest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return AES128CBCUtil.decrypt(keyByte, msgEncryptByte);
    }
}
