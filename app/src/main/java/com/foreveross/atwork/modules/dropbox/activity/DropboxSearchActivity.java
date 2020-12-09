package com.foreveross.atwork.modules.dropbox.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.dropbox.DropboxConfig;
import com.foreveross.atwork.modules.dropbox.fragment.DropboxSearchFragment;
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
 * Created by reyzhang22 on 16/9/9.
 */
public class DropboxSearchActivity extends SingleFragmentActivity {

    public static final String KEY_INTENT_DOMAIN_ID = "KEY_INTENT_DOMAIN_ID";

    public static final String KEY_INTENT_SOURCE_TYPE = "KEY_INTENT_SOURCE_TYPE";

    public static final String KEY_INTENT_SOURCE_ID = "KEY_INTENT_SOURCE_ID";

    public static final String KEY_INTENT_DROPBOX_CONFIG = "KEY_INTENT_DROPBOX_CONFIG";

    public static final Intent getIntent(Context context, String domainId, Dropbox.SourceType sourceType, String sourceId, DropboxConfig dropboxConfig) {
        Intent intent = new Intent(context, DropboxSearchActivity.class);
        intent.putExtra(KEY_INTENT_DOMAIN_ID, domainId);
        intent.putExtra(KEY_INTENT_SOURCE_ID, sourceId);
        intent.putExtra(KEY_INTENT_SOURCE_TYPE, sourceType);
        intent.putExtra(KEY_INTENT_DROPBOX_CONFIG, dropboxConfig);
        return intent;
    }

    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBundle = getIntent().getExtras();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        DropboxSearchFragment fragment = new DropboxSearchFragment();
        fragment.setArguments(mBundle);
        return fragment;
    }
}
