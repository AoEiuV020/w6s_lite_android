package com.foreveross.atwork.modules.file.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.foreverht.db.service.daoService.FileDaoService;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil;
import com.foreveross.atwork.api.sdk.net.model.DownloadFileParamsMaker;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.cordova.plugin.SelectFilePlugin;
import com.foreveross.atwork.cordova.plugin.model.ChooseFilesRequest;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil;
import com.foreveross.atwork.modules.file.fragement.FileSelectFragment;
import com.foreveross.atwork.modules.file.fragement.SDCardFilesFragment;
import com.foreveross.atwork.modules.file.inter.NavigateToFragmentListener;
import com.foreveross.atwork.support.AtWorkFragmentManager;
import com.foreveross.atwork.support.AtworkBaseActivity;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.FileHelper;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by lingen on 15/3/30.
 * Description:
 */
public class FileSelectActivity extends AtworkBaseActivity implements NavigateToFragmentListener {

    public static final String SELECTED_FILES_DATA = "selectedFile";

    public static final String DATA_SELECT_MODE = "data_select_mode";

    public static final String SHOW_DROPBOX_ITEM = "show_dropbox_item";

    public static final String IGNORE_FILE_SIZE = "IGNORE_FILE_SIZE";

    public static final String NEED_CHECK_LOCAL_EXIST = "need_check_local_exist";

    public static final String RESULT_INTENT = "GET_FILE_LIST_FLAG";

    private static final String TAG = FileSelectActivity.class.getSimpleName();

    public boolean mIgnoreSize = true;

    private View mVFakeStatusBar;
    private ImageView mIvBack;
    private TextView mTvTitle;
    //private TextView mCancel;
    private TextView mTvSend;
    private TextView mSelectedFileSize;
    private RelativeLayout mRlSelectBar;


    public static final String sTitle = "TITLE";

    private FileSelectFragment mSelectedFileFragment;
    private AtWorkFragmentManager mFragmentManager;
    public List<FileData> mSelectedFileData = new ArrayList<>();

    public ChooseFilesRequest mChooseFilesRequest;

    public SelectMode mSelectMode;
    public SelectType mSelectType = SelectType.NORMAL;

