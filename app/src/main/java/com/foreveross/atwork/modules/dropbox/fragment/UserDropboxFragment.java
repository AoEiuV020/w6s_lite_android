package com.foreveross.atwork.modules.dropbox.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.cache.DropboxCache;
import com.foreverht.cache.WatermarkCache;
import com.foreverht.db.service.repository.DropboxConfigRepository;
import com.foreverht.db.service.repository.DropboxRepository;
import com.foreverht.db.service.repository.WatermarkRepository;
import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreverht.workplus.module.file_share.FileShareAction;
import com.foreverht.workplus.module.file_share.activity.FileShareActivity;
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.dropbox.DropboxAsyncNetService;
import com.foreveross.atwork.api.sdk.dropbox.requestJson.DropboxRequestJson;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.Watermark;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox.SourceType;
import com.foreveross.atwork.infrastructure.model.dropbox.DropboxConfig;
import com.foreveross.atwork.infrastructure.model.dropbox.Requester;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.model.user.LoginUserBasic;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.encryption.MD5Utils;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.manager.DropboxConfigManager;
import com.foreveross.atwork.manager.DropboxManager;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.modules.dropbox.DropboxFileData;
import com.foreveross.atwork.modules.dropbox.activity.DropboxActivity;
import com.foreveross.atwork.modules.dropbox.activity.DropboxBaseActivity;
import com.foreveross.atwork.modules.dropbox.activity.DropboxModifyActivity;
import com.foreveross.atwork.modules.dropbox.activity.FileDetailActivity;
import com.foreveross.atwork.modules.dropbox.activity.MoveToDropboxActivity;
import com.foreveross.atwork.modules.dropbox.activity.OrgDropboxActivity;
import com.foreveross.atwork.modules.dropbox.activity.SaveToDropboxActivity;
import com.foreveross.atwork.modules.dropbox.adapter.DropboxFileAdapter;
import com.foreveross.atwork.modules.dropbox.component.DropboxFileItem;
import com.foreveross.atwork.modules.dropbox.component.DropboxListFooterView;
import com.foreveross.atwork.modules.dropbox.component.DropboxTimelineItemView;
import com.foreveross.atwork.modules.dropbox.component.SortedTimeAndNamePopup;
import com.foreveross.atwork.modules.dropbox.loader.DropboxLoader;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.theme.manager.SkinMaster;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import static com.foreveross.atwork.manager.DropboxManager.hasOpsAuth;
import static com.foreveross.atwork.modules.dropbox.activity.DropboxBaseActivity.KEY_INTENT_DROPBOX;
import static com.foreveross.atwork.modules.dropbox.activity.MoveToDropboxActivity.REQUEST_CODE_MOVE_DROPBOX;


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
 * Created by reyzhang22 on 16/9/7.
 */
