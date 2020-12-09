package com.foreveross.atwork.modules.voip.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.WhiteClickRecycleView;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.user.SelectedContactList;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.voip.MeetingInfo;
import com.foreveross.atwork.infrastructure.model.voip.VoipType;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.support.VoipSdkType;
import com.foreveross.atwork.infrastructure.utils.ContactHelper;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.manager.OnlineManager;
import com.foreveross.atwork.modules.discussion.activity.DiscussionMemberSelectActivity;
import com.foreveross.atwork.modules.group.activity.UserSelectActivity;
import com.foreveross.atwork.modules.discussion.model.DiscussionMemberSelectControlAction;
import com.foreveross.atwork.modules.group.module.UserSelectControlAction;
import com.foreveross.atwork.modules.voip.activity.VoipSelectModeActivity;
import com.foreveross.atwork.modules.voip.adapter.VoipSelectRecycleAdapter;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.services.ImSocketService;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ContactQueryHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dasunsy on 16/7/11.
 */
public class VoipSelectModeFragment extends BackHandledFragment {

    public static final String TAG = VoipSelectModeFragment.class.getSimpleName();

    public static final String ACTION_FINISH_ACTIVITY = "action_finish_activity";

    public static final int DATA_REQUEST_ADD_VOIP_MEMBER = 1;

    private TextView mTvCancel;
    private TextView mTvSelectUserSumStatus;
    private TextView mTvSelectedUserSum;
    private WhiteClickRecycleView mRvSelectAvatar;
    private ImageView mIvAudioMode;
    private ImageView mIvVideoMode;

    private VoipSelectRecycleAdapter mRecycleAdapter;
    private ArrayList<ShowListItem> mContactSelectedList = new ArrayList<>();
    private MeetingInfo mMeetingInfo;

    private String mDiscussionOrgCode;

