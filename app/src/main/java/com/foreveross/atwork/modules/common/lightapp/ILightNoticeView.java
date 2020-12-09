package com.foreveross.atwork.modules.common.lightapp;

import com.foreveross.atwork.api.sdk.app.model.LightNoticeData;

/**
 * Created by lingen on 15/11/16.
 */
public interface ILightNoticeView {


    /**
     * 刷新当前VIEW的红点
     * @param lightNoticeJson
     */
    void refreshLightNotice(LightNoticeData lightNoticeJson);


}
