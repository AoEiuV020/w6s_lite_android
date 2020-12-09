package com.foreveross.atwork.modules.downLoad.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.modules.downLoad.component.DownloadSearchResultItem;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wuzejie on 2020/1/14.
 * Description:我的下载的搜索页面的返回结果listView的适配器
 */
public class DownloadSearchResultAdapter extends BaseAdapter {

    private List<FileData> mResultList = new ArrayList();

    private Context mContext;

    private String mSearchKey;

    public DownloadSearchResultAdapter(Context context) {
        mContext = context;

    }

    public void setResultList(List<FileData> resultList, String searchKey) {
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
    public FileData getItem(int i) {
        return mResultList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        DownloadSearchResultItem resultItem = null;
        if (view == null) {
            view = new DownloadSearchResultItem(mContext);
        }
        resultItem = (DownloadSearchResultItem)view;
        resultItem.setFile(mResultList.get(i), mSearchKey);
        return resultItem;
    }
}
