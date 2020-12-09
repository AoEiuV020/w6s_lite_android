package com.foreveross.atwork.modules.dropbox.component;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.widget.PopupWindowCompat;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.popview.PopUpView;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.modules.dropbox.fragment.UserDropboxFragment;
import com.foreveross.atwork.utils.ThemeResourceHelper;
import com.foreveross.theme.manager.SkinHelper;


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
 * Created by reyzhang22 on 2016/10/24.
 */

public class SortedTimeAndNamePopup extends RelativeLayout {


    private Context mContext;

    private PopUpView.PopItemOnClickListener mPopItemOnClickListener;

    private PopupWindow popupWindow;

    private View mLayout;

    private View mSortedTimeItem;
    private ImageView mSortedTimeIcon;
    private TextView mSortedTime;
    private ImageView mSortedTimeSelected;

    private View mSortedNameItem;
    private ImageView mSortedNameIcon;
    private TextView mSortedName;
    private ImageView mSortedNameSelected;
    private OnPopupDismissListener mDismissListener;

    public SortedTimeAndNamePopup(Context context) {
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
        popupWindow.setOnDismissListener(() -> {
            if (mDismissListener != null) {
                mDismissListener.onPopupDismiss();
            }
        });
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.component_sorted_time_or_name_popup, this);
        mLayout = view.findViewById(R.id.view_layout);
        mLayout.setOnClickListener(view1 -> {
            dismiss();
        });
        mSortedTimeItem = mLayout.findViewById(R.id.sorted_by_time_item);
        mSortedTimeIcon = (ImageView)mSortedTimeItem.findViewById(R.id.sorted_time_icon);
        mSortedTime = (TextView)mSortedTimeItem.findViewById(R.id.sorted_time_text);
        mSortedTimeSelected = (ImageView)mSortedTimeItem.findViewById(R.id.sorted_time_selected_icon);

        mSortedNameItem = mLayout.findViewById(R.id.sorted_by_name_item);
        mSortedNameIcon = (ImageView)mSortedNameItem.findViewById(R.id.sorted_name_icon);
        mSortedName = (TextView) mSortedNameItem.findViewById(R.id.sorted_name_text);
        mSortedNameSelected = (ImageView)mSortedNameItem.findViewById(R.id.sorted_name_selected_icon);

        registerListener();
    }

    public void setPopItemOnClickListener(PopUpView.PopItemOnClickListener popItemOnClickListener) {
        mPopItemOnClickListener = popItemOnClickListener;
    }

    public void setSortedMode(UserDropboxFragment.SortedMode sortedMode) {
        onItemSelected((sortedMode == UserDropboxFragment.SortedMode.Time ) ? 0 : 1);
    }


    public void pop(View popView) {
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();

        } else {

            PopupWindowCompat.showAsDropDown(popupWindow, popView, 0, 0, 0);
        }
    }

    public void dismiss() {
        popupWindow.dismiss();
    }

    private void registerListener() {
        mSortedTimeItem.setOnClickListener(view -> {
            mPopItemOnClickListener.click(mContext.getString(R.string.sorted_by_time), 0);
            onItemSelected(0);
            dismiss();
        });

        mSortedNameItem.setOnClickListener(view -> {
            mPopItemOnClickListener.click(mContext.getString(R.string.sorted_by_name), 1);
            onItemSelected(1);
            dismiss();
        });
    }

    private void onItemSelected(int pos) {
        Drawable sortedTimeDrawable;
        Drawable sortedNameDrawable;
        int sortedTimeColor;
        int sortedNameColor;

        int size = DensityUtil.dip2px(20);

        if(0 == pos) {  //select time type
            sortedNameDrawable = ContextCompat.getDrawable(getContext(), R.mipmap.sort_by_name_unselected);
            sortedTimeDrawable = ThemeResourceHelper.getThemeResourceBitmapDrawable(getContext(), "sort_by_time_selected", sortedNameDrawable.getIntrinsicHeight());

            sortedTimeColor = SkinHelper.getTabActiveColor();
            sortedNameColor = ContextCompat.getColor(getContext(), R.color.dropbox_common_text_color);

        } else {
            sortedTimeDrawable = ContextCompat.getDrawable(getContext(), R.mipmap.sort_by_time_unselected);
            sortedNameDrawable = ThemeResourceHelper.getThemeResourceBitmapDrawable(getContext(), "sort_by_name_selected", sortedTimeDrawable.getIntrinsicHeight());

            sortedTimeColor = ContextCompat.getColor(getContext(), R.color.dropbox_common_text_color);
            sortedNameColor = SkinHelper.getTabActiveColor();

        }
        if(null != sortedTimeDrawable) {
            mSortedTimeIcon.setImageDrawable(sortedTimeDrawable);
            mSortedNameIcon.setImageDrawable(sortedNameDrawable);
        }

        mSortedTime.setTextColor(sortedTimeColor);
        mSortedName.setTextColor(sortedNameColor);

        Drawable selectedDrawable = ThemeResourceHelper.getThemeResourceBitmapDrawable(getContext(), "sort_selected", size);

        if(null != selectedDrawable) {
            mSortedTimeSelected.setImageDrawable(selectedDrawable);
            mSortedNameSelected.setImageDrawable(selectedDrawable);
        }

        mSortedTimeSelected.setVisibility(pos == 0 ? VISIBLE : GONE);
        mSortedNameSelected.setVisibility(pos == 0 ? GONE : VISIBLE);
    }

    public void setOnPopupDismissListener(OnPopupDismissListener listener) {
        mDismissListener = listener;
    }

    public interface OnPopupDismissListener {
        void onPopupDismiss();
    }
}
