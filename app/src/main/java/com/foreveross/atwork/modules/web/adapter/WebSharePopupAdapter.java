package com.foreveross.atwork.modules.web.adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.foreverht.db.service.repository.MessageRepository;
import com.foreverht.webview.GetWebSnapshotJs;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.message.MessageAsyncNetService;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.component.OrgSwitchDialog;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.component.WebPopupItemView;
import com.foreveross.atwork.component.floatView.service.WorkplusFloatService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.model.share.ExternalShareType;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleItem;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.support.AtworkConstants;
import com.foreveross.atwork.infrastructure.utils.AppUtil;
import com.foreveross.atwork.infrastructure.utils.CommonUtil;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.RegSchemaHelper;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.webview.AtworkWebView;
import com.foreveross.atwork.modules.app.manager.AppManager;
import com.foreveross.atwork.manager.share.ShareFactory;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.app.model.WebJsGetSnapshot;
import com.foreveross.atwork.modules.group.activity.TransferMessageActivity;
import com.foreveross.atwork.modules.group.module.TransferMessageControlAction;
import com.foreveross.atwork.modules.group.module.TransferMessageMode;
import com.foreveross.atwork.modules.setting.fragment.WebviewTextSizeSettingDialogFragment;
import com.foreveross.atwork.modules.web.component.WebUrlHookingFloatView;
import com.foreveross.atwork.modules.web.model.WebShareBuilder;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.atwork.utils.IntentUtil;
import com.foreveross.theme.manager.SkinMaster;
import com.tencent.tauth.Tencent;

import java.util.ArrayList;
import java.util.List;


public class WebSharePopupAdapter extends BaseAdapter {

    public static final String FORWARD_MODE_ALL = "ALL";
    public static final String FORWARD_MODE_NONE = "NONE";

    private Context mContext;

    private List<Integer> mNameList = new ArrayList<>();

    private List<Integer> mImageResList = new ArrayList<>();

    private List<Organization> mOrgListHavingCircle;

    private ArticleItem mItemData;

    private Activity mActivity;

    private Tencent mTencent;

    private Fragment mFragment;

    private ShareChatMessage.ShareType mShareType;

    private DialogFragment mPopupWindow;

    private ProgressDialogHelper mProgressDialogHelper;

    private boolean mPreLoadMode = true;

    private boolean mHookingFloatMode = false;

    private boolean mNeedFetchInfoFromRemote;

    private String mMessageId;
    private String mSessionId;

    public WebSharePopupAdapter(WebShareBuilder builder) {
        mContext = builder.getContext();
        mFragment = builder.getFragment();
        mPopupWindow = builder.getWebSharePopupWindow();
        mActivity = (Activity) mContext;
        mShareType = builder.getShareType();
        mMessageId = builder.getMessageId();
        mSessionId = builder.getSessionId();

        mOrgListHavingCircle = builder.getOrgListHavingCircle();

        mProgressDialogHelper = new ProgressDialogHelper(mContext);

        mNeedFetchInfoFromRemote = builder.isNeedFetchInfoFromRemote();

        buildPreLoadModeList();
    }

    private void addItem2NameList(@StringRes Integer nameRes) {
        mNameList.remove(nameRes);
        mNameList.add(nameRes);

    }

    private void addItem2ImageResList(@DrawableRes Integer imageRes) {
        mImageResList.remove(imageRes);
        mImageResList.add(imageRes);

    }


