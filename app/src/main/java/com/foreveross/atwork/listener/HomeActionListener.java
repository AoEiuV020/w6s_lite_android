package com.foreveross.atwork.listener;

/**
 * Created by dasunsy on 2016/12/5.
 */

public interface HomeActionListener {
    /**
     * 从桌面返回到 app 的标准入口
     * */
    void onBackFromHome();

    /**
     * 按下 home 键
     * */
    void onHome();


    /**
     * 最近任务菜单
     * */
    void onRecentApps();
}
