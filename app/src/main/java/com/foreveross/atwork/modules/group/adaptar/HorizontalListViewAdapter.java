package com.foreveross.atwork.modules.group.adaptar;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.modules.group.component.SelectContactHeadItemView;

/**
 * Created by lingen on 15/4/23.
 * Description:
 */
public class HorizontalListViewAdapter extends ArrayAdapter<ShowListItem> {


    private Context context;

    private SelectContactHeadItemView.RemoveContactListener removeContactListener;

    public HorizontalListViewAdapter(Context context, SelectContactHeadItemView.RemoveContactListener removeContactListener) {
        super(context, 0);
        this.context = context;
        this.removeContactListener = removeContactListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ShowListItem contact = getItem(position);
        if (convertView == null) {
            convertView = new SelectContactHeadItemView(context);
        }
        ((SelectContactHeadItemView) (convertView)).setRemoveContactListener(removeContactListener);
        ((SelectContactHeadItemView) (convertView)).refreshView(contact);
        return convertView;
    }
}
