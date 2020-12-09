package com.foreverht.webview.x5.tbs;

import android.app.Activity;
import android.content.Intent;
import androidx.fragment.app.Fragment;

import org.apache.cordova.CordovaInterfaceImpl;
import org.apache.cordova.CordovaPlugin;

import java.util.concurrent.ExecutorService;

/**
 * Created by dasunsy on 2017/7/31.
 */

public class CordovaFragmentInterfaceImpl extends CordovaInterfaceImpl {
    Fragment fragment;
    public CordovaFragmentInterfaceImpl(Activity activity, Fragment fragment) {
        super(activity, fragment);

        this.fragment = fragment;
    }

    public CordovaFragmentInterfaceImpl(Activity activity, Fragment fragment, ExecutorService threadPool) {
        super(activity, threadPool);
        this.fragment = fragment;

    }

    @Override
    public void startActivityForResult(CordovaPlugin command, Intent intent, int requestCode) {
        setActivityResultCallback(command);
        try {
            if(null != this.fragment) {
                this.fragment.startActivityForResult(intent, requestCode);

            } else {
                this.activity.startActivityForResult(intent, requestCode);

            }
        } catch (RuntimeException e) { // E.g.: ActivityNotFoundException
            activityResultCallback = null;
            throw e;
        }
    }
}
