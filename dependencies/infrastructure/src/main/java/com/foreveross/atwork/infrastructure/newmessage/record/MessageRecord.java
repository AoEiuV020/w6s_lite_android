package com.foreveross.atwork.infrastructure.newmessage.record;

import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.post.ack.AckPostMessage;

public class MessageRecord {
    public String msgId;

    public long msgTime;

    public BodyType bodyType;


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MessageRecord{");
        sb.append("msgId='").append(msgId).append('\'');
        sb.append(", msgTime=").append(msgTime);
        sb.append(", bodyType=").append(bodyType);
        sb.append('}');
        return sb.toString();
    }

    public static boolean isLegal(PostTypeMessage postTypeMessage) {
        if(postTypeMessage instanceof AckPostMessage) {
            AckPostMessage ackPostMessage = (AckPostMessage) postTypeMessage;

            if(AckPostMessage.AckType.WRITE == ackPostMessage.type) {
                return true;
            }
        }


        return postTypeMessage.retained;
    }

    public static MessageRecord transfer(PostTypeMessage postTypeMessage) {
        if(postTypeMessage instanceof AckPostMessage) {
            AckPostMessage ackPostMessage = (AckPostMessage) postTypeMessage;

            if(AckPostMessage.AckType.WRITE == ackPostMessage.type) {
                MessageRecord messageRecord = new MessageRecord();
                messageRecord.msgId = ackPostMessage.ackIds.get(0);
                messageRecord.msgTime = ackPostMessage.ackTime;
                return messageRecord;

            }
        }


        MessageRecord messageRecord = new MessageRecord();
        messageRecord.msgId = postTypeMessage.deliveryId;
        messageRecord.msgTime = postTypeMessage.deliveryTime;
//        messageRecord.bodyType = postTypeMessage.mBodyType;

        return messageRecord;
    }
}
