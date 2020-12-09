package com.foreveross.atwork.modules.file.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.modules.file.component.FileItemLinearLayoutView;

import java.util.List;

/**
 * Created by lingen on 15/3/30.
 * Description:
 */
public class FileArrayListViewAdapter extends BaseAdapter {

    private static final String TAG = FileArrayListViewAdapter.class.getSimpleName();

    private Activity mActivity;
    private List<FileData> mFileDataList;
    private List<FileData> mSelectedFileList;

    private boolean mCordovaSingleFile = false;

    public FileArrayListViewAdapter(Activity activity, List<FileData> fileList, List<FileData> selectedFileList, boolean cordovaSingleFile) {
        if (activity == null || fileList == null || selectedFileList == null) {
            throw new IllegalArgumentException("invalid arguments on " + TAG);
        }
        mActivity = activity;
        mFileDataList = fileList;
        mSelectedFileList = selectedFileList;

        mCordovaSingleFile = cordovaSingleFile;
    }

    public void setFileDataList(List<FileData> fileDataList) {
        mFileDataList = fileDataList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mFileDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFileDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new FileItemLinearLayoutView(mActivity);
        }
        FileItemLinearLayoutView view  = (FileItemLinearLayoutView)convertView;

        view.hideCheckbox(mCordovaSingleFile);

        FileData fileData = mFileDataList.get(position);
        if (fileData == null) {
            return convertView;
        }
        view.setFileData(fileData, mSelectedFileList);
        return convertView;
    }
}
