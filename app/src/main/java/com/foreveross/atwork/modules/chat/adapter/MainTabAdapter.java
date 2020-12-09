package com.foreveross.atwork.modules.chat.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.foreveross.atwork.support.NoticeTabAndBackHandledFragment;

import java.util.List;

/**
 * Created by lingen on 15/3/23.
 * Description:
 */
public class MainTabAdapter extends FragmentPagerAdapter {

    private static final String TAG = MainTabAdapter.class.getSimpleName();


    private List<Fragment> fragmentList;

    public MainTabAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public long getItemId(int position) {
        Fragment fragment = getItem(position);
        if (fragment instanceof NoticeTabAndBackHandledFragment) {
            NoticeTabAndBackHandledFragment noticeTabAndBackHandledFragment = (NoticeTabAndBackHandledFragment) fragment;
            return noticeTabAndBackHandledFragment.mId.hashCode();
        }

        return super.getItemId(position);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {

        if (object instanceof NoticeTabAndBackHandledFragment) {
            NoticeTabAndBackHandledFragment receiveNoticeTabAndBackHandleFragment = (NoticeTabAndBackHandledFragment) object;

            for (int i = 0; i < fragmentList.size(); i++) {
                Fragment fragment = fragmentList.get(i);
                if (fragment instanceof NoticeTabAndBackHandledFragment) {
                    NoticeTabAndBackHandledFragment noticeTabAndBackHandledFragment = (NoticeTabAndBackHandledFragment) fragment;

                    if(noticeTabAndBackHandledFragment.mId.equals(receiveNoticeTabAndBackHandleFragment.mId)) {
                        return i;
                    }
                }
            }
        }

        return POSITION_NONE;
    }


}
