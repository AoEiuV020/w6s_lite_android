package com.foreveross.atwork.api.sdk.organization.responseModel;

import android.content.Context;
import android.os.Parcel;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.ContactModel;
import com.foreveross.atwork.infrastructure.model.employee.DataSchema;
import com.foreveross.atwork.infrastructure.model.employee.EmployeePropertyRecord;
import com.foreveross.atwork.infrastructure.model.employee.Participant;
import com.foreveross.atwork.infrastructure.model.employee.Position;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.model.orgization.Scope;
import com.foreveross.atwork.infrastructure.utils.CustomerHelper;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;

/**
 * 代表一个员工
 */
public class EmployeeResult extends ContactModel {
    @SerializedName("user_id")
    public String userId;

    public String mobile;

    public String email;

    @SerializedName("pinyin")
    public String pinyin;

    public List<PositionResult> positions;

    @SerializedName("tenant_Id")
    public String tenantId;

    @SerializedName("sort_order")
    public long sortOrder;

    public ContactModel parentModel;

    /**
     * 是否为高管
     */

    public boolean senior;

    /**
     * user 的 username
     */
    @SerializedName("username")
    public String username;

    /**
     * user 昵称
     */
    @SerializedName("nickname")
    public String nickName;

    @SerializedName("gender")
    public String gender;

    @SerializedName("org_code")
    public String orgCode;

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
     *传真
     */
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
    public boolean disable;

    /**
     * 省-市
     */
    public String location;

    public String status;

    public boolean mOnline = false;

    /**
     * 雇员的身份, 默认是用户
     * */
    public Participant mParticipant = Participant.USER;


    @SerializedName("data_schemas")
    public List<DataSchema> mDataSchemaList;

    @SerializedName("properties")
    public List<EmployeePropertyRecord>  mEmployeePropertyRecord;


    /**
     * 类型
     *
     * @return
     */
    @Override
    public ContactType type() {
        return ContactType.Employee;
    }

    /**
     * 组织或部门下的总人数
     *
     * @return
     */
    @Override
    public int num() {
        return -1;
    }


    /**
     * 下面的子元素
     *
     * @return
     */
    @Override
    public List<ContactModel> subContactModel() {
        return new ArrayList<>();
    }

    /**
     * 模型转换
     * */
    public com.foreveross.atwork.infrastructure.model.Employee toEmployee() {
        com.foreveross.atwork.infrastructure.model.Employee emp = new com.foreveross.atwork.infrastructure.model.Employee();
        emp.id = id;
        emp.userId = userId;
        emp.domainId = domainId;
        emp.username = username;
        emp.nickName = nickName;
        emp.avatar = avatar;
        emp.mobile = mobile;
        emp.name = name;
        emp.birthday = String.valueOf(birthday);
        emp.email = email;
        emp.pinyin = pinyin;
        emp.mSelect = selected;
        emp.gender = gender;
        emp.status = status;
        emp.orgCode = orgCode;
        emp.positions = toPositions();
        emp.mParticipant = mParticipant;
        return emp;
    }


    /**
     * 模型转换
     * */
    public static EmployeeResult buildEmployee(com.foreveross.atwork.infrastructure.model.Employee empReceived) {
        EmployeeResult emp = new EmployeeResult();
        emp.id = empReceived.id;
        emp.userId = empReceived.userId;
        emp.domainId = empReceived.domainId;
        emp.username = empReceived.username;
        emp.nickName = empReceived.nickName;
        emp.avatar = empReceived.avatar;
        emp.mobile = empReceived.mobile;
        emp.name = empReceived.name;
        try {
            emp.birthday =  Long.parseLong(empReceived.birthday);
        } catch (Exception e) {
            e.printStackTrace();
        }
        emp.email = empReceived.email;
        emp.pinyin = empReceived.pinyin;
        emp.selected = empReceived.mSelect;
        emp.gender = empReceived.gender;
        emp.status = empReceived.status;
        emp.orgCode = empReceived.orgCode;
        emp.positions = buildPositions(empReceived.positions);
        emp.mParticipant = empReceived.mParticipant;
        return emp;
    }


    @Nullable
    public Scope transfer(List<Scope> originalScopeList) {
        for(PositionResult position : positions) {
            if(isAllNotIncludePath(position, originalScopeList)) {
                Scope scope = new Scope(userId, position.path + userId, getShowName(), null, this);
                return scope;
            }
        }

        return null;
    }

    private boolean isAllNotIncludePath(final PositionResult position, List<Scope> originalScopeList) {
        return CollectionsKt.all(originalScopeList, new Function1<Scope, Boolean>() {
            @Override
            public Boolean invoke(Scope scope) {
                return !position.path.contains(scope.getPath());
            }
        });
    }

    /**
     * 返回显示的名字
     * */
    public String getShowName() {

        if(!StringUtils.isEmpty(name)) {
            return name;
        }

        if(!StringUtils.isEmpty(nickName)) {
            return nickName;
        }

        return "";
    }

