package com.foreveross.atwork.modules.main.model;

import com.foreveross.atwork.AtworkApplicationLike;

/**
 * Created by dasunsy on 2017/12/10.
 */

public class MainFabBottomPopItem {

    public MainFabBottomAction mMainFabBottomAction;

    public String mTitle;

    public int mResId;

    public static MainFabBottomPopItem newInstance() {
        return new MainFabBottomPopItem();
    }

    public MainFabBottomPopItem setMainFabBottomAction(MainFabBottomAction mainFabBottomAction) {
        mMainFabBottomAction = mainFabBottomAction;
        return this;
    }

    public MainFabBottomPopItem setTitle(int titleResId) {
        return setTitle(AtworkApplicationLike.getResourceString(titleResId));
    }

    public MainFabBottomPopItem setTitle(String title) {
        mTitle = title;
        return this;
    }

    public MainFabBottomPopItem setResId(int resId) {
        mResId = resId;
        return this;
    }

    public enum MainFabBottomAction {

        /**
         * 新建群聊
         */
        NEW_DISCUSSION_CHAT,

        /**
         * 语音聊天
         * */
        VOIP_CALL,

        /**
         * 会议呼叫(umeeting, 全时云等)
         * */
        MEETING_CALL,

        /**
         * 扫一扫
         * */
        SCAN_QR_CODE,


        /**
         * 新建组织
         * */
        CREATE_NEW_ORG,


        /**
         * 申请加入组织
         * */
        APPLY_TO_JOIN_ORG,


        /**
         * 添加好友
         * */
        ADD_FRIEND,


        /**
         * 打开联系人界面
         * */
        CONTACT

    }
}
