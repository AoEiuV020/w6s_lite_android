package com.foreveross.atwork.infrastructure.utils.encryption;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.utils.StringUtils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by dasunsy on 2016/12/5.
 *
 * 该方法已废弃,请使用 {@link AES128CBCUtil}
 */
@Deprecated
public class AES128ECBUtil_V0 {


    @NonNull
    public static Cipher getCipher(String key, int encryptMode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(encryptMode, skey);
        return cipher;
    }


    @Nullable
    public static byte[] encrypt(byte[] input, String key) {
        byte[] crypted = null;
        try {
            Cipher cipher = getCipher(key, Cipher.ENCRYPT_MODE);
            crypted = cipher.doFinal(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return crypted;

    }

    /**
     * aes+base64加密
     *
     * @param input 内容
     * @param key   加密的种子
     * @return
     */
    public static String encrypt(String input, String key) {
        byte[] crypted = encrypt(input.getBytes(), key);
        if(null != crypted) {
            return Base64Util.encode(crypted);

        } else {
            return StringUtils.EMPTY;
        }

    }

    public static byte[] decrypt(byte[] input, String key) {
        byte[] output = null;
        try {
            Cipher cipher = getCipher(key, Cipher.DECRYPT_MODE);
            output = cipher.doFinal(input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output;
    }

    /**
     * aes+base64解密
     *
     * @param input 密文
     * @param key   解密的种子
     * @return
     */
    public static String decrypt(String input, String key) {
        byte[] output = decrypt(Base64Util.decode(input), key);
        if(null != output) {
            return new String(output);

        } else {
            return StringUtils.EMPTY;
        }
    }




}
