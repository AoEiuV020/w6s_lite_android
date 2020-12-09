package com.foreveross.atwork.modules.advertisement.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.advertisement.BootAdvertisementService;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil;
import com.foreveross.atwork.api.sdk.net.model.DownloadFileParamsMaker;
import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementConfig;
import com.foreveross.atwork.infrastructure.model.advertisement.adEnum.AdvertisementType;
import com.foreveross.atwork.infrastructure.shared.AdvertisementInfo;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.BitmapUtil;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.NetworkStatusUtil;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.aliyun.CdnHelper;
import com.foreveross.atwork.infrastructure.utils.file.FileStreamHelper;
import com.foreveross.atwork.utils.ImageCacheHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;

/**
 * Created by reyzhang22 on 17/9/18.
 */

public class BootAdvertisementManager {

    private static final BootAdvertisementManager sInstance = new BootAdvertisementManager();

    //防止递归死循环
    private int mRetryTime = 0;

    public static BootAdvertisementManager getInstance() {
        return sInstance;
    }



    /**
     * 获取本地某组织id下本地的广告列表数据(合法的)
     * @param context
     * @param orgId
     * @return
     */
    public List<AdvertisementConfig> getLegalLocalAdvertisementsByOrgId(Context context, String orgId) {
        return CollectionsKt.filter(AdvertisementInfo.getInstance().getOrgAdvertisementData(context, orgId), advertisementConfig -> {
            String loginUsername = LoginUserInfo.getInstance().getLoginUserUserName(context);
            return isAdvertisementAccessable(loginUsername, orgId, advertisementConfig);
        });
    }

    public long getLastViewTimeByOrgId(Context context, String orgId) {
        return AdvertisementInfo.getInstance().getLastViewTime(context, orgId);
    }

    /**
     * 获取远端某组织id下的广告数据列表
     * @param context
     * @param orgId
     */
    public synchronized void getRemoteBootAdvertisementsByOrgId(Context context, String orgId) {
        BootAdvertisementService.getInstance().getLatestBootAdvertisements(context, orgId, new BootAdvertisementService.OnFetchLatestAdvertisementListener(){

            @Override
            public void onFetchSuccess(List<AdvertisementConfig> advertisementConfigList) {
                if (ListUtil.isEmpty(advertisementConfigList)) {
                    return;
                }
                String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
                String userName = LoginUserInfo.getInstance().getLoginUserUserName(context);
                String dirPath = AtWorkDirUtils.getInstance().getUserOrgAdvertisementDir(userName, orgId);
                String dirTempPath = AtWorkDirUtils.getInstance().getUserOrgAdvertisementTempDir(userName, orgId);

                boolean isWifi = NetworkStatusUtil.isWifiConnectedOrConnecting(context);
                for (AdvertisementConfig config: advertisementConfigList) {
                    if (TextUtils.isEmpty(config.mMediaId)) {
                        continue;
                    }
                    String filePath = dirPath + config.mMediaId;
                    File file = new File(filePath);
                    if (file.exists()) {
                        continue;
                    }
                    if (!isWifi && config.mWifiLoading) {
                        continue;
                    }
                    String tempFilePath = dirTempPath + config.mMediaId;
                    File tempFile = new File(tempFilePath);
                    if (tempFile.exists()) {
                        FileUtil.delete(tempFilePath);
                    }
                    if (AdvertisementType.Image.equals(config.mType)) {
                        ImageCacheHelper.loadImageByMediaId(config.mMediaId, new ImageCacheHelper.ImageLoadedListener() {
                            @Override
                            public void onImageLoadedComplete(Bitmap bitmap) {
                                asyncSaveImageFile(filePath, bitmap);
                            }

                            @Override
                            public void onImageLoadedFail() {

                            }
                        });
                        return;
                    }
                    String downloadUrl = String.format(UrlConstantManager.getInstance().V2_getDownloadUrl(true), config.mMediaId, accessToken);
                    downloadUrl = CdnHelper.wrapCdnUrl(downloadUrl);
                    asyncDownloadMediaFile(config.mId, downloadUrl, tempFilePath, filePath);
                }
            }

            @Override
            public void onFetchFail() {

            }
        });
    }

