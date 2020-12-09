package com.foreveross.atwork.manager.listener;

/**
 * Created by dasunsy on 2017/4/12.
 */

public interface DomainSettingUpdater {
    /**
     * 检查更新域设置
     * @return true 表示做了处理, 反之 false
     * */
    boolean checkDomainSettingUpdate();

    /**
     * 注册检查更新的广播
     * */
    void registerUpdateReceiver();
}
