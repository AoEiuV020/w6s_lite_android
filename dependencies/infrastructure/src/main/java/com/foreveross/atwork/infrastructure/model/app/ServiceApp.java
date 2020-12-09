package com.foreveross.atwork.infrastructure.model.app;

import android.content.Context;
import android.os.Parcel;

import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.i18n.I18nInfo;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.language.LanguageSupport;
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingen on 15/4/3.
 * Description:
 * 服务号
 */
public class ServiceApp extends App {

    @SerializedName("type")
    public SessionType type = SessionType.Service;

    //服务号菜单信息
    @SerializedName("menuJson")
    public String menuJson;


    public static class ServiceMenu extends I18nInfo implements android.os.Parcelable {

        @SerializedName("name")
        public String name;

        @SerializedName("en_name")
        public String enName;

        @SerializedName("tw_name")
        public String twName;

        @SerializedName("type")
        public ServiceMenuType type;

        @SerializedName("value")
        public String value;

        //多个子菜单
        public List<ServiceMenu> serviceMenus;


        @Nullable
        @Override
        public String getStringName() {
            return name;
        }

        @Nullable
        @Override
        public String getStringTwName() {
            return twName;
        }

        @Nullable
        @Override
        public String getStringEnName() {
            return enName;
        }

        public void addServiceMenu(ServiceMenu serviceMenu) {
            if (serviceMenus == null) {
                serviceMenus = new ArrayList<>();
            }
            serviceMenus.add(serviceMenu);
        }


        public ServiceMenu() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.name);
            dest.writeString(this.enName);
            dest.writeString(this.twName);
            dest.writeInt(this.type == null ? -1 : this.type.ordinal());
            dest.writeString(this.value);
            dest.writeTypedList(this.serviceMenus);
        }

        protected ServiceMenu(Parcel in) {
            this.name = in.readString();
            this.enName = in.readString();
            this.twName = in.readString();
            int tmpType = in.readInt();
            this.type = tmpType == -1 ? null : ServiceMenuType.values()[tmpType];
            this.value = in.readString();
            this.serviceMenus = in.createTypedArrayList(ServiceMenu.CREATOR);
        }

        public static final Creator<ServiceMenu> CREATOR = new Creator<ServiceMenu>() {
            @Override
            public ServiceMenu createFromParcel(Parcel source) {
                return new ServiceMenu(source);
            }

            @Override
            public ServiceMenu[] newArray(int size) {
                return new ServiceMenu[size];
            }
        };
    }

    public enum ServiceMenuType {

        NOTHING,

        //点击事件，点击触发一个消息发送
        Click,

        Tag,

        //URL事件，点击跳转到URL
        VIEW;

        public static ServiceMenuType fromStringValue(String value) {
            if ("CLICK".equalsIgnoreCase(value)) {
                return Click;
            }
            if ("VIEW".equalsIgnoreCase(value)) {
                return VIEW;
            }

            if ("TAG".equalsIgnoreCase(value)) {
                return Tag;
            }

            return NOTHING;
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeString(this.menuJson);
    }

    public ServiceApp() {
    }

    protected ServiceApp(Parcel in) {
        super(in);
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : SessionType.values()[tmpType];
        this.menuJson = in.readString();
    }

    public static final Creator<ServiceApp> CREATOR = new Creator<ServiceApp>() {
        @Override
        public ServiceApp createFromParcel(Parcel source) {
            return new ServiceApp(source);
        }

        @Override
        public ServiceApp[] newArray(int size) {
            return new ServiceApp[size];
        }
    };
}
