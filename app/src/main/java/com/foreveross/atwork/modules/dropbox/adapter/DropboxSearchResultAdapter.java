package com.foreveross.atwork.modules.dropbox.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.modules.dropbox.component.DropboxSearchResultItem;

import java.util.ArrayList;
import java.util.List;

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
 * Created by reyzhang22 on 16/9/9.
 */
public class DropboxSearchResultAdapter extends BaseAdapter {

    private List<Dropbox> mResultList = new ArrayList();

    private Context mContext;

    private String mSearchKey;

    public DropboxSearchResultAdapter(Context context) {
        mContext = context;

    }

    public void setResultList(List<Dropbox> resultList, String searchKey) {
        mResultList = resultList;
        mSearchKey = searchKey;
        notifyDataSetChanged();
    }

    public void clearData() {
        mResultList.clear();
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return mResultList.size();
    }

    @Override
    public Dropbox getItem(int i) {
        return mResultList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        DropboxSearchResultItem resultItem = null;
        if (view == null) {
            view = new DropboxSearchResultItem(mContext);
        }
        resultItem = (DropboxSearchResultItem)view;
        resultItem.setFile(mResultList.get(i), mSearchKey);
        return resultItem;
    }
}
