package com.foreveross.atwork.infrastructure.newmessage.post.notify;

import com.foreveross.atwork.infrastructure.newmessage.post.NotifyPostMessage;

import java.util.Map;

/**
 * 联系人操作通知, 例如星标的添加与修改
 * <p>
 * Created by dasunsy on 16/7/6.
 */
public class ContactNotifyMessage extends NotifyPostMessage {
    public static String FROM = "CONTACTS_HELPER";


    public static final String CONTACTS = "contacts";

    public Operation mOperation;
    public Contact mContact;
    public String mDeviceId;


    public static ContactNotifyMessage getContactNotifyMessageFromJson(Map<String, Object> jsonMap) {
        ContactNotifyMessage contactNotifyMessage = new ContactNotifyMessage();
        contactNotifyMessage.initPostTypeMessageValue(jsonMap);
        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);
        Map<String, Object> contactMap = (Map<String, Object>) bodyMap.get(CONTACTS);

        contactNotifyMessage.mOperation = Operation.fromStringValue((String) bodyMap.get(OPERATION));
        contactNotifyMessage.mContact = new Contact();

        contactNotifyMessage.mContact.mUserId = (String) contactMap.get("user_id");
        contactNotifyMessage.mContact.mDomainId = (String) contactMap.get("domain_id");
        contactNotifyMessage.mContact.mAvatar = (String) contactMap.get("avatar");
        contactNotifyMessage.mContact.mName = (String) contactMap.get("name");

        contactNotifyMessage.mDeviceId = (String) bodyMap.get(DEVICE_ID);

        return contactNotifyMessage;
    }

    public static class Contact {
        public String mUserId;
        public String mDomainId;
        public String mName;
        public String mAvatar;
    }


    public enum Operation {
        ADDED,
        REMOVED,
        UNKNOWN;


        public static Operation fromStringValue(String value) {
            if ("ADDED".equalsIgnoreCase(value)) {
                return ADDED;
            }

            if ("REMOVED".equalsIgnoreCase(value)) {
                return REMOVED;
            }


            return UNKNOWN;
        }

    }

}
