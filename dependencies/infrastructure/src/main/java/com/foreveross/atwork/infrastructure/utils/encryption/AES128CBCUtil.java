package com.foreveross.atwork.infrastructure.utils.encryption;

import androidx.annotation.NonNull;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES128CBCUtil {

    private static SecretKeySpec getAESKey(byte[] keyByte) {
        return new SecretKeySpec(keyByte, "AES");
    }

    private static IvParameterSpec getAESIv(byte[] keyByte) {

        return new IvParameterSpec(keyByte);
    }

    @NonNull
    public static Cipher getCipher(byte[] keyByte, int encryptMode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(encryptMode, getAESKey(keyByte), getAESIv(keyByte));
        return cipher;
    }


    public static byte[] encrypt(String key, String data) {
        return encrypt(key.getBytes(), data.getBytes());
    }

    public static byte[] encrypt(byte[] keyByte, byte[] dataBytes) {
        try {
            Cipher cipher = getCipher(keyByte, Cipher.ENCRYPT_MODE);
            return cipher.doFinal(dataBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    public static byte[] decrypt(String key, String data) {
        return decrypt(key.getBytes(), data.getBytes());
    }

    public static byte[] decrypt(byte[] keyByte, byte[] dataByte) {
        try {
            Cipher cipher = getCipher(keyByte, Cipher.DECRYPT_MODE);
            return cipher.doFinal(dataByte);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

}
