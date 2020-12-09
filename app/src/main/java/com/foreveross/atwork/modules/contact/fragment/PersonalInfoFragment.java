package com.foreveross.atwork.modules.contact.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.foreverht.cache.UserCache;
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.Employee.EmployeeAsyncNetService;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.component.WorkplusBottomPopDialog;
import com.foreveross.atwork.component.viewPager.AdjustHeightViewPager;
import com.foreveross.atwork.db.daoService.EmployeeDaoService;
import com.foreveross.atwork.db.daoService.OrganizationDaoService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.manager.OrganizationSettingsManager;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.MobileContact;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.domain.DomainSettings;
import com.foreveross.atwork.infrastructure.model.employee.DataSchema;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.voip.MeetingInfo;
import com.foreveross.atwork.infrastructure.model.voip.VoipType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleItem;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.support.ContactProviderRepository;
import com.foreveross.atwork.infrastructure.utils.CommonUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.MapUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.manager.EmployeeManager;
import com.foreveross.atwork.manager.OnlineManager;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.manager.model.CheckTalkAuthResult;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.fragment.ChatInfoFragment;
import com.foreveross.atwork.modules.chat.model.EntrySessionRequest;
import com.foreveross.atwork.modules.chat.service.ChatPermissionService;
import com.foreveross.atwork.modules.chat.util.ChatDetailExposeBroadcastSender;
import com.foreveross.atwork.modules.contact.activity.PersonalInfoActivity;
import com.foreveross.atwork.modules.contact.activity.UserAvatarPreviewActivity;
import com.foreveross.atwork.modules.contact.adapter.PersonInfoPagerAdapter;
import com.foreveross.atwork.modules.contact.component.ContactInfoItemView;
import com.foreveross.atwork.modules.contact.component.ContactObservableScrollView;
import com.foreveross.atwork.modules.contact.component.ContactScrollViewListener;
import com.foreveross.atwork.modules.contact.component.PersonInfoPagerView;
import com.foreveross.atwork.modules.contact.data.StarUserListDataWrap;
import com.foreveross.atwork.modules.group.activity.TransferMessageActivity;
import com.foreveross.atwork.modules.group.module.TransferMessageControlAction;
import com.foreveross.atwork.modules.group.module.TransferMessageMode;
import com.foreveross.atwork.modules.voip.activity.VoipSelectModeActivity;
import com.foreveross.atwork.modules.voip.manager.ZoomVoipManager;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.EmployeeHelper;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.foreveross.atwork.utils.IntentUtil;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by lingen on 15/4/13.
 * Description:
 */
public class PersonalInfoFragment extends BackHandledFragment implements ContactScrollViewListener {

    private static final String TAG = PersonalInfoFragment.class.getSimpleName();

    private LinearLayout mLlMessage, mLlPhone, mLlVideoMeeting, mLlDynamicCircle, mLlPersonalInfoExtensionBlock;
    private ImageView mImageAvatarContact;
    private View mViewSeparate, mViewWhiteBelow;
    private ContactObservableScrollView mContactObservableScrollView;
    private RelativeLayout mRelativeContactBody, mRlPersonalInfo;
    private Animation mAnimation_alpha = null;
    private Animation mAnimation_scale = null;
    private Boolean mAnimation_show = false;

    private TextView mAccountNameView, mTvPersonalSignature;

    private ImageView mIvBack;

    private ImageView mFavorView;

    private User mUser;

    private String mUserId;

    private String mDomainId;

    private TextView mTvSend;

    private TextView mTvTitle;
    //头像
    private ImageView mAvatarView;

    private View mAvatarNotOnlineBg;
    //更多功能
    private ImageView mIconMore;

    private ImageView mIvGender;

   // private TextView mTvReadColleague;

    private TextView mTvAccountStatus;
    private TextView mTvAccountNotOnline;

    private LinearLayout mMoreInfoLayout;

    private TabLayout mTabLayout;

    private AdjustHeightViewPager mViewPager;

    private View mViewLine;

    private PersonInfoPagerAdapter mPagerAdapter;

    private List<Employee> mLocalEmployeeList = new ArrayList<>();

    private List<Organization> mLocalIntersectionOrgList = new ArrayList<>();

    private View mLineView;

    private boolean mIsYourself = false;

    private boolean mIsYourFriend = false;
    private boolean mIsLoadEmpListRemoteRequestSuccess = false;


    //点击同步通讯录同步的号码, 与名字
    private MobileContact mContactWaitSave;

    /**
     * 用户是否已经注册 默认已注册
     */
    private boolean mUserRegistered;

    private ProgressDialogHelper mProgressDialogHelper;
    private WorkplusBottomPopDialog mDialog;
    private int mLastSelectedIndex = 0;
    private int mLastScrollPosition = -1;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_personal_info, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerListener();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (null == mUser && StringUtils.isEmpty(mUserId)) {
            return;
        }

        if (null != mUser) {
            mUserId = mUser.mUserId;
            mDomainId = mUser.mDomainId;
        }

        //已注册的才去 user 缓存去找
        if (mUserRegistered) {
            //todo db load
            User user = UserCache.getInstance().getUserCache(mUserId);
            if (user != null) {
                mUser = user;
            }

            if (StarUserListDataWrap.getInstance().containsKey(mUserId)) {
                mFavorView.setImageResource(R.mipmap.icon_avatar_contact);
            }
        }


