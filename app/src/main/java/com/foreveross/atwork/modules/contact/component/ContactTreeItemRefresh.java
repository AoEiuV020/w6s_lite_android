package com.foreveross.atwork.modules.contact.component;

import com.foreveross.atwork.infrastructure.model.ContactModel;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.modules.group.activity.UserSelectActivity;

import java.util.List;


public interface ContactTreeItemRefresh {

    void refreshView(ContactModel contactModel, boolean selectAble, UserSelectActivity.SelectAction selectAction, List<ShowListItem> selectContacts, List<String> notAllowedSelectedContacts);
}
