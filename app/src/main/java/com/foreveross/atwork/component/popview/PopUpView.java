package com.foreveross.atwork.component.popview;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import androidx.core.widget.PopupWindowCompat;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;

/**
 * Created by lingen on 15/5/12.
 * Description:
 * 通用POP VIEW设计
 */
public class PopUpView extends RelativeLayout {

    private PopItemOnClickListener popItemOnClickListener;

    private PopupWindow popupWindow;

    private LinearLayout popContainerView;

    private Context mContext;

    public PopUpView(Context context) {
        super(context);
        mContext = context;
        initView();
        initPopView();
    }

    private void initPopView() {
        popupWindow = new PopupWindow(this, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
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
        View view = inflater.inflate(R.layout.component_popview, this);
        popContainerView = view.findViewById(R.id.pop_view_container);
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
        final PopViewItemView popViewItem = new PopViewItemView(getContext());
        popViewItem.setItem(resId, resName, title);
        popViewItem.setOnClickListener(v -> {
            if (popItemOnClickListener != null) {
                popItemOnClickListener.click(popViewItem.getTitle(), pos);
            }
        });
        popContainerView.addView(popViewItem);

    }

    public void addPopItem(int resId, String title, final int pos) {
        final PopViewItemView popViewItem = new PopViewItemView(getContext());
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
            PopupWindowCompat.showAsDropDown(popupWindow, moreView, 0, y, 0);
        }
    }

    public void hideLine() {
        try {
            //下拉菜单 最后一项不显示分割线
            PopViewItemView item = (PopViewItemView) popContainerView.getChildAt(popContainerView.getChildCount() - 1);
            item.mVLine.setVisibility(GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismiss() {
        popupWindow.dismiss();
    }


}
