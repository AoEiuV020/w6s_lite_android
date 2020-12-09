package com.foreveross.atwork.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.theme.manager.SkinHelper;
import com.foreveross.theme.manager.SkinMaster;

/**
 * Created by shadow on 2016/5/18.
 */
public class OrgSwitchDialogItemView extends LinearLayout {

    private Context mContext;

    private TextView mContentView;

    private ImageView mImgView;

    private View mLineView;

    private String mContent;

    public OrgSwitchDialogItemView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public OrgSwitchDialogItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_org_switch_dialog, this);
        mContentView = view.findViewById(R.id.org_switch_item);
        mImgView = view.findViewById(R.id.org_switch_img);
        mLineView = view.findViewById(R.id.org_switch_line);
    }

    public void setOrgName(String text) {
        mContentView.setText(text);
        mContent = text;
    }

    public void setSelectImg(int img) {

        if (R.mipmap.org_switch_select == img) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), img);
            Bitmap resultBmp = SkinMaster.getInstance().transform(bitmap, SkinHelper.getTabActiveColor());
            mImgView.setImageBitmap(resultBmp);
        } else {
            mImgView.setImageResource(img);
        }
    }

    public void setSelectVisible(int visible) {
        mImgView.setVisibility(visible);
    }

    public void setNameColor(int color) {
        mContentView.setTextColor(color);
    }

    public void hideLine() {
        mLineView.setVisibility(GONE);
    }

    public String getItemContent() {
        return mContent;
    }

    public void setTextColor(int textColor) {
        mContentView.setTextColor(textColor);
    }

}
