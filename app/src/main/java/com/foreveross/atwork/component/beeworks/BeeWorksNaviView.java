package com.foreveross.atwork.component.beeworks;/**
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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 */


import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorksNavigation;
import com.foreveross.atwork.modules.app.component.WebTitleBarRightButtonView;
import com.foreveross.atwork.modules.app.model.WebRightButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by reyzhang22 on 16/5/4.
 */
public class BeeWorksNaviView extends RelativeLayout {

    private static final String TAG = BeeWorksNaviView.class.getSimpleName();

    private Context mContext;

    private RelativeLayout mLayout;

    private WebTitleBarRightButtonView mLeftButton;

    private WebTitleBarRightButtonView mRightButton;

    private TextView mLeftTitle;

    private TextView mRightTitle;

    private TextView mMiddleTitle;

    public BeeWorksNaviView(Context context) {
        super(context);
        initViews(context);
    }

    public BeeWorksNaviView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public BeeWorksNaviView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        mContext = context;

        View view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.beeworks_navigation_bar, this);
        mLeftButton = view.findViewById(R.id.navi_left_button);
        mRightButton = view.findViewById(R.id.navi_right_button);
        mLeftTitle = view.findViewById(R.id.navi_left_title);
        mMiddleTitle = view.findViewById(R.id.navi_middle_title);
        mLayout = view.findViewById(R.id.navi_layout);
        mRightTitle = view.findViewById(R.id.navi_right_title);
    }

    private void setBeeWorksNavigation(final BeeWorksNavigation navigation) {
        if (!TextUtils.isEmpty(navigation.mBackgroundColor)) {
            mLayout.setBackgroundColor(Color.parseColor(navigation.mBackgroundColor));
        }
        if (!TextUtils.isEmpty(navigation.mFontColor)) {
            mMiddleTitle.setTextColor(Color.parseColor(navigation.mFontColor));
            mLeftTitle.setTextColor(Color.parseColor(navigation.mFontColor));
            mRightTitle.setTextColor(Color.parseColor(navigation.mFontColor));
        }
        if (!TextUtils.isEmpty(navigation.mLayout) && !TextUtils.isEmpty(navigation.mTitle)) {
            if ("LEFT".equalsIgnoreCase(navigation.mLayout)) {
                mMiddleTitle.setVisibility(View.GONE);
                mRightTitle.setVisibility(View.GONE);
                mLeftTitle.setText(navigation.mTitle);
            }
            if ("RIGHT".equalsIgnoreCase(navigation.mLayout)) {
                mMiddleTitle.setVisibility(View.GONE);
                mRightTitle.setText(navigation.mTitle);
                mLeftTitle.setVisibility(View.GONE);
            }
            if ("CENTER".equalsIgnoreCase(navigation.mLayout)) {
                mMiddleTitle.setText(navigation.mTitle);
                mRightTitle.setVisibility(View.GONE);
                mLeftTitle.setVisibility(View.GONE);
            }
        }

        if (!navigation.mRightActions.isEmpty()) {
            List<List<WebRightButton>> buttons = mRightButton.getWebRightButtonsFromNavi(navigation);
            if (buttons.isEmpty()) {
                return;
            }
            mRightButton.setWebRightButton(buttons);
        }

        if (navigation.mLeftAction == null) {
            List<WebRightButton> leftButton = mRightButton.getSubButtons(navigation.mLeftAction);
            if (!leftButton.isEmpty()) {
                List<List<WebRightButton>> leftButtons = new ArrayList<>();
                leftButtons.add(leftButton);
                mLeftButton.setWebRightButton(leftButtons);
            }
        }

    }

    private void onButtonAction() {

    }


}
