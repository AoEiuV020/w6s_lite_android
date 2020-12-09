package com.foreveross.atwork.modules.discussion.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.db.daoService.DiscussionDaoService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.utils.ContactHelper;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.discussion.activity.DiscussionSearchListActivity;
import com.foreveross.atwork.modules.discussion.activity.SelectDiscussionListActivity;
import com.foreveross.atwork.modules.group.activity.UserSelectActivity;
import com.foreveross.atwork.modules.discussion.adapter.DiscussionListAdapter;
import com.foreveross.atwork.modules.discussion.model.DiscussionSelectControlAction;
import com.foreveross.atwork.modules.group.module.SelectToHandleAction;
import com.foreveross.atwork.modules.group.service.SelectToHandleActionService;
import com.foreveross.atwork.support.BackHandledFragment;

import java.util.ArrayList;
import java.util.List;


public class SelectDiscussionListFragment extends BackHandledFragment  {

    private static final String TAG = SelectDiscussionListFragment.class.getSimpleName();


    private DiscussionListAdapter mDiscussionListAdapter;

    private View mVNoGroups;

    private ListView mGroupListView;

    private ImageView mIvBack;

    private View mViewTitleBar;

    private TextView mTvRightText;

    private ImageView mIvSearch;

    private TextView mTvTitle;

    private TextView mTvNoGroups;

    private UserSelectActivity.SelectMode mSelectMode = UserSelectActivity.SelectMode.SELECT;

    private List<Discussion> mSelectedDiscussions = new ArrayList<>();
    private List<Discussion> mDiscussionList = new ArrayList<>();

    private DiscussionSelectControlAction mDiscussionSelectControlAction;
    private int mMaxSelect;
    private boolean mNeedSearchBtn;
    private String mTitle;
    private SelectToHandleAction mSelectToHandleAction;
    private List<String> mDiscussionIdPreSelectedList;


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(SelectDiscussionListActivity.ACTION_SELECT.equals(action)) {
                Discussion discussionClicked = intent.getParcelableExtra(SelectDiscussionListActivity.DATA_HANDLE);

                if(discussionClicked.isSelect() && checkIsToThreshold(discussionClicked)) {
                    return;
                }

                handleDiscussion(discussionClicked.getId(), discussionClicked.isSelect());

                mDiscussionListAdapter.notifyDataSetChanged();

                refreshRightSureText();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBroadcast();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_group_list, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        initRefreshUI();

        ProgressDialogHelper progressDialogHelper = new ProgressDialogHelper(getActivity());
        progressDialogHelper.show();
        DiscussionDaoService.getInstance().getAllDiscussions(getActivity(), discussionList -> {
            progressDialogHelper.dismiss();

            mDiscussionList = discussionList;

            initSelectedStatus(mDiscussionList);

            mDiscussionListAdapter.clear();
            mDiscussionListAdapter.addAll(discussionList);
            mDiscussionListAdapter.notifyDataSetChanged();

            refreshViewVisibility(discussionList);
        });

        mDiscussionListAdapter = new DiscussionListAdapter(getActivity());
        mDiscussionListAdapter.setSelectMode(mSelectMode);
        mDiscussionListAdapter.setSingleSelect(isSingleMode());


        mGroupListView.setAdapter(mDiscussionListAdapter);

        registerListener();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
    }


    private void refreshViewVisibility(List<Discussion> discussionList) {
        if(ListUtil.isEmpty(discussionList)) {
            mVNoGroups.setVisibility(View.VISIBLE);
            mGroupListView.setVisibility(View.GONE);

            handleSearchBtnUI(View.GONE);

        } else {
            mVNoGroups.setVisibility(View.GONE);
            mGroupListView.setVisibility(View.VISIBLE);

            handleSearchBtnUI(View.VISIBLE);

        }
    }


    @Override
    protected void findViews(View view) {
        mVNoGroups = view.findViewById(R.id.layout_no_discussions);
        mTvNoGroups = mVNoGroups.findViewById(R.id.tv_no_data);
        mGroupListView = view.findViewById(R.id.lw_items);
        mIvBack = view.findViewById(R.id.title_bar_common_back);
        mTvTitle = view.findViewById(R.id.title_bar_common_title);
        mViewTitleBar = view.findViewById(R.id.friends_title);
        mTvRightText = view.findViewById(R.id.title_bar_common_right_text);
        mIvSearch = view.findViewById(R.id.iv_search);
    }

