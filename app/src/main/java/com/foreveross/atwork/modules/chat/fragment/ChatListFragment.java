package com.foreveross.atwork.modules.chat.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foreverht.db.service.repository.UnreadMessageRepository;
import com.foreverht.workplus.component.DeviceOnlinePopup;
import com.foreverht.workplus.component.DeviceOnlineView;
import com.foreverht.workplus.ui.component.recyclerview.layoutManager.RecyclerViewNoBugLinearLayoutManager;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.app.model.LightNoticeData;
import com.foreveross.atwork.broadcast.NetworkBroadcastReceiver;
import com.foreveross.atwork.component.UnreadImageView;
import com.foreveross.atwork.component.recyclerview.AbsRecyclerViewScrollDetector;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.biometricAuthentication.BiometricAuthenticationProtectItemType;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.FriendNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.OrgNotifyMessage;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.NetworkStatusUtil;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.manager.MessageNoticeManager;
import com.foreveross.atwork.manager.OrgApplyManager;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.manager.OrganizationSettingsHelper;
import com.foreveross.atwork.manager.im.OfflineMessageManager;
import com.foreveross.atwork.manager.im.OfflineMessageReplayStrategyManager;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity;
import com.foreveross.atwork.modules.chat.activity.CollectType;
import com.foreveross.atwork.modules.chat.activity.SessionsCollectActivity;
import com.foreveross.atwork.modules.chat.adapter.ChatSessionListAdapter;
import com.foreveross.atwork.modules.chat.dao.ChatDaoService;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.loader.ChatSessionListLoader;
import com.foreveross.atwork.modules.chat.service.ChatService;
import com.foreveross.atwork.modules.chat.util.ChatListViewHelper;
import com.foreveross.atwork.modules.chat.util.DiscussionHelper;
import com.foreveross.atwork.modules.chat.util.LightAppSessionHelper;
import com.foreveross.atwork.modules.chat.util.SessionRefreshHelper;
import com.foreveross.atwork.modules.common.lightapp.LightNoticeMapping;
import com.foreveross.atwork.modules.common.lightapp.SimpleLightNoticeMapping;
import com.foreveross.atwork.modules.main.activity.MainActivity;
import com.foreveross.atwork.modules.main.data.TabNoticeManager;
import com.foreveross.atwork.modules.main.helper.NetworkErrorViewManager;
import com.foreveross.atwork.modules.newsSummary.activity.NewsSummaryActivity;
import com.foreveross.atwork.modules.organization.activity.OrgApplyingActivity;
import com.foreveross.atwork.modules.route.manager.RouteActionConsumer;
import com.foreveross.atwork.modules.route.model.ActivityInfo;
import com.foreveross.atwork.modules.route.model.RouteParams;
import com.foreveross.atwork.modules.search.model.SearchContent;
import com.foreveross.atwork.support.NoticeTabAndBackHandledFragment;
import com.foreveross.atwork.tab.helper.BeeworksTabHelper;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.IntentUtil;
import com.foreveross.theme.model.Theme;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

import kotlin.collections.CollectionsKt;

import static android.view.View.GONE;


public class ChatListFragment extends NoticeTabAndBackHandledFragment implements NetworkBroadcastReceiver.NetworkChangedListener {

    public static final String TAB_ID = "im";

    public static final String ORG_SESSION_REFRESH = "REFRESH_ORG_SESSION";
    public static final String REFRESH_MESSAGE_COUNT = "REFRESH_MESSAGE_COUNT";
    public static final String REFRESH_BING_COUNT = "REFRESH_BING_COUNT";
    public static final String DATA_RECEIVING = "DATA_RECEIVING";
    public static final String DEVICE_ONLINE_STATUS = "DEVICE_ONLINE_STATUS";

    //刷新消息中的未读数
    public static final String INTENT_RECEIVING_TITLE_HANDLED = "INTENT_RECEIVING_TITLE_HANDLED";

    //显示/隐藏设备在线
    public static final String INTENT_DEVICE_ONLINE_STATUS = "INTENT_DEVICE_ONLINE_STATUS";

    //PC端在线的系统（win/mac）
    public static final String INTENT_DEVICE_SYSTEM = "INTENT_DEVICE_SYSTEM";

    public static final String INTENT_PC_DEVICE_ID = "INTENT_PC_DEVICE_ID";

