package com.w6s.emoji

import com.foreveross.atwork.api.sdk.sticker.responseJson.StickerData
import java.io.File
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils


class StickerCategory {
    private var categoryId: String = "";
    private var name: String = ""
    private var title: String = ""
    private var system: Boolean = false
    private var order = 0
    private var coverId: String = ""
    private var sortedOrder = 1;

    // -1未下载， 0已下载， 1下载中
    private var status = -1
    private var downloadProgress = -1


    @Transient
    private var stickers: List<StickerData> = ArrayList()

    constructor(categoryId: String, name: String, sortedOrder: Int, title: String, system: Boolean, order: Int, coverId: String) {
        this.categoryId = categoryId
        this.name = name
        this.title = title
        this.system = system
        this.order = order
        this.coverId = coverId;
        this.sortedOrder = sortedOrder;
    }

    fun getName(): String {
        return name
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getTitle(): String {
        return title
    }

    fun setTitle(title: String) {
        this.title = title
    }


    fun isSystem(): Boolean {
        return system
    }

    fun setSystem(system: Boolean) {
        this.system = system
    }

    fun getOrder(): Int {
        return order
    }

    fun setOrder(order: Int) {
        this.order = order
    }

    fun getStickers(): List<StickerData> {
        return stickers
    }

    fun setStickers(stickers: List<StickerData>) {
        this.stickers = stickers
    }

    fun hasStickers(): Boolean {
        return stickers != null && stickers.size > 0
    }

    fun getCount(): Int {
        return if (stickers == null || stickers.isEmpty()) {
            0
        } else stickers.size

    }

    fun getCategoryId(): String {
        return categoryId;
    }

    fun getCoverImgPath(): String? {
        return coverId
    }

    fun setStatus(status: Int) {
        this.status = status
    }

    fun getStatus(): Int{
        return status
    }

    fun setDownloadProgress(progress: Int) {
        this.downloadProgress = progress
    }

    fun getDownloadProgress(): Int {
        return downloadProgress
    }

    fun getSortedOrder(): Int {
        return sortedOrder
    }

    override fun equals(obj: Any?): Boolean {
        if (obj == null || obj !is StickerCategory) {
            return false
        }
        if (obj === this) {
            return true
        }
        val r = obj as StickerCategory?
        return r!!.name.equals(name)
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}