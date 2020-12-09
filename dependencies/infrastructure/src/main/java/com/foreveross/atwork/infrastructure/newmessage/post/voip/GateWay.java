package com.foreveross.atwork.infrastructure.newmessage.post.voip;

import com.foreveross.atwork.infrastructure.model.voip.VoipType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.support.VoipSdkType;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

/**
 * Created by dasunsy on 16/8/3.
 */
public class GateWay {

    public static final String GATE_WAY_QUANSHIYUN = "quan_shi";
    public static final String GATE_WAY_AGORA = "agora";
    public static final String GATE_WAY_BIZCONF = "bizconf";
    public static final String GATE_WAY_ZOOM = "zoom";
//    public static final String GATE_WAY_BIZCONF = "agora";

    /**
     * 全时云会议 id
     * */
    @SerializedName("id")
    public String mId;

    /**
     * member里 gateway 的 userId 用于会议中参会者的唯一标记, 区别与user 的 userId(uuid), 它实际上是 int
     * */
    @SerializedName("user_id")
    public String mUid;

    @SerializedName("type")
    public VoipSdkType mType;

    @SerializedName("voip_type")
    public VoipType mVoipType;

    public static GateWay getGateWay(Object bodyMap) {
        LinkedTreeMap<String, Object> linkedTreeMap = (LinkedTreeMap<String, Object>) bodyMap;

        GateWay gateWay = new GateWay();
        //gateWay  赋值
        gateWay.mId = (String) linkedTreeMap.get("id");
        gateWay.mUid = (String) linkedTreeMap.get("user_id");
        String type = ChatPostMessage.getString(linkedTreeMap, "type");
        gateWay.mType = VoipSdkType.parse(type);
        Object voipType = linkedTreeMap.get("voip_type");
        if(null != voipType && !StringUtils.isEmpty((String) voipType)) {
            gateWay.mVoipType = VoipType.valueOf((String) voipType);

        }

        return gateWay;
    }
}
