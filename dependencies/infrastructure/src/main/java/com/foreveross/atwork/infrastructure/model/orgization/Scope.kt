package com.foreveross.atwork.infrastructure.model.orgization

import android.os.Parcelable
import com.foreveross.atwork.infrastructure.model.Employee
import com.foreveross.atwork.infrastructure.model.ShowListItem
import kotlinx.android.parcel.Parcelize


@Parcelize
class Scope @JvmOverloads constructor(
        val id: String,

        val path: String,

        val name: String,

        val organization: ShowListItem? = null,

        val employee: ShowListItem? = null
): Parcelable {

    fun isOrg() = null != organization

    override fun toString(): String {
        return "Scope(id='$id', path='$path', name='$name', organization=$organization, employee=$employee)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Scope

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }


}