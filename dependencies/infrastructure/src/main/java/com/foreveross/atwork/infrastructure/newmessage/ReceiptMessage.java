package com.foreveross.atwork.infrastructure.newmessage;

import java.io.Serializable;

/**
 * Created by lingen on 15/5/14.
 * Description:
 * 已读回执消息
 */
public class ReceiptMessage implements Serializable{

    //主键，自增
    public String identifier;

    //消息ID
    public String msgId;

    //谁发来的
    public String receiveFrom;

    //属于哪个会话的已读回执
    public String sessionIdentifier;

    //时间
    public long timestamp;

    public static final String MESSAGE_TABLE_PRE = "receipt_";

    public static String getMessageTableName(String identifier) {
        return "'" + MESSAGE_TABLE_PRE + identifier + "'";
    }

}
