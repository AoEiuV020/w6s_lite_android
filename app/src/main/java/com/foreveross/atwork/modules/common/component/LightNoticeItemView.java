package com.foreveross.atwork.modules.common.component;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.app.model.LightNoticeData;
import com.foreveross.atwork.component.NewMessageView;
import com.foreveross.atwork.modules.common.lightapp.ILightNoticeView;

/**
 * Created by lingen on 15/11/19.
 */
public class LightNoticeItemView  extends RelativeLayout implements ILightNoticeView {

    private ImageView dotView;

    private NewMessageView numView;

    private ImageView iconView;



    public LightNoticeItemView(Context context) {
        super(context);
        initView();
    }

    public LightNoticeItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_notice_view, this);
        dotView = view.findViewById(R.id.item_notice_dot);
        numView = view.findViewById(R.id.item_notice_num);
        iconView = view.findViewById(R.id.item_notice_icon);

        dotView.setVisibility(GONE);
        numView.setVisibility(GONE);
        iconView.setVisibility(GONE);


    }




    @Override
    public void refreshLightNotice(final LightNoticeData lightNoticeJson) {
        Activity activity = (Activity) getContext();
        activity.runOnUiThread(() -> {
            if (lightNoticeJson == null) {
                return;
            }
            if (lightNoticeJson.isNothing()) {
                showNothing();
                return;
            }

            if (lightNoticeJson.isDot()){
                showDot();
                return;
            }

            if (lightNoticeJson.isDigit()){
                showDigit(Integer.parseInt(lightNoticeJson.tip.num));
                return;
            }

            if (lightNoticeJson.isIcon()){
                showIcon();
                return;
            }
        });
    }

    public void showDot(){
        dotView.setVisibility(VISIBLE);
        numView.setVisibility(GONE);
        iconView.setVisibility(GONE);
    }

    public void showDigit(int num){
        dotView.setVisibility(GONE);
        numView.setVisibility(VISIBLE);
        iconView.setVisibility(GONE);
        numView.setNum(num);
    }

    public void showIcon(){
        dotView.setVisibility(GONE);
        numView.setVisibility(GONE);
        iconView.setVisibility(VISIBLE);
    }

    public void showNothing(){
        dotView.setVisibility(GONE);
        numView.setVisibility(GONE);
        iconView.setVisibility(GONE);
    }

}
