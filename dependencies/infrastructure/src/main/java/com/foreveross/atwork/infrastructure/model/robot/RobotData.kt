package com.foreveross.atwork.infrastructure.model.robot

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class RobotData() : Parcelable {

    /**id*/
    @SerializedName("id")
    var id: String? = null
    /**域id*/
    @SerializedName("domain_id")
    var domainId: String? = null
    /**关键字*/
    @SerializedName("key")
    var key: String? = null
    /**路由地址*/
    @SerializedName("instruction")
    var instruction: String? = null
    /**应用ID*/
    @SerializedName("owner_id")
    var ownerId: String? = null
    /**应用名称*/
    @SerializedName("owner_name")
    var ownerName: String? = null
    /**创建时间*/
    @SerializedName("create_time")
    var createTime = 0L
    /**更改时间*/
    @SerializedName("modify_time")
    var modifyTime = 0L
    /**是否失效*/
    @SerializedName("disabled")
    var disabled: String? = null
    /**是否删除*/
    @SerializedName("deleted")
    var deleted: String? = null
    /**搜索前缀*/
    var prefix: String? = null
    /**搜索后缀*/
    var suffix: String? = null

    constructor(parcel: Parcel) : this() {
        this.id = parcel.readString()
        this.domainId = parcel.readString()
        this.key = parcel.readString()
        this.instruction = parcel.readString()
        this.ownerId = parcel.readString()
        this.ownerName = parcel.readString()
        this.createTime = parcel.readLong()
        this.modifyTime = parcel.readLong()
        this.disabled = parcel.readString()
        this.deleted = parcel.readString()
        this.prefix = parcel.readString()
        this.suffix = parcel.readString()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.id)
        dest.writeString(this.domainId)
        dest.writeString(this.key)
        dest.writeString(this.instruction)
        dest.writeString(this.ownerId)
        dest.writeString(this.ownerName)
        dest.writeLong(this.createTime)
        dest.writeLong(this.modifyTime)
        dest.writeString(this.disabled)
        dest.writeString(this.deleted)
        dest.writeString(this.prefix)
        dest.writeString(this.suffix)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RobotData> {
        override fun createFromParcel(parcel: Parcel): RobotData {
            return RobotData(parcel)
        }

        override fun newArray(size: Int): Array<RobotData?> {
            return arrayOfNulls(size)
        }
    }
}