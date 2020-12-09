package com.foreveross.atwork.modules.downLoad.component;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.model.user.LoginUserBasic;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.utils.FileHelper;
import com.foreveross.theme.manager.SkinMaster;

/**
 * Created by wuzejie on 2020/1/17.
 */

public class
DownLoadFileAttrDialog extends DialogFragment {

    public static final String BUNDLE_KEY_FILE_DATA = "BUNDLE_KEY_FILE_DATA";

    private FileData mFileData;

    private TextView mFileName;

    private TextView mFileSize;

    private TextView mLastModifyTime;

    private TextView mCreator;

    private TextView mDirPath;

    private TextView mClose;

    public final Bundle setData(FileData fileData) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_KEY_FILE_DATA, fileData);
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_MinWidth);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.component_download_file_profile, null);
        SkinMaster.getInstance().changeTheme((ViewGroup) view);
        initView(view);
        registerListener();
        setCancelable(true);
        initData();
        return view;
    }

    private void initView(View view) {
        mFileName = (TextView)view.findViewById(R.id.file_name);
        mCreator = (TextView)view.findViewById(R.id.creator);
        mFileSize = (TextView)view.findViewById(R.id.file_size);
        mLastModifyTime = (TextView)view.findViewById(R.id.last_modify_time);
        mDirPath = (TextView)view.findViewById(R.id.directory_path);
        mClose = (TextView)view.findViewById(R.id.close);
    }

    private void registerListener() {
        mClose.setOnClickListener(view -> {
            this.dismiss();
        });
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        mFileData = (FileData) bundle.getSerializable(BUNDLE_KEY_FILE_DATA);


        StringBuilder name = new StringBuilder(mFileData.title);

        mFileName.setText(name);
        mFileSize.setText(String.format(getString(R.string.file_size), FileHelper.getFileSizeStr(mFileData.size)));
        LoginUserBasic basic = LoginUserInfo.getInstance().getLoginUserBasic(BaseApplicationLike.baseContext);
        mCreator.setText(String.format(getString(R.string.creator), basic.mName));
        mLastModifyTime.setText(String.format(getString(R.string.last_modify_time), TimeUtil.getStringForMillis(mFileData.date, TimeUtil.getTimeFormat2(BaseApplicationLike.baseContext))));
        String path = mFileData.filePath;
        if (!TextUtils.isEmpty(path)) {
            path = path.substring(1, path.lastIndexOf("/"));
        }
        mDirPath.setText(String.format(getString(R.string.dir_path), path));
    }
}
