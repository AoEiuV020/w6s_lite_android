package com.foreveross.atwork.api.sdk;

import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.manager.OrganizationSettingsManager;
import com.foreveross.atwork.infrastructure.model.dropbox.ShareItemRequester;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.UrlHandleHelper;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by dasunsy on 15/12/10.
 */
public class UrlConstantManager {
    private static UrlConstantManager mInstance;

    public static UrlConstantManager getInstance() {
        if (null == mInstance) {
            mInstance = new UrlConstantManager();
        }

        return mInstance;
    }


    /**
     * 查询域列表信息
     * */
    public String getQueryMultiDomainsUrl() {
        return verifyApiUrl() + "licenses";
    }


    /**
     * 获取会员基本信息
     * @return
     */
    public String getMemberBasicInfo(String orgCode){
        return OrganizationSettingsManager.getInstance().getMyIntegralBaseUrl(orgCode)+"v1/members/%s?channel_id=%s&signature=%s&nonce=%s&timestamp=%s&encryption=%s&type=%s&status=%s&distinguish_id=%s";
    }


    /**
     * 获取会员详细信息
     * @return
     */
    public String getMemberDetailedInfo(String orgCode){
        return OrganizationSettingsManager.getInstance().getMyIntegralBaseUrl(orgCode)+"v1/platform/members/%s/details?channel_id=%s&signature=%s&nonce=%s&timestamp=%s&type=%s&encryption=%s";
    }


    /*     ********************************    Contact相关   ******     */


    /**
     * 获取个人用户信息
     */
    public String getUserInfo() {
        return AtworkConfig.API_URL + "organization/employee/%s?type=name&access_token=%s";
    }


    /**
     * 批量人员查询
     */
    public String getBatchQueryContacts() {
        return AtworkConfig.API_URL + "organization/employee?query=%s&access_token=%s&matching=true";
    }

    public String queryDepartmentRemote() {
        return setupApiWithAccessToken("organizations/orgs");
    }

    public String fetchAutoJoinOrg() {
        return appendApiWithAccessToken("organizations?query=L");
    }


    public String autoJoinOrg() {
        return setupApiWithAccessToken("organizations/%s/applications");
    }


    /*     ********************************    Colleague 相关   ******     */

    public String getColleagueNoticeUrl() {
        return AtworkConfig.COLLEAGUE_URL + "notify/?ticket=%s&domainId=%s&orgId=%s&userId=%s";
    }


    /*     ********************************    Login 相关   ******     */


    /**a
     * 发送验证码
     * */
    public String getSendMobileSecureCodeUrl() {
        return verifyApiUrl() + "secure-codes";
    }

    /**
     * 通过验证码预登录
     * */
    public String getPreUserRegistryUrl() {
        return setupApiWithAccessToken("pre-user-registry");
    }

    /**
     * 验证码预登陆后的处理, 设置用户个人信息, 或者密码
     * */
    public String getUserRegistryUrl() {
        return setupApiWithAccessToken("user-registry");
    }

    /**
     * 验证密码的请求 URL
     */
    public String getAuthUrl() {
        return AtworkConfig.API_URL + "auth?access_token=%s";
    }

    /**
     * 获取登录公钥
     */
    public String getTokenInitUrl() {
        return verifyApiUrl() + "init";
    }
    /**
     * 登录TOKEN请求URL
     */
    public String getTokenUrl() {
        return verifyApiUrl() + "token";
    }

    /**
     * 设置设备多端在线静音接口
     */
    public String setDevicesMode() {
        return setupApiWithAccessToken("users/%s/devices/mode");
    }

    /**
     * pc端下线
     */
    public String logoutPCUrl() {
        return setupApiWithAccessToken("users/%s/devices/%s");
    }

    /**
     * 获取SOCKET认证密钥及IP,PORT的请求URL
     */
    public String getENDPOINT() {
        return AtworkConfig.API_URL + "endpoints?access_token=%s";
    }

    /**
     * 获取所有群组接口
     */
    public String getQueryAllGroupUrl() {
        return AtworkConfig.API_URL + "user/%s/discussions/detail?access_token=%s";
    }

    /**
     * 同步联系人
     * GET
     */
    public String getSyncContactsUrl() {
        return AtworkConfig.API_URL + "user/%s/contacts?access_token=%s";
    }

    /**
     * 获取所有应用
     */
    public String getSyncAppUrl() {
        return AtworkConfig.API_URL + "user/%s/apps?access_token=%s&source=interesting&platforms=general,android";
    }

    /**
     * 删除设备
     */
    public String getDelteDevice() {
        return AtworkConfig.API_URL + "users/%s/devices?access_token=%s";
    }

    public String getModifyDeviceSettingsUrl() {
        return setupApiWithAccessToken("users/%s/devices/settings");
    }


    /**
     * 宇视科技：二维码扫描阿里接口appcode
     */
    public String getYSKJAppCode() {
        return AtworkConfig.YSKJ_APP_CODE_URL;
    }



    /**
     * 从服务器中查询远程app应用数据信息
     */
    public String getGetAppInfoFromRemote() {
        return AtworkConfig.API_URL + "users/%s/apps/%s?access_token=%s&platforms=general,android&org_id=%s&access_type=mobile";
    }


    /*     ********************************    应用检查更新 相关   ******     */




    public String getAdminDownloadUrl() {
        String preAdminUrl = AtworkConfig.getAdminMediaUrl();
        if (!preAdminUrl.endsWith("/")) {
           preAdminUrl += "/";
        }
        return preAdminUrl + "public/releases/" + AtworkConfig.DOMAIN_ID + "/" + AtworkConfig.PROFILE + "/download/android";
    }


    //---------------------------------------API V2------------------------------------------------------

    //--------------------------------------- 配置 相关------------------------------------------------------

    public String V2_getDomainSettings() {
        StringBuilder sb = new StringBuilder();

        sb.append(AtworkConfig.API_URL);
        sb.append("domains/%s/settings?refresh_time=-1");
        sb.append("&device_platform=android&pkg_name=%s");
        sb.append("&profile=").append(AtworkConfig.PROFILE);
        return sb.toString();
    }

    public String V2_getCurrentUserOrganizationsSettings() {
        StringBuilder builder = new StringBuilder();
        builder.append(AtworkConfig.API_URL);
        builder.append("users/%s/organization-settings?access_token=%s&refresh_time=%s");
        return builder.toString();
    }

    //---------------------------------------feedback 相关------------------------------------------------------

    public String V2_getFeedBackUrl() {
        return AtworkConfig.API_URL + "feedbacks?access_token=%s";
    }

    //---------------------------------------用户 相关------------------------------------------------------

    /**
     * 解除好友关系
     */
    public String V2_dismissFriendUrl() {
        return appendApiWithAccessToken("users/%s/relationships?friend_domain_id=%s&friend_user_id=%s");
    }

    /**
     * 获取登录用户信息api
     *
     * @return
     */
    public String V2_fetchUserFromRemoteUrl() {
        return setupApiWithAccessToken("user");
    }

