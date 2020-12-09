package com.foreveross.atwork.infrastructure.newmessage.post;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class Participant {

    public static final String PARTICIPANT = "participant";
    public static final String CLIENT_ID = "client_id";
    public static final String DOMAIN_ID = "domain_id";


    @SerializedName("domain_id")
    public String domainId;

    @SerializedName("client_id")
    public String clientId;

    @SerializedName("type")
    public String type;


    @Nullable
    public static Participant parseInfo(Map<String, Object> bodyMap) {
        if(bodyMap.containsKey(PARTICIPANT)) {
            Participant participant = new Participant();
            Map<String, Object> participantMap = (Map<String, Object>) bodyMap.get(PARTICIPANT);
            participant.clientId = ChatPostMessage.getString(participantMap, CLIENT_ID);
            participant.domainId = ChatPostMessage.getString(participantMap, DOMAIN_ID);

            return participant;
        }

        return null;
    }
}
