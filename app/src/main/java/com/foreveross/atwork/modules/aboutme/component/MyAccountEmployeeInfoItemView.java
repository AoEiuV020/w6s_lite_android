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


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.employee.DataSchema;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.modules.aboutme.activity.ModifyMyInfoActivity;
import com.foreveross.atwork.modules.aboutme.fragment.AvatarPopupFragment;
import com.foreveross.atwork.modules.contact.activity.UserAvatarPreviewActivity;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ImageCacheHelper;

/**
 * Created by reyzhang22 on 15/12/22.
 */
public class MyAccountEmployeeInfoItemView extends RelativeLayout {

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

    private Activity mActivity;

    private DataSchema mDataSchema;

    private String mKey;

    private String mValue;

    private Employee mEmployee;

    private AvatarPopupFragment.OnPhotographPathListener mOnPhotoPathListener;


    public MyAccountEmployeeInfoItemView(Context context) {
        super(context);
        initViews(context);
    }

    public MyAccountEmployeeInfoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public MyAccountEmployeeInfoItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_my_account_employee_info, this);
        mLayout = view.findViewById(R.id.my_account_info_layout);
        mMyAccountKey = view.findViewById(R.id.my_account_info_key);
        mMyAccountValue = view.findViewById(R.id.my_account_info_value);
        mMyAccountIcon = view.findViewById(R.id.my_account_info_icon);
        mMyAccountArrow = view.findViewById(R.id.my_account_info_arrow);
        mVCommonDivider = view.findViewById(R.id.v_common_divider);
    }

    public void setMyAccountCommonInfo(Activity activity, DataSchema dataSchema, String value, User user, Employee employee) {
        mActivity = activity;
        mDataSchema = dataSchema;
        mValue = value;
        mEmployee = employee;
        mMyAccountKey.setText(AtworkUtil.getEmployeeDataSchemaAliasI18n(mDataSchema));

        if ("avatar".equalsIgnoreCase(mDataSchema.mProperty)) {
            setAvatar(user, mActivity);

        } else if ("gender".equalsIgnoreCase(dataSchema.mProperty)) {
            setGenderIcon();

        } else {
            if (Employee.InfoType.DATE.equalsIgnoreCase(dataSchema.type)) {
                long longValue = 0;

                try {
                    longValue = Long.valueOf(value);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                if (0 == longValue) {
                    value = "";

                } else {
                    value = TimeUtil.getStringForMillis(longValue, TimeUtil.getTimeFormat1(BaseApplicationLike.baseContext));

                }
            }

            mMyAccountValue.setText(value);
        }

        canOps(mDataSchema);


    }

    public void setMyAccountRecordInfo(Activity activity, DataSchema dataSchema, String key, String value, Employee employee) {
        mActivity = activity;
        mDataSchema = dataSchema;
        mKey = key;
        mValue = value;
        mEmployee = employee;
        if (Employee.InfoType.DATE.equalsIgnoreCase(dataSchema.type)) {
            value = TimeUtil.getStringForMillis(Long.valueOf(value), TimeUtil.getTimeFormat1(BaseApplicationLike.baseContext));
        }

        mMyAccountKey.setText(key);
        mMyAccountValue.setText(value);
        canOps(dataSchema);
    }


    public void registerIconListener(final User user, final Activity activity) {

        mMyAccountIcon.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(user.mAvatar)) {
                Intent jumpIntent = UserAvatarPreviewActivity.getIntent(activity, user.mAvatar);
                activity.startActivity(jumpIntent);
            }

        });
    }

    private void setAvatar(User user, Activity activity) {
        mMyAccountIcon.setVisibility(VISIBLE);
        mMyAccountValue.setVisibility(GONE);
        ImageCacheHelper.displayImageByMediaId(user.mAvatar, mMyAccountIcon, ImageCacheHelper.getMyAccountAvatarOptions());
//        registerIconListener(user, activity);
    }

    private void setGenderIcon() {
        mMyAccountIcon.setVisibility(GONE);
        mMyAccountValue.setVisibility(VISIBLE);
        LayoutParams params = (LayoutParams) mMyAccountIcon.getLayoutParams();
        params.width = LayoutParams.WRAP_CONTENT;
        params.height = LayoutParams.WRAP_CONTENT;

        mMyAccountIcon.setLayoutParams(params);

        if ("male".equalsIgnoreCase(mEmployee.gender)) {
            mMyAccountValue.setText(R.string.male);

        } else if ("female".equalsIgnoreCase(mEmployee.gender)) {
            mMyAccountValue.setText(R.string.female);

        } else {
            mMyAccountValue.setText("");
        }
    }

    private void canOps(DataSchema dataSchema) {

        if (!Employee.InfoType.SELECT.equalsIgnoreCase(dataSchema.type) && dataSchema.mOpsable) {
            ViewUtil.setRightMargin(mMyAccountIcon, DensityUtil.dip2px(38));
            mMyAccountArrow.setVisibility(View.VISIBLE);
            setBackgroundResource(R.drawable.bg_item_common_selector);

        } else {
            ViewUtil.setRightMargin(mMyAccountIcon, DensityUtil.dip2px( 10));
            mMyAccountArrow.setVisibility(View.GONE);
            setBackgroundResource(R.color.white);

        }

        handleAction(dataSchema);
    }

    private void handleAction(DataSchema dataSchema) {
        if ("avatar".equalsIgnoreCase(dataSchema.mProperty)) {

            this.setOnClickListener(v -> {
                //使用对话框fragment
//                    AvatarPopupFragment pwDialog = new AvatarPopupFragment();
//                    pwDialog.setPhotographPathListener(mOnPhotoPathListener);
//                    pwDialog.show(mOnPhotoPathListener.getCurrentFragment().getChildFragmentManager(), "TEXT_POP_DIALOG");

            });

        } else if (!Employee.InfoType.SELECT.equalsIgnoreCase(dataSchema.type) && dataSchema.mOpsable) {

            this.setOnClickListener(v -> {
                Intent intent = ModifyMyInfoActivity.getIntent(mActivity, mDataSchema, mValue, mEmployee);
                mActivity.startActivity(intent);
                //  界面切换动画
                mActivity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            });
        }


    }

    public void setDividerVisible(boolean visible) {
        ViewUtil.setVisible(mVCommonDivider, visible);

    }

    public void addListener(AvatarPopupFragment.OnPhotographPathListener onPhotoPathListener) {
        this.mOnPhotoPathListener = onPhotoPathListener;
    }

}
