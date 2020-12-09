package com.foreveross.atwork.modules.aboutme.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.domain.UserSchemaSettingItem;
import com.foreveross.atwork.infrastructure.model.employee.DataSchema;
import com.foreveross.atwork.modules.aboutme.fragment.ModifyMyInfoFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * 修改电话号码页面
 * Created by ReyZhang on 2015/5/6.
 */
public class ModifyMyInfoActivity extends SingleFragmentActivity {

    private static final String TAG = ModifyMyInfoActivity.class.getSimpleName();

    public static final String INTENT_DATA_TYPE = "intent_data_type";

    public static final String INTENT_DATA_VALUE = "intent_data_value";

    public static final String INTENT_DATA_SCHEMA = "intent_data_schema";

    public static final String INTENT_DATA_EMPLOYEE = "intent_data_employee";

    public static final String INTENT_TYPE = "intent_type";

    public Bundle mBundle;

    public static Intent getIntent(Context context, UserSchemaSettingItem type, String value) {
        Intent intent = new Intent(context, ModifyMyInfoActivity.class);
        intent.putExtra(INTENT_TYPE, false);
        intent.putExtra(INTENT_DATA_TYPE, type);
        intent.putExtra(INTENT_DATA_VALUE, value);
        return intent;
    }

    public static Intent getIntent(Context context, DataSchema dataSchema, String value, Employee employee) {
        Intent intent = new Intent(context, ModifyMyInfoActivity.class);
        intent.putExtra(INTENT_TYPE, true);
        intent.putExtra(INTENT_DATA_SCHEMA, dataSchema);
        intent.putExtra(INTENT_DATA_VALUE, value);
        intent.putExtra(INTENT_DATA_EMPLOYEE, employee);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBundle = getIntent().getExtras();
        super.onCreate(savedInstanceState);

    }

    @Override
    protected Fragment createFragment() {
        ModifyMyInfoFragment fragment = new ModifyMyInfoFragment();
        fragment.setArguments(mBundle);
        return fragment;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //界面回退动画
        this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

}
