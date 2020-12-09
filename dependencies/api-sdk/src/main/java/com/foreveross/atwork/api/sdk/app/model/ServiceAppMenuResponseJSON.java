package com.foreveross.atwork.api.sdk.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.infrastructure.model.app.ServiceApp;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class ServiceAppMenuResponseJSON implements Parcelable {

    @SerializedName("status")
    public int status;

    @SerializedName("message")
    public String message;

    @SerializedName("result")
    public List<MenuResponseJSON> menus;

    public List<ServiceApp.ServiceMenu> toServiceMenuList() {
        List<ServiceApp.ServiceMenu> serviceMenuList = new ArrayList<>();
        for (MenuResponseJSON menu : menus) {
            serviceMenuList.add(toServiceMenu(menu));
        }
        return serviceMenuList;
    }

    public static class MenuResponseJSON implements Parcelable {

        @SerializedName("domain_id")
        public String domainId;

        @SerializedName("org_id")
        public String orgId;

        @SerializedName("name")
        public String name;

        @SerializedName("en_name")
        public String enName;

        @SerializedName("tw_name")
        public String twName;

        @SerializedName("type")
        public String type;

        @SerializedName("value")
        public String value;

        @SerializedName("children")
        public List<MenuResponseJSON> subMenus;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.domainId);
            dest.writeString(this.orgId);
            dest.writeString(this.name);
            dest.writeString(this.type);
            dest.writeString(this.value);
            dest.writeList(this.subMenus);
        }

        public MenuResponseJSON() {
        }

        protected MenuResponseJSON(Parcel in) {
            this.domainId = in.readString();
            this.orgId = in.readString();
            this.name = in.readString();
            this.type = in.readString();
            this.value = in.readString();
            this.subMenus = new ArrayList<MenuResponseJSON>();
            in.readList(this.subMenus, MenuResponseJSON.class.getClassLoader());
        }

        public static final Parcelable.Creator<MenuResponseJSON> CREATOR = new Parcelable.Creator<MenuResponseJSON>() {
            @Override
            public MenuResponseJSON createFromParcel(Parcel source) {
                return new MenuResponseJSON(source);
            }

            @Override
            public MenuResponseJSON[] newArray(int size) {
                return new MenuResponseJSON[size];
            }
        };
    }

    private ServiceApp.ServiceMenu toServiceMenu(MenuResponseJSON menuResponse) {
        ServiceApp.ServiceMenu serviceMenu = new ServiceApp.ServiceMenu();
        serviceMenu.name = menuResponse.name;
        serviceMenu.twName = menuResponse.twName;
        serviceMenu.enName = menuResponse.enName;
        serviceMenu.type = ServiceApp.ServiceMenuType.fromStringValue(menuResponse.type);
        serviceMenu.value = menuResponse.value;
        if (menuResponse.subMenus != null) {
            for (MenuResponseJSON menuResponseJSON : menuResponse.subMenus) {
                serviceMenu.addServiceMenu(toServiceMenu(menuResponseJSON));
            }
        }
        return serviceMenu;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.status);
        dest.writeString(this.message);
        dest.writeTypedList(this.menus);
    }

    public ServiceAppMenuResponseJSON() {
    }

    protected ServiceAppMenuResponseJSON(Parcel in) {
        this.status = in.readInt();
        this.message = in.readString();
        this.menus = in.createTypedArrayList(MenuResponseJSON.CREATOR);
    }

    public static final Parcelable.Creator<ServiceAppMenuResponseJSON> CREATOR = new Parcelable.Creator<ServiceAppMenuResponseJSON>() {
        @Override
        public ServiceAppMenuResponseJSON createFromParcel(Parcel source) {
            return new ServiceAppMenuResponseJSON(source);
        }

        @Override
        public ServiceAppMenuResponseJSON[] newArray(int size) {
            return new ServiceAppMenuResponseJSON[size];
        }
    };
}
