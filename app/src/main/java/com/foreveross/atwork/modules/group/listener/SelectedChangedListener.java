package com.foreveross.atwork.modules.group.listener;

import com.foreveross.atwork.infrastructure.model.ShowListItem;

import java.util.List;

/**
 * 选择监听器
 */
public interface SelectedChangedListener {

    /**
     * 选中某个人
     *
     * @param contact
     */
    void selectContact(ShowListItem contact);

    /**
     * 取消选中某个人
     *
     * @param contact
     */
    void unSelectedContact(ShowListItem contact);

    /**
     * 选中一批人
     *
     * @param contactList
     */
    void selectContactList(List<? extends ShowListItem> contactList);

    /**
     * 取消选中一批人
     *
     * @param contactList
     */
    void unSelectedContactList(List<? extends ShowListItem> contactList);

}
