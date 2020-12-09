package com.foreveross.atwork.infrastructure.model;

import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by lingen on 15/4/11.
 * Description:
 */

public abstract class ContactModel implements ShowListItem, Serializable {

    public final static  int LOAD_STATUS_LOADED_REMOTE = 3;
    public final static  int LOAD_STATUS_LOADED_LOCAL = 2;
    public final static  int LOAD_STATUS_LOADING = 1;
    public final static  int LOAD_STATUS_NOT_LOAD = 0;

    @SerializedName("domain_id")
    public String domainId;

    public String parentOrgId;


    @SerializedName("id")
    public String id;

    /**
     * 头像
     */
    public String avatar;

    /**
     * 名称
     */
    public String name;

    public int loadingStatus = LOAD_STATUS_NOT_LOAD;

    protected HashSet<Integer> loadedStatus = new HashSet<>();
    /**
     * 是否已经展开
     */
    public boolean expand;
    //层级
    public int level;

    //是否选中
    public boolean selected;

    public boolean top;

    @SerializedName("more_info")
    public MoreInfo moreInfo;

    public void select() {
        this.selected = !selected;
    }

    public boolean isLast = false;

    public boolean isLoadCompleted = false;

    public  void addLoadedStatus(int status) {
        loadedStatus.add(status);

        if (LOAD_STATUS_LOADED_REMOTE == status) {
            loadingStatus = LOAD_STATUS_NOT_LOAD;
        }
    }

    public Set<Integer> getLoadedStatus() {
        return loadedStatus;
    }

    public boolean isLoaded() {
        return null != loadedStatus && 0 < loadedStatus.size();
    }

    public boolean isInitLoading() {
        return isLoading() && !isLoaded();
    }

    public boolean isLoading() {
        return ContactModel.LOAD_STATUS_LOADING == loadingStatus;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactModel that = (ContactModel) o;
        return Objects.equals(domainId, that.domainId) &&
                Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(domainId, id);
    }

    /**
     * 类型
     *
     * @return
     */
    public abstract ContactType type();

    /**
     * 组织或部门下的总人数
     *
     * @return
     */
    public abstract int num();

    /**
     * 下面的子元素
     *
     * @return
     */
    public abstract List<ContactModel> subContactModel();


    public enum ContactType {

        Employee,

        Organization

    }

    public class MoreInfo implements Serializable {

        @SerializedName("tax")
        public String tax;

        @SerializedName("tel")
        public String tel;

        @SerializedName("notify")
        public String notify;
    }
}
