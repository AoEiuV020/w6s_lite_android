package com.foreveross.atwork.component;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.workplus.module.file_share.FileShareAction;
import com.foreverht.workplus.module.file_share.activity.FileShareActivity;
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreverht.workplus.ui.component.dialogFragment.CommonPopSelectData;
import com.foreverht.workplus.ui.component.dialogFragment.W6sSelectDialogFragment;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk;
import com.foreveross.atwork.manager.DropboxManager;
import com.foreveross.atwork.modules.downLoad.activity.ImageSwitchInDownloadActivity;
import com.foreveross.atwork.modules.downLoad.adapter.DownloadFileListAdapter;
import com.foreveross.atwork.modules.downLoad.component.DownLoadFileAttrDialog;
import com.foreveross.atwork.modules.downLoad.component.DownloadFileWithMonthTitleItem;
import com.foreveross.atwork.modules.downLoad.component.MyDownloadFileItem;
import com.foreveross.atwork.modules.downLoad.fragment.DownloadFileDetailFragment;
import com.foreveross.atwork.modules.dropbox.activity.SaveToDropboxActivity;
import com.foreveross.atwork.modules.dropbox.fragment.UserDropboxFragment;
import com.foreveross.atwork.modules.file.dao.RecentFileDaoService;
import com.foreveross.atwork.modules.group.activity.TransferMessageActivity;
import com.foreveross.atwork.modules.group.module.TransferMessageControlAction;
import com.foreveross.atwork.modules.group.module.TransferMessageMode;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.OfficeHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.foreveross.atwork.modules.downLoad.activity.MyDownLoadActivity.REFRESH_DOWN_LOAD_VIEW_PAGER;
import static com.foreveross.atwork.modules.downLoad.component.DownloadFileDetailView.MSG_DELETE_RECENT_DOWN_LOAD_FILE_SUCCESS;
import static com.foreveross.atwork.modules.file.activity.OfficeViewActivity.VIEW_FROM_FILE_DATA_LIST;
import static com.foreveross.atwork.modules.file.fragement.FileSelectFragment.MSG_GET_RECENT_FILES_SUCCESS;

/**
 * Created by wuzejie on 2020/1/10.
 */
public class DownloadPagerView extends LinearLayout{

    //广播action：
    public static final String REFRESH_DOWN_LOAD_FILE_LIST = "REFRESH_DOWN_LOAD_FILE_LIST";

    private Activity mActivity;
    private List<FileData> mFileDataList;
    private ListView mMyDownloadList;
    private int mPosition;

