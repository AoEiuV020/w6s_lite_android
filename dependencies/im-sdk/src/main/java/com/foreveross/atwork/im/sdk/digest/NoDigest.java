package com.foreveross.atwork.im.sdk.digest;

import com.foreveross.atwork.im.sdk.Digest;
import com.foreveross.atwork.infrastructure.newmessage.Message;

/**
 * Created by lingen on 15/4/7.
 * 没有签名摘要算法
 */
public class NoDigest implements Digest {

    public byte[] digest(Message message) {
        return new byte[0];
    }
}
