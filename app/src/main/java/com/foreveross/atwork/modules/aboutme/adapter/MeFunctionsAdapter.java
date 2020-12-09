package com.foreveross.atwork.modules.aboutme.adapter;

import android.Manifest;
import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.appcompat.widget.SwitchCompat;

import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.app.model.LightNoticeData;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.manager.OrganizationSettingsManager;
import com.foreveross.atwork.infrastructure.model.app.Shortcut;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.MapUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil;
import com.foreveross.atwork.modules.aboutme.component.MeFunctionsItemView;
import com.foreveross.atwork.modules.aboutme.fragment.AboutMeFragment;
import com.foreveross.atwork.modules.aboutme.model.ListItemType;
import com.foreveross.atwork.modules.aboutme.model.MeFunctionItem;
import com.foreveross.atwork.modules.aboutme.model.ShortcutItem;
import com.foreveross.atwork.modules.aboutme.model.SystemItem;
import com.foreveross.atwork.modules.main.activity.MainActivity;
import com.foreveross.atwork.modules.main.data.TabNoticeManager;
import com.foreveross.atwork.modules.main.helper.TabHelper;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.IntentUtil;
import com.foreveross.atwork.utils.StringConfigHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by Reyzhang on 2015/3/24.
 */
public class MeFunctionsAdapter extends BaseAdapter {
    private Activity mContext;
    private ProgressDialogHelper mVpnProgressDialog;

    private boolean mIsConnectingVpn = false;

    private SwitchCompat mSwitchButton;

    private List<MeFunctionItem> mItemGroupList = new ArrayList<>();
    private TreeMap<Integer, ArrayList<Shortcut>> mShortcutGroup = new TreeMap<>();
    private boolean mIsClickSwitchCompat;
    private long mLastClickOpenVpnSuccessfullyTime = -1;


    public MeFunctionsAdapter(Activity context) {
        mContext = context;
        refreshItemList();
        mVpnProgressDialog = new ProgressDialogHelper(mContext);
    }

    public void setShortcutGroup(TreeMap<Integer, ArrayList<Shortcut>> shortcutGroup) {
        mShortcutGroup = shortcutGroup;
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void refreshItemList() {
        mItemGroupList.clear();

        buildGroup1();

        buildGroup2();

        buildGroupShortcut();

        buildGroup5();

        notifyDataSetChanged();
    }

    private void buildGroupMyDownload() {
        SystemItem meFunctionsItem = SystemItem.newInstance()
                .setListItemType(ListItemType.DOWNLOAD)
                .setTitle(mContext.getString(R.string.down_load))
                .setIcon(R.mipmap.icon_my_down_load);


        mItemGroupList.add(meFunctionsItem);

//        if (BaseApplicationLike.sIsDebug) {
//            mItemGroupList.add(meFunctionsItem);
//        }
    }

    private void buildGroup1() {
        buildGroupMyDownload();
    }



    private void buildGroup2() {

        if(AtworkConfig.ABOUT_ME_CONFIG.isContactItemInAboutMe()) {
            SystemItem meFunctionsItem = SystemItem.newInstance()
                    .setListItemType(ListItemType.CONTACT)
                    .setTitle(mContext.getString(R.string.item_contact))
                    .setIcon(R.mipmap.icon_contact_dark);

            mItemGroupList.add(meFunctionsItem);
        }


        if (AtworkConfig.OPEN_DROPBOX) {
            SystemItem meFunctionsItem = SystemItem.newInstance()
                    .setListItemType(ListItemType.DROPBOX)
                    .setTitle(mContext.getString(R.string.my_dropbox))
                    .setIcon(R.mipmap.icon_dropbox);

            mItemGroupList.add(meFunctionsItem);

        }

    }


    private void buildGroupShortcut() {
        if (!MapUtil.isEmpty(mShortcutGroup)) {
            for (List<Shortcut> shortcutList : mShortcutGroup.values()) {


                for (int i = 0; i < shortcutList.size(); i++) {
                    Shortcut shortcut = shortcutList.get(i);
                    ShortcutItem meFunctionsItem = new ShortcutItem();
                    meFunctionsItem.mShortcut = shortcut;
                    if(0 != i) {
                        meFunctionsItem.mDaggerUp = false;
                    }


                    mItemGroupList.add(meFunctionsItem);

                }



            }
        }
    }

    private void buildGroup5() {

        SystemItem meFunctionsItem1 = SystemItem.newInstance()
                .setListItemType(ListItemType.SETTING)
                .setTitle(mContext.getString(R.string.setting))
                .setIcon(R.mipmap.icon_me_set);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mContext.getString(R.string.about));

        //控制"关于xxx"的 xxx 是否显示出来
        if (needAboutName()) {
            try {
                String appName = BeeWorks.getInstance().config.copyright.companyName;
                if (!TextUtils.isEmpty(appName)) {
                    stringBuilder.append(appName);
                } else {
                    stringBuilder.append("WorkPlus");
                }
            } catch (NullPointerException e) {
                stringBuilder.append("WorkPlus");
            }
        }

        SystemItem meFunctionsItem2 = SystemItem.newInstance()
                .setListItemType(ListItemType.ABOUT)
                .setTitle(stringBuilder.toString())
                .setIcon(R.mipmap.icon_dark_me_info);
//        meFunctionsItem2.setGroupEnd(true);

        mItemGroupList.add(meFunctionsItem1);
//        mItemGroupList.add(meFunctionsItem2);

    }

