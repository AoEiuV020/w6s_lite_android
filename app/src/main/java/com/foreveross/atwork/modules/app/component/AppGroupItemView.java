package com.foreveross.atwork.modules.app.component;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.NotScrollGridView;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.modules.app.adapter.ChildGridAdapter;
import com.foreveross.atwork.modules.app.inter.AppRemoveListener;
import com.foreveross.atwork.modules.app.model.GroupAppItem;
import com.foreveross.atwork.support.BackHandledFragment;

/**
 * Created by lingen on 15/5/8.
 */
public class AppGroupItemView extends LinearLayout {

    private GroupAppItem groupAppItem;

    private NotScrollGridView scrollGridView;

    private ChildGridAdapter childGridAdapter;

    private AppRemoveListener appRemoveListener;

    private BackHandledFragment.OnK9MailClickListener mailClickListener;

    private boolean removeAble;
    private Activity mActivity;

    public AppGroupItemView(Activity context) {
        super(context);
        mActivity = context;
        initView(context);
        initData();
    }

    public AppGroupItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mActivity = (Activity) context;
        initView(context);
        initData();
    }

    private void initData() {
        childGridAdapter = new ChildGridAdapter(mActivity);
        scrollGridView.setAdapter(childGridAdapter);
    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_app_expandlist_child, this);
        scrollGridView = view.findViewById(R.id.appGridView);
    }

    public void refreshView(GroupAppItem groupAppItem, boolean removeAble, boolean canClickable, int paddingLength) {
        this.removeAble = removeAble;
        this.groupAppItem = groupAppItem;
        childGridAdapter.refreshAppItems(groupAppItem, removeAble, canClickable);
        childGridAdapter.setAppRemoveListener(appRemoveListener);
        childGridAdapter.setOnK9MailClickListener(mailClickListener);
        scrollGridView.setOnTouchInvalidPositionListener(motionEvent -> {
            if (appRemoveListener != null) {
                appRemoveListener.removeMode(false);
            }
            return false;
        });

        //12dp 转换成 px
        int vLength = (int) (DensityUtil.DP_8_TO_PX * 1.5);
        if (0 <= paddingLength) {
            scrollGridView.setPadding(paddingLength, vLength, 0, vLength);
        } else {
            scrollGridView.setPadding(vLength, vLength, vLength, vLength);
        }

    }

    public void setAppRemoveListener(AppRemoveListener appRemoveListener) {
        this.appRemoveListener = appRemoveListener;
    }

    public void setMailClickListener(BackHandledFragment.OnK9MailClickListener listener) {
        this.mailClickListener = listener;
    }

}
