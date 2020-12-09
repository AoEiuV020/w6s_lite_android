package com.foreveross.atwork.cordova.plugin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.foreverht.threadGear.DbThreadPoolExecutor;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.Employee.EmployeeAsyncNetService;
import com.foreveross.atwork.api.sdk.contact.ContactAsyncNetService;
import com.foreveross.atwork.api.sdk.discussion.DiscussionAsyncNetService;
import com.foreveross.atwork.api.sdk.message.model.OfflineMessageResponseJson;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.cordova.plugin.model.CordovaUserHandleBasic;
import com.foreveross.atwork.cordova.plugin.model.GetCurrentEmployeeRequestJson;
import com.foreveross.atwork.cordova.plugin.model.GetCurrentUserRequestJson;
import com.foreveross.atwork.cordova.plugin.model.GetEmployeeListRequestJson;
import com.foreveross.atwork.cordova.plugin.model.SelectContactRequest;
import com.foreveross.atwork.cordova.plugin.model.SelectContactsResponse;
import com.foreveross.atwork.cordova.plugin.model.SelectDiscussionMembersRequest;
import com.foreveross.atwork.cordova.plugin.model.SelectDiscussionMembersResponse;
import com.foreveross.atwork.cordova.plugin.model.SelectScopesRequest;
import com.foreveross.atwork.cordova.plugin.model.SelectScopesResponse;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.discussion.DiscussionMember;
import com.foreveross.atwork.infrastructure.model.orgization.Scope;
import com.foreveross.atwork.infrastructure.model.user.Contact;
import com.foreveross.atwork.infrastructure.model.user.SelectedContactList;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.support.ContactProviderRepository;
import com.foreveross.atwork.infrastructure.support.StringConstants;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.MessageCovertUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.app.manager.AppManager;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.manager.EmployeeManager;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment;
import com.foreveross.atwork.modules.chat.model.EntrySessionRequest;
import com.foreveross.atwork.modules.chat.service.ChatPermissionService;
import com.foreveross.atwork.modules.contact.activity.PersonalInfoActivity;
import com.foreveross.atwork.modules.discussion.activity.DiscussionMemberSelectActivity;
import com.foreveross.atwork.modules.group.activity.UserSelectActivity;
import com.foreveross.atwork.modules.discussion.model.DiscussionMemberSelectControlAction;
import com.foreveross.atwork.modules.group.module.UserSelectControlAction;
import com.foreveross.atwork.modules.search.fragment.SearchFragment;
import com.foreveross.atwork.modules.search.model.SearchContent;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.CordovaHelper;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import kotlin.Unit;

import static com.foreveross.atwork.modules.chat.activity.ChatDetailActivity.WAITING_FOR_SEND_MESSAGES;


public class ContactPlugin_New extends WorkPlusCordovaPlugin {

    /**
     * 单选联系人
     */
    private static final String GET_CONTACT = "getContact";

    /**
     * 多选联系人
     */
    private static final String GET_CONTACT_LIST = "getContacts";


    /**
     * 多选雇员
     */
    private static final String GET_EMPLOYEE_LIST_FROM_CURRENT_ORG = "getEmployeesFromCurrentOrg";

    /**
     * 打开通用选择人员界面, 多选用户或者雇员
     * */
    private static final String SELECT_CONTACTS = "selectContacts";

    /**
     * 勾选范围(根据组织传参)
     * */
    private static final String SELECT_SCOPES = "selectScopes";



    /**
     * 获取选人缓存
     * */
    private static final String SELECT_CONTACTS_CACHE = "selectContactsCache";


    /**
     * 清除选人缓存
     * */
    private static final String CLEAR_CONTACTS_CACHE = "clearContactsCache";


    /**
     * 打开群组选人界面
     * */
    private static final String SELECT_DISCUSSION_MEMBERS = "selectDiscussionMembers";


    /**
     * 获取当前用户的用户详情(2.0 旧接口)
     */
    public static final String GET_USER_INFO = "getUserInfo";

    /**
     * 获取当前用户的用户详情
     */
    public static final String GET_CURRENT_USER_INFO = "getCurrentUserInfo";

    /**
     * 获取当前用户的雇员详情
     */
    public static final String GET_CURRENT_EMPLOYEE_INFO = "getCurrentEmployeeInfo";

    /**
     *   用户名获取用户详情
     */
    public static final String GET_USER_INFO_BY_USERNAMES = "getUserInfoByUsernames";

    /**
     * 用户名显示详情界面(2.0 旧接口)
     */
    public static final String SHOW_USER_INFO_BY_USERNAME = "showUserInfoByUsername";

    /**
     * 用户名打开聊天界面(2.0 旧接口)
     */
    public static final String SHOW_USER_CHATVIEW_BY_USERNAME = "showUserChatViewByUsername";

    /**
     * 显示详情界面(3.0 新接口)
     */
    public static final String SHOW_USER_INFO_BY_USER = "showUserInfoByUser";

    /**
     * 打开聊天界面(3.0 新接口)
     */
    public static final String SHOW_USER_CHATVIEW_BY_USER = "showUserChatViewByUser";

