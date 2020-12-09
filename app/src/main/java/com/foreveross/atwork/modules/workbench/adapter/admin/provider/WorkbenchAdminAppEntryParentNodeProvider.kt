package com.foreveross.atwork.modules.workbench.adapter.admin.provider

import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.utils.AppIconHelper

class WorkbenchAdminAppEntryParentNodeProvider: WorkbenchAdminAppEntryNodeProvider() {

    override val itemViewType: Int = 0

    override val layoutId: Int = R.layout.component_workbench_admin_item_app_entry_parent

    private val EXPAND_COLLAPSE_PAYLOAD = 0x111

    init {
        addChildClickViewIds(R.id.ivRootNodeArrow, R.id.tvDesc)
    }

    override fun convert(helper: BaseViewHolder, item: BaseNode, payloads: List<Any>) {
        for (payload in payloads) {
            if (EXPAND_COLLAPSE_PAYLOAD == payload) { // 增量刷新，使用动画变化箭头
                setArrowSpin(helper, item, isAnimate = true)
            }
        }
    }

    override fun convert(helper: BaseViewHolder, item: BaseNode) {
        val parentNode = item as AppEntryParentNode

        helper.setText(R.id.tvName, parentNode.appEntry.getTitleI18n(BaseApplicationLike.baseContext))
        AppIconHelper.setAppIcon(context, parentNode.appEntry, helper.getView(R.id.ivAppCover), true)
        helper.getView<ImageView>(R.id.ivSelect).isVisible =  parentNode.select
        handleRootNodeArrowUI(parentNode, helper)
        helper.getView<ImageView>(R.id.tvDesc).isVisible = shouldShowSetScopeDesc(parentNode)


        setArrowSpin(helper, item, isAnimate = false)

    }

    private fun handleRootNodeArrowUI(parentNode: AppEntryParentNode, helper: BaseViewHolder) {
        if (parentNode.appEntriesChildren.isNotEmpty()) {
            getIvRootNodeArrow(helper).isVisible = true
        } else {
            getIvRootNodeArrow(helper).isInvisible = true

        }
    }

    private fun shouldShowSetScopeDesc(parentNode: AppEntryParentNode) = parentNode.isScopeEmpty() ?: false

    private fun setArrowSpin(helper: BaseViewHolder, item: BaseNode, isAnimate: Boolean) {
        val data = item as AppEntryParentNode

        val ivRootNodeArrow = getIvRootNodeArrow(helper)
        if (data.isExpanded) {
            if (isAnimate) {
                ViewCompat.animate(ivRootNodeArrow).setDuration(200)
                        .setInterpolator(DecelerateInterpolator())
                        .rotation(90f)
                        .start()
            } else {
                ivRootNodeArrow.rotation = 90f
            }

        } else {

            if (isAnimate) {
                ViewCompat.animate(ivRootNodeArrow).setDuration(200)
                        .setInterpolator(DecelerateInterpolator())
                        .rotation(0f)
                        .start()
            } else {
                ivRootNodeArrow.rotation = 0f
            }

        }
    }

    private fun getIvRootNodeArrow(helper: BaseViewHolder) =
            helper.getView<ImageView>(R.id.ivRootNodeArrow)

    override fun onChildClick(helper: BaseViewHolder, view: View, data: BaseNode, position: Int) {
        when (view.id) {
            R.id.ivRootNodeArrow -> getAdapter()?.expandOrCollapse(position, animate = true, notify = true, parentPayload = EXPAND_COLLAPSE_PAYLOAD)

            R.id.tvDesc -> routeAdminAppModifyUrl(data)
        }
    }

    override fun onClick(helper: BaseViewHolder, view: View, data: BaseNode, position: Int) {
        handleClick(data)


    }


}