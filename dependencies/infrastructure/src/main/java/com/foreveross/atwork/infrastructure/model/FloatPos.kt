package com.foreveross.atwork.infrastructure.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *  create by reyzhang22 at 2019-08-28
 */
data class FloatPos(


        var posX: Int = 0,

        var posY: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(posX)
        parcel.writeInt(posY)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FloatPos> {
        override fun createFromParcel(parcel: Parcel): FloatPos {
            return FloatPos(parcel)
        }

        override fun newArray(size: Int): Array<FloatPos?> {
            return arrayOfNulls(size)
        }
    }
}