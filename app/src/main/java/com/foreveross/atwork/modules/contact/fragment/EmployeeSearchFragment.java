package com.foreveross.atwork.modules.contact.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.component.BasicDialogFragment;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.listener.TextWatcherAdapter;
import com.foreveross.atwork.manager.EmployeeManager;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.manager.model.ExpandEmpTreeAction;
import com.foreveross.atwork.modules.contact.activity.PersonalInfoActivity;
import com.foreveross.atwork.modules.contact.adapter.EmployeeListSearchAdapter;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.theme.manager.SkinMaster;

import java.util.List;
import java.util.UUID;

/**
 * Created by lingen on 15/4/16.
 * Description:
 */
public class EmployeeSearchFragment extends BasicDialogFragment {

    private static final String TAG = EmployeeSearchFragment.class.getSimpleName();

    public static final String DATA_ORG_CODE = "data_org_code";

    private View mLlRoot;

    private ListView mSearchListView;

    private EmployeeListSearchAdapter mEmployeeListSearchAdapter;

    private EditText mSearchEditText;

    private TextView mTvSearchBtn;

    private String mSearchId;

    private ImageView mCancelView;

    private ImageView mImgNoResult;

    private TextView mTvNoResult;
    private ImageView mIvBack;

    private String mOrgCode;

    /**
     * 用以控制键盘是否应该在 onResume 弹出, 现在在点击item 进去具体搜索内容再返回来时不会弹出键盘,
     * 这样体验比较好, 用户比较倾向于不希望键盘挡住
     * */
    private boolean mShouldToastInput = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //该方法需要放在onViewCreated比较合适, 若在 onStart 在部分机型(如:小米3)会出现闪烁的情况
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mLlRoot = view.findViewById(R.id.ll_root);
        mIvBack = view.findViewById(R.id.title_bar_chat_search_back);
        mSearchListView = view.findViewById(R.id.search_list_view);
        mSearchEditText = view.findViewById(R.id.title_bar_chat_search_key);
        mCancelView = view.findViewById(R.id.title_bar_chat_search_cancel);
        mTvNoResult = view.findViewById(R.id.tv_no_result);
        mImgNoResult = view.findViewById(R.id.img_no_result);
        mTvSearchBtn = view.findViewById(R.id.title_ba_search_label);

        mSearchEditText.setHint(R.string.action_search);

        mSearchListView.setDivider(null);

        SkinMaster.getInstance().changeTheme((ViewGroup) view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        makeBgTransparent();
        initData();
    }

    private void makeBgTransparent() {
        getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent_70);
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
    public void onDetach() {
        super.onDetach();

    }


    private void initData() {
        mOrgCode = getArguments().getString(DATA_ORG_CODE);
        registerListener();
        mEmployeeListSearchAdapter = new EmployeeListSearchAdapter(getActivity());
    }

    private void registerListener() {

        mSearchListView.setOnTouchListener((v, event) -> {
            AtworkUtil.hideInput(getActivity(), mSearchEditText);
            return false;
        });

        mCancelView.setOnClickListener(v -> {
            mSearchEditText.setText(StringUtils.EMPTY);
            mEmployeeListSearchAdapter.clear();
        });

        mSearchListView.setOnItemClickListener((parent, view, position, id) -> {

            Object object = parent.getItemAtPosition(position);
            if (object instanceof Employee) {
                mShouldToastInput = false;

                final Employee employee = (Employee) object;
                UserManager.getInstance().queryUserByUserId(getActivity(), employee.userId, employee.domainId, new UserAsyncNetService.OnQueryUserListener() {
                    @Override
                    public void onSuccess(User user) {
                        if(null != user) {
                            getActivity().startActivity(PersonalInfoActivity.getIntent(getActivity().getApplicationContext(), user));

                        }
                    }

                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        ErrorHandleUtil.handleError(errorCode, errorMsg);
                    }

                });
            }


        });
        mSearchEditText.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                mSearchId = UUID.randomUUID().toString();

                String searchValue = s.toString();
                if (StringUtils.isEmpty(searchValue)) {
                    mEmployeeListSearchAdapter.clear();
                    mCancelView.setVisibility(View.GONE);
                    mSearchListView.setVisibility(View.GONE);
                    mTvNoResult.setVisibility(View.GONE);
                    mImgNoResult.setVisibility(View.GONE);
                    hideTvSearchBtn();
                    makeBgTransparent();

                    return;
                }

