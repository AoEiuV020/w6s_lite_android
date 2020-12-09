package com.foreveross.atwork.infrastructure.model.location;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetLocationRequest {

    @SerializedName("timeout")
    public int mTimeout = 3;

    @SerializedName("accuracy")
    public int mAccuracy = Accuracy.HIGH;

    @SerializedName("source")
    public String mSource;

    @SerializedName("virtualDevices")
    public List<String> mVirtualDevices;

    public static GetLocationRequest newRequest() {
        return new GetLocationRequest();
    }

    public GetLocationRequest setTimeout(int timeout) {
        mTimeout = timeout;
        return this;
    }

    public GetLocationRequest setAccuracy(int accuracy) {
        mAccuracy = accuracy;
        return this;
    }

    public GetLocationRequest setSource(String source) {
        mSource = source;
        return this;
    }

    @Override
    public String toString() {
        return "GetLocationRequest{" +
                "mTimeout=" + mTimeout +
                ", mAccuracy=" + mAccuracy +
                ", mSource='" + mSource + '\'' +
                '}';
    }

    public static final class Accuracy {

        public static int HIGH = 2;

        public static int MEDIUM = 1;

        public static int LOW = 0;

    }


    public enum Source {

         /**
          * 谷歌地图
          * */
         GOOGLE,

        /**
         * 高德地图
         * */
        AMAP
    }
}
