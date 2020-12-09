package com.foreveross.atwork.modules.main.helper;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.component.ItemHomeTabView;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorksHelper;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorksTab;
import com.foreveross.atwork.infrastructure.model.workbench.Workbench;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.aboutme.fragment.AboutMeFragment;
import com.foreveross.atwork.modules.app.fragment.AppFragment;
import com.foreveross.atwork.modules.chat.fragment.ChatListFragment;
import com.foreveross.atwork.modules.contact.fragment.ContactFragment;
import com.foreveross.atwork.modules.main.activity.MainActivity;
import com.foreveross.atwork.modules.workbench.fragment.WorkbenchFragment;
import com.foreveross.atwork.modules.workbench.manager.WorkbenchManager;
import com.foreveross.atwork.tab.h5tab.H5WebViewFragment;
import com.foreveross.atwork.tab.nativeTab.NativeTabFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.foreveross.atwork.modules.contact.fragment.ContactFragment.SHOW_MAIN_TITLE_BAR;
import static com.foreveross.atwork.support.NoticeTabAndBackHandledFragment.ID;

/**
 * Created by lingen on 15/12/21.
 */
public class TabHelper {

    /**
     * key: tabId
     * value: id
     * */
    public static HashMap<String, String> sTabIdMap = new HashMap<>();

    public static boolean hasAppFragment() {
        return !StringUtils.isEmpty(getAppFragmentId());
    }

    public static boolean hasWorkbenchFragment() {
        return !StringUtils.isEmpty(getWorkbenchFragmentId());
    }

    public static String getAppFragmentId() {
        return getId(AppFragment.TAB_ID.toLowerCase());
    }

    public static String getAboutMeFragmentId() {
        return getId(AboutMeFragment.TAB_ID.toLowerCase());
    }


    public static String getWorkbenchFragmentId() {
        return getId(WorkbenchFragment.TAB_ID);
    }


    public static String getId(String tabId) {
        return sTabIdMap.get(tabId.toLowerCase());
    }


    public static String getTabId(String id) {
        Set<Map.Entry<String, String>> entries = sTabIdMap.entrySet();
        for(Map.Entry<String, String> entry : entries) {
            if(id.equals(entry.getValue())) {
                return entry.getKey();
            }
        }

        return StringUtils.EMPTY;
    }


    public static String getTabName(String tabId) {
        Context context = AtworkApplicationLike.baseContext;


        String id = TabHelper.getId(tabId);
        BeeWorksTab beeWorksTab = BeeWorks.getInstance().getBeeWorksTabById(context, id);
        String tabTitle = StringUtils.EMPTY;
        if(null != beeWorksTab) {
            tabTitle = beeWorksTab.name;

        }

        if(StringUtils.isEmpty(tabTitle)) {
            if (ChatListFragment.TAB_ID.equalsIgnoreCase(tabId)) {
                tabTitle = context.getString(R.string.item_chat);

            } else if (ContactFragment.TAB_ID.equalsIgnoreCase(tabId)) {
                tabTitle = context.getString(R.string.item_contact);

            } else if (AppFragment.TAB_ID.equalsIgnoreCase(tabId)) {
                tabTitle = context.getString(R.string.item_app);

            } else if (AboutMeFragment.TAB_ID.equalsIgnoreCase(tabId)) {
                tabTitle = context.getString(R.string.item_about_me);

            } else if(WorkbenchFragment.TAB_ID.equalsIgnoreCase(tabId)) {
                Workbench workbench = WorkbenchManager.INSTANCE.getCurrentOrgWorkbenchWithoutContent();
                if (null != workbench) {
                    tabTitle = workbench.getNameI18n(AtworkApplicationLike.baseContext);
                }

            }
        }

        return tabTitle;
    }

    public static void clearTabIdMap() {
        sTabIdMap.clear();
    }

    public static void removeTabId(String tabId) {
        sTabIdMap.remove(tabId.toLowerCase());
    }



