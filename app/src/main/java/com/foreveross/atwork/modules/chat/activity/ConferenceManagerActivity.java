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


import androidx.fragment.app.Fragment;

/**
 * 视频聊天会议管理界面
 * Created by reyzhang22 on 16/2/16.
 */
public class ConferenceManagerActivity extends ChatConferenceBaseActivity {
    @Override
    protected Fragment createFragment() {
        return null;
    }

//    private static final String TAG = ConferenceManagerActivity.class.getSimpleName();
//
//    private Bundle mBundle;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        mBundle = getSelectIntent().getBundleExtra(CommonUtil.INTENT_TRANSFER_KEY);
//        if (null == mBundle) {
//            return;
//        }
//        Object confData = mBundle.getSerializable(IntentData.CONFERENCE_ENTITY);
//        if (confData instanceof ConferenceEntity) {
//            conference = (ConferenceEntity) confData;
//            ConferenceFunc.getIns().requestCreateConference(conference);
//        } else {
//            String confId = mBundle.getString(IntentData.MEETING_ID);
//
//            if (null != confId) {
//                conference = ConferenceDataHandler.getIns().getConference(confId);
//            }
//        }
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    protected void shareButtonClicked() {
//
//    }
//
//    @Override
//    protected Fragment createFragment() {
//        ConferenceManagerFragment fragment = new ConferenceManagerFragment();
//        fragment.setArguments(mBundle);
//        return fragment;
//    }
}
