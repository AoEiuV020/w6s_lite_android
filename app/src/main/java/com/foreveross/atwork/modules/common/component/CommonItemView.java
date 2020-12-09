package com.foreveross.atwork.modules.common.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.WorkplusSwitchCompat;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;

/**
 * 简单通用展示TextView的Item
 * Created by ReyZhang on 2015/5/6.
 */
public class CommonItemView extends RelativeLayout{

    private static final String TAG = CommonItemView.class.getSimpleName();

    private RelativeLayout mRlRoot;
    private ImageView mCommonImage;
    private TextView mCommonName;
    private ImageView mCommonTip;
    public ImageView mCommonArrow;
    public WorkplusSwitchCompat mSwitchBtn;
    public TextView mTvRightest;
    public LinearLayout mVersionCodeLayout;
    public TextView mVersionCodeTv;
    public RelativeLayout mRLeftArea;
    private View mVLine;

    public CommonItemView(Context context) {
        super(context);
        initView(context);
    }

    public CommonItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CommonItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_common, this);

        mRlRoot = view.findViewById(R.id.rl_root);
        mCommonImage = view.findViewById(R.id.common_icon);
        mCommonTip = view.findViewById(R.id.common_tip);
        mCommonName = view.findViewById(R.id.common_name);
        mCommonArrow = view.findViewById(R.id.arrow_right);
        mSwitchBtn = view.findViewById(R.id.switch_btn);
        mTvRightest = view.findViewById(R.id.tv_rightest);
        mVersionCodeLayout = view.findViewById(R.id.version_update_layout);
        mVersionCodeTv = view.findViewById(R.id.version_code_tv);
        mRLeftArea = view.findViewById(R.id.rl_left_area);
        mVLine = view.findViewById(R.id.iv_line_item_common);
    }

    public void setCommonImage(int ResId) {
        mCommonImage.setVisibility(View.VISIBLE);
        mCommonImage.setImageResource(ResId);
    }

    public void setCommonName(String name) {
        mCommonName.setText(name);
    }

    public void showCommonTip(boolean show) {
        if (show) {
            mCommonTip.setVisibility(View.VISIBLE);
            return;
        }
        mCommonTip.setVisibility(View.GONE);
    }


    public void showSwitchButton(boolean show) {
        if (show) {
            mSwitchBtn.setVisibility(View.VISIBLE);
            mCommonArrow.setVisibility(View.GONE);
        } else {
            mSwitchBtn.setVisibility(View.GONE);
            mCommonArrow.setVisibility(View.VISIBLE);

        }

    }

    public void setWalletItemAreaMargin(int left, int right) {

        mRlRoot.setPadding(left, 0, 0, 0);

        LinearLayout.LayoutParams arrowLayoutParams = (LinearLayout.LayoutParams) mCommonArrow.getLayoutParams();
        arrowLayoutParams.setMargins(left, 0, right, 0);
    }



    public WorkplusSwitchCompat getSwitchBtn() {
        return mSwitchBtn;
    }

    public TextView getCommonNameTv() {
        return mCommonName;
    }

    public void showNowNewVersionTip() {
        mTvRightest.setVisibility(View.VISIBLE);
        mVersionCodeLayout.setVisibility(View.GONE);
    }

    public void showVersionName() {
        mVersionCodeLayout.setVisibility(View.VISIBLE);
        mTvRightest.setVisibility(View.GONE);
    }

    public void setLineVisible(boolean visible) {
        ViewUtil.setVisible(mVLine, visible);
    }
}
