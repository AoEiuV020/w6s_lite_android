package com.foreveross.atwork.modules.chat.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionTop;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;

import static com.foreveross.atwork.modules.chat.fragment.ChatListFragment.REFRESH_MESSAGE_COUNT;

/**
 * Created by dasunsy on 16/5/4.
 */
public class SessionRefreshHelper {

    public static final String MESSAGE_REFRESH = "MESSAGE_REFRESH";

    public static void notifyRefreshSessionAndCount() {
        notifyRefreshSession();
        notifyRefreshCount();
    }

    public static void notifyRefreshCount() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(REFRESH_MESSAGE_COUNT));
    }

    public static void notifyRefreshSession() {
        notifyRefreshSession(null);
    }

    public static void notifyRefreshSession(@Nullable Context context) {
        if(null == context) {
            context = BaseApplicationLike.baseContext;
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(MESSAGE_REFRESH));
    }

    public static void setAndHighlightOrgApplyingView(TextView tv, String content) {
        SpannableString spannableString = new SpannableString(content);

        String[] keys = {" Apply to join ", " 申请加入 ", " 申請加入 "};

        for (String key : keys) {
            if (content.contains(key)) {
                int startKey = content.indexOf(key);
                int endKey = content.indexOf(key) + key.length();

                spannableString.setSpan(new ForegroundColorSpan(getMainColor()), 0, startKey, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(getMainColor()), endKey, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(spannableString);

                break;
            }
        }
    }



    public static int makeCompareWith(@Nullable Object thisObj, @Nullable Long thisLastTimestamp, @Nullable Object anotherObj, @Nullable Long anotherLastTimestamp) {


        boolean thisObjIllegal = thisObj == null || !(thisObj instanceof Session);
        boolean otherObjIllegal = anotherObj == null || !(anotherObj instanceof Session);
        if (thisObjIllegal && otherObjIllegal) {
            return 0;
        }

        if (thisObjIllegal) {
            return 1;
        }

        if (otherObjIllegal) {
            return -1;
        }

        Session thisSession = (Session) thisObj;
        Session anotherSession = (Session) anotherObj;

        int thisSessionTop = getSessionTop(thisSession);
        int anotherSessionTop = getSessionTop(anotherSession);


        int result = anotherSessionTop - thisSessionTop;
        if (result == 0) {
            if (SessionTop.NONE == thisSessionTop) {
                return Session.compareToByDraft(thisSession, thisLastTimestamp, anotherSession, anotherLastTimestamp);
            }


            if (null != thisLastTimestamp && null != anotherLastTimestamp) {
                return TimeUtil.compareToReverted(thisLastTimestamp, anotherLastTimestamp);

            }
            return TimeUtil.compareToReverted(thisSession.lastTimestamp, anotherSession.lastTimestamp);

        }
        return result;
    }

    private static int getSessionTop(Session session) {
        if(SessionTop.REMOTE_TOP == session.top) {
            return SessionTop.REMOTE_TOP;
        }

        if(ChatSessionDataWrap.getInstance().isTop(session.identifier)) {
            return SessionTop.LOCAL_TOP;
        }

        return SessionTop.NONE;

    }

    private static int getMainColor() {
        return Color.parseColor("#1A98FF");
    }

}