    /**
     * 设置最新的看过广告的时间
     * @param context
     * @param orgId
     */
    public void setLatestViewAdTime(Context context, String orgId) {
        AdvertisementInfo.getInstance().setLastViewTime(context, orgId, TimeUtil.getCurrentTimeInMillis());
    }

    /**
     * 清除上一次查看广告的时间
     * @param context
     * @param orgId
     */
    public void clearLastViewAdTime(Context context, String orgId) {
        AdvertisementInfo.getInstance().setLastViewTime(context, orgId, TimeUtil.getCurrentTimeInMillis());
    }


    @SuppressLint("StaticFieldLeak")
    private void asyncDownloadMediaFile(String id, String downloadUrl, String tempPath, String destPath) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                HttpResult httpResult = MediaCenterHttpURLConnectionUtil.getInstance().downloadFile(
                        DownloadFileParamsMaker.Companion.newRequest().setDownloadId(id).setDownloadUrl(downloadUrl).setDownloadPath(tempPath).setEncrypt(AtworkConfig.OPEN_DISK_ENCRYPTION).setProgressListener(
                                (value, size) -> {
                                    Logger.e("donwload", "progress = " + value);
                                }
                        )
                );
                if (httpResult != null && httpResult.isNetSuccess()) {
                    try {
                        FileUtil.copyFile(tempPath, destPath);
                        FileUtil.delete(tempPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                return null;
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    private void asyncSaveImageFile(final String filePath, final Bitmap bitmap) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                byte[] content = BitmapUtil.Bitmap2Bytes(bitmap);
                FileStreamHelper.saveFile(filePath, content);
                return null;
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    //1.随机从广告数据列表随机挑选一条，并且对比本地是否存在这个广告文件
    public Holder getRandomAdvertisementInList(Context context, String clientId, String orgId) {
        List<AdvertisementConfig> list = BootAdvertisementManager.getInstance().getLegalLocalAdvertisementsByOrgId(context, orgId);
        int listSize = list.size();
        //如果只有一条广告，没必要random，直接返回结果
        Holder holder = new Holder();
        if (listSize == 1) {
            AdvertisementConfig config = list.get(0);
            holder.mAdvertisement = config;
            holder.mAccessAble = isAdvertisementAccessable(clientId, orgId, config);
            return holder;
        }
        Random random = new Random();
        int page = random.nextInt(listSize);
        AdvertisementConfig config = list.get(page);
        holder.mAdvertisement = config;
        //如果随机到的广告不满足播放条件，递归重新选择
        boolean accessAble = isAdvertisementAccessable(clientId, orgId, config);
        if (!accessAble && mRetryTime < listSize) {
            mRetryTime++;
            getRandomAdvertisementInList(context, clientId, orgId);
        }
        holder.mAccessAble = accessAble;
        return holder;
    }

    /**
     * 清空重试
     */
    public void clearRetryTime() {
        mRetryTime = 0;
    }

    //该广告是否满足播放条件
    private boolean isAdvertisementAccessable(String clientId, String orgId, AdvertisementConfig config) {
        //2. 本地是否存在
        String filePath = AtWorkDirUtils.getInstance().getUserOrgAdvertisementDir(clientId, orgId) + config.mMediaId;
        File file = new File(filePath);
        if (!file.exists()) {
            getRemoteBootAdvertisementsByOrgId(AtworkApplicationLike.baseContext, orgId);
            return false;
        }
        //3.如果文件存在,比较当前广告的有效性是否过期
//        return !TimeUtil.isOverdueDate(TimeUtil.getCurrentTimeInMillis(), config.mEndTime);
        return config.isValidDuration();
    }


    public static class Holder {
        public boolean mAccessAble;
        public AdvertisementConfig mAdvertisement;
    }
}
