package com.foreveross.atwork.infrastructure.newmessage.post;

import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.ReadStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lingen on 15/5/13.
 * Description:
 */
public class NotifyPostMessage extends PostTypeMessage implements Comparable {

    public static final String APPLYING = "APPLYING";
    public static final String APPROVED = "APPROVED";
    public static final String REJECTED = "REJECTED";
    public static final String REMOVED = "REMOVED";

    public static final String OPERATION = "operation";
    public static final String OPERATOR = "operator";
    public static final String DEVICE_ID = "device_id";

    /**
     * 消息是否已读
     */
    public ReadStatus read = ReadStatus.Unread;


    /**
     * 通知类消息，只能接收，没有发送
     *
     * @return
     */
    @Override
    public Map<String, Object> getChatBody() {
//        throw new UnsupportedOperationException();
        return new HashMap<>();
    }

    @Override
    public int compareTo(Object another) {
        NotifyPostMessage notifyPostMessage = (NotifyPostMessage) another;
        if (notifyPostMessage != null) {
            return deliveryId.compareTo(notifyPostMessage.deliveryId);
        } else {
            return 0;
        }
    }


    public static abstract class Builder<T extends Builder> extends PostTypeMessage.Builder<T>{
        private ReadStatus mRead;


        public T setRead(ReadStatus read) {
            this.mRead = read;
            return (T) this;
        }


        public void assemble(NotifyPostMessage notifyPostMessage) {
            super.assemble(notifyPostMessage);
            notifyPostMessage.read = mRead;
        }
    }
}
