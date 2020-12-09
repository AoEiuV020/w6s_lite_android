package com.foreveross.atwork.modules.aboutme.fragment;


import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.modules.aboutme.listener.OnPhotoSelectListener;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.IntentUtil;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

/**
 * 点击个人头像弹出的Dialogfragment
 */
public class AvatarPopupFragment extends DialogFragment implements View.OnClickListener, OnPhotoSelectListener {

    public static final int REQUEST_CODE_CHOSE_PHOTO = 0x321;
    private OnPhotoSelectListener mListener = this;
    private TextView mChoicePhoto;
    private TextView mPhotograph;
    private TextView mCancel;
    private OnPhotographPathListener mPhotographListener;

    private String mPhotoPath;

    public AvatarPopupFragment() {

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.component_change_avatar_popup, container, false);
        mChoicePhoto = layout.findViewById(R.id.select_from_photo_libs);
        mPhotograph = layout.findViewById(R.id.select_from_camera);
        mCancel = layout.findViewById(R.id.cancel);
        mChoicePhoto.setOnClickListener(this);
        mPhotograph.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //设置fragment style
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Light);
        //设置fragment背景颜色
        getDialog().getWindow().setBackgroundDrawableResource(R.color.white);
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        //设置fragment大小
        getDialog().getWindow().setLayout(DensityUtil.DP_1O_TO_PX * 30, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void setPhotographPathListener(OnPhotographPathListener listener) {
        mPhotographListener = listener;
    }


    @Override
    public void onClick(View v) {
        dismiss();
        int id = v.getId();
        switch (id) {
            case R.id.select_from_photo_libs:
                mListener.onPhotoSelect(MyAccountFragment.REQUEST_CODE_CHOSE_PHOTO);
                break;

            case R.id.select_from_camera:
                if(VoipHelper.isHandingVideoVoipCall()) {
                    AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);
                    return;
                }

                mListener.onPhotoSelect(MyAccountFragment.REQUEST_CODE_CAMERA);
                break;

        }

    }

    @Override
    public void onPhotoSelect(int message) {
        if (message == REQUEST_CODE_CHOSE_PHOTO) {
            AndPermission
                    .with(mPhotographListener.getCurrentFragment())
                    .runtime()
                    .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                    .onGranted(data -> {
                        IntentUtil.getPhotoFromLibrary(mPhotographListener.getCurrentFragment(), message);
                    })
                    .onDenied(data ->  AtworkUtil.popAuthSettingAlert(mPhotographListener.getCurrentFragment().getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    .start();

            return;
        }
        AndPermission
                .with(mPhotographListener.getCurrentFragment())
                .runtime()
                .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                .onGranted(data -> {
                    AndPermission
                            .with(mPhotographListener.getCurrentFragment())
                            .runtime()
                            .permission(Permission.CAMERA)
                            .onGranted(granted -> {
                                mPhotoPath = IntentUtil.camera(mPhotographListener.getCurrentFragment(), MyAccountFragment.REQUEST_CODE_CAMERA);
                                mPhotographListener.onPhotographPathListen(mPhotoPath);
                            })
                            .onDenied(denied ->  AtworkUtil.popAuthSettingAlert(mPhotographListener.getCurrentFragment().getContext(), Manifest.permission.CAMERA))
                            .start();
                })
                .onDenied(data ->  AtworkUtil.popAuthSettingAlert(mPhotographListener.getCurrentFragment().getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .start();

    }

    public interface OnPhotographPathListener {
        void onPhotographPathListen(String path);

        Fragment getCurrentFragment();
    }

}
