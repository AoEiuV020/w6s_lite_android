package com.foreveross.atwork.modules.file.fragement;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.infrastructure.manager.FileAlbumService;
import com.foreveross.atwork.modules.file.activity.FileSelectActivity;
import com.foreveross.atwork.modules.file.adapter.LocalFileAdapter;
import com.foreveross.atwork.modules.file.inter.NavigateToFragmentListener;
import com.foreveross.atwork.modules.image.fragment.MediaSelectFragment;
import com.foreveross.atwork.support.BackHandledFragment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lingen on 15/3/30.
 * Description: 本地文件，包含本地图片，本地音乐，本地视频，已下载文件，手机内存，SD卡等内容
 */
public class LocalFileFragment extends BackHandledFragment {

    public static final String TAG = LocalFileFragment.class.getSimpleName();


    private static final int MSG_LOAD_FILES_COUNT_FINISH = 0x1001;

    //启动线程数
    private static final int THREAD_NUM = 4;

    private ListView mListView;
    private Activity mActivity;
    private NavigateToFragmentListener mNavigateToFragmentListener;
    private LocalFileAdapter mAdapter;
    private ExecutorService mExecutorService;
    private ProgressDialogHelper mProgressDialogHelper;
    //TODO: TIP注意这里的大小必须与adapter里面的的数组类型总数对应
    private int[] mFilesCounts = new int[6];
    private int mWhat;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mWhat = msg.what;
            switch (mWhat) {
                case MSG_LOAD_FILES_COUNT_FINISH:
                    if (mProgressDialogHelper != null) {
                        mProgressDialogHelper.dismiss();
                    }
                    if (mAdapter != null) {
                        mAdapter.setFileCounts(mFilesCounts);
                    }
                break;
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        mNavigateToFragmentListener = (NavigateToFragmentListener)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_file, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startExecutor();
    }


    @Override
    protected void findViews(View view) {
        mListView = view.findViewById(R.id.local_file_folder_listview);
        mAdapter = new LocalFileAdapter(mActivity);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mItemClickListener);
    }

    private void startExecutor() {

        mExecutorService = Executors.newFixedThreadPool(THREAD_NUM);
        mProgressDialogHelper = new ProgressDialogHelper(mActivity);
        mProgressDialogHelper.show();
        mExecutorService.execute(() -> {
            //第一个素组为图片文件总数
            mFilesCounts[0] = FileAlbumService.getInstance().queryImgCount();
            //第二个数组为音频文件总数
            mFilesCounts[1] = FileAlbumService.getInstance().getAllAudioSizes();
            //第三个数组为视频文件总数
            mFilesCounts[2] = FileAlbumService.getInstance().getAllVideoSizes();

            mHandler.obtainMessage(MSG_LOAD_FILES_COUNT_FINISH).sendToTarget();
        });
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            LocalFileAdapter.LocalFileType fileType = (LocalFileAdapter.LocalFileType)mAdapter.getItem(position);
            Bundle bundle = new Bundle();
            bundle.putString(FileSelectActivity.sTitle, mAdapter.getSelectItemName(position));

            //点击的是选择图片
            if (fileType == LocalFileAdapter.LocalFileType.Image) {
                MediaSelectFragment fragment = new MediaSelectFragment();
                mNavigateToFragmentListener.navigateToFragment(fragment, MediaSelectFragment.TAG);
                return;
            }

            //点击的是手机内存 或者是 SDCard内存
            if (fileType == LocalFileAdapter.LocalFileType.PhoneRam || fileType == LocalFileAdapter.LocalFileType.SDCard) {
                SDCardFilesFragment fragment = new SDCardFilesFragment();
                String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                if (fileType == LocalFileAdapter.LocalFileType.SDCard) {
                    sdcardPath = LocalFileAdapter.getExtSdcardPath(getActivity());
                }
                bundle.putString(SDCardFilesFragment.KEY_SDCARD_PATH, sdcardPath);
                fragment.setArguments(bundle);
                mNavigateToFragmentListener.navigateToFragment(fragment, SDCardFilesFragment.TAG);
                return;
            }

            FileTraversalFragment fragment = new FileTraversalFragment();
            fragment.setLocalFileType(fileType);
            fragment.setArguments(bundle);
            mNavigateToFragmentListener.navigateToFragment(fragment, FileTraversalFragment.TAG);
        }
    };

    @Override
    protected boolean onBackPressed() {
        getActivity().finish();
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
