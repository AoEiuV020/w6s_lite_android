package com.foreveross.atwork.modules.contact.fragment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.modules.contact.activity.UserAvatarPreviewActivity;
import com.foreveross.atwork.modules.image.component.ItemEnlargeImageView;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.ImageCacheHelper;

/**
 * Created by reyzhang22 on 15/6/11.
 */
public class UserAvatarPreviewFragment extends BackHandledFragment {


    private static final String TAG = UserAvatarPreviewFragment.class.getSimpleName();

    private ItemEnlargeImageView mImageAvatar;

    private String mAvatarId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_avatar_preview, null);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @Override
    public void onStart() {
        super.onStart();

        // 设置图片
        ImageCacheHelper.loadImageByMediaId(mAvatarId, new ImageCacheHelper.ImageLoadedListener() {

            @Override
            public void onImageLoadedComplete(Bitmap bitmap) {
                mImageAvatar.setImageBitmap(bitmap);
            }

            @Override
            public void onImageLoadedFail() {
                if (isAdded()) {
                    mImageAvatar.setImageBitmap(BitmapFactory.decodeResource(mActivity.getResources(), R.mipmap.default_photo));
                }

            }
        });
    }

    @Override
    protected void findViews(View view) {
        mImageAvatar = view.findViewById(R.id.image_preview_viewpager);

    }


    private void initView(View view) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mAvatarId = bundle.getString(UserAvatarPreviewActivity.BUNDLE_AVATAR_ID);
        }

        mImageAvatar.setScaleGesture();
        mImageAvatar.setOnTagListener(() -> {
            onBackPressed();
            return true;
        });
    }




    @Override
    protected boolean onBackPressed() {
        getActivity().finish();
        return false;
    }


}
