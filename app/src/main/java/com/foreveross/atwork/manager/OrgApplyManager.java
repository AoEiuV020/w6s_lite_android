package com.foreveross.atwork.manager;

import android.content.Context;

import com.foreverht.db.service.repository.OrgApplyRepository;
import com.foreverht.db.service.repository.UnreadMessageRepository;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.orgization.OrgApply;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.OrgNotifyMessage;
import com.foreveross.atwork.manager.model.ApplyingOrganization;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.service.ChatService;
import com.foreveross.atwork.modules.chat.util.SessionRefreshHelper;
import com.foreveross.atwork.modules.organization.fragment.OrgApplyingFragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * 创建时间：2016年08月16日 下午5:21
 * 创建人：laizihan
 * 类名：OrgApplyManager
 * 用途：
 */
public class OrgApplyManager {

    private static OrgApplyManager sInstance;

    private OrgApplyManager() {

    }

    public static OrgApplyManager getInstance() {
        if (sInstance == null) {
            synchronized (OrgApplyManager.class) {
                if (sInstance == null) {
                    sInstance = new OrgApplyManager();
                }
            }
        }
        return sInstance;
    }


    /**
     * 模型转换
     *
     * @param message
     * @param content
     * @return
     */
    public OrgApply convertModelToOrgApply(OrgNotifyMessage message, String content) {
        OrgApply orgApply = new OrgApply();
        orgApply.mOrgCode = message.mOrgCode;
        orgApply.mMsgId = message.deliveryId;
        orgApply.mLastMsgText = content;
        orgApply.mLastMsgTime = message.deliveryTime;
        return orgApply;
    }


    /**
     * 往org_apply_表插入数据,并发送广播通知Loader
     *
     * @param message 通知信息
     * @param content 包装后的内容
     */
    public void insertAndSendBroadcast(OrgNotifyMessage message, String content) {
        OrgApply orgApply = convertModelToOrgApply(message, content);
        OrgApplyRepository.getInstance().saveOrUpdateOrgApply(orgApply);
        OrgApplyingFragment.refresh();
    }

    public void deleteOrgApplysAsync(List<String> orgCodes) {
        Executors.newSingleThreadExecutor().execute(() -> {
            for (String orgCode : orgCodes) {
                OrgApplyRepository.getInstance().removeOrgApply(orgCode);
            }
        });
    }


    /**
     * 清空未读表的未读通知，并发送广播通知Loader
     *
     * @param context
     * @param applyingOrganization
     */
    public void clearUnreadOrgApply(Context context, ApplyingOrganization applyingOrganization) {
        UnreadMessageRepository.getInstance().removeUnreadOrg(applyingOrganization.mOrgCode);
        Session session = ChatSessionDataWrap.getInstance().getSessionSafely(OrgNotifyMessage.FROM, null);
        if (session != null) {
            session.refreshUnreadSetTotally(UnreadMessageRepository.getInstance().queryUnreadSet(session.identifier), true);
            ChatService.sendSessionReceipts(context, session, new HashSet<>(applyingOrganization.mUnreadMsgIdList));
        }

        OrgApplyingFragment.refresh();
        SessionRefreshHelper.notifyRefreshSessionAndCount();

    }


    public void loadOrg(Context context) {
        OrganizationManager.getInstance().getLocalOrganizations(context, localData -> {
            List<Organization> organizations = (List<Organization>) localData[0];

            OrganizationManager.getInstance().checkOrganizationsUpdate(context, organizations, null);

        });
    }


    public List<String> findUnreadMessagesByOrgCode(String orgCode) {
        return UnreadMessageRepository.getInstance().queryUnreadOrgApplyCountByOrgCode(orgCode);
    }

    public List<String> findUnreadMessagesByOrgCode(List<String> orgCodes) {
        List<String> msgIds = new ArrayList<>();
        for (String orgCode : orgCodes) {
            msgIds.addAll(findUnreadMessagesByOrgCode(orgCode));
        }
        return msgIds;
    }


    public ApplyingOrganization convertModelToApplyOrganization(Organization organization) {
        ApplyingOrganization applyingOrganization = new ApplyingOrganization();
        applyingOrganization.mOrgCode = organization.mOrgCode;
        applyingOrganization.mAppliedTime = -1;
        applyingOrganization.mOrgLogo = organization.mLogo;
        applyingOrganization.mContent = AtworkApplicationLike.getResourceString(R.string.no_applying);
        applyingOrganization.mUnreadMsgIdList = new ArrayList<>();
        applyingOrganization.mOrgName = organization.mName;
        applyingOrganization.mTwOrgName = organization.mTwName;
        applyingOrganization.mEnOrgName = organization.mEnName;
        return applyingOrganization;
    }


}
