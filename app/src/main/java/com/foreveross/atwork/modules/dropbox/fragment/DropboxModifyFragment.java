package com.foreveross.atwork.modules.dropbox.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.cache.DropboxCache;
import com.foreverht.db.service.repository.DropboxRepository;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.dropbox.DropboxAsyncNetService;
import com.foreveross.atwork.api.sdk.dropbox.requestJson.DropboxRequestJson;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.dropbox.DropboxConfig;
import com.foreveross.atwork.infrastructure.model.user.LoginUserBasic;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.listener.TextWatcherAdapter;
import com.foreveross.atwork.manager.DropboxConfigManager;
import com.foreveross.atwork.manager.DropboxManager;
import com.foreveross.atwork.modules.dropbox.activity.DropboxBaseActivity;
import com.foreveross.atwork.modules.dropbox.activity.DropboxModifyActivity;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.foreveross.atwork.modules.dropbox.activity.DropboxBaseActivity.ACTION_FILTER_ERR_CODE;

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
 * Created by reyzhang22 on 16/9/13.
 */
public class DropboxModifyFragment extends BackHandledFragment {

    private static final String sEmojiRex = "\"[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]\"";
    private static final String sOtherRex = "[\\\\]|[\\/]|[\\?]|[\\*]|[\\\"]|[\\|]|[\\:]|[\\<]|[\\>]|[\\?]";

    private ImageView mBackBtn;

    private TextView mTitle;

    private TextView mTvDone;

    private ImageView mDeleteEdit;

    private EditText mEditNewFolder;

    private ProgressDialogHelper mProgressDialog;

    private DropboxModifyActivity.ModifyAction mAction;

    private Dropbox mDropbox;

    private String mSourceId;
    private String mDomainId;
    private Dropbox.SourceType mSourceType;
    private String mParentId = "";

