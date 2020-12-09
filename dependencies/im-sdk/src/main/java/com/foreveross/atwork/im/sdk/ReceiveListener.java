package com.foreveross.atwork.im.sdk;

import com.foreveross.atwork.infrastructure.newmessage.ConnectAckTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.PongMessage;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.UserTypingMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.DeviceInfoMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ack.AckPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.CmdPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.EventPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.NotifyPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.SystemPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.VoipPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.gather.CmdGatherMessage;

/**
 * Created by lingen on 15/4/7.
 */
public interface ReceiveListener {


    /**
     * 消息处理之前
     * */
    void beforeHandle(PostTypeMessage postTypeMessage);

    /**
     * 控制收集数据的消息
     * @param gatherMessage
     * */
    void receiveCmdGatherMessage(CmdGatherMessage gatherMessage);

    /**
     * 接收到的消息，对外公开的接口
     *
     * @param message
     */
    void receiveChatMessage(ChatPostMessage message);

    /**
     * 收到通知类消息
     * @param notifyPostMessage
     */
    void receiveNoticeMessage(NotifyPostMessage notifyPostMessage);

    /**
     * 收到命令消息
     * @param cmdPostMessage
     */
    void receiveCmdMessage(CmdPostMessage cmdPostMessage);

    /**
     * 收到系统消息
     * @param systemPostMessage
     */
    void receiveSystemMessage(SystemPostMessage systemPostMessage);

    /**
     * 收到通知类消息
     * @param eventPostMessage
     */
    void receiveEventMessage(EventPostMessage eventPostMessage);

    /**
     * 收到 voip 消息
     * */
    void receiveVoipMessage(VoipPostMessage voipPostMessage);


    /**
     * 收到一个消息回执
     *
     * @param ackMessage
     */
    void ack(AckPostMessage ackMessage);

    /**
     * 与SOCKET服务器的授权确认
     */
    void confirmAuthorization(ConnectAckTypeMessage connectAckTypeMessage);

    /**
     * 收到一条表明错误的信息
     */
    void receiveError(boolean reSetConnectStatus, String error);

    /**
     * 收到设备在线消息
     * @param message
     */
    void receiveDeviceOnlineMessage(DeviceInfoMessage message);

    /**
     * 收到"用户正在输入"的通知消息
     *
     */
    void receiveUserTyping(UserTypingMessage typingMessage);

    /**
     * 用户主动退出
     */
    void exit();

    /**
     * 心跳回执
     */
    void pong(PongMessage pongMessage);
}
