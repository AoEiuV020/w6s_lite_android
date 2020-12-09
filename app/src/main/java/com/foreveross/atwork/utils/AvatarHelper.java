package com.foreveross.atwork.utils;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.foreverht.cache.AppCache;
import com.foreverht.cache.DiscussionCache;
import com.foreverht.cache.UserCache;
import com.foreverht.db.service.repository.DiscussionRepository;
import com.foreverht.threadGear.ImageThreadPoolExecutor;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.discussion.DiscussionSyncNetService;
import com.foreveross.atwork.api.sdk.discussion.responseJson.QueryDiscussionResponseJson;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.RequestRemoteInterceptor;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.FriendNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.OrgNotifyMessage;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.app.manager.AppManager;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.manager.UserManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.UUID;

public class AvatarHelper {


    public static void setUserInfoAvatar(UserHandleInfo userHandleInfo, ImageView imageView) {
        if (StringUtils.isEmpty(userHandleInfo.mAvatar)) {
            AvatarHelper.setUserAvatarById(imageView, userHandleInfo.mUserId, userHandleInfo.mDomainId, true, true);

        } else {
            ImageCacheHelper.displayImageByMediaId(userHandleInfo.mAvatar, imageView, ImageCacheHelper.getRoundAvatarOptions());
        }
    }

    /**
     * 设置群组头像
     */
    public static void setDiscussionAvatarById(final ImageView view, final String identifierOrAccountName, final boolean needLoadingHolder, final boolean needRound) {
        setListAvatar(view, Type.DISCUSSION, identifierOrAccountName, null, needLoadingHolder, needRound, null);
    }

    /**
     * 设置单聊头像
     */
    public static void setUserAvatarById(final ImageView view, final String identifierOrAccountName, final String domainId, final boolean needLoadingHolder, final boolean needRound) {
        setListAvatar(view, Type.USER, identifierOrAccountName, domainId, needLoadingHolder, needRound, null);
    }

    /**
     * 设置App头像
     */
    public static void setAppAvatarById(final ImageView view, final String identifierOrAccountName, final String orgId, final boolean needLoadingHolder, final boolean needRound) {
        setListAvatar(view, Type.APP, identifierOrAccountName, orgId, needLoadingHolder, needRound, null);
    }



    public static void setUserAvatarByAvaId(String avatar, ImageView avatarView, final boolean needLoadingHolder, final boolean needRound) {
        DisplayImageOptions options;
        if (needLoadingHolder) {
            options = ImageCacheHelper.getRoundOptions(R.mipmap.default_photo, R.mipmap.default_photo);
        } else {
            options = ImageCacheHelper.getRoundOptions(R.mipmap.default_photo, -1);

        }

        ImageCacheHelper.displayImageByMediaId(avatar, avatarView, options);

    }

    public static void setOrgIconrByOrgId(String org, ImageView OrgView, final boolean needLoadingHolder, final boolean needRound) {
        DisplayImageOptions options;
        if (needLoadingHolder) {
            options = ImageCacheHelper.getRoundOptions(R.mipmap.icon_org, R.mipmap.icon_org);
        } else {
            options = ImageCacheHelper.getRoundOptions(R.mipmap.icon_org, -1);

        }

        ImageCacheHelper.displayImageByMediaId(org, OrgView, options);

    }


    public static void setAppAvatarByAvaId(ImageView avatarView, String avatar, final boolean needLoadingHolder, final boolean needRound) {
        DisplayImageOptions options;
        if (needLoadingHolder) {
            options = ImageCacheHelper.getRoundOptions(R.mipmap.default_app, R.mipmap.default_app);
        } else {
            options = ImageCacheHelper.getRoundOptions(R.mipmap.default_app, -1);

        }

        ImageCacheHelper.displayImageByMediaId(avatar, avatarView, options);
    }

    public static void setDiscussionAvatarByAvaId(ImageView avatarView, String avatar, final boolean needLoadingHolder, final boolean needRound) {
        DisplayImageOptions options;
        if (needLoadingHolder) {
            options = ImageCacheHelper.getRoundOptions(R.mipmap.default_discussion_chat, R.mipmap.default_discussion_chat);
        } else {
            options = ImageCacheHelper.getRoundOptions(R.mipmap.default_discussion_chat, -1);

        }

        ImageCacheHelper.displayImageByMediaId(avatar, avatarView, options);
    }


    /**
     * @see #setSessionAvatarById(ImageView, Session, boolean, boolean, DisplayImageOptions)
     * */
    public static void setSessionAvatarById(final ImageView view, final Session session, final boolean needLoadingHolder, boolean needRound) {
        setSessionAvatarById(view, session, needLoadingHolder, needRound, null);
    }

