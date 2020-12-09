package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoFileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.reference.ReferenceMessage;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;

import java.util.HashMap;

public class SomeStatusView {
    private ViewGroup vgSomeStatusWrapperParent;

    private LinearLayout llSomeStatusInfo;

    private TextView tvTime;

    private ImageView ivStatus;

    private ViewGroup vgContentShow;

    private TextView tvContentShow;

    private ChatPostMessage contentMessage;

    @DrawableRes
    private int iconDoubleTick = R.mipmap.icon_double_tick_blue;

    @DrawableRes
    private int iconOneTick = R.mipmap.icon_one_tick_blue;

    private int singleLineTvContentShowSingleHeight = -1;
    private int maxTvContentWidth = -1;

    private HashMap<String, Integer> tvContentAndHeightInfoData = new HashMap<>();
    private HashMap<String, Integer> tvContentAndWidthInfoData = new HashMap<>();
    private int dp20;


    public SomeStatusView setMaxTvContentWidthBaseOn(ViewGroup vgContentShowParent, LinearLayout llSomeStatusInfo) {
        int llSomeStatusInfoWidth = llSomeStatusInfo.getWidth();
        if (0 == llSomeStatusInfoWidth) {
            llSomeStatusInfo.measure(0, 0);
            llSomeStatusInfoWidth = llSomeStatusInfo.getMeasuredWidth();
        }

        int vgContentWidth = vgContentShowParent.getWidth();
        if (0 == vgContentWidth) {
            vgContentShowParent.measure(0, 0);
            vgContentWidth = llSomeStatusInfo.getMeasuredWidth();

        }


        int maxTvContentWidth = vgContentWidth - llSomeStatusInfoWidth;
        setMaxTvContentWidth(maxTvContentWidth);
        return this;
    }

    public SomeStatusView setMaxTvContentWidth(int maxTvContentWidth) {
        this.maxTvContentWidth = maxTvContentWidth;
        return this;
    }

    public void handleSomeStatusInfoFloat() {
        if(null == vgSomeStatusWrapperParent) {
            return;
        }

        if(null == tvContentShow) {
            return;
        }

        if(null == contentMessage) {
            return;
        }

        if(!isMessageConsiderSomeStatusViewFloat()) {
            return;
        }



        if(vgSomeStatusWrapperParent instanceof LinearLayout) {

            LinearLayout llSomeStatusWrapperParent = (LinearLayout) vgSomeStatusWrapperParent;

            if(0 < maxTvContentWidth) {


                int tvContentShowWidth = getTvContentShowWidth();

                if(maxTvContentWidth <= tvContentShowWidth) {

                    llSomeStatusWrapperParent.setOrientation(LinearLayout.VERTICAL);

                } else {

                    llSomeStatusWrapperParent.setOrientation(LinearLayout.HORIZONTAL);
                }

                return;

            }


//            int tvContentShowHeight = getTvContentShowHeight();
//            LogUtil.e("tvContentShowHeight -> " + tvContentShowHeight);

            if(tvContentShow.getMaxWidth() - getDp30() < getTvContentShowWidth()) {
                llSomeStatusWrapperParent.setOrientation(LinearLayout.VERTICAL);
            } else {
                llSomeStatusWrapperParent.setOrientation(LinearLayout.HORIZONTAL);

            }

        }
    }

    private int getDp30() {
        if(0 < dp20) {
            return dp20;
        }

        dp20 = DensityUtil.dip2px(30);
        return dp20;
    }

    private boolean isMessageConsiderSomeStatusViewFloat() {
        if((contentMessage instanceof TextChatMessage)) {
            return true;
        }

        if((contentMessage instanceof ReferenceMessage)) {
            return true;
        }

        if((contentMessage instanceof AnnoFileTransferChatMessage)) {
            return true;
        }

        if((contentMessage instanceof AnnoImageChatMessage)) {
            return true;
        }

        return false;
    }




    public int getTvContentShowSingleLineHeight() {
        if(-1 != singleLineTvContentShowSingleHeight) {
            return singleLineTvContentShowSingleHeight;
        }

        String originalText = tvContentShow.getText().toString();
        originalText = getMessageTextContent(originalText);

        tvContentShow.setText(" ");

//        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(ScreenUtils.getScreenWidth(AtworkApplicationLike.baseContext), View.MeasureSpec.AT_MOST);
//        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        tvContentShow.measure(0, 0);
        singleLineTvContentShowSingleHeight = tvContentShow.getMeasuredHeight();

        tvContentShow.setText(originalText);

        return singleLineTvContentShowSingleHeight;
    }

    public int getTvContentShowHeight() {

        String content = StringUtils.EMPTY;
        content = getMessageTextContent(content);

        String currentTvContent =  tvContentShow.getText().toString();
        if(!content.equals(currentTvContent)) {
            tvContentShow.setText(content);
        }

        Integer heightInteger = tvContentAndHeightInfoData.get(content);
        int textViewHeight = -1;

        if(null != heightInteger) {
            textViewHeight = heightInteger;
        }


        if(0 >= textViewHeight) {
//            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(ScreenUtils.getScreenWidth(AtworkApplicationLike.baseContext), View.MeasureSpec.AT_MOST);
//            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            //            tvContentShow.measure(widthMeasureSpec, heightMeasureSpec);
            tvContentShow.measure(0, 0);

            textViewHeight = tvContentShow.getMeasuredHeight();
        }

        tvContentAndHeightInfoData.put(content, textViewHeight);

        return textViewHeight;
    }


