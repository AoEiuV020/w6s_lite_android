package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.foreveross.atwork.modules.chat.data.AnchorInfo;

public abstract class BasicESpaceChatItemView extends RelativeLayout {
    public BasicESpaceChatItemView(Context context) {
        super(context);
    }
    protected abstract @NonNull View getChatRootView();

    public AnchorInfo getAnchorInfo() {
        getChatRootView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int[] location = new  int[2] ;
        //获取mLlTextContent在屏幕中的位置
        getChatRootView().getLocationOnScreen(location);
        //View的面积高度
        int chatViewHeight;
        chatViewHeight = getChatRootView().getHeight();
        int anchorHeight = location[1];

        AnchorInfo info = new AnchorInfo();
        info.anchorHeight = anchorHeight;
        info.chatViewHeight = chatViewHeight;

        return info;
    }
}
