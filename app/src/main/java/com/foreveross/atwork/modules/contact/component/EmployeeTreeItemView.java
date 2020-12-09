package com.foreveross.atwork.modules.contact.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.organization.requstModel.QureyOrganizationViewRange;
import com.foreveross.atwork.api.sdk.organization.responseModel.EmployeeResult;
import com.foreveross.atwork.infrastructure.model.ContactModel;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.contact.util.ContactInfoViewUtil;
import com.foreveross.atwork.modules.group.activity.UserSelectActivity;
import com.foreveross.atwork.modules.group.listener.LoadMoreListener;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.IntentUtil;
import com.foreveross.atwork.utils.WorkplusTextSizeChangeHelper;

import java.util.List;

/**
 * Created by lingen on 15/4/10.
 * Description:
 * 组织架构树中的人员ITEM
 */
public class EmployeeTreeItemView extends RelativeLayout implements ContactTreeItemRefresh {

    private static int sIntWidth;

    private Context mContext;

    private View mVRoot;
    private TextView mTvContactName;
    private TextView mTvJob;
    private TextView mTvLevel;
    private ViewGroup mVgContact;
    private ImageView mIvCall;
    private ImageView mIvSms;
    private ImageView mIvEmail;

    private LoadMoreListener mLoadMoreListener;

    private boolean mIsShowContact = false;

    private EmployeeResult mEmployeeResult;
    private boolean mSelectedMode = false;
    private UserSelectActivity.SelectAction mSelectAction = null;
    private List<ShowListItem> mSelectContacts;


    private ImageView mIvSelect;

    private ImageView mIvAvatar;

    private ContactModel mContactModel;
    private List<String> mNotAllowedSelectedContacts;

    private TextView mLoadMore;
    private QureyOrganizationViewRange mRange;

    public EmployeeTreeItemView(Context context, QureyOrganizationViewRange range) {
        super(context);
        initView();
        registerListener();
        this.mContext = context;
        mRange = range;
        WorkplusTextSizeChangeHelper.handleHeightEnlargedTextSizeStatus(mVRoot);
    }

    public void setListener(LoadMoreListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
    }

    public void setCurrentRange(QureyOrganizationViewRange range) {
        mRange = range;
    }


    public EmployeeTreeItemView(Context context, AttributeSet attrs, boolean selectedMode) {
        super(context, attrs);
        initView();
        registerListener();
        this.mContext = context;
        this.mSelectedMode = selectedMode;
        WorkplusTextSizeChangeHelper.handleHeightEnlargedTextSizeStatus(mVRoot);

    }

    private void registerListener() {

        //打电话事件
        mIvCall.setOnClickListener(v -> {
            if (StringUtils.isEmpty(mEmployeeResult.mobile)) {
                AtworkToast.showToast(getResources().getString(R.string.personal_info_no_mobile));
            } else {
                new AtworkAlertDialog(getContext(), AtworkAlertDialog.Type.SIMPLE)
                        .setContent(getResources().getString(R.string.call_phone, mEmployeeResult.mobile))
                        .setClickBrightColorListener(dialog -> IntentUtil.callPhoneJump(mContext, mEmployeeResult.mobile))
                        .show();

            }
        });

        //发短信事件
        mIvSms.setOnClickListener(v -> {
            if (StringUtils.isEmpty(mEmployeeResult.mobile)) {
                AtworkToast.showToast(getResources().getString(R.string.personal_info_no_mobile));
            } else {
                IntentUtil.sendSms(mContext, mEmployeeResult.mobile);
            }
        });

        /**
         * 发邮件
         */
        mIvEmail.setOnClickListener(v -> {
            if (StringUtils.isEmpty(mEmployeeResult.email)) {
                AtworkToast.showToast(getResources().getString(R.string.personal_info_no_email));
            } else {
                IntentUtil.email(mContext, mEmployeeResult.email);
            }
        });

        mLoadMore.setOnClickListener(view -> {
            QureyOrganizationViewRange qureyOrganizationViewRange  = new QureyOrganizationViewRange();
            mRange.setEmployeeSkip(mRange.getEmployeeSkip() + QureyOrganizationViewRange.QueryRangeConst.queryLimit);
            qureyOrganizationViewRange.setEmployeeSkip(mRange.getEmployeeSkip());
            qureyOrganizationViewRange.setOrgLimit(mRange.getOrgLimit());
            mLoadMoreListener.onLoadMore(mEmployeeResult, qureyOrganizationViewRange);
        });
    }


