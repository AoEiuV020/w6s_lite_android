package com.foreveross.atwork.modules.group.component;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.organization.responseModel.EmployeeResult;
import com.foreveross.atwork.api.sdk.organization.responseModel.OrganizationResult;
import com.foreveross.atwork.db.daoService.DiscussionDaoService;
import com.foreveross.atwork.db.daoService.UserDaoService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.Relationship;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ContactHelper;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.listener.TextWatcherAdapter;
import com.foreveross.atwork.manager.EmployeeManager;
import com.foreveross.atwork.manager.model.ExpandEmpTreeAction;
import com.foreveross.atwork.modules.contact.activity.EmployeeTreeActivity;
import com.foreveross.atwork.modules.contact.util.ContactInfoViewUtil;
import com.foreveross.atwork.modules.file.service.FileTransferService;
import com.foreveross.atwork.modules.group.activity.UserSelectActivity;
import com.foreveross.atwork.modules.group.adaptar.HorizontalListViewAdapter;
import com.foreveross.atwork.modules.group.inter.SyncActionListener;
import com.foreveross.atwork.modules.group.listener.SelectedChangedListener;
import com.foreveross.atwork.modules.search.model.SearchContent;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ErrorHandleUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import kotlin.collections.CollectionsKt;

public class SelectContactHead extends LinearLayout implements SelectedChangedListener, SelectContactHeadItemView.RemoveContactListener {

    private static int MAX_SIZE;

    private HorizontalListView mHeadListView;
    private HorizontalListViewAdapter mHorizontalListViewAdapter;
    public EditText mSearchText;
    private TextView mTvSearch;
    private String mSearchId;
    private SelectContactSearchListener mSelectContactSearchListener;

    public List<ShowListItem> mNotAllowedSelectedContacts = new ArrayList<>();
    private List<ShowListItem> mSelectedContactList = new ArrayList<>();

    private SyncActionListener mSyncActionListener;
    private int mItemHeadWidth;

    private UserSelectActivity.SelectMode mSelectMode;
    private Boolean mIsMandatoryFilterSenior;
    private UserSelectActivity.SelectAction mSelectAction;


    private List<String> mOrgCodeList;

    private List<String> mSearchModeList = new ArrayList<>();
    private Map<SearchContent, Boolean> mResultMap = new HashMap<>();


    public static final class SearchMode {
        public static final String EMPLOYEE = "EMPLOYEE";
        public static final String FRIEND = "FRIEND";
        public static final String DISCUSSION = "DISCUSSION";
        public static final String DEVICE = "DEVICE";
    }

    public SelectContactHead(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        registerListener();
        initData();
    }

    private void refreshResultMap() {
        for (Map.Entry<SearchContent, Boolean> entry : mResultMap.entrySet()) {
            entry.setValue(null);
        }
    }

    private boolean isAllSearchNoResult() {
        boolean isSearchNoResult = true;
        for (Boolean oneSearchResult : mResultMap.values()) {
            if (null == oneSearchResult || true == oneSearchResult) {
                isSearchNoResult = false;
                break;
            }
        }
        return isSearchNoResult;
    }

    public int getSelectedNum() {
        return this.mSelectedContactList.size();
    }

    public int getSelectedAndNotAllowedSelectedNum() {
        return this.mSelectedContactList.size() + mNotAllowedSelectedContacts.size();
    }

    public List<ShowListItem> getSelectedContact() {
        return this.mSelectedContactList;
    }

    public List<ShowListItem> getSelectedContactHavingPhone() {
        List<ShowListItem> contactListWithPhone = new ArrayList<>();
        for (ShowListItem contact : mSelectedContactList) {

            String phone = "";

            if (contact instanceof User) {
                phone = ((User) contact).mPhone;

            } else if (contact instanceof Employee) {
                phone = ((Employee) contact).mobile;
            }

            if (!StringUtils.isEmpty(phone)) {
                contactListWithPhone.add(contact);
            }
        }
        return contactListWithPhone;
    }

    private void initData() {
        mHorizontalListViewAdapter = new HorizontalListViewAdapter(getContext(), this);
        mHeadListView.setAdapter(mHorizontalListViewAdapter);
        mHeadListView.getLayoutParams().width = 0;

        //分别减去输入框跟两边空白的间距(整体左右边距: 36 输入框总体长度 : 56 每个头像宽度: 51  均为dp)
        mItemHeadWidth = DensityUtil.dip2px(51);


        refreshMaxSize();


    }

