package com.foreveross.atwork.modules.search.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.foreveross.atwork.AtworkApplicationLike;
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
import com.foreveross.atwork.infrastructure.model.orgization.Department;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ContactHelper;
import com.foreveross.atwork.infrastructure.utils.CustomerHelper;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.layout.KeyboardRelativeLayout;
import com.foreveross.atwork.listener.TextWatcherAdapter;
import com.foreveross.atwork.manager.EmployeeManager;
import com.foreveross.atwork.manager.OnlineManager;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.manager.model.ExpandEmpTreeAction;
import com.foreveross.atwork.modules.app.dao.AppDaoService;
import com.foreveross.atwork.modules.chat.dao.ChatDaoService;
import com.foreveross.atwork.modules.chat.service.ChatService;
import com.foreveross.atwork.modules.file.service.FileTransferService;
import com.foreveross.atwork.modules.group.module.SelectToHandleAction;
import com.foreveross.atwork.modules.group.service.SelectToHandleActionService;
import com.foreveross.atwork.modules.search.activity.NewSearchActivity;
import com.foreveross.atwork.modules.search.adapter.SearchResultPagerAdapter;
import com.foreveross.atwork.modules.search.adapter.SearchWelcomeListAdapter;
import com.foreveross.atwork.modules.search.component.searchVoice.OnSearchVoiceViewHandleListener;
import com.foreveross.atwork.modules.search.component.searchVoice.SearchVoiceFloatView;
import com.foreveross.atwork.modules.search.model.NewSearchControlAction;
import com.foreveross.atwork.modules.search.model.SearchAction;
import com.foreveross.atwork.modules.search.model.SearchContent;
import com.foreveross.atwork.modules.search.model.SearchMessageCompatItem;
import com.foreveross.atwork.modules.search.model.SearchMessageItem;
import com.foreveross.atwork.modules.search.model.SearchMode;
import com.foreveross.atwork.modules.search.model.TalkingRecognizeResult;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by dasunsy on 2017/12/12.
 */

public class NewSearchFragment extends BackHandledFragment implements SearchResultPagerAdapter.OnMoreItemClickListener {

    public static final int SEARCH_NEED_MORE_EXACTLY_THRESHOLD = 3;

    private EditText mSearchEditText;
    private ProgressBar mPbVoiceRecognizing;
    private View mBackView;
    private TextView mCancelView;

    private KeyboardRelativeLayout mRlSearchWelcome;
    private TextView tvSearchTip;
    private RecyclerView mRvSearchWelcome;
    private TabLayout mTabLayout;
    private ViewPager mVpResult;
    private View mVSearchNoResult;
    private View mRlSearchResult;
    private SearchVoiceFloatView mSearchVoiceFloatView;


    private NewSearchControlAction mNewSearchControlAction;
    private SearchContent[] mSearchContents;
    private SearchAction mSearchAction = SearchAction.DEFAULT;
    private SelectToHandleAction mSelectToHandleAction;
    private boolean mFilterSelf = false;

    private SearchWelcomeListAdapter mSearchWelcomeListAdapter;
    private SearchResultPagerAdapter mSearchResultPagerAdapter;

    private int mLastSelectedIndex = 0;

    private Handler mHandler = new Handler();

    private GlobalSearchRunnable mGlobalSearchRunnable;

    private Map<SearchContent, Boolean> mResultMap = new HashMap<>();

    private String mSearchKey;

    private boolean mIsInput;

    private SearchMode mSearchMode = SearchMode.COMMON;


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

        mSearchEditText.postDelayed(() -> AtworkUtil.showInput(mActivity, mSearchEditText), 100);

