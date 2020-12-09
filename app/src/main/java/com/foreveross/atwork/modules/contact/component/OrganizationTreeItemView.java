package com.foreveross.atwork.modules.contact.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.organization.requstModel.QureyOrganizationViewRange;
import com.foreveross.atwork.api.sdk.organization.responseModel.EmployeeResult;
import com.foreveross.atwork.api.sdk.organization.responseModel.OrganizationResult;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.manager.OrganizationSettingsManager;
import com.foreveross.atwork.infrastructure.model.ContactModel;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.SetUtil;
import com.foreveross.atwork.modules.contact.util.ContactInfoViewUtil;
import com.foreveross.atwork.modules.group.activity.UserSelectActivity;
import com.foreveross.atwork.modules.group.listener.DeptSelectedListener;
import com.foreveross.atwork.modules.group.listener.LoadMoreListener;

import java.util.List;


public class OrganizationTreeItemView extends RelativeLayout implements ContactTreeItemRefresh {

    private static int intWidth;
    private View mVRoot;
    private TextView nameView;
    private TextView numView;
    private FrameLayout flExpandView;
    private ImageView ivExpandView;
    private ProgressBar pbExpandView;
    private TextView level;
    private boolean selectedMode = false;
    private UserSelectActivity.SelectAction selectAction = null;
    private ImageView selectView;
    private DeptSelectedListener deptSelectedListener;
    private LoadMoreListener mLoadMoreListener;
    private OrganizationResult organization;
    private Organization mCurrentOrg;
    private List<ShowListItem> mSelectContacts;
    private Context mContext;
    private QureyOrganizationViewRange mRange;


    public ImageView topImageView;

    private FrameLayout flTopMoreView;
    private ImageView ivTopMoreView;
    private ProgressBar pbTopMoreView;

    private boolean mIsSuggestiveHideMe;
    private TextView mLoadMore;


    public OrganizationTreeItemView(Context context, Organization currentOrg, QureyOrganizationViewRange range) {
        super(context);
        mContext = context;
        mCurrentOrg = currentOrg;
        mRange = range;
        initView();
        registerListener();

    }

    public void refreshOrg(Organization organization) {
        mCurrentOrg = organization;
    }
    public void setCurrentRange(QureyOrganizationViewRange range) {
        mRange = range;
    }


