package com.foreveross.atwork.modules.contact.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.foreverht.cache.AppCache;
import com.foreverht.cache.DiscussionCache;
import com.foreverht.cache.UserCache;
import com.foreverht.db.service.repository.MessageAppRepository;
import com.foreverht.threadGear.ImageThreadPoolExecutor;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.organization.responseModel.EmployeeResult;
import com.foreveross.atwork.api.sdk.organization.responseModel.OrganizationResult;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.newsSummary.NewsSummaryPostMessage;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.app.manager.AppManager;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.manager.OnlineManager;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.modules.chat.dao.ChatDaoService;
import com.foreveross.atwork.modules.file.service.FileTransferService;
import com.foreveross.atwork.modules.meeting.service.MeetingNoticeService;
import com.foreveross.atwork.modules.newsSummary.util.NewsSummaryHelper;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.AvatarHelper;
import com.foreveross.atwork.utils.ImageCacheHelper;

import java.util.List;
import java.util.UUID;

/**
 * Created by dasunsy on 16/2/22.
 */
public class ContactInfoViewUtil {

    @SuppressLint("StaticFieldLeak")
    public static void dealWithSessionInitializedStatus(ImageView avatarView, TextView nameView, Session session, boolean needLoading) {
        dealWithSessionInitializedStatus(avatarView, nameView, null, session, needLoading);
    }

    @SuppressLint("StaticFieldLeak")
    public static void dealWithSessionInitializedStatus(ImageView avatarView, TextView nameView, TextView signatureView, Session session, boolean needLoading) {

        final String tagHolding = UUID.randomUUID().toString();
        avatarView.setTag(tagHolding);


        if(Session.WORKPLUS_SUMMARY_HELPER.equals(session.identifier)){
            AvatarHelper.setSessionAvatarById(avatarView, session, needLoading, true);
            nameView.setText(BaseApplicationLike.baseContext.getString(R.string.news_summary_title));
            return;
        }

        if(FileTransferService.INSTANCE.checkVariation(avatarView, nameView, session)) {
            return;
        }

        if(SessionType.User.equals(session.type)) {
            if(!Session.COMPONENT_ANNOUNCE_APP.equals(session.identifier)) {
                handleDealWithContactInitializedStatus(tagHolding, avatarView, nameView, signatureView, session, needLoading);

            } else {
                handleDealWithContactInitializedStatus(tagHolding, avatarView, nameView, null, session, needLoading);

            }

            return;
        }


        if(SessionType.LightApp.equals(session.type)
                || SessionType.Service.equals(session.type)
                || SessionType.NativeApp.equals(session.type)
                || SessionType.SystemApp.equals(session.type)) {
            handleDealWithApp(tagHolding, avatarView, nameView, session, needLoading);
            return;
        }


        if(SessionType.Discussion.equals(session.type)) {
            handleDealWithDiscussion(tagHolding, avatarView, nameView, session, needLoading);
            return;
        }

        if(session.isMeetingSession()) {
            MeetingNoticeService.setMeetingNoticeAvatar(session, avatarView);
            nameView.setText(AtworkUtil.getSessionNameI18N(session));
            return;
        }



        AvatarHelper.setSessionAvatarById(avatarView, session, needLoading, true);
        nameView.setText(AtworkUtil.getSessionNameI18N(session));

    }


    private static void handleDealWithApp(String tagHolding, ImageView avatarView, TextView nameView, Session session, boolean needLoading) {

        final Context context = BaseApplicationLike.baseContext;
        App app = AppCache.getInstance().getAppCache(session.identifier);

        if(null != app) {
            handleSetDisplayInfo(avatarView, nameView, session, app, needLoading);

            return;
        }


        //占位处理
        nameView.setText(session.name);
        ImageCacheHelper.setImageResource(avatarView, R.mipmap.default_app);


        new AsyncTask<Void, Void, App>() {
            @Override
            protected App doInBackground(Void... params) {
                App app = AppManager.getInstance().queryAppSync(context, session.identifier, session.orgId);
                return app;
            }

            @Override
            protected void onPostExecute(App app) {

                if(isNeedLoad(avatarView, tagHolding)) {
                    if(null != app) {
                        handleSetDisplayInfo(avatarView, nameView, session, app, needLoading);

                    } else {
                        ImageCacheHelper.setImageResource(avatarView, R.mipmap.default_app);
                    }

                }

            }
        }.executeOnExecutor(ImageThreadPoolExecutor.getInstance());
    }

