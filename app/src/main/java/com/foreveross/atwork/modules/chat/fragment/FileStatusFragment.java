package com.foreveross.atwork.modules.chat.fragment;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.BasicDialogFragment;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MultipartChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.UndoEventMessage;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk;
import com.foreveross.atwork.modules.chat.component.FileStatusView;
import com.foreveross.atwork.modules.image.activity.ImageSwitchInChatActivity;
import com.foreveross.atwork.utils.ChatMessageHelper;
import com.foreveross.atwork.utils.OfficeHelper;
import com.foreveross.theme.manager.SkinMaster;

import java.io.File;
import java.util.List;

import static com.foreveross.atwork.modules.file.activity.OfficeViewActivity.INTENT_TYPE;
import static com.foreveross.atwork.modules.file.activity.OfficeViewActivity.VIEW_FROM_CHAT_LIST;
import static com.foreveross.atwork.modules.file.activity.OfficeViewActivity.VIEW_FROM_FILE_DETAIL;
import static com.foreveross.atwork.modules.file.activity.OfficeViewActivity.VIEW_FROM_OTHER;


public class FileStatusFragment extends BasicDialogFragment {

    public static String FILE_ITEM = "FILE_ITEM";
    public static String MULTIPART_ITEM = "MULTIPART_ITEM";
    public static String SESSION_ID = "SESSION_ID";

    private FileStatusView mFileStatusView;

