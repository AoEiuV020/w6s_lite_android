package com.foreveross.atwork.infrastructure.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileRangeUtil {
    long start;
    long end;
    long length;
    long total;

    /**
     * Construct a byte range.
     *
     * @param start Start of the byte range.
     * @param end   End of the byte range.
     * @param total Total length of the byte source.
     */
    public FileRangeUtil(long start, long end, long total) {
        this.start = start;
        this.end = end;
        this.length = end - start + 1;
        this.total = total;
    }

    public static long sublong(String value, int beginIndex, int endIndex) {
        String substring = value.substring(beginIndex, endIndex);
        return (substring.length() > 0) ? Long.parseLong(substring) : -1;
    }

    public static void copy(InputStream input, OutputStream output, long inputSize, long start, long length) throws IOException {
        byte[] buffer = new byte[1024];
        int read;

        if (inputSize == length) {
            // Write full range.
            while ((read = input.read(buffer)) > 0) {
                output.write(buffer, 0, read);
//                output.flush();
            }
        } else {
            input.skip(start);
            long toRead = length;//1025
            //1

            while ((read = input.read(buffer)) > 0) {
                if ((toRead -= read) > 0) {
                    output.write(buffer, 0, read);
//                    output.flush();
                } else {
                    output.write(buffer, 0, (int) toRead + read);
//                    output.flush();
                    break;
                }
            }
        }
    }
}