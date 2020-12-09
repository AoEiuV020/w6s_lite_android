package com.foreveross.atwork.modules.file.fragement;

import androidx.fragment.app.FragmentActivity;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.modules.file.activity.FileSelectActivity;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.FileHelper;

import java.util.List;

/**
 * Created by dasunsy on 2017/5/22.
 */

public abstract class BasicFileSelectFragment extends BackHandledFragment {

    @Override
    protected boolean onBackPressed() {
        return false;
    }

    /**
     * 检查文件是否能够合法进行勾选
     * @param fileData
     * @return 是否能够勾选
     * */
    protected boolean checkFileSelected(FileData fileData) {
        if(isSingleSelectType()) {

            if(isTotalChooseSizeThreshold(fileData)) {
                AtworkToast.showToast(getString(R.string.max_total_select_file_size, FileHelper.getFileSizeStr(getMaxTotalChooseSize())));
                return true;
            }

            refreshFileData(fileData);
            ((FileSelectActivity)mActivity).doSubmit();

            return true;
        }

        if (isChosenCountThreshold()) {
            AtworkToast.showToast(getString(R.string.max_select_file_num, getMaxChooseCount() + ""));
            return true;
        }

        if(isSingleChooseSizeThreshold(fileData)) {
            AtworkToast.showToast(getString(R.string.max_single_select_file_size, FileHelper.getFileSizeStr(getMaxSingleChooseSize())));
            return true;
        }

        if(isTotalChooseSizeThreshold(fileData)) {
            AtworkToast.showToast(getString(R.string.max_total_select_file_size, FileHelper.getFileSizeStr(getMaxTotalChooseSize())));
            return true;
        }

        return false;
    }

    protected void refreshFileData(FileData fileData){

    }


    protected List<FileData> getFileSelectedList(){
        return null;
    }

    public boolean isFromCordova() {
        FragmentActivity activity = getActivity();
        if(activity instanceof FileSelectActivity) {
            return ((FileSelectActivity)activity).isFromCordova();
        }
        return false;
    }

    public boolean isSingleSelectType() {
        FragmentActivity activity = getActivity();
        if(activity instanceof FileSelectActivity) {
            return ((FileSelectActivity)activity).isSingleSelectType();
        }

        return false;
    }

    private boolean isChosenCountThreshold() {
        return getFileSelectedList().size() >= getMaxChooseCount();
    }

    private boolean isSingleChooseSizeThreshold(FileData selectFileData) {
        long maxSingleChooseSize = getMaxSingleChooseSize();
        return -1 != maxSingleChooseSize && selectFileData.size > maxSingleChooseSize;
    }

    private boolean isTotalChooseSizeThreshold(FileData selectFileData) {
        long maxTotalChooseSize = getMaxTotalChooseSize();
        if(-1 != maxTotalChooseSize) {
            //get total size
            int totalSize = 0;
            for(FileData fileData : getFileSelectedList()) {
                totalSize += fileData.size;
            }

            totalSize += selectFileData.size;

            return totalSize > maxTotalChooseSize;
        }

        return  false;
    }

    private int getMaxChooseCount() {
        return ((FileSelectActivity) mActivity).getMaxChooseCount();
    }

    private long getMaxSingleChooseSize() {
        return ((FileSelectActivity) mActivity).getMaxSingleChooseSize();
    }

    private long getMaxTotalChooseSize() {
        return ((FileSelectActivity) mActivity).getMaxTotalChooseSize();
    }

}
