/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2016.
 */

package org.telegram.messenger;

import android.util.Log;

public class FileLog {


    public static void e(final String tag, final String message) {
        if (!BuildVars.DEBUG_VERSION) {
            return;
        }
        Log.e(tag, message);
    }


    public static void e(final String tag, final Throwable throwable) {
        if (!BuildVars.DEBUG_VERSION) {
            return;
        }
//        Log.e(tag, throwable.getMessage());
    }
}
