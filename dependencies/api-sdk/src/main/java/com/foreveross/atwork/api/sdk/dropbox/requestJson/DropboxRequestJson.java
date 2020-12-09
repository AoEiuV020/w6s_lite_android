package com.foreveross.atwork.api.sdk.dropbox.requestJson;

import com.google.gson.annotations.SerializedName;

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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 * Created by reyzhang22 on 16/9/12.
 */
public class DropboxRequestJson {

    @SerializedName("parent")
    public String mParent;

    @SerializedName("parent_id")
    public String mParentId;

    @SerializedName("name")
    public String mName;

    @SerializedName("user")
    public OptUser mUser =  new OptUser();

    @SerializedName("size")
    public long mSize;

    @SerializedName("digest")
    public String mDigest;

    @SerializedName("mime_type")
    public String mMimeType;

    @SerializedName("file_id")
    public String mFileId;

    @SerializedName("ids")
    public String[] mIds;

    @SerializedName("target_parent")
    public String mTargetParent;

    @SerializedName("thumbnail")
    public String mThumbnail;

    @SerializedName("target_source_id")
    public String mTargetSourceId;

    @SerializedName("target_source_type")
    public String mTargetSourceType;

    public static class OptUser {

        @SerializedName("name")
        public String nName;
    }
}
