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


import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.app.model.LightNoticeData;
import com.foreveross.atwork.api.sdk.util.LightNoticeHelper;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorksGrid;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.common.component.LightNoticeItemView;
import com.foreveross.atwork.modules.common.lightapp.LightNoticeMapping;
import com.foreveross.atwork.modules.common.lightapp.SimpleLightNoticeMapping;
import com.foreveross.atwork.modules.main.data.TabNoticeManager;
import com.foreveross.atwork.tab.helper.BeeworksTabHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by reyzhang22 on 16/3/11.
 */
public class BeeWorksGridAdapter extends BaseAdapter {

    private Context mContext;

    private List<BeeWorksGrid> mList;

    private String mTabId;

    public BeeWorksGridAdapter(Context context, List<BeeWorksGrid> list, int page, int pageSize) {
        mContext = context;
        mList = new ArrayList<BeeWorksGrid>();
        int i = page * pageSize;
        int iEnd = i + pageSize;
        while ((i<list.size()) && (i<iEnd)) {
            mList.add(list.get(i));
            i++;
        }
    }

    public void setTabId(String tabId) {
        mTabId = tabId;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BeeWorksGridItem beeWorkGridItem = new BeeWorksGridItem(mContext);
        beeWorkGridItem.setItem(mList.get(position));
        return beeWorkGridItem;
    }

    /**
     * item view
     */
    public class BeeWorksGridItem extends RelativeLayout {

        public View mItemNS;

        public View mItemWE;

        public ImageView mImageNS;

        public ImageView mImageWE;

        public TextView mNameNS;

        public TextView mNameWE;

        private BeeWorksGrid mBeeworksGrid;

        private LightNoticeItemView mNoticeNS;

        private LightNoticeItemView mNoticeWE;

        public BeeWorksGridItem(Context context) {
            super(context);
            initViews(context);
        }

        public BeeWorksGridItem(Context context, AttributeSet attrs) {
            super(context, attrs);
            initViews(context);
        }

        public BeeWorksGridItem(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            initViews(context);
        }

        private void initViews(Context context) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_beeworks_grid, this);
            mItemNS = view.findViewById(R.id.item_NS);
            mImageNS = (ImageView)view.findViewById(R.id.item_NS_image);
            mNameNS = (TextView)view.findViewById(R.id.item_NS_name);
            mItemWE = view.findViewById(R.id.item_WE);
            mImageWE = (ImageView)view.findViewById(R.id.item_WE_image);
            mNameWE = (TextView)view.findViewById(R.id.item_WE_name);
            mNoticeNS = (LightNoticeItemView)view.findViewById(R.id.notice_ns);
            mNoticeWE = (LightNoticeItemView)view.findViewById(R.id.notice_we);
        }

        private void registerListener() {
            if (mBeeworksGrid == null) {
                return;
            }
            setOnClickListener(v -> {
                if ("URL".equalsIgnoreCase(mBeeworksGrid.mActionType) || TextUtils.isEmpty(mBeeworksGrid.mActionType)) {
                    if (TextUtils.isEmpty(mBeeworksGrid.mValue)) {
                        return;
                    }
                    boolean hideTitle = "FULL_SCREEN".equalsIgnoreCase(mBeeworksGrid.mDisplayMode);
                    WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                                                                                    .setUrl(mBeeworksGrid.mValue)
                                                                                    .setTitle(mBeeworksGrid.mTitle)
                                                                                    .setHideTitle(hideTitle);
                    Intent intent = WebViewActivity.getIntent(mContext, webViewControlAction);
                    mContext.startActivity(intent);
                    return;
                }
            });

        }

        public void setItem(BeeWorksGrid grid) {
            mBeeworksGrid = grid;
            registerListener();
            switch (grid.mNavi) {

                case WEST:
                    mItemWE.setVisibility(View.VISIBLE);
                    BeeworksTabHelper.getInstance().setBeeText(mNameWE, grid.mTitle, grid.mFontColor);
                    BeeworksTabHelper.getInstance().setBeeImage(mImageWE, grid.mIcon, 1);
                    registerLightNotice(grid, mNoticeNS);
                    break;

                case NORTH:
                    mItemNS.setVisibility(View.VISIBLE);
                    BeeworksTabHelper.getInstance().setBeeText(mNameNS, grid.mTitle, grid.mFontColor);
                    BeeworksTabHelper.getInstance().setBeeImage(mImageNS, grid.mIcon, 1);
                    registerLightNotice(grid, mNoticeWE);
                    break;

            }
        }

        //注册红点机制
        private void registerLightNotice(BeeWorksGrid grid, LightNoticeItemView noticeView) {
            if (!StringUtils.isEmpty(grid.mTipUrl)){

                final LightNoticeMapping lightNoticeModel =  SimpleLightNoticeMapping.createInstance(grid.mTipUrl, mTabId, BeeWorksGrid.class.getSimpleName() + grid.mTitle + grid.mTipUrl);
                TabNoticeManager.getInstance().registerLightNoticeMapping(lightNoticeModel);

                LightNoticeHelper.loadLightNotice(lightNoticeModel.getNoticeUrl(), BaseApplicationLike.baseContext, new LightNoticeHelper.LightNoticeListener() {
                    @Override
                    public void success(LightNoticeData lightNoticeJson) {
                        TabNoticeManager.getInstance().update(lightNoticeModel, lightNoticeJson);
                        noticeView.refreshLightNotice(lightNoticeJson);
                    }

                    @Override
                    public void fail() {

                    }
                });
            }
        }

    }


}