    /**
     * 显示app聊天页面
     */
    public static final String SHOW_APP_CHATVIEW = "showAppChatView";

    public static final String GET_MOBILE_CONTACTS = "getMobileContacts";

    /**
     * 根据 userId 获取用户信息
     * */
    public static final String GET_USER_INFO_BY_USER_ID = "getUserInfoByUserId";

    public static final String CREATE_DISCUSSION_CHAT = "createDiscussionChat";

    public static final String OPEN_DISCUSSION_BY_ID = "openDiscussionById";

    public static final String CONTACT_SEARCH = "searchInApp";

    public static final String REFRESH_USER_INFO = "refreshUserInfo";

    private static final int RESULT_CODE_GET_MULTI_CONTACT = 1;

    private static final int RESULT_CODE_GET_SINGLE_CONTACT = 2;

    private static final int RESULT_CODE_GET_EMPLOYEES = 3;

    private static final int RESULT_CREATE_DICUSSION_CHAT = 4;

    private static final int RESULT_CODE_SELECT_CONTACTS = 5;

    private static final int RESULT_CODE_SELECT_DISCUSSION_MEMBERS = 6;

    private static final int RESULT_CODE_SELECT_SCOPES = 7;

    public static final String CONTACT_SINGLE = "contact";

    public static final String INTENT_KEY_SENT_MESSAGE = "INTENT_KEY_SENT_MESSAGE";

    private CallbackContext callbackContext;

    private static List<ShowListItem> sContactSelectedCache = new ArrayList<>();


    public static void setContactSelectedCache(List<ShowListItem> contactSelectedCache) {
        sContactSelectedCache.clear();
        sContactSelectedCache.addAll(contactSelectedCache);
    }

    public static void clearContactSelectedCache() {
        sContactSelectedCache.clear();
    }


    @Override
    public boolean execute(String action, String rawArgs, final CallbackContext callbackContext) throws JSONException {
        if(!this.requestCordovaAuth())return false;

        this.callbackContext = callbackContext;
        this.cordova.setActivityResultCallback(this);

        if (GET_CONTACT.equals(action)) {
            getContact(rawArgs, callbackContext);
            return true;
        }

        //选择联系人
        if (GET_CONTACT_LIST.equals(action)) {
            getContacts(rawArgs, callbackContext);
            return true;
        }

        if(GET_EMPLOYEE_LIST_FROM_CURRENT_ORG.equalsIgnoreCase(action)) {
            getEmpListFromCurrentOrg(rawArgs, callbackContext);

            return true;
        }


        if (GET_USER_INFO.equals(action)) {
            getUserInfo(callbackContext);

            return true;
        }

        if (GET_USER_INFO_BY_USERNAMES.equals(action)) {
            getUserInfoByUsernames(rawArgs, callbackContext);
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

        if (SHOW_APP_CHATVIEW.equals(action)) {
            handleShowAppChatView(rawArgs);
            return true;
        }

        if (GET_CURRENT_USER_INFO.equals(action)) {
            getCurrentUserInfo(rawArgs, callbackContext);
            return true;
        }

        if (GET_CURRENT_EMPLOYEE_INFO.equals(action)) {
            getCurrentEmpInfo(rawArgs, callbackContext);

            return true;
        }

        if (SHOW_USER_CHATVIEW_BY_USER.equals(action)) {
            handleShowUserChatView(rawArgs);
            return true;
        }

        if (GET_MOBILE_CONTACTS.equals(action)) {
            getMobileContacts();
            return true;
        }

        if (GET_USER_INFO_BY_USER_ID.equalsIgnoreCase(action)) {
            getUserByUserId(new JSONArray(rawArgs));
            return true;
        }

        if (CREATE_DISCUSSION_CHAT.equalsIgnoreCase(action)) {
            createGroupChat(new JSONArray(rawArgs));
            return true;
        }

        if (OPEN_DISCUSSION_BY_ID.equalsIgnoreCase(action)) {
            openDiscussionById(new JSONArray(rawArgs));
            return true;
        }

        if (CONTACT_SEARCH.equalsIgnoreCase(action)) {
            openSearchView();
            return true;
        }

        if(REFRESH_USER_INFO.equalsIgnoreCase(action)) {
            //refresh user info
            refreshUserInfo(callbackContext);


            return true;
        }


        if(SELECT_CONTACTS.equalsIgnoreCase(action)) {

            selectContacts(rawArgs, callbackContext);

            return true;
        }

        if(SELECT_SCOPES.equalsIgnoreCase(action)) {
            selectScopes(rawArgs, callbackContext);
            return true;
        }


        if(SELECT_CONTACTS_CACHE.equalsIgnoreCase(action)) {

            selectContactsCache(rawArgs, callbackContext);

            return true;
        }

        if(SELECT_DISCUSSION_MEMBERS.equalsIgnoreCase(action)) {
            selectDiscussionMembers(rawArgs, callbackContext);

            return true;
        }

        return false;
    }

    private void refreshUserInfo(CallbackContext callbackContext) {
        //todo 暂时更新自己
        UserManager.getInstance().asyncFetchLoginUserFromRemote(BaseApplicationLike.baseContext, new UserAsyncNetService.OnQueryUserListener() {
            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleTokenError(errorCode, errorMsg);
                callbackContext.error(errorMsg);
            }

            @Override
            public void onSuccess(@NonNull User loginUser) {


                updateUser(loginUser);
                callbackContext.success();

            }


        });
    }

