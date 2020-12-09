package com.foreveross.atwork.cordova.plugin;/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 */


import android.content.Intent;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.infrastructure.model.WorkPlusInfo;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.utils.AppUtil;
import com.foreveross.atwork.infrastructure.utils.BitmapUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.modules.contact.activity.PersonalInfoActivity;
import com.foreveross.atwork.utils.CordovaHelper;
import com.foreveross.atwork.utils.ErrorHandleUtil;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 公有云插件
 * Created by reyzhang22 on 16/4/8.
 */
public class WorkPlusPublicCloudPlugin extends CordovaPlugin {

    private static final String ACTION_TO_PERSON_INFO = "toPersonal";

    private static final String ACTION_GET_APP_INFO = "getAppInfo";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (ACTION_TO_PERSON_INFO.equalsIgnoreCase(action)) {
            handleToPersonInfo(args, callbackContext);
            return true;
        }

        if (ACTION_GET_APP_INFO.equalsIgnoreCase(action)) {
            handleAppInfo(callbackContext);
            return true;
        }

        return false;
    }

    private void handleAppInfo(CallbackContext callbackContext) {
        this.cordova.getThreadPool().execute(() -> {
            WorkPlusInfo workPlusInfo = new WorkPlusInfo();
            workPlusInfo.mWorkPlusName = AppUtil.getAppName(cordova.getActivity());
            workPlusInfo.mWorkPlusPackage = AppUtil.getPackageName(cordova.getActivity());
            workPlusInfo.mWorkPlusVersionCode = AppUtil.getVersionCode(cordova.getActivity());
            workPlusInfo.mWorkPlusVersionName = AppUtil.getVersionName(cordova.getActivity());
            workPlusInfo.mWorkPlusIcons = BitmapUtil.bitmapToBase64(BitmapFactory.decodeResource(cordova.getActivity().getResources(), R.mipmap.icon_logo));

            CordovaHelper.doSuccess(workPlusInfo, callbackContext);
        });
    }

    private void handleToPersonInfo(JSONArray jsonArray, CallbackContext callbackContext) {
        JSONObject jsonObject = jsonArray.optJSONObject(0);
        boolean registered = jsonObject.optBoolean("registered");

        String userId = jsonObject.optString("user_id");
        String domainId = jsonObject.optString("domain_id");
        String name = jsonObject.optString("username");
        String phone = jsonObject.optString("mobile");

        if(registered && (StringUtils.isEmpty(userId) || StringUtils.isEmpty(domainId))) {
            callbackContext.error();

        } else {
            if(registered) {
                UserManager.getInstance().queryUserByUserId(cordova.getActivity(), userId, domainId, new UserAsyncNetService.OnQueryUserListener() {
                    @Override
                    public void onSuccess(@NonNull User user) {
                        jumpPersonalInfo(user, jsonObject, true);
                    }

                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        callbackContext.error();

                        ErrorHandleUtil.handleBaseError(errorCode, errorMsg);
                    }
                });

            } else {
                User fakeUser = new User();
                fakeUser.mUserId = userId;
                fakeUser.mDomainId = domainId;
                fakeUser.mName = name;
                fakeUser.mPhone = phone;

                jumpPersonalInfo(fakeUser, jsonObject, false);

            }


        }


    }

    public void jumpPersonalInfo(@NonNull User user, JSONObject jsonObject, boolean registered) {
        Intent intent = PersonalInfoActivity.getIntent(cordova.getActivity(), user);
        intent.putExtra(PersonalInfoActivity.USER_REGISTERED, registered);
        cordova.getActivity().startActivity(intent);
        cordova.getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

        if (jsonObject.optBoolean("finish_view", true)) {
            cordova.getActivity().finish();
        }
    }

}