    /**
     * 修改用户密码
     */
    public String V2_modifyUserPassword() {
        return setupApiWithAccessToken("users/%s/credentials");
    }

    /**
     * 修改用户资料
     */
    public String V2_modifyUserInfo() {
        return setupApiWithAccessToken("users/%s/profile");
    }

    /**
     * 修改用户个人签名
     */
    public String V2_modifyPersonalSignature() {
        return setupApiWithAccessToken("users/%s/moments");
    }

    /**
     * 修改用户名字
     *
     * @return
     */
    public String V2_modifyUserName() {
        return setupApiWithAccessToken("users/%s/name");
    }

    /**
     * 修改头像
     */
    public String V2_modifyUserAvatar() {
        return setupApiWithAccessToken("users/%s/avatar");
    }

    /**
     * 获取登录用户群组api
     *
     * @return
     */
    public String V2_fetchUserDiscussionsUrl() {
        return setupApiWithAccessToken("users/%s/discussions/detail");
    }

    /**
     * 获取登录用户好友api
     *
     * @return
     */
    public String V2_fetchUserFriendsUrl() {
        return setupApiWithAccessToken("users/%s/relationships");
    }


    /**
     * 获取用户星标联系人api
     *
     * @return
     */
    public String V2_fetchUserFlagContactsUrl() {
        return setupApiWithAccessToken("users/%s/contacts");
    }

    /**
     * 搜索人员列表api
     *
     * @return
     */
    public String V2_searchUserListUrl() {
        return appendApiWithAccessToken("users?query=%s");
    }

    public String V2_searchEmployeeListUrl() {
        return appendApiWithAccessToken("organizations/%s/employees?query=%s");
    }

    public String V2_searchEmployeeListMultiCodeUrl() {
        return setupApiWithAccessToken("organizations/employees");
    }

    /**
     * 批量拉取人员列表 api
     */
    public String V2_fetchUserListUrl() {
        return appendApiWithAccessToken("users?query=%s&matching=true");
    }


    /**
     * 获取其他用户信息api
     *
     * @return
     */
    public String V2_queryUserInfoUrl() {
        return appendApiWithAccessToken("users/%s?type=%s");
    }


    /**
     * 个人分享
     */
    public String V2_fetchShareUserUrl() {
        return setupApiWithAccessToken("users/%s/share") + "&format=%s";
    }

    /**
     * 组织分享
     *
     * @return
     */
    public String V2_fetchShareOrgUrl() {
        return setupApiWithAccessToken("organizations/%s/share") + "&format=%s&inviter=%s";
    }

    /**
     * 添加或移除星标联系人api
     *
     * @return
     */
    public String V2_addOrRemoveFlagUserUrl() {
        return setupApiWithAccessToken("users/%s/contacts");
    }


    /**
     * 特殊视图检查
     */
    public String getSpecialViewCheckUrl() {
        return setupApiWithAccessToken("users/%s/exist-views");
    }

    //--------------------------------------- 群组相关------------------------------------------------------

    /**
     * 获取群基本信息
     *
     * @return
     */
    public String V2_getDiscussionInfoUrl() {
        return setupApiWithAccessToken("discussions/%s");
    }


    /**
     * 获取请详情接口
     *
     * @return
     */
    public String V2_getDiscussionDetailUrl() {
        return setupApiWithAccessToken("discussions/%s/detail");
    }

    /**
     * 创建群接口
     *
     * @return
     */
    public String V2_createDiscussionUrl() {
        return setupApiWithAccessToken("discussions");
    }

    /**
     * 群成员操作接口
     *
     * @return
     */
    public String V2_discussionMemberOptsUrl() {
        return setupApiWithAccessToken("discussions/%s");
    }

    /**
     * 修改群信息
     *
     * @return
     */
    public String V2_modifyDiscussionUrl() {
        return setupApiWithAccessToken("discussions/%s/profile");
    }

    /**
     * 转移群主
     * */
    public String V2_transferDiscussionOwnerUrl() {
        return setupApiWithAccessToken("discussions/%s");

    }

    /**
     * 退出群
     *
     * @return url
     */
    public String V2_deleteDiscussionUrl() {
        return setupApiWithAccessToken("users/%s/discussions/%s");
    }


    /**
     * 解散群
     *
     * @return url
     */
    public String V2_dismissDiscussionUrl() {
        return appendApiWithAccessToken("discussions/%s?domain_id=%s");
    }

    /**
     * 群模板列表
     */
    public String discussionTemplatesUrl() {
        return setupApiWithAccessToken("discussions/templates");
    }


    /**
     * 删除群模板 feature 信息
     * */
    public String removeDiscussionFeatureUrl() {
        return setupApiWithAccessToken("discussions/%s/features/%s");
    }

    /**
     * 设置群模板 features 信息
     */
    public String discussionSetTemplateFeaturesUrl() {
        return setupApiWithAccessToken("discussions/%s/features");
    }

    /**
     * 查看模板详情
     * */
    public String discussionTemplateUrl() {
        return setupApiWithAccessToken("discussions/templates/%s");

    }

    /**
     * 设置群模板成员标签
     * */
    public String setDiscussionTagsUrl() {
        return setupApiWithAccessToken("discussions/%s/tags");
    }


    /**
     *  删除群模板成员标签
     * */
    public String removeDiscussionTagUrl() {
        return setupApiWithAccessToken("discussions/%s/tags/%s");
    }

    /**
     * 设置群模板成员标签
     * */
    public String setDiscussionTagMemberMapUrl() {
        return setupApiWithAccessToken("discussions/%s");

    }


    /**
     * 修改群模板信息
     * */
    public String modifyDiscussionTemplateUrl() {
        return setupApiWithAccessToken("discussions/%s/template");
    }


    //---------------------------------------app 相关------------------------------------------------------

    /**
     * 获取组织下所有应用(组织管理员身份)
     * */
    public String getAdminQueryAppsUrl() {
        return appendApiWithAccessToken("organizations/%s/apps?skip=%s&limit=%s&entry_types=access&platforms=android,ios");
    }


    /**
     * 获取用户某个组织下安装的应用列表
     *
     * @return
     */
    public String V2_fetchUserAppsByOrgId() {
        return appendApiWithAccessToken("users/%s/apps/interest-list?org_id=%s&platforms=android,general&access_type=mobile&admin_type=mobile");
    }

    public String V2_checkUserAppUpdateList() {
        return appendApiWithAccessToken("users/%s/apps/upgrade-list?org_id=%s&platforms=android,general&access_type=mobile&admin_type=mobile");
    }

    /**
     * 删除应用
     */
    public String V2_removeAppUrl() {
        return appendApiWithAccessToken("users/%s/apps/followships?action=delete");
    }

    /**
     * 安装应用
     */
    public String V2_installAppUrl() {
        return appendApiWithAccessToken("users/%s/apps/followships?action=add");
    }


