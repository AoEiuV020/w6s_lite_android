package com.foreveross.atwork.modules.chat.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foreverht.workplus.module.chat.activity.ShowLocationActivity;
import com.foreverht.workplus.module.sticker.activity.StickerViewActivity;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.user.UserHandleBasic;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MultipartChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.StickerChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceMedia;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.reference.ReferenceMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.UndoEventMessage;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.modules.bing.listener.VoicePlayListener;
import com.foreveross.atwork.modules.chat.activity.MultiPartDetailActivity;
import com.foreveross.atwork.modules.chat.adapter.MultipartMessageAdapterV2;
import com.foreveross.atwork.modules.chat.component.multipart.item.MultipartMessageDetailVoiceView;
import com.foreveross.atwork.modules.chat.component.multipart.item.reference.MultipartMessageDetailReferenceVoiceView;
import com.foreveross.atwork.modules.chat.presenter.VoiceChatViewRefreshUIPresenter;
import com.foreveross.atwork.modules.chat.util.AudioRecord;
import com.foreveross.atwork.modules.chat.util.MultipartMsgHelper;
import com.foreveross.atwork.modules.chat.util.ShareMsgHelper;
import com.foreveross.atwork.modules.contact.activity.PersonalInfoActivity;
import com.foreveross.atwork.modules.group.activity.TransferMessageActivity;
import com.foreveross.atwork.modules.group.module.TransferMessageControlAction;
import com.foreveross.atwork.modules.group.module.TransferMessageMode;
import com.foreveross.atwork.modules.image.activity.ImageSwitchInChatActivity;
import com.foreveross.atwork.modules.image.fragment.ImageSwitchFragment;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ChatMessageHelper;
import com.foreveross.atwork.utils.watermark.WaterMarkHelper;
import com.foreveross.watermark.core.WaterMarkUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.foreveross.atwork.modules.chat.activity.MultiPartDetailActivity.DATA_TRANSLATE_LANGUAGE;

/**
 * Created by dasunsy on 2017/6/22.
 */

public class MultiPartDetailFragment extends BackHandledFragment{

    //广播action：
    public static final String REFRESH_MULTIPART_MESSAGE_LIST = "REFRESH_MULTIPART_MESSAGE_LIST";

    private LinearLayout mLlRoot;
    private ImageView mIvBack;
    private TextView mTvTitle;
    private ImageView mIvForwards;
    private TextView mTvTimeline;
    private RecyclerView mRvList;
    private RelativeLayout mRlTimeline;

    private MultipartMessageAdapterV2 mMultipartMessageAdapter;

    private MultipartChatMessage mMultipartChatMessage;
    //翻译的语种
    private String mStrTranslationShortName = "";

    private ProgressDialogHelper mProgressDialogHelper;

    private  List<ChatPostMessage> mChatPostMessageList = new ArrayList<>();

