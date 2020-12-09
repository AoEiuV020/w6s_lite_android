package com.foreveross.atwork.modules.dropbox.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.dropbox.DropboxConfig;
import com.foreveross.atwork.modules.dropbox.DropboxFileData;
import com.foreveross.atwork.modules.dropbox.activity.DropboxBaseActivity;
import com.foreveross.atwork.modules.dropbox.component.DropboxFileItem;
import com.foreveross.atwork.modules.dropbox.component.DropboxTimelineItemView;
import com.foreveross.theme.manager.SkinMaster;

import java.util.HashSet;
import java.util.Set;

/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 * Created by reyzhang22 on 16/9/8.
 */
public class DropboxFileAdapter extends BaseAdapter {

    private Activity mContext;
    private DropboxFileData mFileData;
    private Set<String> mSelectedList = new HashSet<>();

    private ListView mListView;

    private DropboxBaseActivity.DisplayMode mDisplayMode;
    private DropboxFileItem.OnItemIconClickListener mIconClickListener;

    private boolean mMoveOrCopy;
    private DropboxConfig mDropboxConfig;

    public DropboxFileAdapter(Activity context, DropboxFileData fileData, Set<String> selectedSet, boolean moveOrCopy, DropboxConfig dropboxConfig) {
        mContext = context;
        mFileData = fileData;
        mSelectedList = selectedSet;
        mMoveOrCopy = moveOrCopy;
        mDropboxConfig = dropboxConfig;
    }

    public void setListView(ListView listView) {
        mListView = listView;
    }

    public void setIconSelectListener(DropboxFileItem.OnItemIconClickListener listener) {
        mIconClickListener = listener;
    }

    public void notifyDataChange(DropboxFileData fileData) {
        mFileData = fileData;
        notifyDataSetChanged();
    }
    public void clear() {
        mFileData = null;
    }

    public void setDisplayMode(DropboxBaseActivity.DisplayMode displayMode) {
        mDisplayMode = displayMode;
        notifyDataSetChanged();
    }

    public void setSelectedList(Set<String> selectedList) {
        mSelectedList = selectedList;
        notifyDataSetChanged();
    }

    public void updateProgress(Dropbox dropbox) {
        try {
            updateView(dropbox);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public int getCount() {
        return mFileData.mSubList.size();
    }

    @Override
    public Dropbox getItem(int i) {
        return mFileData.mSubList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        DropboxFileItem fileItem = null;
        DropboxTimelineItemView timelineItem = null;
        if (mFileData.mSubList.get(i).mIsTimeline) {
            if (view == null || view instanceof DropboxFileItem) {
                view = new DropboxTimelineItemView(mContext);
            }

        } else {
            if (view == null || view instanceof DropboxTimelineItemView) {
                view = new DropboxFileItem(mContext);
            }

        }
        if (view instanceof DropboxFileItem) {
            fileItem = (DropboxFileItem)view;
            fileItem.setOnItemSelectedListener(mIconClickListener);
            fileItem.setDropbox(mFileData.mSubList.get(i), mDisplayMode, mSelectedList, mMoveOrCopy, mDropboxConfig);
        }
        if (view instanceof DropboxTimelineItemView) {
            timelineItem = (DropboxTimelineItemView)view;
            timelineItem.setTimeLine(mFileData.mSubList.get(i).mFileName);
        }

        SkinMaster.getInstance().changeTheme((ViewGroup) view);

        return view;
    }

    private void updateView(Dropbox dropbox)  throws  IndexOutOfBoundsException{
        if (dropbox == null) {
            return;
        }
        ///得到第1个可显示控件的位置
        int visiblePos = mListView.getFirstVisiblePosition();
        Dropbox itemDropbox = null;
        for (int i = visiblePos, j = mListView.getLastVisiblePosition(); i <= j; i++) {
            itemDropbox = ((Dropbox) mListView.getItemAtPosition(i));
            if (itemDropbox == null) {
                continue;
            }
            if (dropbox.mFileId.equalsIgnoreCase(itemDropbox.mFileId)) {
                mFileData.mSubList.set(i, dropbox);
                View view = mListView.getChildAt(i - visiblePos);
                DropboxFileItem item = (DropboxFileItem) view;
                item.refreshProgress(dropbox);
                getView(i, view, mListView);
                break;
            }
        }
    }

}
