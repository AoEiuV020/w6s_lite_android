package com.foreveross.atwork.modules.search.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.workplus.module.contact.activity.ShowDepartmentActivity;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.infrastructure.model.app.appEnum.BundleType;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.orgization.Department;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment;
import com.foreveross.atwork.modules.chat.model.EntrySessionRequest;
import com.foreveross.atwork.modules.contact.activity.PersonalInfoActivity;
import com.foreveross.atwork.modules.group.module.SelectToHandleAction;
import com.foreveross.atwork.modules.group.service.SelectToHandleActionService;
import com.foreveross.atwork.modules.search.activity.NewSearchActivity;
import com.foreveross.atwork.modules.search.activity.SessionSearchActivity;
import com.foreveross.atwork.modules.search.fragment.SearchFragment;
import com.foreveross.atwork.modules.search.model.SearchAction;
import com.foreveross.atwork.modules.search.model.SearchContent;
import com.foreveross.atwork.modules.search.model.SearchMessageCompatItem;
import com.foreveross.atwork.modules.search.model.SearchMessageItem;
import com.foreveross.atwork.modules.voip.activity.VoipSelectModeActivity;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.atwork.utils.IntentUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Created by dasunsy on 2017/12/13.
 */

public class SearchHelper {
    public static String getSearchContentTip(SearchContent searchContent) {
        switch (searchContent) {
            case SEARCH_ALL:
                return AtworkApplicationLike.getResourceString(R.string.all);

            case SEARCH_DEVICE:
                return AtworkApplicationLike.getResourceString(R.string.device);

            case SEARCH_APP:
                return AtworkApplicationLike.getResourceString(R.string.search_title_app);

            case SEARCH_USER:
                return AtworkApplicationLike.getResourceString(R.string.search_title_user);

            case SEARCH_DISCUSSION:
                return AtworkApplicationLike.getResourceString(R.string.search_title_group);

            case SEARCH_MESSAGES:
                return AtworkApplicationLike.getResourceString(R.string.search_title_chat);

            case  SEARCH_DEPARTMENT:
                return AtworkApplicationLike.getResourceString(R.string.organization2);

            case  SEARCH_NS_SERVICE:
                return AtworkApplicationLike.getResourceString(R.string.news_summary_search_service);

            case  SEARCH_NS_ARTICLE:
                return AtworkApplicationLike.getResourceString(R.string.news_summary_search_article);
        }

        return StringUtils.EMPTY;
    }


    public static void handleSearchResultCommonClick(Activity activity, String searchValue, ShowListItem searchContactItem, @Nullable SearchAction searchAction, @Nullable SelectToHandleAction selectToHandleAction) {

        if(null == activity) {
            return;
        }

        if(SearchAction.SELECT == searchAction) {

            if(null == selectToHandleAction) {
                handleSelect(activity, searchContactItem);

            } else {
                SelectToHandleActionService.INSTANCE.action(activity, selectToHandleAction, searchContactItem);
            }
            return;
        }

        handleClickRoute(activity, searchValue, searchContactItem, searchAction);
    }

    private static void handleClickRoute(Activity activity,String searchValue, ShowListItem searchContactItem, SearchAction searchAction) {
        LocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent(SearchFragment.ACTION_HANDLE_TOAST_INPUT));
        if (searchContactItem instanceof Discussion) {
            Discussion discussion = (Discussion) searchContactItem;
            EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest(SessionType.Discussion, discussion);
            ChatSessionDataWrap.getInstance().entrySession(entrySessionRequest);

            Intent intent = ChatDetailActivity.getIntent(activity, discussion.mDiscussionId);
            intent.putExtra(ChatDetailFragment.RETURN_BACK, true);

            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            return;
        }
        if (searchContactItem instanceof Employee) {
            Employee employee = (Employee) searchContactItem;
            UserManager.getInstance().queryUserByUserId(activity, employee.userId, employee.domainId, new UserAsyncNetService.OnQueryUserListener() {
                @Override
                public void onSuccess(@NonNull User user) {
                    handleClickUserTypeItemView(activity, user, searchAction);

                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    ErrorHandleUtil.handleBaseError(errorCode, errorMsg);
                }
            });
            return;
        }
        if (searchContactItem instanceof User) {
            User user = (User) searchContactItem;
            handleClickUserTypeItemView(activity, user, searchAction);
            return;
        }
        if (searchContactItem instanceof SearchMessageCompatItem) {
            SearchMessageCompatItem searchMessageCompatItem = (SearchMessageCompatItem) searchContactItem;
            SessionSearchActivity.Companion.startActivity(activity, searchValue, searchMessageCompatItem.mMessageList);
            activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            return;
        }
        if (searchContactItem instanceof SearchMessageItem) {
            SearchMessageItem searchMessageItem = (SearchMessageItem) searchContactItem;
            activity.startActivity(ChatDetailActivity.getIntent(activity, searchMessageItem.session.identifier, searchMessageItem.msgId));
            activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            return;
        }

