package com.foreveross.atwork.modules.aboutme.fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.qrcode.QrcodeAsyncNetService;
import com.foreveross.atwork.component.WorkplusBottomPopDialog;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.utils.BitmapUtil;
import com.foreveross.atwork.infrastructure.utils.CustomerHelper;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.aboutme.activity.PersonalQrcodeActivity;
import com.foreveross.atwork.modules.aboutme.service.MobileContactVcardHelper;
import com.foreveross.atwork.modules.chat.component.chat.PopupMicroVideoRecordingDialog;
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment;
import com.foreveross.atwork.modules.qrcode.activity.QrcodeScanActivity;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.UUID;

/**
 * Created by shadow on 2016/5/17.
 */
public class PersonalQrcodeFragment extends BackHandledFragment implements View.OnClickListener {

    private static final String TAG = PersonalQrcodeFragment.class.getSimpleName();

    private static final int MODE_WORKPLUS_QRCODE_CARD = 0;
    private static final int MODE_MOBILE_CONTACT_QRCODE_CARD = 1;

    private ImageView mAvatarView;

    private TextView mNameView;

    private ImageView mQrcodeView;

    private TextView mTitleView;

    private ImageView mBackView;

    private TextView mMoreView;

    private User mUser;

    private Bitmap mQrBitmap;

    private ImageView mIvSwithchLeft;

    private TextView mTvCardTop;

    private ImageView mIvSwithchRight;

    private TextView mTvCardBottom;

    private Bitmap mW6sUserQrcodeCardBitmap;


