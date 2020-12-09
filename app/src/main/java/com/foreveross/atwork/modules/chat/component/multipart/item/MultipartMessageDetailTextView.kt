package com.foreveross.atwork.modules.chat.component.multipart.item

import android.annotation.SuppressLint
import android.content.Context
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage
import com.foreveross.atwork.modules.chat.component.multipart.item.content.MultipartMessageDetailTextContentView

@SuppressLint("ViewConstructor")
class MultipartMessageDetailTextView(context: Context, strTranslationShortName: String?): MultipartMessageDetailBasicCarrierView<TextChatMessage, MultipartMessageDetailTextContentView>(context) {

    lateinit var  multipartMessageDetailTextContentView: MultipartMessageDetailTextContentView

    init {
        setTextContentViewData(strTranslationShortName)
    }

    override fun newContentView(): MultipartMessageDetailTextContentView {
        this.multipartMessageDetailTextContentView = MultipartMessageDetailTextContentView(context)
        return this.multipartMessageDetailTextContentView
    }


    private fun setTextContentViewData(strTranslationShortName: String?) {
        multipartMessageDetailTextContentView.strTranslationShortName = strTranslationShortName;
    }



}