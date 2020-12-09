package com.foreveross.atwork.modules.chat.component.multipart.item.content

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.chat.TranslateLanguageResponse
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.chat.util.AutoLinkHelper
import com.foreveross.atwork.modules.chat.util.TextTranslateHelper
import com.foreveross.atwork.utils.AtworkToast
import com.foreveross.translate.youdao.YoudaoTranslate
import kotlinx.android.synthetic.main.item_multipart_message_detail_text.view.*

@SuppressLint("ViewConstructor")
class MultipartMessageDetailTextContentView(context: Context): MultipartMessageDetailBasicContentView<TextChatMessage>(context) {

    var strTranslationShortName: String? = null

    override fun findViews(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.item_multipart_message_detail_text, this)

    }

    override fun refreshUI(message: TextChatMessage) {
        val isTranslate = translateEnable(message)
        if (isTranslate) {
            intelligentTranslation(message)
        }

        val spannableString = AutoLinkHelper.getInstance().getSpannableString(context, "", null, tvTextMessage, message.text)
        tvTextMessage.text = spannableString
        refreshTranslateStatusUI(message)
    }

    /**
     * Description:设置文本智能翻译的样式
     * @param textChatMessage
     */
    private fun refreshTranslateStatusUI(textChatMessage: TextChatMessage) {
        if (textChatMessage.isTranslateStatusVisible) {
            if (textChatMessage.isTranslating) {
                handleTranslateView(textTranslationVisible = false, translateSourceVisible = true)
                tvTranslateSource.setText(R.string.text_translating)
            } else {
                if (!StringUtils.isEmpty(textChatMessage.translatedResult) && textChatMessage.isTranslateStatusVisible) {
                    handleTranslateView(textTranslationVisible = true, translateSourceVisible = true)
                    tvTextTranslation.text = textChatMessage.translatedResult
                    tvTranslateSource.text = TextTranslateHelper.getSource(textChatMessage)
                } else {
                    handleTranslateView(textTranslationVisible = false, translateSourceVisible = false)
                }
            }

        } else {
            handleTranslateView(textTranslationVisible = false, translateSourceVisible = false)
        }
    }


    private fun handleTranslateView(textTranslationVisible: Boolean, translateSourceVisible: Boolean) {
        if (textTranslationVisible || translateSourceVisible) {
            vTranslationLine.visibility = View.VISIBLE
        } else {
            vTranslationLine.visibility = View.GONE
        }

        if (textTranslationVisible) {
            tvTextTranslation.visibility = View.VISIBLE
        } else {
            tvTextTranslation.visibility = View.GONE
        }

        if (translateSourceVisible) {
            tvTranslateSource.visibility = View.VISIBLE
        } else {
            tvTranslateSource.visibility = View.GONE
        }
    }


    /**
     * Description:判断是否需要智能翻译：有翻译的语种；没有翻译的结果或者翻译的语种和已经翻译的结果的语种不一致
     * @param textChatMessage
     * @return
     */
    private fun translateEnable(textChatMessage: TextChatMessage): Boolean {
        return if (!StringUtils.isEmpty(strTranslationShortName)) {
            if (StringUtils.isEmpty(textChatMessage.translatedResult)) {
                true
            } else !textChatMessage.translatedLanguage.equals(strTranslationShortName, ignoreCase = true)
        } else false
    }


    //智能翻译
    fun intelligentTranslation(textChatMessage: TextChatMessage) {
        if (StringUtils.isEmpty(textChatMessage.translatedResult)) {
            TextTranslateHelper.setTranslating(textChatMessage, true, true)
            val youdaoTranslate = YoudaoTranslate()
            val url = youdaoTranslate.translate(textChatMessage.text, strTranslationShortName)
            youdaoTranslate.getTranlateLanguage(url, object : YoudaoTranslate.OnTranslateListener {
                override fun networkFail(errorCode: Int, errorMsg: String) {}

                override fun onSuccess(translateLanguageResponse: TranslateLanguageResponse) {
                    if (!StringUtils.isEmpty(translateLanguageResponse.translation[0])) {
                        val result = translateLanguageResponse.translation[0]
                        LogUtil.e("translate result - > $result")
                        TextTranslateHelper.updateTranslateResultAndUpdateDb(textChatMessage, result, strTranslationShortName, true)
                    } else {
                        AtworkToast.showToast(context.resources.getString(R.string.Translate_common))
                        TextTranslateHelper.setTranslating(textChatMessage, false, true)
                    }
                }
            })
        } else {
            TextTranslateHelper.showTranslateStatusAndUpdateDb(textChatMessage, true, true)
        }
    }


}