package com.foreveross.atwork.modules.ad.service;

import android.content.Context;

import androidx.annotation.Nullable;

import com.foreveross.atwork.api.sdk.startPage.StartPageNetService;
import com.foreveross.atwork.api.sdk.startPage.model.CheckStartPageDataResponse;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.infrastructure.shared.OrgCommonShareInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.ZipUtil;
import com.foreveross.atwork.modules.ad.model.StartPageJson;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by dasunsy on 15/12/14.
 */
public class StartPageAdService {
    public static StartPageAdService mInstance;

    public static StartPageAdService getInstance() {
        if(null == mInstance) {
            mInstance = new StartPageAdService();
        }

        return mInstance;
    }

    public static String getStartPageDataPath(String orgCode) {
        return AtWorkDirUtils.getInstance().getDataOrgDir(orgCode) + "startPageData/";
    }


    public File getDataFile(Context context, String path, String orgCode) {
        File dataFile = new File(path);
        if(!dataFile.exists()) {
            try {
                ZipUtil.upZipFile(AtWorkDirUtils.getInstance().getDataOrgDir(orgCode) + "startPageData.zip", getStartPageDataPath(orgCode), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return dataFile;
    }

    @Nullable
    public StartPageJson getStartPageJson(Context context, String orgCode) {
        if(StringUtils.isEmpty(orgCode)) {
            return null;
        }

        StartPageJson startPageJson = null;
        File jsonFile = getDataFile(context, getStartPageDataPath(orgCode) + "boot-setting.json", orgCode);
        try {
            String json = new String(FileUtil.readFileByRAWay(jsonFile));
            startPageJson = JsonUtil.fromJson(json, StartPageJson.class);
            if (null != startPageJson) {
                startPageJson.calModifyDate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return startPageJson;
    }


    public boolean shouldJumpToAdPage(Context context, String orgCode, StartPageJson json) {
        boolean shouldJump = false;
        if(null != json && null != json.adPagesRecord && !ListUtil.isEmpty(json.adPagesRecord.startPageItemList)) {
            boolean isBeforeDead = json.isBeforeAdPageDead();

            if(isBeforeDead && 0 != json.adPagesRecord.adModifyTime) {

                if(!OrgCommonShareInfo.isAdHasShown(context, orgCode, json.adPagesRecord.adModifyTime)) {
                    shouldJump = true;

                }
            }

        }
        return shouldJump;
    }


    public boolean shouldShowSplash(Context context, StartPageJson startPageJson) {
        return !(StringUtils.isEmpty(PersonalShareInfo.getInstance().getCurrentOrg(context)))
                && null != startPageJson
                && null != startPageJson.splashPage
                && startPageJson.isBeforeSplashDead();
    }

    public void handleStartPage(final Context context, final String orgCode) {
        //有组织 code 时才去拉取
        if ((StringUtils.isEmpty(orgCode))) {
            return;
        }

        StartPageNetService.checkStartPagePackage(context, orgCode, OrgCommonShareInfo.getLastLoadStartPageTime(context, orgCode), new StartPageNetService.OnCheckStartPagePackageListener() {

            @Override
            public void networkFail(int errorCode, String errorMsg) {

            }

            @Override
            public void success(CheckStartPageDataResponse response) {
                if(response.result.disabled) {
                    clearLocalPkg(orgCode);
                    return;
                }

                if(StringUtils.isEmpty(response.result.pkgId)) {
                    return;
                }

                refreshLocalPkg(context, response, orgCode);

            }

            @Override
            public void failed() {

            }
        });
    }

    private void refreshLocalPkg(Context context, CheckStartPageDataResponse response, String orgCode) {
        File dataZip = new File(AtWorkDirUtils.getInstance().getDataOrgDir(orgCode) + "startPageData.zip");
        if(!dataZip.exists() || response.hasUp()) {

            StartPageNetService.loadStartPagePackage(context
                    , UUID.randomUUID().toString()
                    , response.result.pkgId
                    , orgCode
                    , AtWorkDirUtils.getInstance().getDataOrgDir(orgCode) + "startPageData.zip"
                    , getStartPageDataPath(orgCode)
                    , new MediaCenterNetManager.MediaDownloadListener() {
                @Override
                public String getMsgId() {
                    return null;
                }

                @Override
                public void downloadSuccess() {
                    //update local last download packet's time
                    StartPageJson startPageJson = StartPageAdService.getInstance().getStartPageJson(context, orgCode);
                    if(null != startPageJson) {
                        OrgCommonShareInfo.setLastLoadStartPageTime(context, orgCode, startPageJson.modifyDate);
                    }
                }

                @Override
                public void downloadFailed(int errorCode, String errorMsg, boolean doRefreshView) {

                }

                @Override
                public void downloadProgress(double progress, double value) {

                }
            });
        }
    }

    private void clearLocalPkg(String orgCode) {
        String zipPath = AtWorkDirUtils.getInstance().getDataOrgDir(orgCode) + "startPageData.zip";
        String unzipDataPath = getStartPageDataPath(orgCode);
        FileUtil.deleteFile(zipPath, true);
        FileUtil.deleteFile(unzipDataPath, true);
    }
}
