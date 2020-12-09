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

public class TemplateContentSingle extends BaseTemplateContentView {

    public TextView mContent;

    public TemplateContentSingle(Context context) {
        super(context);
    }

    @Override
    public void initViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.template_content_single_column, this);
        mContent = view.findViewById(R.id.template_content);
    }

    public void setContent(TemplateMessage.TemplateContent templateContent, List<TemplateMessage.TemplateData> templateDatas) {
        String content = templateContent.mContent;
        setViewAlign(mContent, templateContent.mAlign);
        if (!content.contains(INDEX_DATA)) {
            mContent.setText(content);
            return;
        }
        parseDataToContent(mContent, content, templateDatas);
    }

}
