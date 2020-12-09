package com.foreveross.atwork.modules.aboutme.model;

import android.content.Context;

/**
 * Created by dasunsy on 2017/3/10.
 */

public class SystemItem extends MeFunctionItem{

    public String mTitle;

    public String mTextRightest;

    public int mIcon;



    public static SystemItem newInstance() {
        return new SystemItem();
    }

    public SystemItem setGroupEnd(boolean daggerUp) {
        mDaggerUp = daggerUp;
        return this;
    }

    public SystemItem setListItemType(ListItemType listItemType) {
        mListItemType = listItemType;
        return this;
    }

    public SystemItem setTitle(String title) {
        mTitle = title;
        return this;
    }

    public SystemItem setTextRightest(String textRightest) {
        mTextRightest = textRightest;
        return this;
    }

    public SystemItem setIcon(int icon) {
        mIcon = icon;
        return this;
    }

    @Override
    public String getTextRightest() {
        return mTextRightest;
    }

    @Override
    public String getTitle(Context context) {
        return mTitle;
    }

    @Override
    public int getIconRes() {
        return mIcon;
    }

    @Override
    public String getIconMedia() {
        return null;
    }
}
