package com.foreveross.atwork.infrastructure.model;/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 */


import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.employee.DataSchema;
import com.foreveross.atwork.infrastructure.model.employee.EmployeePropertyRecord;
import com.foreveross.atwork.infrastructure.model.employee.MoreInfo;
import com.foreveross.atwork.infrastructure.model.employee.Participant;
import com.foreveross.atwork.infrastructure.model.employee.Position;
import com.foreveross.atwork.infrastructure.model.employee.Settings;
import com.foreveross.atwork.infrastructure.model.orgization.Scope;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.utils.ArrayUtil;
import com.foreveross.atwork.infrastructure.utils.CustomerHelper;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;

/**
 * 雇员
 * Created by reyzhang22 on 16/3/24.
 */
public class Employee implements Comparable<Employee>, ShowListItem {

    public static final String EMPTY = "";

    @SerializedName("domain_id")
    public String domainId;

    @SerializedName("org_code")
    public String orgCode = "";

    @SerializedName("employee_type_id")
    public int employeeTypeId;

    @SerializedName("employee_type")
    public String employeeType;

    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("en_name")
    public String alias;

    @SerializedName("avatar")
    public String avatar;

    @SerializedName("status")
    public String status;

    @SerializedName("type")
    public String type;

    @SerializedName("sort_order")
    public long sortOrder;

    @SerializedName("user_id")
    public String userId;

    @SerializedName("senior")
    public boolean senior;

    /** employee 对应 user 的名字 */
    @SerializedName("nickname")
    public String nickName;

    /** employee 对应 user 的username */
    @SerializedName("username")
    public String username;

    /** employee 对应 搜索显示的名字 */
    @SerializedName("display_name")
    public String displayName;

    @SerializedName("gender")
    public String gender;

    @SerializedName("pinyin")
    public String pinyin;

    @SerializedName("initial")
    public String initial;

    @SerializedName("mobile")
    public String mobile = StringUtils.EMPTY;

    @SerializedName("created")
    public long created;

    @SerializedName("last_modified")
    public long lastModified;

    @SerializedName("expired")
    public int expired;

    @SerializedName("disabled")
    public boolean disabled;

    @SerializedName("positions")
    public List<Position> positions;

    @SerializedName("more_info")
    public MoreInfo moreInfo;

    @SerializedName("settings")
    public Settings settings;

    @SerializedName("properties")
    public List<EmployeePropertyRecord> properties;

    @SerializedName("data_schemas")
    public List<DataSchema> dataSchemaList;

    @SerializedName("industry")
    public String industry;

    @SerializedName("email")
    public String email = StringUtils.EMPTY;

    /**
     * 工号
     */
    @SerializedName("sn")
    public String sn;

    @SerializedName("label")
    public String label;

    /**
     * 所属大区
     */
    @SerializedName("region")
    public String region;

    /**
     * 所属地
     */
    @SerializedName("location")
    public String location;

    @SerializedName("other_email")
    public String otherEmail;

    @SerializedName("other_phone")
    public String otherPhone;

    @SerializedName("work_phone")
    public String workPhone;

    @SerializedName("online")
    public boolean mOnline = false;

    @SerializedName("platform")
    public String mPlatform;

    /**
     * 传真号码
     */
    @SerializedName("fax")
    public String fax;

    @SerializedName("tel")
    public String tel;

    @SerializedName("birthday")
    public String birthday;

    public OrgInfo orgInfo;

    public boolean mSelect;

    public long mReadTime;

    public String mJobTitle;

    /**
     * 雇员的身份, 默认是用户
     * */
    public Participant mParticipant = Participant.USER;


    public boolean isH3cTypeB() {
        if(ListUtil.isEmpty(properties)) {
            return false;
        }

        for(EmployeePropertyRecord employeePropertyRecord : properties) {
            if("h3c_user_type".equals(employeePropertyRecord.mProperty) && "B用户".equals(employeePropertyRecord.mValue)) {
                return true;
            }
        }

        return false;

    }

