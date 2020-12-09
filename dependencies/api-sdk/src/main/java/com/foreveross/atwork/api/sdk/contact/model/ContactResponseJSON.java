package com.foreveross.atwork.api.sdk.contact.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.infrastructure.model.employee.EmployeePropertyRecord;
import com.foreveross.atwork.infrastructure.model.ContactModel;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.user.Contact;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingen on 15/4/14.
 * Description:
 * 同步用户的Response对象
 */
public class ContactResponseJSON implements Parcelable {

    @SerializedName("status")
    public int status;

    @SerializedName("message")
    public String message;

    @SerializedName("result")
    public List<ContactResult> result;


    public List<Contact> toContacts(Context context) {
        List<Contact> contacts = new ArrayList<>();
        for (ContactResult contactResult : result) {
            Contact contact = new Contact();
            contact.identifier = contactResult.id;
            contact.avatar = contactResult.avatar;
            contact.name = contactResult.name;
            contact.pinyin = contactResult.pinyin;
            contact.accountName = contactResult.accountName;
            contact.mobile = contactResult.mobile;
            contact.email = contactResult.email;
            String orgTitle = "";
            String jobTitle = "";
            String comName = "";

            if (contactResult.positions != null) {
                for (int i = 0; i < contactResult.positions.size(); i++) {
                    OrgInfoResult orgInfoResult = contactResult.positions.get(i);
                    if (i != 0) {
                        orgTitle = orgTitle + Contact.SPLIT + StringUtils.notNull(orgInfoResult.deptName);
                        jobTitle = jobTitle + Contact.SPLIT + StringUtils.notNull(orgInfoResult.jobTile);
                        comName = comName + Contact.SPLIT + StringUtils.notNull(orgInfoResult.comName);
                    }else {
                        orgTitle = orgTitle + StringUtils.notNull(orgInfoResult.deptName);
                        jobTitle = jobTitle + StringUtils.notNull(orgInfoResult.jobTile);
                        comName = comName + StringUtils.notNull(orgInfoResult.comName);
                    }
                }
            }
            contact.positions = toPositions(contactResult);

            contact.gender = contactResult.gender;
            contact.tenantId = contactResult.tenantId;
            contact.sortOrder = contactResult.sortOrder;
            contact.senior = contactResult.senior? 1 : 0;
            contact.nickName = contactResult.nickName;
            contact.sn = contactResult.sn;
            contact.birthday = contactResult.birthday;
            contact.initial = contactResult.initial;
            contact.province = contactResult.province;
            contact.city = contactResult.city;
            contact.tel = contactResult.tel;
            contact.fax = contactResult.fax;
            contact.work_phone = contactResult.workPhone;
            contact.other_phone = contactResult.otherPhone;
            contact.other_email = contactResult.otherEmail;
            contact.industry = contactResult.industry;
            contact.label = contactResult.label;
            contact.region = contactResult.region;
            contact.extended3 = contactResult.extended3;
            contact.extended2 = contactResult.extended2;
            contact.extended1 = contactResult.extended1;
            contact.disable = contactResult.disable ? 1 : 0;
            contact.location = contactResult.location;
            contact.status = contactResult.status;
            contact.mEmployeePropertyRecord = contactResult.employeePropertyRecord;

            contact.orgName = orgTitle;
            contact.jobTitle = jobTitle;
            contact.comName = comName;
            contact.moreInfo = contactResult.moreInfo;
            contacts.add(contact);
        }
        return contacts;
    }

    private List<Contact.Position> toPositions(ContactResult contactResult) {
        List<Contact.Position> positionList = new ArrayList<>();

        for(OrgInfoResult result : contactResult.positions) {
            Contact.Position position = new Contact.Position();
            position.comId = result.comId;
            position.comName = result.comName;
            position.deptId = result.deptId;
            position.deptName = result.deptName;
            position.jobTile = result.jobTile;
            position.path = result.path;
            position.type = result.type;
            position.primary = result.primary;

            positionList.add(position);
        }

        return positionList;
    }

    public static class ContactResult implements Parcelable {

