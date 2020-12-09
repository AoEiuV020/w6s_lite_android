package com.foreveross.atwork.modules.aboutme.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.component.viewPager.AdjustHeightViewPager;
import com.foreveross.atwork.db.daoService.EmployeeDaoService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.domain.UserSchemaSettingItem;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.EmployeeManager;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.modules.aboutme.activity.ModifyMyInfoActivity;
import com.foreveross.atwork.modules.aboutme.activity.ModifyPersonalSignatureActivity;
import com.foreveross.atwork.modules.aboutme.activity.PersonalQrcodeActivity;
import com.foreveross.atwork.modules.aboutme.adapter.MyAccountPagerAdapter;
import com.foreveross.atwork.modules.aboutme.component.MyAccountUserInfoItemView;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.common.fragment.ChangeAvatarFragment;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Reyzhang on 2015/3/25.
 */
public class MyAccountFragment extends ChangeAvatarFragment implements View.OnClickListener {
    private static final String TAG = MyAccountFragment.class.getSimpleName();

    private ImageView mTitleBarCommonRightImg, mImagePersonalAvatar, mImageUpdatePersonalAvatar, mImageAccountName;
    private RelativeLayout mRlAccountBaseInfo, mRlPersonalSignature;

    private TextView mTitleView, mPersonalInfoAccountName, mTvPersonalSignature;

    private int mCurrentIndex = 0;
    private boolean mInitDot = false;

    private User mLoginUser;

    private String mNewAvatarMediaId;

    private LinearLayout mLlUserInfoPlace,mLlUserInfoLeftPlace,mLlUserInfoRightPlace, mPersonalAvatarLayout;


    private TabLayout mTabLayout;

    private AdjustHeightViewPager mViewPager;


    private MyAccountPagerAdapter mPagerAdapter;

    private List<Employee> mLocalEmployeeList = new ArrayList<>();

    private int mLastSelectedIndex = 0;
    private int mLastScrollPosition = -1;

    private LinearLayout mPublicView;

    private TextView mEditProfile;

