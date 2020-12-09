package com.foreveross.atwork.infrastructure.utils;

import android.content.Context;
import android.os.Environment;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;

import java.io.File;
import java.util.ArrayList;

/**
 * 文件夹路径
 * Created by ReyZhang on 2015/4/29.
 */
public class AtWorkDirUtils {

    private static final String TAG = AtWorkDirUtils.class.getSimpleName();

    private static final String USER = "USER";

    //atWork文件夹路径
    public static String ATWORK_DIR;

    private static String CACHE_DIR;

    private static String ATWORK_FOLDER = File.separator + AtworkConfig.APP_FOLDER + File.separator;

    private static AtWorkDirUtils sInstance = new AtWorkDirUtils();

    //todo double check
    public static AtWorkDirUtils getInstance() {
        synchronized (TAG) {
            if (sInstance == null) {
                sInstance = new AtWorkDirUtils();
            }
            if (TextUtils.isEmpty(ATWORK_DIR)) {
                mkAtworkDir();
            }
            return sInstance;
        }
    }

    private static void mkAtworkDir() {

        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            ATWORK_DIR = BaseApplicationLike.baseContext.getExternalFilesDir(null).getAbsolutePath();
        } else {
            String path = getInternalPath();
            if (!TextUtils.isEmpty(path)) {
                ATWORK_DIR = path + ATWORK_FOLDER;
            } else {
                ATWORK_DIR = CACHE_DIR;
            }

        }
    }




    /**
     * ImageLoader图片缓存路径
     */
    public String getImageDiskCacheDir(String clientId) {
        return getUserFilePath(clientId, "DISK_CACHE");
    }

    /**
     * 崩溃日志文件夹路径
     */
    public String getCrashLogDir() {
        return getUserFilePath(null, "CRASH_LOG");
    }

    public String getWebCacheDir() { return getUserFilePath(null, "WEB_CACHE"); }

    public String getImageReserveDiskCacheDir(String clientId) {
        return getUserFilePath(clientId, "DISK_CACHE" + File.separator + "reserve_disk_cache");
    }

    /**
     * user图片文件夹
     */
    public String getImageDir(Context context) {
        return getImageDir(LoginUserInfo.getInstance().getLoginUserUserName(context));
    }

    /**
     * user图片文件夹
     */
    public String getImageDir(String clientId) {
        return getUserFilePath(clientId, "IMAGE" + File.separator);
    }

    /**
     * 网盘目录
     * */
    public String getDropboxDir(Context  context) {
        String loginUsername = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return getUserFilePath(loginUsername, "CLOUD_DISK" + File.separator);
    }

    public String getDropboxDir(String clientId, String distPath) {
        String rootPath = getUserFilePath(clientId, "CLOUD_DISK");
        StringBuilder path = new StringBuilder();
        path.append(rootPath).append(distPath);
        return getFileMkdirPath(path.toString());
    }

    public String getDocDir(String clientId, String distPath) {
        String rootPath = getUserFilePath(clientId, "DOC_CENTER");
        StringBuilder path = new StringBuilder();
        path.append(rootPath).append(distPath);
        return getFileMkdirPath(path.toString());
    }

    /**
     * user相册文件夹
     */
    public String getGalleryDir(String clientId) {
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        String galleryDir;
        if (sdCardExist) {
            //相册文件夹
            galleryDir = getUserFilePath(clientId, AtworkConfig.APP_FOLDER);
        } else {
            //当没有 SD 卡时, 不能使用 cache 作为图片文件, media 没有权限读取
            String internalPath = getInternalPath();

            if (StringUtils.isEmpty(internalPath)) {
                galleryDir = getUserFilePath(clientId, AtworkConfig.APP_FOLDER);
            } else {
                galleryDir = internalPath + ATWORK_FOLDER + clientId + File.separator + AtworkConfig.APP_FOLDER;
            }
        }
        return getFileMkdirPath(galleryDir);
    }

    /**
     * 没有权限的时候 获得的路径
     */
    public String getDefaultGalleryDir(String clientId) {
        return getUserFilePath(clientId, AtworkConfig.APP_FOLDER);
    }

    /**
     * 更新应用文件夹
     */
    public String getAppUpgrade() {
        return getUserFilePath(null, "UPGRADE");
    }

    /**
     * apk 文件夹
     * */
    public String getAppUpgrade(String clientId) {
        return getUserFilePath(clientId, "UPGRADE");
    }

    /**
     * 头像文件夹
     */
    public String getAvatarFile(String clientId) {
        return getUserFilePath(clientId, "AVATAR" + File.separator);
    }


    /**
     * 聊天文件文件夹
     * */
    public String getChatFiles(Context context) {
        return getUserFilePath(LoginUserInfo.getInstance().getLoginUserUserName(context), "FILE_CHAT" + File.separator);
    }

    public String getFiles(Context context) {
        return getFiles(LoginUserInfo.getInstance().getLoginUserUserName(context));
    }

    /**
     * 文件文件夹
     */
    public String getFiles(String clientId) {
        return getUserFilePath(clientId, "FILE" + File.separator);
    }

    /**
     * 音频文件文件夹
     */
    public String getAUDIO(Context context) {
        return getAUDIO(LoginUserInfo.getInstance().getLoginUserUserName(context));
    }

    /**
     * 音频文件文件夹
     */
    public String getAUDIO(String clientId) {
        return getUserFilePath(clientId, "AUDIO" + File.separator);
    }

    public String getPanelSave(Context context) {
        return getPanelSave(LoginUserInfo.getInstance().getLoginUserUserName(context));
    }

    public String getPanelSave(String clientId) {
        return getUserFilePath(clientId, "PANEL" + File.separator);
    }


    /**
     * 日志文件夹
     */
    public String getLOG() {
        return getUserFilePath(null, "LOG");
    }

    public String getStickerRootPath() {

        return getUserFilePath(null, ".STICKER_NEW");
    }

    public String getStickerThumbnailPath(String categoryId, String stickerThumbnailName) {
        return AtWorkDirUtils.getInstance().getStickerDirByCategoryId(categoryId ) + "thumb/" + stickerThumbnailName;
    }

    public String getStickerThumbnailUrl(Context context, String url, String categoryId, String stickerId) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        return String.format(url, categoryId, stickerId, "thumb", accessToken);
    }

    public String getStickerOriginalPath(String categoryId, String stickerOriginName) {
        return AtWorkDirUtils.getInstance().getStickerDirByCategoryId(categoryId ) + "origin/" + stickerOriginName;
    }

    public String getStickerOriginalUrl(Context context, String url, String categoryId, String stickerId) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        if (stickerId.contains(".")) {
            stickerId = stickerId.split("\\.")[0];
        }
        return String.format(url, categoryId, stickerId, "origin", accessToken);
    }

    public String getStickerDirByCategoryId(String categoryId) {
        String path = getStickerRootPath() + File.separator + categoryId + File.separator;
        return path;
    }

    public String getAssetStickerUri(String themeName, String name) {
        return getStickerRootPath() + File.separator + themeName + File.separator  + themeName + "_origin" + File.separator + name;
    }

    /**
     * 全时云语音视频 日志
     * */
    public String getQsyVoipLOG() {
        return getLOG() + File.separator + "QSY" + File.separator;
    }

    /**
     * 声网语音视频 日志
     * */
    public String getAgoraVoipLOG() {
        return getLOG() + File.separator + "AGORA" + File.separator + "agora.log";
    }

    /**
     * 邮件附件文件夹
     */
    public String getEmailAttachmentDir(String clientId) {
        return getUserFilePath(clientId, "EMAIL_ATTACHMENT" + File.separator);
    }

    public String getMultipartDir(Context context) {
        String clientId = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return getUserFilePath(clientId, "MULTIPART" + File.separator);
    }

    public String getTmpDir(Context context) {
        String clientId = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return getUserFilePath(clientId, "TMP" + File.separator);
    }


    /**
     * 小视频接收目录
     * */
    public String getMicroVideoDir(Context context) {
        return getMicroVideoDir(LoginUserInfo.getInstance().getLoginUserUserName(context));
    }

    /**
     * 小视频接收目录
     * */
    public String getMicroVideoDir(String clientId) {
        return getUserFilePath(clientId, "MICRO_VIDEO" + File.separator);
    }

    /**
     * 小视频发送历史目录
     * */
    public String getMicroVideoHistoryDir(Context context) {
        return getMicroVideoHistoryDir(LoginUserInfo.getInstance().getLoginUserUserName(context));
    }

    /**
     * 小视频发送目录
     * */
    public String getMicroVideoHistoryDir(String clientId) {
        return getUserFilePath(clientId, "MICRO_VIDEO" + File.separator + "HISTORY" + File.separator);
    }

    public String getCompressImageDir(String clientId) {
        return getUserFilePath(clientId, "IMAGE_COMPRESS" + File.separator);
    }

    /**
     * 用户组织id下广告根目录
     * @param clientId
     * @return
     */

    public String getUserOrgAdvertisementDir(String clientId, String orgId) {
        return getUserFilePath(clientId, "ADVERTISEMENT" + File.separator + orgId + File.separator);
    }

    public String getUserOrgAdvertisementTempDir(String clientId, String orgId) {
        return getUserFilePath(clientId, "ADVERTISEMENT" + File.separator + orgId + File.separator + "TEMP" + File.separator);
    }

    public String getUserOrgAdvertisementBannerDir(Context context, String orgId) {
        return getUserFilePath(LoginUserInfo.getInstance().getLoginUserUserName(context), "ADVERTISEMENT" + File.separator + orgId + File.separator + "BANNER" + File.separator);
    }


    /**
     * 得到数据包根路径
     * */
    public String getDataRootDir() {
        return getFileMkdirPath(getRootDirPrimary() + "DATA" + File.separator);
    }

    private String getRootDirPrimary() {
        return BaseApplicationLike.baseContext.getExternalFilesDir(null).getAbsolutePath();
    }

    /**
     * 得到数据包文件夹路径, 按组织 code
     *
     * @return 路径
     */
    public String getDataOrgDir(String orgCode) {
        return getFileMkdirPath(getDataRootDir() + orgCode) + File.separator;
    }


    /**
     * 轻应用离线数据包, 按组织 code 区分
     * */
    public String getLightAppOfflineDataOrgDir(String orgCode, String appId) {
        return getFileMkdirPath(getDataOrgDir(orgCode) + appId + File.separator);
    }

    /**
     * 得到 vpn 证书文件夹路径. 按组织 code
     * */
    public String getVpnDir(Context context, String orgCode) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return getUserFilePath(username, "VPN" + File.separator + orgCode + File.separator);

    }

    private String getFileMkdirPath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public void initialAtworkDirPath(Context context) {
        CACHE_DIR = context.getCacheDir() + ATWORK_FOLDER;
    }


    /**
     * 获取内部存储的地址, 不同手机会使用不同名称的目录, 此处是先获得挂载点然后进行判断筛选
     */
    private static String getInternalPath() {
        //所有挂载的地址
        ArrayList<String> mountList = getDevMountList();
        String internalPath = "";
        for (String mountPth : mountList) {
            File dictory = new File(mountPth);
            if (dictory.isDirectory() && dictory.canWrite() && dictory.canRead() && dictory.canExecute()) {
                internalPath = dictory.getAbsolutePath();
                break;
            }
        }

        return internalPath;
    }

    /**
     * 遍历/etc/vold.fstab 获取全部的Android的挂载点信息
     *
     * @return
     */
    private static ArrayList<String> getDevMountList() {
        byte[] contentByte = FileUtil.readFile("/etc/vold.fstab");
        String[] toSearch = new String(contentByte).split(" ");
        ArrayList<String> out = new ArrayList<>();
        for (int i = 0; i < toSearch.length; i++) {
            if (toSearch[i].contains("dev_mount")) {
                if (new File(toSearch[i + 2]).exists()) {
                    out.add(toSearch[i + 2]);
                }
            }
        }
        return out;
    }

    public String getTmpShareSavePath() {
        return getFileMkdirPath(CACHE_DIR + "tmp" + File.separator + "share" + File.separator);
    }

    public String getTmpFilesCachePath() {
        return getFileMkdirPath(CACHE_DIR + "tmp" + File.separator + "revert" + File.separator);
    }

    public String getLoginUserFilePath(Context context, String dir) {
        String loginUserName = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return getUserFilePath(loginUserName, dir);
    }

    /**
     * 根据用户 user 返回 user 对应目录的文件夹, 若 user 为 null, 则返回根目录下对应的文件夹
     * @param clientId
     * @param dir
     * */
    public String getUserFilePath(String clientId, String dir) {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(clientId)) {
            sb.append(ATWORK_DIR).append(USER).append(File.separator).append(clientId).append(File.separator).append(dir);
            return getFileMkdirPath(sb.toString());

        }
        if (TextUtils.isEmpty(ATWORK_DIR)) {
            mkAtworkDir();
        }
        return getFileMkdirPath(sb.append(ATWORK_DIR).append(dir).toString());
    }


    /**
     * 返回用户对应的目录
     * */
    public String getUserDicPath(@NonNull String clientId) {

        return getFileMkdirPath(getUserRootDicPath() + clientId);

    }


    /**
     * 返回用户对应的User 根目录
     * */
    public String getUserRootDicPath() {
        return ATWORK_DIR + USER + File.separator;
    }


    public static boolean isWorkplusFile(String path) {
        return path.startsWith(ATWORK_DIR)  ||  path.startsWith(AtWorkDirUtils.getInstance().getTmpFilesCachePath());
    }

    public static  String getSdCacheDir(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            java.io.File fExternalStorageDirectory = BaseApplicationLike.baseContext.getExternalCacheDir();
            java.io.File autonaviDir = new java.io.File(
                    fExternalStorageDirectory, "amapsdk");
            boolean result = false;
            if (!autonaviDir.exists()) {
                result = autonaviDir.mkdir();
            }
            java.io.File minimapDir = new java.io.File(autonaviDir,
                    "offlineMap");
            if (!minimapDir.exists()) {
                result = minimapDir.mkdir();
            }
            return minimapDir.toString() + "/";
        } else {
            return "";
        }
    }
}
