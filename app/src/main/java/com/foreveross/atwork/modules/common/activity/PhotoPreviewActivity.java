package com.foreveross.atwork.modules.common.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.modules.common.fragment.PhotoPreviewFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by reyzhang22 on 15/11/12.
 */
public class PhotoPreviewActivity extends SingleFragmentActivity {

    private static final String PREVIEW_PHOTO_PATH_INTENT = "PREVIEW_PHOTO_PATH_INTENT";

    private String mPhoto;

    public static Intent getIntent(Context context, String filePath) {
        Intent intent = new Intent(context, PhotoPreviewActivity.class);
        intent.putExtra(PREVIEW_PHOTO_PATH_INTENT, filePath);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPhoto = getIntent().getStringExtra(PREVIEW_PHOTO_PATH_INTENT);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        PhotoPreviewFragment photoPreviewFragment = new PhotoPreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PREVIEW_PHOTO_PATH_INTENT, mPhoto);
        photoPreviewFragment.setArguments(bundle);
        return photoPreviewFragment;
    }

}
