package com.foreveross.atwork.modules.image.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoImageChatMessage;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.modules.image.fragment.ImageSwitchFragment;
import com.foreveross.atwork.support.AtworkBaseActivity;
import com.foreveross.atwork.support.SingleFragmentActivity;
import com.foreveross.atwork.utils.ImageCacheHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.foreveross.atwork.modules.image.fragment.ImageSwitchFragment.ARGUMENT_SESSION;


public class ImageSwitchInChatActivity extends SingleFragmentActivity {


    public static List<ChatPostMessage> sImageChatMessageList = new ArrayList<>();

    private int mIndexSwitchImage;

    private Session mSession;

    private String mBingId;

    private boolean mNeedHideIndexPosUI = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        mIndexSwitchImage = getIntent().getIntExtra(ImageSwitchFragment.INDEX_SWITCH_IMAGE, 0);
        mSession = getIntent().getParcelableExtra(ARGUMENT_SESSION);


        mBingId = getIntent().getStringExtra(ImageSwitchFragment.DATA_BING_ID);
        mNeedHideIndexPosUI = getIntent().getBooleanExtra(ImageSwitchFragment.DATA_HIDE_INDEX_POS_UI, false);

         /*set it to be no title*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*set it to be full screen*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ImageCacheHelper.checkPool();

        super.onCreate(savedInstanceState);


        if(AtworkConfig.SCREEN_ORIENTATION_USER_SENSOR) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        }
    }

    @Override
    protected Fragment createFragment() {
        ImageSwitchFragment imageSwitchFragment = new ImageSwitchFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ImageSwitchFragment.IMAGE_DATA, (java.io.Serializable) sImageChatMessageList);
        bundle.putInt(ImageSwitchFragment.INDEX_SWITCH_IMAGE, mIndexSwitchImage);
        bundle.putString(ImageSwitchFragment.DATA_BING_ID, mBingId);
        bundle.putBoolean(ImageSwitchFragment.DATA_HIDE_INDEX_POS_UI, mNeedHideIndexPosUI);


        if (mSession != null) {
            bundle.putParcelable(ImageSwitchFragment.ARGUMENT_SESSION, mSession);
        }
        imageSwitchFragment.setArguments(bundle);
        return imageSwitchFragment;
    }


    @Override
    public void changeStatusBar() {
        //do nothing
    }


    public static void showImageSwitchView(Context context, @Nullable String filePath, @Nullable String mediaId, boolean isGif) {
        //伪造出 imageChatMessage 传到图片界面
        ImageChatMessage fakeImageChatMessage = new ImageChatMessage();
        fakeImageChatMessage.mBodyType = BodyType.Image;
        fakeImageChatMessage.deliveryId = UUID.randomUUID().toString();
        fakeImageChatMessage.mediaId = mediaId;
        fakeImageChatMessage.isGif = isGif;
        fakeImageChatMessage.filePath = filePath;

        Session fakeSession = new Session();
        fakeSession.type = SessionType.User;

        showImageSwitchView(context, fakeImageChatMessage, fakeSession);
    }


    public static void showImageSwitchView(Context context, ChatPostMessage targetMessage, @Nullable Session session) {
        showImageSwitchView(context, targetMessage, ListUtil.makeSingleList(targetMessage), session);
    }

    public static void showImageSwitchView(Context context, ChatPostMessage targetMessage, List<ChatPostMessage> messageList, @Nullable Session session) {
        refreshImageChatMessageList(messageList);
        int count = ImageSwitchInChatActivity.sImageChatMessageList.indexOf(targetMessage);
        Intent intent = new Intent();
        intent.putExtra(ImageSwitchFragment.INDEX_SWITCH_IMAGE, count);
        intent.setClass(BaseApplicationLike.baseContext, ImageSwitchInChatActivity.class);
        if (null != session) {
            intent.putExtra(ImageSwitchFragment.ARGUMENT_SESSION, session);
        }

        if(context instanceof AtworkBaseActivity) {
            ((AtworkBaseActivity)context).startActivity(intent, false);

        } else {
            context.startActivity(intent);

        }
    }



    public static void refreshImageChatMessageList(List<ChatPostMessage> messageList) {
        ImageSwitchInChatActivity.sImageChatMessageList.clear();

        for (ChatPostMessage message : messageList) {
            if (message.isBurn() || message.isUndo()) {
                continue;
            }

            if (message instanceof ImageChatMessage
                    || message instanceof MicroVideoChatMessage
                    || message instanceof FileTransferChatMessage
                    || message instanceof AnnoImageChatMessage) {

                if (message instanceof FileTransferChatMessage) {
                    FileTransferChatMessage fileTransferChatMessage = (FileTransferChatMessage) message;
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


}