    private boolean needAboutName() {
        return !AtworkConfig.ABOUT_APP_LABEL_PURE && LanguageUtil.isZhLocal(mContext);
    }


    @Override
    public int getCount() {
        return mItemGroupList.size();
    }

    @Override
    public MeFunctionItem getItem(int position) {
        return mItemGroupList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = new MeFunctionsItemView(mContext);

        }
        final MeFunctionsItemView meFunctionsItemView = (MeFunctionsItemView) convertView;

        MeFunctionItem meFunctionItem = getItem(position);

        meFunctionsItemView.refreshItemView(meFunctionItem);

        handleItemViewRedDot(meFunctionsItemView, meFunctionItem);

//        SkinMaster.getInstance().changeTheme((ViewGroup) convertView);
        return convertView;
    }

    /**
     * 控制红点显示
     */
    public void handleItemViewRedDot(MeFunctionsItemView meFunctionsItemView, MeFunctionItem meFunctionItem) {

        ListItemType itemType = meFunctionItem.mListItemType;

        if (ListItemType.SETTING == itemType) {
            if (AtworkUtil.isFoundNewVersion(mContext)) {
                meFunctionsItemView.showDot();
                return;
            }
        }

        if (ListItemType.MAIL == itemType) {
            if (DomainSettingsManager.getInstance().handleEmailSettingsFeature()) {
                LightNoticeData lightNoticeJson = TabNoticeManager.getInstance().getLightNoticeData(TabNoticeManager.getInstance().getEmailId());
                if (null != lightNoticeJson) {
                    meFunctionsItemView.refreshLightNotice(lightNoticeJson);
                    return;

                }
            }
        }
        if (ListItemType.MAIL_SETTING == itemType) {

        }


        if (ListItemType.SHORTCUT == itemType) {
            String appId = ((ShortcutItem) meFunctionItem).mShortcut.mAppId;

            LightNoticeData lightNoticeJson = TabNoticeManager.getInstance().getLightNoticeDataInRange(TabHelper.getAboutMeFragmentId(), appId);
            if (null != lightNoticeJson) {
                meFunctionsItemView.refreshLightNotice(lightNoticeJson);
                return;
            }

        }

        meFunctionsItemView.showNothing();

    }


    public void dismissProgressDialog() {
        if (null != mVpnProgressDialog) {
            mVpnProgressDialog.dismiss();
        }
    }


    public void showPermissionDialog() {
        AtworkAlertDialog alertDialog = AtworkUtil.getAuthSettingAlert(mContext, Manifest.permission.READ_PHONE_STATE);
        alertDialog.hideDeadBtn();
        String content = mContext.getString(R.string.require_auth_content_need_reload,
                mContext.getString(R.string.app_name),
                mContext.getString(R.string.auth_phone_state_name),
                StringConfigHelper.getAuthPhotoStateFunction(mContext));
        alertDialog.setContent(content);
        alertDialog.setClickBrightColorListener(dialog -> IntentUtil.startAppSettings(mContext));
        alertDialog.show();
    }


    private boolean isAboutMeFragment(Activity parentActivity) {
        if (parentActivity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) parentActivity;
            LogUtil.e("selectedFragment", mainActivity.mSelectedFragment + "");
            if (mainActivity.mSelectedFragment instanceof AboutMeFragment) {
                return true;
            }
        }
        return false;
    }


    public void setShowing(boolean isShowing) {
        mIsConnectingVpn = isShowing;
    }


}
