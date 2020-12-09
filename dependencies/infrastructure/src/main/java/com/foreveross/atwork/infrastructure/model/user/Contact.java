package com.foreveross.atwork.infrastructure.model.user;

import android.content.Context;
import android.os.Parcel;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.model.employee.EmployeePropertyRecord;
import com.foreveross.atwork.infrastructure.model.ContactModel;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingen on 15/4/3.
 * Description:通讯录
 */
public class Contact implements Serializable, ShowListItem, Comparable {

    public static final String EMPTY = "";

    //多部门，职位，公司的分隔符
    public static final String SPLIT = " ";



    /**
     * 头像
     */
    @Expose
    public String avatar = EMPTY;

    /**
     * 姓名
     */
    @Expose
    public String name = EMPTY;

    /**
     * 拼音
     */
    public String pinyin = EMPTY;

    /**
     * 用户名
     */
    @Expose
    public String accountName = EMPTY;

    /**
     * 公司名称
     */
    @Expose
    public String comName = EMPTY;

    /**
     * 机构名称
     */
    @Expose
    public String orgName = EMPTY;

    /**
     * 职位
     */
    @Expose
    public String jobTitle = EMPTY;

    /**
     * 手机
     */
    @Expose
    public String mobile = EMPTY;

    /**
     * 邮箱
     */
    @Expose
    public String email = EMPTY;

    /**
     * 平台
     */
    @Expose
    public String platform = "ANDROID";

    // 注意，username仅供cordova使用
    @Expose
    public String username = "";

    /**
     * 更多
     */
    @Expose
    public ContactModel.MoreInfo moreInfo;

    /**
     * 用户ID
     */
    @Expose
    public String identifier;

    public SessionType type = SessionType.User;
    /**
     * 星标联系人 1 是   0 不是
     */
    public int favor;

    /**
     * 创建时间
     */
    public long createTime;

    public boolean selected;

    /**
     * 首字拼音
     */
    public String firstPinyin;

    /**
     * 性别
     */
    @Expose
    public String gender = EMPTY;


    public long readTime;