    @Override
    public int compareTo(Employee another) {

        if (another == null || !(another instanceof Employee)) {
            return 1;
        }

//        long sortOrderResultLong = this.sortOrder - another.sortOrder;
//
//        if(0 < sortOrderResultLong) {
//            return 1;
//        }
//
//        if(0 > sortOrderResultLong) {
//            return -1;
//        }

        if (null == orgInfo && null == another.orgInfo) {
            return 0;
        }

        if (null == another.orgInfo) {
            return 1;
        }

        if (null == orgInfo) {
            return -1;
        }


        long resultSortOrderLong = this.orgInfo.sortOrder - another.orgInfo.sortOrder;
        if(0 < resultSortOrderLong) {
            return 1;

        }

        if(0 > resultSortOrderLong) {
            return -1;
        }


        //排序相同的话,对比创建时间
        long createTimeResultLong = this.orgInfo.createTime - another.orgInfo.createTime;

        if(0 < createTimeResultLong) {
            return 1;
        }

        if(0 > createTimeResultLong) {
            return -1;
        }


        return 0;


    }

    @Override
    public boolean equals(Object o) {
        if(null == o) {
            return false;
        }

        String otherUserId;

        if (o instanceof ShowListItem) {
            ShowListItem other = (ShowListItem) o;
            otherUserId = other.getId();

        } else {
            return false;
        }



        if (this.userId == null || otherUserId == null) {
            return false;
        }

        return this.userId.equals(otherUserId);
    }

    @Override
    public int hashCode() {
        if(StringUtils.isEmpty(userId)) {
            return super.hashCode();

        } else {
            return userId.hashCode();
        }
    }

    public boolean checkPositionLegal() {
        for(Position position : positions) {
            if(null == position.displayNodes) {
                return false;
            }
        }

        return true;
    }



    /**
     * 返回显示的名字
     */
    public String getShowName() {
        if (!StringUtils.isEmpty(name)) {
            return name;
        }

        if (!StringUtils.isEmpty(nickName)) {
            return nickName;
        }

        return "";
    }


    @Override
    public String getTitle() {
        return getShowName();
    }

    @Override
    public String getTitleI18n(Context context) {
        return getParticipantTitle();
    }

    @Override
    public String getTitlePinyin() {
        return pinyin;
    }

    @Override
    public String getParticipantTitle() {
        if(Participant.USER == mParticipant) {
            return nickName;
        }
        return getTitle();
    }

