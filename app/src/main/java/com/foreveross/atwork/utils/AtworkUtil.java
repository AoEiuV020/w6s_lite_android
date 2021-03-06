package com.foreveross.atwork.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.BuildConfig;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.app.AppAsyncNetService;
import com.foreveross.atwork.api.sdk.cordova.CordovaAsyncNetService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.domain.AppVersions;
import com.foreveross.atwork.infrastructure.model.employee.DataSchema;
import com.foreveross.atwork.infrastructure.model.settingPage.W6sGeneralSetting;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.shared.EmpIncomingCallShareInfo;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.support.StringConstants;
import com.foreveross.atwork.infrastructure.utils.AppUtil;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.AudioUtil;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.NetworkStatusUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil;
import com.foreveross.atwork.manager.DomainSettingsHelper;
import com.foreveross.atwork.manager.EmpIncomingCallManager;
import com.foreveross.atwork.manager.WorkplusUpdateManager;
import com.foreveross.atwork.modules.aboutatwork.activity.AboutAtWorkActivity;
import com.foreveross.atwork.modules.aboutatwork.activity.AppUpgradeActivity;
import com.foreveross.atwork.modules.advertisement.activity.AdvertisementActivity;
import com.foreveross.atwork.modules.app.model.WifiPunchListInfo;
import com.foreveross.atwork.modules.main.activity.SplashActivity;
import com.foreveross.atwork.support.BaseActivity;
import com.foreveross.theme.manager.SkinMaster;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;

import static com.foreveross.atwork.infrastructure.support.AtworkConfig.sLastUpdateEmpIncomingCallTime;

/**
 * Created by lingen on 15/6/1.
 * Description:
 */
public class AtworkUtil {

    /**
     * ???????????????????????????
     * */
    public static boolean isSystemCalling() {
        return AudioUtil.isPhoneCalling(AtworkApplicationLike.baseContext);
    }

    //?????????????????????
    public static boolean isFoundNewVersion(Context context) {
        int currentVersion = AppUtil.getVersionCode(context);
        return CommonShareInfo.isFoundNewVersionCode(context, currentVersion);
    }

    //???????????????????????????
    public static String getNewVersionName(Context context) {
        String currentVersionName = AppUtil.getVersionName(context);
        return CommonShareInfo.getNewVersionName(context, currentVersionName);
    }

    public static void hideInput(Activity activity) {
        WeakReference<Activity> weakReference = new WeakReference<>(activity);
        hideInput(weakReference);
    }

    /**
     * ????????????
     */
    public static void hideInput(WeakReference<Activity> activityWeakReference) {
        Activity activity = activityWeakReference.get();
        if (null != activity) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive() && activity.getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }


