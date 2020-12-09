package com.w6s.model.favorite

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class FavoriteTag(
        var tagName: String
) : Parcelable