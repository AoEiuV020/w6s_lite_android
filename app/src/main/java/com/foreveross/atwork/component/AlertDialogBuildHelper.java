package com.foreveross.atwork.component;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.foreveross.atwork.infrastructure.utils.StringUtils;

/**
 * Created by lingen on 15/5/27.
 * Description:
 */
public class AlertDialogBuildHelper {

    private AlertDialogBuildHelper() {

    }

    public static final AlertDialog.Builder createInstance(Context context, String message, String cancel, String ok, DialogInterface.OnClickListener clickListener) {

        return createInstance(context, message, cancel, null, ok, clickListener);
    }

    public static final AlertDialog.Builder createInstance(Context context, String message, String negative, String neutral, String positive
                                                          , DialogInterface.OnClickListener clickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);

        if (!StringUtils.isEmpty(negative)) {
            builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        if (!StringUtils.isEmpty(positive)) {
            builder.setPositiveButton(positive, clickListener);
        }

        if (!StringUtils.isEmpty(neutral)) {
            builder.setNeutralButton(neutral, clickListener);
        }

        return builder;
    }

}