    private void updateUser(User user) {
        LoginUserInfo.getInstance().setLoginUserBasic(BaseApplicationLike.baseContext, user.mUserId, user.mDomainId, null, user.mUsername, user.mName, user.mAvatar, user.mSignature);
        UserManager.getInstance().asyncAddUserToLocal(user);
    }

    private void selectDiscussionMembers(String rawArgs, CallbackContext callbackContext) {
        SelectedContactList.clear();

        SelectDiscussionMembersRequest selectDiscussionMembersRequest = NetGsonHelper.fromCordovaJson(rawArgs, SelectDiscussionMembersRequest.class);
        if (null != selectDiscussionMembersRequest) {
            DiscussionMemberSelectControlAction discussionMemberSelectControlAction = new DiscussionMemberSelectControlAction();
            discussionMemberSelectControlAction.setSelectedContacts(selectDiscussionMembersRequest.mSelectedList);
            discussionMemberSelectControlAction.setSelectedAllowedRemove(true);
            discussionMemberSelectControlAction.setDiscussionId(selectDiscussionMembersRequest.mDiscussionId);
            discussionMemberSelectControlAction.setSelectMode(DiscussionMemberSelectActivity.Mode.SELECT);

            Intent intent = DiscussionMemberSelectActivity.getIntent(cordova.getActivity(), discussionMemberSelectControlAction);
            cordova.startActivityForResult(this, intent, RESULT_CODE_SELECT_DISCUSSION_MEMBERS);

        } else {
            callbackContext.error();
        }
    }

    private void selectContacts(String rawArgs, CallbackContext callbackContext) {
        SelectedContactList.clear();

        SelectContactRequest selectContactRequest = NetGsonHelper.fromCordovaJson(rawArgs, SelectContactRequest.class);
        boolean filterSenior = true;
        int max = -1;
        ArrayList<ShowListItem> selectedList = new ArrayList<>();


        if(null != selectContactRequest) {
            filterSenior = (1 == selectContactRequest.mFilterSenior);
            max = selectContactRequest.mMax;

            if (!ListUtil.isEmpty(selectContactRequest.mSelectedUserList)) {
                selectedList.addAll(selectContactRequest.mSelectedUserList);
            }


            if (!ListUtil.isEmpty(selectContactRequest.mSelectedEmployeeList)) {
                selectedList.addAll(selectContactRequest.mSelectedEmployeeList);
            }

        }




        UserSelectControlAction userSelectControlAction = new UserSelectControlAction();
        userSelectControlAction.setSelectMode(UserSelectActivity.SelectMode.SELECT);
        userSelectControlAction.setNeedSetNotAllowList(false);
        userSelectControlAction.setMandatoryFilterSenior(filterSenior);
        userSelectControlAction.setSuggestiveHideMe(true);
        userSelectControlAction.setMax(max);
        userSelectControlAction.setSelectCanNoOne(true);
        userSelectControlAction.setSelectedContacts(selectedList);


        Intent intent = UserSelectActivity.getIntent(cordova.getActivity(), userSelectControlAction);

        cordova.startActivityForResult(this, intent, RESULT_CODE_SELECT_CONTACTS);

        PluginResult r = new PluginResult(PluginResult.Status.NO_RESULT);
        r.setKeepCallback(true);
        callbackContext.sendPluginResult(r);
    }

    private void selectScopes(String rawArgs, CallbackContext callbackContext) {
        SelectedContactList.clear();

        SelectScopesRequest selectScopesRequest = NetGsonHelper.fromCordovaJson(rawArgs, SelectScopesRequest.class);

        UserSelectControlAction userSelectControlAction = new UserSelectControlAction();
        userSelectControlAction.setSelectMode(UserSelectActivity.SelectMode.SELECT);
        userSelectControlAction.setSelectAction(UserSelectActivity.SelectAction.SCOPE);
        userSelectControlAction.setNeedSetNotAllowList(false);
        userSelectControlAction.setSelectCanNoOne(true);
        userSelectControlAction.setDirectOrgShow(true);
        userSelectControlAction.setSuggestiveHideMe(false);

        if(null != selectScopesRequest) {
            userSelectControlAction.setDirectOrgCode(selectScopesRequest.getOrgCode());
            userSelectControlAction.setSelectScopeSet(selectScopesRequest.getSelectScopes());
        }


        Intent intent = UserSelectActivity.getIntent(cordova.getActivity(), userSelectControlAction);
        cordova.startActivityForResult(this, intent, RESULT_CODE_SELECT_SCOPES);


    }


    private void selectContactsCache(String rawArgs, CallbackContext callbackContext) {
        callbackCordovaUsersAndEmpsFromSelectContactsCache(callbackContext);
    }

