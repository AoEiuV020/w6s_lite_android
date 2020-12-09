package com.foreveross.atwork.tab.nativeTab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.support.NoticeTabAndBackHandledFragment;
import com.foreveross.atwork.tab.helper.BeeworksTabHelper;

/**
 * Created by lingen on 15/12/23.
 */
public class NativeTabFragment extends NoticeTabAndBackHandledFragment {

    private static final String TAG = NativeTabFragment.class.getSimpleName();

    private LinearLayout linearLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_native_tab, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void findViews(View view) {
        linearLayout = view.findViewById(R.id.native_view_layout);
    }

    private void initView() {
        BeeworksTabHelper.getInstance().getNativeDefineView(mActivity,  linearLayout, BeeWorks.getInstance().getBeeWorksTabById(mActivity, mId));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        BeeworksTabHelper.getInstance().refreshMap(mId);
    }



    public String getFragmentName(){
        return mTabTitle;
    }

    @Override
    protected boolean onBackPressed() {
        return false;
    }

}