        if(searchContactItem instanceof App) {
            AppBundles appBundles = ((App) searchContactItem).getMainBundle();
            if(null != appBundles) {
                handleClickRoute(activity, searchValue, appBundles, searchAction);
            }
            return;
        }

        if (searchContactItem instanceof AppBundles) {
            AppBundles bundle = (AppBundles)searchContactItem;
            if (bundle.isLightAppBundle()) {
                IntentUtil.handleLightAppClick(activity, bundle);
                return;
            }
            if (bundle.isNativeAppBundle()) {
                handleNativeAppBundle(activity, bundle);
                return;
            }
            if (bundle.isServiceAppBundle()) {
                EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest(SessionType.Service, bundle).setOrgId(bundle.mOrgId);
                ChatSessionDataWrap.getInstance().entrySession(entrySessionRequest);

                Intent intent = ChatDetailActivity.getIntent(activity, bundle.appId);
                intent.putExtra(ChatDetailFragment.RETURN_BACK, true);

                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                return;
            }
            return;
        }
        if(searchContactItem instanceof Department) {
            Department department = (Department)searchContactItem;
            ShowDepartmentActivity.startActivity(activity, department);
        }
    }

    private static void handleNativeAppBundle(Activity activity, AppBundles bundle) {
        if (BundleType.System.equals(bundle.mBundleType)) {
            IntentUtil.startSystemApp(activity, bundle.mTargetUrl);
            return;
        }
        if (AtworkApplicationLike.getInstalledApps().contains(bundle.mPackageName)) {
            IntentUtil.startApp(activity, bundle.mPackageName, true, bundle);
            return;
        }
        AtworkToast.showResToast(R.string.tip_search_app_not_installed);
    }


    private static void handleClickUserTypeItemView(Activity activity, User user, SearchAction searchAction) {
        if(null == activity) {
            return;
        }

        if (SearchAction.DEFAULT == searchAction) {
            defaultAction(activity, user);
            return;
        }


        if(SearchAction.VOIP == searchAction) {
            voipAction(activity, user);

            return;
        }

    }

    private static void defaultAction(Activity activity, User user) {

        if(user.mFileTransfer) {
            EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest(SessionType.User, user);
            ChatSessionDataWrap.getInstance().entrySession(entrySessionRequest);

            Intent intent = ChatDetailActivity.getIntent(activity, user.mUserId);
            intent.putExtra(ChatDetailFragment.RETURN_BACK, true);

            activity.startActivity(intent);

        } else {
            activity.startActivity(PersonalInfoActivity.getIntent(activity.getApplicationContext(), user));
            activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }

    }

    private static void voipAction(Activity activity, User user) {
        if (AtworkUtil.isSystemCalling()) {
            AtworkToast.showResToast(R.string.alert_is_handling_system_call);
            return;
        }

        if (VoipHelper.isHandlingVoipCall()) {
            AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);

        } else {
            AtworkApplicationLike.getLoginUser(new UserAsyncNetService.OnQueryUserListener() {
                @Override
                public void onSuccess(@NonNull User loginUser) {
                    ArrayList<ShowListItem> contactSelectedList = new ArrayList<>();
                    contactSelectedList.add(user);
                    contactSelectedList.add(loginUser);

                    activity.startActivity(VoipSelectModeActivity.getIntent(activity.getApplicationContext(), contactSelectedList));
                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    ErrorHandleUtil.handleBaseError(errorCode, errorMsg);
                }
            });

        }
    }

    private static void handleSelect(Activity activity, ShowListItem showListItem) {
        if(null == activity) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(NewSearchActivity.DATA_SEARCH_SELECT_RESULT, showListItem);
        activity.setResult(Activity.RESULT_OK, intent);
        activity.finish();
    }

}
