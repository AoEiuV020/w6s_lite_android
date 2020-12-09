package com.foreveross.atwork.modules.dropbox.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.db.service.repository.DropboxConfigRepository;
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.biometricAuthentication.BiometricAuthenticationProtectItemType;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.dropbox.DropboxConfig;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.utils.CommonUtil;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.NetworkStatusUtil;
import com.foreveross.atwork.manager.DropboxManager;
import com.foreveross.atwork.modules.dropbox.component.SortedFileTypePopup;
import com.foreveross.atwork.modules.dropbox.component.SwitchView;
import com.foreveross.atwork.modules.dropbox.fragment.MyDropboxFileFragment;
import com.foreveross.atwork.modules.dropbox.fragment.OrgsDropboxFragment;
import com.foreveross.atwork.modules.dropbox.fragment.UserDropboxFragment;
import com.foreveross.atwork.modules.file.activity.FileSelectActivity;
import com.foreveross.atwork.support.AtWorkFragmentManager;
import com.foreveross.atwork.support.AtworkBaseActivity;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.atwork.utils.FileHelper;
import com.foreveross.atwork.utils.ThemeResourceHelper;
import com.foreveross.atwork.utils.WorkplusTextSizeChangeHelper;
import com.foreveross.theme.manager.SkinHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
 * Created by reyzhang22 on 2016/10/14.
 */

public abstract class DropboxBaseActivity extends AtworkBaseActivity {

    public static final int REQUEST_CODE_FILE_SELECT = 0x001;
    public static final int REQUEST_CODE_FILE_SEARCH = 0x002;

    public static final String KEY_INTENT_TITLE = "KEY_INTENT_TITLE";
    public static final String KEY_INTENT_SUB_TITLE = "KEY_INTENT_SUB_TITLE";
    public static final String KEY_INTENT_SOURCE_TYPE = "KEY_INTENT_SOURCE_TYPE";
    public static final String KEY_INTENT_SOURCE_ID = "KEY_INTENT_SOURCE_ID";
    public static final String KEY_INTENT_DOMAIN_ID = "KEY_INTENT_DOMAIN_ID";
    public static final String KEY_INTENT_FILE_PATH = "KEY_INTENT_FILE_PATH";
    public static final String KEY_INTENT_FILE_URI = "KEY_INTENT_FILE_URI";
    public static final String KEY_INTENT_FILE_NAME = "KEY_INTENT_FILE_NAME";
    public static final String KEY_INTENT_DROPBOX = "KEY_INTENT_DROPBOX";
    public static final String KEY_INTENT_DISPLAY_MODE = "KEY_INTENT_DISPLAY_MODE";
    public static final String KEY_INTENT_FROM_MESSAGE = "KEY_INTENT_FROM_MESSAGE";
    public static final String KEY_INTENT_MOVE_LAST_PARENT_ID = "KEY_INTENT_MOVE_LAST_PARENT_ID";
    public static final String KEY_INTENT_MOVE_CURRENT_PARENT_ID = "KEY_INTENT_MOVE_CURRENT_PARENT_ID";
    public static final String KEY_INTENT_MOVE_LIST = "KEY_INTENT_MOVE_LIST";
    public static final String KEY_INTENT_MOVE_OR_COPY = "KEY_INTENT_MOVE_OR_COPY";
    public static final String KEY_INTENT_COMPLETE_REFRESH = "KEY_INTENT_COMPLETE_REFRESH";
    public static final String KEY_INTENT_SEARCH_DIR_SELECT = "KEY_INTENT_SEARCH_DIR_SELECT";
    public static final String KEY_INTENT_LOCAL_EXIST = "KEY_INTENT_LOCAL_EXIST";
    public static final String KEY_INTENT_SELECT_MAX = "KEY_INTENT_SELECT_MAX";

    public static final String ACTION_FILTER_ERR_CODE = "ACTION_FILTER_ERR_CODE";
    public static final String ERROR_CODE = "ERROR_CODE";

    /**
     * 当前的展现方式
     */
    public enum DisplayMode implements Serializable {
        //正常
        Normal,
        //选择模式
        Select,
        //移动模式
        Move,
        //复制模式
        Copy,
        //发送模式
        Send
    }


    public List<FileData> mSelectedFileData;

    public Fragment mCurrentFragment;
    public UserDropboxFragment mUserDropboxFragment;
    public OrgsDropboxFragment mOrgDropboxFragment;
    public MyDropboxFileFragment mMyDropboxFragment;