    public BroadcastReceiver mErrorCodeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int errorCode = intent.getIntExtra(DropboxBaseActivity.ERROR_CODE, 10001);
            ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Dropbox, errorCode, "");
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_folder, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handleBundle();
        initData();
        registerListener();
        registerReceiver();
    }

    private void handleBundle() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        mAction = (DropboxModifyActivity.ModifyAction) bundle.getSerializable(DropboxModifyActivity.KEY_INTENT_MODIFY_ACTION);
        mDropbox = bundle.getParcelable(DropboxModifyActivity.KEY_INTENT_DROPBOX);
        mSourceId = bundle.getString(DropboxModifyActivity.KEY_INTENT_SOURCE_ID);
        mSourceType = (Dropbox.SourceType) bundle.getSerializable(DropboxModifyActivity.KEY_INTENT_SOURCE_TYPE);
        mDomainId = bundle.getString(DropboxModifyActivity.KEY_INTENT_DOMAI_ID);
        mParentId = bundle.getString(DropboxModifyActivity.KEY_INTENT_PARENT_ID);
    }

    private void initData() {
        if (mAction == DropboxModifyActivity.ModifyAction.CreateFolder) {
            mTitle.setText(getString(R.string.new_folder));
            mTvDone.setTextColor(getResources().getColor(R.color.title_bar_rightest_text_gray));
        }
        if (mAction == DropboxModifyActivity.ModifyAction.Rename) {
            mTitle.setText(getString(R.string.rename));
            mEditNewFolder.setText(mDropbox.mFileName);
            mEditNewFolder.setSelection(mDropbox.mFileName.length());
            mTvDone.setTextColor(getResources().getColor(R.color.common_item_black));
        }
        mTvDone.setVisibility(View.VISIBLE);
        mTvDone.setText(getString(R.string.done));
    }

    private void registerListener() {
        mBackBtn.setOnClickListener(view -> {
            onBackPressed();
        });

        mTvDone.setOnClickListener(view -> {
            String newName = mEditNewFolder.getText().toString();
            if (TextUtils.isEmpty(newName)) {
                return;
            }
            if (newName.length() > 256) {
                AtworkToast.showToast(getString(R.string.file_name_length_invalid));
                return;
            }
            Pattern emojiPattern = Pattern.compile(sEmojiRex, Pattern.CASE_INSENSITIVE);
            Matcher emojiMatcher = emojiPattern.matcher(newName);
            while (emojiMatcher.find()) {
                AtworkToast.showToast(getString(R.string.Dropbox_400));
                return;
            }
            Pattern otherPattern = Pattern.compile(sOtherRex, Pattern.CASE_INSENSITIVE);
            Matcher otherMatcher = otherPattern.matcher(newName);
            while (otherMatcher.find()) {
                AtworkToast.showToast(getString(R.string.Dropbox_400));
                return;
            }

//            boolean isFileExist = DropboxRepository.getInstance().isFileExistInSameParent(mSourceId, mParentId, newName);
//            if (isFileExist && !DropboxModifyActivity.ModifyAction.CreateFolder.equals(mAction)) {
//                AtworkToast.showToast(getString(R.string.dropbox_file_already_exist));
//                return;
//            }

            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialogHelper(mActivity);
            }
            mProgressDialog.show();
            if (DropboxModifyActivity.ModifyAction.CreateFolder.equals(mAction)) {
                onDropboxFolderCreate();
                return;
            }
            if (DropboxModifyActivity.ModifyAction.Rename.equals(mAction)) {
                onDropboxRename();
                return;
            }

        });

        mEditNewFolder.addTextChangedListener(new TextWatcherAdapter(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                if (TextUtils.isEmpty(s)) {
                    mTvDone.setTextColor(getResources().getColor(R.color.title_bar_rightest_text_gray));
                    mDeleteEdit.setVisibility(View.GONE);
                    return;
                }
                mTvDone.setTextColor(getResources().getColor(R.color.common_item_black));
                mDeleteEdit.setVisibility(View.VISIBLE);
            }
        });


        mDeleteEdit.setOnClickListener(view -> {
            mEditNewFolder.setText("");
        });
    }

    @Override
    protected boolean onBackPressed() {
        mActivity.finish();
        //界面回退动画
        mActivity.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        return false;
    }

    private void onDropboxFolderCreate() {
        LoginUserBasic userBasic = LoginUserInfo.getInstance().getLoginUserBasic(mActivity);
        DropboxRequestJson requestJson = new DropboxRequestJson();
        requestJson.mName = mEditNewFolder.getText().toString();
        requestJson.mUser.nName = userBasic.mName;
        requestJson.mParent = mParentId;
        DropboxConfig dropboxConfig = DropboxConfigManager.getInstance().syncGetDropboxConfigBySourceId(mActivity, mSourceId);
        DropboxAsyncNetService.getInstance().makeDropboxFileOrDir(mActivity, mDomainId, mSourceType, mSourceId, 1,
                new Gson().toJson(requestJson), dropboxConfig, true, new DropboxAsyncNetService.OnDropboxListener() {

                    @Override
                    public void onDropboxOpsSuccess(List<Dropbox> dropboxes) {
                        handleResult(dropboxes, dropboxConfig, false);
                    }

                    @Override
                    public void onDropboxOpsFail(int status) {
                        mProgressDialog.dismiss();
                        if (status == 204003) {
                            AtworkToast.showResToast(R.string.no_right_ops_this_folder);
                            return;
                        }
                        if (status == 204010) {
                            AtworkToast.showResToast(R.string.Dropbox_400);
                            return;
                        }
                        if (status == 204014 || status == 204015) {
                            DropboxManager.getInstance().toastDropboxOverlimit(mActivity, mSourceId, mSourceType, 1, status);
                            return;
                        }

                        AtworkToast.showResToast(R.string.dropbox_network_error);


                    }
                });


    }

    private void onDropboxRename() {
        String name = mEditNewFolder.getText().toString();
        if (TextUtils.isEmpty(name)) {
            AtworkToast.showToast(getString(R.string.input_folder_name));
            return;
        }
        DropboxManager.getInstance().renameDropboxFile(mActivity, mDropbox, name, new DropboxAsyncNetService.OnDropboxListener() {
            @Override
            public void onDropboxOpsSuccess(List<Dropbox> dropboxes) {
                DropboxConfig dropboxConfig = DropboxConfigManager.getInstance().syncGetDropboxConfigBySourceId(mActivity, mDropbox.mSourceId);
                handleResult(dropboxes, dropboxConfig, true);
            }

            @Override
            public void onDropboxOpsFail(int status) {
                if (status == 204010) {
                    AtworkToast.showResToast(R.string.Dropbox_400);
                }
                if (status == 204003) {
                    if (DropboxManager.isMyDropbox(mActivity, mDropbox)) {
                        AtworkToast.showResToast(R.string.no_right_ops_this_folder);
                    } else {
                        AtworkToast.showResToast(R.string.no_right_rename_file);
                    }
                }
                if (status == 204006) {
                    if (mProgressDialog  != null) {
                        mProgressDialog.dismiss();
                    }
                    DropboxBaseActivity.refreshDropboxData();
                    AtworkToast.showResToast(R.string.no_file_exist);
                    mActivity.finish();
                    return;
                }
                else {
                    AtworkToast.showResToast(R.string.dropbox_network_error);
                }
                if (mProgressDialog  != null) {
                    mProgressDialog.dismiss();
                }
            }
        });
    }

    private void handleResult(List<Dropbox> dropboxes, DropboxConfig dropboxConfig, boolean isModify) {
        if (mProgressDialog  != null) {
            mProgressDialog.dismiss();
        }
        if (dropboxes == null) {
            return;
        }
        if (DropboxModifyActivity.ModifyAction.Rename.equals(mAction)) {
            mDropbox.mFileName = mEditNewFolder.getText().toString();
            dropboxes.add(mDropbox);
        }
        DropboxCache.getInstance().setDropboxListCache(dropboxes);
        DropboxRepository.getInstance().insertOrUpdateDropbox(dropboxes.get(0));
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(UserDropboxFragment.KEY_INTENT_MODIFY_DROPBOX_CALLBACK, (ArrayList<Dropbox>) dropboxes);
        intent.putExtra(UserDropboxFragment.KEY_INTENT_IS_MODIFY, isModify);
        mActivity.setResult(Activity.RESULT_OK, intent);
        mActivity.finish();
    }

    private void registerReceiver() {
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mErrorCodeReceiver, new IntentFilter(ACTION_FILTER_ERR_CODE));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mErrorCodeReceiver);
    }

    @Override
    protected void findViews(View view) {
        mBackBtn = view.findViewById(R.id.title_bar_common_back);
        mTitle = view.findViewById(R.id.title_bar_common_title);
        mTvDone = view.findViewById(R.id.title_bar_common_right_text);

        mEditNewFolder = view.findViewById(R.id.new_folder_edit);
        mDeleteEdit = view.findViewById(R.id.cancel_btn);
    }
}
