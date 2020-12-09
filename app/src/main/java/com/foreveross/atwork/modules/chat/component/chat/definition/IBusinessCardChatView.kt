package com.foreveross.atwork.modules.chat.component.chat.definition

import android.widget.ImageView
import android.widget.TextView

interface IBusinessCardChatView: IChatView {

    fun coverView(): ImageView

    fun titleView(): TextView

    fun genderView(): ImageView

    fun jobTitleView(): TextView

    fun signatureView(): TextView?

}