    private int mMode = MODE_WORKPLUS_QRCODE_CARD;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_qrcode, container, false);

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

        initUI();

    }

    public void initUI() {
        mMoreView.setTextColor(ContextCompat.getColor(getActivity(), R.color.common_item_black));

        refreshModeUI();
    }

    private void refreshModeUI() {
        switch (mMode) {
            case MODE_MOBILE_CONTACT_QRCODE_CARD:
                mTvCardTop.setText(R.string.personal_contact_top_title_no_holder);
                mTvCardBottom.setText(R.string.mobile_contact_card_wechat_hint);

                if (CustomerHelper.isNewland(AtworkApplicationLike.baseContext)) {
                    mIvSwithchLeft.setVisibility(View.VISIBLE);
                }
                mIvSwithchRight.setVisibility(View.INVISIBLE);

                mQrcodeView.setImageBitmap(null);
                if(0 == mQrcodeView.getWidth() || 0 == mQrcodeView.getHeight()) {

                    mQrcodeView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            mQrcodeView.getViewTreeObserver().removeOnPreDrawListener(this);
                            MobileContactVcardHelper.produceCurrentEmpVcardQrcode(mQrcodeView);

                            return false;
                        }
                    });

                } else {
                    MobileContactVcardHelper.produceCurrentEmpVcardQrcode(mQrcodeView);

                }


                break;

            case MODE_WORKPLUS_QRCODE_CARD:
                if (!StringUtils.isEmpty(getStrings(R.string.personal_qrcode_top_title_no_holder))) {
                    mTvCardTop.setText(getStrings(R.string.personal_qrcode_top_title_no_holder));
                } else {
                    mTvCardTop.setText(getString(R.string.personal_qrcode_top_title, getString(R.string.app_name)));

                }
                mTvCardBottom.setText(getString(R.string.personal_qrcode_hint, getString(R.string.app_name)));

                mIvSwithchLeft.setVisibility(View.INVISIBLE);
                if (CustomerHelper.isNewland(AtworkApplicationLike.baseContext)) {
                    mIvSwithchRight.setVisibility(View.VISIBLE);
                }

                mQrcodeView.setImageBitmap(null);

                String tag = UUID.randomUUID().toString();
                mQrcodeView.setTag(tag);

                if(null != mW6sUserQrcodeCardBitmap) {
                    setW6sUserCard(mW6sUserQrcodeCardBitmap);
                }

                QrcodeAsyncNetService.getInstance().fetchPersonalQrcode(mActivity, mUser.mUserId, new QrcodeAsyncNetService.OnGetQrcodeListener() {
                    @Override
                    public void success(Bitmap qrcodeBitmap, long effectTime) {
                        mW6sUserQrcodeCardBitmap = qrcodeBitmap;

                        if (tag.equals(mQrcodeView.getTag())) {
                            setW6sUserCard(qrcodeBitmap);
                        }
                    }


                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        ErrorHandleUtil.handleError(errorCode, errorMsg);
                    }
                });


                break;
        }




    }

    private void setW6sUserCard(Bitmap qrcodeBitmap) {
        mQrBitmap = qrcodeBitmap;
        mQrcodeView.setImageBitmap(qrcodeBitmap);
        mQrcodeView.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    @Override
    protected void findViews(View view) {
        mAvatarView = view.findViewById(R.id.personal_qr_avatar);
        mNameView = view.findViewById(R.id.personal_qr_name);
        mQrcodeView = view.findViewById(R.id.personal_qr_code);
        mTitleView = view.findViewById(R.id.title_bar_common_title);
        mBackView = view.findViewById(R.id.title_bar_common_back);
        mMoreView = view.findViewById(R.id.title_bar_common_right_text);
        mIvSwithchLeft = view.findViewById(R.id.iv_switch_left);
        mTvCardTop = view.findViewById(R.id.tv_card_top);
        mIvSwithchRight = view.findViewById(R.id.iv_switch_right);
        mTvCardBottom = view.findViewById(R.id.tv_card_bottom);
    }

    private void initData() {

        mTitleView.setText(getString(R.string.qr_postcard));

        mMoreView.setText(getString(R.string.more_item));
        mMoreView.setVisibility(View.VISIBLE);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mUser = bundle.getParcelable(PersonalQrcodeActivity.INTENT_DATA);
        }

        if (mUser == null) {
            return;
        }

        ImageCacheHelper.displayImageByMediaId(mUser.mAvatar, mAvatarView, ImageCacheHelper.getRoundAvatarOptions());

        mNameView.setText(mUser.getShowName());


//        mTvCardTop.setText(getString(R.string.personal_qrcode_top_title, getString(R.string.app_name)));
//        mTvCardBottom.setText(getString(R.string.personal_qrcode_hint, getString(R.string.app_name)));
    }

    private void registerListener() {
        mBackView.setOnClickListener(this);
        mMoreView.setOnClickListener(this);
        mIvSwithchLeft.setOnClickListener(this);
        mIvSwithchRight.setOnClickListener(this);
    }

    private void onMore() {
        WorkplusBottomPopDialog dialog = new WorkplusBottomPopDialog();
        String[] data = mActivity.getResources().getStringArray(R.array.popup_dialog_personal_qrcode_array);
        dialog.refreshData(data);
        dialog.setItemOnListener(tag -> {

            if (getResources().getString(R.string.save_qrcode_img).equals(tag)) {
                AndPermission
                        .with(mActivity)
                        .runtime()
                        .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                        .onGranted(granted -> {
                            byte[] qrBytes = BitmapUtil.Bitmap2Bytes(mQrBitmap, false);
                            boolean result = ImageShowHelper.saveImageToGallery(getActivity(), qrBytes, null, false);
                            if (result) {
                                AtworkToast.showResToast(R.string.save_success);
                            } else {
                                AtworkToast.showResToast(R.string.save_wrong);
                            }
                            dialog.dismiss();
                        })
                        .onDenied(denied ->  AtworkUtil.popAuthSettingAlert(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        .start();
            }

            if (getResources().getString(R.string.sweep_qrcode).equals(tag)){
                if(VoipHelper.isHandlingVoipCall()) {
                    AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);
                    return;
                }


                PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mActivity, new String[]{Manifest.permission.CAMERA}, new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        Intent intent = QrcodeScanActivity.getIntent(mActivity);
                        startActivity(intent);
                        dialog.dismissAllowingStateLoss();
                    }

                    @Override
                    public void onDenied(String permission) {
                        AtworkUtil.popAuthSettingAlert(mActivity, permission);
                    }
                });
            }

        });

        dialog.show(getChildFragmentManager(), "show_more_qrcode");

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {

            case R.id.title_bar_common_back:
                onBackPressed();
                break;

            case R.id.title_bar_common_right_text:
                onMore();
                break;

            case R.id.iv_switch_left:
                mMode = MODE_WORKPLUS_QRCODE_CARD;
                refreshModeUI();
                break;

            case R.id.iv_switch_right:
                mMode = MODE_MOBILE_CONTACT_QRCODE_CARD;
                refreshModeUI();
                break;
        }

    }

    @Override
    protected boolean onBackPressed() {
        finish();
        return false;
    }

}
