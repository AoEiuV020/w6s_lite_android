package com.foreveross.atwork.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.utils.ImageCacheHelper;

/**
 * Created by lingen on 15/5/27.
 * Description:
 */
public class SelectDialogItem extends LinearLayout {

    private TextView titleView;
    private ImageView iconView;
    private TextView messageView;
    public TextView cancelView;
    public TextView okView;

    private View titleViewLine;


    public SelectDialogItem(Context context) {
        super(context);
        initView();
    }

    public SelectDialogItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        LayoutInflater inflater1 = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater1.inflate(R.layout.component_alert_dialog, this);
        titleView = (TextView) view.findViewById(R.id.item_dialog_tv_title);
        titleViewLine = view.findViewById(R.id.item_dialog_tv_line);
        iconView = (ImageView) view.findViewById(R.id.item_dialog_image);
        iconView.setVisibility(View.GONE);
        messageView = (TextView) view.findViewById(R.id.item_dialog_tv_info);
        cancelView = (TextView) view.findViewById(R.id.item_dialog_tv_dlg_left);
        okView = (TextView) view.findViewById(R.id.item_dialog_tv_dlg_right);
    }

    public void setTitle(String title) {
        titleView.setVisibility(VISIBLE);
        titleViewLine.setVisibility(VISIBLE);
        titleView.setText(title);
    }

    public void setIcon(String mediaId) {
        ImageCacheHelper.displayImageByMediaId(mediaId, iconView, ImageCacheHelper.getRoundOptions(-1, -1));
        iconView.setVisibility(View.VISIBLE);
    }

    public void setIconResId(int resId) {
        iconView.setVisibility(VISIBLE);
        iconView.setImageResource(resId);
    }

    public void setMessage(String message) {
        messageView.setText(message);
    }

    public void noCancel() {
        cancelView.setVisibility(GONE);
    }
}