    private void createGroupChat(JSONArray jsonArray) {
        User loginUser = AtworkApplicationLike.getLoginUserSync();
        List<ShowListItem> notAllowContactList = new ArrayList<>();
        notAllowContactList.add(loginUser);

        UserSelectControlAction userSelectControlAction = new UserSelectControlAction();
        userSelectControlAction.setSelectMode(UserSelectActivity.SelectMode.SELECT);
        userSelectControlAction.setSelectAction(UserSelectActivity.SelectAction.DISCUSSION);
        userSelectControlAction.setSelectedContacts(notAllowContactList);
        userSelectControlAction.setFromTag("CORDOVA_CREATE_DISCUSSION");

        Intent intent = UserSelectActivity.getIntent(cordova.getActivity(), userSelectControlAction);
        cordova.startActivityForResult(this, intent, RESULT_CREATE_DICUSSION_CHAT);
    }

    private void openDiscussionById(JSONArray jsonArray) {
        JSONObject json = jsonArray.optJSONObject(0);
        if (json == null) {
            callbackContext.error(-1);
            return;
        }
        String discussionId = json.optString("discussionId");
        DiscussionManager.getInstance().queryDiscussion(cordova.getActivity(), discussionId, new DiscussionAsyncNetService.OnQueryDiscussionListener() {
            @Override
            public void onSuccess(@NonNull Discussion discussion) {

                //检查是否包含自己
                boolean contactMe = false;
                String userId = LoginUserInfo.getInstance().getLoginUserId(cordova.getActivity());
                if (!ListUtil.isEmpty(discussion.mMemberList)) {

                    for(DiscussionMember member : discussion.mMemberList) {
                        member.discussionId = discussion.mDiscussionId;
                        if (member.userId.equalsIgnoreCase(userId)) {
                            contactMe = true;
                        }
                    }
                }
                if (!contactMe) {
                    AtworkToast.showResToast(R.string.discussion_not_found);
                    callbackContext.error();
                    return;
                }

                String messageType = json.optString("body_type");
                PostTypeMessage message = null;
                if (!TextUtils.isEmpty(messageType)) {
                    message = newMessage(json.toString(), discussionId, discussion.mDomainId, discussion.mName, discussion.mAvatar);
                    if (message instanceof TextChatMessage) {
                        StringBuilder stringBuilder = new StringBuilder();
                        TextChatMessage textChatMessage = (TextChatMessage)message;
                        if (textChatMessage.atAll) {
                            stringBuilder.append(StringConstants.SESSION_CONTENT_AT_ALL).append(" ").append(textChatMessage.text);
                            textChatMessage.text = stringBuilder.toString();
                        }
                        if (!ListUtil.isEmpty(textChatMessage.mAtUserList)) {
                            for (int i = 0; i < textChatMessage.mAtUserList.size(); i++) {
                                String name = "";
                                Object object = textChatMessage.mAtUserList.get(i);
                                if (object instanceof TextChatMessage.AtUser) {
                                    TextChatMessage.AtUser atUser = (TextChatMessage.AtUser) object;
                                    name = atUser.mName;
                                }
                                if (object instanceof LinkedTreeMap) {
                                    LinkedTreeMap<String, String> map = (LinkedTreeMap) object;
                                    name = map.get("name");

                                }
                                stringBuilder.append("@").append(name).append(" ");
                            }
                            stringBuilder.append(textChatMessage.text);
                            textChatMessage.text = stringBuilder.toString();
                        }
                    }
                }
                //创建会话
                EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest(SessionType.Discussion, discussion);
                ChatSessionDataWrap.getInstance().entrySessionSafely(entrySessionRequest);

                Intent intent = ChatDetailActivity.getIntent(cordova.getActivity(), discussion.mDiscussionId);
                if (message != null) {
                    List<PostTypeMessage> messages = new ArrayList<PostTypeMessage>();
                    messages.add(message);
                    intent.putExtra(WAITING_FOR_SEND_MESSAGES, (java.io.Serializable)OfflineMessageResponseJson.toChatPostMessages(messages));
                }
                cordova.getActivity().startActivity(intent);
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                AtworkToast.showResToast(R.string.discussion_not_found);
                callbackContext.error();

            }
        });
    }

    private void openSearchView() {
        SearchFragment searchFragment = new SearchFragment();
        searchFragment.addSearch(SearchContent.SEARCH_APP);
        searchFragment.addSearch(SearchContent.SEARCH_DISCUSSION);
        searchFragment.addSearch(SearchContent.SEARCH_MESSAGES);
        searchFragment.addSearch(SearchContent.SEARCH_USER);
        searchFragment.show(cordova.getFragment().getFragmentManager(), "SearchDialog");
    }

