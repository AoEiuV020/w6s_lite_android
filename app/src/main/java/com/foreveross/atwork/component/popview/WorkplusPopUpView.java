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
import com.foreveross.atwork.infrastructure.utils.ViewCompat;


public class WorkplusPopUpView extends RelativeLayout {

    private PopItemOnClickListener popItemOnClickListener;

    private PopupWindow popupWindow;

    private LinearLayout popContainerView;

    private Context mContext;

    public WorkplusPopUpView(Context context) {
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
        View view = inflater.inflate(R.layout.component_workplus_popview, this);
        popContainerView = view.findViewById(R.id.pop_view_container);

        ViewCompat.setElevation(popContainerView, 15);

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
        final WorkplusPopViewItemView popViewItem = new WorkplusPopViewItemView(getContext());
        popViewItem.setItem(resId, resName, title);
        popViewItem.setOnClickListener(v -> {
            if (popItemOnClickListener != null) {
                popItemOnClickListener.click(popViewItem.getTitle(), pos);
            }
        });
        popContainerView.addView(popViewItem);

    }

    public void addPopItem(int resId, String title, final int pos) {
        final WorkplusPopViewItemView popViewItem = new WorkplusPopViewItemView(getContext());
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

            PopupWindowCompat.setOverlapAnchor(popupWindow, true);
            PopupWindowCompat.showAsDropDown(popupWindow, moreView, 0, 0, 0);
        }
    }


    public void dismiss() {
        popupWindow.dismiss();
    }


}
