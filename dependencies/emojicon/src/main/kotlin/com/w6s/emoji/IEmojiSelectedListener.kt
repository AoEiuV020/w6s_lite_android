package com.w6s.emoji

import com.foreveross.atwork.api.sdk.sticker.responseJson.StickerData
import com.rockerhieu.emojicon.emoji.Emojicon

interface IEmojiSelectedListener {

    fun onEmojiSelected(key: Emojicon)

    fun onStickerSelected(categoryId: String, stickerData: StickerData)
}