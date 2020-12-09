package com.foreveross.atwork.modules.group.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.organization.requstModel.QureyOrganizationViewRange;
import com.foreveross.atwork.api.sdk.organization.responseModel.OrganizationResult;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.ContactModel;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.contact.ContactViewMode;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.model.orgization.Scope;
import com.foreveross.atwork.infrastructure.model.user.SelectedContactList;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.UndoEventMessage;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ActivityManagerHelper;
import com.foreveross.atwork.infrastructure.utils.ContactHelper;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.OnlineManager;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.manager.VoipManager;
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity;
import com.foreveross.atwork.modules.chat.fragment.ChatInfoFragment;
import com.foreveross.atwork.modules.chat.fragment.ChatListFragment;
import com.foreveross.atwork.modules.chat.util.DiscussionHelper;
import com.foreveross.atwork.modules.chat.util.UndoMessageHelper;
import com.foreveross.atwork.modules.contact.activity.EmployeeTreeActivity;
import com.foreveross.atwork.modules.contact.adapter.ContactListArrayListAdapter;
import com.foreveross.atwork.modules.contact.component.ContactListItemView;
import com.foreveross.atwork.modules.contact.fragment.ContactFragment;
import com.foreveross.atwork.modules.contact.fragment.EmployeeTreeFragment;
import com.foreveross.atwork.modules.contact.util.ContactInfoViewUtil;
import com.foreveross.atwork.modules.friend.fragment.MyFriendsFragment;
import com.foreveross.atwork.modules.group.component.SelectContactHead;
import com.foreveross.atwork.modules.discussion.fragment.DiscussionListFragment;
import com.foreveross.atwork.modules.group.listener.SelectedChangedListener;
import com.foreveross.atwork.modules.group.module.SelectToHandleAction;
import com.foreveross.atwork.modules.group.module.TransferMessageControlAction;
import com.foreveross.atwork.modules.group.module.UserSelectControlAction;
import com.foreveross.atwork.modules.group.service.SelectToHandleActionService;
import com.foreveross.atwork.modules.search.adapter.SearchListAdapter;
import com.foreveross.atwork.modules.search.component.SearchListItemView;
import com.foreveross.atwork.modules.search.model.SearchAction;
import com.foreveross.atwork.modules.voip.activity.CallActivity;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.support.AtworkBaseActivity;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ChatMessageHelper;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import kotlin.collections.CollectionsKt;

import static com.foreveross.atwork.modules.contact.fragment.ContactFragment.SHOW_MAIN_TITLE_BAR;


public class UserSelectActivity extends AtworkBaseActivity {

    public static final String DATA_USER_SELECT_PARAMS = "DATA_USER_SELECT_PARAMS";

    public static final String DATA_FROM_TAG = "DATA_FROM_TAG";

    public static final String ORG_CODE_CHANGED = "org_code_changed"; //搜索时组织code 的更改

    public static final String DATA_ORG_CODE = "data_org_code";

    public static final String DATA_SEARCH_MODE = "data_is_all_org";

    private UserSelectControlAction mUserSelectControlAction;

    private ContactFragment mContactFragment;

    private EmployeeTreeFragment mEmployeeTreeFragment;

    private DiscussionListFragment mDiscussionListFragment;

    private MyFriendsFragment mFriendListFragment;

    private FrameLayout mStarContactFrameLayout;

    private FrameLayout mContactTreeFrameLayout;

    private FrameLayout mDiscussionFrameLayout;

    private FrameLayout mMyFriendsFrameLayout;

    private ListView mSearchListView;

    private SelectContactHead mSelectUserHead;

    private ImageView mBackView;

    private ContactListArrayListAdapter mSearchContactListAdapter;

    private SearchListAdapter mSearchResultAdapter;

    private List<SelectedChangedListener> mSelectedChangedListeners = new ArrayList<>();

    private TextView mTitleView;

    private Button mBtSelectOk;

    private SelectMode mSelectMode;

    private SelectAction mSelectAction;

    /**
     * 是否允许选人时一个人都不选
     * */
    private boolean mSelectCanNoOne = false;

    private boolean mNeedCacheForCordova = false;

    private List<ChatPostMessage> mSendMessageList;

    private SelectViewModel mLastSelectViewModel = SelectViewModel.ContactViewModel;

    private Organization mLastSelectViewOrg;

    private String mFromTag;

    private boolean mDirectOrgShow = false;

    private String mDirectOrgCode;

    private boolean mNeedSetNotAllowList;

    private boolean mIsSuggestiveHideMe;

    private Boolean mIsMandatoryFilterSenior;

    private boolean mIsSingleContact;

    private HashMap<Organization, List<ContactModel>> mEmployeeTreeMap = new HashMap<>();

    private boolean mSendModeNeedJumpChatDetail = true;  //选择人员发送消息时, 是否跳进聊天界面

    private SelectToHandleAction mSelectToHandleAction;

    /**
     * 最大的选择user数量
     * */
    private int mCountMax;

    private ArrayList<? extends ShowListItem> mCallbackContactsSelected;