    private BroadcastReceiver mRefreshViewBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (REFRESH_MULTIPART_MESSAGE_LIST.equals(action)) {
                refreshListAdapter();

            }
        }
    };

    public void refreshListAdapter() {
        if (null != mMultipartMessageAdapter) {
            mMultipartMessageAdapter.notifyDataSetChanged();

            LogUtil.e("mMultipartMessageAdapter.notifyDataSetChanged");
        }

    }

    private void registerBroadcast() {
        IntentFilter refreshViewIntentFilter = new IntentFilter();
        refreshViewIntentFilter.addAction(REFRESH_MULTIPART_MESSAGE_LIST);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mRefreshViewBroadcastReceiver, refreshViewIntentFilter);
    }
    private void unregisterBroadcast() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mRefreshViewBroadcastReceiver);
    }
    private void clearBroadcast() {
        mRefreshViewBroadcastReceiver = null;
    }

    @Override
    public void onDestroy() {
        unregisterBroadcast();
        super.onDestroy();
        clearBroadcast();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_multipart_detail, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initUI();
        registerListener();
        registerBroadcast();
    }

    @Override
    protected boolean onBackPressed() {
        AudioRecord.stopPlaying();
        finish();
        return false;
    }

    @Override
    protected void findViews(View view) {
        mLlRoot = view.findViewById(R.id.ll_root);
        mIvBack = view.findViewById(R.id.iv_back);
        mTvTitle = view.findViewById(R.id.tv_title);
        mIvForwards = view.findViewById(R.id.iv_forward);
        mTvTimeline = view.findViewById(R.id.tv_time_line);
        mRvList = view.findViewById(R.id.rl_msg_list);
        mRlTimeline = view.findViewById(R.id.rl_time_line);
    }

    private void registerListener() {
        mIvBack.setOnClickListener(v -> onBackPressed());
        mIvForwards.setOnClickListener(v -> {

            TransferMessageControlAction transferMessageControlAction = new TransferMessageControlAction();
            transferMessageControlAction.setSendMessageList(ListUtil.makeSingleList(mMultipartChatMessage));
            transferMessageControlAction.setSendMode(TransferMessageMode.FORWARD);
            Intent intent = TransferMessageActivity.Companion.getIntent(BaseApplicationLike.baseContext, transferMessageControlAction);

            startActivity(intent);
        });
    }

    private void initData() {
        Bundle bundle = getArguments();
        mMultipartChatMessage = (MultipartChatMessage) bundle.getSerializable(MultiPartDetailActivity.DATA_MULTIPART_MSG);
        mStrTranslationShortName = bundle.getString(DATA_TRANSLATE_LANGUAGE);
    }

    private void initUI() {
        AtworkUtil.tempHandleIconColor(mIvForwards);

        ViewUtil.setVisible(mIvForwards, isCanForward());

        mProgressDialogHelper = new ProgressDialogHelper(getActivity());
        String title = MultipartMsgHelper.getTitle(mMultipartChatMessage);
        if(title.contains("的聊天记录")) {
            title = title.replace("的聊天记录", "");
        }
        mTvTitle.setText(title);
        initRecycleView(mChatPostMessageList);
        refreshWatermark();

        MultipartMsgHelper.loadData(getActivity(), mMultipartChatMessage, new MultipartMsgHelper.OnLoadDataListener() {
            @Override
            public void onStart() {
                mProgressDialogHelper.show();

            }

            @Override
            public void onNetStart() {
//                mProgressDialogHelper.show();
            }

            @Override
            public void onSuccess(List<ChatPostMessage> chatPostMessageList) {
                mProgressDialogHelper.dismiss();

                LogUtil.e("成功解析~~~~~~~~");

                mTvTimeline.setText(MultipartMsgHelper.getTimeRange(chatPostMessageList));

                mChatPostMessageList.addAll(chatPostMessageList);
                mMultipartMessageAdapter.notifyDataSetChanged();

                mRlTimeline.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {
                mProgressDialogHelper.dismiss();
                toast(R.string.common);
            }
        });
    }

    private boolean isCanForward() {
        if(!mMultipartChatMessage.hasMedias()) {
            return true;
        }


        if(DomainSettingsManager.getInstance().handleChatFileTransferEnabled()) {
            return true;
        }

        return false;
    }



    private void refreshWatermark() {
        int waterBgColor = Color.parseColor("#FBFBFB");

        if(mMultipartChatMessage.mWatermarkEnable) {

            if(ParticipantType.Discussion != mMultipartChatMessage.mToType) {
                WaterMarkUtil.setLoginUserWatermark(getActivity(), mLlRoot, waterBgColor, -1, "");

            } else {
                WaterMarkHelper.setDiscussionWatermark(getActivity(), mLlRoot, mMultipartChatMessage.to, waterBgColor, -1);
            }

        }
//        else {
//            //非强制打开水印则按正常逻辑控制
//            String sessionId = ChatMessageHelper.getChatUser(mMultipartChatMessage).mUserId;
//            WaterMarkHelper.setWatermark(getActivity(), mLlRoot, sessionId, waterBgColor, -1);
//        }
    }

    private void initRecycleView(List<ChatPostMessage> chatPostMessageList) {

        mMultipartMessageAdapter = new MultipartMessageAdapterV2(chatPostMessageList, mStrTranslationShortName);
        mMultipartMessageAdapter.setOnItemClickListener((adapter, view, position) -> {
            ChatPostMessage chatPostMessage = chatPostMessageList.get(position);
            if(chatPostMessage instanceof ShareChatMessage) {
                ShareChatMessage shareChatMessage = (ShareChatMessage) chatPostMessage;
                if(ShareChatMessage.ShareType.Link.toString().equalsIgnoreCase(shareChatMessage.getShareType())) {
                    ShareMsgHelper.jumpLinkShare(getActivity(), shareChatMessage);

                } else if(ShareChatMessage.ShareType.OrgInviteBody.toString().equalsIgnoreCase(shareChatMessage.getShareType())) {
                    ShareMsgHelper.jumpOrgInvite(getActivity(), shareChatMessage);

                } else if (ShareChatMessage.ShareType.Loc.toString().equalsIgnoreCase(shareChatMessage.getShareType())) {
                    routeLocationDetail(shareChatMessage);
                }
            } else if(chatPostMessage instanceof FileTransferChatMessage) {
                FileTransferChatMessage fileTransferChatMessage = (FileTransferChatMessage) chatPostMessage;
                showFile(fileTransferChatMessage);
            } else if(chatPostMessage instanceof ImageChatMessage) {
                showImageSwitchFragment(chatPostMessage);

            } else if(chatPostMessage instanceof AnnoImageChatMessage) {
//                showImageSwitchFragment(chatPostMessage);

            } else if(chatPostMessage instanceof MicroVideoChatMessage) {
                showImageSwitchFragment(chatPostMessage);
            } else if(chatPostMessage instanceof VoiceChatMessage) {
                VoiceChatMessage voiceChatMessage = (VoiceChatMessage)chatPostMessage;

                handleVoicePlay(view, voiceChatMessage);

            } else if(chatPostMessage instanceof ReferenceMessage) {

                ReferenceMessage referenceMessage = (ReferenceMessage) chatPostMessage;
                if(referenceMessage.mReferencingMessage instanceof ImageChatMessage
                    || referenceMessage.mReferencingMessage instanceof MicroVideoChatMessage) {
                    ImageSwitchInChatActivity.showImageSwitchView(getContext(), referenceMessage.mReferencingMessage, null);


                } else if(referenceMessage.mReferencingMessage instanceof StickerChatMessage) {
                    Intent intent = StickerViewActivity.Companion.getIntent(mActivity, (StickerChatMessage) referenceMessage.mReferencingMessage);
                    startActivity(intent, false);

                } else if(referenceMessage.mReferencingMessage instanceof FileTransferChatMessage) {
                    FileTransferChatMessage fileTransferChatMessage = (FileTransferChatMessage) referenceMessage.mReferencingMessage;
                    showFile(fileTransferChatMessage);

                } else if(referenceMessage.mReferencingMessage instanceof VoiceChatMessage) {
                    handleVoicePlayWithReference(view, (VoiceChatMessage) referenceMessage.mReferencingMessage);

                } else if(referenceMessage.mReferencingMessage instanceof ShareChatMessage) {
                    ShareChatMessage shareChatMessage = (ShareChatMessage) referenceMessage.mReferencingMessage;

                    if (ShareChatMessage.ShareType.Link.toString().equalsIgnoreCase(shareChatMessage.getShareType())){
                        ShareMsgHelper.jumpLinkShare(getActivity(), shareChatMessage);

                    } else if (ShareChatMessage.ShareType.BusinessCard.toString().equalsIgnoreCase(shareChatMessage.getShareType())){
                        if (StringUtils.isEmpty(shareChatMessage.getContent().mShareUserId)){
                            return;
                        }

                        User user = UserManager.getInstance().queryLocalUser(shareChatMessage.getContent().mShareUserId);
                        if (user == null) {
                            user = new User();
                            user.mUserId = shareChatMessage.getContent().mShareUserId;
                            user.mAvatar = shareChatMessage.getContent().mShareUserAvatar;
                            user.mName = shareChatMessage.getContent().mShareUserName;
                            user.mDomainId = shareChatMessage.getContent().mShareDomainId;
                        }
                        startActivity(PersonalInfoActivity.getIntent(getActivity(), user));
                    }else if (ShareChatMessage.ShareType.Loc.toString().equalsIgnoreCase(shareChatMessage.getShareType())) {
                        routeLocationDetail(shareChatMessage);
                    }



                } else if(referenceMessage.mReferencingMessage instanceof MultipartChatMessage) {

                    //暂时屏蔽合并引用的点击
//                    MultipartChatMessage multipartChatMessage = (MultipartChatMessage) referenceMessage.mReferencingMessage;
//                    startActivity(MultiPartDetailActivity.getIntent(getActivity(), multipartChatMessage));
                }
            }

        });



        //list布局
        LinearLayoutManager linearLayoutMgr = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRvList.setLayoutManager(linearLayoutMgr);
        mRvList.setAdapter(mMultipartMessageAdapter);
    }

    private void routeLocationDetail(ShareChatMessage shareChatMessage) {
        Object object = shareChatMessage.getChatBody().get(ShareChatMessage.SHARE_MESSAGE);
        if (!(object instanceof ShareChatMessage.LocationBody)) {
            return;
        }
        ShareChatMessage.LocationBody locationBody = (ShareChatMessage.LocationBody)object;
        ShowLocationActivity.Companion.startActivity(getActivity(), locationBody);
    }

    private void handleVoicePlay(View view, VoiceChatMessage voiceChatMessage) {
        if (voiceChatMessage.playing) {
            AudioRecord.stopPlaying();
            voiceChatMessage.playing = false;


        } else {
            if(view instanceof MultipartMessageDetailVoiceView) {
                MultipartMessageDetailVoiceView multipartMessageDetailVoiceView = (MultipartMessageDetailVoiceView) view;
                playAudio(multipartMessageDetailVoiceView.contentView.getTvMessageVoice(), voiceChatMessage);

            }

        }
    }



    private void handleVoicePlayWithReference(View view, VoiceChatMessage voiceChatMessage) {
        if(view instanceof MultipartMessageDetailReferenceVoiceView)  {
            VoiceChatViewRefreshUIPresenter presenter = ((MultipartMessageDetailReferenceVoiceView) view).multipartMessageDetailReferenceVoiceContentView.getPresenter();


            if (voiceChatMessage.playing) {
                AudioRecord.stopPlaying();
                voiceChatMessage.playing = false;
                presenter.stopPlayingAnimation();

            } else {
                playAudioWithReference(presenter, voiceChatMessage);

            }

        }



    }


    private void showFile(FileTransferChatMessage fileTransferChatMessage) {
        if (fileTransferChatMessage.isGifType() || fileTransferChatMessage.isStaticImgType()) {
            showImageSwitchFragment(fileTransferChatMessage);

        } else {
            showFileStatusFragment(fileTransferChatMessage);
        }
    }


    public void playAudio(TextView tvVoiceMessage, VoiceChatMessage voiceChatMessage) {
        if(VoipHelper.isHandlingVoipCall()) {
            AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);
            return;
        }

        if(MediaCenterNetManager.isDownloading(voiceChatMessage.getMediaId())) {
            return;
        }

        voiceChatMessage.playing = true;

        AudioRecord.playAudio(AtworkApplicationLike.baseContext, voiceChatMessage, new VoicePlayListener() {
            @Override
            public void start() {
                tvVoiceMessage.post(() -> tvVoiceMessage.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(AtworkApplicationLike.baseContext, R.mipmap.icon_bing_voice_stop), null, null, null));

            }

            @Override
            public void stop(VoiceMedia voiceMedia) {
                voiceChatMessage.playing = false;
                tvVoiceMessage.post(() -> tvVoiceMessage.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(AtworkApplicationLike.baseContext, R.mipmap.icon_bing_voice_play), null, null, null));


            }
        });
    }


    public void playAudioWithReference(VoiceChatViewRefreshUIPresenter refreshUIPresenter, VoiceChatMessage voiceChatMessage) {
        if(VoipHelper.isHandlingVoipCall()) {
            AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);
            return;
        }

        if(MediaCenterNetManager.isDownloading(voiceChatMessage.getMediaId())) {
            return;
        }

        voiceChatMessage.playing = true;

        AudioRecord.playAudio(AtworkApplicationLike.baseContext, voiceChatMessage, new VoicePlayListener() {
            @Override
            public void start() {
                BaseApplicationLike.runOnMainThread(refreshUIPresenter::playingAnimation);

            }

            @Override
            public void stop(VoiceMedia voiceMedia) {
                voiceChatMessage.playing = false;
                BaseApplicationLike.runOnMainThread(refreshUIPresenter::stopPlayingAnimation);




            }
        });
    }


    private void showFileStatusFragment(FileTransferChatMessage fileTransferChatMessage) {
        FileStatusFragment fileStatusFragment = new FileStatusFragment();
        UserHandleBasic chatUser = ChatMessageHelper.getChatUser(fileTransferChatMessage);
        fileStatusFragment.initBundle(chatUser.mUserId, fileTransferChatMessage, mMultipartChatMessage);
        fileStatusFragment.show(getChildFragmentManager(), "FILE_DIALOG");

    }

    private void showImageSwitchFragment(ChatPostMessage message) {
        refreshImageChatMessageList();
        int count = ImageSwitchInChatActivity.sImageChatMessageList.indexOf(message);
        Intent intent = new Intent();
        intent.putExtra(ImageSwitchFragment.INDEX_SWITCH_IMAGE, count);
        intent.setClass(BaseApplicationLike.baseContext, ImageSwitchInChatActivity.class);
        startActivity(intent, false);

    }

    /**
     * 获取消息中的所有图片消息
     */
    private void refreshImageChatMessageList() {
        ImageSwitchInChatActivity.sImageChatMessageList.clear();

        for (ChatPostMessage message : mChatPostMessageList) {
            if(message.isBurn() || message.isUndo()) {
                continue;
            }

            if (message instanceof ImageChatMessage
                    || message instanceof MicroVideoChatMessage
                    || message instanceof FileTransferChatMessage
                    || message instanceof AnnoImageChatMessage) {

                if (message instanceof FileTransferChatMessage) {
                    FileTransferChatMessage fileTransferChatMessage = (FileTransferChatMessage)message;
                    if (fileTransferChatMessage.isGifType() || fileTransferChatMessage.isStaticImgType()) {
                        ImageSwitchInChatActivity.sImageChatMessageList.add(message);
                    }
                    continue;
                }


                if(message instanceof AnnoImageChatMessage) {
                    AnnoImageChatMessage annoImageChatMessage = (AnnoImageChatMessage) message;
                    ImageSwitchInChatActivity.sImageChatMessageList.addAll(annoImageChatMessage.getImageContentInfoMessages());
                    continue;
                }

                ImageSwitchInChatActivity.sImageChatMessageList.add(message);
            }

        }
        Collections.sort(ImageSwitchInChatActivity.sImageChatMessageList);
    }

    @Override
    public void onUndoMsgReceive(UndoEventMessage undoEventMessage) {
        if(undoEventMessage.isMsgUndo(mMultipartChatMessage.deliveryId)) {

            if (isAdded()) {
                showUndoDialog(getActivity(), undoEventMessage);
            }

            String dataPath = MultipartMsgHelper.getMultipartPath(mMultipartChatMessage);
            FileUtil.delete(dataPath);
        }

    }
}
