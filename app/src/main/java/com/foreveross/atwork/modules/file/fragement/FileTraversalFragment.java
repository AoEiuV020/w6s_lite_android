package com.foreveross.atwork.modules.file.fragement;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.manager.FileAlbumService;
import com.foreveross.atwork.infrastructure.model.file.AudioItem;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.model.file.VideoItem;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.modules.file.activity.FileSelectActivity;
import com.foreveross.atwork.modules.file.adapter.FileArrayListViewAdapter;
import com.foreveross.atwork.modules.file.adapter.LocalFileAdapter;
import com.foreveross.atwork.modules.file.component.FileItemLinearLayoutView;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.ChatMessageHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by lingen on 15/3/30.
 * Description: 文件遍历
 */
public class FileTraversalFragment extends BasicFileSelectFragment {

    public static final String TAG = FileTraversalFragment.class.getSimpleName();

    private static final int MSG_LOAD_FILES_COMPLETED = 0x0012;

    public static final String FILE_PATH = "file_path";

    private ListView mFileListView;

    private Activity mActivity;

    private List<FileData> mSelectedFileList;

    private List<FileData> mFileList = new ArrayList<>();

    private FileArrayListViewAdapter mAdapter;

    private ProgressDialogHelper mLoadingHelper;

    private ExecutorService mExecutorService;

    private LocalFileAdapter.LocalFileType mLocalFileType;

    private int mWhat;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mWhat = msg.what;
            switch (mWhat) {
                case MSG_LOAD_FILES_COMPLETED:
                    if (mLoadingHelper != null) {
                        mLoadingHelper.dismiss();
                    }
                    mAdapter.setFileDataList(mFileList);

                break;

                case FileSelectFragment.MSG_GET_RECENT_FILES_SUCCESS:
                    mFileList = (List<FileData>)msg.obj;
                    mAdapter.setFileDataList(mFileList);
                    break;
            }
        }
    };

    public void setLocalFileType(LocalFileAdapter.LocalFileType localFileType) {
        if (localFileType == null) {
            throw new IllegalArgumentException("invalid argument on setlocakFileType()");
        }
        mLocalFileType = localFileType;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        mSelectedFileList = ((FileSelectActivity)activity).mSelectedFileData;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_traversal, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        refresh();
    }

    @Override
    public void onStart() {
        super.onStart();

    }



    @Override
    protected void findViews(View view) {
        mFileListView = getView().findViewById(R.id.file_list_view);
        mAdapter = new FileArrayListViewAdapter(mActivity, mFileList, mSelectedFileList, isSingleSelectType());
        mFileListView.setAdapter(mAdapter);
        mFileListView.setOnItemClickListener(mItemClickListener);
    }

    private void initData() {

        mExecutorService = Executors.newSingleThreadExecutor();
    }

    private void refresh() {
        mLoadingHelper = new ProgressDialogHelper(mActivity);
        mLoadingHelper.show();
        mExecutorService.execute(() -> {
            if (mLocalFileType == null) {
                return;
            }
            switch (mLocalFileType) {
                case Audio:
                    handleAudioItems();
                break;

                case Video:
                    handleVideoItems();
                break;

            }
            mHandler.obtainMessage(MSG_LOAD_FILES_COMPLETED).sendToTarget();
        });
    }

    /**
     * 处理音频文件
     */
    private void handleAudioItems() {
        List<AudioItem> audioItems = FileAlbumService.getInstance().getAudioItemList();

        for (AudioItem audio : audioItems) {
            if (audio == null) {
                continue;
            }
            FileData fileData = new FileData();
            fileData.fileType = FileData.FileType.File_Audio;
            fileData.filePath = audio.path;
            fileData.title = audio.title;
            fileData.size = audio.size;
            mFileList.add(fileData);
        }
    }

    /**
     * 处理视频文件
     */
    private void handleVideoItems() {
        List<VideoItem> videoItem = FileAlbumService.getInstance().getAllVideoItemList();

        for (VideoItem video : videoItem) {
            if (video == null) {
                continue;
            }
            FileData fileData = new FileData();
            fileData.fileType = FileData.FileType.File_Video;
            fileData.filePath = video.filePath;
            fileData.title = video.title;
            fileData.size = video.size;
            mFileList.add(fileData);
        }
    }



    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            FileData fileData = mFileList.get(position);
            if (fileData == null) {
                return;
            }
            if (!fileData.isSelect) {
                if(checkFileSelected(fileData)) {
                    return;
                }
            }
            if (fileData.size > DomainSettingsManager.getInstance().getMaxMobileChatFileUploadSize() && !((mActivity instanceof FileSelectActivity) && ((FileSelectActivity)mActivity).mIgnoreSize)) {
                AtworkToast.showToast(AtworkApplicationLike.getResourceString(R.string.send_file_limit_tip, ChatMessageHelper.getMBOrKBString(DomainSettingsManager.getInstance().getMaxMobileChatFileUploadSize())));
                return;
            }
            fileData.isSelect = !fileData.isSelect;
            ((FileItemLinearLayoutView)view).setChecked(fileData.isSelect);
            refreshFileData(fileData);
        }

    };


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
            ((FileSelectActivity)mActivity).onSelectFileSizeUpdate();
            return;
        }
        mSelectedFileList.add(fileData);
        ((FileSelectActivity)mActivity).onSelectFileSizeUpdate();
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


}
