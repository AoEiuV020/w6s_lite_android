package com.foreveross.atwork.modules.newsSummary.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
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

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foreverht.db.service.repository.MessageAppRepository;
import com.foreverht.db.service.repository.UnreadSubcriptionMRepository;
import com.foreverht.threadGear.DbThreadPoolExecutor;
import com.foreveross.atwork.R;
import com.foreveross.atwork.component.recyclerview.layoutManager.FlowLayoutManager;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.ServiceApp;
import com.foreveross.atwork.infrastructure.model.clickStatistics.Type;
import com.foreveross.atwork.infrastructure.model.newsSummary.NewsSummaryPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.listener.TextWatcherAdapter;
import com.foreveross.atwork.modules.app.dao.AppDaoService;
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment;
import com.foreveross.atwork.modules.chat.model.EntrySessionRequest;
import com.foreveross.atwork.modules.clickStatistics.ClickStatisticsManager;
import com.foreveross.atwork.modules.group.module.SelectToHandleAction;
import com.foreveross.atwork.modules.group.service.SelectToHandleActionService;
import com.foreveross.atwork.modules.newsSummary.adapter.NewsSummaryRvAdapter;
import com.foreveross.atwork.modules.newsSummary.data.NewsSummaryRVData;
import com.foreveross.atwork.modules.newsSummary.util.NewsSummaryHelper;
import com.foreveross.atwork.modules.search.activity.NewSearchActivity;
import com.foreveross.atwork.modules.search.adapter.SearchWelcomeListAdapter;
import com.foreveross.atwork.modules.search.model.NewSearchControlAction;
import com.foreveross.atwork.modules.search.model.SearchAction;
import com.foreveross.atwork.modules.search.model.SearchContent;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NewsSummarySearchFragment extends BackHandledFragment {

    public static final int SEARCH_NEED_MORE_EXACTLY_THRESHOLD = 20;

    private EditText mSearchEditText;
    private View mBackView;
    private ImageView mCancelView;
    private TextView mTvSearch;

    private RelativeLayout mRlSearchWelcome;
    private RecyclerView mRvSearchWelcome;
    private RecyclerView mVpResult;
    private View mVSearchNoResult;
    private View mRlSearchResult;


    private NewSearchControlAction mNewSearchControlAction;
    private SearchContent[] mSearchContents;
    private SearchAction mSearchAction = SearchAction.DEFAULT;
    private SelectToHandleAction mSelectToHandleAction;
    private boolean mFilterSelf = false;

    private SearchWelcomeListAdapter mSearchWelcomeListAdapter;
    private NewsSummaryRvAdapter nsRvAdapter;
    private ArrayList<NewsSummaryRVData> adapterList = new ArrayList<>();
    private ArrayList<NewsSummaryRVData> adapterContentList = new ArrayList<>();
    private ArrayList<NewsSummaryRVData> adapterServiceList = new ArrayList<>();

    private int mLastSelectedIndex = 0;

    private Handler mHandler = new Handler();

    private Map<SearchContent, Boolean> mResultMap = new HashMap<>();

    private String mSearchKey;

    private boolean mIsInput;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_news_summary, container, false);
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

        LinearLayoutManager linearLayoutManagerContent = new LinearLayoutManager(getContext());
        linearLayoutManagerContent.setOrientation(LinearLayoutManager.VERTICAL);
        mVpResult.setLayoutManager(linearLayoutManagerContent);
        nsRvAdapter = new NewsSummaryRvAdapter(getActivity(), this,adapterList);
        mVpResult.setAdapter(nsRvAdapter);
        nsRvAdapter.setOnKotlinItemClickListener(this::jumpToChatDetailActivity);
    }

    @Override
    protected void findViews(View view) {
        mBackView = view.findViewById(R.id.title_bar_chat_search_back);
        mSearchEditText = view.findViewById(R.id.title_bar_chat_search_key);
        mCancelView = view.findViewById(R.id.title_bar_chat_search_cancel);
        mTvSearch = view.findViewById(R.id.title_ba_search_label);
        mRvSearchWelcome = view.findViewById(R.id.rv_welcome_list);
        mRlSearchWelcome = view.findViewById(R.id.rl_search_welcome);
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

    private void registerListener() {
        mBackView.setOnClickListener(v -> {
            onBackPressed();
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

    private void jumpToChatDetailActivity(NewsSummaryRVData newsSummaryRVData){
        NewsSummaryHelper.Companion.selectApp(mActivity, newsSummaryRVData, (mSession, mApp) -> {
            if(mSession != null){
                doRouteChatDetailView(newsSummaryRVData, mSession);
                return;
            }
            if(mApp != null){
                doRouteChatDetailView(newsSummaryRVData, mApp);
                return;
            }
            if(isAdded()) {
                toast(getString(R.string.session_invalid_app_no));
            }
        });
    }

    private void doRouteChatDetailView(NewsSummaryRVData newsSummaryRVData,Session mSession){
        EntrySessionRequest entrySessionRequest = EntrySessionRequest
                .newRequest(SessionType.Service, mSession)
                .setOrgId(mSession.orgId);
        ChatSessionDataWrap.getInstance().entrySession(entrySessionRequest);
        Intent intent = ChatDetailActivity.getIntent(getActivity(), newsSummaryRVData.getChatId());
        intent.putExtra(ChatDetailFragment.RETURN_BACK, true);
        intent.putExtra(NewsSummaryFragment.NEWS_SUMMARY_CLICK,true);
        getActivity().startActivity(intent);

        UnreadSubcriptionMRepository.getInstance().removeByAppId(mSession.identifier);
        //更新点击率
        ClickStatisticsManager.INSTANCE.updateClick(mSession.identifier, Type.NEWS_SUMMARY);
    }
    private void doRouteChatDetailView(NewsSummaryRVData newsSummaryRVData,App mApp){
        EntrySessionRequest entrySessionRequest = EntrySessionRequest
                .newRequest(SessionType.Service, mApp)
                .setOrgId(mApp.mOrgId);
        ChatSessionDataWrap.getInstance().entrySession(entrySessionRequest);
        Intent intent = ChatDetailActivity.getIntent(getActivity(), newsSummaryRVData.getChatId());
        intent.putExtra(ChatDetailFragment.RETURN_BACK, true);
        intent.putExtra(NewsSummaryFragment.NEWS_SUMMARY_CLICK,true);
        getActivity().startActivity(intent);

        UnreadSubcriptionMRepository.getInstance().removeByAppId(mApp.mAppId);
        //更新点击率
        ClickStatisticsManager.INSTANCE.updateClick(mApp.mAppId, Type.NEWS_SUMMARY);
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

    private void search(String value) {
        if (StringUtils.isEmpty(value)) {
            emptyResult();
            return;
        }
        adapterList.clear();
        searchContent(value);
        searchService(value);
    }

    @SuppressLint("StaticFieldLeak")
    private void searchContent(String value){
        new AsyncTask<String, Double, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                adapterContentList.clear();
                List<NewsSummaryPostMessage> dataList = MessageAppRepository.getInstance().search(getContext(),value);
                ArrayList<NewsSummaryRVData> rvDataList = new ArrayList<>();
                for (NewsSummaryPostMessage newsSummaryPostMessage : dataList ) {
                    NewsSummaryRVData newsSummaryRVData = new NewsSummaryRVData();
                    newsSummaryRVData.setType(NewsSummaryRVData.SEARCH_CONTENT);
                    newsSummaryRVData.setChatPostMessage(newsSummaryPostMessage.getChatPostMessage());
                    newsSummaryRVData.setChatId(newsSummaryPostMessage.getChatId());
                    newsSummaryRVData.setSearchValue(value);
                    newsSummaryRVData.setShowTitle(false);
                    rvDataList.add(newsSummaryRVData);
                }
                Collections.sort(rvDataList, (t1, t2) -> Long.compare(Objects.requireNonNull(t2.getChatPostMessage()).deliveryTime, Objects.requireNonNull(t1.getChatPostMessage()).deliveryTime));
                if(!rvDataList.isEmpty()){
                    rvDataList.get(0).setShowTitle(true);
                }
                adapterContentList.addAll(rvDataList);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(adapterList.size() >= adapterServiceList.size()){
                    adapterList.addAll(adapterServiceList.size(),adapterContentList);
                }else {
                    adapterList.addAll(0,adapterContentList);
                }
                showResult();
                nsRvAdapter.notifyDataSetChanged();
            }
        }.executeOnExecutor(DbThreadPoolExecutor.getInstance());
    }

    private void searchService(String value){
        //搜索应用
        AppDaoService.getInstance().searchApps("", value,null, (searchKey1, appList) -> {
            adapterServiceList.clear();
            for (int i=0;i<appList.size();i++) {
                if(appList.get(i) instanceof ServiceApp) {
                    TextChatMessage textChatMessage = new TextChatMessage();
                    textChatMessage.mDisplayName = appList.get(i).mAppName;
                    NewsSummaryRVData newsSummaryRVData = new NewsSummaryRVData();
                    newsSummaryRVData.setChatId(appList.get(i).mAppId);
                    newsSummaryRVData.setType(NewsSummaryRVData.SEARCH_SERVICE);
                    newsSummaryRVData.setChatPostMessage(textChatMessage);
                    if(i == 0){
                        newsSummaryRVData.setShowTitle(true);
                    }else{
                        newsSummaryRVData.setShowTitle(false);
                    }
                    adapterServiceList.add(newsSummaryRVData);
                }
            }
            adapterList.addAll(0,adapterServiceList);
            showResult();
            nsRvAdapter.notifyDataSetChanged();

           new Handler().postDelayed(() -> {
               if(adapterServiceList.size() <= 0 && adapterContentList.size() <= 0){
                   noResult();
               }
           }, 500);
        });
    }

    private void emptyResult() {
        adapterList.clear();
        nsRvAdapter.notifyDataSetChanged();
        mRlSearchWelcome.setVisibility(View.VISIBLE);
        mVSearchNoResult.setVisibility(View.GONE);
        mRlSearchResult.setVisibility(View.GONE);
    }

    private void showResult(){
        mVSearchNoResult.setVisibility(View.GONE);
        mRlSearchResult.setVisibility(View.VISIBLE);
        mRlSearchWelcome.setVisibility(View.GONE);
    }

    private void noResult(){
        mVSearchNoResult.setVisibility(View.VISIBLE);
        mRlSearchResult.setVisibility(View.GONE);
        mRlSearchWelcome.setVisibility(View.GONE);
    }

}
