package com.foreveross.atwork.api.sdk.organization.responseModel;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.model.user.UserHandleBasic;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.OrgNotifyMessage;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dasunsy on 16/5/24.
 */
public class OrgApplyingCheckResponseJson extends BasicResponseJSON {

    @SerializedName("result")
    public List<ApplyingOrg> resultList;

    public class ApplyingOrg {
        @SerializedName("org_code")
        public String orgCode;

        @SerializedName("org_name")
        public String orgName;

        @SerializedName("org_logo")
        public String orgLogo;

        @SerializedName("status")
        public OrgNotifyMessage.Operation status;

        @SerializedName("intro")
        public String intro;

        @SerializedName("addresser")
        public Addresser addresser;

        @Override
        public boolean equals(Object o) {
            return this.orgCode.equals(((ApplyingOrg) o).orgCode);
        }
    }

    class Addresser extends UserHandleBasic {
        @SerializedName("name")
        public String name;

        @SerializedName("phone")
        public String phone;
    }
}
