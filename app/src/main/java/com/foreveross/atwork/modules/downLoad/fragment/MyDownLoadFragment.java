package com.foreveross.atwork.modules.downLoad.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.viewPager.AdjustHeightViewPager;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.modules.downLoad.activity.MyDownLoadActivity;
import com.foreveross.atwork.modules.downLoad.activity.MyDownLoadSearchActivity;
import com.foreveross.atwork.modules.downLoad.adapter.DownloadPagerAdapter;
import com.foreveross.atwork.modules.file.dao.RecentFileDaoService;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkUtil;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import static com.foreveross.atwork.modules.downLoad.activity.MyDownLoadActivity.REFRESH_DOWN_LOAD_VIEW_PAGER;
import static com.foreveross.atwork.modules.file.fragement.FileSelectFragment.MSG_GET_RECENT_FILES_SUCCESS;

/**
 * Created by wuzejie on 2020/1/8.
 */

public class MyDownLoadFragment extends BackHandledFragment {

    private ImageView mIvBackBtn;
    private ImageView mIvSearchBtn;
    private TabLayout mMyDownloadTabLayout;
    private AdjustHeightViewPager mMyDownloadViewPager;
    private DownloadPagerAdapter mDownloadPagerAdapter;
    private List<FileData> mFileList;
    private int mWhat;

    private Boolean isNotifyDataSetChanged = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mWhat = msg.what;
            switch (mWhat) {

                case MSG_GET_RECENT_FILES_SUCCESS:
                    if (mFileList != null) {
                        mFileList.clear();
                    }
                    mFileList = (List<FileData>) msg.obj;
                    if (mFileList != null) {
                        initViewPager();
                    }
                    break;
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.activity_my_download, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshUI();
        getFileData();
        registerBroadcast();
        //initViewPager();
        registerListener();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(mWhat);
        unregisterBroadcast();
        clearBroadcast();
    }

    @Override
    protected void findViews(View view) {

        mIvBackBtn = view.findViewById(R.id.back_btn);
        mIvSearchBtn = view.findViewById(R.id.search_btn);
        mMyDownloadTabLayout = view.findViewById(R.id.my_download_tabLayout);
        mMyDownloadViewPager = view.findViewById(R.id.my_download_viewPager);

    }

    private void refreshUI() {
        AtworkUtil.tempHandleIconColor(mIvSearchBtn);
    }

    private BroadcastReceiver mRefreshViewBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (REFRESH_DOWN_LOAD_VIEW_PAGER.equals(action)) {
                isNotifyDataSetChanged = true;
                getFileData();
            }
        }
    };
    private void registerBroadcast() {
        IntentFilter refreshViewIntentFilter = new IntentFilter();
        refreshViewIntentFilter.addAction(REFRESH_DOWN_LOAD_VIEW_PAGER);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mRefreshViewBroadcastReceiver, refreshViewIntentFilter);
    }
    private void unregisterBroadcast() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mRefreshViewBroadcastReceiver);
    }
    private void clearBroadcast() {
        mRefreshViewBroadcastReceiver = null;
    }


    private void initViewPager() {

        if(isNotifyDataSetChanged){
            mDownloadPagerAdapter.updateViewpager(mFileList);
        }else{
            mDownloadPagerAdapter = new DownloadPagerAdapter(mActivity, mFileList);
            mMyDownloadViewPager.setAdapter(mDownloadPagerAdapter);
            mMyDownloadTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    mMyDownloadViewPager.setCurrentItem(tab.getPosition());
                    MyDownLoadActivity.mCurrentViewPagerPosition = tab.getPosition();
                    mMyDownloadViewPager.reMeasureCurrentPage(mMyDownloadViewPager.findViewWithTag(AdjustHeightViewPager.TAG + tab.getPosition()));
                }
                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }
                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            mMyDownloadViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {
                    mMyDownloadViewPager.reMeasureCurrentPage(mMyDownloadViewPager.findViewWithTag(AdjustHeightViewPager.TAG + i));
                    MyDownLoadActivity.mCurrentViewPagerPosition = i;
                    TabLayout.Tab tab = mMyDownloadTabLayout.getTabAt(i);
                    tab.select();
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });
            mMyDownloadViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mMyDownloadTabLayout));
        }
        isNotifyDataSetChanged = false;
    }

    /**
     * Descripition:获取文件列表
     */
    private void getFileData(){
        RecentFileDaoService.getInstance().getRecentFiles(mHandler);
    }

    private void registerListener() {
        mIvBackBtn.setOnClickListener(v ->{
            finish();
        });
        mIvSearchBtn.setOnClickListener(v ->{
            Intent intent = MyDownLoadSearchActivity.getIntent(mActivity);
            startActivity(intent);
        });
    }

    @Override
    protected boolean onBackPressed() {
        finish();
        return false;
    }
}
