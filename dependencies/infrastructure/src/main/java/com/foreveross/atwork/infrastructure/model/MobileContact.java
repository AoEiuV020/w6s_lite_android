package com.foreveross.atwork.infrastructure.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 手机联系人
 */
public class MobileContact {

    public String mName;
    public List<String> mMobileList = new ArrayList<>();
    public List<String> mEmailList = new ArrayList<>();

    public MobileContact() {

    }

    public MobileContact(String name, String mobile) {
        this.mName = name;
        setMobile(mobile);
    }

    public void setMobile(String mobile) {
        List<String> singleList = new ArrayList<>();
        singleList.add(mobile);

        mMobileList = singleList;
    }

    public void setEmail(String email) {
        List<String> singleList = new ArrayList<>();
        singleList.add(email);

        mEmailList = singleList;
    }

    public MobileContact(String name, List<String> mobileList, List<String> emailList) {
        this.mName = name;
        this.mMobileList = mobileList;
        this.mEmailList = emailList;
    }
}
