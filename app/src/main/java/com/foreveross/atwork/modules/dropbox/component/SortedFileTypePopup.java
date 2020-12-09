package com.foreveross.atwork.modules.dropbox.component;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.popview.PopUpView;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.utils.PopupWindowCompat;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.modules.dropbox.adapter.SortedFileTypeAdapter;

import java.util.List;

/**
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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 * Created by reyzhang22 on 16/9/24.
 */

public class SortedFileTypePopup extends RelativeLayout {

    private Context mContext;

    private PopUpView.PopItemOnClickListener mPopItemOnClickListener;

    private PopupWindow popupWindow;

    private GridView mSortedGrid;

    private SortedFileTypeAdapter mAdapter;

    private View mLayout;

    public SortedFileTypePopup(Context context) {
        super(context);
        mContext = context;
        initView();
        initPopView();
    }


    private void initPopView() {
        popupWindow = new PopupWindow(this, WindowManager.LayoutParams.MATCH_PARENT,
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
        View view = inflater.inflate(R.layout.component_sorted_file_type_popup, this);
        mSortedGrid = view.findViewById(R.id.file_type_grid);
        mLayout = view.findViewById(R.id.view_layout);
        mLayout.setOnClickListener(view1 -> {
            dismiss();
        });
    }

    public void addPopItem(List<String>names, List<Integer> resIds) {
        if (mAdapter == null) {
            mAdapter = new SortedFileTypeAdapter(mContext, names, resIds);
        }
        mSortedGrid.setAdapter(mAdapter);
        mSortedGrid.setOnItemClickListener((parent, view1, position, id) -> {
            mPopItemOnClickListener.click(names.get(position), position);
        });
    }


    public void setPopItemOnClickListener(PopUpView.PopItemOnClickListener popItemOnClickListener) {
        mPopItemOnClickListener = popItemOnClickListener;
    }



    public void pop(View anchorView) {
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {

            int[] location = new int[2];
            anchorView.getLocationInWindow(location);
            int yoff = location[1] + anchorView.getHeight();
            ViewUtil.setHeight(mLayout, ScreenUtils.getScreenHeight(BaseApplicationLike.baseContext) - yoff);

            PopupWindowCompat.showAsDropDown(popupWindow, anchorView, 0, 0, 0);
        }
    }

    public void dismiss() {
        popupWindow.dismiss();
    }
}
