package com.foreveross.atwork.modules.search.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.BasicDialogFragment;
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
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.listener.TextWatcherAdapter;
import com.foreveross.atwork.manager.EmployeeManager;
import com.foreveross.atwork.manager.OnlineManager;
import com.foreveross.atwork.manager.model.ExpandEmpTreeAction;
import com.foreveross.atwork.modules.app.dao.AppDaoService;
import com.foreveross.atwork.modules.chat.dao.ChatDaoService;
import com.foreveross.atwork.modules.chat.service.ChatService;
import com.foreveross.atwork.modules.search.adapter.SearchListAdapter;
import com.foreveross.atwork.modules.search.model.SearchAction;
import com.foreveross.atwork.modules.search.model.SearchContent;
import com.foreveross.atwork.modules.search.model.SearchMessageItem;
import com.foreveross.atwork.modules.search.util.SearchHelper;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.theme.manager.SkinMaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by lingen on 15/3/27.
 * Description: 搜索对话框
 */
public class SearchFragment extends BasicDialogFragment {
    public static final String ACTION_HANDLE_TOAST_INPUT = "action_handle_toast_input";

    public static final String DATA_SEARCH_ACTION = "DATA_SEARCH_ACTION";

    public List<SearchContent> mSearchList = new ArrayList<>();
    private Map<SearchContent, Boolean> mResultMap = new HashMap<>();
    private LinearLayout mLlRoot;
    private ListView mSearchListView;
    private SearchListAdapter mSearchListAdapter;
    private EditText mSearchEditText;
    private String mSearchKey;
    private View mBackView;
    private TextView mTvNoResult;

    private ImageView mCancelView;

    private ImageView mImgNoResult;

    private boolean mIsInput;

    private Handler mHandler = new Handler();

    private GlobalSearchRunnable mGlobalSearchRunnable;

    private Context mContext;

    private boolean mShouldToastInput = true;

    private SearchAction mSearchAction;


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /**
             * 用以控制键盘是否应该在 onResume 弹出, 现在在点击item 进去具体搜索内容再返回来时不会弹出键盘,
             * 这样体验比较好, 用户比较倾向于不希望键盘挡住
             * */
            mShouldToastInput = false;
        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置Dialog占用全屏
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mLlRoot = view.findViewById(R.id.ll_root);
        mSearchListView = view.findViewById(R.id.search_list_view);
        mSearchEditText = view.findViewById(R.id.title_bar_chat_search_key);
        mBackView = view.findViewById(R.id.title_bar_chat_search_back);
        mTvNoResult = view.findViewById(R.id.tv_no_result);
        mImgNoResult = view.findViewById(R.id.img_no_result);
        mCancelView = view.findViewById(R.id.title_bar_chat_search_cancel);
        mSearchListView.setDivider(null);
        SkinMaster.getInstance().changeTheme((ViewGroup) view);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerListener();
        //该方法需要放在onViewCreated比较合适, 若在 onStart 在部分机型(如:小米3)会出现闪烁的情况
        getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent_70);

        registerBroadcast();

        initData();

        setSearchHint();

        mSearchListAdapter = new SearchListAdapter(getActivity(), mSearchAction);
        mSearchListView.setAdapter(mSearchListAdapter);

