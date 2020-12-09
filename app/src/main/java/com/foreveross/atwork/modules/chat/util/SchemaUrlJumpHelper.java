package com.foreveross.atwork.modules.chat.util;

import android.content.Context;
import android.net.Uri;

import com.foreveross.atwork.modules.route.manager.RouteActionConsumer;
import com.foreveross.atwork.modules.route.model.RouteParams;

/**
 * Created by dasunsy on 2017/12/24.
 */

public class SchemaUrlJumpHelper {

    public static boolean handleUrl(Context context, String schemaUrl) {
        try {
            Uri uri = Uri.parse(schemaUrl.toLowerCase());
            RouteParams params = new RouteParams.Builder().uri(uri).build();
            return RouteActionConsumer.INSTANCE.route(context, params);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }



}