    /**
     * 设置 session 列表头像
     */
    public static void setSessionAvatarById(final ImageView view, final Session session, final boolean needLoadingHolder, boolean needRound, @Nullable DisplayImageOptions receiveDisplayImageOptions) {
        int type;
        if (SessionType.Discussion.equals(session.type)) {
            type = Type.DISCUSSION;

        } else if (SessionType.User.equals(session.type)) {
            type = Type.USER;

        } else if(SessionType.Notice.equals(session.type)) {
            type = Type.NOTICE;

        } else if(SessionType.Custom.equals(session.type)) {
            type = Type.CUSTOM;

        } else {
            type = Type.APP;
        }


        if (Type.NOTICE == type) {
            if(FriendNotifyMessage.FROM.equalsIgnoreCase(session.identifier)) {
                ImageCacheHelper.setImageResource(view, R.mipmap.icon_new_friends);
                return;
            }
            if(OrgNotifyMessage.FROM.equalsIgnoreCase(session.identifier)) {
                ImageCacheHelper.setImageResource(view, R.mipmap.icon_org_applying);
                return;
            }
            if(Session.WORKPLUS_SYSTEM.equalsIgnoreCase(session.identifier)) {
                ImageCacheHelper.setImageResource(view, R.mipmap.icon_system_notice);
                return;

            }
            if(Session.ASSET_NOTIFY_SYSTEM.equalsIgnoreCase(session.identifier)) {
                ImageCacheHelper.setImageResource(view, R.mipmap.icon_transaction_assistant_notice);
                return;
            }
            if (Session.DROPBOX_OVERDUE_REMIND.equalsIgnoreCase(session.identifier)) {
                ImageCacheHelper.setImageResource(view, R.mipmap.icon_file_share_notice);
                return;
            }
            return;
        }
        if (Session.EMAIL_APP_ID.equalsIgnoreCase(session.identifier)) {
            ImageCacheHelper.setImageResource(view, R.mipmap.icon_email_session);

            return;
        }



        if (Session.WORKPLUS_DISCUSSION_HELPER.equals(session.identifier)) {
            view.setImageResource(R.mipmap.icon_discussion_helper);
            return;
        }

        if (Session.WORKPLUS_SUMMARY_HELPER.equals(session.identifier)) {
            view.setImageResource(R.mipmap.new_summary_icon);
            return;
        }


        if (Session.COMPONENT_ANNOUNCE_APP.equals(session.identifier)) {
            view.setImageResource(R.mipmap.icon_announce_app);
            return;
        }

        setListAvatar(view, type, session.identifier, session.orgId, needLoadingHolder, needRound, receiveDisplayImageOptions);
    }



    /**
     * 设置 session, 组群等列表的头像, 使用 imageLoader
     *
     * @param view
     * @param type
     * @param identifierOrAccountName
     * @param needLoadingHolder
     * @param needRound
     */
    @SuppressLint("StaticFieldLeak")
    public static void setListAvatar(final ImageView view, int type, final String identifierOrAccountName, final String secondKey, final boolean needLoadingHolder, final boolean needRound, @Nullable DisplayImageOptions receiveDisplayImageOptions) {

        final String tagHolding = UUID.randomUUID().toString();
        view.setTag(R.id.key_image_loading, tagHolding);


        if(StringUtils.isEmpty(identifierOrAccountName)) {
            ImageCacheHelper.setImageResource(view, getDefaultResId(type));
            ImageCacheHelper.resetImageViewMediaIdLoaded(view);

            return;
        }


        ShowListItem avatarObj = null;
        //从缓存中取
        if (Type.DISCUSSION == type) {
            avatarObj = DiscussionCache.getInstance().getDiscussionCache(identifierOrAccountName);
        } else if (Type.APP == type) {
            avatarObj = AppCache.getInstance().getAppCache(identifierOrAccountName);
        } else if(Type.USER == type){
            avatarObj = UserCache.getInstance().getUserCache(identifierOrAccountName);
        }

        if (null != avatarObj) {
            showListAvatarByAva(avatarObj.getAvatar(), type, avatarObj.getId(), needLoadingHolder, needRound, receiveDisplayImageOptions, view);

        } else {

            //缓存里没有对象, 先显示占位符
            ImageCacheHelper.setImageResource(view, getDefaultResId(type));


            new AsyncTask<Void, ShowListItem, ShowListItem>() {
                @Override
                protected ShowListItem doInBackground(Void... params) {
                    ShowListItem avatarObj = null;

                    //按照缓存->数据库->网络 的顺序读取
                    if (Type.DISCUSSION == type) {
                        avatarObj = DiscussionManager.getInstance().queryDiscussionSync(BaseApplicationLike.baseContext, identifierOrAccountName);
                    } else if (Type.APP == type) {
                        avatarObj = AppManager.getInstance().queryAppSync(BaseApplicationLike.baseContext, identifierOrAccountName, secondKey);
                    } else if(Type.USER == type){
                        avatarObj = UserManager.getInstance().queryUserInSyncByUserId(BaseApplicationLike.baseContext, identifierOrAccountName, secondKey);

                    }

                    return avatarObj;
                }

                @Override
                protected void onPostExecute(ShowListItem item) {
                    if(!isNeedLoad(view, tagHolding)) {
                        return;
                    }

                    if(null != item) {
                        showListAvatarByAva(item.getAvatar(), type, item.getId(), needLoadingHolder, needRound, receiveDisplayImageOptions, view);

                    } else {
                        ImageCacheHelper.setImageResource(view, getDefaultResId(type));
                        ImageCacheHelper.resetImageViewMediaIdLoaded(view);
                    }
                }
            }.executeOnExecutor(ImageThreadPoolExecutor.getInstance());


        }
    }

