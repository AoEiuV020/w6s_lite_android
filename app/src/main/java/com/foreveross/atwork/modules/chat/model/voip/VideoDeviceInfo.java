package com.foreveross.atwork.modules.chat.model.voip;


import java.io.Serializable;

/**
 * 视频设备信息
 */
public class VideoDeviceInfo implements Comparable<VideoDeviceInfo>, Serializable {
    @Override
    public int compareTo(VideoDeviceInfo another) {
        return 0;
    }
//    private static final long serialVersionUID = -8832393429691170742L;
//    private long userid;
//    private long deviceID;
//    private String deviceName;
//    private ConfMsg.VideoDeviceStatus status = ConfMsg.VideoDeviceStatus.Close;
//    /**
//     * 摄像头方向，目前只有Android Phone使用
//     */
//    private int orientation = 0;
//    private ConfMsg.VideoDeviceChange vdChange = ConfMsg.VideoDeviceChange.Add;
//
//    public VideoDeviceInfo(long userid, long deviceID, String deviceName) {
//        super();
//        this.userid = userid;
//        this.deviceID = deviceID;
//        this.deviceName = deviceName;
//    }
//
//    @Override
//    public int compareTo(VideoDeviceInfo another) {
//        try {
//            long d = deviceID - another.getDeviceId();
//
//            if (d > 0) {
//                return 1;
//            } else if (d == 0) {
//                return 0;
//            } else {
//                return -1;
//            }
//        } catch (Exception e) {
//        }
//        return 0;
//    }
//
//    public long getUserId() {
//        return userid;
//    }
//
//    public void setUserId(long userid) {
//        this.userid = userid;
//    }
//
//    public long getDeviceId() {
//        return deviceID;
//    }
//
//    public String getDeviceName() {
//        return deviceName;
//    }
//
//    /**
//     * @return the status
//     */
//    public ConfMsg.VideoDeviceStatus getStatus() {
//        return status;
//    }
//
//    /**
//     * @param status the status to set
//     */
//    public void setStatus(ConfMsg.VideoDeviceStatus status) {
//        this.status = status;
//    }
//
//    public boolean isOpen() {
//        return ConfMsg.VideoDeviceStatus.Open == status;
//    }
//
//    public int getOrientation() {
//        return orientation;
//    }
//
//    public void setOrientation(int orientation) {
//        this.orientation = orientation;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if ((o == null) || (super.getClass() != o.getClass())) {
//            return false;
//        }
//
//        VideoDeviceInfo that = (VideoDeviceInfo) o;
//
//        if (0 == deviceID || deviceID != that.deviceID) {
//            return false;
//        }
//
//        return true;
//    }
//
//    @Override
//    public int hashCode() {
//        return (int) deviceID;
//    }
//
//    @Override
//    public String toString() {
//        StringBuilder builder = new StringBuilder();
//        builder.append("VideoDeviceInfo [userid=");
//        builder.append(userid);
//        builder.append(", deviceID=");
//        builder.append(deviceID);
//        builder.append(", deviceName=");
//        builder.append(deviceName);
//        builder.append(", status=");
//        builder.append(status);
//        builder.append(", isPlaying=");
//        // builder.append(isPlaying);
//        builder.append("]");
//        return builder.toString();
//    }
}
