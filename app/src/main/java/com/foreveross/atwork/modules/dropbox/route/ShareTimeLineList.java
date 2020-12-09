package com.foreveross.atwork.modules.dropbox.route;

import androidx.annotation.NonNull;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.dropbox.ShareItem;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ShareTimeLineList<T> extends ArrayList {

    private List mTimeList = new ArrayList();

    public int timeSize = 0;

    @Override
    public boolean addAll(@NonNull Collection c) {
        ShareTimeLineList newList = new ShareTimeLineList();

        List<ShareItem> dropboxDataList = new ArrayList(c);

        for (ShareItem shareItem : dropboxDataList) {
            if (shareItem == null) {
                continue;
            }

            String monthOfDay = TimeUtil.getStringForMillis(shareItem.mCreateTime, TimeUtil.getTimeFormat4(BaseApplicationLike.baseContext));
            monthOfDay = TimeUtil.isSameMonth(BaseApplicationLike.baseContext, TimeUtil.getCurrentTimeInMillis(), monthOfDay);
            if (!mTimeList.contains(monthOfDay)) {
                mTimeList.add(monthOfDay);
                ShareItem timeLineDropbox = new ShareItem();
                timeLineDropbox.mIsTimeLine = true;
                timeLineDropbox.mName = monthOfDay;
                newList.add(timeLineDropbox);
            }
            newList.add(shareItem);
        }
        timeSize = mTimeList.size();
        return super.addAll(newList);
    }

    public void reset() {

        mTimeList.clear();
        timeSize = 0;
    }
}