    /**
     * 获取应用列表(应用市场接口)
     *
     * https://api4.workplus.io/v1/users/9dab1613e93943538bfdf54620ea50b2/apps
     * ?org_id=c8522121-c038-4de1-8e8e-cb8ab6a6d32f
     * &category_id=
     * &kw=
     * &skip=0
     * &limit=20
     * &platforms=Android,general
     * &access_type=mobile
     * &access_token=55493712187645c3876f6f6a1aa7d6d4
     * */
    public String getQueryAppListInAppStoreUrl() {
        return appendApiWithAccessToken("users/%s/apps?org_id=%s&category_id=%s&kw=%s&skip=%s&limit=%s&platforms=android,general&access_type=mobile");
    }


    //---------------------------------------雇员 相关------------------------------------------------------

    /**
     * 查询用户间的组织交集(orgCode list)
     * */
    public String V2_queryIntersectionOrgCodes() {
        return setupApiWithAccessToken("organizations/colleague-of/%s");
    }

    /**
     * 根据user_id 查询组织以及所属的雇员(根据同事关系而返回结果)
     */
    public String V2_queryOrgAndEmpList() {
        return appendApiWithAccessToken("users/%s/employees?filter_senior=false&filter_rank=%s&rank_view=%s");
    }

    /**
     * 修改雇员
     */
    public String V2_modifyEmployee() {
        return setupApiWithAccessToken("admin/organizations/%s/employees/%s");
    }


    /**
     * 查看雇员信息
     */
    public String V2_queryEmployeeInfoUrl() {
        return setupApiWithAccessToken("organizations/%s/employees/%s");
    }


    /**
     * 查看雇员信息
     * 用户
     */
    public String V2_queryUserEmployeeInfoUrl() {
        return setupApiWithAccessToken("organizations/%s/employees/%s") + "&type=user";
    }


    /**
     * 批量拉取雇员列表 api
     */
    public String V2_fetchEmpListUrl() {
        return appendApiWithAccessToken("organizations/%s/employees?query=%s&matching=true");
    }

    public String V2_fetchEmpIncomingCallUrl() {
        return appendApiWithAccessToken("users/%s/employee-phones?skip=%s&limit=%s&refresh_time=%s");
    }



    //---------------------------------------API V2 组织相关------------------------------------------------------

    /**
     * 查询单个组织
     */
    public String V2_queryOrgByCode() {
        return setupApiWithAccessToken("organizations/%s");
    }


    /***
     * 查询org_id下面的所有人员
     * 不包括子部门
     */
    public String V2_queryEmployeeByOrgIdUrl() {
        return setupApiWithAccessToken("organizations/%s/employees") + "&org_id=%s";
    }

    /**
     * 获取所有组织架构接口
     *
     * @return
     */
    public String V2_fetchOrganizationsUrl() {
        return setupApiWithAccessToken("organizations");
    }


    /**
     * 根据用户获取组织架构
     */
    public String V2_fetchOrganizationsByUserIdUrl() {
        return setupApiWithAccessToken("organizations") + "&user_id=%s";
    }


    /**
     * 根据组织架构code获取该组织视图接口
     *
     * @return
     */
    public String V2_fetchOrganizationViewByCodeUrl() {
        return appendApiWithAccessToken("organizations/%s/view?filter_senior=%s&filter_rank=%s&rank_view=%s&org_limit=%s&org_skip=%s&employee_limit=%s&employee_skip=%s");
    }

    /**
     * 根据组织架构id和机构id获取下级视图
     *
     * @return
     */
    public String V2_fetchOrganizationViewByOrgIdUrl() {
        return appendApiWithAccessToken("organizations/%s/view?org_id=%s&filter_senior=%s&filter_rank=%s&rank_view=%s&org_limit=%s&org_skip=%s&employee_limit=%s&employee_skip=%s");
    }

    /**
     * 查询org_id下面的所有人员(包括子部门) 全部展开~
     * 包括子部门
     */
    public String V2_queryRecursionEmployeeByOrgIdUrl() {
        return appendApiWithAccessToken("organizations/%s/employees?&org_id=%s&recursion=true&filter=true&filter_senior=%s&filter_rank=%s&rank_view=%s&org_limit=%s&org_skip=%s&employee_limit=%s&employee_skip=%s");
    }


    /**
     * 检查用户组织架构更新列表
     *
     * @return
     */
    public String V2_checkOrgsUpdate() {
        return setupApiWithAccessToken("organizations/upgrade-list");
    }

    /**
     * 删除组织
     */
    public String V2_removeOrgUrl() {
        return setupApiWithAccessToken("organizations/%s/leave");
    }

    /**
     * 查看组织申请状态
     */
    public String V2_checkApplyingOrgUrl() {
        return setupApiWithAccessToken("organizations/applications");
    }

    //---------------------------------------API V2 启动页, 广告页相关------------------------------------------------------

    public String V2_checkStartPagePackageUrl() {
        return AtworkConfig.API_URL + "boot-settings?refresh_time=%s&org_id=%s&domain_id=%s";
    }

    public String V2_getStartPagePackageUrl() {
        return AtworkConfig.API_URL + "boot-settings/%s?domain_id=%s&org_id=%s";
    }


    //---------------------------------------API V2 消息相关------------------------------------------------------


    /**
     * 获取消息会话列表􏰀􏰁􏰂􏰃􏰄􏰅􏰆􏰇􏰈􏰉 􏰋􏰌􏰍􏰎􏰏􏰍􏰋􏰐􏰌􏰍􏰎􏰏􏰑􏰒􏰓􏰋􏰔􏰕􏰖􏰗􏰘􏰙􏰚􏰎􏰍􏰛􏰜􏰖􏰔􏰎􏰍􏰜􏰕􏰔􏰝􏰞 􏰐􏰜􏰖􏰔􏰎􏰍􏰜􏰕􏰔􏰝􏰓􏰟􏰗􏰖􏰔􏰖􏰜􏰞􏰠􏰡􏰡􏰟􏰕􏰢􏰢􏰎􏰍􏰍􏰣􏰜􏰙􏰤􏰎􏰥􏰞􏰐􏰕􏰢􏰢􏰎􏰍􏰍􏰣􏰜􏰙􏰤􏰎􏰥􏰀􏰁􏰂􏰃􏰄􏰅􏰆􏰇􏰈􏰉 􏰋􏰌􏰍􏰎􏰏􏰍􏰋􏰐􏰌􏰍􏰎􏰏􏰑􏰒􏰓􏰋􏰔􏰕􏰖􏰗􏰘􏰙􏰚􏰎􏰍􏰛􏰜􏰖􏰔􏰎􏰍􏰜􏰕􏰔􏰝􏰞 􏰐􏰜􏰖􏰔􏰎􏰍􏰜􏰕􏰔􏰝􏰓􏰟􏰗􏰖􏰔􏰖􏰜􏰞􏰠􏰡􏰡􏰟􏰕􏰢􏰢􏰎􏰍􏰍􏰣􏰜􏰙􏰤􏰎􏰥􏰞􏰐􏰕􏰢􏰢􏰎􏰍􏰍􏰣􏰜
     * */
    public String querySessionListUrl() {
        return appendApiWithAccessToken("users/%s/mailboxes?timestamp=%s&limit=%s");
    }

