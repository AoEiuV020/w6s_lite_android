package com.foreveross.atwork.modules.dropbox.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.UndoEventMessage;
import com.foreveross.atwork.infrastructure.utils.CommonUtil;
import com.foreveross.atwork.infrastructure.utils.NetworkStatusUtil;
import com.foreveross.atwork.manager.DropboxManager;
import com.foreveross.atwork.modules.dropbox.fragment.UserDropboxFragment;
import com.foreveross.atwork.modules.file.activity.FileSelectActivity;
import com.foreveross.atwork.support.AtWorkFragmentManager;

import java.util.List;

import static android.view.View.GONE;

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
 * Created by reyzhang22 on 2016/10/13.
 */

public class OrgDropboxActivity extends DropboxBaseActivity {

    /**
     * 获取传送的intent对象
     * @param context
     * @param sourceType
     * @param sourceId
     * @param domainId
     * @param title
     * @param subTitle
     * @return
     */
    public static final Intent getIntent(Context context, Dropbox.SourceType sourceType, String sourceId, String domainId,
                                         String title, String subTitle, Dropbox dropbox, ChatPostMessage fromMessage, DisplayMode displayMode, boolean allRefresh, boolean moveOrCopy) {
        Intent intent = new Intent(context, OrgDropboxActivity.class);
        intent.putExtra(DropboxBaseActivity.KEY_INTENT_SOURCE_TYPE, sourceType);
        intent.putExtra(DropboxBaseActivity.KEY_INTENT_SOURCE_ID, sourceId);
        intent.putExtra(DropboxBaseActivity.KEY_INTENT_DOMAIN_ID, domainId);
        intent.putExtra(DropboxBaseActivity.KEY_INTENT_TITLE, title);
        intent.putExtra(DropboxBaseActivity.KEY_INTENT_SUB_TITLE, subTitle);
        intent.putExtra(DropboxBaseActivity.KEY_INTENT_DROPBOX, dropbox);
        intent.putExtra(DropboxBaseActivity.KEY_INTENT_FROM_MESSAGE, fromMessage);
        intent.putExtra(DropboxBaseActivity.KEY_INTENT_DISPLAY_MODE, displayMode);
        intent.putExtra(DropboxBaseActivity.KEY_INTENT_COMPLETE_REFRESH, allRefresh);
        intent.putExtra(KEY_INTENT_MOVE_OR_COPY, moveOrCopy);
        return intent;
    }

    private Bundle mBundle;

    private Dropbox mDropbox;

    private ChatPostMessage mFromMessage;

    private DisplayMode mDisplayMode;

    private boolean mMoveOrCopy;


    /**
     * 隐藏上传图标
     */
    public void invisibleUploadIcon(boolean show) {
        mUploadBtn.setVisibility(show? View.VISIBLE :View.GONE);
    }

