package com.foreveross.atwork.infrastructure.support

import com.foreveross.atwork.infrastructure.model.contact.ContactViewMode
import java.util.*

class ContactConfig: BaseConfig() {

    var onlyVersionContactViewMode = -1

    override fun parse(pro: Properties) {

//        pro.getProperty("CONTACT_CONFIG_ONLY_VERSION_CONTACT_VIEW_MODE")?.apply {
//            onlyVersionContactViewMode = this.toInt()
//        }

        //3.19.0 默认值值开启简洁版
        onlyVersionContactViewMode = ContactViewMode.SIMPLE_VERSION

    }
}