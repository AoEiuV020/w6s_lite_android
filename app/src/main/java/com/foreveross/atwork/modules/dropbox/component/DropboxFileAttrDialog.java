package com.foreveross.atwork.modules.dropbox.component;

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
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.manager.DropboxManager;
import com.foreveross.theme.manager.SkinMaster;

/**
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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 * Created by reyzhang22 on 2016/10/8.
 */

public class
DropboxFileAttrDialog extends DialogFragment {

    public static final String BUNDLE_KEY_DROPBOX = "BUNDLE_KEY_DROPBOX";

    private Dropbox mDropbox;

    private TextView mFileName;

    private TextView mFileSize;

    private TextView mLastModifyTime;

    private TextView mCreator;

    private TextView mDirPath;

    private TextView mClose;

    public final Bundle setData(Dropbox dropbox) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_KEY_DROPBOX, dropbox);
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
        View view = inflater.inflate(R.layout.component_dropbox_file_profile, null);
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
        mDropbox = bundle.getParcelable(BUNDLE_KEY_DROPBOX);


        StringBuilder name = new StringBuilder(mDropbox.mFileName);
        if (!TextUtils.isEmpty(mDropbox.mExtension)) {
            if (!mDropbox.mExtension.startsWith(".")) {
                name.append(".");
            }
            name.append(mDropbox.mExtension);
        }
        mFileName.setText(name);
        mFileSize.setText(String.format(getString(R.string.file_size), FileUtil.formatFromSize(mDropbox.mFileSize)));
        mCreator.setText(String.format(getString(R.string.creator), mDropbox.mOwnerName));
        mLastModifyTime.setText(String.format(getString(R.string.last_modify_time), TimeUtil.getStringForMillis(mDropbox.mLastModifyTime, TimeUtil.getTimeFormat2(BaseApplicationLike.baseContext))));
        String path = DropboxManager.getInstance().getDropboxAbsPath(getContext(), mDropbox);
        if (!TextUtils.isEmpty(path)) {
            path = path.substring(1, path.lastIndexOf("/"));
        }
        mDirPath.setText(String.format(getString(R.string.dir_path), path));
    }
}
