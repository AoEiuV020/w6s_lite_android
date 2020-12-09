package com.foreveross.atwork.manager.model;

import com.foreveross.atwork.api.sdk.net.HttpResult;

/**
 * Created by dasunsy on 16/6/5.
 */
public class MultiResult<T> {
    public HttpResult httpResult;
    public T localResult;
}
