package com.foreveross.atwork.api.sdk.contact;

import android.content.Context;

import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.contact.model.ContactResponseJSON;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.infrastructure.model.user.Contact;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingen on 15/4/9.
 * Description:
 * 通讯录同步网络服务
 */
public class ContactAsyncNetService {
    public static final Object sLock = new Object();
    private static ContactAsyncNetService sInstance;
    private UrlConstantManager mUrlConstantManager = UrlConstantManager.getInstance();

    public static final String TOP = "-1";
    public static final int TOP_LEVEL = 0;
    public static final int MAX_QUERY_NUM = 100;


    //查询组织结构树===============================================

    private ContactAsyncNetService() {
    }

    public static ContactAsyncNetService getInstance() {
        synchronized (sLock) {
            if(null == sInstance) {
                sInstance = new ContactAsyncNetService();
            }

            return sInstance;
        }
    }


    /**
     * 批量查询人员信息
     *
     * @param contactUsernames
     */
    public List<Contact> batchQueryContacts(Context context, final List<String> contactUsernames) {
        //批量查询，按照每10个进行查询，以防止URL过长
        List<Contact> contacts = new ArrayList<>();
        if (contactUsernames.size() == 0) {
            return contacts;
        }
        int i = 0;
        StringBuffer stringBuffer = new StringBuffer();
        for (String contactUsername : contactUsernames) {
            stringBuffer.append(contactUsername + ",");
            if (i++ > MAX_QUERY_NUM) {
                //计算一次
                contacts.addAll(batchQuery10Contacts(context, stringBuffer.toString()));
                i = 0;
                stringBuffer = new StringBuffer();
            }
        }

        contacts.addAll(batchQuery10Contacts(context, stringBuffer.toString()));

        return contacts;
    }

    private List<Contact> batchQuery10Contacts(Context context, final String usernames) {
        List<Contact> contacts = new ArrayList<>();
        final String batchQueryUrl = String.format(mUrlConstantManager.getBatchQueryContacts(), usernames, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(batchQueryUrl);
        if (httpResult.isNetSuccess()) {
            ContactResponseJSON contactResponseJSON = JsonUtil.fromJson(httpResult.result, ContactResponseJSON.class);
            if (contactResponseJSON != null && contactResponseJSON.status == 0) {
                contacts.addAll(contactResponseJSON.toContacts(context));
            }
        }
        return contacts;
    }




}
