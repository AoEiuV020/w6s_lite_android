package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TemplateMessage;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.modules.chat.component.ChatDetailItemDataRefresh;
import com.foreveross.atwork.modules.chat.component.TemplateActionButtonsView;
import com.foreveross.atwork.modules.chat.component.TemplateActionDetailView;
import com.foreveross.atwork.modules.chat.component.TemplateContentSingle;
import com.foreveross.atwork.modules.chat.component.TemplateContentTwoColumn;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.modules.chat.inter.ChatItemLongClickListener;
import com.foreveross.atwork.modules.chat.inter.HasChatItemLongClickListener;
import com.foreveross.atwork.modules.chat.util.TemplateHelper;
import com.foreveross.atwork.utils.ImageCacheHelper;

import java.util.List;

/**
 * Created by reyzhang22 on 17/8/17.
 */

public class TemplateMessageView extends RelativeLayout implements ChatDetailItemDataRefresh, HasChatItemLongClickListener {

    private Context mContext;

    private TemplateMessage mTemplateMessage;

    private View mColorView;

    private View mAppInfoView;
    private TextView mAppName;
    private ImageView mAppAvatar;

    private View mDiver;

    private TextView mTemplateTitle;
    private TextView mDate;

    private LinearLayout mTemplateContentView;

    private View mRootView;

    private LinearLayout mTemplateActionView;
    private LinearLayout mTemplateActionButtonsLayout;

    private ChatItemLongClickListener mChatItemLongClickListener;

    private Session mSession;

    public TemplateMessageView(Context context, Session session) {
        super(context);
        mContext = context;
        mSession = session;
        initViews();
    }

    private void initViews() {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.chat_template_message, this);
        mRootView = view.findViewById(R.id.root_view);
        mColorView = view.findViewById(R.id.color_view);
        mAppInfoView = view.findViewById(R.id.app_info_view);
        mDiver = view.findViewById(R.id.diver);
        mAppName = mAppInfoView.findViewById(R.id.app_name);
        mAppAvatar = mAppInfoView.findViewById(R.id.app_avatar);
        mTemplateTitle = view.findViewById(R.id.template_title);
        mDate = view.findViewById(R.id.template_date);
        mTemplateContentView = view.findViewById(R.id.template_content_view);
        mTemplateActionView = view.findViewById(R.id.template_action_layout);
        mTemplateActionButtonsLayout = mTemplateActionView.findViewById(R.id.action_buttons_layout);

