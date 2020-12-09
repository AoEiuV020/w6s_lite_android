package com.foreveross.atwork.modules.downLoad.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.foreveross.atwork.component.DownloadPagerView;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.modules.downLoad.activity.MyDownLoadActivity;
import com.foreveross.theme.manager.SkinMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuzejie on 2020/1/9.
 */
public class DownloadPagerAdapter extends PagerAdapter {

    private final int PAGER_COUNT = 4;

    private static int mCounter = 4;

    private List<FileData> mFileDataList;
    private Context mContext;
    public DownloadPagerAdapter() {
        super();
    }

    public DownloadPagerAdapter(Context context, List<FileData> fileDataList) {
        this.mContext = context;
        this.mFileDataList = fileDataList;
    }

    public void updateViewpager(List<FileData> fileDataList){
        this.mFileDataList = fileDataList;
        mCounter = 0;
        notifyDataSetChanged();
    }

    public void clear() {
        mFileDataList = null;
    }
    /**
     * Description:根据文件类型更新文件列表
     */
    private List<FileData> updataFileList(int type) {
        List<FileData> fileDataList = new ArrayList<FileData>();
        if(type != 0){
            for (int i = 0; i < mFileDataList.size(); i++) {
                if (FileData.getFileType(mFileDataList.get(i).fileType) == type) {
                    fileDataList.add(mFileDataList.get(i));
                }
            }
            return fileDataList;
        } else {
            return mFileDataList;
        }
    }

    /**
     * Description:重写该方法，处理viewpager的缓存机制带来的的刷新问题
     * @param object
     * @return
     */
    @Override
    public int getItemPosition(@NonNull Object object) {
        //return super.getItemPosition(object);
        View view = (View)object;
        if(mCounter == 4){
            return POSITION_UNCHANGED;
        }
        else{
            mCounter++;
            if(MyDownLoadActivity.mCurrentViewPagerPosition == (int)view.getTag()){
                return POSITION_UNCHANGED;
            }
            else{
                return POSITION_NONE;
            }
        }

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        List<FileData> fileDataList = new ArrayList<FileData>();

        fileDataList = updataFileList(position);

        DownloadPagerView itemView = new DownloadPagerView(mContext, fileDataList, position);
        itemView.setTag(position);
        container.addView(itemView);
        SkinMaster.getInstance().changeTheme(itemView);

        return itemView;
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        try {
            container.removeView((View) object);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
