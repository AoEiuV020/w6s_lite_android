package com.w6s.emoji

import android.content.Context
import android.text.TextUtils
import com.foreveross.atwork.api.sdk.sticker.StickerAsyncNetService
import com.foreveross.atwork.api.sdk.sticker.responseJson.StickerAlbumData
import com.foreveross.atwork.api.sdk.sticker.responseJson.StickerAlbumResult
import com.foreveross.atwork.api.sdk.sticker.responseJson.StickerData
import com.foreveross.atwork.infrastructure.shared.StickerShareInfo
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*
import kotlin.collections.HashMap


class StickerManager private constructor() {

    //数据源
    private val stickerCategories: MutableMap<Int, StickerCategory> = mutableMapOf()
    private val stickerCategoryMap: MutableMap<String, StickerCategory> = HashMap()
    private val stickerMap = HashMap<String, StickerItem>();


    public val stickerTabRecoder = mutableMapOf<Int, Int>()


    companion object {
        val instance = StickerManager()
    }

    fun checkRemoteStickerAlbums(context: Context, quite: Boolean) {
        val service = StickerAsyncNetService.getInstance();
        service.checkStickerAlbum(context) { stickerAlbumList ->
            if (!quite) {
                stickerCategories.clear()
                stickerCategoryMap.clear()
                for (stickerAlbumData in stickerAlbumList!!) {
                    val stickerCategory= StickerCategory(stickerAlbumData.mId, stickerAlbumData.mName, stickerAlbumData.mSortOrder, stickerAlbumData.mName, false, stickerAlbumData.mSortOrder, stickerAlbumData.mIcon);
                    stickerCategories.put(stickerAlbumData.mSortOrder, stickerCategory)
                    stickerCategoryMap.put(stickerAlbumData.mId, stickerCategory)
                    loadStickerAlbumConfig(stickerCategory)
                }
                val updateIdsData = StickerShareInfo.getInstance().getStickerUpdateList(context)
                if (!TextUtils.isEmpty(updateIdsData)) {
                    val updateIds: List<String> = Gson().fromJson(updateIdsData, object: TypeToken<List<String>>(){}.type)
                    for (id in updateIds) {
                        val stickerCategory = stickerCategoryMap.get(id)
                        if (stickerCategory != null) {
                            downloadStickerAlbumZip(context, stickerCategory, null, null,  true)
                        }
                        StickerShareInfo.getInstance().setStickerUpdateList(context, "")
                    }
                }
            }
        };
    }

    fun loadStickerAlbumConfig(stickerCategory: StickerCategory) {
        val configPath = AtWorkDirUtils.getInstance().getStickerDirByCategoryId(stickerCategory.getCategoryId()) + "config.json";
        val configFile = File(configPath)
        if (!configFile.exists()) {
            return
        }
        val configText : String
        try{
            configText  = configFile.readText()
            if (TextUtils.isEmpty(configText)) {
                return
            }
        }catch (e : Exception){
            return e.printStackTrace()
        }
        val stickerAlbumData = Gson().fromJson(configText, StickerAlbumData::class.java)
        //补充最后一页缺少的贴图
        val stickerSize = stickerAlbumData.mStickers.size
        val tmp =  stickerSize % EmojiLayout.STICKER_PER_PAGE
        if (tmp != 0) {
            val tmp2 = EmojiLayout.STICKER_PER_PAGE - (stickerSize - stickerSize / EmojiLayout.STICKER_PER_PAGE * EmojiLayout.STICKER_PER_PAGE)
            var start = 0
            for (sticker in stickerAlbumData.mStickers) {
                start = Math.max(start, sticker.mIndex)
            }
            for (i in 0 until tmp2) {
                val stickerData = StickerData()
                stickerData.mIndex = start + i
                stickerAlbumData.mStickers.add(stickerData)
            }
        }
        stickerCategory.setStickers(stickerAlbumData.mStickers.sortedBy { it.mIndex })
        stickerCategoryMap.put(stickerAlbumData.mId, stickerCategory)
    }

    fun getStickerCategories(): Map<Int, StickerCategory> {
        return stickerCategories
    }

    fun getCategory(name: String): StickerCategory? {
        return stickerCategoryMap.get(name)
    }



//    inner class EntryLoader() : DefaultHandler() {
//        var categoryName: String = ""
//        var categoryOrder: Int = 0
//        lateinit var stickerCategory: StickerCategory
//        val stickers: MutableList<StickerItem> = mutableListOf()
//        fun load(path: String) {
//            var inputStream : InputStream? = null
//            try {
//                inputStream = FileInputStream(path)
//                Xml.parse(inputStream, Xml.Encoding.UTF_8, this)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            } finally {
//                try {
//                    if (inputStream != null) {
//                        inputStream.close()
//                    }
//                } catch (e : Exception) {
//                    e.printStackTrace()
//                }
//            }
//        }
//
//        override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes) {
//            if (localName.equals("Stickers")) {
//                categoryOrder = attributes.getValue(uri, "Order").toInt()
//            }
//            if (localName.equals("Catalog")) {
//                categoryName = attributes.getValue(uri, "Title")
//                stickerCategory = StickerCategory(categoryName, categoryName, true, categoryOrder)
//            }
//            if (localName.equals("Emoticon")) {
//                val orderId: Int = attributes.getValue(uri, "ID").toInt()
//                val tag: String = attributes.getValue(uri, "Tag")
//                val fileName: String  = attributes.getValue(uri, "File")
//                val width: Int = attributes.getValue(uri, "Width").toInt()
//                val height: Int = attributes.getValue(uri, "Height").toInt()
//                val endFixed: String = attributes.getValue(uri, "EndFixed")
//                val stickerItem = StickerItem(categoryName, orderId, tag, fileName, width, height, endFixed)
//                stickers.add(stickerItem)
//            }
//
//        }
//
//        override fun endDocument() {
//            //补充最后一页缺少的贴图
//            val tmp = stickers.size % EmojiLayout.STICKER_PER_PAGE
//            if (tmp != 0) {
//                val tmp2 = EmojiLayout.STICKER_PER_PAGE - (stickers.size - stickers.size / EmojiLayout.STICKER_PER_PAGE * EmojiLayout.STICKER_PER_PAGE)
//                val start = stickers.size
//                for (i in 0 until tmp2) {
//                    stickers.add(StickerItem(categoryName, start+ i, "", "", 80, 80, ".png"))
//                }
//            }
//            stickerCategory.setStickers(stickers.sortedBy { it.getOrderId() })
//            stickerCategories.put(categoryOrder, stickerCategory)
//            stickerCategories.toSortedMap()
//            stickerCategoryMap.put(categoryName, stickerCategory)
//        }
//    }
}
