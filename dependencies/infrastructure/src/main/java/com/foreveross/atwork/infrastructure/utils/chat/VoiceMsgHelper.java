package com.foreveross.atwork.infrastructure.utils.chat;

import android.content.Context;

import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage;
import com.foreveross.atwork.infrastructure.utils.file.FileStreamHelper;

/**
 * Created by dasunsy on 2017/7/25.
 */

public class VoiceMsgHelper {
    public static byte[] readAudioContent(Context context, String audioId) {

        String path = VoiceChatMessage.getAudioPath(context, audioId);
        return FileStreamHelper.readFile(path);
    }

}
