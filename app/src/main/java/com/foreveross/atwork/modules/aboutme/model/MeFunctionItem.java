package com.foreveross.atwork.modules.aboutme.model;

import android.content.Context;

/**
 * Created by dasunsy on 2017/3/10.
 */

public abstract class MeFunctionItem {

    public ListItemType mListItemType;

    public boolean mDaggerUp = true;

    public boolean mNeedSwitch;

    public abstract String getTextRightest();

    public abstract String getTitle(Context context);

    public abstract int getIconRes();

    public abstract String getIconMedia();
}