    private void buildAllList() {

        buildAllList(WebViewControlAction.FROM_OTHER);

    }
    //重写这个函数，解决收藏的BUG
    private void buildAllList(int from) {
        buildInnerList(false);

        if (FORWARD_MODE_ALL.equalsIgnoreCase(mItemData.mForwardMode)) {
            addItem2NameList(R.string.open_by_system_explorer);
            addItem2ImageResList(R.mipmap.icon_browser);

        }
        if (BeeWorks.getInstance().config.beeWorksShare.mShareWX.enable && FORWARD_MODE_ALL.equalsIgnoreCase(mItemData.mForwardMode)) {
            addItem2NameList(R.string.share_to_wx);
            addItem2ImageResList(R.mipmap.icon_wx_session);

            addItem2NameList(R.string.share_to_wx_circle);
            addItem2ImageResList(R.mipmap.icon_wx_timeline);
        }

        if (BeeWorks.getInstance().config.beeWorksShare.mShareQQ.mShareAndroid.enable && FORWARD_MODE_ALL.equalsIgnoreCase(mItemData.mForwardMode)) {
            addItem2NameList(R.string.share_to_qq);
            addItem2ImageResList(R.mipmap.icon_qq_session);

            addItem2NameList(R.string.share_to_qq_zone);
            addItem2ImageResList(R.mipmap.icon_qq_qzone);
        }


        if (BeeWorks.getInstance().config.beeWorksShare.mShareWB.enable && FORWARD_MODE_ALL.equalsIgnoreCase(mItemData.mForwardMode)) {
            addItem2NameList(R.string.share_to_wb);
            addItem2ImageResList(R.mipmap.icon_qq_session);

        }

        if (FORWARD_MODE_ALL.equalsIgnoreCase(mItemData.mForwardMode)) {
            addItem2NameList(R.string.copy_url);
            addItem2ImageResList(R.mipmap.icon_copy);
        }

        addItem2NameList(R.string.refresh);
        addItem2ImageResList(R.mipmap.icon_refresh_url);


        addItem2NameList(R.string.text_size);
        addItem2ImageResList(R.mipmap.icon_text_size);

//        addItem2NameList(R.string.test_share_url);
//        addItem2ImageResList(R.mipmap.icon_logo);



    }

    /**
     * 构建内部使用入口: "发送给", "分享到同事圈", "收藏"
     * */
    private void buildInnerList(boolean needRefresh) {
        mNameList.clear();
        mImageResList.clear();


//        if (mHookingFloatMode) {
//            addItem2NameList(R.string.web_un_float);
//            addItem2ImageResList(R.mipmap.icon_copy);
//
//        } else {
//            addItem2NameList(R.string.web_float);
//            addItem2ImageResList(R.mipmap.icon_copy);
//        }

        if (!FORWARD_MODE_NONE.equalsIgnoreCase(mItemData.mForwardMode)) {
            addItem2NameList(R.string.send_to);
            addItem2ImageResList(R.mipmap.icon_send_to);
        }


        if (!ListUtil.isEmpty(mOrgListHavingCircle) && !FORWARD_MODE_NONE.equalsIgnoreCase(mItemData.mForwardMode)) {
            addItem2NameList(R.string.share_to_circle);
            addItem2ImageResList(R.mipmap.icon_college_circle);
        }

        if (needRefresh) {
            addItem2NameList(R.string.refresh);
            addItem2ImageResList(R.mipmap.icon_refresh_url);
        }

    }

    private void buildPreLoadModeList() {
        mNameList.clear();
        mImageResList.clear();

        addItem2NameList(R.string.refresh);
        addItem2ImageResList(R.mipmap.icon_refresh_url);

    }

    public void setCommonMode() {
        setCommonMode(WebViewControlAction.FROM_OTHER);
    }

    public void setCommonMode(int from) {
        mPreLoadMode = false;
        buildAllList(from);

        notifyDataSetChanged();
    }

    public void setCommonModeExcludeRefresh() {
        mPreLoadMode = false;
        buildAllList();

        mNameList.remove(mNameList.size() - 1);
        mImageResList.remove(mImageResList.size() - 1);

        notifyDataSetChanged();
    }

    public void setInnerShareMode() {
        mPreLoadMode = false;
        buildInnerList(true);

        notifyDataSetChanged();
    }

    public void setPreLoadMode() {
        mPreLoadMode = true;
        buildPreLoadModeList();

        notifyDataSetChanged();
    }


    public void setHookingFloatMode(boolean floatMode) {
        mHookingFloatMode = floatMode;
    }


    @Override
    public int getCount() {
        return mNameList.size();
    }

    @Override
    public String getItem(int position) {
        return mContext.getString(mNameList.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = new WebPopupItemView(mContext);
        }

        final WebPopupItemView itemView = (WebPopupItemView) convertView;
        final String name = mContext.getString(mNameList.get(position));

        itemView.setVisibility(View.VISIBLE);
        itemView.refreshItemView(name, mImageResList.get(position));

        handleClickEvent(itemView, name);

        SkinMaster.getInstance().changeTheme((ViewGroup) convertView);


        return convertView;
    }

