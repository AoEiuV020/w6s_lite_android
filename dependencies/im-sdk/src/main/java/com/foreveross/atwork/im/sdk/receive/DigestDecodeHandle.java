package com.foreveross.atwork.im.sdk.receive;

import com.foreveross.atwork.im.sdk.protocol.Protocol;
import com.foreveross.atwork.im.sdk.socket.DecodeHandle;

/**
 * Created by lingen on 15/4/7.
 */
public class DigestDecodeHandle implements DecodeHandle {

    public boolean decodeMessage(Protocol protocol) throws Exception{

        byte val = protocol.getContent()[1];
        int digestVal = val;
        byte[] digest = new byte[digestVal];
        System.arraycopy(protocol.getContent(), 2, digest, 0, digestVal);
        protocol.setDigest(digest);
        return true;
    }
}