    public static Fragment getTabFragment(Activity activity, BeeWorksTab beeWorksTab) {
        if (activity == null) {
            return null;
        }
        sTabIdMap.put(beeWorksTab.tabId.toLowerCase(), beeWorksTab.id);
        Map<String, ItemHomeTabView> map = ((MainActivity) activity).mTabMap;

        //系统内置
        if ("system".equalsIgnoreCase(beeWorksTab.type)) {

            //IM消息块
            if (ChatListFragment.TAB_ID.equalsIgnoreCase(beeWorksTab.tabId)) {
                ChatListFragment fragment = getFragment(activity, beeWorksTab, ChatListFragment.class);
                if(null == fragment) {
                    fragment = new ChatListFragment();
                }
                fragment.setBeeWorksInfo(beeWorksTab.id, beeWorksTab.tabId, beeWorksTab.name, true);
                fragment.initBeeWorksInfo();
                ItemHomeTabView tab = createItemHomeTab(activity, beeWorksTab, fragment.getFragmentName());
                map.put(beeWorksTab.id, tab);
                return fragment;
            }

            if (ContactFragment.TAB_ID.equalsIgnoreCase(beeWorksTab.tabId)) {
                ContactFragment fragment = getFragment(activity, beeWorksTab, ContactFragment.class);
                if(null == fragment) {
                    fragment = new ContactFragment();
                }

                Bundle bundle = new Bundle();
                bundle.putBoolean(SHOW_MAIN_TITLE_BAR, true);
                fragment.setArguments(bundle);
                fragment.setBeeWorksInfo(beeWorksTab.id, beeWorksTab.tabId, beeWorksTab.name, true);
                fragment.initBeeWorksInfo();
                ItemHomeTabView tab = createItemHomeTab(activity, beeWorksTab, fragment.getFragmentName());
                map.put(beeWorksTab.id, tab);
                return fragment;
            }

            if (AppFragment.TAB_ID.equalsIgnoreCase(beeWorksTab.tabId)) {
                AppFragment fragment = getFragment(activity, beeWorksTab, AppFragment.class);
                if(null == fragment) {
                    fragment = new AppFragment();
                }
                String tabName = "";
                try {
                    String beeworksName = BeeWorks.getInstance().getBeeWorksTabById(BaseApplicationLike.baseContext, beeWorksTab.id).name;
                    tabName = TextUtils.isEmpty(beeworksName) ? AtworkApplicationLike.getResourceString(R.string.item_app) : beeworksName;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    tabName = AtworkApplicationLike.getResourceString(R.string.item_app);
                }
                ItemHomeTabView tab = createItemHomeTab(activity, beeWorksTab, tabName);
                fragment.setBeeWorksInfo(beeWorksTab.id, beeWorksTab.tabId, beeWorksTab.name, true);
                fragment.initBeeWorksInfo();
                map.put(beeWorksTab.id, tab);
                return fragment;
            }

            if (AboutMeFragment.TAB_ID.equalsIgnoreCase(beeWorksTab.tabId)) {
                AboutMeFragment fragment = getFragment(activity, beeWorksTab, AboutMeFragment.class);
                if(null == fragment) {
                    fragment = new AboutMeFragment();
                }

                fragment.setBeeWorksInfo(beeWorksTab.id, beeWorksTab.tabId, beeWorksTab.name, true);
                fragment.initBeeWorksInfo();
                ItemHomeTabView tab = createItemHomeTab(activity, beeWorksTab, fragment.getFragmentName());
                map.put(beeWorksTab.id, tab);
                return fragment;
            }
        }

        //H5 TAB
        if ("h5".equalsIgnoreCase(beeWorksTab.type)) {
            H5WebViewFragment fragment = getFragment(activity, beeWorksTab, H5WebViewFragment.class);
            if(null == fragment) {
                fragment = new H5WebViewFragment();
            }

            String tabName = BeeWorksHelper.getString(BaseApplicationLike.baseContext, beeWorksTab.name);

            fragment.setBeeWorksInfo(beeWorksTab.id, beeWorksTab.tabId, tabName, false);
            ItemHomeTabView tab = createItemHomeTab(activity, beeWorksTab, tabName);
            map.put(beeWorksTab.id, tab);
            return fragment;
        }

        if ("native".equalsIgnoreCase(beeWorksTab.type)) {
            NativeTabFragment nativeTabFragment = getFragment(activity, beeWorksTab, NativeTabFragment.class);
            if(null == nativeTabFragment) {
                nativeTabFragment = new NativeTabFragment();
            }

            nativeTabFragment.setBeeWorksInfo(beeWorksTab.id, beeWorksTab.tabId, beeWorksTab.name, false);
            nativeTabFragment.initBeeWorksInfo();
            ItemHomeTabView tab = createItemHomeTab(activity, beeWorksTab, nativeTabFragment.getFragmentName());
            map.put(beeWorksTab.id, tab);
            return nativeTabFragment;
        }

        if("workbench".equalsIgnoreCase(beeWorksTab.type)) {
            WorkbenchFragment workbenchFragment = getFragment(activity, beeWorksTab, WorkbenchFragment.class);
            if(null == workbenchFragment) {
                workbenchFragment = new WorkbenchFragment();
            }

            String tabName = BeeWorksHelper.getString(AtworkApplicationLike.baseContext, beeWorksTab.name);


            workbenchFragment.setBeeWorksInfo(beeWorksTab.id, beeWorksTab.tabId, tabName, false);
            workbenchFragment.initBeeWorksInfo();
            ItemHomeTabView tab = createItemHomeTab(activity, beeWorksTab, tabName);
            map.put(beeWorksTab.id, tab);

            return workbenchFragment;

        }

        return null;
    }