    /**
     * 按指定会话拉取消息
     * */
    public String queryMessagesOnSessionUrl() {
        return appendApiWithAccessToken("users/%s/mailboxes/%s?begin=%s&end=%s&limit=%s&reversed=true");
    }

    /**
     * amr 转 mp3, 返回媒体流
     * https://jc-api-int.bba-app.com/api/v1/medias/5a3a75f5-a270-40e2-9f35-09cea4b42431/voice?access_token=99679e96cbc84a2593367567e74a6a2e
     * */
    public String getMp3Voice() {
        return setupApiWithAccessToken("medias/%s/voice");
    }


    /**
     * 下拉获取消息和漫游消息, 必应消息通过指定 bingId 拉取所有相关消息
     *
     * @param includeType
     * @param excludeType
     * @return
     */
    public String V2_queryRoamingMessages(String includeType, String excludeType, String sortOrder, long begin, long end) {
        StringBuilder sb = new StringBuilder();
        sb.append("users/%s/mbox?participant_domain=%s&participant_type=%s&participant_id=%s&limit=%s");
        if (begin != -1) {
            sb.append("&begin=").append(begin);
        }
        if (end != -1) {
            sb.append("&end=").append(end);
        }
        if (!TextUtils.isEmpty(includeType)) {
            sb.append("&include_types=").append(includeType);
        }
        if (!TextUtils.isEmpty(excludeType)) {
            sb.append("&exclude_types=").append(excludeType);
        }
        if (!TextUtils.isEmpty(sortOrder)) {
            sb.append("&order=").append(sortOrder);
        }

        return appendApiWithAccessToken(sb.toString());
    }

    /**
     * 查看阅后即焚消息的权限
     * */
    public String V2_queryBurnMessageAuth() {
        return AtworkConfig.API_URL + "users/%s/messages/%s?access_token=%s";
    }


    /**
     * 拉取离线接口
     *
     */
    public String V2_queryOfflineMessages() {
        return appendApiWithAccessToken("users/%s/mboxes?begin=%s&limit=%s");
    }


    /**
     *
     * 服务号历史消息接口
     * apps/mbox/{orgCode}/{appId}/serve-messages?access_token=XX&skip=XX&limit=XX
     * */
    public String getQueryMessageHistoryUrl() {
        return appendApiWithAccessToken("apps/mbox/%s/%s/serve-messages?skip=%s&limit=%s");
    }

    /**
     * 服务号标签接口
     */
    public String queryMessageTagsUrl() {
        return setupApiWithAccessToken("apps/mbox/%s/%s/serve-tags");
    }

    /**
     * 紧急呼消息确认
     * */
    public String getEmergencyMsgConfirmUrl() {
        return  setupApiWithAccessToken("apps/mbox/%s/confirm");
    }


    public String V2_queryDiscussionReadUnread() {
        return AtworkConfig.API_URL + "users/%s/receipts/%s?access_token=%s&status=";
    }


    //---------------------------------------设置相关------------------------------------------------------



    /**
     * 会话设置(消息勿扰, 置顶等)
     * @return
     */
    public String setConversationSetting() {
        return appendApiWithAccessToken("users/%s/conversations?type=%s");
    }

    /**
     * 群相关设置
     * */
    public String setDiscussionSettings() {
        return setupApiWithAccessToken("discussions/%s/settings");
    }

    public String getUserConversations() {
        return appendApiWithAccessToken("users/%s/conversations?client_id=%s&domain_id=%s&type=%s");
    }

    public String getDiscussionConversations() {
        return setupApiWithAccessToken("discussions/%s");
    }


    /**
     * 批量获取会话设置
     * */
    public String getConversationsSetting() {
        return setupApiWithAccessToken("users/%s/conversation-settings");

    }


    /**
     * 获取用户设置(群聊助手等)
     * */
    public String userSettings() {
        return setupApiWithAccessToken("users/%s/settings");
    }




    //---------------------------------------API V2 媒体类相关------------------------------------------------------

    /**
     * 上传文件的方法
     */
    public String V2_uploadFileMedia() {
        return appendApiMediaWithAccessToken("medias/?file_digest=%s&file_size=%s&expire_time=%s&domain_id=%s");
    }



    /**
     * 上传图片
     */
    public String V2_uploadImageMedia() {
        return appendApiMediaWithAccessToken("medias/images?file_digest=%s&file_size=%s&expire_time=%s&domain_id=%s");

    }

    /**
     * 上传原图的接口, 后台做压缩处理
     * */
    public String V2_uploadImageMediaAndCompress() {
        return appendApiMediaWithAccessToken("medias/compress?thumb=true&original=true&digest=%s&expire_time=%s&domain_id=%s");
    }

    /**
     * 上传原图的接口, 后台做压缩处理
     * */
    public String V2_getCompressMediaInfo() {
        return appendApiMediaWithAccessToken("medias/compress?thumb=true&original=true&digest=%s&domain_id=%s");
    }



    /**
     * 检查媒体, fileId or fileDigest
     */
    public String V2_checkFileMedia() {
        return appendApiMediaWithAccessToken("medias/%s/info?type=%s&domain_id=%s");
    }

    public String V2_checkFileDigest() {
        return appendApiMediaWithAccessToken("medias/%s?type=digest&file_size=%s&expire_time=%s&domain_id=%s");
    }

    public String V2_getDownloadUrl() {
        return V2_getDownloadUrl(false);
    }


    public String V2_getDownloadUrl(boolean considerCdn) {
        String api = "medias/%s?domain_id=" + AtworkConfig.DOMAIN_ID;

        if(considerCdn && AtworkConfig.CDN_CONFIG.getEnable()) {
            return appendBasicApiWithAccessToken(verifyApiUrl(AtworkConfig.CDN_CONFIG.getMediaUrl()), api);
        }

        return appendApiMediaWithAccessToken(api);
    }

    private String appendBasicApiWithAccessToken(String apiPrefix, String api) {
        StringBuilder sb = new StringBuilder();

        sb.append(apiPrefix);
        sb.append(api);
        sb.append("&access_token=%s");
        return sb.toString();
    }

    public String V2_getCompressImageUrl() {
        return appendApiMediaWithAccessToken("medias/%s?quality=%s&width=%s&height=%s&domain_id=" + AtworkConfig.DOMAIN_ID);
    }


    /**
     * 媒体续期接口
     * */
    public String linkMedia() {
        return appendApiMediaWithAccessToken("medias/%s/link?type=id&domain_id=%s");
    }

    /**
     * 批量媒体续期接口
     * @return
     */
    public String linkMedias() {
        return setupApiMediaWithAccessToken("medias/links");
    }


    /**
     * office 在线预览地址
     * */
    public String officePreviewUrl() {
        return "https://view.officeapps.live.com/op/view.aspx?src=%s";
    }

    //---------------------------------------API V2 二维码功能 相关 ------------------------------------------------------

    /**
     * 生成群分享二维码
     */
    public String V2_getDiscussionQrcode() {
        return appendApiWithAccessToken("discussions/%s/share?domain_id=%s&inviter=%s&format=image");
    }

