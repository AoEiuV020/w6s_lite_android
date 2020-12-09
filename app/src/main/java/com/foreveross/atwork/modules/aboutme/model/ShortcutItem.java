package com.foreveross.atwork.modules.aboutme.model;

import android.content.Context;

import com.foreveross.atwork.infrastructure.model.app.Shortcut;

/**
 * Created by dasunsy on 2017/3/10.
 */

public class ShortcutItem extends MeFunctionItem {

    public Shortcut mShortcut;

    public ShortcutItem() {
        mListItemType = ListItemType.SHORTCUT;
    }

    @Override
    public String getTextRightest() {
        return null;
    }

    @Override
    public String getTitle(Context context) {
        return mShortcut.getTitleI18n(context);
    }

    @Override
    public int getIconRes() {
        return -1;
    }

    @Override
    public String getIconMedia() {
        return mShortcut.mIcon;
    }
}
