package com.foreveross.atwork.modules.main.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.fragment.app.FragmentActivity;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.component.WorkplusBottomPopDialog;
import com.foreveross.atwork.component.popview.PopUpView;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.manager.OrganizationSettingsManager;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.organizationSetting.OrganizationSettings;
import com.foreveross.atwork.infrastructure.model.user.SelectedContactList;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.contact.activity.ContactActivity;
import com.foreveross.atwork.modules.group.activity.UserSelectActivity;
import com.foreveross.atwork.modules.group.module.UserSelectControlAction;
import com.foreveross.atwork.modules.main.activity.MainActivity;
import com.foreveross.atwork.modules.main.adapter.MainFabBottomPopAdapter;
import com.foreveross.atwork.modules.main.helper.extension.MainFabBottomPopupHelperExtensionKt;
import com.foreveross.atwork.modules.main.model.MainFabBottomPopItem;
import com.foreveross.atwork.modules.qrcode.activity.QrcodeScanActivity;
import com.foreveross.atwork.modules.voip.activity.VoipHistoryActivity;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.atwork.utils.ToastHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by reyzhang22 on 17/8/15.
 */

public class MainFabBottomPopupHelper {

    private static boolean sIsHandling = false;

    public static void createMainFabBottomPopUp(String tabId, MainFabBottomPopAdapter adapter) {
        if ("im".equalsIgnoreCase(tabId)) {
            createChatListPopup(adapter);
            return;
        }

        if ("contact".equalsIgnoreCase(tabId)) {
            createContactListPopup(adapter);
            return;
        }
    }

    public static void createChatListPopup(MainFabBottomPopAdapter adapter) {
        List<MainFabBottomPopItem> dataList = new ArrayList<>();



        if (VoipHelper.isVoipEnable(BaseApplicationLike.baseContext)) {
            MainFabBottomPopItem voipItem = MainFabBottomPopItem.newInstance()
                    .setMainFabBottomAction(MainFabBottomPopItem.MainFabBottomAction.VOIP_CALL)
                    .setTitle(R.string.label_voip_meeting_chat_pop)
                    .setResId(R.mipmap.icon_voip_conf);

            dataList.add(voipItem);

        }

        MainFabBottomPopItem scanItem = MainFabBottomPopItem.newInstance()
                .setMainFabBottomAction(MainFabBottomPopItem.MainFabBottomAction.SCAN_QR_CODE)
                .setTitle(R.string.qrcode)
                .setResId(R.mipmap.icon_scan_qrcode_white);

        dataList.add(scanItem);

        if (AtworkConfig.MAIN_FAB_BOTTOM_POP_CONFIG.isFriendItemInChat()) {
            if (DomainSettingsManager.getInstance().handleFriendsRelationshipsFeature()) {
                MainFabBottomPopItem addFriendItem = MainFabBottomPopItem.newInstance()
                        .setMainFabBottomAction(MainFabBottomPopItem.MainFabBottomAction.ADD_FRIEND)
                        .setTitle(R.string.add_new_friend)
                        .setResId(R.mipmap.icon_new_friend_pop);

                dataList.add(addFriendItem);

            }
        }

        if (AtworkConfig.DISSCUSSION_CONFIG.isNeedEntry()) {
            MainFabBottomPopItem groupChatItem = MainFabBottomPopItem.newInstance()
                    .setMainFabBottomAction(MainFabBottomPopItem.MainFabBottomAction.NEW_DISCUSSION_CHAT)
                    .setTitle(R.string.more_create_discussion_chat)
                    .setResId(R.mipmap.icon_groupchat);

            dataList.add(groupChatItem);
        }


        if (AtworkConfig.ZOOM_CONFIG.isUrlEnabled()) {
            MainFabBottomPopItem meetingItem = MainFabBottomPopItem.newInstance()
                    .setMainFabBottomAction(MainFabBottomPopItem.MainFabBottomAction.MEETING_CALL)
                    .setTitle(R.string.video_meeting)
                    .setResId(R.mipmap.icon_pop_more_start_meeting);

            dataList.add(meetingItem);
        }


        if(AtworkConfig.MAIN_FAB_BOTTOM_POP_CONFIG.isContactItemInChat()) {
            MainFabBottomPopItem contactItem = MainFabBottomPopItem.newInstance()
                    .setMainFabBottomAction(MainFabBottomPopItem.MainFabBottomAction.CONTACT)
                    .setTitle(R.string.item_contact)
                    .setResId(R.mipmap.icon_contact_white);

            dataList.add(contactItem);
        }


//        if (AtworkConfig.BING_ENTRY) {
//            MainFabBottomPopItem bingItem = MainFabBottomPopItem.newInstance()
//                    .setMainFabBottomAction(MainFabBottomPopItem.MainFabBottomAction.NEW_BING)
//                    .setTitle(R.string.start_bing)
//                    .setResId(R.mipmap.icon_start_bing);
//
//            dataList.add(bingItem);
//        }

        adapter.refreshData(dataList);

    }


