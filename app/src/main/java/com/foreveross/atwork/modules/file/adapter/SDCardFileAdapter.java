package com.foreveross.atwork.modules.file.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.model.file.SDCardFileData;
import com.foreveross.atwork.modules.file.component.SDCardFileItem;

import java.util.List;


public class SDCardFileAdapter extends BaseAdapter {

    private static final String TAG = SDCardFileAdapter.class.getSimpleName();

    private Context mContext;
    private SDCardFileData mFileData;
    private List<FileData> mSelectedFileList;

    private boolean mSingleFile;

    public SDCardFileAdapter(Context context, SDCardFileData fileData, List<FileData> selectedFileList, boolean singleFile) {
        if (context == null || fileData == null || selectedFileList == null) {
            throw new IllegalArgumentException("invalid arguments on " + TAG);
        }
        mContext = context;
        mFileData = fileData;
        mSelectedFileList = selectedFileList;
        mSingleFile = singleFile;
    }

    public void setFileData(SDCardFileData fileData, List<FileData> selectedFileList) {
        mFileData = fileData;
        mSelectedFileList = selectedFileList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mFileData.fileInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mFileData.fileInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new SDCardFileItem(mContext);
        }

        SDCardFileItem fileItem = (SDCardFileItem) convertView;

        fileItem.setFileInfo(mFileData.fileInfos.get(position), mSelectedFileList, mSingleFile);

        return convertView;
    }
}
