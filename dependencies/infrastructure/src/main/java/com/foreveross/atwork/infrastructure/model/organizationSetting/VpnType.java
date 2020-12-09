package com.foreveross.atwork.infrastructure.model.organizationSetting;

import com.foreveross.atwork.infrastructure.utils.EnumLookupUtil;

/**
 * Created by dasunsy on 2017/11/15.
 */

public enum VpnType {
    /**
     * openVpn
     * */
    OPENVPN,


    /**
     * ipsec
     * */
    IPSEC,

    /**
     * 深信服 vpn
     * */
    SANGFOR;



    public static VpnType lookUp(String id) {
        return EnumLookupUtil.lookup(VpnType.class, id);
    }
}
