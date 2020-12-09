package com.w6s.emoji

import android.os.Parcel
import android.os.Parcelable

class StickerItem : Parcelable {
    private var orderId: Int = -1

    private var category: String = ""

    private var tag: String = ""//类别名

    private var name: String = ""

    private var width = 80

    private var height = 80

    private var endFixed = ".png"

    constructor(category: String, orderId: Int, tag: String, name: String, width: Int, height: Int, endFixed: String) {
        this.category = category
        this.orderId = orderId
        this.tag = tag
        this.name = name
        this.width = width
        this.height = height
        this.endFixed = endFixed
    }

    constructor(category: String, name: String, width: Int, height: Int) {
        this.category = category
        this.name = name
        this.width = width
        this.height = height
    }

    fun getWidth(): Int {
        return this.width
    }

    fun getHieght(): Int {
        return this.height
    }

    fun getCategory(): String {
        return category
    }

    fun getIdentifier(): String {
        return "$category/$tag/$name"
    }

    fun getStickerTag(): String {
        return tag
    }

    fun getName(): String {
        return name
    }

    fun getOrderId(): Int {
        return orderId
    }

    fun  getEndfixed(): String {
        return endFixed
    }

    override fun equals(obj: Any?): Boolean {
        if (obj != null && obj is StickerItem) {
            val item = obj as StickerItem?
            return item!!.getCategory().equals(category) && item.getName().equals(name) && item.getOrderId().equals(orderId)
        }
        return false
    }

    constructor(source: Parcel) : super(
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {}

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<StickerItem> = object : Parcelable.Creator<StickerItem> {
            override fun createFromParcel(source: Parcel): StickerItem = StickerItem(source)
            override fun newArray(size: Int): Array<StickerItem?> = arrayOfNulls(size)
        }
    }
}