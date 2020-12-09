package com.foreveross.atwork.modules.friend.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.component.sortlistview.SideBar;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.user.SelectedContactList;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.manager.FriendManager;
import com.foreveross.atwork.manager.OnlineManager;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.contact.activity.PersonalInfoActivity;
import com.foreveross.atwork.modules.friend.adapter.FriendListAdapter;
import com.foreveross.atwork.modules.friend.loader.FriendListLoader;
import com.foreveross.atwork.modules.friend.utils.FriendLetterListHelper;
import com.foreveross.atwork.modules.group.activity.UserSelectActivity;
import com.foreveross.atwork.modules.group.listener.SelectedChangedListener;
import com.foreveross.atwork.modules.group.module.UserSelectControlAction;
import com.foreveross.atwork.support.BackHandledFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dasunsy on 16/5/19.
 */
public class MyFriendsFragment extends BackHandledFragment implements LoaderManager.LoaderCallbacks<List<User>>, SelectedChangedListener {

    private static final String TAG = MyFriendsFragment.class.getSimpleName();

    private FriendListAdapter mFriendListAdapter;
    private ListView mLwFriend;

    private ImageView mIvBack;

    private TextView mTvNewFri;

    private TextView mTvTitle;

    private View mViewTitleBar;

    private UserSelectControlAction mUserSelectControlAction;

    private UserSelectActivity.SelectMode mSelectMode = UserSelectActivity.SelectMode.NO_SELECT;

    private UserSelectActivity.SelectAction mSelectAction;

    private List<User> mFriendList = new ArrayList<>();

    private List<String> mSelectedContacts = new ArrayList<>();
    private List<String> mNotAllowedSelectedContacts = new ArrayList<>();

    private RelativeLayout mRlHavingFriends;
    private TextView mTvNoFriends;
    private SideBar mSideBar;
    private TextView mDialog;

    private boolean mIsSingleContact = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_friends, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();

        initData();

        List<User> friendListNotCheck = FriendManager.getInstance().getFriendListNotCheck();
        if(null != friendListNotCheck) {
            mFriendList = friendListNotCheck;
            refreshListUI(mFriendList);
        }

