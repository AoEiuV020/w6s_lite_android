package com.foreveross.atwork.api.sdk.cordova.responseJson;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.google.gson.annotations.SerializedName;

/**
 * Created by reyzhang22 on 15/7/15.
 */
public class UserTicketResponseJSON extends BasicResponseJSON{

    @SerializedName("result")
    public UserTokenResultResponseJSON token;

    public String getUserTicket() {
        return token.ticketId;
    }

    public class UserTokenResultResponseJSON {

        @SerializedName("ticket_id")
        public String ticketId;

    }
}