        mSearchVoiceFloatView.handleInit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSearchVoiceFloatView.handleDestroy();
    }

    private void initUI() {
        mSearchWelcomeListAdapter = new SearchWelcomeListAdapter(getActivity(), mSearchContents);

        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
        flowLayoutManager.setAutoMeasureEnabled(true);
        // Grid布局
//        GridLayoutManager gridLayoutMgr = new GridLayoutManager(getActivity(), 5);
        mRvSearchWelcome.setLayoutManager(flowLayoutManager);
        mRvSearchWelcome.setAdapter(mSearchWelcomeListAdapter);


        mSearchResultPagerAdapter = new SearchResultPagerAdapter(getActivity());
        mSearchResultPagerAdapter.initResultData(mSearchContents);
        mSearchResultPagerAdapter.setSearchAction(mSearchAction);
        mSearchResultPagerAdapter.setSelectToHandleAction(mSelectToHandleAction);
        mSearchResultPagerAdapter.setOnScrollListViewListener(() -> AtworkUtil.hideInput(getActivity(), mSearchEditText));
        mSearchResultPagerAdapter.setMoreItemClickListener(this);
        mVpResult.setAdapter(mSearchResultPagerAdapter);
        mTabLayout.setupWithViewPager(mVpResult);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabLayout.setTabTextColors(ContextCompat.getColor(getContext(), R.color.common_text_color), ContextCompat.getColor(getContext(), R.color.white));
        refreshTab();


    }

    @Override
    protected void findViews(View view) {
        mBackView = view.findViewById(R.id.iv_back);
        mSearchEditText = view.findViewById(R.id.et_search_content);
        mPbVoiceRecognizing = view.findViewById(R.id.pb_voice_recognizing);
        mCancelView = view.findViewById(R.id.tv_cancel);
        tvSearchTip = view.findViewById(R.id.tv_search_tip);
        mRvSearchWelcome = view.findViewById(R.id.rv_welcome_list);
        mRlSearchWelcome = view.findViewById(R.id.rl_search_welcome);
        mTabLayout = view.findViewById(R.id.tabLayout_search);
        mVpResult = view.findViewById(R.id.vp_search_result);
        mVSearchNoResult = view.findViewById(R.id.ll_no_result);
        mRlSearchResult = view.findViewById(R.id.rl_search_result);
        mSearchVoiceFloatView = view.findViewById(R.id.v_search_voice_float_view);
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (null != bundle) {
            mNewSearchControlAction = bundle.getParcelable(NewSearchActivity.DATA_NEW_SEARCH_CONTROL_ACTION);


            if (null != mNewSearchControlAction) {
                mSearchContents = mNewSearchControlAction.getSearchContentList();
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


    private void refreshSearchUIOnMode() {
        if(SearchMode.VOICE == mSearchMode) {
            tvSearchTip.setVisibility(View.INVISIBLE);
            mRvSearchWelcome.setVisibility(View.INVISIBLE);
            mSearchEditText.setHint(R.string.tip_talk_content);

            return;
        }

        tvSearchTip.setVisibility(View.VISIBLE);
        mRvSearchWelcome.setVisibility(View.VISIBLE);
        mSearchEditText.setHint(R.string.action_search);
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

                if(!shouldSearch(s.toString())) {
                    return;
                }

                search(s.toString(), false);
            }
        });


        mSearchEditText.setOnEditorActionListener((v, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                AtworkUtil.hideInput(getActivity(), mSearchEditText);
                search(mSearchEditText.getText().toString(), true);
                return true;
            }
            return false;
        });

        mCancelView.setOnClickListener(v -> {
            String searchStr = mSearchEditText.getText().toString();
            if(TextUtils.isEmpty(searchStr)){
                AtworkUtil.hideInput(getActivity(), mSearchEditText);
                finish();
                return;
            }
            mSearchKey = UUID.randomUUID().toString();

            mSearchEditText.setText(StringUtils.EMPTY);
            emptyResult();
        });


        mRlSearchWelcome.setOnKeyboardChangeListener(state -> {
            if(CustomerHelper.isRfchina(getContext())) {
                if (KeyboardRelativeLayout.KEYBOARD_STATE_SHOW == state) {
                    if (AtworkConfig.VOICE_CONFIG.isEnabled()) {
                        mSearchVoiceFloatView.setVisibility(View.VISIBLE);

                    }

                } else if (KeyboardRelativeLayout.KEYBOARD_STATE_HIDE == state) {
                    mSearchVoiceFloatView.setVisibility(View.GONE);
                }
            }
        });


        mSearchVoiceFloatView.setOnSearchVoiceViewHandleListener(new OnSearchVoiceViewHandleListener() {
            @Override
            public void onStart() {
                mSearchMode = SearchMode.VOICE;
                refreshSearchUIOnMode();
            }

            @Override
            public void onTalking(@NotNull TalkingRecognizeResult result) {

                String text;
                if(result.getReplaced()) {
                    text = result.getMessage();
                } else {
                    text = mSearchEditText.getText().toString() + result.getMessage();

                }

                mSearchEditText.setText(text);
                mSearchEditText.setHint(StringUtils.EMPTY);
                mSearchEditText.setSelection(text.length());

                if(!StringUtils.isEmpty(text)) {
//                    mPbVoiceRecognizing.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancel() {
                mSearchMode = SearchMode.COMMON;

//                mPbVoiceRecognizing.setVisibility(View.GONE);

                search(mSearchEditText.getText().toString(), true);
            }
        });

    }


    private boolean shouldSearch(String text) {
        if(StringUtils.isEmpty(text)) {
            return true;
        }


        if(SearchMode.VOICE == mSearchMode) {
            return false;
        }

        if(AtworkConfig.SEARCH_CONFIG.isAutoSearchInMainBusiness()) {
            return true;
        }

        return false;
    }



    @Override
    protected boolean onBackPressed() {
        AtworkUtil.hideInput(getActivity(), mSearchEditText);
        finish();
        return false;
    }

    private void search(String value, boolean instant) {
        mSearchKey = UUID.randomUUID().toString();
        mIsInput = true;
        refreshResultMap();
        if (StringUtils.isEmpty(value)) {
            emptyResult();

            return;
        }

        mGlobalSearchRunnable = new GlobalSearchRunnable(mSearchKey, value);
//        mLlRoot.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        if (instant) {
            mGlobalSearchRunnable.run();
            return;
        }
        mHandler.postDelayed(mGlobalSearchRunnable, 800);
    }

    private void emptyResult() {
        mSearchResultPagerAdapter.clearData();
        mRlSearchWelcome.setVisibility(View.VISIBLE);
        mVSearchNoResult.setVisibility(View.GONE);
        mRlSearchResult.setVisibility(View.GONE);
        //清除搜索时, tab 会滚到初始位置
        mVpResult.setCurrentItem(0);

        refreshSearchUIOnMode();
    }

    @Override
    public void moreItemClickContent(int pos) {
        mLastSelectedIndex = pos;
        refreshTab();
    }


    public class GlobalSearchRunnable implements Runnable {

        private String searchKey;

        private String searchValue;

        public GlobalSearchRunnable(String searchKey, String searchValue) {
            this.searchKey = searchKey;
            this.searchValue = searchValue;
        }

        @Override
        public void run() {
            //searchKey一样时,才进行搜索
            if (searchKey.equals(NewSearchFragment.this.mSearchKey)) {
                mSearchResultPagerAdapter.clearData();
                mSearchResultPagerAdapter.setKey(searchValue);

                if (searchContentContains(SearchContent.SEARCH_USER)) {
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

                if (searchContentContains(SearchContent.SEARCH_DISCUSSION)) {
                    //搜索群组
                    DiscussionDaoService.getInstance().searchDiscussionInfo(searchKey, searchValue, (searchKey1, discussionList) -> {
                        if (searchKey1.equals(NewSearchFragment.this.mSearchKey)) {
                            refreshResultData(SearchContent.SEARCH_DISCUSSION, discussionList, true);

                        }
                    });

                }

                if (searchContentContains(SearchContent.SEARCH_APP)) {

                    String orgCode = PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext);

                    AppDaoService.getInstance().searchBundles(searchKey, searchValue, orgCode, (searchKey1, appBundleList) -> {
                        if (searchKey1.equals(NewSearchFragment.this.mSearchKey)) {
                            refreshResultData(SearchContent.SEARCH_APP, appBundleList, true);

                        }
                    });
                }

                if (searchContentContains(SearchContent.SEARCH_MESSAGES)) {
                    //搜索所有聊天记录
                    List<Session> sessions = ChatService.queryAllSessionsDb();
                    final List<ShowListItem> totalSearchItems = new ArrayList<>();

                    for (final Session session : sessions) {
                        ChatDaoService.getInstance().searchMessages(BaseApplicationLike.baseContext, searchKey, searchValue, session.identifier, (searchKey, messages) -> {
                            List<ShowListItem> searchMessageCompatItems = new ArrayList<>();
                            SearchMessageCompatItem searchMessageCompatItem = new SearchMessageCompatItem();
                            searchMessageCompatItem.session = session;
                            if (!messages.isEmpty()) {
                                for (ChatPostMessage chatPostMessage : messages) {
                                    SearchMessageItem searchMessageItem = new SearchMessageItem();
                                    searchMessageItem.session = session;
                                    searchMessageItem.content = chatPostMessage.getSearchAbleString();
                                    searchMessageItem.msgId = chatPostMessage.deliveryId;
                                    searchMessageItem.msgTime = chatPostMessage.deliveryTime;
                                    searchMessageItem.message = chatPostMessage;
                                    searchMessageCompatItem.mMessageList.add(searchMessageItem);
                                }
                                searchMessageCompatItems.add(searchMessageCompatItem);
                            }
                            if (searchKey.equals(NewSearchFragment.this.mSearchKey)) {
                                totalSearchItems.addAll(searchMessageCompatItems);
//                                Collections.sort(totalSearchItems, (o1, o2) -> {
//                                    if(o1 instanceof SearchMessageItem
//                                        && o2 instanceof SearchMessageItem)  {
//
//                                        SearchMessageItem searchMessageItem1 = (SearchMessageItem) o1;
//                                        SearchMessageItem searchMessageItem2 = (SearchMessageItem) o2;
//
//                                        return Long.compare(searchMessageItem2.msgTime, searchMessageItem1.msgTime);
//                                    }
//                                    return 0;
//                                });

                                refreshResultData(SearchContent.SEARCH_MESSAGES, totalSearchItems, true);


                            }

                        });
                    }
                }


                if (searchContentContains(SearchContent.SEARCH_DEVICE)) {
                    FileTransferService.INSTANCE.search(searchKey, searchValue, (searchKey, fileTransfer) -> {
                        if (null == fileTransfer) {
                            return;
                        }

                        if (searchKey.equals(NewSearchFragment.this.mSearchKey)) {
                            refreshResultData(SearchContent.SEARCH_DEVICE, ListUtil.makeSingleList(fileTransfer), true);

                        }
                    });
                }

                if (searchContentContains(SearchContent.SEARCH_DEPARTMENT)) {
                    searchDepartments();
                }

            }
        }

        public void searchDepartments() {
            OrganizationManager.getInstance().searchDepartmentRemote(AtworkApplicationLike.baseContext, searchKey, searchValue, null, new OrganizationManager.RemoteSearchDepartmentsListener() {
                @Override
                public void onSuccess(String searchKeyCallBack, List<Department> departments) {
                    if (!searchKey.equals(searchKeyCallBack) || ListUtil.isEmpty(departments)) {
                        return;
                    }
                    refreshResultData(SearchContent.SEARCH_DEPARTMENT, departments, true);
                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    if (isDetached()) {
                        return;
                    }
                    try {
                        AtworkToast.showToast(getResources().getString(R.string.contact_search_fail));
                    } catch (Exception e) {

                    }

                }
            });
        }

        public void searchFriends(@Nullable List<String> needCheckEmpIdList) {
            //如果域设置下好友开关关闭，不搜索好友
            if (!DomainSettingsManager.getInstance().handleFriendsRelationshipsFeature()) {
                return;
            }
            UserDaoService.getInstance().searchUsers(searchKey, searchValue, Relationship.Type.FRIEND, (searchKeyCallBack, userList) -> {
                if (!mSearchKey.equals(searchKeyCallBack)) {
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
                    if (!mSearchKey.equals(searchKeyCallBack)) {
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
                    if (!mSearchKey.equals(searchKeyCallBack)) {
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






    public boolean searchContentContains(SearchContent searchContent) {
        return Arrays.asList(mSearchContents).contains(searchContent);
    }

    public void refreshResultData(SearchContent searchContent, List<? extends ShowListItem> resultList, boolean handleResultView) {
        mSearchResultPagerAdapter.refreshResultData(searchContent, resultList);
        notifyAdapterDataSetChanged(searchContent, resultList, handleResultView);
    }


    private <V> void notifyAdapterDataSetChanged(SearchContent type, List<V> sourceList, boolean shouldHandleUiAfterSearch) {

        if (shouldHandleUiAfterSearch) {
            mResultMap.put(type, !ListUtil.isEmpty(sourceList));
        }


        String refreshKey = mSearchKey;


        mVpResult.postDelayed(() -> {
            if(!mSearchKey.equals(refreshKey)) {
                return;
            }

            doNotifyAdapterDataSetChanged();

            handleUiAfterSearch();


        }, 300);
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

        refreshSearchUIOnMode();
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


