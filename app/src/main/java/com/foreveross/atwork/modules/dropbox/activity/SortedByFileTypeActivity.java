package com.foreveross.atwork.modules.dropbox.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.modules.dropbox.fragment.SortedByTypeFragment;
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

public class SortedByFileTypeActivity extends SingleFragmentActivity {

    public static final String INTENT_KEY_SORTED_TYPE = "INTENT_KEY_SORTED_TYPE";
    public static final String INTENT_KEY_SOURCE_TYPE = "INTENT_KEY_SOURCE_TYPE";
    public static final String INTENT_KEY_SOURCE_ID = "INTENT_KEY_SOURCE_ID";
    public static final String INTENT_KEY_DOMAIN_ID = "INTENT_KEY_DOMAIN_ID";
    public static final String INTENT_KEY_PARENT_ID = "INTENT_KEY_PARENT_ID";
    public static final String INTENT_KEY_DROPBOX_CONFIG = "INTENT_KEY_DROPBOX_CONFIG";

    /**
     *
     * @param context
     * @param fileType
     * @param sourceType
     * @param sourceId
     * @param domainId
     * @return
     */
    public static Intent getIntent(Context context, Dropbox.DropboxFileType fileType, Dropbox.SourceType sourceType,
                                   String sourceId, String domainId, String parentId) {
        Intent intent = new Intent(context, SortedByFileTypeActivity.class);
        intent.putExtra(INTENT_KEY_SORTED_TYPE, fileType);
        intent.putExtra(INTENT_KEY_SOURCE_TYPE, sourceType);
        intent.putExtra(INTENT_KEY_SOURCE_ID, sourceId);
        intent.putExtra(INTENT_KEY_DOMAIN_ID, domainId);
        intent.putExtra(INTENT_KEY_PARENT_ID, parentId);
        return intent;
    }

    private Bundle mBundle;

    private SortedByTypeFragment mFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBundle = getIntent().getExtras();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        mFragment = new SortedByTypeFragment();
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFragment.onActivityResult(requestCode, resultCode, data);
    }
}