    public void handleClickEvent(WebPopupItemView itemView, String name) {
        itemView.setOnClickListener(v -> {

            mPopupWindow.dismiss();

            if (name.equals(mContext.getResources().getString(R.string.refresh))) {
                if (mFragment instanceof AtworkWebView) {
                    ((AtworkWebView) mFragment).reload();
                }
            }
            //选择回话
            else if (name.equals(mContext.getResources().getString(R.string.send_to))) {
                shareToSession();

            }

            //同事圈
            else if (name.equals(mContext.getResources().getString(R.string.share_to_circle))) {
                //弹出组织机构选择页面
                setOrgPopupView();
            }

            //复制链接
            else if (name.equals(mContext.getResources().getString(R.string.copy_url))) {
                ClipboardManager cmb = (ClipboardManager) BaseApplicationLike.baseContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("newPlainTextLabel", mItemData.url);
                cmb.setPrimaryClip(clipData);
                AtworkToast.showToast(mContext.getResources().getString(R.string.copy_success));
            }

            //浏览器
            else if (name.equals(mContext.getResources().getString(R.string.open_by_system_explorer))) {
                IntentUtil.routeSystemWebView(mContext, mItemData.url);
            }


            //微信
            else if (name.equalsIgnoreCase(mContext.getString(R.string.share_to_wx))) {

                if (CommonUtil.isFastClick(1000)) {
                    return;
                }
                shareToWxSession();
            }

            //微信朋友圈
            else if (name.equalsIgnoreCase(mContext.getString(R.string.share_to_wx_circle))) {
                if (CommonUtil.isFastClick(1000)) {
                    return;
                }
                shareToWxTimeline();

            }

            //QQ
            else if (name.equalsIgnoreCase(mContext.getString(R.string.share_to_qq))) {


                if (!AppUtil.isQQClientAvailable(mActivity)) {
                    AtworkToast.showToast(mActivity.getString(R.string.qq_not_avaliable));
                    return;
                }
                if (CommonUtil.isFastClick(1000)) {
                    return;
                }
                shareToQQSession();

            }

            //QQ空间
            else if (name.equalsIgnoreCase(mContext.getString(R.string.share_to_qq_zone))) {
                if (!AppUtil.isQQClientAvailable(mActivity)) {
                    AtworkToast.showToast(mActivity.getString(R.string.qq_not_avaliable));
                    return;
                }
                if (CommonUtil.isFastClick(1000)) {
                    return;
                }
                shareToQzone();

            }


            else if(name.equalsIgnoreCase(mContext.getString(R.string.share_to_wb))) {
                if (CommonUtil.isFastClick(1000)) {
                    return;
                }

                shareToWb();
            }


            else if(name.equalsIgnoreCase(mContext.getString(R.string.web_float))) {
//                mActivity.moveTaskToBack(true);
                PersonalShareInfo.getInstance().setUrlHookingForFloat(BaseApplicationLike.baseContext, mItemData.url);

                mActivity.finish();
                WorkplusFloatService.Companion.sendCreateFloatingWindow(WebUrlHookingFloatView.TYPE);

            } else if(name.equalsIgnoreCase(mContext.getString(R.string.web_un_float))) {
                PersonalShareInfo.getInstance().setUrlHookingForFloat(BaseApplicationLike.baseContext, StringUtils.EMPTY);
                WebViewActivity.setHookingFloatMode(false);

                AtworkToast.showResToast(R.string.web_un_float_successfully);

            } else if(name.equalsIgnoreCase(mContext.getString(R.string.test_share_url))) {
                Uri uri = Uri.parse("workplus_test://sharelink?url=www.baidu.com&title=hello");
                Intent intent = new Intent(Intent.ACTION_SEND, uri);
                intent.addCategory(Intent.CATEGORY_DEFAULT);

                PackageManager pm = mContext.getPackageManager();

                List<ResolveInfo> allResInfos = pm.queryIntentActivities(intent, 0);
                LogUtil.e("uri -> " + uri.toString() + " allResInfos  -> " + allResInfos.toString());

                if(1 <= allResInfos.size()) {
                    mContext.startActivity(intent);
                } else {
                    AtworkToast.showToast("不存在分享的 app");
                }


            } else if(name.equalsIgnoreCase(mContext.getString(R.string.text_size))) {
                WebviewTextSizeSettingDialogFragment dialogFragment = new WebviewTextSizeSettingDialogFragment();
                if (mFragment instanceof AtworkWebView) {
                    dialogFragment.setAtworkWebView(((AtworkWebView) mFragment));
                }

                dialogFragment.show(mFragment.getFragmentManager(), "webviewTextSizeSettingDialog");

            }

        });
    }

