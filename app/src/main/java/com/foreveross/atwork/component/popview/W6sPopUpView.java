package com.foreveross.atwork.component.popview;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;

/**
 * Created by wuzejie on 19/9/16.
 * Description:
 * 通用POP VIEW设计
 */
public class W6sPopUpView extends RelativeLayout {

    private PopItemOnClickListener popItemOnClickListener;

    private PopupWindow popupWindow;

    private LinearLayout popContainerView;

    private Context mContext;

    private View view;

    private RelativeLayout RlPopContainerLayout;

    public W6sPopUpView(Context context) {
        super(context);
        mContext = context;
        initView();
        initPopView();
    }

    private void initPopView() {
        popupWindow = new PopupWindow(this, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.component_w6s_popview, this);
        popContainerView = view.findViewById(R.id.pop_view_container);
        RlPopContainerLayout = view.findViewById(R.id.pop_container_layout);
        RlPopContainerLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    public void addPopItem(int resId, int titleResId, int pos) {
        addPopItem(resId, mContext.getString(titleResId), pos);
    }

    public void addPopItem(int resId, String resName, String title, final int pos) {
        if (resId != -1) {
            addPopItem(resId, title, pos);
            return;
        }

        //如果本地没有，去查一下网络
        final W6sPopViewItemView popViewItem = new W6sPopViewItemView(getContext());
        popViewItem.setItem(resId, resName, title);
        popViewItem.setOnClickListener(v -> {
            if (popItemOnClickListener != null) {
                popItemOnClickListener.click(popViewItem.getTitle(), pos);
            }
        });
        popContainerView.addView(popViewItem);

    }

    public void addPopItem(int resId, String title, final int pos) {
        final W6sPopViewItemView popViewItem = new W6sPopViewItemView(getContext());
        popViewItem.setItem(resId, title);
        popViewItem.setOnClickListener(v -> {
            if (popItemOnClickListener != null) {
                popItemOnClickListener.click(popViewItem.getTitle(), pos);
            }
        });
        popContainerView.addView(popViewItem);
    }

    public void setPopItemOnClickListener(PopItemOnClickListener popItemOnClickListener) {
        this.popItemOnClickListener = popItemOnClickListener;
    }


    public interface PopItemOnClickListener {
        void click(String title, int pos);
    }

    public void pop(View moreView) {
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            hideLine();

            int y = DensityUtil.DP_8_TO_PX >> 1;
//            PopupWindowCompat.showAsDropDown(popupWindow, moreView, 0, 0, 0);
            popupWindow.showAtLocation(moreView, Gravity.TOP,0,0  );
        }
    }

    public void hideLine() {
        try {
            //下拉菜单 最后一项不显示分割线
            W6sPopViewItemView item = (W6sPopViewItemView) popContainerView.getChildAt(popContainerView.getChildCount() - 1);
            item.mVLine.setVisibility(GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismiss() {
        popupWindow.dismiss();
    }


}
