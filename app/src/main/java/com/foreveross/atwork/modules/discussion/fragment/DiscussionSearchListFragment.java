package com.foreveross.atwork.modules.discussion.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.db.daoService.DiscussionDaoService;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.listener.TextWatcherAdapter;
import com.foreveross.atwork.modules.discussion.activity.SelectDiscussionListActivity;
import com.foreveross.atwork.modules.discussion.adapter.DiscussionSearchListAdapter;
import com.foreveross.atwork.modules.discussion.model.DiscussionSelectControlAction;
import com.foreveross.atwork.modules.group.module.SelectToHandleAction;
import com.foreveross.atwork.modules.group.service.SelectToHandleActionService;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by dasunsy on 2018/1/16.
 */

public class DiscussionSearchListFragment extends BackHandledFragment {

    private EditText mSearchEditText;
    private View mBackView;
    private ImageView mCancelView;
    private RelativeLayout mRlResult;
    private RecyclerView mRvResult;
    private LinearLayout mLlNoResult;

    private DiscussionSearchListAdapter mDiscussionSearchListAdapter;
    private String mSearchKey;
    private GlobalSearchRunnable mGlobalSearchRunnable;

    private Handler mHandler = new Handler();

    private DiscussionSelectControlAction mDiscussionSelectControlAction;
    private SelectToHandleAction mSelectToHandleAction;
    private int mMaxSelect;
    private Set<String> mSelectedDiscussionIdSet = new HashSet<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_discussion, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initAdapter();
        registerListener();

        initData();
        initRefreshUI();

    }

    private void initData() {
        Bundle bundle = getArguments();
        if(null != bundle) {
            mDiscussionSelectControlAction = getArguments().getParcelable(SelectDiscussionListActivity.DATA_DISCUSSION_SELECT_CONTROL_ACTION);

            if (null != mDiscussionSelectControlAction) {
                List<String> discussionIdListPreSelected = mDiscussionSelectControlAction.getDiscussionIdListPreSelected();
                if (null != discussionIdListPreSelected) {
                    mSelectedDiscussionIdSet.addAll(discussionIdListPreSelected);
                }

                mSelectToHandleAction = mDiscussionSelectControlAction.getSelectToHandleAction();
                mMaxSelect = mDiscussionSelectControlAction.getMax();
            }
        }
    }

    private void initAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRvResult.setLayoutManager(linearLayoutManager);

        mDiscussionSearchListAdapter = new DiscussionSearchListAdapter(getActivity());
        mRvResult.setAdapter(mDiscussionSearchListAdapter);
    }

    @Override
    protected void findViews(View view) {
        mBackView = view.findViewById(R.id.title_bar_chat_search_back);
        mSearchEditText = view.findViewById(R.id.title_bar_chat_search_key);
        mCancelView = view.findViewById(R.id.title_bar_chat_search_cancel);
        mRlResult = view.findViewById(R.id.rl_search_result);
        mRvResult = view.findViewById(R.id.rv_result);
        mLlNoResult = view.findViewById(R.id.ll_no_result);
    }

    private void initRefreshUI() {
        mSearchEditText.setHint(R.string.search_discussion);

    }


    private void registerListener() {
        mBackView.setOnClickListener(v -> {
            onBackPressed();
        });

        mCancelView.setOnClickListener(v -> {
            mSearchEditText.setText(StringUtils.EMPTY);
            emptyResult();
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



        mDiscussionSearchListAdapter.setOnHandleOnItemCLickListener(discussion -> {

            if(!discussion.isSelect() && checkIsToThreshold(discussion)){
                return;
            }


            discussion.select(!discussion.isSelect());

            if(discussion.isSelect()) {
                mSelectedDiscussionIdSet.add(discussion.getId());

                toast(R.string.add_successfully);

            } else {
                mSelectedDiscussionIdSet.remove(discussion.getId());

            }

            mDiscussionSearchListAdapter.notifyDataSetChanged();

            SelectDiscussionListActivity.handleSelect(discussion);
        });


        mRvResult.setOnTouchListener((v, event) -> {
            AtworkUtil.hideInput(getActivity(), mSearchEditText);
            return false;
        });

    }

    private void emptyResult() {
        mDiscussionSearchListAdapter.clearData();
        mRlResult.setVisibility(View.VISIBLE);
        mLlNoResult.setVisibility(View.GONE);


    }

    private void search(String value) {
        mSearchKey = UUID.randomUUID().toString();
        if (StringUtils.isEmpty(value)) {
            emptyResult();
            return;
        }

        mGlobalSearchRunnable = new GlobalSearchRunnable(mSearchKey, value);
//        mLlRoot.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        mHandler.postDelayed(mGlobalSearchRunnable, 800);
    }


    @Override
    protected boolean onBackPressed() {
        finish();
        return false;
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
            if (searchKey.equals(DiscussionSearchListFragment.this.mSearchKey)) {
                mDiscussionSearchListAdapter.clearData();

                //搜索群组
                DiscussionDaoService.getInstance().searchDiscussion(searchKey, searchValue, (searchKey1, discussionList) -> {
                    if (searchKey1.equals(DiscussionSearchListFragment.this.mSearchKey)) {
                        launderDiscussionList(discussionList);
                        refreshResultData(discussionList);
                        handleUiAfterSearch(discussionList);

                    }
                });


            }
        }

        public void launderDiscussionList(List<Discussion> discussionList) {
            for(Discussion discussion : discussionList) {
                if(mSelectedDiscussionIdSet.contains(discussion.getId())) {
                    discussion.select(true);
                }
            }
        }

        public void refreshResultData(List<Discussion> discussionList) {
            mDiscussionSearchListAdapter.setDiscussionList(discussionList);
        }

        private void handleUiAfterSearch(List<Discussion> discussionList) {

            if (ListUtil.isEmpty(discussionList)) {
                mLlNoResult.setVisibility(View.VISIBLE);
                mRlResult.setVisibility(View.GONE);


            } else {
                mLlNoResult.setVisibility(View.GONE);
                mRlResult.setVisibility(View.VISIBLE);

            }
        }


    }

    public interface OnHandleOnItemCLickListener {
        void onItemClick(Discussion  discussion);
    }


    private boolean checkIsToThreshold(Discussion discussion) {
        if(isSelectToActionMode()) {

            boolean isToThreshold = SelectToHandleActionService.INSTANCE.checkIsToThreshold(mSelectToHandleAction, discussion);
            return isToThreshold;
        }

        boolean isToThreshold = mSelectedDiscussionIdSet.size() + 1 > mMaxSelect;

        if(isToThreshold) {
            toast(mDiscussionSelectControlAction.getMaxTip());
        }

        return isToThreshold;
    }
    private boolean isSelectToActionMode() {
        return null != mSelectToHandleAction;
    }
}
