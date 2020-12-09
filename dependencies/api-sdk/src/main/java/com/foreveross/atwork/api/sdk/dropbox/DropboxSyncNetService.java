package com.foreveross.atwork.api.sdk.dropbox;

import android.content.Context;
import android.text.TextUtils;

import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.dropbox.requestJson.FileTranslateRequest;
import com.foreveross.atwork.api.sdk.dropbox.responseJson.DropboxResponse;
import com.foreveross.atwork.api.sdk.dropbox.responseJson.ShareFileResponseJson;
import com.foreveross.atwork.api.sdk.dropbox.responseJson.ShareItemsRespJson;
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.api.sdk.net.BaseSyncNetService;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.api.sdk.util.NetWorkHttpResultHelper;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.dropbox.Requester;
import com.foreveross.atwork.infrastructure.model.dropbox.DropboxConfig;
import com.foreveross.atwork.infrastructure.model.dropbox.ShareItemRequester;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.HttpUrlConnectionUtil;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 * Created by reyzhang22 on 16/9/12.
 */
public class DropboxSyncNetService extends BaseSyncNetService {

    private static DropboxSyncNetService sInstance = new DropboxSyncNetService();

    public static DropboxSyncNetService getInstance() {
        return sInstance;
    }

    /**
     * 全量或增量查询网盘全部数据
     * @return List<Dropbox>
     */
    public HttpResult getDropboxBySource(Context context, Requester requester, DropboxConfig config) {
        String url = String.format(UrlConstantManager.getInstance().getDropboxBySource(), requester.mDomainId, requester.mSourceType, requester.mSourceId,
                config == null ? -1 : config.mLastRefreshTime,
                requester.mLimit, requester.mSkip, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        if (!TextUtils.isEmpty(requester.mParent)) {
            url  = url + "&parent=" + requester.mParent;
        }
        HttpResult result = HttpURLConnectionComponent.getInstance().getHttp(url);
        return result;
    }

    /**
     * 根据查询条件全量或增量查询网盘数据
     * @param domainId      域id
     * @param sourceType    源类型 USER DISCUSSION ORG
     * @param sourceId      对应源类型的id
     * @param parent        目录节点
     * @param keyword       关键字
     * @param fileType      文件类型 TEXT ARCHIVE IMAGE VIDEO AUDIO APPLICATION OTHER
     * @param skip          分页， 第几页
     * @param limit         分页， 一页多少条数据
     * @param sort          排序  TIME NAME SIZE   默认TIME
     * @param order         排序  ASC  DESC
     * @param unlimited     true parent无效，全量查询， false 分批查询
     * @param refreshTime   上次查询刷新时间
     * @param ownerId       查询该拥有者用户id
     * @param ownerDomainId 查询该拥有者domainId
     * @return List<Dropbox>
     */
    public HttpResult getDropboxByParams(Context context, String domainId, String sourceType, String sourceId,
                                            String parent, String keyword, String fileType, String skip,
                                            String limit, String sort, String order, boolean unlimited,
                                            String refreshTime, String ownerId, String ownerDomainId, DropboxConfig dropboxConfig) {
        List<Dropbox> dropboxes= null;
        String url = String.format("domains/%s/%s/%s/pan?", domainId, sourceType, sourceId);
        url = UrlConstantManager.getInstance().
                                    getDropboxByParams(url, parent, keyword, fileType, skip, limit, sort,
                                    order, unlimited, refreshTime, ownerId, ownerDomainId, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult result = HttpURLConnectionComponent.getInstance().getHttp(url);


        return result;
    }


    /**
     *
     * 根据域与类型创建相应文件夹
     * @param domainId      域id
     * @param sourceType    源类型
     * @param sourceId      相对于源的id
     * @param isDir         0表示是文件 取值为(0,1)  默认1
     * @param params        文件夹示例： {
                                "parent": "父节点名称，如果在根节点创建，不传此字段或传空值",
                                "name" : "文件夹名称",
                                "user" : {
                                    "name":"操作者名字"
                                }
                            }

                            文件示例 {
                                "name" : "文件名称.xml",
                                "size":1000,
                                "digest":"md5码",
                                "file_id":"文件的媒体标识",
                                "user" : {
                                    "name":"操作者名字"
                                }
                            }
     * @return List<Dropbox>
     */
    public HttpResult makeDropboxFileOrDir(Context context, String domainId, String sourceType, String sourceId, int isDir, String params, DropboxConfig dropboxConfig) {
        String url = String.format(UrlConstantManager.getInstance().makeDropboxFileOrDir(isDir), domainId, sourceType, sourceId, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult result = HttpURLConnectionComponent.getInstance().postHttp(url, params);
        return result;
    }

    /**
     * 移动文件或文件夹
     * @param domainId      域id
     * @param sourceType    源类型
     * @param sourceId      相对于源的id
     * @param params        文件夹
     *                      示例： {
                                "parent" : "待移动文件(文件夹)的父标识,如无父标识可不传或传空",
                                "ids":[待移动文件(文件夹)的标识,数据形式],
                                "target_parent" : "文件(文件夹)的移动到的新父标识",
                                "user" : {
                                    "name":"操作者名字"
                                }
                            }
    }
     * @return
     */
    public HttpResult moveDropbox(Context context, String domainId, String sourceType, String sourceId, String params, DropboxConfig dropboxConfig) {
        String url = String .format(UrlConstantManager.getInstance().moveDropbox(), domainId, sourceType, sourceId, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult result = HttpURLConnectionComponent.getInstance().postHttp(url, params);
        return result;
    }


    /**
     * 复制文件或文件夹
     * @param domainId      域id
     * @param sourceType    源类型
     * @param sourceId      相对于源的id
     * @param params        文件夹
                            参数：
                            {
                                "parent" : "待移动文件(文件夹)的父标识",如无父标识可不传或传空",
                                "ids":[待复制文件(文件夹)的标识,数据形式],
                                "target_parent" : "文件(文件夹)的移动到的新父标识",
                                "target_source_id":"文件(文件夹)的移动到的新的网盘标识，对应组织的ORG_CODE 或 讨论组的 ID  或  用户的 ID",
                                "target_source_type":"文件(文件夹)的移动到的新的网盘类型PERSONAL(个人), DISCUSSION(讨论组), ORG(组织)",
                                "user" : {
                                    "name":"操作者名字"
                                }
                            }
     * @return
     */
    public HttpResult copyDropbox(Context context, String domainId, String sourceType, String sourceId, String params) {
        String url = String .format(UrlConstantManager.getInstance().copyDropbox(), domainId, sourceType, sourceId, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult result = HttpURLConnectionComponent.getInstance().postHttp(url, params);
        return result;
    }

    /**
     * 移除文件或文件夹
     * @param domainId      域id
     * @param sourceType    源类型
     * @param sourceId      相对于源的id
     * @param params        文件夹
                            参数：
                            {
                                "parent" : "待移动文件(文件夹)的父标识",如无父标识可不传或传空",
                                "ids":[待删除文件(文件夹)的标识,数据形式],
                                "user" : {
                                    "name":"操作者名字"
                                }
                            }

     * @return
     */
    public HttpResult removeDropbox(Context context, String domainId, String sourceType, String sourceId, String params) {
        String url = String .format(UrlConstantManager.getInstance().removeDropbox(), domainId, sourceType, sourceId, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult result = HttpURLConnectionComponent.getInstance().postHttp(url, params);
        return result;
    }


    /**
     * 重命名文件或文件夹
     * @param domainId      域id
     * @param sourceType    源类型
     * @param sourceId      相对于源的id
     * @param params        文件夹
                            参数：
                            {
                                "name" : "新文件名称",
                                "user" : {
                                    "name":"操作者名字"
                                }
                            }

     * @return
     */
    public HttpResult renameDropbox(Context context, String domainId, String sourceType, String sourceId, String fileId, String params) {
        String url = String .format(UrlConstantManager.getInstance().renameDropbox(), domainId, sourceType, sourceId, fileId, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult result = HttpURLConnectionComponent.getInstance().postHttp(url, params);
        return result;
    }

    public HttpResult setDropboxRWSetting(Context context, String domainId, Dropbox.SourceType sourceType, String opsUserId, String params) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String url = String.format(UrlConstantManager.getInstance().setRealOnlySetting(), domainId, sourceType.toString(), opsUserId, accessToken);
        HttpResult result = HttpURLConnectionComponent.getInstance().postHttp(url, params);
        return result;
    }

    public HttpResult shareDropbox(Context context, String domainId, Dropbox.SourceType sourceType, String opsId, String params) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String url = String.format(UrlConstantManager.getInstance().shareDropbox(), domainId, sourceType.toString(), opsId, accessToken);
        HttpResult result = HttpURLConnectionComponent.getInstance().postHttp(url, params);
        if (result.isNetSuccess()) {
            result.result(NetGsonHelper.fromNetJson(result.result, ShareFileResponseJson.class));
        }
        return result;
    }

    public HttpResult translateFile(Context context, FileTranslateRequest req) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String url = String.format(UrlConstantManager.getInstance().translateFile(), req.mMediaId, req.mFileType, req.mSkip, req.mLimit, accessToken);
        HttpResult result = HttpURLConnectionComponent.getInstance().getHttp(url);
        return result;
    }

    public HttpResult getDropboxShareItems(Context context, ShareItemRequester requester) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String url = String.format(UrlConstantManager.getInstance().getDropboxShareItems(requester), accessToken);
        HttpResult result = HttpURLConnectionComponent.getInstance().getHttp(url);
        if (result.isNetSuccess()) {
            result.result(NetGsonHelper.fromNetJson(result.result, ShareItemsRespJson.class));
        }
        return result;
    }


    public HttpResult getDropboxInfo(Context context, String domainId, String sourceType, String sourceId, String fileId) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String url = String.format(UrlConstantManager.getInstance().getDropboxInfo(), domainId, sourceType, sourceId, fileId, accessToken);
        HttpResult result = HttpURLConnectionComponent.getInstance().getHttp(url);
        if(result.isNetSuccess()) {
            DropboxResponse basicResponseJSON = JsonUtil.fromJson(result.result, DropboxResponse.class);

            if(null == basicResponseJSON) {
                return result;
            }

            String resultText = NetWorkHttpResultHelper.getResultText(result.result);

            try {
                if (!StringUtils.isEmpty(resultText)) {
                    basicResponseJSON.result = Dropbox.parser(new JSONObject(resultText));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            result.result(basicResponseJSON);

        }

        return result;
    }


}
