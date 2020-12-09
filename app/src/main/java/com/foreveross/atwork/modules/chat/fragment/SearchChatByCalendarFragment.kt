package com.foreveross.atwork.modules.chat.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.chat.SimpleMessageData
import com.foreveross.atwork.infrastructure.utils.TimeUtil
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity
import com.foreveross.atwork.modules.chat.activity.INTENT_SESSION_ID
import com.foreveross.atwork.modules.chat.dao.ChatDaoService
import com.foreveross.atwork.modules.chat.util.daysOfWeekFromLocale
import com.foreveross.atwork.support.BackHandledFragment
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import kotlinx.android.synthetic.main.item_calendar_day_legend.*
import kotlinx.android.synthetic.main.item_calendar_month_header.view.*
import kotlinx.android.synthetic.main.view_calendar_day.view.*
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.ZoneOffset


/**
 *  create by reyzhang22 at 2020-02-11
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class SearchChatByCalendarFragment : BackHandledFragment() {

    private lateinit var tvTitle:   TextView
    private lateinit var ivBack: ImageView
    private lateinit var calendarView: CalendarView
    private lateinit var sessionId:     String

    private lateinit var chatDataInDayMap: MutableMap<String, SimpleMessageData>

    private val today = LocalDate.now()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search_chat_by_calendar, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        findViews(view)
        initData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun findViews(view: View) {
        calendarView = view.findViewById(R.id.cv)
        tvTitle = view.findViewById(R.id.title_bar_common_title)
        ivBack = view.findViewById(R.id.title_bar_common_back)

        tvTitle.text = getString(R.string.search_by_calendar)
        ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initData() {
        sessionId = arguments?.get(INTENT_SESSION_ID) as String? ?: return

        ChatDaoService.getInstance().mapMessageByDay(mActivity, sessionId ){ _, datas ->
            for(data in datas) {
                chatDataInDayMap = datas
                setupCalendarData()
            }
        }
    }


    private fun setupCalendarData() {
        val currentMonth = YearMonth.now()
        val daysOfWeek = daysOfWeekFromLocale()
        legendLayout.children.forEachIndexed { index, view ->
            (view as TextView).apply {
//                text = daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale.getDefault())
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                setTextColor(ContextCompat.getColor(context, R.color.common_text_hint_gray2))
            }
        }
        var monthsToSubtract = 0L
        if (chatDataInDayMap.isNotEmpty()) {
            val lastTimestamp = chatDataInDayMap.values.first().messageTime
            monthsToSubtract = TimeUtil.calculateBetweenTwoMonth(lastTimestamp, System.currentTimeMillis())
        }
        calendarView.setup(currentMonth.minusMonths(monthsToSubtract), currentMonth, daysOfWeek.first())
        calendarView.scrollToMonth(currentMonth)

        calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.textView
                val roundBgView = container.roundBgView

                textView.text = null
                textView.background = null
                roundBgView.visibility = View.INVISIBLE
                textView.text = day.day.toString()

                if (day.owner == DayOwner.THIS_MONTH) {
                    if (day.date.isAfter(today)) {
                        textView.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                        return
                    }
                    if (day.date == today) {
                        textView.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                        roundBgView.visibility = View.VISIBLE
                        roundBgView.setBackgroundResource(R.drawable.calendar_selected_bg)
                        return
                    }
                    if (isChatInDay(day.date)) {
                        textView.setTextColor(ContextCompat.getColor(context!!, R.color.common_text_color))
                        roundBgView.visibility = View.GONE
                        return
                    }
                    textView.setTextColor(ContextCompat.getColor(context!!, R.color.common_text_hint_gray2))
                    return
                }
                textView.visibility = View.GONE
            }
        }


        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            @SuppressLint("DefaultLocale")
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                val monthTitle = "${month.year}年${month.yearMonth.monthValue}月"
                container.textView.text = monthTitle
            }
        }
    }

    fun isChatInDay(date: LocalDate): Boolean {
         chatDataInDayMap.keys.apply {
            return contains(TimeUtil.getStringForMillis(date.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli(), TimeUtil.getTimeFormat1(context)))
        }
    }

    override fun onBackPressed(): Boolean {
        mActivity.finish()
        return true
    }

    inner class DayViewContainer(view: View) : ViewContainer(view) {
        lateinit var day: CalendarDay
        val textView = view.tv_day_text
        val roundBgView = view.view_round_bg

        init {
            view.setOnClickListener {
                if (isChatInDay(day.date)) {
                    val data = chatDataInDayMap.get(TimeUtil.getStringForMillis(day.date.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli(), TimeUtil.getTimeFormat1(context)))
                    val intent = ChatDetailActivity.getIntent(context, sessionId, data?.messageId)
                    startActivity(intent)
                }
            }
        }
    }

    inner class MonthViewContainer(view: View) : ViewContainer(view) {
        val textView = view.exFourHeaderText
    }

}