                showTvSearchBtn();
                mCancelView.setVisibility(View.VISIBLE);

                if(!shouldSearch(searchValue)) {
                    return;
                }

                search(searchValue);
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

        mTvSearchBtn.setOnClickListener(v -> {
            search(mSearchEditText.getText().toString());

        });

        mIvBack.setOnClickListener(v -> cancelFragment());

        mLlRoot.setOnClickListener(v -> {
            cancelFragment();

        });
    }

    private void search(String searchValue) {
        getDialog().getWindow().setBackgroundDrawableResource(R.color.white);

        new Handler().postDelayed(new SearchRunnable(mSearchId, searchValue), 800);
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

    private void hideTvSearchBtn() {
        mTvSearchBtn.setVisibility(View.GONE);
    }

    private void showTvSearchBtn() {
        if(AtworkConfig.SEARCH_CONFIG.isShowSearchBtnInMainBusiness()) {
            mTvSearchBtn.setVisibility(View.VISIBLE);
        }
    }

    private void toastInput() {
        mSearchEditText.setFocusable(true);
        //延迟处理 避免View没绘制好 软键盘无法弹出
        getActivity().getWindow().getDecorView().postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                mSearchEditText.requestFocus();
                imm.showSoftInput(mSearchEditText, InputMethodManager.SHOW_FORCED);
            }
        }, 100);
    }

    private void cancelFragment() {
        AtworkUtil.hideInput(getActivity(), mSearchEditText);
        dismiss();
    }

    /**
     * 搜索线程
     */
    public class SearchRunnable implements Runnable {

        private String tmpSearchId;

        private String searchKeyValue;

        public SearchRunnable(String searchId, String searchKeyValue) {
            this.tmpSearchId = searchId;
            this.searchKeyValue = searchKeyValue;
        }

        @Override
        public void run() {
            if (this.tmpSearchId.equals(mSearchId)) {
                mSearchListView.setAdapter(null);

                mEmployeeListSearchAdapter.setKey(this.searchKeyValue);

                ExpandEmpTreeAction expandEmpTreeAction = ExpandEmpTreeAction.newExpandEmpTreeAction()
                        .setSelectMode(false);

                //SEARCH
                EmployeeManager.getInstance().searchEmployeesRemote(BaseApplicationLike.baseContext, mOrgCode, mSearchId, searchKeyValue, expandEmpTreeAction, new EmployeeManager.RemoteSearchEmployeeListListener() {
                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        if(!ErrorHandleUtil.handleBaseError(errorCode, errorMsg)) {
                            //本地搜索
                            searchLocal(expandEmpTreeAction);

                        }
                    }

                    @Override
                    public void onSuccess(String searchKey, List<Employee> employeeList) {
                        handleSearchUI(searchKey, employeeList);
                    }

                });
            }
        }

        /**
         * 本地搜索雇员
         * */
        public void searchLocal(ExpandEmpTreeAction expandEmpTreeAction) {
            EmployeeManager.getInstance().searchEmployeesLocal(BaseApplicationLike.baseContext, mOrgCode, mSearchId, searchKeyValue, expandEmpTreeAction, new EmployeeManager.LocalSearchEmployeeListListener() {
                @Override
                public void onSuccess(String searchKey, List<Employee> employeeList) {
                    handleSearchUI(searchKey, employeeList);
                }

                @Override
                public void onFail() {
                    AtworkToast.showToast(getResources().getString(R.string.contact_search_fail));

                }
            });
        }
    }

    public void handleSearchUI(String searchKey, List<Employee> employeeList) {
        if (!searchKey.equals(mSearchId)) {
            return;
        }
        mEmployeeListSearchAdapter.refreshData(employeeList);
        mSearchListView.setAdapter(mEmployeeListSearchAdapter);

        if (ListUtil.isEmpty(employeeList)) {
            mSearchListView.setVisibility(View.GONE);
            mTvNoResult.setVisibility(View.VISIBLE);
            mImgNoResult.setVisibility(View.VISIBLE);
        } else {
            mSearchListView.setVisibility(View.VISIBLE);
            mTvNoResult.setVisibility(View.GONE);
            mImgNoResult.setVisibility(View.GONE);
        }
    }

}
