package com.foreveross.atwork.modules.voip.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.voip.MeetingInfo;
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil;
import com.foreveross.atwork.modules.voip.fragment.VoipSelectModeFragment;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

import java.util.ArrayList;

/**
 * Created by dasunsy on 16/7/11.
 */
public class VoipSelectModeActivity extends SingleFragmentActivity {

    public final static String DATA_USER_SELECTED = "DATA_USER_SELECTED";
    public final static String DATA_SESSION_INFO = "DATA_SESSION_INFO";
    public final static String DATA_DISCUSSION_ORG_CODE = "DATA_DISCUSSION_ORG_CODE";

    private BackHandledFragment mFragment;

    public static Intent getIntent(final Context context, MeetingInfo meetingInfo, ArrayList<? extends ShowListItem> contactSelectedList, String discussionOrgCode) {
        Intent intent = new Intent();
        intent.setClass(context, VoipSelectModeActivity.class);
        intent.putParcelableArrayListExtra(DATA_USER_SELECTED, contactSelectedList);
        intent.putExtra(DATA_SESSION_INFO, meetingInfo);
        intent.putExtra(DATA_DISCUSSION_ORG_CODE, discussionOrgCode);

        return intent;
    }

    public static Intent getIntent(final Context context, ArrayList<? extends ShowListItem> contactSelectedList) {
        return getIntent(context, null, contactSelectedList, null);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        mFragment = new VoipSelectModeFragment();
        return mFragment;
    }

    @Override
    public void changeStatusBar() {
        StatusBarUtil.setColorNoTranslucent(this, ContextCompat.getColor(this, R.color.voip_bg_dark_blue));
    }
}
