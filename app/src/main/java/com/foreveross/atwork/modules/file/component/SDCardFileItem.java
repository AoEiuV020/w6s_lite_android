package com.foreveross.atwork.modules.file.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.model.file.SDCardFileData;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.file.util.FileMediaTypeUtil;
import com.foreveross.atwork.utils.FileHelper;

import java.util.List;


public class SDCardFileItem extends RelativeLayout {

    private static final String TAG = SDCardFileItem.class.getSimpleName();

    private Context mContext;

    private TextView mFileSize;
    private TextView mFileName;
    private ImageView mFileIcon;
    private TextView mDirName;
    private ImageView mDirIcon;
    public CheckBox mSelectBtn;

    public SDCardFileItem(Context context) {
        super(context);
        if (context == null) {
            throw new IllegalArgumentException("invalid argument on " + TAG);
        }
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_sdcard_file, this);

        mFileSize = (TextView)view.findViewById(R.id.item_file_size);
        mFileName = (TextView)view.findViewById(R.id.item_file_name);
        mFileIcon = (ImageView)view.findViewById(R.id.item_file_icon);
        mDirIcon = (ImageView)view.findViewById(R.id.item_dir_icon);
        mDirName = (TextView)view.findViewById(R.id.item_dir_name);
        mSelectBtn = (CheckBox)view.findViewById(R.id.item_file_select_checkbox);
    }

    public void setFileInfo(SDCardFileData.FileInfo fileInfo, List<FileData> selectedFileList, boolean cordovaSingleFile) {
        if (fileInfo.directory()) {
            mFileSize.setVisibility(View.GONE);
            mFileIcon.setVisibility(View.GONE);
            mFileName.setVisibility(View.GONE);
            mSelectBtn.setVisibility(View.GONE);
            mDirIcon.setVisibility(View.VISIBLE);
            mDirName.setVisibility(View.VISIBLE);

            mDirName.setText(fileInfo.name);
        } else {
            mFileSize.setVisibility(View.VISIBLE);
            mFileIcon.setVisibility(View.VISIBLE);
            mFileName.setVisibility(View.VISIBLE);

            hideCheckbox(cordovaSingleFile);
            mDirIcon.setVisibility(View.GONE);
            mDirName.setVisibility(View.GONE);

            mFileName.setText(StringUtils.middleEllipse(fileInfo.name, 26, 8, 4, 7));
            mFileSize.setText(FileHelper.getFileSizeStr(fileInfo.size));
            setFileIcon(fileInfo);
            setFileSelectedStatus(fileInfo, selectedFileList);
        }

    }

    private void setFileIcon(SDCardFileData.FileInfo fileInfo) {
        mFileIcon.setImageResource(FileMediaTypeUtil.getFileTypeIcon(fileInfo.type));
    }

    private void setFileSelectedStatus(SDCardFileData.FileInfo fileInfo, List<FileData> selectedFileList) {
        if (selectedFileList != null) {
            for (FileData fileData : selectedFileList) {
                if (fileData.filePath.equalsIgnoreCase(fileInfo.path)) {
                    fileInfo.selected = true;
                }
            }
        }
        mSelectBtn.setChecked(fileInfo.selected);
    }


    public void hideCheckbox(boolean hide) {
        if(hide) {
            mSelectBtn.setVisibility(GONE);

        } else {
            mSelectBtn.setVisibility(VISIBLE);

        }
    }


}
