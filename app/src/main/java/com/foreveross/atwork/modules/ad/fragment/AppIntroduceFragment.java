package com.foreveross.atwork.modules.ad.fragment;

import android.Manifest;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.foreverht.threadGear.HighPriorityCachedTreadPoolExecutor;
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.support.VoipSdkType;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.DeviceUtil;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.manager.DomainSettingsHelper;
import com.foreveross.atwork.modules.main.service.HandleLoginService;
import com.foreveross.atwork.modules.workbench.manager.WorkbenchManager;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.IntentUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;


public class AppIntroduceFragment extends BackHandledFragment {

    private ViewPager mViewPager;
    private LinearLayout mLlGalleryPoint;
    private TextView mTvOverJump;

    private WelcomePagerAdapter mAdvertsPagerAdapter;
    private Handler mHandler = new Handler();
    private ArrayList<ImageView> mIvDotList = new ArrayList<>();
    private int mCurrentAdvertIndex;
    RequestOptions options = new RequestOptions()
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .fallback(R.mipmap.loading_gray_holding)
            .error(R.mipmap.loading_gray_holding);

    boolean mHandleInit = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_app_introduce, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CommonShareInfo.setIntroduceVersionShowed(mActivity);
        registerListener();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requestPermissions();
        initData();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        HandleLoginService.getInstance().cancel();
    }

    @Override
    protected void findViews(View view) {
        mViewPager = view.findViewById(R.id.vp_ad);
        mLlGalleryPoint = view.findViewById(R.id.gallery_point);
        mTvOverJump = view.findViewById(R.id.tv_over_jump);
    }


    private void registerListener() {
        mTvOverJump.setOnClickListener(v -> HandleLoginService.getInstance().toStart(mActivity, mHandler, 0));
    }

    @Override
    protected boolean onBackPressed() {
        mActivity.finish();
        return false;
    }


    private void initData() {
        String listString = FileUtil.getFromAssets(mActivity, "introduce/config");
        ArrayList<String> slideList = new Gson().fromJson(listString, new TypeToken<List<String>>(){}.getType());
        mAdvertsPagerAdapter = new WelcomePagerAdapter(mActivity, slideList);
        mViewPager.setAdapter(mAdvertsPagerAdapter);
        drawSliderPoint();
        adjustGalleryPoint();

        if(1 == slideList.size()) {
            mTvOverJump.setVisibility(View.GONE);
        } else {
            mTvOverJump.setVisibility(View.VISIBLE);

        }

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setSelectedDot(position);
                mCurrentAdvertIndex = position;
                if (mAdvertsPagerAdapter.getCount() - 1 == position) {
                    mTvOverJump.setVisibility(View.GONE);


                } else {
                    mTvOverJump.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void drawSliderPoint() {
        mLlGalleryPoint.removeAllViews();
        mIvDotList.clear();
        for (int i = 0; i < mAdvertsPagerAdapter.getDotCount(); i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    15, 15);
            ImageView pointView = new ImageView(getActivity());
            // 获取
            int pixels = DensityUtil.dip2px(8);
            params.leftMargin = pixels;
            if (mAdvertsPagerAdapter.getDotCount() == 1) {
                pointView.setVisibility(View.GONE);
            }
            mIvDotList.add(pointView);
            if (i == mCurrentAdvertIndex) {
                pointView.setImageResource(R.mipmap.bluedot);
            } else {
                pointView.setImageResource(R.mipmap.blue_lightdot);
            }
            mLlGalleryPoint.addView(pointView, params);
        }
    }

    private void adjustGalleryPoint() {
        mTvOverJump.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int marginBottom = DensityUtil.dip2px(20) + mTvOverJump.getHeight() / 2 - 7;

                RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mLlGalleryPoint.getLayoutParams();
                rlp.setMargins(0, 0, 0, marginBottom);
                mLlGalleryPoint.setLayoutParams(rlp);

                mTvOverJump.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

    }

    private void setSelectedDot(int position) {
        for (int i = 0; i < mIvDotList.size(); i++) {
            ImageView dot = mIvDotList.get(i);
            if (i == position) {
                mIvDotList.get(position).setImageResource(R.mipmap.bluedot);
            } else {
                dot.setImageResource(R.mipmap.blue_lightdot);
            }
        }
    }


    class WelcomePagerAdapter extends PagerAdapter {

        private Activity context;
        private ArrayList<String> mIntroduceList;

        public WelcomePagerAdapter(Activity context, ArrayList<String> advertsList) {
            this.context = context;
            this.mIntroduceList = advertsList;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return mIntroduceList.size();
        }


        public int getDotCount() {
            return mIntroduceList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.item_pager_ad, null);
            if (null == view.getParent()) {
                container.addView(view);
            }

            ImageView img = view.findViewById(R.id.image);
            img.setScaleType(ImageView.ScaleType.FIT_XY);
            if (!ListUtil.isEmpty(mIntroduceList)) {
                Glide.with(mActivity).load(Uri.parse("file:///android_asset/introduce/"+ mIntroduceList.get(position))).apply(options).into(img);
            }

            if (mIntroduceList.size() - 1 == position) {
                final TextView startTv = view.findViewById(R.id.tv_start_ad_enter);
                startTv.setVisibility(View.VISIBLE);
                startTv.setOnClickListener(v -> HandleLoginService.getInstance().toStart(mActivity, mHandler, 0));

            }

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    private void requestPermissions() {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionsResultAction() {
            @Override
            public void onGranted() {

                PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        requestCameraPermission();
                    }

                    @Override
                    public void onDenied(String permission) {
                        AtworkUtil.popAuthSettingAlert(mActivity, permission);
                    }
                });


            }

            @Override
            public void onDenied(String permission) {
                AtworkUtil.popAuthSettingAlert(mActivity, permission);
            }
        });

    }

    private void requestCameraPermission() {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mActivity, new String[]{Manifest.permission.CAMERA}, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                requestPhoneStatePermission();
            }

            @Override
            public void onDenied(String permission) {
                AtworkUtil.popAuthSettingAlert(mActivity, permission);
            }
        });
    }


    private void requestPhoneStatePermission() {

        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mActivity, new String[]{Manifest.permission.READ_PHONE_STATE}, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                if (!mHandleInit) {
                    handleInit();
                }
                PermissionsManager.getInstance().clear();
            }

            @Override
            public void onDenied(String permission) {
                boolean needCheckPhonePermission = (VoipSdkType.QSY == AtworkConfig.VOIP_SDK_TYPE);
                if(needCheckPhonePermission) {
                    AtworkUtil.popAuthSettingAlert(mActivity, permission);
                    return;
                }
                popCommonAuthSettingAlert(permission);
            }

            public void popCommonAuthSettingAlert(String permission) {
                AtworkAlertDialog alertDialog = AtworkUtil.getAuthSettingAlert(mActivity, permission);
                alertDialog.setClickBrightColorListener(dialog -> {
                    IntentUtil.startAppSettings(mActivity);
                    mActivity.finish();
                    PermissionsManager.getInstance().clear();

                }).setClickDeadColorListener(dialog -> alertDialog.dismiss()).setOnDismissListener(dialog -> finish());

                alertDialog.show();
            }
        });
        CommonShareInfo.setVpnPermissionHasShown(mActivity, true);
    }

    private void handleInit() {


        //初始化设备ID
        DeviceUtil.initDeviceId(BaseApplicationLike.baseContext);

        handleInitAfterGetDeviceId();

        HighPriorityCachedTreadPoolExecutor.getInstance().execute(() -> {
            FileUtil.copyAssetsToSdCard(AtworkApplicationLike.sApp, "STICKER", AtWorkDirUtils.getInstance().getStickerRootPath());
        });

        WorkbenchManager.INSTANCE.initCurrentOrgWorkbench();

        mHandleInit = true;
    }

    private void handleInitAfterGetDeviceId() {
        AtworkApplicationLike.initDomainAndOrgSettings();
        DomainSettingsHelper.getInstance().getDomainSettingsFromRemote(BaseApplicationLike.baseContext, true, null);

    }
}
