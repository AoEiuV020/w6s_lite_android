package com.foreveross.atwork.modules.chat.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.cache.DiscussionCache;
import com.foreverht.cache.WatermarkCache;
import com.foreverht.db.service.repository.WatermarkRepository;
import com.foreverht.workplus.module.chat.activity.BaseMessageHistoryActivity;
import com.foreverht.workplus.module.chat.activity.MessageHistoryActivity;
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener;
import com.foreveross.atwork.api.sdk.BaseNetWorkListener;
import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.ConversionConfigSettingItem;
import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.ConversionConfigSettingParticipant;
import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.request.ConversionConfigSettingRequest;
import com.foreveross.atwork.api.sdk.discussion.DiscussionAsyncNetService;
import com.foreveross.atwork.api.sdk.discussion.requestJson.DiscussionSettingsRequest;
import com.foreveross.atwork.api.sdk.discussion.responseJson.DiscussionSettingsResponse;
import com.foreveross.atwork.api.sdk.message.MessageAsyncNetService;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.api.sdk.users.UserSyncNetService;
import com.foreveross.atwork.component.NotScrollGridView;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.component.WorkplusSwitchCompat;
import com.foreveross.atwork.db.daoService.DiscussionDaoService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.manager.OrganizationSettingsManager;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.Watermark;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.LightApp;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.discussion.DiscussionMember;
import com.foreveross.atwork.infrastructure.model.discussion.template.DiscussionFeature;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.dropbox.DropboxConfig;
import com.foreveross.atwork.infrastructure.model.setting.BusinessCase;
import com.foreveross.atwork.infrastructure.model.setting.ConfigSetting;
import com.foreveross.atwork.infrastructure.model.setting.SourceType;
import com.foreveross.atwork.infrastructure.model.user.SelectedContactList;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.support.AtworkConstants;
import com.foreveross.atwork.infrastructure.utils.ContactHelper;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.modules.app.manager.AppManager;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.manager.DropboxConfigManager;
import com.foreveross.atwork.manager.OnlineManager;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.manager.listener.BaseQueryListener;
import com.foreveross.atwork.manager.model.SetReadableNameParams;
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity;
import com.foreveross.atwork.modules.chat.activity.ChatInfoActivity;
import com.foreveross.atwork.modules.chat.activity.DiscussionQrcodeActivity;
import com.foreveross.atwork.modules.chat.activity.IntelligentTranslationActivity;
import com.foreveross.atwork.modules.chat.activity.SearchChatContentActivity;
import com.foreveross.atwork.modules.chat.adapter.CanOperationType;
import com.foreveross.atwork.modules.chat.adapter.ChatInfoUserListBaseAdapter;
import com.foreveross.atwork.modules.chat.dao.ChatDaoService;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.model.TranslateLanguageType;
import com.foreveross.atwork.modules.chat.service.ChatService;
import com.foreveross.atwork.modules.chat.util.BurnModeHelper;
import com.foreveross.atwork.modules.chat.util.DiscussionHelper;
import com.foreveross.atwork.modules.chat.util.HideMessageHelper;
import com.foreveross.atwork.modules.chat.util.SessionRefreshHelper;
import com.foreveross.atwork.modules.common.fragment.ChangeAvatarFragment;
import com.foreveross.atwork.modules.configSettings.manager.ConfigSettingsManager;
import com.foreveross.atwork.modules.contact.activity.PersonalInfoActivity;
import com.foreveross.atwork.modules.discussion.manager.extension.DiscussionManagerKt;
import com.foreveross.atwork.modules.discussion.util.DiscussionUIHelper;
import com.foreveross.atwork.modules.discussion.util.LabelViewWrapper;
import com.foreveross.atwork.modules.dropbox.activity.DropboxRWSettingActivity;
import com.foreveross.atwork.modules.file.service.FileTransferService;
import com.foreveross.atwork.modules.discussion.activity.DiscussionMemberSelectActivity;
import com.foreveross.atwork.modules.discussion.activity.DiscussionModifyActivity;
import com.foreveross.atwork.modules.group.activity.UserSelectActivity;
import com.foreveross.atwork.modules.discussion.fragment.DiscussionModifyFragment;
import com.foreveross.atwork.modules.discussion.model.DiscussionMemberSelectControlAction;
import com.foreveross.atwork.modules.group.module.UserSelectControlAction;
import com.foreveross.atwork.modules.main.activity.MainActivity;
import com.foreveross.atwork.modules.newsSummary.util.NewsSummaryHelper;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AvatarHelper;
import com.foreveross.atwork.utils.ChatMessageHelper;
import com.foreveross.atwork.utils.ContactShowNameHelper;
import com.foreveross.atwork.utils.ErrorHandleUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.foreveross.atwork.modules.chat.activity.IntelligentTranslationActivity.DATA_SELECTEDLANGUAGE;
import static com.foreveross.atwork.modules.chat.model.TranslateLanguageType.getLocalLanguageShortName;

/**
 * Created by lingen on 15/3/26.
 * Description:
 */
public class ChatInfoFragment extends ChangeAvatarFragment {

    public static final String TAG = ChatInfoFragment.class.getSimpleName();

    public static final int RESULT_ADD_DISCUSSION_MEMBERS = 1;

    private static final int RESULT_TRANSFER_OWNER = 2;

    private static final int RESULT_INTELLIGENT_TRANSLATION = 3;

    //关掉当前聊天界面
    public static final String ACTION_FINISH = "ACTION_FINISH";

    public static final String REFRESH_CHAT_INFO = "refresh_chat_info";

    private ConstraintLayout mClDiscussionBasicInfoArea;
    private ImageView mIvDiscussionLogoInBasicInfoArea;
    private TextView mTvDiscussionNameInBasicInfoArea;
    private FrameLayout mFlDiscussionLabelInBasicInfoArea;
    private ImageView mIvDiscussionLabelInBasicInfoArea;
    private TextView mTvDiscussionLabelInBasicInfoArea;

    private LinearLayout mLlDiscussionEntriesInChatInfo;


    private NotScrollGridView mGridView;

    private SessionType mChatInfoType;

    private String mChatIdentifier;

    private String mDomainId;

    private String mOrgId;

    private Session mSession;

    private View mDiscussionInfoView;

    private View mOtherInfoView;

    private View mLlDiscussionFile;

    //群名称VIEW
    private TextView mDiscussionNameView;

    //群人数
    private TextView mDiscussionCountView;

    //群创建时间
    private TextView mDiscussionCreateTimeView;

    //群主
    private TextView mTvDiscussionOwnerName;

    //群简介
    private TextView mDiscussionDetailInfoView;

    private TextView mDiscussionDetailLabel;

    //退出群聊天
    private TextView mExitGroupView;

    private TextView mTvTitle;

    private ChatInfoUserListBaseAdapter mChatInfoUserListBaseAdapter;

    private User mUserInfoP2pChat;

    private Discussion mDiscussion;

    private App mApp;

    private CanOperationType mCanOperationType = null;

    private RelativeLayout mRlSetTop;
    private WorkplusSwitchCompat mTopSwitchButton;
    private View mVSetTopDivider;

    private View mAppView;

    private TextView mAppNameView;

    //消息漫游VIEW
    private View mHistoryMessageView;

    private ProgressDialogHelper mProgressDialogHelper;

    private ImageView mIvDiscussionOwnerArrow;

    private ImageView mIvDiscussionOwnerAvatar;

    private RelativeLayout mRlNameInfo;

    private RelativeLayout mRlDetailInfo;

    private RelativeLayout mRlDiscussionAvatar;

    private ImageView mIvDiscussionAvatar;

    private LinearLayout mLlOwnerHandleArea;

    private RelativeLayout mRlOwnerOnlyAdd;

    private WorkplusSwitchCompat mSwitchBtnOwnerOnlyAdd;

    private RelativeLayout mRlOwnerTransfer;

    private LinearLayout mLlMessageHistoryArea;

    private RelativeLayout mRlMessageHistory;

    private boolean mHasMoreShow = true;

    private boolean mSmallMode = true;

    private RelativeLayout mRlNewMsgNotice;
    /**
     * 智能翻译
     */
    private LinearLayout mLlIntelligentTranslation;
    private WorkplusSwitchCompat mIntelligentTranslationSwitcher;
    private RelativeLayout mRlTranslateIntoACertainLanguage;
    private TextView mTvTranslation;
    private String mStrTranslationShortName;

    /**
     * 新消息通知开关
     */
    private WorkplusSwitchCompat mMessageNoDisturbingSwitcher;

    private View mChatInfoMoreView;

    private RelativeLayout mRlDiscussionIdentityArea;
    private FrameLayout mFlDiscussionLabelInIdentityArea;
    private ImageView mIvDiscussionLabelInIdentityArea;
    private TextView mTvDiscussionLabelInIdentityArea;

    private RelativeLayout mRlDiscussionQrcode;

    private RelativeLayout mRlDiscussionOwner;

//    private RelativeLayout chatInfoMoreLayout;

    private boolean mIsChangingMember = false;

    private List<String> mCheckList = new ArrayList<>();

    private View mRlSyncWeChat;
    private WorkplusSwitchCompat mSwitchBtnSyncWeChat;

    private boolean mIsFirstTimeLoad = true;

    private View mChatWatermarkView;
    private View mDiscussionFileSettingView;
    private WorkplusSwitchCompat mWatermarkSwitcher;

    private View mDiscussionSearchChatContentView;

    /**
     * 上一次成功拉取到群设置的时间
     */
    private long mLastSyncDiscussionSettingSuccessTime = -1;

    private boolean mHandlingDismiss = false;

    private boolean mClearedSessionInRomaing = false;

