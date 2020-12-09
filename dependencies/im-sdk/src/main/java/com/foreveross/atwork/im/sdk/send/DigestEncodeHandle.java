package com.foreveross.atwork.im.sdk.send;

import com.foreveross.atwork.im.sdk.Digest;
import com.foreveross.atwork.im.sdk.protocol.Protocol;
import com.foreveross.atwork.im.sdk.socket.EncodeHandle;

/**
 * Created by lingen on 15/4/7.
 */
public class DigestEncodeHandle implements EncodeHandle {

    private Digest digest;

    public DigestEncodeHandle(Digest digest) {
        this.digest = digest;
    }

    @Override
    public Protocol encode(Protocol protocol) {
        byte[] digestVal = digest.digest(protocol.getMessage());
        protocol.setDigest(digestVal);
        return protocol;
    }
}
