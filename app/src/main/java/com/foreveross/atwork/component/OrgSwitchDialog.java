package com.foreveross.atwork.component;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.theme.manager.SkinHelper;
import com.foreveross.theme.manager.SkinMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shadow on 2016/5/31.
 */
public class OrgSwitchDialog extends DialogFragment {

    private static final String DATA_TYPE = "data_type";

    private static final String DATA_TITLE = "data_title";

    private LinearLayout mContentLayout;

    private List<Organization> mOrgList = new ArrayList<>();

    private OrgSwitchItemOnClickListener mListener;

    private TextView mTvTitle;

    private int mType = Type.DEFAULT;

    private String mTitle;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);

        getData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_org_switch, null);
        SkinMaster.getInstance().changeTheme((ViewGroup) view);
        initView(view);
//        registerListener(view);
        setCancelable(true);

        return view;
    }

    public void setData(int type, String title) {
        Bundle bundle = new Bundle();
        bundle.putInt(DATA_TYPE, type);
        bundle.putString(DATA_TITLE, title);
        setArguments(bundle);
    }

    public void getData() {
        Bundle bundle = getArguments();
        if(null != bundle) {
            mType = bundle.getInt(DATA_TYPE, Type.DEFAULT);
            mTitle = bundle.getString(DATA_TITLE);
        }
    }

    private void initView(View view) {
        mTvTitle = view.findViewById(R.id.title);
        mContentLayout = view.findViewById(R.id.org_switch_layout);
        String currentOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(getContext());

        for (Organization organization : mOrgList) {
            OrgSwitchDialogItemView dialogItem = new OrgSwitchDialogItemView(getContext());
            dialogItem.setOrgName(organization.getNameI18n(BaseApplicationLike.baseContext));

            if (Type.DEFAULT == mType) {
                if (currentOrgCode.equals(organization.mOrgCode)) {
                    dialogItem.setSelectImg(R.mipmap.org_switch_select);
                    dialogItem.setTextColor(SkinHelper.getTabActiveColor());
                } else {
                    dialogItem.setSelectImg(R.mipmap.org_switch_no_select);
                }
                dialogItem.setSelectVisible(View.VISIBLE);

            } else {
                dialogItem.setSelectVisible(View.GONE);
            }

            mContentLayout.addView(dialogItem);

        }

        OrgSwitchDialogItemView itemView = (OrgSwitchDialogItemView) mContentLayout.getChildAt(mOrgList.size() - 1);
        itemView.hideLine();

        mTvTitle.setText(mTitle);
    }

    public void setOrgData(List<Organization> orgData) {
        mOrgList.clear();
        mOrgList.addAll(orgData);
    }

    public final class Type {
        public static final int DEFAULT = 0;

        public static final int SHARE_CIRCLE = 1;

    }

    public void setItemOnClickListener(OrgSwitchItemOnClickListener listener){
        mListener = listener;
    }


    public interface OrgSwitchItemOnClickListener{
        void onItemClick(Organization organization);
    }


}