    //关闭当前activity
    private BroadcastReceiver mSideBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_FINISH.equals(action)) {
                String finishSessionId = intent.getStringExtra(ChatInfoActivity.DATA_CHAT_IDENTIFIER);
                if (mChatIdentifier.equals(finishSessionId)) {
                    finish(false);
                }
            }
        }
    };

    //刷新当前的activity
    private BroadcastReceiver mRefreshChatInfoBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mChatInfoUserListBaseAdapter.notifyDataSetChanged();
        }
    };

    //session无效（判断群聊是否无效）
    private BroadcastReceiver mSessionInvalidBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }


            String id = intent.getStringExtra(DiscussionHelper.SESSION_INVALID_ID);
            int type = intent.getIntExtra(DiscussionHelper.SESSION_INVALID_TYPE, -1);

            if (TextUtils.isEmpty(id) || !id.equalsIgnoreCase(mChatIdentifier)) {
                return;
            }
            showKickDialog(ChatListFragment.getSessionInvalidContent(type));
        }
    };

    //群聊拉人删人的监听器
    private AddOrRemoveListener addOrRemoveListener = new AddOrRemoveListener() {
        @Override
        public void add() {

            if (null != mDiscussion) {

                SelectedContactList.clear();

                UserSelectControlAction userSelectControlAction = new UserSelectControlAction();
                userSelectControlAction.setSelectMode(UserSelectActivity.SelectMode.SELECT);
                userSelectControlAction.setSelectAction(UserSelectActivity.SelectAction.DISCUSSION);
                userSelectControlAction.setSelectedContacts(mDiscussion.changeMembersToContacts());
                userSelectControlAction.setFromTag(TAG);

                if(mDiscussion.showEmployeeInfo()) {
                    userSelectControlAction.setDirectOrgCode(mDiscussion.getOrgCodeCompatible());
                    userSelectControlAction.setDirectOrgShow(true);
                }


                Intent intent = UserSelectActivity.getIntent(getActivity(), userSelectControlAction);
                startActivityForResult(intent, RESULT_ADD_DISCUSSION_MEMBERS);


            } else {
                AtworkApplicationLike.getLoginUser(new UserAsyncNetService.OnQueryUserListener() {
                    @Override
                    public void onSuccess(@NonNull User loginUser) {

                        SelectedContactList.clear();

                        List<ShowListItem> contactList = new ArrayList<>();
                        contactList.add(loginUser);
                        contactList.add(mUserInfoP2pChat);

                        UserSelectControlAction userSelectControlAction = new UserSelectControlAction();
                        userSelectControlAction.setSelectMode(UserSelectActivity.SelectMode.SELECT);
                        userSelectControlAction.setSelectAction(UserSelectActivity.SelectAction.DISCUSSION);
                        userSelectControlAction.setSelectedContacts(contactList);
                        userSelectControlAction.setFromTag(TAG);

                        Intent intent = UserSelectActivity.getIntent(getActivity(), userSelectControlAction);
                        startActivityForResult(intent, RESULT_ADD_DISCUSSION_MEMBERS);

                    }

                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        ErrorHandleUtil.handleBaseError(errorCode, errorMsg);
                    }
                });

            }


        }

        @Override
        public void remove() {
            mChatInfoUserListBaseAdapter.removeClick();
        }

        //删除群聊中的成员
        @Override
        public void removeUser(ShowListItem contact) {
            if (null != mDiscussion.mOwner && contact.getId().equals(mDiscussion.mOwner.mUserId)) {
                AtworkToast.showToast(getResources().getString(R.string.not_allowed_delete_owner));
                return;
            }
            //设置弹出框不可按键返回
            mProgressDialogHelper.show(false);

            DiscussionManager.getInstance().removeMember(getActivity(), mDiscussion.mDiscussionId, contact, new DiscussionAsyncNetService.HandledResultListener() {
                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    if (mProgressDialogHelper != null) {
                        mProgressDialogHelper.dismiss();
                    }

                    ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Group, errorCode, errorMsg);
                }

                @Override
                public void success() {
                    mDiscussion.removeMember(contact);
                    removeOnlineList(contact);

                    //dialog消失太快, 延迟处理, 避免设置按钮是否可点击判断错误
                    new Handler().postDelayed(() -> {

                        refreshDiscussionInfo(mDiscussion);
//                        DiscussionManager.getInstance().generateRemoveGroupContactMessage(getActivity(), mDiscussion, contact);
                        refreshContactCacheWithoutAvatar();
                    }, 100);

                    AtworkToast.showToast(getResources().getString(R.string.remove_group_member_db_success));
                    if (mProgressDialogHelper != null) {
                        mProgressDialogHelper.dismiss();
                    }
                }


            });

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_info, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTvTitle.setText(getResources().getString(R.string.chat_info_title));
        setDetailInfoTvMaxWidth();
        registerBroadcast();
        initData();
        registerListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (SessionType.Discussion.equals(mChatInfoType)) {
            if (null != mProgressDialogHelper) {
                mProgressDialogHelper.show();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //设置UI显示为true
        setUserVisibleHint(true);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (null != mChatInfoUserListBaseAdapter) {
            mChatInfoUserListBaseAdapter.resetRemoveMode();
        }

        if (!mIsChangingMember) {
            loadData();
        }

//        Session session = ChatSessionDataWrap.getInstance().getSession(mChatIdentifier, null);
//        if (session != null && session.top == SessionTop.LOCAL_TOP) {
//            mTopSwitchButton.setChecked(true);
//        }

        syncConversationSettings();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
    }

    @Override
    protected void findViews(View view) {
        mClDiscussionBasicInfoArea = view.findViewById(R.id.cl_discussion_basic_info_area);
        mIvDiscussionLogoInBasicInfoArea = view.findViewById(R.id.iv_discussion_logo_in_basic_info_area);
        mTvDiscussionNameInBasicInfoArea = view.findViewById(R.id.tv_discussion_name_in_basic_info_area);
        mFlDiscussionLabelInBasicInfoArea = view.findViewById(R.id.fl_discussion_label_in_basic_info_area);
        mIvDiscussionLabelInBasicInfoArea = view.findViewById(R.id.iv_discussion_label_in_basic_info_area);
        mTvDiscussionLabelInBasicInfoArea = view.findViewById(R.id.tv_discussion_label_in_basic_info_area);

        mLlDiscussionEntriesInChatInfo = view.findViewById(R.id.ll_discussion_entries_in_chat_info);

        mGridView = view.findViewById(R.id.chat_info_user_list);
        mDiscussionInfoView = view.findViewById(R.id.chat_info_discussion_info);
        mDiscussionNameView = view.findViewById(R.id.chat_info_discussion_name);
        mDiscussionCountView = view.findViewById(R.id.chat_info_discussion_count);
        mDiscussionCreateTimeView = view.findViewById(R.id.chat_info_create_time);
        mTvDiscussionOwnerName = view.findViewById(R.id.chat_info_discussion_owner);
        mDiscussionDetailInfoView = view.findViewById(R.id.chat_info_detail_info);
        mDiscussionDetailLabel = view.findViewById(R.id.chat_info_detail_info_title);

        mTvTitle = view.findViewById(R.id.title_bar_common_title);
        mOtherInfoView = view.findViewById(R.id.chat_info_other);
        mHistoryMessageView = view.findViewById(R.id.chat_info_detail_romaing_messages);
        mRlNewMsgNotice = view.findViewById(R.id.rl_new_msg_notice);
        mMessageNoDisturbingSwitcher = view.findViewById(R.id.message_notice_switcher);
        mExitGroupView = view.findViewById(R.id.exit_group);
        mTopSwitchButton = view.findViewById(R.id.chat_info_detail_set_top_switcher);
        mRlSetTop = view.findViewById(R.id.rl_set_top);
        mVSetTopDivider = view.findViewById(R.id.v_divider_set_top);
        mIvDiscussionOwnerAvatar = view.findViewById(R.id.chat_info_group_owner_avatar);
        mIvDiscussionOwnerArrow = view.findViewById(R.id.iv_group_owner_arrow);
        mChatInfoMoreView = view.findViewById(R.id.chat_info_more_layout);

        mAppView = view.findViewById(R.id.chat_info_app_name_layout);
        mAppNameView = view.findViewById(R.id.chat_info_app_name);

        mRlDiscussionIdentityArea = view.findViewById(R.id.rl_chat_info_discussion_identity_area);
        mFlDiscussionLabelInIdentityArea = view.findViewById(R.id.fl_discussion_label_in_identity_area);

        mRlDiscussionIdentityArea.setVisibility(View.GONE);
        mFlDiscussionLabelInIdentityArea.setVisibility(View.GONE);


        mRlDiscussionQrcode = view.findViewById(R.id.rl_discussion_qrcode);
        mRlDiscussionOwner = view.findViewById(R.id.chat_info_discussion_owner_layout);

        mRlNameInfo = view.findViewById(R.id.rl_chat_info_discussion_name_area);
        mRlDetailInfo = view.findViewById(R.id.chat_info_detail_info_line);

        mChatWatermarkView = view.findViewById(R.id.watermark_settings_group);
        mWatermarkSwitcher = view.findViewById(R.id.group_watermark_switcher);

        mLlDiscussionFile = view.findViewById(R.id.chat_info_discussion_file_line);
        mDiscussionFileSettingView = view.findViewById(R.id.group_file_setting_layout);

        mRlDiscussionAvatar = view.findViewById(R.id.rl_discussion_avatar);
        mIvDiscussionAvatar = view.findViewById(R.id.iv_discussion_avatar);

        mLlOwnerHandleArea = view.findViewById(R.id.ll_owner_handle_area);
        mRlOwnerOnlyAdd = view.findViewById(R.id.rl_owner_only_add);
        mSwitchBtnOwnerOnlyAdd = view.findViewById(R.id.sb_can_member_handle);

        mRlOwnerTransfer = view.findViewById(R.id.rl_transfer_owner);
        mSwitchBtnSyncWeChat = view.findViewById(R.id.sync_wechat_switcher);
        mRlSyncWeChat = view.findViewById(R.id.rl_sync_wechat_item);


        mLlMessageHistoryArea = view.findViewById(R.id.ll_message_history_area);
        mRlMessageHistory = view.findViewById(R.id.rl_message_history);

        mLlIntelligentTranslation = view.findViewById(R.id.ll_intelligent_translation);
        mIntelligentTranslationSwitcher = view.findViewById(R.id.intelligent_translation_switcher);
        mRlTranslateIntoACertainLanguage = view.findViewById(R.id.rl_translate_into_a_certain_language);
        mTvTranslation = view.findViewById(R.id.tv_translation);

        mDiscussionSearchChatContentView = view.findViewById(R.id.group_search_chat_content);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    public void removeOnlineList(ShowListItem contact) {
        mCheckList.remove(contact.getId());
    }


    @Override
    protected void changeAvatar(String mediaId) {
        mDiscussion.mAvatar = mediaId;
        modifyDiscussionAvatar();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_ADD_DISCUSSION_MEMBERS) {
            addDiscussionMemberOnResult(resultCode, data);
        } else if (requestCode == RESULT_TRANSFER_OWNER) {
            transferOwnerOnResult(resultCode);
        } else if (requestCode == RESULT_INTELLIGENT_TRANSLATION && resultCode == RESULT_OK) {
            String languageSelected = data.getStringExtra(DATA_SELECTEDLANGUAGE);

            if (!StringUtils.isEmpty(languageSelected)) {
                mTvTranslation.setText(TranslateLanguageType.getTranslateText(languageSelected));
            }
        }

    }

    private void addDiscussionMemberOnResult(int resultCode, Intent data) {
        if (null != getActivity() && Activity.RESULT_OK == resultCode) {
            List<ShowListItem> contactList = SelectedContactList.getContactList();
            List<UserHandleInfo> handleInfoList = ContactHelper.transferContactList(contactList);

            if (mChatInfoType.equals(SessionType.Discussion)) {

                mIsChangingMember = true;
                mProgressDialogHelper.show();

                syncAddMembersUserInfo(handleInfoList);


            } else if (mChatInfoType.equals(SessionType.User)) { //如果是单聊添加人员，则主动创建群组
                contactList.add(mUserInfoP2pChat);
                mProgressDialogHelper.show(getResources().getString(R.string.create_group_ing));

                createDiscussionRemote(contactList);
            }

            //检查在线离线状态
            checkOnline(UserHandleInfo.toUserIdList(handleInfoList));

        }

    }

    private void transferOwnerOnResult(int resultCode) {
        if (null != getActivity() && Activity.RESULT_OK == resultCode) {
            List<ShowListItem> contactList = new ArrayList<>();
            contactList.addAll(SelectedContactList.getContactList());
            SelectedContactList.clear();

            mIsChangingMember = true;
            mProgressDialogHelper.show();

            ShowListItem newOwnerSelected = contactList.get(0);
            DiscussionManager.getInstance().transferOwner(getActivity(), mDiscussion, newOwnerSelected, new BaseCallBackNetWorkListener() {
                @Override
                public void onSuccess() {
                    refreshDiscussionInfo(mDiscussion);
                    refreshOwnerViewIfYouOwner();

                    mProgressDialogHelper.dismiss();

                    toast(R.string.group_owner_transfer_successfully);

                    mIsChangingMember = false;

                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    mProgressDialogHelper.dismiss();
                    ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Group, errorCode, errorMsg);

                    mIsChangingMember = false;

                }
            });
        }
    }

    private void syncAddMembersUserInfo(List<UserHandleInfo> handleInfoList) {
        UserManager.getInstance().batchQueryUsers(getActivity(), UserHandleInfo.toUserIdList(handleInfoList), true, new UserAsyncNetService.OnUserCallBackListener() {
            @Override
            public void networkFail(int errorCode, String errorMsg) {
                mProgressDialogHelper.dismiss();

            }

            @Override
            public void onFetchUserDataSuccess(Object... object) {

                List<User> userList = (List<User>) object[0];

                addMemberRemote(userList);

            }

        });
    }

    private void createDiscussionRemote(List<ShowListItem> contactList) {

        DiscussionManager.getInstance().createDiscussion(getActivity(), contactList, null, null, null, null, true, new DiscussionAsyncNetService.OnCreateDiscussionListener() {
            @Override
            public void networkFail(int errorCode, String errorMsg) {
                mProgressDialogHelper.dismiss();

                ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Group, errorCode, errorMsg);
            }

            @Override
            public void onDiscussionSuccess(Discussion discussion) {
                mProgressDialogHelper.dismiss();
                if (null != discussion) {
                    Intent intent = ChatDetailActivity.getIntent(getActivity(), discussion.mDiscussionId);
                    startActivity(intent);
                }
            }

        });
    }

    public void addMemberRemote(final List<User> userList) {


        DiscussionManager.getInstance().addMember(getActivity(), mDiscussion.mDiscussionId, User.toUserHandleInfoList(userList), new DiscussionAsyncNetService.HandledResultListener() {
            @Override
            public void networkFail(int errorCode, String errorMsg) {
                mIsChangingMember = false;

                mProgressDialogHelper.dismiss();

                ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Group, errorCode, errorMsg);
            }

            @Override
            public void success() {
                mDiscussion.addMemberUserList(mDiscussion.mDiscussionId, userList);
                refreshDiscussionInfo(mDiscussion);
//                DiscussionManager.getInstance().generateAddContactsToGroupMessages(getActivity(), mDiscussion, userList);

                refreshContactCacheWithoutAvatar();

                mIsChangingMember = false;

                mProgressDialogHelper.dismiss();

            }

        });
    }

    private void initData() {

        mProgressDialogHelper = new ProgressDialogHelper(getActivity());

        mChatInfoUserListBaseAdapter = new ChatInfoUserListBaseAdapter(getActivity(), CanOperationType.Noting, addOrRemoveListener);
        mGridView.setAdapter(mChatInfoUserListBaseAdapter);

        //refresh mode
//        mHasMoreShow();

        if (getArguments() != null) {
            Bundle bundle = getArguments();


            mSession = bundle.getParcelable(ChatInfoActivity.DATA_SESSION);
            if (null != mSession) {
                mChatInfoType = mSession.type;
                mChatIdentifier = mSession.identifier;
                mDomainId = mSession.mDomainId;
                mOrgId = mSession.orgId;

            } else {
                mChatInfoType = (SessionType) bundle.getSerializable(ChatInfoActivity.CHAT_INFO_TYPE);
                mChatIdentifier = bundle.getString(ChatInfoActivity.DATA_CHAT_IDENTIFIER);
                mDomainId = bundle.getString(ChatInfoActivity.DATA_CHAT_DOMAIN_ID);
                mOrgId = bundle.getString(ChatInfoActivity.DATA_ORG_ID);
            }


        } else {
            AtworkToast.showToast(getResources().getString(R.string.get_param_error));
        }


        initRefreshNoDisturbingSwitcherStatus();
        initRefreshSessionTopSwitcherStatus();
        initRefreshTranslationSwitcherStatus();
    }

    //初始化刷新免打扰切换器状态
    private void initRefreshNoDisturbingSwitcherStatus() {

        ConfigSetting configSetting = new ConfigSetting();
        configSetting.mSourceId = mChatIdentifier;
        configSetting.mSourceType = SourceType.valueOf(mChatInfoType);
        configSetting.mBusinessCase = BusinessCase.SESSION_SHIELD;

        ConfigSettingsManager.INSTANCE.querySessionSetting(configSetting, configSettingReceived -> {
            if (null != configSettingReceived) {
                if (1 == configSettingReceived.mValue) {
                    mMessageNoDisturbingSwitcher.setChecked(true);
                } else {
                    mMessageNoDisturbingSwitcher.setChecked(false);
                }
            }

            return null;
        });
    }

    /**
     * Description:初始化刷新智能翻译切换器状态
     */
    private void initRefreshTranslationSwitcherStatus() {
        ConfigSetting configSetting = new ConfigSetting();
        configSetting.mSourceId = mChatIdentifier;
        configSetting.mSourceType = SourceType.valueOf(mChatInfoType);
        configSetting.mBusinessCase = BusinessCase.SESSION_TRANSLATION;
        ConfigSettingsManager.INSTANCE.querySessionSetting(configSetting, configSettingReceived -> {
            if (null != configSettingReceived) {
                if (1 == configSettingReceived.mValue) {
                    mIntelligentTranslationSwitcher.setChecked(true);
                    mRlTranslateIntoACertainLanguage.setVisibility(View.VISIBLE);
                    mStrTranslationShortName = TranslateLanguageType.TranslateLanguage.getTranslateLanguageShortName(configSettingReceived.mValue);
                    mTvTranslation.setText(TranslateLanguageType.getTranslateText(mStrTranslationShortName));

                } else {
                    mIntelligentTranslationSwitcher.setChecked(false);
                    mRlTranslateIntoACertainLanguage.setVisibility(View.GONE);
                }
            }

            return null;
        });
    }

    //初始化刷新会话置顶切换器状态
    private void initRefreshSessionTopSwitcherStatus() {
        ConfigSetting configSetting = new ConfigSetting();
        configSetting.mSourceId = mChatIdentifier;
        configSetting.mSourceType = SourceType.valueOf(mChatInfoType);
        configSetting.mBusinessCase = BusinessCase.SESSION_TOP;

        ConfigSettingsManager.INSTANCE.querySessionSetting(configSetting, configSettingReceived -> {
            if (null != configSettingReceived) {
                if (1 == configSettingReceived.mValue) {
                    mTopSwitchButton.setChecked(true);
                } else {
                    mTopSwitchButton.setChecked(false);
                }
            }

            return null;
        });

    }


    private void loadData() {
        //单聊
        if (mChatInfoType.equals(SessionType.User)) {
            loadP2P();

        }
        //群聊
        else if (mChatInfoType.equals(SessionType.Discussion)) {
            loadDiscussion();

        }
        //应用
        else if (mChatInfoType.isApp()) {
            loadApp();
        }


//        syncConversationSettings();
    }

    private void loadApp() {
        mRlSyncWeChat.setVisibility(View.GONE);
        mExitGroupView.setVisibility(View.GONE);
        mDiscussionInfoView.setVisibility(View.GONE);
        mLlIntelligentTranslation.setVisibility(View.GONE);
        mDiscussionSearchChatContentView.setVisibility(View.GONE);
        if (ChatSessionDataWrap.getInstance().isAnnounceAppNotCheckDb(mChatIdentifier)) {
            mRlNewMsgNotice.setVisibility(View.GONE);
        }

        if (SessionType.Service == mChatInfoType) {
            mLlMessageHistoryArea.setVisibility(View.VISIBLE);
            mHistoryMessageView.setVisibility(View.VISIBLE);

        } else {
            mLlMessageHistoryArea.setVisibility(View.GONE);
            mHistoryMessageView.setVisibility(View.GONE);

        }

        mGridView.setVisibility(View.GONE);


        mTvTitle.setText(getResources().getString(R.string.chat_info_title_app));
        AppManager.getInstance().queryApp(mActivity, mChatIdentifier, mOrgId, new AppManager.GetAppFromMultiListener() {
            @Override
            public void onSuccess(@NonNull App app) {
                mApp = app;

                mAppNameView.setText(app.getTitleI18n(BaseApplicationLike.baseContext));
                mAppView.setVisibility(View.VISIBLE);

                if (null != mSession) {
                    handleSetTopView(mSession);
                } else {
                    Session session = ChatSessionDataWrap.getInstance().getSessionSafely(mChatIdentifier, null);
                    if (null != session) {
                        handleSetTopView(session);
                    }
                }
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleTokenError(errorCode, errorMsg);

            }
        });
    }

    private void handleSetTopView(Session session) {
        if (session.isRemoteTop()) {
            mRlSetTop.setVisibility(View.GONE);
            mVSetTopDivider.setVisibility(View.GONE);
        } else {
            mRlSetTop.setVisibility(View.VISIBLE);
            mVSetTopDivider.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 初始化群组信息
     */
    private void loadDiscussion() {
        if (mSmallMode) {
            mDiscussionInfoView.setVisibility(View.VISIBLE);
        }
        mDiscussionSearchChatContentView.setVisibility(View.VISIBLE);
        mLlIntelligentTranslation.setVisibility(View.VISIBLE);
//        mClDiscussionBasicInfoArea.setVisibility(View.VISIBLE);

        DiscussionManager.getInstance().queryDiscussion(mActivity, mChatIdentifier, true, false, new DiscussionAsyncNetService.OnQueryDiscussionListener() {
            @Override
            public void networkFail(int errorCode, String errorMsg) {
                if (mProgressDialogHelper != null) {
                    mProgressDialogHelper.dismiss();
                }

                if (AtworkConstants.USER_NOT_FOUND_IN_DISCUSSION == errorCode) {

                    ChatSessionDataWrap.getInstance().removeSessionSafely(mChatIdentifier);
                    DiscussionDaoService.getInstance().removeDiscussion(mChatIdentifier);
                    showKickDialog(R.string.discussion_not_found);

                    smallMode();

                } else {
                    ErrorHandleUtil.handleTokenError(errorCode, errorMsg);
                }

            }

            @Override
            public void onSuccess(@NonNull Discussion discussion) {
                mDiscussion = discussion;

                mLlDiscussionFile.setVisibility(View.GONE);

                refreshOwnerViewIfYouOwner();

                syncDiscussionBasicInfo(discussion);

                syncDiscussionMembersUserInfo(discussion);

            }

            public void syncDiscussionMembersUserInfo(@NonNull final Discussion discussion) {
                refreshDiscussionInfo(discussion);

                if (mIsFirstTimeLoad) {
                    smallMode();
                }

                checkMemberListOnline();

                mProgressDialogHelper.dismiss();

                mIsFirstTimeLoad = false;
            }


        });

    }

    //判断退出群聊按钮是否显示
    private void refreshExitDiscussionView(@NonNull Discussion discussion) {
        if (discussion.isInternalDiscussion()) {
            mExitGroupView.setVisibility(View.GONE);
        } else {
//                    mExitGroupView.setText();
            if (discussion.isYouOwner(AtworkApplicationLike.baseContext)) {
                mExitGroupView.setText(R.string.dismiss_group);
            } else {
                mExitGroupView.setText(R.string.exit_group);

            }
            mExitGroupView.setVisibility(View.VISIBLE);
        }
    }

    //获取服务器最新的会话配置! 同步会话设置：微信同步；免打扰；会话置顶；
    private void syncConversationSettings() {

        ConversionConfigSettingParticipant participant = new ConversionConfigSettingParticipant(mChatIdentifier, mDomainId, mChatInfoType);
        //mProgressDialogHelper.show();
        ConfigSettingsManager.INSTANCE.getConversationSettingRemote(participant, new BaseNetWorkListener<ConversionConfigSettingItem>() {
            @Override
            public void onSuccess(ConversionConfigSettingItem conversionConfigSettingItem) {

                if (SessionType.User.equals(mChatInfoType)) {
                    mSwitchBtnSyncWeChat.setChecked(conversionConfigSettingItem.getWeixinSyncEnabled());
                    LoginUserInfo.getInstance().setWeChatConversationSettings(mActivity, mChatIdentifier, conversionConfigSettingItem.getWeixinSyncEnabled());
                }

                //将获取到的服务器最新的会话配置保存到本地session（保存到缓存中，以及本地数据库中）
                setSessionShieldSetting(!conversionConfigSettingItem.getNotifyEnabled());
                setSessionTopSetting(conversionConfigSettingItem.getStickyEnabled());
                mStrTranslationShortName = conversionConfigSettingItem.getLanguage();
                setSessionTranslationSetting(!StringUtils.isEmpty(conversionConfigSettingItem.getLanguage()), conversionConfigSettingItem.getLanguage());
                //mProgressDialogHelper.dismiss();
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleTokenError(errorCode, errorMsg);
            }
        });

    }

    private void syncDiscussionBasicInfo(@NonNull Discussion discussion) {

        DiscussionManagerKt.queryDiscussionBasicInfoRemote(DiscussionManager.getInstance(), mActivity, mDiscussion.mDiscussionId, result -> {
            mLastSyncDiscussionSettingSuccessTime = System.currentTimeMillis();

            if(result instanceof  DiscussionSettingsResponse) {

                if (isAdded()) {
                    DiscussionSettingsResponse response = (DiscussionSettingsResponse) result;
                    mSwitchBtnSyncWeChat.setChecked(response.mWeChatSyncEnable);
                    LoginUserInfo.getInstance().setWeChatConversationSettings(mActivity, mDiscussion.mDiscussionId, response.mWeChatSyncEnable);

                    if (verifyDiscussionWatermarkCondition()) {
                        handleWatermarkAction(response.mWatermarkEnable);
                    } else {
                        mChatWatermarkView.setVisibility(View.GONE);
                    }


                    //更新"仅群主可添加新成员"按钮状态以及本地数据
                    updateSwitchBtnOwnOnlyAddStatus(discussion, response.mOwnerControl);
                    refreshOperationType(response.mOwnerControl);
                    refreshQrcodeShareGroup(response.mOwnerControl);
                }


                return null;
            }

            if(result instanceof Discussion) {
                Discussion newDiscussion = (Discussion) result;
                if (null != mDiscussion) {
                    newDiscussion.mMemberList = mDiscussion.mMemberList;
                    newDiscussion.mMemberContactList = mDiscussion.mMemberContactList;
                }

                mDiscussion = newDiscussion;

                return null;
            }


           return null;
        });

    }

    //设置本地Session的新消息通知开关
    private void setSessionShieldSetting(boolean shield) {
        ConfigSetting configSetting = new ConfigSetting();
        configSetting.mSourceId = mChatIdentifier;
        configSetting.mSourceType = SourceType.valueOf(mChatInfoType);
        configSetting.mBusinessCase = BusinessCase.SESSION_SHIELD;

        if (shield) {
            configSetting.mValue = 1;
        } else {
            configSetting.mValue = 0;

        }

        ConfigSettingsManager.INSTANCE.setSessionSettingLocal(configSetting, result -> {
            if (result) {
                mMessageNoDisturbingSwitcher.setChecked(shield);
            }

            return null;
        });

    }

    //设置本地Session的会话置顶
    private void setSessionTopSetting(boolean top) {
        ConfigSetting configSetting = new ConfigSetting();
        configSetting.mSourceId = mChatIdentifier;
        configSetting.mSourceType = SourceType.valueOf(mChatInfoType);
        configSetting.mBusinessCase = BusinessCase.SESSION_TOP;

        if (top) {
            configSetting.mValue = 1;
        } else {
            configSetting.mValue = 0;

        }

        ConfigSettingsManager.INSTANCE.setSessionSettingLocal(configSetting, result -> {
            if (result) {
                mTopSwitchButton.setChecked(top);
            }

            return null;
        });

    }

    //设置本地Session的智能翻译
    private void setSessionTranslationSetting(Boolean waitToOpen, String shortLanguage) {
        ConfigSetting configSetting = new ConfigSetting();
        configSetting.mSourceId = mChatIdentifier;
        configSetting.mSourceType = SourceType.valueOf(mChatInfoType);
        configSetting.mBusinessCase = BusinessCase.SESSION_TRANSLATION;

        if (waitToOpen) {
            configSetting.mValue = TranslateLanguageType.TranslateLanguage.getTranslateLanguageValue(shortLanguage);
        } else {
            configSetting.mValue = TranslateLanguageType.TranslateLanguage.NO.getValue();
        }
        //设置到本地Session（保存到缓存中，以及本地数据库中）
        ConfigSettingsManager.INSTANCE.setSessionSettingLocal(configSetting, result -> {
            if (result) {
                if (!waitToOpen) {
                    mIntelligentTranslationSwitcher.setChecked(false);
                    mRlTranslateIntoACertainLanguage.setVisibility(View.GONE);
                    mStrTranslationShortName = "";
                } else {
                    mIntelligentTranslationSwitcher.setChecked(true);
                    mRlTranslateIntoACertainLanguage.setVisibility(View.VISIBLE);
                    //mTvTranslation.setText(shortLanguage);
                    mTvTranslation.setText(TranslateLanguageType.getTranslateText(shortLanguage));
                    mStrTranslationShortName = shortLanguage;
                }
            }
            return null;
        });

    }

    private void showLlDiscussionEntriesInChatInfo(FragmentActivity activity, List<DiscussionFeature> discussionFeatures) {
        if(5 < discussionFeatures.size()) {
            discussionFeatures = discussionFeatures.subList(0, 5);
            DiscussionFeature moreFeature = new DiscussionFeature();
            moreFeature.setId(DiscussionFeature.ID_MORE);
            moreFeature.setFeature(DiscussionFeature.ENTRY_LIST);
            discussionFeatures.add(moreFeature);
        }


        mLlDiscussionEntriesInChatInfo.removeAllViews();

        ImageView vDivider = new ImageView(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ScreenUtils.getScreenWidth(AtworkApplicationLike.baseContext), activity.getResources().getDimensionPixelSize(R.dimen.chat_info_list_divider_height));
        vDivider.setLayoutParams(params);
        vDivider.setBackgroundColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.common_page_bg));

        mLlDiscussionEntriesInChatInfo.addView(vDivider);

        mLlDiscussionEntriesInChatInfo.setVisibility(View.VISIBLE);
    }

    private void hideLlDiscussionEntriesInChatInfo() {
        mLlDiscussionEntriesInChatInfo.setVisibility(View.GONE);
        mLlDiscussionEntriesInChatInfo.removeAllViews();
    }


    /**
     * 刷新群主相关功能的界面
     */
    private void refreshOwnerViewIfYouOwner() {
        refreshExitDiscussionView(mDiscussion);

        handleDiscussionWatermarkGroup();
        handleDiscussionFileSettings();

        setDiscussionAvatarView();
        setDiscussionOwnerAreaView();

    }

    //是否符合显示水印开关条件， 1. 是否是群组，2是否是群主，3域设置是否开启手动模式
    private boolean verifyDiscussionWatermarkCondition() {


        return "customer".equalsIgnoreCase(DomainSettingsManager.getInstance().handleDiscussionWatermarkFeature())
                && isYouOwner();
    }

    private void handleDiscussionWatermarkGroup() {

        if (verifyDiscussionWatermarkCondition()) {
            mChatWatermarkView.setVisibility(View.VISIBLE);
            Watermark watermark = new Watermark(mDiscussion.mDiscussionId, Watermark.Type.DISCUSSION);
            boolean result = WatermarkCache.getInstance().getWatermarkConfigCache(watermark);
            mWatermarkSwitcher.setChecked(result);

        } else {
            mChatWatermarkView.setVisibility(View.GONE);

        }

    }

    private void handleDiscussionFileSettings() {
        mDiscussionFileSettingView.setVisibility(isYouOwner() ? View.VISIBLE : View.GONE);
    }
    private boolean isYouOwner() {
        return mDiscussion.isYouOwner(BaseApplicationLike.baseContext);
    }

    private void setDiscussionOwnerAreaView() {
        //普通群, 且是群主才显示"转让群主", "添加新成员权限控制"的功能
        if (isYouOwner()) {
            mLlOwnerHandleArea.setVisibility(View.VISIBLE);

            ViewUtil.setVisible(mRlOwnerOnlyAdd, !mDiscussion.isInternalDiscussion());

            mRlOwnerTransfer.setVisibility(View.VISIBLE);

            initOwnerOnlyAddSwitchBtn();

        } else {
            mLlOwnerHandleArea.setVisibility(View.GONE);
            mRlOwnerOnlyAdd.setVisibility(View.GONE);
            mRlOwnerTransfer.setVisibility(View.GONE);

        }

        setDiscussionSyncView();

    }

    private void setUserSyncView() {
        boolean syncWeChatMessageFeature = DomainSettingsManager.getInstance().syncWeChatMessageFeature();
        if (syncWeChatMessageFeature) {
            mRlDiscussionOwner.setVisibility(View.VISIBLE);
            mLlOwnerHandleArea.setVisibility(View.VISIBLE);

            mSwitchBtnSyncWeChat.setChecked(LoginUserInfo.getInstance().getWeChatConversationSettings(mActivity, mChatIdentifier));

        } else {
            mRlDiscussionOwner.setVisibility(View.GONE);
            mLlOwnerHandleArea.setVisibility(View.GONE);
        }


    }

    private void setDiscussionSyncView() {
        boolean syncWeChatMessageFeature = DomainSettingsManager.getInstance().syncWeChatMessageFeature();
        //群主且"微信同步"配置打开, 才显示"微信同步"的功能
        if (syncWeChatMessageFeature && isYouOwner()) {
            mRlSyncWeChat.setVisibility(View.VISIBLE);
            mSwitchBtnSyncWeChat.setChecked(LoginUserInfo.getInstance().getWeChatConversationSettings(mActivity, mChatIdentifier));
        } else {
            mRlSyncWeChat.setVisibility(View.GONE);
        }

    }

    private void setDiscussionAvatarView() {

        AvatarHelper.setDiscussionAvatarById(mIvDiscussionLogoInBasicInfoArea, mDiscussion.mDiscussionId, true, true);

        //非群主, 不显示头像选项
        if (isYouOwner()) {
            mRlDiscussionAvatar.setVisibility(View.VISIBLE);
            AvatarHelper.setDiscussionAvatarById(mIvDiscussionAvatar, mDiscussion.mDiscussionId, true, true);

        } else {
            mRlDiscussionAvatar.setVisibility(View.GONE);

        }
    }

    private void initOwnerOnlyAddSwitchBtn() {
        long queryTime = System.currentTimeMillis();
        getOnlyOwnerAddConfigSetting(configSetting -> {
            if (queryTime > mLastSyncDiscussionSettingSuccessTime) {
                if (null != configSetting) {
                    mSwitchBtnOwnerOnlyAdd.setChecked(configSetting.isOpen());
                }
            }
        });
    }

    private void handleWatermarkAction(boolean actionResult) {
        mWatermarkSwitcher.setChecked(actionResult);
        Watermark watermark = new Watermark();
        watermark.mSourceId = mDiscussion.mDiscussionId;
        watermark.mType = Watermark.Type.DISCUSSION;
        WatermarkRepository repository = WatermarkRepository.getInstance();
        WatermarkCache.getInstance().setWatermarkConfigCache(watermark, actionResult);
        long result = actionResult ? repository.insertOrUpdateWatermark(watermark) : repository.deleteWatermark(watermark);
    }

    private void updateSwitchBtnOwnOnlyAddStatus(@NonNull Discussion discussion, boolean ownerChangeEnable) {
        mSwitchBtnOwnerOnlyAdd.setChecked(ownerChangeEnable);
        ConfigSetting configSetting = new ConfigSetting(discussion.mDiscussionId, SourceType.DISCUSSION, BusinessCase.ONLY_OWNER_ADD);
        if (ownerChangeEnable) {
            configSetting.mValue = 1;

        } else {
            configSetting.mValue = 0;

        }
        ConfigSettingsManager.INSTANCE.insertOrUpdateConfigSetting(configSetting);
    }

    /**
     * 获取"仅群主可添加新成员"开关配置
     *
     * @param baseNetWorkListener
     */
    private void getOnlyOwnerAddConfigSetting(BaseQueryListener<ConfigSetting> baseNetWorkListener) {
        ConfigSettingsManager.INSTANCE.getConfigSettingFromDb(mDiscussion.mDiscussionId, SourceType.DISCUSSION, BusinessCase.ONLY_OWNER_ADD, baseNetWorkListener);

    }


    private void loadP2P() {

        mLlIntelligentTranslation.setVisibility(View.VISIBLE);
        if (FileTransferService.INSTANCE.needVariation(mChatIdentifier)) {
            mGridView.setVisibility(View.GONE);
            mRlNewMsgNotice.setVisibility(View.GONE);
            mLlIntelligentTranslation.setVisibility(View.GONE);
        }
        mDiscussionSearchChatContentView.setVisibility(View.VISIBLE);
        mExitGroupView.setVisibility(View.GONE);
        mDiscussionInfoView.setVisibility(View.GONE);
        mCanOperationType = getOnlyCanAddCheckConfig();

        UserManager.getInstance().queryUserByUserId(getActivity(), mChatIdentifier, mDomainId, new UserAsyncNetService.OnQueryUserListener() {
            @Override
            public void onSuccess(@NonNull User user) {

                mUserInfoP2pChat = user;
                setUserSyncView();
                List<User> userList = new ArrayList<>();
                userList.add(mUserInfoP2pChat);
                mChatInfoUserListBaseAdapter.setUserContactItemList(getOnlyCanAddCheckConfig(), userList);

                if (!DomainSettingsManager.getInstance().handleUserOnlineFeature()) {
                    return;
                }
                List<String> checkOnlineList = new ArrayList<>();
                checkOnlineList.add(mUserInfoP2pChat.mUserId);
                checkOnline(checkOnlineList);
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleTokenError(errorCode, errorMsg);
            }

        });


    }


    /**
     * 刷新群组信息
     *
     * @param discussion
     */
    private void refreshDiscussionInfo(Discussion discussion) {
        if (!isAdded()) {
            return;
        }
        mDiscussionNameView.setText(discussion.mName);
        mDiscussionCountView.setText(getResources().getString(R.string.person, discussion.mMemberContactList.size() + ""));
        if (null != discussion.mTimerInfo) {
            mDiscussionCreateTimeView.setText(TimeUtil.getStringForMillis(discussion.mTimerInfo.mCreateTime, TimeUtil.getTimeFormat1(BaseApplicationLike.baseContext)));
        }

        mTvDiscussionNameInBasicInfoArea.setText(discussion.mName);
        DiscussionUIHelper.refreshLabel(discussion
                , new LabelViewWrapper(
                        mFlDiscussionLabelInBasicInfoArea,
                        mIvDiscussionLabelInBasicInfoArea,
                        mTvDiscussionLabelInBasicInfoArea)
                , null);


        setDiscussionViewTitle(discussion);


        //内部群没有群主时, 需要判断群管理员是否可以手动选自己成为群主
        refreshDiscussionOwnerInfoView(discussion);

        mDiscussionDetailInfoView.setText(mDiscussion.mIntro);
        mDiscussionDetailInfoView.post(() -> {
            if (mDiscussionDetailInfoView.getLineCount() > 1) {
                mDiscussionDetailInfoView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            }
        });

        refreshQrcodeShareGroup(null);

        refreshAdapterAndOperationType(discussion);

    }

    /**
     * 设置群组的标题栏(根据点击"显示更多成员"变动)
     */
    private void setDiscussionViewTitle(Discussion discussion) {
        int title;
        if (mSmallMode) {
            title = R.string.chat_info_title_group_info;

        } else {
            title = R.string.chat_info_title_group_members;

        }
        mTvTitle.setText(getResources().getString(title, discussion.mMemberContactList.size() + ""));
    }

    private void refreshDiscussionOwnerInfoView(Discussion discussion) {
        if (null != mDiscussion.mOwner) {
            showDiscussionOwnerView();

            SetReadableNameParams setReadableNameParams = SetReadableNameParams.newSetReadableNameParams()
                    .setTextView(mTvDiscussionOwnerName)
                    .setUserId(mDiscussion.mOwner.mUserId)
                    .setDomainId(mDiscussion.mOwner.mDomainId);

            if (mDiscussion.showEmployeeInfo()) {
                setReadableNameParams.setOrgCode(mDiscussion.getOrgCodeCompatible());
            }
            ContactShowNameHelper.setReadableNames(setReadableNameParams);


            AvatarHelper.setUserAvatarById(mIvDiscussionOwnerAvatar, mDiscussion.mOwner.mUserId, mDiscussion.mOwner.mDomainId, false, true);
        } else {

            if (isInternalDiscussionCanBeOwner(discussion)) {
                hideDiscussionOwnerView();
            } else {
                showDiscussionOwnerView();

                mIvDiscussionOwnerAvatar.setImageDrawable(null);
                mTvDiscussionOwnerName.setText(StringUtils.EMPTY);
            }
        }
    }

    private void hideDiscussionOwnerView() {
        mIvDiscussionOwnerArrow.setVisibility(View.VISIBLE);
        mIvDiscussionOwnerAvatar.setVisibility(View.GONE);
        mTvDiscussionOwnerName.setVisibility(View.GONE);
    }

    private void showDiscussionOwnerView() {
        mIvDiscussionOwnerArrow.setVisibility(View.GONE);
        mIvDiscussionOwnerAvatar.setVisibility(View.VISIBLE);
        mTvDiscussionOwnerName.setVisibility(View.VISIBLE);
    }

    /**
     * 根据配置刷新二维码view, 当"仅群主可添加新成员"时, 非群主不显示二维码
     *
     * @param ownerControl 当为空时, 则用本地存储的配置来控制
     */
    private void refreshQrcodeShareGroup(@Nullable Boolean ownerControl) {
        //内部群不显示二维码选项
        if (mDiscussion.isInternalDiscussion()) {
            mRlDiscussionQrcode.setVisibility(View.GONE);
        } else {
            if (isYouOwner()) {
                mRlDiscussionQrcode.setVisibility(View.VISIBLE);

            } else {

                if (null != ownerControl) {
                    setRlGroupQrcodeShow(!ownerControl);
                } else {
                    long queryTime = System.currentTimeMillis();
                    getOnlyOwnerAddConfigSetting(configSetting -> {

                        if (queryTime > mLastSyncDiscussionSettingSuccessTime) {
                            setRlGroupQrcodeShow(null != configSetting && !configSetting.isOpen());
                        }

                    });
                }


            }

        }
    }

    private void setRlGroupQrcodeShow(boolean visible) {
        if (visible) {
            mRlDiscussionQrcode.setVisibility(View.VISIBLE);

        } else {
            mRlDiscussionQrcode.setVisibility(View.GONE);

        }
    }

    private void refreshAdapterAndOperationType(Discussion discussion) {
        if (mDiscussion.isInternalDiscussion()) {
            mCanOperationType = CanOperationType.Noting;
            mChatInfoUserListBaseAdapter.setUserContactItemList(mCanOperationType, mDiscussion.mMemberContactList);
            setInfoMoreStatus(discussion);

        } else if (discussion.isYouOwner(BaseApplicationLike.baseContext)) {
            mCanOperationType = getCanAddAndRemoveCheckConfig();
            mChatInfoUserListBaseAdapter.setUserContactItemList(mCanOperationType, mDiscussion.mMemberContactList);
            setInfoMoreStatus(discussion);

        } else {
            long queryTime = System.currentTimeMillis();
            getOnlyOwnerAddConfigSetting(configSetting -> {
                if (queryTime > mLastSyncDiscussionSettingSuccessTime) {
                    if (null != configSetting && configSetting.isOpen()) {
                        mCanOperationType = CanOperationType.Noting;


                    } else {
                        mCanOperationType = getOnlyCanAddCheckConfig();

                    }
                }
                mChatInfoUserListBaseAdapter.setUserContactItemList(mCanOperationType, mDiscussion.mMemberContactList);
                setInfoMoreStatus(discussion);


            });
        }
    }

    private void refreshOperationType(boolean ownerControl) {
        if (mDiscussion.isInternalDiscussion()) {
            mCanOperationType = CanOperationType.Noting;

        } else if (isYouOwner()) {
            mCanOperationType = getCanAddAndRemoveCheckConfig();

        } else {
            if (ownerControl) {
                mCanOperationType = CanOperationType.Noting;

            } else {
                mCanOperationType = getOnlyCanAddCheckConfig();


            }
        }

        mChatInfoUserListBaseAdapter.updateOperationType(mCanOperationType);
        setInfoMoreStatus(mDiscussion);
    }

    private CanOperationType getOnlyCanAddCheckConfig() {
        if (!AtworkConfig.DISSCUSSION_CONFIG.isNeedEntry()) {
            return CanOperationType.Noting;
        }

        return CanOperationType.OnlyCanAdd;
    }


    private CanOperationType getCanAddAndRemoveCheckConfig() {
        if (!AtworkConfig.DISSCUSSION_CONFIG.isNeedEntry()) {
            return CanOperationType.Noting;
        }

        return CanOperationType.CanAddAndRemove;
    }

    /**
     * 防止 IM 传过来的PROFILE_CHANGED有延迟, 先设置缓存, 但不刷新头像
     */
    private void refreshContactCacheWithoutAvatar() {
        //头像的更新依赖于 IM通知, 为了不影响 IM 通知那边刷新头像的操作, 尝试获取"可能为最新"的 avatar
        Discussion discussionInCache = DiscussionCache.getInstance().getDiscussionCache(mDiscussion.mDiscussionId);
        if (null != discussionInCache) {
            mDiscussion.mAvatar = discussionInCache.getAvatar();

        }
    }


    private void historySelectDialog() {

        new AtworkAlertDialog(getActivity(), AtworkAlertDialog.Type.SIMPLE)
                .setContent(R.string.confirm_romaing_history)
                .setClickBrightColorListener(dialog -> {
                    String participantDomain = null;
                    String participantId = null;
                    ParticipantType participantType = null;

                    if (mChatInfoType.equals(SessionType.User)) {
                        participantDomain = mUserInfoP2pChat.mDomainId;
                        participantId = mUserInfoP2pChat.mUserId;
                        participantType = ParticipantType.User;
                    } else if (mChatInfoType.equals(SessionType.Discussion)) {
                        participantDomain = mDiscussion.mDomainId;
                        participantId = mDiscussion.mDiscussionId;
                        participantType = ParticipantType.Discussion;

                    } else if (mChatInfoType.equals(SessionType.Service)) {
                        participantDomain = mApp.mDomainId;
                        participantId = mApp.mAppId;
                        participantType = ParticipantType.App;
                    }

                    if (!StringUtils.isEmpty(participantDomain)) {
                        mProgressDialogHelper.show(getResources().getString(R.string.romaing_messages));
                        long romaingTime = TimeUtil.getTimeInMillisDaysBefore(7);
                        long fetchInDayTimeDomainSetting = DomainSettingsManager.getInstance().getMessagePullLatestTime();
                        if (fetchInDayTimeDomainSetting > romaingTime) {
                            romaingTime = fetchInDayTimeDomainSetting;
                        }

                        romaingMessages(participantDomain, participantType, participantId, romaingTime, -1);
                    }
                })
                .show();
    }

    private void romaingMessages(final String participantDomain, final ParticipantType participantType, final String participantId, final long begin, final long end) {


        ChatService.queryRoamingMessages(mActivity, participantDomain, participantType, participantId, begin, end, AtworkConfig.ROAMING_AND_PULL_SYNC_INCLUDE_TYPE, null, "first_in", AtworkConfig.COUNT_SYNC_MESSAGE_BATCH, !mClearedSessionInRomaing, new MessageAsyncNetService.GetHistoryMessageListener() {
            @Override
            public void networkFail(int errorCode, String errorMsg) {
                if (isAdded()) {
                    AtworkToast.showToast(getResources().getString(R.string.romaing_messages_network_fail));
                    mProgressDialogHelper.dismiss();
                }
            }

            @Override
            public void getHistoryMessageSuccess(List<ChatPostMessage> historyMessages, int realOfflineSize) {
                mClearedSessionInRomaing = true;

                HideMessageHelper.hideMessageList(historyMessages);
                BurnModeHelper.checkMsgExpired(historyMessages);
                NewsSummaryHelper.Companion.filtrateMessageList(historyMessages);

                for (ChatPostMessage chatPostMessage : historyMessages) {
                    ChatMessageHelper.dealWithChatMessage(mActivity, chatPostMessage, false);
                }

//                historyMessages = BasicMsgHelper.filterExpiredMsg(historyMessages);

                ChatSessionDataWrap.getInstance().asyncBatchAddMessages(participantId, participantType, historyMessages);
                Intent intent = new Intent(ChatDetailFragment.ROMAING_MESSAGE_RECEIVED);
                intent.putExtra(ChatDetailFragment.DATA_NEW_MESSAGE, (Serializable) historyMessages);
                LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent);

                if (realOfflineSize < AtworkConfig.COUNT_SYNC_MESSAGE_BATCH) {
                    Logger.d(TAG, "拉取7天漫游消息完毕");
                    try {
                        AtworkToast.showToast(getResources().getString(R.string.romaing_messages_success));
                        mProgressDialogHelper.dismiss();
//                        getActivity().finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
                //拿该批消息第最后条作为时间点, 再次去拉取
                PostTypeMessage postTypeMessage = historyMessages.get(historyMessages.size() - 1);
                if (postTypeMessage == null) {
                    return;
                }

                mClearedSessionInRomaing = false;
                romaingMessages(participantDomain, participantType, participantId, postTypeMessage.deliveryTime, -1);
            }


        });
    }


    private void notSmallMode() {

        mDiscussionInfoView.setVisibility(View.GONE);
        mOtherInfoView.setVisibility(View.GONE);
        mDiscussionSearchChatContentView.setVisibility(View.GONE);
        if (!mChatInfoUserListBaseAdapter.hasMore()) {
            mChatInfoMoreView.setVisibility(View.GONE);
            mHasMoreShow = false;
        }

        mSmallMode = false;

        setDiscussionViewTitle(mDiscussion);

    }

    private void smallMode() {
        if (!isAdded() || null == mDiscussion) {
            return;
        }


        mDiscussionInfoView.setVisibility(View.VISIBLE);
        mOtherInfoView.setVisibility(View.VISIBLE);
        mDiscussionSearchChatContentView.setVisibility(View.VISIBLE);
        mHasMoreShow = true;
        mSmallMode = true;

        mChatInfoUserListBaseAdapter.smallModel();
        setInfoMoreStatus(mDiscussion);
        setDiscussionViewTitle(mDiscussion);
    }

    private void setInfoMoreStatus(Discussion discussion) {

        if (mHasMoreShow) {
            if (CanOperationType.CanAddAndRemove.equals(mCanOperationType)) {
                if (7 <= discussion.mMemberContactList.size()) {
                    mChatInfoMoreView.setVisibility(View.VISIBLE);
                    return;

                }

            } else if (CanOperationType.OnlyCanAdd.equals(mCanOperationType)) {
                if (8 <= discussion.mMemberContactList.size()) {
                    mChatInfoMoreView.setVisibility(View.VISIBLE);
                    return;

                }
            } else {
                if (9 <= discussion.mMemberContactList.size()) {
                    mChatInfoMoreView.setVisibility(View.VISIBLE);
                    return;

                }
            }
        }

        mChatInfoMoreView.setVisibility(View.GONE);

    }


    private void checkMemberListOnline() {
        if (!DomainSettingsManager.getInstance().handleUserOnlineFeature()) {
            return;
        }
        List<String> checkOnlineList = new ArrayList<>();
        for (DiscussionMember discussionMember : mDiscussion.mMemberList) {
            checkOnlineList.add(discussionMember.userId);
        }
        checkOnline(checkOnlineList);
    }

    private void checkOnline(List<String> checkOnlineList) {
        mCheckList.addAll(0, checkOnlineList);
        OnlineManager.getInstance().checkOnlineList(mActivity, checkOnlineList, onlineList -> {

            if (mChatInfoUserListBaseAdapter != null) {
                mChatInfoUserListBaseAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Description: 判断，如果是关闭的话，获取系统语言；否则，将语言置为空。
     *
     * @param waitToOpen :
     * @return
     */
    private String checkTranslateLanuage(Boolean waitToOpen) {
        String lanauageName = "";
        if (waitToOpen)
            lanauageName = getLocalLanguageShortName(getContext());
        return lanauageName;
    }

    private void registerListener() {
        mRlTranslateIntoACertainLanguage.setOnClickListener(v -> {
            Intent intent = IntelligentTranslationActivity.getIntent(mStrTranslationShortName, mChatInfoType, mChatIdentifier, mDomainId);
            //startActivity(intent);
            startActivityForResult(intent, RESULT_INTELLIGENT_TRANSLATION);
        });

        //显示更多成员
        mChatInfoMoreView.setOnClickListener(v -> {
            mChatInfoUserListBaseAdapter.notSmallModel();
            notSmallMode();

        });

        //点击群主
        mRlDiscussionOwner.setOnClickListener(v -> {
            if (null != mDiscussion) {

                if (null != mDiscussion.mOwner) {
                    UserManager.getInstance().queryUserByUserId(getActivity(), mDiscussion.mOwner.mUserId, mDiscussion.mOwner.mDomainId, new UserAsyncNetService.OnQueryUserListener() {
                        @Override
                        public void onSuccess(@NonNull User user) {

                            if (null != getActivity()) {
                                startActivity(PersonalInfoActivity.getIntent(getActivity(), user));

                            }

                        }

                        @Override
                        public void networkFail(int errorCode, String errorMsg) {
                            ErrorHandleUtil.handleError(errorCode, errorMsg);
                        }

                    });

                } else {
                    if (isInternalDiscussionCanBeOwner(mDiscussion)) {
                        alertBecomeOwner();

                    }
                }

            }
        });
        //智能翻译：
        mIntelligentTranslationSwitcher.setOnClickNotPerformToggle(() -> {
            boolean waitToOpen = !mIntelligentTranslationSwitcher.isChecked();
            String languageShortName = checkTranslateLanuage(waitToOpen);
            LogUtil.e("智能翻译语种：", languageShortName);
            ConversionConfigSettingRequest conversationsRequest = new ConversionConfigSettingRequest();
            ConversionConfigSettingParticipant participant = new ConversionConfigSettingParticipant(mChatIdentifier, mDomainId, mChatInfoType);
            conversationsRequest.setParticipant(participant);
            conversationsRequest.setLanguage(languageShortName);

            ConfigSettingsManager.INSTANCE.setConversationSettingRemote(conversationsRequest, new BaseCallBackNetWorkListener() {
                @Override
                public void onSuccess() {

                    setSessionTranslationSetting(waitToOpen, languageShortName);
                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    ErrorHandleUtil.handleError(errorCode, errorMsg);
                }
            });

        });

        mTopSwitchButton.setOnClickNotPerformToggle(() -> {

            boolean waitToOpen = !mTopSwitchButton.isChecked();


            ConversionConfigSettingRequest conversationsRequest = new ConversionConfigSettingRequest();
            ConversionConfigSettingParticipant participant = new ConversionConfigSettingParticipant(mChatIdentifier, mDomainId, mChatInfoType);

            conversationsRequest.setParticipant(participant);
            conversationsRequest.setStickyEnabled(waitToOpen);
            conversationsRequest.setLanguage(mStrTranslationShortName);

            ConfigSettingsManager.INSTANCE.setConversationSettingRemote(conversationsRequest, new BaseCallBackNetWorkListener() {
                @Override
                public void onSuccess() {
                    setSessionTopSetting(waitToOpen);

                    if (SessionType.LightApp.equals(mChatInfoType)) {
                        refreshLightAppTop();

                    }
                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    ErrorHandleUtil.handleError(errorCode, errorMsg);
                }
            });


        });

        //退出群聊天
        mExitGroupView.setOnClickListener(v -> {

            AtworkAlertDialog atworkAlertDialog = new AtworkAlertDialog(getActivity(), AtworkAlertDialog.Type.SIMPLE);

            if (isYouOwner()) {
                atworkAlertDialog
                        .setContent(R.string.dismiss_discussion_alert_tip)
                        .setClickBrightColorListener(dialog -> dismissDiscussion());


            } else {
                atworkAlertDialog
                        .setContent(R.string.exit_discussion_alert_tip)
                        .setClickBrightColorListener(dialog -> quitDiscussion());
            }
            atworkAlertDialog.show();
        });

        //消息漫游事件
        mHistoryMessageView.setOnClickListener(v -> historySelectDialog());

        mGridView.setOnItemClickListener((parent, view, position, id) -> {


            if (CanOperationType.CanAddAndRemove.equals(mCanOperationType)) {
                if (position == 0) {
                    addOrRemoveListener.add();
                    return;
                } else if (position == 1) {
                    addOrRemoveListener.remove();
                    return;
                }


            } else if (CanOperationType.OnlyCanAdd.equals(mCanOperationType)) {
                if (position == 0) {
                    addOrRemoveListener.add();
                    return;
                }
            }

            //删除模式下无法进入个人详情
            if (!mChatInfoUserListBaseAdapter.isRemoveMode()) {
                ShowListItem contact = (ShowListItem) mChatInfoUserListBaseAdapter.getItem(position);

                UserManager.getInstance().queryUserByUserId(mActivity, contact.getId(), contact.getDomainId(), new UserAsyncNetService.OnQueryUserListener() {
                    @Override
                    public void onSuccess(@NonNull User user) {
                        if (null != getActivity()) {
                            startActivityRouteBack(PersonalInfoActivity.getIntent(getActivity().getApplicationContext(), user), false);

                        }
                    }

                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        ErrorHandleUtil.handleError(errorCode, errorMsg);
                    }

                });


            }

        });

        //修改群名称操作
        mRlNameInfo.setOnClickListener(v -> {
            if (null != mDiscussion) {
                startActivity(DiscussionModifyActivity.getIntent(getActivity(), mDiscussion, DiscussionModifyFragment.DiscussionModifyType.NAME_MODIFY));
            }
        });

        //群二维码
        mRlDiscussionQrcode.setOnClickListener(v -> {

            if (null != mDiscussion) {
                startActivity(DiscussionQrcodeActivity.getIntent(mActivity, mDiscussion));

            }
        });


        //修改群信息操作
        mRlDetailInfo.setOnClickListener(v -> {

            if (mDiscussion != null) {
                startActivity(DiscussionModifyActivity.getIntent(getActivity(), mDiscussion, DiscussionModifyFragment.DiscussionModifyType.DETAIL_MODIFY));
            }
        });


        //返回事件
        getView().findViewById(R.id.title_bar_common_back).setOnClickListener(v -> onBackPressed());

        //免打扰
        mMessageNoDisturbingSwitcher.setOnClickNotPerformToggle(() -> {
            boolean waitToOpen = !mMessageNoDisturbingSwitcher.isChecked();
            boolean waitToNotifyEnabled = !waitToOpen;


            ConversionConfigSettingRequest conversationsRequest = new ConversionConfigSettingRequest();
            ConversionConfigSettingParticipant participant = new ConversionConfigSettingParticipant(mChatIdentifier, mDomainId, mChatInfoType);

            conversationsRequest.setParticipant(participant);
            conversationsRequest.setNotifyEnabled(waitToNotifyEnabled);
            conversationsRequest.setLanguage(mStrTranslationShortName);

            ConfigSettingsManager.INSTANCE.setConversationSettingRemote(conversationsRequest, new BaseCallBackNetWorkListener() {
                @Override
                public void onSuccess() {
                    setSessionShieldSetting(waitToOpen);
                    checkDiscussionHelperHideStatus();
                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    ErrorHandleUtil.handleError(errorCode, errorMsg);
                }
            });


        });

        //同步至微信企业号
        mSwitchBtnSyncWeChat.setOnClickNotPerformToggle(() -> {
            boolean isOpen = !mSwitchBtnSyncWeChat.isChecked();
            if (SessionType.User.equals(mChatInfoType)) {

                ConversionConfigSettingRequest conversationsRequest = new ConversionConfigSettingRequest();
                ConversionConfigSettingParticipant participant = new ConversionConfigSettingParticipant(mChatIdentifier, mDomainId, mChatInfoType);


                conversationsRequest.setParticipant(participant);
                conversationsRequest.setWeixinSyncEnabled(isOpen);

                ConfigSettingsManager.INSTANCE.setConversationSettingRemote(conversationsRequest, new BaseCallBackNetWorkListener() {
                    @Override
                    public void onSuccess() {
                        mSwitchBtnSyncWeChat.setChecked(isOpen);
                        LoginUserInfo.getInstance().setWeChatConversationSettings(mActivity, mChatIdentifier, isOpen);
                    }

                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        if (isAdded()) {
                            wechatSettingErrorToast(isOpen);
                        }

                    }
                });


                return;
            }

            if (SessionType.Discussion.equals(mChatInfoType)) {
                DiscussionSettingsRequest request = new DiscussionSettingsRequest();
                request.mWeChatSyncEnable = isOpen;

                DiscussionManager.getInstance().setDiscussionSettings(mActivity, mChatIdentifier, request, new UserSyncNetService.OnUserConversationsListener() {
                    @Override
                    public void onSetConversationsSuccess() {
                        mSwitchBtnSyncWeChat.setChecked(isOpen);
                        LoginUserInfo.getInstance().setWeChatConversationSettings(mActivity, mChatIdentifier, isOpen);
                    }

                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        if (isAdded()) {
                            wechatSettingErrorToast(isOpen);
                        }
                    }
                });
            }
        });

        //是否添加水印
        mWatermarkSwitcher.setOnClickNotPerformToggle(() -> {
            boolean isOpen = !mWatermarkSwitcher.isChecked();
            if (mChatInfoType.equals(SessionType.Discussion)) {

                DiscussionSettingsRequest request = new DiscussionSettingsRequest();
                request.mWatermarkEnable = isOpen;

                DiscussionManager.getInstance().setDiscussionSettings(mActivity, mChatIdentifier, request, new UserSyncNetService.OnUserConversationsListener() {
                    @Override
                    public void onSetConversationsSuccess() {
                        AtworkToast.showToast(getString(R.string.setting_success));
                        handleWatermarkAction(isOpen);
                    }

                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        ErrorHandleUtil.handleError(errorCode, errorMsg);

                    }
                });
            }
        });


        //群头像
        mRlDiscussionAvatar.setOnClickListener(v -> startChangeAvatar());

        //转让群主
        mRlOwnerTransfer.setOnClickListener(v -> {
            SelectedContactList.clear();
            DiscussionMemberSelectControlAction discussionMemberSelectControlAction = new DiscussionMemberSelectControlAction();
            discussionMemberSelectControlAction.setDiscussionId(mDiscussion.mDiscussionId);
            discussionMemberSelectControlAction.setSelectMode(DiscussionMemberSelectActivity.Mode.SINGLE);

            Intent intent = DiscussionMemberSelectActivity.getIntent(getActivity(), discussionMemberSelectControlAction);
            startActivityForResult(intent, RESULT_TRANSFER_OWNER);
        });

        //群文件设置
        mDiscussionFileSettingView.setOnClickListener(v -> {
            DropboxConfig dropboxConfig = DropboxConfigManager.getInstance().syncGetDropboxConfigBySourceId(mActivity, mDiscussion.mDiscussionId);

            Intent intent = DropboxRWSettingActivity.getIntent(getActivity(), mDiscussion.getId(), mDiscussion.getDomainId(), Dropbox.SourceType.Discussion, dropboxConfig.mReadOnly);
            startActivity(intent);
        });

        //是否只有群主可添加成员
        mSwitchBtnOwnerOnlyAdd.setOnClickNotPerformToggle(() -> {
            boolean isOpen = !mSwitchBtnOwnerOnlyAdd.isChecked();

            DiscussionSettingsRequest request = new DiscussionSettingsRequest();
            request.mOwnerControl = isOpen;

            DiscussionManager.getInstance().setDiscussionSettings(mActivity, mChatIdentifier, request, new UserSyncNetService.OnUserConversationsListener() {
                @Override
                public void onSetConversationsSuccess() {
                    updateSwitchBtnOwnOnlyAddStatus(mDiscussion, isOpen);

                    AtworkToast.showToast(getString(R.string.setting_success));
                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    ErrorHandleUtil.handleError(errorCode, errorMsg);

                }
            });

        });

        //历史群聊
        mRlMessageHistory.setOnClickListener(v -> {
            FragmentActivity activity = getActivity();
            if (null != mApp && null != activity) {
                Intent intent = MessageHistoryActivity.Companion.getIntent(activity, new BaseMessageHistoryActivity.MessageHistoryViewAction(mApp, null));
                startActivity(intent);
            }
        });

        //查找聊天内容
        mDiscussionSearchChatContentView.setOnClickListener(view -> {
            SearchChatContentActivity.Companion.startActivity(mActivity, mChatIdentifier);
        });

    }

    private void checkDiscussionHelperHideStatus() {
        if (SessionType.Discussion.equals(mChatInfoType)) {
//            PersonalShareInfo.getInstance().setHideDiscussionHelper(AtworkApplicationLike.baseContext, false);
        }
    }

//    private void refreshChatTop(String sessionIdentifier) {
//        if (mTopSwitchButton.isChecked()) {
//            if (null == mSession) {
//                ChatSessionDataWrap.getInstance().setTop(sessionIdentifier, SessionTop.NONE);
//
//            } else {
//                ChatSessionDataWrap.getInstance().setTop(mSession, SessionTop.NONE);
//                ChatDetailExposeBroadcastSender.changeSession(getActivity(), mSession);
//
//            }
//        } else {
//            if (null == mSession) {
//                ChatSessionDataWrap.getInstance().setTop(sessionIdentifier, SessionTop.LOCAL_TOP);
//            } else {
//
//                ChatSessionDataWrap.getInstance().setTop(mSession, SessionTop.LOCAL_TOP);
//                ChatDetailExposeBroadcastSender.changeSession(getActivity(), mSession);
//
//            }
//        }
//
//        mTopSwitchButton.toggle();
//    }

    private void refreshLightAppTop() {
        AppManager.getInstance().queryApp(mActivity, mChatIdentifier, mOrgId, new AppManager.GetAppFromMultiListener() {
            @Override
            public void onSuccess(@NonNull App app) {
                Session session = ChatSessionDataWrap.getInstance().getSession(mChatIdentifier, null);
                if (session == null) {
                    session = new Session();
                }
                session.identifier = mChatIdentifier;
                session.name = app.getTitleI18n(BaseApplicationLike.baseContext);
                session.type = SessionType.LightApp;
                session.lastTimestamp = TimeUtil.getCurrentTimeInMillis();
                session.entryType = Session.EntryType.To_URL;
                if (app instanceof LightApp) {
                    if (((LightApp) app).mAccessEndPoints != null) {
                        session.entryValue = ((LightApp) app).mAccessEndPoints.get(LightApp.MOBILE_ENDPOINT);
                    }

                }

//                if (mTopSwitchButton.isChecked()) {
//                    session.top = SessionTop.NONE;
//                } else {
//                    session.top = SessionTop.LOCAL_TOP;
//                }
//                mTopSwitchButton.toggle();
                if (!ChatSessionDataWrap.getInstance().mSessionMap.containsKey(mChatIdentifier)) {
                    ChatSessionDataWrap.getInstance().getSessions().add(session);
                }
                ChatDaoService.getInstance().sessionRefresh(session);
                SessionRefreshHelper.notifyRefreshSession();

            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleError(errorCode, errorMsg);
            }
        });
    }

    private void alertBecomeOwner() {
        AtworkAlertDialog alertAsk1 =
                new AtworkAlertDialog(getActivity(), AtworkAlertDialog.Type.SIMPLE)
                        .setContent(R.string.internal_discussion_empty_owner_set_tip)
                        .setClickBrightColorListener(dialog -> alertBecomeOwnerAgain());

        alertAsk1.show();
    }

    public void alertBecomeOwnerAgain() {
        String content = getString(R.string.internal_discussion_empty_owner_set_tip_again, mDiscussion.mName);

        AtworkAlertDialog alertAsk2 =
                new AtworkAlertDialog(getActivity(), AtworkAlertDialog.Type.SIMPLE)
                        .setContent(content)
                        .setClickBrightColorListener(dialog -> {

                            ProgressDialogHelper progressDialogHelper = new ProgressDialogHelper(getActivity());
                            progressDialogHelper.show();


                            DiscussionManager.getInstance().becomeOwner(getActivity(), mDiscussion, new BaseCallBackNetWorkListener() {
                                @Override
                                public void onSuccess() {

                                    refreshDiscussionInfo(mDiscussion);
                                    refreshOwnerViewIfYouOwner();

                                    progressDialogHelper.dismiss();
                                    AtworkToast.showToast(getString(R.string.setting_success));

                                }

                                @Override
                                public void networkFail(int errorCode, String errorMsg) {
                                    progressDialogHelper.dismiss();
                                    ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Group, errorCode, errorMsg);

                                }
                            });
                        });

        alertAsk2.show();
    }

    private boolean isInternalDiscussionCanBeOwner(Discussion discussion) {
        return discussion.isInternalDiscussion() && OrganizationSettingsManager.getInstance().isInternalDiscussionOwnerCustomer(discussion.getOrgId());
    }


    public void wechatSettingErrorToast(boolean isOpen) {
        if (isOpen) {
            AtworkToast.showLongToast(getString(R.string.open_setting_we_chat_sync_fail));

        } else {
            AtworkToast.showLongToast(getString(R.string.close_setting_we_chat_sync_fail));

        }
    }

    private void registerBroadcast() {
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mRefreshChatInfoBroadcastReceiver, new IntentFilter(REFRESH_CHAT_INFO));
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mSideBroadcast, new IntentFilter(ACTION_FINISH));

        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mSessionInvalidBroadcast, new IntentFilter(DiscussionHelper.SESSION_INVALID));

    }

    private void unregisterBroadcast() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mSessionInvalidBroadcast);
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mRefreshChatInfoBroadcastReceiver);
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mSideBroadcast);
    }

    private void quitDiscussion() {
        DiscussionManager.getInstance().quitDiscussion(getActivity(), LoginUserInfo.getInstance().getLoginUserId(getActivity()), mDiscussion.mDiscussionId, new DiscussionAsyncNetService.HandledResultListener() {

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Group, errorCode, errorMsg);
            }

            @Override
            public void success() {
                AtworkToast.showToast(getResources().getString(R.string.exit_discussion_success));
                startActivity(MainActivity.getMainActivityIntent(getActivity(), false));
                ChatInfoFragment.this.finish();
            }


        });
    }

    private void dismissDiscussion() {
        mHandlingDismiss = true;
        mProgressDialogHelper.show();
        DiscussionManager.getInstance().dismissDiscussion(getActivity(), mDiscussion.mDiscussionId, new DiscussionAsyncNetService.HandledResultListener() {

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                mHandlingDismiss = false;

                if (isAdded()) {
                    mProgressDialogHelper.dismiss();
                }


                ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Group, errorCode, errorMsg);

            }

            @Override
            public void success() {
                toastOver(getResources().getString(R.string.dismiss_discussion_success));

                if (isAdded()) {
                    mProgressDialogHelper.dismiss();
                    startActivity(MainActivity.getMainActivityIntent(getActivity(), false));

                    ChatInfoFragment.this.finish();
                }
            }


        });

    }

    private void modifyDiscussionAvatar() {
        DiscussionManager.getInstance().modifyDiscussion(getActivity(), mDiscussion, new DiscussionAsyncNetService.HandledResultListener() {
            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Group, errorCode, errorMsg);

                dismissUpdatingHelper();

            }

            @Override
            public void success() {
                toast(R.string.modify_group_info_success);

                dismissUpdatingHelper();

                setDiscussionAvatarView();
            }


        });
    }


    /**
     * Description设置群聊简介的最大宽度
     */
    private void setDetailInfoTvMaxWidth() {
        mDiscussionDetailLabel.getViewTreeObserver().addOnPreDrawListener(() -> {
            //动态设定群简介textview的最大宽度
            mDiscussionDetailInfoView.setMaxWidth(ScreenUtils.getScreenWidth(getActivity())
                    - mDiscussionDetailLabel.getMeasuredWidth() - DensityUtil.dip2px(60));
            return true;
        });

    }


    @Override
    protected boolean onBackPressed() {
        if (SessionType.Discussion.equals(mChatInfoType) && !mDiscussionInfoView.isShown()) {
            smallMode();
        } else {
            finish();
        }
        return false;
    }


    public static void finishView(Context context, String sessionId) {
        Intent intent = new Intent(ACTION_FINISH);
        intent.putExtra(ChatInfoActivity.DATA_CHAT_IDENTIFIER, sessionId);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    //加人减人监听
    public interface AddOrRemoveListener {

        void add();

        void remove();

        void removeUser(ShowListItem contact);
    }


}
