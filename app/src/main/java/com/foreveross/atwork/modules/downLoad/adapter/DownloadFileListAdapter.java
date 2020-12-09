package com.foreveross.atwork.modules.downLoad.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.modules.downLoad.component.DownloadFileWithMonthTitleItem;
import com.foreveross.atwork.modules.downLoad.component.MyDownloadFileItem;
import com.foreveross.theme.manager.SkinMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuzejie on 20/1/12.
 */
public class DownloadFileListAdapter extends BaseAdapter {

    private static final int VIEW_COUNT = 2;

    private static final String NAKED_FILE_DATA = "NAKED_FILE_DATA";

    public static final Integer LIVE_WITH_MONTH = 0;
    public static final Integer LIVE_NO_MONTH = 1;

    private Activity mActivity;
    private List<FileData> mFileDataList;
    private String mMonthTime = "";
    private List<String> mFileDataKey = new ArrayList<String>();
    private MyDownloadFileItem.OnItemIconClickListener mOnIconSelectedListener;
    private DownloadFileWithMonthTitleItem.OnItemIconClickListener mOnIconSelectedWithMonthListener;

    /**
     * Description:根据不同的日期进行分类存放
     */
    private void getFileDataKeyList(List<FileData> fileDataList){
        mFileDataKey.clear();
        mMonthTime = "";
        for(int i = 0; i < fileDataList.size(); i++){
            String monthTime = TimeUtil.getStringForMillis(fileDataList.get(i).date, TimeUtil.getTimeFormat4(BaseApplicationLike.baseContext));
            monthTime = TimeUtil.isSameMonth(BaseApplicationLike.baseContext, TimeUtil.getCurrentTimeInMillis(), monthTime);
            if(!monthTime.equals(mMonthTime)){
                mFileDataKey.add(monthTime);
                mMonthTime = monthTime;
            }else{
                mFileDataKey.add(NAKED_FILE_DATA);
            }
        }
    }

    public DownloadFileListAdapter(Activity activity, List<FileData> fileList) {

        mActivity = activity;
        mFileDataList = fileList;
        getFileDataKeyList(mFileDataList);
    }

    public void setFileDataList(List<FileData> fileDataList) {
        mFileDataList = fileDataList;
        getFileDataKeyList(mFileDataList);
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_COUNT;
    }
    @Override
    public int getItemViewType(int position) {
        if (mFileDataKey.get(position).equals(NAKED_FILE_DATA)) {
            return LIVE_NO_MONTH;
        } else {
            return LIVE_WITH_MONTH;
        }
    }

    @Override
    public int getCount() {
        return mFileDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFileDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MyDownloadFileItem myDownloadFileItem = null;
        DownloadFileWithMonthTitleItem downloadFileWithMonthTitleItem = null;

        //隐藏月份的
        if(getItemViewType(position) == LIVE_NO_MONTH){
            if (convertView == null || convertView instanceof DownloadFileWithMonthTitleItem) {
                convertView = new MyDownloadFileItem(mActivity);
            }
        }else {
            if (convertView == null || convertView instanceof MyDownloadFileItem) {
                convertView = new DownloadFileWithMonthTitleItem(mActivity);
            }
        }
        if (convertView instanceof MyDownloadFileItem) {
            myDownloadFileItem  = (MyDownloadFileItem)convertView;
            FileData fileData = mFileDataList.get(position);
            myDownloadFileItem.setOnItemSelectedListener(mOnIconSelectedListener);
            myDownloadFileItem.setFileItemListener(fileData);
            myDownloadFileItem.setFileData(fileData);
        }
        if (convertView instanceof DownloadFileWithMonthTitleItem) {
            downloadFileWithMonthTitleItem  = (DownloadFileWithMonthTitleItem)convertView;
            FileData fileData = mFileDataList.get(position);
            downloadFileWithMonthTitleItem.setOnItemSelectedListener(mOnIconSelectedWithMonthListener);
            downloadFileWithMonthTitleItem.setFileItemListener(fileData);
            downloadFileWithMonthTitleItem.setFileData(fileData, mFileDataKey.get(position));
        }
        SkinMaster.getInstance().changeTheme((ViewGroup) convertView);

        return convertView;
    }
    public void setIconSelectListener(MyDownloadFileItem.OnItemIconClickListener listener) {
        mOnIconSelectedListener = listener;
    }
    public void setIconSelectListener(DownloadFileWithMonthTitleItem.OnItemIconClickListener listener) {
        mOnIconSelectedWithMonthListener = listener;
    }
}