        registerListener();

    }

    @Override
    public void onStart() {
        super.onStart();

        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void findViews(View view) {
        mLwFriend = view.findViewById(R.id.lw_items);
        mIvBack = view.findViewById(R.id.title_bar_common_back);
        mViewTitleBar = view.findViewById(R.id.friends_title);
        mTvNewFri = view.findViewById(R.id.title_bar_common_right_text);
        mTvTitle = view.findViewById(R.id.title_bar_common_title);
        mRlHavingFriends = view.findViewById(R.id.rl_having_friends);
        mTvNoFriends = view.findViewById(R.id.tv_no_friends);
        mSideBar = view.findViewById(R.id.sidebar);
        mDialog = view.findViewById(R.id.dialog);
        mSideBar.setTextView(mDialog);
    }



    private void initViews() {
        //取消默认的分割线
//        mLvOrgApplying.setDivider(null);

        mTvTitle.setText(getResources().getString(R.string.workplus_friends));
        mTvNewFri.setText(R.string.new_friend_in_btn);
        mTvNewFri.setTextColor(ContextCompat.getColor(getActivity(), R.color.common_item_black));
        mTvNewFri.setVisibility(View.VISIBLE);

    }

    private void registerListener() {
        mIvBack.setOnClickListener(v -> {
            onBackPressed();
        });

        mTvNewFri.setOnClickListener(v -> {
            String url = UrlConstantManager.getInstance().getFriendApproval();
            WebViewControlAction webViewControlAction = WebViewControlAction.newAction().setUrl(url).setTitle(getString(R.string.new_friend_in_btn)).setNeedShare(false);
            Intent intent = WebViewActivity.getIntent(mActivity, webViewControlAction);
            startActivity(intent);
        });

        if (!selectOrSendMode()) {
            mLwFriend.setOnItemClickListener((parent, view, position, id) -> {
                ShowListItem showListItem = (ShowListItem) parent.getItemAtPosition(position);
                if(showListItem instanceof User) {
                    final User user = (User) showListItem;
                    Intent intent = PersonalInfoActivity.getIntent(getActivity(), user);
                    startActivity(intent);

                }

            });

        }


        if (selectOrSendMode()) {
            mLwFriend.setOnItemClickListener((parent, view, position, id) -> {
                ShowListItem showListItem = (ShowListItem) parent.getItemAtPosition(position);

                if (!(showListItem instanceof User)) {
                    return;
                }

                final User user = (User) showListItem;

                if (getActivity() instanceof UserSelectActivity) {
                    UserSelectActivity userSelectActivity = (UserSelectActivity) getActivity();
                    //todo cordova 单选的配置

                    if (mIsSingleContact) {
                        user.select();
                        SelectedContactList.setContactList(ListUtil.makeSingleList(user));
                        Intent returnIntent = new Intent();
                        mActivity.setResult(Activity.RESULT_OK, returnIntent);
                        mActivity.finish();
                        return;
                    }


                    if (!userSelectActivity.getNotAllowedSelectedList().contains(user)) {
                        if (!user.mSelect && !userSelectActivity.isAllowed()) {
                            toast(mUserSelectControlAction.getMaxTip());

                            return;
                        }

                        if (!user.mSelect && userSelectActivity.isOneSelected()) {
                            return;
                        }

                        user.select();
                        userSelectActivity.action(user);
                    }
                }

            });

        }

        mSideBar.setOnTouchingLetterChangedListener(letter -> {
            int position = mFriendListAdapter.getPositionForSection(letter.charAt(0));
            if(position != -1){
                mLwFriend.setSelection(position);
            }
        });
    }

    private void initData() {
        if (getArguments() != null) {

            mUserSelectControlAction = getArguments().getParcelable(UserSelectActivity.DATA_USER_SELECT_PARAMS);

            if (null != mUserSelectControlAction) {
                mSelectMode = mUserSelectControlAction.getSelectMode();

                mSelectAction = mUserSelectControlAction.getSelectAction();

                mIsSingleContact = 1 == mUserSelectControlAction.getMax();
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

        mFriendListAdapter = new FriendListAdapter(getActivity(), selectOrSendMode(), mIsSingleContact);
        mLwFriend.setAdapter(mFriendListAdapter);
    }

    private boolean selectOrSendMode() {
        return UserSelectActivity.SelectMode.SELECT == mSelectMode;
    }

    @Override
    protected boolean onBackPressed() {
        finish();
        return false;
    }



    @Override
    public Loader<List<User>> onCreateLoader(int id, Bundle args) {
        return new FriendListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<User>> loader, List<User> data) {
        mFriendList.clear();
        if (!ListUtil.isEmpty(data)) {
            mFriendList.addAll(data);

        }

        FriendManager.getInstance().setFriendsTotally(data);
        refreshListUI(data);
    }

    private void refreshListUI(List<User> data) {
        if(!ListUtil.isEmpty(data)) {
            mRlHavingFriends.setVisibility(View.VISIBLE);
            mTvNoFriends.setVisibility(View.GONE);

            mSideBar.refreshLetters(FriendLetterListHelper.getFirstLetterLinkedSet(data));
            refreshSelectedData(mFriendList);
            mFriendListAdapter.setData(mFriendList);
            mFriendListAdapter.setNotAllowedSelectedContacts(mNotAllowedSelectedContacts);

            mFriendListAdapter.notifyDataSetChanged();
            if (data.isEmpty()) {
                return;
            }
            checkOnlineStatus(data);

        } else {
            mRlHavingFriends.setVisibility(View.GONE);
            mTvNoFriends.setVisibility(View.VISIBLE);
        }
    }

    private void checkOnlineStatus(List<User> userList) {

        List<String> ids = User.toUserIdList(userList);

        OnlineManager.getInstance().checkOnlineList(mActivity, ids, onlineList -> mFriendListAdapter.notifyDataSetChanged());
    }

    public void refreshSelectedData(List<User> data) {
        if (!ListUtil.isEmpty(mSelectedContacts) || !ListUtil.isEmpty(mNotAllowedSelectedContacts)) {
            for(User user : data) {
                if(mSelectedContacts.contains(user.mUserId) || mNotAllowedSelectedContacts.contains(user.mUserId)) {
                    user.mSelect = true;
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<User>> loader) {

    }

    @Override
    public void selectContact(ShowListItem contactSelect) {
        for(User user : mFriendList) {
            if(user.mUserId.equals(contactSelect.getId())) {
                user.mSelect = true;
                break;
            }
        }

        mFriendListAdapter.notifyDataSetChanged();

    }

    @Override
    public void unSelectedContact(ShowListItem contactUnSelect) {
        for(User user : mFriendList) {
            if(user.mUserId.equals(contactUnSelect.getId())) {
                user.mSelect = false;
                break;
            }
        }
        mFriendListAdapter.notifyDataSetChanged();

    }

    @Override
    public void selectContactList(List<? extends ShowListItem> contactList) {

    }

    @Override
    public void unSelectedContactList(List<? extends ShowListItem> contactList) {

    }
}
