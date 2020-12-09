package com.foreveross.atwork.infrastructure.model.discussion;

import com.foreveross.atwork.infrastructure.newmessage.ReceiptMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingen on 15/5/14.
 * Description:
 * 群的已读未读信息
 */
public class DiscussionReadUnRead implements Serializable {

    /**
     * 已读人员信息列表
     */
    public List<ReceiptMessage> readUsers = new ArrayList<>();
    /**
     * 未读人员信息列表
     */
    public List<String> unReadUsers = new ArrayList<>();
    private List<String> reads = new ArrayList<>();

    public boolean isRead(String user) {
        return reads.contains(user);
    }

    public void addReadUser(ReceiptMessage user) {
        //重复用户不添加
        if (reads.contains(user.receiveFrom)) {
            return;
        }
        readUsers.add(user);
        reads.add(user.receiveFrom);
    }


    public void addUnReadUser(String user) {
        if (unReadUsers == null) {
            unReadUsers = new ArrayList<>();
        }
        unReadUsers.add(user);
    }

}
