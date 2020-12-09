package com.foreveross.atwork.modules.ad.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 15/12/14.
 */
public class StartPageItem implements Comparable {
    @SerializedName("media_id")
    public String mediaId;

    @SerializedName("link_url")
    public String linkUrl;

    @SerializedName("sort")
    public int sort;

    @SerializedName("path")
    public String path;

    @SerializedName("modify_time")
    public long modifyTime;


    @Override
    public int compareTo(Object another) {
        StartPageItem anotherItem = (StartPageItem) another;
        return this.sort - anotherItem.sort;
    }
}
