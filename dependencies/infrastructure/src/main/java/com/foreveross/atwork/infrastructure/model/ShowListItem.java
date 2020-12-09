package com.foreveross.atwork.infrastructure.model;


import android.content.Context;
import android.os.Parcelable;

public interface ShowListItem extends Parcelable {

    /**
     * 返回应该显示的标题
     */
    String getTitle();

    /**
     * 返回国际化标题
     * */
    String getTitleI18n(Context context);

    /**
     * 返回标题对应的 pinyin
     * */
    String getTitlePinyin();

    /**
     * 返回对应身份的标题, 如雇员, 有时是用户身份, 有时是雇员身份
     * */
    String getParticipantTitle();

    /**
     * 返回应该显示的详细描述
     *
     * @return
     */
    String getInfo();


    /**
     * 返回头像
     *
     * @return
     */
    String getAvatar();

    /**
     * key id
     */
    String getId();


    String getDomainId();

    /**
     * 返回 状态
     */
    String getStatus();

    /**
     * 返回 选中的状态
     */
    boolean isSelect();

    /**
     * 选中操作
     *
     * @param isSelect
     */
    void select(boolean isSelect);

    boolean isOnline();

}
