package com.foreveross.atwork.infrastructure.utils.encryption;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by lingen on 14-4-28.
 */
public class MD5Utils {
    private static final String MD5 = "MD5";


    /**
     * 计算一个文件的md5值
     *
     * @return
     */
    public static String getDigest(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        return getDigest(new File(filePath));
    }

    /**
     * 计算一个文件的md5值
     *
     * @return
     */
    public static String getDigest(File file) {
        String fileMd5 = "";
        if (file == null || !file.exists()) {
            return fileMd5;
        }
        try {
            MessageDigest messageDigest = MessageDigest
                    .getInstance(MD5);
            FileInputStream in = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                messageDigest.update(buffer, 0, len);
            }
            in.close();
            fileMd5 = hex(messageDigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileMd5;
    }

    @Deprecated
    public static String hex(String str) {
        return hex(str.getBytes());
    }

    public static String hex(final byte[] bytes) {
        final StringBuffer buffer = new StringBuffer(bytes.length * 2);
        for (final byte element : bytes) {
            if ((element & 0xff) < 0x10) {
                buffer.append("0");
            }
            buffer.append(Long.toString(element & 0xff, 16));
        }
        return buffer.toString();
    }

    public static String md5ToBase64(String str) {
        return Base64Util.encode(md5(str));
    }

    public static byte[] md5(String str) {
        if (TextUtils.isEmpty(str)) {
            return new byte[0];
        }
        MessageDigest md5;

        try {
            md5 = MessageDigest.getInstance(MD5);
            byte[] bytes = md5.digest(str.getBytes());

            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    public static String encoderByMd5(String str){
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance(MD5);
            byte[] bytes = md5.digest(str.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
