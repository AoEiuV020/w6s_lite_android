package com.foreveross.atwork.modules.dropbox.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.modules.dropbox.fragment.DropboxRWSettingFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
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
 * Created by reyzhang22 on 2016/10/18.
 */

public class DropboxRWSettingActivity extends SingleFragmentActivity {

    public static final String KEY_INTENT_SOURCE_ID = "KEY_INTENT_SOURCE_ID";
    public static final String KEY_INTENT_DOMAIN_ID = "KEY_INTENT_DOMAIN_ID";
    public static final String KEY_INTENT_READ_ONLY = "KEY_INTENT_READ_ONLY";
    public static final String KEY_INTENT_SOURCE_TYPE = "KEY_INTENT_SOURCE_TYPE";

    private Bundle mBundle;

    public static Intent getIntent(Context context, String sourceId, String domainId, Dropbox.SourceType sourceType,  boolean readOnly) {
        Intent intent = new Intent(context, DropboxRWSettingActivity.class);
        intent.putExtra(KEY_INTENT_SOURCE_ID, sourceId);
        intent.putExtra(KEY_INTENT_DOMAIN_ID, domainId);
        intent.putExtra(KEY_INTENT_READ_ONLY, readOnly);
        intent.putExtra(KEY_INTENT_SOURCE_TYPE, sourceType);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBundle = getIntent().getExtras();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        DropboxRWSettingFragment fragment = new DropboxRWSettingFragment();
        fragment.setArguments(mBundle);
        return fragment;
    }
}
