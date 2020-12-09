package com.foreveross.atwork.modules.search.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.recyclerview.layoutManager.FlowLayoutManager;
import com.foreveross.atwork.db.daoService.DiscussionDaoService;
import com.foreveross.atwork.db.daoService.UserDaoService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.Relationship;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ContactHelper;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.listener.TextWatcherAdapter;
import com.foreveross.atwork.manager.EmployeeManager;
import com.foreveross.atwork.manager.OnlineManager;
import com.foreveross.atwork.manager.model.ExpandEmpTreeAction;
import com.foreveross.atwork.modules.app.dao.AppDaoService;
import com.foreveross.atwork.modules.chat.dao.ChatDaoService;
import com.foreveross.atwork.modules.chat.service.ChatService;
import com.foreveross.atwork.modules.group.module.SelectToHandleAction;
import com.foreveross.atwork.modules.group.service.SelectToHandleActionService;
import com.foreveross.atwork.modules.search.activity.NewSearchActivity;
import com.foreveross.atwork.modules.search.adapter.MinjieSearchResultPagerAdapter;
import com.foreveross.atwork.modules.search.adapter.SearchWelcomeListAdapter;
import com.foreveross.atwork.modules.search.model.NewSearchControlAction;
import com.foreveross.atwork.modules.search.model.SearchAction;
import com.foreveross.atwork.modules.search.model.SearchContent;
import com.foreveross.atwork.modules.search.model.SearchMessageItem;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 敏捷搜索定制, 默认搜索改成联系人，全部放到后面去，去掉搜索设备
 * */
public class MinjieNewSearchFragment extends BackHandledFragment {

    public static final int SEARCH_NEED_MORE_EXACTLY_THRESHOLD = 20;

    private EditText mSearchEditText;
    private View mBackView;
    private ImageView mCancelView;
    private TextView mTvSearch;

    private RelativeLayout mRlSearchWelcome;
    private RecyclerView mRvSearchWelcome;
    private TabLayout mTabLayout;
    private ViewPager mVpResult;
    private View mVSearchNoResult;
    private View mRlSearchResult;


    private NewSearchControlAction mNewSearchControlAction;
    private SearchContent[] mSearchContents;
    private SearchAction mSearchAction = SearchAction.DEFAULT;
    private SelectToHandleAction mSelectToHandleAction;
    private boolean mFilterSelf = false;

    private SearchWelcomeListAdapter mSearchWelcomeListAdapter;
    private MinjieSearchResultPagerAdapter mSearchResultPagerAdapter;

    private int mLastSelectedIndex = 0;

    private Handler mHandler = new Handler();

    private GlobalSearchRunnable mGlobalSearchRunnable;

    private Map<SearchContent, Boolean> mResultMap = new HashMap<>();

    private String mSearchKey;

    private boolean mIsInput;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_new, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerListener();
        initData();

