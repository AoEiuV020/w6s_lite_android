package com.foreveross.atwork.modules.contact.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.threadGear.DbThreadPoolExecutor;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.organization.OrganizationAsyncNetService;
import com.foreveross.atwork.api.sdk.organization.responseModel.OrgApplyingCheckResponseJson;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.broadcast.NetworkBroadcastReceiver;
import com.foreveross.atwork.component.CommonPopSelectListWithTitleDialogFragment;
import com.foreveross.atwork.component.CommonPopSelectWithTitleData;
import com.foreveross.atwork.component.TitleItemView;
import com.foreveross.atwork.component.UnreadImageView;
import com.foreveross.atwork.component.listview.AbsListViewScrollDetector;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.manager.OrganizationSettingsManager;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.contact.ContactViewMode;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.model.user.SelectedContactList;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ContactHelper;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.NetworkStatusUtil;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.manager.FriendManager;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.manager.OrganizationSettingsHelper;
import com.foreveross.atwork.modules.app.activity.AppListActivity;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity;
import com.foreveross.atwork.modules.chat.component.SessionItemView;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment;
import com.foreveross.atwork.modules.chat.fragment.GuidePageDialogFragment;
import com.foreveross.atwork.modules.chat.model.EntrySessionRequest;
import com.foreveross.atwork.modules.chat.service.ChatService;
import com.foreveross.atwork.modules.contact.activity.EmployeeTreeActivity;
import com.foreveross.atwork.modules.contact.activity.PersonalInfoActivity;
import com.foreveross.atwork.modules.contact.adapter.ContactListArrayListAdapter;
import com.foreveross.atwork.modules.contact.adapter.ContactListInSimpleModeAdapter;
import com.foreveross.atwork.modules.contact.component.ContactHeadView;
import com.foreveross.atwork.modules.contact.component.ContactTitleView;
import com.foreveross.atwork.modules.contact.component.SimpleModeSideBar;
import com.foreveross.atwork.modules.contact.data.StarUserListDataWrap;
import com.foreveross.atwork.modules.contact.loader.UserListLoader;
import com.foreveross.atwork.modules.contact.manager.ContactSimpleModeManager;
import com.foreveross.atwork.modules.file.service.FileTransferService;
import com.foreveross.atwork.modules.friend.activity.MyFriendsActivity;
import com.foreveross.atwork.modules.friend.utils.FriendLetterListHelper;
import com.foreveross.atwork.modules.discussion.activity.DiscussionListActivity;
import com.foreveross.atwork.modules.group.activity.UserSelectActivity;
import com.foreveross.atwork.modules.group.component.SelectContactHead;
import com.foreveross.atwork.modules.group.listener.SelectedChangedListener;
import com.foreveross.atwork.modules.group.module.SelectToHandleAction;
import com.foreveross.atwork.modules.group.module.TransferMessageControlAction;
import com.foreveross.atwork.modules.group.module.UserSelectControlAction;
import com.foreveross.atwork.modules.main.activity.MainActivity;
import com.foreveross.atwork.modules.main.helper.NetworkErrorViewManager;
import com.foreveross.atwork.modules.search.model.SearchContent;
import com.foreveross.atwork.support.NoticeTabAndBackHandledFragment;
import com.foreveross.atwork.tab.helper.BeeworksTabHelper;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.theme.model.Theme;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;

/**
 * Created by lingen on 15/3/20.
 * Description: 聊天界面的Fragment
 */
//TODO 实现不好, 待优化, 尤其星标联系人那块
public class ContactFragment extends NoticeTabAndBackHandledFragment implements LoaderManager.LoaderCallbacks<List<User>>, SelectedChangedListener, NetworkBroadcastReceiver.NetworkChangedListener {

    public static final String TAB_ID = "contact";
    public static final String CONTACT_DATA_CHANGED = "CONTACT_DATA_CHANGED";
    public static final String REFRESH_ORGANIZATION = "REFRESH_ORGANIZATION";
    public static final String SHOW_MAIN_TITLE_BAR = "SHOW_MAIN_TITLE_BAR";
    public static final String SHOW_COMMON_TITLE_BAR = "SHOW_COMMON_TITLE_BAR";

    public static final int BASE_SESSION_NUM = 4;
    public static final int INCREASE_SESSION_NUM = 10;
    private static final String TAG = ContactFragment.class.getSimpleName();
    private static StarUserListDataWrap sStarUserListDataWrap = StarUserListDataWrap.getInstance();

    /**
     * 通讯录变更广播
     */
    private BroadcastReceiver mStarUserChangeBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (CONTACT_DATA_CHANGED.equals(intent.getAction())) {
                loadStartUserData();
                return;
            }

            if (REFRESH_ORGANIZATION.equals(intent.getAction())) {
                loadLocalOrgData();
                return;
            }

