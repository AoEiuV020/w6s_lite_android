package com.foreveross.atwork.infrastructure.newmessage.post.notify;

import androidx.annotation.NonNull;

import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.newmessage.Participator;
import com.foreveross.atwork.infrastructure.newmessage.post.NotifyPostMessage;

import java.util.Map;

/**
 * Created by dasunsy on 16/5/20.
 */
public class OrgNotifyMessage extends NotifyPostMessage {
    public static String FROM = "ORGANIZATION_HELPER";
    public String mDeviceId;
    public Operation mOperation;
    public String mOrgName;
    public String mOrgCode;
    public String mLogo;
    public String mDomainId;

    public Participator mRecipient; //接收者
    public Participator mAddresser; //发送者
    public Participator mAuditor; //篡改者


    public static OrgNotifyMessage getOrgNotifyMessageFromJson(Map<String, Object> jsonMap) {
        OrgNotifyMessage orgNotifyMessage = new OrgNotifyMessage();
        orgNotifyMessage.initPostTypeMessageValue(jsonMap);
        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);

        if (null != bodyMap.get("recipient")) {
            orgNotifyMessage.mRecipient = Participator.getParticipator(bodyMap.get("recipient"));

        }

        if (null != bodyMap.get("addresser")) {
            orgNotifyMessage.mAddresser = Participator.getParticipator(bodyMap.get("addresser"));

        }

        if (null != bodyMap.get("auditor")) {
            orgNotifyMessage.mAuditor = Participator.getParticipator(bodyMap.get("auditor"));

        }

        orgNotifyMessage.mOperation = Operation.fromStringValue((String) bodyMap.get(OPERATION));
        orgNotifyMessage.mOrgName = (String) bodyMap.get("org_name");
        orgNotifyMessage.mOrgCode = (String) bodyMap.get("org_code");
        orgNotifyMessage.mLogo = (String) bodyMap.get("org_logo");
        orgNotifyMessage.mDeviceId = (String) bodyMap.get(DEVICE_ID);

        return orgNotifyMessage;
    }

    /**
     * 获取基本的 org(只包含基本信息)
     *
     * @return org
     */
    @NonNull
    public Organization getBasicOrg() {
        Organization org = new Organization();
        org.mOrgCode = mOrgCode;
        org.mName = mOrgName;
        org.mLogo = mLogo;

        return org;
    }


    public enum Operation {
        APPLYING,
        APPROVED,
        REJECTED,
        UNKNOWN;


        public static Operation fromStringValue(String value) {
            if ("APPLYING".equalsIgnoreCase(value)) {
                return APPLYING;
            }
            if ("APPROVED".equalsIgnoreCase(value)) {
                return APPROVED;
            }
            if ("REJECTED".equalsIgnoreCase(value)) {
                return REJECTED;
            }

            return UNKNOWN;
        }

    }

}
