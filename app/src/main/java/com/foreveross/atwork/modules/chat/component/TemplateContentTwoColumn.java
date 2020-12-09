package com.foreveross.atwork.modules.chat.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TemplateMessage;

import java.util.List;

import static com.foreveross.atwork.infrastructure.utils.chat.TemplateDataHelper.INDEX_DATA;

/**
 * Created by reyzhang22 on 17/8/18.
 */

public class TemplateContentTwoColumn extends BaseTemplateContentView {

    public TextView mLeftContent;
    public TextView mRightContent;

    public TemplateContentTwoColumn(Context context) {
        super(context);
    }

    @Override
    public void initViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.template_content_two_column, this);
        mLeftContent = (TextView)view.findViewById(R.id.template_content_left);
        mRightContent = (TextView)view.findViewById(R.id.template_content_right);
    }

    public void setContent(List<TemplateMessage.TemplateContent> templateContents, List<TemplateMessage.TemplateData> templateDatas) {
        for (int i = 0; i < templateContents.size(); i++) {
            TemplateMessage.TemplateContent templateContent = templateContents.get(i);
            String content = templateContent.mContent;
            if (i == 0) {
                setSubContent(mLeftContent, content, templateContent, templateDatas);
            } else {
                setSubContent(mRightContent, content, templateContent, templateDatas);
            }
        }
    }

    private void setSubContent(TextView view, String content, TemplateMessage.TemplateContent templateContent, List<TemplateMessage.TemplateData> templateDatas) {
        setViewAlign(view, templateContent.mAlign);
        if (!content.contains(INDEX_DATA)) {
            view.setText(content);
            return;
        }
        parseDataToContent(view, content, templateDatas);
    }

}
