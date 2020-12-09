package com.foreveross.atwork.modules.dropbox.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.modules.dropbox.activity.DropboxBaseActivity;
import com.foreveross.atwork.modules.dropbox.activity.OrgDropboxActivity;
import com.foreveross.atwork.modules.dropbox.adapter.OrgsDropboxAdapter;
import com.foreveross.atwork.modules.dropbox.component.DropboxListFooterView;
import com.foreveross.atwork.modules.dropbox.loader.OrgDropboxLoader;
import com.foreveross.atwork.support.BackHandledFragment;
import com.google.gson.internal.LinkedTreeMap;

import java.util.List;
import java.util.Map;

import static com.foreveross.atwork.modules.dropbox.activity.DropboxBaseActivity.KEY_INTENT_DROPBOX;

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
 * Created by reyzhang22 on 16/9/8.
 */
public class OrgsDropboxFragment extends BackHandledFragment implements LoaderManager.LoaderCallbacks<Map<String, List<Discussion>>> {

    private static final int REQUEST_CODE = 0X0133;

    private LinkedTreeMap<String, List<Discussion>> mOrgMapping;

    private ExpandableListView mExpandedListView;
    private OrgsDropboxAdapter mExpandedAdapter;
    private Dropbox mSaveDropbox;
    private ChatPostMessage mFromMessage;
    private DropboxBaseActivity.DisplayMode mDisplayMode;
    private boolean mMoveOrCopy;
    private TextView mNoOrgView;

    private View mFooterView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dropbox_orgs, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        registerListener();
    }

    @Override
    public void onStart() {
        super.onStart();

        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
//        ((DropboxBaseActivity)mActivity).setTitleButtonStatus(false);
        if (mExpandedAdapter != null) {
            mExpandedAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void findViews(View view) {
        mExpandedListView = view.findViewById(R.id.orgs_files_listview);
        mNoOrgView = view.findViewById(R.id.no_orgs_tip);
        mExpandedListView.setGroupIndicator(null);
        mFooterView = new DropboxListFooterView(mActivity);
        mExpandedListView.addFooterView(mFooterView);
    }


    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mSaveDropbox = bundle.getParcelable(DropboxBaseActivity.KEY_INTENT_DROPBOX);
            mFromMessage = (ChatPostMessage) bundle.getSerializable(DropboxBaseActivity.KEY_INTENT_FROM_MESSAGE);
            mDisplayMode = (DropboxBaseActivity.DisplayMode) bundle.getSerializable(DropboxBaseActivity.KEY_INTENT_DISPLAY_MODE);
            mMoveOrCopy = bundle.getBoolean(DropboxBaseActivity.KEY_INTENT_MOVE_OR_COPY, false);
        }

        mExpandedAdapter = new OrgsDropboxAdapter(mActivity, mMoveOrCopy);
        mExpandedListView.setAdapter(mExpandedAdapter);
    }

    private void loadData() {
        Loader loader = getLoaderManager().initLoader(0, null, this);
        loader.forceLoad();
    }

    private void registerListener() {
        mFooterView.setOnClickListener(view -> {});
        mExpandedListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            if (mExpandedAdapter == null) {
                return false;
            }
            Discussion discussion = mExpandedAdapter.getChild(groupPosition, childPosition);
            Dropbox.SourceType sourceType = Dropbox.SourceType.Organization;
            String sourceId = "";
            String domainId = "";
            String title = "";
            String subTitle = "";
            Organization organization = OrganizationManager.getInstance().getOrganizationSyncByOrgCode(mActivity, mExpandedAdapter.getGroup(groupPosition));
            domainId = organization.mDomainId;

            if (OrgDropboxLoader.PUBLIC_ID.equalsIgnoreCase(discussion.mDiscussionId)) {

                sourceId = organization.mOrgCode;
                sourceType = Dropbox.SourceType.Organization;
                title = getString(R.string.public_area);
                subTitle = organization.getNameI18n(BaseApplicationLike.baseContext);
            } else {
                sourceId = discussion.mDiscussionId;
                sourceType = Dropbox.SourceType.Discussion;
                title = getString(R.string.group_file2);
                subTitle = discussion.mName;
            }
            int maxSelectCount = 9;
            if(mActivity instanceof DropboxBaseActivity) {
                DropboxBaseActivity activity = (DropboxBaseActivity)mActivity;
                maxSelectCount = activity.mMaxSelectCount;
            }
            Intent intent = OrgDropboxActivity.getIntent(mActivity, sourceType, sourceId, domainId, title, subTitle, mSaveDropbox, mFromMessage, mDisplayMode, false, mMoveOrCopy);
            intent.putExtra(DropboxBaseActivity.KEY_INTENT_SELECT_MAX, maxSelectCount);
            mActivity.startActivityForResult(intent, REQUEST_CODE);

            return false;
        });
    }

    @Override
    protected boolean onBackPressed() {
        return false;
    }



    @Override
    public Loader<Map<String, List<Discussion>>> onCreateLoader(int id, Bundle args) {
        return new OrgDropboxLoader(mActivity);
    }

    @Override
    public void onLoadFinished(Loader<Map<String, List<Discussion>>> loader, Map<String, List<Discussion>> data) {
        if (data.isEmpty()) {
            mNoOrgView.setVisibility(View.VISIBLE);
            mExpandedListView.setVisibility(View.GONE);
            return;
        }
        mOrgMapping = (LinkedTreeMap)data;
        mExpandedAdapter.setData(mOrgMapping);
        for(int i = 0; i < mExpandedAdapter.getGroupCount(); i++){
            if (i == 0) {
                mExpandedListView.expandGroup(i);
                break;
            }

        }

    }

    @Override
    public void onLoaderReset(Loader<Map<String, List<Discussion>>> loader) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mActivity.setResult(Activity.RESULT_OK);
            mActivity.finish();
        }
    }

    public void refreshDropbox(Dropbox dropbox) {
        Bundle bundle = getArguments();
        if(null == bundle) {
            bundle = new Bundle();
        }

        bundle.putParcelable(KEY_INTENT_DROPBOX, dropbox);

        mSaveDropbox = dropbox;
    }

    private boolean isFromMsg() {
        return null != mFromMessage;
    }
}