    private String getMessageTextContent(String content) {
        if (contentMessage instanceof TextChatMessage) {
            TextChatMessage textChatMessage = (TextChatMessage) contentMessage;
            content = textChatMessage.text;

        } else if (contentMessage instanceof ReferenceMessage) {
            ReferenceMessage referenceMessage = (ReferenceMessage) contentMessage;
            content = referenceMessage.mReply;

        } else if (contentMessage instanceof AnnoFileTransferChatMessage) {
            AnnoFileTransferChatMessage annoFileTransferChatMessage = (AnnoFileTransferChatMessage) contentMessage;
            content = annoFileTransferChatMessage.comment;

        } else if (contentMessage instanceof AnnoImageChatMessage) {
            AnnoImageChatMessage annoImageChatMessage = (AnnoImageChatMessage) contentMessage;
            content = annoImageChatMessage.comment;
        }
        return content;
    }


    public int getTvContentShowWidth() {

        String content = StringUtils.EMPTY;
        content = getMessageTextContent(content);

        String currentTvContent =  tvContentShow.getText().toString();
        if(!content.equals(currentTvContent)) {
            tvContentShow.setText(content);
        }

        Integer widthInteger = tvContentAndWidthInfoData.get(content);
        int textViewWidth = -1;

        if(null != widthInteger) {
            textViewWidth = widthInteger;
        }


        if(0 >= textViewWidth) {
//            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(ScreenUtils.getScreenWidth(AtworkApplicationLike.baseContext), View.MeasureSpec.AT_MOST);
//            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            tvContentShow.measure(0, 0);

            textViewWidth = tvContentShow.getMeasuredWidth();
        }

        tvContentAndWidthInfoData.put(content, textViewWidth);

        return textViewWidth;
    }

    public ChatPostMessage getContentMessage() {
        return contentMessage;
    }

    public SomeStatusView setContentMessage(ChatPostMessage contentMessage) {
        this.contentMessage = contentMessage;
        return this;
    }

    public ViewGroup getVgContentShow() {
        return vgContentShow;
    }

    public SomeStatusView setVgContentShow(ViewGroup vgContentShow) {
        this.vgContentShow = vgContentShow;
        return this;
    }

    public TextView getTvContentShow() {
        return tvContentShow;
    }

    public SomeStatusView setTvContentShow(TextView tvContentShow) {
        this.tvContentShow = tvContentShow;
        return this;
    }

    public int getIconDoubleTick() {
        return iconDoubleTick;
    }

    public SomeStatusView setIconDoubleTick(int iconDoubleTick) {
        this.iconDoubleTick = iconDoubleTick;
        return this;
    }

    public int getIconOneTick() {
        return iconOneTick;
    }

    public SomeStatusView setIconOneTick(int iconOneTick) {
        this.iconOneTick = iconOneTick;
        return this;
    }

    public ViewGroup getVgSomeStatusWrapperParent() {
        return vgSomeStatusWrapperParent;
    }

    public LinearLayout getLlSomeStatusInfo() {
        return llSomeStatusInfo;
    }

    public TextView getTvTime() {
        return tvTime;
    }

    public ImageView getIvStatus() {
        return ivStatus;
    }

    public static SomeStatusView newSomeStatusView() {
        return new SomeStatusView();
    }

    public SomeStatusView setVgSomeStatusWrapperParent(ViewGroup vgSomeStatusWrapperParent) {
        this.vgSomeStatusWrapperParent = vgSomeStatusWrapperParent;
        return this;
    }

    public SomeStatusView setLlSomeStatusInfo(LinearLayout llSomeStatusInfo) {
        this.llSomeStatusInfo = llSomeStatusInfo;
        return this;
    }

    public SomeStatusView setTvTime(TextView tvTime) {
        this.tvTime = tvTime;
        return this;
    }

    public SomeStatusView setTvTimeTextColor(@ColorInt int color) {
        if(null != tvTime) {
            tvTime.setTextColor(color);
        }

        return this;
    }

    public SomeStatusView setIvStatus(ImageView ivStatus) {
        this.ivStatus = ivStatus;
        return this;
    }

    public SomeStatusView setSomeStatusInfoAreaGrayBg(Context context) {

        if (null != llSomeStatusInfo) {
            llSomeStatusInfo.setBackgroundResource(R.drawable.shape_time_info_som_status_gray_bg);

            int dp5 = DensityUtil.dip2px(5);
            int dp3 = DensityUtil.dip2px(3);
            llSomeStatusInfo.setPadding(dp5, dp3, dp5, dp3);

            setIconDoubleTick(R.mipmap.icon_double_tick_white);
            setIconOneTick(R.mipmap.icon_one_tick_white);
        }

        return this;
    }
}
