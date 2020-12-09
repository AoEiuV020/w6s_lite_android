package com.foreveross.atwork.api.sdk;

/**
 * Created by dasunsy on 2017/1/17.
 */

public  interface BaseNetWorkListener<T> extends NetWorkFailListener {
    void onSuccess(T t);
}
