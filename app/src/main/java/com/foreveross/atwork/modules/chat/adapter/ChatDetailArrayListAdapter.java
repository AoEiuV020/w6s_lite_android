package com.foreveross.atwork.modules.chat.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.chat.TranslateLanguageResponse;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.chat.SystemChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ESpaceChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.NetworkStatusUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.inter.ReSendListener;
import com.foreveross.atwork.modules.chat.component.ChatDetailItemDataRefresh;
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment;
import com.foreveross.atwork.modules.chat.inter.ChatItemClickListener;
import com.foreveross.atwork.modules.chat.inter.ChatItemLongClickListener;
import com.foreveross.atwork.modules.chat.inter.ChatModeListener;
import com.foreveross.atwork.modules.chat.inter.HasChatItemClickListener;
import com.foreveross.atwork.modules.chat.inter.HasChatItemLongClickListener;
import com.foreveross.atwork.modules.chat.inter.HasResendListener;
import com.foreveross.atwork.modules.chat.inter.SelectModelListener;
import com.foreveross.atwork.modules.chat.inter.SkinModeUpdater;
import com.foreveross.atwork.modules.chat.util.MessageChatViewBuild;
import com.foreveross.atwork.modules.chat.util.TextTranslateHelper;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.translate.OnResultListener;
import com.foreveross.translate.youdao.YoudaoTranslate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.foreveross.atwork.modules.chat.util.MessageChatViewBuild.isLeftView;


public class ChatDetailArrayListAdapter extends BaseAdapter {
    /**
     * cache??????
     */
    private ChatItemClickListener chatItemClickListener;

    private ChatItemLongClickListener chatItemLongClickListener;
    private ReSendListener reSendListener;

    private ChatModeListener chatModeListener;

    private List<ChatPostMessage> messages = new ArrayList<>();

    private Map<String, View> messageViews = new HashMap<>();

    private Context context;

    private Session session;

    private String translateLanguage;

    private Bundle savedInstanceState;

    public ChatDetailArrayListAdapter(Context context, Bundle savedInstanceState, Session session, String translateLanguage, ChatItemClickListener chatItemClickListener, ChatItemLongClickListener chatItemLongClickListener, ChatModeListener chatModeListener, ReSendListener reSendListener) {
        this.context = context;
        this.chatItemClickListener = chatItemClickListener;
        this.chatItemLongClickListener = chatItemLongClickListener;
        this.chatModeListener = chatModeListener;
        this.reSendListener = reSendListener;
        this.session = session;
        this.translateLanguage = translateLanguage;
        this.savedInstanceState = savedInstanceState;
    }

    /***
     * ??????????????????????????????
     */
    public void setMessages(List<ChatPostMessage> messages) {
        this.messages = messages;
    }
    /***
     * ?????????????????????
     */
    public void updateTranslateLanguage(String translateLanguage){
        this.translateLanguage = translateLanguage;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public ChatPostMessage getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).deliveryId.hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getItemViewType(int position) {
        ChatPostMessage chatDetailItem = getItem(position);
        return MessageChatViewBuild.getMsgChatViewType(chatDetailItem);

    }

    @Override
    public int getViewTypeCount() {
        return MessageChatViewBuild.MESSAGE_VIEW_TYPE_COUNT;
    }

