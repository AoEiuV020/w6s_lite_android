package com.foreveross.atwork.modules.organization.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.foreveross.atwork.manager.model.ApplyingOrganization;
import com.foreveross.atwork.modules.organization.component.ApplyingItemView;
import com.foreveross.theme.manager.SkinMaster;

import java.util.List;

/**
 * Created by dasunsy on 16/6/14.
 */
public class OrgApplyingListAdapter extends BaseAdapter {

    public Context mContext;
    public List<ApplyingOrganization> mApplyingList;

    public OrgApplyingListAdapter(Context context, List<ApplyingOrganization> applyingOrganizationList) {
        mContext = context;
        mApplyingList = applyingOrganizationList;
    }

    @Override
    public int getCount() {
        return mApplyingList.size();
    }

    @Override
    public ApplyingOrganization getItem(int position) {
        return mApplyingList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ApplyingOrganization applyingOrganization = getItem(position);
        if (null == convertView) {
            convertView = new ApplyingItemView(mContext);
        }

        ((ApplyingItemView) convertView).refreshView(applyingOrganization);

        SkinMaster.getInstance().changeTheme((ViewGroup) convertView);

        return convertView;
    }
}
