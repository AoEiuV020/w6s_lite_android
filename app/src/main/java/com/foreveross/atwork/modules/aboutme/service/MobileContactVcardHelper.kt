@file: JvmName("MobileContactVcardHelper")


package com.foreveross.atwork.modules.aboutme.service

import android.widget.ImageView
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.api.sdk.Employee.EmployeeAsyncNetService
import com.foreveross.atwork.infrastructure.model.Employee
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.manager.EmployeeManager
import com.foreveross.atwork.qrcode.zxing.decode.BitmapQrcodeDecoder
import com.foreveross.atwork.utils.ErrorHandleUtil
import java.util.*


class VcardData(
        var name: String? = null,

        var orgName: String? = null,

        var role: String? = null,

        var jobTitle: String? = null,

        var mobile: String? = null,

        var workPhone: String? = null,

        var email: String? = null,

        var address: String? = null,

        var note: String? = null

) {

    companion object {
        fun parse(emp: Employee): VcardData {
            val vcardData = VcardData()
            with(emp) {
                vcardData.name = getTitleI18n(AtworkApplicationLike.baseContext)

                if(!ListUtil.isEmpty(positions)) {
                    vcardData.orgName = positions[0].corpName
                    vcardData.role = positions[0].orgName
                    vcardData.jobTitle = positions[0].jobTitle

                }

                vcardData.mobile = mobile
                vcardData.workPhone = tel
                vcardData.email = email
                vcardData.note = sn
                vcardData.address = location

            }

            return vcardData
        }
    }
}



fun produceCurrentEmpVcardQrcode(iv: ImageView) {
    val tag = UUID.randomUUID().toString()
    iv.tag = tag
    EmployeeManager.getInstance().queryLoginCurrentOrgEmp(AtworkApplicationLike.baseContext, object : EmployeeAsyncNetService.QueryEmployeeInfoListener {
        override fun onSuccess(employee: Employee) {
            if (tag == iv.tag) {
                val vcardData = VcardData.parse(employee)
                produceVcardQrcode(iv, vcardData)
            }
        }

        override fun networkFail(errorCode: Int, errorMsg: String?) {
            ErrorHandleUtil.handleError(errorCode, errorMsg)
        }

    })
}

fun produceVcardQrcode(iv: ImageView, vcardData: VcardData) {
    val data = produceVcardData(vcardData)
    val decoder = BitmapQrcodeDecoder(AtworkApplicationLike.baseContext)
    decoder.createQRImage(data,iv , iv.width, iv.height)
}

/**
 *
    BEGIN:VCARD
    N:郑宇 //姓名
    ORG:福建新大陆电脑股份有限公司 // 公司
    ROLE:数字化部  // 部门
    NOTE:01060041  // 工号
    TITLE:IT开发工程师  // 职位
    TEL;CELL:13655001892 // 手机号码
    TEL;WORK:0591-83979887 // 座机号码
    EMAIL:wupp@newland.com.cn // 邮箱地址
    ADR;WORK: 中国.福州.马尾儒江西路1号新大陆科技园A楼三层 // 工作地址
    END:VCARD
 */
fun produceVcardData(vcardData: VcardData): String {
    val vcardDataBuilder = StringBuilder()
    vcardDataBuilder.append("BEGIN:VCARD\n")
    vcardDataBuilder.append("N:").append(vcardData.name?:StringUtils.EMPTY).append("\n")
    vcardDataBuilder.append("ORG:").append(vcardData.orgName?:StringUtils.EMPTY).append("\n")
    vcardDataBuilder.append("ROLE:").append(vcardData.role?:StringUtils.EMPTY).append("\n")
    vcardDataBuilder.append("NOTE:").append(vcardData.note?:StringUtils.EMPTY).append("\n")
    vcardDataBuilder.append("TITLE:").append(vcardData.jobTitle?:StringUtils.EMPTY).append("\n")
    vcardDataBuilder.append("TEL;CELL:").append(vcardData.mobile?:StringUtils.EMPTY).append("\n")
    vcardDataBuilder.append("TEL;WORK:").append(vcardData.workPhone?:StringUtils.EMPTY).append("\n")
    vcardDataBuilder.append("EMAIL:").append(vcardData.email?:StringUtils.EMPTY).append("\n")
    vcardDataBuilder.append("ADR;WORK:").append(vcardData.address?:StringUtils.EMPTY).append("\n")
//    vcardData.orgName?.let { vcardDataBuilder.append("ORG:$it\n") }
//    vcardData.department?.let { vcardDataBuilder.append("ROLE:$it\n") }
//    vcardData.note?.let { vcardDataBuilder.append("NOTE:$it\n") }
//    vcardData.jobTitle?.let { vcardDataBuilder.append("TITLE:$it\n") }
//    vcardData.mobile?.let { vcardDataBuilder.append("TEL;CELL:$it\n") }
//    vcardData.workPhone?.let { vcardDataBuilder.append("TEL;WORK:$it\n") }
//    vcardData.email?.let { vcardDataBuilder.append("EMAIL:$it\n") }
//    vcardData.address?.let { vcardDataBuilder.append("ADR;WORK:$it\n") }
    vcardDataBuilder.append("END:VCARD\n")

    return vcardDataBuilder.toString()
}