    private PostTypeMessage newMessage(String json, String participantId, String participantDomainId, String participantName, String participantAvatar) {
        PostTypeMessage message = MessageCovertUtil.covertJsonToMessage(json.toString());
        if (message == null) {
            return message;
        }
        message.deliveryId = UUID.randomUUID().toString();
        message.chatSendType = ChatSendType.SENDER;
        if(message instanceof FileTransferChatMessage){
            FileTransferChatMessage fileTransferChatMessage = (FileTransferChatMessage)message;
            if (LoginUserInfo.getInstance().getLoginUserId(cordova.getActivity()).equals(message.from)) {
                fileTransferChatMessage.fileStatus = FileStatus.SENDED;
            } else {
                fileTransferChatMessage.fileStatus = FileStatus.NOT_DOWNLOAD;
            }

        }
        if (message instanceof VoiceChatMessage){
            VoiceChatMessage voiceChatMessage = (VoiceChatMessage) message;
            voiceChatMessage.play = true;
        }

        message.chatStatus = ChatStatus.Sended;
        User loginUser = AtworkApplicationLike.getLoginUserSync();
        message.from = loginUser.mUserId;
        message.mFromDomain = loginUser.mDomainId;
        message.mFromType = ParticipantType.User;
        message.to = participantId;
        message.mToType = ParticipantType.Discussion;
        message.mToDomain = participantDomainId;
        message.mMyAvatar = loginUser.mAvatar;
        message.mMyName = loginUser.mName;
        message.mDisplayAvatar = participantAvatar;
        message.mDisplayName = participantName;
        return message;
    }

