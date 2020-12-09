package com.foreveross.atwork.utils.watermark;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.foreverht.cache.WatermarkCache;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.Watermark;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.model.user.LoginUserBasic;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.manager.EmployeeManager;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.watermark.core.DrawWaterMark;
import com.foreveross.watermark.core.WaterMarkUtil;

/**
 * Created by dasunsy on 2017/6/9.
 */

public class WaterMarkHelper {




    /**
     * 设置群组水印
     * */
    public static void setDiscussionWatermark(Context context, View view, String sessionId) {
        setDiscussionWatermark(context, view, sessionId, -1, -1);
    }


    /**
     * 设置群组水印
     * */
    public static void setDiscussionWatermark(Context context, View view, String sessionId, int bgColor, int textColor) {
        view.setVisibility(View.VISIBLE);

        Discussion discussion = DiscussionManager.getInstance().queryDiscussionSync(context, sessionId);
        if (discussion == null) {
            WaterMarkUtil.setLoginUserWatermark(context, view, bgColor, textColor, "");

        } else if (TextUtils.isEmpty(discussion.getOrgCodeCompatible())) {
            WaterMarkUtil.setLoginUserWatermark(context, view, bgColor, textColor, "");

        } else {
            setEmployeeWatermarkByOrgId(context, view, discussion.getOrgCodeCompatible(), bgColor, textColor, -1, -1, 0);
        }
    }


    /**
     * 根据orgId设置雇员水印
     *
     * @param context
     * @param view
     * @param orgId
     */
    //todo 偶尔卡顿的问题 fixed
    public static void setEmployeeWatermarkByOrgId(Context context, View view, String orgId) {
        setEmployeeWatermarkByOrgId(context, view, orgId, -1, -1, -1, -1, 0);
    }

    /**
     * 根据orgId设置雇员水印
     *
     * @param context
     * @param view
     * @param orgId
     */
    //todo 偶尔卡顿的问题 fixed
    public static void setEmployeeWatermarkByOrgId(Context context, View view, String orgId, int bgColor, int textColor, int textSize, int padding, float alpha) {
        LoginUserBasic meUser = LoginUserInfo.getInstance().getLoginUserBasic(context);
        Organization organization = OrganizationManager.getInstance().getOrganizationSyncByOrgCode(context, orgId);
        if (organization != null) {
            orgId = organization.mOrgCode;
        } else {
            Discussion discussion = DiscussionManager.getInstance().queryDiscussionSync(context, orgId);
            if (discussion != null) {
                orgId = discussion.getOrgCodeCompatible();
            }
        }
        Employee employee = EmployeeManager.getInstance().queryEmpInSync(context, meUser.mUserId, orgId);
        //如果无法找到雇员名称，则使用当前登录的用户名
        if (employee == null) {
            WaterMarkUtil.setLoginUserWatermark(context, view, bgColor, textColor, "");
            return;
        }

        DrawWaterMark drawWaterMark = new DrawWaterMark(employee.name, meUser.mUsername, bgColor, textColor, textSize, padding, alpha, "");

        WaterMarkUtil.setWaterMark(context, view, drawWaterMark);

    }


    public static void setWatermark(Context context, View view, String sessionId) {
        setWatermark(context, view, sessionId, -1, -1);
    }

    public static void setWatermark(Context context, View view, String sessionId, int bgColor, int textColor) {
        Session session = ChatSessionDataWrap.getInstance().getSession(sessionId, null);

        if (null != session) {
            view.setVisibility(View.VISIBLE);

            if (SessionType.User.equals(session.type)) {
                if ("show".equalsIgnoreCase(DomainSettingsManager.getInstance().handleUserWatermarkFeature())) {
                    WaterMarkUtil.setLoginUserWatermark(context, view, bgColor, textColor, "");

                }

            } else if (SessionType.Discussion.equals(session.type)) {
                boolean showDiscussionWatermark = false;

                String watermarkFeature = DomainSettingsManager.getInstance().handleDiscussionWatermarkFeature();
                if ("show".equalsIgnoreCase(watermarkFeature)) {
                    showDiscussionWatermark = true;

                } else if ("customer".equalsIgnoreCase(watermarkFeature)) {
                    showDiscussionWatermark = WatermarkCache.getInstance().getWatermarkConfigCache(new Watermark(sessionId, Watermark.Type.DISCUSSION));
                }

                if (showDiscussionWatermark) {
                    setDiscussionWatermark(context, view, sessionId, bgColor, textColor);
                }


            }
        }
    }

    public static boolean isWatermarkEnable(String identifier, SessionType sessionType) {
        if(SessionType.User == sessionType) {
            return "show".equalsIgnoreCase(DomainSettingsManager.getInstance().handleUserWatermarkFeature());

        } else if(SessionType.Discussion == sessionType) {

            String watermarkFeature = DomainSettingsManager.getInstance().handleDiscussionWatermarkFeature();
            if ("show".equalsIgnoreCase(watermarkFeature)) {
                return true;

            } else if ("customer".equalsIgnoreCase(watermarkFeature)) {

                return WatermarkCache.getInstance().getWatermarkConfigCache(new Watermark(identifier, Watermark.Type.DISCUSSION));


            }

            return false;
        }

        return false;
    }
}