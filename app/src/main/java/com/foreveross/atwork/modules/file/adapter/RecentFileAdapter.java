package com.foreveross.atwork.modules.file.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.modules.file.component.FileItemLinearLayoutView;

import java.util.List;

/**
 * Created by ReyZhang on 2015/5/12.
 */
public class RecentFileAdapter extends BaseAdapter {

    private static final String TAG = RecentFileAdapter.class.getSimpleName();

    private Activity mActivity;

    private List<FileData> mRecentFiles;

    public RecentFileAdapter(Activity activity, List<FileData> list) {
        if (activity == null || list == null) {
            throw new IllegalArgumentException("invalid arguments on " + TAG);
        }
        mActivity = activity;
        mRecentFiles = list;
    }

    @Override
    public int getCount() {
        return mRecentFiles.size();
    }

    @Override
    public Object getItem(int position) {
        return mRecentFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new FileItemLinearLayoutView(mActivity);
        }
        FileItemLinearLayoutView item = (FileItemLinearLayoutView)convertView;

        return item;
    }
}