    public void getUserInfoByUsernames(String rawArgs, CallbackContext callbackContext) throws JSONException {
        final JSONArray jsonArray = new JSONArray(rawArgs);
        final List<String> nameList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            nameList.add(jsonObject.getString("username"));
        }
        cordova.getThreadPool().execute(() -> {
            ContactAsyncNetService contactAsyncNetService = ContactAsyncNetService.getInstance();
            List<Contact> contactList = contactAsyncNetService.batchQueryContacts(BaseApplicationLike.baseContext, nameList);

            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().serializeNulls().create();

            try {
                JSONArray jsonObject = new JSONArray(gson.toJson(contactList));
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
                callbackContext.sendPluginResult(pluginResult);
                callbackContext.success();
            } catch (Exception e) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("message", "获取失败");
                    callbackContext.error(jsonObject);
                } catch (Exception e1) {
                    LogUtil.e("error!", e.getMessage(), e);
                }

            }
        });
    }

    public void getEmpListFromCurrentOrg(String rawArgs, CallbackContext callbackContext) {
        SelectedContactList.clear();

        GetEmployeeListRequestJson getEmployeeListJson = NetGsonHelper.fromCordovaJson(rawArgs, GetEmployeeListRequestJson.class);

        UserSelectControlAction userSelectControlAction = new UserSelectControlAction();
        userSelectControlAction.setSelectMode(UserSelectActivity.SelectMode.SELECT);
        userSelectControlAction.setNeedSetNotAllowList(false);
        userSelectControlAction.setDirectOrgShow(true);


        if (null != getEmployeeListJson) {

            userSelectControlAction.setMandatoryFilterSenior(1 == getEmployeeListJson.mFilterSenior);
            userSelectControlAction.setMax(getEmployeeListJson.max);
            userSelectControlAction.setCallbackContactsSelected(getEmployeeListJson.selectedEmpList);
            userSelectControlAction.setSuggestiveHideMe(getEmployeeListJson.hideMe);
        } else {
            userSelectControlAction.setSuggestiveHideMe(false);
        }
        userSelectControlAction.setSelectCanNoOne(true);

        Intent intent = UserSelectActivity.getIntent(cordova.getActivity(), userSelectControlAction);


        cordova.startActivityForResult(this, intent, RESULT_CODE_GET_EMPLOYEES);

        PluginResult r = new PluginResult(PluginResult.Status.NO_RESULT);
        r.setKeepCallback(true);
        callbackContext.sendPluginResult(r);
    }

    public void getContact(String rawArgs, CallbackContext callbackContext) throws JSONException {
        SelectedContactList.clear();

        JSONObject jsonObject;
        boolean isHideMe = false;
        boolean isMandatoryFilterSenior = true;

        if (!TextUtils.isEmpty(rawArgs) && !rawArgs.equals("[]")) {
            if (rawArgs.contains("hideMe")) {
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
    }

    public void getContacts(String rawArgs, CallbackContext callbackContext) throws JSONException {
        SelectedContactList.clear();

        ArrayList<User> selectedUserList = null;
        JSONObject jsonObject;
        boolean isHideMe = false;
        boolean isMandatoryFilterSenior = true;

        if (!TextUtils.isEmpty(rawArgs) && !rawArgs.equals("[]")) {
            JSONArray jsonArray = new JSONArray(rawArgs);
            jsonObject = jsonArray.getJSONObject(0);


            if (rawArgs.contains("selectedContacts")) {

                String contactLists = jsonObject.getString("selectedContacts");
                if (!StringUtils.isEmpty(contactLists)) {
                    selectedUserList = CordovaHelper.getCompatibleSelectedUsers(contactLists);
                }

            }

            if (rawArgs.contains("hideMe")) {
                isHideMe = jsonObject.getBoolean("hideMe");
            }

            if (rawArgs.contains("filterSenior")) {
                isMandatoryFilterSenior = (1 == jsonObject.getInt("filterSenior"));
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
    }



    /**
     * 获取用户信息(2.0旧接口)
     * */
    public void getUserInfo(final CallbackContext callbackContext) {
        AtworkApplicationLike.getLoginUser(new UserAsyncNetService.OnQueryUserListener() {
            @Override
            public void onSuccess(@NonNull User user) {
                String currentOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(cordova.getActivity());

                EmployeeManager.getInstance().queryEmp(cordova.getActivity(), user.mUserId, currentOrgCode, new EmployeeAsyncNetService.QueryEmployeeInfoListener() {
                    @Override
                    public void onSuccess(@NonNull Employee employee) {
                        if(StringUtils.isEmpty(employee.avatar)) {
                            employee.avatar = user.getAvatar();
                        }

                        JSONObject empJsonObj = CordovaHelper.getCompatibleContact(employee);
                        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, empJsonObj);
                        callbackContext.sendPluginResult(pluginResult);
                        callbackContext.success();
                    }

                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        ErrorHandleUtil.handleBaseError(errorCode, errorMsg);
                    }
                });

            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleTokenError(errorCode, errorMsg);
            }
        });
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
                    callbackContext.error();

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

                    JSONObject json = jsonArray.optJSONObject(0);
                    String messageType = json.optString("body_type");
                    PostTypeMessage message = null;
                    if (!TextUtils.isEmpty(messageType)) {
                        message = newMessage(json.toString(), user.mUserId, user.mDomainId, user.mName, user.mAvatar);
                    }
                    EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest(SessionType.User, user);
                    ChatSessionDataWrap.getInstance().entrySessionSafely(entrySessionRequest);

                    Intent intent = ChatDetailActivity.getIntent(cordova.getActivity(), user.mUserId);
                    if (message != null) {
                        List<PostTypeMessage> messages = new ArrayList<>();
                        messages.add(message);
                        intent.putExtra(WAITING_FOR_SEND_MESSAGES, (java.io.Serializable)OfflineMessageResponseJson.toChatPostMessages(messages));
                    }
                    intent.putExtra(ChatDetailFragment.RETURN_BACK, true);

                    cordova.getActivity().startActivity(intent);
                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    callbackContext.error();

                    ErrorHandleUtil.handleBaseError(errorCode, errorMsg);
                }
            });
            return;
        }
        callbackContext.error();
    }

    private void handleShowAppChatView(String rawArgs) throws JSONException {
        JSONArray jsonArray = new JSONArray(rawArgs);
        JSONObject json = jsonArray.optJSONObject(0);
        final String appId = json.optString("app_id");
        final String orgId = json.optString("org_id");
        final SessionType sessionType = SessionType.toType(json.optString("session_type"));
        if (!StringUtils.isEmpty(appId)) {
            AppManager.getInstance().queryApp(cordova.getActivity(), appId, orgId, new AppManager.GetAppFromMultiListener() {
                @Override
                public void onSuccess(@NonNull App app) {
                    PostTypeMessage message = null;
                    EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest(sessionType, app);
                    ChatSessionDataWrap.getInstance().entrySessionSafely(entrySessionRequest);

                    Intent intent = ChatDetailActivity.getIntent(cordova.getActivity(), appId);
                    if (message != null) {
                        List<PostTypeMessage> messages = new ArrayList<>();
                        messages.add(message);
                        intent.putExtra(WAITING_FOR_SEND_MESSAGES, (java.io.Serializable)OfflineMessageResponseJson.toChatPostMessages(messages));
                    }
                    intent.putExtra(ChatDetailFragment.RETURN_BACK, true);

                    cordova.getActivity().startActivity(intent);
                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    callbackContext.error();
                    ErrorHandleUtil.handleBaseError(errorCode, errorMsg);
                }
            });
            return;
        }
        callbackContext.error();
    }

    /**
     * 处理聊天界面跳转
     * */
    public void handleShowUserChatView(String rawArgs) {
        CordovaUserHandleBasic userHandleBasic = NetGsonHelper.fromCordovaJson(rawArgs, CordovaUserHandleBasic.class);
        if(checkShowChatViewUserLegal(userHandleBasic)) {

            ChatPermissionService.INSTANCE.canChat(new ArrayList<>(), userHandleBasic.mUserId, userHandleBasic.mDomainId, result -> {
                switch (result) {
                    case CAN_TALK:
                        doShowUserChatView(rawArgs, userHandleBasic);
                        break;

                    case CANNOT_TALK:
                    case MAY_NOT_TALK:
                        AtworkToast.showToast(DomainSettingsManager.getInstance().getConnectionNonsupportPrompt());
                        break;

                    case NETWORK_FAILED:
                        AtworkToast.showResToast(R.string.network_not_avaluable);
                        break;
                }

                return Unit.INSTANCE;
            });



        } else {
            callbackContext.error();

        }
    }

    private void doShowUserChatView(String rawArgs, CordovaUserHandleBasic userHandleBasic) {
        UserManager.getInstance().queryUserByUserId(cordova.getActivity(), userHandleBasic.mUserId, userHandleBasic.mDomainId, new UserAsyncNetService.OnQueryUserListener() {
            @Override
            public void onSuccess(@NonNull User user) {
                JSONObject json = null;
                try {
                    json = new JSONArray(rawArgs).optJSONObject(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String messageType = json.optString("body_type");
                PostTypeMessage message = null;
                if (!TextUtils.isEmpty(messageType)) {
                    message = newMessage(json.toString(), user.mUserId, user.mDomainId, user.mName, user.mAvatar);
                }

                EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest(SessionType.User, user);
                ChatSessionDataWrap.getInstance().entrySessionSafely(entrySessionRequest);

                Intent intent = ChatDetailActivity.getIntent(cordova.getActivity(), user.mUserId);
                if (message != null) {
                    List<PostTypeMessage> messages = new ArrayList<PostTypeMessage>();
                    messages.add(message);
                    intent.putExtra(WAITING_FOR_SEND_MESSAGES, (java.io.Serializable) OfflineMessageResponseJson.toChatPostMessages(messages));
                }
                intent.putExtra(ChatDetailFragment.RETURN_BACK, true);

                cordova.getActivity().startActivity(intent);
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {

                ErrorHandleUtil.handleBaseError(errorCode, errorMsg);
            }
        });
    }

    private boolean checkShowChatViewUserLegal(CordovaUserHandleBasic userHandleBasic) {
        return null != userHandleBasic && !StringUtils.isEmpty(userHandleBasic.mUserId) && !StringUtils.isEmpty(userHandleBasic.mDomainId);
    }

    public void getCurrentEmpInfo(String rawArgs, final CallbackContext callbackContext) {
        GetCurrentEmployeeRequestJson getCurrentEmployeeRequestJson = NetGsonHelper.fromCordovaJson(rawArgs, GetCurrentEmployeeRequestJson.class);
        String orgCode = null;
        if (null != getCurrentEmployeeRequestJson) {
            orgCode = getCurrentEmployeeRequestJson.mOrgCode;

        }

        if(StringUtils.isEmpty(orgCode)) {
            orgCode = PersonalShareInfo.getInstance().getCurrentOrg(cordova.getActivity());
        }

        EmployeeManager.getInstance().queryEmp(cordova.getActivity(), LoginUserInfo.getInstance().getLoginUserId(cordova.getActivity()), orgCode, new EmployeeAsyncNetService.QueryEmployeeInfoListener() {
            @Override
            public void onSuccess(@NonNull Employee employee) {
                CordovaHelper.doSuccess(employee, callbackContext);

            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                callbackContext.error();

                ErrorHandleUtil.handleTokenError(errorCode, errorMsg);

            }
        });
    }

    public void getCurrentUserInfo(String rawArgs, final CallbackContext callbackContext) {
        GetCurrentUserRequestJson getCurrentUserRequestJson = NetGsonHelper.fromCordovaJson(rawArgs, GetCurrentUserRequestJson.class);

        AtworkApplicationLike.getLoginUser(new UserAsyncNetService.OnQueryUserListener() {
            @Override
            public void onSuccess(@NonNull User user) {
                if (null == getCurrentUserRequestJson || getCurrentUserRequestJson.mNeedEmpInfo) {
                    String currentOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(cordova.getActivity());

                    if (StringUtils.isEmpty(currentOrgCode)) {
                        CordovaHelper.doSuccess(user, callbackContext);

                    } else {
                        EmployeeManager.getInstance().queryEmp(cordova.getActivity(), user.mUserId, currentOrgCode, new EmployeeAsyncNetService.QueryEmployeeInfoListener() {
                            @Override
                            public void onSuccess(@NonNull Employee employee) {
                                user.mCurrentEmp = employee;

                                CordovaHelper.doSuccess(user, callbackContext);

                            }

                            @Override
                            public void networkFail(int errorCode, String errorMsg) {
                                CordovaHelper.doSuccess(user, callbackContext);

                            }
                        });

                    }

                } else {
                    CordovaHelper.doSuccess(user, callbackContext);
                }

            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleTokenError(errorCode, errorMsg);
            }
        });
    }


    private void getUserByUserId(JSONArray jsonArray) {
        JSONObject jsonObject = jsonArray.optJSONObject(0);
        final String userId = jsonObject.optString("user_id");
        final String domainId = jsonObject.optString("domain_id");
        if (TextUtils.isEmpty(userId)) {
            callbackContext.error();

            Logger.d("plugin", "user id is null");
            return;
        }
        UserManager.getInstance().queryUserByUserId(cordova.getActivity(), userId, domainId, new UserAsyncNetService.OnQueryUserListener() {

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                callbackContext.error();

                ErrorHandleUtil.handleBaseError(errorCode, errorMsg);
            }

            @Override
            public void onSuccess(@NonNull User loginUser) {
                CordovaHelper.doSuccess(loginUser, callbackContext);
            }


        });

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
                callbackContext.success(jsonArray);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return;
        }

        if (RESULT_CODE_GET_SINGLE_CONTACT == requestCode) {
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
                    callbackContext.success(jsonArray);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return;

        }
        if(RESULT_CODE_GET_EMPLOYEES == requestCode) {
            List<ShowListItem> contactList = SelectedContactList.getContactList();
            List<Employee> empList = new ArrayList<>();
            for(ShowListItem contact : contactList) {
                if(contact instanceof Employee) {
                    Employee emp = (Employee) contact;
                    emp.mJobTitle = emp.getSearchShowJobTitle();

                    empList.add(emp);

                } else if(contact instanceof User) {
                    User user = (User) contact;
                    empList.add(user.toEmployee());
                }
            }
            try {
                JSONArray jsonArray = new JSONArray(JsonUtil.toJsonList(empList));
                callbackContext.success(jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return;
        }

        if (RESULT_CREATE_DICUSSION_CHAT == requestCode) {
            ProgressDialogHelper progressDialogHelper = new ProgressDialogHelper(cordova.getActivity());
            progressDialogHelper.show(cordova.getActivity().getString(R.string.create_group_ing));
            List<ShowListItem> contactList = SelectedContactList.getContactList();
            if (contactList.size() == 1) {
                progressDialogHelper.dismiss();
                AtworkToast.showResToast(R.string.create_discussion_three_people_at_least);
                return;
            }
            DiscussionManager.getInstance().createDiscussion(cordova.getActivity(), contactList, null, null, null,null, true, new DiscussionAsyncNetService.OnCreateDiscussionListener() {
                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    progressDialogHelper.dismiss();
                    ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Group, errorCode, errorMsg);
                }

                @Override
                public void onDiscussionSuccess(Discussion discussion) {
                    progressDialogHelper.dismiss();
                    if (null != discussion) {
                        CordovaHelper.doSuccess(discussion, callbackContext);
                    }
                }
            });

            return;
        }


        if(RESULT_CODE_SELECT_CONTACTS == requestCode) {
            callbackCordovaUsersAndEmpsFromSelectContacts(callbackContext);

            return;
        }


        if(RESULT_CODE_SELECT_SCOPES == requestCode) {
            List<Scope> scopeContactList = SelectedContactList.getScopeList();
            SelectScopesResponse response = new SelectScopesResponse();
            response.flatResult(scopeContactList);

            CordovaHelper.doSuccess(response, callbackContext);

            return;
        }



        if(RESULT_CODE_SELECT_DISCUSSION_MEMBERS == requestCode) {
            List<ShowListItem> contactList = SelectedContactList.getContactList();
            SelectDiscussionMembersResponse response = new SelectDiscussionMembersResponse();
            try {
                for(ShowListItem contact : contactList) {

                    if(contact instanceof User) {
                        User user = (User) contact;
                        response.mSelectedList.add(user.toEmployee());

                    } else if(contact instanceof Employee) {
                        Employee employee = (Employee) contact;
                        response.mSelectedList.add(employee);
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            CordovaHelper.doSuccess(response, callbackContext);


            return;

        }
    }


    private void callbackCordovaUsersAndEmpsFromSelectContactsCache(@NonNull CallbackContext callbackContext) {
        callbackCordovaUsersAndEmps(sContactSelectedCache, callbackContext);
    }

    private void callbackCordovaUsersAndEmpsFromSelectContacts(@NonNull CallbackContext callbackContext) {
        List<ShowListItem> contactList = SelectedContactList.getContactList();
        callbackCordovaUsersAndEmps(contactList, callbackContext);
    }

    private void callbackCordovaUsersAndEmps(List<ShowListItem> contactList, @NonNull CallbackContext callbackContext) {
        SelectContactsResponse selectContactsResponse = new SelectContactsResponse();

        for(ShowListItem contact : contactList) {
            if(contact instanceof Employee) {
                Employee emp = (Employee) contact;
                emp.mJobTitle = emp.getSearchShowJobTitle();

                selectContactsResponse.mEmployees.add(emp);

            } else if(contact instanceof User) {
                User user = (User) contact;
                selectContactsResponse.mUsers.add(user);
            }
        }

        CordovaHelper.doSuccess(selectContactsResponse, callbackContext);
    }


    @SuppressLint("StaticFieldLeak")
    private void getMobileContacts() {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(cordova.getActivity(), new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, new PermissionsResultAction() {
            @Override
            public void onGranted() {

                new AsyncTask<Void, Void, JSONObject>(){

                    @Override
                    protected JSONObject doInBackground(Void... voids) {
                        ContactProviderRepository repository = new ContactProviderRepository(cordova.getActivity().getContentResolver());
                        JSONObject mobileContacts = repository.getMobileContacts();
                        return mobileContacts;
                    }

                    @Override
                    protected void onPostExecute(JSONObject jsonObject) {
                        callbackContext.success(jsonObject);

                    }
                }.executeOnExecutor(DbThreadPoolExecutor.getInstance());

            }

            @Override
            public void onDenied(String permission) {
//                AtworkUtil.popAuthSettingAlert(cordova.getActivity(), permission);

                //没有权限需要传入空的对象, 轻应用拿到了进行UI 显示
                JSONObject emptyObj = new JSONObject();
                callbackContext.success(emptyObj);
            }
        });
    }

}
