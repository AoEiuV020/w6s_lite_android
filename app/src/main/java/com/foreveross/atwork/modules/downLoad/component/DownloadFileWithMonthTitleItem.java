package com.foreveross.atwork.modules.downLoad.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.file.util.FileMediaTypeUtil;
import com.foreveross.atwork.utils.FileHelper;
import com.foreveross.atwork.utils.TimeViewUtil;

/**
 * Created by wuzejie on 20/1/11.
 */
public class DownloadFileWithMonthTitleItem extends RelativeLayout {


    /**
     * 月份区域
     */
    private TextView mFileGroupTime;
    /**
     * 右侧扩展的图标
     */
    private ImageView mExpandIcon;
    /**
     * 文件图标
     */
    private ImageView mFileIcon;
    /**
     * 文件名称
     */
    private TextView mFileName;
    /**
     * 文件修改日期
     */
    private TextView mFileModifyTime;
    /**
     * 文件大小
     */
    private TextView mFileSize;

    /**
     * 右侧扩展的图标点击事件监听
     */
    private OnItemIconClickListener mOnIconSelectedListener;
    private Context mContext;

    private String abc = "";


    public DownloadFileWithMonthTitleItem(Context context) {
        super(context);
        mContext = context;
        initViews(context);
    }



    private void initViews(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_my_download_file, this);

        mExpandIcon = view.findViewById(R.id.file_expand_icon);
        mFileIcon = view.findViewById(R.id.file_icon);
        mFileName = view.findViewById(R.id.file_name);
        mFileModifyTime = view.findViewById(R.id.file_modify_time);
        mFileSize = view.findViewById(R.id.file_size);
        mFileGroupTime = view.findViewById(R.id.file_group_time);
    }

    public void setGroupTime(long fileData, String groupTime) {

        mFileGroupTime.setText(groupTime);
        mFileGroupTime.setVisibility(VISIBLE);

    }

    public void setSize(long size) {
        String fileSize = FileHelper.getFileSizeStr(size);
        mFileSize.setText(fileSize);
    }

    public void setName(String name) {
        mFileName.setText(StringUtils.middleEllipse(name, 26, 6, 4, 7));
    }

    public void setIcon(FileData.FileType fileType) {
        mFileIcon.setImageResource(FileMediaTypeUtil.getFileTypeIcon(fileType));
    }

    public void setDate(long date) {
        if (date == 0) {
            mFileModifyTime.setVisibility(View.GONE);
        }
        mFileModifyTime.setText(TimeViewUtil.getUserCanViewTime(BaseApplicationLike.baseContext , date));
    }

    public void setFileData(FileData fileData, String monthData) {
        if (fileData == null) {
            return;
        }
        setName(fileData.title);
        setIcon(fileData.fileType);
        setSize(fileData.size);
        setDate(fileData.date);
        setGroupTime(fileData.date, monthData);
    }

    public void setFileItemListener(FileData fileData) {
        mExpandIcon.setOnClickListener(view2 -> {
            if (mOnIconSelectedListener != null) {
                mOnIconSelectedListener.onExpandIconWithMonthClick(fileData);
            }
        });
    }

    public void setOnItemSelectedListener(OnItemIconClickListener listener) {
        mOnIconSelectedListener = listener;
    }

    public interface OnItemIconClickListener{
        void onExpandIconWithMonthClick(FileData fileData);
    }
}
