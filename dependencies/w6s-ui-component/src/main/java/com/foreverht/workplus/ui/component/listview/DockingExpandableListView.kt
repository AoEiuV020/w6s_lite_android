package com.foreverht.workplus.ui.component.listview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.AbsListView
import android.widget.ExpandableListView

class DockingExpandableListView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ExpandableListView(context, attrs, defStyleAttr), AbsListView.OnScrollListener {
    private var mDockingHeader: View? = null
    private var mDockingHeaderWidth = 0
    private var mDockingHeaderHeight = 0
    private var mDockingHeaderVisible = false
    private var mDockingHeaderState = IDockingController.DOCKING_HEADER_HIDDEN
    private var mListener: IDockingHeaderUpdateListener? = null

    fun setDockingHeader(header: View?, listener: IDockingHeaderUpdateListener?) {
        mDockingHeader = header
        mListener = listener
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mDockingHeader != null) {
            measureChild(mDockingHeader, widthMeasureSpec, heightMeasureSpec)
            mDockingHeaderWidth = mDockingHeader!!.measuredWidth
            mDockingHeaderHeight = mDockingHeader!!.measuredHeight
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (mDockingHeader != null) {
            mDockingHeader!!.layout(0, 0, mDockingHeaderWidth, mDockingHeaderHeight)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (mDockingHeaderVisible) { // draw header view instead of adding into view hierarchy
            drawChild(canvas, mDockingHeader, drawingTime)
        }
    }

    override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        val packedPosition = getExpandableListPosition(firstVisibleItem)
        val groupPosition = getPackedPositionGroup(packedPosition)
        val childPosition = getPackedPositionChild(packedPosition)
        updateDockingHeader(groupPosition, childPosition)
    }

    override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}
    private fun updateDockingHeader(groupPosition: Int, childPosition: Int) {
        if (expandableListAdapter == null) {
            return
        }
        if (expandableListAdapter is IDockingController) {
            val dockingController = expandableListAdapter as IDockingController
            mDockingHeaderState = dockingController.getDockingState(groupPosition, childPosition)
            when (mDockingHeaderState) {
                IDockingController.DOCKING_HEADER_HIDDEN -> mDockingHeaderVisible = false
                IDockingController.DOCKING_HEADER_DOCKED -> {
                    if (mListener != null) {
                        mListener!!.onUpdate(mDockingHeader, groupPosition, isGroupExpanded(groupPosition))
                    }
                    mDockingHeader!!.measure(
                            MeasureSpec.makeMeasureSpec(mDockingHeaderWidth, MeasureSpec.AT_MOST),
                            MeasureSpec.makeMeasureSpec(mDockingHeaderHeight, MeasureSpec.AT_MOST))
                    mDockingHeader!!.layout(0, 0, mDockingHeaderWidth, mDockingHeaderHeight)
                    mDockingHeaderVisible = true
                }
                IDockingController.DOCKING_HEADER_DOCKING -> {
                    if (mListener != null) {
                        mListener!!.onUpdate(mDockingHeader, groupPosition, isGroupExpanded(groupPosition))
                    }
                    val firstVisibleView = getChildAt(0)
                    val yOffset: Int
                    yOffset = if (firstVisibleView.bottom < mDockingHeaderHeight) {
                        firstVisibleView.bottom - mDockingHeaderHeight
                    } else {
                        0
                    }
                    // The yOffset is always non-positive. When a new header view is "docking",
// previous header view need to be "scrolled over". Thus we need to draw the
// old header view based on last child's scroll amount.
                    mDockingHeader!!.measure(
                            MeasureSpec.makeMeasureSpec(mDockingHeaderWidth, MeasureSpec.AT_MOST),
                            MeasureSpec.makeMeasureSpec(mDockingHeaderHeight, MeasureSpec.AT_MOST))
                    mDockingHeader!!.layout(0, yOffset, mDockingHeaderWidth, mDockingHeaderHeight + yOffset)
                    mDockingHeaderVisible = true
                }
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN && mDockingHeaderVisible) {
            val rect = Rect()
            mDockingHeader!!.getDrawingRect(rect)
            if (rect.contains(ev.x.toInt(), ev.y.toInt())
                    && mDockingHeaderState == IDockingController.DOCKING_HEADER_DOCKED) { // Hit header view area, intercept the touch event
                return true
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (mDockingHeaderVisible) {
            val rect = Rect()
            mDockingHeader!!.getDrawingRect(rect)
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> if (rect.contains(ev.x.toInt(), ev.y.toInt())) { // forbid event handling by list view's item
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    val flatPostion = getExpandableListPosition(firstVisiblePosition)
                    val groupPos = getPackedPositionGroup(flatPostion)
                    if (rect.contains(ev.x.toInt(), ev.y.toInt()) &&
                            mDockingHeaderState == IDockingController.DOCKING_HEADER_DOCKED) { // handle header view click event (do group expansion & collapse)
                        if (isGroupExpanded(groupPos)) {
                            collapseGroup(groupPos)
                        } else {
                            expandGroup(groupPos)
                        }
                        return true
                    }
                }
            }
        }
        return super.onTouchEvent(ev)
    }

    interface IDockingController {
        fun getDockingState(firstVisibleGroup: Int, firstVisibleChild: Int): Int

        companion object {
            const val DOCKING_HEADER_HIDDEN = 1
            const val DOCKING_HEADER_DOCKING = 2
            const val DOCKING_HEADER_DOCKED = 3
        }
    }

    interface IDockingHeaderUpdateListener {
        fun onUpdate(headerView: View?, groupPosition: Int, expanded: Boolean)
    }

    init {
        setOnScrollListener(this)
    }
}