    public void select(boolean isSelect) {
        this.selected = !selected;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    //-----------------------------------------新增字段--------------------------------------

    /**
     * 租户ID
     */
    public String tenantId = EMPTY;

    /**
     * 排序号
     */
    public int sortOrder;

    /**
     * 是否为高管
     */
    public int senior;

    /**
     * 昵称
     */
    public String nickName;

    /**
     * 序列号
     */
    public String sn;

    /**
     * 生日
     */
    public long birthday;

    /**
     * 简拼
     */
    public String initial;

    /**
     * 省份
     */
    public String province;

    /**
     * 城市
     */
    public String city;

    /**
     * 固话
     */
    public String tel;

    /**
     * 传真
     */
    public String fax;

    /**
     * 工作电话
     */
    public String work_phone;

    /**
     * 其他电话
     */
    public String other_phone;

    /**
     * 其他邮箱
     */
    public String other_email;

    /**
     *
     */
    public String industry;

    /**
     * 民族
     */
    public String region;

    /**
     * 标签
     */
    public String label;

    /**
     * 扩展字段1
     */
    public String extended1;

    /**
     * 扩展字段2
     */
    public String extended2;

    /**
     * 扩展字段3
     */
    public String extended3;

    /**
     * 是否可用
     */
    public int disable;

    /**
     * 省-市
     */
    public String location;

    public List<EmployeePropertyRecord> mEmployeePropertyRecord;

    /**
     * 职位相关
     */
    @Expose
    public List<Position> positions;

    @Expose
    public String status = User.STATUS_INITIALIZED;


    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof Contact == false) {
            return false;
        }
        Contact other = (Contact) o;
        if (accountName == null || other.accountName == null) {
            return false;
        }
        return accountName.equals(other.accountName);
    }

    @Override
    public String getTitle() {
        if (StringUtils.isEmpty(name)) {
            return accountName;
        } else {
            return name;
        }
    }

    @Override
    public String getTitleI18n(Context context) {
        return null;
    }

    @Override
    public String getTitlePinyin() {
        return pinyin;
    }

    @Override
    public String getParticipantTitle() {
        if (StringUtils.isEmpty(name)) {
            return accountName;
        } else {
            return name;
        }

    }

    @Override
    public String getInfo() {
        if (StringUtils.isEmpty(orgName)) {
            return null;
        }
        if (orgName.contains(SPLIT) && comName.contains(SPLIT) && jobTitle.contains(SPLIT)) {
            List<MultiOrg> multiOrgList = getMultiOrg();
            MultiOrg multiOrg = multiOrgList.get(0);
            return multiOrg.org + " " + multiOrg.job;
        } else {
            return orgName + " " + jobTitle;
        }

    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    @Override
    public String getId() {
        return username;
    }

    @Override
    public String getDomainId() {
        return null;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public boolean isSelect() {
        return false;
    }

    public String getPositionAllShowStr() {
        StringBuilder showSb = new StringBuilder();
        if (!ListUtil.isEmpty(positions)) {

            for (int i = 0; i < positions.size(); i++) {
                Position position = positions.get(i);
                showSb.append(position.comName).append("-").append(position.deptName).append("-").append(position.jobTile);

                if (i != positions.size() - 1) {
                    showSb.append("\r\n");
                }
            }
        }


        if (TextUtils.isEmpty(showSb.toString())) {
            List<Contact.MultiOrg> multiOrgList = getMultiOrg();

            if (multiOrgList.size() == 0) {
                showSb.append(comName).append("-").append(orgName).append("-").append(jobTitle);

            } else {
                for (int i = 0; i < multiOrgList.size(); i++) {
                    Contact.MultiOrg multiOrg = multiOrgList.get(i);
                    showSb.append(comName).append("-").append(multiOrg.org).append("-").append(multiOrg.job);
                    if (i != multiOrgList.size() - 1) {
                        showSb.append("\r\n");
                    }
                }
                showSb.append("\r\n");
            }
        }
        String showSth = showSb.toString();
        showSth = showSth.replaceAll("null-", "");
        return showSth;
    }

    public String getPositionTwoShowStr() {
        StringBuilder showSb = new StringBuilder();

        if (!ListUtil.isEmpty(positions)) {

            for (int i = 0; i < positions.size(); i++) {
                Position position = positions.get(i);
                showSb.append(position.deptName).append("-").append(position.jobTile);

                if (i != positions.size() - 1) {
                    showSb.append("\r\n");
                }
            }
        }

        if (TextUtils.isEmpty(showSb.toString())) {
            List<Contact.MultiOrg> multiOrgList = getMultiOrg();

            if (multiOrgList.size() == 0) {
                showSb.append(orgName).append("-").append(jobTitle);

            } else {
                for (int i = 0; i < multiOrgList.size(); i++) {
                    Contact.MultiOrg multiOrg = multiOrgList.get(i);
                    showSb.append(multiOrg.org).append("-").append(multiOrg.job);
                    if (i != multiOrgList.size() - 1) {
                        showSb.append("\r\n");
                    }
                }
                showSb.append("\r\n");
            }
        }

        return showSb.toString();
    }

    @Override
    public int compareTo(Object another) {
        if (another == null || !(another instanceof Contact)) {
            return 0;
        }
        Contact other = (Contact) another;
        if (StringUtils.isEmpty(pinyin) && StringUtils.isEmpty(other.pinyin)) {
            return accountName.compareTo(other.accountName);
        }

        if (StringUtils.isEmpty(pinyin) && !StringUtils.isEmpty(other.pinyin)) {
            return 1;
        }

        if (!StringUtils.isEmpty(pinyin) && StringUtils.isEmpty(other.pinyin)) {
            return -1;
        }

        return pinyin.compareTo(other.pinyin);
    }

    /**
     * 返回多组织信息
     *
     * @return
     */
    public List<MultiOrg> getMultiOrg() {
        List<MultiOrg> multiOrgList = new ArrayList<>();

        if (comName.contains(SPLIT)) {
            String[] coms = comName.split(SPLIT);
            String[] orgs = orgName.split(SPLIT);
            String[] jobs = jobTitle.split(SPLIT);

            for (int i = 0; i < coms.length; i++) {
                MultiOrg multiOrg = new MultiOrg(coms[i], orgs[i], jobs[i]);
                multiOrgList.add(multiOrg);
            }
        } else {
            MultiOrg multiOrg = new MultiOrg(comName, orgName, jobTitle);
            multiOrgList.add(multiOrg);
        }
        return multiOrgList;
    }


    public static class MultiOrg {

        //公司
        public String com = EMPTY;

        //部门
        public String org = EMPTY;

        //岗位
        public String job = EMPTY;


        public MultiOrg(String com, String org, String job) {
            this.com = com;
            this.org = org;
            this.job = job;
        }


    }

    public static class Position implements Serializable {

        @SerializedName("org_id")
        @Expose
        public String deptId;

        @SerializedName("org_name")
        @Expose
        public String deptName;

        @SerializedName("com_id")
        @Expose
        public String comId;

        @SerializedName("com_name")
        @Expose
        public String comName;

        @SerializedName("job_title")
        @Expose
        public String jobTile;

        @Expose
        public String path;

        @Expose
        public String type;

        @Expose
        public boolean primary;

    }

    public static boolean havingFieldByProperty(String property) {
        return null != getFieldByProperty(property);
    }

    public static Field getFieldByProperty(String property) {
        Field field = null;
        try {
            field = Contact.class.getField(property);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return field;
    }

    public static Object getFieldValueByProperty(String property, Contact contact) {
        Object valObj = null;

        try {
            Field field = getFieldByProperty(property);
            if(null != field) {
                valObj = field.get(contact);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return valObj;
    }

    //todo value 使用数组
    public static void setFiledValueByProperty(Contact contact, String property, Object value) {
        try {
            Field field = Contact.class.getField(property);
            if (null != field) {
                field.set(contact, value);

            } else {
                if (!ListUtil.isEmpty(contact.mEmployeePropertyRecord)) {

                    for (EmployeePropertyRecord record : contact.mEmployeePropertyRecord) {
                        if (null == record) {
                            continue;
                        }

                        if (property.equalsIgnoreCase(record.mProperty)) {
                            record.mValues = new String[]{(String) value};
                            record.mValue = (String) value;
                            break;
                        }
                    }

                }


            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public static EmployeePropertyRecord getEmployeePropertyRecordByProperty(Contact contact, String property) {
        if (ListUtil.isEmpty(contact.mEmployeePropertyRecord)) {
            return null;
        }
        for (EmployeePropertyRecord record : contact.mEmployeePropertyRecord) {
            if (record == null) {
                continue;
            }
            record.mEmployeeId = contact.identifier;
            if (property.equalsIgnoreCase(record.mProperty)) {
                return record;
            }
        }
        return null;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.avatar);
        dest.writeString(this.name);
        dest.writeString(this.pinyin);
        dest.writeString(this.accountName);
        dest.writeString(this.comName);
        dest.writeString(this.orgName);
        dest.writeString(this.jobTitle);
        dest.writeString(this.mobile);
        dest.writeString(this.email);
        dest.writeString(this.platform);
        dest.writeString(this.username);
        dest.writeSerializable(this.moreInfo);
        dest.writeString(this.identifier);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeInt(this.favor);
        dest.writeLong(this.createTime);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
        dest.writeString(this.firstPinyin);
        dest.writeString(this.gender);
        dest.writeLong(this.readTime);
        dest.writeString(this.tenantId);
        dest.writeInt(this.sortOrder);
        dest.writeInt(this.senior);
        dest.writeString(this.nickName);
        dest.writeString(this.sn);
        dest.writeLong(this.birthday);
        dest.writeString(this.initial);
        dest.writeString(this.province);
        dest.writeString(this.city);
        dest.writeString(this.tel);
        dest.writeString(this.fax);
        dest.writeString(this.work_phone);
        dest.writeString(this.other_phone);
        dest.writeString(this.other_email);
        dest.writeString(this.industry);
        dest.writeString(this.region);
        dest.writeString(this.label);
        dest.writeString(this.extended1);
        dest.writeString(this.extended2);
        dest.writeString(this.extended3);
        dest.writeInt(this.disable);
        dest.writeString(this.location);
        dest.writeList(this.mEmployeePropertyRecord);
        dest.writeList(this.positions);
        dest.writeString(this.status);
    }

    public Contact() {
    }

    protected Contact(Parcel in) {
        this.avatar = in.readString();
        this.name = in.readString();
        this.pinyin = in.readString();
        this.accountName = in.readString();
        this.comName = in.readString();
        this.orgName = in.readString();
        this.jobTitle = in.readString();
        this.mobile = in.readString();
        this.email = in.readString();
        this.platform = in.readString();
        this.username = in.readString();
        this.moreInfo = (ContactModel.MoreInfo) in.readSerializable();
        this.identifier = in.readString();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : SessionType.values()[tmpType];
        this.favor = in.readInt();
        this.createTime = in.readLong();
        this.selected = in.readByte() != 0;
        this.firstPinyin = in.readString();
        this.gender = in.readString();
        this.readTime = in.readLong();
        this.tenantId = in.readString();
        this.sortOrder = in.readInt();
        this.senior = in.readInt();
        this.nickName = in.readString();
        this.sn = in.readString();
        this.birthday = in.readLong();
        this.initial = in.readString();
        this.province = in.readString();
        this.city = in.readString();
        this.tel = in.readString();
        this.fax = in.readString();
        this.work_phone = in.readString();
        this.other_phone = in.readString();
        this.other_email = in.readString();
        this.industry = in.readString();
        this.region = in.readString();
        this.label = in.readString();
        this.extended1 = in.readString();
        this.extended2 = in.readString();
        this.extended3 = in.readString();
        this.disable = in.readInt();
        this.location = in.readString();
        this.mEmployeePropertyRecord = new ArrayList<EmployeePropertyRecord>();
        in.readList(this.mEmployeePropertyRecord, EmployeePropertyRecord.class.getClassLoader());
        this.positions = new ArrayList<Position>();
        in.readList(this.positions, Position.class.getClassLoader());
        this.status = in.readString();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}
