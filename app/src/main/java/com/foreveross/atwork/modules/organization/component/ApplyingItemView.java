package com.foreveross.atwork.modules.organization.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.NewMessageTipView;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.manager.model.ApplyingOrganization;
import com.foreveross.atwork.modules.chat.util.SessionRefreshHelper;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.foreveross.atwork.utils.TimeViewUtil;

/**
 * Created by dasunsy on 16/6/14.
 */
public class ApplyingItemView extends RelativeLayout {
    private View mVRoot;
    private ImageView mIvAvatar;
    private TextView mNameView;
    private TextView mTvContent;
    private TextView mTvTime;
    private NewMessageTipView mTipsView;

    public ApplyingItemView(Context context) {
        super(context);
        initViews();
    }

    public ApplyingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item_applying_org, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mIvAvatar = view.findViewById(R.id.chat_item_avatar);
        mNameView = view.findViewById(R.id.chat_item_title);
        mTvContent = view.findViewById(R.id.chat_item_last_message);
        mTvTime = view.findViewById(R.id.chat_item_time);
        mTipsView = view.findViewById(R.id.chat_item_new_messages_count);

    }

    public void refreshView(ApplyingOrganization applyingOrg) {

        ImageCacheHelper.displayImageByMediaId(applyingOrg.mOrgLogo, mIvAvatar, ImageCacheHelper.getOrgLogoOptions());
        mNameView.setText(applyingOrg.getOrgNameI18n(BaseApplicationLike.baseContext));

        if (applyingOrg.mAppliedTime > 0) {
            mTvTime.setText(TimeViewUtil.getSimpleUserCanViewTime(BaseApplicationLike.baseContext , applyingOrg.mAppliedTime));
        } else {
            mTvTime.setText("");
        }
        if (applyingOrg.mContent.equalsIgnoreCase(getContext().getString(R.string.no_applying))) {
            mTvContent.setText(applyingOrg.mContent);
        } else {
            SessionRefreshHelper.setAndHighlightOrgApplyingView(mTvContent, applyingOrg.mContent);
        }


        if (applyingOrg.mUnreadMsgIdList != null && applyingOrg.mUnreadMsgIdList.size() > 0) {
            mTipsView.setVisibility(VISIBLE);
        } else {
            mTipsView.setVisibility(GONE);
        }
        mTipsView.numberModel(applyingOrg.mUnreadMsgIdList.size());


//        String tag = UUID.randomUUID().toString();
//        mTvContent.setTag(tag);
//        UserManager.getInstance().queryUserByUserId(getContext(), applyingOrg.mApplicantId, applyingOrg.mOrg.mDomainId, new UserAsyncNetService.OnQueryUserListener() {
//            @Override
//            public void onSuccess(@NonNull User user) {
//                if(tag.equals(mTvContent.getTag().toString())) {
//                    String content = getContext().getResources().getString(R.string.tip_applying_org, user.getShowName(), applyingOrg.mOrg.mName);
//                    SessionRefreshHelper.setAndHighlightOrgApplyingView(mTvContent, content);
//                }
//
//
//            }
//
//            @Override
//            public void networkFail(int errorCode, String errorMsg) {
//                ErrorHandleUtil.handleTokenError(errorCode);
//            }
//        });

    }
}
