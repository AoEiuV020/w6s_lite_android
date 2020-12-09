package com.foreveross.atwork.cordova.plugin;

import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.api.sdk.contact.ContactAsyncNetService;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.cordova.plugin.model.PluginError;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.user.Contact;
import com.foreveross.atwork.infrastructure.model.user.SelectedContactList;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment;
import com.foreveross.atwork.modules.chat.model.EntrySessionRequest;
import com.foreveross.atwork.modules.contact.activity.PersonalInfoActivity;
import com.foreveross.atwork.modules.group.activity.UserSelectActivity;
import com.foreveross.atwork.modules.group.module.UserSelectControlAction;
import com.foreveross.atwork.utils.CordovaHelper;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingen on 15/5/1.
 * 联系人选择插件
 */
public class ContactPlugin extends CordovaPlugin {

    /**
     * 单选联系人
     */
    private static final String GET_CONTACT = "getContact";

    /**
     * 多选联系人
     */
    private static final String GET_CONTACT_LIST = "getContactList";

    /**
     * 获取当前用户详情
     */
    public static final String GET_USER_INFO = "getUserInfo";

    /**
     * 根据用户名获取用户详情
     */
    public static final String GET_USER_INFO_BY_USERNAMES = "getUserInfoByUsernames";

    /**
     * 根据用户名显示详情界面
     */
    public static final String SHOW_USER_INFO_BY_USERNAME = "showUserInfoByUsername";

    public static final String SHOW_USER_CHATVIEW_BY_USERNAME = "showUserChatViewByUsername";


    private static final int RESULT_CODE_GET_MULTI_CONTACT = 1;
    private static final int RESULT_CODE_GET_SINGLE_CONTACT = 2;

    private CallbackContext callbackContext;


