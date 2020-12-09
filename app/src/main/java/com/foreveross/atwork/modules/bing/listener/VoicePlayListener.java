package com.foreveross.atwork.modules.bing.listener;

import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceMedia;

/**
 * Created by dasunsy on 2017/9/26.
 */

public interface VoicePlayListener {
    void start();

    void stop(VoiceMedia voiceMedia);
}