    public static final int MSG_REFRESH_SESSION_LIST = 1000123;

    private static final String TAG = ChatListFragment.class.getSimpleName();
    /**
     * 数据封装对象
     */
    private static ChatSessionDataWrap chatSessionDataWrap = ChatSessionDataWrap.getInstance();

    public static boolean IS_FRAGMENT_VISIBLE_HINT = false;

    public static boolean IS_FRAGMENT_SHOW = false;


    protected ChatSessionListAdapter mChatListArrayAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private RecyclerView mChatListView;
    private View mVNoMessage;

    private SessionLoaderCallbacks mSessionLoaderCallbacks = new SessionLoaderCallbacks();

    private UiHandler mHandler = new UiHandler(this);

    private Activity mActivity;

    private View mTitleBar;
    private View mVFakeStatusBar;
    private TextView mTitleView;
    private ProgressBar mMessageReceiving;
    private UnreadImageView mBingUnreadImageView;
    private ImageView mIvSearch,imagePcOnline;
    private boolean mShouldShowTitle;

    private List<Session> mSessionList = new ArrayList<>();
    private DeviceOnlineView mDeviceOnlineView;
    private DeviceOnlinePopup mPopup;
    private View mTransparentView;
    private ImageView mIvRobot;

    private NetworkErrorViewManager mNetworkErrorViewManager;