    @Nullable
    private static <T> T getFragment(Activity activity, BeeWorksTab beeWorksTab, Class<T> classOfT) {
        Fragment fragment = getFragmentInSupportManager(activity, beeWorksTab);
        if(null != fragment && classOfT.isInstance(fragment)) {
            return (T) fragment;
        }

        try {
            return classOfT.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Nullable
    private static Fragment getFragmentInSupportManager(Activity activity, BeeWorksTab beeWorksTab) {
        List<Fragment> fragmentList = ((MainActivity)activity).getSupportFragmentManager().getFragments();
        for(Fragment fragment : fragmentList) {
            Bundle arguments = fragment.getArguments();
            if(null != arguments) {
                if(beeWorksTab.id.equals(arguments.get(ID))) {
                    return fragment;
                }
            }
        }

        return null;

    }

    private static ItemHomeTabView createItemHomeTab(Activity activity, BeeWorksTab beeWorksTab, String tabName) {
        ItemHomeTabView tab = new ItemHomeTabView(activity);

        tab.setTabId(beeWorksTab.tabId);
        tab.setTitle(tabName);

        String imageOff = StringUtils.EMPTY;
        String imageHover = StringUtils.EMPTY;

        if ("im".equalsIgnoreCase(beeWorksTab.tabId)) {
            imageOff = "tab_icon_message_off";
            imageHover = "tab_icon_message_hover";

        } else if ("contact".equalsIgnoreCase(beeWorksTab.tabId)) {
            imageOff = "tab_icon_contact_off";
            imageHover = "tab_icon_contact_hover";

        } else if ("app".equalsIgnoreCase(beeWorksTab.tabId)) {
            imageOff = "tab_icon_app_off";
            imageHover = "tab_icon_app_hover";

        } else if ("find".equalsIgnoreCase(beeWorksTab.tabId)) {
            imageOff = "tab_icon_find_off";
            imageHover = "tab_icon_find_hover";

        } else if ("me".equalsIgnoreCase(beeWorksTab.tabId)) {
            imageOff = "tab_icon_me_off";
            imageHover = "tab_icon_me_hover";

        } else if ("native".equalsIgnoreCase(beeWorksTab.tabId)) {
            imageOff = "tab_icon_me_off";
            imageHover = "tab_icon_me_hover";

        } else if ("h5".equalsIgnoreCase(beeWorksTab.tabId)) {
            imageOff = "tab_icon_finding_off";
            imageHover = "tab_icon_finding_hover";

        }

        tab.setTextImageResource(imageOff);
        tab.setSelectedImageResource(imageHover);

        tab.setTextImageResourceFromBeeworks(beeWorksTab.iconUnSelected);
        tab.setSelectedImageResourceFromBeeworks(beeWorksTab.iconSelected);

        return tab;
    }
}
