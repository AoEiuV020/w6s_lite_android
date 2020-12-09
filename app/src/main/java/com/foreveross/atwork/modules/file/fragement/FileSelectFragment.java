package com.foreveross.atwork.modules.file.fragement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.modules.dropbox.activity.DropboxBaseActivity;
import com.foreveross.atwork.modules.dropbox.activity.SaveToDropboxActivity;
import com.foreveross.atwork.modules.file.activity.FileSelectActivity;
import com.foreveross.atwork.modules.file.adapter.FileArrayListViewAdapter;
import com.foreveross.atwork.modules.file.component.FileItemLinearLayoutView;
import com.foreveross.atwork.modules.file.dao.RecentFileDaoService;
import com.foreveross.atwork.modules.file.inter.NavigateToFragmentListener;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.foreveross.atwork.modules.dropbox.activity.SaveToDropboxActivity.KEY_INTENT_SELECT_DROPBOX_SEND_EMAIL;
import static com.foreveross.atwork.modules.file.activity.FileSelectActivity.RESULT_INTENT;

/**
 * Created by lingen on 15/3/30.
 * Description:
 */
public class FileSelectFragment extends BasicFileSelectFragment implements AdapterView.OnItemClickListener {

    public static final String TAG = FileSelectFragment.class.getSimpleName();

    public static final int MSG_GET_RECENT_FILES_SUCCESS = 0x1090;
    public static final int MSG_GET_RECENT_FILES_FAIL = 0x1091;

    public static final int MSG_SEND_DROPBOX_EMAIL = 123;

    private RelativeLayout mSelectLocalFile;
    private NavigateToFragmentListener mNavigateToFragmentListener;
    private ListView mRecentFileListView;
    private FileArrayListViewAdapter mAdapter;
    private List<FileData> mSelectedFileList;

    private List<FileData> mFileList;

    private TextView currentFileTextView;
    private View uplineView;
    private View downlineView;
    private int mWhat;

    private View mDropboxSelectLayout;

    private boolean mShowDropboxItem;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mWhat = msg.what;
            switch (mWhat) {
                case MSG_GET_RECENT_FILES_FAIL:

                    break;

                case MSG_GET_RECENT_FILES_SUCCESS:
                    mFileList = (List<FileData>) msg.obj;
                    //判断最近文件列表是否为空 如果不为空则显示“最近文件”控件
                    if (!(mFileList.isEmpty())) {
                        currentFileTextView.setVisibility(View.VISIBLE);
                        uplineView.setVisibility(View.VISIBLE);
                        downlineView.setVisibility(View.VISIBLE);
                    }
                    mAdapter = new FileArrayListViewAdapter(mActivity, mFileList, mSelectedFileList, isSingleSelectType());
                    mRecentFileListView.setAdapter(mAdapter);
                    break;
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mNavigateToFragmentListener = ((FileSelectActivity) activity);
        mSelectedFileList = ((FileSelectActivity) activity).mSelectedFileData;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_file, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupData();
    }

    @Override
    protected void findViews(View view) {
        mSelectLocalFile = view.findViewById(R.id.select_local_file_layout);
        mSelectLocalFile.setOnClickListener(mOnClickListener);
        mRecentFileListView = view.findViewById(R.id.recent_files_listview);
        mRecentFileListView.setOnItemClickListener(this);
        currentFileTextView = view.findViewById(R.id.current_file_tv);
        uplineView = view.findViewById(R.id.lineview_up);
        downlineView = view.findViewById(R.id.v_common_divider);
        //默认设置“最近文件”控件为隐藏
        currentFileTextView.setVisibility(View.GONE);
        uplineView.setVisibility(View.GONE);
        downlineView.setVisibility(View.GONE);

        mDropboxSelectLayout = view.findViewById(R.id.select_dropbox_file_layout);
        mDropboxSelectLayout.setOnClickListener(mOnClickListener);
    }

    private void setupData() {
        //查询最近文件数据库
        RecentFileDaoService.getInstance().getRecentFiles(mHandler);
        mShowDropboxItem = getArguments().getBoolean(FileSelectActivity.SHOW_DROPBOX_ITEM, false);
        mDropboxSelectLayout.setVisibility(mShowDropboxItem && AtworkConfig.OPEN_DROPBOX ? View.VISIBLE : View.GONE);
    }

    //点击事件汇总
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.select_local_file_layout:
                    LocalFileFragment localFileFragment = new LocalFileFragment();
                    mNavigateToFragmentListener.navigateToFragment(localFileFragment, LocalFileFragment.TAG);
                    break;

                case R.id.select_dropbox_file_layout:
                    Dropbox dropbox = new Dropbox();
                    dropbox.mDomainId = AtworkConfig.DOMAIN_ID;
                    dropbox.mSourceId = LoginUserInfo.getInstance().getLoginUserId(mActivity);
                    dropbox.mSourceType = Dropbox.SourceType.User;
                    Intent intent = SaveToDropboxActivity.getIntent(mActivity, dropbox, DropboxBaseActivity.DisplayMode.Send, true);
                    startActivityForResult(intent, MSG_SEND_DROPBOX_EMAIL);
                    break;
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FileData fileData = mFileList.get(position);
        if (fileData == null) {
            return;
        }
        if (!fileData.isSelect) {

            if (checkFileSelected(fileData)) {
                return;
            }

        }
        fileData.isSelect = !fileData.isSelect;
        ((FileItemLinearLayoutView) view).setChecked(fileData.isSelect);
        refreshFileData(fileData);
    }

    @Override
    protected void refreshFileData(FileData fileData) {
        List<FileData> fileDataRemovedList = new ArrayList<>();

        for (FileData data : mSelectedFileList) {
            if (data == null) {
                continue;
            }
            if (data.equals(fileData)) {
                fileDataRemovedList.add(data);
            }


        }
        if (!ListUtil.isEmpty(fileDataRemovedList)) {
            mSelectedFileList.removeAll(fileDataRemovedList);
            ((FileSelectActivity) mActivity).onSelectFileSizeUpdate();
            return;
        }

        mSelectedFileList.add(fileData);
        ((FileSelectActivity) mActivity).onSelectFileSizeUpdate();
    }

    @Override
    protected List<FileData> getFileSelectedList() {
        return mSelectedFileList;
    }

    @Override
    protected boolean onBackPressed() {
        finish();
        return false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(mWhat);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == MSG_SEND_DROPBOX_EMAIL) {
            if (data == null) {
                return;
            }
            ArrayList<FileData> fileDatas = (ArrayList<FileData>) data.getSerializableExtra(KEY_INTENT_SELECT_DROPBOX_SEND_EMAIL);
            Intent result = new Intent();
            result.putExtra(RESULT_INTENT, fileDatas);
            mActivity.setResult(RESULT_OK, result);
            finish();
            //界面回退动画
            mActivity.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            return;
        }
    }


}
