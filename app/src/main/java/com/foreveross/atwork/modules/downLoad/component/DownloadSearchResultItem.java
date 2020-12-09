package com.foreveross.atwork.modules.downLoad.component;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.file.util.FileMediaTypeUtil;
import com.foreveross.atwork.utils.FileHelper;


/**
 * Created by wuzejie on 2020/1/14.
 * Description:我的下载的搜索页面的子项
 */
public class DownloadSearchResultItem extends LinearLayout {

    private ImageView mFileIcon;

    private TextView mFileName;

    private TextView mFileSize;

    public DownloadSearchResultItem(Context context) {
        super(context);
        initViews(context);
    }

    public DownloadSearchResultItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public DownloadSearchResultItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_download_search_result, this);
        mFileIcon = view.findViewById(R.id.file_icon);
        mFileName = view.findViewById(R.id.file_name);
        mFileSize = view.findViewById(R.id.file_size);
    }

    public void setFile(FileData fileData, String searchKey) {

        mFileIcon.setImageResource(FileMediaTypeUtil.getFileTypeIcon(fileData.fileType));
        StringBuilder name = new StringBuilder(StringUtils.middleEllipse(fileData.title, 26, 6, 4, 7));

        mFileName.setText(name.toString());
        mFileSize.setText(FileHelper.getFileSizeStr(fileData.size));
        highlightKey(mFileName, searchKey);
    }

    private void highlightKey(TextView textView, String searchKey) {

        int color = getContext().getResources().getColor(R.color.common_message_num_bg);

        String text = textView.getText().toString();
        if (!StringUtils.isEmpty(text)) {
            int start = text.indexOf(searchKey);
            int end = -1;
            if (start != -1) {
                end = start + searchKey.length();
                SpannableString spannableString = new SpannableString(text);
                spannableString.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(spannableString);
            }
        }
    }
}
