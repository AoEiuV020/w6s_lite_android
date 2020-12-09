package com.foreveross.atwork.modules.file.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;

/**
 * Created by ReyZhang on 2015/4/22.
 */
public class LocalFileItem extends LinearLayout {

    private Context mContext;

    private View mTopView;
    private View mVCommonDivider;
    private ImageView mIcon;
    private TextView mName;
    private RelativeLayout mItem;

    public LocalFileItem(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public LocalFileItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public LocalFileItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_local_file, this);

        mTopView = view.findViewById(R.id.item_file_topview);
        mVCommonDivider = view.findViewById(R.id.v_common_divider);
        mItem = view.findViewById(R.id.item_file_layout);
        mIcon = view.findViewById(R.id.item_file_icon);
        mName = view.findViewById(R.id.item_file_name);
    }

    public void setIcon(int resId) {
        mIcon.setImageResource(resId);
    }

    public void setName(String name,int fileCount) {
        if (fileCount == 0) {
            name = String.format(name, "");
        } else {
            name = String.format(name, "(" + fileCount + ")");
        }

        mName.setText(name);
    }

    public void needShowTopView(boolean show) {
        if (show) {
            mTopView.setVisibility(View.VISIBLE);
            mVCommonDivider.setVisibility(View.VISIBLE);
            return;
        }
        mTopView.setVisibility(View.GONE);
        mVCommonDivider.setVisibility(View.GONE);
    }

    public void hideItem() {
        mItem.setVisibility(View.GONE);
    }

}