    public static void createContactListPopup(MainFabBottomPopAdapter adapter) {
        List<MainFabBottomPopItem> dataList = new ArrayList<>();


        if (DomainSettingsManager.getInstance().handleOrgApplyFeature()) {
            MainFabBottomPopItem applyJoinOrgItem = MainFabBottomPopItem.newInstance()
                    .setMainFabBottomAction(MainFabBottomPopItem.MainFabBottomAction.APPLY_TO_JOIN_ORG)
                    .setTitle(R.string.apply_organization)
                    .setResId(R.mipmap.icon_apply_organization);

            dataList.add(applyJoinOrgItem);
        }

        if (DomainSettingsManager.getInstance().handleOrgCreatePermission()) {
            MainFabBottomPopItem createOrgItem = MainFabBottomPopItem.newInstance()
                    .setMainFabBottomAction(MainFabBottomPopItem.MainFabBottomAction.CREATE_NEW_ORG)
                    .setTitle(R.string.create_organization)
                    .setResId(R.mipmap.icon_create_organization);

            dataList.add(createOrgItem);
        }

        if (AtworkConfig.MAIN_FAB_BOTTOM_POP_CONFIG.isFriendItemInChat()) {
            if (DomainSettingsManager.getInstance().handleFriendsRelationshipsFeature()) {
                MainFabBottomPopItem addFriendItem = MainFabBottomPopItem.newInstance()
                        .setMainFabBottomAction(MainFabBottomPopItem.MainFabBottomAction.ADD_FRIEND)
                        .setTitle(R.string.add_new_friend)
                        .setResId(R.mipmap.icon_new_friend_pop);

                dataList.add(addFriendItem);
            }
        }

        if (AtworkConfig.DISSCUSSION_CONFIG.isNeedEntry()) {
            MainFabBottomPopItem groupChatItem = MainFabBottomPopItem.newInstance()
                    .setMainFabBottomAction(MainFabBottomPopItem.MainFabBottomAction.NEW_DISCUSSION_CHAT)
                    .setTitle(R.string.more_create_discussion_chat)
                    .setResId(R.mipmap.icon_groupchat);

            dataList.add(groupChatItem);
        }


        adapter.refreshData(dataList);

    }

