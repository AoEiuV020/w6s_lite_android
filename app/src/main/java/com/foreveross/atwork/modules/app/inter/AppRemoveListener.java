package com.foreveross.atwork.modules.app.inter;

import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.modules.app.model.GroupAppItem;

import java.util.Map;

/**
 * Created by lingen on 15/5/14.
 * Description:
 */
public interface AppRemoveListener {

    void removeMode(boolean removeAble);

    void removeComplete(GroupAppItem groupAppItem, AppBundles appbundle);

    Map<String, AppBundles> getNativeAppRemoveFlagHashTable();
}
