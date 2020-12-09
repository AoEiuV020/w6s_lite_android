package com.foreveross.atwork.modules.search.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.modules.search.fragment.MinjieNewSearchFragment;
import com.foreveross.atwork.modules.search.fragment.NewSearchFragment;
import com.foreveross.atwork.modules.search.model.NewSearchControlAction;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by dasunsy on 2017/12/12.
 */

public class NewSearchActivity extends SingleFragmentActivity {

    public static final String DATA_NEW_SEARCH_CONTROL_ACTION = "DATA_NEW_SEARCH_CONTROL_ACTION";

    public static final String DATA_SEARCH_SELECT_RESULT = "DATA_SEARCH_SELECT_RESULT";


    public static Intent getIntent(Context context, NewSearchControlAction searchControlAction) {
        Intent intent = new Intent(context, NewSearchActivity.class);
        intent.putExtra(DATA_NEW_SEARCH_CONTROL_ACTION, searchControlAction);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        if(AtworkConfig.SEARCH_CONFIG.isMinjieSearch()) {
            return new MinjieNewSearchFragment();

        }

        return new NewSearchFragment();
    }
}