    public static void handleItemClick(Activity activity, MainFabBottomPopItem.MainFabBottomAction fabBottomAction) {

        switch (fabBottomAction) {
            case NEW_DISCUSSION_CHAT:
                if (true) {
                    MainFabBottomPopupHelperExtensionKt.handleNewDiscussionChat(activity);
                    break;
                }

                SelectedContactList.clear();


                break;

            case VOIP_CALL:{
                String orgCode = PersonalShareInfo.getInstance().getCurrentOrg(BaseApplicationLike.baseContext);
                OrganizationSettings organizationSetting = OrganizationSettingsManager.getInstance().getCurrentUserOrgSetting(orgCode);
                if(!organizationSetting.isAgoraEnabled()){
                    ToastHelper.showShortToast(BaseApplicationLike.baseContext,"此功能未开启");
                    return;
                }

                handleMorePopClickStartVoip(activity);
            }


                break;

            case MEETING_CALL:{
                String orgCode = PersonalShareInfo.getInstance().getCurrentOrg(BaseApplicationLike.baseContext);
                OrganizationSettings organizationSetting = OrganizationSettingsManager.getInstance().getCurrentUserOrgSetting(orgCode);
                if(!organizationSetting.isZoomEnabled()){
                    ToastHelper.showShortToast(BaseApplicationLike.baseContext,"此功能未开启");
                    return;
                }

                if(activity instanceof FragmentActivity) {
                    FragmentActivity fragmentActivity = (FragmentActivity) activity;

                    String[] meetingActions = new String[]{AtworkApplicationLike.getResourceString(R.string.meeting_reservation), AtworkApplicationLike.getResourceString(R.string.meeting_instant)};

                    WorkplusBottomPopDialog workplusBottomPopDialog = new WorkplusBottomPopDialog();
                    workplusBottomPopDialog.refreshData(meetingActions);
//                    workplusBottomPopDialog.setAllItemTextColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.common_blue_bg));

                    workplusBottomPopDialog.setItemTextDrawable(0, R.mipmap.icon_meeting_reservation_new);
                    workplusBottomPopDialog.setItemTextDrawable(1, R.mipmap.icon_meeting_instant_new);

                    workplusBottomPopDialog.setItemOnListener(tag -> {

                        workplusBottomPopDialog.dismiss();

                        SelectedContactList.clear();

                        UserSelectControlAction userSelectControlAction = new UserSelectControlAction();
                        userSelectControlAction.setSelectMode(UserSelectActivity.SelectMode.SELECT);
                        userSelectControlAction.setNeedCacheForCordova(true);

                        Intent intent = UserSelectActivity.getIntent(activity, userSelectControlAction);

                        if(AtworkApplicationLike.getResourceString(R.string.meeting_reservation).equalsIgnoreCase(tag)) {
                            activity.startActivityForResult(intent, MainActivity.CREATE_BIZCONF_MEETING_RESERVATION);
                            return;

                        }

                        if(AtworkApplicationLike.getResourceString(R.string.meeting_instant).equalsIgnoreCase(tag)) {
                            activity.startActivityForResult(intent, MainActivity.CREATE_ZOOM_MEETING_INSTANT);
                            return;
                        }




                    });

                    workplusBottomPopDialog.show(fragmentActivity.getSupportFragmentManager(), "meetingActionDialog" );
                }

            }




                break;

            case SCAN_QR_CODE:
                onQRClick(activity);

                break;

            case CREATE_NEW_ORG:
                String createNewOrgurl = UrlConstantManager.getInstance().getNewOrgUrl();
                WebViewControlAction createNewOrgWebAction = WebViewControlAction.newAction()
                        .setUrl(createNewOrgurl)
                        .setTitle(activity.getString(R.string.create_organization))
                        .setNeedShare(false);
                activity.startActivity(WebViewActivity.getIntent(activity, createNewOrgWebAction));

                break;

            case APPLY_TO_JOIN_ORG:
                String applyJoinOrgUrl = UrlConstantManager.getInstance().getSearchOrgsUrl();
                WebViewControlAction applyJoinOrgWebAction = WebViewControlAction.newAction()
                        .setUrl(applyJoinOrgUrl)
                        .setTitle(activity.getString(R.string.apply_organization))
                        .setNeedShare(false);

                activity.startActivity(WebViewActivity.getIntent(activity, applyJoinOrgWebAction));
                break;

            case ADD_FRIEND:
                String addFriendUrl = UrlConstantManager.getInstance().getSearchFriendsUrl();

                WebViewControlAction addFriendWebAction = WebViewControlAction.newAction()
                        .setUrl(addFriendUrl)
                        .setTitle(activity.getString(R.string.add_new_friend))
                        .setNeedShare(false);
                activity.startActivity(WebViewActivity.getIntent(activity, addFriendWebAction));
                break;

            case CONTACT:
                Intent contactIntent = ContactActivity.getIntent(activity);
                activity.startActivity(contactIntent);
                break;

        }

    }

    private static void addMoreViewItem(PopUpView morePopupView, int iconRes, int stringRes, int pos) {
        morePopupView.addPopItem(iconRes, stringRes, pos);
    }