    public static void showListAvatarByAva(final String avatar, int type, String discussionId, boolean needLoadingHolder, boolean needRound, @Nullable DisplayImageOptions receiveDisplayImageOptions , final ImageView view) {

        if(needLoadingHolder && ImageCacheHelper.isImageViewMediaIdLoaded(view, avatar)) {
            needLoadingHolder = false;
        }



        DisplayImageOptions options = receiveDisplayImageOptions;

        if(null == options) {
            if (needLoadingHolder) {
                options = needRound ? ImageCacheHelper.getRoundOptions(getDefaultResId(type), getDefaultResId(type)) : ImageCacheHelper.getRectOptions(getDefaultResId(type), getDefaultResId(type));
            } else {
                options = needRound ? ImageCacheHelper.getRoundOptions(getDefaultResId(type), -1) : ImageCacheHelper.getRectOptions(getDefaultResId(type), -1);
            }
        }



        //如果是群，查询下是否有群头像，没有则主动去拉取
        if (Type.DISCUSSION == type && null == avatar) {
            showDiscussionNoAvatar(discussionId, view, options);

        } else {

            ImageCacheHelper.displayImageByMediaId(avatar, view, options);

        }

    }

    private static boolean isNeedLoad(ImageView view, String tagHolding) {
        return StringUtils.isEmpty(tagHolding) || tagHolding.equals(view.getTag(R.id.key_image_loading));
    }


    @SuppressLint("StaticFieldLeak")
    private static void showDiscussionNoAvatar(final String discussionId, final ImageView view, final DisplayImageOptions options) {
        final String requestId = "showDiscussionNoAvatar_" + discussionId;
        if (RequestRemoteInterceptor.INSTANCE.checkLegal(requestId)) {

            final String tagHolding = UUID.randomUUID().toString();
            view.setTag(R.id.key_image_loading, tagHolding);


            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... parms) {
                    String mediaId = "";

                    HttpResult httpResult = DiscussionSyncNetService.getInstance().queryDiscussionBasicInfo(BaseApplicationLike.baseContext, discussionId);
                    Discussion discussion = null;

                    if(httpResult.isRequestSuccess()) {
                        QueryDiscussionResponseJson queryDiscussionResponseJson = (QueryDiscussionResponseJson) httpResult.resultResponse;
                        discussion = queryDiscussionResponseJson.discussion;
                    } else {
                        if(null != httpResult.resultResponse) {
                            ErrorHandleUtil.handleTokenError(httpResult.resultResponse.status, httpResult.resultResponse.message);

                        }
                    }

                    if (null == discussion || StringUtils.isEmpty(discussion.mAvatar)) {

                        RequestRemoteInterceptor.INSTANCE.addInterceptRequestId(requestId);

                    } else {
                        DiscussionRepository.getInstance().updateDiscussionAvatar(discussionId, discussion.mAvatar);
                    }

                    return mediaId;
                }

                @Override
                protected void onPostExecute(String mediaId) {
                    if (isNeedLoad(view, tagHolding)) {
                        ImageCacheHelper.displayImageByMediaId(mediaId, view, options);
                    }
                }
            }.executeOnExecutor(ImageThreadPoolExecutor.getInstance());

        } else {

            view.setImageDrawable(options.getImageOnFail(view.getResources()));
        }
    }



    private static int getDefaultResId(int type) {
        int defaultResId;
        if (Type.DISCUSSION == type) {
            defaultResId = R.mipmap.default_discussion_chat;

        } else if (Type.APP == type) {
            defaultResId = R.mipmap.default_app;

        } else {
            defaultResId = R.mipmap.default_photo;
        }

        return defaultResId;
    }

    public final class Type {
        public static final int USER = 0;
        public static final int DISCUSSION = 1;
        public static final int APP = 2;
        public static final int NOTICE = 3;
        public static final int CUSTOM = 4;
    }

}
