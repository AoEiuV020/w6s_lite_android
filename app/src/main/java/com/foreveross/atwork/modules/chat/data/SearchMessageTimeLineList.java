package com.foreveross.atwork.modules.chat.data;

import androidx.annotation.NonNull;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SearchMessageTimeLineList<T> extends ArrayList {

    private List mTimeList = new ArrayList();

    public int timeSize = 0;

    private boolean mIsNeedTimeLine = true;

    @Override
    public boolean addAll(@NonNull Collection c) {
        SearchMessageTimeLineList newList = new SearchMessageTimeLineList();

        List<SearchMessageItemData> dataList = new ArrayList(c);

        for (SearchMessageItemData data : dataList) {
            if (data == null) {
                continue;
            }

            String monthOfDay = TimeUtil.getStringForMillis(data.mMessage.deliveryTime, TimeUtil.getTimeFormat4(BaseApplicationLike.baseContext));
            monthOfDay = TimeUtil.isSameMonth(BaseApplicationLike.baseContext, TimeUtil.getCurrentTimeInMillis(), monthOfDay);
            if (!mTimeList.contains(monthOfDay) && mIsNeedTimeLine) {
                mTimeList.add(monthOfDay);
                SearchMessageItemData searchMessageItemData = new SearchMessageItemData();
                searchMessageItemData.mIsTimeLine = true;
                searchMessageItemData.mName = monthOfDay;
                newList.add(searchMessageItemData);
            }
            newList.add(data);
        }
        timeSize = mTimeList.size();
        return super.addAll(newList);
    }

    public void setNeedTimeLine(boolean isNeedTimeLine) {
        mIsNeedTimeLine = isNeedTimeLine;
    }

    public void reset() {

        mTimeList.clear();
        timeSize = 0;
    }
}
