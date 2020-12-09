package com.foreveross.atwork.infrastructure.newmessage.post.gather;

import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.utils.EnumLookupUtil;

import java.util.Map;

public class CmdGatherMessage extends PostTypeMessage {

    private static final String DATA_URL = "data_url";

    private static final String DATA_TYPE = "data_type";

    public GatherDataType mGatherDataType;
    public String mDataUrl;

    public static CmdGatherMessage getCmdGatherMessage(Map<String, Object> jsonMap) {
        CmdGatherMessage cmdGatherMessage = new CmdGatherMessage();
        cmdGatherMessage.initPostTypeMessageValue(jsonMap);
        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);

        cmdGatherMessage.mGatherDataType = EnumLookupUtil.lookup(GatherDataType.class, ChatPostMessage.getString(bodyMap, DATA_TYPE));
        cmdGatherMessage.mDataUrl = ChatPostMessage.getString(bodyMap, DATA_URL);

        return cmdGatherMessage;
    }

    @Override
    public Map<String, Object> getChatBody() {
        return null;
    }
}