    public String getJobTitle() {
        StringBuilder showSb = new StringBuilder();

        if (!ListUtil.isEmpty(positions)) {
            PositionResult position = positions
                    .stream()
                    .filter(it -> it.orgId.equals(parentOrgId))
                    .findAny()
                    .orElse(positions.get(0));


            if (!TextUtils.isEmpty(position.fullNamePath) && CustomerHelper.isRuYuan(BaseApplicationLike.baseContext)) {
                showSb.append(makeFullNamePath(position.fullNamePath, position.jobTitle));
            } else {
                getShowJobTitleFromPosition(showSb, position);
            }

        }

        return showSb.toString();
    }

    private String makeFullNamePath(String fullNamePath, String jobTitle) {
        StringBuilder stringBuilder = new StringBuilder();
        String replacePath = fullNamePath.replaceAll("/", "-");
        if (replacePath.startsWith("-")) {
            replacePath = replacePath.substring(0, replacePath.length() - 1);
        }
        stringBuilder.append(replacePath);
        if (!TextUtils.isEmpty(jobTitle)) {
            stringBuilder.append("-").append(jobTitle);
        }
        return stringBuilder.toString();
    }

    public void getShowJobTitleFromPosition(StringBuilder showSb, PositionResult position) {
        if(!StringUtils.isEmpty(position.corpName)) {
            showSb.append(position.corpName);
        }

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
    }

    private List<Position> toPositions() {
        List<Position> positionList = new ArrayList<>();
        if (positions == null) {
            return positionList;
        }
        for(PositionResult result : positions) {
            Position position = new Position();
            position.employeeId = result.employeeId;
            position.orgId = result.orgId;
            position.orgName = result.orgName;
            position.corpName = result.corpName;
            position.jobTitle = result.jobTitle;
            position.primary = result.primary;
            position.path = result.path;
            position.type = result.type;
            position.chief = result.chief;
            position.fullNamePath = result.fullNamePath;
            positionList.add(position);
        }

        return positionList;
    }

    private static List<PositionResult> buildPositions(List<Position> empPositionList) {
        List<PositionResult> positionList = new ArrayList<>();
        for(Position result : empPositionList) {
            PositionResult position = new PositionResult();
            position.employeeId = result.employeeId;
            position.orgId = result.orgId;
            position.orgName = result.orgName;
            position.corpName = result.corpName;
            position.jobTitle = result.jobTitle;
            position.primary = result.primary;
            position.path = result.path;
            position.type = result.type;
            position.chief = result.chief;
            position.fullNamePath = result.fullNamePath;
            positionList.add(position);
        }

        return positionList;

    }


    public boolean canSelect(final Organization orgSelected) {
        return CollectionsKt.all(positions, new Function1<PositionResult, Boolean>() {
            @Override
            public Boolean invoke(PositionResult positionResult) {
                return !positionResult.path.contains(orgSelected.mPath);
            }
        });
    }


    public boolean canSelect(final OrganizationResult orgSelected) {
        return CollectionsKt.all(positions, new Function1<PositionResult, Boolean>() {
            @Override
            public Boolean invoke(PositionResult positionResult) {
                return !positionResult.path.contains(orgSelected.path);
            }
        });
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
        return null;
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
        return false;
    }

    @Override
    public void select(boolean isSelect) {
        this.selected = isSelect;
    }

    @Override
    public boolean isOnline() {
        return mOnline;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.mobile);
        dest.writeString(this.email);
        dest.writeString(this.pinyin);
        dest.writeList(this.positions);
        dest.writeString(this.tenantId);
        dest.writeLong(this.sortOrder);
        dest.writeByte(this.senior ? (byte) 1 : (byte) 0);
        dest.writeString(this.username);
        dest.writeString(this.nickName);
        dest.writeString(this.gender);
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
        dest.writeList(this.mDataSchemaList);
        dest.writeList(this.mEmployeePropertyRecord);
    }

    public EmployeeResult() {
    }

    protected EmployeeResult(Parcel in) {
        this.userId = in.readString();
        this.mobile = in.readString();
        this.email = in.readString();
        this.pinyin = in.readString();
        this.positions = new ArrayList<PositionResult>();
        in.readList(this.positions, PositionResult.class.getClassLoader());
        this.tenantId = in.readString();
        this.sortOrder = in.readLong();
        this.senior = in.readByte() != 0;
        this.username = in.readString();
        this.nickName = in.readString();
        this.gender = in.readString();
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
        this.mDataSchemaList = new ArrayList<DataSchema>();
        in.readList(this.mDataSchemaList, DataSchema.class.getClassLoader());
        this.mEmployeePropertyRecord = new ArrayList<EmployeePropertyRecord>();
        in.readList(this.mEmployeePropertyRecord, EmployeePropertyRecord.class.getClassLoader());
    }

    public static final Creator<EmployeeResult> CREATOR = new Creator<EmployeeResult>() {
        @Override
        public EmployeeResult createFromParcel(Parcel source) {
            return new EmployeeResult(source);
        }

        @Override
        public EmployeeResult[] newArray(int size) {
            return new EmployeeResult[size];
        }
    };
}