        @SerializedName("id")
        public String id;

        @SerializedName("avatar")
        public String avatar;

        @SerializedName("type")
        public SessionType type = SessionType.User;

        @SerializedName("name")
        public String name;

        @SerializedName("pinyin")
        public String pinyin;

        @SerializedName("account_name")
        public String accountName;

        @SerializedName("mobile")
        public String mobile;

        @SerializedName("email")
        public String email;

        @SerializedName("positions")
        public List<OrgInfoResult> positions = new ArrayList<>();

        @SerializedName("gender")
        public String gender;

        @SerializedName("tenant_Id")
        public String tenantId;

        @SerializedName("sort_order")
        public int sortOrder;

        /**
         * 是否为高管
         */

        @SerializedName("senior")
        public boolean senior;

        /**
         * 昵称
         */
        @SerializedName("nick_name")
        public String nickName;

        /**
         * 序列号
         */

        @SerializedName("sn")
        public String sn;

        /**
         * 生日
         */
        @SerializedName("birthday")
        public long birthday;

        /**
         * 简拼
         */
        @SerializedName("initial")
        public String initial;

        /**
         * 省份
         */
        @SerializedName("province")
        public String province;

        /**
         * 城市
         */
        @SerializedName("city")
        public String city;

        /**
         * 固话
         */
        @SerializedName("tel")
        public String tel;

        /**
         *传真
         */
        @SerializedName("fax")
        public String fax;

        /**
         * 工作电话
         */
        @SerializedName("work_phone")
        public String workPhone;

        /**
         * 其他电话
         */
        @SerializedName("other_phone")
        public String otherPhone;

        /**
         * 其他邮箱
         */
        @SerializedName("other_email")
        public String otherEmail;

        /**
         *
         */
        @SerializedName("industry")
        public String industry;

        /**
         * 民族
         */
        @SerializedName("region")
        public String region;

        /**
         * 标签
         */
        @SerializedName("label")
        public String label;

        /**
         * 扩展字段1
         */
        @SerializedName("extended1")
        public String extended1;

        /**
         * 扩展字段2
         */
        @SerializedName("extended2")
        public String extended2;

        /**
         * 扩展字段3
         */

        @SerializedName("extended3")
        public String extended3;

        /**
         * 是否可用
         */
        @SerializedName("disable")
        public boolean disable;

        /**
         * 省-市
         */
        @SerializedName("location")
        public String location;

        @SerializedName("status")
        public String status;

        @SerializedName("more_info")
        public ContactModel.MoreInfo moreInfo;

        @SerializedName("properties")
        public List<EmployeePropertyRecord> employeePropertyRecord;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeString(this.avatar);
            dest.writeInt(this.type == null ? -1 : this.type.ordinal());
            dest.writeString(this.name);
            dest.writeString(this.pinyin);
            dest.writeString(this.accountName);
            dest.writeString(this.mobile);
            dest.writeString(this.email);
            dest.writeTypedList(this.positions);
            dest.writeString(this.gender);
            dest.writeString(this.tenantId);
            dest.writeInt(this.sortOrder);
            dest.writeByte(this.senior ? (byte) 1 : (byte) 0);
            dest.writeString(this.nickName);
            dest.writeString(this.sn);
            dest.writeLong(this.birthday);
            dest.writeString(this.initial);
            dest.writeString(this.province);
            dest.writeString(this.city);
            dest.writeString(this.tel);
            dest.writeString(this.fax);
            dest.writeString(this.workPhone);
            dest.writeString(this.otherPhone);
            dest.writeString(this.otherEmail);
            dest.writeString(this.industry);
            dest.writeString(this.region);
            dest.writeString(this.label);
            dest.writeString(this.extended1);
            dest.writeString(this.extended2);
            dest.writeString(this.extended3);
            dest.writeByte(this.disable ? (byte) 1 : (byte) 0);
            dest.writeString(this.location);
            dest.writeString(this.status);
            dest.writeSerializable(this.moreInfo);
            dest.writeList(this.employeePropertyRecord);
        }

        public ContactResult() {
        }

