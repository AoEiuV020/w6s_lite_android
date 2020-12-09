package com.foreveross.atwork.modules.chat.component;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TemplateMessage;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;

import java.util.ArrayList;
import java.util.List;

import static com.foreveross.atwork.infrastructure.utils.chat.TemplateDataHelper.INDEX_DATA;
import static com.foreveross.atwork.infrastructure.utils.chat.TemplateDataHelper.INDEX_PREFIX;

/**
 * Created by reyzhang22 on 17/8/18.
 */

public abstract class BaseTemplateContentView extends LinearLayout {

    public Context mContext;

    private List<TemplateMessage.TemplateData> mReplaceTemplateDatas = new ArrayList<>();

    public BaseTemplateContentView(Context context) {
        super(context);
        mContext = context;
        initViews(context);
    }

    public abstract void initViews(Context context);

    public void setViewAlign(TextView textView, String align) {
        if ("left".equalsIgnoreCase(align) || TextUtils.isEmpty(align)) {
            textView.setGravity((Gravity.LEFT | Gravity.CENTER_VERTICAL));
        }
        if ("right".equalsIgnoreCase(align)) {
            textView.setGravity((Gravity.RIGHT | Gravity.CENTER_VERTICAL));
        }
        if ("center".equalsIgnoreCase(align)) {
            textView.setGravity((Gravity.CENTER | Gravity.CENTER_VERTICAL));
        }
    }

    /**
     * 解析data里面的{{*.DATA}}数据
     * @param textView
     * @param content
     * @param templateDatas
     */
    int i = 0;
    public void parseDataToContent(TextView textView, String content, List<TemplateMessage.TemplateData> templateDatas) {
        if (ListUtil.isEmpty(templateDatas)) {
            return;
        }
        for (TemplateMessage.TemplateData templateData : templateDatas) {
            String key = new StringBuilder().append(INDEX_PREFIX).append(templateData.mKey).append(INDEX_DATA).toString();
            if (!content.contains(key)) {
                continue;
            }
            mReplaceTemplateDatas.add(templateData);
            content = content.replace(key, templateData.mValue);
            break;
        }
        //递归处理 且防止data和content里面相互无值，导致死循环
        if (content.contains(INDEX_DATA) && i <= templateDatas.size()) {
            i++;
            parseDataToContent(textView, content, templateDatas);
            return;
        }
        if (i > templateDatas.size() && content.contains(INDEX_DATA)) {
            String notDataKey = content.substring(content.indexOf(INDEX_PREFIX), content.indexOf(INDEX_DATA)+7);
            content = content.replace(notDataKey, "");
            i = 0;
            if (content.contains(INDEX_DATA)){
                parseDataToContent(textView, content, templateDatas);
                return;
            }
        }
        setContentStyle(textView, content);
        i = 0;
    }

    /**
     * 设置字号大小，颜色等
     * @param textView
     * @param replacedContent
     */
    private void setContentStyle(TextView textView, String replacedContent) {
        SpannableStringBuilder builder = new SpannableStringBuilder(replacedContent);
        if (ListUtil.isEmpty(mReplaceTemplateDatas)) {
            setText(textView, builder);
            return;
        }

        for (TemplateMessage.TemplateData templateData: mReplaceTemplateDatas) {
            int color = -1;
            try {
                color = Color.parseColor(templateData.mColor);
            } catch (Exception e) {
                color = mContext.getResources().getColor(R.color.light_black);
            }
            ColorStateList colorStateList = ColorStateList.valueOf(color);
            int fontSize = 14;
            try {
                fontSize = Integer.valueOf(templateData.mFontSize);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int style = "normal".equalsIgnoreCase(templateData.mTextStyle) || TextUtils.isEmpty(templateData.mTextStyle) ? 0 : 1;
            TextAppearanceSpan span = new TextAppearanceSpan(null, style, DensityUtil.dip2px(fontSize), colorStateList, null);
            if (!TextUtils.isEmpty(templateData.mValue)) {
                int startIndex = replacedContent.indexOf(templateData.mValue);
                int endIndex = startIndex + templateData.mValue.length();
                builder.setSpan(span, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            }
        }
        setText(textView, builder);
    }

    private void setText(TextView textView, SpannableStringBuilder builder) {
        textView.setText(builder);
        mReplaceTemplateDatas.clear();
    }

}
