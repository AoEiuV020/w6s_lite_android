package com.foreveross.atwork.modules.chat.util;

import android.content.Context;
import android.os.AsyncTask;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceMedia;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk;
import com.foreveross.atwork.infrastructure.utils.file.FileStreamHelper;

/**
 * Created by dasunsy on 2017/6/9.
 */

public class AudioFileHelper {

    public static void getVoiceFileOriginalPath(Context context, VoiceChatMessage voiceChatMessage, OnGetVoiceFilePathListener listener) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return getVoiceFileOriginalPathSync(context, voiceChatMessage);
            }

            @Override
            protected void onPostExecute(String path) {
                listener.onResult(path);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }


    public static String getVoiceFileOriginalPathSync(Context context, VoiceMedia voiceMedia) {
        String path = VoiceChatMessage.getAudioPath(context, voiceMedia.getKeyId());

        checkAudioFileExist(context, voiceMedia, path);

        path = EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(path, false);

        return path;
    }

    public static void checkAudioFileExist(Context context, VoiceMedia voiceMedia, String path) {
        if (!FileUtil.isExist(path)) {
            MediaCenterNetManager mediaCenterNetManager = new MediaCenterNetManager(BaseApplicationLike.baseContext);
            String filePath = VoiceChatMessage.getAudioPath(context, voiceMedia.getKeyId());
            mediaCenterNetManager.syncDownloadFile(voiceMedia.getMediaId(), voiceMedia.getKeyId(), filePath);

            if(voiceMedia instanceof VoiceChatMessage) {
                VoiceChatMessage voiceChatMessage = (VoiceChatMessage) voiceMedia;
                voiceChatMessage.content = FileStreamHelper.readFile(filePath);

            }
        }
    }


    public interface OnGetVoiceFilePathListener {
        void onResult(String path);
    }

}