    @Override
    public String getInfo() {
        return getSearchShowJobTitle();
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    @Override
    public String getId() {
        return userId;
    }

    @Override
    public String getDomainId() {
        return domainId;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public boolean isSelect() {
        return mSelect;
    }

    @Override
    public void select(boolean isSelect) {
        mSelect = isSelect;
    }

    @Override
    public boolean isOnline() {
        return mOnline;
    }

    public void select() {
        mSelect = !mSelect;
    }


    /**
     * 数据显示类型
     */
    public enum InfoType {

        TEXT {
            public String toString() {
                return "TEXT";
            }
        },
        RADIO {
            @Override
            public String toString() {
                return "RADIO";
            }
        },
        DATE {
            @Override
            public String toString() {
                return "DATE";
            }
        },
        TEL_PHONE {
            @Override
            public String toString() {
                return "TEL_PHONE";
            }
        },
        MOBILE_PHONE {
            @Override
            public String toString() {
                return "MOBILE_PHONE";
            }
        },
        EMAIL {
            @Override
            public String toString() {
                return "EMAIL";
            }
        },
        SELECT {
            @Override
            public String toString() {
                return "SELECT";
            }
        };

        public abstract String toString();


        public static String fromInfoType(InfoType infoType) {
            if (infoType == RADIO) {
                return "RADIO";
            }
            if (infoType == DATE) {
                return "DATE";
            }
            if (infoType == TEL_PHONE) {
                return "TEL_PHONE";
            }
            if (infoType == MOBILE_PHONE) {
                return "MOBILE_PHONE";
            }
            if (infoType == EMAIL) {
                return "EMAIL";
            }
            if (infoType == SELECT) {
                return "SELECT";
            }
            return "TEXT";
        }

        public static InfoType fromString(String infoType) {
            if (infoType.equalsIgnoreCase("RADIO")) {
                return RADIO;
            }
            if (infoType.equalsIgnoreCase("DATE")) {
                return DATE;
            }
            if (infoType.equalsIgnoreCase("TEL_PHONE")) {
                return TEL_PHONE;
            }
            if (infoType.equalsIgnoreCase("MOBILE_PHONE")) {
                return MOBILE_PHONE;
            }
            if (infoType.equalsIgnoreCase("EMAIL")) {
                return EMAIL;
            }
            if (infoType.equalsIgnoreCase("SELECT")) {
                return SELECT;
            }
            return TEXT;
        }

        public boolean equalsIgnoreCase(String string) {
            return this.toString().equalsIgnoreCase(string);
        }

    }


    public Employee() {
    }

    public static List<Employee> toEmpList(List<ShowListItem> contactList) {
        List<Employee> empList = new ArrayList<>();

        for(ShowListItem contact : contactList) {
            if(contact instanceof Employee) {
                empList.add((Employee) contact);
            }

        }
        return empList;
    }

    /**
     * 得到 userId list
     * */
    public static List<String> toUserIdList(List<Employee> userList) {
        List<String> userIdList = new ArrayList<>();
        for(Employee employee : userList) {
            userIdList.add(employee.userId);
        }

        return userIdList;
    }

    /**
     * 联合 key
     * */
    public String getCombinedKey() {
        return userId + "_" + orgCode;
    }


    public UserHandleInfo toUserHandleInfo() {
        UserHandleInfo handleInfo = new UserHandleInfo();
        handleInfo.mUserId = this.userId;
        handleInfo.mDomainId = this.domainId;
        if(!StringUtils.isEmpty(this.nickName)) {
            handleInfo.mShowName = this.nickName;

        } else {
            handleInfo.mShowName = this.name;

        }
        handleInfo.mAvatar = this.avatar;

        return handleInfo;
    }

    public void setOrgInfo(String orgName, long createTime, long sortOrder) {
        if (null == orgInfo) {
            orgInfo = new OrgInfo();
        }

        orgInfo.orgName = orgName;
        orgInfo.createTime = createTime;
        orgInfo.sortOrder = sortOrder;
    }

    public String getLastOrgName() {
        if(!ListUtil.isEmpty(positions)) {
            return positions.get(0).corpName;
        }

        return StringUtils.EMPTY;
    }

    public String getLastTwoShowJobTitleFrom() {
        if(!ListUtil.isEmpty(positions)) {
            return getTwoShowJobTitleFromPosition(new StringBuilder(), positions.get(0));
        }

        return StringUtils.EMPTY;

    }

    public String getLastJobTitle() {
        if(!ListUtil.isEmpty(positions)) {
            return positions.get(0).jobTitle;
        }

        return StringUtils.EMPTY;
    }

    /**
     * 返回搜索显示使用的职称信息
     */
    public String getSearchShowJobTitle() {

        String show = StringUtils.EMPTY;
        if (!ListUtil.isEmpty(positions)) {
            Position position = positions.get(0);
            show = getThreeShowJobTitleFromPosition(position);

        }

        return show;
    }




    /**
     * 返回公司-部门-职位, 以及组织架构等显示
     */
    public String getPositionThreeShowStr() {
        StringBuilder showSb = new StringBuilder();

        if (!ListUtil.isEmpty(positions)) {

            for (int i = 0; i < positions.size(); i++) {
                Position position = positions.get(i);

                String positionStr = getThreeShowJobTitleFromPosition(position);

                showSb.append(positionStr);

                if (i != positions.size() - 1) {
                    showSb.append("\n");
                }
            }
        }

        return showSb.toString();
    }



    /**
     * 返回部门-职位, 用于"我"的界面
     */
    public String getPositionTwoShowStr() {
        StringBuilder showSb = new StringBuilder();

        if (!ListUtil.isEmpty(positions)) {

            for (int i = 0; i < positions.size(); i++) {
                Position position = positions.get(i);
                StringBuilder sb = new StringBuilder();
                String positionStr = getTwoShowJobTitleFromPosition(sb, position);

                showSb.append(positionStr);

                if (i != positions.size() - 1) {
                    showSb.append("\n");
                }
            }
        }

        return showSb.toString();
    }


    /**
     * 返回公司-部门-职位
     */
    public String getThreeShowJobTitleFromPosition(Position position) {
        StringBuilder showSb = new StringBuilder();
        if(!StringUtils.isEmpty(position.corpName)) {
            showSb.append(position.corpName);
        }

        return getTwoShowJobTitleFromPosition(showSb, position);
    }


    /**
     * @see {@link #getJobTitleWithLast3OrgName
     * */
    public String getJobTitleWithLast3OrgName() {
        return getJobTitleWithLast3OrgName(getDepartmentNameSplit(positions.get(0)), positions.get(0).jobTitle);
    }

    /**
     * 在一些特殊环境里, position 里的 orgName 返回的是"xxx-xxx-xxx-xxx"的形式
     * 该方法返回最后<b>最多</b>三个 xxx 以及 jobTitle 的拼接, 例如: "xxx-xxx-xxx-jobTitle".
     *
     * */
    public static String getJobTitleWithLast3OrgName(String[] departmentNames, String jobTitle) {
        String[] last3DepartmentNameSplit = getLast3DepartmentNameSplit(departmentNames);
        if(ArrayUtil.isEmpty(last3DepartmentNameSplit)) {
            return jobTitle;
        }


        StringBuilder sb = new StringBuilder();

        int startIndex = 0;

        for(int i = startIndex; i < last3DepartmentNameSplit.length; i++) {
            sb.append(last3DepartmentNameSplit[i]);

            if(i != last3DepartmentNameSplit.length -1) {
                sb.append("-");
            }
        }

        if (!StringUtils.isEmpty(jobTitle)) {
            sb.append("-").append(jobTitle);
        }

        return sb.toString();

    }

    public String[] getLast3DepartmentNameSplit() {
        String[] departmentNameSplit = getDepartmentNameSplit(positions.get(0));
        return getLast3DepartmentNameSplit(departmentNameSplit);
    }


    public static String[] getLast3DepartmentNameSplit(String[] departmentNameSplit) {
        if(ArrayUtil.isEmpty(departmentNameSplit)) {
            return new String[0];
        }


        int startIndex = 0;
        if(departmentNameSplit.length > 3) {
            startIndex = departmentNameSplit.length - 3;
        }

        List<String> departmentNameSplitList = Arrays.asList(departmentNameSplit).subList(startIndex, departmentNameSplit.length);

        return departmentNameSplitList.toArray(new String[0]);
    }

    public static String[] getDepartmentNameSplit(Position position) {
        String departmentName;
        if(!StringUtils.isEmpty(position.corpName)) {
            departmentName = position.corpName + "-" + position.orgName;

        } else {
            departmentName = position.orgName;
        }

        return departmentName.split("-");
    }


    /**
     * 部门-职位
     */
    public String getTwoShowJobTitleFromPosition(StringBuilder showSb, Position position) {
        if(!StringUtils.isEmpty(position.orgName)) {
            if(0 != showSb.length()) {
                showSb.append("-");
            }

            showSb.append(position.orgName);
        }

        if (!StringUtils.isEmpty(position.jobTitle)) {
            if(0 != showSb.length()) {
                showSb.append("-");
            }
            showSb.append(position.jobTitle);
        }

        return showSb.toString();
    }

    /**
     * 更换雇员身份
     * */
    public void setEmpParticipant() {
        mParticipant = Participant.EMPLOYEE;
    }

    @Nullable
    public Scope transfer(List<Scope> originalScopeList) {
        for(final Position position : positions) {
            if(isAllNotIncludePath(position, originalScopeList)) {
                Scope scope = new Scope(userId, position.path + userId, getShowName(), null, this);
                return scope;
            }
        }

        return null;
    }

    private boolean isAllNotIncludePath(final Position position, List<Scope> originalScopeList) {
        return CollectionsKt.all(originalScopeList, new Function1<Scope, Boolean>() {
            @Override
            public Boolean invoke(Scope scope) {
                return !position.path.contains(scope.getPath());
            }
        });
    }


    public static class OrgInfo implements Parcelable {
        /**
         * 公司组织名
         */
        public String orgName = EMPTY;

        /**
         * 公司创建时间
         */
        public long createTime = -1;

        /**
         * 默认排序
         * */
        public long sortOrder = -1;

        public OrgInfo() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.orgName);
            dest.writeLong(this.createTime);
            dest.writeLong(this.sortOrder);
        }

        protected OrgInfo(Parcel in) {
            this.orgName = in.readString();
            this.createTime = in.readLong();
            this.sortOrder = in.readLong();
        }

        public static final Creator<OrgInfo> CREATOR = new Creator<OrgInfo>() {
            @Override
            public OrgInfo createFromParcel(Parcel source) {
                return new OrgInfo(source);
            }

            @Override
            public OrgInfo[] newArray(int size) {
                return new OrgInfo[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.domainId);
        dest.writeString(this.orgCode);
        dest.writeInt(this.employeeTypeId);
        dest.writeString(this.employeeType);
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.alias);
        dest.writeString(this.avatar);
        dest.writeString(this.status);
        dest.writeString(this.type);
        dest.writeLong(this.sortOrder);
        dest.writeString(this.userId);
        dest.writeByte(this.senior ? (byte) 1 : (byte) 0);
        dest.writeString(this.nickName);
        dest.writeString(this.username);
        dest.writeString(this.displayName);
        dest.writeString(this.gender);
        dest.writeString(this.pinyin);
        dest.writeString(this.initial);
        dest.writeString(this.mobile);
        dest.writeLong(this.created);
        dest.writeLong(this.lastModified);
        dest.writeInt(this.expired);
        dest.writeByte(this.disabled ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.positions);
        dest.writeParcelable(this.moreInfo, flags);
        dest.writeParcelable(this.settings, flags);
        dest.writeTypedList(this.properties);
        dest.writeList(this.dataSchemaList);
        dest.writeString(this.industry);
        dest.writeString(this.email);
        dest.writeString(this.sn);
        dest.writeString(this.label);
        dest.writeString(this.region);
        dest.writeString(this.location);
        dest.writeString(this.otherEmail);
        dest.writeString(this.otherPhone);
        dest.writeString(this.workPhone);
        dest.writeByte(this.mOnline ? (byte) 1 : (byte) 0);
        dest.writeString(this.mPlatform);
        dest.writeString(this.fax);
        dest.writeString(this.tel);
        dest.writeString(this.birthday);
        dest.writeParcelable(this.orgInfo, flags);
        dest.writeByte(this.mSelect ? (byte) 1 : (byte) 0);
        dest.writeLong(this.mReadTime);
        dest.writeString(this.mJobTitle);
        dest.writeInt(this.mParticipant == null ? -1 : this.mParticipant.ordinal());
    }

    protected Employee(Parcel in) {
        this.domainId = in.readString();
        this.orgCode = in.readString();
        this.employeeTypeId = in.readInt();
        this.employeeType = in.readString();
        this.id = in.readString();
        this.name = in.readString();
        this.alias = in.readString();
        this.avatar = in.readString();
        this.status = in.readString();
        this.type = in.readString();
        this.sortOrder = in.readLong();
        this.userId = in.readString();
        this.senior = in.readByte() != 0;
        this.nickName = in.readString();
        this.username = in.readString();
        this.displayName = in.readString();
        this.gender = in.readString();
        this.pinyin = in.readString();
        this.initial = in.readString();
        this.mobile = in.readString();
        this.created = in.readLong();
        this.lastModified = in.readLong();
        this.expired = in.readInt();
        this.disabled = in.readByte() != 0;
        this.positions = in.createTypedArrayList(Position.CREATOR);
        this.moreInfo = in.readParcelable(MoreInfo.class.getClassLoader());
        this.settings = in.readParcelable(Settings.class.getClassLoader());
        this.properties = in.createTypedArrayList(EmployeePropertyRecord.CREATOR);
        this.dataSchemaList = new ArrayList<DataSchema>();
        in.readList(this.dataSchemaList, DataSchema.class.getClassLoader());
        this.industry = in.readString();
        this.email = in.readString();
        this.sn = in.readString();
        this.label = in.readString();
        this.region = in.readString();
        this.location = in.readString();
        this.otherEmail = in.readString();
        this.otherPhone = in.readString();
        this.workPhone = in.readString();
        this.mOnline = in.readByte() != 0;
        this.mPlatform = in.readString();
        this.fax = in.readString();
        this.tel = in.readString();
        this.birthday = in.readString();
        this.orgInfo = in.readParcelable(OrgInfo.class.getClassLoader());
        this.mSelect = in.readByte() != 0;
        this.mReadTime = in.readLong();
        this.mJobTitle = in.readString();
        int tmpMParticipant = in.readInt();
        this.mParticipant = tmpMParticipant == -1 ? null : Participant.values()[tmpMParticipant];
    }

    public static final Creator<Employee> CREATOR = new Creator<Employee>() {
        @Override
        public Employee createFromParcel(Parcel source) {
            return new Employee(source);
        }

        @Override
        public Employee[] newArray(int size) {
            return new Employee[size];
        }
    };
}
