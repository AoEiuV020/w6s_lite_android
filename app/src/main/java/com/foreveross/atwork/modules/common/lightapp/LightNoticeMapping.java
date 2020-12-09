package com.foreveross.atwork.modules.common.lightapp;

import java.util.List;

/**
 * Created by lingen on 15/11/16.
 * 轻应用红点机制的模块
 */
public interface LightNoticeMapping {

    /**
     * 返回轻应用对应的获取红点的URL
     * @return
     */
    String getNoticeUrl();


    /**
     * 返回APP对应的Tab Id
     * @return
     */
    List<String> getTabIds();


    /**
     * 返回APP_ID
     * @return
     */
    String getAppId();






}

