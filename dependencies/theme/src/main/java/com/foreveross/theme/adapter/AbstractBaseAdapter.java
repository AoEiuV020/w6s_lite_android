package com.foreveross.theme.adapter;

import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.foreveross.theme.manager.SkinMaster;

/**
 * 创建时间：2016年09月18日 上午9:35
 * 创建人：laizihan
 * 类名：AbstractBaseAdapter
 * 用途：
 */
public abstract class AbstractBaseAdapter extends BaseAdapter {


    public void resetStatus(ViewGroup view) {
        SkinMaster.getInstance().changeTheme(view);
    }

}
