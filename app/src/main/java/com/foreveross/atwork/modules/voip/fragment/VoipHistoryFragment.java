package com.foreveross.atwork.modules.voip.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.user.SelectedContactList;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingGroup;
import com.foreveross.atwork.modules.group.activity.UserSelectActivity;
import com.foreveross.atwork.modules.group.module.UserSelectControlAction;
import com.foreveross.atwork.modules.search.component.SearchHeadView;
import com.foreveross.atwork.modules.search.fragment.SearchFragment;
import com.foreveross.atwork.modules.search.model.SearchAction;
import com.foreveross.atwork.modules.search.model.SearchContent;
import com.foreveross.atwork.modules.voip.activity.VoipSelectModeActivity;
import com.foreveross.atwork.modules.voip.adapter.VoipHistoryRecycleAdapter;
import com.foreveross.atwork.modules.voip.loader.VoipMeetingRecordListLoader;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ErrorHandleUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dasunsy on 16/7/13.
 */
public class VoipHistoryFragment extends BackHandledFragment implements LoaderManager.LoaderCallbacks<List<VoipMeetingGroup>>{

    private static final String TAG = VoipHistoryFragment.class.getSimpleName();

    public static final String ACTION_REFRESH = "action_refresh";

    //创建语音会议 code
    public static final int CREATE_VOIP_CONF = 1;

    private ImageView mIvBack;
    private TextView mTvTitle;
    private SearchHeadView mSearchHeaderView;
    private RecyclerView mRvHistory;
    private TextView mTvStartConf;

    private VoipHistoryRecycleAdapter mHistoryRecycleAdapter;
    private List<VoipMeetingGroup> mHistoryList;


    private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(ACTION_REFRESH.equals(intent.getAction())) {
                loadData();
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_REFRESH);

        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mRefreshBroadcastReceiver, filter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voip_history, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerListener();
        initViews();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mRefreshBroadcastReceiver);
    }

    private void loadData() {
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (CREATE_VOIP_CONF == requestCode && Activity.RESULT_OK == resultCode) {
            AtworkApplicationLike.getLoginUser(new UserAsyncNetService.OnQueryUserListener() {
                @Override
                public void onSuccess(@NonNull User user) {
                    List<ShowListItem> contacts = SelectedContactList.getContactList();
                    contacts.add(user);
                    Intent intent = VoipSelectModeActivity.getIntent(getActivity(), (ArrayList<? extends ShowListItem>) contacts);
                    startActivity(intent);
                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    ErrorHandleUtil.handleBaseError(errorCode, errorMsg);
                }
            });

        }
    }

    @Override
    protected void findViews(View view) {
        mIvBack = view.findViewById(R.id.title_bar_common_back);
        mTvTitle = view.findViewById(R.id.title_bar_common_title);
        mSearchHeaderView = view.findViewById(R.id.search_bar);
        mTvStartConf = view.findViewById(R.id.tv_start_voip_conf);
        mRvHistory = view.findViewById(R.id.rv_voip_history);
    }

    private void initViews() {
        mTvTitle.setText(R.string.label_voip_meeting_chat_pop);

        mSearchHeaderView.setHint(R.string.voip_search_tip);
    }

    private void initData() {
        mHistoryList = new ArrayList<>();
        //list布局
        LinearLayoutManager linearLayoutMgr = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mHistoryRecycleAdapter = new VoipHistoryRecycleAdapter(getActivity(), mHistoryList);

        mRvHistory.setLayoutManager(linearLayoutMgr);
        mRvHistory.setAdapter(mHistoryRecycleAdapter);

    }
    private void registerListener() {
        mIvBack.setOnClickListener((v)-> finish());

        mTvStartConf.setOnClickListener((v)-> {
            if(AtworkUtil.isSystemCalling()) {
                AtworkToast.showResToast(R.string.alert_is_handling_system_call);
                return;
            }

            if (VoipHelper.isHandlingVoipCall()) {
                AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);
            } else {
                List<ShowListItem> notAllowContactList = new ArrayList<>();
                AtworkApplicationLike.getLoginUser(new UserAsyncNetService.OnQueryUserListener() {
                    @Override
                    public void onSuccess(@NonNull User user) {
                        notAllowContactList.add(user);

                        UserSelectControlAction userSelectControlAction = new UserSelectControlAction();
                        userSelectControlAction.setSelectMode(UserSelectActivity.SelectMode.SELECT);
                        userSelectControlAction.setSelectAction(UserSelectActivity.SelectAction.VOIP);
                        userSelectControlAction.setSelectedContacts(notAllowContactList);
                        userSelectControlAction.setFromTag(TAG);

                        Intent intent = UserSelectActivity.getIntent(getActivity(), userSelectControlAction);

                        startActivityForResult(intent, CREATE_VOIP_CONF);
                    }

                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        ErrorHandleUtil.handleBaseError(errorCode, errorMsg);
                    }
                });

            }

        });


        mSearchHeaderView.getEditTextSearch().setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                SearchFragment searchFragment = new SearchFragment();
                searchFragment.setSearchAction(SearchAction.VOIP);

                searchFragment.addSearch(SearchContent.SEARCH_USER);
                searchFragment.show(getActivity().getSupportFragmentManager(), "SearchDialog");
                mSearchHeaderView.getEditTextSearch().clearFocus();
            }
        });

    }

    public static void refresh() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(ACTION_REFRESH));

    }

    @Override
    protected boolean onBackPressed() {
        finish();
        return false;
    }

    @Override
    public Loader<List<VoipMeetingGroup>> onCreateLoader(int id, Bundle args) {
        return new VoipMeetingRecordListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<VoipMeetingGroup>> loader, List<VoipMeetingGroup> data) {
        mHistoryList.clear();
        mHistoryList.addAll(data);

        mHistoryRecycleAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<VoipMeetingGroup>> loader) {

    }
}
