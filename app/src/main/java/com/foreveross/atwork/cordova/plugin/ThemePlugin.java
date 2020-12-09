package com.foreveross.atwork.cordova.plugin;

import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.cordova.plugin.model.ChangeThemeRequestJson;
import com.foreveross.atwork.db.daoService.OrganizationDaoService;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.OrganizationSettingsHelper;
import com.foreveross.atwork.manager.SkinManger;
import com.foreveross.atwork.utils.CordovaHelper;
import com.foreveross.theme.model.Theme;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;

/**
 * Created by dasunsy on 2016/9/27.
 */

public class ThemePlugin extends CordovaPlugin {

    private static final String CHANGE_THEME = "changeTheme";

    private static final String GET_THEME = "getTheme";

    @Override
    public boolean execute(String action, String rawArgs, CallbackContext callbackContext) throws JSONException {
        if(CHANGE_THEME.equals(action)) {
            ChangeThemeRequestJson getEmployeeListJson = NetGsonHelper.fromCordovaJson(rawArgs, ChangeThemeRequestJson.class);

            if (null != getEmployeeListJson && !StringUtils.isEmpty(getEmployeeListJson.mOrgCode)) {

                OrganizationDaoService.getInstance().queryLoginOrgCodeList(cordova.getActivity(), codeList -> {
                    if(codeList.contains(getEmployeeListJson.mOrgCode)) {

                        OrganizationSettingsHelper.getInstance().updateThemeSetting(cordova.getActivity(), getEmployeeListJson.mOrgCode, getEmployeeListJson.mThemeName);

                        if (PersonalShareInfo.getInstance().getCurrentOrg(cordova.getActivity()).equalsIgnoreCase(getEmployeeListJson.mOrgCode)) {
                            SkinManger.getInstance().load(getEmployeeListJson.mOrgCode, null);
                        }


                    } else {
                        callbackContext.error();

                    }
                });


            } else {
                callbackContext.error();
            }

            return true;

        } else if(GET_THEME.equals(action)) {

            Theme currentTheme = SkinManger.getInstance().getDefaultTheme();
            CordovaHelper.doSuccess(currentTheme, callbackContext);

            return true;

        }


        return false;
    }
}
