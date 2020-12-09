package com.foreveross.atwork.infrastructure.utils;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by dasunsy on 2017/2/22.
 */

public class IOUtil {

    public static void release(@Nullable InputStream inputStream) {
        if(null != inputStream) {
            try {
                inputStream.close();
                inputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void release(@Nullable OutputStream outputStream) {
        if(null != outputStream) {
            try {
                outputStream.close();
                outputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * code from apache copy
     * */
    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        return count > 2147483647L?-1:(int)count;
    }

    public static long copyLarge(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[4096];
        long count = 0L;

        int n;
        for(boolean var5 = false; -1 != (n = input.read(buffer)); count += (long)n) {
            output.write(buffer, 0, n);
        }

        return count;
    }
}
