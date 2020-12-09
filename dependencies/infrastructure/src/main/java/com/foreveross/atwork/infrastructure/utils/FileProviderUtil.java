package com.foreveross.atwork.infrastructure.utils;

import android.content.Intent;

/**
 * Created by dasunsy on 2017/2/26.
 */

public class FileProviderUtil {


    public static void grantRWPermission(Intent intent) {
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    }

}