    private BroadcastReceiver mFinishBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_FINISH_ACTIVITY.equals(intent.getAction())) {
                Activity activity = getActivity();
                if (null != activity) {
                    activity.finish();

                }
            }
        }
    };


    private VoipSelectRecycleAdapter.OnHandleClickAddOrRemoveUserListener mOnHandleClickAddOrRemoveUserListener = new VoipSelectRecycleAdapter.OnHandleClickAddOrRemoveUserListener() {
        @Override
        public void onClickAdd() {
            Intent intent;
            if (null != mMeetingInfo && MeetingInfo.Type.DISCUSSION.equals(mMeetingInfo.mType)) {
                DiscussionMemberSelectControlAction discussionMemberSelectControlAction = new DiscussionMemberSelectControlAction();
                discussionMemberSelectControlAction.setSelectedContacts(mContactSelectedList);
                discussionMemberSelectControlAction.setDiscussionId(mMeetingInfo.mId);
                discussionMemberSelectControlAction.setSelectMode(DiscussionMemberSelectActivity.Mode.VOIP);

                intent = DiscussionMemberSelectActivity.getIntent(getActivity(), discussionMemberSelectControlAction);

            } else {
                UserSelectControlAction userSelectControlAction = new UserSelectControlAction();
                userSelectControlAction.setSelectMode(UserSelectActivity.SelectMode.SELECT);
                userSelectControlAction.setSelectAction(UserSelectActivity.SelectAction.VOIP);
                userSelectControlAction.setSelectedContacts(mContactSelectedList);
                userSelectControlAction.setFromTag(TAG);

                intent = UserSelectActivity.getIntent(getActivity(), userSelectControlAction);

            }
            startActivityForResult(intent, DATA_REQUEST_ADD_VOIP_MEMBER);
        }

        @Override
        public void onClickRemoveUser(int position) {
            if (isAdded()) {
                ShowListItem userSelected = mContactSelectedList.get(position);

                if (!User.isYou(getActivity(), userSelected.getId())) {
                    mContactSelectedList.remove(position);
                    refreshUI();
                }
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_FINISH_ACTIVITY);

        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mFinishBroadcastReceiver, filter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voip_selectmode, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        registerListener();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadContacts();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mFinishBroadcastReceiver);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (DATA_REQUEST_ADD_VOIP_MEMBER == requestCode) {
            addVoipConfMemberOnResult(resultCode, data);
        }

    }

    private void loadContacts() {
        final List<String> userIdList = ContactHelper.toIdList(mContactSelectedList);

        ContactQueryHelper.queryContactsWithParticipant(getActivity(), userIdList, mDiscussionOrgCode, contacts -> {
            mContactSelectedList.clear();
            mContactSelectedList.addAll(contacts);

            sort();

            // Grid布局
            GridLayoutManager gridLayoutMgr = new GridLayoutManager(getActivity(), 4);
            mRecycleAdapter = new VoipSelectRecycleAdapter(getActivity(), mContactSelectedList, mOnHandleClickAddOrRemoveUserListener);

            mRvSelectAvatar.setLayoutManager(gridLayoutMgr);
            mRvSelectAvatar.setAdapter(mRecycleAdapter);

            refreshUI();

            OnlineManager.getInstance().checkOnlineList(getActivity(), userIdList, onlineList -> {
                mRecycleAdapter.notifyDataSetChanged();
            });
        });

    }

    public static void finishActivity() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(ACTION_FINISH_ACTIVITY));
    }


    private void addVoipConfMemberOnResult(int resultCode, Intent data) {
        if (null != getActivity() && resultCode == Activity.RESULT_OK) {
            List<ShowListItem> contactList = SelectedContactList.getContactList();
            mContactSelectedList.addAll(contactList);

            refreshUI();
        }

    }

    @Override
    protected void findViews(View view) {
        mTvCancel = view.findViewById(R.id.tv_cancel);
        mTvSelectUserSumStatus = view.findViewById(R.id.tv_select_sum);
        mTvSelectedUserSum = view.findViewById(R.id.tv_sum);
        mRvSelectAvatar = view.findViewById(R.id.rv_select_avatar);
        mIvAudioMode = view.findViewById(R.id.iv_select_audio);
        mIvVideoMode = view.findViewById(R.id.iv_select_video);
    }

    private void registerListener() {
        mTvCancel.setOnClickListener((v) -> {
            finish();
        });


        mRvSelectAvatar.setOnTouchInvalidPositionListener(motionEvent -> {
            mRecycleAdapter.mRemoveMode = false;
            mRecycleAdapter.notifyDataSetChanged();

            return false;
        });

        mIvAudioMode.setOnClickListener((v) -> {
            if (AtworkUtil.isSystemCalling()) {
                AtworkToast.showResToast(R.string.alert_is_handling_system_call);
                return;
            }

            if (VoipHelper.isHandlingVoipCall()) {
                AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);
            } else {
                PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(VoipSelectModeFragment.this, new String[]{Manifest.permission.RECORD_AUDIO}, new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        if(AtworkConfig.VOIP_SDK_TYPE == VoipSdkType.QSY) {

                            //全时云版本目前如果进入到会议里再动态申请摄像头权限, 会出现不了画面
                            PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(VoipSelectModeFragment.this, new String[]{Manifest.permission.CAMERA}, new PermissionsResultAction() {
                                @Override
                                public void onGranted() {
                                    startMeeting(VoipType.VOICE);

                                }

                                @Override
                                public void onDenied(String permission) {
                                    AtworkUtil.popAuthSettingAlert(getContext(), permission);

                                }
                            });


                        } else {
                            startMeeting(VoipType.VOICE);

                        }

                    }

                    @Override
                    public void onDenied(String permission) {
                        AtworkUtil.popAuthSettingAlert(getContext(), permission);

                    }
                });
            }

        });


        mIvVideoMode.setOnClickListener((v -> {
            if (AtworkUtil.isSystemCalling()) {
                AtworkToast.showResToast(R.string.alert_is_handling_system_call);
                return;
            }

            if (VoipHelper.isHandlingVoipCall()) {
                AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);

            } else {
                PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(VoipSelectModeFragment.this, new String[]{Manifest.permission.CAMERA}, new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(VoipSelectModeFragment.this, new String[]{Manifest.permission.RECORD_AUDIO}, new PermissionsResultAction() {
                            @Override
                            public void onGranted() {
                                startMeeting(VoipType.VIDEO);

                            }

                            @Override
                            public void onDenied(String permission) {
                                AtworkUtil.popAuthSettingAlert(getContext(), permission);

                            }
                        });

                    }

                    @Override
                    public void onDenied(String permission) {
                        AtworkUtil.popAuthSettingAlert(getContext(), permission);
                    }
                });

            }


        }));
    }

    private void startMeeting(VoipType type) {
        if (null == mMeetingInfo) {
            mMeetingInfo = new MeetingInfo();

            if (2 == mContactSelectedList.size()) {
                ShowListItem peer = VoipHelper.getPeer(getActivity(), mContactSelectedList);
                if (null != peer) {
                    mMeetingInfo.mType = MeetingInfo.Type.USER;
                    mMeetingInfo.mId = peer.getId();

                }
            } else {
                mMeetingInfo.mType = MeetingInfo.Type.MULTI;

            }
        } else if (MeetingInfo.Type.USER.equals(mMeetingInfo.mType)) {
            //大于2个人时, 转换类型为MULTI
            if (2 < mContactSelectedList.size()) {
                mMeetingInfo.mType = MeetingInfo.Type.MULTI;
            }
        }

        boolean isGroupType = (null == mMeetingInfo || !MeetingInfo.Type.USER.equals(mMeetingInfo.mType));

        if (null != mMeetingInfo) {
            mMeetingInfo.mOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(getActivity());
        }

        //语音通话前检查 im, 保证语音通话收到消息正常
        ImSocketService.checkConnection(BaseApplicationLike.baseContext);

        VoipHelper.goToCallActivity(getActivity(), mMeetingInfo, type, isGroupType, mContactSelectedList, true, null, null, null, ContactHelper.findLoginUserHandleInfo(getActivity(), mContactSelectedList));
        //暂时无论是否接听都关掉该界面
        finishActivity();

    }

    private void initData() {
        mContactSelectedList = getArguments().getParcelableArrayList(VoipSelectModeActivity.DATA_USER_SELECTED);
        mMeetingInfo = getArguments().getParcelable(VoipSelectModeActivity.DATA_SESSION_INFO);
        mDiscussionOrgCode = getArguments().getString(VoipSelectModeActivity.DATA_DISCUSSION_ORG_CODE);
    }

    private void refreshUI() {
        if (AtworkConfig.VOIP_MEMBER_COUNT_MAX == mContactSelectedList.size()) {
            mRecycleAdapter.setMode(VoipSelectRecycleAdapter.Mode.CAN_ONLY_REMOVE);

        } else if (2 == mContactSelectedList.size()) {
            mRecycleAdapter.setMode(VoipSelectRecycleAdapter.Mode.CAN_ONLY_ADD);
            mRecycleAdapter.mRemoveMode = false;// 只能"添加"的模式里, 去除 removeMode

        } else {
            mRecycleAdapter.setMode(VoipSelectRecycleAdapter.Mode.CAN_ADD_AND_REMOVE);
        }

        mRecycleAdapter.notifyDataSetChanged();
        mTvSelectUserSumStatus.setText(mContactSelectedList.size() + "/" + AtworkConfig.VOIP_MEMBER_COUNT_MAX);
        mTvSelectedUserSum.setText(getString(R.string.person, mContactSelectedList.size() + ""));
    }

    /**
     * 排序处理, "我"排在第一
     */
    private void sort() {
        if (!ListUtil.isEmpty(mContactSelectedList)) {
            ShowListItem meContact = null;
            for (ShowListItem contact : mContactSelectedList) {
                if (User.isYou(getActivity(), contact.getId())) {
                    meContact = contact;
                    break;
                }
            }

            if (null != meContact) {
                mContactSelectedList.remove(meContact);
                mContactSelectedList.add(0, meContact);
            }
        }
    }


    @Override
    protected boolean onBackPressed() {
        if (mRecycleAdapter.mRemoveMode) {
            mRecycleAdapter.mRemoveMode = false;
            mRecycleAdapter.notifyDataSetChanged();

        } else {
            finish();

        }
        return false;
    }

}