    /**
     * 根据 qrcode pin key 获取扫描后的行为
     */
    public String V2_getInfoFromQrPinId() {
        return appendApiWithAccessToken("pins?id=%s&addresser=%s");
    }


    /**
     * 获取服务号菜单URL
     */
    public String V2_getGetMenuUrl() {
        return appendApiWithAccessToken("users/%s/apps/menus?app_id=%s");
    }

    /**
     * 分享接口
     * format URL 返回url地址  IMAGE 返回图片 OCTET 流
     *
     * @return
     */
    public String V2_shareWorkplus() {
        return appendApiWithAccessToken("workplus/share?format=%s");
    }

    public String V2_shareWorkplusQRImageNew() {
        return  setupApiWithAccessToken("releases/%s/shares");
    }



    public String V2_shareChatOrgInviteUrl() {
        return "file:///android_asset/www/register/index.html?/#/application-detail?orgcode=%s&orgname=%s&orgavatar=%s&from=chat&domainid=%s&lang=%s";
    }

    /**
     * 二维码扫码登录
     *
     * @return
     */
    public String V2_QrLogin() {
        return setupApiWithAccessToken("qr-token");
    }


    //---------------------------------------API V2 voip 相关------------------------------------------------------


    /**
     * api/_v1/{domain_id}/meetings/{meeting_id}?provider={zoom/bizconf}
     *
     * 查询zoom 产商的会议状态
     * */
    public String getQueryZoomTypeMeetingStatusUrl() {
        String basicApi = verify_ApiUrl();
        return basicApi + "%s/meetings/%s?provider=%s";
    }


    /**
     * 发起 voip 会议
     * */
    public String V2_startMeeting() {
        return setupApiWithAccessToken("meetings");
    }

    /**
     *  voip 会议 restful 接口
     * */
    public String V2_handleMeeting() {
        return setupApiWithAccessToken("meetings/%s");
    }

    /**
     * 查询 voip 会议
     * */
    public String V2_queryMeeting() {
        return setupApiWithAccessToken("meetings/%s");
    }

    //---------------------------------------API 轻应用 URL 相关 ------------------------------------------------------

    /**
     * 同事圈 url
     */
    public String getColleagueCircleUrl(String orgId) {
        return "file:///android_asset/www/colleague-circle/main/index.html?orgId=" + orgId;
    }

    /**
     * 分享到同事圈的url
     */
    public String getColleagueCircleShareUrl() {
        return "local://colleague-circle/main/index_share.html#views/publish.html?summary={%s}&orgId={%s}&type={share}&icon={%s}&url={%s}&forwardMode={%s}";
    }

    /**
     * 使用2.0的同事圈的打包需要注意, 规则上需要用旧版本的规则
     * */
    public String getColleagueCircleShareUrlV2() {
        return "local://colleague-circle/main/index_share.html#views/publish.html?url=%s&icon=%s&summary=%s";
    }


    /**
     * 应用市场 url
     */
    public String getAppStoreUrl() {
        return "file:///android_asset/www/appstore/index.html?orgid=%s&userid=%s&isIsv=%s";
    }


    /**
     * 应用修改界面(管理员)
     * */
    public String getAdminAppModifyUrl() {
        return "file:///android_asset/www/appstore/index_authority.html?userid=%s&orgid=%s&appid=%s";
    }

    public String getRegisterBasicUrl() {
        return "file:///android_asset/www/register/index.html?/#/";
    }

    public String getFriendBasicUrl() {
        return "file:///android_asset/www/friend/index.html?/#/";
    }

    public String getOrgBasicUrl() {
        return "file:///android_asset/www/organization/index.html?/#/";
    }

    /**
     * 新建组织 url
     */
    public String getNewOrgUrl() {
        return getRegisterBasicUrl() + "add-application";
    }

    /**
     * 搜索组织 url
     */
    public String getSearchOrgsUrl() {
        return getRegisterBasicUrl() + "application-search";
    }


    /**
     * 申请组织 url
     */
    public String getApplyOrgsUrl() {
        return getOrgBasicUrl() + "approval?orgcode=%s&domain_id=%s";
    }

    /**
     * 组织管理 url
     */
    public String getOrgManagerUrl() {
        return getOrgBasicUrl() + "management?orgcode=%s";
    }

    /**
     * 组织二维码 url
     */
    public String getOrgQrcodeUrl() {
        return getOrgBasicUrl() + "qrcode?orgcode=%s&props=%s&caniswitch=%s";
    }

    /**
     * 注册相关 url
     */
    public String getRegisterUrl() {
        return getRegisterBasicUrl() + "register?type=%s";
    }

    /**
     * 编辑人员的接口(方舟)
     * */
    public String getFangzhouUserInfoEditUrl() {
        return getRegisterBasicUrl() + "h3c-account-msg?type=edit";
    }

    /**
     * 搜索好友 url
     */
    public String getSearchFriendsUrl() {
        return getFriendBasicUrl() + "search-results";
    }


    /**
     * 好友邀请 url
     */
    public String getContactInviteUrl() {
        return getFriendBasicUrl() + "contact-list";
    }

    /**
     * 好友审批 url(来自 main)
     */
    public String getFriendApprovalFromMain() {
        return getFriendBasicUrl() + "approval?from=main";
    }

    /**
     * 好友审批 url
     */
    public String getFriendApproval() {
        return getFriendBasicUrl() + "approval";
    }

    /**
     * 新增好友 url
     */
    public String getNewFriendUrl() {
        return getFriendBasicUrl() + "send?userid=%s&domainid=%s";
    }

    public String getUserOnlineStatus() {
        return setupApiWithAccessToken("users/online-list");
    }

    //-------------------------------- 网盘 -----------------------------------

    /**
     * 网盘所有记录url
     * @return
     */
    public String getDropboxBySource() {
        return appendApiWithAccessToken("domains/%s/%s/%s/pan?unlimited=false&refresh_time=%s&limit=%s&skip=%s");
    }

    /**
     * 根据查询条件获取网盘数据
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
     * @return
     */
    public String getDropboxByParams(String url, String parent, String keyword, String fileType, String skip,
                                     String limit, String sort, String order, boolean unlimited,
                                     String refreshTime, String ownerId, String ownerDomainId, String accessToken) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(AtworkConfig.API_URL);
        stringBuilder.append(url);
        String requestUrl = stringBuilder.toString();
        if (!TextUtils.isEmpty(parent)) {
            requestUrl = requestUrl + "parent="+parent+"&";
        }

