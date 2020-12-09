package com.foreveross.atwork.modules.dropbox.component;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.file.util.FileMediaTypeUtil;

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
 * Created by reyzhang22 on 16/9/9.
 */
public class DropboxSearchResultItem extends LinearLayout {

    private ImageView mFileIcon;

    private TextView mFileName;

    private TextView mFileSize;

    public DropboxSearchResultItem(Context context) {
        super(context);
        initViews(context);
    }

    public DropboxSearchResultItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public DropboxSearchResultItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_dropbox_search_result, this);
        mFileIcon = view.findViewById(R.id.file_icon);
        mFileName = view.findViewById(R.id.file_name);
        mFileSize = view.findViewById(R.id.file_size);
    }

    public void setFile(Dropbox dropbox, String searchKey) {

        mFileIcon.setImageResource(dropbox.mIsDir ? R.mipmap.icon_dropbox_item_dir : FileMediaTypeUtil.getFileTypeByExtension(dropbox.mExtension));
        StringBuilder name = new StringBuilder(dropbox.mFileName);
        if (!TextUtils.isEmpty(dropbox.mExtension) && -1 == name.lastIndexOf(dropbox.mExtension)) {
            name.append(".").append(dropbox.mExtension);
        }
        mFileName.setText(name.toString());
        mFileSize.setText(FileUtil.formatFromSize(dropbox.mFileSize));
        mFileSize.setVisibility(dropbox.mIsDir ? GONE : VISIBLE);
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
