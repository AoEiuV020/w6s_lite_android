package com.foreveross.atwork.tab.nativeTab.component;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.foreveross.atwork.component.listview.ScrollListView;
import com.foreveross.atwork.infrastructure.beeworks.NativeContent;
import com.foreveross.atwork.infrastructure.beeworks.NativeItem;

/**
 * Created by lingen on 15/12/23.
 */
public class ListGroupView extends ScrollListView {


    private NativeContent nativeContent;

    private ListGroupAdapter listGroupAdapter;

    private String tabId;

    public ListGroupView(Context context, NativeContent nativeContent,String tabId) {
        super(context);
        this.nativeContent = nativeContent;
        this.tabId = tabId;
        listGroupAdapter = new ListGroupAdapter(tabId, getContext(), nativeContent);
        setAdapter(listGroupAdapter);
        refreshAdapter();
    }

    public void refreshAdapter(){
        listGroupAdapter.notifyDataSetChanged();
    }
}

class ListGroupAdapter extends BaseAdapter {

    private NativeContent nativeContent;

    private Context context;

    private String tabId;

    public ListGroupAdapter(String tabId,Context context, NativeContent nativeContent) {
        this.nativeContent = nativeContent;
        this.context = context;
        this.tabId = tabId;
    }

    @Override
    public int getCount() {
        return this.nativeContent.mValues.size();
    }

    @Override
    public NativeItem getItem(int position) {
        return nativeContent.mValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NativeItem nativeItem = getItem(position);
        if (convertView == null) {
            convertView = new ListNativeItemView(context);
        }
        ListNativeItemView listNativeItemView = (ListNativeItemView) convertView;
        listNativeItemView.refreshView(tabId, nativeItem, listNativeItemView.mNoticeView);


        return convertView;
    }


}
