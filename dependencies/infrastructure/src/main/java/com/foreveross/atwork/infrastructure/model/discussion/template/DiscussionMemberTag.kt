package com.foreveross.atwork.infrastructure.model.discussion.template

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class DiscussionMemberTag(
        @SerializedName("id")
        var id: String = "",


        @SerializedName("name")
        var name: String = "",

        @SerializedName("sort")
        var sort: Int = -1
): Parcelable, Comparable<DiscussionMemberTag> {

        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as DiscussionMemberTag

                if (id != other.id) return false

                return true
        }

        override fun hashCode(): Int {
                return id.hashCode()
        }

        override fun compareTo(other: DiscussionMemberTag): Int {
                return sort.compareTo(other.sort)
        }


}