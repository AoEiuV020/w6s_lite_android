package com.foreveross.atwork.infrastructure.model.dropbox;


public class ShareItemRequester {

    public static final int REQUEST_LIMIT = 10;

    public String mItemId;

    public String mKw;

    public int mSkip = 0;

    public int mLimit = REQUEST_LIMIT;
}
