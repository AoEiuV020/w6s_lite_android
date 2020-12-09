package com.foreveross.atwork.modules.dropbox.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.net.model.UploadFileParamsMaker;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.UndoEventMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.CommonUtil;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.IOUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.manager.DropboxManager;
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity;
import com.foreveross.atwork.modules.chat.util.UndoMessageHelper;
import com.foreveross.atwork.modules.dropbox.adapter.DropboxPagerAdapter;
import com.foreveross.atwork.modules.dropbox.fragment.OrgsDropboxFragment;
import com.foreveross.atwork.modules.dropbox.fragment.UserDropboxFragment;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.ChatMessageHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

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
 * Created by reyzhang22 on 2016/11/8.
 */

public class SaveToDropboxActivity extends DropboxBaseActivity {

    private Bundle mBundle;

    public static final String KEY_INTENT_SELECT_DROPBOX_SEND = "KEY_INTENT_SELECT_DROPBOX_SEND";

    public static final String KEY_INTENT_SELECT_DROPBOX_SEND_EMAIL = "KEY_INTENT_SELECT_DROPBOX_SEND_EMAIL";

    private ChatPostMessage mFromMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBundle = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initSaveFragment();
        initTabLineWidth();
        initListener();

        checkLocalFileUpload();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DisplayMode.Send.equals(mCurrentDisplayMode)) {
            onSelectFileSizeUpdate();
        }
    }

    /**
     * 获取传送的intent对象
     * @param context
     * @param dropbox
     * @return
     */
    public static final Intent getIntent(Context context, Dropbox dropbox, ChatPostMessage chatPostMessage) {
        Intent intent = new Intent(context, SaveToDropboxActivity.class);
        intent.putExtra(KEY_INTENT_DROPBOX, dropbox);
        intent.putExtra(KEY_INTENT_TITLE, context.getString(R.string.please_select_dir));
        intent.putExtra(KEY_INTENT_FROM_MESSAGE, chatPostMessage);
        intent.putExtra(KEY_INTENT_MOVE_OR_COPY, true);
        return intent;
    }

    public static final Intent getIntent(Context context, Dropbox dropbox, DisplayMode displayMode, boolean localExist) {
        Intent intent = new Intent(context, SaveToDropboxActivity.class);
        intent.putExtra(KEY_INTENT_DROPBOX, dropbox);
        intent.putExtra(KEY_INTENT_TITLE, context.getString(R.string.please_select_dir));
        intent.putExtra(KEY_INTENT_DISPLAY_MODE, displayMode);
        intent.putExtra(KEY_INTENT_MOVE_OR_COPY, true);
        intent.putExtra(KEY_INTENT_LOCAL_EXIST, localExist);
        return intent;
    }


    public static final Intent getIntent(Context context, String path) {
        Intent intent = new Intent(context, SaveToDropboxActivity.class);
        intent.putExtra(KEY_INTENT_FILE_PATH, path);
        return intent;
    }

    private ViewPager mViewPager;

    private List<Fragment> mFragmentList = new ArrayList<>();
    private DropboxPagerAdapter mFragmentAdapter;

    private View mPagerTag;
    private ImageView mTabLineIv;
    private TextView mMyFileText;
    private TextView mOrgFileText;
    private int mCurrentIndex;
    /**
     * 屏幕的宽度
     */
    private int mScreenWidth;

    private UserDropboxFragment mSaveUserFragment;
    private OrgsDropboxFragment mSaveOrgFragment;

    private Dropbox mDropbox;
    private String mLocalPath;
    private Uri mLocalUri;

    private boolean mIsTempLocalPath = false;

    private boolean mLocalExist;

    ProgressDialogHelper mProgressDialogHelper;


    public void changeBackVisual(boolean showCancel){
        mCancelBtn.setVisibility(showCancel ? View.VISIBLE : GONE);
        mBackBtn.setVisibility(showCancel ? GONE : View.VISIBLE);
        if (mPagerTag != null) {
            mPagerTag.setVisibility(showCancel ? View.VISIBLE : GONE);
        }

    }

    private void checkLocalFileUpload() {
        if(StringUtils.isEmpty(mDropbox.mMediaId)) {
            mLocalPath = mDropbox.mLocalPath;
        }

        if(null != mLocalUri || !StringUtils.isEmpty(mLocalPath)) {
            mProgressDialogHelper = new ProgressDialogHelper(this);
            mProgressDialogHelper.show(false);

            setUploadFilePath();
            if(FileUtil.isExist(mLocalPath)) {
                final String messageId = UUID.randomUUID().toString();
//                mediaCenterNetManager.uploadFile(this, MediaCenterNetManager.COMMON_FILE, messageId, mLocalPath, true, null);

                UploadFileParamsMaker uploadFileParamsMaker = UploadFileParamsMaker.newRequest()
                        .setType(MediaCenterNetManager.COMMON_FILE)
                        .setMsgId(messageId)
                        .setFilePath(mLocalPath)
                        .setNeedCheckSum(true);

                MediaCenterNetManager.uploadFile(this, uploadFileParamsMaker);

                addUploadListener(messageId);
            }

        }
    }

    private void addUploadListener(String messageId) {
        MediaCenterNetManager.MediaUploadListener mediaUploadListener
                = MediaCenterNetManager.getMediaUploadListenerById(messageId, MediaCenterNetManager.UploadType.EMAIL_ATTACH);

        if (null == mediaUploadListener) {
            mediaUploadListener = new MediaCenterNetManager.MediaUploadListener() {
                @Override
                public String getMsgId() {
                    return messageId;
                }

                @Override
                public MediaCenterNetManager.UploadType getType() {
                    return MediaCenterNetManager.UploadType.EMAIL_ATTACH;
                }

                @Override
                public void uploadSuccess(String mediaInfo) {
                    MediaCenterNetManager.removeMediaUploadListener(this);
                    mProgressDialogHelper.dismiss();

                    if (null == mDropbox) {
                        mDropbox = Dropbox.convertFromFilePath(SaveToDropboxActivity.this, mLocalPath, mediaInfo);
                    } else {
                        mDropbox.mMediaId = mediaInfo;
                    }
                    mSaveUserFragment.refreshDropbox(mDropbox);
                    mSaveOrgFragment.refreshDropbox(mDropbox);

                    if (mIsTempLocalPath) {
                        FileUtil.delete(mLocalPath);
                    }


                }

                @Override
                public void uploadFailed(int errorCode, String errorMsg, boolean doRefreshView) {
                    if (mIsTempLocalPath) {
                        FileUtil.delete(mLocalPath);
                    }

                    MediaCenterNetManager.removeMediaUploadListener(this);
                    mProgressDialogHelper.dismiss();
                    if (errorCode != -99) {
                        AtworkToast.showResToast(R.string.upload_file_error);
                        MediaCenterNetManager.removeUploadFailList(getMsgId());
                    }

                    finish();

                }

                @Override
                public void uploadProgress(double progress) {

                }
            };
            MediaCenterNetManager.addMediaUploadListener(mediaUploadListener);

        }
    }

    private void setUploadFilePath() {
        if (null != mLocalUri) {
            InputStream in = null;
            OutputStream out = null;
            try {
                String fileName = getIntent().getStringExtra(KEY_INTENT_FILE_NAME);
                if(StringUtils.isEmpty(fileName)) {
                    fileName = UUID.randomUUID().toString();
                }
                mLocalPath = AtWorkDirUtils.getInstance().getTmpDir(this) + fileName;
                mIsTempLocalPath = true;

                File file = new File(mLocalPath);
                in = getContentResolver().openInputStream(mLocalUri);
                out = new FileOutputStream(file);
                IOUtil.copy(in, out);
                out.flush();

            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                IOUtil.release(out);
                IOUtil.release(in);

            }
        }
    }

    private void initData() {
        mDropbox = getIntent().getParcelableExtra(KEY_INTENT_DROPBOX);
        mCurrentDomainId = AtworkConfig.DOMAIN_ID;
        mCurrentSourceId = LoginUserInfo.getInstance().getLoginUserId(this);
        mCurrentSourceType = Dropbox.SourceType.User;
        mFromMessage = (ChatPostMessage) getIntent().getSerializableExtra(KEY_INTENT_FROM_MESSAGE);
        mLocalExist = getIntent().getBooleanExtra(KEY_INTENT_LOCAL_EXIST, false);
        DisplayMode displayMode = (DisplayMode) getIntent().getSerializableExtra(KEY_INTENT_DISPLAY_MODE);
        if (displayMode != null) {
            mCurrentDisplayMode = displayMode;
        } else {
            mCurrentDisplayMode = DisplayMode.Copy;
        }

        mLocalPath = getIntent().getStringExtra(KEY_INTENT_FILE_PATH);
        mLocalUri = getIntent().getParcelableExtra(KEY_INTENT_FILE_URI);

    }
    private void initView() {
        mBaseView.setVisibility(GONE);
        mFragmentPagerView.setVisibility(View.VISIBLE);
        mPagerTag = findViewById(R.id.pager_tag);
        mViewPager = mFragmentPagerView.findViewById(R.id.dropbox_view_pager);
        mTabLineIv = mFragmentPagerView.findViewById(R.id.id_tab_line_iv);
        mMyFileText = mFragmentPagerView.findViewById(R.id.id_my_file_tv);
        mOrgFileText = mFragmentPagerView.findViewById(R.id.id_org_file_tv);

    }

    private void initListener() {
        mMoveToBtn.setOnClickListener(view -> {
            if(CommonUtil.isFastClick(1500)) {
                return;
            }

            if (isFromMsg()) {
                mSaveUserFragment.onDropboxCreate(mDropbox);
                return;
            }
            mSaveUserFragment.onDropboxCopy(mDropbox);
        });

        mNewFolderBtn.setOnClickListener(view -> {
            mSaveUserFragment.onNewFolder();
        });

        mMyFileText.setOnClickListener(view -> {
            mViewPager.setCurrentItem(0);
        });

        mOrgFileText.setOnClickListener(view -> {
            mViewPager.setCurrentItem(1);
        });

        //不要去掉这个点击事件:) #issuse 19828
        mSelectFileLayout.setOnClickListener(v -> {

        });

        mSend.setOnClickListener(view -> {
            onDropboxFileSend();
        });

        mCancelBtn.setOnClickListener(view -> {
            if (!ListUtil.isEmpty(mSelectedDropbox)) {
                mSelectedDropbox.clear();
            }
            setResult(RESULT_OK);
            finish();
        });
        mBackBtn.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void onDropboxFileSend() {
        if (mLocalExist) {
            sendDropboxListByLocalPaths();
            return;
        }

        sendDropboxListByMediaIds();
        finish();
    }

    private void sendDropboxListByLocalPaths() {
        ProgressDialogHelper dialogHelper = new ProgressDialogHelper(this);
        dialogHelper.show();
        ArrayList<FileData> fileDatas = new ArrayList<>();
        for (Dropbox dropbox : mSelectedDropbox) {
            String filePath = dropbox.mLocalPath;
            if (!TextUtils.isEmpty(filePath) && new File(filePath).exists()) {
                FileData fileData = new FileData();
                fileData.filePath = filePath;
                fileData.title = dropbox.mFileName;
                fileDatas.add(fileData);
                continue;
            }
            DropboxManager.getInstance().downloadFileFromDropbox(this, dropbox, new DropboxManager.OnFileDownloadListener() {
                @Override
                public void onFileStartDownloading(Dropbox dropbox) {}

                @Override
                public void onFileDownloadingProgress(long progress) {}

                @Override
                public void onFileDownloaded(Dropbox dropbox) {
                    FileData fileData = new FileData();
                    fileData.filePath = dropbox.mLocalPath;
                    fileData.title = dropbox.mFileName;
                    fileDatas.add(fileData);
                }

                @Override
                public void onFileDownloadPause(Dropbox dropbox) {
                    FileData fileData = new FileData();
                    fileData.filePath = dropbox.mLocalPath;
                    fileData.title = dropbox.mFileName;
                    fileDatas.add(fileData);
                }
            });
        }
        Executors.newCachedThreadPool().submit(() -> {
            while (fileDatas.size() != mSelectedDropbox.size()) {
                Log.i("Dropbox", "downloading... downloaded size = " + fileDatas.size() + " and selected size = " + mSelectedDropbox.size());
            }
            dialogHelper.dismiss();

            Intent intent = new Intent();
            intent.putExtra(KEY_INTENT_SELECT_DROPBOX_SEND_EMAIL, fileDatas);
            setResult(RESULT_OK, intent);
            DropboxBaseActivity.mSelectedDropbox.clear();
            finish();
        });

    }

    private void sendDropboxListByMediaIds() {
        List<ChatPostMessage> messages = new ArrayList<>();
        for (Dropbox dropbox : mSelectedDropbox) {
            FileData fileData = FileData.fromDropbox(dropbox);
            User LoginUser = AtworkApplicationLike.getLoginUserSync();
            long overtime = DomainSettingsManager.getInstance().handleChatFileExpiredFeature() ? TimeUtil.getTimeInMillisDaysAfter(DomainSettingsManager.getInstance().getChatFileExpiredDay()) : -1;
            FileTransferChatMessage message = FileTransferChatMessage.newFileTransferChatMessage(AtworkApplicationLike.baseContext, fileData, LoginUser, "",
                    ParticipantType.User, ParticipantType.User, "", "", "", BodyType.File, "", overtime, null);
            message.mediaId = dropbox.mMediaId;
            messages.add(message);
        }
        Intent intent = new Intent();
        intent.putExtra(KEY_INTENT_SELECT_DROPBOX_SEND, (Serializable) messages);
        setResult(RESULT_OK, intent);
    }


    private void initSaveFragment() {
        mSaveUserFragment =  new UserDropboxFragment();
        mSaveUserFragment.setArguments(mBundle);
        mSaveOrgFragment = new OrgsDropboxFragment();
        mSaveOrgFragment.setArguments(mBundle);
        mFragmentList.add(mSaveUserFragment);
        mFragmentList.add(mSaveOrgFragment);
        changeDisplayMode(mCurrentDisplayMode);
        changeBackVisual(true);
        if (mCurrentDisplayMode.equals(DisplayMode.Send)) {
            setSelectMode();
        }
        mFragmentAdapter = new DropboxPagerAdapter(
                this.getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(mFragmentAdapter);
        mViewPager.setOffscreenPageLimit(2);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int state) {

            }

            @Override
            public void onPageScrolled(int position, float offset,
                                       int offsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.e("offset", "position = " + position);
                resetTextView();
                switch (position) {
                    case 0:
                        mMyFileText.setTextColor(getResources().getColor(R.color.blue_lock));
                        mMoveModeBottomLayout.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        mOrgFileText.setTextColor(getResources().getColor(R.color.blue_lock));
                        mMoveModeBottomLayout.setVisibility(GONE);
                        break;
                }
                mCurrentIndex = position;

                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                        .getLayoutParams();

                if (mCurrentIndex == 0 ) {
                    lp.leftMargin = 1;

                }
                if (mCurrentIndex == 1 ) // 1->0
                {
                    lp.leftMargin = mScreenWidth / 2 + 1;

                }
                Log.e("offset:", "leftMargin = " + lp.leftMargin);
                mTabLineIv.setLayoutParams(lp);
            }
        });

    }


    private void initTabLineWidth() {
        DisplayMetrics dpMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay()
                .getMetrics(dpMetrics);
        mScreenWidth = dpMetrics.widthPixels;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                .getLayoutParams();
        lp.width = mScreenWidth / 2;
        mTabLineIv.setLayoutParams(lp);
    }

    /**
     * 重置颜色
     */
    private void resetTextView() {
        mMyFileText.setTextColor(getResources().getColor(R.color.dropbox_common_text_color));
        mOrgFileText.setTextColor(getResources().getColor(R.color.dropbox_common_text_color));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && DisplayMode.Send.equals(mCurrentDisplayMode)) {
            onDropboxFileSend();
            return;
        }
        mSaveUserFragment.onActivityResult(requestCode, resultCode, data);
        mSaveOrgFragment.onActivityResult(requestCode, resultCode, data);
    }

    private void setSelectMode() {
        mSaveUserFragment.updateSelectedVisual(mCurrentDisplayMode);

    }

    @Override
    public void onBackPressed() {
        if (!mSaveUserFragment.handleDropboxDirBackEvent()) {
            if (!ListUtil.isEmpty(DropboxBaseActivity.mSelectedDropbox)) {
                DropboxBaseActivity.mSelectedDropbox.clear();
            }
            setResult(RESULT_OK);
            finish();
            //界面回退动画
            this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            return;
        }
        return;
    }

    @Override
    public void onUndoMsgReceive(UndoEventMessage undoEventMessage) {
        if(!isFromMsg()) {
            return;
        }

        if(null != undoEventMessage && undoEventMessage.mEnvIds.contains(mFromMessage.deliveryId)) {
            showUndoDialog(SaveToDropboxActivity.this, mFromMessage);
        }
    }

    private boolean isFromMsg() {
        return null != mFromMessage;
    }


    @Override
    protected void showUndoDialog(Context context, PostTypeMessage message) {
        AtworkAlertDialog dialog = new AtworkAlertDialog(context, AtworkAlertDialog.Type.SIMPLE)
                .setContent(UndoMessageHelper.getUndoContent(context, message))
                .hideDeadBtn()
                .setClickBrightColorListener(dialog1 -> {

                    Intent intent = ChatDetailActivity.getIntent(context, ChatMessageHelper.getChatUser(mFromMessage).mUserId);
                    startActivity(intent);
                    finish();

                });
        dialog.setCancelable(false);
        dialog.show();
    }
}
