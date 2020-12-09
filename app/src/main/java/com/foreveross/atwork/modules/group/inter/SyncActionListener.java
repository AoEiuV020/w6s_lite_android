package com.foreveross.atwork.modules.group.inter;

import com.foreveross.atwork.infrastructure.model.ShowListItem;

import java.util.List;

/**
 * Created by lingen on 15/6/4.
 * Description:
 */
public interface SyncActionListener {

    void syncToMobileAction(List<? extends ShowListItem> contactList, boolean selected);

    void syncToMobileAction(ShowListItem contact);

}
