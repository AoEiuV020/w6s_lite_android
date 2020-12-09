package com.foreveross.atwork.api.sdk.Employee.requestModel;

/**
 * create by reyzhang22 at 2019-08-19
 */
public class CallerRequester {

    public static final int REQUEST_CALLER_LIMIT = 5000;

    public String mUserId = "";

    public int mSkip = 0;

    public int mLimit = REQUEST_CALLER_LIMIT;

    public long mRefreshTime = -1;
}