    public static void hideInput(Activity activity, EditText editText) {
        if (activity == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    public static void hideInput(Activity activity, View view) {
        if (activity == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }



    public static void showInput(Activity activity, EditText editText) {
        WeakReference<Activity> ref = new WeakReference<>(activity);
        showInput(ref, editText);
    }

    public static void showInput(WeakReference<Activity> activityWeakReference, EditText editText) {
        Activity activity = activityWeakReference.get();
        if (null != activity) {
            editText.requestFocus();

            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
        }
    }

    public static String getInputName(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
    }

    /**
     * ????????????
     */
    public static void checkUpdate(final Activity act, boolean immediately) {

        LogUtil.e("checkUpdate ~~~~~  atworkUtil");

        long current = System.currentTimeMillis();

        if (immediately || current - AtworkConfig.sLastUpdateDomainSettingTime >= AtworkConfig.INTERVAL_REFRESH_DOMAIN_SETTINGS || CommonShareInfo.isForcedUpdatedState(act)) {
            DomainSettingsHelper.getInstance().getDomainSettingsFromRemote(act, true, null);
            AtworkConfig.sLastUpdateDomainSettingTime = current;
        }

        if (current - sLastUpdateEmpIncomingCallTime >= AtworkConfig.INTERVAL_REFRESH_DOMAIN_SETTINGS * 2) {
            onEmpIncomingDataCheck(act);
            sLastUpdateEmpIncomingCallTime = current;
        }

    }

    public static void updateApp(BaseActivity activity, @Nullable AppVersions appVersions, boolean silentMode) {
        if (appVersions == null) {
//            CommonShareInfo.clearNewVersionCode(activity);
//            CommonShareInfo.setForcedUpdatedState(activity, false);
//            LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).sendBroadcast(new Intent(AboutMeFragment.ACTION_CHECK_UPDATE_NOTICE));

            return;
        }
        onAppUpdate(activity, appVersions.mBuildNo, appVersions.mRelease, appVersions.mForcedUpdate, appVersions.mIntro, silentMode);

    }

    /**
     * ????????????????????????????????? ?????????????????????
     *
     * @param baseActivity
     * @param remoteBuildNo
     * @param remoteRelease
     * @param forcedUpdate
     * @param intro
     */
    private static void onAppUpdate(BaseActivity baseActivity, int remoteBuildNo, String remoteRelease, boolean forcedUpdate, String intro, boolean silentMode) {
        try {

            int nowVersionCode = AppUtil.getVersionCode(baseActivity);
            if (remoteBuildNo > nowVersionCode) {
//                //?????????????????????????????????????????????????????????
//                CommonShareInfo.setNewVersionCode(baseActivity, remoteBuildNo);
//                CommonShareInfo.setNewVersionName(baseActivity, remoteRelease);
//
//                CommonShareInfo.setForcedUpdatedState(baseActivity, forcedUpdate);

                showUpgradeDialog(baseActivity, remoteBuildNo, forcedUpdate, intro, silentMode);

            } else {
//                CommonShareInfo.clearNewVersionCode(baseActivity);
//                CommonShareInfo.setForcedUpdatedState(baseActivity, false);

                if (!silentMode && baseActivity instanceof AboutAtWorkActivity) {
                    new AtworkAlertDialog(baseActivity)
                            .setTitleText(R.string.app_upgrade)
                            .setContent(R.string.tip_version_is_latest)
                            .hideDeadBtn()
                            .show();
//
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void showUpgradeDialog(final BaseActivity baseActivity, final int buildNo, final boolean forcedUpdate, final String intro, boolean silentMode) {
        if (baseActivity instanceof AdvertisementActivity || baseActivity instanceof SplashActivity) {
            return;
        }
        //??????????????????, "?????? app"???????????????, ????????????????????????????????????????????????, ???????????????????????????
        if (shouldShowUpgradeDialog(buildNo, forcedUpdate, silentMode)) {
            AtworkAlertDialog dialog = baseActivity.getUpdateAlertDialog();

            LogUtil.e("showUpgradeDialog #isShowing");

            if (dialog.isShowing()) {
                return;
            }


            dialog.setTitleText(R.string.app_upgrade);
            dialog.setContent(intro);
            dialog.setClickBrightColorListener(dialog1 -> {
                Intent intent = AppUpgradeActivity.getIntent(baseActivity, buildNo);
                intent.putExtra(AppUpgradeActivity.INTENT_FORCE_UPDATED, forcedUpdate);
                baseActivity.startActivity(intent);
            });


            //????????????????????????(debug ????????????????????????, ?????????????????????)
            if (!BuildConfig.DEBUG && forcedUpdate) {

                dialog.setCanceledOnTouchOutside(false);
                dialog.setClickDeadColorListener(dialog1 -> {
                    AtworkApplicationLike.exitAll(baseActivity, false);
                });

                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    boolean hasExit = false;

                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (KeyEvent.KEYCODE_BACK == keyCode && !hasExit) {
                            hasExit = true;
                            return true;
                        }
                        return false;

                    }
                });

            } else {
                dialog.setClickDeadColorListener(dialog12 -> WorkplusUpdateManager.INSTANCE.setTipFloatStatusAndRefresh(true));

                dialog.setOnKeyListener((dialog13, keyCode, event) -> {
                    if (KeyEvent.KEYCODE_BACK == keyCode) {

                        WorkplusUpdateManager.INSTANCE.setTipFloatStatusAndRefresh(true);

                    }
                    return false;

                });
            }

            dialog.show();

            CommonShareInfo.setLatestVersionCodeAlerted(baseActivity, buildNo);
            CommonShareInfo.setLatestAlertUpdateTime(baseActivity, System.currentTimeMillis());
        }
    }

    private static boolean shouldShowUpgradeDialog(int buildNo, boolean forcedUpdate, boolean silentMode) {
        if(forcedUpdate) {
            return true;
        }

        if(!silentMode) {
            return true;
        }

        switch (DomainSettingsManager.getInstance().getUpgradeRemindMode()) {
            case NEVER:
                return false;

            case ONCE:
                return CommonShareInfo.isVersionCodeNotAlerted(BaseApplicationLike.baseContext, buildNo);


        }


        //mode REPEATED ->
        return CommonShareInfo.shouldAlertUpdate(BaseApplicationLike.baseContext, DomainSettingsManager.getInstance().getUpdateRemindInterval());
    }

    /**
     * ????????????bitmap
     *
     * @param bitmap
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        if (true) {
            return bitmap;
        }
        try {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight()));
            final float roundPx = 10;
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.BLACK);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

            final Rect src = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());

            canvas.drawBitmap(bitmap, src, rect, paint);
            return output;
        } catch (Exception e) {
            return bitmap;
        }
    }

    public static String[] getNativeAppFiles(Context context) {
        File dir = new File(AtWorkDirUtils.getInstance().getAppUpgrade(LoginUserInfo.getInstance().getLoginUserUserName(context)));
        if (dir == null) {
            return null;
        }
        return dir.list();
    }

    public static void popAuthSettingAlert(final Context context, final String permission) {
        popAuthSettingAlert(context, permission, true);
    }

    public static void popAuthSettingAlert(final Context context, final String permission, boolean cancelable) {
        final AtworkAlertDialog alertDialog = getAuthSettingAlert(context, permission, cancelable);

        if (isBasePermissions(permission) || !cancelable) {
            alertDialog.setClickDeadColorListener(dialog -> {
                alertDialog.dismiss();

                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
            }).setCancelable(false);
        }
        alertDialog.show();
    }


    @NonNull
    public static AtworkAlertDialog getAuthSettingAlert(final Context context, final String permission) {
        return getAuthSettingAlert(context, permission, true);
    }


    @NonNull
    public static AtworkAlertDialog getAuthSettingAlert(final Context context, final String permission, boolean cancelable) {
        String content = context.getString(R.string.require_auth_basic_content, context.getString(R.string.app_name), context.getString(R.string.auth_storage_name));

        if (Manifest.permission.CAMERA.equals(permission)) {
            content = context.getString(R.string.require_auth_content, context.getString(R.string.app_name), context.getString(R.string.auth_camera_name), StringConfigHelper.getAuthCameraFunction(context));

        } else if (Manifest.permission.RECORD_AUDIO.equals(permission)) {
            content = context.getString(R.string.require_auth_content, context.getString(R.string.app_name), context.getString(R.string.auth_record_name), StringConfigHelper.getAuthRecordFunction(context));

        } else if (Manifest.permission.CALL_PHONE.equals(permission)) {
            content = context.getString(R.string.require_auth_content, context.getString(R.string.app_name), context.getString(R.string.auth_phone_name), context.getString(R.string.auth_phone_function));

        } else if (Manifest.permission.WRITE_CONTACTS.equals(permission) || Manifest.permission.READ_CONTACTS.equals(permission)) {
            content = context.getString(R.string.require_auth_content, context.getString(R.string.app_name), context.getString(R.string.auth_contact_name), context.getString(R.string.auth_contact_function));

        } else if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)) {

            content = context.getString(R.string.require_auth_content_access_fine_location);
            if(StringUtils.isEmpty(content)) {
                content = context.getString(R.string.require_auth_content, context.getString(R.string.app_name), context.getString(R.string.auth_location_name), context.getString(R.string.auth_location_function));

            }

        } else if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)) {
            content = context.getString(R.string.require_auth_basic_content, context.getString(R.string.app_name), context.getString(R.string.auth_storage_name));

        } else if (Manifest.permission.READ_PHONE_STATE.equals(permission)) {
            content = context.getString(R.string.require_auth_content, context.getString(R.string.app_name), context.getString(R.string.auth_phone_state_name), StringConfigHelper.getAuthPhotoStateFunction(context));
        } else if (Manifest.permission.WRITE_CALENDAR.equals(permission) || Manifest.permission.READ_CALENDAR.equals(permission)) {
            content = context.getString(R.string.require_auth_content, context.getString(R.string.app_name), context.getString(R.string.calendar), context.getString(R.string.my_calendar));
        }

        final AtworkAlertDialog alertDialog = new AtworkAlertDialog(context);
        alertDialog.setTitleText(R.string.require_auth_title)
                .setContent(content)
                .setBrightBtnText(R.string.setting)
                .setClickBrightColorListener(dialog -> {
                    alertDialog.shouldHandleDismissEvent = false;

                    IntentUtil.startAppSettings(context);

                    if (isBasePermissions(permission) || !cancelable) {
                        if (context instanceof Activity) {
                            ((Activity) context).finish();
                        }

                    }
                });
        return alertDialog;
    }


    public static void checkBasePermissions(Activity activity) {
        if (!AtworkUtil.hasBasePermissions(activity)) {
            activity.startActivity(SplashActivity.getIntent(activity));
            activity.finish();
        }
    }

    /**
     * Deprecated in 4.9.0. ???????????????????????????????????????????????????????????????????????????????????????????????????true
     */
    @Deprecated
    public static boolean isBasePermissions(String permission) {

//        if(Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)) {
//            return true;
//        }
//
//
//        if(Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission)) {
//            return true;
//        }
//
//
//        if(Manifest.permission.READ_PHONE_STATE.equals(permission)) {
//            return true;
//        }

//        if (Manifest.permission.CAMERA.equalsIgnoreCase(permission)) {
//            return true;
//        }

        return false;
    }

    /**
     * Deprecated in 4.9.0. ??????????????????????????????????????????????????????????????????????????????
     *  ?????????????????????????????????????????????????????????????????????????????????
     */
    @Deprecated
    public static boolean hasBasePermissions(Context context) {

//        if(!(PermissionsManager.getInstance().hasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
//            return false;
//        }
//
//        if(!(PermissionsManager.getInstance().hasPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE))) {
//            return false;
//        }
//
//        if(!(PermissionsManager.getInstance().hasPermission(context, Manifest.permission.READ_PHONE_STATE))) {
//            return false;
//        }

        return true;

    }


    /**
     * ??????????????????bitmap
     */
    public static Bitmap createBitmapByString(Context context, String str) {
        //??????????????????????????????16sp
        int size = DensityUtil.sp2px(16);
        Paint paint = new Paint();
        paint.setTextSize(size);
        //?????????????????????????????????
        int width = (int) paint.measureText(str);
        //?????????+10??????????????????????????????????????????????????????
        int height = 10 + size;
        Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
//        c.drawColor(Color.BLUE);
        Paint stringPaint = new Paint();
        stringPaint.setAntiAlias(true);
        stringPaint.setTextSize(size);
        stringPaint.setColor(Color.parseColor("#000333"));
        //????????????????????? y???????????????????????????????????????
        c.drawText(str, 0, DensityUtil.DP_8_TO_PX * 2, stringPaint);
        c.save();
        c.restore();

        return bm;
    }

    public static String getAtAllI18n(String text) {
        if(text.contains("@????????????")) {
            return text.replace("@????????????", StringConstants.SESSION_CONTENT_AT_ALL);

        } else if(text.contains("@All")) {
            return text.replace("@All", StringConstants.SESSION_CONTENT_AT_ALL);

        }

        return text;
    }

    public static String getUserDataSchemaAliasI18n(String alias) {
        if("??????".equalsIgnoreCase(alias)) {
            return AtworkApplicationLike.getResourceString(R.string.avatar);
        }

        if("??????".equalsIgnoreCase(alias)) {
            return AtworkApplicationLike.getResourceString(R.string.nickname);
        }

        if("??????".equalsIgnoreCase(alias)) {
            return AtworkApplicationLike.getResourceString(R.string.nickname);
        }

        if("???????????????".equalsIgnoreCase(alias)) {
            return AtworkApplicationLike.getResourceString(R.string.qr_postcard);

        }

        if("??????".equalsIgnoreCase(alias)) {
            return AtworkApplicationLike.getResourceString(R.string.sex);
        }

        if("??????".equalsIgnoreCase(alias)) {
            return AtworkApplicationLike.getResourceString(R.string.birthday);
        }

        if ("??????".equalsIgnoreCase(alias)) {
            return AtworkApplicationLike.getResourceString(R.string.item_email);
        }

        if ("??????".equalsIgnoreCase(alias)) {
            return AtworkApplicationLike.getResourceString(R.string.auth_phone_name);
        }

        return alias;
    }


    public static String getEmployeeDataSchemaAliasI18n(DataSchema dataSchema) {
        String aliasI18n = dataSchema.getAliasI18n(AtworkApplicationLike.baseContext);
        if(!StringUtils.isEmpty(aliasI18n)) {
            return aliasI18n;
        }


        aliasI18n = dataSchema.mAlias;
        //?????????????????????
        if ("??????".equalsIgnoreCase(aliasI18n)) {
            if (!LanguageUtil.isZhLocal(AtworkApplicationLike.baseContext)) {
                aliasI18n = "Birthday";
            }

        }
        if ("??????".equalsIgnoreCase(aliasI18n)) {
            if (!LanguageUtil.isZhLocal(AtworkApplicationLike.baseContext)) {
                aliasI18n = "Name";
            }
        }
        if ("??????".equalsIgnoreCase(aliasI18n)) {
            if (!LanguageUtil.isZhLocal(AtworkApplicationLike.baseContext)) {
                aliasI18n = "Title";
            } else {
                if (!"zh-cn".equalsIgnoreCase(LanguageUtil.getWorkplusLocaleTag(AtworkApplicationLike.baseContext))) {
                    aliasI18n = "??????";
                }
            }
        }
        if ("??????".equalsIgnoreCase(aliasI18n)) {
            if (!LanguageUtil.isZhLocal(AtworkApplicationLike.baseContext)) {
                aliasI18n = "Email Address";
            } else {
                if (!"zh-cn".equalsIgnoreCase(LanguageUtil.getWorkplusLocaleTag(AtworkApplicationLike.baseContext))) {
                    aliasI18n = "????????????";
                }
            }
        }
        if ("??????".equalsIgnoreCase(aliasI18n)) {
            if (!LanguageUtil.isZhLocal(AtworkApplicationLike.baseContext)) {
                aliasI18n = "Mobile";
            } else {
                if (!"zh-cn".equalsIgnoreCase(LanguageUtil.getWorkplusLocaleTag(AtworkApplicationLike.baseContext))) {
                    aliasI18n = "?????????";
                }
            }
        }
        return aliasI18n;
    }


    public static String getSessionNameI18N(Session session) {
        return getSessionNameI18N(session, session.name);
    }

    public static String getSessionNameI18N(Session session, String name) {

        if (!"zh-cn".equalsIgnoreCase(LanguageUtil.getWorkplusLocaleTag(AtworkApplicationLike.baseContext))) {
            if(SessionType.Notice == session.type) {
                if(StringConstants.SESSION_NAME_ORG_APPLY.equals(name)) {
                    return AtworkApplicationLike.getResourceString(R.string.org_applying);

                } else if(StringConstants.SESSION_NAME_SYSTEM_NOTICE.equals(name)) {
                    return AtworkApplicationLike.getResourceString(R.string.system_notice);

                } else if(StringConstants.SESSION_NAME_FRIEND_NOTICE.equals(name)) {
                    return AtworkApplicationLike.getResourceString(R.string.new_friend_in_btn);

                } else if(StringConstants.SESSION_NAME_ASSET_NOTICE.equals(name)) {
                    return AtworkApplicationLike.getResourceString(R.string.transaction_assistant);
                } else if (StringConstants.SESSION_NAME_FILE_SHARE_NOTICE.equalsIgnoreCase(name)) {
                    return AtworkApplicationLike.getResourceString(R.string.share_file_notice);
                }

            } else if(SessionType.Local == session.type) {
                if(Session.EntryType.To_K9Email == session.entryType) {
                    return AtworkApplicationLike.getResourceString(R.string.my_email);
                }
            }
        }

        return name;
    }


    public static String getMessageTypeNameI18N(Context context, Session session, String typeName) {
        if (!"zh-cn".equalsIgnoreCase(LanguageUtil.getWorkplusLocaleTag(context))) {

            if(SessionType.User == session.type || SessionType.Service == session.type) {
                return getP2pSessionTxtI18N(typeName);


            } else if(SessionType.Discussion == session.type) {
                //@all ??????
                if(typeName.contains(StringConstants.SEMICOLON + StringConstants.SESSION_CONTENT_AT_ALL)) {
                    return typeName.replace(StringConstants.SESSION_CONTENT_AT_ALL, "@" + context.getString(R.string.at_all_group));
                }

                return getDiscussionTxtI18N(typeName);

            }

        }


        return typeName;
    }


    private static String getDiscussionTxtI18N(String typeName) {
        String txtI18N = replaceSessionTxtI18N(Rule.MIDDLE_WITH_SEMICOLON, typeName, StringConstants.SESSION_CONTENT_FILE, R.string.file);
        if(!txtI18N.equals(typeName)) {
            return txtI18N;
        }

        txtI18N = replaceSessionTxtI18N(Rule.END_WITH_SEMICOLON, typeName, StringConstants.SESSION_CONTENT_IMG, R.string.message_type_image);
        if(!txtI18N.equals(typeName)) {
            return txtI18N;
        }

        txtI18N = replaceSessionTxtI18N(Rule.END_WITH_SEMICOLON, typeName, StringConstants.SESSION_CONTENT_MICRO_VIDEO, R.string.label_micro_video_chat_pop);
        if(!txtI18N.equals(typeName)) {
            return txtI18N;
        }
        txtI18N = replaceSessionTxtI18N(Rule.END_WITH_SEMICOLON, typeName, StringConstants.SESSION_CONTENT_VOICE, R.string.audio3);
        if(!txtI18N.equals(typeName)) {
            return txtI18N;
        }
        txtI18N = replaceSessionTxtI18N(Rule.END_WITH_SEMICOLON, typeName, StringConstants.SESSION_CONTENT_VOIP, R.string.session_title_voip_meeting);
        if(!txtI18N.equals(typeName)) {
            return txtI18N;
        }
        txtI18N = replaceSessionTxtI18N(Rule.MIDDLE_WITH_SEMICOLON, typeName, StringConstants.SESSION_CONTENT_LINK_SHARE, R.string.message_type_link);
        if(!txtI18N.equals(typeName)) {
            return txtI18N;
        }
        txtI18N = replaceSessionTxtI18N(Rule.END_WITH_SEMICOLON, typeName, StringConstants.SESSION_CONTENT_CARD_SHARE, R.string.label_personal_card_chat_pop);
        if(!txtI18N.equals(typeName)) {
            return txtI18N;
        }
        txtI18N = replaceSessionTxtI18N(Rule.MIDDLE_WITH_SEMICOLON, typeName, StringConstants.SESSION_LOCATION, R.string.location);
        if(!txtI18N.equals(typeName)) {
            return txtI18N;
        }
        txtI18N = replaceSessionTxtI18N(Rule.END_WITH_SEMICOLON, typeName, StringConstants.SESSION_CONTENT_ORG_SHARE, R.string.message_type_invite_join_org);
        if(!txtI18N.equals(typeName)) {
            return txtI18N;
        }
        txtI18N = replaceSessionTxtI18N(Rule.MIDDLE_WITH_SEMICOLON, typeName, StringConstants.SESSION_CONTENT_UNKNOWN_MESSAGE, R.string.unknown_message_session_text);
        if(!txtI18N.equals(typeName)) {
            return txtI18N;
        }
        txtI18N = replaceSessionTxtI18N(Rule.MIDDLE_WITH_SEMICOLON, typeName, StringConstants.SESSION_NAME_STICKER, R.string.message_type_sticker);

        txtI18N = replaceSessionTxtI18N(Rule.MIDDLE_WITH_SEMICOLON, typeName, StringConstants.SESSION_CONTENT_MULTIPART, R.string.session_multipart_chat);

        if(!txtI18N.equals(typeName)) {
            return txtI18N;
        }
        return typeName;
    }

    private static String getP2pSessionTxtI18N(String typeName) {
        String txtI18N = replaceSessionTxtI18N(Rule.TOTAL, typeName, StringConstants.SESSION_CONTENT_ARTICLE, R.string.article);

        if(!txtI18N.equals(typeName)) {
            return txtI18N;
        }

        txtI18N = replaceSessionTxtI18N(Rule.TOTAL, typeName, StringConstants.SESSION_CONTENT_BURN_MESSAGE, R.string.burn_message_label);

        if(!txtI18N.equals(typeName)) {
            return txtI18N;
        }

        txtI18N = replaceSessionTxtI18N(Rule.TOTAL, typeName, StringConstants.SESSION_CONTENT_UNKNOWN_MESSAGE, R.string.unknown_message_session_text);
        if(!txtI18N.equals(typeName)) {
            return txtI18N;
        }

        txtI18N = replaceSessionTxtI18N(Rule.START, typeName, StringConstants.SESSION_CONTENT_FILE, R.string.file);

        if(!txtI18N.equals(typeName)) {
            return txtI18N;
        }

        txtI18N = replaceSessionTxtI18N(Rule.TOTAL, typeName, StringConstants.SESSION_CONTENT_IMG, R.string.message_type_image);

        if(!txtI18N.equals(typeName)) {
            return txtI18N;
        }

        txtI18N = replaceSessionTxtI18N(Rule.TOTAL, typeName, StringConstants.SESSION_CONTENT_MICRO_VIDEO, R.string.label_micro_video_chat_pop);

        if(!txtI18N.equals(typeName)) {
            return txtI18N;
        }

        txtI18N = replaceSessionTxtI18N(Rule.TOTAL, typeName, StringConstants.SESSION_CONTENT_VOICE, R.string.audio3);

        if(!txtI18N.equals(typeName)) {
            return txtI18N;
        }

        txtI18N = replaceSessionTxtI18N(Rule.TOTAL, typeName, StringConstants.SESSION_CONTENT_VOIP, R.string.session_title_voip_meeting);

        if(!txtI18N.equals(typeName)) {
            return txtI18N;
        }

        txtI18N = replaceSessionTxtI18N(Rule.START, typeName, StringConstants.SESSION_CONTENT_LINK_SHARE, R.string.message_type_link);

        if(!txtI18N.equals(typeName)) {
            return txtI18N;
        }

        txtI18N = replaceSessionTxtI18N(Rule.START, typeName, StringConstants.SESSION_LOCATION, R.string.location);

        if(!txtI18N.equals(typeName)) {
            return txtI18N;
        }


        txtI18N = replaceSessionTxtI18N(Rule.TOTAL, typeName, StringConstants.SESSION_CONTENT_CARD_SHARE, R.string.label_personal_card_chat_pop);

        if(!txtI18N.equals(typeName)) {
            return txtI18N;
        }

        txtI18N = replaceSessionTxtI18N(Rule.TOTAL, typeName, StringConstants.SESSION_CONTENT_ORG_SHARE, R.string.message_type_invite_join_org);

        if(!txtI18N.equals(typeName)) {
            return txtI18N;
        }

        txtI18N = replaceSessionTxtI18N(Rule.TOTAL, typeName, StringConstants.SESSION_CONTENT_MULTIPART, R.string.session_multipart_chat);

        if(!txtI18N.equals(typeName)) {
            return txtI18N;
        }

        return typeName;
    }

    public static String replaceSessionTxtI18N(int rule, String txt, String strFlag, int resI18N) {
        if(Rule.TOTAL == rule) {
            if(strFlag.equalsIgnoreCase(txt)) {
                return getI18NWithWithFlag(resI18N);
            }

        } else if(Rule.START == rule) {
            if(txt.startsWith(strFlag)) {
                return txt.replace(strFlag, getI18NWithWithFlag(resI18N));
            }
        } else if(Rule.MIDDLE_WITH_SEMICOLON == rule) {
            if(txt.contains(StringConstants.SEMICOLON + strFlag)) {
                return txt.replace(strFlag, getI18NWithWithFlag(resI18N));
            }
        } else if(Rule.END_WITH_SEMICOLON == rule) {
            if(txt.endsWith(StringConstants.SEMICOLON + strFlag)) {
                return txt.replace(strFlag, getI18NWithWithFlag(resI18N));
            }
        }

        return txt;
    }

    public static String getI18NWithWithFlag(int resId) {
        return "[" +  AtworkApplicationLike.getResourceString(resId) + "]";
    }


    public static String tempMakeI18n(String tabName) {
        if("??????".equalsIgnoreCase(tabName)) {
            tabName = AtworkApplicationLike.getResourceString(R.string.item_find);
        }
        return tabName;
    }

    /**
     * wifi ????????????
     * @param context
     */
    public static void autoPunchInWifi(Context context) {

        if(!BeeWorks.getInstance().config.enableCheckIn) {
            return;
        }

        String curOrgId = PersonalShareInfo.getInstance().getCurrentOrg(context);
        if (TextUtils.isEmpty(curOrgId)) {
            return;
        }
        CordovaAsyncNetService.getUserTicket(context, new CordovaAsyncNetService.GetUserTicketListener() {
            @Override
            public void getUserTicketSuccess(String userTicket) {
                String url = UrlConstantManager.getInstance().getWifiAutoPunch();
                AppAsyncNetService service = new AppAsyncNetService(context);
                WifiPunchListInfo listInfo = new WifiPunchListInfo();
                WifiPunchListInfo.WifiPunchInfo punchInfo = new WifiPunchListInfo.WifiPunchInfo();
                punchInfo.mTicket = userTicket;
                punchInfo.mDeviceId = AtworkConfig.getDeviceId();
                punchInfo.mDomainId = AtworkConfig.DOMAIN_ID;
                punchInfo.mOrgId = curOrgId;
                punchInfo.mUserId = LoginUserInfo.getInstance().getLoginUserId(context);
                punchInfo.mMacAddress = NetworkStatusUtil.getWifiBSSIDAddress(context);
                listInfo.mList.add(punchInfo);
                service.autoPunchInWifi(url, new Gson().toJson(listInfo), success -> {
                    if (success) {
                        AtworkToast.showResToast(R.string.auto_wifi_punch_success);
                    }
                });
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
            }
        });
    }


    public static void tempHandleIconColor(ImageView ivIcon) {
        Drawable drawable = SkinMaster.getInstance().transformImmutable(ivIcon.getDrawable(), ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.common_item_black));
        ivIcon.setImageDrawable(drawable);
    }

    public final class Rule {

        public final static int TOTAL = 0;

        public final static int START = 1;

        public final static int MIDDLE_WITH_SEMICOLON = 2;

        public final static int END_WITH_SEMICOLON = 3;
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        try {
            // ??????ListView?????????Adapter
            ListAdapter listAdapter = listView.getAdapter();
            if (listAdapter == null) {
                return;
            }

            int totalHeight = 0;
            for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
                // listAdapter.getCount()????????????????????????
                View listItem = listAdapter.getView(i, null, listView);
                // ????????????View ?????????
                listItem.measure(0, 0);
                // ??????????????????????????????
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            // listView.getDividerHeight()???????????????????????????????????????
            // params.height??????????????????ListView???????????????????????????
            listView.setLayoutParams(params);
        } catch (Exception e) {
        }
    }

    /**
     * ????????????????????????????????????????????????
     */
    public static void onEmpIncomingDataCheck(Context context) {

        if(AtworkConfig.SETTING_PAGE_CONFIG.isInvisible(W6sGeneralSetting.EMP_INCOMING_ASSISTANT)) {
            return;
        }

        int lastStatus = EmpIncomingCallShareInfo.getInstance().getEmpIncomingCallSyncStatus(context);
        long lastSyncSuccessTime = EmpIncomingCallShareInfo.getInstance().getLastSyncFinishTime(context);
        long currentTime = System.currentTimeMillis();

//        //???????????????????????????
//        if (currentTime - lastStatus < 0) {
//            return;
//        }
        boolean isSameDate = TimeUtil.isSameDate(currentTime, lastSyncSuccessTime);
        //???????????????????????????????????????????????????????????????????????????????????????????????????
        if (lastStatus == 0 && isSameDate) {
            return;
        }
        //???????????????????????????????????????????????????????????????????????????
        if (!isSameDate) {
            EmpIncomingCallShareInfo.getInstance().setEmpIncomingCallSyncStatus(context, -1);
        }
        EmpIncomingCallManager.getInstance().startFetchInComingCallRemote(context);

    }

    /**
     * ???????????????ID????????????????????????
     * @param context
     * @param pid ??????ID
     * @param p_name ?????????
     * @return true??????????????????
     */
    public static boolean isPidOfProcessName(Context context, int pid, String p_name) {
        if (p_name == null)
            return false;
        boolean isMain = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //??????????????????
        for (ActivityManager.RunningAppProcessInfo process : am.getRunningAppProcesses()) {
            if (process.pid == pid) {

                LogUtil.e("pid " + pid + " process.processName " +  process.processName);

                //??????ID???????????????????????????????????????
                if (process.processName.equals(p_name)) {
                    isMain = true;
                }
                break;
            }
        }
        return isMain;
    }

    /**
     * ??????????????????
     * @param context ?????????
     * @return ????????????
     */
    public static String getMainProcessName(Context context) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getApplicationInfo(context.getPackageName(), 0).processName;
    }


    /**
     * ?????????????????????
     * @param context ?????????
     * @return true????????????
     */
    public static boolean isMainProcess(Context context) throws PackageManager.NameNotFoundException {
        return isPidOfProcessName(context, android.os.Process.myPid(), getMainProcessName(context));
    }

    public static boolean isMainProcessEnsured(Context context) {
        try {
            return isMainProcess(context);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return isMainProcessByProc(context);
    }


    public static boolean isMainProcessByProc(Context context)  {
        // ??????????????????
        String packageName = context.getPackageName();
        // ?????????????????????
        String processName = getProcessNameByProc(android.os.Process.myPid());
        return packageName.equals(processName);
    }



    public static String getProcessNameByProc() {
        return getProcessNameByProc(android.os.Process.myPid());
    }


    /**
     * ?????????????????????????????????
     *
     * @param pid ?????????
     * @return ?????????
     */
    private static String getProcessNameByProc(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

}
