package com.foreveross.atwork.infrastructure.shared;

import android.content.Context;
import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.PreferencesUtils;
import com.foreveross.atwork.infrastructure.utils.StringUtils;

/**
 * Created by dasunsy on 2017/11/20.
 */

public class VpnSettingPersonalShareInfo extends PersonalShareInfo {

    private final static String SP_KEY_VPN_SELECTED_PREFIX = "vpn_selected_";
    private final static String SP_KEY_VPN_MANUAL_INFO_PREFIX = "vpn_manual_info_";

    private static VpnSettingPersonalShareInfo sInstance;


    public static VpnSettingPersonalShareInfo getInstance() {

        synchronized (sLock) {
            if (null == sInstance) {
                sInstance = new VpnSettingPersonalShareInfo();
            }
            return sInstance;
        }
    }

    public void selectVpn(Context context, String orgCode, String vpnId) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putString(context, getPersonalSpName(username), getVpnSelectKey(orgCode), vpnId);
    }

    public String getSelectedVpnId(Context context, String orgCode) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getString(context, getPersonalSpName(username), getVpnSelectKey(orgCode));
    }

    public void setManualVpnInfo(Context context, String orgCode, String vpnKeyId, VpnLoginInfo vpnLoginInfo) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        String vpnInfoStr = JsonUtil.toJson(vpnLoginInfo);

        PreferencesUtils.putString(context, getPersonalSpName(username), getVpnInfoKey(orgCode, vpnKeyId), vpnInfoStr);
    }

    @Nullable
    public VpnLoginInfo getManualVpnInfo(Context context, String orgCode, String vpnKeyId) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        String vpnInfoStr = PreferencesUtils.getString(context, getPersonalSpName(username), getVpnInfoKey(orgCode, vpnKeyId));

        if(StringUtils.isEmpty(vpnInfoStr)) {
            return null;
        }

        VpnLoginInfo vpnLoginInfo = JsonUtil.fromJson(vpnInfoStr, VpnLoginInfo.class);
        return vpnLoginInfo;
    }


    private String getVpnSelectKey(String orgCode) {
        return SP_KEY_VPN_SELECTED_PREFIX + orgCode;
    }

    private String getVpnInfoKey(String orgCode, String vpnKeyId) {
        return SP_KEY_VPN_MANUAL_INFO_PREFIX + orgCode + "_" + vpnKeyId;
    }

    public static class VpnLoginInfo {

        public String mUsername;

        public String mPassword;

        public static VpnLoginInfo newInstance() {
            return new VpnLoginInfo();
        }

        public VpnLoginInfo setUsername(String username) {
            mUsername = username;
            return this;
        }

        public VpnLoginInfo setPassword(String password) {
            mPassword = password;
            return this;
        }
    }

}
