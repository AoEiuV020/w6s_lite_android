package com.foreveross.atwork.modules.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.modules.app.adapter.AppListAdapter;
import com.foreveross.atwork.modules.app.loader.AppListLoader;
import com.foreveross.atwork.support.BackHandledFragment;

import java.util.ArrayList;
import java.util.List;


public class AppListFragment extends BackHandledFragment implements LoaderManager.LoaderCallbacks<List<App>> {

    private static final String TAG = AppListFragment.class.getSimpleName();

    private RecyclerView mRvAppList;
    private ImageView mIvBack;
    private View mViewTitleBar;
    private View mVFakeStatusBar;
    private TextView mTvTitle;

    private AppListAdapter mAppListAdapter;
    private List<App> mAppList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mTvTitle.setText(R.string.my_service_app);
        initRecycleView();

        registerListener();

    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().initLoader(0, null, this).forceLoad();


    }

    @Override
    protected void findViews(View view) {
        mVFakeStatusBar = view.findViewById(R.id.v_fake_statusbar);
        mRvAppList = view.findViewById(R.id.rw_app);
        mIvBack = view.findViewById(R.id.title_bar_common_back);
        mViewTitleBar = view.findViewById(R.id.layout_app_title);
        mTvTitle = mViewTitleBar.findViewById(R.id.title_bar_common_title);
    }

    @Override
    protected View getFakeStatusBar() {
        return null;
    }

    private void initRecycleView() {
        //list布局
        LinearLayoutManager linearLayoutMgr = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRvAppList.setLayoutManager(linearLayoutMgr);
        mAppListAdapter = new AppListAdapter(getActivity(), mAppList);
        mRvAppList.setAdapter(mAppListAdapter);
    }

    private void registerListener() {

        mIvBack.setOnClickListener(v -> finish());

    }


    @Override
    public Loader<List<App>> onCreateLoader(int id, Bundle args) {
        return new AppListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<App>> loader, List<App> data) {
        mAppListAdapter.refreshAppList(data);
    }

    @Override
    public void onLoaderReset(Loader<List<App>> loader) {

    }

    @Override
    protected boolean onBackPressed() {
        finish();
        return false;
    }


}
