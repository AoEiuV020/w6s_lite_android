package com.foreveross.atwork.modules.contact.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.foreveross.atwork.api.sdk.organization.requstModel.QureyOrganizationViewRange;
import com.foreveross.atwork.infrastructure.model.ContactModel;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.modules.contact.component.ContactTreeItemRefresh;
import com.foreveross.atwork.modules.contact.component.EmployeeTreeItemView;
import com.foreveross.atwork.modules.contact.component.OrganizationTreeItemView;
import com.foreveross.atwork.modules.group.activity.UserSelectActivity;
import com.foreveross.atwork.modules.group.listener.DeptSelectedListener;
import com.foreveross.atwork.modules.group.listener.LoadMoreListener;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.foreveross.theme.manager.SkinMaster;

import java.util.List;

/**
 * Created by lingen on 15/4/11.
 * Description:
 */
public class EmployeeTreeListAdapter extends ArrayAdapter<ContactModel> {

    private Context context;
    private boolean selectedMode;
    private boolean isSingleContact = false;
    private UserSelectActivity.SelectAction selectAction;
    private Organization mCurrentOrg;
    private List<ShowListItem> mSelectContacts;
    private List<String> mNotAllowedSelectedContacts;

    private DeptSelectedListener deptSelectedListener;
    private LoadMoreListener mLoadMoreListener;
    private boolean mIsSuggestiveHideMe;
    private QureyOrganizationViewRange mCurrentRange;


    public EmployeeTreeListAdapter(Context context, boolean selectedMode, UserSelectActivity.SelectAction selectAction, Organization organization, DeptSelectedListener deptSelectedListener, LoadMoreListener loadMoreListener) {
        super(context, 0);
        this.context = context;
        this.selectedMode = selectedMode;
        this.selectAction = selectAction;
        this.mCurrentOrg = organization;
        this.deptSelectedListener = deptSelectedListener;
        this.mLoadMoreListener = loadMoreListener;
    }

    public void setCurrentRange(QureyOrganizationViewRange range) {
        mCurrentRange = range;
    }

    public QureyOrganizationViewRange getCurrentRange() {
        return mCurrentRange;
    }

    public void setSelectContacts(List<ShowListItem> selectContacts) {
        this.mSelectContacts = selectContacts;
    }

    public void setNotAllowedSelectedContacts(List<String> notAllowedSelectedContacts) {
        this.mNotAllowedSelectedContacts = notAllowedSelectedContacts;
    }

    public List<ShowListItem> getSelectContacts() {
        return mSelectContacts;
    }

    public List<String> getNotAllowedSelectedContacts() {
        return mNotAllowedSelectedContacts;
    }

    public void refreshOrg(Organization org) {
        mCurrentOrg = org;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ContactModel contactModel = getItem(position);

        if (null == convertView)  {
            if (Type.ORG == getItemViewType(position)) {
                OrganizationTreeItemView organizationTreeItemView = new OrganizationTreeItemView(context, mCurrentOrg, mCurrentRange);
                convertView = organizationTreeItemView;

            } else {
                EmployeeTreeItemView employeeTreeItemView = new EmployeeTreeItemView(context, mCurrentRange);
                convertView = employeeTreeItemView;
            }
        }

        if (convertView instanceof OrganizationTreeItemView) {
            OrganizationTreeItemView organizationTreeItemView = (OrganizationTreeItemView) convertView;
            organizationTreeItemView.refreshOrg(mCurrentOrg);
            organizationTreeItemView.setCurrentRange(mCurrentRange);
            organizationTreeItemView.setListener(deptSelectedListener, mLoadMoreListener);
            organizationTreeItemView.setSuggestiveHideMe(mIsSuggestiveHideMe);

            if (position == 0) {
                ImageCacheHelper.displayImageByMediaId(mCurrentOrg.mLogo, organizationTreeItemView.topImageView, ImageCacheHelper.getOrgLogoOptions());
            }
            organizationTreeItemView.hideLoadMore(true);

            int refreshLast = mCurrentRange.getOrgSkip() + mCurrentRange.getOrgLimit();
            if(refreshLast > position){
                contactModel.isLast = false;
            }
            if (contactModel.isLast && !contactModel.isLoadCompleted && mCurrentRange.getOrgSkip() == 0) {
                organizationTreeItemView.hideLoadMore(false);
            }

        }

        if (convertView instanceof EmployeeTreeItemView) {
            EmployeeTreeItemView employeeTreeItemView = (EmployeeTreeItemView) convertView;
            employeeTreeItemView.setListener(mLoadMoreListener);
            employeeTreeItemView.hideLoadMore(true);
            employeeTreeItemView.setCurrentRange(mCurrentRange);
            int refreshLast = mCurrentRange.getEmployeeSkip() + mCurrentRange.getEmployeeLimit();
            if(refreshLast > position){
                contactModel.isLast = false;
            }
            if (contactModel.isLast && !contactModel.isLoadCompleted) {
                employeeTreeItemView.hideLoadMore(false);
            }
        }
        SkinMaster.getInstance().changeTheme((ViewGroup) convertView);
        if (isSingleContact) {
            ((ContactTreeItemRefresh) convertView).refreshView(contactModel, false, selectAction, mSelectContacts, mNotAllowedSelectedContacts);
        } else {
            ((ContactTreeItemRefresh) convertView).refreshView(contactModel, selectedMode, selectAction, mSelectContacts, mNotAllowedSelectedContacts);
        }

        return convertView;
    }

    public void setSelectedMode(boolean selectedMode) {
        this.selectedMode = selectedMode;
        notifyDataSetChanged();
    }

    public void setSingleContact(boolean isContact) {
        isSingleContact = isContact;
    }


    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        ContactModel contactModel = getItem(position);
        if(ContactModel.ContactType.Organization.equals(contactModel.type())) {
            return Type.ORG;
        }

        return Type.EMPLOYEE;
    }


    public void setSuggestiveHideMe(boolean suggestiveHideMe) {
        this.mIsSuggestiveHideMe = suggestiveHideMe;
    }

    class Type {
        public static final int EMPLOYEE = 0;

        public static final int ORG = 1;
    }

}