    private PostTypeMessage mPostTypeMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mFileStatusView = new FileStatusView(getActivity());
        mFileStatusView.findViewById(R.id.title_bar_chat_detail_back).setOnClickListener(v -> dismiss());
        SkinMaster.getInstance().changeTheme(mFileStatusView);
        return mFileStatusView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFileStatusView != null) {
            mFileStatusView.onResume();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            FileTransferChatMessage fileTransferChatMessage = (FileTransferChatMessage) getArguments().getSerializable(FILE_ITEM);
            MultipartChatMessage multipartChatMessage = (MultipartChatMessage) getArguments().getSerializable(MULTIPART_ITEM);
            String sessionId = getArguments().getString(SESSION_ID, "");
            int intentType = getArguments().getInt(INTENT_TYPE, VIEW_FROM_OTHER);
            getArguments().putInt(INTENT_TYPE, VIEW_FROM_OTHER);
            mFileStatusView.setChatMessage(sessionId, fileTransferChatMessage, multipartChatMessage, intentType);
            mFileStatusView.shouldDownloadInAdvance();
        }


    }

    @Override
    public void onPause() {
        if (mFileStatusView != null) {
            mFileStatusView.mIsViewPause = true;
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(AtworkConfig.SCREEN_ORIENTATION_USER_SENSOR) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onUndoMsgReceive(UndoEventMessage undoEventMessage) {
        if (null != undoEventMessage && undoEventMessage.isMsgUndo(mPostTypeMessage.deliveryId)) {
            showUndoDialog(getActivity(), undoEventMessage);
        }
    }

    public void initBundle(String sessionId, FileTransferChatMessage fileTransferChatMessage, MultipartChatMessage multipartChatMessage) {
        initBundle(sessionId, fileTransferChatMessage, multipartChatMessage, VIEW_FROM_OTHER);
    }

    public void initBundle(String sessionId, FileTransferChatMessage fileTransferChatMessage, MultipartChatMessage multipartChatMessage, int intentType){
        Bundle bundle = new Bundle();
        bundle.putSerializable(FILE_ITEM, fileTransferChatMessage);
        if (null == multipartChatMessage) {
            mPostTypeMessage = fileTransferChatMessage;

        } else {
            bundle.putSerializable(MULTIPART_ITEM, multipartChatMessage);
            mPostTypeMessage = multipartChatMessage;
        }
        if(VIEW_FROM_OTHER != intentType){
            bundle.putString(SESSION_ID, sessionId);
            bundle.putInt(INTENT_TYPE, VIEW_FROM_FILE_DETAIL);
        }
        setArguments(bundle);
    }


    public static void showFileStatusView(OpenFileStatusViewAction openFileStatusViewAction) {
        FragmentActivity activity = openFileStatusViewAction.activity;
        FileTransferChatMessage fileTransferChatMessage = openFileStatusViewAction.fileTransferChatMessage;
        List<ChatPostMessage>  messageList = openFileStatusViewAction.messageList;
        Session session = openFileStatusViewAction.session;
        MultipartChatMessage multipartChatMessage = openFileStatusViewAction.multipartChatMessage;
        int intentType = openFileStatusViewAction.intentType;


        if (fileTransferChatMessage.fileType.equals(FileData.FileType.File_Image) || fileTransferChatMessage.fileType.equals(FileData.FileType.File_Gif)) {
            ImageSwitchInChatActivity.showImageSwitchView(activity, fileTransferChatMessage, messageList, session);
            return;
        }


        if(shouldPreviewLocal(fileTransferChatMessage)){
            previewLocal(activity, fileTransferChatMessage, VIEW_FROM_CHAT_LIST);
            return;
        }

        doShowFileStatusView(activity, fileTransferChatMessage, multipartChatMessage, intentType);

    }

    private static void doShowFileStatusView(FragmentActivity activity, FileTransferChatMessage fileTransferChatMessage, MultipartChatMessage multipartChatMessage, int intentType) {
        FileStatusFragment fileStatusFragment = new FileStatusFragment();
        fileStatusFragment.initBundle(ChatMessageHelper.getChatUserSessionId(fileTransferChatMessage), fileTransferChatMessage, multipartChatMessage, intentType);
        fileStatusFragment.show(activity.getSupportFragmentManager(), "FILE_DIALOG");
    }


    public static boolean shouldPreviewLocal(FileTransferChatMessage fileTransferChatMessage) {
        if(!OfficeHelper.isSupportType(fileTransferChatMessage.filePath)) {
            return false;
        }
        if(!isFileExist(fileTransferChatMessage)) {
            return false;
        }
        return true;
    }


    private static boolean isFileExist(FileTransferChatMessage fileTransferChatMessage) {
        if (StringUtils.isEmpty(fileTransferChatMessage.filePath)) {
            return false;
        }
        File file = new File(fileTransferChatMessage.filePath);
        return file.exists();
    }


    public static void previewLocal(Context context, FileTransferChatMessage fileTransferChatMessage, int intentType) {
        EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(fileTransferChatMessage.filePath, false, fileName -> {

            OfficeHelper.previewByX5(context, fileName, ChatMessageHelper.getChatUserSessionId(fileTransferChatMessage), fileTransferChatMessage, intentType);

        });
    }


    public class OpenFileStatusViewAction {
        public FragmentActivity activity;
        public FileTransferChatMessage fileTransferChatMessage;
        public MultipartChatMessage multipartChatMessage;
        public int intentType;
        public List<ChatPostMessage> messageList;
        public Session session;

        private OpenFileStatusViewAction(Builder builder) {
            activity = builder.activity;
            fileTransferChatMessage = builder.fileTransferChatMessage;
            multipartChatMessage = builder.multipartChatMessage;
            intentType = builder.intentType;
            messageList = builder.messageList;
            session = builder.session;
        }


        public final class Builder {
            private FragmentActivity activity;
            private FileTransferChatMessage fileTransferChatMessage;
            private MultipartChatMessage multipartChatMessage;
            private int intentType;
            private List<ChatPostMessage> messageList;
            private Session session;

            public Builder() {
            }

            public Builder activity(FragmentActivity val) {
                activity = val;
                return this;
            }


            public Builder fileTransferChatMessage(FileTransferChatMessage val) {
                fileTransferChatMessage = val;
                return this;
            }

            public Builder multipartChatMessage(MultipartChatMessage val) {
                multipartChatMessage = val;
                return this;
            }

            public Builder intentType(int val) {
                intentType = val;
                return this;
            }

            public Builder messageList(List<ChatPostMessage> val) {
                messageList = val;
                return this;
            }

            public Builder session(Session val) {
                session = val;
                return this;
            }

            public OpenFileStatusViewAction build() {
                return new OpenFileStatusViewAction(this);
            }
        }
    }


}
