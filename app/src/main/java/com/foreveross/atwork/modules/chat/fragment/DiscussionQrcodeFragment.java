package com.foreveross.atwork.modules.chat.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreverht.workplus.ui.component.dialogFragment.CommonPopSelectData;
import com.foreverht.workplus.ui.component.dialogFragment.W6sSelectDialogFragment;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.qrcode.QrcodeAsyncNetService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.utils.BitmapUtil;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.modules.chat.activity.DiscussionQrcodeActivity;
import com.foreveross.atwork.modules.qrcode.service.QrcodeManager;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AvatarHelper;
import com.foreveross.atwork.utils.ErrorHandleUtil;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by dasunsy on 16/2/3.
 */
public class DiscussionQrcodeFragment extends BackHandledFragment {

    private ImageView mIvBack;
    private TextView mTvTitle;
    private TextView mTvMode;
    private ImageView mIvQrcode;
    private ImageView mIvGroupAvatar;
    private TextView mTvGroupName;
    private TextView mTvExpireTime;

    private Discussion mDiscussion;

    private Bitmap mQrBitmap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_qrcode, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();

        mTvTitle.setText(R.string.group_qrcode_title);
        mTvMode.setText(R.string.more);
        mTvMode.setVisibility(View.VISIBLE);
        mTvMode.setTextColor(getResources().getColor(R.color.common_item_black));

        if (null != mDiscussion) {
            AvatarHelper.setDiscussionAvatarById(mIvGroupAvatar, mDiscussion.mDiscussionId, true, true);
            mTvGroupName.setText(mDiscussion.mName);
        }
    }

    @Override
    protected void findViews(View view) {
        mIvBack = view.findViewById(R.id.title_bar_common_back);
        mTvTitle = view.findViewById(R.id.title_bar_common_title);
        mTvMode = view.findViewById(R.id.title_bar_common_right_text);
        mIvQrcode = view.findViewById(R.id.iv_qrcode);
        mIvGroupAvatar = view.findViewById(R.id.iv_discussion_avatar);
        mTvGroupName = view.findViewById(R.id.tv_group_name);
        mTvExpireTime = view.findViewById(R.id.tv_valid_time);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerListeners();

        QrcodeManager.getInstance().getDiscussionJoinQrcode(mActivity, mDiscussion.mDiscussionId, mDiscussion.mDomainId, new QrcodeAsyncNetService.OnGetQrcodeListener() {
            @Override
            public void success(Bitmap qrcodeBitmap, long effectTime) {
                String showTime = TimeUtil.getStringForMillis(effectTime, TimeUtil.getTimeFormat2(BaseApplicationLike.baseContext));

                mIvQrcode.setImageBitmap(qrcodeBitmap);
                mTvExpireTime.setText(getString(R.string.tip_qrcode_expire, showTime));

                mQrBitmap = qrcodeBitmap;
            }


            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Qrcode, errorCode, errorMsg);

            }
        });
    }



    private void initData() {
        Bundle bundle = getArguments();
        if (null != bundle) {
            mDiscussion = bundle.getParcelable(DiscussionQrcodeActivity.DATA_DISCUSSION);
        }
    }

    private void registerListeners() {
        mIvBack.setOnClickListener(v -> onBackPressed());

        mTvMode.setOnClickListener(v -> {
            if (null == mQrBitmap) {
                return;
            }
            String items[] = {mActivity.getResources().getString(R.string.save_img), mActivity.getResources().getString(R.string.cancel)};
            ArrayList<String> itemList = new ArrayList<>();
            itemList.addAll(Arrays.asList(items));

            W6sSelectDialogFragment w6sSelectDialogFragment = new W6sSelectDialogFragment();
            w6sSelectDialogFragment.setData(new CommonPopSelectData(itemList, null))
                    .setOnClickItemListener((position, item) ->  {
                        if (TextUtils.isEmpty(item)) {
                            return;
                        }
                        if (item.equals(getString(R.string.save_img))) {
                            byte[] qrBytes = BitmapUtil.Bitmap2Bytes(mQrBitmap, false);
                            boolean result = ImageShowHelper.saveImageToGallery(getActivity(), qrBytes, null, false);
                            if (result) {
                                AtworkToast.showResToast(R.string.save_success);
                            } else {
                                AtworkToast.showResToast(R.string.save_wrong);
                            }

                        } else if (item.equals(getString(R.string.cancel))) {
                            w6sSelectDialogFragment.dismiss();
                        }

                    })
                    .show(getChildFragmentManager(), "TEXT_POP_DIALOG");

        });
    }

    @Override
    protected boolean onBackPressed() {
        finish();
        return false;
    }



}