    private Map<String, MyAccountUserInfoItemView> mUserInfoViewMap = new HashMap<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sortAndLayoutUserInfoView();
        registerListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();

//        handleSettingsView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDomainSettingChange() {
//        handleSettingsView();

    }


    @Override
    protected void findViews(View view) {
        mPublicView = view.findViewById(R.id.public_my_account_info_layout);
        mTitleView = view.findViewById(R.id.title_bar_common_title);
        //mTitleView.setText(getResources().getString(R.string.personal_info_title));
        mLlUserInfoPlace = view.findViewById(R.id.ll_user_info_place);
        mImageAccountName = view.findViewById(R.id.image_account_name);
        mRlAccountBaseInfo = view.findViewById(R.id.rl_account_base_info);
        mRlPersonalSignature = view.findViewById(R.id.rl_personal_signature);
        mTitleBarCommonRightImg = view.findViewById(R.id.title_bar_common_right_img);
        mTitleBarCommonRightImg.setImageResource(R.mipmap.icon_qrcode_account);
        mTvPersonalSignature = view.findViewById(R.id.tv_personal_info);
        mImagePersonalAvatar = view.findViewById(R.id.image_personal_avatar);
        mImageUpdatePersonalAvatar = view.findViewById(R.id.image_update_personal_avatar);
        mPersonalInfoAccountName = view.findViewById(R.id.personal_info_account_name);
        mLlUserInfoLeftPlace = view.findViewById(R.id.ll_user_info_left_place);
        mLlUserInfoRightPlace = view.findViewById(R.id.ll_user_info_right_place);
        mPersonalAvatarLayout = view.findViewById(R.id.personal_avatar_layout);

        fetchUserInfoItemView();


        mViewPager = view.findViewById(R.id.my_account_viewPager);
        mTabLayout = view.findViewById(R.id.my_account_tabLayout);


//        mTabLayout.setVisibility(View.VISIBLE);
        initViewPager();
        mPublicView.setVisibility(View.VISIBLE);


        mEditProfile = view.findViewById(R.id.title_bar_common_right_text);
        mEditProfile.setText(R.string.edit);


    }

    //获取用户数据
    private void fetchUserInfoItemView() {

        List<UserSchemaSettingItem> userSchemaSettingItemList = DomainSettingsManager.getInstance().getUserSchemaSettings();
        for(UserSchemaSettingItem userSchema : userSchemaSettingItemList) {
            mUserInfoViewMap.put(userSchema.getProperty(), new MyAccountUserInfoItemView(getActivity()));

        }


    }

    private void sortAndLayoutUserInfoView() {
        int count = 0;
        Boolean isUpName = false;

        mLlUserInfoPlace.removeAllViews();
        mLlUserInfoLeftPlace.removeAllViews();
        mLlUserInfoRightPlace.removeAllViews();
        List<UserSchemaSettingItem> userSchemaVisibleItemList = new ArrayList<>();

        List<UserSchemaSettingItem> userSchemaSettingItemList = DomainSettingsManager.getInstance().getUserSchemaSettings();
        Collections.sort(userSchemaSettingItemList);

        for(UserSchemaSettingItem userSchemaVisibleItem : userSchemaSettingItemList) {
            String property =  userSchemaVisibleItem.getProperty();
            if(userSchemaVisibleItem.getVisible()&&!property.equals("qr_code") &&!property.equals("avatar")&&!property.equals("name")){
                userSchemaVisibleItemList.add(userSchemaVisibleItem);
            }
            //判断是否可以编辑名称
            if(property.equals("name")&&userSchemaVisibleItem.getModifiable()){
                isUpName = true;
            }
            //判断是否二维码是否显示
            if(property.equals("qr_code")&&userSchemaVisibleItem.getVisible()){
                mTitleBarCommonRightImg.setVisibility(View.VISIBLE);
            }


        }
        //判断是否可以编辑名称
        if(isUpName){
            mImageAccountName.setVisibility(VISIBLE);
        }else {
            mImageAccountName.setVisibility(GONE);
        }
        for(UserSchemaSettingItem userSchemaSettingItem : userSchemaVisibleItemList) {
            View view = mUserInfoViewMap.get(userSchemaSettingItem.getProperty());

            if (null != view && userSchemaSettingItem.getVisible()) {
                if(userSchemaSettingItem.getModifiable()) {
                    view.setEnabled(true);
                } else {
                    view.setEnabled(false);

                }
               // mLlUserInfoPlace.addView(view);
                ++count;
                if(count%2 == 0 ){
                    mLlUserInfoRightPlace.addView(view);
                }else if(count == userSchemaVisibleItemList.size()){
                    mLlUserInfoPlace.addView(view);
                }else{
                    mLlUserInfoLeftPlace.addView(view);
                }
            }

        }

        //隐藏最后个view 的分割线
        if (0 < mLlUserInfoPlace.getChildCount()) {
            MyAccountUserInfoItemView lastView = (MyAccountUserInfoItemView) mLlUserInfoPlace.getChildAt(mLlUserInfoPlace.getChildCount() - 1);
            lastView.setDividerVisible(false);
        }
    }

