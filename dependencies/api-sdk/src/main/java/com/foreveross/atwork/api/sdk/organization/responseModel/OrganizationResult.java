package com.foreveross.atwork.api.sdk.organization.responseModel;

import android.content.Context;
import android.os.Parcel;

import com.foreveross.atwork.infrastructure.model.ContactModel;
import com.foreveross.atwork.infrastructure.model.orgization.Department;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.model.orgization.Scope;
import com.foreveross.atwork.infrastructure.model.user.Contact;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 代表一个组织
 */
public class OrganizationResult extends ContactModel {

    @SerializedName("org_code")
    public String orgCode;

    @SerializedName("logo")
    public String logo;

    @SerializedName("path")
    public String path;

    @SerializedName("sn")
    public String sn;

    @SerializedName("employee_count")
    public int employeeCount;

    @SerializedName("all_employee_count")
    public int allEmployeeCount;

    /**
     * 主要是用于展示组织架构的人数，为true表示显示这个组织架构的人，false，表示不显示
     * */
    @SerializedName("counting")
    public boolean counting;

    public List<OrganizationResult> children;

    @SerializedName("employees")
    public List<EmployeeResult> employeeResults;

    //用于组织架构树
    public List<ContactModel> contactModels;

    public boolean hasChildrenData() {
        return !ListUtil.isEmpty(children)  || !ListUtil.isEmpty(employeeResults) ;
    }

    /**
     * 类型
     *
     * @return
     */
    @Override
    public ContactType type() {
        return ContactType.Organization;
    }

    /**
     * 组织或部门下的总人数
     *
     * @return
     */
    @Override
    public int num() {
        return allEmployeeCount;
    }


    /**
     * 下面的子元素
     *
     * @return
     */
    @Override
    public List<ContactModel> subContactModel() {
        if (contactModels == null) {
            contactModels = new ArrayList<>();
        }
        contactModels.clear();
        if(!ListUtil.isEmpty(employeeResults)) {
            employeeResults.get(employeeResults.size() -1).isLast = true;
            if (employeeResults.size() < 10) {
                employeeResults.get(employeeResults.size() -1).isLoadCompleted = true;
            }
        }
//            contactModels.removeAll(employees);
        contactModels.addAll(employeeResults);
        if (!ListUtil.isEmpty(children)) {
            children.get(children.size() - 1).isLast = true;
        }
//            contactModels.removeAll(children);

        contactModels.addAll(children);
        return contactModels;
    }

    public boolean canSelect(final OrganizationResult orgSelected) {
        return !path.contains(orgSelected.path);
    }

    public boolean canSelect(final Organization orgSelected) {
        return !path.contains(orgSelected.mPath);
    }

    public boolean isSelected(Context context, boolean needFilterSelf) {
        if (subContactModel().size() == 0) {
            return selected;

        } else {
            for (EmployeeResult employeeResult : employeeResults) {

                if(needFilterSelf && User.isYou(context, employeeResult.userId)) {
                    continue;
                }

                if (!employeeResult.selected) {
                    return false;
                }
            }
            for (OrganizationResult organization : children) {
                if (!organization.isSelected(context, needFilterSelf)) {
                    return false;
                }
            }
            return true;
        }
    }

    public Scope transfer(Context context) {
        return new Scope(id, path, getTitleI18n(context), this.toOrganization());
    }

    /**
     * 模型转换
     * */
    public com.foreveross.atwork.infrastructure.model.orgization.Organization toOrganization() {
        com.foreveross.atwork.infrastructure.model.orgization.Organization org = new com.foreveross.atwork.infrastructure.model.orgization.Organization();
        org.mId = id;
        org.mDomainId = domainId;
        org.mOrgCode = orgCode;
        org.mName = name;
        org.mPath = path;
        org.mLogo = logo;

        return org;
    }

