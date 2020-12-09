package com.foreveross.atwork.modules.discussion.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.discussion.DiscussionAsyncNetService;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.db.daoService.DiscussionDaoService;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.discussion.DiscussionMember;
import com.foreveross.atwork.infrastructure.model.user.SelectedContactList;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.support.AtworkConstants;
import com.foreveross.atwork.infrastructure.utils.ContactHelper;
import com.foreveross.atwork.infrastructure.utils.FirstLetterUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.listener.TextWatcherAdapter;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.manager.OnlineManager;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.contact.adapter.ContactListArrayListAdapter;
import com.foreveross.atwork.modules.discussion.activity.DiscussionMemberSelectActivity;
import com.foreveross.atwork.modules.group.activity.UserSelectActivity;
import com.foreveross.atwork.modules.group.adaptar.HorizontalListViewAdapter;
import com.foreveross.atwork.modules.discussion.component.AtAllDiscussionMembersView;
import com.foreveross.atwork.modules.group.component.HorizontalListView;
import com.foreveross.atwork.modules.group.component.SelectContactHeadItemView;
import com.foreveross.atwork.modules.discussion.model.DiscussionMemberSelectControlAction;
import com.foreveross.atwork.modules.search.component.SearchHeadView;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ErrorHandleUtil;

import java.util.ArrayList;
import java.util.List;


public class DiscussionMemberSelectFragment extends BackHandledFragment implements SelectContactHeadItemView.RemoveContactListener {

    private static final String TAG = DiscussionMemberSelectFragment.class.getSimpleName();

    //标题栏
    private TextView titleView;

    //退回
    private View mBackView;

    private ListView mDiscussionAtListView;

    private HorizontalListView mHorizontalListView;

    private SearchHeadView mSearchHeadView;

    private AtAllDiscussionMembersView mAtAllDiscussionMembersView;

    private Button mOkButton;

    private Discussion mDiscussion;

    private TextView mSelectAllButton;

    private ContactListArrayListAdapter mContactListAdapter;

    private HorizontalListViewAdapter mHorizontalListViewAdapter;

    private List<ShowListItem> mSelectedContactList = new ArrayList<>();

    private List<ShowListItem> mNotAllowedSelectedContactList = new ArrayList<>();

    private ProgressDialogHelper mProgressDialogHelper;

    private String mDiscussionId;

    private int mSelectMode;

    private boolean mSelectedIsAllowedRemoved;

    private int mDisplayMode;

    private boolean mFilterMe;

