package com.foreveross.atwork.im.sdk.socket;

import com.foreveross.atwork.im.sdk.Client;
import com.foreveross.atwork.im.sdk.ReceiveListener;
import com.foreveross.atwork.im.sdk.body.GsonBodyDecode;
import com.foreveross.atwork.im.sdk.protocol.Protocol;
import com.foreveross.atwork.im.sdk.receive.BodyDecodeHandle;
import com.foreveross.atwork.im.sdk.receive.DigestDecodeHandle;
import com.foreveross.atwork.im.sdk.receive.QosDecodeHandle;
import com.foreveross.atwork.im.sdk.receive.TypeDecodeHandle;
import com.foreveross.atwork.infrastructure.newmessage.ConnectAckTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.Message;
import com.foreveross.atwork.infrastructure.newmessage.PongMessage;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.UserTypingMessage;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType;
import com.foreveross.atwork.infrastructure.newmessage.post.DeviceOnlineMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.DeviceOutlineMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ack.AckPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.CmdPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.EventPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.NotifyPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.SystemPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.VoipPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.UnknownChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.gather.CmdGatherMessage;
import com.foreveross.atwork.infrastructure.utils.LogUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingen on 15/4/7.
 * 接收消息处理
 */
public class ReceiveRunnable implements Runnable {

    private List<DecodeHandle> receiveHandleList = new ArrayList<>();

    private ReceiveListener receiveListener;


    private byte[] content;

    public ReceiveRunnable(byte[] content, ReceiveListener receiveListener) {
        this.content = content;
        initReceiveHandle();
        this.receiveListener = receiveListener;
    }

    private void initReceiveHandle() {
        receiveHandleList.add(new TypeDecodeHandle());
        receiveHandleList.add(new QosDecodeHandle());
        receiveHandleList.add(new DigestDecodeHandle());
        receiveHandleList.add(new BodyDecodeHandle(new GsonBodyDecode()));
    }