        initUI();
    }

    private void initUI() {
        mSearchWelcomeListAdapter = new SearchWelcomeListAdapter(getActivity(), mSearchContents);

        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
        flowLayoutManager.setAutoMeasureEnabled(true);
        // Grid布局
//        GridLayoutManager gridLayoutMgr = new GridLayoutManager(getActivity(), 5);
        mRvSearchWelcome.setLayoutManager(flowLayoutManager);
        mRvSearchWelcome.setAdapter(mSearchWelcomeListAdapter);


        mSearchResultPagerAdapter = new MinjieSearchResultPagerAdapter(getActivity());
        mSearchResultPagerAdapter.initResultData(mSearchContents);
        mSearchResultPagerAdapter.setSearchAction(mSearchAction);
        mSearchResultPagerAdapter.setSelectToHandleAction(mSelectToHandleAction);
        mSearchResultPagerAdapter.setOnScrollListViewListener(() -> AtworkUtil.hideInput(getActivity(), mSearchEditText));

        mVpResult.setAdapter(mSearchResultPagerAdapter);
        mTabLayout.setupWithViewPager(mVpResult);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabLayout.setTabTextColors(ContextCompat.getColor(getContext(), R.color.common_text_color_999), ContextCompat.getColor(getContext(), R.color.common_item_black));

        refreshTab();
    }

    @Override
    protected void findViews(View view) {
        mBackView = view.findViewById(R.id.title_bar_chat_search_back);
        mSearchEditText = view.findViewById(R.id.title_bar_chat_search_key);
        mCancelView = view.findViewById(R.id.title_bar_chat_search_cancel);
        mTvSearch = view.findViewById(R.id.title_ba_search_label);
        mRvSearchWelcome = view.findViewById(R.id.rv_welcome_list);
        mRlSearchWelcome = view.findViewById(R.id.rl_search_welcome);
        mTabLayout = view.findViewById(R.id.tabLayout_search);
        mVpResult = view.findViewById(R.id.vp_search_result);
        mVSearchNoResult = view.findViewById(R.id.ll_no_result);
        mRlSearchResult = view.findViewById(R.id.rl_search_result);
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (null != bundle) {
            mNewSearchControlAction = bundle.getParcelable(NewSearchActivity.DATA_NEW_SEARCH_CONTROL_ACTION);


            if (null != mNewSearchControlAction) {
                SearchContent[] searchContentArray = mNewSearchControlAction.getSearchContentList();
                if(null != searchContentArray) {
                    List<SearchContent> searchContentList = new ArrayList<>(Arrays.asList(searchContentArray));

                    searchContentList.remove(SearchContent.SEARCH_DEVICE);
                    searchContentList.add(SearchContent.SEARCH_ALL);

                    mSearchContents = searchContentList.toArray(new SearchContent[0]);
                }

                mSearchAction = mNewSearchControlAction.getSearchAction();
                mSelectToHandleAction = mNewSearchControlAction.getSelectToHandleAction();
                mFilterSelf = mNewSearchControlAction.getFilterMe();
            }

        }


        if (null != mSelectToHandleAction) {
            FragmentActivity activity = getActivity();
            if (activity instanceof NewSearchActivity) {
                ((NewSearchActivity) activity).setFinishChainTag(SelectToHandleActionService.TAG_HANDLE_SELECT_TO_ACTION);
            }
        }
    }

    private void refreshTab() {
        //自定义tab
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);

            if (tab != null) {
                tab.setCustomView(mSearchResultPagerAdapter.getTabView(getActivity(), i));

                if (mLastSelectedIndex == i) {
                    tab.select();
                }
            }

        }
    }

    private void registerListener() {
        mBackView.setOnClickListener(v -> {
            onBackPressed();
        });


        mVpResult.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mLastSelectedIndex = position;

                search(mSearchEditText.getText().toString());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                mVpResult.setCurrentItem(tab.getPosition());

                if (null != tab.getCustomView()) {
                    TextView tv = tab.getCustomView().findViewById(R.id.tv_custom);
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.common_item_black));
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (null != tab.getCustomView()) {
                    TextView tv = tab.getCustomView().findViewById(R.id.tv_custom);
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.common_text_color_999));

                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (null != tab.getCustomView()) {
                    TextView tv = tab.getCustomView().findViewById(R.id.tv_custom);
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.common_item_black));

                }
            }
        });


        mSearchEditText.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtils.isEmpty(s.toString())) {
                    mCancelView.setVisibility(View.VISIBLE);
                    showTvSearchLabel();


                } else {
                    mCancelView.setVisibility(View.GONE);
                    hideTvSearchLabel();

                }


                if(!shouldSearch(s.toString())) {
                    return;
                }

                search(s.toString());
            }
        });


        mSearchEditText.setOnEditorActionListener((v, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                AtworkUtil.hideInput(getActivity(), mSearchEditText);
                search(mSearchEditText.getText().toString());
                return true;
            }
            return false;
        });


        mTvSearch.setOnClickListener(v -> {
            AtworkUtil.hideInput(getActivity(), mSearchEditText);
            search(mSearchEditText.getText().toString());
        });

        mCancelView.setOnClickListener(v -> {
            mSearchEditText.setText(StringUtils.EMPTY);
            emptyResult();
        });

    }


    private boolean shouldSearch(String text) {
        if(StringUtils.isEmpty(text)) {
            return true;
        }


        if(AtworkConfig.SEARCH_CONFIG.isAutoSearchInMainBusiness()) {
            return true;
        }

        return false;
    }

    private void hideTvSearchLabel() {
        mTvSearch.setVisibility(View.GONE);
    }

    private void showTvSearchLabel() {
        if(AtworkConfig.SEARCH_CONFIG.isShowSearchBtnInMainBusiness()) {
            mTvSearch.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected boolean onBackPressed() {
        AtworkUtil.hideInput(getActivity(), mSearchEditText);
        finish();
        return false;
    }

    private SearchContent getCurrentSearchingContent() {
        return mSearchContents[mLastSelectedIndex];
    }

    private void search(String value) {
        mSearchKey = UUID.randomUUID().toString();
        mIsInput = true;
        mResultMap.put(getCurrentSearchingContent(), null);

        if (StringUtils.isEmpty(value)) {
//            emptyResult();
            return;
        }

        mGlobalSearchRunnable = new GlobalSearchRunnable(mSearchKey, value, getCurrentSearchingContent());
//        mLlRoot.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        mHandler.postDelayed(mGlobalSearchRunnable, 800);
    }

    private void emptyResult() {
        refreshResultMap();

        mSearchResultPagerAdapter.clearData();
        mRlSearchWelcome.setVisibility(View.VISIBLE);
        mVSearchNoResult.setVisibility(View.GONE);
        mRlSearchResult.setVisibility(View.GONE);
        //清除搜索时, tab 会滚到初始位置
        mVpResult.setCurrentItem(0);

    }


    public class GlobalSearchRunnable implements Runnable {

        private String searchKey;

        private String searchValue;

        private SearchContent searchingContent;

        public GlobalSearchRunnable(String searchKey, String searchValue, SearchContent searchingContent) {
            this.searchKey = searchKey;
            this.searchValue = searchValue;
            this.searchingContent = searchingContent;
        }

        @Override
        public void run() {
            //searchKey一样时,才进行搜索
            if (searchKey.equals(MinjieNewSearchFragment.this.mSearchKey)) {
                mSearchResultPagerAdapter.clearTargetData(searchingContent);
                mSearchResultPagerAdapter.setKey(searchValue);

                if (searchingContentContains(SearchContent.SEARCH_USER)) {
                    if(AtworkConfig.SEARCH_CONFIG.isSearchUsersLocalFirst()) {
                        //全局搜索入口, 需要根据配置设置是否需要视图权限控制
                        ExpandEmpTreeAction expandEmpTreeAction = ExpandEmpTreeAction.newExpandEmpTreeAction()
                                .setSelectMode(false)
                                .setViewAcl(AtworkConfig.SEARCH_CONFIG.isEmployeeViewAcl());

                        searchEmployeeLocal(expandEmpTreeAction);

                    } else {

                        if (DomainSettingsManager.getInstance().handleFriendsRelationshipsFeature()) {
                            searchFriends(new ArrayList<>());

                        } else {
                            handleAfterLocalUserSearch(new ArrayList<>());

                        }

                    }

                }

                if (searchingContentContains(SearchContent.SEARCH_DISCUSSION)) {
                    //搜索群组
                    DiscussionDaoService.getInstance().searchDiscussion(searchKey, searchValue, (searchKey1, discussionList) -> {
                        if (searchKey1.equals(MinjieNewSearchFragment.this.mSearchKey)) {
                            refreshResultData(SearchContent.SEARCH_DISCUSSION, discussionList, true);

                        }
                    });

                }

                if (searchingContentContains(SearchContent.SEARCH_APP)) {
                    //搜索应用
                    AppDaoService.getInstance().searchApps(searchKey, searchValue, null, (searchKey1, appList) -> {
                        if (searchKey1.equals(MinjieNewSearchFragment.this.mSearchKey)) {
//                            mSearchListAdapter.setAppSearchItem(appList);
                            refreshResultData(SearchContent.SEARCH_APP, appList, true);

                        }
                    });
                }

                if (searchingContentContains(SearchContent.SEARCH_MESSAGES)) {
                    //搜索所有聊天记录
                    List<Session> sessions = ChatService.queryAllSessionsDb();
                    final List<ShowListItem> totalSearchItems = new ArrayList<>();

                    for (final Session session : sessions) {
                        ChatDaoService.getInstance().searchMessages(BaseApplicationLike.baseContext, searchKey, searchValue, session.identifier, (searchKey, messages) -> {
                            List<ShowListItem> searchMessageItems = new ArrayList<>();
                            for (ChatPostMessage chatPostMessage : messages) {
                                SearchMessageItem searchMessageItem = new SearchMessageItem();
                                searchMessageItem.session = session;
                                searchMessageItem.content = chatPostMessage.getSearchAbleString();
                                searchMessageItem.msgId = chatPostMessage.deliveryId;
                                searchMessageItems.add(searchMessageItem);
                            }
                            if (searchKey.equals(MinjieNewSearchFragment.this.mSearchKey)) {
                                totalSearchItems.addAll(searchMessageItems);

                                refreshResultData(SearchContent.SEARCH_MESSAGES, totalSearchItems, true);


                            }

                        });
                    }
                }
            }
        }

        public void searchFriends(@Nullable List<String> needCheckEmpIdList) {
            //如果域设置下好友开关关闭，不搜索好友
            if (!DomainSettingsManager.getInstance().handleFriendsRelationshipsFeature()) {
                return;
            }
            UserDaoService.getInstance().searchUsers(searchKey, searchValue, Relationship.Type.FRIEND, (searchKeyCallBack, userList) -> {
                if (!searchKey.equals(searchKeyCallBack)) {
                    return;
                }
                List<ShowListItem> itemList = new ArrayList<>();
                itemList.addAll(userList);

                addUserSearchResultData(itemList);

                //作为搜索雇员以及好友的终结点去判断是否结果为空
                handleAfterLocalUserSearch(mSearchResultPagerAdapter.getResultMap(SearchContent.SEARCH_USER));

                //检查本地搜出好友, 或者本地搜出的雇员的在线离线状态
                checkLocalContactOnlineStatus(needCheckEmpIdList, userList);


            });
        }

        public void searchEmployeesRemote(List<ShowListItem> localResultList) {

            //全局搜索入口, 需要根据配置设置是否需要视图权限控制
            ExpandEmpTreeAction expandEmpTreeAction = ExpandEmpTreeAction.newExpandEmpTreeAction()
                    .setSelectMode(false)
                    .setViewAcl(AtworkConfig.SEARCH_CONFIG.isEmployeeViewAcl());


            //根据搜索key搜索雇员列表
            EmployeeManager.getInstance().searchEmployeesRemote(BaseApplicationLike.baseContext, searchKey, searchValue, expandEmpTreeAction, new EmployeeManager.RemoteSearchEmployeeListListener() {
                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    if (!ErrorHandleUtil.handleBaseError(errorCode, errorMsg)) {
                        if(ListUtil.isEmpty(localResultList)) {
                            toast(R.string.network_not_avaluable);
                        }
                    }
                }

                @Override
                public void onSuccess(String searchKeyCallBack, List<Employee> employeeList) {
                    if (!searchKey.equals(searchKeyCallBack)) {
                        return;
                    }
                    List<ShowListItem> itemList = new ArrayList<>();
                    itemList.addAll(localResultList);
                    itemList.addAll(employeeList);

                    filterResult(itemList);

                    refreshResultData(SearchContent.SEARCH_USER, itemList, true);


                }



            });
        }



        /**
         * 本地搜索雇员
         * */
        public void searchEmployeeLocal(@NonNull ExpandEmpTreeAction expandEmpTreeAction) {
            EmployeeManager.getInstance().searchEmployeesLocal(BaseApplicationLike.baseContext, searchKey, searchValue, expandEmpTreeAction, new EmployeeManager.LocalSearchEmployeeListListener() {
                @Override
                public void onSuccess(String searchKeyCallBack, List<Employee> employeeList) {
                    if (!searchKey.equals(searchKeyCallBack)) {
                        return;
                    }

                    List<ShowListItem> itemList = new ArrayList<>();
                    itemList.addAll(employeeList);

                    filterResult(itemList);

                    refreshResultData(SearchContent.SEARCH_USER, itemList, !DomainSettingsManager.getInstance().handleFriendsRelationshipsFeature());

                    if (!DomainSettingsManager.getInstance().handleFriendsRelationshipsFeature()) {
                        handleAfterLocalUserSearch(itemList);
                        return;
                    }

                    searchFriends(Employee.toUserIdList(employeeList));

                }

                @Override
                public void onFail() {
                    AtworkToast.showToast(getResources().getString(R.string.contact_search_fail));

                }
            });
        }


        public boolean searchingContentContains(SearchContent searchContent) {
            return searchContent == this.searchingContent || SearchContent.SEARCH_ALL == this.searchingContent;
        }


        public void refreshResultData(SearchContent searchContent, List<? extends ShowListItem> resultList, boolean handleResultView) {
            mSearchResultPagerAdapter.refreshResultData(searchContent, resultList);
//            if(SearchContent.SEARCH_ALL == this.searchingContent) {
//                mSearchResultPagerAdapter.refreshResultData(SearchContent.SEARCH_ALL, resultList);
//            }

            notifyAdapterDataSetChanged(searchContent, resultList, handleResultView);
//        refreshTab();
        }

        private  void handleAfterLocalUserSearch(List<ShowListItem> sourceList) {
            if (sourceList.size() <= SEARCH_NEED_MORE_EXACTLY_THRESHOLD) {
                searchEmployeesRemote(sourceList);
            }
        }

    }




    private void filterResult(List<ShowListItem> contactList) {
        List<ShowListItem> contactListFiltered =  ContactHelper.filterDuplicated(contactList);
        contactList.clear();
        contactList.addAll(contactListFiltered);


        if (!mFilterSelf) {
            return;
        }

        filterResultLoginUser(contactList);


    }

    private void filterResultLoginUser(List<ShowListItem> contactList) {
        List<ShowListItem> contactRemoveList = new ArrayList<>();
        for (ShowListItem contact : contactList) {
            if (User.isYou(BaseApplicationLike.baseContext, contact.getId())) {
                contactRemoveList.add(contact);
            }
        }

        if (!ListUtil.isEmpty(contactRemoveList)) {
            contactList.removeAll(contactRemoveList);
        }
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


    private boolean isAllSearchHasResult() {
        boolean isSearchHasResult = false;
        for (Boolean oneSearchResult : mResultMap.values()) {
            if (true == oneSearchResult) {
                isSearchHasResult = true;
                break;
            }
        }

        return isSearchHasResult;

    }










    private boolean isNotifying = false;

    private <V> void notifyAdapterDataSetChanged(SearchContent type, List<V> sourceList, boolean shouldHandleUiAfterSearch) {

        if (shouldHandleUiAfterSearch) {
            mResultMap.put(type, !ListUtil.isEmpty(sourceList));
        }

        if(isNotifying) {
            return;
        }

        isNotifying = true;
        mVpResult.postDelayed(()->{

            doNotifyAdapterDataSetChanged();

            minjieHandleUiAfterSearch();

            isNotifying = false;

        }, 300);
    }

    private void minjieHandleUiAfterSearch() {
        mVSearchNoResult.setVisibility(View.GONE);
        mRlSearchResult.setVisibility(View.VISIBLE);
        mRlSearchWelcome.setVisibility(View.GONE);
    }

    private void handleUiAfterSearch() {
        if (isAllSearchNoResult()) {
            mVSearchNoResult.setVisibility(View.VISIBLE);
            mRlSearchResult.setVisibility(View.GONE);
            mRlSearchWelcome.setVisibility(View.GONE);

        } else  {

            mVSearchNoResult.setVisibility(View.GONE);
            mRlSearchResult.setVisibility(View.VISIBLE);
            mRlSearchWelcome.setVisibility(View.GONE);

        }
    }


    private void doNotifyAdapterDataSetChanged() {
        mSearchResultPagerAdapter.notifyDataSetChanged();
    }


    public void addUserSearchResultData(List<ShowListItem> friendUserSearchItem) {
        List<ShowListItem> bucketList = new ArrayList<>();
        List<ShowListItem> userResultMap = mSearchResultPagerAdapter.getResultMap(SearchContent.SEARCH_USER);

        //去重处理
        for (ShowListItem friendListItem : friendUserSearchItem) {
            boolean isDuplicated = false;

            for (ShowListItem showListItem : userResultMap) {
                if (friendListItem.getId().equals(showListItem.getId())) {
                    isDuplicated = true;
                    break;
                }

            }

            if (!isDuplicated) {
                bucketList.add(friendListItem);

            }
        }


        userResultMap.addAll(bucketList);

        notifyAdapterDataSetChanged(SearchContent.SEARCH_USER, mSearchResultPagerAdapter.getResultMap(SearchContent.SEARCH_USER), true);

    }


    public void checkLocalContactOnlineStatus(@Nullable List<String> needCheckEmpIdList, List<User> userList) {
        if (!ListUtil.isEmpty(userList)) {
            List<String> requestIdList = new ArrayList<>();
            requestIdList.addAll(User.toUserIdList(userList));

            if (!ListUtil.isEmpty(needCheckEmpIdList)) {

                requestIdList.addAll(needCheckEmpIdList);
            }


            OnlineManager.getInstance().checkOnlineList(getActivity(), requestIdList, onlineList -> doNotifyAdapterDataSetChanged());
        }
    }

}
