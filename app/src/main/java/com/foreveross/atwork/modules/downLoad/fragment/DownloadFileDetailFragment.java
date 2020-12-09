package com.foreveross.atwork.modules.downLoad.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.BasicDialogFragment;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.modules.downLoad.component.DownloadFileDetailView;
import com.foreveross.theme.manager.SkinMaster;

/**
 * Created by wuzejie on 20/1/13.
 * Description:我的下载的文件详情
 */
public class DownloadFileDetailFragment extends BasicDialogFragment {

    public static String FILE_DATA = "FILE_DATA";
    private DownloadFileDetailView mFileStatusView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mFileStatusView = new DownloadFileDetailView(getActivity(), (FileData)getArguments().getSerializable(FILE_DATA));
        mFileStatusView.findViewById(R.id.title_bar_chat_detail_back).setOnClickListener(v -> dismiss());
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
    public void onDestroy() {
        super.onDestroy();
        mFileStatusView.destroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            FileData fileData = (FileData)getArguments().getSerializable(FILE_DATA);
            mFileStatusView.setFileData(fileData);
            //mFileStatusView.previewLocal();
        }
    }
    public void initBundle(FileData fileData){
        Bundle bundle = new Bundle();
        bundle.putSerializable(FILE_DATA, fileData);
        setArguments(bundle);
    }

}
