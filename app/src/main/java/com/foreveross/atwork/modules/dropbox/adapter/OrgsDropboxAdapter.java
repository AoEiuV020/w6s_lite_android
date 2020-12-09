package com.foreveross.atwork.modules.dropbox.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.modules.dropbox.component.DropboxOrgChileItemView;
import com.foreveross.atwork.modules.dropbox.component.DropboxOrgGroupItemView;
import com.foreveross.theme.manager.SkinMaster;

import java.util.List;
import java.util.Map;
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
public class OrgsDropboxAdapter extends BaseExpandableListAdapter {

    private Map<String, List<Discussion>> mOrgList;

    private Context mContext;

    private boolean mMoveOrCopy;

    public OrgsDropboxAdapter(Context context, boolean moveOrCopy) {
        mContext = context;
        mMoveOrCopy = moveOrCopy;
    }

    public void setData(Map<String, List<Discussion>> list) {
        mOrgList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        if (mOrgList == null) {
            return 0;
        }
        return mOrgList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        if (mOrgList == null) {
            return 0;
        }
        return mOrgList.get(getKey(i)).size();
    }

    private String getKey(int pos) {
        int j = 0;
        Set<String> set = mOrgList.keySet();
        String myKey = "";
        for (String key : set) {
            if (pos == j) {
                myKey = key;
                break;
            }
            j++;
        }
        return myKey;
    }

    @Override
    public String getGroup(int i) {
        if (mOrgList == null) {
            return "";
        }
        return getKey(i);
    }

    @Override
    public Discussion getChild(int i, int i1) {
        if (mOrgList == null) {
            return null;
        }
        return mOrgList.get(getKey(i)).get(i1);
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
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        DropboxOrgGroupItemView groupItem = null;
        if (view == null) {
            view = new DropboxOrgGroupItemView(mContext);
        }
        groupItem = (DropboxOrgGroupItemView)view;
        groupItem.setOrganization(OrganizationManager.getInstance().getOrganizationSyncByOrgCode(mContext, getGroup(i)), b);
        return groupItem;
    }


    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        DropboxOrgChileItemView chileItem = null;
        if (view == null) {
            view = new DropboxOrgChileItemView(mContext);
        }
        chileItem = (DropboxOrgChileItemView)view;
        Discussion discussion = getChild(i, i1);
        chileItem.setDiscussion(discussion, mMoveOrCopy, i1 == (getChildrenCount(i) - 1));

        SkinMaster.getInstance().changeTheme((ViewGroup) view);

        return chileItem;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