    public AtWorkFragmentManager mFragmentManager;

    public ImageView mBackBtn;

    public View mIconLayout;
    private ImageView mFilterBtn;
    private ImageView mSearchBtn;
    public ImageView mUploadBtn;

    public View mTitleLayout;
    public TextView mTitle;
    public TextView mSubTitle;
    public TextView mAllSelectBtn;
    public TextView mCancelBtn;

    protected View mOrgSwitcherLayout;
    public TextView mMyFileBtn;
    public TextView mOrgFileBtn;

    private View mSelectModeBottomLayout;
    public TextView mDeleteBtn;
    public TextView mMoveBtn;

    public View mMoveModeBottomLayout;
    public TextView mNewFolderBtn;
    public TextView mMoveToBtn;

    public View mSelectFileLayout;
    public TextView mSelectedFileSize;
    public Button mSend;

    public String mCurrentSourceId;
    public Dropbox.SourceType mCurrentSourceType;
    public String mCurrentDomainId;
    public DisplayMode mCurrentDisplayMode = DisplayMode.Normal;
    //default max 9
    public int mMaxSelectCount = 9;

    public  View mFragmentPagerView;

    public View mBaseView;

    public static List<Dropbox> mSelectedDropbox = new ArrayList<>();
    private View mBottomLayout;

    public String mRootName;

    public String mCurrentParentId = "";

    public View mRlSwitchLayout;
    public SwitchView mSwitch;

