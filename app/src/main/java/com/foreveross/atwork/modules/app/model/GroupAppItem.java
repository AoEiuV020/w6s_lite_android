package com.foreveross.atwork.modules.app.model;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.modules.common.lightapp.LightNoticeMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lingen on 15/3/25.
 * Description:
 */
public class GroupAppItem {

    public static final int TYPE_TITLE = 0;
    public static final int TYPE_APP = 1;

    /**
     * 分类标题
     */
    public String title;

    /**
     * 排序
     */
    public int order;

    public int type = -1;

    public boolean custom = false;



    /**
     * app bundles
     */
    public List<AppBundles> mAppBundleList = new ArrayList<>();

    public Map<String, LightNoticeMapping> mLightNoticeMap = new HashMap<>();



    public GroupAppItem(String key, int type) {
        this.title = key;
        this.type = type;
    }

    public GroupAppItem(String key, List<AppBundles> appBundles, int type) {
        this(key, type);
        this.mAppBundleList = appBundles;
    }


    public void sortAppBundles() {
        Collections.sort(mAppBundleList, (o1, o2)-> AppBundles.sort(BaseApplicationLike.baseContext, o1, o2));
    }
}
