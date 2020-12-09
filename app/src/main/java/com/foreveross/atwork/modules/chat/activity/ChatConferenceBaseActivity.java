package com.foreveross.atwork.modules.chat.activity;/**
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                       __           __
 .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 |________|_____|__|  |__|__|   __||__||_____|_____|
                            |__|
 */


import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * voip 基类页面
 * Created by reyzhang22 on 16/2/3.
 */
public abstract class ChatConferenceBaseActivity extends SingleFragmentActivity {

//    //刷新与会者列表
//    protected static final int UPDATE_CONFERENCE_MEMBER = 101;
//    //更新会议时间
//    protected static final int UPDATE_CONF_TIME = 103;
//    // 结束会议的标志
//    protected static final int END_CONFERENCE_TYPE = 104;
//    // 退出会议的标志
//    protected static final int EXIT_CONFERENCE_TYPE = 105;
//    // 发送退出会议的请求回来后的推送
//    protected static final int EXIT_TYPE_PUSH = 106;
//    // 结束activity
//    protected static final int END_ACTIVITY = 107;
//
//    //会议实体
//    protected com.foreverht.workplus.espacelib.espace.ConferenceEntity conference;
//
//
//    protected ButtonBarView buttonBar;
//    //更多
//    protected ButtonControl moreButton;
//    //底部五个菜单之一的“共享数据”菜单
//    protected ButtonControl shareViewButton;
//
//    //会议时间
//    protected TextView timeView;
//
//    private List<Dialog> dialogList = new ArrayList<Dialog>();
//
//    /**
//     * 更新会议时间
//     */
//    public void updateTime() {
//        if (conference != null && 0 != conference.getTimeCount() && null != timeView) {
//            timeView.setText(CommonUtil.getTime(conference.getTimeCount()));
//        }
//    }
//
//    public void setPopWindowNormal() {
//        if (null != moreButton && buttonBar.indexOf(moreButton) != -1) {
//            moreButton.updateStatus();
//        }
//    }
//
//    /**
//     * [方法的功能描述]初始化“退出会议”菜单
//     */
//    public void initExitButton() {
//        showExitConfDialog();
//    }
//
//    /**
//     * [方法的功能描述]初始化“共享多媒体数据”菜单
//     */
//    public void initShareViewButton() {
//        shareViewButton = new ButtonControl(ButtonRes.SHAREVIEW_BTN) {
//            @Override
//            public void onControlAction() {
//                setPopWindowNormal();
//                shareButtonClicked();
//            }
//
//            @Override
//            public void onStateChange() {
//                if (needShareConfButton()) {
//                    changeMainRes(ButtonRes.SHARING_INDEX);
//                } else {
//                    changeMainRes(ButtonRes.NOSHARE_INDEX);
//                }
//
//                if (isCurConfCreating() || isCurConfCreated()) {
//                    disableControl();
//                    return;
//                }
//
//                normalControl();
//            }
//        };
//    }
//
//    protected abstract void shareButtonClicked();
//
//    /**
//     * 显示退会的对话框
//     */
//    private void showExitConfDialog() {
//        BaseDialog dialog;
//        View.OnClickListener endConf = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showExitConfConfirm(END_CONFERENCE_TYPE);
//            }
//        };
//        View.OnClickListener exitConf = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showExitConfConfirm(EXIT_CONFERENCE_TYPE);
//            }
//        };
//        if (isConfControlEnable()) {
//            ConferenceMemberEntity entity = conference.getSelfInConf();
//            if ((null != entity)
//                    && (entity.getStatus() == ConferenceMemberEntity.STATUS_LEAVE_CONF)) {
//                dialog = new ConfirmDialog(this, R.string.caller_ctc_quit);
//                dialog.setRightButtonListener(endConf);
//            } else {
//                dialog = new TripleDialog(this);
//                dialog.setLeftButtonListener(endConf);
//                dialog.setRightButtonListener(exitConf);
//            }
//        } else {
//            dialog = new ConfirmDialog(this, R.string.ctc_quit);
//            dialog.setRightButtonListener(exitConf);
//        }
//        dialog.show();
//    }
//
//    private void showExitConfConfirm(final int type) {
//        String message = END_CONFERENCE_TYPE == type
//                ? getString(R.string.note_over_conf) : getString(R.string.note_exit_conf);
//        ConfirmDialog dialog = new ConfirmDialog(this, message);
//        dialog.setRightButtonListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                exitConference(type);
//            }
//        });
//        dialog.show();
//    }
//
//    public void addDialog(Dialog dialog) {
//        dialogList.add(dialog);
//    }
//
//    /**
//     * 关闭并释放对话框
//     */
//    public void clearDialog() {
//        if (dialogList == null || dialogList.isEmpty()) {
//            return;
//        }
//
//        for (Dialog dialog : dialogList) {
//            if (dialog != null) {
//                dialog.dismiss();
//            }
//        }
//
//        dialogList = null;
//    }
//
//    /**
//     * 判断当前会议的ConfId是否为空
//     *
//     * @return 为空，返回true；否之，返回false
//     */
//    public boolean isCurConfCreating() {
//        return StringUtil.isStringEmpty(conference.getMeetingId());
//    }
//
//    /**
//     * 判断当前会议的状态是否为STATUS_CREATED
//     *
//     * @return 是，返回true；否之，返回false
//     */
//    public boolean isCurConfCreated() {
//        return conference.getStatus() == com.foreverht.workplus.espacelib.espace.ConferenceEntity.STATUS_CREATED;
//    }
//
//    public boolean isMcu() {
//        return conference != null && conference.isMcu();
//    }
//
//    public boolean isConfControlEnable() {
//        return conference != null && conference.isConfControlEnable();
//    }
//
//    public List<ConferenceMemberEntity> getConfMemList() {
//        // 用于适配器数据的与会成员列表，以免数据更新对list适配器照成影响
//        List<ConferenceMemberEntity> confMemberList = new ArrayList<ConferenceMemberEntity>();
//
//        if (conference != null) {
//            confMemberList.addAll(conference.getConfMemberList());
//        }
//
//        return confMemberList;
//    }
//
//    private boolean needShareConfButton() {
//        int shareStatus = ConferenceFunc.getIns().getSharedStatus(
//                conference.getMeetingId());
//        return isInDataConf() && shareStatus != ConferenceFunc.SHARE_DATA_STOP;
//    }
//
//    /**
//     * 判断是否具有升级多媒体会议权限与当前会议是否是多媒体会议
//     *
//     * @return 同时满足，返回true；否之，返回false
//     */
//    public boolean isInDataConf() {
//        return true;
//    }
//
//    /**
//     * [方法的功能描述] 与会者退会流程处理
//     *
//     * @param exitType 退会的类型
//     */
//    public void exitConference(int exitType) {
//        switch (exitType) {
//            case END_CONFERENCE_TYPE:
//                ConferenceFunc.getIns().endDataConference(
//                        conference.getMeetingId());
//                break;
//            case EXIT_CONFERENCE_TYPE:
//                ConferenceFunc.getIns().leaveDataConference(
//                        conference.getMeetingId());
//                finish();
//                break;
//            case EXIT_TYPE_PUSH:
//                break;
//
//            default:
//                break;
//        }
//    }
//
//
}
