package com.foreveross.atwork.infrastructure.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by dasunsy on 2017/5/16.
 */

public class PayHelper {
    public static boolean isAlipayUrl(String url) {
        return url.startsWith("https://mclient.alipay.com/cashier/");
    }

    public static void openDefaultSystemWeb(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        context.startActivity(intent);
    }
}