public class UserDropboxFragment extends BackHandledFragment implements LoaderManager.LoaderCallbacks<List<Dropbox>>,
        DropboxManager.OnFileUploadListener, DropboxFileItem.OnItemIconClickListener {

    public static final String ACTION_DROPBOX_DATA_FRESH = "ACTION_DROPBOX_DATA_FRESH";
    public static final String ACTION_DATA_DELETED = "ACTION_DATA_DELETED";
    public static final String ACTION_DATA_DROPBOX_CONFIG = "ACTION_DATA_DROPBOX_CONFIG";

    public static final String KEY_INTENT_MODIFY_DROPBOX_CALLBACK = "KEY_INTENT_MODIFY_DROPBOX_CALLBACK";
    public static final String KEY_INTENT_IS_MODIFY = "KEY_INTENT_IS_MODIFY";
    public static final String KEY_INTENT_DROPBOX_CONFIG = "KEY_INTENT_DROPBOX_CONFIG";
    public static final int REQUEST_CODE_MODIFY_DROPBOX = 0x122;
    public static final int REQUEST_CODE_COPY_DROPBOX = 0x111;
    public static final int REQUEST_CODE_FILE_DETAIL = 0x113;

    private static final int FATCH_DROPBOX_LIMIT = 500;

    private ListView mUserFileList;

    private View mFunctionView;
    private TextView mSortOrder;
    private TextView mMultiSelected;
    private TextView mNewFolder;
    private ListView mDropboxList;
    private DropboxFileAdapter mDropboxFileAdapter;

    //当前目录中的目录数据
    private DropboxFileData mCurrentDropboxFileData;
    //表示打开目录到第几级
    private int mHierarchyLve = 0;
    //记录层级以及sdcard数据的映射关系，用于用户回退
    private Map<Integer, DropboxFileData> mFileDataMap = new HashMap<>();
    private String mCurrentParentId = "";
    private String mLastMoveParentId = "";
    private List<Dropbox> mAllList = new ArrayList<>();
    private Set<String> mSelectedSet = new HashSet<>();
    private DropboxListFooterView mFooterView;

    private View mNoFileView;
    private TextView mNoFileTip;

    private SortedMode mSortedMode = SortedMode.Time;

    /**
     * 是否全量更新
     */
    private boolean mCompleteRefresh = false;

    public enum SortedMode {
        Time,
        Name
    }

    private boolean mShowTimeLine = false;

    private Dropbox mSaveDropbox;

    public DropboxConfig mDropboxConfig = new DropboxConfig();

    private boolean mMoveOrCopy;

    private DropboxBaseActivity.DisplayMode mDisplayMode = DropboxBaseActivity.DisplayMode.Normal;

    private ProgressDialogHelper mProgressDialogHelper;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Dropbox dropbox = (Dropbox)msg.obj;
            if (dropbox == null) {
                return;
            }
            if (mDropboxFileAdapter != null) {
                mDropboxFileAdapter.updateProgress(dropbox);
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dropbox_user, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        registerListener();
        registerReceiver();

        SkinMaster.getInstance().changeTheme((ViewGroup) view);
        loadData();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
//        ((DropboxBaseActivity)mActivity).setTitleButtonStatus(true);
    }

    @Override
    public void onDestroy() {
        unRegisterReceiver();
        super.onDestroy();
    }

    @Override
    protected void findViews(View view) {
        mFunctionView = view.findViewById(R.id.function_view);
        mSortOrder = view.findViewById(R.id.sort_order_ops);
        mMultiSelected = view.findViewById(R.id.multi_selected_ops);
        mNewFolder = view.findViewById(R.id.new_folder_ops);
        mDropboxList = view.findViewById(R.id.my_dropbox_list);
        mFooterView = new DropboxListFooterView(mActivity);
        mDropboxList.addFooterView(mFooterView);
        mNoFileView = view.findViewById(R.id.not_dropbox_file_layout);
        mNoFileTip = mNoFileView.findViewById(R.id.not_file_tip);

        mProgressDialogHelper = new ProgressDialogHelper(getActivity());
    }

    private BroadcastReceiver mDataRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            resetData();
        }
    };

    private BroadcastReceiver mDataDeleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            ArrayList<String> deletedList = intent.getStringArrayListExtra("DELETED_LIST");
            onDropboxDelete(deletedList);
        }
    };

    private BroadcastReceiver mDropboxConfigReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            DropboxConfig dropboxConfig = (DropboxConfig) intent.getSerializableExtra(KEY_INTENT_DROPBOX_CONFIG);
            if (dropboxConfig == null) {
                return;
            }
            Watermark watermark = new Watermark();
            watermark.mSourceId = ((DropboxBaseActivity) mActivity).mCurrentSourceId;
            watermark.mType = Watermark.Type.DROPBOX;
            if ("show".equalsIgnoreCase(mDropboxConfig.mWatermark)){
                WatermarkRepository.getInstance().insertOrUpdateWatermark(watermark);
                WatermarkCache.getInstance().setWatermarkConfigCache(watermark, true);
            }
            if ("none".equalsIgnoreCase(mDropboxConfig.mWatermark)) {
                WatermarkRepository.getInstance().deleteWatermark(watermark);
                WatermarkCache.getInstance().setWatermarkConfigCache(watermark, false);
            }
            DropboxBaseActivity activity = (DropboxBaseActivity)mActivity;
            if (!dropboxConfig.mSourceId.equalsIgnoreCase(activity.mCurrentSourceId)) {
                return;
            }
            long maxTime = Math.max(mDropboxConfig.mLastRefreshTime, dropboxConfig.mLastRefreshTime);
            mDropboxConfig = dropboxConfig;
            mDropboxConfig.mLastRefreshTime = maxTime;
            SourceType sourceType = ((DropboxBaseActivity)mActivity).mCurrentSourceType;
            if (SourceType.Organization.equals(sourceType)) {
                invisibleUploadIcon(isAdmin());
            }
            if (SourceType.Discussion.equals(sourceType)) {
                invisibleUploadIcon(isMyDiscussion());
            }

            DropboxCache.getInstance().setDropboxConfigCache(mDropboxConfig);
            DropboxConfigRepository.getInstance().insertOrUpdateDropboxConfig(mDropboxConfig);
            updateVisualByConfig();
            mDropboxFileAdapter = null;
            updateListView();
        }
    };

    private boolean isAdmin() {
        return OrganizationManager.getInstance().isLoginAdminOrgSync(mActivity, ((DropboxBaseActivity)mActivity).mCurrentSourceId);
    }

    private boolean isMyDiscussion() {
        User loginUser = AtworkApplicationLike.getLoginUserSync();
        Discussion discussion = DiscussionManager.getInstance().queryDiscussionSync(mActivity, ((DropboxBaseActivity)mActivity).mCurrentSourceId);
        return discussion != null && discussion.mOwner != null && loginUser.mUserId.equalsIgnoreCase(discussion.mOwner.mUserId);
    }

    private void invisibleUploadIcon(boolean auth) {
        if (mDropboxConfig.mReadOnly && !auth && mActivity instanceof OrgDropboxActivity) {
            ((OrgDropboxActivity)mActivity).invisibleUploadIcon(false);
        } else if (!mDropboxConfig.mReadOnly && !auth && mActivity instanceof OrgDropboxActivity) {
            ((OrgDropboxActivity)mActivity).invisibleUploadIcon(true);
        }
    }

    private void resetData() {
        final DropboxBaseActivity baseActivity = (DropboxBaseActivity)mActivity;
        getRemoteUserDropbox();
        String sourceId = baseActivity.mCurrentSourceId;
        List<Dropbox> dropboxes = DropboxRepository.getInstance().getDropboxBySourceId(sourceId);
        mAllList.clear();
        mAllList.addAll(0, dropboxes);
        if (SortedMode.Time.equals(mSortedMode)) {
            Collections.sort(mAllList, (dropbox, t1) -> new Date(t1.mLastModifyTime).compareTo(new Date(dropbox.mLastModifyTime)));
        }

        updateListView();
    }

    public void getRemoteUserDropbox() {
        final DropboxBaseActivity baseActivity = (DropboxBaseActivity)mActivity;
        String configId = TextUtils.isEmpty(baseActivity.mCurrentParentId) ? baseActivity.mCurrentSourceId : baseActivity.mCurrentParentId;
        DropboxConfigManager.getInstance().getDropboxConfigBySourceId(mActivity, configId, config -> {
            mDropboxConfig = config;

            Requester requester = Requester.newRequester().mDomainId(baseActivity.mCurrentDomainId).mSourceType(baseActivity.mCurrentSourceType.toString())
                    .mSourceId(baseActivity.mCurrentSourceId).mLimit(FATCH_DROPBOX_LIMIT).mSkip(0).mParent(baseActivity.mCurrentParentId).build();
            SourceType sourceType  = ((DropboxBaseActivity)mActivity).mCurrentSourceType;
            if (SourceType.Organization.equals(sourceType)) {
                invisibleUploadIcon(isAdmin());
            }
            if (SourceType.Discussion.equals(sourceType)) {
                invisibleUploadIcon(isMyDiscussion());
            }

            updateVisualByConfig();
//            if (mCompleteRefresh) {
//                mDropboxConfig.mLastRefreshTime = -1;
//            }
//            if (!mCompleteRefresh && mDropboxConfig.mLastRefreshTime == -1 && !ListUtil.isEmpty(mAllList)) {
//                mDropboxConfig.mLastRefreshTime = mAllList.get(0).mLastModifyTime;
//            }
            List<Dropbox> dropboxes = new ArrayList<>();
            DropboxAsyncNetService.getInstance().getDropboxBySource( mActivity, requester, mDropboxConfig, dropboxes, new DropboxAsyncNetService.OnDropboxListener(){
                        @Override
                        public void onDropboxOpsSuccess(List<Dropbox> dropboxes) {
                            PersonalShareInfo.getInstance().setDropboxRefreshId(mActivity, baseActivity.mCurrentSourceId);
                            if (mDropboxConfig.mLastRefreshTime == -1) {
                                if(PersonalShareInfo.getInstance().isDropboxForceAllRefresh(AtworkApplicationLike.baseContext, baseActivity.mCurrentSourceId)) {
                                    PersonalShareInfo.getInstance().putDropboxForceAllRefresh(AtworkApplicationLike.baseContext, false, baseActivity.mCurrentSourceId);
                                }
                            }

                            if (dropboxes.isEmpty()) {
                                updateListView();
                                return;
                            }
                            asyncHandleDropboxData(dropboxes, config);
                        }

                        @Override
                        public void onDropboxOpsFail(int status) {

                        }
                    });
        });
    }

    private void asyncHandleDropboxData(List<Dropbox> dropboxes, DropboxConfig config) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                mDropboxConfig.mLastRefreshTime = dropboxes.get(0).mLastModifyTime;

                List<Dropbox> dropboxList = new ArrayList<>();
                for (Dropbox dropbox : dropboxes) {
                    Dropbox cacheBox = DropboxCache.getInstance().getDropboxCache(dropbox.mFileId);
                    if (cacheBox != null) {
                        dropbox.mDownloadStatus = cacheBox.mDownloadStatus;
                        dropbox.mLocalPath = cacheBox.mLocalPath;
                        dropbox.mIsOverdueReport = cacheBox.mIsOverdueReport;
                    }
                    dropboxList.add(dropbox);
                }
                DropboxBaseActivity activity = (DropboxBaseActivity)mActivity;
                String configId = TextUtils.isEmpty(activity.mCurrentParentId) ? activity.mCurrentSourceId : activity.mCurrentParentId;
                DropboxManager.getInstance().batchInsertDropboxes(mActivity, configId, dropboxList, config.mReadOnly);
                DropboxCache.getInstance().setDropboxListCache(dropboxList);
