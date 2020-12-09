package com.foreveross.atwork.modules.contact.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.TitleItemView;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.modules.contact.component.EmployeeSearchListItemView;
import com.foreveross.atwork.modules.search.model.SearchTextTitleItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingen on 15/3/24.
 * Description:
 */
public class EmployeeListSearchAdapter extends ArrayAdapter<ShowListItem> {

    private static final int VIEW_TYPE_CONTACT = 0;
    private static final int VIEW_TYPE_TITLE = 1;

    private Activity mActivity;

    private String mKey;


    public EmployeeListSearchAdapter(Activity activity) {
        super(activity, 0, new ArrayList<>());
        this.mActivity = activity;
    }

    public void setKey(String key) {
        this.mKey = key;
    }

    public void refreshData(List<Employee> employeeItemList) {
        clear();
        addAll(employeeItemList);

        if(20 < employeeItemList.size()) {
            add(getMoreTipItem());
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ShowListItem item = getItem(position);

        if (convertView == null) {
            if(VIEW_TYPE_TITLE == getItemViewType(position)) {
                convertView = new TitleItemView(mActivity);

            } else {
                convertView = new EmployeeSearchListItemView(mActivity);

            }
        }

        if(VIEW_TYPE_TITLE == getItemViewType(position)) {
            TitleItemView titleItemView = (TitleItemView) convertView;
            SearchTextTitleItem textTitleItem = (SearchTextTitleItem) item;

            titleItemView.setTitle(textTitleItem.getTitle());
            if (textTitleItem.center) {
                titleItemView.center();
                titleItemView.white();
            }


        } else {
            EmployeeSearchListItemView listItemView = (EmployeeSearchListItemView) convertView;
            listItemView.setSelectedMode(false);
            listItemView.refreshView((Employee) item, mKey);
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        ShowListItem item = getItem(position);
        if(item instanceof SearchTextTitleItem) {
            return VIEW_TYPE_TITLE;
        } else {
            return VIEW_TYPE_CONTACT;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public SearchTextTitleItem getMoreTipItem() {
        SearchTextTitleItem moreTip = new SearchTextTitleItem(mActivity.getResources().getString(R.string.search_user_too_much));
        moreTip.center = true;
        return moreTip;
    }

}