    private ChatPostMessage checkFromArticleChatMessage(ChatPostMessage message) {
        if(message instanceof ArticleChatMessage) {
            ShareChatMessage shareChatMessage = ShareChatMessage.makeFakeShareMessage(mItemData.title, mItemData.url, mItemData.mCoverUrl, mItemData.summary);
            shareChatMessage.deliveryId = message.deliveryId;
            shareChatMessage.deliveryTime = message.deliveryTime;
            shareChatMessage.from = message.from;
            shareChatMessage.mFromDomain = message.mFromDomain;
            shareChatMessage.mFromType = message.mFromType;
            shareChatMessage.mMyName = message.mMyName;
            shareChatMessage.mMyAvatar = message.mMyAvatar;

            if(StringUtils.isEmpty(shareChatMessage.mMyName)) {
                App app = AppManager.getInstance().queryAppSync(AtworkApplicationLike.baseContext, message.from, message.mOrgId);
                if(null != app) {
                    shareChatMessage.mMyName = app.getTitleI18n(AtworkApplicationLike.baseContext);
                    shareChatMessage.mMyAvatar = app.getAvatar();

                }
            }

            message = shareChatMessage;
        }
        return message;
    }

    private void shareToQzone() {
        parseUrlForShare(new ShareAdapterOnParseUrlForShareListener(mItemData, mNeedFetchInfoFromRemote, mProgressDialogHelper) {
            @Override
            public void onAction(ArticleItem articleItem) {
                doShareToQzone(articleItem);
            }
        });

    }

    private void doShareToQzone(ArticleItem articleItem) {
        mTencent = Tencent.createInstance(AtworkConfig.QQ_APP_ID, BaseApplicationLike.baseContext);
        ExternalShareType share = ShareFactory.factory(mActivity, ExternalShareType.ShareType.QZONE);
        share.shareMessage(articleItem);
    }

    private void shareToQQSession() {
        parseUrlForShare(new ShareAdapterOnParseUrlForShareListener(mItemData, mNeedFetchInfoFromRemote, mProgressDialogHelper) {
            @Override
            public void onAction(ArticleItem articleItem) {
                doShareToQQSession(articleItem);
            }
        });

    }

    private void doShareToQQSession(ArticleItem articleItem) {
        mTencent = Tencent.createInstance(AtworkConfig.QQ_APP_ID, BaseApplicationLike.baseContext);
        ExternalShareType share = ShareFactory.factory(mActivity, ExternalShareType.ShareType.QQ);
        share.shareMessage(articleItem);
    }

    private void shareToWxTimeline() {
        parseUrlForShare(new ShareAdapterOnParseUrlForShareListener(mItemData, mNeedFetchInfoFromRemote, mProgressDialogHelper) {
            @Override
            public void onAction(ArticleItem articleItem) {
                doShareToWxTimeline(articleItem);
            }
        });

    }

    private void doShareToWxTimeline(ArticleItem itemData) {
        ExternalShareType share = ShareFactory.factory(mActivity, ExternalShareType.ShareType.WXTimeLine);
        share.shareMessage(itemData);
    }


    private void shareToWb() {
        parseUrlForShare(new ShareAdapterOnParseUrlForShareListener(mItemData, mNeedFetchInfoFromRemote, mProgressDialogHelper) {
            @Override
            public void onAction(ArticleItem articleItem) {
                doShareToWb(articleItem);
            }
        });

    }


    private void doShareToWb(ArticleItem itemData) {
        ExternalShareType share = ShareFactory.factory(mActivity, ExternalShareType.ShareType.WbShare);
        share.shareMessage(itemData);
    }

    private void shareToWxSession() {
        parseUrlForShare(new ShareAdapterOnParseUrlForShareListener(mItemData, mNeedFetchInfoFromRemote, mProgressDialogHelper) {
            @Override
            public void onAction(ArticleItem articleItem) {
                doShareToWxSession(articleItem);
            }
        });

    }



    private void doShareToWxSession(ArticleItem itemData) {
        ExternalShareType share = ShareFactory.factory(mActivity, ExternalShareType.ShareType.WXSession);
        share.shareMessage(itemData);
    }

    private void shareToSession() {
        parseUrlForShare(new ShareAdapterOnParseUrlForShareListener(mItemData, mNeedFetchInfoFromRemote, mProgressDialogHelper) {
            @Override
            public void onAction(ArticleItem articleItem) {
                doShareToSession(articleItem);
            }
        });
    }

