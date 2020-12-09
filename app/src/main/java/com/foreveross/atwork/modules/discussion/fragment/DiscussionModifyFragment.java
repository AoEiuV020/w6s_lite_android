package com.foreveross.atwork.modules.discussion.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.discussion.DiscussionAsyncNetService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ErrorHandleUtil;


public class DiscussionModifyFragment extends BackHandledFragment {

    private static final String TAG = DiscussionModifyFragment.class.getSimpleName();


    public static final String MODIFY_TYPE = "GROUP_MODIFY_TYPE";

    public static final String DISCUSSION_INFO = "DISCUSSION_INFO";

    private int MAX_GROUP_NAME_LENGTH = 30;

    private int MAX_GROUP_DETAIL_LENGTH = 150;

    //=========标题栏 BEGIN=======

    //返回VIEW
    private ImageView mBackView;

    //确定按钮
    private Button mOkButton;

    //标题VIEW
    private TextView mTitleView;

    //=========标题栏 END=======

    private EditText mModifyText;


    private Discussion mDiscussion;

    private DiscussionModifyType mDiscussionModifyType;

    private String mOriginValue = StringUtils.EMPTY;

    private ImageView mEmptyInputText;

    private TextView mNumberTv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_info_modify, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerListener();
        initData();

        //如果是群主 弹出键盘，可以操作
        if (isDiscussionOwner()) {

            mModifyText.requestFocus();
            mModifyText.postDelayed(() -> AtworkUtil.showInput(mActivity, mModifyText), 300);

        } else {
            mModifyText.setEnabled(false);
            mOkButton.setVisibility(View.GONE);
            mNumberTv.setVisibility(View.GONE);
        }
        refreshView();
    }

    @Override
    protected void findViews(View view) {
        mBackView = view.findViewById(R.id.title_bar_group_info_back);
        mTitleView = view.findViewById(R.id.title_bar_group_info_name);
        mOkButton = view.findViewById(R.id.title_bar_group_info_ok);
        mModifyText = view.findViewById(R.id.group_info_modify_text);
        mEmptyInputText = view.findViewById(R.id.empty_edit_group_text);
        mNumberTv = view.findViewById(R.id.group_info_number_tv);
    }

    private void initData() {
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            mDiscussion = bundle.getParcelable(DISCUSSION_INFO);
            mDiscussionModifyType = (DiscussionModifyType) bundle.getSerializable(MODIFY_TYPE);
        }
    }

    private void registerListener() {

        //返回事件
        mBackView.setOnClickListener(v -> onBackPressed());

        mOkButton.setOnClickListener(v -> {
            String changedValue = mModifyText.getText().toString().trim();
//                if (StringUtils.isEmpty(changedValue)) {
//                    AtworkToast.showToast(getResources().getString(R.string.group_name_not_allow_null));
//                    return;
//                }
            if (TextUtils.isEmpty(changedValue)) {
                return;
            }
            if (DiscussionModifyType.NAME_MODIFY.equals(mDiscussionModifyType)) {
                if (StringUtils.getWordCount(changedValue) > MAX_GROUP_NAME_LENGTH) {
                    AtworkToast.showToast(getResources().getString(R.string.group_name_too_long));
                    return;
                }
                mDiscussion.mName = changedValue;

            } else if (DiscussionModifyType.DETAIL_MODIFY.equals(mDiscussionModifyType)) {
                if (StringUtils.getWordCount(changedValue) > MAX_GROUP_DETAIL_LENGTH) {
                    AtworkToast.showToast(getResources().getString(R.string.group_detail_too_long));
                    return;
                }
                mDiscussion.mIntro = changedValue;
            }

            //没有修改，不进行任何操作
            if (mOriginValue != null && mOriginValue.equals(changedValue)) {
                AtworkUtil.hideInput(mActivity, mModifyText);
                getActivity().finish();
                //界面退出动画
                getActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                return;
            }
            AtworkUtil.hideInput(mActivity, mModifyText);

            modifyDiscussion();
        });

//        mEmptyInputText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mModifyText.setText("");
//            }
//        });

        mModifyText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                mEmptyInputText.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(s.toString().trim())) {
                    mOkButton.setTextColor(getResources().getColor(R.color.common_item_black));
//                    mEmptyInputText.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(s.toString().trim())) {
                    mOkButton.setTextColor(getResources().getColor(R.color.title_bar_rightest_text_gray));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = "";
                if (DiscussionModifyType.NAME_MODIFY.equals(mDiscussionModifyType)) {
                    str = StringUtils.getWordCount(s.toString()) + "/30";
                } else if (DiscussionModifyType.DETAIL_MODIFY.equals(mDiscussionModifyType)) {
                    str = StringUtils.getWordCount(s.toString()) + "/150";
                }
                mNumberTv.setText(str);
            }
        });
    }

    private void modifyDiscussion() {
        DiscussionManager.getInstance().modifyDiscussion(getActivity(), mDiscussion, new DiscussionAsyncNetService.HandledResultListener() {
            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Group, errorCode, errorMsg);
            }

            @Override
            public void success() {
                toast(R.string.modify_group_info_success);
//                ChatSessionDataWrap.getInstance().updateSessionByGroup(group);
                finish();
            }


        });
    }

    private void refreshView() {


        if (DiscussionModifyType.NAME_MODIFY.equals(mDiscussionModifyType)) {
            //如果不是群主
            if (!isDiscussionOwner()) {
                mTitleView.setText(R.string.title_discussion_name);
            } else {
                mTitleView.setText(mDiscussionModifyType.getTitle());
            }

            mNumberTv.setText("0/30");
            mOriginValue = mDiscussion.mName;
            mModifyText.setHint(R.string.hint_title_group_name_modify);
            mModifyText.setText(mOriginValue);
            mModifyText.setSelection(mOriginValue.length());

            //修改群名称时不可换行
//            mModifyText.setSingleLine(true);

            //吃掉换行符的做法
            if (mOriginValue.contains("\n")) {
                mOriginValue = mOriginValue.replace("\n", " ");
                mModifyText.setText(mOriginValue);
            }
            mModifyText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String str = mModifyText.getText().toString();
                    int index = mModifyText.getSelectionStart();
                    if (str.contains("\n")) {
                        String str1 = str.replace("\n", "");
                        mModifyText.setText(str1);
                        if (index > 0) {
                            mModifyText.setSelection(index - 1);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });


        } else if (DiscussionModifyType.DETAIL_MODIFY.equals(mDiscussionModifyType)) {
            mNumberTv.setText("0/150");
            mOriginValue = mDiscussion.mIntro;
            mModifyText.setHint(R.string.hint_title_group_detail_modify);
            mModifyText.setText(mOriginValue);
            if (mOriginValue != null) {
                mModifyText.setSelection(mOriginValue.length());
            }

            //如果不是群主
            if (!isDiscussionOwner()) {
                mTitleView.setText(R.string.title_group_detail);

                if (StringUtils.isEmpty(mModifyText.getText().toString())) {
                    mModifyText.setHint(R.string.hint_no_content);
                }

            } else {
                mTitleView.setText(mDiscussionModifyType.getTitle());
            }


            //修改群简介时可以换行
//            mModifyText.setSingleLine(false);
        }

    }

    private boolean isDiscussionOwner() {
        if (null != mDiscussion.mOwner) {
            return LoginUserInfo.getInstance().getLoginUserId(mActivity).equals(mDiscussion.mOwner.mUserId);

        } else {
            return false;
        }
    }


    public void initBundle(Discussion discussion, DiscussionModifyType discussionModifyType) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(DISCUSSION_INFO, discussion);
        bundle.putSerializable(MODIFY_TYPE, discussionModifyType);
        this.setArguments(bundle);
    }


    /**
     * 群修改类型
     */
    public enum DiscussionModifyType {

        NAME_MODIFY {
            @Override
            public String getTitle() {
                return BaseApplicationLike.baseContext.getResources().getString(R.string.title_group_name_modify);
            }
        },

        DETAIL_MODIFY {
            @Override
            public String getTitle() {
                return BaseApplicationLike.baseContext.getResources().getString(R.string.title_group_detail_modify);
            }
        };

        public abstract String getTitle();
    }

    @Override
    protected boolean onBackPressed() {
        AtworkUtil.hideInput(mActivity, mModifyText);

        finish();
        return false;
    }


}