    public boolean isContactShow() {
        return mIsShowContact;
    }


    public void showContact() {
        mVgContact.setVisibility(VISIBLE);
        mIsShowContact = true;
    }

    public void hiddenContact() {
        mVgContact.setVisibility(GONE);
        mIsShowContact = false;
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_organization_child, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mTvContactName = view.findViewById(R.id.tv_name);
        mTvJob = view.findViewById(R.id.tv_mobile);
        mTvLevel = view.findViewById(R.id.employee_level);
        mVgContact = view.findViewById(R.id.employee_contact_group);
        mVgContact.setVisibility(GONE);
        mIvAvatar = view.findViewById(R.id.iv_avatar);
        mIvCall = view.findViewById(R.id.make_call);
        mIvSms = view.findViewById(R.id.make_sms);
        mIvEmail = view.findViewById(R.id.make_email);
        mLoadMore = view.findViewById(R.id.load_more);

        //选择框
        mIvSelect = view.findViewById(R.id.org_contact_select);
        mIvSelect.setVisibility(GONE);
    }

    private void refreshSelected() {
        if (!mSelectedMode) {
            mIvSelect.setVisibility(GONE);
            return;
        }


        mIvSelect.setVisibility(VISIBLE);

        if(UserSelectActivity.SelectAction.SCOPE == mSelectAction&&
                !ContactInfoViewUtil.canEmployeeTreeEmpSelect(mEmployeeResult, mSelectContacts)) {
            mIvSelect.setImageResource(R.mipmap.icon_selected_disable_new);
            return;
        }


        if(null != mNotAllowedSelectedContacts && mNotAllowedSelectedContacts.contains(mEmployeeResult.userId)) {
            mIvSelect.setImageResource(R.mipmap.icon_selected_disable_new);
            return;
        }

        if (mContactModel.selected) {
            mIvSelect.setImageResource(R.mipmap.icon_selected);
        } else {
            mIvSelect.setImageResource(R.mipmap.icon_seclect_no_circular);
        }

    }

    @Override
    public void refreshView(ContactModel contactModel, boolean selectedMode, UserSelectActivity.SelectAction selectAction, List<ShowListItem> selectContacts, List<String> notAllowedSelectedContacts) {
        mEmployeeResult = (EmployeeResult) contactModel;

        this.mSelectContacts = selectContacts;
        this.mNotAllowedSelectedContacts = notAllowedSelectedContacts;
        this.mSelectedMode = selectedMode;
        this.mSelectAction = selectAction;
        this.mContactModel = contactModel;

        refreshSelected();

        dealWithContactInitializedStatus(mEmployeeResult);

        if (AtworkConfig.EMPLOYEE_CONFIG.getShowPeerJobTitle()) {
            mTvJob.setText(mEmployeeResult.getJobTitle());
            mTvJob.setVisibility(VISIBLE);

        } else {
            mTvJob.setVisibility(GONE);

        }
        RelativeLayout.LayoutParams margin = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        margin.setMargins((int) ((contactModel.level) * DensityUtil.DP_8_TO_PX * 1.5), 0, 0, 0);
        mTvLevel.setLayoutParams(margin);
        hiddenContact();
    }

    private void dealWithContactInitializedStatus(EmployeeResult employeeResult) {
        ContactInfoViewUtil.dealWithContactInitializedStatus(mIvAvatar, mTvContactName, employeeResult, true, true);
    }


    public int getSelectedModeWidth() {
        if (sIntWidth == 0) {
            sIntWidth = DensityUtil.dip2px(10);
        }
        if (mSelectedMode) {
            return sIntWidth;
        }
        return 0;
    }

    public void hideLoadMore(boolean hide) {
        mLoadMore.setVisibility(hide ? GONE : VISIBLE);
    }

}
