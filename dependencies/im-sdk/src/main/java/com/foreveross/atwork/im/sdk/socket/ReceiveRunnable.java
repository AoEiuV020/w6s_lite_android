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
 * ??????????????????
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
        LogUtil.e(Client.TAG, "????????????, ???????????? -> " + new String(content));

        Protocol protocol = Protocol.newReceiveProtocol(content);
        try {
            for (DecodeHandle receiveHandle : receiveHandleList) {
                receiveHandle.decodeMessage(protocol);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                String str = new String(content, "UTF-8");
                LogUtil.e(Client.TAG, "??????????????????:" + str);
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            //????????????
            error();
            return;
        }

        LogUtil.e(Client.TAG, "????????????, ????????????  type ->" + protocol.getType());
        LogUtil.e_endTag(Client.TAG);

        int protocolType = protocol.getType();

//        checkProtocolLegal(protocolType);

        //????????????
        if (protocolType == Message.CONNECT_ACK_TYPE) {
            confirmAuthorization((ConnectAckTypeMessage) protocol.getMessage());
        }

        //????????????
        if (protocolType == Message.POST_TYPE) {
            received(protocol.getMessage());
        }

        //????????????
        if (protocolType == Message.PONG_TYPE) {
            pong((PongMessage) protocol.getMessage());
        }

        //??????????????????
        if (protocolType == Message.CONNECT_RST_TYPE) {
            error();
        }

        //??????????????????
        if (protocolType == Message.DEVICE_ONLINE) {
            DeviceOnlineMessage  message = ((DeviceOnlineMessage)protocol.getMessage());
            message.showMessage();
            if (receiveListener != null) {
                receiveListener.receiveDeviceOnlineMessage(message);
            }
        }

        //??????????????????
        if (protocolType == Message.DEVICE_OUTLINE) {
            DeviceOutlineMessage message = ((DeviceOutlineMessage)protocol.getMessage());
            message.showMessage();
            if (receiveListener != null) {
                receiveListener.receiveDeviceOnlineMessage(message);
            }
        }

        //??????????????????
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
     * ???Socket????????????????????????
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

            //ack??????
            if (BodyType.Ack.equals(postTypeMessage.mBodyType)) {
                AckPostMessage ackPostMessage = (AckPostMessage) message;
                receiveListener.ack(ackPostMessage);
                return;
            }

            if(BodyType.Gather.equals(postTypeMessage.mBodyType)) {
                receiveListener.receiveCmdGatherMessage((CmdGatherMessage) message);
            }
            //CHAT??????
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

            //CMD??????
            if (BodyType.Cmd.equals(postTypeMessage.mBodyType)) {
                receiveListener.receiveCmdMessage((CmdPostMessage) message);
                return;
            }

            if(BodyType.System.equals(postTypeMessage.mBodyType)) {
                receiveListener.receiveSystemMessage((SystemPostMessage) message);
                return;
            }

            //????????????
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
     * ?????????????????????????????????
     *
     * @param pongMessage
     */
    private void pong(PongMessage pongMessage) {
        if (receiveListener != null) {
            receiveListener.pong(pongMessage);
        }
    }
}

