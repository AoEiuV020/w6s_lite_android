package com.foreveross.atwork.modules.aboutatwork.fragment;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.api.sdk.setting.DynamicPropertiesAsyncNetService;
import com.foreveross.atwork.api.sdk.util.NetWorkHttpResultHelper;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.domain.DomainSettings;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.manager.DomainSettingsHelper;
import com.foreveross.atwork.qrcode.zxing.decode.BitmapQrcodeDecoder;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.ErrorHandleUtil;

import org.json.JSONObject;

/**
 * Created by dasunsy on 15/12/15.
 */
public class IntroFriendsFragment extends BackHandledFragment{
    public static final String TAG = IntroFriendsFragment.class.getSimpleName();

    private ImageView mIvQrcode;
    private TextView mTvTitle;
    private ImageView mIvBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_intro_friends, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerListener();
        mTvTitle.setText(R.string.intro_friends);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        DomainSettings domainSetting = BaseApplicationLike.getDomainSetting();
        if (null == domainSetting || TextUtils.isEmpty(domainSetting.getWorkPlusUrl())) {
            queryDomainSettings();


        } else {
            BitmapQrcodeDecoder decoder = new BitmapQrcodeDecoder(mActivity);
            decoder.createQRImage(domainSetting.getWorkPlusUrl(), mIvQrcode, 300, 300);
        }


    }

    private void queryDomainSettings() {
        DomainSettingsHelper.getInstance().getDomainSettingsFromRemote(BaseApplicationLike.baseContext, true, new DynamicPropertiesAsyncNetService.OnDomainSettingsListener() {
            @Override
            public void onDomainSettingsCallback(DomainSettings domainSettingCallback) {
                if (!TextUtils.isEmpty(domainSettingCallback.getWorkPlusUrl())) {
                    BitmapQrcodeDecoder decoder = new BitmapQrcodeDecoder(mActivity);
                    decoder.createQRImage(domainSettingCallback.getWorkPlusUrl(), mIvQrcode, 300, 300);
                }
            }

            @Override
            public void onDomainSettingsFail() {

            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleError(errorCode, errorMsg);

            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public void getShareUrlQrcodeFromDomainId() {
        LoginUserInfo loginUserInfo = LoginUserInfo.getInstance();
        String url = String.format(UrlConstantManager.getInstance().V2_shareWorkplusQRImageNew(), AtworkConfig.DOMAIN_ID, loginUserInfo.getLoginToken(mActivity).mAccessToken);

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                String downloadUrl = "";
                try {
                    String httpParams = new JSONObject().put("format", "url").toString();
                    HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, httpParams);
                    String result = NetWorkHttpResultHelper.getResultText(httpResult.result);
                    JSONObject resultObject = new JSONObject(result);
                    downloadUrl = resultObject.optString("content");
                } catch (Exception e) {
                    e.printStackTrace();
                }


                return downloadUrl;
            }

            @Override
            protected void onPostExecute(String url) {
                super.onPostExecute(url);
                BitmapQrcodeDecoder decoder = new BitmapQrcodeDecoder(mActivity);
                decoder.createQRImage(url, mIvQrcode, 300, 300);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    @Override
    protected void findViews(View view) {
        mIvQrcode = view.findViewById(R.id.iv_qrcode);
        mTvTitle = view.findViewById(R.id.title_bar_common_title);
        mIvBack = view.findViewById(R.id.title_bar_common_back);
    }

    private void registerListener () {
        mIvBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    protected boolean onBackPressed() {
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        return false;
    }


}