    private void registerListener() {
        selectView.setOnClickListener(v -> deptSelectedListener.select(organization, new QureyOrganizationViewRange()));
        mLoadMore.setOnClickListener(view -> {
            QureyOrganizationViewRange qureyOrganizationViewRange  = new QureyOrganizationViewRange();
            qureyOrganizationViewRange.setOrgLimit(mRange.getOrgLimit() + QureyOrganizationViewRange.QueryRangeConst.queryLimit);
            qureyOrganizationViewRange.setEmployeeLimit(mRange.getEmployeeLimit());
            mLoadMoreListener.onLoadMore(organization, qureyOrganizationViewRange);
        });
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_organization_group, this);
        mVRoot = view.findViewById(R.id.rl_root);
        nameView = view.findViewById(R.id.org_name);
        numView = view.findViewById(R.id.org_num);
        ivExpandView = view.findViewById(R.id.iv_expand_view);
        pbExpandView = view.findViewById(R.id.pb_expand_view);
        flExpandView = view.findViewById(R.id.fl_expand_view);
        level = view.findViewById(R.id.group_level);
        selectView = view.findViewById(R.id.group_select);
        topImageView = view.findViewById(R.id.group_top_image);
        ivTopMoreView = view.findViewById(R.id.iv_org_top_more);
        pbTopMoreView = view.findViewById(R.id.pb_org_top_more);
        flTopMoreView = view.findViewById(R.id.fl_org_top_more);
        mLoadMore = view.findViewById(R.id.load_more);
    }


    private void topOrgModel() {
        topImageView.setVisibility(VISIBLE);
//        ivTopMoreView.setVisibility(VISIBLE);
        numView.setVisibility(GONE);
//        ivExpandView.setVisibility(GONE);
    }

    private void notTopOrgModel() {
        topImageView.setVisibility(GONE);
//        ivTopMoreView.setVisibility(GONE);
        numView.setVisibility(VISIBLE);
//        ivExpandView.setVisibility(VISIBLE);
    }

    private void refreshSelected() {
        if(selectedMode && selectAction == UserSelectActivity.SelectAction.SCOPE) {
            selectView.setVisibility(VISIBLE);

            if(!ContactInfoViewUtil.canEmployeeTreeOrgSelect(organization, mSelectContacts)) {
                selectView.setImageResource(R.mipmap.icon_selected_disable_new);
                return;
            }

            if(organization.isSelect()) {
                selectView.setImageResource(R.mipmap.icon_selected);
            } else {
                selectView.setImageResource(R.mipmap.icon_seclect_no_circular);
            }
            return;
        }


        if (selectedMode &&
                !( OrganizationSettingsManager.getInstance().getOrgSelectNode(mCurrentOrg.mOrgCode) == DomainSettingsManager.SYNC_CONTACT_PERSONAL)) {
            selectView.setVisibility(VISIBLE);
            if (organization.isSelected(BaseApplicationLike.baseContext, mIsSuggestiveHideMe)) {
                selectView.setImageResource(R.mipmap.icon_selected);
            } else {
                selectView.setImageResource(R.mipmap.icon_seclect_no_circular);
            }
        } else {
            selectView.setVisibility(GONE);
        }
    }

    @Override
    public void refreshView(ContactModel contactModel, boolean selectedMode, UserSelectActivity.SelectAction selectAction, List<ShowListItem> selectContacts, List<String> notAllowedSelectedContacts) {

        this.mSelectContacts = selectContacts;
        this.selectedMode = selectedMode;
        this.selectAction = selectAction;
        this.organization = (OrganizationResult) contactModel;

        switchOrgView(contactModel);
        refreshSelected();


        if(mCurrentOrg.mId.equals(contactModel.id)) {
            nameView.setText(mCurrentOrg.getNameI18n(BaseApplicationLike.baseContext));

        } else {
            nameView.setText(contactModel.name);

        }
        if (OrganizationSettingsManager.getInstance().handleOrgMembersCountingFeature(mCurrentOrg.mOrgCode)) {
            numView.setText(getResources().getString(R.string.person, contactModel.num()));
        }

        if (((OrganizationResult) contactModel).counting && OrganizationSettingsManager.getInstance().handleOrgMembersCountingFeature(mCurrentOrg.mOrgCode)){
            numView.setText(getResources().getString(R.string.person, contactModel.num()));
        }else {
            numView.setVisibility(GONE);
        }

        RelativeLayout.LayoutParams margin = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        margin.setMargins((int) ((contactModel.level) * DensityUtil.DP_8_TO_PX * 1.5), 0, 0, 0);
        level.setLayoutParams(margin);
    }

    private void switchOrgView(ContactModel contactModel){


        handleLoadedView(contactModel);

        if (isTop(contactModel)) {
            topOrgModel();
        } else {
            notTopOrgModel();
        }

        if (!contactModel.expand) {

            ivExpandView.setImageResource(R.mipmap.icon_down_11);
            ivTopMoreView.setImageResource(R.mipmap.icon_down_11);

//            nameView.setTextColor(SkinHelper.getPrimaryTextColor());
        } else {
            ivExpandView.setImageResource(R.mipmap.icon_down_1);
            ivTopMoreView.setImageResource(R.mipmap.icon_down_1);

            //3.0.4开始展开不用颜色
//            if(1 == contactModel.level){
//                nameView.setTextColor(SkinHelper.getMainColor());
//            }
        }
    }

    private void handleLoadedView(ContactModel contactModel) {

        if (isTop(contactModel)) {
            flTopMoreView.setVisibility(VISIBLE);
            flExpandView.setVisibility(GONE);

            if (shouldShowLoading(contactModel)) {
                pbTopMoreView.setVisibility(VISIBLE);
                ivTopMoreView.setVisibility(INVISIBLE);
            } else {
                pbTopMoreView.setVisibility(INVISIBLE);
                ivTopMoreView.setVisibility(VISIBLE);
            }

        } else {
            flTopMoreView.setVisibility(GONE);
            flExpandView.setVisibility(VISIBLE);


            if (shouldShowLoading(contactModel)) {
                pbExpandView.setVisibility(VISIBLE);
                ivExpandView.setVisibility(INVISIBLE);
            } else {
                pbExpandView.setVisibility(INVISIBLE);
                ivExpandView.setVisibility(VISIBLE);
            }
        }

    }

    private boolean shouldShowLoading(ContactModel contactModel) {
        if(contactModel instanceof EmployeeResult) {
            return false;
        }

        if(!contactModel.isLoading()) {
            return false;
        }

        if(SetUtil.isEmpty(contactModel.getLoadedStatus())) {
            return false;
        }

        OrganizationResult organizationResult = (OrganizationResult) contactModel;
        if(organizationResult.hasChildrenData()) {
            return false;
        }

        return true;

    }


    private boolean isTop(ContactModel contactModel) {
        return contactModel.top && 0 == contactModel.level;
    }

    public int getSelectedModeWidth() {
        if (intWidth == 0) {
            intWidth = DensityUtil.dip2px(15);
        }

        if (selectedMode) {
            return intWidth;
        }
        return 0;
    }

    public void setListener(DeptSelectedListener deptSelectedListener, LoadMoreListener listener) {
        this.deptSelectedListener = deptSelectedListener;
        mLoadMoreListener = listener;
    }


    public void setSuggestiveHideMe(boolean suggestiveHideMe) {
        this.mIsSuggestiveHideMe = suggestiveHideMe;
    }

    public void hideLoadMore(boolean hide) {
        mLoadMore.setVisibility(hide ? GONE : VISIBLE);
    }
}