        protected ContactResult(Parcel in) {
            this.id = in.readString();
            this.avatar = in.readString();
            int tmpType = in.readInt();
            this.type = tmpType == -1 ? null : SessionType.values()[tmpType];
            this.name = in.readString();
            this.pinyin = in.readString();
            this.accountName = in.readString();
            this.mobile = in.readString();
            this.email = in.readString();
            this.positions = in.createTypedArrayList(OrgInfoResult.CREATOR);
            this.gender = in.readString();
            this.tenantId = in.readString();
            this.sortOrder = in.readInt();
            this.senior = in.readByte() != 0;
            this.nickName = in.readString();
            this.sn = in.readString();
            this.birthday = in.readLong();
            this.initial = in.readString();
            this.province = in.readString();
            this.city = in.readString();
            this.tel = in.readString();
            this.fax = in.readString();
            this.workPhone = in.readString();
            this.otherPhone = in.readString();
            this.otherEmail = in.readString();
            this.industry = in.readString();
            this.region = in.readString();
            this.label = in.readString();
            this.extended1 = in.readString();
            this.extended2 = in.readString();
            this.extended3 = in.readString();
            this.disable = in.readByte() != 0;
            this.location = in.readString();
            this.status = in.readString();
            this.moreInfo = (ContactModel.MoreInfo) in.readSerializable();
            this.employeePropertyRecord = new ArrayList<EmployeePropertyRecord>();
            in.readList(this.employeePropertyRecord, EmployeePropertyRecord.class.getClassLoader());
        }

        public static final Parcelable.Creator<ContactResult> CREATOR = new Parcelable.Creator<ContactResult>() {
            @Override
            public ContactResult createFromParcel(Parcel source) {
                return new ContactResult(source);
            }

            @Override
            public ContactResult[] newArray(int size) {
                return new ContactResult[size];
            }
        };
    }

    public static class OrgInfoResult implements Parcelable {

        @SerializedName("org_id")
        public String deptId;

        @SerializedName("org_name")
        public String deptName;

        @SerializedName("com_id")
        public String comId;

        @SerializedName("com_name")
        public String comName;

        @SerializedName("job_title")
        public String jobTile;

        @SerializedName("path")
        public String path;

        @SerializedName("type")
        public String type;

        @SerializedName("primary")
        public boolean primary;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.deptId);
            dest.writeString(this.deptName);
            dest.writeString(this.comId);
            dest.writeString(this.comName);
            dest.writeString(this.jobTile);
            dest.writeString(this.path);
            dest.writeString(this.type);
            dest.writeByte(this.primary ? (byte) 1 : (byte) 0);
        }

        public OrgInfoResult() {
        }

        protected OrgInfoResult(Parcel in) {
            this.deptId = in.readString();
            this.deptName = in.readString();
            this.comId = in.readString();
            this.comName = in.readString();
            this.jobTile = in.readString();
            this.path = in.readString();
            this.type = in.readString();
            this.primary = in.readByte() != 0;
        }

        public static final Parcelable.Creator<OrgInfoResult> CREATOR = new Parcelable.Creator<OrgInfoResult>() {
            @Override
            public OrgInfoResult createFromParcel(Parcel source) {
                return new OrgInfoResult(source);
            }

            @Override
            public OrgInfoResult[] newArray(int size) {
                return new OrgInfoResult[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.status);
        dest.writeString(this.message);
        dest.writeTypedList(this.result);
    }

    public ContactResponseJSON() {
    }

    protected ContactResponseJSON(Parcel in) {
        this.status = in.readInt();
        this.message = in.readString();
        this.result = in.createTypedArrayList(ContactResult.CREATOR);
    }

    public static final Parcelable.Creator<ContactResponseJSON> CREATOR = new Parcelable.Creator<ContactResponseJSON>() {
        @Override
        public ContactResponseJSON createFromParcel(Parcel source) {
            return new ContactResponseJSON(source);
        }

        @Override
        public ContactResponseJSON[] newArray(int size) {
            return new ContactResponseJSON[size];
        }
    };
}