    private BroadcastReceiver mMessageReceivingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && DATA_RECEIVING.equalsIgnoreCase(intent.getAction())) {
                mShouldShowTitle = intent.getBooleanExtra(INTENT_RECEIVING_TITLE_HANDLED, false);
                checkMessageReceiving();
            }
        }
    };

    private BroadcastReceiver mDeviceOnlineReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && DEVICE_ONLINE_STATUS.equalsIgnoreCase(intent.getAction())) {
                boolean isOnline = intent.getBooleanExtra(INTENT_DEVICE_ONLINE_STATUS, false);
                String deviceSystem = intent.getStringExtra(INTENT_DEVICE_SYSTEM);
                String pcDeviceId = intent.getStringExtra(INTENT_PC_DEVICE_ID);
                //注意不能多次setheader，所以判断当前状态是否相等
                boolean currentStatus = PersonalShareInfo.getInstance().isPCOnline(context);
                String spDeviceSystem = PersonalShareInfo.getInstance().getDeviceSystem(context);
                boolean isMute = PersonalShareInfo.getInstance().isDeviceOnlineMuteMode(context);
                if (isOnline == currentStatus && null != deviceSystem && deviceSystem.equals(spDeviceSystem)) {
                    return;
                }
                //保持状态到sp中
                //PersonalShareInfo.getInstance().setIsPCOnline(context, isOnline);
                PersonalShareInfo.getInstance().setIsPCOnline(context, isOnline, deviceSystem, pcDeviceId);
                //改变UI
                showDeviceOnlineStatus(isOnline, isMute, deviceSystem);
            }
        }

    };

    private BroadcastReceiver mMessageRefreshBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SessionRefreshHelper.MESSAGE_REFRESH.equalsIgnoreCase(intent.getAction())) {
                refreshViewTotally();
                return;
            }

            if(OfflineMessageManager.ACTION_END_PULL_OFFLINE_MESSAGES.equalsIgnoreCase(intent.getAction())) {
                OrgApplyManager.getInstance().loadOrg(mActivity);
            }
        }
    };

    private BroadcastReceiver mRefreshOrgSessionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            OrgApplyManager.getInstance().loadOrg(mActivity);
        }
    };

    /**
     * 计算未读消息总数广播接收器
     */
    private BroadcastReceiver mUnReadNumRefreshReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(null == intent) {
                return;
            }

            String action = intent.getAction();
            if (REFRESH_MESSAGE_COUNT.equals(action)) {
                calMessageUnread();
            }
        }
    };


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        registerBroadcast();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        getLoaderManager().initLoader(0, null, mSessionLoaderCallbacks).forceLoad();

        mNetworkErrorViewManager.registerImReceiver(mActivity);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        //清除通知栏
        MessageNoticeManager.getInstance().clear();
        calMessageUnread();
        registerListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkMessageReceiving();
        refreshViewTotally();

        IS_FRAGMENT_SHOW = true;

        mIvRobot.setVisibility(GONE);
    }

    @Override
    public void onPause() {
        super.onPause();

        IS_FRAGMENT_SHOW = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterBroadcast();
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    protected void findViews(View view) {
        mTitleBar = view.findViewById(R.id.chat_list_title_bar);
        mTitleView = mTitleBar.findViewById(R.id.title_bar_main_title);
        mVFakeStatusBar = mTitleBar.findViewById(R.id.v_fake_statusbar);
        mMessageReceiving = mTitleBar.findViewById(R.id.message_receiving);
        mBingUnreadImageView = mTitleBar.findViewById(R.id.unread_imageview);
        mIvSearch = mTitleBar.findViewById(R.id.iv_search);
        mNetworkErrorViewManager = new NetworkErrorViewManager(mTitleBar);
        imagePcOnline = mTitleBar.findViewById(R.id.image_pc_online);
        mIvRobot = mTitleBar.findViewById(R.id.robot_imageview);

        mChatListView = view.findViewById(R.id.chat_list_view);
        mVNoMessage = view.findViewById(R.id.layout_no_message);

        mDeviceOnlineView = new DeviceOnlineView(mActivity);
        mDeviceOnlineView.setDeviceOnlineClickListener(() -> {
            mPopup = new DeviceOnlinePopup(mActivity);
            mPopup.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            mTransparentView.setVisibility(View.VISIBLE);
            mPopup.setOnDismissListener(() -> mTransparentView.setVisibility(View.GONE));
        });

        mTransparentView = new View(mActivity);
        mTransparentView.setBackgroundColor(Color.BLACK);
        mTransparentView.setAlpha(0.5f);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mActivity.addContentView(mTransparentView, lp);
        mTransparentView.setVisibility(View.GONE);

        //取消默认的分割线
//        mChatListView.setDivider(null);
    }

    @Override
    protected View getFakeStatusBar() {
        return mVFakeStatusBar;
    }

    private void initData() {
        mBingUnreadImageView.setIcon(R.mipmap.icon_bing_dark);
        mBingUnreadImageView.setVisibility( View.GONE);

        mChatListArrayAdapter = new ChatSessionListAdapter(mSessionList);
        mChatListArrayAdapter.setHasStableIds(true);
        mLinearLayoutManager = new RecyclerViewNoBugLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mChatListView.setLayoutManager(mLinearLayoutManager);
        mChatListView.setAdapter(mChatListArrayAdapter);

//        ((SimpleItemAnimator)mChatListView.getItemAnimator()).setSupportsChangeAnimations(false);

        showDeviceOnlineStatus(PersonalShareInfo.getInstance().isPCOnline(getActivity()),
                PersonalShareInfo.getInstance().isDeviceOnlineMuteMode(getActivity()),
                PersonalShareInfo.getInstance().getDeviceSystem(getActivity()));
    }

    @Override
    public void onDomainSettingChange() {
        Executors.newSingleThreadExecutor().submit(() -> {

            if (DomainSettingsManager.getInstance().handleFriendsRelationshipsFeature() &&
                    DomainSettingsManager.getInstance().handleEmailSettingsFeature() &&
                    DomainSettingsManager.getInstance().handleOrgApplyFeature()) {
                ChatSessionDataWrap.getInstance().checkSessionListUpdate(true);

            } else {
                ChatSessionDataWrap.getInstance().checkFilteredSessions();

            }

            SessionRefreshHelper.notifyRefreshCount();
            refreshViewTotally();

        });

    }

    private void registerBroadcast() {

        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mMessageReceivingReceiver, new IntentFilter(DATA_RECEIVING));

        IntentFilter messageRefreshIntentFilter = new IntentFilter();
        messageRefreshIntentFilter.addAction(SessionRefreshHelper.MESSAGE_REFRESH);
        messageRefreshIntentFilter.addAction(OfflineMessageReplayStrategyManager.ACTION_END_PULL_OFFLINE_MESSAGES);

        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mMessageRefreshBroadcastReceiver, messageRefreshIntentFilter);
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mDeviceOnlineReceiver, new IntentFilter(DEVICE_ONLINE_STATUS));

        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mRefreshOrgSessionReceiver, new IntentFilter(ChatListFragment.ORG_SESSION_REFRESH));

        IntentFilter filter = new IntentFilter();
        filter.addAction(ChatListFragment.REFRESH_MESSAGE_COUNT);
        filter.addAction(ChatListFragment.REFRESH_BING_COUNT);
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mUnReadNumRefreshReceiver, filter);

    }

    private boolean mRefreshingViewTotally = false;
    private long mLastRefreshViewTotallyTime = -1;

    public  void refreshViewTotally() {

        LogUtil.e("refreshViewTotally");

        if(mRefreshingViewTotally) {
            return;
        }

        if(1000 < System.currentTimeMillis() - mLastRefreshViewTotallyTime) {
            refreshViewTotallySync();
            return;
        }



        mRefreshingViewTotally = true;
        AtworkApplicationLike.getHandler().postDelayed(() -> {
            refreshViewTotallySync();

            mRefreshingViewTotally = false;
        }, 500);


    }

    private void refreshViewTotallySync() {

        LogUtil.e("refreshViewTotally core");


        if (mChatListView == null) {
            return;
        }

        mLastRefreshViewTotallyTime = System.currentTimeMillis();

        mSessionList.clear();
        mSessionList.addAll(chatSessionDataWrap.getDisplaySessions());


        //防止排序时异步时间戳变动, 导致闪退
        long begin = System.currentTimeMillis();
        ChatSessionDataWrap.getInstance().sortAvoidShaking(mSessionList);
        LogUtil.e("refreshViewTotally core -> sortAvoidShaking: " + (System.currentTimeMillis() - begin));

        mActivity.runOnUiThread(() -> {
//            mChatListArrayAdapter.updateChatItemList(mSessionList);
            mChatListArrayAdapter.notifyDataSetChanged();

            if(ListUtil.isEmpty(mSessionList)) {
                mVNoMessage.setVisibility(View.VISIBLE);

                //设备多端登录，消息列表没有消息时，显示提示语
                mChatListView.setVisibility(View.VISIBLE);

            } else {
                mVNoMessage.setVisibility(View.GONE);
                mChatListView.setVisibility(View.VISIBLE);
            }
        });
    }


    private void getSystemNavigation() {
        BeeworksTabHelper.getInstance().getSystemNavigation(mActivity, BeeWorks.getInstance().getBeeWorksTabById(mActivity, mId));
    }

    private void registerListener() {

        AbsRecyclerViewScrollDetector absListViewScrollDetector = new AbsRecyclerViewScrollDetector() {

            static final long FAB_BTN_SHOW_GAP = 300;
            long mLastHandleFabTime = -1;



            @Override
            public void onScrollUp() {
//                LogUtil.e("onScrollUp");

                Activity activity = getActivity();
                if(isAdded() && activity instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) activity;

                    long currentFabTime = System.currentTimeMillis();

                    if (FAB_BTN_SHOW_GAP < currentFabTime - mLastHandleFabTime) {
                        mLastHandleFabTime = currentFabTime;
                        mainActivity.getFab().hide();
                        mLastHandleFabTime = currentFabTime;
                    }


//                    MainFabBottomPopupHelper.animateOut(mainActivity.getFab());
                }
            }

            @Override
            public void onScrollDown() {
//                LogUtil.e("onScrollDown");

                Activity activity = getActivity();
                if(isAdded() && activity instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) activity;

                    long currentFabTime = System.currentTimeMillis();

                    if (FAB_BTN_SHOW_GAP < currentFabTime - mLastHandleFabTime) {
                        mLastHandleFabTime = currentFabTime;
                        mainActivity.getFab().show();
                        mLastHandleFabTime = currentFabTime;

                    }

//                    MainFabBottomPopupHelper.animateIn(mainActivity.getFab());

                }
            }
        };
        absListViewScrollDetector.setListView(mChatListView);
        absListViewScrollDetector.setScrollThreshold(20);
        mChatListView.addOnScrollListener(absListViewScrollDetector);

        imagePcOnline.setOnClickListener(v -> {
            mPopup = new DeviceOnlinePopup(mActivity);
            mPopup.showAtLocation(getView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            mTransparentView.setVisibility(View.VISIBLE);
            mPopup.setOnDismissListener(() -> mTransparentView.setVisibility(View.GONE));
        });

        mChatListArrayAdapter.setOnItemClickListener((adapter, view, position) -> {
            final Session session = (Session) adapter.getItem(position);
            if(null == session) {
                return;
            }

            //进入应用详情  聊天界面
            if (Session.EntryType.To_Chat_Detail.equals(session.entryType) || session.entryType == null) {
                if(Session.WORKPLUS_SUMMARY_HELPER.equals(session.identifier)) {
                    Activity activity = getActivity();
                    if(null == activity) {
                        return;
                    }

                    Intent intent = NewsSummaryActivity.Companion.getIntent(activity);
                    startActivity(intent);
                    session.clearUnread();
                    ChatDaoService.getInstance().sessionRefresh(session);
//                    ChatService.clearSessionsFoldReceipts(AtworkApplicationLike.baseContext, session);
                }else {
                    doRouteChatDetailView(session);
                }

            }
            //进入网页
            else if (Session.EntryType.To_URL.equals(session.entryType)) {
                doRouteWebView(session);
            }
            //进入原生应用
            else if (Session.EntryType.To_Native.equals(session.entryType)) {
                //TODO 待完成
            }
            //进入组织申请列表
            else if (Session.EntryType.To_ORG_APPLYING.equals(session.entryType)) {

                OrganizationManager.getInstance().queryLoginAdminOrg(getActivity(), orgCodeList -> {
                    Intent intentJump;
                    if (orgCodeList.size() > 1) {
                        intentJump = OrgApplyingActivity.getIntent(getActivity());
                    } else {
                        //跳转到网页审批
                        if (orgCodeList.size() == 0) {
                            return;
                        }
                        String orgCode = orgCodeList.get(0);
                        String url = String.format(UrlConstantManager.getInstance().getApplyOrgsUrl(), orgCode, AtworkConfig.DOMAIN_ID);
                        WebViewControlAction webViewControlAction = WebViewControlAction.newAction().setUrl(url).setNeedShare(false);
                        intentJump = WebViewActivity.getIntent(getActivity(), webViewControlAction);

                        Executors.newSingleThreadExecutor().execute(() -> {
                            ChatService.sendSessionReceiptsSync(mActivity, session, new HashSet<>(UnreadMessageRepository.getInstance().queryUnreadOrgApplyCountByOrgCode(orgCode)));

                            UnreadMessageRepository.getInstance().removeUnreadOrg(orgCode);
                            ChatSessionDataWrap.getInstance().getSessionSafely(OrgNotifyMessage.FROM, null).clearUnread();
                            SessionRefreshHelper.notifyRefreshSessionAndCount();
                        });

                    }


                    startActivity(intentJump);

                });


            }

            else if(Session.EntryType.DISCUSSION_HELPER.equals(session.entryType)) {
                Activity activity = getActivity();
                if(null == activity) {
                    return;
                }

                Intent intent = SessionsCollectActivity.getIntent(activity, CollectType.DISCUSSION_HELPER);
                startActivity(intent);
            }

            else if(Session.EntryType.APP_ANNOUNCE.equals(session.entryType)) {
                Activity activity = getActivity();
                if(null == activity) {
                    return;
                }

                Intent intent = SessionsCollectActivity.getIntent(activity, CollectType.APP_ANNOUNCE);
                startActivity(intent);

                ChatService.clearSessionsFoldReceipts(AtworkApplicationLike.baseContext, session);
                PersonalShareInfo.getInstance().setLastTimeEnterAnnounceApp(AtworkApplicationLike.baseContext, TimeUtil.getCurrentTimeInMillis());
            }
        });



        mChatListArrayAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            final Session session = (Session) adapter.getItem(position);

            popItemListDialog(session);
            return true;
        });



        mIvSearch.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) getActivity();

                List<SearchContent> searchContentList = new ArrayList<>();
                searchContentList.add(SearchContent.SEARCH_USER);

                if (AtworkConfig.DISSCUSSION_CONFIG.isNeedEntry()) {
                    searchContentList.add(SearchContent.SEARCH_DISCUSSION);
                }

                searchContentList.add(SearchContent.SEARCH_DEPARTMENT);

                if (AtworkConfig.APP_CONFIG.isNeedAppInSearch()) {
                    searchContentList.add(SearchContent.SEARCH_APP);
                }

                searchContentList.add(SearchContent.SEARCH_MESSAGES);

                if(DomainSettingsManager.getInstance().handleFileAssistantEnable()) {
                    searchContentList.add(SearchContent.SEARCH_DEVICE);
                }


                mainActivity.search(searchContentList.toArray(new SearchContent[0]));
            }

        });

        mTransparentView.setOnClickListener(v -> {
            if (mPopup != null && mPopup.isShowing()) {
                mPopup.dismiss();
            }
        });
    }

    private void doRouteChatDetailView(Session session) {
        if (isAdded()) {
            Intent intent = new Intent();

            intent.setClass(getActivity(), ChatDetailActivity.class);
            intent.putExtra(ChatDetailActivity.IDENTIFIER, session.identifier);
            intent.putExtra(ChatDetailFragment.RETURN_BACK, true);
            intent.putExtra(ChatDetailActivity.SESSION_LEGAL_CHECK, false);

            startActivity(intent);
        }
    }

    private void doRouteWebView(Session session) {
        ChatService.sendSessionReceipts(getContext(), session);

        ChatSessionDataWrap.getInstance().readSpecialSession(getActivity(), session);

        RouteParams routeParams = RouteParams.newRouteParams().uri(session.entryValue).build();

        boolean result = RouteActionConsumer.INSTANCE.route(mActivity, routeParams);

        if(result) {
            return;
        }


        doRouteWebViewFallback(session);
    }

    private void doRouteWebViewFallback(Session session) {
        WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                .setUrl(session.entryValue)
                .setTitle(session.name)
                .setSessionId(session.identifier);

        if (SessionType.LightApp.equals(session.type)) {
            LightAppSessionHelper.startActivityFromLightAppSession(mActivity, session, webViewControlAction);

        } else {
            final Intent webIntent = WebViewActivity.getIntent(getActivity(), webViewControlAction);
            startActivity(webIntent);

        }
    }

    private void popItemListDialog(Session session) {

        ChatListViewHelper.popChatItemListDialog(ChatListFragment.this, session, () -> {
            refreshViewTotally();
            return null;
        });

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

//        WorkplusStatusBarHelper.setCommonStatusBar(getActivity());


        calMessageUnread();
        IS_FRAGMENT_VISIBLE_HINT = isVisibleToUser;

        //如果fragment显示可见
        if (isVisibleToUser) {
            refreshViewTotally();
            LogUtil.i("ChatListFragment", "Show");

            checkSessionValid();
            getSystemNavigation();
            OrganizationSettingsHelper.getInstance().checkOrgSettingsUpdate(mActivity, 0);
            AtworkUtil.checkUpdate(mActivity, false);

            ChatService.calibrateUserSessions(false);
        }
    }

    @Override
    protected void refreshNetworkStatusUI() {
        if (mNetworkErrorViewManager != null) {
            mNetworkErrorViewManager.refreshNetworkStatusUI(NetworkStatusUtil.isNetworkAvailable(BaseApplicationLike.baseContext));
        }
    }

    /**
     * 启动线程，检查当前的session缓存和数据库的数量进行对比，如果有差异，更新当前缓存和数据库
     */
    public void checkSessionValid() {
        if (mHandler == null) {
            return;
        }
        Executors.newSingleThreadExecutor().execute(() -> {

            List<Session> sessions = null;
            if (DomainSettingsManager.getInstance().handleFriendsRelationshipsFeature()
                    && DomainSettingsManager.getInstance().handleOrgApplyFeature()
                    && DomainSettingsManager.getInstance().handleEmailSettingsFeature()) {

                sessions = ChatService.queryAllSessionsDb();

            } else {
                sessions = ChatService.queryFilteredSessionsFromDb();
            }
            if (sessions == null) {
                return;
            }
            Set<Session> cacheSessions = ChatSessionDataWrap.getInstance().getSessions();
            if (cacheSessions == null) {
                ChatSessionDataWrap.getInstance().setSessionList(sessions);
                mHandler.obtainMessage(MSG_REFRESH_SESSION_LIST).sendToTarget();
                return;
            }

            Set<Session> sessionsCompared = new HashSet<>(sessions);
            Set<Session> cacheSessionsCompared = new HashSet<>(cacheSessions);

            filterSessionFromDb(sessionsCompared);
            filterSessionFromCache(cacheSessionsCompared);


            if (cacheSessionsCompared.size() == sessionsCompared.size()) {
                return;
            }
            ChatSessionDataWrap.getInstance().clearSessionData();
            ChatSessionDataWrap.getInstance().setSessionList(sessions);
            mHandler.obtainMessage(MSG_REFRESH_SESSION_LIST).sendToTarget();
        });
    }

    private void filterSessionFromCache(Set<Session> cacheSessionsCompared) {
        CollectionsKt.removeAll(cacheSessionsCompared, session -> {

            if(Session.WORKPLUS_DISCUSSION_HELPER.equals(session.identifier)) {
                return true;
            }

            if(Session.WORKPLUS_SUMMARY_HELPER.equals(session.identifier)) {
                return true;
            }

            if(!DomainSettingsManager.getInstance().handleFriendsRelationshipsFeature()) {
                if(FriendNotifyMessage.FROM.equals(session.identifier)) {
                    return true;
                }
            }

            if(!DomainSettingsManager.getInstance().handleOrgApplyFeature()) {
                if(OrgNotifyMessage.FROM.equals(session.identifier)) {
                    return true;
                }
            }

            if(!DomainSettingsManager.getInstance().handleEmailSettingsFeature()) {
                if(Session.EMAIL_APP_ID.equals(session.identifier)) {
                    return true;
                }
            }


            return false;
        });
    }

    private void filterSessionFromDb(Set<Session> sessionsCompared) {
        CollectionsKt.removeAll(sessionsCompared, session -> {

            if(Session.WORKPLUS_DISCUSSION_HELPER.equals(session.identifier)) {
                return true;
            }

            if(Session.WORKPLUS_SUMMARY_HELPER.equals(session.identifier)) {
                return true;
            }

            return false;
        });
    }

    @Override
    public void networkChanged(NetworkBroadcastReceiver.NetWorkType networkType) {
        if (mNetworkErrorViewManager != null) {
            LogUtil.e("refreshNetworkStatusUI  ChatListFragment");

            mNetworkErrorViewManager.refreshNetworkStatusUI(networkType.hasNetwork());
        }
    }


    /**
     * 回调
     */
    private class SessionLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Session>> {

        @Override
        public Loader<List<Session>> onCreateLoader(int id, Bundle args) {
            return new ChatSessionListLoader(getActivity());
        }

        @Override
        public void onLoadFinished(Loader<List<Session>> loader, List<Session> data) {
            refreshViewTotally();
            SessionRefreshHelper.notifyRefreshCount();

            //清除过期的 session
            ChatService.calibrateExpiredSessions();
        }

        @Override
        public void onLoaderReset(Loader<List<Session>> loader) {

        }
    }

    @Override
    protected boolean onBackPressed() {
        return false;
    }


    private void unRegisterBroadcast() {
        if (mMessageRefreshBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mMessageRefreshBroadcastReceiver);
        }
        mMessageRefreshBroadcastReceiver = null;

        if (mDeviceOnlineReceiver != null) {
            LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mDeviceOnlineReceiver);
        }
        mDeviceOnlineReceiver = null;

        if (mMessageReceivingReceiver != null) {
            LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mMessageReceivingReceiver);
        }
        mMessageReceivingReceiver = null;

        if (mRefreshOrgSessionReceiver != null) {
            LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mRefreshOrgSessionReceiver);
        }
        mRefreshOrgSessionReceiver = null;

        if (mUnReadNumRefreshReceiver != null) {
            LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mUnReadNumRefreshReceiver);
        }
        mUnReadNumRefreshReceiver = null;
    }

    public static int getSessionInvalidContent(int type) {

        switch (type) {
            case DiscussionHelper.SESSION_INVALID_DISCUSSION_KICKED:
                return R.string.session_invalid_discussion_kicked;

            case DiscussionHelper.SESSION_INVALID_DISCUSSION_DISMISSED:
                return R.string.session_invalid_discussion_dimissed;

            case DiscussionHelper.SESSION_INVALID_SERVE_NO:
                return R.string.session_invalid_serve_no;


        }

        return R.string.session_invalid;
    }

    public static class UiHandler extends Handler {
        public WeakReference<ChatListFragment> mWeakRef;

        public UiHandler(ChatListFragment chatListFragment) {
            mWeakRef = new WeakReference<>(chatListFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case MSG_REFRESH_SESSION_LIST:
                    ChatListFragment listFragment = mWeakRef.get();
                    if (null != listFragment) {
                        listFragment.refreshViewTotally();

                    }
                    break;
            }
        }
    }

    @Override
    public void onThemeUpdate(Theme theme) {
        super.onThemeUpdate(theme);
        if (null != mChatListArrayAdapter) {
            mChatListArrayAdapter.notifyDataSetChanged();
        }
    }

    public String getFragmentName() {
        String title = "";
        try {
            title = BeeWorks.getInstance().getBeeWorksTabById(BaseApplicationLike.baseContext, mId).name;
            title = TextUtils.isEmpty(title) ? AtworkApplicationLike.getResourceString(R.string.item_chat) : title;
        } catch (NullPointerException e) {
            e.printStackTrace();
            title = AtworkApplicationLike.getResourceString(R.string.item_chat);
        }
        return title;
    }

    /**
     * 更改消息界面顶部栏状态
     */
    private void checkMessageReceiving() {
        if (mShouldShowTitle) {
            mMessageReceiving.setVisibility(View.VISIBLE);
            setTitle(getString(R.string.message_receiving));
            return;
        }
        setTitle(getFragmentName());
        mMessageReceiving.setVisibility(GONE);
    }

    private void setTitle(String title) {
        mTitleView.setText(title);
    }

    private void calMessageUnread() {
        int allUnread = ChatSessionDataWrap.getInstance().getUnreadCount();
        chatListUnread(allUnread);
        IntentUtil.setBadge(mActivity);

    }

    public void chatListUnread(int count) {
        LightNoticeMapping lightNoticeModel = SimpleLightNoticeMapping.createInstance(mId, TabNoticeManager.APP_ID_UNREAD_UPDATE);
        LightNoticeData lightNoticeJson = LightNoticeData.createNumLightNotice(count);
        TabNoticeManager.getInstance().update(lightNoticeModel, lightNoticeJson);

    }

    public synchronized void scrollToUnreadUnSaw() {
        List<Integer> unreadPositionList = getUnreadSessionPositionList();
        if(ListUtil.isEmpty(unreadPositionList)) {
            return;
        }


        int scrollUnreadPosition = getScrollUnreadPosition(unreadPositionList);
        LogUtil.e("scrollUnreadPosition -> " + scrollUnreadPosition);

        mLinearLayoutManager.scrollToPositionWithOffset(scrollUnreadPosition, 0);

    }

    private List<Integer> getUnreadSessionPositionList() {
        List<Integer> unreadPositionList = new ArrayList<>();
        for(int i = 0; i < mSessionList.size(); i++) {
            Session session = mSessionList.get(i);
            if(session.havingUnread()) {
                unreadPositionList.add(i);
            }
        }

        return unreadPositionList;
    }

    private int getScrollUnreadPosition(List<Integer> unreadPositionList) {

        int firstVisiblePosition = mLinearLayoutManager.findFirstVisibleItemPosition();
        int lastVisiblePosition = mLinearLayoutManager.findLastVisibleItemPosition();

        LogUtil.e("firstVisiblePosition: " + firstVisiblePosition);
        LogUtil.e("lastVisiblePosition: " + lastVisiblePosition);

        if(lastVisiblePosition == mSessionList.size() - 1) {
            return unreadPositionList.get(0);
        }


        for(Integer unreadPosition : unreadPositionList) {
            if(firstVisiblePosition + 1 <= unreadPosition) {
                return unreadPosition;
            }
        }

        return unreadPositionList.get(0);

    }

    private void showDeviceOnlineStatus(boolean isDeviceOnline, boolean isMute, String deviceSystem) {
        try {
            if (isDeviceOnline) {
                mChatListArrayAdapter.addHeaderView(mDeviceOnlineView);
                mDeviceOnlineView.setContent(AtworkApplicationLike.baseContext, isMute, deviceSystem);

    //            showOnlineV0(deviceSystem);

            } else {

                mChatListArrayAdapter.removeHeaderView(mDeviceOnlineView);
                imagePcOnline.setVisibility(GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        mChatListView.setAdapter(mChatListArrayAdapter);
    }

    private void showOnlineV0(String deviceSystem) {
        imagePcOnline.setVisibility(View.VISIBLE);

        if(deviceSystem != null && !deviceSystem.equals("")){
            if(deviceSystem.equalsIgnoreCase("windows")){
                imagePcOnline.setImageResource(R.mipmap.image_win_online);
            }else if(deviceSystem.equalsIgnoreCase("mac")){
                imagePcOnline.setImageResource(R.mipmap.image_mac_online);
            }else{
                imagePcOnline.setImageResource(R.mipmap.tab_icon_pc_online);
            }
        }
    }
}