//        if (!StringUtils.isEmpty(mSearchEditText.getText().toString())) {
//            search(mSearchEditText.getText().toString());
//        }

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mShouldToastInput) {
            toastInput();
        }

        mShouldToastInput = true;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (isAdded()) {
            AtworkUtil.hideInput(getActivity(), mSearchEditText);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unRegisterBroadcast();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mGlobalSearchRunnable);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            if (!this.isAdded()) {
                super.show(manager, tag);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }


    private void initData() {
        Bundle arguments = getArguments();

        if (null != arguments) {
            mSearchAction = (SearchAction) arguments.getSerializable(DATA_SEARCH_ACTION);
        }

        if (null == mSearchAction) {
            mSearchAction = SearchAction.DEFAULT;
        }
    }

    public void setSearchAction(SearchAction searchAction) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DATA_SEARCH_ACTION, searchAction);
        setArguments(bundle);
    }

    private void registerBroadcast() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mBroadcastReceiver, new IntentFilter(ACTION_HANDLE_TOAST_INPUT));
    }

    private void unRegisterBroadcast() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mBroadcastReceiver);
    }

    private void registerListener() {

        mSearchListView.setOnTouchListener((v, event) -> {
            if (mIsInput) {
                AtworkUtil.hideInput(getActivity(), mSearchEditText);
                mIsInput = false;
            }
            return false;
        });

        mSearchListView.setOnItemClickListener((parent, view, position, id) -> {
            SearchHelper.handleSearchResultCommonClick(getActivity(), mSearchEditText.getText().toString(), mSearchListAdapter.getItem(position), mSearchAction, null);

        });


        mCancelView.setOnClickListener(v -> {
            mSearchEditText.setText(StringUtils.EMPTY);
            mSearchListAdapter.clearData();
        });

        mBackView.setOnClickListener(v -> cancelFragment());

        mLlRoot.setOnClickListener(v -> {
            cancelFragment();

        });

        mSearchEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && !StringUtils.isEmpty(mSearchEditText.getText().toString())) {
                mCancelView.setVisibility(View.VISIBLE);
            } else {
                mCancelView.setVisibility(View.GONE);
            }
        });

        mSearchEditText.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtils.isEmpty(s.toString())) {
                    mCancelView.setVisibility(View.VISIBLE);
                } else {
                    mCancelView.setVisibility(View.GONE);
                }
                search(s.toString());
            }
        });

    }

    private void toastInput() {
        mSearchEditText.setFocusable(true);
        //延迟处理 避免View没绘制好 软键盘无法弹出
        getActivity().getWindow().getDecorView().postDelayed(() -> {
            if (isAdded()) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    mSearchEditText.requestFocus();
                    imm.showSoftInput(mSearchEditText, InputMethodManager.SHOW_FORCED);
                }
            }
        }, 100);
    }

    private void cancelFragment() {
        AtworkUtil.hideInput(getActivity(), mSearchEditText);
        dismiss();
    }

    private void search(String value) {
        mSearchKey = UUID.randomUUID().toString();
        mIsInput = true;
        refreshResultMap();
        if (StringUtils.isEmpty(value)) {
            mSearchListAdapter.clearData();
            mTvNoResult.setVisibility(View.GONE);
            mImgNoResult.setVisibility(View.GONE);
            mSearchListView.setVisibility(View.VISIBLE);
            return;
        }
        mGlobalSearchRunnable = new GlobalSearchRunnable(mSearchKey, value);
        mLlRoot.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        mHandler.postDelayed(mGlobalSearchRunnable, 800);
    }

    public void addSearch(SearchContent searchContent) {
        mSearchList.add(searchContent);
        mResultMap.put(searchContent, null);
    }


    private void setSearchHint() {
        if (SearchAction.VOIP.equals(mSearchAction)) {
            mSearchEditText.setHint(R.string.voip_search_tip);

        } else {
            if (!mSearchList.contains(SearchContent.SEARCH_APP)
                    && !mSearchList.contains(SearchContent.SEARCH_MESSAGES)
                    ) {

                if (mSearchList.contains(SearchContent.SEARCH_USER)
                        && mSearchList.contains(SearchContent.SEARCH_DISCUSSION)) {
                    mSearchEditText.setHint(R.string.search_contact_group);

                } else if (mSearchList.contains(SearchContent.SEARCH_USER)) {
                    mSearchEditText.setHint(R.string.search_contact);

                }
            }

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

    private <V> void handleUiAfterSearch(SearchContent type, List<V> sourceList) {
        mResultMap.put(type, !ListUtil.isEmpty(sourceList));

        if (ListUtil.isEmpty(sourceList) && isAllSearchNoResult()) {
            mTvNoResult.setVisibility(View.VISIBLE);
            mImgNoResult.setVisibility(View.VISIBLE);
            mSearchListView.setVisibility(View.GONE);

        } else if (!ListUtil.isEmpty(sourceList)) {

            mTvNoResult.setVisibility(View.GONE);
            mImgNoResult.setVisibility(View.GONE);
            mSearchListView.setVisibility(View.VISIBLE);
        }
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
            if (searchKey.equals(SearchFragment.this.mSearchKey)) {
                mSearchListAdapter.clearData();

                mSearchListAdapter.setKey(searchValue);

                if (mSearchList.contains(SearchContent.SEARCH_USER)) {
                    searchEmployees();

                }

                if (mSearchList.contains(SearchContent.SEARCH_DISCUSSION)) {
                    //搜索群组
                    DiscussionDaoService.getInstance().searchDiscussionInfo(searchKey, searchValue, (searchKey1, groupList) -> {
                        if (searchKey1.equals(SearchFragment.this.mSearchKey)) {
                            mSearchListAdapter.setDiscussionSearchItem(groupList);
                            handleUiAfterSearch(SearchContent.SEARCH_DISCUSSION, groupList);

                        }
                    });

                }

                if (mSearchList.contains(SearchContent.SEARCH_APP)) {
                    //搜索应用
                    AppDaoService.getInstance().searchApps(searchKey, searchValue, null, (searchKey1, appList) -> {
                        if (searchKey1.equals(SearchFragment.this.mSearchKey)) {
                            mSearchListAdapter.setAppSearchItem(appList);
                            handleUiAfterSearch(SearchContent.SEARCH_APP, appList);

                        }
                    });
                }

                if (mSearchList.contains(SearchContent.SEARCH_MESSAGES)) {
                    //搜索所有聊天记录
                    List<Session> sessions = ChatService.queryAllSessionsDb();
                    final List<ShowListItem> totalSearchItems = new ArrayList<>();

                    for (final Session session : sessions) {
                        ChatDaoService.getInstance().searchMessages(mContext, searchKey, searchValue, session.identifier, (searchKey, messages) -> {
                            List<ShowListItem> searchMessageItems = new ArrayList<>();
                            for (ChatPostMessage chatPostMessage : messages) {
                                SearchMessageItem searchMessageItem = new SearchMessageItem();
                                searchMessageItem.session = session;
                                searchMessageItem.content = chatPostMessage.getSearchAbleString();
                                searchMessageItem.msgId = chatPostMessage.deliveryId;
                                searchMessageItems.add(searchMessageItem);
                            }
                            if (searchKey.equals(SearchFragment.this.mSearchKey)) {
                                mSearchListAdapter.addSearchItems(searchMessageItems);
                                totalSearchItems.addAll(searchMessageItems);
                                handleUiAfterSearch(SearchContent.SEARCH_MESSAGES, totalSearchItems);

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

                mSearchListAdapter.addUserSearchItem(itemList, mSearchAction);

                //作为搜索雇员以及好友的终结点去判断是否结果为空
                handleUiAfterSearch(SearchContent.SEARCH_USER, mSearchListAdapter.userSearchItem);

                //检查本地搜出好友, 或者本地搜出的雇员的在线离线状态
                checkLocalContactOnlineStatus(needCheckEmpIdList, userList);


            });
        }

        public void searchEmployees() {

            ExpandEmpTreeAction expandEmpTreeAction = ExpandEmpTreeAction.newExpandEmpTreeAction()
                    .setSelectMode(false);

            //根据搜索key搜索雇员列表
            EmployeeManager.getInstance().searchEmployeesRemote(BaseApplicationLike.baseContext, searchKey, searchValue, expandEmpTreeAction, new EmployeeManager.RemoteSearchEmployeeListListener() {
                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    if (!ErrorHandleUtil.handleBaseError(errorCode, errorMsg)) {
                        searchLocal(expandEmpTreeAction);

                    }
                }

                @Override
                public void onSuccess(String searchKeyCallBack, List<Employee> employeeList) {
                    if (!searchKey.equals(searchKeyCallBack)) {
                        return;
                    }
                    List<ShowListItem> itemList = new ArrayList<>();
                    itemList.addAll(employeeList);

                    mSearchListAdapter.setUserSearchItem(itemList, mSearchAction);
//                    handleUiAfterSearch(SearchContent.SEARCH_USER, itemList);

                    searchFriends(null);

                }


                /**
                 * 本地搜索雇员
                 * */
                public void searchLocal(@NonNull ExpandEmpTreeAction expandEmpTreeAction) {
                    EmployeeManager.getInstance().searchEmployeesLocal(BaseApplicationLike.baseContext, searchKey, searchValue, expandEmpTreeAction, new EmployeeManager.LocalSearchEmployeeListListener() {
                        @Override
                        public void onSuccess(String searchKeyCallBack, List<Employee> employeeList) {
                            if (!searchKey.equals(searchKeyCallBack)) {
                                return;
                            }

                            List<ShowListItem> itemList = new ArrayList<>();
                            itemList.addAll(employeeList);

                            mSearchListAdapter.setUserSearchItem(itemList, mSearchAction);

//                            handleUiAfterSearch(SearchContent.SEARCH_USER, itemList);

                            searchFriends(Employee.toUserIdList(employeeList));

                        }

                        @Override
                        public void onFail() {
                            AtworkToast.showToast(getResources().getString(R.string.contact_search_fail));

                        }
                    });
                }

            });
        }


    }



    public void checkLocalContactOnlineStatus(@Nullable List<String> needCheckEmpIdList, List<User> userList) {
        if(!ListUtil.isEmpty(userList)) {
            List<String> requestIdList = new ArrayList<>();
            requestIdList.addAll(User.toUserIdList(userList));

            if(!ListUtil.isEmpty(needCheckEmpIdList)) {

                requestIdList.addAll(needCheckEmpIdList);
            }


            OnlineManager.getInstance().checkOnlineList(getActivity(), requestIdList, onlineList -> mSearchListAdapter.notifyDataSetChanged());
        }
    }
}
