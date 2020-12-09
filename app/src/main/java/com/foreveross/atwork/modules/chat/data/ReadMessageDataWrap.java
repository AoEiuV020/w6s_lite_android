package com.foreveross.atwork.modules.chat.data;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.newmessage.ReadStatus;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ack.AckPostMessage;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.modules.chat.dao.ChatDaoService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 正在发送"已读"ack的封装类，用于处理发送"已读"ack消息后接收的回执
 */
public class ReadMessageDataWrap {

    private static ReadMessageDataWrap mReadMessageDataWrap = new ReadMessageDataWrap();
    //该处的 key 是 ack 的 DELIVER_ID
    private Map<String, List<ChatPostMessage>> mAckReadMessageMap = new LinkedHashMap<>();


    private ReadMessageDataWrap() {

    }

    public static ReadMessageDataWrap getInstance() {
        return mReadMessageDataWrap;
    }


    public void putReadSendingMessage(String ackDeliveryId, List<ChatPostMessage> chatPostMessages) {
        List<ChatPostMessage> unReadMessageList = new ArrayList<>();
        unReadMessageList.addAll(chatPostMessages);
        mAckReadMessageMap.put(ackDeliveryId, unReadMessageList);
    }

    /**
     * 标志某条消息为发送已读成功
     *
     * @param ackPostMessage
     */
    public void dealMessageReadReceived(AckPostMessage ackPostMessage) {
        for(String ackId : ackPostMessage.ackIds){

            List<ChatPostMessage> chatPostMessageList = mAckReadMessageMap.get(ackId);

            if(!ListUtil.isEmpty(chatPostMessageList)){
                for(ChatPostMessage message : chatPostMessageList){
                    message.read = ReadStatus.AbsolutelyRead;
                    if (ParticipantType.Bing != message.mToType) {
                        ChatDaoService.getInstance().insertOrUpdateMessage(BaseApplicationLike.baseContext, message);
                    }
                }

                mAckReadMessageMap.remove(ackPostMessage.deliveryId);
            }
        }


    }

}