    public static void handleDealWithAppNewsSummary(TextView nameView, String oldStr, Session session, NewsSummaryHelper.SessionItemColorCallBack callBack) {
        final Context context = BaseApplicationLike.baseContext;
        //占位处理
//        nameView.setText(session.name+oldStr);
        if(!nameView.getText().toString().contains(oldStr)){
            nameView.setText("");
        }

        new AsyncTask<Void, Void, List<NewsSummaryPostMessage>>() {
            @Override
            protected List<NewsSummaryPostMessage> doInBackground(Void... params) {
                List<NewsSummaryPostMessage> newsSummaryPostMessageList = MessageAppRepository.getInstance().queryMessagesByMsgId(context,session.lastMessageId);
                return newsSummaryPostMessageList;
            }

            @Override
            protected void onPostExecute(List<NewsSummaryPostMessage> dataList) {
                String textStr;
                if(!dataList.isEmpty()) {
                    textStr = dataList.get(0).getChatPostMessage().mDisplayName;
                }else {
                    textStr = session.name;
                }
                //若最后为语音, 而且有未读时, 则改变颜色
                if (Session.ShowType.Audio.equals(session.lastMessageShowType)) {
                    callBack.onResult(true,textStr);
                }else {
                    callBack.onResult(false,textStr);
                }

            }
        }.executeOnExecutor(ImageThreadPoolExecutor.getInstance());
    }

    private static void handleSetDisplayInfo(ImageView avatarView, TextView nameView, Session session, App app, boolean needLoading) {
        Context context = BaseApplicationLike.baseContext;
        nameView.setText(app.getTitleI18n(context));
        AvatarHelper.setAppAvatarByAvaId(avatarView, app.getAvatar(), needLoading, true);
        if(!app.getTitleI18n(context).equals(session.name)) {
            session.name = app.getTitleI18n(context);
            ChatDaoService.getInstance().sessionRefresh(session);

        }
    }


    private static void handleDealWithDiscussion(String tagHolding, ImageView avatarView, TextView nameView, Session session, boolean needLoading) {


        final Context context = BaseApplicationLike.baseContext;
        Discussion discussion = DiscussionCache.getInstance().getDiscussionCache(session.identifier);

        if(null != discussion) {
            handleSetDisplayInfo(avatarView, nameView, session, discussion, needLoading);

            return;
        }


        //占位处理
        nameView.setText(session.name);
        ImageCacheHelper.setImageResource(avatarView, R.mipmap.default_discussion_chat);

        new AsyncTask<Void, Void, Discussion>() {
            @Override
            protected Discussion doInBackground(Void... params) {
                Discussion discussion = DiscussionManager.getInstance().queryDiscussionSync(context, session.identifier);
                return discussion;
            }

            @Override
            protected void onPostExecute(Discussion discussion) {

                if(isNeedLoad(avatarView, tagHolding)) {
                    if(null != discussion) {
                        handleSetDisplayInfo(avatarView, nameView, session, discussion, needLoading);

                    } else {
                        ImageCacheHelper.setImageResource(avatarView, R.mipmap.default_discussion_chat);

                    }

                }

            }
        }.executeOnExecutor(ImageThreadPoolExecutor.getInstance());
    }

    private static void handleSetDisplayInfo(ImageView avatarView, TextView nameView, Session session, Discussion discussion, boolean needLoading) {
        Context context = BaseApplicationLike.baseContext;
        nameView.setText(discussion.getTitleI18n(context));
        AvatarHelper.setDiscussionAvatarByAvaId(avatarView, discussion.mAvatar, needLoading, true);
        if(!discussion.getTitleI18n(context).equals(session.name)) {
            session.name = discussion.getTitleI18n(context);
            ChatDaoService.getInstance().sessionRefresh(session);

        }
    }

    private static void handleDealWithContactInitializedStatus(String tagHolding, ImageView avatarView, TextView nameView, TextView signatureView, Session session, boolean needLoading) {

        User user = UserCache.getInstance().getUserCache(session.identifier);

        if(null != user) {
            dealWithContactInitializedStatus(avatarView, nameView, signatureView, user, needLoading, false);
            if(!user.getShowName().equals(session.name)) {
                session.name = user.getShowName();
                ChatDaoService.getInstance().sessionRefresh(session);
            }


        } else {

            //占位处理
            nameView.setText(session.name);
            ImageCacheHelper.setImageResource(avatarView, R.mipmap.default_photo);

            new AsyncTask<Void, Void, User>() {
                @Override
                protected User doInBackground(Void... params) {
                    User user = UserManager.getInstance().queryUserInSyncByUserId(BaseApplicationLike.baseContext, session.identifier, session.mDomainId);
                    return user;
                }

                @Override
                protected void onPostExecute(User user) {

                    if(isNeedLoad(avatarView, tagHolding)) {
                        if(null != user) {
                            dealWithContactInitializedStatus(avatarView, nameView, signatureView, user, needLoading, false);

                            if(!user.getShowName().equals(session.name)) {
                                session.name = user.getShowName();
                                ChatDaoService.getInstance().sessionRefresh(session);

                            }

                        } else {

                            ImageCacheHelper.setImageResource(avatarView, R.mipmap.default_photo);

                        }

                    }

                }
            }.executeOnExecutor(ImageThreadPoolExecutor.getInstance());
        }
    }

