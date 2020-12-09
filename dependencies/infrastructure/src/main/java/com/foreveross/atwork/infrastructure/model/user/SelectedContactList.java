package com.foreveross.atwork.infrastructure.model.user;

import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.orgization.Scope;

import java.util.ArrayList;
import java.util.List;

import kotlin.collections.CollectionsKt;

public class SelectedContactList {

    private static List<ShowListItem> sContactList = new ArrayList<>();
    private static List<Scope> sScopeList = new ArrayList<>();

    public static void setContactList(List<? extends ShowListItem> list) {
        sContactList = null;
        sContactList = new ArrayList<>();
        sContactList.addAll(list);
    }

    public static void setScopeList(List<Scope> list) {
        sScopeList = null;
        sScopeList = new ArrayList<>();
        sScopeList.addAll(list);
    }


    public static List<ShowListItem> getContactList() {
        return sContactList;
    }

    public static List<Scope> getScopeList() {
        return sScopeList;
    }

    public static boolean isSelected(String userId) {
        return CollectionsKt.any(sContactList, showListItem -> userId.equals(showListItem.getId()) && showListItem.isSelect());
    }


    public static void clear() {
        sContactList.clear();
        sScopeList.clear();
    }

}