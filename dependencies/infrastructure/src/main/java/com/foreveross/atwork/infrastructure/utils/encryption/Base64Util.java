package com.foreveross.atwork.infrastructure.utils.encryption;


import com.foreveross.atwork.infrastructure.utils.StringUtils;

/**
 * Created by lingen on 15/4/8.
 * Base64工具类，避免依赖其
 */
public class Base64Util {

    private final static char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

    private static int[] toInt = new int[128];

    static {
        for (int i = 0; i < ALPHABET.length; i++) {
            toInt[ALPHABET[i]] = i;
        }
    }

    /**
     * Translates the specified byte array into Base64 string.
     *
     * @param buf the byte array (not null)
     * @return the translated Base64 string (not null)
     */
    public static String encode(byte[] buf) {
        try {
            Base64.Encoder encoder = Base64.getEncoder();
            return encoder.encodeToString(buf);
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }



    public static String decode2Str(String s) {
        byte[] base64Bytes = decode(s);
        return new String(base64Bytes);

    }

    /**
     * Translates the specified Base64 string into a byte array.
     *
     * @param s the Base64 string (not null)
     * @return the byte array (not null)
     */
    public static byte[] decode(String s) {
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            return decoder.decode(s);
        } catch (Exception e) {
            return new byte[0];
        }


    }

}