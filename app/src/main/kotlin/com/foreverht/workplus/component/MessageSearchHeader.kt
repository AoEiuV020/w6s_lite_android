package com.foreverht.workplus.component

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.foreverht.workplus.module.chat.SearchMessageType
import com.foreverht.workplus.module.chat.activity.BaseMessageHistoryActivity
import com.foreverht.workplus.module.chat.activity.MessageByTypeActivity
import com.foreveross.atwork.R

class MessageSearchHeader(context: Context?, private val messageHistoryViewAction: BaseMessageHistoryActivity.MessageHistoryViewAction?) : LinearLayout(context) {

    private lateinit var header:    View;
    private lateinit var tvArticle: TextView;
    private lateinit var tvVideo:   TextView;
    private lateinit var tvFile:    TextView;
    private lateinit var tvImage:   TextView;
    private lateinit var tvText:    TextView;
    private lateinit var tvVoice:   TextView;

    init {
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        header = inflater.inflate(R.layout.view_message_search_header, this)
        tvArticle = header.findViewById(R.id.tv_article)
        tvVideo = header.findViewById(R.id.tv_video)
        tvFile = header.findViewById(R.id.tv_file)
        tvImage = header.findViewById(R.id.tv_image)
        tvText = header.findViewById(R.id.tv_text)
        tvVoice = header.findViewById(R.id.tv_audio)
        registenerListener()
    }

    fun registenerListener() {
        tvArticle.setOnClickListener {
            searchByType(SearchMessageType.article)
        }

        tvVideo.setOnClickListener {
            searchByType(SearchMessageType.video)
        }

        tvFile.setOnClickListener {
            searchByType(SearchMessageType.file)
        }

        tvImage.setOnClickListener {
            searchByType(SearchMessageType.image)
        }

        tvText.setOnClickListener {
            searchByType(SearchMessageType.text)
        }

        tvVoice.setOnClickListener {
            searchByType(SearchMessageType.voice)
        }
    }

    fun searchByType(messageType: SearchMessageType) {
        context?.startActivity(MessageByTypeActivity.getIntent(context, messageType, messageHistoryViewAction));
    }
}