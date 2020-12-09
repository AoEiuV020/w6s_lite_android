package com.foreveross.atwork.modules.discussion.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment;
import com.foreveross.atwork.modules.chat.model.EntrySessionRequest;
import com.foreveross.atwork.modules.group.activity.UserSelectActivity;
import com.foreveross.atwork.modules.discussion.adapter.DiscussionListAdapter;
import com.foreveross.atwork.modules.group.listener.SelectedChangedListener;
import com.foreveross.atwork.modules.discussion.loader.DiscussionListLoader;
import com.foreveross.atwork.modules.group.module.UserSelectControlAction;
import com.foreveross.atwork.support.BackHandledFragment;

import java.util.ArrayList;
import java.util.List;


public class DiscussionListFragment extends BackHandledFragment implements LoaderManager.LoaderCallbacks<List<Discussion>>, SelectedChangedListener {

    private static final String TAG = DiscussionListFragment.class.getSimpleName();

    private DiscussionListAdapter mDiscussionListAdapter;

    private View mVNoDiscussions;

    private ListView mLvDiscussion;

    private ImageView mIvBack;

    private View mViewTitleBar;

    private TextView mTvNoDiscussion;


    private UserSelectControlAction mUserSelectControlAction;

    private UserSelectActivity.SelectMode mSelectMode = UserSelectActivity.SelectMode.NO_SELECT;

    private UserSelectActivity.SelectAction mSelectAction;


    private List<String> mSelectedContacts = new ArrayList<>();
    private List<String> mNotAllowedSelectedContacts = new ArrayList<>();

    private List<Discussion> mDiscussionList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discussion_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();

        mDiscussionListAdapter = new DiscussionListAdapter(getActivity());

        mDiscussionListAdapter.setSelectMode(mSelectMode);

        mLvDiscussion.setAdapter(mDiscussionListAdapter);



        registerListener();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        List<Discussion> discussionListNotCheck = DiscussionManager.getInstance().getDiscussionListNotCheck();
        mDiscussionList = discussionListNotCheck;

        if (null != discussionListNotCheck) {
            refreshListUI(discussionListNotCheck);
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().initLoader(0, null, this).forceLoad();

    }

    @Override
    protected void findViews(View view) {
        mVNoDiscussions = view.findViewById(R.id.layout_no_discussions);
        mTvNoDiscussion = mVNoDiscussions.findViewById(R.id.tv_no_data);
        mLvDiscussion = view.findViewById(R.id.lw_items);
        mIvBack = view.findViewById(R.id.title_bar_common_back);
        ((TextView) (view.findViewById(R.id.title_bar_common_title))).setText(getResources().getString(R.string.discussion_title));
        mViewTitleBar = view.findViewById(R.id.friends_title);
        //取消默认的分割线
        mLvDiscussion.setDivider(null);

        mTvNoDiscussion.setText(R.string.no_discussion);
    }

    private void initData() {
        if (getArguments() != null) {

            mUserSelectControlAction = getArguments().getParcelable(UserSelectActivity.DATA_USER_SELECT_PARAMS);

            if (null != mUserSelectControlAction) {
                mSelectMode = mUserSelectControlAction.getSelectMode();

                mSelectAction = mUserSelectControlAction.getSelectAction();
            }


        }

        if (!(UserSelectActivity.SelectMode.NO_SELECT.equals(mSelectMode))) {

            mViewTitleBar.setVisibility(View.GONE);
            if (getActivity() instanceof UserSelectActivity) {
                UserSelectActivity userSelectActivity = (UserSelectActivity) getActivity();
                mSelectedContacts = userSelectActivity.getSelectedContactIdList();
                mNotAllowedSelectedContacts = userSelectActivity.getNotAllowedSelectedStringList();
            }
        }


    }


    private boolean selectOrSendMode() {
        return UserSelectActivity.SelectMode.SELECT == mSelectMode;
    }

    private void registerListener() {

        mIvBack.setOnClickListener(v -> finish());

        mLvDiscussion.setOnItemClickListener((parent, view, position, id) -> {

            if(selectOrSendMode()) {

                Discussion discussion = (Discussion) parent.getItemAtPosition(position);

                if (getActivity() instanceof UserSelectActivity) {
                    UserSelectActivity userSelectActivity = (UserSelectActivity) getActivity();
                    //todo cordova 单选的配置

                    if (!userSelectActivity.getNotAllowedSelectedList().contains(discussion)) {
                        if (!discussion.mSelect && !userSelectActivity.isAllowed()) {
                            toast(mUserSelectControlAction.getMaxTip());

                            return;
                        }

                        if (!discussion.mSelect && userSelectActivity.isOneSelected()) {
                            return;
                        }

                        discussion.select();
                        userSelectActivity.action(discussion);
                    }
                }
                return;
            }



            Discussion discussion = (Discussion) parent.getItemAtPosition(position);

            EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest(SessionType.Discussion, discussion)
                    .setOrgId(discussion.getOrgCodeCompatible());

            ChatSessionDataWrap.getInstance().entrySessionSafely(entrySessionRequest);

            Intent intent = ChatDetailActivity.getIntent(getActivity(), discussion.mDiscussionId);
            intent.putExtra(ChatDetailFragment.RETURN_BACK, true);
            startActivity(intent);

        });


    }


    @Override
    public Loader<List<Discussion>> onCreateLoader(int id, Bundle args) {
        return new DiscussionListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Discussion>> loader, List<Discussion> data) {

        refreshListUI(data);

        DiscussionManager.getInstance().setDiscussions(data);

    }

    private void refreshListUI(List<Discussion> data) {

        mDiscussionListAdapter.clear();
        mDiscussionListAdapter.addAll(data);
        mDiscussionListAdapter.notifyDataSetChanged();

        if (ListUtil.isEmpty(data)) {
            mVNoDiscussions.setVisibility(View.VISIBLE);
            mLvDiscussion.setVisibility(View.GONE);
        } else {
            mVNoDiscussions.setVisibility(View.GONE);
            mLvDiscussion.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Discussion>> loader) {

    }

    @Override
    protected boolean onBackPressed() {
        finish();
        return false;
    }



    @Override
    public void selectContact(ShowListItem contactSelect) {
        if(null != mDiscussionList) {
            for(Discussion discussion : mDiscussionList) {
                if(discussion.getId().equals(contactSelect.getId())) {
                    discussion.mSelect = true;
                    break;
                }
            }

            mDiscussionListAdapter.notifyDataSetChanged();
        }


    }


    @Override
    public void unSelectedContact(ShowListItem contactUnSelect) {
        if(null != mDiscussionList) {
            for(Discussion discussion : mDiscussionList) {
                if(discussion.getId().equals(contactUnSelect.getId())) {
                    discussion.mSelect = false;
                    break;
                }
            }

            mDiscussionListAdapter.notifyDataSetChanged();
        }

    }


    @Override
    public void selectContactList(List<? extends ShowListItem> contactList) {

    }


    @Override
    public void unSelectedContactList(List<? extends ShowListItem> contactList) {

    }
}
