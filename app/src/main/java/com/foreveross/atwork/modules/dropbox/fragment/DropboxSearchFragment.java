package com.foreveross.atwork.modules.dropbox.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.dropbox.DropboxConfig;
import com.foreveross.atwork.infrastructure.shared.DropboxSearchHistoryData;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.listener.TextWatcherAdapter;
import com.foreveross.atwork.manager.DropboxManager;
import com.foreveross.atwork.modules.dropbox.activity.DropboxSearchActivity;
import com.foreveross.atwork.modules.dropbox.activity.FileDetailActivity;
import com.foreveross.atwork.modules.dropbox.adapter.DropboxSearchHistoryAdapter;
import com.foreveross.atwork.modules.dropbox.adapter.DropboxSearchResultAdapter;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static com.foreveross.atwork.modules.dropbox.activity.DropboxBaseActivity.KEY_INTENT_SEARCH_DIR_SELECT;
import static com.foreveross.atwork.modules.search.fragment.SearchFragment.ACTION_HANDLE_TOAST_INPUT;

/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 * Created by reyzhang22 on 16/9/9.
 */
public class DropboxSearchFragment extends BackHandledFragment {

    public static final String REPLACE_COMMA_STR = "!!=REPLACE_COMMA=!!";

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

    private boolean mShouldToastInput = true;

    private DropboxSearchResultAdapter mSearchResultAdapter;

    private DropboxSearchHistoryAdapter mSearchHistoryAdapter;

    private boolean mIsInput;

    private Handler mHandler = new Handler();

    private SearchRunnable mGlobalSearchRunnable;

    private String mSearchKey;

    private String mDomainId;
    private Dropbox.SourceType mSourceType;
    private String mSourceId;

    private DropboxConfig mDropboxConfig = new DropboxConfig();

    private String[] mArrays;

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    protected boolean onBackPressed() {
        mActivity.finish();
        return false;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置Dialog占用全屏
//        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dropbox_search, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerListener();
        registerBroadcast();
        handleBundle();
        initData();

        mSearchResultAdapter = new DropboxSearchResultAdapter(getActivity());
        mSearchResultListView.setAdapter(mSearchResultAdapter);

    }

    private void handleBundle() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        mDomainId = bundle.getString(DropboxSearchActivity.KEY_INTENT_DOMAIN_ID);
        mSourceId = bundle.getString(DropboxSearchActivity.KEY_INTENT_SOURCE_ID);
        mSourceType = (Dropbox.SourceType) bundle.getSerializable(DropboxSearchActivity.KEY_INTENT_SOURCE_TYPE);
        mDropboxConfig = (DropboxConfig)bundle.getSerializable(DropboxSearchActivity.KEY_INTENT_DROPBOX_CONFIG);
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
    }

    private void initData() {
        mSearchEditText.setHint(R.string.search_hint);

        loadHistorySearchData();
    }

    /**
     * 加载搜索历史记录
     */
    private void loadHistorySearchData() {
        String historyData = DropboxSearchHistoryData.getInstance().getDropboxSearchHistoryData(mActivity, LoginUserInfo.getInstance().getLoginUserId(mActivity));
         mArrays = historyData.split(",");
        if (mArrays.length == 0 || TextUtils.isEmpty(mArrays[0])) {
            mSearchHistoryView.setVisibility(View.GONE);
            return;
        }
        List<String> historys = new ArrayList<>();
        int size = (mArrays.length > 10) ? 10 : mArrays.length;
        for (int i = 0; i < size; i++) {
            String value = mArrays[i];
            if (value.contains(REPLACE_COMMA_STR)) {
                value = value.replaceAll(REPLACE_COMMA_STR, ",");
            }
            historys.add(value);
        }
        mSearchHistoryAdapter = null;
        if (mSearchHistoryAdapter == null) {
            mSearchHistoryAdapter = new DropboxSearchHistoryAdapter(mActivity, historys);
        }
        mSearchHistoryListView.setAdapter(mSearchHistoryAdapter);
        mSearchHistoryAdapter.notifyDataSetChanged();
    }


    private void registerBroadcast() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mBroadcastReceiver, new IntentFilter(ACTION_HANDLE_TOAST_INPUT));
    }

    private void unRegisterBroadcast() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mBroadcastReceiver);
    }

    private void registerListener() {

        mSearchResultListView.setOnItemClickListener((parent, view, position, id) -> {
            Dropbox dropbox = mSearchResultAdapter.getItem(position);
            if (dropbox.mIsDir) {
                Intent intent = new Intent();
                intent.putExtra(KEY_INTENT_SEARCH_DIR_SELECT, dropbox);
                mActivity.setResult(RESULT_OK, intent);
                mActivity.finish();
                return;
            }
            Intent intent = FileDetailActivity.getIntent(mActivity, dropbox, mDropboxConfig);
            startActivity(intent);
            mActivity.finish();
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
            DropboxSearchHistoryData.getInstance().clearDropboxHistoryData(mActivity, LoginUserInfo.getInstance().getLoginUserId(mActivity));
            mSearchHistoryView.setVisibility(View.GONE);
        });

        mSearchHistoryListView.setOnItemClickListener((parent, view, position, id) -> {
            String searchValue = mSearchHistoryAdapter.getItem(position);
            mSearchEditText.setText(searchValue);
            mSearchEditText.setSelection(searchValue.length());
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
        onBackPressed();
    }

    private void updateHistoryKey(String key) {
        if (key.contains(",")) {
            key = key.replaceAll(",", REPLACE_COMMA_STR);
        }
        String userId = LoginUserInfo.getInstance().getLoginUserId(mActivity);
        String historyData = DropboxSearchHistoryData.getInstance().getDropboxSearchHistoryData(mActivity, userId);
        StringBuilder builder = new StringBuilder(key);
        builder.append("," + historyData);
        if (!TextUtils.isEmpty(key)) {
            boolean inArray = false;
            if (mArrays != null) {
                int size = (mArrays.length > 10) ? 10 : mArrays.length;
                for (int i = 0; i < size; i++) {
                    if (key.equalsIgnoreCase(mArrays[i])) {
                        inArray = true;
                        break;
                    }
                }
            }
            if (!inArray) {
                DropboxSearchHistoryData.getInstance().setDropboxSearchHistoryData(mActivity, userId, builder.toString());
            }

        }
    }

    private void search(String value) {
        mSearchKey = UUID.randomUUID().toString();
        mIsInput = true;
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


    private void handleUiAfterSearch(List<Dropbox> sourceList) {
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
            if (mSearchKey.equals(DropboxSearchFragment.this.mSearchKey)) {
                mSearchResultAdapter.clearData();
                DropboxManager.getInstance().searchDropboxByName(mActivity, mDomainId, mSourceType, mSourceId, mSearchKey, mSearchValue, (key, dropboxList) -> {
                    if (key.equalsIgnoreCase(DropboxSearchFragment.this.mSearchKey)) {
                        updateHistoryKey(mSearchValue);
                        handleUiAfterSearch(dropboxList);
                    }
                });
            }

        }


    }

}