            if (FriendManager.ACTION_REFRESH_NEW_FRIEND_APPLIES.equals(intent.getAction())) {
                mNewFriendsApplyView.setUnreadNum(PersonalShareInfo.getInstance().getNewApplyFriendUserIds(BaseApplicationLike.baseContext).size());
                return;
            }

        }
    };


    /**
     * 通讯录LISTVIEW
     */
    private ListView mContactListView;
    private SimpleModeSideBar mSimpleModeSideBar;

    private ContactHeadView mFileTransferHeadView;
    private ContactHeadView mDiscussionHeadView;
    private ContactHeadView mServiceAppHeadView;
    private ContactHeadView mMyFriendsView;
    private ContactHeadView mNewFriendsApplyView;

    private View mFileTransferTitleView;
    private View mStartUserTitleView;
    private View mSimpleModeTitleView;
    private View mFuncTitleView;
    private View mOrgTitleView;
    private View mSessionTitleView;
    private TitleItemView mMoreSessionView;
    private UnreadImageView mIvSwitchMode;
    private ImageView mIvSearch;

    private ContactListArrayListAdapter mStarContactListAdapter;
    private ContactListInSimpleModeAdapter mContactListInSimpleModeAdapter;

    //转发模式下的SESSION
    private List<Session> mSessionList = new ArrayList<>();
    private List<Organization> mUserOrgLists;
    private List<OrgApplyingCheckResponseJson.ApplyingOrg> mApplyingOrgList = new ArrayList<>();


    private List<SessionItemView> mSessionViewList = new ArrayList<>();

    //组织(包括有管理权限跟普通) 头部 view 集合
    private List<ContactHeadView> mUserOrgHeadViewList;
    //组织申请头部 view 集合
    private List<ContactHeadView> mApplyHeadViewList = new ArrayList<>();
    //所有组织(申请, 管理, 普通)的头部 view 集合
    private List<ContactHeadView> mOrgContactViewList = new ArrayList<>();

    private List<ShowListItem> mCallbackContacts;
    private List<ShowListItem> mContactListInSimpleMode;
    private List<String> mNotAllowedSelectedContacts = new ArrayList<>();

    private UserSelectControlAction mUserSelectControlAction;

    //当前是否是选人模式
    private UserSelectActivity.SelectMode mSelectMode;


    private UserSelectActivity.SelectAction mSelectAction;

    private SelectToHandleAction mSelectToHandleAction;

    private int mNum = 0;

    private boolean mIsSingleContact;

    private int mShareSessionListNum;

    private boolean mIsFragmentVisible = false;

    private View mVMainTitleBar;
    private View mVFakeStatusBar;
    private TextView mTvMainTitleView;
    private NetworkErrorViewManager mNetworkErrorViewManager;

    private View mVCommonTitleBar;
    private ImageView mIvBack;
    private TextView mTvCommonTitleBar;
    private TextView mTvSlideToast;

    private boolean mShowMainTitleBar;
    private boolean mShowCommonTitleBar;

    private boolean mIsFileTransferSelect;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        registerBroadcast();

        if (getArguments() != null) {
            mUserSelectControlAction = getArguments().getParcelable(UserSelectActivity.DATA_USER_SELECT_PARAMS);
            if (null != mUserSelectControlAction) {
                mSelectMode = mUserSelectControlAction.getSelectMode();
                mSelectAction = mUserSelectControlAction.getSelectAction();
                mCallbackContacts = SelectedContactList.getContactList();
                mSelectToHandleAction = mUserSelectControlAction.getSelectToHandleAction();

                mIsSingleContact = (1 == mUserSelectControlAction.getMax());


                if (UserSelectActivity.SelectMode.SELECT.equals(mSelectMode)) {
                    if (mActivity instanceof UserSelectActivity) {
                        UserSelectActivity userSelectActivity = (UserSelectActivity) mActivity;
                        mNotAllowedSelectedContacts = userSelectActivity.getNotAllowedSelectedStringList();
                    }
                }

            }


            mShowMainTitleBar = getArguments().getBoolean(SHOW_MAIN_TITLE_BAR, true);
            mShowCommonTitleBar = getArguments().getBoolean(SHOW_COMMON_TITLE_BAR, false);
        }

        if (null == mSelectMode) {
            mSelectMode = UserSelectActivity.SelectMode.NO_SELECT;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handleTitleViewBarShow();

        mApplyHeadViewList.clear();
        loadLocalOrgData();

//        if (UserSelectActivity.SelectMode.SEND_MESSAGES.equals(mSelectMode)) {
////            loadSessions();
//        }

        mStarContactListAdapter.needLine(false);
        mStarContactListAdapter.setMediumBoldMode(true);
        mStarContactListAdapter.setSingleContact(mIsSingleContact);

        if (isContactSimpleViewMode()) {
            mContactListView.setAdapter(mContactListInSimpleModeAdapter);
        } else {


            mContactListView.setAdapter(mStarContactListAdapter);
        }

        registerListener();

        mNetworkErrorViewManager.registerImReceiver(mActivity);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        setTitle();
        if (ListUtil.isEmpty(mUserOrgLists)) {
            loadLocalOrgData();
        } else {
            checkOrgUpdate();
        }
        checkOrgApplying();
        loadStartUserData();

    }



    @Override
    public void onResume() {
        super.onResume();
        refreshListViewTotally();
        checkStarUserOnline();
        calibrateDataInContactSimpleMode();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        unregisterBroadcast();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    private void calibrateDataInContactSimpleMode() {
        ContactSimpleModeManager.INSTANCE.calibrateData(result -> {
            if(result) {
                FriendManager.getInstance().clear();
                DiscussionManager.getInstance().clear();

                refreshContactInSimpleModeViewData();
            }
        });
    }

    private void handleTitleViewBarShow() {
        if (mShowMainTitleBar) {
            mVMainTitleBar.setVisibility(View.VISIBLE);
            return;
        }

        mVMainTitleBar.setVisibility(View.GONE);

        if (mShowCommonTitleBar) {
            mVCommonTitleBar.setVisibility(View.VISIBLE);
        } else {
            mVCommonTitleBar.setVisibility(View.GONE);

        }

    }

    private void handleSimModeSideBarShow() {
        if (isContactSimpleViewMode()) {
            mSimpleModeSideBar.setVisibility(View.VISIBLE);
        } else {
            mSimpleModeSideBar.setVisibility(View.GONE);

        }
    }


    @Override
    protected void findViews(View view) {
        mVMainTitleBar = view.findViewById(R.id.contact_main_title_bar);
        mTvMainTitleView = mVMainTitleBar.findViewById(R.id.title_bar_main_title);
        mVFakeStatusBar = mVMainTitleBar.findViewById(R.id.v_fake_statusbar);
        mIvSwitchMode = mVMainTitleBar.findViewById(R.id.unread_imageview);
        mIvSearch = mVMainTitleBar.findViewById(R.id.iv_search);
        mNetworkErrorViewManager = new NetworkErrorViewManager(mVMainTitleBar);

        mVCommonTitleBar = view.findViewById(R.id.contact_common_title_bar);
        mIvBack = mVCommonTitleBar.findViewById(R.id.title_bar_common_back);
        mTvCommonTitleBar = mVCommonTitleBar.findViewById(R.id.title_bar_common_title);


        mContactListView = view.findViewById(R.id.contact_list_view);
        mSimpleModeSideBar = view.findViewById(R.id.sidebar);
        mTvSlideToast = view.findViewById(R.id.tv_slide_toast);

        mSimpleModeSideBar.setTextView(mTvSlideToast);

        mStarContactListAdapter = new ContactListArrayListAdapter(getActivity(), selectMode());
        mContactListInSimpleModeAdapter = new ContactListInSimpleModeAdapter(getActivity(), selectMode(), mIsSingleContact);

        mFileTransferTitleView = new ContactTitleView(getActivity(), R.string.device);
        mStartUserTitleView = new ContactTitleView(getActivity(), R.string.avatar_contact);
        mSimpleModeTitleView = new ContactTitleView(getActivity(), -1);
        mOrgTitleView = new ContactTitleView(getActivity(), R.string.contact_org_title);
        mFuncTitleView = new ContactTitleView(getActivity(), -1);

        mSessionTitleView = new ContactTitleView(getActivity(), R.string.latest_sessions);
        mMoreSessionView = new TitleItemView(getActivity());

        mMoreSessionView.setTitle(getResources().getString(R.string.more_chat_session));
        mMoreSessionView.center();

        mFileTransferHeadView = ContactHeadView.getInstance(this.getActivity(), R.mipmap.icon_file_transfer, getResources().getString(R.string.file_transfer));
        mDiscussionHeadView = ContactHeadView.getInstance(this.getActivity(), R.mipmap.icon_group_chat, getResources().getString(R.string.title_discussion));
        mServiceAppHeadView = ContactHeadView.getInstance(this.getActivity(), R.mipmap.icon_service_app, getResources().getString(R.string.my_service_app));
        mMyFriendsView = ContactHeadView.getInstance(getContext(), R.mipmap.icon_my_friend, getString(R.string.workplus_friends));
        mNewFriendsApplyView = ContactHeadView.getInstance(getContext(), R.mipmap.icon_my_friend, getString(R.string.new_friend_in_btn));


        mIvSwitchMode.setIcon(R.mipmap.icon_switch_common);
        if (-1 == AtworkConfig.CONTACT_CONFIG.getOnlyVersionContactViewMode()) {
            mIvSwitchMode.setVisibility(View.VISIBLE);

        } else {
            mIvSwitchMode.setVisibility(View.GONE);

        }

        if (selectMode()) {
            mFileTransferHeadView.showSelect();

            checkInitFileTransferStatus();
        }

        mContactListView.setDivider(null);

    }

    private void checkInitFileTransferStatus() {
        if (!ListUtil.isEmpty(mCallbackContacts)) {
            for (ShowListItem selectContact : mCallbackContacts) {
                if (FileTransferService.INSTANCE.isClickFileTransfer(selectContact)) {
                    refreshFileTransferHeaderViewSelectStatus(true);
                    break;
                }
            }
        }
    }

    @Override
    protected View getFakeStatusBar() {
        return mVFakeStatusBar;
    }

    @Override
    public void onDomainSettingChange() {
        mApplyHeadViewList.clear();
        mContactListView.removeHeaderView(mMyFriendsView);
        mContactListView.removeHeaderView(mNewFriendsApplyView);
        loadLocalOrgData();
    }

    @Override
    public void onOrgSettingChange() {
        sortViewListHeader(mUserOrgHeadViewList);
    }

    public void unregisterBroadcast() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mStarUserChangeBroadcast);
    }

    public void registerBroadcast() {
        IntentFilter intent = new IntentFilter(CONTACT_DATA_CHANGED);
        intent.addAction(REFRESH_ORGANIZATION);
        intent.addAction(FriendManager.ACTION_REFRESH_NEW_FRIEND_APPLIES);

        //注册本地广播
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mStarUserChangeBroadcast, intent);
    }

    public static void contactsDataChanged(Context context) {
        Intent intent = new Intent(ContactFragment.CONTACT_DATA_CHANGED);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void orgDataChanged(Context context) {
        Intent intent = new Intent(ContactFragment.REFRESH_ORGANIZATION);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        WorkplusStatusBarHelper.setCommonStatusBar(getActivity());


        mIsFragmentVisible = isVisibleToUser;
        //如果fragment显示可见
        if (isVisibleToUser) {
            if (ListUtil.isEmpty(mUserOrgLists)) {
                loadLocalOrgData();
            } else {
                checkOrgUpdate();
            }
            AtworkUtil.checkUpdate(mActivity, false);
            getSystemNavigation();
            checkOrgApplying();
            OrganizationSettingsHelper.getInstance().checkOrgSettingsUpdate(mActivity, 0);
            checkStarUserOnline();
            calibrateDataInContactSimpleMode();


            checkShowGuidePage();

        }
    }

    @Override
    protected void refreshNetworkStatusUI() {
        if (mNetworkErrorViewManager != null) {
            mNetworkErrorViewManager.refreshNetworkStatusUI(NetworkStatusUtil.isNetworkAvailable(BaseApplicationLike.baseContext));
        }
    }

    private void checkStarUserOnline() {
        if (!DomainSettingsManager.getInstance().handleUserOnlineFeature()) {
            return;
        }
        sStarUserListDataWrap.checkStarUserOnline(mActivity, onlineList -> {

            refreshAdapterLightly();

        });
    }

    private void refreshAdapterLightly() {
        if (isContactSimpleViewMode()) {
            if (null != mContactListInSimpleModeAdapter) {
                mContactListInSimpleModeAdapter.notifyDataSetChanged();
            }
        } else {

            if (null != mStarContactListAdapter) {
                mStarContactListAdapter.notifyDataSetChanged();
            }
        }
    }

    public void checkOrgApplying() {
        //只有在"非选择"模式, 且该 fragment 为当前选择的 fragment 才检查更新 org 申请列表
        if (!mIsFragmentVisible || !UserSelectActivity.SelectMode.NO_SELECT.equals(mSelectMode)) {
            return;
        }

        OrganizationAsyncNetService.getInstance().checkApplyingOrgs(getActivity(), new OrganizationAsyncNetService.OnCheckOrgApplyingListener() {
            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleTokenError(errorCode, errorMsg);
            }

            @Override
            public void onSuccess(List<OrgApplyingCheckResponseJson.ApplyingOrg> applyingOrgList) {
                //检查有没最新的检查更新列表
                if (mApplyingOrgList.size() != applyingOrgList.size() || !mApplyingOrgList.containsAll(applyingOrgList)
                        || applyingOrgList.size() != mApplyHeadViewList.size()) {
                    mApplyingOrgList.clear();
                    mApplyingOrgList.addAll(applyingOrgList);
                    mApplyHeadViewList.clear();
                    if (!DomainSettingsManager.getInstance().handleOrgApplyFeature()) {
                        return;
                    }

                    sortViewListHeader(mUserOrgHeadViewList);
                    for (OrgApplyingCheckResponseJson.ApplyingOrg applyingOrg : applyingOrgList) {
                        ContactHeadView contactHeadView = ContactHeadView.getInstance(mActivity, applyingOrg);
                        mApplyHeadViewList.add(contactHeadView);
                    }


                    sortViewListHeader(mUserOrgHeadViewList);


                }

            }


        });
    }

    public void checkOrgUpdate() {
        //只有在"非选择"模式, 且该 fragment 为当前选择的 fragment 才检查更新 org
        if (!mIsFragmentVisible || !UserSelectActivity.SelectMode.NO_SELECT.equals(mSelectMode)) {
            return;
        }

        OrganizationManager.getInstance().checkOrganizationsUpdate(getActivity(), mUserOrgLists, new OrganizationManager.OnCheckOrgListUpdateListener() {
            @Override
            public void onRefresh(OrganizationManager.CheckOrgUpdateResult checkOrgUpdateResult) {
                if (checkOrgUpdateResult.shouldUpdate) {
                    mUserOrgLists = checkOrgUpdateResult.orgList;
                    notifyHeadViewListChanged();

                }

            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleTokenError(errorCode, errorMsg);
            }
        });
    }


    private void setAndRefreshAdapter() {
        if (isContactSimpleViewMode()) {
            if (null != mContactListInSimpleModeAdapter) {
                mContactListView.setAdapter(mContactListInSimpleModeAdapter);
                mContactListInSimpleModeAdapter.notifyDataSetChanged();

            }
            return;
        }


        if (null != mStarContactListAdapter) {
            mContactListView.setAdapter(mStarContactListAdapter);
            mStarContactListAdapter.notifyDataSetChanged();
        }
    }

    private void refreshContactInSimpleModeViewData() {

        ContactSimpleModeManager.INSTANCE.fetchData(dataList -> {

            List<ShowListItem> dataListFiltered = new ArrayList<>();
            if (shouldFilterDiscussionInSimpleModeView()) {
                for (ShowListItem data : dataList) {
                    if (data instanceof User || data instanceof Employee) {
                        dataListFiltered.add(data);
                    }
                }

            } else {
                dataListFiltered.addAll(dataList);
            }

            refreshSelectedStatus(dataListFiltered);

            mContactListInSimpleMode = dataListFiltered;

            mContactListInSimpleModeAdapter.refreshData(dataListFiltered);
            mContactListInSimpleModeAdapter.setNotAllowedSelectedContacts(mNotAllowedSelectedContacts);

            mSimpleModeSideBar.refreshLetters(FriendLetterListHelper.getFirstLetterLinkedSet(dataListFiltered));

            handleSimModeSideBarShow();



        });
    }


    private boolean shouldFilterDiscussionInSimpleModeView() {
        if (sendMessageMode()) {
            return false;
        }

        if (selectMode()) {
            return true;
        }

        return false;
    }

    @SuppressLint("StaticFieldLeak")
    private void loadSessions() {
        new AsyncTask<Void, Void, List<Session>>() {
            @Override
            protected List<Session> doInBackground(Void... params) {
                List<Session> sessions = new ArrayList<>();
                sessions.addAll(ChatSessionDataWrap.getInstance().getSessions());
                if (sessions.size() == 0) {
                    sessions = ChatService.queryAllSessionsDb();
                }
                sessions = getSessionsCanForward(sessions);

                ChatSessionDataWrap.getInstance().sortAvoidShaking(sessions);
                return sessions;
            }

            @Override
            protected void onPostExecute(List<Session> sessions) {
                mSessionList.clear();
                mSessionViewList.clear();
                mSessionList.addAll(sessions);
                for (final Session session : sessions) {
                    SessionItemView sessionItemView = new SessionItemView(mActivity);
                    sessionItemView.refresh(session);
                    sessionItemView.notShowUnread();
                    mSessionViewList.add(sessionItemView);
                    sessionItemView.setOnClickListener(v -> {
                        if (getActivity() instanceof UserSelectActivity) {
                            UserSelectActivity userSelectActivity = (UserSelectActivity) getActivity();
//                            userSelectActivity.sendMessagesToSession(session);
                        }
                    });
                }
//                refreshSessionHead();
                sortViewListHeader(mUserOrgHeadViewList);
            }
        }.executeOnExecutor(DbThreadPoolExecutor.getInstance());
    }

    private List<Session> getSessionsCanForward(List<Session> sessions) {
        List<Session> forwardSessionList = new ArrayList<>();
        for (Session session : sessions) {
            if ((!(Session.WORKPLUS_SYSTEM.equals(session.identifier)) && (Session.EntryType.To_Chat_Detail == session.entryType || null == session.entryType))) {
                forwardSessionList.add(session);
            }
        }
        return forwardSessionList;
    }

    private List<SessionItemView> getFixNumSessionViewList() {
        int max = BASE_SESSION_NUM + INCREASE_SESSION_NUM * mNum;
        List<SessionItemView> sessionItemViews = new ArrayList<>();
//        if (UserSelectActivity.SendMode.SEND_LINK.equals(mSendMode)) {
//            handleWebShareSession(max, sessionItemViews);
//            return sessionItemViews;
//        }
        if (mSessionViewList.size() <= max) {
            sessionItemViews.addAll(mSessionViewList);
        } else {
            for (int i = 0; i < max; i++) {
                sessionItemViews.add(mSessionViewList.get(i));
            }
        }
        return sessionItemViews;
    }

    private void handleWebShareSession(int max, List<SessionItemView> sessionItemViews) {
        int count = mSessionViewList.size();
        if (count <= max) {
            sessionItemViews.addAll(mSessionViewList);
            return;
        }
        int i = 0;
        for (SessionItemView sessionItemView : mSessionViewList) {
            if (i >= max) {
                break;
            }
            if (sessionItemView.getSession().type.equals(SessionType.Discussion) || sessionItemView.getSession().type.equals(SessionType.User)) {
                sessionItemViews.add(sessionItemView);
                i++;
            }
        }

        for (SessionItemView sessionItemView : mSessionViewList) {
            if (sessionItemView.getSession().type.equals(SessionType.Discussion) || sessionItemView.getSession().type.equals(SessionType.User)) {
                mShareSessionListNum++;
            }
        }

    }


    private void getSystemNavigation() {
        BeeworksTabHelper.getInstance().getSystemNavigation(mActivity, BeeWorks.getInstance().getBeeWorksTabById(mActivity, mId));
    }


    private void loadStartUserData() {
        Loader loader = getLoaderManager().getLoader(0);
        if (null == loader) {
            loader = getLoaderManager().initLoader(0, null, this);
        }

        loader.forceLoad();
    }

    private void clearSessionHead() {
        mContactListView.removeHeaderView(mSessionTitleView);
        for (SessionItemView sessionItemView : mSessionViewList) {
            mContactListView.removeHeaderView(sessionItemView);
        }

        for (ContactHeadView contactHeadView : mOrgContactViewList) {
            mContactListView.removeHeaderView(contactHeadView);
        }

        mContactListView.removeHeaderView(mFuncTitleView);
        mContactListView.removeHeaderView(mOrgTitleView);
        mContactListView.removeHeaderView(mMoreSessionView);
        mContactListView.removeHeaderView(mDiscussionHeadView);

        if (AtworkConfig.SERVICE_APP_LIST_ENTRY) {
            mContactListView.removeHeaderView(mServiceAppHeadView);
        }




        mContactListView.removeHeaderView(mMyFriendsView);
        mContactListView.removeHeaderView(mNewFriendsApplyView);


        mContactListView.removeHeaderView(mFileTransferTitleView);
        mContactListView.removeHeaderView(mFileTransferHeadView);


        mContactListView.removeHeaderView(mSimpleModeTitleView);
        //默认隐藏头部
        removeStartUserTitleView();

    }


    private void sortViewListHeader(List<ContactHeadView> orgHeadViews) {
        clearSessionHead();
        mContactListView.setAdapter(null);

//        if (UserSelectActivity.SelectMode.SEND_MESSAGES.equals(mSelectMode)) {
//            refreshSessionHead();
//        }


        handleAddFuncHeadViews();

        //    ---    组织列表头部维护 start   ---

        mOrgContactViewList.clear();

        handleAddOrgHeadViews(orgHeadViews);

        //    ---    组织列表头部维护 end   ---

        handleAddFileTransferViews();

        handleRefreshViewInModes();

        addMainListViewTitleView();

        setAndRefreshAdapter();
    }

    private void handleRefreshViewInModes() {
        refreshArrowView();
        refreshStatusBtn();
        refreshAvatarSize();
        refreshHeight();
    }


    private void refreshArrowView() {
        if (selectMode()) {
            mFileTransferHeadView.hideArrow();
        } else {

            if (isContactSimpleViewMode()) {
                mFileTransferHeadView.hideArrow();

            } else {
                mFileTransferHeadView.showArrow();

            }
        }


        if (isContactSimpleViewMode()) {

            for (ContactHeadView orgHeaderView : mOrgContactViewList) {
                orgHeaderView.hideArrow();
            }
        } else {


            for (ContactHeadView orgHeaderView : mOrgContactViewList) {
                orgHeaderView.showArrow();
            }
        }


    }


    private void refreshStatusBtn() {
        if (isContactSimpleViewMode()) {

            for (ContactHeadView orgHeaderView : mOrgContactViewList) {
                ViewUtil.setRightMargin(orgHeaderView.getBtnStatus(), DensityUtil.dip2px( 20));

            }
        } else {


            for (ContactHeadView orgHeaderView : mOrgContactViewList) {
                ViewUtil.setRightMargin(orgHeaderView.getBtnStatus(), DensityUtil.dip2px(DensityUtil.getDimen( R.dimen.title_common_padding)));

            }
        }

    }

    private void refreshAvatarSize() {
        Context context = BaseApplicationLike.baseContext;
        if(isContactSimpleViewMode()) {
            int pxFrom36dp = DensityUtil.dip2px( 36);

            ViewUtil.setSize(mDiscussionHeadView.getContactListHeadAvatar(), pxFrom36dp, pxFrom36dp);
            ViewUtil.setSize(mNewFriendsApplyView.getContactListHeadAvatar(), pxFrom36dp, pxFrom36dp);
            ViewUtil.setSize(mFileTransferHeadView.getContactListHeadAvatar(), pxFrom36dp, pxFrom36dp);
            ViewUtil.setSize(mMyFriendsView.getContactListHeadAvatar(), pxFrom36dp, pxFrom36dp);

            for (ContactHeadView orgHeaderView : mOrgContactViewList) {
                ViewUtil.setSize(orgHeaderView.getContactListHeadAvatar(), pxFrom36dp, pxFrom36dp);
            }


        } else {
            float dpFromCommonSize = DensityUtil.getDimen( R.dimen.common_contact_item_img_size);
            int pxFromCommonSize = DensityUtil.dip2px( dpFromCommonSize);

            ViewUtil.setSize(mDiscussionHeadView.getContactListHeadAvatar(), pxFromCommonSize, pxFromCommonSize);
            ViewUtil.setSize(mNewFriendsApplyView.getContactListHeadAvatar(), pxFromCommonSize, pxFromCommonSize);
            ViewUtil.setSize(mFileTransferHeadView.getContactListHeadAvatar(), pxFromCommonSize, pxFromCommonSize);
            ViewUtil.setSize(mMyFriendsView.getContactListHeadAvatar(), pxFromCommonSize, pxFromCommonSize);


            for (ContactHeadView orgHeaderView : mOrgContactViewList) {
                ViewUtil.setSize(orgHeaderView.getContactListHeadAvatar(), pxFromCommonSize, pxFromCommonSize);
            }
        }
    }

    private void refreshHeight() {
        Context context = BaseApplicationLike.baseContext;
        if(isContactSimpleViewMode()) {
            int pxFrom48dp = DensityUtil.dip2px( 48);

            ViewUtil.setHeight(mDiscussionHeadView.getRlRoot(), pxFrom48dp);
            ViewUtil.setHeight(mNewFriendsApplyView.getRlRoot(), pxFrom48dp);
            ViewUtil.setHeight(mFileTransferHeadView.getRlRoot(), pxFrom48dp);

            for (ContactHeadView orgHeaderView : mOrgContactViewList) {
                ViewUtil.setHeight(orgHeaderView.getRlRoot(), pxFrom48dp);
            }


        } else {
            int pxFrom60dp = DensityUtil.dip2px( 60);

            ViewUtil.setHeight(mDiscussionHeadView.getRlRoot(), pxFrom60dp);
            ViewUtil.setHeight(mNewFriendsApplyView.getRlRoot(), pxFrom60dp);
            ViewUtil.setHeight(mFileTransferHeadView.getRlRoot(), pxFrom60dp);


            for (ContactHeadView orgHeaderView : mOrgContactViewList) {
                ViewUtil.setHeight(orgHeaderView.getRlRoot(), pxFrom60dp);
            }
        }
    }

    private boolean isContactSimpleViewMode() {
        return ContactViewMode.SIMPLE_VERSION == ContactHelper.getContactViewMode(BaseApplicationLike.baseContext);
    }

    private void handleAddFileTransferViews() {
        if (shouldAddFileTransferViews()) {
            mContactListView.addHeaderView(mFileTransferTitleView);
            mContactListView.addHeaderView(mFileTransferHeadView);
        }
    }

    private boolean shouldAddFileTransferViews() {
        if (!DomainSettingsManager.getInstance().handleFileAssistantEnable()) {
            return false;
        }

        if (!selectMode()) {
            return true;
        }

        if (sendMessageMode()) {
            return true;
        }

        return false;
    }

    private void refreshFileTransferHeaderViewSelectStatus(boolean select) {
        mIsFileTransferSelect = select;

        if (mIsFileTransferSelect) {
            mFileTransferHeadView.select();
        } else {
            mFileTransferHeadView.unselect();

        }
    }

    private void handleAddFuncHeadViews() {

        if (isContactSimpleViewMode()) {

            mContactListView.addHeaderView(mFuncTitleView);



            boolean hasFuncHeader = false;

//            boolean shouldAddFriendsView = DomainSettingsManager.getInstance().handleFriendsRelationshipsFeature() && UserSelectActivity.SelectMode.NO_SELECT.equals(mSelectMode);
//
//
//            if (shouldAddFriendsView) {
//                mContactListView.addHeaderView(mNewFriendsApplyView);
//
//                FriendManager.getInstance().notifyRefreshNewFriendApplies();
//
//                hasFuncHeader = true;
//
//            }

            boolean shouldAddFriendsView = DomainSettingsManager.getInstance().handleFriendsRelationshipsFeature();
            if (shouldAddFriendsView) {
                mContactListView.addHeaderView(mMyFriendsView);
                hasFuncHeader = true;

            }

            if (!shouldFilterDiscussionInSimpleModeView() && AtworkConfig.DISSCUSSION_CONFIG.isNeedEntry()) {
                mContactListView.addHeaderView(mDiscussionHeadView);
                hasFuncHeader = true;
            }

            if (!hasFuncHeader) {
                mContactListView.removeHeaderView(mFuncTitleView);
            }


            return;
        }


        mContactListView.addHeaderView(mFuncTitleView);


        boolean hasFuncHeader = false;

        if (UserSelectActivity.SelectMode.NO_SELECT.equals(mSelectMode)
                && AtworkConfig.DISSCUSSION_CONFIG.isNeedEntry()) {
            mContactListView.addHeaderView(mDiscussionHeadView);
            hasFuncHeader = true;
        }

        if (UserSelectActivity.SelectMode.NO_SELECT.equals(mSelectMode) && AtworkConfig.SERVICE_APP_LIST_ENTRY) {
            mContactListView.addHeaderView(mServiceAppHeadView);
            hasFuncHeader = true;

        }


        boolean shouldAddFriendsView = DomainSettingsManager.getInstance().handleFriendsRelationshipsFeature();
        if (shouldAddFriendsView) {
            mContactListView.addHeaderView(mMyFriendsView);
            hasFuncHeader = true;

        }
        if (!hasFuncHeader) {
            mContactListView.removeHeaderView(mFuncTitleView);
        }


    }

    private void handleAddOrgHeadViews(List<ContactHeadView> orgHeadViews) {
        if (!ListUtil.isEmpty(orgHeadViews)) {
            if (!UserSelectActivity.SelectMode.NO_SELECT.equals(mSelectMode)) {
                if (AtworkConfig.ORG_CONFIG.isShowSelect()) {
                    addOrgHeadViews(orgHeadViews);

                }

            } else {
                if (AtworkConfig.ORG_CONFIG.isShowDisplay()) {
                    List<ContactHeadView> orgFilteredHeadViews = getFilterOrgHeadViews(orgHeadViews);

                    addOrgHeadViews(orgFilteredHeadViews);

                }

            }
        }

        //申请列表添加
        if (UserSelectActivity.SelectMode.NO_SELECT.equals(mSelectMode)) {
            if (!ListUtil.isEmpty(mApplyHeadViewList) && DomainSettingsManager.getInstance().handleOrgApplyFeature()) {
                mOrgContactViewList.addAll(mApplyHeadViewList);

                for (ContactHeadView contactHeadView : mApplyHeadViewList) {
                    mContactListView.addHeaderView(contactHeadView);
                }
            }
        }

        //最后个 view 隐藏分割线
        if (!ListUtil.isEmpty(mOrgContactViewList)) {
            ContactHeadView headView = mOrgContactViewList.get(mOrgContactViewList.size() - 1);
        }
    }

    @NonNull
    private List<ContactHeadView> getFilterOrgHeadViews(List<ContactHeadView> orgHeadViews) {
        List<ContactHeadView> orgFilteredHeadViews = new ArrayList<>();
        List<ContactHeadView> orgRemovedHeadViews = new ArrayList<>();

        orgFilteredHeadViews.addAll(orgHeadViews);


        for (ContactHeadView contactHeadView : orgFilteredHeadViews) {
            Organization org = contactHeadView.getOrg();
            if (null != org && !OrganizationSettingsManager.getInstance().onOrgViewShow(org.mOrgCode)) {
                orgRemovedHeadViews.add(contactHeadView);
            }
        }

        orgFilteredHeadViews.removeAll(orgRemovedHeadViews);
        return orgFilteredHeadViews;
    }

    private void addOrgHeadViews(List<ContactHeadView> orgHeadViews) {
        mOrgContactViewList.addAll(orgHeadViews);

        if(null != orgHeadViews && orgHeadViews.size() !=0 ){
            mContactListView.addHeaderView(mOrgTitleView);
        }

        for (ContactHeadView contactHeadView : orgHeadViews) {
            mContactListView.addHeaderView(contactHeadView);
        }
    }

    private void refreshSessionHead() {
        int num = 0;

        List<SessionItemView> sessionItemViews = getFixNumSessionViewList();
        if (sessionItemViews.size() > 0) {
            mContactListView.addHeaderView(mSessionTitleView);
        }

        SessionItemView lastSessionItemView = null;

        for (SessionItemView sessionItemView : sessionItemViews) {
            sessionItemView.setLineVisible(true);
            lastSessionItemView = sessionItemView;

            mContactListView.addHeaderView(sessionItemView);


        }

        //最后的session view 隐藏分割线
        if (null != lastSessionItemView) {
            lastSessionItemView.setLineVisible(false);
        }


        if ((this.mSessionViewList.size() - num) > sessionItemViews.size()) {
            mContactListView.addHeaderView(mMoreSessionView);
        }

//        if (UserSelectActivity.SendMode.SEND_LINK.equals(mSendMode) && mShareSessionListNum <= 4) {
//            mMoreSessionView.setVisibility(View.GONE);
//        }

    }

    private void refreshStarUserTitleView() {
        if (null == mStartUserTitleView || null == mContactListView) {
            return;
        }
        if (0 == sStarUserListDataWrap.getContactList().size()) {
            removeStartUserTitleView();

        } else {

            if (View.GONE == mStartUserTitleView.getVisibility()) {
                //兼容 android 4.4的处理方式, 当需要设置星标 title 时, 在 addHeaderView 之前 先设置 null
                mContactListView.setAdapter(null);

                mStartUserTitleView.setVisibility(View.VISIBLE);
                mContactListView.addHeaderView(mStartUserTitleView);

                setAndRefreshAdapter();

            }

        }

    }

    private void addMainListViewTitleView() {
        if (isContactSimpleViewMode()) {
            mContactListView.addHeaderView(mSimpleModeTitleView);
            return;
        }


        if (0 != sStarUserListDataWrap.getContactList().size() && View.GONE == mStartUserTitleView.getVisibility()) {
            mStartUserTitleView.setVisibility(View.VISIBLE);
            mContactListView.addHeaderView(mStartUserTitleView);
        }
    }

    private void removeStartUserTitleView() {
        mStartUserTitleView.setVisibility(View.GONE);
        mContactListView.removeHeaderView(mStartUserTitleView);
    }

    public void loadLocalOrgData() {
        OrganizationManager.getInstance().getLocalOrganizations(getActivity(), localData -> {
            List<Organization> organizations = (List<Organization>) localData[0];

            if (organizations == null) {
                return;
            }
            mUserOrgLists = organizations;
            notifyHeadViewListChanged();
            refreshListViewTotally();
            checkOrgApplying();
            checkOrgUpdate();
        });

    }


    private void notifyHeadViewListChanged() {
        mUserOrgHeadViewList = null;
        mUserOrgHeadViewList = new ArrayList<>();
        LogUtil.e(" size ---> " + mUserOrgLists.size());

        for (Organization organization : mUserOrgLists) {
            if (organization == null) {
                continue;
            }
            ContactHeadView orgHeadView = ContactHeadView.getInstance(mActivity, mSelectMode, organization);
            registerOrgHeadViewListener(orgHeadView, organization);

            mUserOrgHeadViewList.add(orgHeadView);

        }
        sortViewListHeader(mUserOrgHeadViewList);

        if (!UserSelectActivity.SelectMode.NO_SELECT.equals(mSelectMode)) {
            List<String> modeList = new ArrayList<>();

            if (shouldAddFileTransferViews()) {
                modeList.add(SelectContactHead.SearchMode.DEVICE);
            }

            modeList.add(SelectContactHead.SearchMode.FRIEND);
            modeList.add(SelectContactHead.SearchMode.EMPLOYEE);
            if(isContactSimpleViewMode() && !shouldFilterDiscussionInSimpleModeView()) {
                modeList.add(SelectContactHead.SearchMode.DISCUSSION);

            }

            UserSelectActivity.changeSelectHeaderSearchOrgsAndMode(getActivity(), mUserOrgLists, (ArrayList<String>) modeList);
        }
    }

    private boolean selectMode() {
        return UserSelectActivity.SelectMode.SELECT.equals(mSelectMode);
    }

    private boolean sendMessageMode() {
        if (UserSelectActivity.SelectMode.SELECT.equals(mSelectMode)) {
            if (null != mSelectToHandleAction && mSelectToHandleAction instanceof TransferMessageControlAction) {
                if (!((TransferMessageControlAction) mSelectToHandleAction).isNeedCreateDiscussion()) {
                    return true;
                }
            }
        }

        return false;
    }


    public void refreshListViewTotally() {
        if (isContactSimpleViewMode()) {
            refreshContactInSimpleModeViewData();
            return;
        }


        resetStarContactListSelectStatus();

        if (mStarContactListAdapter == null) {
            mStarContactListAdapter = new ContactListArrayListAdapter(getActivity(), true);
            mStarContactListAdapter.needLine(false);
            mStarContactListAdapter.setMediumBoldMode(true);

        }
        mStarContactListAdapter.refreshData(sStarUserListDataWrap.getContactList());

        refreshStarUserTitleView();
    }

    private void resetStarContactListSelectStatus() {
        if (selectMode() && getActivity() instanceof UserSelectActivity) {
            UserSelectActivity userSelectActivity = (UserSelectActivity) getActivity();
            userSelectActivity.filterSelf(sStarUserListDataWrap.getContactList());

            List<ShowListItem> notAllowedSelectedContactList = userSelectActivity.getNotAllowedSelectedList();
            List<ShowListItem> hadSelectedContactList = userSelectActivity.getSelectedList();

            for (User user : sStarUserListDataWrap.getContactList()) {
                if (notAllowedSelectedContactList.contains(user) || hadSelectedContactList.contains(user)) {
                    user.mSelect = true;

                } else {
                    user.mSelect = false;

                }

            }
        }
    }

    private void registerOrgHeadViewListener(ContactHeadView orgHeadView, Organization organization) {
        //普通模式下的事件监听
        if (UserSelectActivity.SelectMode.NO_SELECT.equals(mSelectMode)) {
            orgHeadView.setOnClickListener(v -> {
                Intent intent = EmployeeTreeActivity.getEmployeeTreeIntent(getContext(), organization);
                getContext().startActivity(intent);
            });

        } //人员选择, 消息转发下的事件监听的模式
        else if (selectMode()) {
            orgHeadView.setOnClickListener(v -> {

                List<String> modeList = new ArrayList<>();
                modeList.add(SelectContactHead.SearchMode.EMPLOYEE);

                UserSelectActivity.changeSelectHeaderSearchOrgAndMode(getActivity(), organization, (ArrayList<String>) modeList);

                UserSelectActivity userSelectActivity = (UserSelectActivity) getActivity();
                userSelectActivity.showEmployeeTreeFragment(organization);

            });


        }

    }

    private void registerListener() {
        AbsListViewScrollDetector absListViewScrollDetector = new AbsListViewScrollDetector() {
            @Override
            public void onScrollUp() {
                Activity activity = getActivity();
                if (isAdded() && activity instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) activity;

                    mainActivity.getFab().hide();
//                    MainFabBottomPopupHelper.animateOut(mainActivity.getFab());
                }
            }

            @Override
            public void onScrollDown() {
                Activity activity = getActivity();
                if (isAdded() && activity instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) activity;

                    mainActivity.getFab().show();
//                    MainFabBottomPopupHelper.animateIn(mainActivity.getFab());

                }
            }
        };
        absListViewScrollDetector.setListView(mContactListView);
        absListViewScrollDetector.setScrollThreshold(20);
        mContactListView.setOnScrollListener(absListViewScrollDetector);

        mContactListView.setOnTouchListener((v, event) -> {
            AtworkUtil.hideInput(getActivity());
            return false;
        });

        mMoreSessionView.setOnClickListener(v -> {
            mNum++;
//            refreshSessionHead();
            sortViewListHeader(mUserOrgHeadViewList);
        });


        mIvSearch.setOnClickListener((v) -> {
            if (getActivity() instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) getActivity();

                List<SearchContent> searchContentList = new ArrayList<>();
                searchContentList.add(SearchContent.SEARCH_USER);


                if (AtworkConfig.DISSCUSSION_CONFIG.isNeedEntry()) {
                    searchContentList.add(SearchContent.SEARCH_DISCUSSION);
                }

                searchContentList.add(SearchContent.SEARCH_DEPARTMENT);

                if (DomainSettingsManager.getInstance().handleFileAssistantEnable()) {
                    searchContentList.add(SearchContent.SEARCH_DEVICE);

                }

                mainActivity.search(searchContentList.toArray(new SearchContent[0]));
            }
        });


        mSimpleModeSideBar.setOnTouchingLetterChangedListener(letter -> {
            int position = mContactListInSimpleModeAdapter.getPositionForSection(letter.charAt(0));
            if (position != -1) {
                mContactListView.setSelection(position + mContactListView.getHeaderViewsCount());
            }
        });


        //普通模式下的事件监听
        if (UserSelectActivity.SelectMode.NO_SELECT.equals(mSelectMode)) {


            mIvSwitchMode.setOnClickListener(v -> {
                CommonPopSelectWithTitleData commonPopSelectWithTitleData = new CommonPopSelectWithTitleData();
                commonPopSelectWithTitleData.getItemList().add(getStrings(R.string.classic_version));
                commonPopSelectWithTitleData.getItemList().add(getStrings(R.string.simple_version));
                commonPopSelectWithTitleData.setTitle(getStrings(R.string.switch_contact_view_show_mode));

                switch (ContactHelper.getContactViewMode(BaseApplicationLike.baseContext)) {
                    case ContactViewMode.CLASSIC_VERSION:
                        commonPopSelectWithTitleData.setItemSelect(getStrings(R.string.classic_version));
                        break;

                    case ContactViewMode.SIMPLE_VERSION:
                        commonPopSelectWithTitleData.setItemSelect(getStrings(R.string.simple_version));
                        break;
                }

                CommonPopSelectListWithTitleDialogFragment commonPopSelectListWithTitleDialogFragment = new CommonPopSelectListWithTitleDialogFragment();
                commonPopSelectListWithTitleDialogFragment.setData(commonPopSelectWithTitleData);
                commonPopSelectListWithTitleDialogFragment.setOnClickItemListener((position, value) -> {
                    if (value.equals(getStrings(R.string.classic_version))) {
                        PersonalShareInfo.getInstance().setContactViewMode(BaseApplicationLike.baseContext, ContactViewMode.CLASSIC_VERSION);

                    } else if (value.equals(getStrings(R.string.simple_version))) {
                        PersonalShareInfo.getInstance().setContactViewMode(BaseApplicationLike.baseContext, ContactViewMode.SIMPLE_VERSION);
                    }

                    refreshListViewTotally();
                    sortViewListHeader(mUserOrgHeadViewList);
                    if (ContactViewMode.CLASSIC_VERSION == ContactHelper.getContactViewMode(BaseApplicationLike.baseContext)) {
                        mSimpleModeSideBar.setVisibility(View.GONE);
                    }

                });

                commonPopSelectListWithTitleDialogFragment.show(getActivity().getSupportFragmentManager(), "popSelectWithTitle");


            });


            mIvBack.setOnClickListener(v -> {
                finish();
            });

            mMyFriendsView.setOnClickListener(v -> {
                Intent intent = MyFriendsActivity.getIntent(mActivity);
                startActivity(intent);

            });


            mNewFriendsApplyView.setOnClickListener(v -> {
                String url = UrlConstantManager.getInstance().getFriendApproval();
                WebViewControlAction webViewControlAction = WebViewControlAction.newAction().setUrl(url).setTitle(getString(R.string.new_friend_in_btn)).setNeedShare(false);
                Intent intent = WebViewActivity.getIntent(mActivity, webViewControlAction);
                startActivity(intent);
            });

            mDiscussionHeadView.setOnClickListener(v -> {
                startActivity(DiscussionListActivity.getIntent(getActivity()));
            });

            mServiceAppHeadView.setOnClickListener(v -> {
                Intent intent = AppListActivity.getIntent(getActivity());
                startActivity(intent);
            });


            mContactListView.setOnItemClickListener((parent, view, position, id) -> {


                final ShowListItem contact = (ShowListItem) parent.getItemAtPosition(position);
                if (contact == null) {
                    return;
                }

                if (contact instanceof User) {
                    User user = (User) contact;
                    startActivity(PersonalInfoActivity.getIntent(getActivity().getApplicationContext(), user));

                } else if (contact instanceof Discussion) {
                    Discussion discussion = (Discussion) contact;
                    EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest(SessionType.Discussion, discussion)
                            .setOrgId(discussion.getOrgCodeCompatible());

                    ChatSessionDataWrap.getInstance().entrySessionSafely(entrySessionRequest);

                    Intent intent = ChatDetailActivity.getIntent(getActivity(), discussion.mDiscussionId);
                    intent.putExtra(ChatDetailFragment.RETURN_BACK, true);
                    startActivity(intent);

                }
            });

            mFileTransferHeadView.setOnClickListener(v -> {

                AtworkApplicationLike.getLoginUser(new UserAsyncNetService.OnQueryUserListener() {
                    @Override
                    public void onSuccess(@NonNull User user) {
                        if (isAdded()) {
                            EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest(SessionType.User, user);
                            ChatSessionDataWrap.getInstance().entrySession(entrySessionRequest);

                            Intent intent = ChatDetailActivity.getIntent(getActivity(), user.mUserId);
                            intent.putExtra(ChatDetailFragment.RETURN_BACK, true);

                            getActivity().startActivity(intent);
                        }
                    }

                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        ErrorHandleUtil.handleError(errorCode, errorMsg);
                    }
                });

            });

        }
        //人员选择下的事件监听
        else if (selectMode()) {
            if (DomainSettingsManager.getInstance().handleFriendsRelationshipsFeature()) {
                mMyFriendsView.setOnClickListener(v -> {
                    UserSelectActivity.changeSelectHeaderSearchMode(getActivity(), (ArrayList<String>) ListUtil.makeSingleList(SelectContactHead.SearchMode.FRIEND));

                    UserSelectActivity userSelectActivity = (UserSelectActivity) getActivity();
                    userSelectActivity.showFriendsFragment();
                });
            }


            mDiscussionHeadView.setOnClickListener(v -> {
                if (getActivity() instanceof UserSelectActivity) {
                    UserSelectActivity.changeSelectHeaderSearchMode(getActivity(), (ArrayList<String>) ListUtil.makeSingleList(SelectContactHead.SearchMode.DISCUSSION));

                    UserSelectActivity userSelectActivity = (UserSelectActivity) getActivity();
                    userSelectActivity.showDiscussionListFragment();
                }
            });

            mContactListView.setOnItemClickListener((parent, view, position, id) -> {
                final ShowListItem contact = (ShowListItem) parent.getItemAtPosition(position);
                if (contact == null) {
                    return;
                }

                if (contact instanceof User) {
                    selectUserContact((User) contact);

                } else if (contact instanceof Discussion) {
                    selectDiscussionContact((Discussion) contact);
                }

            });


            mFileTransferHeadView.setOnClickListener(v -> {
                FileTransferService.INSTANCE.getFileTransfer(new UserAsyncNetService.OnQueryUserListener() {
                    @Override
                    public void onSuccess(@NonNull User user) {

                        user.select(mIsFileTransferSelect);

                        selectUserContact(user);

                        if (user.isSelect()) {
                            mFileTransferHeadView.select();
                        } else {
                            mFileTransferHeadView.unselect();

                        }

                        mIsFileTransferSelect = user.isSelect();

                    }

                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        ErrorHandleUtil.handleError(errorCode, errorMsg);
                    }
                });
            });
        }
        //消息转发下的模式
