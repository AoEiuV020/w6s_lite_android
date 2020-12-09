package com.foreveross.atwork.modules.file.component;

import android.app.Activity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.file.util.FileMediaTypeUtil;
import com.foreveross.atwork.utils.FileHelper;
import com.foreveross.atwork.utils.TimeViewUtil;

import java.io.File;
import java.util.List;

/**
 * Created by lingen on 15/3/30.
 * Description:
 */
public class FileItemLinearLayoutView extends RelativeLayout {


    private Activity activity;
    /**
     * 文件是否选中
     */
    private CheckBox mSelectBtn;

    /**
     * 文件图标
     */
    private ImageView mIcon;

    /**
     * 文件名称
     */
    private TextView mName;

    /**
     * 文件大小
     */
    private TextView mSize;

    /**
     * 发送日期
     */
    private TextView mDate;

    public FileItemLinearLayoutView(Activity activity) {
        super(activity);
        this.activity = activity;
        initView();
    }

    public FileItemLinearLayoutView(Activity activity, File file) {
        super(activity);
        this.activity = activity;
        initView();
    }

    public FileItemLinearLayoutView(Activity activity, AttributeSet attrs) {
        super(activity, attrs);
        this.activity = activity;
        initView();
    }

    private void initView() {
        View view = activity.getLayoutInflater().inflate(R.layout.item_file_info, this);
        mSelectBtn = view.findViewById(R.id.item_file_select_checkbox);
        mIcon = view.findViewById(R.id.item_file_icon);
        mName = view.findViewById(R.id.item_file_name);
        mSize = view.findViewById(R.id.item_file_size);
        mDate = view.findViewById(R.id.send_date);
    }


    public void setSize(long size) {
        String fileSize = FileHelper.getFileSizeStr(size);
        mSize.setText(fileSize);
    }

    public void setName(String name) {
        mName.setText(StringUtils.middleEllipse(name, 26, 6, 4, 7));
    }

    public void setIcon(FileData.FileType fileType) {
        mIcon.setImageResource(FileMediaTypeUtil.getFileTypeIcon(fileType));
    }

    public void setChecked(boolean checked) {
        mSelectBtn.setChecked(checked);
    }

    public void setFileData(FileData fileData, List<FileData> selectedList) {
        if (fileData == null) {
            return;
        }
        setName(fileData.title);
        setIcon(fileData.fileType);
        setSize(fileData.size);
        setDate(fileData.date);
        setCheckBoxStatus(fileData, selectedList);
    }

    public void setCheckBoxStatus(FileData fileData, List<FileData> selectedList) {
        if (selectedList != null) {
            for (FileData data : selectedList) {
                if (!TextUtils.isEmpty(data.getMediaId()) && !TextUtils.isEmpty(fileData.getMediaId())) {
                    if (data.getMediaId().equalsIgnoreCase(fileData.getMediaId()) ) {
                        fileData.isSelect = true;
                        break;
                    }
                } else if(fileData.filePath.equalsIgnoreCase(data.filePath)) {
                    fileData.isSelect = true;
                    break;
                }
            }
        }
        mSelectBtn.setChecked(fileData.isSelect);
    }

    public void setDate(long date) {
        if (date == 0) {
            mDate.setVisibility(View.GONE);
        }
        mDate.setText(TimeViewUtil.getUserCanViewTime(BaseApplicationLike.baseContext , date));
    }

    public void hideCheckbox(boolean hide) {
        if(hide) {
            mSelectBtn.setVisibility(GONE);

            LayoutParams layoutParams = (LayoutParams) mIcon.getLayoutParams();
            layoutParams.leftMargin = DensityUtil.dip2px(10);
//            mIcon.setLayoutParams(layoutParams);

        } else {
            mSelectBtn.setVisibility(VISIBLE);

            LayoutParams layoutParams = (LayoutParams) mIcon.getLayoutParams();
            layoutParams.leftMargin = 0;
//            mIcon.setLayoutParams();

        }
    }
}