    private void refreshMaxSize() {
        mTvSearch.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                //动态计算头像最大数量
                int width = mTvSearch.getWidth();
                if (0 != width) {
                    width = DensityUtil.px2dip(width) + 18; //margin right
                }
                doRefreshMaxSize(width);

                mTvSearch.getViewTreeObserver().removeOnPreDrawListener(this);

                return false;
            }
        });
    }

    private void doRefreshMaxSize(int searchBtnWidth) {
        MAX_SIZE = (DensityUtil.px2dip(ScreenUtils.getScreenWidth(getContext())) - 36 - 56 - searchBtnWidth) / 51;

        refreshHeadListViewWidth();
    }

    public void setSelectMode(UserSelectActivity.SelectMode selectMode, Boolean isMandatoryFilterSenior, UserSelectActivity.SelectAction selectAction) {
        this.mSelectMode = selectMode;
        this.mIsMandatoryFilterSenior = isMandatoryFilterSenior;
        this.mSelectAction = selectAction;

    }

    public void setSearchModeAndOrgCodes(List<String> orgList, List<String> searchModeList) {
        this.mOrgCodeList = orgList;
        this.mSearchModeList.clear();
        this.mSearchModeList.addAll(searchModeList);
    }


    public void clear() {
        mSelectedContactList.clear();
        mNotAllowedSelectedContacts.clear();
        refreshHead(false);
    }


    /**
     * 选中某个人
     *
     * @param contact
     */
    @Override
    public void selectContact(ShowListItem contact) {

        if (contains(contact)) {
            return;
        }


        ContactHelper.addContact(mSelectedContactList, contact);
        filterSelectContactList();

        refreshHead(true);
    }

    @Override
    public void unSelectedContact(ShowListItem contact) {
        ContactHelper.removeContact(mSelectedContactList, contact);

        refreshHead(false);
    }

    @Override
    public void selectContactList(List<? extends ShowListItem> contactList) {
        for (ShowListItem contact : contactList) {
            if (!contains(contact)) {
                ContactHelper.addContact(mSelectedContactList, contact);
            }
        }
        filterSelectContactList();

        refreshHead(true);
    }

    @Override
    public void unSelectedContactList(List<? extends ShowListItem> contactList) {

        ContactHelper.removeContacts(mSelectedContactList, contactList);

        refreshHead(false);
    }

    private void filterSelectContactList() {

        if(UserSelectActivity.SelectAction.SCOPE == mSelectAction) {
            List<ShowListItem> contactRemovedList = new ArrayList<>();
            List<ShowListItem> orgContactList = CollectionsKt.filter(mSelectedContactList, showListItem -> showListItem instanceof Organization || showListItem instanceof OrganizationResult);
            List<ShowListItem> empContactList = CollectionsKt.filter(mSelectedContactList, showListItem -> showListItem instanceof Employee || showListItem instanceof EmployeeResult);

            for(ShowListItem empContact: empContactList) {
                if(!ContactInfoViewUtil.canEmployeeTreeEmpSelect(empContact, orgContactList)) {
                    contactRemovedList.add(empContact);
                }
            }

            mSelectedContactList.removeAll(contactRemovedList);
        }
    }

    private void refreshHead(boolean isSelected) {
        mHorizontalListViewAdapter.clear();
        mHorizontalListViewAdapter.addAll(mSelectedContactList);

        int size = refreshHeadListViewWidth();

        //当删除被联系人在最后能见的列表中, 则无论如何都直接移动到最后
        if (isSelected) {
            mHeadListView.setLastSection();
        }
        //控制搜索图标的显示隐藏
        if (0 == size) {
            mSearchText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.icon_search_1), null, null, null);
        } else {
            mSearchText.setCompoundDrawables(null, null, null, null);
        }
    }

    private int refreshHeadListViewWidth() {
        int size = mSelectedContactList.size();
        if (size > MAX_SIZE) {
            size = MAX_SIZE;
        }

        mHeadListView.getLayoutParams().width = mItemHeadWidth * size;
        mHorizontalListViewAdapter.notifyDataSetChanged();
        return size;
    }

    private void registerListener() {
        mSearchText.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                mSearchId = UUID.randomUUID().toString();
                String searchValue = s.toString();

                if (StringUtils.isEmpty(searchValue)) {
                    hideTvSearch();
                } else {
                    showTvSearch();

                }


                if (!shouldSearch(searchValue)) {
                    return;
                }


                if (mSelectContactSearchListener != null) {

                    search(searchValue);
                }
            }
        });

        mSearchText.setOnEditorActionListener((v, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                AtworkUtil.hideInput((Activity) getContext(), mSearchText);
                search(mSearchText.getText().toString());
                return true;
            }
            return false;
        });


        mTvSearch.setOnClickListener(v -> {
            AtworkUtil.hideInput((Activity) getContext(), mSearchText);
            search(mSearchText.getText().toString());
        });

        mHeadListView.setOnItemClickListener((parent, view, position, id) -> removeContact(mSelectedContactList.get(position)));
    }


    private boolean shouldSearch(String text) {
        if (StringUtils.isEmpty(text)) {
            return true;
        }


        if (AtworkConfig.SEARCH_CONFIG.isAutoSearchInMainBusiness()) {
            return true;
        }

        return false;
    }

    private void hideTvSearch() {

        doRefreshMaxSize(0);
        mHeadListView.setLastSection();
        mTvSearch.setVisibility(GONE);
    }

    private void showTvSearch() {
        if (!AtworkConfig.SEARCH_CONFIG.isShowSearchBtnInMainBusiness()) {
            return;
        }

        refreshMaxSize();
        mHeadListView.setLastSection();
        mTvSearch.setVisibility(VISIBLE);
    }

    private void search(String searchValue) {
        refreshResultMap();

        if (StringUtils.isEmpty(searchValue)) {
            mSelectContactSearchListener.searchClear();
        } else {
            new Handler().postDelayed(new SearchRunnable(mSearchId, searchValue), 800);
        }
    }

    /**
     * 初始化VIEW
     */
    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.component_select_user_head, this);
        mSearchText = view.findViewById(R.id.select_user_search_text);
        mTvSearch = view.findViewById(R.id.tv_search_btn);
        mHeadListView = view.findViewById(R.id.select_user_head_listview);


    }

    public void clearInput() {
        mSearchText.setText(StringUtils.EMPTY);
    }

    public SelectContactSearchListener getSelectUserSearchListener() {
        return mSelectContactSearchListener;
    }

    public void setSelectUserSearchListener(SelectContactSearchListener selectContactSearchListener) {
        this.mSelectContactSearchListener = selectContactSearchListener;
    }

    @Override
    public void removeContact(ShowListItem contact) {
        contact.select(false);

        if (getContext() instanceof UserSelectActivity) {
            UserSelectActivity userSelectActivity = (UserSelectActivity) getContext();
            userSelectActivity.action(contact);
        }

        if (getContext() instanceof EmployeeTreeActivity && mSyncActionListener != null) {
            mSyncActionListener.syncToMobileAction(contact);
        }


        if (null != mSelectContactSearchListener) {
            mSelectContactSearchListener.refresh(contact);
        }
    }

    public void setNotAllowedSelectedContacts(List<ShowListItem> notAllowedSelectedContacts) {
        this.mNotAllowedSelectedContacts = notAllowedSelectedContacts;
    }

    public boolean contains(ShowListItem contact) {
        return ContactHelper.containsContact(mSelectedContactList, contact)
                || ContactHelper.containsContact(mNotAllowedSelectedContacts, contact);
    }

    public void setSyncActionListener(SyncActionListener mSyncActionListener) {
        this.mSyncActionListener = mSyncActionListener;
    }

    public interface SelectContactSearchListener {
        void searchStart(String key);

        void searchSuccess(List<? extends ShowListItem> contactList, List<String> userIdNeedCheckOnlineStatusList, boolean isAllSearchNoResult);

        //搜索内容清空时应当调用的方法
        void searchClear();

        //指定 user 刷新搜索列表
        void refresh(ShowListItem contact);
    }

    private class SearchRunnable implements Runnable {
        private String tmpSearchId;

        private String searchValue;

        public SearchRunnable(String searchId, String searchValue) {
            this.tmpSearchId = searchId;
            this.searchValue = searchValue;
        }

        @Override
        public void run() {

            if (tmpSearchId.equals(mSearchId)) {

                if (null != mSelectContactSearchListener) {
                    mSelectContactSearchListener.searchStart(searchValue);
                }

                searchUserTypeContacts();

                searchDiscussions();

                searchFileTransfer();

            }

        }

        private void searchDiscussions() {
            if (mSearchModeList.contains(SearchMode.DISCUSSION)) {
                //搜索群组
                DiscussionDaoService.getInstance().searchDiscussion(mSearchId, searchValue, (searchKeyCallBack, discussionList) -> {

                    if (!mSearchId.equals(searchKeyCallBack)) {
                        return;
                    }


                    if (mSelectContactSearchListener != null) {
                        mResultMap.put(SearchContent.SEARCH_DISCUSSION, !ListUtil.isEmpty(discussionList));
                        mSelectContactSearchListener.searchSuccess(discussionList, new ArrayList<>(), isAllSearchNoResult());
                    }
                });
            }
        }

        private void searchUserTypeContacts() {
            if (DomainSettingsManager.getInstance().handleFriendsRelationshipsFeature() && mSearchModeList.contains(SearchMode.FRIEND)) {
                //先搜索好友
                UserDaoService.getInstance().searchUsers(mSearchId, searchValue, Relationship.Type.FRIEND, (searchKeyCallBack, users) -> {
                    if (!mSearchId.equals(searchKeyCallBack)) {
                        return;
                    }

                    //好友与雇员结果一起显示
                    if (mSearchModeList.contains(SearchMode.EMPLOYEE)) {
                        searchEmployees(users);

                    } else {
                        //不用搜索雇员, 只搜索好友
                        if (mSelectContactSearchListener != null) {
                            mResultMap.put(SearchContent.SEARCH_USER, !ListUtil.isEmpty(users));
                            mSelectContactSearchListener.searchSuccess(users, User.toUserIdList(users), isAllSearchNoResult());
                        }
                    }

                });

            } else {
                if (mSearchModeList.contains(SearchMode.EMPLOYEE)) {
                    List<User> emptyFriendList = new ArrayList<>();
                    searchEmployees(emptyFriendList);
                }

            }
        }

        public void searchEmployees(@NonNull List<User> friendList) {
            ExpandEmpTreeAction expandEmpTreeAction = ExpandEmpTreeAction.newExpandEmpTreeAction()
                    .setSelectMode(UserSelectActivity.SelectMode.NO_SELECT != mSelectMode)
                    .setMandatoryFilterSenior(mIsMandatoryFilterSenior);

            if (UserSelectActivity.SelectMode.SELECT == mSelectMode) {
                expandEmpTreeAction.setViewAcl(AtworkConfig.SEARCH_CONFIG.isEmployeeViewAcl());
            }


            EmployeeManager.getInstance().searchEmployeesRemote(BaseApplicationLike.baseContext, mOrgCodeList, mSearchId, searchValue, expandEmpTreeAction, new EmployeeManager.RemoteSearchEmployeeListListener() {
                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    if (!ErrorHandleUtil.handleBaseError(errorCode, errorMsg)) {
                        searchLocal(friendList);
                    }
                }

                @Override
                public void onSuccess(String searchKey, List<Employee> employeeList) {
                    if (!mSearchId.equals(searchKey)) {
                        return;
                    }

                    if (mSelectContactSearchListener != null) {
                        //清洗重复的 user
                        Set<ShowListItem> contactSet = new LinkedHashSet<>();
                        contactSet.addAll(friendList);
                        contactSet.addAll(employeeList);

                        mResultMap.put(SearchContent.SEARCH_USER, !ListUtil.isEmpty(contactSet));
                        mSelectContactSearchListener.searchSuccess(new ArrayList<>(contactSet), User.toUserIdList(friendList), isAllSearchNoResult());
                    }
                }


                /**
                 * 本地搜索雇员
                 * */
                public void searchLocal(List<User> friendList) {
                    EmployeeManager.getInstance().searchEmployeesLocal(BaseApplicationLike.baseContext, mOrgCodeList, mSearchId, searchValue, expandEmpTreeAction, new EmployeeManager.LocalSearchEmployeeListListener() {
                        @Override
                        public void onSuccess(String searchKey, List<Employee> employeeList) {
                            if (!mSearchId.equals(searchKey)) {
                                return;
                            }

                            //检查在线离线状态
                            List<String> requestIdList = new ArrayList<>();
                            if (!ListUtil.isEmpty(friendList)) {
                                requestIdList.addAll(User.toUserIdList(friendList));
                            }

                            if (!ListUtil.isEmpty(employeeList)) {
                                requestIdList.addAll(Employee.toUserIdList(employeeList));
                            }

                            if (mSelectContactSearchListener != null) {

                                //清洗重复的 user
                                Set<ShowListItem> contactSet = new LinkedHashSet<>();
                                contactSet.addAll(friendList);
                                contactSet.addAll(employeeList);


                                mResultMap.put(SearchContent.SEARCH_USER, !ListUtil.isEmpty(employeeList));
                                mSelectContactSearchListener.searchSuccess(new ArrayList<>(contactSet), requestIdList, isAllSearchNoResult());
                            }

                        }

                        @Override
                        public void onFail() {
                            AtworkToast.showToast(getResources().getString(R.string.contact_search_fail));

                        }
                    });
                }
            });
        }


        private void searchFileTransfer() {
            if (!mSearchModeList.contains(SearchMode.DEVICE)) {
                return;
            }


            FileTransferService.INSTANCE.search(mSearchId, searchValue, (searchKey, fileTransfer) -> {
                if (!mSearchId.equals(searchKey)) {
                    return;
                }

                if (null != fileTransfer) {
                    if (mSelectContactSearchListener != null) {
                        List<ShowListItem> fileTransferSingleList = ListUtil.makeSingleList(fileTransfer);
                        mResultMap.put(SearchContent.SEARCH_DEVICE, !ListUtil.isEmpty(fileTransferSingleList));
                        mSelectContactSearchListener.searchSuccess(fileTransferSingleList, new ArrayList<>(), isAllSearchNoResult());
                    }
                }
            });
        }
    }
}