//        else if (UserSelectActivity.SelectMode.SEND_MESSAGES.equals(mSelectMode)) {
//
//            mMyFriendsView.setOnClickListener(v -> {
//                UserSelectActivity.changeSelectHeaderSearchMode(getActivity(), SelectUserHead.SearchMode.getFriendSearchMode());
//
//                UserSelectActivity userSelectActivity = (UserSelectActivity) getActivity();
//                userSelectActivity.showFriendsFragment();
//            });
//            mDiscussionHeadView.setOnClickListener(v -> {
//                if (getActivity() instanceof UserSelectActivity) {
//                    UserSelectActivity userSelectActivity = (UserSelectActivity) getActivity();
//                    userSelectActivity.showDiscussionListFragment();
//                }
//            });
//
//            mContactListView.setOnItemClickListener((parent, view, position, id) -> {
//                final User user = (User) parent.getItemAtPosition(position);
//                if (user == null) {
//                    return;
//                }
//                if (getActivity() instanceof UserSelectActivity && UserSelectActivity.SelectMode.SEND_MESSAGES.equals(mSelectMode)) {
//                    UserSelectActivity userSelectActivity = (UserSelectActivity) getActivity();
//                    userSelectActivity.sendMessagesToContact(user);
//                }
//            });
//        }


    }

    private void selectUserContact(User user) {
        if (getActivity() instanceof UserSelectActivity) {
            UserSelectActivity userSelectActivity = (UserSelectActivity) getActivity();
            if (mIsSingleContact) {
                user.select();
                List<User> contactList = new ArrayList<>(1);
                contactList.add(user);
                SelectedContactList.setContactList(contactList);

                Intent returnIntent = new Intent();
                mActivity.setResult(Activity.RESULT_OK, returnIntent);
                mActivity.finish();
                return;
            }

            if(mNotAllowedSelectedContacts.contains(user.getId())) {
                return;
            }

            if (!user.mSelect && !userSelectActivity.isAllowed()) {
                toast(mUserSelectControlAction.getMaxTip());
//                ToastTipHelper.toastMaxTipInUserSelectActivity(userSelectActivity, mSelectAction);

                return;
            }

            if (!user.mSelect && userSelectActivity.isOneSelected()) {
                return;
            }

            user.select();
            userSelectActivity.action(user);
        }
    }


    private void selectDiscussionContact(Discussion discussion) {
        if (!sendMessageMode()) {
            return;
        }

        if (!(getActivity() instanceof UserSelectActivity)) {
            return;
        }

        UserSelectActivity userSelectActivity = (UserSelectActivity) getActivity();
        if (!discussion.mSelect && !userSelectActivity.isAllowed()) {
            toast(mUserSelectControlAction.getMaxTip());

            return;
        }


        if (!discussion.mSelect && userSelectActivity.isOneSelected()) {
            return;
        }

        discussion.select();
        userSelectActivity.action(discussion);


    }


    @Override
    public Loader<List<User>> onCreateLoader(int id, Bundle args) {
        return new UserListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<User>> loader, List<User> data) {
        if (mCallbackContacts != null) {
            data = refreshSelectedStatus(data, mCallbackContacts);
        }

        sStarUserListDataWrap.clear();
        sStarUserListDataWrap.setContactList(data);
        refreshListViewTotally();
    }

    @Override
    public void onLoaderReset(Loader<List<User>> loader) {

    }


    private List<User> refreshSelectedStatus(List<User> loadedList, List<ShowListItem> selectedList) {
        if (loadedList == null || selectedList == null) {
            return loadedList;
        }

        for (User loadedUser : loadedList) {
            if (loadedUser == null) {
                continue;
            }

            for (ShowListItem selectedUser : selectedList) {
                if (selectedUser == null) {
                    continue;
                }

                if (selectedUser.getId().equalsIgnoreCase(loadedUser.mUserId) || mNotAllowedSelectedContacts.contains(selectedUser.getId())) {
                    loadedUser.mSelect = true;
                    continue;
                }
            }
        }
        return loadedList;
    }

    private void refreshSelectedStatus(List<ShowListItem> contactList) {


        for(ShowListItem contact : contactList) {

            if((null != mCallbackContacts && mCallbackContacts.contains(contact)) || mNotAllowedSelectedContacts.contains(contact.getId())) {
                contact.select(true);
            } else {
                contact.select(false);

            }

        }

    }


    @Override
    public void selectContact(ShowListItem contactSelected) {


        selectContactList(ListUtil.makeSingleList(contactSelected));
    }

    @Override
    public void unSelectedContact(ShowListItem contactUnselected) {
        unSelectedContactList(ListUtil.makeSingleList(contactUnselected));
    }

    @Override
    public void selectContactList(List<? extends ShowListItem> selectContactList) {
        if (isContactSimpleViewMode()) {
            if(null != mContactListInSimpleMode) {

                CollectionsKt.forEach(mContactListInSimpleMode, contactInSimpleMode -> {

                    CollectionsKt.forEach(selectContactList, (Function1<ShowListItem, Unit>) contactInSelectList -> {

                        if(contactInSimpleMode.getId().equals(contactInSelectList.getId())) {
                            contactInSimpleMode.select(true);

                        }


                        if (FileTransferService.INSTANCE.isClickFileTransfer(contactInSelectList)) {
                            refreshFileTransferHeaderViewSelectStatus(true);
                        }



                        return Unit.INSTANCE;
                    });



                    return Unit.INSTANCE;
                });


            }



        } else {
            Vector<User> contactVector = new Vector<>(sStarUserListDataWrap.getContactList());

            for (ShowListItem contact : selectContactList) {
                for (User user : contactVector) {
                    if (user.mUserId.equals(contact.getId())) {
                        user.mSelect = true;
                    }
                }
            }

        }
//        refreshListViewTotally();
        refreshAdapterLightly();

    }


    @Override
    public void unSelectedContactList(List<? extends ShowListItem> unselectContactList) {
        if (isContactSimpleViewMode()) {
            if(null != mContactListInSimpleMode) {

                CollectionsKt.forEach(mContactListInSimpleMode, contactInSimpleMode -> {

                    CollectionsKt.forEach(unselectContactList, (Function1<ShowListItem, Unit>) contactInUnselectList -> {

                        if(contactInSimpleMode.getId().equals(contactInUnselectList.getId())) {
                            contactInSimpleMode.select(false);


                        }


                        if (FileTransferService.INSTANCE.isClickFileTransfer(contactInUnselectList)) {
                            refreshFileTransferHeaderViewSelectStatus(false);
                        }

                        return Unit.INSTANCE;
                    });




                    return Unit.INSTANCE;
                });


            }


        } else {
            Vector<User> contactVector = new Vector<>(sStarUserListDataWrap.getContactList());

            for (ShowListItem contact : unselectContactList) {
                for (User user : contactVector) {
                    if (user.mUserId.equals(contact.getId())) {
                        user.mSelect = false;
                    }
                }
            }

        }
//        refreshListViewTotally();
        refreshAdapterLightly();

    }

    public List<Organization> getUserOgList() {
        return mUserOrgLists;
    }

    @Override
    protected boolean onBackPressed() {
        return false;
    }


    @Override
    public void onThemeUpdate(Theme theme) {
        super.onThemeUpdate(theme);
        if (null != mUserOrgHeadViewList) {
            for (ContactHeadView contactHeadView : mUserOrgHeadViewList) {
                contactHeadView.refreshStatusBtnColor();
            }
        }

        refreshAdapterLightly();

    }

    public String getFragmentName() {
        String titleName = "";
        try {
            titleName = BeeWorks.getInstance().getBeeWorksTabById(BaseApplicationLike.baseContext, mId).name;
            titleName = TextUtils.isEmpty(titleName) ? AtworkApplicationLike.getResourceString(R.string.item_contact) : titleName;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return titleName;
    }

    private void setTitle() {
        mTvMainTitleView.setText(getFragmentName());
        mTvCommonTitleBar.setText(R.string.item_contact);
    }

    @Override
    public void networkChanged(NetworkBroadcastReceiver.NetWorkType networkType) {
        if (mNetworkErrorViewManager != null) {
            LogUtil.e("refreshNetworkStatusUI  ContactFragment");

            mNetworkErrorViewManager.refreshNetworkStatusUI(networkType.hasNetwork());
        }
    }



    private void checkShowGuidePage() {

        if(-1 != AtworkConfig.CONTACT_CONFIG.getOnlyVersionContactViewMode()) {
            return;
        }

        int shownCount = PersonalShareInfo.getInstance().getMainContactViewModeSwitchFirstGuidePageShownCount(BaseApplicationLike.baseContext);
        if(shownCount >= PersonalShareInfo.DEFAULT_SHOULD_MAIN_FAB_BOTTOM_SLIDE_NOTICE_FINGER_SHOWN_COUNT || getActivity() == null) {
            return;
        }

        GuidePageDialogFragment guidePageDialogFragment = new GuidePageDialogFragment();
        guidePageDialogFragment.setGuideIconRes(R.mipmap.icon_switch_white);
        guidePageDialogFragment.setGuideContent(getStrings(R.string.switch_contact_view_show_mode));
        guidePageDialogFragment.show(getActivity().getSupportFragmentManager(), "guidePageDialog");

        PersonalShareInfo.getInstance().putMainContactViewModeSwitchFirstGuidePageShownCount(BaseApplicationLike.baseContext, ++shownCount);
    }
}
