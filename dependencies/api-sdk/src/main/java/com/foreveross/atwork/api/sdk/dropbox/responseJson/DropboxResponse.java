package com.foreveross.atwork.api.sdk.dropbox.responseJson;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;

public class DropboxResponse extends BasicResponseJSON {

    public Dropbox result;

    public boolean isLegal() {
        return null != result;
    }

}