    public BroadcastReceiver mErrorCodeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int errorCode = intent.getIntExtra(ERROR_CODE, -1);
            if (!intent.getBooleanExtra("SHOW_ERROR", true)) {
                return;
            }
            ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Dropbox, errorCode, "");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropbox);
        initViews();
        refreshUI();
        initData();
        registerListener();
        registerReceiver();
    }

    private void initViews() {
        mBaseView = findViewById(R.id.dropbox_layout);
        mBackBtn = findViewById(R.id.back_btn);
        mIconLayout = findViewById(R.id.icon_layout);
        mFilterBtn = mIconLayout.findViewById(R.id.filter_btn);
        mSearchBtn = mIconLayout.findViewById(R.id.search_btn);
        mUploadBtn = mIconLayout.findViewById(R.id.upload_btn);
        mBottomLayout = findViewById(R.id.dropbox_bottom_layout);

        mTitle = findViewById(R.id.title_name);
        mSubTitle = findViewById(R.id.sub_title_name);

        mOrgSwitcherLayout = findViewById(R.id.org_switch_layout);
        mMyFileBtn = mOrgSwitcherLayout.findViewById(R.id.my_file_btn);
        mOrgFileBtn = mOrgSwitcherLayout.findViewById(R.id.org_file_btn);

        mSelectModeBottomLayout = findViewById(R.id.select_mode_bottom_layout);
        mDeleteBtn = mSelectModeBottomLayout.findViewById(R.id.del_btn);
        mMoveBtn = mSelectModeBottomLayout.findViewById(R.id.move_btn);


        mMoveModeBottomLayout = findViewById(R.id.move_mode_bottom_layout);
        mNewFolderBtn = mMoveModeBottomLayout.findViewById(R.id.new_folder_btn);
        mMoveToBtn = mMoveModeBottomLayout.findViewById(R.id.move_to_btn);

        mTitleLayout = findViewById(R.id.title_layout);
        mAllSelectBtn = mTitleLayout.findViewById(R.id.done_btn);
        mCancelBtn = mTitleLayout.findViewById(R.id.cancel_btn);
        mFragmentPagerView = findViewById(R.id.fragment_pager_layout);

        mSelectFileLayout = findViewById(R.id.select_file_statistics_layout);
        mSelectedFileSize = mSelectFileLayout.findViewById(R.id.file_selected_size);
        mSend = mSelectFileLayout.findViewById(R.id.send_btn);

        mRlSwitchLayout = findViewById(R.id.switch_rl_layout);
        mSwitch = mRlSwitchLayout.findViewById(R.id.switch_dropbox);
    }


    public void changeBottomStatusWhenSelected(Set<String> selectedSet) {
        Drawable deleteDrawable;
        Drawable moveDrawable;
        int deleteBtnColor;
        int moveBtnColor;
        int size = DensityUtil.dip2px(30);



        if(ListUtil.isEmpty(selectedSet)) {
            deleteDrawable = ContextCompat.getDrawable(this, R.mipmap.icon_del_file_unselected);
            deleteBtnColor = ContextCompat.getColor(this, R.color.dropbox_hint_text_color);

            moveDrawable = ContextCompat.getDrawable(this, R.mipmap.file_move_unselected);
            moveBtnColor = ContextCompat.getColor(this, R.color.dropbox_hint_text_color);

            mDeleteBtn.setClickable(false);
            mMoveBtn.setClickable(false);

        } else {
            deleteDrawable = ContextCompat.getDrawable(this, R.mipmap.icon_del_file_selected);
            deleteBtnColor = ContextCompat.getColor(this, R.color.red_lock);

            moveDrawable = ThemeResourceHelper.getThemeResourceBitmapDrawable(this, "file_move");
            moveBtnColor = SkinHelper.getTabActiveColor();


            mDeleteBtn.setClickable(true);
            mMoveBtn.setClickable(true);
        }

        deleteDrawable.setBounds(0, 0, size, size);
        mDeleteBtn.setCompoundDrawables(null, deleteDrawable, null, null);

        if(null != moveDrawable) {
            moveDrawable.setBounds(0, 0, size, size);
            mMoveBtn.setCompoundDrawables(null, moveDrawable, null, null);
        }

        mDeleteBtn.setTextColor(deleteBtnColor);
        mMoveBtn.setTextColor(moveBtnColor);

    }

    private void refreshUI() {
        WorkplusTextSizeChangeHelper.handleHeightEnlargedTextSizeStatus(mTitleLayout, 1.3f);

        Drawable newFolderDrawable = ThemeResourceHelper.getThemeResourceBitmapDrawable(this, "file_create_new_floder");
        Drawable moveToDrawable = ThemeResourceHelper.getThemeResourceBitmapDrawable(this, "file_move_to");

        int size = DensityUtil.dip2px(30);

        if(null != newFolderDrawable) {
            newFolderDrawable.setBounds(0, 0, size, size);
            mNewFolderBtn.setCompoundDrawables(null, newFolderDrawable, null, null);
        }

        if(null != moveToDrawable) {
            moveToDrawable.setBounds(0, 0, size, size);
            mMoveToBtn.setCompoundDrawables(null, moveToDrawable, null, null);

        }

        tempHandleIconColor();

    }

    private void tempHandleIconColor() {

        AtworkUtil.tempHandleIconColor(mFilterBtn);
        AtworkUtil.tempHandleIconColor(mSearchBtn);
        AtworkUtil.tempHandleIconColor(mUploadBtn);
    }

    private void initData() {
        mCurrentDomainId = getIntent().getStringExtra(KEY_INTENT_DOMAIN_ID);
        mCurrentSourceId = getIntent().getStringExtra(KEY_INTENT_SOURCE_ID);
        mCurrentSourceType = (Dropbox.SourceType) getIntent().getSerializableExtra(KEY_INTENT_SOURCE_TYPE);
        mMaxSelectCount = getIntent().getIntExtra(KEY_INTENT_SELECT_MAX, 9);

        mRootName = getIntent().getStringExtra(KEY_INTENT_TITLE);
        if (!TextUtils.isEmpty(mRootName)) {
            mTitle.setText(mRootName);
        }
        String subTitleName = getIntent().getStringExtra(KEY_INTENT_SUB_TITLE);
        if (TextUtils.isEmpty(subTitleName)) {
            return;
        }
        mSubTitle.setVisibility(View.VISIBLE);
        mSubTitle.setText(subTitleName);

    }

    /**
     * 修改标题
     * @param title
     */
    public void changeTitle(String title) {
        boolean hideTitle = true;
        if (TextUtils.isEmpty(title)) {
            title = mRootName;
            hideTitle = false;
        }
        mTitle.setText(title);
        hideSearchIcon(hideTitle);
    }

    public void hideSearchIcon(boolean hide) {
        mSearchBtn.setVisibility(hide ? View.GONE : View.VISIBLE);
    }

    private void registerListener() {
        mBottomLayout.setOnClickListener(view ->{});
        mBackBtn.setOnClickListener(view -> {
            onBackPressed();
        });

        mFilterBtn.setOnClickListener(view -> {
            SortedFileTypePopup popup = new SortedFileTypePopup(this);
            List<String> names = Arrays.asList(getResources().getStringArray(R.array.dropbox_sorted_file_type));
            List<Integer> iconResList = new ArrayList<>();
            iconResList.add(R.mipmap.icon_dropbox_file);
            iconResList.add(R.mipmap.icon_dropbpx_achive);
            iconResList.add(R.mipmap.icon_dropbox_image);
            iconResList.add(R.mipmap.icon_dropbox_video);
            iconResList.add(R.mipmap.icon_dropbox_audio);
            iconResList.add(R.mipmap.icon_dropbox_app);
            iconResList.add(R.mipmap.icon_dropbox_others);
            popup.addPopItem(names, iconResList);
            popup.setPopItemOnClickListener((title, pos) -> {
                Dropbox.DropboxFileType fileType = Dropbox.DropboxFileType.valueOf(pos + 1);
                Intent intent = SortedByFileTypeActivity.getIntent(DropboxBaseActivity.this,
                        fileType, mCurrentSourceType, mCurrentSourceId, mCurrentDomainId, mCurrentParentId );
                startActivity(intent);
                popup.dismiss();
            });

            popup.pop(mTitleLayout);
        });

        mSearchBtn.setOnClickListener(view -> {
            DropboxConfig dropboxConfig = DropboxConfigRepository.getInstance().getDropboxConfigBySourceId(mCurrentSourceId);
            Intent intent = DropboxSearchActivity.getIntent(this, mCurrentDomainId, mCurrentSourceType, mCurrentSourceId, dropboxConfig);
            startActivityForResult(intent, REQUEST_CODE_FILE_SEARCH);
        });

        mUploadBtn.setOnClickListener(view -> {
            Intent intent = FileSelectActivity.getIntent(this, FileSelectActivity.SelectMode.UPLOAD, false, true);
            startActivityForResult(intent, REQUEST_CODE_FILE_SELECT);
        });

        mAllSelectBtn.setOnClickListener(view -> {
            if (mCurrentFragment instanceof UserDropboxFragment) {
                ((UserDropboxFragment)mCurrentFragment).selectAll();
            }
        });

        mCancelBtn.setOnClickListener(view -> {
            changeDisplayMode(DisplayMode.Normal);
            if (mCurrentFragment instanceof UserDropboxFragment) {
                ((UserDropboxFragment)mCurrentFragment).onCancelClick();
            }
        });

        mDeleteBtn.setOnClickListener(view -> {
            if (mCurrentFragment instanceof UserDropboxFragment) {
                ((UserDropboxFragment)mCurrentFragment).showDelFileDialog();
            }
        });

        mMoveBtn.setOnClickListener(view -> {
//            changeDisplayMode(DisplayMode.Move);
            if (mCurrentFragment instanceof UserDropboxFragment) {
                ((UserDropboxFragment)mCurrentFragment).startMoveActivity();
            }
//            changeDisplayMode(DisplayMode.Normal);
        });


        mNewFolderBtn.setOnClickListener(view -> {
            if (mCurrentFragment instanceof UserDropboxFragment) {
                ((UserDropboxFragment)mCurrentFragment).onNewFolder();
            }
        });

        mMoveToBtn.setOnClickListener(view -> {
            if(CommonUtil.isFastClick(1500)) {
                return;
            }

            if (mCurrentFragment instanceof UserDropboxFragment) {
                ((UserDropboxFragment)mCurrentFragment).onDropboxMove();
            }
        });

        mSwitch.setOnClickCheckedListener(() -> {
            if (mSwitch.isChecked()) {

                return;
            }

        });

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
        if (requestCode == REQUEST_CODE_FILE_SEARCH && resultCode == RESULT_OK) {
            if (data == null){
                return;
            }
            Dropbox dropbox = data.getParcelableExtra(KEY_INTENT_SEARCH_DIR_SELECT);
            mUserDropboxFragment.onDropboxDirSelect(dropbox);
            return;
        }
        if (mCurrentFragment == null) {
            return;
        }
        mCurrentFragment.onActivityResult(requestCode, resultCode, data);

    }

    public void showWarningDialog() {
        AtworkAlertDialog dialog = new AtworkAlertDialog(this);
        dialog.setTitleText(R.string.upload_continue);
        dialog.setCancelable(false);
        dialog.setContent(R.string.mobile_network_warning);
        dialog.setBrightBtnText(R.string.ok);
        dialog.setDeadBtnText(R.string.cancel);
        dialog.setClickBrightColorListener(dialog1 -> {
            uploadFilesToDropbox();
            dialog.dismiss();

        });
        dialog.setClickDeadColorListener(dialog1 -> {
            mSelectedFileData = null;
            dialog.dismiss();
            return;
        });
        dialog.showBrightBtn();
        dialog.showDeadBtn();
        dialog.show();
    }

    public void uploadFilesToDropbox() {
        if (mCurrentFragment instanceof UserDropboxFragment) {
            for (FileData fileData : mSelectedFileData) {
                if (DropboxManager.getInstance().isOverPanItemLimitAndAlert(fileData.size, DropboxManager.getInstance().getPanSettingTypeBySourceId(this, mCurrentSourceId, mCurrentSourceType))) {
                    return;
                }

            }

            ((UserDropboxFragment)mCurrentFragment).uploadFilesToDropbox(mSelectedFileData, mCurrentDomainId, mCurrentSourceId, mCurrentSourceType);
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

    public void changeBottomVisual(DisplayMode displayMode) {
        mCurrentDisplayMode = displayMode;
        mOrgSwitcherLayout.setVisibility( View.GONE);
        mSelectModeBottomLayout.setVisibility(mCurrentDisplayMode.equals(DisplayMode.Select) ? View.VISIBLE : View.GONE);
        mMoveModeBottomLayout.setVisibility(mCurrentDisplayMode.equals(DisplayMode.Move) || mCurrentDisplayMode.equals(DisplayMode.Copy) ? View.VISIBLE : View.GONE);
        if (displayMode.equals(DisplayMode.Copy) || displayMode.equals(DisplayMode.Move)) {
            mMoveToBtn.setText(displayMode.equals(DisplayMode.Move) ? getString(R.string.move_to_current_folder) : getString(R.string.save_to_current_folder));
            Drawable moveToDrawable;
            if (displayMode.equals(DisplayMode.Move)) {
                moveToDrawable = ThemeResourceHelper.getThemeResourceBitmapDrawable(this, "file_move_to");
            } else {
               moveToDrawable = ThemeResourceHelper.getThemeResourceBitmapDrawable(this, "file_save_to");
            }

            int size = DensityUtil.dip2px(30);
            if(null != moveToDrawable) {
                moveToDrawable.setBounds(0, 0, size, size);
                mMoveToBtn.setCompoundDrawables(null, moveToDrawable, null, null);

            }
        }
        mSelectFileLayout.setVisibility(mCurrentDisplayMode.equals(DisplayMode.Send) ? View.VISIBLE : View.GONE);

    }

    public void  changeTitleVisual() {
        mCancelBtn.setVisibility(mCurrentDisplayMode.equals(DisplayMode.Select) ? View.VISIBLE : View.GONE);
        mAllSelectBtn.setVisibility(mCurrentDisplayMode.equals(DisplayMode.Select) ? View.VISIBLE : View.GONE);
        mBackBtn.setVisibility(mCurrentDisplayMode.equals(DisplayMode.Select)  ? View.GONE : View.VISIBLE);
        mIconLayout.setVisibility(mCurrentDisplayMode.equals(DisplayMode.Normal) ? View.VISIBLE : View.GONE);
    }

    public void setSelectMode(boolean selectMode) {
        if (mCurrentFragment != null && mCurrentFragment instanceof UserDropboxFragment) {
            ((UserDropboxFragment)mCurrentFragment).updateSelectedVisual(selectMode ? DisplayMode.Select : DisplayMode.Normal);
        }
    }

    public void setMoveMode() {
        if (mCurrentFragment != null && mCurrentFragment instanceof UserDropboxFragment) {
            ((UserDropboxFragment)mCurrentFragment).updateMoveVisual(DisplayMode.Move);
        }
    }

    public void setNormalMode() {
        if (mCurrentFragment != null && mCurrentFragment instanceof UserDropboxFragment) {
            ((UserDropboxFragment)mCurrentFragment).updateNormalVisual(DisplayMode.Normal);
        }
    }

    public boolean isSelectMaxFile() {
        if (mSelectedDropbox.size() >= mMaxSelectCount) {
            AtworkToast.showResToast(R.string.max_select_file_num, mMaxSelectCount);
            return true;
        }
        return false;
    }

    public void hideBottomLayout() {
        mOrgSwitcherLayout.setVisibility(View.GONE);
    }


    public void onScopeDropboxSelect(Dropbox dropbox, Set<String> selectedSet) {

        if (isInSelectedDropbox(dropbox)) {
            removeSelectedDropbox(dropbox);
            if (selectedSet.contains(dropbox.mFileId)) {
                selectedSet.remove(dropbox.mFileId);
            }
        } else {
            if (isSelectMaxFile()) {
                return;
            }
            mSelectedDropbox.add(dropbox);
            selectedSet.add(dropbox.mFileId);
        }
        onSelectFileSizeUpdate();
    }

    //todo 暂时不重写 equals 跟hashcode, 防止引起 bug
    private boolean isInSelectedDropbox(Dropbox dropbox) {
        for(Dropbox selectDropbox : mSelectedDropbox) {
            if(selectDropbox.mFileId.equals(dropbox.mFileId)) {
                return true;
            }
        }

        return false;
    }

    //todo 暂时不重写 equals 跟hashcode, 防止引起 bug
    private void removeSelectedDropbox(Dropbox dropbox) {
        Dropbox dropboxRemoved = null;
        for(Dropbox selectDropbox : mSelectedDropbox) {
            if(selectDropbox.mFileId.equals(dropbox.mFileId)) {
                dropboxRemoved = selectDropbox;
            }
        }

        if (null != dropboxRemoved) {
            mSelectedDropbox.remove(dropboxRemoved);
        }
    }

    public void setBottomSelector(int selector) {

        Drawable myFileDrawableTop = null;
        Drawable orgFileDrawableTop = null;
        int myFileColor;
        int orgFileColor;
        int size = DensityUtil.dip2px(30);

        if(0 == selector) {
            myFileColor = SkinHelper.getTabActiveColor();
            orgFileColor = ContextCompat.getColor(this, R.color.dropbox_hint_text_color);


            myFileDrawableTop = ThemeResourceHelper.getThemeResourceBitmapDrawable(this, "my_file_selected");
            if(null == myFileDrawableTop) {
                myFileDrawableTop = ContextCompat.getDrawable(this, R.mipmap.my_file_selected);
            }
            orgFileDrawableTop = ContextCompat.getDrawable(this, R.mipmap.org_file_unselected);

        } else {
            myFileColor = ContextCompat.getColor(this, R.color.dropbox_hint_text_color);
            orgFileColor = SkinHelper.getTabActiveColor();

            orgFileDrawableTop = ThemeResourceHelper.getThemeResourceBitmapDrawable(this, "org_file_selected");
            if(null == orgFileDrawableTop) {
                orgFileDrawableTop = ContextCompat.getDrawable(this, R.mipmap.org_file_selected);
            }
            myFileDrawableTop = ContextCompat.getDrawable(this, R.mipmap.my_file_unselected);
        }

        myFileDrawableTop.setBounds(0, 0, size, size);
        orgFileDrawableTop.setBounds(0, 0, size, size);

        mMyFileBtn.setCompoundDrawables(null, myFileDrawableTop, null, null);
        mOrgFileBtn.setCompoundDrawables(null, orgFileDrawableTop, null, null);
        mOrgFileBtn.setTextColor(orgFileColor);
        mMyFileBtn.setTextColor(myFileColor);
    }

    public void onSelectFileSizeUpdate() {
        long size = 0;
        for (Dropbox dropbox : mSelectedDropbox) {
            if (dropbox == null) {
                continue;
            }
            size = size + dropbox.mFileSize;
        }
        mSelectedFileSize.setText(String.format(getString(R.string.already_select), FileHelper.getFileSizeStr(size)));
        if (mSelectedDropbox.isEmpty()) {
            mSend.setText(getString(R.string.button_send));
            mSend.setTextColor(getResources().getColor(R.color.common_text_color_999));
            mSend.setBackgroundResource(R.mipmap.icon_unsend_btn_bg);
        } else {
            mSend.setText(getString(R.string.button_send) + "(" + mSelectedDropbox.size() + "/" + mMaxSelectCount + ")");
            mSend.setTextColor(getResources().getColor(R.color.white));
            mSend.setBackgroundResource(R.mipmap.icon_send_btn_bg);
        }

    }

    protected void showSwitch(boolean show) {
        mSwitch.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setTitleButtonStatus(boolean show) {
        mIconLayout.setVisibility(!mCurrentDisplayMode.equals(DisplayMode.Select) && show ? View.VISIBLE : View.GONE);
    }

    private void registerReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mErrorCodeReceiver, new IntentFilter(ACTION_FILTER_ERR_CODE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mErrorCodeReceiver);
    }

    public static final void refreshDropboxData() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(UserDropboxFragment.ACTION_DROPBOX_DATA_FRESH));
    }



    @Nullable
    @Override
    public BiometricAuthenticationProtectItemType getBiometricAuthenticationProtectItemTag() {
        return BiometricAuthenticationProtectItemType.DROPBOX;
    }

}