    private  DownloadFileDetailFragment mDownloadFileDetailFragment;
    private DownloadFileListAdapter mDownloadFileListAdapter;
    private int mWhat;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mWhat = msg.what;
            switch (mWhat) {
                case MSG_DELETE_RECENT_DOWN_LOAD_FILE_SUCCESS:
                    sendBroadCast();
                    refreshListAdapter();
                    break;

                case MSG_GET_RECENT_FILES_SUCCESS:
                    mFileDataList = (List<FileData>) msg.obj;
                    mFileDataList = updataFileList(mPosition);
                    dismissDownloadFileDetailFragment();
                    mDownloadFileListAdapter.setFileDataList(mFileDataList);

                    break;
            }
        }
    };


    private BroadcastReceiver mRefreshViewBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (REFRESH_DOWN_LOAD_FILE_LIST.equals(action)) {
                refreshListAdapter();
            }
        }
    };
    public void refreshListAdapter() {
        getFileData();

    }

    private void registerBroadcast() {
        IntentFilter refreshViewIntentFilter = new IntentFilter();
        refreshViewIntentFilter.addAction(REFRESH_DOWN_LOAD_FILE_LIST);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mRefreshViewBroadcastReceiver, refreshViewIntentFilter);
    }
    private void unregisterBroadcast() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mRefreshViewBroadcastReceiver);
    }
    private void clearBroadcast() {
        mRefreshViewBroadcastReceiver = null;
    }

    public void unregisterAndClearBroadcast(){
        unregisterBroadcast();
        clearBroadcast();
    }

    public static void sendBroadCast() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(REFRESH_DOWN_LOAD_VIEW_PAGER));
    }

    /**
     * Descripition:获取文件列表
     */
    private void getFileData(){
        //查询最近文件数据库
        RecentFileDaoService.getInstance().getRecentFiles(mHandler);
    }

    /**
     * Description:根据文件类型更新文件列表
     */
    private List<FileData> updataFileList(int type) {
        List<FileData> fileDataList = new ArrayList<FileData>();
        if(type != 0){
            for (int i = 0; i < mFileDataList.size(); i++) {
                if (FileData.getFileType(mFileDataList.get(i).fileType) == type) {
                    fileDataList.add(mFileDataList.get(i));
                }
            }
            return fileDataList;
        } else {
            return mFileDataList;
        }
    }

    public DownloadPagerView(Context context, List<FileData> fileDataList, int position) {
        super(context);
        mActivity = (Activity) context;
        mFileDataList = fileDataList;
        mPosition = position;
        initView();
        registerBroadcast();
    }

    public DownloadPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mActivity = (Activity) context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View view = inflater.inflate(R.layout.view_download_pager, this);
        mMyDownloadList = view.findViewById(R.id.my_download_list);
        mDownloadFileListAdapter = new DownloadFileListAdapter(mActivity, mFileDataList);
        mMyDownloadList.setAdapter(mDownloadFileListAdapter);
        mDownloadFileListAdapter.setIconSelectListener(new MyDownloadFileItem.OnItemIconClickListener() {
            @Override
            public void onExpandIconClick(FileData fileData) {
                handleMyDownloadItemLongClick(fileData);
            }
        });
        mDownloadFileListAdapter.setIconSelectListener((DownloadFileWithMonthTitleItem.OnItemIconClickListener) fileData -> handleMyDownloadItemLongClick(fileData));

        mMyDownloadList.setOnItemClickListener((adapterView, viewItem, i, l) -> {
            FileData fileData = mFileDataList.get(i);
            if(shouldPreviewLocal(fileData)){
                previewLocal(fileData);
            }
            else if(fileData.fileType == FileData.FileType.File_Image){
                ImageSwitchInDownloadActivity.showImageSwitchView(mActivity, fileData);
            }
            else if(fileData.fileType == FileData.FileType.File_Gif){
                ImageSwitchInDownloadActivity.showImageSwitchView(mActivity, fileData);

            } else{
                FragmentActivity fragmentActivity = (FragmentActivity)mActivity;
                mDownloadFileDetailFragment = new DownloadFileDetailFragment();
                mDownloadFileDetailFragment.initBundle(fileData);
                mDownloadFileDetailFragment.show(fragmentActivity.getSupportFragmentManager(), "DOWNLOAD_FILE_DETAIL_FRAGMENT");
            }

        });
        mMyDownloadList.setOnItemLongClickListener((parent, viewItem, position, id) -> {
            handleMyDownloadItemLongClick(mFileDataList.get(position));
            return true;
        });
    }
    /**
     * Description:判断是否直接预览
     * @param fileData
     * @return
     */
    public boolean shouldPreviewLocal(FileData fileData) {
        if(OfficeHelper.isSupportType(fileData.filePath)) {
            return true;
        }
        return false;
    }
    /**
     * Description:本地预览
     */
    public void previewLocal(FileData fileData) {
        EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(fileData.filePath, false, fileName -> {

            //OfficeHelper.previewByX5(getContext(), fileName);
            OfficeHelper.previewByX5(getContext(), fileData.filePath, null, fileData, VIEW_FROM_FILE_DATA_LIST);

        });
    }


    /**
     * Description:初始化子项长按弹窗
     */

    private void handleMyDownloadItemLongClick(FileData fileData) {
        FragmentActivity fragmentActivity = (FragmentActivity)mActivity;
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();

        List<String> items = new ArrayList<>();
        items.add(mActivity.getString(R.string.forwarding_item));
        items.add(mActivity.getString(R.string.share));
        items.add(mActivity.getString(R.string.save_to_dropbox));
        items.add(mActivity.getString(R.string.send_email));
        items.add(mActivity.getString(R.string.file_attr));
        items.add(mActivity.getString(R.string.delete));

        ArrayList<String> itemList = new ArrayList<>();
        itemList.addAll(items);
        W6sSelectDialogFragment w6sSelectDialogFragment = new W6sSelectDialogFragment();
        w6sSelectDialogFragment.setData(new CommonPopSelectData(itemList, null));
        w6sSelectDialogFragment.setDialogWidth(160);
        w6sSelectDialogFragment.setOnClickItemListener(new W6sSelectDialogFragment.OnClickItemListener() {
            @Override
            public void onClick(int position, @NotNull String value) {
                if (position == 0) {

                    List<ChatPostMessage> messages = new ArrayList<>();

                    User LoginUser = AtworkApplicationLike.getLoginUserSync();
                    long overtime = DomainSettingsManager.getInstance().handleChatFileExpiredFeature() ? TimeUtil.getTimeInMillisDaysAfter(DomainSettingsManager.getInstance().getChatFileExpiredDay()) : -1;
                    FileTransferChatMessage message = FileTransferChatMessage.newFileTransferChatMessage(AtworkApplicationLike.baseContext, fileData, LoginUser, "",
                            ParticipantType.User, ParticipantType.User, "", "", "", BodyType.File, "", overtime, null);
                    message.mediaId = fileData.mediaId;
                    messages.add(message);


                    TransferMessageControlAction transferMessageControlAction = new TransferMessageControlAction();
                    transferMessageControlAction.setSendMessageList(messages);
                    transferMessageControlAction.setSendMode(TransferMessageMode.SEND);
                    Intent intent = TransferMessageActivity.Companion.getIntent(BaseApplicationLike.baseContext, transferMessageControlAction);

                   mActivity.startActivity(intent);
                }
                if (position == 1) {
                    shareFile(fileData);
                }

                if (position == 2) {
                    FileTransferChatMessage fileTransferChatMessage = FileTransferChatMessage.getFIleTransferChatMessageFromFileData(fileData);
                    Dropbox dropboxTransfer = Dropbox.convertFromChatPostMessage(mActivity, fileTransferChatMessage);
                    Intent intent = SaveToDropboxActivity.getIntent(mActivity, dropboxTransfer, fileTransferChatMessage);
                    mActivity.startActivityForResult(intent, UserDropboxFragment.REQUEST_CODE_COPY_DROPBOX);
                }
                if (position == 3) {
                    Dropbox dropbox = Dropbox.convertFromFilePath(getContext(), fileData.filePath, fileData.mediaId);
                    doCommandSendEmail(mActivity, fragmentActivity.getSupportFragmentManager(), dropbox);
                }
                if (position == 4) {
                    doCommandFileAttr(fileData);
                }
                if (position == 5) {
                    deleteFile(fileData);
                }

            }

        });

        w6sSelectDialogFragment.show(fragmentManager, "TEXT_POP_DIALOG");
    }

    /**
     * 通过邮件发送
     */
    public void doCommandSendEmail(Activity context, FragmentManager fragmentManager, Dropbox dropbox) {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mActivity, new String[]{Manifest.permission.WRITE_CONTACTS}, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                DropboxManager.getInstance().doCommandSendEmail(mActivity, fragmentManager, dropbox);

            }

            @Override
            public void onDenied(String permission) {
                final AtworkAlertDialog alertDialog = AtworkUtil.getAuthSettingAlert(mActivity, permission);
                alertDialog.setOnDismissListener(dialog -> {
                    if(alertDialog.shouldHandleDismissEvent) {
                        DropboxManager.getInstance().doCommandSendEmail(mActivity, fragmentManager, dropbox);


                    }

                });

                alertDialog.show();
            }
        });
    }
    /**
     * 查看文件属性
     */
    public void doCommandFileAttr(FileData fileData) {
        FragmentActivity fragmentActivity = (FragmentActivity)mActivity;
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        DownLoadFileAttrDialog dialog = new DownLoadFileAttrDialog();
        dialog.setArguments(dialog.setData(fileData));
        dialog.show(fragmentManager, "download_attr");
    }

    /**
     * Description:删除本地数据库记录
     * @param fileData
     */
    public void deleteFile(FileData fileData){
        AtworkAlertDialog dialog = new AtworkAlertDialog(mActivity);
        dialog.setTitleText(R.string.delete_file);
        dialog.setContent(R.string.delete_file_tip);
        dialog.setBrightBtnText(R.string.ok);
        dialog.setDeadBtnText(R.string.cancel);
        dialog.setClickDeadColorListener(dialog1 -> dialog.dismiss());
        dialog.setClickBrightColorListener(dialog1 -> {
            RecentFileDaoService.getInstance().deleteDownloadFileByFileId(mHandler, fileData.mediaId, fileData.filePath);
        });
        dialog.show();
    }
    /**
     * 分享文件
     */
    public void shareFile(FileData fileData){
        FileShareAction fileShareAction = new FileShareAction();
        fileShareAction.setDomainId(AtworkConfig.DOMAIN_ID);
        fileShareAction.setOpsId(LoginUserInfo.getInstance().getLoginUserId(mActivity));
        fileShareAction.setType("file_id");
        fileShareAction.setSourceType(Dropbox.SourceType.User);
        fileShareAction.setFileId(fileData.mediaId);
        FileShareActivity.Companion.startActivity(mActivity, fileShareAction);
    }

    private void dismissDownloadFileDetailFragment(){
        if(mDownloadFileDetailFragment != null)
            mDownloadFileDetailFragment.dismiss();
    }


}
