package com.foreveross.atwork.infrastructure.model.share;/**
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


import android.os.Parcel;

import java.util.List;

/**
 * Created by reyzhang22 on 16/6/27.
 */
public class ExternalShare extends ShareModel {

    /**
     * 外部分享类型
     */
    public ExternalShareType.ShareType mShareType;

    public List<String> mImageList;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mShareType == null ? -1 : this.mShareType.ordinal());
    }

    public ExternalShare() {
    }

    protected ExternalShare(Parcel in) {
        int tmpMShareType = in.readInt();
        this.mShareType = tmpMShareType == -1 ? null : ExternalShareType.ShareType.values()[tmpMShareType];
    }

    public static final Creator<ExternalShare> CREATOR = new Creator<ExternalShare>() {
        @Override
        public ExternalShare createFromParcel(Parcel source) {
            return new ExternalShare(source);
        }

        @Override
        public ExternalShare[] newArray(int size) {
            return new ExternalShare[size];
        }
    };
}