    private DiscussionMemberSelectControlAction mDiscussionMemberSelectControlAction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_at, container, false);


        return view;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerListener();
        initData();
    }

    @Override
    protected void findViews(View view) {
        mBackView = view.findViewById(R.id.discussion_at_back);
        titleView = view.findViewById(R.id.discussion_at_title);
        mHorizontalListView = view.findViewById(R.id.discussion_at_select_head);
        mDiscussionAtListView = view.findViewById(R.id.discussion_at_list_view);
        mOkButton = view.findViewById(R.id.discussion_at_ok);
        mSelectAllButton = view.findViewById(R.id.discussion_at_select_all);
        mSearchHeadView = new SearchHeadView(getActivity());
        mAtAllDiscussionMembersView = new AtAllDiscussionMembersView(mActivity);

        mSearchHeadView.clearFocusableInTouchMode();
        mSearchHeadView.setHint(R.string.search_action);
    }

    private void initData() {

//        if (mSelectedContactList.size()==0){
//            mSelectAllButton.setTextColor(getResources().getColor(R.color.transparet_50));
//        }
        mHorizontalListViewAdapter = new HorizontalListViewAdapter(getActivity(), this);
        mHorizontalListView.setAdapter(mHorizontalListViewAdapter);


        mDiscussionAtListView.setAdapter(null);
        mDiscussionAtListView.addHeaderView(mSearchHeadView);

        mContactListAdapter = new ContactListArrayListAdapter(getActivity(), true);
        mDiscussionAtListView.setAdapter(mContactListAdapter);

        if (getArguments() != null) {

            mDiscussionMemberSelectControlAction = getArguments().getParcelable(DiscussionMemberSelectActivity.DATA_DISCUSSION_MEMBER_SELECT_CONTROL_ACTION);

            if(null != mDiscussionMemberSelectControlAction) {
                mDiscussionId = mDiscussionMemberSelectControlAction.getDiscussionId();
                mSelectMode = mDiscussionMemberSelectControlAction.getSelectMode();
                mSelectedIsAllowedRemoved = mDiscussionMemberSelectControlAction.isSelectedAllowedRemove();
                mDisplayMode = mDiscussionMemberSelectControlAction.getDisplayMode();
                mFilterMe = mDiscussionMemberSelectControlAction.getFilterMe();

                mContactListAdapter.setDisplayMode(mDisplayMode);
            }

            initDiscussion(mDiscussionId);

            if(DiscussionMemberSelectActivity.Mode.AT == mSelectMode) {
                mDiscussionAtListView.addHeaderView(mAtAllDiscussionMembersView);

            }

        }

        if(!ListUtil.isEmpty(SelectedContactList.getContactList())) {
            if (mSelectedIsAllowedRemoved) {
                mSelectedContactList.addAll(SelectedContactList.getContactList());
            } else {
                mNotAllowedSelectedContactList.addAll(SelectedContactList.getContactList());

            }

        }


        if(DiscussionMemberSelectActivity.Mode.SINGLE == mSelectMode) {
            mSelectAllButton.setVisibility(View.GONE);
        }

        if(DiscussionMemberSelectActivity.Mode.SELECT == mSelectMode || DiscussionMemberSelectActivity.Mode.VOIP == mSelectMode) {
            mContactListAdapter.setNotAllowedSelectedContacts(ContactHelper.toIdList(mNotAllowedSelectedContactList));
        }
    }

    private void initSelectStatus() {
        for (ShowListItem contact : mDiscussion.mMemberContactList) {
            if (mFilterMe && contact.getId().equals(LoginUserInfo.getInstance().getLoginUserId(mActivity))) {
                continue;
            }

            if(mSelectedContactList.contains(contact)) {
                contact.select(true);
            }


        }
    }

    /**
     * 初始化群组信息
     */
    private void initDiscussion(final String discussionId) {
        mProgressDialogHelper = new ProgressDialogHelper(getActivity());
        mProgressDialogHelper.show(false);

        boolean forceNeedEmpInfo = false;
        if(ContactListArrayListAdapter.MODE_JOB_INFO == mDisplayMode) {
            forceNeedEmpInfo = true;
        }
        DiscussionManager.getInstance().queryDiscussion(getActivity(), discussionId, true, forceNeedEmpInfo, new DiscussionAsyncNetService.OnQueryDiscussionListener() {
            @Override
            public void networkFail(int errorCode, String errorMsg) {
                if (mProgressDialogHelper != null) {
                    mProgressDialogHelper.dismiss();
                }

                if (AtworkConstants.USER_NOT_FOUND_IN_DISCUSSION == errorCode) {

                    ChatSessionDataWrap.getInstance().removeSessionSafely(discussionId);
                    DiscussionDaoService.getInstance().removeDiscussion(discussionId);
                    showKickDialog(R.string.discussion_not_found);


                } else {
                    ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Group, errorCode, errorMsg);
                }

            }

            @Override
            public void onSuccess(@NonNull Discussion discussion) {
                mDiscussion = discussion;

                mContactListAdapter.setDiscussionId(discussionId);
                initSelectStatus();

                refreshView(true);

                checkMemberListOnline();


                mProgressDialogHelper.dismiss();

            }


        });


    }

    private void checkMemberListOnline() {
        if (DomainSettingsManager.getInstance().handleUserOnlineFeature()) {
            List<String> checkOnlineList = new ArrayList<>();
            for (DiscussionMember discussionMember : mDiscussion.mMemberList) {
                checkOnlineList.add(discussionMember.userId);
            }
            checkOnline(checkOnlineList);
        }
    }

    private void checkOnline(List<String> checkOnlineList) {
        OnlineManager.getInstance().checkOnlineList(mActivity, checkOnlineList, onlineList -> {
            if (mContactListAdapter != null) {
                mContactListAdapter.notifyDataSetChanged();
            }
        });
    }


    private void registerListener() {

        //滑动隐藏键盘
        mDiscussionAtListView.setOnTouchListener((v, event) -> {
            AtworkUtil.hideInput(getActivity(), mSearchHeadView.getEditTextSearch());
            return false;
        });

        mHorizontalListView.setOnItemClickListener((parent, view, position, id) -> removeContact(mSelectedContactList.get(position)));

        mSelectAllButton.setOnClickListener(v -> {

            if (mSelectedContactList.size() == mDiscussion.mMemberList.size() - 1) {
                for (ShowListItem contact : mDiscussion.mMemberContactList) {
                    if (mFilterMe && contact.getId().equals(LoginUserInfo.getInstance().getLoginUserId(mActivity))) {
                        continue;
                    }
                    if (contact.isSelect()) {
                        contact.select(false);
                        mSelectedContactList.remove(contact);
                    }
                }
                refreshView(true);
                return;
            }

            //去掉自己
            int count = mDiscussion.mMemberList.size() - 1;
            if(isToThreshold(getRealSelectedCount(count))) {
                toastSelectMax();

            } else {
                selectAllMembers();

            }


            refreshView(true);
        });

        //返回事件
        mBackView.setOnClickListener(v -> {
            SelectedContactList.setContactList(new ArrayList<>());
            finish();
        });

        mDiscussionAtListView.setOnItemClickListener((parent, view, position, id) -> {
            ShowListItem contact = (ShowListItem) parent.getItemAtPosition(position);

            if (!mNotAllowedSelectedContactList.contains(contact)) {

                //选中前需要预判断是否已经满人了
                if(!contact.isSelect() && isToThreshold(getRealSelectedCount(mSelectedContactList.size()) + 1)) {
                    toastSelectMax();

                } else {
                    contact.select(!contact.isSelect());
                    if (contact.isSelect()) {
                        mSelectedContactList.add(contact);
                    } else {
                        mSelectedContactList.remove(contact);
                    }
                    refreshView(contact.isSelect());

                    mSearchHeadView.getEditTextSearch().setText("");   //清空搜索栏
                }
            }

        });

        if (DiscussionMemberSelectActivity.Mode.AT == mSelectMode) {
            mAtAllDiscussionMembersView.setOnClickListener(v -> {
                selectAllMembers();
                submit();
            });
        }


        //确定事件
        mOkButton.setOnClickListener(v -> {
            if (mSelectedContactList.size() == 0) {
                AtworkToast.showToast(getResources().getString(R.string.select_user_zero));
                return;
            }

            submit();
        });


        mSearchHeadView.getEditTextSearch().addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (StringUtils.isEmpty(s.toString())) {
                    refreshView(true);

                    mSearchHeadView.getImageViewClearSearch().setVisibility(View.GONE);
                } else {
                    List<ShowListItem> contacts = searchForKey(s.toString());
                    mContactListAdapter.clear();
                    mContactListAdapter.addAll(contacts);

                    mSearchHeadView.getImageViewClearSearch().setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void toastSelectMax() {
        int max = 1;

        if(DiscussionMemberSelectActivity.Mode.VOIP == mSelectMode) {
            max = AtworkConfig.VOIP_MEMBER_COUNT_MAX - mNotAllowedSelectedContactList.size();
        }

        VoipHelper.toastSelectMax(getActivity(), max);
    }

    /**
     * 因为群成员列表过滤了自己, 所以在所选人的数量上需要 + 1, 以此加回自己或者加上 传进来
     * */
    private int getRealSelectedCount(int count) {
        int realTotal;
        if(!ListUtil.isEmpty(mNotAllowedSelectedContactList)) {
            boolean notAllowListHasMe = false;
            for(ShowListItem contact : mNotAllowedSelectedContactList) {
                if(User.isYou(getActivity(), contact.getId())) {
                    notAllowListHasMe = true;
                    break;
                }
            }

            if(notAllowListHasMe) {
                realTotal = mNotAllowedSelectedContactList.size() + count;
            } else {
                realTotal = mNotAllowedSelectedContactList.size() + count + 1;

            }


        } else {
            realTotal = count;
        }

        return realTotal;
    }


    /**
     * 全选所有人员
     */
    private void selectAllMembers() {

        if (mDiscussion == null) {
            return;
        }
        for (ShowListItem contact : mDiscussion.mMemberContactList) {
            if (mFilterMe && contact.getId().equals(LoginUserInfo.getInstance().getLoginUserId(mActivity))) {
                continue;
            }
            if (!contact.isSelect()) {
                contact.select(true);
                mSelectedContactList.add(contact);
            }
        }
    }

    private boolean isToThreshold(int count) {
        boolean isToThreshold = false;

        if(DiscussionMemberSelectActivity.Mode.VOIP == mSelectMode && AtworkConfig.VOIP_MEMBER_COUNT_MAX < count) {
            isToThreshold = true;

        } else if(DiscussionMemberSelectActivity.Mode.SINGLE == mSelectMode && 1 < count){
            isToThreshold = true;
        }

        return isToThreshold;
    }


    private void submit() {
        Intent returnIntent = new Intent();
        SelectedContactList.setContactList(mSelectedContactList);
        getActivity().setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }


    private List<ShowListItem> searchForKey(String key) {
        List<ShowListItem> contacts = new ArrayList<>();
        key = key.toLowerCase();
        for (ShowListItem contact : mDiscussion.mMemberContactList) {
            if (mFilterMe && contact.getId().equals(LoginUserInfo.getInstance().getLoginUserId(mActivity))) {
                continue;
            }
            //先判断首字母
            if (!StringUtils.isEmpty(contact.getTitle())) {
                String firstLetterStr = FirstLetterUtil.getFullLetter(contact.getTitle());
                if (firstLetterStr.contains(key)) {
                    contacts.add(contact);
                    continue;
                }
            }

            //再判断username
            String username = ContactHelper.getUserName(contact);
            if ((!StringUtils.isEmpty(username) && username.contains(key))
                    || (!StringUtils.isEmpty(contact.getTitlePinyin()) && contact.getTitlePinyin().contains(key))
                    || (!StringUtils.isEmpty(contact.getTitle()) && contact.getTitle().contains(key))
                    ) {
                contacts.add(contact);
            }
        }
        return contacts;
    }


    /**
     * 刷新头像选择显示栏
     *
     * @param isSelected 若是勾选, 则该栏目滑动到末端, 否则不做处理
     */
    private void refreshView(boolean isSelected) {

//        //判断选择列表里是否有点击选择，若没有则改变“全选”按钮颜色
//        if (mSelectedContactList.size()==0){
//            mSelectAllButton.setTextColor(getResources().getColor(R.color.transparet_50));
//        }

        mContactListAdapter.clear();

        List<ShowListItem> contacts = new ArrayList<>();
        for (ShowListItem contact : mDiscussion.mMemberContactList) {
            if (mFilterMe && contact.getId().equals(LoginUserInfo.getInstance().getLoginUserId(mActivity))) {
                continue;
            }

            if(mNotAllowedSelectedContactList.contains(contact)) {
                contact.select(true);
            }



            contacts.add(contact);
        }
        mContactListAdapter.clear();
        mContactListAdapter.addAll(contacts);

        mHorizontalListViewAdapter.clear();
        mHorizontalListViewAdapter.addAll(mSelectedContactList);

        if (isSelected) {
            mHorizontalListView.setLastSection();
        }
    }


    @Override
    public void removeContact(ShowListItem contact) {
        mSelectedContactList.remove(contact);
        contact.select(false);
        refreshView(false);
    }

    @Override
    protected boolean onBackPressed() {
        finish();
        return false;
    }


}