    private void doShareToSession(ArticleItem articleItem) {
        AtworkApplicationLike.getLoginUser(new UserAsyncNetService.OnQueryUserListener() {
            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleError(null, errorCode, errorMsg);
            }

            @Override
            public void onSuccess(@NonNull User user) {
                ShareChatMessage shareChatMessage = ShareChatMessage.newSendShareMessage(AtworkApplicationLike.baseContext, articleItem, user.mUserId, user.mDomainId, user.getShowName(), user.mAvatar, ParticipantType.User, BodyType.Share, mShareType);
                List<ChatPostMessage> singleList = new ArrayList<>();
                singleList.add(shareChatMessage);

                TransferMessageControlAction transferMessageControlAction = new TransferMessageControlAction();
                transferMessageControlAction.setSendMessageList(singleList);
                transferMessageControlAction.setSendMode(TransferMessageMode.SEND);
                Intent intent = TransferMessageActivity.Companion.getIntent(BaseApplicationLike.baseContext, transferMessageControlAction);


                mContext.startActivity(intent);
                //界面切换效果
                mActivity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }


        });
    }

    public void setItemData(ArticleItem articleItem) {
        mItemData = articleItem;
        if (!StringUtils.isEmpty(articleItem.url)) {
//            mItemData.url = combineWithOrgId(articleItem.url);
        }
        if (TextUtils.isEmpty(articleItem.mForwardMode)) {
            mItemData.mForwardMode = FORWARD_MODE_ALL;
        }

    }
    private static final String ALLOWED_URI_CHARS = "@&=*+-_.,:!?()/~'%";

    @Nullable
    private String getCollegeCircleUrl(String orgId, ArticleItem itemData) {
        String shareUrl = UrlConstantManager.getInstance().getColleagueCircleShareUrl();
        if (StringUtils.isEmpty(itemData.url)) {
            return null;
        }

        String icon = StringUtils.EMPTY;
        String summary = StringUtils.EMPTY;
        String url = itemData.url;

        if (!StringUtils.isEmpty(itemData.coverMediaId)) {
            icon = itemData.coverMediaId;
        }

        if (StringUtils.isEmpty(icon) && !StringUtils.isEmpty(itemData.mCoverUrl)) {
            icon = itemData.mCoverUrl;

        }

        if (!StringUtils.isEmpty(itemData.title)) {
            summary = itemData.title;
        }
        return String.format(shareUrl, Uri.encode(summary, ALLOWED_URI_CHARS), orgId, icon, Uri.encode(url, ALLOWED_URI_CHARS), itemData.mForwardMode);
    }


    private String getCollegeCircleUrlV2_Old(ArticleItem articleItem) {
        String shareUrl = UrlConstantManager.getInstance().getColleagueCircleShareUrlV2();
        if (StringUtils.isEmpty(articleItem.url)) {
            return null;
        }

        String icon = StringUtils.EMPTY;
        String summary = StringUtils.EMPTY;
        String url = articleItem.url;

        if (!StringUtils.isEmpty(articleItem.mCoverUrl)) {
            icon = articleItem.mCoverUrl;

        }


        if (!StringUtils.isEmpty(articleItem.title)) {
            summary = articleItem.title;
        }

        return String.format(shareUrl, url, icon, summary);
    }

    private void setOrgPopupView() {
        if (1 == mOrgListHavingCircle.size()) {
            toCollegeCircleByOrgCode(mOrgListHavingCircle.get(0).mOrgCode, null);
            return;
        }

        popupOrgSelectView(mOrgListHavingCircle);

    }

    private void popupOrgSelectView(List<Organization> organizations) {
        OrgSwitchDialog orgSwitchDialog = new OrgSwitchDialog();

        orgSwitchDialog.setData(OrgSwitchDialog.Type.SHARE_CIRCLE, mActivity.getString(R.string.choice_orgs));

        orgSwitchDialog.setOrgData(organizations);
        orgSwitchDialog.setItemOnClickListener(organization -> {

            toCollegeCircleByOrgCode(organization.mOrgCode, orgSwitchDialog);

        });

        orgSwitchDialog.show(mFragment.getFragmentManager(), "org_switch");
    }


    private void toCollegeCircleByOrgCode(String orgCode, @Nullable OrgSwitchDialog orgSwitchDialog) {
        parseUrlForShare(new ShareAdapterOnParseUrlForShareListener(mItemData, mNeedFetchInfoFromRemote, mProgressDialogHelper) {
            @Override
            public void onAction(ArticleItem articleItem) {
                doToCollegeCircleByOrgCode(orgCode, articleItem, orgSwitchDialog);
            }
        });

    }

    /**
     * 进入同事圈前需要去网络拉取最新配置数据(可能后台关闭同事圈)才做跳转
     */
    private void doToCollegeCircleByOrgCode(String orgCode, ArticleItem itemData, @Nullable OrgSwitchDialog orgSwitchDialog) {
        if (null != orgSwitchDialog) {
            orgSwitchDialog.dismiss();
        }
        String url = null;
        if (AtworkConfig.USE_V2_COLLEAGUE_CIRCLE) {
            url = getCollegeCircleUrlV2_Old(itemData);

        } else {
            url = getCollegeCircleUrl(orgCode, itemData);

        }
        WebViewControlAction webViewControlAction = WebViewControlAction
                .newAction()
                .setUrl(url)
                .setNeedShare(false)
                .setFrom(WebViewControlAction.FROM_MOMENTS);

        mActivity.startActivity(WebViewActivity.getIntent(mActivity, webViewControlAction));
        //界面切换效果
        mActivity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

    }

    private void parseUrlForShare(final ShareAdapterOnParseUrlForShareListener listener) {
        if(!mNeedFetchInfoFromRemote) {

            parseUrlLocalCrawl(listener);
            return;
        }

        MessageAsyncNetService.parseUrlForShare(BaseApplicationLike.baseContext, mItemData.url, listener);

    }

    private void parseUrlLocalCrawl(ShareAdapterOnParseUrlForShareListener listener) {

        if (mFragment instanceof AtworkWebView) {

            if(mItemData.isShareAllMatch()) {

                listener.onAction(mItemData);
                return;
            }

            ((AtworkWebView) mFragment).evaluateJavascript(GetWebSnapshotJs.JS, value -> {

                LogUtil.e("GetWebSnapshotJs.JS -> " + value);

                if(!StringUtils.isNull(value)) {
                    WebJsGetSnapshot webJsGetSnapshot = JsonUtil.fromJson(value, WebJsGetSnapshot.class);

                    if (null != webJsGetSnapshot) {
                        if(StringUtils.isEmpty(mItemData.title)) {
                            mItemData.title = webJsGetSnapshot.getTitle();
                        }

                        if(StringUtils.isEmpty(mItemData.mCoverUrl) || !RegSchemaHelper.isUrlLink(mItemData.mCoverUrl)) {
                            mItemData.mCoverUrl = webJsGetSnapshot.getCoverUrl();
                        }

                        if(StringUtils.isEmpty(mItemData.summary)) {
                            mItemData.summary = webJsGetSnapshot.getDescription();
                        }
                    }

                }

                listener.onAction(mItemData);
                return;

            });

            return;
        }


        listener.onAction(mItemData);
        return;
    }


    private String combineWithOrgId(@NonNull String url) {
        if (url.contains("org_id_")) {
            return url;
        }
        if (!url.contains("?")) {
            StringBuilder stringBuilder = new StringBuilder(url);
            stringBuilder.append("/?org_id_=").append(PersonalShareInfo.getInstance().getCurrentOrg(mContext));
            return stringBuilder.toString();
        }
        StringBuilder builder = new StringBuilder(url);
        builder.append("&org_id_=").append(PersonalShareInfo.getInstance().getCurrentOrg(mContext));
        return builder.toString();
    }



    public static abstract class ShareAdapterOnParseUrlForShareListener implements MessageAsyncNetService.OnParseUrlForShareListener {

        public ArticleItem mArticleItemNotFromRemote;
        private boolean mNeedFetchInfoFromRemote;
        private ProgressDialogHelper mProgressDialogHelper;

        public ShareAdapterOnParseUrlForShareListener(ArticleItem articleItem, boolean needFetchInfoFromRemote, ProgressDialogHelper progressDialogHelper) {
            mArticleItemNotFromRemote = articleItem;
            mNeedFetchInfoFromRemote = needFetchInfoFromRemote;
            mProgressDialogHelper = progressDialogHelper;

            onParseStart();
        }

        public void onParseStart() {
            if (mNeedFetchInfoFromRemote) {
                mProgressDialogHelper.show();
            }
        }

        @Override
        public void onParseSuccess(ArticleItem articleItem) {
            mProgressDialogHelper.dismiss();
            onAction(articleItem);
        }


        @Override
        public void networkFail(int errorCode, String errorMsg) {
            mProgressDialogHelper.dismiss();
            if(AtworkConstants.URL_TRANSLATED_FAIL == errorCode) {
                onAction(mArticleItemNotFromRemote);

                return;
            }


            ErrorHandleUtil.handleError(errorCode, errorMsg);
        }

        public abstract void onAction(ArticleItem articleItem);

    }
}
