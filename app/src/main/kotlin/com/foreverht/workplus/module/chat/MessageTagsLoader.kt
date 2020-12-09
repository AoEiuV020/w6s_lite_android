package com.foreverht.workplus.module.chat

import android.content.Context
import androidx.loader.content.AsyncTaskLoader
import com.foreverht.db.service.repository.MessageTagsRepository
import com.foreveross.atwork.infrastructure.model.app.App
import com.w6s.module.MessageTags

class MessageTagsLoader(context: Context, serverApp: App) : AsyncTaskLoader<List<MessageTags>>(context) {

    val app = serverApp;
    override fun loadInBackground(): List<MessageTags>? {
        return MessageTagsRepository.getInstance().getMessageTags(app)
    }

}