//                mAllList.addAll(0, dropboxes);

                //有可能这个文件在本地数据库已经存在，比如说，另外一端改了文件名，所以不能单纯addAll方式
                List<Dropbox> addList = new ArrayList<Dropbox>();
                for (Dropbox updateDropbox : dropboxList) {
                    boolean needAdd = true;
                    for (int i = 0; i < mAllList.size(); i++) {
                        Dropbox dropbox = mAllList.get(i);
                        if (dropbox.mFileId.equalsIgnoreCase(updateDropbox.mFileId)) {
                            mAllList.set(i, updateDropbox);
                            needAdd = false;
                        }
                    }
                    if (needAdd) {
                        if (!updateDropbox.mIsDir && mMoveOrCopy && !DropboxBaseActivity.DisplayMode.Send.equals(mDisplayMode)) {
                            continue;
                        }
                        addList.add(updateDropbox);
                    }

                }
                mAllList.addAll(0, addList);
                if (activity.mCurrentDisplayMode.equals(DropboxBaseActivity.DisplayMode.Move) && !ListUtil.isEmpty(mSelectedSet) && !ListUtil.isEmpty(mAllList)) {
                    List<Dropbox> filterList = new ArrayList<>();
                    for (Dropbox dropbox : mAllList) {
                        for (String fileId : mSelectedSet) {
                            if (dropbox.mFileId.equalsIgnoreCase(fileId)) {
                                filterList.add(dropbox);
                                continue;
                            }
                        }
                    }
                    mAllList.removeAll(filterList);
                }
                if (SortedMode.Time.equals(mSortedMode)) {
                    Collections.sort(mAllList, (dropbox, t1) -> new Date(t1.mLastModifyTime).compareTo(new Date(dropbox.mLastModifyTime)));
                } else {
                    Collections.sort(mAllList);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                setLayoutView(true);
                updateListView();
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    private void updateVisualByConfig() {
        if (mDropboxConfig == null || mActivity instanceof DropboxActivity || !isAdded()) {
            return;
        }
        SourceType sourceType = ((DropboxBaseActivity)mActivity).mCurrentSourceType;
        if (SourceType.Organization.equals(sourceType)) {
            updateVisual(isAdmin());
        }
        if (SourceType.Discussion.equals(sourceType)) {
            updateVisual(isMyDiscussion());
        }
    }

    private void updateVisual(boolean auth) {
        mNewFolder.setTextColor(mDropboxConfig.mReadOnly && !auth ?
                getResources().getColor(R.color.dropbox_hint_text_color) : getResources().getColor(R.color.dropbox_common_text_color));
        mNewFolder.setClickable(!(mDropboxConfig.mReadOnly && !auth));
        mMultiSelected.setTextColor(mDropboxConfig.mReadOnly && !auth  ?
                getResources().getColor(R.color.dropbox_hint_text_color) : getResources().getColor(R.color.dropbox_common_text_color));
        mMultiSelected.setClickable(!(mDropboxConfig.mReadOnly && !auth));
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mSaveDropbox = bundle.getParcelable(SaveToDropboxActivity.KEY_INTENT_DROPBOX);
            List<String>selectedList = bundle.getStringArrayList(DropboxBaseActivity.KEY_INTENT_MOVE_LIST);
            mLastMoveParentId = bundle.getString(DropboxBaseActivity.KEY_INTENT_MOVE_LAST_PARENT_ID, "");
            mCompleteRefresh = bundle.getBoolean(DropboxBaseActivity.KEY_INTENT_COMPLETE_REFRESH, false);
            mMoveOrCopy = bundle.getBoolean(DropboxBaseActivity.KEY_INTENT_MOVE_OR_COPY, false);
            DropboxBaseActivity.DisplayMode displayMode = (DropboxBaseActivity.DisplayMode)bundle.getSerializable(DropboxBaseActivity.KEY_INTENT_DISPLAY_MODE);
            if (displayMode != null ) {
                mDisplayMode = displayMode;
            }
            if (!ListUtil.isEmpty(selectedList)) {
                mSelectedSet.addAll(selectedList);
            }
            if (mSaveDropbox != null || !ListUtil.isEmpty(selectedList) || mMoveOrCopy) {
                mFunctionView.setVisibility(View.GONE);
            }

        }

        if(PersonalShareInfo.getInstance().isDropboxForceAllRefresh(BaseApplicationLike.baseContext, ((DropboxBaseActivity)mActivity).mCurrentSourceId)) {
            mCompleteRefresh = true;
        }
    }

    private void loadData() {
        mProgressDialogHelper.show();


        Loader loader = getLoaderManager().initLoader(0, null, this);
        loader.forceLoad();
    }

    public void onCancelClick() {
        mSelectedSet.clear();
        onSelectedChange(false);
        ((DropboxBaseActivity)mActivity).changeBottomStatusWhenSelected(mSelectedSet);
        if (mActivity instanceof DropboxActivity) {
            ((DropboxActivity)mActivity).changeBottomByDir(!TextUtils.isEmpty(mCurrentParentId));
        }
    }

    private void updateSortOrder(boolean isExpand) {
        Bitmap resourceBmp;
        if (isExpand) {
            resourceBmp = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_sort_unexpanded);
        } else {
            resourceBmp = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_sort_expanded);
        }
        mSortOrder.setCompoundDrawablesWithIntrinsicBounds(null, null, new BitmapDrawable(getResources(), resourceBmp), null);
    }

    private void registerListener() {
        mFooterView.setOnClickListener(view -> {});
        mSortOrder.setOnClickListener(view -> {
            updateSortOrder(true);
            SortedTimeAndNamePopup popup = new SortedTimeAndNamePopup(mActivity);
            popup.setOnPopupDismissListener(() -> {
                updateSortOrder(false);
            });
            popup.setSortedMode(mSortedMode);
            popup.setPopItemOnClickListener((title, pos) -> {
                if (pos == 0) {
                    mSortedMode = SortedMode.Time;
                    mShowTimeLine = true;
                    mSortOrder.setText(R.string.sorted_by_time);
                } else {
                    mSortedMode = SortedMode.Name;
                    mShowTimeLine = false;
                    mSortOrder.setText(R.string.sorted_by_name);
                }
                if (mAllList.isEmpty()) {
                    return;
                }
                mCurrentDropboxFileData = null;
                mCurrentDropboxFileData = new DropboxFileData(mAllList, null, mCurrentParentId, mSortedMode);
                mDropboxFileAdapter.notifyDataChange(mCurrentDropboxFileData);

            });


            popup.pop(mSortOrder);

        });

        mMultiSelected.setOnClickListener(view -> {
            ((DropboxBaseActivity)mActivity).changeDisplayMode(DropboxActivity.DisplayMode.Select);
            mDisplayMode = DropboxBaseActivity.DisplayMode.Select;
            updateSelectedVisual(mDisplayMode);
            onSelectedChange(true);
            ((DropboxBaseActivity)mActivity).changeBottomStatusWhenSelected(mSelectedSet);
        });

        mNewFolder.setOnClickListener(view -> {
            onNewFolder();
        });

        mDropboxList.setOnItemClickListener((adapterView, view, i, l) -> {
            if (view instanceof DropboxTimelineItemView) {
                return;
            }
            Dropbox dropbox = mDropboxFileAdapter.getItem(i);
            onDropboxItemClick(dropbox);
        });

        mDropboxList.setOnItemLongClickListener((parent, view, position, id) -> {
            if (view instanceof DropboxTimelineItemView) {
                return true;
            }
            if (DropboxBaseActivity.DisplayMode.Send.equals(mDisplayMode) || DropboxBaseActivity.DisplayMode.Select.equals(mDisplayMode)) {
                return true;
            }
            Dropbox dropbox = mDropboxFileAdapter.getItem(position);
            onDropboxItemLongClick(dropbox);
            return true;
        });

        mDropboxList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 不滚动时保存当前滚动到的位置
                if (mCurrentDropboxFileData == null) {
                    return;
                }
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    mCurrentDropboxFileData.mIndex = mDropboxList.getFirstVisiblePosition();
                    View v = mDropboxList.getChildAt(0);
                    mCurrentDropboxFileData.mTop = (v == null) ? 0 : v.getTop();
                    mFileDataMap.put(mHierarchyLve, mCurrentDropboxFileData);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }


    private void toFileDetail(Dropbox dropbox) {
        Intent intent = FileDetailActivity.getIntent(mActivity, dropbox, mDropboxConfig);
        mActivity.startActivityForResult(intent, REQUEST_CODE_FILE_DETAIL);
        mActivity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    private void onDropboxItemClick(Dropbox dropbox) {
        if (Dropbox.UploadStatus.Fail.equals(dropbox.mUploadStatus) || Dropbox.UploadStatus.Uploading.equals(dropbox.mUploadStatus)) {
            return;
        }
        //普通选择模式下的选择处理
        if (DropboxBaseActivity.DisplayMode.Select.equals(mDisplayMode)) {
            if (!DropboxManager.hasOpsAuth(mDropboxConfig)) {
                AtworkToast.showResToast(R.string.no_right_ops_this_folder);
                return;
            }
            if (mSelectedSet.contains(dropbox.mFileId)) {
                mSelectedSet.remove(dropbox.mFileId);
            } else {
                mSelectedSet.add(dropbox.mFileId);
            }
            ((DropboxBaseActivity)mActivity).changeBottomStatusWhenSelected(mSelectedSet);
            if (mDropboxFileAdapter != null) {
                mDropboxFileAdapter.setSelectedList(mSelectedSet);
            }
            return;
        }
        //发送模式下
        if (DropboxBaseActivity.DisplayMode.Send.equals(mDisplayMode)) {
            DropboxBaseActivity activity = (DropboxBaseActivity)mActivity;
            if (dropbox.mIsDir) {
                activity.changeTitle(dropbox.mFileName);
                if (mActivity instanceof SaveToDropboxActivity) {
                    ((SaveToDropboxActivity)mActivity).changeBackVisual(false);
                }
                initCurrentData(dropbox.mFileId);
                if (mDropboxFileAdapter != null) {
                    mDropboxFileAdapter.notifyDataChange(mCurrentDropboxFileData);
                }
                getRemoteUserDropbox();
                return;
            } else {
                if (!DropboxManager.hasOpsAuth(mDropboxConfig)) {
                    AtworkToast.showResToast(R.string.no_right_ops_this_folder);
                    return;
                }

                if (mSelectedSet.contains(dropbox.mFileId)) {
                    mSelectedSet.remove(dropbox.mFileId);
                } else {
                    if (activity.isSelectMaxFile()) {
                        return;
                    }
                    mSelectedSet.add(dropbox.mFileId);
                }
                activity.onScopeDropboxSelect(dropbox, mSelectedSet);
            }
            ((DropboxBaseActivity)mActivity).changeBottomStatusWhenSelected(mSelectedSet);
            if (mDropboxFileAdapter != null) {
                mDropboxFileAdapter.setSelectedList(mSelectedSet);
            }
            return;
        }
        //选择目录的处理
        if (dropbox.mIsDir) {
            ((DropboxBaseActivity)mActivity).changeTitle(dropbox.mFileName);
            if (mActivity instanceof SaveToDropboxActivity) {
                ((SaveToDropboxActivity)mActivity).changeBackVisual(false);
            }
            if (mActivity instanceof MoveToDropboxActivity) {
                ((MoveToDropboxActivity)mActivity).changeBackVisual(false);
            }
            if (mActivity instanceof DropboxActivity) {
                ((DropboxActivity)mActivity).changeBottomByDir(true);
            }
            initCurrentData(dropbox.mFileId);
            if (mDropboxFileAdapter != null) {
                mDropboxFileAdapter.notifyDataChange(mCurrentDropboxFileData);
            }
            getRemoteUserDropbox();
            return;
        }
        toFileDetail(dropbox);
    }

    private void onDropboxItemLongClick(Dropbox dropbox) {
        if (mMoveOrCopy) {
            return;
        }
        if (Dropbox.UploadStatus.Fail.equals(dropbox.mUploadStatus) || Dropbox.UploadStatus.Uploading.equals(dropbox.mUploadStatus)) {
            return;
        }
        DropboxManager.getInstance().popupLongClick(mActivity, this, ((DropboxBaseActivity) mActivity).mCurrentSourceType, ((DropboxBaseActivity) mActivity).mCurrentSourceId, mDropboxConfig, dropbox, new DropboxManager.OnItemLongClickListener() {
            @Override
            public void onItemLongClickCallback(String value, Dropbox dropbox) {
                dropboxItemClick(value, dropbox);
            }
        });

    }

    private void dropboxItemClick(String tag, Dropbox dropbox) {
        if (tag.equalsIgnoreCase(getString(R.string.send_to_contact))) {
            DropboxManager.getInstance().doCommandSendToContact(mActivity, dropbox);
            return;
        }
        if (tag.equalsIgnoreCase(getString(R.string.send_email))) {

            PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mActivity, new String[]{Manifest.permission.WRITE_CONTACTS}, new PermissionsResultAction() {
                @Override
                public void onGranted() {
                    DropboxManager.getInstance().doCommandSendEmail(mActivity, UserDropboxFragment.this, dropbox);

                }

                @Override
                public void onDenied(String permission) {
                    final AtworkAlertDialog alertDialog = AtworkUtil.getAuthSettingAlert(mActivity, permission);
                    alertDialog.setOnDismissListener(dialog -> {
                        if(alertDialog.shouldHandleDismissEvent) {
                            DropboxManager.getInstance().doCommandSendEmail(mActivity, UserDropboxFragment.this, dropbox);

                        }

                    });

                    alertDialog.show();
                }
            });


        }
        if (tag.equalsIgnoreCase(getString(R.string.title_share_file))) {

            FileShareAction fileShareAction = new FileShareAction();
            fileShareAction.setDomainId(dropbox.mDomainId);
            fileShareAction.setOpsId(((DropboxBaseActivity)mActivity).mCurrentSourceId);
            fileShareAction.setSourceType(((DropboxBaseActivity)mActivity).mCurrentSourceType);
            fileShareAction.setFileId(dropbox.mFileId);
            FileShareActivity.Companion.startActivity(mActivity, fileShareAction);
            return;
        }
        if (tag.equalsIgnoreCase(getString(R.string.save_to_dropbox))) {
            DropboxManager.getInstance().doCommandSaveToDropbox(mActivity, dropbox);
        }
        if (tag.equalsIgnoreCase(getString(R.string.file_attr))) {
            DropboxManager.getInstance().doCommandFileAttr(this, dropbox);
        }
        if (tag.equalsIgnoreCase(getString(R.string.rename))) {
            if (!DropboxManager.getInstance().hasRightToDoCommand(mActivity, dropbox, ((DropboxBaseActivity)mActivity).mCurrentSourceId)) {
                AtworkToast.showResToast(R.string.no_right_rename_file);
                return;
            }
            DropboxManager.getInstance().doCommandRename(mActivity, dropbox);
        }
        if (tag.equalsIgnoreCase(getString(R.string.move))) {
            doCommandMove(dropbox);
        }
        if (tag.equalsIgnoreCase(getString(R.string.delete))) {
            doCommandeDelete(dropbox);
        }

    }

    public void selectAll() {
        if (mCurrentDropboxFileData.mSubList.size() == mSelectedSet.size()) {
            mSelectedSet.clear();
        } else {
            mSelectedSet.clear();
            for (Dropbox dropbox : mCurrentDropboxFileData.mSubList) {
                mSelectedSet.add(dropbox.mFileId);
            }
        }
        ((DropboxBaseActivity)mActivity).changeBottomStatusWhenSelected(mSelectedSet);
        if (mDropboxFileAdapter != null) {
            mDropboxFileAdapter.setSelectedList(mSelectedSet);
        }
    }

    public void showDelFileDialog() {
        //不把deletelist 对象传过去，直接用新对象，清除全局对象
        List<String> deleteList = new ArrayList<>();

        boolean isAdmin = OrganizationManager.getInstance().isLoginAdminOrgSync(getActivity(), mDropboxConfig.mSourceId);
        boolean isDisOwner = DropboxManager.getInstance().isDiscussionOwner(mActivity, ((DropboxBaseActivity)mActivity).mCurrentSourceType, mDropboxConfig.mSourceId);

        for (String id : mSelectedSet) {
            if (TextUtils.isEmpty(id)) {
                continue;
            }
            if (isAdmin || isDisOwner) {
                deleteList.add(id);
                continue;
            }
            Dropbox dropbox = DropboxManager.getInstance().getLocalDropboxByFileId(id);
            if (!DropboxManager.isMyDropbox(mActivity, dropbox)) {
                AtworkToast.showResToast(R.string.no_right_delete_file);
                return;
            }
            deleteList.add(id);
        }
        mSelectedSet.clear();
        showDelFileDialog(deleteList);
    }

    public void showDelFileDialog(List<String> deleteList) {
        AtworkAlertDialog dialog = new AtworkAlertDialog(mActivity);
        dialog.setTitleText(R.string.delete_these_files);
        dialog.setContent(R.string.delete_these_files_message);
        dialog.setBrightBtnText(R.string.ok);
        dialog.setDeadBtnText(R.string.cancel);
        dialog.setClickDeadColorListener(dialog1 -> dialog.dismiss());
        dialog.setClickBrightColorListener(dialog1 -> {
            onSelectedChange(false);
            DropboxBaseActivity activity = (DropboxBaseActivity)mActivity;
            DropboxManager.getInstance().deleteDropboxFile(mActivity, deleteList, mCurrentParentId, activity.mCurrentDomainId, activity.mCurrentSourceType, activity.mCurrentSourceId, new DropboxAsyncNetService.OnDropboxListener() {
                @Override
                public void onDropboxOpsSuccess(List<Dropbox> dropboxes) {
                    ((DropboxBaseActivity)mActivity).changeDisplayMode(DropboxBaseActivity.DisplayMode.Normal);
                    onDropboxDelete(deleteList);
                    if (mActivity instanceof OrgDropboxActivity) {
                        ((OrgDropboxActivity)mActivity).onViewResume();
                    }
                }

                @Override
                public void onDropboxOpsFail(int status) {
                    ((DropboxBaseActivity)mActivity).changeDisplayMode(DropboxBaseActivity.DisplayMode.Normal);
                    if (mActivity instanceof OrgDropboxActivity) {
                        ((OrgDropboxActivity)mActivity).onViewResume();
                    }
                    if (status == 204003) {
                        if (mDropboxConfig.mReadOnly) {
                            AtworkToast.showResToast(R.string.no_right_ops_this_folder);
                            return;
                        }
                        AtworkToast.showResToast(R.string.no_right_delete_file);
                        return;
                    }
                    if (status == 204000) {
                        AtworkToast.showResToast(R.string.no_right_ops_this_folder);
                        return;
                    }
                    if (status == 204006) {
                        getRemoteUserDropbox();
                        AtworkToast.showResToast(R.string.no_file_exist);
                        return;
                    }

                    AtworkToast.showToast(getString(R.string.dropbox_network_error));
                }
            });
        });
        dialog.show();
    }

    public void onDropboxDirSelect(Dropbox dropbox) {
        if (dropbox == null) {
            return;
        }
        ((DropboxBaseActivity)mActivity).changeTitle(dropbox.mFileName);
        initCurrentData(dropbox.mFileId);
        if (mDropboxFileAdapter != null) {
            mDropboxFileAdapter.notifyDataChange(mCurrentDropboxFileData);
        }
    }

    private void onDropboxDelete(List<String> deleteList) {
        DropboxManager.getInstance().deleteLocalDropboxFile(mActivity, deleteList);
        if (!ListUtil.isEmpty(mAllList)) {

            List<Dropbox> deleteDropbox = new ArrayList<>();
            long count = 0;
            for (Dropbox dropbox : mAllList) {
                boolean hasDeleteList = false;
                for(String deleteId : deleteList) {
                    if (dropbox.mFileId.equalsIgnoreCase(deleteId)) {
                        deleteDropbox.add(dropbox);
                        hasDeleteList = true;
                        continue;
                    }
                }
                if (hasDeleteList) {
                    continue;
                }
                count += dropbox.mFileSize;
            }
            mDropboxConfig.mMaxVolume = String.valueOf(count);
            mAllList.removeAll(deleteDropbox);
        }

        if (mDropboxFileAdapter != null) {
            mDropboxFileAdapter.clear();
        }
        updateListView();
    }

    @Override
    protected boolean onBackPressed() {

        return false;
    }


    @Override
    public Loader<List<Dropbox>> onCreateLoader(int id, Bundle args) {
        DropboxBaseActivity activity = (DropboxBaseActivity)mActivity;
        return new DropboxLoader(mActivity, activity.mCurrentSourceId, mMoveOrCopy && !DropboxBaseActivity.DisplayMode.Send.equals(mDisplayMode));
    }

    @Override
    public void onLoadFinished(Loader<List<Dropbox>> loader, List<Dropbox> data) {
        if (isAdded()) {
            mProgressDialogHelper.dismiss();
        }

        getRemoteUserDropbox();
        mDropboxConfig = DropboxConfigRepository.getInstance().getDropboxConfigBySourceId(((DropboxBaseActivity)mActivity).mCurrentSourceId);
        if (data.isEmpty()) {
            return;
        }
        mAllList = data;
        setLayoutView(true);
        resetCurrentData();
        DropboxCache.getInstance().setDropboxListCache(mAllList);
        filterData();
        initCurrentData(mCurrentParentId);
        initAdapter();
        refreshSendView();
        retryUploading();
    }

    /**
     * 发送模式下，选中上次退出该界面的时选中的数据
     */
    private void refreshSendView() {
        if (!ListUtil.isEmpty(DropboxBaseActivity.mSelectedDropbox)) {
            for (Dropbox dropbox : DropboxBaseActivity.mSelectedDropbox) {
                mSelectedSet.add(dropbox.mFileId);
            }
            mDropboxFileAdapter.setSelectedList(mSelectedSet);
        }

    }

    /**
     * 重新初始化adapter
     */
    private void initAdapter() {
        mDropboxFileAdapter = new DropboxFileAdapter(mActivity, mCurrentDropboxFileData, mSelectedSet, mMoveOrCopy, mDropboxConfig);
        mDropboxFileAdapter.setIconSelectListener(this);
        mDropboxFileAdapter.setListView(mDropboxList);
        mDropboxList.setAdapter(mDropboxFileAdapter);
        mDropboxFileAdapter.setDisplayMode(mDisplayMode);

    }

    /**
     * 重置当前的网盘数据
     */
    private void resetCurrentData() {
        mCurrentDropboxFileData = null;
        if (mDropboxFileAdapter != null) {
            mDropboxFileAdapter.clear();
        }
    }

    /**
     * 根据不同条件过滤查询出来的数据
     */
    private void filterData() {
        DropboxBaseActivity activity = (DropboxBaseActivity)mActivity;
        List<Dropbox> filterList = new ArrayList<>();
        if (activity.mCurrentDisplayMode.equals(DropboxBaseActivity.DisplayMode.Move) && !ListUtil.isEmpty(mSelectedSet) && !ListUtil.isEmpty(mAllList)) {
            for (Dropbox dropbox : mAllList) {
                for (String fileId : mSelectedSet) {
                    if (dropbox.mFileId.equalsIgnoreCase(fileId)) {
                        filterList.add(dropbox);
                        continue;
                    }
                }
            }

        }
        if (DropboxBaseActivity.DisplayMode.Send.equals(mDisplayMode)) {
            for (Dropbox dropbox : mAllList) {
                if (dropbox != null && (Dropbox.UploadStatus.Uploading.equals(dropbox.mUploadStatus) || Dropbox.UploadStatus.Fail.equals(dropbox.mUploadStatus))) {
                    filterList.add(dropbox);
                }
            }
        }
        if (filterList.isEmpty()) {
            return;
        }
        mAllList.removeAll(filterList);

    }

    /**
     * 继续发送上传中的文件
     */
    private void retryUploading() {
        for(Dropbox dropbox : mAllList) {
            if (dropbox != null && Dropbox.UploadStatus.Uploading.equals(dropbox.mUploadStatus)) {
                //TODO..必须先断开上次正在上次的网络连接，因为如果后台继续在上，这时又以当前的端点上次，就会出现来回数值变化的问题
                DropboxManager.getInstance().pauseUpload(mActivity, dropbox);
                onRetryClick(dropbox);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Dropbox>> loader) { }

    private void initCurrentData(String currentParentId) {
        mCurrentParentId = currentParentId;
        ((DropboxBaseActivity)mActivity).mCurrentParentId = mCurrentParentId;
        if (TextUtils.isEmpty(currentParentId)) {
            mCurrentDropboxFileData = new DropboxFileData(mAllList, null, "", mSortedMode);
            mHierarchyLve = 0;
        } else {
            mCurrentDropboxFileData = new DropboxFileData(mAllList, null, currentParentId, mSortedMode);
            mHierarchyLve++;
        }
        mFileDataMap.put(mHierarchyLve, mCurrentDropboxFileData);
        if (mCurrentDropboxFileData.mSubList.isEmpty() || mAllList.isEmpty()) {
            setLayoutView(false);
        } else {
            setLayoutView(true);
        }
    }

    public boolean handleDropboxDirBackEvent() {
        boolean result = false;
        if (mFileDataMap == null) {
            return result;
        }
        // 如果是第一级。。。
        if (mHierarchyLve == 0 || mFileDataMap.size() == 1) {
            updateFooterView();
            return result;
        }
        // 如果不是第一级。。

        if (mFileDataMap.size() == 2) {
            if (mActivity instanceof SaveToDropboxActivity) {
                ((SaveToDropboxActivity)mActivity).changeBackVisual(true);
            }
            if (mActivity instanceof MoveToDropboxActivity) {
                ((MoveToDropboxActivity)mActivity).changeBackVisual(true);
            }
            if (mActivity instanceof DropboxActivity) {
                ((DropboxActivity)mActivity).changeBottomByDir(false);
            }
        }
        result = true;
        mFileDataMap.remove(mHierarchyLve);
        mHierarchyLve--;
        updateFooterView();
        mCurrentDropboxFileData = mFileDataMap.get(mHierarchyLve);
        String parentId = mCurrentDropboxFileData.mParent;
        String name = "";
        if (!TextUtils.isEmpty(parentId)) {
            Dropbox dropbox = DropboxCache.getInstance().getDropboxCache(parentId);
            if (dropbox != null) {
                name = dropbox.mFileName;
            }
        }
        ((DropboxBaseActivity)mActivity).changeTitle(name);
        mCurrentParentId = mCurrentDropboxFileData.mParent;
        int lastIndex = mCurrentDropboxFileData.mIndex;
        int lastTop = mCurrentDropboxFileData.mTop;
        ((DropboxBaseActivity)mActivity).mCurrentParentId = mCurrentParentId;
        mCurrentDropboxFileData = null;
        mCurrentDropboxFileData = new DropboxFileData(mAllList, null, mCurrentParentId, mSortedMode);
        if (mDropboxFileAdapter != null) {
            mDropboxFileAdapter.clear();
            mDropboxFileAdapter.notifyDataChange(mCurrentDropboxFileData);
            mDropboxList.setSelectionFromTop(lastIndex, lastTop);
        }
        if (mCurrentDropboxFileData.mSubList.isEmpty() || mAllList.isEmpty()) {
            setLayoutView(false);
        } else {
            setLayoutView(true);
        }
        return result;
    }

    public void uploadFilesToDropbox(List<FileData> selectedFileData, String domainId, String sourceId, SourceType sourceType) {
        //强制刷新一次,因为有可能存在组织文件同时在不同设备上上传，导致数据不一致
        getRemoteUserDropbox();
        DropboxManager.getInstance().uploadFileDataToDropbox(mActivity, selectedFileData, domainId, sourceType, sourceId, mCurrentParentId, this);
    }


    @Override
    public void onFileStartUploading(List<Dropbox> newList) {
        if (newList == null ||newList.isEmpty()) {
            return;
        }
        if (mNoFileView.isShown()) {
            setLayoutView(true);
        }
        mAllList.addAll(0, newList);
        DropboxCache.getInstance().setDropboxListCache(newList);
        updateListView();
    }

    @Override
    public void onFileUploadingProgress(Dropbox dropbox) {
        int i = 0;
        for(Dropbox orgDropbox : mAllList) {
            if (dropbox.mFileId.equalsIgnoreCase(orgDropbox.mFileId)) {
                if (orgDropbox.mUploadBreakPoint > dropbox.mUploadBreakPoint) {
                    return;
                }
                orgDropbox.mUploadBreakPoint = dropbox.mUploadBreakPoint;
                break;
            }
        }
        mHandler.obtainMessage(0, dropbox).sendToTarget();

    }

    @Override
    public void refreshView(List<Dropbox> dropboxList) {
        mActivity.runOnUiThread(() -> {
            mAllList = dropboxList;
            DropboxCache.getInstance().setDropboxListCache(mAllList);
            long count = 0;
            for (Dropbox dropbox : mAllList) {
                if (dropbox == null) {
                    continue;
                }
                count += dropbox.mFileSize;
            }
            mDropboxConfig.mMaxVolume = String.valueOf(count);
            updateListView();
        });
    }

    @Override
    public void OnFileUploadFail(Dropbox dropbox) {

        for(Dropbox origDropbox : mAllList) {
            if (origDropbox.mFileId.equalsIgnoreCase(dropbox.mFileId)) {
                origDropbox.mUploadStatus = Dropbox.UploadStatus.Fail;
                DropboxRepository.getInstance().insertOrUpdateDropbox(origDropbox);
                break;
            }
        }
        mDropboxFileAdapter.notifyDataSetChanged();
    }

    private void updateListView() {

        mCurrentDropboxFileData = new DropboxFileData(mAllList, null, mCurrentParentId, mSortedMode);
        if (mDropboxFileAdapter == null) {
            initAdapter();
        } else {
            mDropboxFileAdapter.notifyDataChange(mCurrentDropboxFileData);
        }
        mDropboxList.setSelectionFromTop(0, 0);
        if (!ListUtil.isEmpty(mAllList)) {
            updateFooterView();
        }
        if (mCurrentDropboxFileData.mSubList.isEmpty() || mAllList.isEmpty()) {
            setLayoutView(false);
            return;
        }
    }

    private void updateFooterView() {
        if(null == mFooterView) {
            return;
        }


        mFooterView.setVisibility(View.GONE);
        if (!AtworkConfig.SPECIAL_ENABLE_DISCUSSION_FILE_WHEN_CLOSE_DROPBOX) {
            if ( mHierarchyLve == 0 || mFileDataMap.size() == 1) {
                mFooterView.setVisibility(View.VISIBLE);
                DomainSettingsManager.PanSettingsType panSettingsType = DropboxManager.getInstance().getPanSettingTypeBySourceId(mActivity, ((DropboxBaseActivity)mActivity).mCurrentSourceId, ((DropboxBaseActivity)mActivity).mCurrentSourceType);
                mFooterView.setUsedText(DropboxManager.getInstance().getPanUsedAndTotalLimit(mDropboxConfig.mMaxVolume, panSettingsType));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            if ((requestCode == REQUEST_CODE_FILE_DETAIL ||
                    requestCode == REQUEST_CODE_COPY_DROPBOX ||
                    requestCode == REQUEST_CODE_MOVE_DROPBOX) &&
                    resultCode == Activity.RESULT_OK) {
                getRemoteUserDropbox();
            }
            return;
        }
        if (requestCode == REQUEST_CODE_MODIFY_DROPBOX && resultCode == Activity.RESULT_OK) {

            List<Dropbox> list = data.getParcelableArrayListExtra(KEY_INTENT_MODIFY_DROPBOX_CALLBACK);
            if (ListUtil.isEmpty(list)) {
                return;
            }
            boolean isModify = data.getBooleanExtra(KEY_INTENT_IS_MODIFY, false);
            if (isModify) {

                Dropbox modifiedDropbox = list.get(0);

                int i = 0;
                for (Dropbox dropbox : mAllList) {
                    if (dropbox.mFileId.equalsIgnoreCase(modifiedDropbox.mFileId)) {
                        mAllList.remove(i);
                        break;
                    }
                    i ++;
                }
                mAllList.add(0, modifiedDropbox);
                updateListView();
            } else {

                setLayoutView(true);
                mAllList.addAll(0, list);
                updateListView();
            }

            return;
        }

    }

    /**
     * 更新选择视图
     */
    public void updateSelectedVisual(DropboxBaseActivity.DisplayMode displayMode) {
        mDisplayMode = displayMode;
        if (mDropboxFileAdapter != null) {
            mDropboxFileAdapter.setDisplayMode(displayMode);
        }
    }

    /**
     * 返回正常模式,主要是处理从其他模式上返回的问题
     */
    public void updateNormalVisual(DropboxBaseActivity.DisplayMode displayMode) {
        if (mDropboxConfig != null) {
            onSelectedChange(!hasOpsAuth(mDropboxConfig));
        }
        if (mActivity instanceof DropboxActivity) {
            ((DropboxActivity)mActivity).changeBottomByDir(!TextUtils.isEmpty(mCurrentParentId));
        }
        mSelectedSet.clear();
        updateSelectedVisual(displayMode);
    }

    public void updateMoveVisual(DropboxBaseActivity.DisplayMode displayMode) {
        updateSelectedVisual(mDisplayMode);
        if (mMoveOrCopy) {
            return;
        }

    }

    /**
     * 删除文件
     */
    private void doCommandeDelete(Dropbox dropbox) {
        if (!DropboxManager.getInstance().hasRightToDoCommand(mActivity, dropbox, ((DropboxBaseActivity)mActivity).mCurrentSourceId)) {
            AtworkToast.showResToast(R.string.no_right_delete_file);
            return;
        }
        List<String> deleteList = new ArrayList<>();
        deleteList.add(dropbox.mFileId);
        showDelFileDialog(deleteList);
    }


    /**
     * 移动文件
     */
    private void doCommandMove(Dropbox dropbox) {
        if (!DropboxManager.getInstance().hasRightToDoCommand(mActivity, dropbox, ((DropboxBaseActivity)mActivity).mCurrentSourceId)) {
            AtworkToast.showResToast(R.string.no_right_move_file);
            return;
        }
        mSelectedSet.add(dropbox.mFileId);
        startMoveActivity();
    }

    public void startMoveActivity() {
        DropboxBaseActivity activity = (DropboxBaseActivity)mActivity;
        boolean isAdmin = OrganizationManager.getInstance().isLoginAdminOrgSync(mActivity, mDropboxConfig.mSourceId);
        boolean isDiscussionOwner = DropboxManager.getInstance().isDiscussionOwner(mActivity, activity.mCurrentSourceType, mDropboxConfig.mSourceId);
        ArrayList<String> moveList = new ArrayList<>();
        if (isAdmin || isDiscussionOwner) {
            for (String id : mSelectedSet) {
                if (TextUtils.isEmpty(id)) {
                    continue;
                }
                moveList.add(id);
            }
        } else {
            for (String id : mSelectedSet) {
                if (TextUtils.isEmpty(id)) {
                    continue;
                }
                Dropbox dropbox = DropboxManager.getInstance().getLocalDropboxByFileId(id);
                if (!DropboxManager.isMyDropbox(mActivity, dropbox)) {
                    AtworkToast.showResToast(R.string.no_right_move_file);
                    return;
                }
                moveList.add(id);
            }
        }

        mSelectedSet.clear();
        activity.changeDisplayMode(DropboxBaseActivity.DisplayMode.Normal);
        if (mActivity instanceof OrgDropboxActivity) {
            ((OrgDropboxActivity)mActivity).changeDisplayMode(DropboxBaseActivity.DisplayMode.Normal);
        }
        onSelectedChange(false);
        MoveToDropboxActivity.actionDropboxMove(mActivity, activity.mCurrentDomainId, activity.mCurrentSourceId, activity.mCurrentSourceType, mCurrentParentId, moveList);
    }

    public void onDropboxMove() {
        if (mLastMoveParentId.equalsIgnoreCase(mCurrentParentId)) {
            AtworkToast.showResToast(R.string.file_in_current_dir);
            return;
        }
        DropboxManager.getInstance().moveDropboxFile(mActivity, mSelectedSet, mLastMoveParentId, mCurrentParentId, ((DropboxBaseActivity)mActivity).mCurrentDomainId,
                ((DropboxBaseActivity)mActivity).mCurrentSourceType, ((DropboxBaseActivity)mActivity).mCurrentSourceId, new DropboxAsyncNetService.OnDropboxListener() {
                    @Override
                    public void onDropboxOpsSuccess(List<Dropbox> dropboxes) {
                        mSelectedSet.clear();
                        if (ListUtil.isEmpty(dropboxes)) {
                            AtworkToast.showToast(getString(R.string.dropbox_network_error));
                            mActivity.finish();
                            return;
                        }
                        //移动后，不能直接覆盖数据库，要查出以前数据库中相应文件的本地路径，复制到当前最新的移动路径当中
                        List<Dropbox> updateList = new ArrayList<>();
                        for (Dropbox dropbox : dropboxes) {
                            if (TextUtils.isEmpty(dropbox.mFileId)) {
                                continue;
                            }
                            Dropbox orgDropbox = DropboxManager.getInstance().getLocalDropboxByFileId(dropbox.mFileId);
                            if(orgDropbox == null) {
                                continue;
                            }
                            dropbox.mLocalPath = orgDropbox.mLocalPath;
                            updateList.add(dropbox);
                        }
                        if (!ListUtil.isEmpty(updateList)) {
                            DropboxRepository.getInstance().batchInsertDropboxes(dropboxes);
                        }
                        AtworkToast.showResToast(R.string.dropbox_move_success);
                        mActivity.setResult(Activity.RESULT_OK);
                        mActivity.finish();
                    }

                    @Override
                    public void onDropboxOpsFail(int status) {
                        if (status == 204003) {
                            if (mDropboxConfig.mReadOnly) {
                                AtworkToast.showResToast(R.string.no_right_ops_this_folder);
                                mActivity.finish();
                                return;
                            }
                            AtworkToast.showResToast(R.string.no_right_move_file);
                            mActivity.finish();
                            return;
                        }
                        if (status == 204006) {
                            AtworkToast.showResToast(R.string.no_file_exist);
                        } else {
                            AtworkToast.showResToast(R.string.dropbox_network_error);
                        }

                        mActivity.finish();
                    }
                });
    }


    public void onDropboxCopy(Dropbox dropbox) {
        DropboxBaseActivity activity = (DropboxBaseActivity)mActivity;
        DropboxManager.getInstance().copyDroboxFile(mActivity, mSaveDropbox, activity.mCurrentDomainId, activity.mCurrentSourceType, activity.mCurrentSourceId, mCurrentParentId, new DropboxAsyncNetService.OnDropboxListener() {
            @Override
            public void onDropboxOpsSuccess(List<Dropbox> dropboxes) {
                AtworkToast.showResToast(R.string.dropbox_save_success);
//                DropboxRepository.getInstance().batchInsertDropboxes(dropboxes);
                Dropbox copy = dropboxes.get(0);
                if (copy != null) {
                    DropboxRepository.getInstance().insertOrUpdateDropbox(copy);
                }
                mActivity.setResult(Activity.RESULT_OK);
//                onDestroy();
                mActivity.finish();
//                LocalBroadcastManager.getInstance(mActivity).sendBroadcast(new Intent(ACTION_DATA_FRESH));
            }

            @Override
            public void onDropboxOpsFail(int status) {
                int msgId = R.string.dropbox_network_error;
                if (status == 204003) {
                    msgId = R.string.no_right_move_file;
                }
                if (status == 204000) {
                    msgId = R.string.no_right_ops_this_folder;
                }
                if (status == 204006) {
                    msgId = R.string.no_file_exist;
                }

                SourceType currentSourceType = activity.mCurrentSourceType;
                if(null == currentSourceType) {
                    currentSourceType = dropbox.mSourceType;
                }

                if (status == 204014 || status == 204015) {
                    DropboxManager.getInstance().toastDropboxOverlimit(mActivity, dropbox.mSourceId, currentSourceType, dropbox.mFileSize, status);
                } else {
                    AtworkToast.showResToast(msgId);
                }
                mActivity.setResult(Activity.RESULT_OK);
//                mActivity.finish();

            }
        });
    }

    public void onDropboxCreate(Dropbox dropbox) {
        DropboxBaseActivity activity = (DropboxBaseActivity)mActivity;
        DropboxRequestJson json = new DropboxRequestJson();
        json.mDigest = MD5Utils.getDigest(dropbox.mLocalPath);
        json.mSize = dropbox.mFileSize;
        json.mFileId = dropbox.mMediaId;
        json.mName = dropbox.mFileName;
        json.mParent = mCurrentParentId;

        DropboxRequestJson.OptUser user = new DropboxRequestJson.OptUser();
        LoginUserBasic basic = LoginUserInfo.getInstance().getLoginUserBasic(mActivity);
        user.nName = basic.mName;
        json.mUser = user;
        DropboxConfig dropboxConfig = DropboxConfigManager.getInstance().syncGetDropboxConfigBySourceId(mActivity, activity.mCurrentSourceId);
        DropboxAsyncNetService.getInstance().makeDropboxFileOrDir(mActivity, activity.mCurrentDomainId, activity.mCurrentSourceType, activity.mCurrentSourceId, 0, new Gson().toJson(json), dropboxConfig, true, new DropboxAsyncNetService.OnDropboxListener() {
            @Override
            public void onDropboxOpsSuccess(List<Dropbox> dropboxes) {
                if (dropboxes == null) {
                    return;
                }
                boolean readOnly = false;
                if (dropboxConfig != null) {
                    DropboxCache.getInstance().setDropboxConfigCache(dropboxConfig);
                    readOnly = dropboxConfig.mReadOnly;
                }
                asyncUpdateDataAndUI(dropboxes, readOnly, activity.mCurrentSourceId);
                DropboxBaseActivity.refreshDropboxData();
                if (isAdded()) {
                    AtworkToast.showToast(getString(R.string.save_success));
                    mActivity.setResult(Activity.RESULT_OK);
                    mActivity.finish();
                }
            }

            @Override
            public void onDropboxOpsFail(int status) {
//                mListener.OnFileUploadFail(mDropboxFile);
                if (status == 204003) {
                    AtworkToast.showResToast(R.string.no_right_move_file);
                }
                if (status == 204000) {
                    AtworkToast.showResToast(R.string.no_right_ops_this_folder);
                }
                if (status == 204014 || status == 204015) {
                    SourceType currentSourceType = activity.mCurrentSourceType;
                    if(null == currentSourceType) {
                        currentSourceType = dropbox.mSourceType;
                    }
                    DropboxManager.getInstance().toastDropboxOverlimit(mActivity, dropbox.mSourceId, currentSourceType, dropbox.mFileSize, status);
                } else {
                    AtworkToast.showResToast(R.string.dropbox_network_error);
                }

                mActivity.setResult(Activity.RESULT_OK);
//                mActivity.finish();
            }
        });
    }

    private void asyncUpdateDataAndUI(List<Dropbox> dropboxes, boolean readOnly, String sourceId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            if (dropboxes != null) {
                DropboxManager.getInstance().batchInsertDropboxes(mActivity, sourceId, dropboxes, readOnly);
            }

        });
    }


    public void onNewFolder() {
        if (mDropboxConfig != null && mDropboxConfig.mReadOnly) {
            AtworkToast.showResToast(R.string.no_right_ops_this_folder);
            return;
        }
        DropboxBaseActivity activity = (DropboxBaseActivity)mActivity;
        Intent intent = DropboxModifyActivity.getIntent(mActivity, DropboxModifyActivity.ModifyAction.CreateFolder, null,
                activity.mCurrentSourceId, activity.mCurrentDomainId, activity.mCurrentSourceType, mCurrentParentId);
        mActivity.startActivityForResult(intent, REQUEST_CODE_MODIFY_DROPBOX);
        mActivity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    private void setLayoutView(boolean showViewList) {
        mNoFileView.setVisibility(showViewList ? View.GONE : View.VISIBLE);
        mDropboxList.setVisibility(showViewList ? View.VISIBLE : View.GONE);
        if (mMoveOrCopy && !DropboxBaseActivity.DisplayMode.Send.equals(mDisplayMode)) {
            mNoFileTip.setText(R.string.not_dir_so_far);
        }
    }

    @Override
    public void onSelectIconClick(Dropbox dropbox) {
        onDropboxItemClick(dropbox);
    }

    @Override
    public void onExpandIconClick(Dropbox dropbox) {
        onDropboxItemLongClick(dropbox);
    }

    @Override
    public void onCancelClick(Dropbox dropbox) {
        if (ListUtil.isEmpty(mAllList)) {
            return;
        }
        for (Dropbox orgDrobox : mAllList) {
            if (orgDrobox.mFileId.equalsIgnoreCase(dropbox.mFileId)) {
                mAllList.remove(orgDrobox);
                break;
            }
        }
        mCurrentDropboxFileData = new DropboxFileData(mAllList, null, mCurrentParentId, mSortedMode);
        if (mDropboxFileAdapter != null) {
            mDropboxFileAdapter.notifyDataChange(mCurrentDropboxFileData);
            DropboxManager.getInstance().breakDropboxUpload(mActivity, dropbox, mDropboxFileAdapter);
        }

    }

    @Override
    public void onRetryClick(Dropbox dropbox) {

        dropbox.mUploadStatus = Dropbox.UploadStatus.Uploading;
        updateListView();
        List<Dropbox> dropboxList = new ArrayList<>();
        dropboxList.add(dropbox);
        DropboxBaseActivity activity = (DropboxBaseActivity)mActivity;
        DropboxManager.getInstance().uploadFileToDropbox(mActivity, dropboxList, activity.mCurrentDomainId, activity.mCurrentSourceType, activity.mCurrentSourceId, mCurrentParentId, true, this);
    }


    private void registerReceiver() {
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mDataRefreshReceiver, new IntentFilter(ACTION_DROPBOX_DATA_FRESH));
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mDataDeleteReceiver, new IntentFilter(ACTION_DATA_DELETED));
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mDropboxConfigReceiver, new IntentFilter(ACTION_DATA_DROPBOX_CONFIG));
    }

    private void unRegisterReceiver() {
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mDataRefreshReceiver);
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mDataDeleteReceiver);
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mDropboxConfigReceiver);
    }


    /**
     * 选择模式下，改变颜色和是否有点击事件
     */
    public void onSelectedChange(boolean selectMode) {
        int selectedColor = getResources().getColor(R.color.dropbox_common_text_color);
        int unSelectedColor = getResources().getColor(R.color.dropbox_hint_text_color);
        mMultiSelected.setTextColor(selectMode ? unSelectedColor : selectedColor);
        mMultiSelected.setClickable(!selectMode);
        mNewFolder.setTextColor(selectMode ? unSelectedColor : selectedColor);
        mNewFolder.setClickable(!selectMode);

    }

    public void refreshDropbox(Dropbox dropbox) {
        Bundle bundle = getArguments();
        if(null == bundle) {
            bundle = new Bundle();
        }

        bundle.putParcelable(KEY_INTENT_DROPBOX, dropbox);

        mSaveDropbox = dropbox;
    }
}