        mRootView.setOnLongClickListener(v ->{
            if (mChatItemLongClickListener != null) {
                mChatItemLongClickListener.showDeleteLongClick(mTemplateMessage, getAnchorInfo());
            }
            return true;
        });
    }

    public void inflateTemplateMessageView(TemplateMessage templateMessage) {
        setTopColor(templateMessage.mTopColor);
        setAPPInfo(templateMessage.mTopTitle, templateMessage.mTopAvatar);
        setTemplateTitle(templateMessage.mTitle, TimeUtil.getStringForMillis(templateMessage.deliveryTime, TimeUtil.getTimeFormat2(mContext)));
        setTemplateMessageContentData(templateMessage.mTemplateContents, templateMessage.mDataList);
        setTemplateActions(templateMessage.mTemplateActions, templateMessage.mDataList);
    }

    @Override
    public void refreshItemView(ChatPostMessage message) {
        mTemplateMessage = (TemplateMessage)message;

        inflateTemplateMessageView(mTemplateMessage);
    }

    @Override
    public void refreshMessagesContext(List<ChatPostMessage> messages) {

    }


    @Override
    public String getMsgId() {
        if (mTemplateMessage == null) {
            return null;
        }
        return mTemplateMessage.deliveryId;
    }

    /**
     * 设置头部颜色
     * @param topColor
     */
    private void setTopColor(String topColor) {
        if (TextUtils.isEmpty(topColor)) {
            mColorView.setVisibility(GONE);
            return;
        }
        try {
            int color = Color.parseColor(topColor);
            mColorView.setVisibility(VISIBLE);
            GradientDrawable drawable = (GradientDrawable)mColorView.getBackground();
            drawable.setColor(color);
        } catch (Exception e) {
            e.printStackTrace();
            mColorView.setVisibility(GONE);
        }
    }

    /**
     * 设置模板应用信息
     * @param appName
     * @param appAvatar
     */
    private void setAPPInfo(String appName, String appAvatar) {
        mDiver.setVisibility(VISIBLE);
        if (TextUtils.isEmpty(appAvatar) && TextUtils.isEmpty(appName)) {
            mAppInfoView.setVisibility(GONE);
            mDiver.setVisibility(GONE);
            return;
        }
        mAppInfoView.setVisibility(VISIBLE);
        mAppAvatar.setVisibility(TextUtils.isEmpty(appAvatar) ? GONE : VISIBLE);
        ImageCacheHelper.displayImageByMediaId(appAvatar, mAppAvatar, ImageCacheHelper.getAppImageOptions());
        mAppName.setText(appName);
    }

    /**
     * 设置模板消息标题
     */
    private void setTemplateTitle(String title, String date) {
        mTemplateTitle.setText(title);
        mDate.setText(date);
    }

    /**
     * 设置模板消息内容
     * @param contents
     * @param datas
     */
    private void setTemplateMessageContentData(List<List<TemplateMessage.TemplateContent>> contents, List<TemplateMessage.TemplateData> datas) {
        mTemplateContentView.removeAllViews();

        if(ListUtil.isEmpty(contents)) {
            return;
        }

        View contentView  = null;
        for (List<TemplateMessage.TemplateContent> list : contents) {
            if (list.size() == 1) {
                contentView = inflateSingleContentData(list.get(0), datas);
            } else {
                contentView = inflateTwoColumnContentData(list, datas);
            }
            mTemplateContentView.addView(contentView);
        }
    }

    /**
     * 显示单行模板内容
     * @param templateContent
     * @param datas
     * @return
     */
    private TemplateContentSingle inflateSingleContentData(TemplateMessage.TemplateContent templateContent, List<TemplateMessage.TemplateData> datas) {
        TemplateContentSingle contentSingle = new TemplateContentSingle(mContext);
        contentSingle.setContent(templateContent, datas);
        return contentSingle;
    }

    /**
     * 显示左右结构模板内容
     * @param templateContents
     * @param datas
     * @return
     */
    private TemplateContentTwoColumn inflateTwoColumnContentData(List<TemplateMessage.TemplateContent> templateContents, List<TemplateMessage.TemplateData> datas) {
        TemplateContentTwoColumn twoColumn = new TemplateContentTwoColumn(mContext);
        twoColumn.setContent(templateContents, datas);
        return twoColumn;
    }

    private void setTemplateActions(List<TemplateMessage.TemplateActions> templateActions, List<TemplateMessage.TemplateData> datas) {
        if (ListUtil.isEmpty(templateActions)) {
            return;
        }
        mTemplateActionButtonsLayout.removeAllViews();
        if (templateActions.size() == 1) {
            inflateTemplateActionDetail(templateActions.get(0), datas);
            return;
        }
        inflateTemplateActionButtons(templateActions, datas);
    }

    private void inflateTemplateActionDetail(TemplateMessage.TemplateActions templateAction, List<TemplateMessage.TemplateData> datas) {
        TemplateActionDetailView actionDetailView = new TemplateActionDetailView(mContext);

        setOnClickListener(v -> TemplateHelper.routeWebview(mContext, templateAction, datas, mSession));
        actionDetailView.setAction(actionDetailView.mTvAction, templateAction, datas, mSession);
        mTemplateActionButtonsLayout.addView(actionDetailView);
    }

    private void inflateTemplateActionButtons(List<TemplateMessage.TemplateActions> templateActions, List<TemplateMessage.TemplateData> datas) {
        TemplateActionButtonsView actionButtonsView = new TemplateActionButtonsView(mContext);
        if(1 == templateActions.size()) {
            setOnClickListener(v -> TemplateHelper.routeWebview(mContext, templateActions.get(0), datas, mSession));

        } else {
            setOnClickListener(null);
        }

        actionButtonsView.setButtons(templateActions, datas, mSession);
        mTemplateActionButtonsLayout.addView(actionButtonsView);
    }

    @Override
    public void setChatItemLongClickListener(ChatItemLongClickListener chatItemLongClickListener) {
        mChatItemLongClickListener = chatItemLongClickListener;
    }

    public AnchorInfo getAnchorInfo() {
        mRootView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int[] location = new  int[2] ;
        //获取mLlTextContent在屏幕中的位置
        mRootView.getLocationOnScreen(location);
        //View的面积高度
        int chatViewHeight;
        chatViewHeight = mRootView.getHeight();
        int anchorHeight = location[1];

        AnchorInfo info = new AnchorInfo();
        info.anchorHeight = anchorHeight;
        info.chatViewHeight = chatViewHeight;

        return info;
    }
}
