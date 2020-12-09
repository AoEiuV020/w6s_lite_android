package com.w6s.emoji

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import android.util.Xml
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.io.InputStream
import java.util.regex.Pattern
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.util.DisplayMetrics
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils
import com.rockerhieu.emojicon.emoji.Emojicon
import com.rockerhieu.emojicon.emoji.People
import java.io.IOException


class EmojiManager private constructor() {

    private val emojiDir = "emoji/"

    private val CACHE_MAX_SIZE = 1024

    private var drawableCache: LruCache<String, Bitmap> = LruCache(CACHE_MAX_SIZE)

    private var entriesList : List<Entry> = ArrayList()

    private var entriesMap : Map<String, Entry> = HashMap()

    private var pattern : Pattern? = null

    val defaultEmojiData: Array<Emojicon> = People.DATA;

    companion object {
        val instance  = EmojiManager()
    }

    init {
        pattern = makePattern()
//        load(BaseApplication.baseContext, "emoji.xml")
    }

    fun getDisplayCount(): Int {
        return defaultEmojiData.size
    }

    fun getDisplayDrawable(context: Context, index: Int): Drawable? {
        val text : String = if (index >= 0 && index < entriesList.size) entriesList.get(index).text else ""
        return if (text == null) null else getDrawable(context, text)
    }

    fun getDisplayText(index: Int): String? {
        return if (index >= 0 && index < entriesList.size) entriesList.get(index).text else null
    }

    fun getDrawable(context: Context, text: String): Drawable? {
        val entry = entriesMap.get(text)
        if (entry == null || TextUtils.isEmpty(entry.text)) {
            return null
        }

        var cache = drawableCache.get(entry.assetPath)
        if (cache == null) {
            cache = loadAssetBitmap(context, entry.assetPath)
        }
        return BitmapDrawable(context.resources, cache)
    }

    private fun loadAssetBitmap(context: Context, assetPath: String): Bitmap? {
        var inputStream: InputStream? = null
        try {
            val resources = context.resources
            val options = BitmapFactory.Options()
            options.inDensity = DisplayMetrics.DENSITY_HIGH
            options.inScreenDensity = resources.displayMetrics.densityDpi
            options.inTargetDensity = resources.displayMetrics.densityDpi
            inputStream = context.assets.open(assetPath)
            val bitmap = BitmapFactory.decodeStream(inputStream, Rect(), options)
            if (bitmap != null) {
                drawableCache.put(assetPath, bitmap)
            }
            return bitmap
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return null
    }


    private fun load(context: Context, xmlPath: String) {
        EntryLoader().load(context, xmlPath)

    }

    private fun makePattern() : Pattern {
        return Pattern.compile(patternOfDefault())
    }

    private fun patternOfDefault(): String {
        return "\\[[^\\[]{1,10}\\]"
    }

    inner class Entry {
        var text: String = ""

        var assetPath: String = ""

        constructor(text: String, assetPath: String) {
            this.text = text
            this.assetPath = assetPath
        }
    }

    inner class EntryLoader : DefaultHandler() {
        var category: String = ""
        fun load(context: Context, assetPath: String) {
            var inputStream : InputStream? = null
            try {
                inputStream = context.assets.open(assetPath)
                Xml.parse(inputStream, Xml.Encoding.UTF_8, this)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close()
                    }
                } catch (e : Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes) {
            if (localName.equals("Category")) {
                category = attributes.getValue(uri, "Title")
            }
            if (localName.equals("Emoji_workplus")) {
                var tag: String = attributes.getValue(uri, "Tag")
                var fileName: String  = attributes.getValue(uri, "File")
                var entry: Entry = Entry(tag, emojiDir + category + "/" + fileName)
            }

        }
    }
}