        if (!TextUtils.isEmpty(keyword)) {
            String encodeKey = "";
            try {
                encodeKey = URLEncoder.encode(keyword, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            requestUrl = requestUrl + "kw="+encodeKey+"&";
        }
        if (!TextUtils.isEmpty(fileType)) {
            requestUrl = requestUrl + "file_type="+fileType+"&";
        }
        if (!TextUtils.isEmpty(skip)) {
            requestUrl = requestUrl + "skip="+skip+"&";
        }
        if (!TextUtils.isEmpty(limit)){
            requestUrl = requestUrl + "limit="+limit+"&";
        }
        if (!TextUtils.isEmpty(sort)) {
            requestUrl = requestUrl + "sort="+sort+"&";
        }
        if (!TextUtils.isEmpty(order)) {
            requestUrl = requestUrl + "order="+order+"&";
        }
        if (!TextUtils.isEmpty(ownerId) && !TextUtils.isEmpty(ownerDomainId)) {
            requestUrl = requestUrl + "owner_id="+ownerId+"&";
            requestUrl = requestUrl + "owner_domain_id="+ ownerDomainId + "&";
        }
        requestUrl = requestUrl + "unlimited="+unlimited+"&";
        requestUrl = requestUrl + "refresh_time="+refreshTime+"&";
        requestUrl = requestUrl + "&access_token="+accessToken;
        return requestUrl;
    }

    /**
     * 创建文件或文件夹 0 文件， 1 文件夹
     * @param isDir
     * @return
     */
    public String makeDropboxFileOrDir(int isDir) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getDropboxUrlByOps("create"));
        stringBuilder.append("&is_dir=").append(isDir);
        return stringBuilder.toString();
    }

    /**
     * 移动文件或文件夹
     * @return
     */
    public String moveDropbox() {
        return getDropboxUrlByOps("move");
    }

    /**
     * 复制文件或文件夹
     * @return
     */
    public String copyDropbox() {
        return getDropboxUrlByOps("copy");
    }

    /**
     * 移除文件或文件夹
     * @return
     */
    public String removeDropbox() {
        return getDropboxUrlByOps("remove");
    }

    /**
     * 修改名字
     * @return
     */
    public String renameDropbox() {
        return appendApiWithAccessToken("domains/%s/%s/%s/pan/%s?ops=rename");
    }

    /**
     * 设置组织网盘的只读权限
     * @return
     */
    public String setRealOnlySetting() {
        return getDropboxUrlByOps("chmod");
    }

    /**
     * 分享网盘
     */
    public String shareDropbox() {
        return getDropboxUrlByOps("share");
    }

    private String getDropboxUrlByOps(String ops) {
        StringBuilder stringBuilder = new StringBuilder("domains/%s/%s/%s/pan?ops=");
        return appendApiWithAccessToken(stringBuilder.append(ops).toString());
    }

    /**
     * 网盘上传接口
     * */
    public String dropboxUploadMediaFile() {
        return appendApiWithAccessToken("domains/%s/%s/%s/pan/%s?file_size=%s&expired_time=%s");
    }

    public String dropboxMediaFile() {
        return setupApiWithAccessToken("domains/%s/%s/%s/pan/%s");
    }

    public String translateFile() {
        return appendApiWithAccessToken("medias/%s/translate?source_type=%s&dest_type=jpg&skip=%s&limit=%s");
    }

    public String getDropboxInfo() {
        return setupApiWithAccessToken("domains/%s/%s/%s/pan/%s/info");
    }

    public String getDropboxShareItems(ShareItemRequester requester) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(AtworkConfig.API_URL);
        stringBuilder.append("volumes/share-items?");
        if (!TextUtils.isEmpty(requester.mItemId)) {
            stringBuilder.append("item_id=").append(requester.mItemId).append("&");
        }

        if (!TextUtils.isEmpty(requester.mKw)) {
            String encodeKey = "";
            try {
                encodeKey = URLEncoder.encode(requester.mKw, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            stringBuilder.append("kw=").append(encodeKey).append("&");
        }
        stringBuilder.append("skip=").append(requester.mSkip).append("&");
        stringBuilder.append("limit=").append(requester.mLimit).append("&");
        stringBuilder.append("access_token=%s");
        return stringBuilder.toString();
    }

    //---------------------------------------API V2 用户登录协议相关------------------------------------------------------
    public String getUserLoginAgreement() {
        return setupApiWithAccessToken("domains/%s/user-agreement");
    }

    public String signUserLoginAgreement() {
        return setupApiWithAccessToken("users/%s/user-agreement");
    }



    public String getLoginSecureCode() {
        return verifyApiUrl() + "secure-captchas?bits=4&survival_seconds=300&addresser=%s&recipient=%s&timestamp=%s&width=180&height=46";
    }

    /**
     * 解析url地址成分享
     * @return
     */
    public String parseUrlForShare() {
        return setupApiWithAccessToken("translators");
    }



    //------------------------------------新版广告页-------------------------------------
    /**
     * 获取广告数据(boot 类型)
     * @return
     */
    public String getBootAdvertisements() {
        return appendApiWithAccessToken("advertisements?org_id=%s");
    }

    /**
     * 获取广告数据(可通过 kind 选择类型)
     * @return
     */
    public String getAdvertisements() {
        return appendApiWithAccessToken("advertisements?org_id=%s&kind=%s");
    }



    /**
     * 广告统计日志
     */
    public String postAdvertisementsEvent() {
        return setupApiWithAccessToken("advertisements/logs");
    }


    //---------------------------------------API V2 必应消息相关------------------------------------------------------

    /**
     * 发出必应消息
     * */
    public String getPostBingUrl() {
        return setupApiWithAccessToken("mboxes/bing");
    }

    /**
     * 操作必应消息(确认, 星标, 取消星标)
     * */
    public String getHandleBingUrl() {
        return setupApiWithAccessToken("mboxes/bing/%s");
    }


    /**
     * 分批加载更多的必应消息
     * */
    public String getLoadMoreBingUrl() {
         return appendApiWithAccessToken("mboxes/bing?end=%s&limit=%s&order=desc");
    }

    //-----------------------------------全时云日历获取token接口-------------------------
    public String getUCCalendarToken() {
        return appendApiWithAccessToken("meeting-users?domain_id=%s&gateway=QUAN_SHI&users=%s");
    }

    /**
     * wifi自动打卡
     * */
    public String getWifiAutoPunch() {

        if(!StringUtils.isEmpty(DomainSettingsManager.getInstance().getCheckinBasicApiUrl())) {
            return UrlHandleHelper.addPathInfo(DomainSettingsManager.getInstance().getCheckinBasicApiUrl(), "checkin");
        }

        String checkInUrl = BeeWorks.getInstance().config.checkInUrl;
        if(!StringUtils.isEmpty(checkInUrl)) {
            return checkInUrl + "mobile/checkin";
        }

        return "http://daka.workapps.io/attendapi/mobile/checkin";
    }

    public String getOutFieldIntervalPunch() {

        if(!StringUtils.isEmpty(DomainSettingsManager.getInstance().getCheckinBasicApiUrl())) {
            return UrlHandleHelper.addPathInfo(DomainSettingsManager.getInstance().getCheckinBasicApiUrl(), "signin");
        }

        String checkInUrl = BeeWorks.getInstance().config.checkInUrl;
        if(!StringUtils.isEmpty(checkInUrl)) {
            return checkInUrl + "mobile/signin";
        }

        return "http://daka.workapps.io/attendapi/mobile/signin";
    }


    //----------------------------------- 钱包 -------------------------

    /**
     * 查询帐号信息
     * */
    public String getQueryWalletAccount() {
        return setupApiWithAccessToken("assets/%s/accounts");
    }


    /**
     * 钱包绑定, 发送短信验证码
     * */
    public String getSendWalletMobileSecureCodeUrl() {
        return setupApiWithAccessToken("assets/%s/accounts/%s/secure-codes");
    }

    /**
     * 验证短信验证码
     * */
    public String getVerifyWalletMobileSecureCodeUrl() {
        return setupApiWithAccessToken("assets/%s/accounts/%s/secure-codes/verify");
    }

    /**
     * 初次设置支付密码或者强制修改密码
     * */
    public String getForcedSetPayPasswordUrl() {
        return setupApiWithAccessToken("assets/%s/accounts/%s/force-credentials");
    }

    /**
     * 修改支付密码
     * */
    public String getSetPayPasswordUrl() {
        return setupApiWithAccessToken("assets/%s/accounts/%s/credentials");
    }

    /**
     * 修改绑定手机
     * */
    public String getModifyWalletMobileUrl() {
        return setupApiWithAccessToken("assets/%s/accounts/%s/reset-phone");
    }


    /**
     * 发红包
     * */
    public String getGiveRedEnvelopeUrl() {
        return setupApiWithAccessToken("assets/%s/red-envelops");
    }

    /**
     * 群发红包
     * */
    public String getMultiDiscussionGiveRedEnvelopeUrl() {
        return setupApiWithAccessToken("assets/%s/red-envelops/batch");
    }

    /**
     * 查看红包领取详情
     */
    public String getQueryRedEnvelopeGainDetailUrl() {
        return setupApiWithAccessToken("assets/%s/red-envelops/%s/detail");
    }

    /**
     * 抢红包
     * */
    public String getGrabRedEnvelopeUrl() {
        return setupApiWithAccessToken("assets/%s/red-envelops/%s");
    }


    /**
     * 转账
     * */
    public String getMakeTransactionRequestUrl() {
        return setupApiWithAccessToken("assets/%s/transactions");
    }


    /**
     * 获取表情专辑更新列表
     * @return
     */
    public String checkStickerAlbumsUrl() {
        return setupApiWithAccessToken("sticker-albums");
    }

    /**
     * 获取表情专辑详情
     */
    public String getStickerAlbumsDetailUrl() {
        return setupApiWithAccessToken("sticker-albums/%s");
    }

    /**
     * 下载表情专辑zip包
     */
    public String getStickerAlbumZipUrl() {
        return setupApiWithAccessToken("sticker-albums/%s/stickers");
    }

    /**
     * 下载具体表情
     */
    public String getStickerImageUrl() {
        return appendApiWithAccessToken("sticker-albums/%s/stickers/%s?type=%s");
    }




    //----------------------------------- 用户行为分析 -------------------------

    /**
     * 新增日志接口
     * */
    public String getBehaviorLogUrl() {
        return setupApiWithAccessToken("behavior-logs");
    }



    //----------------------------------- 渠道管理 -------------------------

    public String clientReleaseLogsUrl() {
        return setupApiWithAccessToken("client-release-logs");
    }


    //----------------------------------- 安全相关 -------------------------

    public String getApkVerifyInfoUrl() {
        return AtworkConfig.API_URL + "public/release/%s?domain_id=" + AtworkConfig.DOMAIN_ID + "&profile=" + AtworkConfig.PROFILE + "&type=md5";
    }


    //----------------------------------- 工作台相关 -------------------------
    /**
     * 查询工作台卡片信息
     * */
    public String getQueryWorkbenchUrl() {
        return appendApiWithAccessToken("organizations/%s/workbench?refresh_time=%s");
    }


    /**
     * 查询工作台列表(管理员)
     * */
    public String getQueryWorkbenchListUrl() {
        return appendApiWithAccessToken("organizations/%s/widget-views?skip=%s&limit=%s&primary=%s");

    }

    /**
     * 新增工作台(管理员)
     * */
    public String getAddWorkbenchUrl() {
        return setupApiWithAccessToken("organizations/%s/widget-views");
    }


    /**
     * 修改工作台(管理员)
     * */
    public String getModifyWorkbenchUrl() {
        return setupApiWithAccessToken("organizations/%s/widget-views/%s");

    }

    /**
     * 修改工作台定义(管理员)
     * */
    public String getModifyWorkbenchDefinitionUrl() {
        return setupApiWithAccessToken("organizations/%s/widget-views/%s/definitions");

    }

    /**
     * 新增卡片(管理员)
     * */
    public String getAddCardUrl() {
        return setupApiWithAccessToken("organizations/%s/widgets");

    }

    /**
     * 修改卡片(管理员)
     * */
    public String getModifyCardUrl() {
        return setupApiWithAccessToken("organizations/%s/widgets/%s");
    }

    /**
     * 工作台卡片信息(管理员)
     * */
    public String getAdminQueryWorkbenchUrl() {
        return setupApiWithAccessToken("organizations/%s/widget-views/%s");
    }


    /**
     * 增加工作台banner卡片 banner数据
     * */
    public String getAdminAddBannerItemUrl() {
        return appendApiWithAccessToken("organizations/%s/ads?owner_id=%s");
    }

    /**
     * 查询工作台banner卡片 banner列表
     * */
    public String getAdminQueryBannerListUrl() {
        return appendApiWithAccessToken("organizations/%s/ads?owner_id=%s&skip=%s&limit=%s&catalog=work_bench");

    }

    /**
     * 删除工作台banner卡片 banner数据
     * */
    public String getAdminDeleteBannerItemUrl() {
        return appendApiWithAccessToken("organizations/%s/ads/%s?owner_id=%s");
    }


    /**
     * 上下架 工作台banner卡片 banner数据
     *
     * api/organizations/a9a4d18231a14c3f974dcec0624b4b06/ads/764ceda3c0b74178806e7bd46dcd5f48/disable?access_token=f2d406b1d17d4a9e9cda109101f36015&owner_id=46
     * api/organizations/a9a4d18231a14c3f974dcec0624b4b06/ads/764ceda3c0b74178806e7bd46dcd5f48/enable?access_token=f2d406b1d17d4a9e9cda109101f36015&owner_id=46
     * */
    public String getAdminPutAwayBannerItemUrl() {
        return appendApiWithAccessToken("organizations/%s/ads/%s/%s?owner_id=%s");
    }



    //----------------------------------- 设备相关 -------------------------

    /**
     * 登录设备操作相关接口
     * */
    public String getHandleLoginDevicesUrl() {
        return setupApiWithAccessToken("users/%s/devices");
    }

    //-----------------------------ocr ---------------------------

    public String getOctUrl() {
        return setupApiWithAccessToken("connectors/ocr");
    }





    /**
     * 暂时能够获取文件下载白名单信息
     * */
    public String getCustomizationInfoUrl() {
        return setupApiWithAccessToken("domains/%s/customizations");
    }

    public String getEditFangZhouEditProfileUrl() {
        return AtworkConfig.API_URL + "#/h3c-account-msg?type=edit";
    }

    public String postH3cEmailEvent() {
        return setupApiWithAccessToken("h3c-logs");
    }

    public String getOctQrResultUrl(String code) {
        return AtworkConfig.OCT_CONFIG.getOctQrEpassUrl() + code;
    }

//-----------------------------收藏-------------------------------

    /**
     * 获取收藏标签
     */
    public String getFavoritesTagsUrl() {
        return setupApiWithAccessToken("users/%s/favorites-tags");
    }

    /**
     * 获取搜藏列表
     * @return
     */
    public String getFavoritesUrl() {
        return appendApiWithAccessToken("users/%s/favorites?skip=%s&limit=%s&refresh_time=%s");
    }

    /**
     * 新增收藏
     * @return
     */
    public String postFavoritesUrl() {
        return setupApiWithAccessToken("users/%s/favorites");
    }

    public String modifyFavoritesUrl() {
        return setupApiWithAccessToken("users/%s/favorites/%s");
    }


    /**
     * 取消收藏
     * @return
     */
    public String deleteFavoritesUrl() {
        return setupApiWithAccessToken("users/%s/favorites/multi-delete");
    }

    /**
     * 收藏汇总
     * @return
     */
    public String getFavoriteUsage() {
        return setupApiWithAccessToken("users/%s/favorite-usage");
    }


    //-----------------------------  人脸识别  -------------------------------

//    public String getFaceBioTokenUrl() {
//        return setupApi("faceAuth/tokens/%s/%s/%s");
//    }

    public String verifyFaceBioUrl() {
        return setupApi("faceAuth/verify/%s/%s/%s");
    }

    //人脸识别登录
    public String loginByFaceBioUrl() {
        return setupApi("token");
    }

    //设置底片url
    public String setFaceBioFilmUrl() {
        return setupApiWithAccessToken("users/%s/face-id");
    }

    //验证人脸
    public String verifyFaceBioAuthUrl() {
        return setupApiWithAccessToken("users/%s/auth-face-id");
    }

    //查询⽤用户是否开启⼈人脸功能
    public String isFaceBioAuthOpenUrl() {
        return setupApi("face-id?domain_id=%s&username=%s");
    }

    // 获取⼈脸识别提供商token(用于登录)
    public String getFaceBioTokenUrl() {
        return setupApi("face-token?domain_id=%s&username=%s");
    }

    //获取⼈脸识别提供商token(登录后)
    public String getAuthorizedFaceBioTokenUrl() {
        return setupApiWithAccessToken("users/%s/face-token");
    }

    //开启⼈人脸验证
    public String enableFaceIdAuthUrl() {
        return setupApiWithAccessToken("users/%s/enable-face-id");
    }

    //关闭人脸认证
    public String disableFaceIdAuthUrl() {
        return setupApiWithAccessToken("users/%s/disable-face-id");
    }

    /**
     * 获取协议url
     * @return
     */
    public String getFaceBioAgreementUrl() {
        return appendApiWithAccessToken("domains/%s/user-agreement?type=biological_auth");
    }

    /**
     * 获取刷脸登录协议
     * @return
     */
    public String getFaceLoginAgreementUrl() {
        return setupApi("face-id-agreement?domain_id=%s&local=%s");
    }

    /**
     * 签订刷脸登录协议
     * @return
     */
    public String signFaceLoginAgreementUrl() {
        return setupApi("face-id-agreement?ticket_id=%s");
    }


    /**
     * 签订协议url
     * @return
     */
    public String signFaceBioAgreementUrl() {
        return setupApiWithAccessToken("users/%s/agreements");
    }

    /**
     * 验证 ticket
     * */
    public String checkFaceBioTicketUrl() {
        return setupApi("aliyun/tickets/%s");

    }

    /**
     * 开启关闭人脸识别
     * */
    public String switchFaceCloudAuthUrl() {
        return setupApiWithAccessToken("users/%s/cloud-authAvatar");
    }


    /**
     * 设置/更新人脸识别底片和开关
     * */
    public String toggleFaceBioFilm() {
        return setupApiWithAccessToken("users/%s/cloud-auth-avatar");
    }

    /**智能机器人语音指令接口*/
    public String getRobotInstructionsUrl() {
        return setupApi("domain-instructions?access_token=%s&skip=%s" +
                "&limit=%s&refresh_time=%s");
    }


    public String getStaticMapUrl() {
        return "https://restapi.amap.com/v3/staticmap?markers=-1,https://webapi.amap.com/theme/v1.3/markers/b/mark_bs.png,0:%s&zoom=18&size=400*200&key=%s";
    }

    public String getProtocolPrivacyUrl() {
        return "file:///android_asset/www/protocol/privacy.html";
    }

    public String getProtocolProtocolUrl() {
        return "file:///android_asset/www/protocol/protocol.html";
    }


    public String getTicketUrl() {
        return setupApiWithAccessToken("tickets");
    }



    public String setupApi(String api) {
        return doSetupApi(api).toString();
    }


    @NotNull
    private StringBuilder doSetupApiMedia(String api) {
        StringBuilder sb = new StringBuilder();
        sb.append(verifyApiMediaUrl());
        sb.append(api);
        return sb;
    }

    @NotNull
    private StringBuilder doSetupApi(String api) {
        StringBuilder sb = new StringBuilder();
        sb.append(verifyApiUrl());
        sb.append(api);
        return sb;
    }

    /**
     * 这个方法获取的api地址需要拼装accessToken
     *
     * @param api
     * @return
     */
    public String setupApiWithAccessToken(String api) {
        StringBuilder sb = doSetupApi(api);
        sb.append("?access_token=%s");
        return sb.toString();
    }

    /**
     * 拼装accesstoken api， 与上面不同是accessToken拼装语法方面
     *
     * @param api
     * @return
     */
    public String appendApiWithAccessToken(String api) {
        StringBuilder sb = doSetupApi(api);
        sb.append("&access_token=%s");
        return sb.toString();
    }


    private String setupApiMediaWithAccessToken(String api) {
        StringBuilder sb = doSetupApiMedia(api);
        sb.append("?access_token=%s");
        return sb.toString();
    }

    private String appendApiMediaWithAccessToken(String api) {
        StringBuilder sb = doSetupApiMedia(api);
        sb.append("&access_token=%s");
        return sb.toString();
    }

    /**
     * Description:拼接duplicate_removal参数
     * @param api
     * @param flag
     * @return
     */
    public String appendApiWithduplicate_removal(String api, Boolean flag) {
        StringBuilder sb = new StringBuilder();
        sb.append(api);
        sb.append("&duplicate_removal="+flag);
        return sb.toString();
    }


    @NotNull
    private String verify_ApiUrl() {
        String basicApi = verifyApiUrl();
        if(basicApi.endsWith("v1/")) {
            basicApi = basicApi.replaceAll("v1/", "_v1/");
        }
        return basicApi;
    }

    public String verifyApiMediaUrl() {
        return verifyApiUrl(AtworkConfig.getApiMediaUrl());
    }

    public String verifyApiUrl() {
        return verifyApiUrl(AtworkConfig.API_URL);
    }

    private String verifyApiUrl(String api) {
        String url = api;
        url = url.endsWith("/") ? url : url + "/";
        return url;
    }
}
