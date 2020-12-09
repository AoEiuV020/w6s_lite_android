package com.foreveross.atwork.modules.chat.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.chat.TranslateLanguageResponse;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.StickerChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.EmployeeManager;
import com.foreveross.atwork.modules.chat.util.ArticleItemHelper;
import com.foreveross.atwork.modules.chat.util.AutoLinkHelper;
import com.foreveross.atwork.modules.chat.util.TextTranslateHelper;
import com.foreveross.atwork.modules.file.util.FileMediaTypeUtil;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AvatarHelper;
import com.foreveross.atwork.utils.ChatMessageHelper;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.foreveross.atwork.utils.ImageChatHelper;
import com.foreveross.atwork.utils.TimeViewUtil;
import com.foreveross.translate.youdao.YoudaoTranslate;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by dasunsy on 2017/6/23.
 */
@Deprecated
public class MultipartMessageAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private final LayoutInflater mInflater;
    private List<ChatPostMessage> mChatPostMessageList;
    private OnHandleClickListener mOnHandleClickListener;
    //翻译的语种
    private String mStrTranslationShortName = "";

    public MultipartMessageAdapter(Context context, List<ChatPostMessage> chatPostMessageList, OnHandleClickListener onHandleClickListener) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mChatPostMessageList = chatPostMessageList;
        this.mOnHandleClickListener = onHandleClickListener;
    }

    public MultipartMessageAdapter(Context context, List<ChatPostMessage> chatPostMessageList, String strTranslationShortName, OnHandleClickListener onHandleClickListener) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mChatPostMessageList = chatPostMessageList;
        this.mOnHandleClickListener = onHandleClickListener;
        this.mStrTranslationShortName = strTranslationShortName;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = mInflater.inflate(R.layout.item_message_detail_list, parent, false);
        MultipartItemViewHolder holder = new MultipartItemViewHolder(rootView);

        holder.mFlContent.setOnClickListener(v -> {
            mOnHandleClickListener.onClick(holder.getAdapterPosition(), holder);
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatPostMessage chatPostMessage = mChatPostMessageList.get(position);
        if (null != chatPostMessage) {
            MultipartItemViewHolder itemViewHolder = (MultipartItemViewHolder) holder;

            if (!StringUtils.isEmpty(chatPostMessage.mMyNameInDiscussion)) {
                itemViewHolder.mTvName.setText(chatPostMessage.mMyNameInDiscussion);
            } else {
                itemViewHolder.mTvName.setText(chatPostMessage.mMyName);

            }
            itemViewHolder.mTvTime.setText(TimeViewUtil.getMultipartItemViewTime(BaseApplicationLike.baseContext , chatPostMessage.deliveryTime));

            refreshAvatar(itemViewHolder, chatPostMessage, position);


            if(position == getItemCount() - 1) {
                itemViewHolder.mVLineBottom.setVisibility(View.GONE);
                itemViewHolder.mVLineEnd.setVisibility(View.INVISIBLE);
            } else {
                itemViewHolder.mVLineBottom.setVisibility(View.VISIBLE);
                itemViewHolder.mVLineEnd.setVisibility(View.GONE);

            }

            if (chatPostMessage instanceof ImageChatMessage) {
                refreshImageUI((ImageChatMessage) chatPostMessage, itemViewHolder);

            } else if (chatPostMessage instanceof TextChatMessage) {

                TextChatMessage textChatMessage = (TextChatMessage) chatPostMessage;
                Boolean isTranslate = translateEnable(textChatMessage);
                if(isTranslate){
                    intelligentTranslation(textChatMessage);
                }
                refreshTextUI(textChatMessage, itemViewHolder, isTranslate);


            } else if (chatPostMessage instanceof StickerChatMessage) {
                refreshStickerUI((StickerChatMessage)chatPostMessage, itemViewHolder);
            } else if (chatPostMessage instanceof VoiceChatMessage) {
                refreshVoiceUI((VoiceChatMessage)chatPostMessage, itemViewHolder);
            }


            else {
                itemViewHolder.refreshUI(ViewType.MEDIA);

                if(chatPostMessage instanceof FileTransferChatMessage) {
                    refreshFileUI((FileTransferChatMessage) chatPostMessage, itemViewHolder);

                } else if(chatPostMessage instanceof ShareChatMessage) {
                    ShareChatMessage shareChatMessage = (ShareChatMessage) chatPostMessage;

                    if(ShareChatMessage.ShareType.Link.toString().equalsIgnoreCase(shareChatMessage.getShareType())) {
                        refreshLinkUI(itemViewHolder, shareChatMessage);

                    } else if(ShareChatMessage.ShareType.OrgInviteBody.toString().equalsIgnoreCase(shareChatMessage.getShareType())) {
                        refreshOrgInviteUI(itemViewHolder, shareChatMessage);

                    }
                } else if(chatPostMessage instanceof MicroVideoChatMessage) {
                    refreshMicroVideoUI((MicroVideoChatMessage) chatPostMessage, itemViewHolder);

                }

            }

        }
    }

    /**
     * Description:判断是否需要智能翻译：有翻译的语种；没有翻译的结果或者翻译的语种和已经翻译的结果的语种不一致
     * @param textChatMessage
     * @return
     */
    private Boolean translateEnable(TextChatMessage textChatMessage){
        if(!StringUtils.isEmpty(mStrTranslationShortName)){
            if(StringUtils.isEmpty(textChatMessage.getTranslatedResult())){
                return true;
            }
            else if(!textChatMessage.getTranslatedLanguage().equalsIgnoreCase(mStrTranslationShortName)){
                return true;
            }
            else
                return false;
        }
        return false;
    }

    private void refreshAvatar(MultipartItemViewHolder itemViewHolder, ChatPostMessage chatPostMessage, int position) {
        boolean shouldHideAvatar = false;
        if(0 < position) { //have last
            ChatPostMessage lastChatPostMessage = mChatPostMessageList.get(position - 1);
            if(lastChatPostMessage.from.equals(chatPostMessage.from)) {
                shouldHideAvatar = true;
            }

        }

        if(shouldHideAvatar) {
            itemViewHolder.mIvAvatar.setVisibility(View.INVISIBLE);
        } else {
            AvatarHelper.setUserAvatarByAvaId(chatPostMessage.mMyAvatar, itemViewHolder.mIvAvatar, true, true);

            itemViewHolder.mIvAvatar.setVisibility(View.VISIBLE);

        }
    }


    private void refreshFileUI(FileTransferChatMessage chatPostMessage, MultipartItemViewHolder itemViewHolder) {
        FileTransferChatMessage fileTransferChatMessage = chatPostMessage;
        itemViewHolder.mIvIconFlag.setImageResource(FileMediaTypeUtil.getFileTypeIcon(fileTransferChatMessage));
        itemViewHolder.mTvTitle.setText(fileTransferChatMessage.name);
        itemViewHolder.mTvContent.setText(ChatMessageHelper.getMBOrKBString(fileTransferChatMessage.size));
    }

    private void refreshMicroVideoUI(MicroVideoChatMessage chatPostMessage, MultipartItemViewHolder itemViewHolder) {
        MicroVideoChatMessage microVideoChatMessage = chatPostMessage;
        itemViewHolder.mTvTitle.setText(R.string.video2);
        itemViewHolder.mIvIconFlag.setImageResource(R.mipmap.icon_b_video);
        itemViewHolder.mTvContent.setText(StringUtils.EMPTY);
    }

    private void refreshOrgInviteUI(MultipartItemViewHolder itemViewHolder, ShareChatMessage shareChatMessage) {
        AvatarHelper.setUserAvatarByAvaId(shareChatMessage.getContent().mOrgAvatar, itemViewHolder.mIvIconFlag, false, false);

        if (!TextUtils.isEmpty(shareChatMessage.getContent().mOrgName)) {
            itemViewHolder.mTvContent.setText(shareChatMessage.getContent().mOrgName);
        }

        EmployeeManager.getInstance().setEmployeeNameForShareMsg(shareChatMessage, itemViewHolder.mTvTitle);


        if (!TextUtils.isEmpty(shareChatMessage.getContent().mOrgName)) {
            itemViewHolder.mTvContent.setText(mContext.getString(R.string.invite_you, shareChatMessage.getContent().mOrgName));
        }
    }

    private void refreshLinkUI(MultipartItemViewHolder itemViewHolder, ShareChatMessage shareChatMessage) {

        ImageCacheHelper.displayImage(ArticleItemHelper.getCoverUrl(shareChatMessage.getContent()), itemViewHolder.mIvIconFlag, ImageCacheHelper.getRectOptions(R.mipmap.default_link));


        if (!TextUtils.isEmpty(shareChatMessage.getContent().title)) {
            itemViewHolder.mTvTitle.setText(shareChatMessage.getContent().title);
            itemViewHolder.mTvContent.setText(shareChatMessage.getContent().url);
        } else {
            itemViewHolder.mTvTitle.setText(shareChatMessage.getContent().url);
            itemViewHolder.mTvContent.setText(StringUtils.EMPTY);
        }
    }
    //智能翻译
    public void intelligentTranslation(TextChatMessage textChatMessage){
        if (StringUtils.isEmpty(textChatMessage.getTranslatedResult())) {
            TextTranslateHelper.setTranslating(textChatMessage, true, true);
            YoudaoTranslate youdaoTranslate = new YoudaoTranslate();
            String url = youdaoTranslate.translate(textChatMessage.text, mStrTranslationShortName);
            youdaoTranslate.getTranlateLanguage(url, new YoudaoTranslate.OnTranslateListener() {
                @Override
                public void networkFail(int errorCode, String errorMsg) {
                }

                @Override
                public void onSuccess(TranslateLanguageResponse translateLanguageResponse) {
                    if (!StringUtils.isEmpty(translateLanguageResponse.translation.get(0))) {
                        String result = translateLanguageResponse.translation.get(0);
                        LogUtil.e("translate result - > " + result);
                        TextTranslateHelper.updateTranslateResultAndUpdateDb(textChatMessage, result, mStrTranslationShortName, true);
                    } else {
                        AtworkToast.showToast(mContext.getResources().getString(R.string.Translate_common));
                        TextTranslateHelper.setTranslating(textChatMessage, false, true);
                    }
                }
            });
        }
        else{
            TextTranslateHelper.showTranslateStatusAndUpdateDb(textChatMessage, true, true);
        }
    }

    //TODO:
    private void refreshTextUI(TextChatMessage textChatMessage, MultipartItemViewHolder itemViewHolder ,Boolean isTranslate) {
        itemViewHolder.refreshUI(ViewType.TEXT);
        SpannableString spannableString = AutoLinkHelper.getInstance().getSpannableString(mContext, "", null, itemViewHolder.mTvTextMessage, textChatMessage.text);
        itemViewHolder.mTvTextMessage.setText(spannableString);
        refreshTranslateStatusUI(textChatMessage, itemViewHolder);
    }

    /**
     * Description:设置文本智能翻译的样式
     * @param textChatMessage
     * @param itemViewHolder
     */
    private void refreshTranslateStatusUI(TextChatMessage textChatMessage, MultipartItemViewHolder itemViewHolder) {
        if(textChatMessage.isTranslateStatusVisible()){
            if(textChatMessage.isTranslating()){
                handleTranslateView(itemViewHolder, false, true);
                itemViewHolder.mTvTranslateSource.setText(R.string.text_translating);
            }
            else {
                if(!StringUtils.isEmpty(textChatMessage.getTranslatedResult()) && textChatMessage.isTranslateStatusVisible()) {
                    handleTranslateView(itemViewHolder, true, true);
                    itemViewHolder.mTvTextTranslation.setText(textChatMessage.getTranslatedResult());
                    itemViewHolder.mTvTranslateSource.setText(TextTranslateHelper.getSource(textChatMessage));
                }
                else{
                    handleTranslateView(itemViewHolder, false, false);
                }
            }

        }else{
            handleTranslateView(itemViewHolder, false, false);
        }
    }
    private void handleTranslateView(MultipartItemViewHolder itemViewHolder, boolean TextTranslationVisible, boolean TranslateSourceVisible) {
        if(TextTranslationVisible || TranslateSourceVisible){
            itemViewHolder.mVLineTranslation.setVisibility(View.VISIBLE);
        }else{
            itemViewHolder.mVLineTranslation.setVisibility(View.GONE);
        }

        if(TextTranslationVisible){
            itemViewHolder.mTvTextTranslation.setVisibility(View.VISIBLE);
        }
        else{
            itemViewHolder.mTvTextTranslation.setVisibility(View.GONE);
        }

        if(TranslateSourceVisible) {
            itemViewHolder.mTvTranslateSource.setVisibility(View.VISIBLE);
        }
        else {
            itemViewHolder.mTvTranslateSource.setVisibility(View.GONE);
        }
    }

    private void refreshImageUI(ImageChatMessage imageChatMessage, MultipartItemViewHolder itemViewHolder) {
        if (imageChatMessage.isGif) {
            itemViewHolder.refreshUI(ViewType.GIF);

            itemViewHolder.mIvGif.setTag(imageChatMessage.deliveryId);
            ImageChatHelper.showGif(mContext, itemViewHolder.mIvGif, itemViewHolder.mIvTagGif, imageChatMessage);

        } else {
            itemViewHolder.refreshUI(ViewType.IMAGE);
            ImageChatHelper.initImageContent(imageChatMessage, itemViewHolder.mIvImageMessage);
        }
    }

    private void refreshStickerUI(StickerChatMessage stickerChatMessage, MultipartItemViewHolder itemViewHolder) {
        if (stickerChatMessage.isGif()) {
            //TODO...
        }
        itemViewHolder.refreshUI(ViewType.STICKER);
        ImageChatHelper.initStickerContent(stickerChatMessage, itemViewHolder.mIvStickerMessage);
    }

    private void refreshVoiceUI(VoiceChatMessage voiceChatMessage, MultipartItemViewHolder itemViewHolder) {
        itemViewHolder.refreshUI(ViewType.VOICE);
        itemViewHolder.mTvVoiceMessage.setText(voiceChatMessage.duration + "\"");

        if(voiceChatMessage.playing) {
            itemViewHolder.mTvVoiceMessage.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.mipmap.icon_bing_voice_stop), null, null, null);
        } else {
            itemViewHolder.mTvVoiceMessage.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.mipmap.icon_bing_voice_play), null, null, null);

        }
    }




    @Override
    public int getItemCount() {
        return mChatPostMessageList.size();
    }

    public static class MultipartItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView mIvAvatar;
        private TextView mTvName;
        private TextView mTvTime;
        private FrameLayout mFlContent;
        private ImageView mIvImageMessage;
        private ImageView mIvStickerMessage;
        private FrameLayout mFlGifMessage;
        private GifImageView mIvGif;
        private ImageView mIvTagGif;
        private TextView mTvTextMessage;
        private RelativeLayout mRlTextMessage;
        private View mVLineTranslation;
        private TextView mTvTextTranslation;
        private TextView mTvTranslateSource;

        private RelativeLayout mRlOtherMessage;
        private ImageView mIvIconFlag;
        private TextView mTvTitle;
        private TextView mTvContent;
        private View mVLineBottom;
        private View mVLineEnd;
        private TextView mTvVoiceMessage;

        public MultipartItemViewHolder(View itemView) {
            super(itemView);
            mIvAvatar = itemView.findViewById(R.id.chat_left_multipart_avatar);
            mTvName = itemView.findViewById(R.id.tv_name);
            mTvTime = itemView.findViewById(R.id.tv_time);
            mFlContent = itemView.findViewById(R.id.fl_content);
            mFlGifMessage = itemView.findViewById(R.id.fl_gif_message);
            mIvGif = itemView.findViewById(R.id.iv_gif);
            mIvTagGif = itemView.findViewById(R.id.iv_tag_gif);
            mIvImageMessage = itemView.findViewById(R.id.iv_image_message);
            mIvStickerMessage = itemView.findViewById(R.id.iv_sticker_message);
            mTvTextMessage = itemView.findViewById(R.id.tv_text_message);
            mRlTextMessage = itemView.findViewById(R.id.rl_text_message);
            mVLineTranslation = itemView.findViewById(R.id.v_translation_line);
            mTvTextTranslation = itemView.findViewById(R.id.tv_text_translation);
            mTvTranslateSource = itemView.findViewById(R.id.tv_translate_source);
            mTvVoiceMessage = itemView.findViewById(R.id.tv_message_voice);
            mRlOtherMessage = itemView.findViewById(R.id.rl_other_message);
            mIvIconFlag = itemView.findViewById(R.id.tv_icon_flag);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mTvContent = itemView.findViewById(R.id.tv_content);
            mVLineBottom = itemView.findViewById(R.id.v_bottom_line);
            mVLineEnd = itemView.findViewById(R.id.rl_end);
        }


        public TextView getTvVoiceMessage() {
            return mTvVoiceMessage;
        }

        public void refreshUI(ViewType viewType) {
            if (ViewType.IMAGE == viewType) {
                mIvImageMessage.setVisibility(View.VISIBLE);
                mRlTextMessage.setVisibility(View.GONE);
                mTvTextMessage.setVisibility(View.GONE);
                mRlOtherMessage.setVisibility(View.GONE);
                mFlGifMessage.setVisibility(View.GONE);
                mIvStickerMessage.setVisibility(View.GONE);
                mTvVoiceMessage.setVisibility(View.GONE);
                return;
            }
            if (ViewType.TEXT == viewType) {
                mRlTextMessage.setVisibility(View.VISIBLE);
                mTvTextMessage.setVisibility(View.VISIBLE);
                mIvImageMessage.setVisibility(View.GONE);
                mRlOtherMessage.setVisibility(View.GONE);
                mFlGifMessage.setVisibility(View.GONE);
                mIvStickerMessage.setVisibility(View.GONE);
                mTvVoiceMessage.setVisibility(View.GONE);


                return;
            }
            if (ViewType.GIF == viewType) {
                mFlGifMessage.setVisibility(View.VISIBLE);
                mRlTextMessage.setVisibility(View.GONE);
                mTvTextMessage.setVisibility(View.GONE);
                mIvImageMessage.setVisibility(View.GONE);
                mRlOtherMessage.setVisibility(View.GONE);
                mIvStickerMessage.setVisibility(View.GONE);
                mTvVoiceMessage.setVisibility(View.GONE);
                return;
            }
            if (ViewType.STICKER == viewType) {
                mIvStickerMessage.setVisibility(View.VISIBLE);
                mRlOtherMessage.setVisibility(View.GONE);
                mIvImageMessage.setVisibility(View.GONE);
                mRlTextMessage.setVisibility(View.GONE);
                mTvTextMessage.setVisibility(View.GONE);
                mFlGifMessage.setVisibility(View.GONE);
                mTvVoiceMessage.setVisibility(View.GONE);
                return;
            }
            if (ViewType.VOICE == viewType) {
                mTvVoiceMessage.setVisibility(View.VISIBLE);
                mIvStickerMessage.setVisibility(View.GONE);
                mRlOtherMessage.setVisibility(View.GONE);
                mIvImageMessage.setVisibility(View.GONE);
                mRlTextMessage.setVisibility(View.GONE);
                mTvTextMessage.setVisibility(View.GONE);
                mFlGifMessage.setVisibility(View.GONE);
                return;
            }
            mRlOtherMessage.setVisibility(View.VISIBLE);
            mIvImageMessage.setVisibility(View.GONE);
            mRlTextMessage.setVisibility(View.GONE);
            mTvTextMessage.setVisibility(View.GONE);
            mFlGifMessage.setVisibility(View.GONE);
            mIvStickerMessage.setVisibility(View.GONE);
            mTvVoiceMessage.setVisibility(View.GONE);
        }


    }

    private enum ViewType {

        IMAGE,

        GIF,

        TEXT,

        MEDIA,

        VOICE,

        STICKER
    }


    public interface OnHandleClickListener {
        void onClick(int position, MultipartItemViewHolder holder);
    }


}
