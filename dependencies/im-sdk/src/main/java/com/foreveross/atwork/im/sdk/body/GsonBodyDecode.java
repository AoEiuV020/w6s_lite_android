package com.foreveross.atwork.im.sdk.body;

import com.foreveross.atwork.im.sdk.BodyDecode;
import com.foreveross.atwork.im.sdk.Client;
import com.foreveross.atwork.im.sdk.encrypt.EncryptHandler;
import com.foreveross.atwork.infrastructure.newmessage.HasBodyMessage;
import com.foreveross.atwork.infrastructure.newmessage.Message;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.MessageCovertUtil;

import java.nio.charset.Charset;

/**
 * Created by lingen on 15/4/7.
 */
public class GsonBodyDecode implements BodyDecode {

    public HasBodyMessage getMessage(int type, byte[] bytes) {

        //POST TYPE消息
        HasBodyMessage hasBodyMessage = null;

        if (Message.POST_TYPE == type) {
            if (AtworkConfig.OPEN_IM_CONTENT_ENCRYPTION) {
                LogUtil.e(Client.TAG, "aes128 解密处理 -> " );
                bytes = EncryptHandler.decrypt(bytes);
            }

            String json = new String(bytes, Charset.forName("UTF-8"));

            hasBodyMessage = MessageCovertUtil.covertJsonToMessage(json);
        }

        return hasBodyMessage;
    }


}


