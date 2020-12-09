package com.foreveross.atwork.modules.dropbox.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.cache.DropboxCache;
import com.foreverht.db.service.repository.DropboxRepository;
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.dropbox.DropboxAsyncNetService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.dropbox.DropboxConfig;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.manager.DropboxConfigManager;
import com.foreveross.atwork.manager.DropboxManager;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.modules.dropbox.activity.DropboxBaseActivity;
import com.foreveross.atwork.modules.dropbox.activity.FileDetailActivity;
import com.foreveross.atwork.modules.dropbox.activity.MoveToDropboxActivity;
import com.foreveross.atwork.modules.dropbox.activity.SortedByFileTypeActivity;
import com.foreveross.atwork.modules.dropbox.adapter.SortedTypeAdapter;
import com.foreveross.atwork.modules.dropbox.component.DropboxFileItem;
import com.foreveross.atwork.modules.dropbox.component.SortedTypeItem;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.DropboxUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

import static com.foreveross.atwork.modules.dropbox.activity.MoveToDropboxActivity.REQUEST_CODE_MOVE_DROPBOX;
import static com.foreveross.atwork.modules.dropbox.fragment.UserDropboxFragment.REQUEST_CODE_COPY_DROPBOX;
import static com.foreveross.atwork.modules.dropbox.fragment.UserDropboxFragment.REQUEST_CODE_MODIFY_DROPBOX;

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
 * Created by reyzhang22 on 16/9/26.
 */

public class SortedByTypeFragment extends BackHandledFragment implements SortedTypeItem.OnImageGridItemClickListener, DropboxFileItem.OnItemIconClickListener {

    private TextView mSortedTitle;

    private ExpandableListView mSortedList;
    private SortedTypeAdapter mAdapter;

    private ImageView mBack;

    private TextView mTitle;

    private ImageView mMore;

    private Dropbox.DropboxFileType mFileType;
    private Dropbox.SourceType mSourceType;
    private String mSourceId;
    private String mDomainId;
    private String mParentId;
    private DropboxConfig mDropboxConfig = new DropboxConfig();


    private View mNotDropboxView;

