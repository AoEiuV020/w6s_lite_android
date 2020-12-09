package com.foreveross.atwork.modules.voip.adapter.qsy;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.foreveross.atwork.modules.voip.component.qsy.TangVideoView;
import com.foreveross.atwork.modules.voip.support.qsy.TangSDKInstance;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RocXu on 2015/12/31.
 */
public class GroupVideoPagerAdapter extends PagerAdapter {
    private List<TangVideoView> videoViewList;

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public GroupVideoPagerAdapter(List<TangVideoView> list) {
        this.videoViewList = list;
    }

    @Override
    public int getCount() {
        return videoViewList == null ? 0 : videoViewList.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (!TangSDKInstance.getInstance().isVideoCallOpened()) {
            TangSDKInstance.getInstance().openVideoCall();
        }
        TangVideoView itemView = videoViewList.get(position);
        container.addView(itemView, 0);
        //fix bug B160202-004
        boolean bIsMe =  (itemView.getBindUserID().equals(TangSDKInstance.getInstance().getMySelf().mUserId));
        if (!bIsMe) {
            itemView.onLoadStart();
        }
        TangSDKInstance.getInstance().videoShowVideoItem(itemView.getBindUserID());
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        String userId = ((TangVideoView) object).getBindUserID();
        if (userId.length()>0) {
            TangSDKInstance.getInstance().videoHideVideoItem(userId, false);
        }
        container.removeView((TangVideoView) object);
    }

    /**
     * @param userid
     * @param view
     * @brief 添加一个视频画面
     */
    public void addItem(String userid, TangVideoView view) {
        if (userid.length() == 0) {
            return;
        }
        if (videoViewList == null) {
            videoViewList = new ArrayList<>();
        }
        videoViewList.add(view);
        notifyDataSetChanged();
    }

    /**
     * @param userid
     * @brief 删除一个视频画面
     */
    public void deleteItem(String userid) {
        if (videoViewList == null || userid.length() == 0) {
            return;
        }
        for (TangVideoView view : videoViewList) {
            if (userid.equals(view.getBindUserID()) ) {
                videoViewList.remove(view);
                break;
            }
        }

        TangSDKInstance.getInstance().videoHideVideoItem(userid, false);

        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        if(videoViewList != null && videoViewList.size() > 0){
            int size = videoViewList.size();
            for (int i = 0; i < size; i++) {
                if (videoViewList.get(i) == object) {
                    return i;
                }
            }
        }
        return POSITION_NONE;
    }

    public void clear() {
        if (videoViewList != null) {
            videoViewList.clear();
            videoViewList = null;
        }
    }
}
