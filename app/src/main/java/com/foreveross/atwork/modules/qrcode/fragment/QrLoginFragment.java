package com.foreveross.atwork.modules.qrcode.fragment;/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 */


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.qrcode.QrcodeAsyncNetService;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.ErrorHandleUtil;

/**
 * Created by reyzhang22 on 16/6/14.
 */
public class QrLoginFragment extends BackHandledFragment {


    private static final String TAG = QrLoginFragment.class.getSimpleName();

    public static final String ARGUMENT_QR_LOGIN_CODE = "ARGUMENT_QR_LOGIN_CODE";

    public static final String ARGUMENT_QR_LOGIN_FROM = "ARGUMENT_QR_LOGIN_FROM";

    private Button mQrLogin;

    private ImageView mIvBack;

    private TextView mTvTitle;

    private TextView mTvCancelLogin;

    private TextView mQrTextView;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr_login, container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewsUI();
        registerListener();
    }

    private void initViewsUI() {
        mTvTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(getString(R.string.qr_login));

        String from = getArguments().getString(ARGUMENT_QR_LOGIN_FROM);
        if (FromType.PCWEB.equalsIgnoreCase(from)) {

            mQrTextView.setText(getString(R.string.qr_login_to_pcweb));

        } else if (FromType.BPM_DASHBOARD.equalsIgnoreCase(from)){
            mQrTextView.setText(getString(R.string.qr_login_to_bpm));


        } else if(FromType.DASHBOARD.equalsIgnoreCase(from)) {
            mQrTextView.setText(getString(R.string.qr_login_to_admin));

        }

        ViewUtil.addUnderline(mTvCancelLogin);
    }

    @Override
    protected void findViews(View view) {
        mQrLogin = view.findViewById(R.id.qr_login_pc);
        mIvBack = view.findViewById(R.id.title_bar_common_back);
        mTvTitle = view.findViewById(R.id.title_bar_common_title);
        mTvCancelLogin = view.findViewById(R.id.tv_cancel_login);
        mQrTextView = view.findViewById(R.id.qr_login_text);
    }

    private void registerListener() {
        mQrLogin.setOnClickListener(view -> {
            String qrCode = getArguments().getString(ARGUMENT_QR_LOGIN_CODE);
            QrcodeAsyncNetService.getInstance().qrLogin(mActivity, qrCode, "login", new QrcodeAsyncNetService.OnQrLoginListener() {
                @Override
                public void success() {
                    onBackPressed();
                }



                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Qrcode, errorCode, errorMsg);

                }
            });
        });

        mIvBack.setOnClickListener(view -> {
            onBackPressed();
        });

        mTvCancelLogin.setOnClickListener(view -> {
            String code = getArguments().getString(ARGUMENT_QR_LOGIN_CODE);
            QrcodeAsyncNetService.getInstance().qrLogin(mActivity, code, "cancel", new QrcodeAsyncNetService.OnQrLoginListener() {
                @Override
                public void success() {
                    onBackPressed();
                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Qrcode, errorCode, errorMsg);


                }
            });
        });
    }

    @Override
    protected boolean onBackPressed() {
        mActivity.finish();
        return false;
    }


    private final class FromType {
        /**
         * pc客户端
         * */
        static final String PCWEB = "pcweb";

        /**
         * 管理后台
         * */
        static final String DASHBOARD = "dashboard";


        /**
         * 流程大师后台
         * */
        static final String BPM_DASHBOARD = "bpm_dashboard";
    }


}
