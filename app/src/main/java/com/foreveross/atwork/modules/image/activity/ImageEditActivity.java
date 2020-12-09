package com.foreveross.atwork.modules.image.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.infrastructure.model.file.MediaItem;
import com.foreveross.atwork.modules.image.fragment.ImageEditFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by dasunsy on 2016/11/9.
 */

public class ImageEditActivity extends SingleFragmentActivity {

    public static String DATA_IMAGE = "data_image";

    private ImageEditFragment mFragment = null;

    private MediaItem mImageItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mImageItem = (MediaItem) getIntent().getSerializableExtra(DATA_IMAGE);

        super.onCreate(savedInstanceState);
    }

    public static Intent getIntent(Context context, MediaItem imageItem) {
        Intent intent = new Intent();
        intent.setClass(context, ImageEditActivity.class);
        intent.putExtra(ImageEditActivity.DATA_IMAGE, imageItem);

        return intent;
    }


    @Override
    protected Fragment createFragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DATA_IMAGE, mImageItem);
        mFragment = new ImageEditFragment();
        mFragment.setArguments(bundle);

        return mFragment;
    }

    @Override
    public void changeStatusBar() {
        //do nothing
    }
}
