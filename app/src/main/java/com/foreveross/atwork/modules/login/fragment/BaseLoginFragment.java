package com.foreveross.atwork.modules.login.fragment;

import android.content.Intent;
import android.os.Handler;
import android.widget.ImageView;

import com.foreverht.workplus.component.DeviceInfoDialog;
import com.foreveross.atwork.infrastructure.utils.CommonUtil;
import com.foreveross.atwork.modules.chat.activity.SyncMessagesSettingActivity;
import com.foreveross.atwork.support.BackHandledFragment;

public abstract class BaseLoginFragment extends BackHandledFragment {

    abstract ImageView getAvatarIv();

    private static int mAvatarClickCounter = 0;

    private Handler handler = null;

    void registerAvatarClickListener() {
        getAvatarIv().setOnClickListener(v -> {
            if (handler == null) {
                handler = new Handler();
                handler.postDelayed(() -> {
                    mAvatarClickCounter = 0;
                    handler =null;
                }, 2000);
            }
            if (CommonUtil.isFastClick( 2000)) {
                if (mAvatarClickCounter > 1) {
                    showDeviceInfo();
                    mAvatarClickCounter = 0;
                    return;
                }
                ++mAvatarClickCounter;
            } else {
                if (mAvatarClickCounter == 0) {
                    ++mAvatarClickCounter;
                } else {
                    mAvatarClickCounter = 0;
                }

            }
        });
    }

    void showDeviceInfo() {
        DeviceInfoDialog dialog = new DeviceInfoDialog();
        dialog.show(getFragmentManager(), "DEVICE_DIALOG");
    }

    protected void openSyncMessageSetting() {
        if (null != mActivity) {
            Intent intent = SyncMessagesSettingActivity.Companion.getIntent(mActivity);
            startActivity(intent);
        }
    }

}
