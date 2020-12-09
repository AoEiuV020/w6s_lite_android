package com.foreveross.atwork.modules.dropbox;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.modules.dropbox.fragment.UserDropboxFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DropboxFileData {
    
    private List<Dropbox> mAllList;
    private List<String> mSelectedId;
    public String mParent;
    public List<Dropbox> mSubList;
    public int mIndex;
    public int mTop;
    private UserDropboxFragment.SortedMode mSortedMode;
    private List<String> mTimeList = new ArrayList<>();

    public DropboxFileData(List<Dropbox> dropboxList,
                           ArrayList<String> selectedId, String parent, UserDropboxFragment.SortedMode sortedMode) {
        mSubList = new ArrayList<>();
        mSortedMode = sortedMode;
        if (dropboxList == null)
            this.mAllList = new ArrayList<>();
        else
            this.mAllList = dropboxList;
        if (selectedId == null)
            this.mSelectedId = new ArrayList<>();
        else
            this.mSelectedId = selectedId;
        if (parent == null)
            this.mParent = "";
        else
            this.mParent = parent;

        if (dropboxList == null) {
            return;
        }

        List<Dropbox> dropboxDataList = new ArrayList<>(dropboxList);

        for (Dropbox dropbox : dropboxDataList) {
            if (dropbox == null) {
                continue;
            }

            if (mParent.equalsIgnoreCase(dropbox.mParentId)) {
                if (mSortedMode == UserDropboxFragment.SortedMode.Time) {
                    String monthOfDay = TimeUtil.getStringForMillis(dropbox.mLastModifyTime, TimeUtil.getTimeFormat4(BaseApplicationLike.baseContext));
                    monthOfDay = TimeUtil.isSameMonth(BaseApplicationLike.baseContext, TimeUtil.getCurrentTimeInMillis(), monthOfDay);
                    if (!mTimeList.contains(monthOfDay)) {
                        mTimeList.add(monthOfDay);
                        Dropbox timeLineDropbox = new Dropbox();
                        timeLineDropbox.mIsTimeline = true;
                        timeLineDropbox.mFileName = monthOfDay;
                        mSubList.add(timeLineDropbox);
                    }
                }
                mSubList.add(dropbox);
            }
        }
        if (mSortedMode == UserDropboxFragment.SortedMode.Name) {
            Collections.sort(mSubList);
        }
    }
}
