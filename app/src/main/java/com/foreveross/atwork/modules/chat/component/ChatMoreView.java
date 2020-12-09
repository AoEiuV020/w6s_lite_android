package com.foreveross.atwork.modules.chat.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.ViewPager;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.chat.adapter.ChatMorePagerAdapter;
import com.foreveross.atwork.modules.chat.model.ChatMoreItem;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;

import java.util.ArrayList;
import java.util.List;


public class ChatMoreView extends LinearLayout {

    private ViewPager mVpMore;
    private LinearLayout mLlGalleryPoint;

    private ArrayList<ImageView> mIvDotList = new ArrayList<>();

    private ChatMorePagerAdapter mChatMorePagerAdapter;

    private List<List<ChatMoreItem>> mChatMoreItemList = new ArrayList<>();

    private int mCurrentAdvertIndex;


    public ChatMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        findViews();
        initData();
        registerListener();
    }




    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_detail_more_pager, this);
    }

    private void findViews() {
        mVpMore = findViewById(R.id.vp_more);
        mLlGalleryPoint = findViewById(R.id.gallery_point);
    }

    private void initData() {
        mChatMorePagerAdapter = new ChatMorePagerAdapter(getContext(), mChatMoreItemList);

        mVpMore.setAdapter(mChatMorePagerAdapter);
        drawSliderPoint();
    }

    public void configChatMoreItem(SessionType sessionType, boolean fileTransfer) {
        List<ChatMoreItem> mPagerItemAllList = new ArrayList<>();

        ChatMoreItem chatMoreItemPhoto = ChatMoreItem.newInstance()
                .setChatMoreAction(ChatMoreItem.ChatMoreAction.PHOTO)
                .setResId(R.mipmap.icon_photo)
                .setText(AtworkApplicationLike.getResourceString(R.string.label_image_chat_pop));

        mPagerItemAllList.add(chatMoreItemPhoto);


        ChatMoreItem chatMoreItemCamera = ChatMoreItem.newInstance()
                .setChatMoreAction(ChatMoreItem.ChatMoreAction.CAMERA)
                .setResId(R.mipmap.icon_crema)
                .setText(AtworkApplicationLike.getResourceString(R.string.label_camera_chat_pop));
        mPagerItemAllList.add(chatMoreItemCamera);


        ChatMoreItem chatMoreItemFile = ChatMoreItem.newInstance()
                .setChatMoreAction(ChatMoreItem.ChatMoreAction.FILE)
                .setResId(R.mipmap.icon_file)
                .setText(AtworkApplicationLike.getResourceString(R.string.label_file_chat_pop));
        mPagerItemAllList.add(chatMoreItemFile);

        ChatMoreItem chatMoreItemMicroVideo = ChatMoreItem.newInstance()
                .setChatMoreAction(ChatMoreItem.ChatMoreAction.MICRO_VIDEO)
                .setResId(R.mipmap.icon_micro_video)
                .setText(AtworkApplicationLike.getResourceString(R.string.label_micro_video_chat_pop));
        mPagerItemAllList.add(chatMoreItemMicroVideo);


        if (isEnableShareLocation(sessionType)) {
            ChatMoreItem chatMoreItemShareLocation = ChatMoreItem.newInstance()
                    .setChatMoreAction(ChatMoreItem.ChatMoreAction.LOCATION)
                    .setResId(R.mipmap.icon_chat_more_location)
                    .setText(AtworkApplicationLike.getResourceString(R.string.location));
            mPagerItemAllList.add(chatMoreItemShareLocation);
        }

        if (isEnableCard(sessionType)) {
            ChatMoreItem chatMoreItemCard = ChatMoreItem.newInstance()
                    .setChatMoreAction(ChatMoreItem.ChatMoreAction.CARD)
                    .setResId(R.mipmap.icon_card)
                    .setText(AtworkApplicationLike.getResourceString(R.string.label_personal_card_chat_pop));
            mPagerItemAllList.add(chatMoreItemCard);
        }


        if (isEnableVoip(sessionType, fileTransfer)) {
            ChatMoreItem chatMoreItemVoip = ChatMoreItem.newInstance()
                    .setChatMoreAction(ChatMoreItem.ChatMoreAction.VOIP)
                    .setResId(R.mipmap.icon_conf)
                    .setText(AtworkApplicationLike.getResourceString(R.string.label_voip_meeting_chat_pop));
            mPagerItemAllList.add(chatMoreItemVoip);
        }


        if (isEnableMeeting(sessionType, fileTransfer)) {
            ChatMoreItem chatMoreItemMeeting = ChatMoreItem.newInstance()
                    .setChatMoreAction(ChatMoreItem.ChatMoreAction.Meeting)
                    .setResId(R.mipmap.icon_start_meeting)
                    .setText(AtworkApplicationLike.getResourceString(R.string.start_meeting));
            mPagerItemAllList.add(chatMoreItemMeeting);
        }

        pagingChatMoreItem(mPagerItemAllList);
        drawSliderPoint();


    }


    public void configBingMoreItem() {
        List<ChatMoreItem> mPagerItemAllList = new ArrayList<>();

        ChatMoreItem chatMoreItemPhoto = ChatMoreItem.newInstance()
                .setChatMoreAction(ChatMoreItem.ChatMoreAction.PHOTO)
                .setResId(R.mipmap.icon_photo)
                .setText(AtworkApplicationLike.getResourceString(R.string.label_image_chat_pop));

        mPagerItemAllList.add(chatMoreItemPhoto);


        ChatMoreItem chatMoreItemCamera = ChatMoreItem.newInstance()
                .setChatMoreAction(ChatMoreItem.ChatMoreAction.CAMERA)
                .setResId(R.mipmap.icon_crema)
                .setText(AtworkApplicationLike.getResourceString(R.string.label_camera_chat_pop));
        mPagerItemAllList.add(chatMoreItemCamera);


        ChatMoreItem chatMoreItemFile = ChatMoreItem.newInstance()
                .setChatMoreAction(ChatMoreItem.ChatMoreAction.FILE)
                .setResId(R.mipmap.icon_file)
                .setText(AtworkApplicationLike.getResourceString(R.string.label_file_chat_pop));
        mPagerItemAllList.add(chatMoreItemFile);

        pagingChatMoreItem(mPagerItemAllList);
        drawSliderPoint();
    }

    private boolean isEnableCard(SessionType sessionType) {
        if(SessionType.Service.equals(sessionType)) {
            return false;
        }

        return !SessionType.LightApp.equals(sessionType);
    }

    private boolean isEnableMeeting(SessionType sessionType, boolean fileTransfer) {
        if(fileTransfer) {
            return false;
        }

        if(!AtworkConfig.ZOOM_CONFIG.isUrlEnabled()) {
            return false;
        }

        if(SessionType.Service.equals(sessionType)) {
            return false;
        }

        return !SessionType.LightApp.equals(sessionType);
    }

    private boolean isEnableVoip(SessionType sessionType, boolean fileTransfer) {
        if(fileTransfer) {
            return false;
        }

        if(SessionType.Service.equals(sessionType)) {
            return false;
        }

        return !SessionType.LightApp.equals(sessionType);
    }

    private boolean isEnableShareLocation(SessionType sessionType) {
        if (SessionType.User.equals(sessionType) || SessionType.Discussion.equals(sessionType)) {
            return true;
        }

        return false;
    }

    private void pagingChatMoreItem(List<ChatMoreItem> mPagerItemAllList) {
        for(int i = 0; i < mPagerItemAllList.size(); i++) {
            if(0 == i % 8) {
                List<ChatMoreItem> newChatMorePager = new ArrayList<>();
                mChatMoreItemList.add(newChatMorePager);
            }

            int page = i / 8;
            List<ChatMoreItem> chatMorePager  = mChatMoreItemList.get(page);
            chatMorePager.add(mPagerItemAllList.get(i));
        }

        adjustMoreViewDivder();

        mChatMorePagerAdapter.notifyDataSetChanged();
    }

    private void adjustMoreViewDivder() {
        if(1 < mChatMoreItemList.size()) {
            mChatMorePagerAdapter.setSlideLayoutHeight(DensityUtil.dip2px(40));
        } else {
            mChatMorePagerAdapter.setSlideLayoutHeight(0);

        }
    }

    private void registerListener() {

        mVpMore.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setSelectedDot(position);
                mCurrentAdvertIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void drawSliderPoint() {
        mLlGalleryPoint.removeAllViews();
        mIvDotList.clear();

        if (0 < mChatMorePagerAdapter.getCount()) {
            for (int i = 0; i < mChatMorePagerAdapter.getCount(); i++) {
                LayoutParams params = new LayoutParams(
                        15, 15);
                ImageView pointView = new ImageView(getContext());
                // 获取
                int pixels = DensityUtil.dip2px(8);
                params.leftMargin = pixels;
                if (mChatMorePagerAdapter.getCount() == 1) {
                    pointView.setVisibility(View.GONE);
                }
                mIvDotList.add(pointView);
                if (i == mCurrentAdvertIndex) {
                    pointView.setImageResource(R.mipmap.dot_drak_gray);
                } else {
                    pointView.setImageResource(R.mipmap.dot_light_gray);
                }
                mLlGalleryPoint.addView(pointView, params);
            }



        }
    }

    private void setSelectedDot(int position) {
        for (int i = 0; i < mIvDotList.size(); i++) {
            ImageView dot = mIvDotList.get(i);
            if (i == position) {
                mIvDotList.get(position).setImageResource(R.mipmap.dot_drak_gray);
            } else {
                dot.setImageResource(R.mipmap.dot_light_gray);
            }
        }
    }



    public void setChatMoreViewListener(ChatMoreViewListener chatMoreViewListener) {
        this.mChatMorePagerAdapter.setChatMoreViewListener(chatMoreViewListener);
    }

    public void setBurnMode(boolean burnMode) {
        this.mChatMorePagerAdapter.setBurnMode(burnMode);
    }

    public void setSlideLayoutHeight(int slideLayoutHeight) {
        this.mChatMorePagerAdapter.setSlideLayoutHeight(slideLayoutHeight);
    }






    /**
     * 更多工具栏事件监听
     */
    public interface ChatMoreViewListener {

        /**
         * 拍照事件
         */
        void cameraClick();

        /**
         * 选取图片事件
         */
        void photoClick();

        /**
         * 选取文件事件
         */
        void fileClick();

        /**
         * 小视频事件
         */
        void microVideoClick();

        /**
         * 音视频会议
         */
        void voipConfClick();

        /**
         * 会议管理(umeeting)
         * */
        void meetingClick();

        /**
         * 个人名片
         */
        void cardClick();

        /**
         *  地理位置信息
         */
        void locationClick();
    }
}