    private UserDropboxFragment mOrgUserDropboxFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBundle = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        mOrgSwitcherLayout.setVisibility(GONE);
        if (mBundle != null) {
            mDropbox = mBundle.getParcelable(SaveToDropboxActivity.KEY_INTENT_DROPBOX);
            mFromMessage = (ChatPostMessage) mBundle.getSerializable(KEY_INTENT_FROM_MESSAGE);
            mDisplayMode = (DisplayMode) mBundle.getSerializable(KEY_INTENT_DISPLAY_MODE);
            mMoveOrCopy = mBundle.getBoolean(KEY_INTENT_MOVE_OR_COPY, false);

        }
        initFragment();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDropbox != null) {
            if (mDisplayMode == null) {
                changeDisplayMode(DisplayMode.Copy);
                new Handler().postDelayed(() -> mIconLayout.setVisibility(GONE), 10);
            }

        }
        if (mDisplayMode != null) {
            changeDisplayMode(mDisplayMode);
            if (DisplayMode.Send.equals(mDisplayMode)) {
                onSelectFileSizeUpdate();
                new Handler().postDelayed(() -> mIconLayout.setVisibility(GONE), 10);
            }
        }

        onViewResume();
    }

    public void onViewResume() {
        mOrgSwitcherLayout.setVisibility(GONE);
    }

    protected void initFragment() {
        mFragmentManager = new AtWorkFragmentManager(this, R.id.dropbox_layout);
        if (mOrgUserDropboxFragment == null) {
            mOrgUserDropboxFragment = new UserDropboxFragment();
            mOrgUserDropboxFragment.setArguments(mBundle);
            if (mDisplayMode != null) {
                if (DisplayMode.Send.equals(mDisplayMode)) {
                    mOrgUserDropboxFragment.updateSelectedVisual(mDisplayMode);
                }
            }
        }
        mFragmentManager.addFragmentAndAdd2BackStack(mOrgUserDropboxFragment, UserDropboxFragment.class.getSimpleName());
    }

    private void initListener() {
        mBackBtn.setOnClickListener(view -> {
            onBackPressed();
        });
        mAllSelectBtn.setOnClickListener(view -> {
            mOrgUserDropboxFragment.selectAll();
        });

        mCancelBtn.setOnClickListener(view -> {
            changeDisplayMode(DisplayMode.Normal);
            mOrgUserDropboxFragment.onCancelClick();
        });

        mDeleteBtn.setOnClickListener(view -> {
            mOrgUserDropboxFragment.showDelFileDialog();
        });

        mMoveBtn.setOnClickListener(view -> {
            mOrgUserDropboxFragment.startMoveActivity();
//            changeDisplayMode(DisplayMode.Normal);
        });
        mMoveToBtn.setOnClickListener(view -> {
            if(CommonUtil.isFastClick(1500)) {
                return;
            }

            if (isFromMessage()) {
                mOrgUserDropboxFragment.onDropboxCreate(mDropbox);
                return;
            }
            if (mDropbox != null) {
                mOrgUserDropboxFragment.onDropboxCopy(mDropbox);
                return;
            }
            mOrgUserDropboxFragment.onDropboxMove();

        });

        mNewFolderBtn.setOnClickListener(view -> {
            mOrgUserDropboxFragment.onNewFolder();
        });

        mCancelBtn.setOnClickListener(view -> {
            changeDisplayMode(DisplayMode.Normal);
            mOrgSwitcherLayout.setVisibility(GONE);
            mOrgUserDropboxFragment.onCancelClick();
        });

        mSend.setOnClickListener(view -> {
            setResult(RESULT_OK);
            finish();
        });
    }

    public void setSelectMode(boolean selectMode) {
        if (mOrgUserDropboxFragment != null) {
            mOrgUserDropboxFragment.updateSelectedVisual(selectMode ? mDisplayMode : DisplayMode.Normal);
        }

    }

    public void setMoveMode() {
        if (mOrgUserDropboxFragment != null) {
            mOrgUserDropboxFragment.updateMoveVisual(DisplayMode.Move);
        }

    }

    public void setNormalMode() {
        if (mOrgUserDropboxFragment != null) {
            mOrgUserDropboxFragment.updateNormalVisual(DisplayMode.Normal);
        }

    }

    public void changeDisplayMode(DisplayMode displayMode) {
        mCurrentDisplayMode = displayMode;
        changeTitleVisual();
        changeBottomVisual(displayMode);
        switch (displayMode) {
            case Normal:
                changeToNormalMode();
                break;

            case Send:
            case Select:
                changeToSelectMode();
                break;

            case Move:
            case Copy:
                changeToMoveMode();
                break;

        }

    }

    public void changeToNormalMode() {
        setSelectMode(false);
        setNormalMode();
    }

    public void changeToSelectMode() {
        setSelectMode(true);
    }

    public void changeToMoveMode() {
        setSelectMode(false);
        setMoveMode();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_FILE_SELECT && resultCode == RESULT_OK) {
            mSelectedFileData = (List<FileData>)data.getSerializableExtra(FileSelectActivity.RESULT_INTENT);
            if (NetworkStatusUtil.isMobileNetWorkConnectedOrConnecting(this)) {
                showWarningDialog();
                return;
            }
            uploadFilesToDropbox();
            return;
        }
        //TODO.. 搜索是文件夹的返回
        if (requestCode == REQUEST_CODE_FILE_SEARCH && resultCode == RESULT_OK) {
            if (data == null){
                return;
            }
            Dropbox dropbox = data.getParcelableExtra(KEY_INTENT_SEARCH_DIR_SELECT);
            mOrgUserDropboxFragment.onDropboxDirSelect(dropbox);
            return;
        }
        mOrgUserDropboxFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (!DisplayMode.Normal.equals(mCurrentDisplayMode) && !mMoveOrCopy) {
            changeDisplayMode(DisplayMode.Normal);
            mOrgSwitcherLayout.setVisibility(GONE);
            return;
        }
        if (mOrgUserDropboxFragment instanceof UserDropboxFragment) {
            if (!mOrgUserDropboxFragment.handleDropboxDirBackEvent()) {
                finish(true);
                return;
            }
            return;
        }
        finish(true);

    }

    @Override
    public void uploadFilesToDropbox() {
        for (FileData fileData : mSelectedFileData) {
            if (DropboxManager.getInstance().isOverPanItemLimitAndAlert(fileData.size, DropboxManager.getInstance().getPanSettingTypeBySourceId(this, mCurrentSourceId, mCurrentSourceType))) {
                return;
            }

        }

        mOrgUserDropboxFragment.uploadFilesToDropbox(mSelectedFileData, mCurrentDomainId, mCurrentSourceId, mCurrentSourceType);
    }

    @Override
    public void onUndoMsgReceive(UndoEventMessage undoEventMessage) {
        super.onUndoMsgReceive(undoEventMessage);
    }

    private boolean isFromMessage() {
        return null != mFromMessage;
    }
}