    public void intelligentTranslation(TextChatMessage textChatMessage, final OnResultListener listener){
        if (StringUtils.isEmpty(textChatMessage.getTranslatedResult())) {
            TextTranslateHelper.setTranslating(textChatMessage, true);

            YoudaoTranslate youdaoTranslate = new YoudaoTranslate();
            String url = youdaoTranslate.translate(textChatMessage.text, translateLanguage);
            youdaoTranslate.getTranlateLanguage(url, new YoudaoTranslate.OnTranslateListener() {
                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    LogUtil.e("translate result - > " + "???????????????");
                    listener.onResult(null);
                }

                @Override
                public void onSuccess(TranslateLanguageResponse translateLanguageResponse) {
                    if (!StringUtils.isEmpty(translateLanguageResponse.translation.get(0))) {
                        listener.onResult(translateLanguageResponse.translation.get(0));
                    } else {
                        listener.onResult(null);
                    }

                }
            });

        } else {
            TextTranslateHelper.showTranslateStatusAndUpdateDb(textChatMessage, true);

        }
    }

    private Boolean isIntelligentTranslation(TextChatMessage textChatMessage){
        if( !StringUtils.isEmpty(translateLanguage)){
            if(!textChatMessage.isTranslateStatusVisible()){
                if(StringUtils.isEmpty(textChatMessage.getTranslatedResult())){
                    if(!textChatMessage.getTranslatedLanguage().equalsIgnoreCase(translateLanguage)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ChatPostMessage chatDetailItem = getItem(position);

        if (chatDetailItem.getChatType().equals(ChatPostMessage.ChatType.Text)) {
            if (isLeftView(chatDetailItem)) {
                TextChatMessage textChatMessage = (TextChatMessage) chatDetailItem;
                if(isIntelligentTranslation(textChatMessage)){
                    //???????????????????????????????????????
                    if(NetworkStatusUtil.isNetworkAvailable(BaseApplicationLike.baseContext)) {
                        intelligentTranslation(textChatMessage, result -> {
                            if (!StringUtils.isEmpty(result)) {
                                LogUtil.e("translate result - > " + result);
                                TextTranslateHelper.updateTranslateResultAndUpdateDb(textChatMessage, result, translateLanguage);
                            } else {
                                TextTranslateHelper.setTranslating(textChatMessage, false);
                            }
                        });
                    }else{
                        AtworkToast.showToast(context.getResources().getString(R.string.Translate_common));
                    }

                }
            }
        }

        if (convertView == null || chatDetailItem instanceof ESpaceChatMessage || chatDetailItem instanceof SystemChatMessage) {
            convertView = MessageChatViewBuild.getMsgChatView(context, savedInstanceState, chatDetailItem, session);

        }

        //????????????????????????
        if (convertView instanceof SelectModelListener) {
            SelectModelListener selectModelListener = (SelectModelListener) convertView;
            if (chatModeListener.getChatModel().equals(ChatDetailFragment.ChatModel.COMMON)) {
                selectModelListener.hiddenSelect();
            }

            if (chatModeListener.getChatModel().equals(ChatDetailFragment.ChatModel.SELECT)) {
                selectModelListener.showSelect();
            }
        }

        initListener(convertView);

        if (convertView instanceof ChatDetailItemDataRefresh) {
            if(!StringUtils.isEmpty(chatDetailItem.deliveryId)){
                ChatDetailItemDataRefresh chatDetailItemDataRefresh = (ChatDetailItemDataRefresh) convertView;
                chatDetailItemDataRefresh.refreshMessagesContext(messages);
                chatDetailItemDataRefresh.refreshItemView(chatDetailItem);


            }

        }

        if(convertView instanceof SkinModeUpdater) {
            if(!StringUtils.isEmpty(chatDetailItem.deliveryId)){
                SkinModeUpdater skinModeUpdater = (SkinModeUpdater) convertView;
                skinModeUpdater.refreshSkinUI();
            }

        }

        messageViews.put(chatDetailItem.deliveryId, convertView);
        return convertView;
    }

    private void initListener(View convertView) {
        //??????????????????
        if (convertView instanceof HasChatItemClickListener) {
            HasChatItemClickListener hasChatItemClickListener = (HasChatItemClickListener) convertView;
            hasChatItemClickListener.setChatItemClickListener(chatItemClickListener);
        }

        //??????????????????
        if (convertView instanceof HasChatItemLongClickListener) {
            HasChatItemLongClickListener hasChatItemLongClickListener = (HasChatItemLongClickListener) convertView;
            hasChatItemLongClickListener.setChatItemLongClickListener(chatItemLongClickListener);
        }

        if (convertView instanceof HasResendListener) {
            HasResendListener hasResendListener = (HasResendListener) convertView;
            hasResendListener.setReSendListener(reSendListener);
        }

    }


    public void setSession(Session session) {
        this.session = session;
    }
}