    public Department toDepartment() {
        Department department = new Department();
        department.mId = id;
        department.mDomainId = domainId;
        department.mParentOrgId = parentOrgId;
        department.mOrgCode = orgCode;
        department.mName = name;
        department.mEmployeeCount = employeeCount;
        department.mAllEmployeeCount = allEmployeeCount;
        department.mPath = path;

        return department;

    }


    public static OrganizationResult toOrganizationResult(Organization organization) {
        OrganizationResult organizationResult = new OrganizationResult();

        organizationResult.id = organization.mId;
        organizationResult.domainId = organization.mDomainId;
        organizationResult.orgCode = organization.mOrgCode;
        organizationResult.name = organization.mName;
        organizationResult.path = organization.mPath;
        organizationResult.logo = organization.mLogo;
        organizationResult.path = organization.mPath;
        organizationResult.children = new ArrayList<>();
        organizationResult.employeeResults = new ArrayList<>();
        organizationResult.contactModels = new ArrayList<>();

        return organizationResult;
    }


    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getTitleI18n(Context context) {
        return name;
    }

    @Override
    public String getTitlePinyin() {
        return StringUtils.EMPTY;
    }

    @Override
    public String getParticipantTitle() {
        return name;
    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public String getAvatar() {
        return logo;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDomainId() {
        return domainId;
    }

    @Override
    public String getStatus() {
        return null;
    }

    @Override
    public boolean isSelect() {
        return selected;
    }

    @Override
    public void select(boolean isSelect) {
        selected = isSelect;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    public OrganizationResult() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.logo);
        dest.writeString(this.path);
        dest.writeString(this.sn);
        dest.writeInt(this.employeeCount);
        dest.writeInt(this.allEmployeeCount);
        dest.writeByte(this.counting ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.children);
        dest.writeTypedList(this.employeeResults);
        dest.writeList(this.contactModels);
        dest.writeString(this.domainId);
        dest.writeString(this.parentOrgId);
        dest.writeString(this.orgCode);
        dest.writeString(this.id);
        dest.writeString(this.avatar);
        dest.writeString(this.name);
        dest.writeInt(this.loadingStatus);
        dest.writeList(new ArrayList(this.loadedStatus));
        dest.writeByte(this.expand ? (byte) 1 : (byte) 0);
        dest.writeInt(this.level);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
        dest.writeByte(this.top ? (byte) 1 : (byte) 0);
        dest.writeSerializable(this.moreInfo);
        dest.writeByte(this.isLast ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isLoadCompleted ? (byte) 1 : (byte) 0);
    }

    protected OrganizationResult(Parcel in) {
        this.logo = in.readString();
        this.path = in.readString();
        this.sn = in.readString();
        this.employeeCount = in.readInt();
        this.allEmployeeCount = in.readInt();
        this.counting = in.readByte() != 0;
        this.children = in.createTypedArrayList(OrganizationResult.CREATOR);
        this.employeeResults = in.createTypedArrayList(EmployeeResult.CREATOR);
        this.contactModels = new ArrayList<ContactModel>();
        in.readList(this.contactModels, ContactModel.class.getClassLoader());
        this.domainId = in.readString();
        this.parentOrgId = in.readString();
        this.orgCode = in.readString();
        this.id = in.readString();
        this.avatar = in.readString();
        this.name = in.readString();
        this.loadingStatus = in.readInt();
        this.loadedStatus = new HashSet<>();
        this.loadedStatus.addAll(in.readArrayList(Integer.class.getClassLoader()));
        this.expand = in.readByte() != 0;
        this.level = in.readInt();
        this.selected = in.readByte() != 0;
        this.top = in.readByte() != 0;
        this.moreInfo = (MoreInfo) in.readSerializable();
        this.isLast = in.readByte() != 0;
        this.isLoadCompleted = in.readByte() != 0;
    }

    public static final Creator<OrganizationResult> CREATOR = new Creator<OrganizationResult>() {
        @Override
        public OrganizationResult createFromParcel(Parcel source) {
            return new OrganizationResult(source);
        }

        @Override
        public OrganizationResult[] newArray(int size) {
            return new OrganizationResult[size];
        }
    };
}
