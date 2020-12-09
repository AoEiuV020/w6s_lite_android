package com.foreveross.atwork.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.foreveross.atwork.R;

/**
 * Created by lingen on 15/5/13.
 * Description:
 */
public class NewMessageTipView extends RelativeLayout {

    //======不同显示方式布局 BEGIN=========
    private RelativeLayout imageView;

    private RelativeLayout dotView;

    private RelativeLayout numberView;
    //======不同显示方式布局 OVER==========

    private ImageView image;

    private NewMessageView newMessageView;

    public NewMessageTipView(Context context) {
        super(context);
        initView();
    }

    public NewMessageTipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }


    /**
     * 初始化UI VIEW
     */
    private void initView() {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.component_tip, this);
        imageView = view.findViewById(R.id.new_message_tip_image_layout);
        dotView = view.findViewById(R.id.new_message_tip_dot_layout);
        numberView = view.findViewById(R.id.new_message_tip_number_layout);
        image = view.findViewById(R.id.new_message_tip_image);
        newMessageView = view.findViewById(R.id.new_message_tip_number_view);
        dotView.setVisibility(GONE);
    }

    /**
     * 显示图片模式
     *
     * @param content
     */
    public void imageModel(byte[] content) {
        numberView.setVisibility(GONE);
        imageView.setVisibility(VISIBLE);
        dotView.setVisibility(GONE);
    }

    /**
     * 显示数字模式
     *
     * @param number
     */
    public void numberModel(int number) {
        numberView.setVisibility(VISIBLE);
        imageView.setVisibility(GONE);
        dotView.setVisibility(GONE);

        newMessageView.setNum(number);
    }

    /**
     * 红点模式
     */
    public void dotModel() {
        numberView.setVisibility(GONE);
        imageView.setVisibility(GONE);
        dotView.setVisibility(VISIBLE);
    }

}

