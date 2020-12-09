package com.foreveross.atwork.component.popview.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.foreveross.atwork.component.popview.PopTextViewItemView;
import com.foreveross.atwork.infrastructure.model.app.ServiceApp;

import java.util.List;

/**
 * Created by lingen on 15/5/14.
 * Description:
 */
public class ServiceMenuArrayAdapter extends ArrayAdapter<ServiceApp.ServiceMenu> {

    private int popWidth;
    private int xoff = -1;

    public ServiceMenuArrayAdapter(Context context, int width) {
        super(context, 0);
        this.popWidth = width;
    }


    public void setList(List<ServiceApp.ServiceMenu> items, int xoff) {
        clear();
        addAll(items);

        this.xoff = xoff;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ServiceApp.ServiceMenu value = getItem(position);
        if (convertView == null) {
            convertView = new PopTextViewItemView(getContext());
        }
        ((PopTextViewItemView) convertView).refreshView(value, popWidth, xoff);
        return convertView;
    }
}
