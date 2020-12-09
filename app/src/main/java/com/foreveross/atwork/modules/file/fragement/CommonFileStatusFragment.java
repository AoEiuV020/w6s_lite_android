package com.foreveross.atwork.modules.file.fragement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.BasicDialogFragment;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.file.FileStatusInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.bing.listener.UpdateFileDataListener;
import com.foreveross.atwork.support.AtworkBaseActivity;
import com.foreveross.theme.manager.SkinMaster;


public class CommonFileStatusFragment extends BasicDialogFragment {

    public static String ACTION_REFRESH_UI = "ACTION_REFRESH_UI";
    public static String FILE_ITEM = "FILE_ITEM";
    public static String SESSION_ID = "SESSION_ID";

    private CommonFileStatusView mFileStatusView;

    private UpdateFileDataListener mUpdateFileDataListener;

    private OnFileViewLifycycle mListener;

    public void setOnFileViewLifecycle(OnFileViewLifycycle listener) {
        mListener = listener;
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(ACTION_REFRESH_UI.equalsIgnoreCase(action)) {
                mFileStatusView.refreshView();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar);
        registerBroadcast();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mFileStatusView = new CommonFileStatusView(getActivity());
        mFileStatusView.findViewById(R.id.title_bar_chat_detail_back).setOnClickListener(v -> dismiss());
        mFileStatusView.setUpdateFileDataListener(mUpdateFileDataListener);

        SkinMaster.getInstance().changeTheme(mFileStatusView);

        return mFileStatusView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFileStatusView != null) {
            mFileStatusView.onResume();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            FileStatusInfo fileStatusInfo = getArguments().getParcelable(FILE_ITEM);
            String sessionId = getArguments().getString(SESSION_ID, StringUtils.EMPTY);
            mFileStatusView.setFileStatusInfo(sessionId, fileStatusInfo);
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(AtworkConfig.SCREEN_ORIENTATION_USER_SENSOR) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if (mListener != null) {
            mListener.onViewFinish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
    }

    public void initBundle(@Nullable String sessionId, FileStatusInfo fileStatusInfo) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(FILE_ITEM, fileStatusInfo);
        bundle.putString(SESSION_ID, sessionId);
        setArguments(bundle);
    }

    public void setUpdateFileDataListener(UpdateFileDataListener updateFileDataListener) {
        mUpdateFileDataListener = updateFileDataListener;
    }

    public static void refreshUI() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(ACTION_REFRESH_UI));
    }

    private void registerBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_REFRESH_UI);

        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void unregisterBroadcast() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mBroadcastReceiver);
    }

    public static void showOverall(FileStatusInfo fileStatusInfo) {
        Intent intent = new Intent(AtworkBaseActivity.ACTION_SHOW_COMMON_FILE_STATUS_VIEW);
        intent.putExtra(AtworkBaseActivity.DATA_FILE_STATUS_INFO, fileStatusInfo);

        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent);
    }

    public interface OnFileViewLifycycle {
        void onViewFinish();
    }

}