    @Override
    public boolean execute(String action, String rawArgs, final CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        this.cordova.setActivityResultCallback(this);

        if (GET_CONTACT.equals(action)) {
            JSONObject jsonObject;
            boolean isHideMe = false;
            boolean isMandatoryFilterSenior = true;

            if (!TextUtils.isEmpty(rawArgs) && !rawArgs.equals("[]")) {
                if (rawArgs.contains("hideMe")){
                    JSONArray jsonArray = new JSONArray(rawArgs);
                    jsonObject = jsonArray.getJSONObject(0);
                    isHideMe = jsonObject.getBoolean("hideMe");
                }

                if (rawArgs.contains("filterSenior")) {
                    JSONArray jsonArray = new JSONArray(rawArgs);
                    jsonObject = jsonArray.getJSONObject(0);
                    isMandatoryFilterSenior = (1 == jsonObject.getInt("filterSenior"));
                }

            }

            UserSelectControlAction userSelectControlAction = new UserSelectControlAction();
            userSelectControlAction.setSelectMode(UserSelectActivity.SelectMode.SELECT);
            userSelectControlAction.setNeedSetNotAllowList(false);
            userSelectControlAction.setMandatoryFilterSenior(isMandatoryFilterSenior);
            userSelectControlAction.setSuggestiveHideMe(isHideMe);
            userSelectControlAction.setDirectOrgShow(true);
            userSelectControlAction.setMax(1);

            Intent intent = UserSelectActivity.getIntent(cordova.getActivity(), userSelectControlAction);


            cordova.startActivityForResult(this, intent, RESULT_CODE_GET_SINGLE_CONTACT);
            PluginResult r = new PluginResult(PluginResult.Status.NO_RESULT);
            r.setKeepCallback(true);
            callbackContext.sendPluginResult(r);
            return true;
        }

        //选择联系人
        if (GET_CONTACT_LIST.equals(action)) {
            ArrayList<User> selectedUserList = null;
            JSONObject jsonObject;
            boolean isHideMe = false;
            boolean isMandatoryFilterSenior = true;

            if (!TextUtils.isEmpty(rawArgs) && !rawArgs.equals("[]")) {
                if (rawArgs.contains("selectedContacts")) {
                    JSONArray jsonArray = new JSONArray(rawArgs);
                    jsonObject = jsonArray.getJSONObject(0);
                    String contactLists = jsonObject.getString("selectedContacts");
                    if (!StringUtils.isEmpty(contactLists)) {
                        selectedUserList = CordovaHelper.getCompatibleSelectedUsers(contactLists);
                    }
                    if (rawArgs.contains("hideMe")) {
                        isHideMe = jsonObject.getBoolean("hideMe");
                    }

                    if (rawArgs.contains("filterSenior")) {
                        isMandatoryFilterSenior = (1 == jsonObject.getInt("filterSenior"));
                    }

                }
            }


            UserSelectControlAction userSelectControlAction = new UserSelectControlAction();
            userSelectControlAction.setSelectMode(UserSelectActivity.SelectMode.SELECT);
            userSelectControlAction.setNeedSetNotAllowList(false);
            userSelectControlAction.setMandatoryFilterSenior(isMandatoryFilterSenior);
            userSelectControlAction.setSuggestiveHideMe(isHideMe);
            userSelectControlAction.setCallbackContactsSelected(selectedUserList);
            userSelectControlAction.setDirectOrgShow(true);
            userSelectControlAction.setSelectCanNoOne(true);

            Intent intent = UserSelectActivity.getIntent(cordova.getActivity(), userSelectControlAction);

            cordova.startActivityForResult(this, intent, RESULT_CODE_GET_MULTI_CONTACT);
            PluginResult r = new PluginResult(PluginResult.Status.NO_RESULT);
            r.setKeepCallback(true);
            callbackContext.sendPluginResult(r);
            return true;
        }

        if (GET_USER_INFO.equals(action)) {
            cordova.getThreadPool().execute(() -> queryCurrentLoginInfo(callbackContext));
            return true;
        }

        if (GET_USER_INFO_BY_USERNAMES.equals(action)) {
            JSONArray jsonArray = new JSONArray(rawArgs);
            final List<String> nameList = new ArrayList<>();
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                nameList.add(jsonObject.getString("username"));
            }
            cordova.getThreadPool().execute(() -> {
                ContactAsyncNetService contactAsyncNetService = ContactAsyncNetService.getInstance();
                List<Contact> contactList = contactAsyncNetService.batchQueryContacts(BaseApplicationLike.baseContext, nameList);

                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().serializeNulls().create();

                try {
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, gson.toJson(contactList));
                    callbackContext.sendPluginResult(pluginResult);
                    callbackContext.success();
                } catch (Exception e) {
                    LogUtil.e("error!", e.getMessage(), e);
                    PluginError pluginError = PluginError.createInstance(e.getLocalizedMessage());
                    callbackContext.error(gson.toJson(pluginError));
                }
            });
            return true;
        }

        //显示个人用户信息
        if (SHOW_USER_INFO_BY_USERNAME.equals(action)) {
            handleShowUserInfoOldVersion(rawArgs);
            return true;
        }

        if (SHOW_USER_CHATVIEW_BY_USERNAME.equals(action)) {
            handleShowChatViewOldVersion(rawArgs);
            return true;

        }
        return false;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (intent == null) {
            return;
        }

        if (RESULT_CODE_GET_MULTI_CONTACT == requestCode) {
            List<ShowListItem> contactList = SelectedContactList.getContactList();

            try {
                JSONArray jsonArray = new JSONArray();
                for (ShowListItem contact : contactList) {
                    if(contact instanceof User) {
                        contact = ((User)contact).toEmployee();
                    }

                    if(contact instanceof Employee) {
                        JSONObject jsonObject = CordovaHelper.getCompatibleContact((Employee) contact);
                        jsonArray.put(jsonObject);
                    }

                }
                callbackContext.success(jsonArray.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (RESULT_CODE_GET_SINGLE_CONTACT == requestCode) {
            List<ShowListItem> contactList = SelectedContactList.getContactList();

            try {
                JSONArray jsonArray = new JSONArray();
                ShowListItem contact = contactList.get(0);

                if(contact instanceof User) {
                    contact = ((User)contact).toEmployee();
                }

                if(contact instanceof Employee) {
                    JSONObject jsonObject = CordovaHelper.getCompatibleContact((Employee) contact);
                    jsonArray.put(jsonObject);
                    callbackContext.success(jsonArray.toString());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 获取当前登录用户信息
     */
    private void queryCurrentLoginInfo(CallbackContext callbackContext) {
        User user = AtworkApplicationLike.getLoginUserSync();
        if (null != user) {
            user.mUsername =  LoginUserInfo.getInstance().getLoginUserUserName(BaseApplicationLike.baseContext);
        }
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().serializeNulls().create();

        try {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, gson.toJson(user));
            callbackContext.sendPluginResult(pluginResult);
            callbackContext.success();
        } catch (Exception e) {
            LogUtil.e("error!", e.getMessage(), e);
            PluginError pluginError = PluginError.createInstance(e.getLocalizedMessage());
            callbackContext.error(gson.toJson(pluginError));
        }
    }

    /**
     * 处理个人详情界面跳转(针对旧版本的接口, 使用 username)
     * */
    public void handleShowUserInfoOldVersion(String rawArgs) throws JSONException {
        JSONArray jsonArray = new JSONArray(rawArgs);
        final String username = jsonArray.optJSONObject(0).optString("username");

        if (!StringUtils.isEmpty(username)) {

            UserManager.getInstance().queryUserByUserName(cordova.getActivity(), username, AtworkConfig.DOMAIN_ID, new UserAsyncNetService.OnQueryUserListener() {
                @Override
                public void onSuccess(@NonNull User user) {
                    cordova.getActivity().startActivity(PersonalInfoActivity.getIntent(cordova.getActivity(), user));

                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    ErrorHandleUtil.handleBaseError(errorCode, errorMsg);
                }
            });



        } else {
            callbackContext.error();
        }

    }

    /**
     * 处理聊天界面跳转(针对旧版本的接口, 使用 username)
     * */
    public void handleShowChatViewOldVersion(String rawArgs) throws JSONException {
        JSONArray jsonArray = new JSONArray(rawArgs);
        final String username = jsonArray.optJSONObject(0).optString("username");
        if (!StringUtils.isEmpty(username)) {

            UserManager.getInstance().queryUserByUserName(cordova.getActivity(), username, AtworkConfig.DOMAIN_ID, new UserAsyncNetService.OnQueryUserListener() {
                @Override
                public void onSuccess(@NonNull User user) {
                    EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest(SessionType.User, user);
                    ChatSessionDataWrap.getInstance().entrySessionSafely(entrySessionRequest);

                    Intent intent = ChatDetailActivity.getIntent(cordova.getActivity(), user.mUserId);
                    intent.putExtra(ChatDetailFragment.RETURN_BACK, true);

                    cordova.getActivity().startActivity(intent);
                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    ErrorHandleUtil.handleBaseError(errorCode, errorMsg);
                }
            });



        } else {
            callbackContext.error();
        }
    }
}
