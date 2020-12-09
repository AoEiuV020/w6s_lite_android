package com.foreveross.atwork.modules.contact.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.modules.contact.activity.PersonalInfoActivity;
import com.foreveross.atwork.modules.contact.activity.SyncContactFailedActivity;
import com.foreveross.atwork.modules.contact.adapter.SyncContactFailedAdapter;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.ErrorHandleUtil;

import java.util.ArrayList;

/**
 * Created by dasunsy on 2015/6/29 0029.
 */
public class SyncContactFailedFragment extends BackHandledFragment {

    private static final String TAG = SyncContactFailedFragment.class.getSimpleName();

    private ListView mListView;
    private TextView mTvTitle;
    private ImageView mIvBack;

    private ArrayList<ShowListItem> mContactListFailed = new ArrayList<>();
    private SyncContactFailedAdapter mSyncContactFailedAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sync_contact_failed, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mSyncContactFailedAdapter = new SyncContactFailedAdapter(getActivity(), mContactListFailed);
        mListView.setAdapter(mSyncContactFailedAdapter);
        registerClickListener();
    }

    @Override
    protected void findViews(View view) {
        mListView = view.findViewById(R.id.lv_record_sync_failed);
        mTvTitle = view.findViewById(R.id.title_bar_common_title);
        mIvBack = view.findViewById(R.id.title_bar_common_back);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            ArrayList<ShowListItem> contactArrayList = (ArrayList<ShowListItem>) bundle.getSerializable(SyncContactFailedActivity.BUNDLE_USER_LIST);
            mContactListFailed.clear();
            mContactListFailed.addAll(contactArrayList);

            mTvTitle.setText(getString(R.string.sync_fail_record, mContactListFailed.size()));
            mSyncContactFailedAdapter.notifyDataSetChanged();
        }
    }

    private void registerClickListener() {
        mIvBack.setOnClickListener(v -> {
            finish();
        });

        mListView.setOnItemClickListener((parent, view, position, id) -> {
            ShowListItem contact = mContactListFailed.get(position);
            UserManager.getInstance().queryUserByUserId(mActivity, contact.getId(), contact.getDomainId(), new UserAsyncNetService.OnQueryUserListener() {
                @Override
                public void onSuccess(@NonNull User user) {
                    startActivity(PersonalInfoActivity.getIntent(getActivity(), user));

                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    ErrorHandleUtil.handleBaseError(errorCode, errorMsg);
                }
            });
        });
    }

    @Override
    protected boolean onBackPressed() {
        finish();
        return false;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onDetach() {
        super.onDetach();
    }
}
