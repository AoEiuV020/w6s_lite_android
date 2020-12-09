package com.foreveross.atwork.infrastructure.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.webkit.URLUtil;

import java.io.UnsupportedEncodingException;

/**
 * Created by dasunsy on 2018/1/4.
 */

public class SystemDownloadUtil {

    public static void download(Context context, String url, String contentDisposition, String mimeType, String cookie) {

        // 指定下载地址
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        // 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
        request.allowScanningByMediaScanner();
        // 设置通知的显示类型，下载进行时和完成后显示通知
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // 设置通知栏的标题，如果不设置，默认使用文件名
//        request.setTitle("This is title");
        // 设置通知栏的描述
//        request.setDescription("This is description");
        // 允许在计费流量下下载
        request.setAllowedOverMetered(true);
        // 允许该记录在下载管理界面可见
        request.setVisibleInDownloadsUi(false);
        // 允许漫游时下载
        request.setAllowedOverRoaming(true);
        // 允许下载的网路类型
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        // 设置下载文件保存的路径和文件名
        String fileName  = getNameFromDownloadUrl(url, contentDisposition, mimeType);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        if(!StringUtils.isEmpty(cookie)) {
            request.addRequestHeader("Cookie", cookie);
        }


        final DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        // 添加一个下载任务
        long downloadId = downloadManager.enqueue(request);
    }

    public static String getFilePathFromDownloadUrlChecking(Context context, String url, String contentDisposition, String mimeType) {
        return getFileInfoFromDownloadUrlChecking(context, url, contentDisposition, mimeType).filePath;
    }

    public static String getFileNameFromDownloadUrlChecking(Context context, String url, String contentDisposition, String mimeType) {
        return getFileInfoFromDownloadUrlChecking(context, url, contentDisposition, mimeType).fileName;
    }

    public static FileUtil.FileInfo getFileInfoFromDownloadUrlChecking(Context context, String url, String contentDisposition, String mimeType) {

        String shouldOriginalName = getNameFromDownloadUrl(url, contentDisposition, mimeType);

        return FileUtil.getFileInfoByChecking(shouldOriginalName, AtWorkDirUtils.getInstance().getFiles(context));

    }





    public static String getNameFromDownloadUrl(String url, String contentDisposition, String mimeType) {
        String fileName = null;
        String fileNameTag = "filename=";
        if(contentDisposition.contains(fileNameTag)) {
            String reg = "(^[\"'])|([\"']$)";

            fileName = Uri.decode(contentDisposition.substring(contentDisposition.indexOf(fileNameTag) + fileNameTag.length()));

            //去除头尾的双引号或单引号
            fileName = fileName.replaceAll(reg, StringUtils.EMPTY);

        }

        if(StringUtils.isEmpty(fileName) || !fileName.contains(".")) {
            fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
        }

        return fileName;
    }


}
