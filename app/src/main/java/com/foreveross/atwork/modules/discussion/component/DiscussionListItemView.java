package com.foreveross.atwork.modules.discussion.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.discussion.DiscussionAsyncNetService;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.modules.discussion.util.DiscussionUIHelper;
import com.foreveross.atwork.modules.discussion.util.LabelViewWrapper;
import com.foreveross.atwork.modules.group.activity.UserSelectActivity;
import com.foreveross.atwork.utils.AvatarHelper;
import com.foreveross.atwork.utils.ErrorHandleUtil;

import kotlin.Unit;


public class DiscussionListItemView extends RelativeLayout {
    private ImageView mIvSelect;
    private ImageView mIvAvatarView;
    private TextView mTvNameView;
    private ImageView mIvArrow;

    private FrameLayout mFlLabel;
    private TextView mTvLabel;
    private ImageView mIvLabel;

    private UserSelectActivity.SelectMode mSelectMode;
    private boolean mSingleSelect;

    public DiscussionListItemView(Context context) {
        super(context);
        initView();
    }


    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.component_discussion_list_item_v2, this);
        mIvSelect = view.findViewById(R.id.iv_select);
        mIvAvatarView = view.findViewById(R.id.discussion_list_head_avatar);
        mTvNameView = view.findViewById(R.id.discussion_list_head_title);
        mIvArrow = view.findViewById(R.id.arrow_image);

        mFlLabel = view.findViewById(R.id.fl_discussion_label_in_basic_info_area);
        mTvLabel = view.findViewById(R.id.tv_discussion_label_in_basic_info_area);
        mIvLabel = view.findViewById(R.id.iv_discussion_label_in_basic_info_area);

    }

    public void setSelectMode(UserSelectActivity.SelectMode selectMode) {
        mSelectMode = selectMode;
    }


    public void setSingleSelect(boolean singleSelect) {
        mSingleSelect = singleSelect;
    }

    public void refreshView(final Discussion discussion) {
        if(UserSelectActivity.SelectMode.SELECT == mSelectMode) {

            if (discussion.isSelect()) {
                mIvSelect.setImageResource(R.mipmap.icon_selected);
            } else {
                mIvSelect.setImageResource(R.mipmap.icon_seclect_no_circular);
            }

            if (mSingleSelect) {
                mIvSelect.setVisibility(GONE);
            } else {
                mIvSelect.setVisibility(VISIBLE);

            }

            mIvArrow.setVisibility(GONE);


        } else {
            mIvSelect.setVisibility(GONE);
            mIvArrow.setVisibility(VISIBLE);

        }

        mTvNameView.setText(discussion.getTitle());

        setDiscussionLabel(discussion);

        AvatarHelper.setDiscussionAvatarById(mIvAvatarView, discussion.mDiscussionId, true, true);
    }


    public void setDiscussionLabel(Discussion discussion) {
        String tag = discussion.getId();
        mFlLabel.setTag(tag);

        DiscussionManager.getInstance().queryDiscussion(getContext(), discussion.getId(), new DiscussionAsyncNetService.OnQueryDiscussionListener() {
            @Override
            public void onSuccess(@NonNull Discussion discussion) {

                if(tag.equals(mFlLabel.getTag())) {
                    DiscussionUIHelper.refreshLabel(discussion, new LabelViewWrapper(mFlLabel, mIvLabel, mTvLabel), null);
                }
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {

                if(tag.equals(mFlLabel.getTag())) {
                    hideLabelDiscussion();

                }

                ErrorHandleUtil.handleTokenError(errorCode, errorMsg);

            }
        });
    }


    public void hideLabelDiscussion() {
        mFlLabel.setVisibility(GONE);
    }


}