    public boolean mNeedCheckLocalExist;
    public boolean mShowDropboxItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_select);
        initView();
        initData();

        initFragment();
        setup();
        onSelectFileSizeUpdate();
        registerListener();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyComponent();
    }

    public static Intent getIntent(Context context, SelectMode selectMode, boolean showDropboxItem, boolean ignoreFileSize) {
        Intent intent = new Intent();
        intent.setClass(context, FileSelectActivity.class);
        intent.putExtra(DATA_SELECT_MODE, selectMode);
        intent.putExtra(SHOW_DROPBOX_ITEM, showDropboxItem);
        intent.putExtra(IGNORE_FILE_SIZE, ignoreFileSize);
        return intent;
    }

    private void registerListener() {
        //返回
        findViewById(R.id.title_bar_common_back).setOnClickListener(v -> onBackPressed());


        //发送
        mTvSend.setOnClickListener(v -> {
            if (mSelectedFileData == null) {
                return;
            }
            if (mSelectedFileData.isEmpty()) {
                AtworkToast.showToast(getString(R.string.please_select_file));
                return;
            }

            doSubmit();
        });
    }

    public void doSubmit() {
        if (mNeedCheckLocalExist) {
            if (checkLocalExistAndSubmitData()) {
                handleSubmitData();
            }
            return;
        }

        handleSubmitData();


    }

    private boolean checkLocalExistAndSubmitData() {
        List<FileData> localNotExistFileDatas = getLocalNotExistFileDatas();

        if(ListUtil.isEmpty(localNotExistFileDatas)) {
            return true;
        }

        List<FileData> flagFileHandleList = new ArrayList<>();
        flagFileHandleList.addAll(localNotExistFileDatas);

        ProgressDialogHelper dialogHelper = new ProgressDialogHelper(this);
        dialogHelper.show();

        for (FileData fileData : localNotExistFileDatas) {

            doDownloadNotExist(flagFileHandleList, dialogHelper, fileData);
        }
        return false;
    }

    private void doDownloadNotExist(List<FileData> flagFileHandleList, ProgressDialogHelper dialogHelper, FileData fileData) {
        String path = AtWorkDirUtils.getInstance().getChatFiles(FileSelectActivity.this) + fileData.title;
        String fileId = UUID.randomUUID().toString();
        MediaCenterNetManager manager = new MediaCenterNetManager(FileSelectActivity.this);
        MediaCenterNetManager.addMediaDownloadListener(new MediaCenterNetManager.MediaDownloadListener() {
            @Override
            public String getMsgId() {
                return fileId;
            }

            @Override
            public void downloadSuccess() {
                fileData.filePath = path;
                fileData.size = new File(path).length();
                fileData.isDownload = 1;
                FileDaoService.getInstance().insertRecentFile(fileData);

                checkSubmitAfterDownload(flagFileHandleList, fileData, dialogHelper);

            }

            @Override
            public void downloadFailed(int errorCode, String errorMsg, boolean doRefreshView) {

                checkSubmitAfterDownload(flagFileHandleList, fileData, dialogHelper);
            }

            @Override
            public void downloadProgress(double progress, double value) {

            }
        });
        manager.downloadFile(
                DownloadFileParamsMaker.Companion.newRequest().setMediaId(fileData.mediaId).setDownloadId(fileId).setDownloadPath(path)
                        .setDownloadType(MediaCenterHttpURLConnectionUtil.DOWNLOAD_TYPE.FILE)
        );
    }

    private void checkSubmitAfterDownload(List<FileData> flagFileHandleList, FileData fileData, ProgressDialogHelper dialogHelper) {
        flagFileHandleList.remove(fileData);
        if(ListUtil.isEmpty(flagFileHandleList)) {
            dialogHelper.dismiss();

            handleSubmitData();
        }
    }

    private List<FileData> getLocalNotExistFileDatas() {
        List<FileData> localNotExistFileDatas = new ArrayList<>();
        for(FileData fileData : mSelectedFileData) {
            if (!FileUtil.isExist(fileData.filePath)) {
                localNotExistFileDatas.add(fileData);

            }
        }

        return localNotExistFileDatas;
    }

    private void handleSubmitData() {
        if (SelectType.JS_BRIDGE == mSelectType && isFileSelectLimit()) {
            selectFilesBackToCordovaPlugin();
            return;
        }

        Intent result = new Intent();
        result.putExtra(RESULT_INTENT, (Serializable) mSelectedFileData);
        setResult(RESULT_OK, result);
        finish();
        //界面回退动画
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }


    private void initView() {
        mIvBack = findViewById(R.id.title_bar_common_back);
        mTvTitle = findViewById(R.id.title_bar_common_title);
        mVFakeStatusBar = findViewById(R.id.v_fake_statusbar);
        // mCancel = (TextView) findViewById(R.id.title_bar_common_right_text);
        mTvSend = findViewById(R.id.title_bar_common_right_text);
        mSelectedFileSize = findViewById(R.id.file_selected_size);
        mRlSelectBar = findViewById(R.id.select_file_statistics_layout);

        handleFakeStatusBar();
    }

    private void handleFakeStatusBar() {
        View view = getFakeStatusBar();
        if(null != view && StatusBarUtil.supportStatusBarMode()) {
            ViewUtil.setHeight(view, StatusBarUtil.getStatusBarHeight(BaseApplicationLike.baseContext));
            view.setVisibility(View.VISIBLE);
        }
    }

    protected View getFakeStatusBar() {
        return null;
    }

    private void initData() {

        mChooseFilesRequest = getIntent().getParcelableExtra(SelectFilePlugin.DATA_CHOOSE_FILES_REQUEST);

        if(null != mChooseFilesRequest) {
            if(mChooseFilesRequest.mFromCordova) {
                mSelectType = SelectType.JS_BRIDGE;
            }


            if(!ListUtil.isEmpty(mChooseFilesRequest.mFileKeys)) {
                List<FileData> fileDataSelectedList = FileData.fromPathList(mChooseFilesRequest.mFileKeys);
                mSelectedFileData.addAll(fileDataSelectedList);
            }
        }

        mSelectMode = (SelectMode) getIntent().getSerializableExtra(DATA_SELECT_MODE);

        mNeedCheckLocalExist = getIntent().getBooleanExtra(NEED_CHECK_LOCAL_EXIST, false);
        mShowDropboxItem = getIntent().getBooleanExtra(SHOW_DROPBOX_ITEM, false);
        mIgnoreSize = getIntent().getBooleanExtra(IGNORE_FILE_SIZE, true);
    }

    private void initFragment() {
        mFragmentManager = new AtWorkFragmentManager(this, R.id.fragment_files);
        mSelectedFileFragment = new FileSelectFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(SHOW_DROPBOX_ITEM, mShowDropboxItem);
        mSelectedFileFragment.setArguments(bundle);
        mFragmentManager.addFragmentAndAdd2BackStack(mSelectedFileFragment, FileSelectFragment.TAG);
    }

    private void setup() {
        mTvTitle.setText(getString(R.string.select_file));
        mTvSend.setText(getBtnSendText());
        // mCancel.setVisibility(View.VISIBLE);
        mSelectedFileSize.setText(String.format(getString(R.string.already_select), "0k"));

        if(isSingleSelectType()) {
            mTvSend.setVisibility(View.GONE);
            mRlSelectBar.setVisibility(View.GONE);

        } else {
            mTvSend.setVisibility(View.VISIBLE);
            mRlSelectBar.setVisibility(View.VISIBLE);

        }

    }

    @Override
    public void onBackPressed() {
        if (mFragmentManager == null) {
            finish();
            //界面回退动画
            this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }

        //NoSuchElementException thrown sometimes
        try {
            Fragment fragment = mFragmentManager.getCurrentFragment();

            if (fragment instanceof SDCardFilesFragment) {
                if (((SDCardFilesFragment) fragment).handleSdCardFileBackEvent()) {
                    return;
                }

            }

            if (!mFragmentManager.popBackStack()) {
                finish();
                //界面回退动画
                this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void navigateToFragment(Fragment fragment, String tag) {
        if (fragment == null || mFragmentManager == null) {
            return;
        }
        mFragmentManager.replaceFragmentAndAdd2BackStack(fragment, tag);
    }

    /**
     * 回收内存
     */
    private void destroyComponent() {
        mSelectedFileFragment = null;
        mFragmentManager = null;
        if (mSelectedFileData != null) {
            mSelectedFileData.clear();
            mSelectedFileData = null;
        }
    }

    public void onSelectFileSizeUpdate() {
        long size = 0;
        for (FileData fileData : mSelectedFileData) {
            if (fileData == null) {
                continue;
            }
            size = size + fileData.size;
        }
        mSelectedFileSize.setText(String.format(getString(R.string.already_select), FileHelper.getFileSizeStr(size)));
        if (mSelectedFileData.isEmpty()) {
            mTvSend.setText(getBtnSendText());
            mTvSend.setTextColor(getResources().getColor(R.color.title_bar_rightest_text_gray));
        } else {
            if (isSingleSelectType()) {
                mTvSend.setText(getBtnSendText());
                mTvSend.setTextColor(getResources().getColor(R.color.common_item_black));
                return;
            }
            mTvSend.setText(getBtnSendText() + "(" + mSelectedFileData.size() + "/" + getMaxChooseCount() + ")");
            mTvSend.setTextColor(getResources().getColor(R.color.common_item_black));
        }

    }

    @NonNull
    public String getBtnSendText() {
        if(null != mSelectMode) {
            if(SelectMode.GET == mSelectMode) {
                return getString(R.string.done);

            } else if(SelectMode.UPLOAD == mSelectMode){
                return getString(R.string.upload);

            }

        }

        return getString(R.string.button_send);
    }


    public void selectFilesBackToCordovaPlugin() {
        if (mSelectedFileData != null && mSelectedFileData.size() != 0) {

            for (int i = 0; i < mSelectedFileData.size(); i++) {
                FileData fileData = mSelectedFileData.get(i);
                fileData.filePath = "file://" + fileData.filePath;

            }
        }


        Intent intent = new Intent();
        intent.putExtra(SELECTED_FILES_DATA, (Serializable) mSelectedFileData);
        this.setResult(SelectFilePlugin.RESULT_CODE_SELECT_FILES, intent);
        this.finish();
    }

    public boolean isFromCordova() {
        return SelectType.JS_BRIDGE ==  mSelectType;
    }

    public boolean isFileSelectLimit() {
        return null != mChooseFilesRequest;
    }

    public boolean isSingleSelectType() {
        return isFileSelectLimit() && mChooseFilesRequest.isSingleType();
    }

    public int getMaxChooseCount() {
        if(isFileSelectLimit()) {
            return mChooseFilesRequest.mFileLimit.mMaxSelectCount;
        }

        return 9;
    }

    public long getMaxSingleChooseSize() {
        if(isFileSelectLimit()) {
            return mChooseFilesRequest.mFileLimit.mSingleSelectSize;
        }

        return -1;
    }

    public long getMaxTotalChooseSize() {
        if(isFileSelectLimit()) {
            return mChooseFilesRequest.mFileLimit.mTotalSelectSize;
        }

        return -1;
    }


    public ChooseFilesRequest getChooseFilesRequest() {
        return mChooseFilesRequest;
    }

    public enum SelectType {

        NORMAL,

        JS_BRIDGE

    }

    public enum SelectMode {
        SEND,

        GET,

        UPLOAD
    }


}
