package com.foreveross.atwork.infrastructure.webview;

import android.view.View;
import android.webkit.ValueCallback;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.Serializable;

/**
 * Created by lingen on 15/6/27.
 */
public interface AtworkWebView extends Serializable {

    /**
     * 是否可以回退页面
     * */
    boolean canGoBack();

    /**
     * 是否有回退页面
     *
     * @return
     */
    boolean backHistory();


    /**
     * 重新加载页面
     */
    void reload();

    /**
     * 加载页面
     *
     * @param url
     */
    void loadUrl(String url);

    /**
     * 设置监听器
     *
     * @param listener
     */
    void setFragmentCreateListener(OnWebViewFragmentCreate listener);

    /**
     * 监听fragment已经创建了view
     */
    interface OnWebViewFragmentCreate {
        void onFragmentCreate();
    }

    /**
     * 加载js
     */
    void loadJS(String js);

    void evaluateJavascript(String js, ValueCallback<String> callback);

    void makeKeyboardCompatible();


    String getCrawlerCoverUrl();

    void setOnSetWebTitleListener(OnSetWebUiChangeListener onSetWebUiChangeListener);

    void setWatermark(boolean always, String name, String num, String textColor, int textSize, int paddingTop, double alpha, String addValue );

    void showWatermark(boolean show);


    ProgressBar getProgressBarLoading();

    View getInflaterView();


    View getWatermarkView();

    View getVFakeStatusBar();

    void setDownloadListener(WorkplusDownloadListener downloadListener);

    View getWebView();

    View getTitleBarView();

    TextView getTitleTextView();

    ImageView getReloadImageView();

    void changeTextSize(int size);

    void setCmdFinishCheckNoGoBack(boolean cmd);

}