    private void registerBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SelectDiscussionListActivity.ACTION_SELECT);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void unregisterBroadcast() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mBroadcastReceiver);

    }

    private void initData() {
        Bundle bundle = getArguments();
        if(null != bundle) {
            mDiscussionSelectControlAction = bundle.getParcelable(SelectDiscussionListActivity.DATA_DISCUSSION_SELECT_CONTROL_ACTION);
            if (null != mDiscussionSelectControlAction) {
                mMaxSelect = mDiscussionSelectControlAction.getMax();
                mNeedSearchBtn = mDiscussionSelectControlAction.getNeedSearchBtn();
                mTitle = mDiscussionSelectControlAction.getViewTitle();
                mSelectToHandleAction = mDiscussionSelectControlAction.getSelectToHandleAction();
                mDiscussionIdPreSelectedList = mDiscussionSelectControlAction.getDiscussionIdListPreSelected();
            }


        }


        if(null != mSelectToHandleAction) {
            FragmentActivity activity = getActivity();
            if(activity instanceof SelectDiscussionListActivity) {
                ((SelectDiscussionListActivity)activity).setFinishChainTag(SelectToHandleActionService.TAG_HANDLE_SELECT_TO_ACTION);
            }
        }
    }

    private void initRefreshUI() {
        //取消默认的分割线
        mGroupListView.setDivider(null);

        if (StringUtils.isEmpty(mTitle)) {
            mTvTitle.setText(R.string.select);
        } else {
            mTvTitle.setText(mTitle);
        }

        if(mNeedSearchBtn) {
            mIvSearch.setVisibility(View.VISIBLE);

        } else {
            mIvSearch.setVisibility(View.GONE);

        }


        if(!isSingleMode()) {
            mTvRightText.setVisibility(View.VISIBLE);
        } else {
            mTvRightText.setVisibility(View.GONE);

        }


        refreshRightSureText();
    }

    private void handleSearchBtnUI(int visibility) {
        if(mNeedSearchBtn) {
            mIvSearch.setVisibility(visibility);
            return;
        }


        mIvSearch.setVisibility(View.GONE);

    }

    private void refreshRightSureText() {
        if (isSelectToActionMode()) {
            doRefreshRightSureText(SelectToHandleActionService.INSTANCE.getContactList());

        } else {
            doRefreshRightSureText(mSelectedDiscussions);

        }
    }

    private void doRefreshRightSureText(List<? extends ShowListItem> selectedDiscussions) {
        if(ListUtil.isEmpty(selectedDiscussions)) {
            mTvRightText.setText(R.string.ok);
            mTvRightText.setAlpha(0.5f);
            mTvRightText.setEnabled(false);

        } else {
            mTvRightText.setText(getStrings(R.string.ok_with_num, selectedDiscussions.size() + "") );
            mTvRightText.setAlpha(1f);
            mTvRightText.setEnabled(true);

        }
    }

    private void registerListener() {

        mIvBack.setOnClickListener(v -> finish());

        mGroupListView.setOnItemClickListener((parent, view, position, id) -> {

            Discussion discussion = (Discussion) parent.getItemAtPosition(position);


            if (isSingleMode()) {

                if(isSelectToActionMode()) {
                    SelectToHandleActionService.INSTANCE.action(getActivity(), mSelectToHandleAction, discussion);


                } else {
                    submit();

                }


            } else {
                if(!discussion.isSelect() && checkIsToThreshold(discussion)) {
                    return;
                }


                discussion.select(!discussion.isSelect());

                if(discussion.isSelect()) {
                    mSelectedDiscussions.add(discussion);

                } else {
                    mSelectedDiscussions.remove(discussion);

                }


                if (isSelectToActionMode()) {
                    SelectToHandleActionService.INSTANCE.handleSelect(discussion);
                }

                refreshRightSureText();
                mDiscussionListAdapter.notifyDataSetChanged();
            }

        });


        mTvRightText.setOnClickListener(v -> {

            if (isSelectToActionMode()) {
                SelectToHandleActionService.INSTANCE.action(getActivity(), mSelectToHandleAction, SelectToHandleActionService.INSTANCE.getContactList());

            } else {
                submit();

            }
        });


        mIvSearch.setOnClickListener(v -> {

            mDiscussionSelectControlAction.setDiscussionIdListPreSelected(ContactHelper.toIdList(mSelectedDiscussions));

            Intent intent = DiscussionSearchListActivity.getIntent(getActivity(), mDiscussionSelectControlAction);
            startActivity(intent);
        });



    }

    private boolean checkIsToThreshold(Discussion discussion) {
        if(isSelectToActionMode()) {

            boolean isToThreshold = SelectToHandleActionService.INSTANCE.checkIsToThreshold(mSelectToHandleAction, discussion);
            return isToThreshold;
        }

        boolean isToThreshold = mSelectedDiscussions.size() + 1 > mMaxSelect;

        if(isToThreshold) {
            toast(mDiscussionSelectControlAction.getMaxTip());
        }

        return isToThreshold;
    }

    private boolean isSelectToActionMode() {
        return null != mSelectToHandleAction;
    }

    private boolean isSingleMode() {
        return 1 == mMaxSelect;
    }

    private void submit() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(SelectDiscussionListActivity.DATA_DISCUSSION_LIST, (ArrayList<? extends Parcelable>) mSelectedDiscussions);
        getActivity().setResult(Activity.RESULT_OK, intent);
        finish();
    }


    private void initSelectedStatus(List<Discussion> discussionList) {
        if(!ListUtil.isEmpty(mDiscussionIdPreSelectedList)) {
            for(Discussion discussion : discussionList) {
                if(mDiscussionIdPreSelectedList.contains(discussion.mDiscussionId)) {
                    discussion.select(true);
                }
            }
        }
    }

    private void handleDiscussion(String discussionId, boolean select) {
        for(Discussion discussion : mDiscussionList) {
            if(discussionId.equals(discussion.getId())) {
                discussion.select(select);

                if(discussion.isSelect()) {
                    mSelectedDiscussions.add(discussion);
                } else {
                    mSelectedDiscussions.remove(discussion);

                }


                break;
            }
        }


    }


    @Override
    protected boolean onBackPressed() {
        finish();
        return false;
    }


}
