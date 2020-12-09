package com.foreveross.atwork.modules.dropbox.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.dropbox.DropboxConfig;
import com.foreveross.atwork.modules.dropbox.activity.DropboxBaseActivity;
import com.foreveross.atwork.modules.dropbox.component.DropboxFileItem;
import com.foreveross.atwork.modules.dropbox.component.DropboxTimelineItemView;
import com.foreveross.atwork.modules.dropbox.component.SortedTypeItem;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
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
public class SortedTypeAdapter extends BaseExpandableListAdapter {

    private LinkedHashMap<String,List<Dropbox>> mSortedMap = new LinkedHashMap<>();

    private Context mContext;

    private SortedTypeItem.OnImageGridItemClickListener mListener;

    private Dropbox.DropboxFileType mFileType;

    private Set<String> mSelectedSet = new HashSet<>();

    private DropboxFileItem.OnItemIconClickListener mIconClickListener;

    private DropboxConfig mDropboxConfig;

    public SortedTypeAdapter(Context context, LinkedHashMap<String, List<Dropbox>> sortedMap, Dropbox.DropboxFileType fileType, DropboxConfig dropboxConfig,
                             SortedTypeItem.OnImageGridItemClickListener listener, DropboxFileItem.OnItemIconClickListener iconClickListener) {
        mContext = context;
        mSortedMap = sortedMap;
        mFileType = fileType;
        mDropboxConfig = dropboxConfig;
        mListener = listener;
        mIconClickListener = iconClickListener;
    }

    @Override
    public int getGroupCount() {
        return mSortedMap.size();
    }

    @Override
    public int getChildrenCount(int i) {
        if (Dropbox.DropboxFileType.Image.equals(mFileType)) {
            return 1;
        }
        return getChileList(i).size();
    }

    @Override
    public String getGroup(int i) {

        return getKey(i);
    }

    @Override
    public Dropbox getChild(int i, int i1) {
        return getChileList(i).get(i1);
    }

    private List<Dropbox> getChileList(int pos) {
        String key = getKey(pos);
        List<Dropbox> list =  mSortedMap.get(key);
        Collections.sort(list, (dropbox, t1) -> new Date(t1.mLastModifyTime).compareTo(new Date(dropbox.mLastModifyTime)));
        return list;
    }

    private String getKey(int pos) {
        Set<String> set = mSortedMap.keySet();
        String key = "";
        int j = 0;
        for(String keyString : set) {
            if (pos == j) {
                key = keyString;
            }
            j++;
        }
        return key;
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean b, View view, ViewGroup viewGroup) {
        DropboxTimelineItemView groupItem = null;
        if (view == null) {
            view = new DropboxTimelineItemView(mContext);
        }
        groupItem = (DropboxTimelineItemView)view;
        groupItem.setTimeLine(getGroup(groupPosition));
        groupItem.handleLineVisibility(true);

        return groupItem;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View view, ViewGroup viewGroup) {
        if (mFileType == Dropbox.DropboxFileType.Image) {
            SortedTypeItem childItem = null;
            if (view == null) {
                view = new SortedTypeItem(mContext, mListener);
            }
            childItem = (SortedTypeItem)view;
            childItem.setList(getChileList(groupPosition));

            //最后个条目不显示底部的线条
            if(groupPosition == getGroupCount() -1 && childPosition == getChildrenCount(groupPosition) - 1) {
                childItem.handleLineVisibility(false);

            } else {
                childItem.handleLineVisibility(true);

            }

        } else {
            DropboxFileItem fileItem = null;
            if (view == null) {
                view = new DropboxFileItem(mContext);
            }
            fileItem = (DropboxFileItem)view;
            fileItem.setDropbox(getChild(groupPosition, childPosition), DropboxBaseActivity.DisplayMode.Normal, mSelectedSet, false, mDropboxConfig);
            fileItem.setOnItemSelectedListener(mIconClickListener);

            //最后个条目不显示底部的线条
            if(groupPosition == getGroupCount() -1 && childPosition == getChildrenCount(groupPosition) - 1) {
                fileItem.handleLineVisibility(false);

            } else {
                fileItem.handleLineVisibility(true);

            }

        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
