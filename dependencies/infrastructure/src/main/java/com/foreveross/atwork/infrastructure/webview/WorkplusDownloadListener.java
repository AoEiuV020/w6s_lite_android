package com.foreveross.atwork.infrastructure.webview;

/**
 * Created by dasunsy on 2018/1/9.
 */

public interface WorkplusDownloadListener {
    void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength);
}
