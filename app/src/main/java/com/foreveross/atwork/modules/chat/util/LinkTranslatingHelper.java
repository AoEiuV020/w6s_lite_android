package com.foreveross.atwork.modules.chat.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;

import com.foreveross.atwork.api.sdk.message.MessageAsyncNetService;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleItem;
import com.foreveross.atwork.infrastructure.utils.PatternUtils;

import java.util.UUID;

/**
 * Created by dasunsy on 2017/9/4.
 */

public class LinkTranslatingHelper {
    public static String sCurrentMatchingId;


    public static void handleLinkMatch(Context context, String value, LinkMatchListener linkMatchListener) {
        if (null == linkMatchListener || BurnModeHelper.isBurnMode()) {
            return;
        }

        sCurrentMatchingId = UUID.randomUUID().toString();
        linkMatchListener.hidePopView();

        new Handler().postDelayed(new LinkMatchRunnable(context, sCurrentMatchingId, value, linkMatchListener), 800);
    }

    private static class LinkMatchRunnable implements Runnable {
        private Context mContext;
        private String mRunnableMatchingId;
        private String mMatchingUrl;
        private LinkMatchListener mLinkMatchListener;
        private int mErrorTime = 0;

        public LinkMatchRunnable(Context context, String runnableMatchingId, String matchingUrl, LinkMatchListener linkMatchListener) {
            mContext = context;
            mRunnableMatchingId = runnableMatchingId;
            mMatchingUrl = matchingUrl;
            mLinkMatchListener = linkMatchListener;
            mErrorTime = 0;
        }


        @SuppressLint("StaticFieldLeak")
        @Override
        public void run() {
            makeTranslateLink();
        }

        private void dismissNotMatchView() {
            new Handler().postDelayed(() -> {
                if (isLegal()) {
                    mLinkMatchListener.hidePopView();
                    mErrorTime = 0;
                }
            }, 2000);

        }

        private boolean isLegal() {
            return null != sCurrentMatchingId && sCurrentMatchingId.equals(mRunnableMatchingId);
        }

        private void makeTranslateLink() {
            if (isLegal()) {
                if (PatternUtils.isUrlLink(mMatchingUrl)) {
                    mLinkMatchListener.matching();
                    MessageAsyncNetService.parseUrlForShare(mContext, mMatchingUrl, new MessageAsyncNetService.OnParseUrlForShareListener() {
                        @Override
                        public void onParseSuccess(ArticleItem articleItem) {
                            if (isLegal()) {
                                mLinkMatchListener.showMatchedResult(articleItem);
                                mErrorTime = 0;
                            }
                        }


                        @Override
                        public void networkFail(int errorCode, String errorMsg) {

                            if(!isLegal()) {
                                return;
                            }

                            if (mErrorTime < 3) {
                                mErrorTime++;
                                makeTranslateLink();
                                return;
                            }

                            mLinkMatchListener.showNotMatch();
                            dismissNotMatchView();
                            return;
                        }
                    });
                }
            }

        }
    }


    public interface LinkMatchListener {
        void hidePopView();

        void matching();

        void showMatchedResult(ArticleItem articleItem);

        void showNotMatch();
    }
}
