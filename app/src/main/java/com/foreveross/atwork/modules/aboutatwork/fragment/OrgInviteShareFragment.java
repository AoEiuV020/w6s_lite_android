package com.foreveross.atwork.modules.aboutatwork.fragment;

import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.qrcode.QrcodeAsyncNetService;
import com.foreveross.atwork.component.OrgSwitchDialog;
import com.foreveross.atwork.db.daoService.OrganizationDaoService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorksHelper;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.model.user.LoginUserBasic;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleItem;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.manager.OrganizationSettingsHelper;
import com.foreveross.atwork.modules.aboutatwork.activity.OrgInviteShareActivity;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.common.adapter.CommonAdapter;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;

import java.util.List;

/**
 * Created by dasunsy on 16/6/21.
 */
public class OrgInviteShareFragment extends BackHandledFragment {

    private View mLayout;
    private ImageView mIvBack;
    private TextView mTvTitle;
    private ListView mLwOrg;
    private TextView mTvOrgName;
    private TextView mTvSwitch;
    private String[] mCommonNames;
    private TypedArray mCommonIconResIds;
    private CommonAdapter mCommonAdapter;
    private TextView mTvCopyright;

    private Organization mCurrentOrg;
    private View mTransparentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_org_share_invite, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViews();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mCommonIconResIds.recycle();
    }


    @Override
    protected void findViews(View view) {
        mLayout = view.findViewById(R.id.org_share_invite_layout);
        mIvBack = view.findViewById(R.id.title_bar_common_back);
        mTvTitle = view.findViewById(R.id.title_bar_common_title);
        mLwOrg = view.findViewById(R.id.lw_org_invite);
        mTvOrgName = view.findViewById(R.id.tv_org_name);
        mTvSwitch = view.findViewById(R.id.tv_switch);
        mTvCopyright = view.findViewById(R.id.tv_about_copyright_common);
    }

    private void initViews() {
        mCommonNames = getResources().getStringArray(R.array.org_share_invite_array);
        mCommonIconResIds = getResources().obtainTypedArray(R.array.org_share_invite_img_array);

        mCommonAdapter = new CommonAdapter(mActivity, mCommonNames, mCommonIconResIds);

        mTvTitle.setText(R.string.org_invite_to_share);
        mLwOrg.setAdapter(mCommonAdapter);
        mLwOrg.setDivider(null);

        mTvCopyright.setText(BeeWorksHelper.getCopyright(BaseApplicationLike.baseContext, true));


        OrganizationManager.getInstance().getLocalCurrentOrg(getActivity(), (org) -> {
            mTvOrgName.setText(org.getNameI18n(BaseApplicationLike.baseContext));

            mCurrentOrg = org;

        });

        OrganizationDaoService.getInstance().queryOrgCount(LoginUserInfo.getInstance().getLoginUserId(getActivity()), (count)->{
            if(1 >= count) {
                mTvSwitch.setVisibility(View.GONE);

            } else {

                mTvSwitch.setVisibility(View.VISIBLE);
            }
        });

    }

    private void registerListener() {
        mIvBack.setOnClickListener((v) -> onBackPressed());

        mTvSwitch.setOnClickListener((v) -> {
            OrganizationManager manager = OrganizationManager.getInstance();
            manager.getLocalOrganizations(getActivity(), localData -> {
                if (localData == null) {
                    return;
                }
                List<Organization> orgList = (List<Organization>) localData[0];
                if (ListUtil.isEmpty(orgList)) {
                    return;
                }

                OrgSwitchDialog orgSwitchDialog = new OrgSwitchDialog();
                orgSwitchDialog.setData(OrgSwitchDialog.Type.DEFAULT, getString(R.string.switch_orgs));
                orgSwitchDialog.setOrgData(orgList);

                orgSwitchDialog.setItemOnClickListener(organization -> {
                    orgSwitchDialog.dismiss();
                    OrganizationSettingsHelper.getInstance().setCurrentOrgCodeAndRefreshSetting(BaseApplicationLike.baseContext, organization.mOrgCode, true);

                    mTvOrgName.setText(organization.getNameI18n(BaseApplicationLike.baseContext));

                    mCurrentOrg = organization;
                });

                orgSwitchDialog.show(getFragmentManager(), "org_switch");
            });
        });

        mLwOrg.setOnItemClickListener((parent, view, position, id) -> {
            String name = (String) parent.getItemAtPosition(position);
            LoginUserBasic userBasic = LoginUserInfo.getInstance().getLoginUserBasic(mActivity);
            if (getString(R.string.org_share_invite_member).equals(name)) {
                QrcodeAsyncNetService.getInstance().fetchOrgQrUrl(mActivity, mCurrentOrg.mOrgCode, new QrcodeAsyncNetService.onOrgQrUrlListener() {
                    @Override
                    public void onQrUrlSuccess(String url) {

                        OrganizationManager.getInstance().getLocalCurrentOrg(mActivity, org -> {
                            if (isAdded()) {
                                ArticleItem item = new ArticleItem();
                                item.url = url;
                                item.coverMediaId = org.mLogo;
                                item.title = String.format(getString(R.string.invite_you_to_join), org.mName);
                                item.summary = userBasic.mName + String.format(getString(R.string.invite_to_join_org_summary), org.mName);
                                item.mOrgCode = org.mOrgCode;
                                item.mOrgOwner = org.mOwner;
                                item.mOrgAvatar = org.mLogo;
                                item.mOrgName = org.mName;
                                item.mOrgDomainId = org.mDomainId;
                                ((OrgInviteShareActivity)mActivity).showShareDialog(item);
                            }
                        });

                    }

                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        AtworkToast.showToast(getString(R.string.get_org_qr_url_fail));
                    }
                });

            } else if (getString(R.string.org_qrcode_invite_member).equals(name)) {
                String url = String.format(UrlConstantManager.getInstance().getOrgQrcodeUrl(), mCurrentOrg.mOrgCode, Uri.encode(getString(R.string.app_name)), true);
                WebViewControlAction webViewControlAction = WebViewControlAction.newAction().setUrl(url).setNeedShare(false);
                Intent intent = WebViewActivity.getIntent(getActivity(), webViewControlAction);
                startActivity(intent);
            }
        });
    }

    @Override
    protected boolean onBackPressed() {
        finish();
        return false;
    }

}
