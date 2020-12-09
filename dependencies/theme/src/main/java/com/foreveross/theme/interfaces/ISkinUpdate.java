package com.foreveross.theme.interfaces;

import com.foreveross.theme.model.Theme;

/**
 * 创建时间：2016年09月12日 上午10:22
 * 创建人：laizihan
 * 类名：ISkinUpdate
 * 用途：
 */
public interface ISkinUpdate {

    /**
     * 换肤更换的通知
     * */
    void onThemeUpdate(Theme theme);


    /**
     * 更换皮肤
     * */
    void changeTheme();


}

