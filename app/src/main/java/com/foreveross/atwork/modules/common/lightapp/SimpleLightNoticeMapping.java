package com.foreveross.atwork.modules.common.lightapp;

import com.foreveross.atwork.modules.main.data.TabNoticeManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingen on 15/11/16.
 */
public class SimpleLightNoticeMapping implements LightNoticeMapping {

    private String mNoticeUrl;

    private List<String> mTabIds;

    private String mAppId;

    @Override
    public String getNoticeUrl() {
        return mNoticeUrl;
    }

    @Override
    public List<String> getTabIds() {
        return mTabIds;
    }

    @Override
    public String getAppId() {
        return mAppId;
    }

    public static LightNoticeMapping createInstance(final String noticeUrl, final String tabId, final String appId) {
        List<String> singleList = new ArrayList<>();
        singleList.add(tabId);

        return createInstance(noticeUrl, singleList, appId);
    }

    public static LightNoticeMapping createInstance(final String noticeUrl, final List<String> tabIds, final String appId) {
        SimpleLightNoticeMapping simpleLightNoticeModel = new SimpleLightNoticeMapping();
        simpleLightNoticeModel.mNoticeUrl = noticeUrl;



        simpleLightNoticeModel.mTabIds = tabIds;
        simpleLightNoticeModel.mAppId = appId;
        return simpleLightNoticeModel;
    }

    public static LightNoticeMapping createInstance(final String tabId, final String appId) {
        SimpleLightNoticeMapping simpleLightNoticeModel = new SimpleLightNoticeMapping();

        List<String> singleList = new ArrayList<>();
        singleList.add(tabId);

        simpleLightNoticeModel.mTabIds = singleList;

        simpleLightNoticeModel.mAppId = appId;
        TabNoticeManager.getInstance().registerLightNoticeMapping(simpleLightNoticeModel);
        return simpleLightNoticeModel;
    }
}
