package com.foreveross.atwork.modules.file.fragement;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.model.file.SDCardFileData;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.modules.file.activity.FileSelectActivity;
import com.foreveross.atwork.modules.file.adapter.SDCardFileAdapter;
import com.foreveross.atwork.modules.file.component.SDCardFileItem;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.ChatMessageHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by ReyZhang on 2015/4/22.
 */
public class SDCardFilesFragment extends BasicFileSelectFragment {

    public static final String TAG = SDCardFilesFragment.class.getSimpleName();

    public static final String KEY_SDCARD_PATH = "SDCARD_PATH";
    private final static String PRE_CUR_PATH = "currentPath";

    private Activity mActivity;
    private String mSDCardPath;
    private String mTitle;
    //当前sdcard目录中的目录数据
    private SDCardFileData mCurrentData;
    //表示打开sdcard目录到第几级
    private int mHierarchyLve = 0;
    //记录层级以及sdcard数据的映射关系，用于用户回退
    private Map<Integer, SDCardFileData> mFileDataMap = new HashMap<>();

    private TextView mDirPath;
    private ListView mListView;
    //表示该sdcard下目录为空
    private TextView mEmptyTip;

    private SDCardFileAdapter mSDCardFileAdapter;

    private List<FileData> mSelectedFileData;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        mSelectedFileData = ((FileSelectActivity)activity).mSelectedFileData;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sdcard_file, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupData();
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        if (mCurrentData == null || savedInstanceState == null) {
            super.onViewStateRestored(savedInstanceState);
            return;
        }
        mCurrentData.path = savedInstanceState.getString(PRE_CUR_PATH);
        if (mCurrentData.path == null)
            mCurrentData.path = mSDCardPath;
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mCurrentData == null) {
            super.onSaveInstanceState(outState);
            return;
        }
        outState.putString(PRE_CUR_PATH, mCurrentData.path);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void findViews(View view) {
        mDirPath = view.findViewById(R.id.directory_path);
        mListView = view.findViewById(R.id.sdcard_file_list_view);
        mEmptyTip = view.findViewById(R.id.empty);
    }

    private void setupData() {
        mTitle = getArguments().getString(FileSelectActivity.sTitle);
        mSDCardPath = getArguments().getString(KEY_SDCARD_PATH);


        if (mCurrentData == null) {
            initCurrentData(null);
        }
        mSDCardFileAdapter = new SDCardFileAdapter(mActivity, mCurrentData, mSelectedFileData, isSingleSelectType());
        mListView.setAdapter(mSDCardFileAdapter);
        mListView.setOnItemClickListener(mItemClickListener);


    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SDCardFileData.FileInfo fileInfo = (SDCardFileData.FileInfo) mSDCardFileAdapter.getItem(position);
            if(null == fileInfo) {
                return;
            }

            //如果点击的是一个文件夹
            if (fileInfo.directory()) {
                initCurrentData(fileInfo.path);
                if (mSDCardFileAdapter != null) {
                    mSDCardFileAdapter.setFileData(mCurrentData, mSelectedFileData);
                }
                return;
            }

            if (!fileInfo.selected) {
                if(checkFileSelected(fileInfo.getFileData(fileInfo))) {
                    return;
                }
            }

            //是文件的时候
            if (fileInfo.size > DomainSettingsManager.getInstance().getMaxMobileChatFileUploadSize() && !((mActivity instanceof FileSelectActivity) && ((FileSelectActivity)mActivity).mIgnoreSize)) {
                AtworkToast.showToast(AtworkApplicationLike.getResourceString(R.string.send_file_limit_tip, ChatMessageHelper.getMBOrKBString(DomainSettingsManager.getInstance().getMaxMobileChatFileUploadSize())));
                return;
            }
            fileInfo.selected = !fileInfo.selected;
            ((SDCardFileItem) view).mSelectBtn.setChecked(fileInfo.selected);
            FileData fileData = fileInfo.getFileData(fileInfo);
            refreshFileData(fileData);
        }
    };


    private void initCurrentData(String currentPath) {
        ArrayList<SDCardFileData.FileInfo> list = new ArrayList<>();
        if (TextUtils.isEmpty(currentPath)) {
            mCurrentData = new SDCardFileData(list, null, mSDCardPath);
            mHierarchyLve = 0;
        } else {
            mCurrentData = new SDCardFileData(list, null, currentPath);
            mHierarchyLve++;
        }
        mFileDataMap.put(mHierarchyLve, mCurrentData);
        if (list.isEmpty()) {
            mEmptyTip.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else {
            mEmptyTip.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }
        showDirectoryPath(mCurrentData.path);
    }

    private void showDirectoryPath(String path) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(mSDCardPath)) {
            return;
        }
        String directoryPath = "";
        if (path.contains(mSDCardPath)) {
            directoryPath = path.replace(mSDCardPath, mTitle);
        } else {
            directoryPath = path;
        }
        mDirPath.setText(directoryPath);
    }

    @Override
    protected void refreshFileData(FileData fileData) {

        List<FileData> fileDataRemovedList = new ArrayList<>();
        for (FileData data : mSelectedFileData) {
            if (data == null) {
                continue;
            }
            if (data.equals(fileData)) {
                fileDataRemovedList.add(data);
            }
        }
        if (!ListUtil.isEmpty(fileDataRemovedList)) {
            mSelectedFileData.removeAll(fileDataRemovedList);
            ((FileSelectActivity) mActivity).onSelectFileSizeUpdate();

            return;
        }
        mSelectedFileData.add(fileData);
        ((FileSelectActivity) mActivity).onSelectFileSizeUpdate();
    }

    @Override
    protected List<FileData> getFileSelectedList() {
        return mSelectedFileData;
    }


    public boolean handleSdCardFileBackEvent() {
        boolean result = false;
        if (mFileDataMap == null) {
            return result;
        }
        // 如果是第一级。。。
        if (mHierarchyLve == 0 || mFileDataMap.size() == 1) {
            return result;
        }
        // 如果不是第一级。。
        result = true;
        mFileDataMap.remove(mHierarchyLve);
        mHierarchyLve--;
        mCurrentData = mFileDataMap.get(mHierarchyLve);
        mEmptyTip.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);
        if (mSDCardFileAdapter != null) {
            mSDCardFileAdapter.setFileData(mCurrentData, mSelectedFileData);
        }
        showDirectoryPath(mCurrentData.path);
        return result;
    }

    @Override
    protected boolean onBackPressed() {
        getActivity().finish();
        return false;
    }


}

