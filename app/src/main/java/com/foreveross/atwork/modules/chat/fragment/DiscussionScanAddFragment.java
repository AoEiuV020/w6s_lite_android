package com.foreveross.atwork.modules.chat.fragment;

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

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.discussion.DiscussionAsyncNetService;
import com.foreveross.atwork.api.sdk.qrcode.responseModel.WorkplusQrCodeInfo;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.utils.CommonUtil;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.modules.chat.activity.DiscussionScanAddActivity;
import com.foreveross.atwork.modules.chat.model.EntrySessionRequest;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.atwork.utils.ImageCacheHelper;

/**
 * Created by dasunsy on 16/2/3.
 */
public class DiscussionScanAddFragment extends BackHandledFragment {
    public final static String ACTION_CAN_CLICK = "action_canclick";

    private ImageView mIvBack;
    private TextView mTvTitle;
    private ImageView mIvGroupAvatar;
    private TextView mTvGroupName;
    private TextView mTvJoin;

    private WorkplusQrCodeInfo mWorkplusQrCodeInfo;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mCanClick = true;
        }
    };

    private boolean mCanClick = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_scan_add, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();

        mTvTitle.setText(R.string.join_group);

        if(null != mWorkplusQrCodeInfo) {
            ImageCacheHelper.displayImageByMediaId(mWorkplusQrCodeInfo.avatar, mIvGroupAvatar, ImageCacheHelper.getRoundOptions(R.mipmap.default_discussion_chat));
            mTvGroupName.setText(mWorkplusQrCodeInfo.name);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerListeners();
        registerBroadCast();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBroadCast();
    }

    @Override
    protected void findViews(View view) {
        mIvBack = view.findViewById(R.id.title_bar_common_back);
        mTvTitle = view.findViewById(R.id.title_bar_common_title);
        mIvGroupAvatar = view.findViewById(R.id.iv_discussion_avatar);
        mTvGroupName = view.findViewById(R.id.tv_group_name);
        mTvJoin = view.findViewById(R.id.tv_join);

    }


    private void initData() {
        Bundle bundle = getArguments();
        if(null != bundle) {
            mWorkplusQrCodeInfo = bundle.getParcelable(DiscussionScanAddActivity.DATA_DISCUSSION_SCAN_INFO);
        }
    }
    private void registerListeners() {
        mIvBack.setOnClickListener(v -> onBackPressed());

        mTvJoin.setOnClickListener(v -> {
            if (CommonUtil.isFastClick(3000)) {
                return;
            }

            if(mCanClick) {
                mCanClick = false;

                DiscussionManager.getInstance().inviteToDiscussion(mActivity, mWorkplusQrCodeInfo, new DiscussionAsyncNetService.HandledResultListener() {
                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        LocalBroadcastManager.getInstance(mActivity).sendBroadcast(new Intent(DiscussionScanAddFragment.ACTION_CAN_CLICK));

                        ErrorHandleUtil.handleError(errorCode, errorMsg);

                    }

                    @Override
                    public void success() {
                        EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest()
                                .setChatType(SessionType.Discussion)
                                .setName(mWorkplusQrCodeInfo.name)
                                .setIdentifier(mWorkplusQrCodeInfo.getDiscussionId())
                                .setDomainId(mWorkplusQrCodeInfo.getDomainId());

                        DiscussionManager.getInstance().getChatDetailAct(mActivity, entrySessionRequest);

                    }

                });
            }
        });
    }


    @Override
    protected boolean onBackPressed() {
        finish();
        return false;
    }


    private void registerBroadCast() {
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mReceiver, new IntentFilter(ACTION_CAN_CLICK));
    }

    private void unregisterBroadCast() {
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mReceiver);

    }

}