//    private void handleSettingsView() {
//        if (!DomainSettingsManager.getInstance().handleUsernameModifyFeature()) {
//            mUsernameArrow.setVisibility(View.GONE);
//
//        } else {
//            mUsernameArrow.setVisibility(View.VISIBLE);
//
//        }
//    }

    private void initViewPager() {
        mPagerAdapter = new MyAccountPagerAdapter(this, mLoginUser, mLocalEmployeeList);
        mViewPager.setAdapter(mPagerAdapter);
//        mTabLayoutHelper = new TabLayoutHelper(mTabLayout,mViewPager);
//        mTabLayoutHelper.setAutoAdjustTabModeEnabled(true);
        mTabLayout.setupWithViewPager(mViewPager);
        //mTabLayout.setTabTextColors(SkinHelper.getTabInactiveColor(), SkinHelper.getActiveColor());
        mTabLayout.setTabTextColors(Color.parseColor("#333333"), Color.parseColor("#333333"));
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                mViewPager.reMeasureCurrentPage(mViewPager.findViewWithTag(AdjustHeightViewPager.TAG + tab.getPosition()));

                if (null != tab.getCustomView()) {
                    TextView tv = tab.getCustomView().findViewById(R.id.tv_custom);
//                    tv.setTextColor(ContextCompat.getColor(BaseApplicationLike.baseContext, R.color.common_blue_bg));
//                    tv.setTextColor(SkinHelper.parseColorFromTag((String) mTabLayout.getTag()));
                    tv.setTextColor(Color.parseColor("#333333"));
                    tv.setTypeface(Typeface.DEFAULT_BOLD);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (null != tab.getCustomView()) {
                    TextView tv = tab.getCustomView().findViewById(R.id.tv_custom);
                    //tv.setTextColor(SkinHelper.getTabInactiveColor());
                    tv.setTextColor(Color.parseColor("#333333"));
                    tv.setTypeface(Typeface.DEFAULT);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (null != tab.getCustomView()) {
                    TextView tv = tab.getCustomView().findViewById(R.id.tv_custom);
                    //tv.setTextColor(SkinHelper.parseColorFromTag((String) mTabLayout.getTag()));
                    tv.setTextColor(Color.parseColor("#333333"));
                    tv.setTypeface(Typeface.DEFAULT_BOLD);
                }
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mLastSelectedIndex = position;
                mViewPager.reMeasureCurrentPage(mViewPager.findViewWithTag(AdjustHeightViewPager.TAG + position));

                mCurrentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initData() {
        AtworkApplicationLike.getLoginUser(new UserAsyncNetService.OnQueryUserListener() {
            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleError(errorCode, errorMsg);
            }

            @Override
            public void onSuccess(User loginUser) {
                mLoginUser = loginUser;

                OrganizationManager.getInstance().queryLoginOrgList(orgList
                        -> {
                    List<String> codeList = Organization.getOrgCodeList(orgList);

                    EmployeeDaoService.getInstance().queryEmpListLocal(loginUser.mUserId, codeList, employeeList
                            -> {
                        if (isAdded()) {
                            mLocalEmployeeList.clear();
                            mLocalEmployeeList.addAll(employeeList);

                            //赋值 orgName 给雇员
                            for (Employee employee : mLocalEmployeeList) {

                                for (Organization org : orgList) {
                                    if (org.mOrgCode.equalsIgnoreCase(employee.orgCode)) {
                                        employee.setOrgInfo(org.getNameI18n(BaseApplicationLike.baseContext), org.mCreated, org.getSortOrder());
                                        break;
                                    }
                                }
                            }

                            Collections.sort(mLocalEmployeeList);

                            refreshProfileEditBtn();

                            refreshView();

                            queryUserFromRemote();
                        }
                    });
                });


            }


        });

    }

    private void refreshProfileEditBtn() {
        if(!ListUtil.isEmpty(mLocalEmployeeList)) {

            if(mLocalEmployeeList.get(0).isH3cTypeB()) {
                mEditProfile.setVisibility(View.VISIBLE);
            } else {
                mEditProfile.setVisibility(View.GONE);

            }
        }
    }

    private void refreshViewPager() {
        if(!AtworkConfig.EMPLOYEE_CONFIG.getShowEmpInfo()) {
            mTabLayout.setVisibility(View.GONE);
            mViewPager.setVisibility(View.GONE);

            return;
        }

        if (ListUtil.isEmpty(mLocalEmployeeList)) {
            mTabLayout.setVisibility(View.GONE);
            mViewPager.setVisibility(View.GONE);

        } else {
            mTabLayout.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.VISIBLE);

//            mViewPager.removeAllViews();

            Collections.sort(mLocalEmployeeList);

            mLastScrollPosition = mTabLayout.getScrollX();


            mPagerAdapter.notifyDataSetChanged();
            refreshTab();

            mTabLayout.scrollTo(mLastScrollPosition, 0);

            //调整 viewpager 高度, 以适应 scrollView, 否则会存在偶尔无法滑动的问题
            mViewPager.postDelayed(() -> mViewPager.reMeasureCurrentPage(mViewPager.findViewWithTag(AdjustHeightViewPager.TAG + mViewPager.getCurrentItem())), 500);
        }


    }


    private void refreshTab() {
        //自定义tab
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);

            if (tab != null) {
                tab.setCustomView(mPagerAdapter.getTabView(getActivity(), i));

                if (mLastSelectedIndex == i) {
                    tab.select();
                }
            }

        }
    }


    void updateUser(User user) {
        if(!StringUtils.isEmpty(mNewAvatarMediaId) && !mNewAvatarMediaId.equalsIgnoreCase(user.mAvatar)) {
            user.mAvatar = mNewAvatarMediaId;
        }

        this.mLoginUser = user;

        LoginUserInfo.getInstance().setLoginUserBasic(mActivity, user.mUserId, user.mDomainId, null, user.mUsername, user.mName, user.mAvatar, user.mSignature);
        UserManager.getInstance().asyncAddUserToLocal(user);
    }

    private void refreshView() {
        if (mLoginUser == null) {
            return;
        }

        refreshBasicUI();
        refreshViewPager();
    }


    private void refreshBasicUI() {
        List<UserSchemaSettingItem> userSchemaSettingItemList = DomainSettingsManager.getInstance().getUserSchemaSettings();

        for(UserSchemaSettingItem userSchema : userSchemaSettingItemList) {
            mUserInfoViewMap.get(userSchema.getProperty()).setMyAccountItemInfo(mLoginUser, userSchema);
        }

        ImageCacheHelper.displayImageByMediaId(mLoginUser.mAvatar, mImagePersonalAvatar, ImageCacheHelper.getRoundOptions(R.mipmap.default_photo, -1));
        mPersonalInfoAccountName.setText(mLoginUser.getShowName());
        if (DomainSettingsManager.getInstance().handlePersonalSignatureEnable()) {
            mRlPersonalSignature.setVisibility(VISIBLE);
            mTvPersonalSignature.setText(StringUtils.isEmpty(mLoginUser.getSignature()) ?getResources().getString( R.string.enter_personal_signature ): mLoginUser.getSignature());

        } else {
            mRlPersonalSignature.setVisibility(GONE);

        }
    }

    private void registerListener() {
        //返回
        getView().findViewById(R.id.title_bar_common_back).setOnClickListener(this);

        for(Map.Entry<String, MyAccountUserInfoItemView> entry : mUserInfoViewMap.entrySet()) {

            String key = entry.getKey();
            switch (key) {
                case "avatar": {
                    MyAccountUserInfoItemView viewInfoAvatar = mUserInfoViewMap.get(key);
                    if(null != viewInfoAvatar) {
                        viewInfoAvatar.setOnClickListener(v -> startChangeAvatar());
                    }

                    break;
                }



                case "qr_code": {
                    MyAccountUserInfoItemView viewInfoQr = mUserInfoViewMap.get(key);
                    if(null != viewInfoQr) {
                        viewInfoQr.setOnClickListener(v -> {

                            Intent intent = PersonalQrcodeActivity.getIntent(mActivity, mLoginUser);
                            startActivity(intent);
                        });
                    }

                    break;
                }

                case "gender": {
                    MyAccountUserInfoItemView viewInfoGender = mUserInfoViewMap.get(key);
                    if(null != viewInfoGender) {
                        viewInfoGender.setOnClickListener(v -> {
                            startModifyInfo(viewInfoGender.getUserSchemaSettingItem(), viewInfoGender.getMyAccountValue().getText().toString());

                        });
                    }

                    break;
                }

                case "birthday" : {
                    MyAccountUserInfoItemView viewInfoBirthday = mUserInfoViewMap.get(key);
                    if(null != viewInfoBirthday) {
                        viewInfoBirthday.setOnClickListener(v -> {

                            Long date;
                            if (!StringUtils.isEmpty(mLoginUser.mBirthday)) {
                                date = Long.valueOf(mLoginUser.mBirthday);
                            } else {
                                date = System.currentTimeMillis();
                            }
                            startModifyInfo(viewInfoBirthday.getUserSchemaSettingItem(), date.toString());

                        });
                    }

                    break;
                }

                default:
                    UpUserData(key);
            }

        }
        mRlPersonalSignature.setOnClickListener(v ->{
            if(mRlPersonalSignature.getVisibility()== VISIBLE){
                Intent intent = ModifyPersonalSignatureActivity.getIntent(getActivity(),  mLoginUser.getSignature());
                startActivity(intent);

            }

        });

        mRlAccountBaseInfo.setOnClickListener(v ->{
            if(mImageAccountName.getVisibility()== VISIBLE){

                MyAccountUserInfoItemView viewInfoCommon = mUserInfoViewMap.get("name");
                startModifyInfo(viewInfoCommon.getUserSchemaSettingItem(), mPersonalInfoAccountName.getText().toString());
            }

        });

        mEditProfile.setOnClickListener(v -> {
            String url = String.format(UrlConstantManager.getInstance().getFangzhouUserInfoEditUrl());
            WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                    .setUrl(url)
                    .setNeedShare(false);

            startActivity(WebViewActivity.getIntent(mActivity, webViewControlAction));
        });

        mTitleBarCommonRightImg.setOnClickListener(v -> {
            Intent intent = PersonalQrcodeActivity.getIntent(mActivity, mLoginUser);
            startActivity(intent);
        });

        mPersonalAvatarLayout.setOnClickListener(v -> {
            startChangeAvatar();
        });

    }

    public void UpUserData(String key){
        MyAccountUserInfoItemView viewInfoCommon = mUserInfoViewMap.get(key);
        if(null != viewInfoCommon) {
            viewInfoCommon.setOnClickListener(v -> {
                startModifyInfo(viewInfoCommon.getUserSchemaSettingItem(), viewInfoCommon.getMyAccountValue().getText().toString());
            });
        }
    }

    /**
     * 从网络实时获取user信息
     */
    public void queryUserFromRemote() {
        UserManager.getInstance().asyncFetchLoginUserFromRemote(getActivity(), new UserAsyncNetService.OnQueryUserListener() {
            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleError(errorCode, errorMsg);
            }

            @Override
            public void onSuccess(@NonNull User loginUser) {
                updateUser(loginUser);
                refreshBasicUI();

                syncOrgAndEmpDataRemote();
            }


        });
    }

    /**
     * 同步组织与雇员的数据
     */
    private void syncOrgAndEmpDataRemote() {
        EmployeeManager.getInstance().queryOrgAndEmpListRemote(AtworkApplicationLike.baseContext, mLoginUser.mUserId, new EmployeeManager.QueryOrgAndEmpListListener() {

            @Override
            public void onSuccess(List<Organization> organizationList, List<Employee> employeeList) {
                if (isAdded()) {
                    if (!ListUtil.isEmpty(employeeList)) {
                        mLocalEmployeeList.clear();
                        mLocalEmployeeList.addAll(employeeList);


                        refreshProfileEditBtn();
                        refreshView();
                    }
                }
            }

            @Override
            public void onFail() {

            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {

            case R.id.title_bar_common_back:
                MyAccountFragment.this.finish();
                break;

        }
    }


    private void startModifyInfo(UserSchemaSettingItem userSchemaSettingItem, String value) {
//        if (getString(R.string.nickname).equals(type) && !DomainSettingsManager.getInstance().handleUsernameModifyFeature()) {
//            return;
//        }
        Intent intent = ModifyMyInfoActivity.getIntent(getActivity(), userSchemaSettingItem, value);
        startActivity(intent);
    }


    @Override
    protected void changeAvatar(final String mediaId) {
        UserManager.getInstance().modifyUserAvatar(getActivity(), mediaId, new UserAsyncNetService.OnHandleUserInfoListener() {
            @Override
            public void networkFail(int errorCode, String errorMsg) {
                if (!ErrorHandleUtil.handleBaseError(errorCode, errorMsg)) {
                    toastOver(R.string.update_avatar_fail);

                }


                dismissUpdatingHelper();

            }

            @Override
            public void success() {
                mNewAvatarMediaId = mediaId;
                mLoginUser.mAvatar = mediaId;
                UserManager.getInstance().asyncAddUserToLocal(mLoginUser);

                refreshBasicUI();

//                queryUserFromRemote();

                toastOver(R.string.update_avatar_success);
                dismissUpdatingHelper();

            }


        });

    }


    @Override
    protected boolean onBackPressed() {
        finish();
        return false;
    }

}