    private BroadcastReceiver mDataRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadLocalData();
        }
    };

    private LinkedHashMap<String, List<Dropbox>> mMap = new LinkedHashMap<String, List<Dropbox>>();

    private List<String> searchList = new ArrayList<>();
    private List<String> searchedList = new ArrayList<>();

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (searchedList.size() == searchList.size()) {
                if (mMap.isEmpty()) {
                    mNotDropboxView.setVisibility(View.VISIBLE);
                    mSortedList.setVisibility(View.GONE);
                    return;
                }
                LinkedHashMap<String, List<Dropbox>> filterMap = new LinkedHashMap<String, List<Dropbox>>();
                Set<String> keys = mMap.keySet();
                for (String key : keys) {
                    List<Dropbox> list = mMap.get(key);
                    if (ListUtil.isEmpty(list)) {
                        continue;
                    }
                    filterMap.put(key, list);
                }
                if (filterMap.isEmpty()) {
                    mNotDropboxView.setVisibility(View.VISIBLE);
                    mSortedList.setVisibility(View.GONE);
                    return;
                }
                mAdapter = null;
                mAdapter = new SortedTypeAdapter(mActivity, filterMap, mFileType, mDropboxConfig, SortedByTypeFragment.this, SortedByTypeFragment.this);
                mSortedList.setAdapter(mAdapter);
                for(int i = 0; i < mAdapter.getGroupCount(); i++){
                    mSortedList.expandGroup(i);
                }
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sorted_by_type, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        handleBundle();
        initData();
        registerListener();
        registerBroadcaset();
    }

    @Override
    protected boolean onBackPressed() {
        mActivity.finish();
        return false;
    }


    private void initData() {
        mTitle.setText(getString(R.string.sorted_category));
        String name = "";
        if (!TextUtils.isEmpty(mParentId)) {
            Dropbox dropbox = DropboxCache.getInstance().getDropboxCache(mParentId);
            if (dropbox != null) {
                name = dropbox.mFileName;
            }
        }
        if (TextUtils.isEmpty(name)) {
            if (Dropbox.SourceType.User.equals(mSourceType)) {
                name = getString(R.string.my_file);
            }
            if (Dropbox.SourceType.Discussion.equals(mSourceType)) {
                Discussion discussion = DiscussionManager.getInstance().queryDiscussionSync(mActivity, mSourceId);
                name = discussion == null ? getString(R.string.group_file2) : discussion.mName;
            }
            if (Dropbox.SourceType.Organization.equals(mSourceType)) {
                Organization organization = OrganizationManager.getInstance().getOrganizationSyncByOrgCode(mActivity, mSourceId);
                name = organization == null ? getString(R.string.org_file) : organization.getNameI18n(mActivity);
            }
        }
        String typeName = DropboxUtils.getChineseFromFileType(mFileType);
        String tip = String.format(getString(R.string.sorted_type_title), name, AtworkApplicationLike.getResourceString(R.string.others).equalsIgnoreCase(typeName) ? AtworkApplicationLike.getResourceString(R.string.other_files) : typeName);
        mSortedTitle.setText(StringUtils.middleEllipse(tip, 30, 15, 10, 10));
    }

    private void handleBundle() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        mFileType = (Dropbox.DropboxFileType)bundle.get(SortedByFileTypeActivity.INTENT_KEY_SORTED_TYPE);
        mSourceType = (Dropbox.SourceType)bundle.get(SortedByFileTypeActivity.INTENT_KEY_SOURCE_TYPE);
        mSourceId = bundle.getString(SortedByFileTypeActivity.INTENT_KEY_SOURCE_ID);
        mDomainId = bundle.getString(SortedByFileTypeActivity.INTENT_KEY_DOMAIN_ID);
        mParentId = bundle.getString(SortedByFileTypeActivity.INTENT_KEY_PARENT_ID);
        mDropboxConfig = (DropboxConfig) bundle.getSerializable(SortedByFileTypeActivity.INTENT_KEY_DROPBOX_CONFIG);
        loadLocalData();
    }

    private void loadLocalData() {
        mMap.clear();
        DropboxConfigManager.getInstance().getDropboxConfigBySourceId(mActivity, mSourceId, config -> {
            mDropboxConfig = config;
            Executors.newSingleThreadExecutor().submit(() -> loadData(mParentId));
        });

    }

    private void loadData(String parentId){
        searchList.add(parentId);
        List<Dropbox> dropboxList = DropboxRepository.getInstance().getDropboxByFileTypeInSourceId(mSourceId, mFileType, parentId);


        List<Dropbox> dirList = new ArrayList<>();
        for (Dropbox dropbox : dropboxList) {
            String monthOfDay = TimeUtil.getStringForMillis(dropbox.mLastModifyTime, TimeUtil.getTimeFormat4(BaseApplicationLike.baseContext));
            monthOfDay = TimeUtil.isSameMonth(BaseApplicationLike.baseContext, TimeUtil.getCurrentTimeInMillis(), monthOfDay);
            List<Dropbox> dropboxes = null;
            if (!mMap.containsKey(monthOfDay)) {
                dropboxes = new ArrayList<>();
                mMap.put(monthOfDay, dropboxes);
            } else {
                dropboxes = mMap.get(monthOfDay);
            }
            if (dropbox.mIsDir) {
                dirList.add(dropbox);
            }
            if (mFileType.equals(dropbox.mFileType) && !dropbox.mIsDir) {
                dropboxes.add(dropbox);
            }

        }
        for (Dropbox nextDropbox : dirList) {
            loadData(nextDropbox.mFileId);
        }
        searchedList.add(parentId);
        mHandler.obtainMessage().sendToTarget();

    }



    private void registerListener() {
        mBack.setOnClickListener(view-> {
            onBackPressed();
        });
        mSortedList.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int i) {
                mSortedList.expandGroup(i);
                return;
            }
        });
        mSortedList.setOnGroupClickListener((parent, v, groupPosition, id) -> false);
        mSortedList.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            Dropbox dropbox = mAdapter.getChild(groupPosition, childPosition);
            Intent intent = FileDetailActivity.getIntent(mActivity, dropbox, mDropboxConfig);
            startActivity(intent);
            return true;
        });
        mSortedList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    int childPosition = ExpandableListView.getPackedPositionChild(id);
                    Dropbox dropbox = mAdapter.getChild(groupPosition, childPosition);
                    onDropboxItemLongClick(dropbox);
                    return true;
                }

                return true;
            }
        });
    }

    private void onDropboxItemLongClick(Dropbox dropbox) {
        if (Dropbox.DropboxFileType.Image.equals(dropbox.mFileType)) {
            return;
        }
        DropboxManager.getInstance().popupLongClick(mActivity, this, mSourceType, mSourceId, mDropboxConfig, dropbox, (value, dropbox1) -> dropboxItemClick(value, dropbox1));
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
                    DropboxManager.getInstance().doCommandSendEmail(mActivity, SortedByTypeFragment.this, dropbox);

                }

                @Override
                public void onDenied(String permission) {
                    final AtworkAlertDialog alertDialog = AtworkUtil.getAuthSettingAlert(mActivity, permission);
                    alertDialog.setOnDismissListener(dialog -> {
                        if(alertDialog.shouldHandleDismissEvent) {
                            DropboxManager.getInstance().doCommandSendEmail(mActivity, SortedByTypeFragment.this, dropbox);

                        }

                    });

                    alertDialog.show();
                }
            });
        }
        if (tag.equalsIgnoreCase(getString(R.string.file_attr))) {
            DropboxManager.getInstance().doCommandFileAttr(this, dropbox);
        }
        if (tag.equalsIgnoreCase(getString(R.string.rename))) {
            if (!DropboxManager.getInstance().hasRightToDoCommand(mActivity, dropbox, mSourceId)) {
                AtworkToast.showResToast(R.string.no_right_rename_file);
                return;
            }
            DropboxManager.getInstance().doCommandRename(mActivity, dropbox);
        }
        if (tag.equalsIgnoreCase(getString(R.string.move))) {
            startMoveActivity(dropbox);
        }
        if (tag.equalsIgnoreCase(getString(R.string.delete))) {
            doCommandeDelete(dropbox);
        }

    }

    /**
     * 删除文件
     */
    private void doCommandeDelete(Dropbox dropbox) {
        if (!DropboxManager.getInstance().hasRightToDoCommand(mActivity, dropbox, mSourceId)) {
            AtworkToast.showResToast(R.string.no_right_delete_file);
            return;
        }

        showDelFileDialog(dropbox);
    }


    public void startMoveActivity(Dropbox dropbox) {
        if (!DropboxManager.getInstance().hasRightToDoCommand(mActivity, dropbox, mSourceId)) {
            AtworkToast.showResToast(R.string.no_right_move_file);
            return;
        }
        ArrayList<String> moveList = new ArrayList<>();
        moveList.add(0, dropbox.mFileId);
        MoveToDropboxActivity.actionDropboxMove(mActivity, mDomainId, mSourceId, mSourceType, dropbox.mParentId, moveList);
    }


    public void showDelFileDialog(Dropbox dropbox) {
        List<String> deleteList = new ArrayList<>();
        deleteList.add(dropbox.mFileId);
        AtworkAlertDialog dialog = new AtworkAlertDialog(mActivity);
        dialog.setTitleText(R.string.delete_these_files);
        dialog.setContent(R.string.delete_these_files_message);
        dialog.setBrightBtnText(R.string.ok);
        dialog.setDeadBtnText(R.string.cancel);
        dialog.setClickDeadColorListener(dialog1 -> dialog.dismiss());
        dialog.setClickBrightColorListener(dialog1 -> {
            DropboxManager.getInstance().deleteDropboxFile(mActivity, deleteList, dropbox.mParentId, mDomainId, mSourceType, mSourceId, new DropboxAsyncNetService.OnDropboxListener() {
                @Override
                public void onDropboxOpsSuccess(List<Dropbox> dropboxes) {
                    onDropboxDelete(deleteList);
                }

                @Override
                public void onDropboxOpsFail(int status) {
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
                    AtworkToast.showToast(getString(R.string.dropbox_network_error));
                }
            });
        });
        dialog.show();
    }

    private void onDropboxDelete(List<String> deleteList) {
        DropboxRepository.getInstance().batchDeleteDropboxList(deleteList);
//        loadLocalData();
        DropboxBaseActivity.refreshDropboxData();
    }


    @Override
    public void onImageGridItemClick(Dropbox dropbox) {
        Intent intent = FileDetailActivity.getIntent(mActivity, dropbox, mDropboxConfig);
        startActivity(intent);
    }


    @Override
    public void onSelectIconClick(Dropbox dropbox) {}

    @Override
    public void onExpandIconClick(Dropbox dropbox) {
        onDropboxItemLongClick(dropbox);
    }

    @Override
    public void onCancelClick(Dropbox dropbox) {}

    @Override
    public void onRetryClick(Dropbox dropbox) {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterBroadcast();
    }

    @Override
    protected void findViews(View view) {
        mSortedTitle = view.findViewById(R.id.sorted_title);
        mSortedList = view.findViewById(R.id.sorted_listview);
        mSortedList.setGroupIndicator(null);
        mBack = view.findViewById(R.id.title_bar_chat_detail_back);
        mTitle = view.findViewById(R.id.title_bar_chat_detail_name);
        mMore = view.findViewById(R.id.title_bar_main_more_btn);
        mMore.setVisibility(View.GONE);
        mNotDropboxView = view.findViewById(R.id.not_dropbox_file_layout);
    }

    private void registerBroadcaset() {
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mDataRefreshReceiver, new IntentFilter(UserDropboxFragment.ACTION_DROPBOX_DATA_FRESH));
    }

    private void unRegisterBroadcast() {
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mDataRefreshReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == REQUEST_CODE_MODIFY_DROPBOX || requestCode == REQUEST_CODE_COPY_DROPBOX  || requestCode == REQUEST_CODE_MOVE_DROPBOX)&& resultCode == Activity.RESULT_OK) {
//            loadLocalData();
            DropboxBaseActivity.refreshDropboxData();
            return;
        }
    }
}
