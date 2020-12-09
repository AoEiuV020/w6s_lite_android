package com.foreveross.atwork.modules.organization.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.manager.OrgApplyManager;
import com.foreveross.atwork.manager.model.ApplyingOrganization;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.organization.adapter.OrgApplyingListAdapter;
import com.foreveross.atwork.modules.organization.loader.OrgApplyingListLoader;
import com.foreveross.atwork.support.BackHandledFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by dasunsy on 16/6/14.
 */
public class OrgApplyingFragment extends BackHandledFragment implements LoaderManager.LoaderCallbacks<List<ApplyingOrganization>> {

    public static final String REFRESH_ORG_APPLYING = "refresh_org_apply";

    private Object sLock = new Object();

    public ListView mLvOrgApplying;
    public ImageView mIvBack;
    private View mViewTitleBar;
    private TextView mTvTitle;

    private OrgApplyingListAdapter mOrgApplyingListAdapter;

    private List<ApplyingOrganization> mApplyingList;
    private BroadcastReceiver mBroadcastReceiver;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_org_applying, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerRefreshReceiver();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initData();
        registerListener();

        refresh();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterReceiver();

    }

    public static void refresh() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(OrgApplyingFragment.REFRESH_ORG_APPLYING));
    }


    private void registerRefreshReceiver() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (TextUtils.isEmpty(intent.getAction()) || !intent.getAction().equalsIgnoreCase(REFRESH_ORG_APPLYING)) {
                    return;
                }

                if (getLoaderManager().getLoader(0) != null) {
                    getLoaderManager().getLoader(0).forceLoad();

                } else {
                    getLoaderManager().initLoader(0, null, OrgApplyingFragment.this).forceLoad();
                }
            }
        };
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mBroadcastReceiver, new IntentFilter(OrgApplyingFragment.REFRESH_ORG_APPLYING));
    }


    private void initData() {
        mApplyingList = new ArrayList<>();
        mOrgApplyingListAdapter = new OrgApplyingListAdapter(getActivity(), mApplyingList);
        mLvOrgApplying.setAdapter(mOrgApplyingListAdapter);
    }

    @Override
    protected void findViews(View view) {
        mLvOrgApplying = view.findViewById(R.id.lw_applying_org);
        mIvBack = view.findViewById(R.id.title_bar_common_back);
        mViewTitleBar = view.findViewById(R.id.applying_org_title);
        mTvTitle = view.findViewById(R.id.title_bar_common_title);
    }

    private void initViews() {
        mTvTitle.setText(getResources().getString(R.string.org_applying));
    }

    private void registerListener() {
        mIvBack.setOnClickListener((v) -> finish());

        mLvOrgApplying.setOnItemClickListener((parent, view, position, id) -> {
            ApplyingOrganization applyingOrganization = mApplyingList.get(position);
            String url = String.format(UrlConstantManager.getInstance().getApplyOrgsUrl(), applyingOrganization.mOrgCode, AtworkConfig.DOMAIN_ID);
            WebViewControlAction webViewControlAction = WebViewControlAction.newAction().setUrl(url).setNeedShare(false);
            Intent intent = WebViewActivity.getIntent(getActivity(), webViewControlAction);

            Executors.newSingleThreadExecutor().execute(() -> {
                OrgApplyManager.getInstance().clearUnreadOrgApply(mActivity, applyingOrganization);
            });

            startActivity(intent);
        });
    }

    @Override
    protected boolean onBackPressed() {
        finish();
        return false;
    }

    @Override
    public Loader<List<ApplyingOrganization>> onCreateLoader(int id, Bundle args) {
        return new OrgApplyingListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<ApplyingOrganization>> loader, List<ApplyingOrganization> data) {

        synchronized (sLock) {
            mApplyingList.clear();
            mApplyingList.addAll(data);

            Collections.sort(mApplyingList);

            mOrgApplyingListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<ApplyingOrganization>> loader) {

    }


    private void unregisterReceiver() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mBroadcastReceiver);
    }

}