    public static Intent getIntent(Context context, UserSelectControlAction userSelectControlAction) {
        Intent intent = new Intent();
        intent.putExtra(DATA_USER_SELECT_PARAMS, userSelectControlAction);
        intent.setClass(context, UserSelectActivity.class);

        return intent;
    }

    private BroadcastReceiver mSessionInvalidBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ChatInfoFragment.TAG.equalsIgnoreCase(mFromTag)) {
                int type = intent.getIntExtra(DiscussionHelper.SESSION_INVALID_TYPE, -1);

                showKickDialog(ChatListFragment.getSessionInvalidContent(type), false);
            }

        }
    };

    private BroadcastReceiver mOrgCodeChangedBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<String> orgCodeList = intent.getStringArrayListExtra(DATA_ORG_CODE);
            List<String> searchMode = intent.getStringArrayListExtra(DATA_SEARCH_MODE);
            mSelectUserHead.setSearchModeAndOrgCodes(orgCodeList, searchMode);
            mSelectUserHead.setSelectMode(mSelectMode, mIsMandatoryFilterSenior, mSelectAction);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);
        findView();
        initData();
        registerListener();
        registerBroadcast();
        if(isDirectOrgShow()) {
            if (AtworkConfig.ORG_CONFIG.isShowSelect()) {

                String orgCode = mDirectOrgCode;
                if(StringUtils.isEmpty(orgCode)) {
                    orgCode = PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext);
                }

                OrganizationManager.getInstance().queryLocalOrg(this, orgCode, org -> {

                    List<String> modeList = new ArrayList<>();
                    modeList.add(SelectContactHead.SearchMode.EMPLOYEE);

                    UserSelectActivity.changeSelectHeaderSearchOrgAndMode(this, org, (ArrayList<String>) modeList);

                    showEmployeeTreeFragment(org);

                    updateButtonNumFromCordova();
                });

            } else {
                mLastSelectViewModel = null;
                mStarContactFrameLayout.setVisibility(View.GONE);

                updateButtonNumFromCordova();
            }

        } else {

            initContactFragment();
            showContactFragment();

        }

        updateButtonNum();

        mSearchContactListAdapter = new ContactListArrayListAdapter(this, true);
        mSearchContactListAdapter.setSingleContact(mIsSingleContact);
        mSearchContactListAdapter.setSelectAction(mSelectAction);
        mSearchContactListAdapter.setSelectContacts(getSelectedList());
        mSearchContactListAdapter.setNotAllowedSelectedContacts(getNotAllowedSelectedStringList());

        mSearchListView.setAdapter(mSearchContactListAdapter);
        initSelectedChangedListener();


        if(VoipHelper.isHandlingVoipCall() && !VoipManager.getInstance().getVoipMeetingController().isGroupChat() && CallActivity.TAG.equals(mFromTag)) {
            ActivityManagerHelper.addActivity(this);
        }

    }

    private void updateButtonNumFromCordova() {
        if(!ListUtil.isEmpty(mCallbackContactsSelected)) {
            mSelectUserHead.selectContactList(mCallbackContactsSelected);
            updateButtonNum();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();

        if(VoipHelper.isHandlingVoipCall() && !VoipManager.getInstance().getVoipMeetingController().isGroupChat() && CallActivity.TAG.equals(mFromTag)) {
            ActivityManagerHelper.removeActivity(this);
        }
    }

    @Override
    public void onUndoMsgReceive(UndoEventMessage undoEventMessage) {
        if(null != undoEventMessage) {
            if(ListUtil.isEmpty(mSendMessageList)) {
                return;
            }

            List<ChatPostMessage> msgUndoList = new ArrayList<>();
            for(String undoMsgId : undoEventMessage.mEnvIds) {
                for(ChatPostMessage chatPostMessage : mSendMessageList) {

                    if(undoMsgId.equals(chatPostMessage.deliveryId)) {
                        msgUndoList.add(chatPostMessage);
                    }
                }
            }

            mSendMessageList.removeAll(msgUndoList);


            if(!ListUtil.isEmpty(msgUndoList)) {
                showUndoDialog(UserSelectActivity.this, msgUndoList.get(0), ListUtil.isEmpty(mSendMessageList));

            }
        }
    }


    protected void showUndoDialog(Context context, PostTypeMessage message, boolean needFinish) {
        AtworkAlertDialog dialog = new AtworkAlertDialog(context, AtworkAlertDialog.Type.SIMPLE)
                .setContent(UndoMessageHelper.getUndoContent(context, message))
                .hideDeadBtn()
                .setClickBrightColorListener(dialog1 -> {
                    if(needFinish) {
                        Intent intent = ChatDetailActivity.getIntent(context, ChatMessageHelper.getChatUser(message).mUserId);
                        startActivity(intent);
                        finish();
                    }
                });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void registerBroadcast() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mSessionInvalidBroadcast, new IntentFilter(DiscussionHelper.SESSION_INVALID));
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mOrgCodeChangedBroadcast, new IntentFilter(ORG_CODE_CHANGED));
    }

    private void unregisterBroadcast() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mSessionInvalidBroadcast);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mOrgCodeChangedBroadcast);
    }

    private void findView() {
        mStarContactFrameLayout = findViewById(R.id.select_user_fragment);
        mContactTreeFrameLayout = findViewById(R.id.select_contact_tree_fragment);
        mDiscussionFrameLayout = findViewById(R.id.select_discussion_fragment);
        mMyFriendsFrameLayout = findViewById(R.id.select_friends_fragment);
        mSearchListView = findViewById(R.id.select_user_list_view);
        mSelectUserHead = findViewById(R.id.select_user_contact_head);
        mBtSelectOk = findViewById(R.id.select_user_ok);
        mBackView = findViewById(R.id.title_bar_select_user_back);
        mTitleView = findViewById(R.id.title_bar_select_user_title);

    }


    private void updateButtonNum() {
        int size;
        if(isSelectToActionMode() && !isTransferMessageOrCreateDiscussionActionMode()) {
            size = SelectToHandleActionService.INSTANCE.getContactList().size();

        } else {
            size = mSelectUserHead.getSelectedNum();

        }
        if (size > 0) {

            mBtSelectOk.setTextColor(getResources().getColor(R.color.common_item_black));
            mBtSelectOk.setText(getResources().getString(R.string.ok_with_num, size));

        } else {
            //恢复为原样
            setRightBtnGray();
            mBtSelectOk.setText(getResources().getString(R.string.ok));
        }
    }

    private void initData() {


        mUserSelectControlAction = getIntent().getParcelableExtra(DATA_USER_SELECT_PARAMS);
        if (mUserSelectControlAction == null)  {
            //TODO://注意，这边生成默认的action是为了满足文档中心无参数跳转的临时处理，如果需要其他入口处理，只能通过将此action下放到infrastructure里面去,改动较大，延后处理
            mUserSelectControlAction = new UserSelectControlAction();
            ArrayList callbackList = new ArrayList();
            callbackList.addAll(SelectedContactList.getContactList());
            mUserSelectControlAction.setCallbackContactsSelected(callbackList);
            mUserSelectControlAction.setSelectedContacts(SelectedContactList.getContactList());
            mUserSelectControlAction.setNeedSetNotAllowList(false);
            mUserSelectControlAction.setSelectAction(SelectAction.SCOPE);
            mUserSelectControlAction.setFromTag("CORDOVA_CREATE_DISCUSSION");
        }

        resetParamsFromControlAction();

        if(1 == mCountMax) {
            mIsSingleContact = true;

        }


        if (mSelectMode.equals(SelectMode.SELECT)) {
            mTitleView.setText(getResources().getString(R.string.select_contact_title));
            List<ShowListItem> contactList = SelectedContactList.getContactList();
            if (contactList != null) {
                if (mNeedSetNotAllowList) {
                    mSelectUserHead.setNotAllowedSelectedContacts(contactList);

                } else {
                    mSelectUserHead.selectContactList(contactList);
                }
            }

            List<Scope> scopeList = SelectedContactList.getScopeList();
            if(null != scopeList) {
                List scopeContactList = CollectionsKt.map(scopeList, scope -> {
                    if(null != scope.getOrganization()) {
                        return scope.getOrganization();
                    }

                    if(null != scope.getEmployee()) {
                        return scope.getEmployee();
                    }

                    return null;
                });

                scopeContactList = CollectionsKt.filterNotNull(scopeContactList);
                mSelectUserHead.selectContactList(scopeContactList);

            }


        }


        if (mIsSingleContact) {
            mBtSelectOk.setVisibility(View.GONE);
        }

        setRightBtnGray();

        if(isSelectToActionMode()) {
            setFinishChainTag(SelectToHandleActionService.TAG_HANDLE_SELECT_TO_ACTION);
        }


    }

    private void setRightBtnGray() {
        if (!mSelectCanNoOne) {
            mBtSelectOk.setTextColor(getResources().getColor(R.color.title_bar_rightest_text_gray));
        }
    }

    private void resetParamsFromControlAction() {
        if(null != mUserSelectControlAction) {
            mSelectMode = mUserSelectControlAction.getSelectMode();
            mSelectAction = mUserSelectControlAction.getSelectAction();

            mSelectCanNoOne = mUserSelectControlAction.isSelectCanNoOne();
            mIsSuggestiveHideMe = mUserSelectControlAction.isSuggestiveHideMe();
            mNeedSetNotAllowList = mUserSelectControlAction.isNeedSetNotAllowList();
            mSendModeNeedJumpChatDetail = mUserSelectControlAction.isSendModeNeedJumpChatDetail();

            mIsMandatoryFilterSenior = mUserSelectControlAction.isMandatoryFilterSenior();
            mCallbackContactsSelected = mUserSelectControlAction.getCallbackContactsSelected();
            mNeedCacheForCordova = mUserSelectControlAction.getNeedCacheForCordova();

            //refresh select
            if (!ListUtil.isEmpty(mCallbackContactsSelected)) {
                for(ShowListItem contact : mCallbackContactsSelected) {
                    contact.select(true);
                }
            }

            mCountMax = mUserSelectControlAction.getMax();

            mFromTag = mUserSelectControlAction.getFromTag();

            mDirectOrgShow = mUserSelectControlAction.getDirectOrgShow();
            mDirectOrgCode = mUserSelectControlAction.getDirectOrgCode();

            mSelectToHandleAction = mUserSelectControlAction.getSelectToHandleAction();

        }
    }

    /**
     * 初始化选择变更监听
     */
    private void initSelectedChangedListener() {
        mSelectedChangedListeners.add(mSelectUserHead);

        if (SelectMode.SELECT.equals(mSelectMode)) {
            mSelectedChangedListeners.add(mContactFragment);
        }

    }






    public boolean isOneSelectedMode() {
        return mIsSingleContact;
    }

    public boolean isOneSelected() {
        return isOneSelectedMode() && 1 == mSelectUserHead.getSelectedContact().size();
    }

    public List<String> getSelectedContactIdList() {
        List<String> idList = new ArrayList<>();
        for (ShowListItem contact : getSelectedList()) {
            idList.add(contact.getId());
        }
        return idList;
    }

    private void registerListener() {

        mBtSelectOk.setOnClickListener(v -> {

            if (!mSelectCanNoOne && mSelectUserHead.getSelectedContact().size() == 0) {
                AtworkToast.showToast(getResources().getString(R.string.select_user_zero));
                return;

            } else {
                doClickOkAction();
            }
        });


        //搜索出结果 进行选择
        mSelectUserHead.setSelectUserSearchListener(new SelectContactHead.SelectContactSearchListener() {
            @Override
            public void searchStart(String key) {
                if(isContactSimpleViewMode() && !shouldFilterDiscussionInSimpleModeView()) {
                    if(null == mSearchResultAdapter) {
                        mSearchResultAdapter = new SearchListAdapter(UserSelectActivity.this, SearchAction.SELECT);
                        mSearchResultAdapter.setNeedSelectStatus(true);

                        mSearchListView.setAdapter(mSearchResultAdapter);

                    }


                    mSearchResultAdapter.clearData();
                    mSearchResultAdapter.setKey(key);


                }
            }

            @Override
            public void searchSuccess(List<? extends ShowListItem> contactList, List<String> userIdNeedCheckOnlineStatusList, boolean isAllSearchNoResult) {
                if (mIsSuggestiveHideMe) {
                    filterContacts(contactList);
                }


                List<ShowListItem> selectedContactList = mSelectUserHead.getSelectedContact();
                for (ShowListItem selected : selectedContactList) {
                    for (ShowListItem contact : contactList) {
                        if (selected.getId().equals(contact.getId())) {
                            contact.select(true);
                        }
                    }
                }
                showSearchFragment(contactList);
            }

            @Override
            public void searchClear() {
                mSearchContactListAdapter.clear();
                showBeforeSearchView();
            }

            @Override
            public void refresh(ShowListItem contact) {
                int position = mSearchContactListAdapter.getPosition(contact);
                if (-1 != position) {
                    mSearchContactListAdapter.getItem(position).select(false);
                    mSearchContactListAdapter.notifyDataSetChanged();
                }

            }

        });


        mSearchListView.setOnTouchListener((v, event) -> {
//                AtworkUtil.hideInput(UserSelectActivity.this, mSelectUserHead.mSearchText);
            return false;
        });

        mSearchListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (SCROLL_STATE_IDLE != scrollState) {
                    AtworkUtil.hideInput(UserSelectActivity.this, mSelectUserHead.mSearchText);

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        //搜索结果中的选中
        mSearchListView.setOnItemClickListener((parent, view, position, id) -> {


            final ShowListItem contact = (ShowListItem) parent.getItemAtPosition(position);

            if(UserSelectActivity.SelectAction.SCOPE == mSelectAction
                    && !ContactInfoViewUtil.canEmployeeTreeEmpSelect(contact, getSelectedList())) {
                return;
            }

            if (mIsSingleContact) {
                contact.select(!contact.isSelect());
                List<ShowListItem> contactList = new ArrayList<>(1);
                contactList.add(contact);
                SelectedContactList.setContactList(contactList);

                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                finish();
                return;
            }
            if (getNotAllowedSelectedList().contains(contact)) {
                return;
            }
            if (!contact.isSelect() && !isAllowed()) {
                AtworkToast.showToast(mUserSelectControlAction.getMaxTip());

                return;
            }

            if (!contact.isSelect() && isOneSelected()) {
                return;
            }

            contact.select(!contact.isSelect());


            if(view instanceof ContactListItemView) {
                ContactListItemView contactListItemView = (ContactListItemView) view;
                contactListItemView.refreshContactView(contact);
            } else if(view instanceof SearchListItemView) {
                SearchListItemView searchListItemView = (SearchListItemView) view;
                searchListItemView.refreshView(contact);
            }

            action(contact);

            mSelectUserHead.mSearchText.setText(StringUtils.EMPTY);
        });

        //返回事件
        mBackView.setOnClickListener(v -> {
            WeakReference<Activity> weakReference = new WeakReference<>(UserSelectActivity.this);
            AtworkUtil.hideInput(weakReference);

            onBackPressed();
        });

    }

    protected void doClickOkAction() {
        if (SelectMode.SELECT.equals(mSelectMode)) {

            if (isSelectToActionMode()) {
                if (isTransferMessageOrCreateDiscussionActionMode()) {
                    SelectToHandleActionService.INSTANCE.action(this, mSelectToHandleAction, mSelectUserHead.getSelectedContact());

                } else {
                    SelectToHandleActionService.INSTANCE.action(this, mSelectToHandleAction, SelectToHandleActionService.INSTANCE.getContactList());

                }

            } else {

                handleScopeDataSelected();

                SelectedContactList.setContactList(mSelectUserHead.getSelectedContact());

                Intent returnIntent = new Intent();
                AtworkUtil.hideInput(UserSelectActivity.this);
                setResult(RESULT_OK, returnIntent);
                finish();
                UserSelectActivity.this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

            }
        }
    }


    private void handleScopeDataSelected() {
            if(null != mEmployeeTreeFragment) {


                List<Scope> scopesSelected = new ArrayList<>();
                List<ShowListItem> scopeContactList = new ArrayList<>(getSelectedList());


                //1. 检查是否新增org scope数据
                for (ShowListItem scopeContact : scopeContactList) {
                    if (scopeContact instanceof OrganizationResult) {
                        OrganizationResult orgNext = (OrganizationResult) scopeContact;
                        scopesSelected.add(orgNext.transfer(AtworkApplicationLike.baseContext));
                    }
                }


                //2. 检查emp 雇员数据
                List<Scope> empScopesSelected = new ArrayList<>();
                for (ShowListItem scopeContact : scopeContactList) {
                    if (scopeContact instanceof Employee) {
                        Employee empNext = (Employee) scopeContact;
                        Scope scope = empNext.transfer(scopesSelected);
                        if(null != scope) {
                            empScopesSelected.add(scope);
                        }
                    }
                }

                scopesSelected.addAll(empScopesSelected);
                SelectedContactList.setScopeList(scopesSelected);


        }
    }

    private boolean isOrgScopeSelected(OrganizationResult orgNext, Set<OrganizationResult> orgSet) {
        if(!orgNext.isSelected(AtworkApplicationLike.baseContext, false)) {
            return false;
        }

        for (OrganizationResult org : orgSet) {
            if (!orgNext.path.equals(org.path)
                    && orgNext.path.contains(org.path)
                    && org.isSelected(AtworkApplicationLike.baseContext, false)) {
                return false;
            }
        }

        return true;
    }


    private void showBeforeSearchView() {

        mSearchListView.setVisibility(View.GONE);

        if (SelectViewModel.ContactTreeViewModel.equals(mLastSelectViewModel)) {
            showEmployeeTreeFragment(mLastSelectViewOrg);

        } else if (SelectViewModel.ContactViewModel.equals(mLastSelectViewModel)) {
            showContactFragment();

        } else if (SelectViewModel.DiscussionViewModel.equals(mLastSelectViewModel)) {
            showDiscussionListFragment();

        } else if (SelectViewModel.MyFriendsViewModel.equals(mLastSelectViewModel)) {
            showFriendsFragment();
        }
    }

    @Override
    public void onBackPressed() {

        if (isFragmentVisible() && !isDirectOrgShow()) {
            showContactFragment();
        } else {
            finish(true);
        }
    }

    public boolean isFragmentVisible() {
        return (null != mEmployeeTreeFragment && mEmployeeTreeFragment.isVisible())
                || (null != mDiscussionListFragment && mDiscussionListFragment.isVisible())
                || (null != mFriendListFragment && mFriendListFragment.isVisible());
    }

    public void action(List<? extends ShowListItem> contactList, boolean selected) {
        for(ShowListItem contactItem : contactList) {
            contactItem.select(selected);
        }


        if (isSelectToActionMode() && !isTransferMessageOrCreateDiscussionActionMode()) {
            SelectToHandleActionService.INSTANCE.handleSelect(contactList, selected);
        }

        for (SelectedChangedListener selectedChangedListener : mSelectedChangedListeners) {
            if(null == selectedChangedListener) {
                continue;
            }

            if (selected) {
                selectedChangedListener.selectContactList(contactList);
            } else {
                selectedChangedListener.unSelectedContactList(contactList);
            }
        }
        updateButtonNum();
    }

    public void action(ShowListItem contact) {

        if (isSelectToActionMode() && !isTransferMessageOrCreateDiscussionActionMode()) {
            SelectToHandleActionService.INSTANCE.handleSelect(contact);
        }

        for (SelectedChangedListener selectedChangedListener : mSelectedChangedListeners) {
            if(null == selectedChangedListener) {
                continue;
            }

            if (contact.isSelect()) {
                selectedChangedListener.selectContact(contact);
            } else {
                selectedChangedListener.unSelectedContact(contact);
            }
        }
        updateButtonNum();
    }

    private void initContactFragment() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.select_user_fragment, createContactFragment()).commit();

    }


    /**
     * 显示搜索结果
     */
    public void showSearchFragment(List<? extends ShowListItem> contactList) {
        switchToSearchModel();
        for (ShowListItem contact : contactList) {
            if (mSelectUserHead.mNotAllowedSelectedContacts.contains(contact)) {
                contact.select(true);
            }
        }

        if(isContactSimpleViewMode() && !shouldFilterDiscussionInSimpleModeView()) {

            if (!ListUtil.isEmpty(contactList)) {
                ShowListItem listItem = contactList.get(0);
                if(listItem instanceof Discussion) {
                    mSearchResultAdapter.setDiscussionSearchItem((List<Discussion>) contactList);

                } else if(listItem instanceof User) {
                    if(((User) listItem).mFileTransfer) {
                        mSearchResultAdapter.setDeviceSearchItem((List<ShowListItem>) contactList);
                    } else {
                        mSearchResultAdapter.setUserSearchItem((List<ShowListItem>) contactList, SearchAction.SELECT);
                    }
                } else {
                    mSearchResultAdapter.setUserSearchItem((List<ShowListItem>) contactList, SearchAction.SELECT);

                }


            }



            return;
        }


        mSearchContactListAdapter.clear();
        mSearchContactListAdapter.addAll(contactList);
        if (contactList.isEmpty() || !DomainSettingsManager.getInstance().handleUserOnlineFeature()) {
            return;
        }
        List<String> checkList = new ArrayList<>();
        for (ShowListItem item : contactList) {
            checkList.add(item.getId());
        }
        OnlineManager.getInstance().checkOnlineList(this, checkList, onlineList -> {
            if (onlineList == null) {
                return;
            }
            mSearchContactListAdapter.notifyDataSetChanged();
        });
    }


    private void switchToSearchModel() {
        mSearchListView.setVisibility(View.VISIBLE);
        mStarContactFrameLayout.setVisibility(View.GONE);
        mContactTreeFrameLayout.setVisibility(View.GONE);
        mDiscussionFrameLayout.setVisibility(View.GONE);
        mMyFriendsFrameLayout.setVisibility(View.GONE);
    }

    public List<ShowListItem> getSelectedList() {
        return mSelectUserHead.getSelectedContact();
    }

    public List<ShowListItem> getNotAllowedSelectedList() {
        return mSelectUserHead.mNotAllowedSelectedContacts;
    }

    @NonNull
    public List<String> getNotAllowedSelectedStringList() {
        List<String> userIdList = new ArrayList<>();
        for (ShowListItem contact : mSelectUserHead.mNotAllowedSelectedContacts) {
            if (contact == null) {
                continue;
            }
            userIdList.add(contact.getId());
        }
        return userIdList;
    }

    /**
     * 显示星标联系人选择界面
     */
    public void showContactFragment() {
        mStarContactFrameLayout.setVisibility(View.VISIBLE);
        mSearchListView.setVisibility(View.GONE);
        mContactTreeFrameLayout.setVisibility(View.GONE);
        mDiscussionFrameLayout.setVisibility(View.GONE);
        mMyFriendsFrameLayout.setVisibility(View.GONE);

        mSelectUserHead.setVisibility(View.VISIBLE);
//        if (mSelectMode.equals(SelectMode.SEND_MESSAGES)) {
////            mSelectUserHead.setVisibility(View.GONE);
////            mBtSelectOk.setVisibility(View.GONE);
//        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (mDiscussionListFragment != null) {
            fragmentTransaction.hide(mDiscussionListFragment);
        }
        if (mEmployeeTreeFragment != null) {
            fragmentTransaction.hide(mEmployeeTreeFragment);
        }

        if (mFriendListFragment != null) {
            fragmentTransaction.hide(mFriendListFragment);
        }
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.show(mContactFragment).commit();

        mLastSelectViewModel = SelectViewModel.ContactViewModel;

        //通知搜索的组织范围更改, 用回 mContactFragment 的全部 org 列表
        if (!ListUtil.isEmpty(mContactFragment.getUserOgList())) {
            List<String> modeList = new ArrayList<>();

            if(isTransferMessageOrCreateDiscussionActionMode()) {
                modeList.add(SelectContactHead.SearchMode.DEVICE);
            }

            modeList.add(SelectContactHead.SearchMode.FRIEND);
            modeList.add(SelectContactHead.SearchMode.EMPLOYEE);

            if(isContactSimpleViewMode() && !shouldFilterDiscussionInSimpleModeView()) {
                modeList.add(SelectContactHead.SearchMode.DISCUSSION);

            }

            UserSelectActivity.changeSelectHeaderSearchOrgsAndMode(this, mContactFragment.getUserOgList(), (ArrayList<String>) modeList);
        }
    }


    private boolean isContactSimpleViewMode() {
        return ContactViewMode.SIMPLE_VERSION == ContactHelper.getContactViewMode(BaseApplicationLike.baseContext);
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



    public void showDiscussionListFragment() {
//        mSelectUserHead.setVisibility(View.GONE);
        mDiscussionFrameLayout.setVisibility(View.VISIBLE);
        mSearchListView.setVisibility(View.GONE);
        mStarContactFrameLayout.setVisibility(View.GONE);
        mContactTreeFrameLayout.setVisibility(View.GONE);
        mMyFriendsFrameLayout.setVisibility(View.GONE);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (mDiscussionListFragment == null) {
            fragmentTransaction.add(R.id.select_discussion_fragment, createDiscussionListFragment());
            mSelectedChangedListeners.add(mDiscussionListFragment);

        }
        if (mEmployeeTreeFragment != null) {
            fragmentTransaction.hide(mEmployeeTreeFragment);
        }
        if (mContactFragment != null) {
            fragmentTransaction.hide(mContactFragment);
        }

        if (mFriendListFragment != null) {
            fragmentTransaction.hide(mFriendListFragment);
        }

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.show(mDiscussionListFragment).commit();

        mLastSelectViewModel = SelectViewModel.DiscussionViewModel;
    }

    public void showFriendsFragment() {
        mMyFriendsFrameLayout.setVisibility(View.VISIBLE);
        mDiscussionFrameLayout.setVisibility(View.GONE);
        mSearchListView.setVisibility(View.GONE);
        mStarContactFrameLayout.setVisibility(View.GONE);
        mContactTreeFrameLayout.setVisibility(View.GONE);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (mFriendListFragment == null) {
            fragmentTransaction.add(R.id.select_friends_fragment, createFriendListFragment());
            mSelectedChangedListeners.add(mFriendListFragment);
        }

        if (mEmployeeTreeFragment != null) {
            fragmentTransaction.hide(mEmployeeTreeFragment);
        }

        if (mContactFragment != null) {
            fragmentTransaction.hide(mContactFragment);
        }

        if (mDiscussionListFragment != null) {
            fragmentTransaction.hide(mDiscussionListFragment);
        }

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.show(mFriendListFragment).commit();

        mLastSelectViewModel = SelectViewModel.MyFriendsViewModel;
    }

    /**
     * 选择组织架构树界面
     */
    public void showEmployeeTreeFragment(Organization org) {
        mContactTreeFrameLayout.setVisibility(View.VISIBLE);
        mStarContactFrameLayout.setVisibility(View.GONE);
        mDiscussionFrameLayout.setVisibility(View.GONE);
        mMyFriendsFrameLayout.setVisibility(View.GONE);
        mSearchListView.setVisibility(View.GONE);


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        handleEmployeeTreeFragment(org, fragmentTransaction);


        if (mContactFragment != null) {
            fragmentTransaction.hide(mContactFragment);
        }

        if (mDiscussionListFragment != null) {
            fragmentTransaction.hide(mDiscussionListFragment);
        }

        if (mFriendListFragment != null) {
            fragmentTransaction.hide(mFriendListFragment);
        }
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.show(mEmployeeTreeFragment).commit();

        mLastSelectViewModel = SelectViewModel.ContactTreeViewModel;
        mLastSelectViewOrg = org;
    }

    public void handleEmployeeTreeFragment(Organization org, FragmentTransaction fragmentTransaction) {
        if (mEmployeeTreeFragment == null) {
            fragmentTransaction.add(R.id.select_contact_tree_fragment, createEmployeeTreeFragment(org));
            mSelectedChangedListeners.add(mEmployeeTreeFragment);

        } else {
            //保存当前的org 雇员树数据
            List<ContactModel> copyList = new ArrayList<>();
            copyList.addAll(mEmployeeTreeFragment.mRootContactModels);
            mEmployeeTreeMap.put(mEmployeeTreeFragment.mOrg, copyList);

            //刷新 fragment 的 org 数据
            mEmployeeTreeFragment.refreshOrg(org);

            List<ContactModel> treeContactModelList = mEmployeeTreeMap.get(org);
            if (ListUtil.isEmpty(treeContactModelList)) {
                mEmployeeTreeFragment.refreshToEmptyView();
                mEmployeeTreeFragment.loadData(new QureyOrganizationViewRange());

            } else {
                mEmployeeTreeFragment.mRootContactModels.clear();
                mEmployeeTreeFragment.mRootContactModels.addAll(treeContactModelList);
                mEmployeeTreeFragment.refresh();
            }
        }
    }

    private Fragment createFriendListFragment() {
        if (mFriendListFragment == null) {
            mFriendListFragment = new MyFriendsFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(DATA_USER_SELECT_PARAMS, mUserSelectControlAction);

            mFriendListFragment.setArguments(bundle);
        }
        return mFriendListFragment;
    }

    private Fragment createDiscussionListFragment() {
        if (mDiscussionListFragment == null) {
            mDiscussionListFragment = new DiscussionListFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(DATA_USER_SELECT_PARAMS, mUserSelectControlAction);

            mDiscussionListFragment.setArguments(bundle);
        }
        return mDiscussionListFragment;
    }

    private Fragment createContactFragment() {
        if (mContactFragment == null) {
            mContactFragment = new ContactFragment();
            Bundle bundle = new Bundle();

            bundle.putParcelable(DATA_USER_SELECT_PARAMS, mUserSelectControlAction);
            bundle.putBoolean(SHOW_MAIN_TITLE_BAR, false);


            mContactFragment.setArguments(bundle);
        }
        return mContactFragment;
    }

    public Fragment createEmployeeTreeFragment(Organization org) {
        if (mEmployeeTreeFragment == null) {
            mEmployeeTreeFragment = new EmployeeTreeFragment();
            Bundle bundle = new Bundle();

            bundle.putParcelable(DATA_USER_SELECT_PARAMS, mUserSelectControlAction);
            bundle.putParcelable(EmployeeTreeActivity.DATA_ORG, org);

            mEmployeeTreeFragment.setArguments(bundle);
        }
        return mEmployeeTreeFragment;
    }

    public boolean isAllowed(List<? extends ShowListItem> contactList) {
        boolean isAllowed = true;
        int addNum = 0;
        for (ShowListItem contact : contactList) {
            if (!mSelectUserHead.contains(contact)) {
                addNum++;
                if (mSelectUserHead.getSelectedAndNotAllowedSelectedNum() + addNum > mCountMax) {
                    isAllowed = false;
                    break;
                }
            }
        }

        return isAllowed;
    }

    public boolean isAllowed() {

        return mSelectUserHead.getSelectedAndNotAllowedSelectedNum() + 1 <= mCountMax;
    }



    /**
     * 在群聊选人时, 过滤掉自己, 不显示出来
     *
     * @param contactList 被过滤的 list
     */
    private void filterContacts(List<? extends ShowListItem> contactList) {
        if (mSelectMode.equals(SelectMode.SELECT)) {
            filterSelf(contactList);
        }
    }


    public List<? extends ShowListItem> filterSelf(List<? extends ShowListItem> contactList) {
        return filterOneContact(contactList, AtworkApplicationLike.getLoginUserSync());
    }

    private List<? extends ShowListItem> filterOneContact(List<? extends ShowListItem> contactList, User user) {
        if (contactList.contains(user)) {
            contactList.remove(user);
        }

        return contactList;
    }


    private boolean isTransferMessageOrCreateDiscussionActionMode() {
        if(!isSelectToActionMode()) {
            return false;
        }

        if(mSelectToHandleAction instanceof TransferMessageControlAction) {
            if(((TransferMessageControlAction) mSelectToHandleAction).isNeedCreateDiscussion()) {
                return true;
            }
        }

        return false;
    }

    private boolean isSelectToActionMode() {
        return SelectMode.SELECT == mSelectMode && null != mSelectToHandleAction;
    }


    private boolean isDirectOrgShow() {
        return mDirectOrgShow;
    }

    /**
     * 是否来自多选雇员的轻应用接口, 这种模式下, 显示当前组织下雇员界面, 以及相关的 UI 细节控制
     * **/

    public static void changeSelectHeaderSearchOrgAndMode(Context context, Organization org, ArrayList<String> searchModeList) {
        List<Organization> singleList = new ArrayList<>();
        singleList.add(org);

        changeSelectHeaderSearchOrgsAndMode(context, singleList, searchModeList);
    }

    /**
     * 改变当前搜索框的组织 code 搜索范围
     */
    public static void changeSelectHeaderSearchOrgsAndMode(Context context, List<Organization> organizations, ArrayList<String> searchModeList) {
        if (null != context) {
            Intent intent = new Intent(UserSelectActivity.ORG_CODE_CHANGED);
            intent.putStringArrayListExtra(UserSelectActivity.DATA_ORG_CODE, Organization.getOrgCodeList(organizations));
            intent.putStringArrayListExtra(UserSelectActivity.DATA_SEARCH_MODE, searchModeList);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        }
    }

    public static void changeSelectHeaderSearchMode(Context context, ArrayList<String> searchModeList) {
        if (null != context) {
            Intent intent = new Intent(UserSelectActivity.ORG_CODE_CHANGED);
            intent.putStringArrayListExtra(UserSelectActivity.DATA_SEARCH_MODE, searchModeList);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        }
    }

    public SelectContactHead getSelectUserHead() {
        return mSelectUserHead;
    }

    public enum SelectMode implements Serializable {

        NO_SELECT,

        //选择人员模式
        SELECT,

    }


    /**
     * 选人的行为
     * */
    public enum SelectAction implements Serializable {

        DISCUSSION,

        VOIP,


        SCOPE
    }


    private enum SelectViewModel {

        ContactViewModel,

        ContactTreeViewModel,

        DiscussionViewModel,

        MyFriendsViewModel,

        SearchViewModel,
    }

}
