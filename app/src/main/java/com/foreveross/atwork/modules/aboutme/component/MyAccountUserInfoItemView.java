package com.foreveross.atwork.modules.aboutme.component;/**
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
 */


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.domain.UserSchemaSettingItem;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.utils.AtworkUtil;

/**
 * Created by reyzhang22 on 15/12/22.
 */
public class MyAccountUserInfoItemView extends RelativeLayout {


    //private RelativeLayout personalInfoCard;
    //private ImageView imagePersonalAvatar,imageUpdatePersonalAvatar;


    /**
     * 列表
     */
    private RelativeLayout mLayout;

    /**
     * 帐号key
     */
    private TextView mMyAccountKey;



    /**
     * 帐号Value
     */
    private TextView mMyAccountValue;

    /**
     * 帐号图标
     */
    private ImageView mMyAccountIcon;

    /**
     * 分割线
     * */
    private View mVCommonDivider;

    /**
     * 帐号箭头
     */
    private ImageView mMyAccountArrow;

    private UserSchemaSettingItem mUserSchemaSettingItem;



    public MyAccountUserInfoItemView(Context context) {
        super(context);
        initViews(context);
    }

    public MyAccountUserInfoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public MyAccountUserInfoItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_my_account_info, this);
        mLayout = view.findViewById(R.id.my_account_info_layout);
        mMyAccountKey = view.findViewById(R.id.my_account_info_key);
        mMyAccountValue = view.findViewById(R.id.my_account_info_value);
        mMyAccountIcon = view.findViewById(R.id.my_account_info_icon);
        mMyAccountArrow = view.findViewById(R.id.my_account_info_arrow);
        mVCommonDivider = view.findViewById(R.id.v_common_divider);
    }

    public void setMyAccountItemInfo(User user, UserSchemaSettingItem dataSchema) {
        mUserSchemaSettingItem = dataSchema;

        mMyAccountKey.setText(AtworkUtil.getUserDataSchemaAliasI18n(dataSchema.getAlias()));

        setMyAccountItemValue(user, dataSchema);

        canOps(dataSchema);
    }

    private void setMyAccountItemValue(User user, UserSchemaSettingItem dataSchema) {
        switch (dataSchema.getProperty()) {
            case "gender":
                if ("male".equalsIgnoreCase(user.mGender)) {
                    mMyAccountValue.setText(R.string.male);
                } else if ("female".equalsIgnoreCase(user.mGender)) {
                    mMyAccountValue.setText(R.string.female);
                } else {
                    mMyAccountValue.setText(StringUtils.EMPTY);
                }


                break;
            case "birthday":
                if (!StringUtils.isEmpty(user.mBirthday)) {
                    long longValue = 0;
                    String birthdayTxt;
                    try {
                        longValue = Long.valueOf(user.mBirthday);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    if (0 == longValue) {
                        birthdayTxt = "";

                    } else {
                        birthdayTxt = TimeUtil.getStringForMillis(longValue, TimeUtil.getTimeFormat1(BaseApplicationLike.baseContext));

                    }
                    mMyAccountValue.setText(birthdayTxt);
                }
                break;

            case "avatar":
                GoneAllView();
                break;

            case "qr_code":
                GoneAllView();
                break;

            case "name":
                GoneAllView();
                break;

            case "email":
                mMyAccountValue.setText(user.mEmail);
                break;

            case "phone":
                mMyAccountValue.setText(user.mPhone);
                break;

        }
    }


    private void GoneAllView() {
        mMyAccountKey.setVisibility(GONE);
        mMyAccountValue.setVisibility(GONE);
        mVCommonDivider.setVisibility(GONE);
        mMyAccountArrow.setVisibility(GONE);
        mLayout.setVisibility(GONE);
    }



    private void canOps(UserSchemaSettingItem dataSchema) {

        if (dataSchema.getModifiable()) {
            ViewUtil.setRightMargin(mMyAccountIcon, DensityUtil.dip2px(38));
            mMyAccountArrow.setVisibility(View.VISIBLE);
            setBackgroundResource(R.drawable.bg_item_common_selector);

        } else {
            ViewUtil.setRightMargin(mMyAccountIcon, DensityUtil.dip2px(10));

            mMyAccountArrow.setVisibility(View.GONE);
            setBackgroundResource(R.color.white);

}

    }


    public void setDividerVisible(boolean visible) {
        ViewUtil.setVisible(mVCommonDivider, visible);

    }


    public TextView getMyAccountValue() {
        return mMyAccountValue;
    }

    public UserSchemaSettingItem getUserSchemaSettingItem() {
        return mUserSchemaSettingItem;
    }
}