    /**
     * 点击"更多"里的发起会议的事件操作
     */
    private static void handleMorePopClickStartVoip(final Activity activity) {
        //TODO:保留逻辑。。。原本想先处理一段sql语句处理
        boolean hasVoipHistory = true;

        if (hasVoipHistory) {
            Intent intent = VoipHistoryActivity.getIntent(activity);
            activity.startActivity(intent);
            return;
        }

        if (AtworkUtil.isSystemCalling()) {
            AtworkToast.showResToast(R.string.alert_is_handling_system_call);
            return;
        }

        if (VoipHelper.isHandlingVoipCall()) {
            AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);
            return;
        }

        List<ShowListItem> notAllowContactList = new ArrayList<>();
        AtworkApplicationLike.getLoginUser(new UserAsyncNetService.OnQueryUserListener() {
            @Override
            public void onSuccess(@NonNull User user) {
                notAllowContactList.add(user);

                UserSelectControlAction userSelectControlAction = new UserSelectControlAction();
                userSelectControlAction.setSelectMode(UserSelectActivity.SelectMode.SELECT);
                userSelectControlAction.setSelectAction(UserSelectActivity.SelectAction.VOIP);
                userSelectControlAction.setSelectedContacts(notAllowContactList);
                userSelectControlAction.setFromTag(MainActivity.TAG);

                Intent intent = UserSelectActivity.getIntent(activity, userSelectControlAction);
                activity.startActivityForResult(intent, MainActivity.CREATE_VOIP_MEETING);
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleBaseError(errorCode, errorMsg);
            }
        });

    }

    public static void onQRClick(Activity activity) {
        if (VoipHelper.isHandlingVoipCall()) {
            AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);
            return;
        }


        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(activity, new String[]{Manifest.permission.CAMERA}, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                Intent intent = QrcodeScanActivity.getIntent(activity);
                activity.startActivity(intent);
            }

            @Override
            public void onDenied(String permission) {
                AtworkUtil.popAuthSettingAlert(activity, permission);
            }
        });
    }


    public static void animateOut(final FloatingActionButton button) {
        if (!button.isShown()) {
            return;
        }

        if (sIsHandling) {
            return;
        }

        Interpolator INTERPOLATOR = new LinearInterpolator();

        int flyDuration = button.getHeight() + DensityUtil.dip2px(106);
        ViewCompat.animate(button).translationY(flyDuration).setInterpolator(INTERPOLATOR).withLayer().setDuration(200)
                .setListener(new ViewPropertyAnimatorListener() {
                    public void onAnimationStart(View view) {
                        sIsHandling = true;
                    }

                    public void onAnimationCancel(View view) {
                        sIsHandling = false;

                    }

                    public void onAnimationEnd(View view) {
                        sIsHandling = false;

                        view.setVisibility(View.GONE);
                    }
                }).start();


    }


    public static void doFingerAnimation(View fingerView) {
        Animation animation = AnimationUtils.loadAnimation(fingerView.getContext(), R.anim.slide_notice_finger_move);
        fingerView.startAnimation(animation);
    }

    public static void animateIn(FloatingActionButton button) {
        if (button.isShown()) {
            return;
        }

        button.setVisibility(View.VISIBLE);

        Interpolator INTERPOLATOR = new LinearInterpolator();

        ViewCompat.animate(button).translationY(0).setDuration(200)
                .setInterpolator(INTERPOLATOR).withLayer().setListener(null)
                .start();

    }

    public static void revertTranslation(FloatingActionButton button, final OnRevertTranslationListener listener) {
        Interpolator INTERPOLATOR = new LinearInterpolator();
        ViewCompat.animate(button).translationY(0).setDuration(200)
                .setInterpolator(INTERPOLATOR).withLayer().setListener(new ViewPropertyAnimatorListener() {
            @Override
            public void onAnimationStart(View view) {

            }

            @Override
            public void onAnimationEnd(View view) {
                ViewCompat.animate(button).setListener(null);
                listener.finish();
            }

            @Override
            public void onAnimationCancel(View view) {
                ViewCompat.animate(button).setListener(null);
                listener.finish();

            }
        })
                .start();
    }

    public interface OnRevertTranslationListener {
        void finish();
    }


}
