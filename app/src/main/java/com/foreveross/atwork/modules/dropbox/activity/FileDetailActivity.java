package com.foreveross.atwork.modules.dropbox.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.dropbox.DropboxConfig;
import com.foreveross.atwork.modules.dropbox.fragment.FileDetailFragment;
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
 * Created by reyzhang22 on 16/9/26.
 */

public class FileDetailActivity extends SingleFragmentActivity {

    public static final String INTENT_KEY_DROPBOX_FILE = "INTENT_KEY_DROPBOX_FILE";

    public static final String INTENT_KEY_DROPBOX_CONFIG = "INTENT_KEY_DROPBOX_CONFIG";

    FileDetailFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBundle = getIntent().getExtras();
        super.onCreate(savedInstanceState);

    }

    /**
     * @param context
     * @param dropbox
     * @return
     */
    public static Intent getIntent(Context context, Dropbox dropbox, DropboxConfig dropboxConfig) {
        Intent intent = new Intent(context, FileDetailActivity.class);
        intent.putExtra(INTENT_KEY_DROPBOX_FILE, dropbox);
        intent.putExtra(INTENT_KEY_DROPBOX_CONFIG, dropboxConfig);
        return intent;
    }

    private Bundle mBundle;

    @Override
    protected Fragment createFragment() {
        mFragment = new FileDetailFragment();
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != mFragment) {
            mFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

}
