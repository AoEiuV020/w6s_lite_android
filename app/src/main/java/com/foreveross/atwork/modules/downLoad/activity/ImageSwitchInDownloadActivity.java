package com.foreveross.atwork.modules.downLoad.activity;

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
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoImageChatMessage;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.modules.downLoad.fragment.ImageSwitchInDownloadFragment;
import com.foreveross.atwork.support.AtworkBaseActivity;
import com.foreveross.atwork.support.SingleFragmentActivity;
import com.foreveross.atwork.utils.ImageCacheHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.foreveross.atwork.modules.downLoad.fragment.ImageSwitchInDownloadFragment.ARGUMENT_SESSION;

/**
 * Created by wuzejie on 2020/1/19.
 * Description:我的下载
 */

public class ImageSwitchInDownloadActivity extends SingleFragmentActivity {


    public static List<ChatPostMessage> sImageChatMessageList = new ArrayList<>();

    private int mIndexSwitchImage;

    private Session mSession;

    private String mBingId;

    private boolean mNeedHideIndexPosUI = false;

    private static FileData mFileData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        mIndexSwitchImage = getIntent().getIntExtra(ImageSwitchInDownloadFragment.INDEX_SWITCH_IMAGE, 0);
        mSession = getIntent().getParcelableExtra(ARGUMENT_SESSION);


        mBingId = getIntent().getStringExtra(ImageSwitchInDownloadFragment.DATA_BING_ID);
        mNeedHideIndexPosUI = getIntent().getBooleanExtra(ImageSwitchInDownloadFragment.DATA_HIDE_INDEX_POS_UI, false);

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
        ImageSwitchInDownloadFragment imageSwitchFragment = new ImageSwitchInDownloadFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ImageSwitchInDownloadFragment.IMAGE_DATA, (java.io.Serializable) sImageChatMessageList);
        bundle.putSerializable(ImageSwitchInDownloadFragment.FILE_DATA, mFileData);
        bundle.putInt(ImageSwitchInDownloadFragment.INDEX_SWITCH_IMAGE, mIndexSwitchImage);
        bundle.putString(ImageSwitchInDownloadFragment.DATA_BING_ID, mBingId);
        bundle.putBoolean(ImageSwitchInDownloadFragment.DATA_HIDE_INDEX_POS_UI, mNeedHideIndexPosUI);


        if (mSession != null) {
            bundle.putParcelable(ImageSwitchInDownloadFragment.ARGUMENT_SESSION, mSession);
        }
        imageSwitchFragment.setArguments(bundle);
        return imageSwitchFragment;
    }


    @Override
    public void changeStatusBar() {
        //do nothing
    }



    public static void showImageSwitchView(Context context, FileData fileData) {
        //伪造出 imageChatMessage 传到图片界面
        ImageChatMessage fakeImageChatMessage = new ImageChatMessage();
        fakeImageChatMessage.mBodyType = BodyType.Image;
        fakeImageChatMessage.deliveryId = UUID.randomUUID().toString();
        fakeImageChatMessage.mediaId = fileData.mediaId;
        fakeImageChatMessage.isGif = fileData.fileType == FileData.FileType.File_Gif;
        fakeImageChatMessage.filePath = fileData.filePath;

        Session fakeSession = new Session();
        fakeSession.type = SessionType.User;

        mFileData = fileData;

        showImageSwitchView(context, fakeImageChatMessage, fakeSession);
    }


    public static void showImageSwitchView(Context context, ChatPostMessage targetMessage, @Nullable Session session) {
        showImageSwitchView(context, targetMessage, ListUtil.makeSingleList(targetMessage), session);
    }

    public static void showImageSwitchView(Context context, ChatPostMessage targetMessage, List<ChatPostMessage> messageList, @Nullable Session session) {
        refreshImageChatMessageList(messageList);
        int count = ImageSwitchInDownloadActivity.sImageChatMessageList.indexOf(targetMessage);
        Intent intent = new Intent();
        intent.putExtra(ImageSwitchInDownloadFragment.INDEX_SWITCH_IMAGE, count);
        intent.setClass(BaseApplicationLike.baseContext, ImageSwitchInDownloadActivity.class);
        if (null != session) {
            intent.putExtra(ImageSwitchInDownloadFragment.ARGUMENT_SESSION, session);
        }

        if(context instanceof AtworkBaseActivity) {
            ((AtworkBaseActivity)context).startActivity(intent, false);

        } else {
            context.startActivity(intent);

        }
    }



    public static void refreshImageChatMessageList(List<ChatPostMessage> messageList) {
        ImageSwitchInDownloadActivity.sImageChatMessageList.clear();

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
                        ImageSwitchInDownloadActivity.sImageChatMessageList.add(message);
                    }
                    continue;
                }

                if(message instanceof AnnoImageChatMessage) {
                    AnnoImageChatMessage annoImageChatMessage = (AnnoImageChatMessage) message;
                    ImageSwitchInDownloadActivity.sImageChatMessageList.addAll(annoImageChatMessage.getImageContentInfoMessages());
                    continue;
                }

                ImageSwitchInDownloadActivity.sImageChatMessageList.add(message);
            }

        }
        Collections.sort(ImageSwitchInDownloadActivity.sImageChatMessageList);
    }


}
