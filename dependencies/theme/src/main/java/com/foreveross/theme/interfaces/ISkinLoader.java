package com.foreveross.theme.interfaces;

import com.foreveross.theme.model.Theme;

/**
 * 创建时间：2016年09月12日 上午10:20
 * 创建人：laizihan
 * 类名：ISkinLoader
 * 用途：
 */
public interface ISkinLoader {
    void attach(ISkinUpdate observer);

    void detach(ISkinUpdate observer);

    void notifySkinChange(Theme theme);
}
