package com.foreveross.atwork.modules.contact.activity;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.modules.contact.fragment.ContactFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by dasunsy on 2018/3/30.
 */

public class ContactActivity extends SingleFragmentActivity {

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, ContactActivity.class);
        intent.putExtra(ContactFragment.SHOW_COMMON_TITLE_BAR, true);
        intent.putExtra(ContactFragment.SHOW_MAIN_TITLE_BAR, false);

        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return new ContactFragment();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
