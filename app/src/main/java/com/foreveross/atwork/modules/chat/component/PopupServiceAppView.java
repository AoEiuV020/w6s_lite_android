package com.foreveross.atwork.modules.chat.component;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.popview.adapter.ServiceMenuArrayAdapter;
import com.foreveross.atwork.infrastructure.model.app.ServiceApp;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.PopupWindowCompat;

import java.util.List;


public class PopupServiceAppView extends LinearLayout {

    private PopupWindow mPopupWindow;

    private ServiceMenuArrayAdapter mTextViewArrayAdapter;

    private ListViewInPopUp mPopUpListView;

    private ServiceMenuListener mServiceMenuListener;


    public PopupServiceAppView(Context context, int width) {
        super(context);
        initView();
        initPopUpView();
        initData(width);
        registerListener();
    }

    private void registerListener() {
        mPopUpListView.setOnItemClickListener((parent, view, position, id) -> {
            ServiceApp.ServiceMenu serviceMenu = (ServiceApp.ServiceMenu) parent.getItemAtPosition(position);
            if (serviceMenu.type.equals(ServiceApp.ServiceMenuType.Click) && mServiceMenuListener != null) {
                mServiceMenuListener.clickEvent(serviceMenu);
            }

            if (serviceMenu.type.equals(ServiceApp.ServiceMenuType.VIEW) && mServiceMenuListener != null) {
                mServiceMenuListener.viewEvent(serviceMenu);
            }

            if (serviceMenu.type.equals(ServiceApp.ServiceMenuType.Tag) && mServiceMenuListener != null) {
                mServiceMenuListener.viewServiceTagEvent(serviceMenu);
            }

            mPopupWindow.dismiss();
        });
    }

    private void initView() {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.component_pop_view_list, this);
        mPopUpListView = view.findViewById(R.id.pop_view_list_view);
    }



    private void initPopUpView() {
        mPopupWindow = new PopupWindow(this, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setTouchable(true);
    }


    private void initData(int width) {
        mTextViewArrayAdapter = new ServiceMenuArrayAdapter(getContext(), width);
        mPopUpListView.setAdapter(mTextViewArrayAdapter);
    }

    public void setPopItem(List<ServiceApp.ServiceMenu> popItems, int xOff) {
        mTextViewArrayAdapter.setList(popItems, xOff);
    }

    public void pop(View moreView, int popWidth) {
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        } else {
            //center
            int xoff = (moreView.getMeasuredWidth() - popWidth) / 2;
            int popupItemHeight = DensityUtil.dip2px( 50);
            PopupWindowCompat.showAsPopUp(mPopupWindow, moreView, xoff, 20, Gravity.NO_GRAVITY, popupItemHeight * mTextViewArrayAdapter.getCount());
        }
    }


    public void dismiss() {
        mPopupWindow.dismiss();
    }

    public void setServiceMenuListener(ServiceMenuListener serviceMenuListener) {
        this.mServiceMenuListener = serviceMenuListener;
    }

    public interface ServiceMenuListener {

        void clickEvent(ServiceApp.ServiceMenu serviceMenu);

        void viewEvent(ServiceApp.ServiceMenu serviceMenu);

        void viewServiceTagEvent(ServiceApp.ServiceMenu serviceMenu);
    }
}