    private static boolean isNeedLoad(ImageView avatarView, String tagHolding) {
        return null != avatarView.getTag() && tagHolding.equals(avatarView.getTag().toString());
    }

    public static void dealWithContactInitializedStatus(ImageView avatarView, TextView nameView, TextView signatureView, User user, boolean needLoading, boolean needOfflineStatus){
        /**
         * TODO:
         *     Description：当用户还未设置个人签名时，给出的提示为使用国际化(未使用国际化)
         */
        if(null != signatureView){
            if(TextUtils.isEmpty(user.getSignature())){
                signatureView.setVisibility(View.GONE);
            }else{
                signatureView.setVisibility(View.VISIBLE);
                signatureView.setText(user.getSignature());
            }

        }
        dealWithContactInitializedStatus(avatarView, nameView, user, needLoading, needOfflineStatus);
    }

    public static void dealWithContactInitializedStatus(ImageView avatarView, TextView nameView, ShowListItem contact, boolean needLoading, boolean needOfflineStatus) {
        String showName = null;
        if(contact instanceof Employee) {
            showName = ((Employee)contact).displayName;
        }

        if(StringUtils.isEmpty(showName)) {
            showName = contact.getTitleI18n(BaseApplicationLike.baseContext);
        }

        String userId = LoginUserInfo.getInstance().getLoginUserId(BaseApplicationLike.baseContext);
        ColorMatrix matrix = new ColorMatrix();
        if(!User.isInitialized(contact.getStatus()) && !(userId.equals(contact.getId()))) {
            showName += BaseApplicationLike.baseContext.getString(R.string.tip_not_initialize);
            matrix.reset();
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            avatarView.setColorFilter(filter);

            ImageCacheHelper.setImageResource(avatarView, R.mipmap.avatar_not_initialize);

        } else {
            //有些界面不需要显示离线状态
            if (needOfflineStatus) {
                boolean onlineFeature = DomainSettingsManager.getInstance().handleUserOnlineFeature();
                if (onlineFeature && !OnlineManager.getInstance().isOnline(contact.getId())) {
                    showName += BaseApplicationLike.baseContext.getString(R.string.tip_not_online);
//                    matrix.setSaturation(0);
                } else {
                    matrix.reset();
                }
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                avatarView.setColorFilter(filter);
            }


            String avatar = contact.getAvatar();
            if (!StringUtils.isEmpty(avatar)) {
                AvatarHelper.setUserAvatarByAvaId(avatar, avatarView, needLoading, true);

            } else {
                AvatarHelper.setUserAvatarById(avatarView, contact.getId(), contact.getDomainId(), needLoading, true);
            }

        }

        if (null != nameView) {
            nameView.setText(showName);
        }
    }




    public static boolean canEmployeeTreeOrgSelect(OrganizationResult organization, List<ShowListItem> contactList) {
        for(ShowListItem contact : contactList) {
            Organization orgSelected = null;
            if(contact instanceof OrganizationResult) {
                orgSelected = ((OrganizationResult) contact).toOrganization();
            } else if(contact instanceof Organization) {
                orgSelected = (Organization) contact;
            }

            if(null == orgSelected || organization.id.equals(orgSelected.mId)) {
                continue;
            }

            if(!organization.canSelect(orgSelected)) {
                return false;
            }

        }

        return true;
    }

    public static boolean canEmployeeTreeEmpSelect(ShowListItem contact, List<ShowListItem> contactList) {
        if(contact instanceof Employee) {
            return canEmployeeTreeEmpSelect((Employee)contact, contactList);
        }

        if(contact instanceof EmployeeResult) {
            return canEmployeeTreeEmpSelect((EmployeeResult)contact, contactList);
        }

        return true;
    }

    public static boolean canEmployeeTreeEmpSelect(Employee employee, List<ShowListItem> contactList) {
        return canEmployeeTreeEmpSelect(EmployeeResult.buildEmployee(employee), contactList);
    }

    public static boolean canEmployeeTreeEmpSelect(EmployeeResult employeeResult, List<ShowListItem> contactList) {
        for(ShowListItem contact : contactList) {
            if(contact instanceof OrganizationResult) {
                OrganizationResult organization = (OrganizationResult) contact;
                if(!employeeResult.canSelect(organization)) {
                    return false;
                }
            }

            if(contact instanceof Organization) {
                Organization organization = (Organization) contact;
                if(!employeeResult.canSelect(organization)) {
                    return false;
                }
            }
        }

        return true;
    }



}
