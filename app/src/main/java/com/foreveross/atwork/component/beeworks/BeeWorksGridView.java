package com.foreveross.atwork.component.beeworks;/**
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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 */


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.AtWorkGridView;
import com.foreveross.atwork.component.viewPager.WrapContentHeightViewPager;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorksGrid;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorksTab;
import com.foreveross.atwork.infrastructure.beeworks.NativeContent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by reyzhang22 on 16/3/14.
 */
public class BeeWorksGridView extends FrameLayout {

    private static final String TAG = BeeWorksGridView.class.getSimpleName();

    private Activity mActivity;

    private WrapContentHeightViewPager mViewPager;

    private List<AtWorkGridView> mGridViews;

    private BeeWorksGridPagerAdapter mAdapter;

    private CircleIndicator mCircleIndicator;

    public BeeWorksGridView(Activity context) {
        super(context);
        initView(context);
        mActivity = context;
    }


    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_beeworks_gridview, this);
        mViewPager = (WrapContentHeightViewPager)view.findViewById(R.id.grid_view_pager);
        mCircleIndicator = (CircleIndicator)view.findViewById(R.id.grid_indicator);

    }

    /**
     * 设置gridview
     * @param pageCount     页面数量
     * @param beeWorksGrids 详细view下的item
     */
    public void setGridViews(final List<BeeWorksGrid> beeWorksGrids, int pageCount, NativeContent content, BeeWorksTab beeWorksTab) {
        mGridViews = null;
        mGridViews = new ArrayList<AtWorkGridView>();
        for (int i = 0; i < pageCount; i++) {
            AtWorkGridView appPage = new AtWorkGridView(mActivity, content.mShowLine);
            appPage.setSelector(new StateListDrawable());
            appPage.setNumColumns(content.mColumns);
            BeeWorksGridAdapter beeWorksGridAdapter = new BeeWorksGridAdapter(mActivity, beeWorksGrids, i, content.mColumns * content.mRows);
            beeWorksGridAdapter.setTabId(beeWorksTab.id);
            appPage.setAdapter(beeWorksGridAdapter);
            mGridViews.add(appPage);
        }
        mAdapter = new BeeWorksGridPagerAdapter(mActivity, mGridViews);
        mViewPager.setAdapter(mAdapter);
        if (pageCount > 1) {
            mCircleIndicator.setViewPager(mViewPager);
        }

    }

    public void refreshAdapter() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
