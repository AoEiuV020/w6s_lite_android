package com.foreveross.atwork.api.sdk.organization.requstModel;
/**
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
 *                            |__|
 */


import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by reyzhang22 on 16/5/11.
 */
public class CheckOrgsUpdatePostJson {

    @SerializedName("orgs")
    public List<OrgsUpdatePostItem> orgs;

    public static class OrgsUpdatePostItem {
        @SerializedName("org_code")
        public String mOrgCode;

        @SerializedName("refresh_time")
        public long mRefreshTime;
    }

    public static CheckOrgsUpdatePostJson createPostJson(List<Organization> orgList) {
        CheckOrgsUpdatePostJson postJson = new CheckOrgsUpdatePostJson();
        postJson.orgs = new ArrayList<>();
        for(Organization organization : orgList) {
            OrgsUpdatePostItem item = new OrgsUpdatePostItem();
            item.mOrgCode = organization.mOrgCode;
            item.mRefreshTime = organization.mRefreshTime;

            postJson.orgs.add(item);
        }

        return postJson;
    }

}
