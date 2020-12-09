package com.foreveross.atwork.component;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by reyzhang22 on 15/6/30.
 */
public class SelectDialogListAdapter extends BaseAdapter {

    private ArrayList<String> mDialogItems;

    private Context mContext;

    public SelectDialogListAdapter(Context context, ArrayList<String> dialogItems) {
        mContext = context;
        mDialogItems = dialogItems;
    }

    public void addItem(String item) {
        if (mDialogItems == null) {
            mDialogItems = new ArrayList<>();
        }
        mDialogItems.add(item);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDialogItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mDialogItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new SelectDialogListItem(mContext);
        }
        SelectDialogListItem item = (SelectDialogListItem)convertView;
        item.setItem(mDialogItems.get(position));
        return convertView;
    }
}
