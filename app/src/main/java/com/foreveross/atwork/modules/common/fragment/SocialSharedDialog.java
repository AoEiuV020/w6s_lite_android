package com.foreveross.atwork.modules.common.fragment;/**
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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 */


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.share.ShareModel;
import com.tencent.tauth.Tencent;

/**
 * 社会化分享dialog
 * Created by reyzhang22 on 16/4/8.
 */
public class SocialSharedDialog extends DialogFragment {

    private static final String TAG = SocialSharedDialog.class.getSimpleName();

    private View mWXSessionShare;

    private View mWXFriendsShare;

    private View mQQShare;

    private View mQZoneShare;

    private Tencent mTencent;

    private ShareModel mModel;

    @Nullable
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog view = new Dialog(getActivity(), R.style.micro_video_style);
        view.requestWindowFeature(Window.FEATURE_NO_TITLE);
        view.setContentView(R.layout.view_social_share_dialog);
        view.setCanceledOnTouchOutside(true);
        Window window = view.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        mWXSessionShare = view.findViewById(R.id.share_wx_session);
        mWXFriendsShare = view.findViewById(R.id.share_wx_friends);
        mQQShare = view.findViewById(R.id.share_qq);
        mQZoneShare = view.findViewById(R.id.share_qzone);
        registerListener();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

    }

    public void setShareModel(ShareModel shareModel) {
        mModel = shareModel;
    }

    private void registerListener() {

//        mWXFriendsShare.setOnClickListener(v -> {
//            ExternalShareType share = ShareFactory.getInstance().factory(getActivity(), ExternalShareType.ShareType.WXTimeLine);
//            share.shareMessage(mModel);
//        });
//
//        mWXSessionShare.setOnClickListener(v -> {
//            ExternalShareType share = ShareFactory.getInstance().factory(getActivity(), ExternalShareType.ShareType.WXSession);
//            share.shareMessage(mModel);
//        });
//
//        mQQShare.setOnClickListener(v -> {
//            mTencent = Tencent.createInstance(AtworkConfig.QQ_APP_ID, BaseApplicationLike.baseContext);
//            ExternalShareType share = ShareFactory.getInstance().factory(getActivity(), ExternalShareType.ShareType.QQ);
//            share.shareMessage(mModel);
//        });
//
//        mQZoneShare.setOnClickListener(v -> {
//            mTencent = Tencent.createInstance(AtworkConfig.QQ_APP_ID, BaseApplicationLike.baseContext);
//            ExternalShareType share = ShareFactory.getInstance().factory(getActivity(), ExternalShareType.ShareType.QZONE);
//            share.shareMessage(mModel);
//        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != mTencent) {
            mTencent.onActivityResult(requestCode, resultCode, data);
        }

    }
}
