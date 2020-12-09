package com.foreveross.atwork.modules.common.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.ImageCacheHelper;

import java.io.File;

/**
 * Created by reyzhang22 on 15/11/12.
 */
public class PhotoPreviewFragment extends BackHandledFragment {

    private static final String TAG = PhotoPreviewFragment.class.getSimpleName();

    private Activity mActivity;

    private ImageView mPhoto;

    private Button mCancel;

    private Button mSubmit;

    private Bitmap mBitmap;

    private String mPath;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preview_photo, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerListener();
        initData();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    @Override
    protected void findViews(View view) {
        mPhoto = view.findViewById(R.id.photo_preview);
        mCancel = view.findViewById(R.id.cancel_photo);
        mSubmit = view.findViewById(R.id.submit_photo);
    }

    private void registerListener() {
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFile();
                mActivity.setResult(Activity.RESULT_CANCELED);
                mActivity.finish();
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mActivity.setResult(mActivity.RESULT_OK, new Intent().putExtra(PhotoPreviewActivity.PREVIEW_PHOTO_PATH_INTENT, mPath));
                mActivity.finish();
            }
        });
    }

    private void deleteFile() {
        File file = new File(mPath);
        file.delete();
        refreshGallery(file);
    }


    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
    }

    private void initData() {
//        mPath = getArguments().getString(PhotoPreviewActivity.PREVIEW_PHOTO_PATH_INTENT);
        if (TextUtils.isEmpty(mPath)) {
            mActivity.finish();
            return;
        }
        ImageCacheHelper.displayImage(mPath, mPhoto, ImageCacheHelper.getDefaultImageOptions(false, false, true));
    }


    @Override
    protected boolean onBackPressed() {
        mActivity.finish();
        return false;
    }

}
