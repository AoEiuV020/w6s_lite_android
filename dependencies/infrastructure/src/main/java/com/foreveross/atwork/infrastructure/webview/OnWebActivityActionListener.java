package com.foreveross.atwork.infrastructure.webview;

import android.content.Context;

import java.io.Serializable;

/**
 * Created by lingen on 15/6/27.
 */
public interface OnWebActivityActionListener extends Serializable {

    void showCloseView();

    void hiddenCloseView();

    boolean handleSchemaUrlJump(Context context, String url);

    void registerShake();

    void unregisterShake();


}
