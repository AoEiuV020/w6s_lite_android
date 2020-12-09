package com.foreveross.atwork.modules.bing.listener.download;

import com.foreverht.db.service.daoService.FileDaoService;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.model.file.FileStatusInfo;
import com.foreveross.atwork.infrastructure.model.file.SDCardFileData;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus;
import com.foreveross.atwork.modules.bing.listener.UpdateFileDataListener;
import com.foreveross.atwork.modules.file.fragement.CommonFileStatusFragment;
import com.foreveross.atwork.utils.AtworkToast;

/**
 * Created by dasunsy on 2017/9/18.
 */

public class CommonFileStatusViewDownloadListener implements MediaCenterNetManager.MediaDownloadListener{

    public FileStatusInfo mFileStatusInfo;

    public UpdateFileDataListener mUpdateFileDataListener;

    public CommonFileStatusViewDownloadListener(FileStatusInfo fileStatusInfo, UpdateFileDataListener updateFileDataListener) {
        this.mFileStatusInfo = fileStatusInfo;
        this.mUpdateFileDataListener = updateFileDataListener;
    }

    @Override
    public String getMsgId() {
        return mFileStatusInfo.getKeyId();
    }

    @Override
    public void downloadSuccess() {
        if (FileStatus.DOWNLOADING.equals(mFileStatusInfo.getFileStatus())) {
//            mFileStatusInfo.setPath(AtWorkDirUtils.getInstance().getFiles(BaseApplicationLike.baseContext) + mFileStatusInfo.getName());
            mFileStatusInfo.setFileStatus(FileStatus.DOWNLOADED);

            mUpdateFileDataListener.update(mFileStatusInfo);

        }

        MediaCenterNetManager.removeMediaDownloadListener(this);
        CommonFileStatusFragment.refreshUI();
        updateRecentFileDB(mFileStatusInfo);
    }
    /**
     * Description:更新最近文件本地数据库
     * @param fileStatusInfo
     */

    private void updateRecentFileDB(FileStatusInfo fileStatusInfo) {
        SDCardFileData.FileInfo fileInfo = new SDCardFileData.FileInfo(fileStatusInfo.getPath());
        FileData fileData = fileInfo.getFileData(fileInfo);
        if (fileData == null) {
            return;
        }
        fileData.mediaId = fileStatusInfo.getMediaId();
        fileData.isDownload = 1;

        FileDaoService.getInstance().insertRecentFile(fileData);
    }

    @Override
    public void downloadFailed(int errorCode, String errorMsg, boolean doRefreshView) {
        if (errorCode != -99) {
            AtworkToast.showToast(AtworkApplicationLike.getResourceString(R.string.download_file_fail));
        }

        if (errorCode == -99) {
            mFileStatusInfo.setFileStatus(FileStatus.NOT_DOWNLOAD);
        } else {
            mFileStatusInfo.setFileStatus(FileStatus.DOWNLOAD_FAIL);
        }

        mUpdateFileDataListener.update(mFileStatusInfo);
        MediaCenterNetManager.removeMediaDownloadListener(this);

        if (doRefreshView) {
            CommonFileStatusFragment.refreshUI();
        }

    }

    @Override
    public void downloadProgress(double progress, double value) {
        mFileStatusInfo.setProgress((int) progress);
        CommonFileStatusFragment.refreshUI();
    }
}
