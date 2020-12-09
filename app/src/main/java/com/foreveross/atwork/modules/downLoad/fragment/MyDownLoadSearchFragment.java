package com.foreveross.atwork.modules.downLoad.fragment;

import android.app.Activity;
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
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.listener.TextWatcherAdapter;
import com.foreveross.atwork.modules.downLoad.adapter.DownloadSearchResultAdapter;
import com.foreveross.atwork.modules.file.dao.RecentFileDaoService;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkUtil;

import java.util.List;
import java.util.UUID;

import static com.foreveross.atwork.modules.search.fragment.SearchFragment.ACTION_HANDLE_TOAST_INPUT;

/**
 * Created by wuzejie on 2020/1/14.
 * Description:我的下载的搜索页面
 */
public class MyDownLoadSearchFragment extends BackHandledFragment {

    private ImageView mBackView;
    private Activity mActivity;
    private View mSearchHistoryView;
    private ListView mSearchHistoryListView;
    private TextView mEmptyHistoryText;
    private ListView mSearchResultListView;
    private ImageView mImgNoResult;
    private TextView mTvNoResult;
    private AutoCompleteTextView mSearchEditText;
    private ImageView mCancelView;

    private  DownloadFileDetailFragment mDownloadFileDetailFragment;

    private Handler mHandler = new Handler();
    //Begin:开始处理搜索的逻辑
    private SearchRunnable mGlobalSearchRunnable;
    private DownloadSearchResultAdapter mSearchResultAdapter;
    private String mSearchKey;

    private void search(String value) {
        mSearchKey = UUID.randomUUID().toString();

        if (StringUtils.isEmpty(value)) {
            mSearchResultAdapter.clearData();
            mTvNoResult.setVisibility(View.GONE);
            mImgNoResult.setVisibility(View.GONE);
            mSearchResultListView.setVisibility(View.VISIBLE);
            mSearchHistoryListView.setVisibility(View.VISIBLE);
            return;
        }
        mGlobalSearchRunnable = new SearchRunnable(mSearchKey, value);
        mHandler.postDelayed(mGlobalSearchRunnable, 800);
    }

    private void handleUiAfterSearch(List<FileData> sourceList) {
        mSearchHistoryView.setVisibility(View.GONE);
        if (ListUtil.isEmpty(sourceList)) {
            mTvNoResult.setVisibility(View.VISIBLE);
            mImgNoResult.setVisibility(View.VISIBLE);
            mSearchResultListView.setVisibility(View.GONE);
            return;
        }

        mTvNoResult.setVisibility(View.GONE);
        mImgNoResult.setVisibility(View.GONE);
        mSearchResultListView.setVisibility(View.VISIBLE);
        mSearchResultAdapter.setResultList(sourceList, mSearchEditText.getText().toString());
    }

    public class SearchRunnable implements Runnable {

        private String mSearchKey;

        private String mSearchValue;

        public SearchRunnable(String searchKey, String searchValue) {
            this.mSearchKey = searchKey;
            this.mSearchValue = searchValue;
        }

        @Override
        public void run() {
            //searchKey一样时,才进行搜索
            if (mSearchKey.equals(this.mSearchKey)) {
                mSearchResultAdapter.clearData();
                RecentFileDaoService.getInstance().searchRecentFiles(this.mSearchValue, new RecentFileDaoService.OnSearchRecentFilesListener() {
                    @Override
                    public void onSearchRecentFilesResult(List<FileData> fileDataList) {

                        //updateHistoryKey(mSearchValue);TODO：更新历史记录的功能（之后再做）
                        handleUiAfterSearch(fileDataList);
                    }
                });
            }
        }
    }

    //End:处理搜索的逻辑

    private boolean mShouldToastInput = true;
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
    private void registerBroadcast() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mBroadcastReceiver, new IntentFilter(ACTION_HANDLE_TOAST_INPUT));
    }
    private void unRegisterBroadcast() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_download_search, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerListener();
        registerBroadcast();
        initData();

        mSearchResultAdapter = new DownloadSearchResultAdapter(mActivity);
        mSearchResultListView.setAdapter(mSearchResultAdapter);
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

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterBroadcast();
    }

    @Override
    protected void findViews(View view) {
        mSearchResultListView = view.findViewById(R.id.search_list_view);
        mSearchEditText = view.findViewById(R.id.title_bar_chat_search_key);
        mBackView = view.findViewById(R.id.title_bar_chat_search_back);
        mTvNoResult = view.findViewById(R.id.tv_no_result);
        mImgNoResult = view.findViewById(R.id.img_no_result);
        mCancelView = view.findViewById(R.id.title_bar_chat_search_cancel);
        mSearchHistoryView = view.findViewById(R.id.search_history_view);
        mSearchHistoryListView = mSearchHistoryView.findViewById(R.id.search_history_list);
        mEmptyHistoryText = mSearchHistoryView.findViewById(R.id.empty_history_view);
        mEmptyHistoryText.setVisibility(View.GONE);
    }

    private void registerListener() {

        mSearchResultListView.setOnItemClickListener((parent, view, position, id) -> {
            FileData fileData = mSearchResultAdapter.getItem(position);

            FragmentActivity fragmentActivity = (FragmentActivity)mActivity;
            mDownloadFileDetailFragment = new DownloadFileDetailFragment();
            mDownloadFileDetailFragment.initBundle(fileData);
            mDownloadFileDetailFragment.show(fragmentActivity.getSupportFragmentManager(), "DOWNLOAD_FILE_DETAIL_FRAGMENT");
        });


        mCancelView.setOnClickListener(v -> {
            mSearchEditText.setText(StringUtils.EMPTY);
            mSearchResultAdapter.clearData();
            mSearchHistoryView.setVisibility(View.VISIBLE);
            loadHistorySearchData();
        });

        mBackView.setOnClickListener(v -> cancelFragment());

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

        mEmptyHistoryText.setOnClickListener(view -> {
//            DropboxSearchHistoryData.getInstance().clearDropboxHistoryData(mActivity, LoginUserInfo.getInstance().getLoginUserId(mActivity));
//            mSearchHistoryView.setVisibility(View.GONE);
        });
        mSearchHistoryListView.setOnItemClickListener((parent, view, position, id) -> {
//            String searchValue = mSearchHistoryAdapter.getItem(position);
//            mSearchEditText.setText(searchValue);
//            mSearchEditText.setSelection(searchValue.length());
        });

    }

    private void cancelFragment() {
        AtworkUtil.hideInput(getActivity(), mSearchEditText);
        onBackPressed();
    }

    private void initData() {
        mSearchEditText.setHint(R.string.search_hint);

        loadHistorySearchData();
    }

    /**
     * 加载搜索历史记录
     */
    private void loadHistorySearchData() {

    }

    @Override
    protected boolean onBackPressed() {
        mActivity.finish();
        return false;
    }
}
