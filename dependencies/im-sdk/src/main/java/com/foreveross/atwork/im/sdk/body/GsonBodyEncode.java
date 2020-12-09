package com.foreveross.atwork.im.sdk.body;

import com.foreveross.atwork.im.sdk.BodyEncode;
import com.foreveross.atwork.im.sdk.Client;
import com.foreveross.atwork.im.sdk.encrypt.EncryptHandler;
import com.foreveross.atwork.im.sdk.send.BodyEncodeHandle;
import com.foreveross.atwork.infrastructure.newmessage.HasBodyMessage;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.MessageCovertUtil;

/**
 * Created by lingen on 15/4/7.
 */
public class GsonBodyEncode implements BodyEncode {

    public byte[] getBody(HasBodyMessage message) {
        String body = MessageCovertUtil.covertMessageToJson(message);
        LogUtil.e_startTag(Client.TAG);
        LogUtil.e(Client.TAG, "发送消息 -> " + LogUtil.jsonFormat(body));
        LogUtil.e_endTag(Client.TAG);

        if(message.encryptHandle()) {

            byte[] encrypt = EncryptHandler.encrypt(body.getBytes());
            LogUtil.e(Client.TAG, "aes128 加密处理 -> " + new String(encrypt));

            return encrypt;
        }
        return body.getBytes();
    }



}