        initData();
    }

    @Override
    protected void findViews(View view) {
        mLlMessage = view.findViewById(R.id.linear_message);
        mLlPhone = view.findViewById(R.id.linear_tel);
        mLlVideoMeeting = view.findViewById(R.id.linear_video_meeting);
        mImageAvatarContact = view.findViewById(R.id.image_avatar_contact);
        mFavorView = view.findViewById(R.id.image_avatar_contact);
        mViewSeparate =  view.findViewById(R.id.view_separate);
        mContactObservableScrollView =  view.findViewById(R.id.contact_observable_scroll_view);
        mLlPersonalInfoExtensionBlock = view.findViewById(R.id.personal_info_extension_block);
        mRelativeContactBody = view.findViewById(R.id.relative_contact_body);
        mViewWhiteBelow = view.findViewById(R.id.view_white_below);
        mRlPersonalInfo = view.findViewById(R.id.rl_personal_signature);
        mTvPersonalSignature = view.findViewById(R.id.tv_personal_info);

        mTvTitle = view.findViewById(R.id.title_bar_personal_title);
        mAccountNameView = view.findViewById(R.id.personal_info_account_name);
        mIvBack = view.findViewById(R.id.title_bar_personal_icon);
        //mFavorView = view.findViewById(R.id.personal_info_favor);
        //mTvSend = view.findViewById(R.id.tv_sendMessage);
        mIconMore = view.findViewById(R.id.title_bar_personal_more);
        mAvatarView = view.findViewById(R.id.personal_info_avatar);
        mAvatarNotOnlineBg = view.findViewById(R.id.avatar_not_online_bg);
        mIvGender = view.findViewById(R.id.person_info_gender);
        mTvAccountStatus = view.findViewById(R.id.tv_account_status);
        mTvAccountNotOnline = view.findViewById(R.id.tv_account_not_online);
        mMoreInfoLayout = view.findViewById(R.id.personal_info_layout);
        mViewPager = view.findViewById(R.id.personal_viewPager);
        mViewLine = view.findViewById(R.id.v_line);
        mTabLayout = view.findViewById(R.id.personal_tabLayout);
        //mScrollView = view.findViewById(R.id.person_scroll_view);
        mLineView = view.findViewById(R.id.personal_tabLayout_line);
        mMoreInfoLayout.setOrientation(LinearLayout.VERTICAL);

        //mTvTitle.setText(R.string.personal_info_title);
        mUser = getArguments().getParcelable(PersonalInfoActivity.DATA_USER);
        mUserRegistered = getArguments().getBoolean(PersonalInfoActivity.USER_REGISTERED, true);
        initViewPager();
        initFuncPopDialog();
    }

    private void initViewPager() {
        mPagerAdapter = new PersonInfoPagerAdapter(this, mLocalEmployeeList);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setTabTextColors(Color.parseColor("#333333"), Color.parseColor("#333333"));
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                mViewPager.reMeasureCurrentPage(mViewPager.findViewWithTag(AdjustHeightViewPager.TAG + tab.getPosition()));

                if (null != tab.getCustomView()) {
                    TextView tv = tab.getCustomView().findViewById(R.id.tv_custom);
//                    tv.setTextColor(ContextCompat.getColor(BaseApplicationLike.baseContext, R.color.common_blue_bg));
                  //  tv.setTextColor(SkinHelper.getTabActiveColor());
                    tv.setTextColor(Color.parseColor("#333333"));
                    tv.setTypeface(Typeface.DEFAULT_BOLD);
                }

                refreshIconMore();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (null != tab.getCustomView()) {
                    TextView tv = tab.getCustomView().findViewById(R.id.tv_custom);
//                    tv.setTextColor(ContextCompat.getColor(BaseApplicationLike.baseContext, R.color.alarm_text));
                   // tv.setTextColor(SkinHelper.getTabInactiveColor());
                    tv.setTypeface(Typeface.DEFAULT);
                    tv.setTextColor(Color.parseColor("#333333"));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (null != tab.getCustomView()) {
                    TextView tv = tab.getCustomView().findViewById(R.id.tv_custom);
//                    tv.setTextColor(ContextCompat.getColor(BaseApplicationLike.baseContext, R.color.common_blue_bg));
                    //tv.setTextColor(SkinHelper.getTabActiveColor());
                    tv.setTextColor(Color.parseColor("#333333"));
                    tv.setTypeface(Typeface.DEFAULT_BOLD);
                }
            }
        });

        mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mLastSelectedIndex = position;
                mViewPager.reMeasureCurrentPage(mViewPager.findViewWithTag(AdjustHeightViewPager.TAG + position));

                mViewSeparate.setVisibility(GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    private void initFuncPopDialog() {
        mDialog = new WorkplusBottomPopDialog();

        mDialog.setItemOnListener(tag -> {

            mDialog.dismiss();

            if (mActivity.getResources().getString(R.string.add_to_friend).equals(tag)) {
                if (mUser == null) {
                    Logger.d(TAG, "user is null");
                    return;
                }

                String url = String.format(UrlConstantManager.getInstance().getNewFriendUrl(), mUserId, mUser.mDomainId);
                WebViewControlAction webViewControlAction = WebViewControlAction.newAction().setUrl(url).setNeedShare(false);
                Intent intent = WebViewActivity.getIntent(getActivity(), webViewControlAction);
                getActivity().startActivity(intent);

            } else if (getResources().getString(R.string.dismiss_to_friend).equals(tag)) {

                new AtworkAlertDialog(getContext(), AtworkAlertDialog.Type.SIMPLE)
                        .setContent(R.string.ask_to_dismiss_friend)
                        .setClickBrightColorListener(dialog -> {
                            mProgressDialogHelper.show();

                            UserManager.getInstance().dismissYourFriend(getActivity(), mUser.mDomainId, mUserId, new UserManager.OnDismissFriendListener() {
                                @Override
                                public void networkFail(int errorCode, String errorMsg) {
                                    mProgressDialogHelper.dismiss();
                                    AtworkToast.showResToast(R.string.dismiss_friend_fail);
                                }

                                @Override
                                public void onSuccess() {
                                    mProgressDialogHelper.dismiss();
                                    if (isAdded()) {
                                        AtworkToast.showResToast(R.string.dismiss_friend_success);
                                        //刷新相关icon
                                        refreshViewNeedFriendShipData();
                                    }

                                }


                            });
                        })
                        .show();


            } else if (getResources().getString(R.string.send_my_card).equals(tag)) {

                AtworkApplicationLike.getLoginUser(new UserAsyncNetService.OnQueryUserListener() {
                    @Override
                    public void onSuccess(@NonNull User loginUser) {
                        ArticleItem articleItem = new ArticleItem();
                        articleItem.setBusinessCardData(mUser, getCurrentSelectEmp());

                        ChatPostMessage shareChatMessage = ShareChatMessage.newSendShareMessage(AtworkApplicationLike.baseContext, articleItem, loginUser.mUserId, loginUser.mDomainId, loginUser.getShowName(), loginUser.mAvatar, ParticipantType.User, BodyType.Share, ShareChatMessage.ShareType.BusinessCard);
                        List<ChatPostMessage> messageList = new ArrayList<>();
                        messageList.add(shareChatMessage);


                        TransferMessageControlAction transferMessageControlAction = new TransferMessageControlAction();
                        transferMessageControlAction.setSendMessageList(messageList);
                        transferMessageControlAction.setSendMode(TransferMessageMode.SEND);
                        Intent intent = TransferMessageActivity.Companion.getIntent(BaseApplicationLike.baseContext, transferMessageControlAction);

                        startActivity(intent);
                    }

                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        ErrorHandleUtil.handleError(errorCode, errorMsg);
                    }

                });


            } else if (getResources().getString(R.string.invite_join_my_org).equals(tag)) {
                checkCanChatAndInviteOrg();


            } else if (getResources().getString(R.string.sync_contact_to_mobile).equals(tag)) {
                saveMobilePhone();
            }

        });

    }

    private void doPopSelectOrgDialog() {
        List<Organization> orgList = OrganizationDaoService.getInstance().queryLoginOrganizationsSync(getActivity());
        String[] orgNameList = new String[orgList.size()];

        for (int i = 0; i < orgList.size(); i++) {
            Organization organization = orgList.get(i);

            if (organization != null && !StringUtils.isEmpty(organization.getNameI18n(BaseApplicationLike.baseContext))) {
                orgNameList[i] = organization.getNameI18n(BaseApplicationLike.baseContext);
            }
        }

        if (orgNameList.length == 0) {
            return;
        }

        //从底部弹出组织选择框
        popSelectOrgDialog(orgNameList, orgList);
    }

    /**
     * 处理数据流程
     */
    private void initData() {

        if (mUserRegistered) {
            mProgressDialogHelper = new ProgressDialogHelper(getActivity());

            //1. 渲染基本用户信息， 用户名，头像等
            inflateBasicUserInfo();

            //2. 判断用户
            refreshView();

        } else {  //如果用户没有注册过， 不往下执行


            mIconMore.setVisibility(GONE);
            //mFavorView.setVisibility(GONE);
           // mTvReadColleague.setVisibility(GONE);

           // mTvSend.setVisibility(VISIBLE);
//            mTvSend.setBackgroundResource(R.drawable.shape_send_gray);
           // mTvSend.setText(getResources().getString(R.string.personal_invite_by_send_msg));
            mAccountNameView.setText(mUser.mName);

            handleMobileView();
        }
        //controlDotLinearshow();


    }

    /**
     * 渲染基本用户信息， 用户名，头像等
     */

    private void inflateBasicUserInfo() {
        mAccountNameView.setText(TextUtils.isEmpty(mUser.getShowName()) ? mUser.mPhone : mUser.getShowName());
        refreshAvatar();
        mIvGender.setVisibility(GONE);
        if (DomainSettingsManager.getInstance().isUserShowGender()) {

            if ("male".equalsIgnoreCase(mUser.mGender)) {
                mIvGender.setBackgroundResource(R.mipmap.icon_gender_male);
                mIvGender.setVisibility(VISIBLE);

            }
            if ("female".equalsIgnoreCase(mUser.mGender)) {
                mIvGender.setBackgroundResource(R.mipmap.icon_gender_female);
                mIvGender.setVisibility(VISIBLE);
            }
        }

        refreshStatusView();

        boolean needVoipActionBtn = !ListUtil.isEmpty(getVoipInstantActionList());
        ViewUtil.setVisible(mLlVideoMeeting, needVoipActionBtn);
        if(DomainSettingsManager.getInstance().handlePersonalSignatureEnable() && !StringUtils.isEmpty(mUser.getSignature())){
            mTvPersonalSignature.setText(mUser.getSignature());
            mRlPersonalInfo.setVisibility(VISIBLE);

        }else{
            mRlPersonalInfo.setVisibility(GONE);

        }

    }

    /**
     * 判断刷新start时候的view，主要是判断当前用户与登录用户是否为同一个
     */
    private void refreshView() {

        //如果用户为登录用户 处理一下显示隐藏逻辑
        if (!TextUtils.isEmpty(mUserId)
                && User.isYou(getActivity(), mUserId)) {
            //mTvSend.setVisibility(GONE);
            mContactObservableScrollView.setPadding(0, 0, 0, 10);
            //mFavorView.setVisibility(GONE);
            mLlPersonalInfoExtensionBlock.setVisibility(GONE);
            mIsYourself = true;

        }else{
            mLlPersonalInfoExtensionBlock.setVisibility(VISIBLE);
        }
        //3. 取当前用户和登录用户的组织结构交集
        OrganizationManager.getInstance().queryOrgIntersectionInLoginUser(mUserId, intersectionList -> {
            mLocalIntersectionOrgList.addAll(intersectionList);

            queryEmpInfoLocal();
        });
    }


    /**
     * 查询本地雇员信息
     */
    private void queryEmpInfoLocal() {

        EmployeeDaoService.getInstance().queryEmpListLocal(mUserId, Organization.getOrgCodeList(mLocalIntersectionOrgList), empList -> {
            if (isAdded()) {
                mLocalEmployeeList.clear();
                mLocalEmployeeList.addAll(empList);

                //赋值 orgName 给雇员
                for (Employee employee : mLocalEmployeeList) {

                    for (Organization organization : mLocalIntersectionOrgList) {
                        if (organization.mOrgCode.equalsIgnoreCase(employee.orgCode)) {
                            employee.setOrgInfo(organization.getNameI18n(BaseApplicationLike.baseContext), organization.mCreated, organization.getSortOrder());
                            break;
                        }
                    }
                }

                Collections.sort(mLocalEmployeeList);

                refreshEmployeeUI();

                syncUserFromRemote();

            }
        });

    }


    /**
     * 刷新需要好友关系才能判断显示隐藏的视图, 如自己, 非同事, 非好友 都要隐藏星标, 还有"更多"图标
     */
    private void refreshViewNeedFriendShipData() {
        if (mIsYourself) {
            //mFavorView.setVisibility(GONE);
            refreshIconMore();

        } else {
            UserManager.getInstance().isYourFriend(mUserId, right -> {
                if (isAdded()) {
                    if (right || !ListUtil.isEmpty(mLocalIntersectionOrgList)) {
                      //  mFavorView.setVisibility(VISIBLE);

                    } else {
                        //mFavorView.setVisibility(GONE);

                    }
                    mIsYourFriend = right;
                    refreshIconMore();
                }

            });


        }


    }

    private void refreshIconMore() {
        if (mIsYourself || ListUtil.isEmpty(getPopItemList())) {
            mIconMore.setVisibility(GONE);

        } else {
            mIconMore.setVisibility(VISIBLE);

        }
    }


    private void refreshEmployeeUI() {
        boolean needPhone = !MapUtil.isEmpty(EmployeeHelper.getMobileDataSchemaStringHashMap(mLocalEmployeeList));
        ViewUtil.setVisible(mLlPhone, needPhone);

        refreshViewNeedFriendShipData();
        refreshViewPager();
    }

    /**
     * 公有云使用 viewpager
     */
    private void refreshViewPager() {
        if(!AtworkConfig.EMPLOYEE_CONFIG.getShowEmpInfo()) {
            handleViewpagerLayoutVisible(GONE);
            mMoreInfoLayout.setVisibility(VISIBLE);

            return;
        }


        if (noLocalEmp()) {
            handleViewpagerLayoutVisible(GONE);

            mMoreInfoLayout.setVisibility(VISIBLE);
//            handleMobileView();


        } else {
            handleViewpagerLayoutVisible(VISIBLE);

            mMoreInfoLayout.setVisibility(GONE);

            Collections.sort(mLocalEmployeeList);

            mLastScrollPosition = mTabLayout.getScrollX();

            mPagerAdapter.notifyDataSetChanged();

            refreshTab();

            mTabLayout.scrollTo(mLastScrollPosition, 0);

            //调整 viewpager 高度, 以适应 scrollView, 否则会存在偶尔无法滑动的问题
            mViewPager.postDelayed(() -> mViewPager.reMeasureCurrentPage(mViewPager.findViewWithTag(AdjustHeightViewPager.TAG + mViewPager.getCurrentItem())), 100);

        }

    }

    private void refreshTab() {
        //自定义tab
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);

            if (tab != null) {
                tab.setCustomView(mPagerAdapter.getTabView(getActivity(), i));

                if (mLastSelectedIndex == i) {
                    tab.select();
                }
            }

        }
    }


    /**
     * 控制 viewpager 视图的可见与不可见
     */
    private void handleViewpagerLayoutVisible(int showOrHide) {
        mTabLayout.setVisibility(showOrHide);
        mViewLine.setVisibility(showOrHide);
        mViewPager.setVisibility(showOrHide);
    }

    /**
     * 私有云使用 2.0的UI模式
     */
    private void refreshPersonalView() {
        handleViewpagerLayoutVisible(GONE);
        mMoreInfoLayout.setVisibility(VISIBLE);

        if (!noLocalEmp()) {
            PersonInfoPagerView itemView = new PersonInfoPagerView(getActivity(), this);
            Employee employee = mLocalEmployeeList.get(0);
            itemView.refreshDataSchema(employee, employee.dataSchemaList);

            mMoreInfoLayout.addView(itemView);
        }

        if (!StringUtils.isEmpty(AtworkConfig.COLLEAGUE_URL)) {
           // addReadColleagueItemView();
        }


    }


    public void callPhoneByEmps() {
        if(null == mLocalEmployeeList) {
            toast(R.string.network_not_avaluable);
            syncOrgAndEmpDataRemote();

            return;
        }

        EmployeeHelper.callPhone(getActivity(), mLocalEmployeeList);
    }


    /**
     * 同步组织与雇员的数据
     */
    private void syncOrgAndEmpDataRemote() {
        EmployeeManager.getInstance().queryOrgAndEmpListRemote(AtworkApplicationLike.baseContext, mUserId, new EmployeeManager.QueryOrgAndEmpListListener() {

            @Override
            public void onSuccess(List<Organization> organizationList, List<Employee> employeeList) {
                if (isAdded()) {
                    UserManager.getInstance().addInterceptRequestIdCheckCheckUserEmpInfoRemote(mUserId);

                    if (!ListUtil.isEmpty(employeeList)) {
                        mLocalEmployeeList.clear();
                        mLocalIntersectionOrgList.clear();

                        mLocalEmployeeList.addAll(employeeList);
                        mLocalIntersectionOrgList.addAll(organizationList);


                    }

                    refreshEmployeeUI();

                }

                mIsLoadEmpListRemoteRequestSuccess = true;

            }

            @Override
            public void onFail() {

            }
        });
    }




    private void refreshAvatar() {
        if (mUser.isStatusInitialized()) {
            makeAvatarGray();

            ImageCacheHelper.displayImageByMediaId(mUser.mAvatar, mAvatarView, ImageCacheHelper.getRoundOptions(R.mipmap.default_photo, -1));

            return;
        }
        mAvatarView.setImageResource(R.mipmap.avatar_not_initialize);
    }

    /**
     * 制灰头像
     */
    private void makeAvatarGray() {
        if (DomainSettingsManager.getInstance().handleUserOnlineFeature() && !OnlineManager.getInstance().isOnline(mUserId)) {
            ColorMatrix matrix = new ColorMatrix();
//            matrix.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            mAvatarView.setColorFilter(filter);

        } else {
            mAvatarView.clearColorFilter();
        }
    }

    /**
     * 控制激活状态的显示
     */
    private void refreshStatusView() {
        if (mUser.isStatusInitialized() || StringUtils.isEmpty(mUser.mStatus)) {
           mTvAccountStatus.setVisibility(GONE);
//            mAvatarView.clearColorFilter();
            mTvAccountNotOnline.setVisibility(DomainSettingsManager.getInstance().handleUserOnlineFeature() && !OnlineManager.getInstance().isOnline(mUserId) ? VISIBLE : GONE);

        } else {
            mTvAccountStatus.setVisibility(VISIBLE);
            mTvAccountNotOnline.setVisibility(GONE);
//            mAvatarView.setColorFilter(0x70000000, PorterDuff.Mode.SRC_ATOP);

        }
    }


    /**
     * 从网络中同步用户
     */
    private void syncUserFromRemote() {
        if(!UserManager.getInstance().checkLegalRequestIdCheckCheckUserEmpInfoRemote(mUserId)) {
            return;
        }

        UserManager.getInstance().asyncQueryUserInfoFromRemote(getContext(), mUserId, mUser.mDomainId, new UserAsyncNetService.OnUserCallBackListener() {
            @Override
            public void networkFail(int errorCode, String errorMsg) {

                if (!ErrorHandleUtil.handleTokenError(errorCode, errorMsg)) {
                    ErrorHandleUtil.handleError(errorCode, errorMsg);
                    syncOrgAndEmpDataRemote();

                }
            }

            @Override
            public void onFetchUserDataSuccess(Object... object) {
                mUser = (User) object[0];

                UserManager.getInstance().asyncAddUserToLocal(mUser);

                //update online status
                OnlineManager.getInstance().setOnlineStatus(mUser.mUserId, mUser.isOnline());

                inflateBasicUserInfo();

                syncOrgAndEmpDataRemote();

                ChatDetailExposeBroadcastSender.refreshMessageListViewUI();
                LocalBroadcastManager.getInstance(mActivity).sendBroadcast(new Intent(ChatInfoFragment.REFRESH_CHAT_INFO));
                ChatDetailExposeBroadcastSender.changeUser(mActivity, mUser);
            }


        });

    }


    private void registerListener() {

        mIconMore.setOnClickListener(v -> {

            if (!CommonUtil.isFastClick(2000)) {
                popPersonalFuncDialog();

            }

        });

        //将此人加为常用联系人
        mFavorView.setOnClickListener(v -> {
            if (StarUserListDataWrap.getInstance().containsKey(mUserId)) {
                removeFavorContact();
                return;
            }

            checkCanChatAndStarContact();
        });


        //返回事件
        mIvBack.setOnClickListener(v -> finish());

        //消息点击事件,跳转到聊天页面
        mLlMessage.setOnClickListener(v -> {
            if (mUserRegistered) {

                checkCanChatAndSendMessage();


            } else {
                DomainSettings domainSettings = BaseApplicationLike.getDomainSetting();
                if (null != domainSettings) {
                    IntentUtil.sendSms(mActivity, mUser.mPhone, getString(R.string.using_send_msg, getString(R.string.app_name), domainSettings.getWorkPlusUrl()));

                }
            }

        });
        //电话点击事件，跳转到电话页面
        mLlPhone.setOnClickListener(v -> {
            callPhoneByEmps();
        });

        mLlVideoMeeting.setOnClickListener(view -> {
            List<String> voipCallActionList = getVoipInstantActionList();

            if(1 == voipCallActionList.size()) {
                handlePopVoipActionItemClick(voipCallActionList.get(0));
                return;
            }

            String[] voipActions = voipCallActionList.toArray(new String[0]);

            WorkplusBottomPopDialog workplusBottomPopDialog = new WorkplusBottomPopDialog();
            workplusBottomPopDialog.refreshData(voipActions);

            workplusBottomPopDialog.setItemTextDrawable(0, R.mipmap.icon_bing_voice);
            workplusBottomPopDialog.setItemTextDrawable(1, R.mipmap.icon_meeting_instant_new);

            workplusBottomPopDialog.setItemOnListener(this::handlePopVoipActionItemClick);

            workplusBottomPopDialog.show(getFragmentManager(), "voipActionDialog");

        });

        mAvatarView.setOnClickListener(v -> {
            if (!StringUtils.isEmpty(mUser.mAvatar) && mUser.isStatusInitialized()) {
                Intent intent = UserAvatarPreviewActivity.getIntent(getActivity(), mUser.mAvatar);
                startActivity(intent);
            }

        });

        mContactObservableScrollView.setScrollViewListener(this);

    }

    private void handlePopVoipActionItemClick(String tag) {
        if(getStrings(R.string.start_meeting).equals(tag)) {
            bizconfRouteP2pCallInstant(getActivity());
            return;
        }

        if(getStrings(R.string.label_voip_meeting_chat_pop).equals(tag)) {
            startP2pTypeVoipInstant();
            return;
        }
    }

    @NotNull
    private List<String> getVoipInstantActionList() {
        List<String> voipCallActionList = new ArrayList<>();
        if(VoipHelper.isVoipEnable(AtworkApplicationLike.baseContext)) {
            voipCallActionList.add(getStrings(R.string.label_voip_meeting_chat_pop));
        }

        if (AtworkConfig.ZOOM_CONFIG.isUrlEnabled()) {
            voipCallActionList.add(getStrings(R.string.start_meeting));
        }
        return voipCallActionList;
    }

    public void startP2pTypeVoipInstant() {
        if (VoipHelper.isHandlingVoipCall()) {
            AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);
            return;
        }

        ArrayList<User> coupleList = new ArrayList<>();
        coupleList.add(mUser);

        AtworkApplicationLike.getLoginUser(new UserAsyncNetService.OnQueryUserListener() {
            @Override
            public void onSuccess(@NonNull User user) {
                coupleList.add(user);

                Intent intent = VoipSelectModeActivity.getIntent(getActivity(), coupleList);
                startActivity(intent);
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleTokenError(errorCode, errorMsg);
            }
        });
    }


    private void bizconfRouteP2pCallInstant(Activity activity) {
        if (VoipHelper.isHandlingVoipCall()) {
            AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);
            return;
        }

        MeetingInfo meetingInfo = new MeetingInfo();
        meetingInfo.mType = MeetingInfo.Type.USER;
        meetingInfo.mId = mUserId;
        meetingInfo.mOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext);

        AtworkApplicationLike.getLoginUser(new UserAsyncNetService.OnQueryUserListener() {
            @Override
            public void onSuccess(@NonNull User loginUser) {
                List<ShowListItem> selectContactList = new ArrayList<>();
                selectContactList.add(loginUser);
                selectContactList.add(mUser);

                ZoomVoipManager.INSTANCE.goToCallActivity(activity, null, meetingInfo, VoipType.VIDEO, false, true, selectContactList, null, null);
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleError(errorCode, errorMsg);
            }
        });
    }


    private void checkCanChatAndStarContact() {

        checkCanChatAndAction(new CheckCanChatAndActionListener(this) {
            @Override
            public Unit invoke(CheckTalkAuthResult checkTalkAuthResult) {
                super.invoke(checkTalkAuthResult);

                if (CheckTalkAuthResult.CAN_TALK == checkTalkAuthResult) {
                    addFavorContact();

                }

                return Unit.INSTANCE;
            }
        });


    }

    private void checkCanChatAndSendMessage() {

        checkCanChatAndAction(new CheckCanChatAndActionListener(this) {
            @Override
            public Unit invoke(CheckTalkAuthResult checkTalkAuthResult) {
                super.invoke(checkTalkAuthResult);

                if (CheckTalkAuthResult.CAN_TALK == checkTalkAuthResult) {
                    doSendMessage();

                }

                return Unit.INSTANCE;

            }
        });


    }


    private void checkCanChatAndInviteOrg() {
        checkCanChatAndAction(new CheckCanChatAndActionListener(this) {
            @Override
            public Unit invoke(CheckTalkAuthResult checkTalkAuthResult) {
                super.invoke(checkTalkAuthResult);

                if (CheckTalkAuthResult.CAN_TALK == checkTalkAuthResult) {
                    doPopSelectOrgDialog();
                }
                return Unit.INSTANCE;


            }
        });
    }

    private void checkCanChatAndAction(CheckCanChatAndActionListener checkCanChatAndAction) {
        if (ListUtil.isEmpty(mLocalEmployeeList)) {
            if (!mIsLoadEmpListRemoteRequestSuccess && UserManager.getInstance().checkLegalRequestIdCheckCheckUserEmpInfoRemote(mUserId)) {
                toast(R.string.network_not_avaluable);
                syncOrgAndEmpDataRemote();
                return;
            }

        }

        mProgressDialogHelper.show();
        ChatPermissionService.INSTANCE.canChat(mLocalEmployeeList, mUserId, mDomainId, checkCanChatAndAction);
    }
    @Override
    public void onScrollChanged(ContactObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
        if(scrollView == mContactObservableScrollView){
            // 监听屏幕滑动状态-当控件可见时，执行相关操作
            if(isVisible(mAccountNameView)){
                mTvTitle.setVisibility(GONE);
                mViewWhiteBelow.setVisibility(GONE);
                mAnimation_show = false;
            }else{
                mTvTitle.setText(mAccountNameView.getText());
                mTvTitle.setVisibility(VISIBLE);
                mViewWhiteBelow.setVisibility(VISIBLE);
                if(mAnimation_show ==false){
                    mAnimation_alpha = AnimationUtils.loadAnimation(getContext(),R.anim.alpha_show);
                    mTvTitle.startAnimation(mAnimation_alpha);
                    mAnimation_show = true;
                }
            }
        }
    }

    private boolean isVisible(View v) {
        return v.getLocalVisibleRect(new Rect());
    }

    public static class CheckCanChatAndActionListener implements Function1<CheckTalkAuthResult, Unit> {

        private WeakReference<PersonalInfoFragment> mFragmentWeakReference;

        public CheckCanChatAndActionListener(PersonalInfoFragment personalInfoFragment) {
            mFragmentWeakReference = new WeakReference<>(personalInfoFragment);
        }

        @Override
        public Unit invoke(CheckTalkAuthResult checkTalkAuthResult) {
            PersonalInfoFragment personalInfoFragment = mFragmentWeakReference.get();

            switch (checkTalkAuthResult) {

                case CANNOT_TALK:
                case MAY_NOT_TALK:

                    AtworkToast.showToast(DomainSettingsManager.getInstance().getConnectionNonsupportPrompt());


                    break;

                case NETWORK_FAILED:
                    AtworkToast.showResToast(R.string.network_not_avaluable);
                    break;
            }

            if (null != personalInfoFragment) {
                personalInfoFragment.getProgressDialogHelper().dismiss();
            }

            return null;
        }
    }


    private void doSendMessage() {

        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }

        EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest(SessionType.User, mUser);

        ChatSessionDataWrap.getInstance().entrySession(entrySessionRequest);
        startActivity(ChatDetailActivity.getIntent(activity, mUserId));
    }

    private void popPersonalFuncDialog() {

        List<String> popItemList = getPopItemList();

        mDialog.refreshData(popItemList.toArray(new String[0]));

        if (!ListUtil.isEmpty(popItemList)) {
            if (getString(R.string.dismiss_to_friend).equals(popItemList.get(0))) {
                mDialog.setItemTextColor(0, ContextCompat.getColor(getActivity(), R.color.red_lock));

            } else {
                mDialog.setItemTextColor(0, ContextCompat.getColor(getActivity(), R.color.common_text_color));

            }

            if (isAdded()) {
                mDialog.show(getChildFragmentManager(), "show_more");

            }
        }

    }

    @NonNull
    private List<String> getPopItemList() {
        List<String> popItemList = new ArrayList<>();

        if (mIsYourself) {
            popItemList.add(getString(R.string.sync_contact_to_mobile));
            return popItemList;
        }

        if (DomainSettingsManager.getInstance().handleFriendsRelationshipsFeature()) {
            if (mIsYourFriend) {
                popItemList.add(getString(R.string.dismiss_to_friend));
            } else {
                popItemList.add(getString(R.string.add_to_friend));
            }

        }

        if (DomainSettingsManager.getInstance().handleOrgApplyFeature()) {
            popItemList.add(getString(R.string.invite_join_my_org));
        }

        boolean showSendMyCard = isShowSendMyCard();

        if (showSendMyCard) {
            popItemList.add(getString(R.string.send_my_card));
        }

        boolean shouldShowSyncItem = false;

        if (DomainSettingsManager.getInstance().handleMobileContactSyncFeature() >= DomainSettingsManager.SYNC_CONTACT_PERSONAL) {
            popItemList.add(getString(R.string.sync_contact_to_mobile));
            if (noLocalEmp()) {
                if (!StringUtils.isEmpty(mUser.mPhone)) {
                    mContactWaitSave = new MobileContact(mUser.getShowName(), mUser.mPhone);
                    shouldShowSyncItem = true;
                }

                //没有当前的组织 code, 视为没有组织
                if (StringUtils.isEmpty(PersonalShareInfo.getInstance().getCurrentOrg(getActivity()))) {
                    popItemList.remove(getString(R.string.invite_join_my_org));

                }


            } else {
                Employee currentEmp = getCurrentSelectEmp();

                if (null != currentEmp) {
                    List<String> mobileList = EmployeeHelper.getShowMobileList(currentEmp);
                    if (!ListUtil.isEmpty(mobileList)) {

                        List<String> emailList = EmployeeHelper.getShowEmailList(currentEmp);
                        if (!ListUtil.isEmpty(emailList)) {
                            emailList.addAll(emailList);
                        }

                        mContactWaitSave = new MobileContact(currentEmp.getShowName(), mobileList, emailList);
                        shouldShowSyncItem = true;

                    }
                }
            }
            if (!shouldShowSyncItem) {
                popItemList.remove(getString(R.string.sync_contact_to_mobile));
                mContactWaitSave = null;
            }
        }
        return popItemList;
    }

    private boolean isShowSendMyCard() {
        boolean showSendMyCard = true;

        Employee currentSelectEmp = getCurrentSelectEmp();
        if (null == currentSelectEmp) {
            //若网络请求没有正常完成, 则先不显示出名片先
            showSendMyCard = mIsLoadEmpListRemoteRequestSuccess;

        } else {
            if (OrganizationSettingsManager.getInstance().isSeniorShowOpen(getActivity(), currentSelectEmp.orgCode)) {
                //"显示高管"开关打开时, 不能分享高管的名片
                showSendMyCard = !currentSelectEmp.senior;
            }


        }


        return showSendMyCard;
    }


    @Nullable
    private Employee getCurrentSelectEmp() {
        if (noLocalEmp()) {
            return null;
        }

        int selectedTabPosition = mTabLayout.getSelectedTabPosition();
        if (-1 == selectedTabPosition) {
            selectedTabPosition = 0;
        }
        return mLocalEmployeeList.get(selectedTabPosition);
    }

    private boolean noLocalEmp() {
        return ListUtil.isEmpty(mLocalEmployeeList);
    }


    @Nullable
    private Boolean canSeeEmp() {
        boolean noLocalEmp = noLocalEmp();
        if (!noLocalEmp) {
            return true;
        }

        if (mIsLoadEmpListRemoteRequestSuccess) {
            return false;
        }

        return null;
    }

    private void popSelectOrgDialog(String[] orgNameList, List<Organization> orgList) {
        WorkplusBottomPopDialog popDialog = new WorkplusBottomPopDialog();
        popDialog.refreshData(orgNameList);
        popDialog.setItemOnListener(tag1 -> {

            AtworkApplicationLike.getLoginUser(new UserAsyncNetService.OnQueryUserListener() {
                @Override
                public void onSuccess(@NonNull User loginUser) {
                    for (Organization organization : orgList) {
                        if (organization.getNameI18n(BaseApplicationLike.baseContext).equals(tag1)) {
                            ArticleItem articleItem = new ArticleItem();
                            articleItem.setOrgInviteData(organization);

                            EmployeeManager.getInstance().queryEmp(mActivity, loginUser.mUserId, organization.mOrgCode, new EmployeeAsyncNetService.QueryEmployeeInfoListener() {
                                @Override
                                public void onSuccess(@NonNull Employee employee) {
                                    //分享描述的内容
                                    articleItem.mDescription = EmployeeManager.getInstance().getOrgInviteDescription(mActivity, organization.mOwner, organization.mName, loginUser.mUserId, employee.getShowName());
                                    articleItem.mOrgInviterName = employee.getShowName();

                                    ChatPostMessage shareChatMessage = ShareChatMessage.newSendShareMessage(AtworkApplicationLike.baseContext, articleItem, loginUser.mUserId, loginUser.mDomainId, loginUser.getShowName(), loginUser.mAvatar, ParticipantType.User, BodyType.Share, ShareChatMessage.ShareType.OrgInviteBody);
                                    shareChatMessage.reGenerate(mActivity, loginUser.mUserId, mUserId, mUser.mDomainId, ParticipantType.User, ParticipantType.User, shareChatMessage.mBodyType, null, mUser, loginUser.getShowName(), loginUser.mAvatar);

                                    List<ChatPostMessage> messageList = new ArrayList<>();
                                    messageList.add(shareChatMessage);

                                    EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest(SessionType.User, mUser);

                                    ChatSessionDataWrap.getInstance().entrySession(entrySessionRequest);
                                    startActivity(ChatDetailActivity.getIntent(mActivity, mUserId, messageList));
                                    popDialog.dismiss();
                                }

                                @Override
                                public void networkFail(int errorCode, String errorMsg) {
                                    ErrorHandleUtil.handleBaseError(errorCode, errorMsg);

                                }
                            });

                            break;

                        }
                    }

                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    ErrorHandleUtil.handleError(errorCode, errorMsg);
                }

            });

        });
        popDialog.show(getChildFragmentManager(), "show_more");
    }

    private void saveMobilePhone() {

        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(PersonalInfoFragment.this, new String[]{Manifest.permission.WRITE_CONTACTS}, new PermissionsResultAction() {
            @Override
            public void onGranted() {

                PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(PersonalInfoFragment.this, new String[]{Manifest.permission.READ_CONTACTS}, new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        doSaveMobilePhone();

                    }

                    @Override
                    public void onDenied(String permission) {
                        AtworkUtil.popAuthSettingAlert(getContext(), permission);
                    }
                });

            }

            @Override
            public void onDenied(String permission) {
                AtworkUtil.popAuthSettingAlert(getContext(), permission);
            }
        });

    }

    private void doSaveMobilePhone() {
        ContactProviderRepository contactProviderRepository = new ContactProviderRepository(getActivity().getContentResolver());
        boolean result = contactProviderRepository.syncContactToMobile(mContactWaitSave);
        if (result) {
            AtworkToast.showToast(getResources().getString(R.string.sync_success));
        } else {
            AtworkToast.showToast(getResources().getString(R.string.sync_fail));
        }
    }

    /**
     * 删除此好友
     */
    private void removeFavorContact() {
        UserManager.getInstance().addOrRemoveStarUser(mActivity, mUser, false, new UserAsyncNetService.OnHandleUserInfoListener() {
            @Override
            public void networkFail(int errorCode, String errorMsg) {
                if (!ErrorHandleUtil.handleBaseError(errorCode, errorMsg)) {
                    AtworkToast.showResToast(R.string.database_error);

                }

            }

            @Override
            public void success() {
                StarUserListDataWrap.getInstance().removeUser(mUser);
                if (getActivity() != null) {
                    AtworkToast.showToast(getResources().getString(R.string.contact_remove_success));
                }
                mFavorView.setImageResource(R.mipmap.icon_avatar_contact_off);
            }

        });
    }

    /**
     * 添加此好友
     */
    private void addFavorContact() {

        UserManager.getInstance().addOrRemoveStarUser(mActivity, mUser, true, new UserAsyncNetService.OnHandleUserInfoListener() {
            @Override
            public void networkFail(int errorCode, String errorMsg) {
                if (!ErrorHandleUtil.handleBaseError(errorCode, errorMsg)) {
                    AtworkToast.showResToast(R.string.database_error);

                }
            }

            @Override
            public void success() {
                StarUserListDataWrap.getInstance().addUser(mUser);
                AtworkToast.showResToast(R.string.contact_add_success);
                mFavorView.setImageResource(R.mipmap.icon_avatar_contact);
            }

        });

    }

    private void handleMobileView() {
        if (mMoreInfoLayout != null) {
            mMoreInfoLayout.removeAllViews();
        }
        if (!TextUtils.isEmpty(mUser.mPhone)) {
            DataSchema dataSchema = new DataSchema();
            dataSchema.type = Employee.InfoType.MOBILE_PHONE.toString();
            dataSchema.mOpsable = false;
            dataSchema.mAlias = getString(R.string.personal_info_mobile);
            handleCommonSchemaUI(dataSchema, mUser.mPhone);
        }
    }

    private void handleCommonSchemaUI(DataSchema dataSchema, String value) {
        mMoreInfoLayout.setVisibility(VISIBLE);

        ContactInfoItemView item = new ContactInfoItemView(mActivity);

        item.setInfoData(dataSchema, value);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mMoreInfoLayout.addView(item, params);
        handleActionByType(item, dataSchema, value);

    }


    private void handleActionByType(ContactInfoItemView item, DataSchema dataSchema, String value) {
        if (Employee.InfoType.MOBILE_PHONE.equalsIgnoreCase(dataSchema.type)) {
            item.registerMobileListenerAndRefreshUI(this, value);

        }
        else if (Employee.InfoType.TEL_PHONE.equalsIgnoreCase(dataSchema.type)) {
            item.registerTelPhoneListenerAndRefreshUI(this, value);

        }
        else if (Employee.InfoType.EMAIL.equalsIgnoreCase(dataSchema.type)) {
            item.registerEmailListenerRefreshUI(mActivity, value);
        }
    }

    public ProgressDialogHelper getProgressDialogHelper() {
        return mProgressDialogHelper;
    }

    @Override
    protected boolean onBackPressed() {
        finish();
        return false;
    }


}
