package com.foreveross.atwork.modules.dropbox.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.modules.dropbox.fragment.DropboxModifyFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

import java.io.Serializable;

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
 * Created by reyzhang22 on 16/9/13.
 */
public class DropboxModifyActivity extends SingleFragmentActivity {

    public static final String KEY_INTENT_MODIFY_ACTION = "KEY_INTENT_MODIFY_ACTION";
    public static final String KEY_INTENT_DROPBOX = "KEY_INTENT_DROPBOX";
    public static final String KEY_INTENT_SOURCE_ID = "KEY_INTENT_SOURCE_ID";
    public static final String KEY_INTENT_SOURCE_TYPE = "KEY_INTENT_SOURCE_TYPE";
    public static final String KEY_INTENT_DOMAI_ID = "KEY_INTENT_DOMAI_ID";
    public static final String KEY_INTENT_PARENT_ID = "KEY_INTENT_PARENT_ID";

    public static Intent getIntent(Context context, ModifyAction action, Dropbox dropbox, String sourceId, String domainId, Dropbox.SourceType sourceType, String parentId) {
        Intent intent = new Intent(context, DropboxModifyActivity.class);
        intent.putExtra(KEY_INTENT_MODIFY_ACTION, action);
        intent.putExtra(KEY_INTENT_DROPBOX, dropbox);
        intent.putExtra(KEY_INTENT_SOURCE_ID, sourceId);
        intent.putExtra(KEY_INTENT_SOURCE_TYPE, sourceType);
        intent.putExtra(KEY_INTENT_DOMAI_ID, domainId);
        intent.putExtra(KEY_INTENT_PARENT_ID, parentId);
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
        DropboxModifyFragment fragment = new DropboxModifyFragment();
        fragment.setArguments(mBundle);
        return fragment;
    }

    public enum ModifyAction implements Serializable {
        CreateFolder,
        Rename
    }
}
