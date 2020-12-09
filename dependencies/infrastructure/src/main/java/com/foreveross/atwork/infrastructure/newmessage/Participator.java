package com.foreveross.atwork.infrastructure.newmessage;

import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.model.user.UserHandleBasic;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

/**
 * Created by dasunsy on 16/5/20.
 */
public class Participator extends UserHandleBasic{
    @SerializedName("name")
    public String mName;

    @SerializedName("avatar")
    public String mAvatar;


    public static Participator getParticipator(Object bodyMap) {

        Participator participator = new Participator();
        try {
            if (bodyMap instanceof  String) {
                String value = String.valueOf(bodyMap);
                if (TextUtils.isEmpty(value)) {
                    return participator;
                }

            }
            LinkedTreeMap<String, String> linkedTreeMap = (LinkedTreeMap<String, String>) bodyMap;



            participator.mUserId = linkedTreeMap.get("user_id");
            participator.mDomainId = linkedTreeMap.get("domain_id");
            participator.mName = linkedTreeMap.get("name");
            participator.mAvatar = linkedTreeMap.get("avatar");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return participator;
    }
}