    @Override
    public void run() {
        LogUtil.e_startTag(Client.TAG);
        LogUtil.e(Client.TAG, "收到消息, 开始解析 -> " + new String(content));

        Protocol protocol = Protocol.newReceiveProtocol(content);
        try {
            for (DecodeHandle receiveHandle : receiveHandleList) {
                receiveHandle.decodeMessage(protocol);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                String str = new String(content, "UTF-8");
                LogUtil.e(Client.TAG, "解析消息出错:" + str);
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            //解析出错
            error();
            return;
        }

        LogUtil.e(Client.TAG, "收到消息, 结束解析  type ->" + protocol.getType());
        LogUtil.e_endTag(Client.TAG);

        int protocolType = protocol.getType();

//        checkProtocolLegal(protocolType);

        //连接确认
        if (protocolType == Message.CONNECT_ACK_TYPE) {
            confirmAuthorization((ConnectAckTypeMessage) protocol.getMessage());
        }

        //聊天消息
        if (protocolType == Message.POST_TYPE) {
            received(protocol.getMessage());
        }

        //心跳响应
        if (protocolType == Message.PONG_TYPE) {
            pong((PongMessage) protocol.getMessage());
        }

        //收到错误响应
        if (protocolType == Message.CONNECT_RST_TYPE) {
            error();
        }

        //设备上线通知
        if (protocolType == Message.DEVICE_ONLINE) {
            DeviceOnlineMessage  message = ((DeviceOnlineMessage)protocol.getMessage());
            message.showMessage();
            if (receiveListener != null) {
                receiveListener.receiveDeviceOnlineMessage(message);
            }
        }

        //设备下线通知
        if (protocolType == Message.DEVICE_OUTLINE) {
            DeviceOutlineMessage message = ((DeviceOutlineMessage)protocol.getMessage());
            message.showMessage();
            if (receiveListener != null) {
                receiveListener.receiveDeviceOnlineMessage(message);
            }
        }

        //用户正在输入
        if(protocolType == Message.USER_TYPING) {
            UserTypingMessage typingMessage = (UserTypingMessage) protocol.getMessage();

            if (receiveListener != null) {
                receiveListener.receiveUserTyping(typingMessage);
            }
        }


    }


    private void error() {
        if (receiveListener != null) {
            receiveListener.receiveError(false, null);
        }
    }

    /**
     * 与Socket服务器的连接确认
     */
    private void confirmAuthorization(ConnectAckTypeMessage connectAckTypeMessage) {
        if (receiveListener != null) {
            receiveListener.confirmAuthorization(connectAckTypeMessage);
        }
    }


    private void received(Message message) {
        if (message == null) {
            return;
        }

        if (receiveListener != null) {
            PostTypeMessage postTypeMessage = (PostTypeMessage) message;

            receiveListener.beforeHandle(postTypeMessage);

            //ack消息
            if (BodyType.Ack.equals(postTypeMessage.mBodyType)) {
                AckPostMessage ackPostMessage = (AckPostMessage) message;
                receiveListener.ack(ackPostMessage);
                return;
            }

            if(BodyType.Gather.equals(postTypeMessage.mBodyType)) {
                receiveListener.receiveCmdGatherMessage((CmdGatherMessage) message);
            }
            //CHAT消息
            if (BodyType.Text.equals(postTypeMessage.mBodyType)    ||
                    BodyType.File.equals(postTypeMessage.mBodyType)     ||
                    BodyType.Image.equals(postTypeMessage.mBodyType)    ||
                    BodyType.Voice.equals(postTypeMessage.mBodyType)    ||
                    BodyType.Video.equals(postTypeMessage.mBodyType)    ||
                    BodyType.Article.equals(postTypeMessage.mBodyType)  ||
                    BodyType.Multipart.equals(postTypeMessage.mBodyType)  ||
                    BodyType.Share.equals(postTypeMessage.mBodyType) ||
                    BodyType.Template.equals(postTypeMessage.mBodyType) ||
                    BodyType.Sticker.equals(postTypeMessage.mBodyType) ||
                    BodyType.Face.equals(postTypeMessage.mBodyType) ||
                    BodyType.Quoted.equals(postTypeMessage.mBodyType) ||
                    BodyType.AnnoFile.equals(postTypeMessage.mBodyType) ||
                    BodyType.AnnoImage.equals(postTypeMessage.mBodyType) ||
                    BodyType.UnKnown.equals(postTypeMessage.mBodyType)) {
                postTypeMessage.chatSendType = ChatSendType.RECEIVER;

                receiveListener.receiveChatMessage((ChatPostMessage) postTypeMessage);
                return;
            }

            if(postTypeMessage instanceof UnknownChatMessage) {
                return;
            }

            //CMD消息
            if (BodyType.Cmd.equals(postTypeMessage.mBodyType)) {
                receiveListener.receiveCmdMessage((CmdPostMessage) message);
                return;
            }

            if(BodyType.System.equals(postTypeMessage.mBodyType)) {
                receiveListener.receiveSystemMessage((SystemPostMessage) message);
                return;
            }

            //通知消息
            if (BodyType.Notice.equals(postTypeMessage.mBodyType)) {
                receiveListener.receiveNoticeMessage((NotifyPostMessage) postTypeMessage);
                return;
            }

            if(BodyType.Event.equals(postTypeMessage.mBodyType)) {
                receiveListener.receiveEventMessage((EventPostMessage) postTypeMessage);
                return;
            }

            if(BodyType.Voip.equals(postTypeMessage.mBodyType)) {
                receiveListener.receiveVoipMessage((VoipPostMessage) postTypeMessage);
                return;
            }

        }
    }




    /**
     * 接收到服务器的心跳响应
     *
     * @param pongMessage
     */
    private void pong(PongMessage pongMessage) {
        if (receiveListener != null) {
            receiveListener.pong(pongMessage);
        }
    }
}

