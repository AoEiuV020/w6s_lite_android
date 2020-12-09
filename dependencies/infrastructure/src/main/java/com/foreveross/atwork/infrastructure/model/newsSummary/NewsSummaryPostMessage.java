package com.foreveross.atwork.infrastructure.model.newsSummary;

import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewsSummaryPostMessage {
    @Expose
    @SerializedName("orgId")
    public String orgId;
    @Expose
    @SerializedName("chatId")
    public String chatId;
    @Expose
    @SerializedName("chatPostMessage")
    public ChatPostMessage chatPostMessage;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public ChatPostMessage getChatPostMessage() {
        return chatPostMessage;
    }

    public void setChatPostMessage(ChatPostMessage chatPostMessage) {
        this.chatPostMessage = chatPostMessage;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
}
