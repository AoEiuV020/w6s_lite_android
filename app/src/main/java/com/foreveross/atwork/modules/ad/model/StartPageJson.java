package com.foreveross.atwork.modules.ad.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by dasunsy on 15/12/14.
 */
public class StartPageJson {

    @SerializedName("modifyDate")
    public long modifyDate;

    @SerializedName("ad_settings")
    public AdPagesRecord adPagesRecord;

    @SerializedName("boot_settings")
    public SplashRecord splashPage;

    public class SplashRecord extends StartPageItem {
        @SerializedName("end_time")
        public long deadLine = -1;
    }

    public class AdPagesRecord {
        @SerializedName("end_time")
        public long deadLine = -1;

        @SerializedName("adModifyTime")
        public long adModifyTime;

        @SerializedName("ads")
        public ArrayList<StartPageItem> startPageItemList;
    }

    public boolean isBeforeAdPageDead() {
        return 0 >= adPagesRecord.deadLine
                || System.currentTimeMillis() < adPagesRecord.deadLine;
    }

    public boolean isBeforeSplashDead() {
        return 0 >= splashPage.deadLine || System.currentTimeMillis() < splashPage.deadLine;
    }

    public void calModifyDate() {
        long lastTotalModifyDate = -1;
        long adLastModifyDate = -1;

        if(null != adPagesRecord && adPagesRecord.startPageItemList != null) {
            for(StartPageItem pageItem : adPagesRecord.startPageItemList) {
                if(adLastModifyDate < pageItem.modifyTime) {
                    adLastModifyDate = pageItem.modifyTime;
                }
            }

            adPagesRecord.adModifyTime = adLastModifyDate;
        }

        if(null != splashPage) {
            lastTotalModifyDate = splashPage.modifyTime;
        }

        if(lastTotalModifyDate < adLastModifyDate) {
            lastTotalModifyDate = adLastModifyDate;
        }

        this.modifyDate = lastTotalModifyDate;
    }




}
