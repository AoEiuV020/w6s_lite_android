package com.foreveross.atwork.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.cache.UCCalendarCache;
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.auth.WangwangAsyncNetService;
import com.foreveross.atwork.api.sdk.cordova.CordovaAsyncNetService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.infrastructure.model.app.NativeApp;
import com.foreveross.atwork.infrastructure.model.uccalendar.UCCalendarToken;
import com.foreveross.atwork.infrastructure.plugin.UCCalendarPlugin;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AppUtil;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.FileProviderUtil;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.UriCompat;
import com.foreveross.atwork.infrastructure.utils.rom.RomUtil;
import com.foreveross.atwork.modules.app.manager.AppManager;
import com.foreveross.atwork.manager.LightAppDownloadManager;
import com.foreveross.atwork.manager.UCCalendarManager;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.main.activity.MainActivity;
import com.foreveross.atwork.modules.main.activity.SplashActivity;
import com.foreveross.atwork.support.AtworkBaseActivity;

import org.apache.cordova.CordovaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class IntentUtil {

    private  static  final String NATIVE_APP_REPLACE_USERNAME_TAG = "${username}";

    public static void routeSystemWebView(Context context, String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
//                url = url + "&lang=" + LanguageUtil.getWorkplusLocaleTag(mContext);
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
//                    intent.setClassName("com.android.browser","com.android.browser.BrowserActivity");
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(Intent.createChooser(intent, "请选择一种浏览器"));
        }


    }

    public static void startApp(final Context context, final String appPackageName, boolean needCheckAuth, final AppBundles appBundle) {
        if(appBundle.isSchemaUri()) {
            startAppSchemaUrl(context, appPackageName, needCheckAuth, appBundle);
        } else {
            startAppIntent(context, appPackageName, needCheckAuth, appBundle);
        }
    }

    private static void startAppSchemaUrl(final Context context, final String appPackageName, boolean needCheckAuth, final AppBundles appBundle) {
        StringBuilder schemaUrl = new StringBuilder(appBundle.mInitUrl);
        String auth = StringUtils.EMPTY;

        if (appBundle.mBundleParams != null) {
            auth = appBundle.mBundleParams.get("auth");
            Set<String> set = appBundle.mBundleParams.keySet();

            for (String key : set) {
                String value = appBundle.mBundleParams.get(key);
                if (value.equalsIgnoreCase(NATIVE_APP_REPLACE_USERNAME_TAG)) {
                    appendSchemaUrl(schemaUrl, key , AtworkApplicationLike.getLoginUserSync().mUsername);
                } else {
                    appendSchemaUrl(schemaUrl, key ,value);

                }

            }
        }

        if (!needCheckAuth) {
            routeSchemaUrl(context, appPackageName, schemaUrl);

        } else {

            startAppSchemaUrlCheckAuth(context, appPackageName, auth, schemaUrl);


        }


    }

    private static void startAppSchemaUrlCheckAuth(Context context, String appPackageName, String auth, StringBuilder schemaUrl) {
        CordovaAsyncNetService.getUserTicket(context, new CordovaAsyncNetService.GetUserTicketListener() {
            @Override
            public void getUserTicketSuccess(String userTicket) {

                try {
                    appendSchemaUrl(schemaUrl,"ticket", userTicket);
                    appendSchemaUrl(schemaUrl,"KEY_TICKET", userTicket);
                    appendSchemaUrl(schemaUrl,"KEY_FROM_PLATFORM", AtworkConfig.ANDROID_PLATFORM);
                    appendSchemaUrl(schemaUrl,"KEY_DEVICE_ID", AtworkConfig.getDeviceId());
                    appendSchemaUrl(schemaUrl,"KEY_TENANT_ID", AtworkConfig.DOMAIN_ID);
                    appendSchemaUrl(schemaUrl,"KEY_API_HOST", AtworkConfig.API_URL);


                    if(WangwangAsyncNetService.AUTH_TAG.equalsIgnoreCase(auth)) {
                        WangwangAsyncNetService.auth(context, result -> {
                            if (!StringUtils.isEmpty(result)) {
                                appendSchemaUrl(schemaUrl,"token", result);

                                routeSchemaUrl(context, appPackageName, schemaUrl);

                            } else {
                                AtworkToast.showResToast(R.string.network_not_avaluable);
                            }

                        });
                    } else {
                        routeSchemaUrl(context, appPackageName, schemaUrl);


                    }



                } catch (Exception e) {
                    e.printStackTrace();
                    AtworkToast.showResToast(R.string.cannot_start_native_app_intent);
                }
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleError(ErrorHandleUtil.Module.App, errorCode, errorMsg);
            }
        });
    }

    private static void routeSchemaUrl(Context context, String appPackageName, StringBuilder schemaUrl) {
        Uri uri = Uri.parse(schemaUrl.toString());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (!routeIgnorePackageName(uri)) {
            intent.setPackage(appPackageName);

        }
        context.startActivity(intent);
    }


    private static boolean routeIgnorePackageName(Uri uri) {
        try {
            String ignoreParams = uri.getQueryParameter(NativeApp.SCHEME_JUMP_IGNORE_PACKAGE_NAME);
            if(!StringUtils.isEmpty(ignoreParams)) {
                int ignoreResult = Integer.valueOf(ignoreParams);
                if(1 == ignoreResult) {
                    return true;
                }

            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static void appendSchemaUrl(StringBuilder schemaUrl, String key, String value) {
        if(schemaUrl.toString().contains("?")) {
            schemaUrl.append("&").append(key).append("=").append(value);

        } else {
            schemaUrl.append("?").append(key).append("=").append(value);
        }
    }

    /**
     * 启动一个app
     */
    private static void startAppIntent(final Context context, final String appPackageName, boolean needCheckAuth, final AppBundles appBundle) {
        try {
            final Intent intent = context.getPackageManager().getLaunchIntentForPackage(appPackageName);
            String auth = StringUtils.EMPTY;

            if (null != intent) {
                if (appBundle.isNativeAppBundle()) {
                    if (!TextUtils.isEmpty(appBundle.mInitUrl)) {
                        ComponentName componentName = new ComponentName(appPackageName, appBundle.mInitUrl);
                        intent.setComponent(componentName);
                    }
                }

                if (appBundle.mBundleParams != null) {
                    Set<String> set = appBundle.mBundleParams.keySet();

                    auth = appBundle.mBundleParams.get("auth");

                    for (String key : set) {
                        String value = appBundle.mBundleParams.get(key);
                        if (value.equalsIgnoreCase(NATIVE_APP_REPLACE_USERNAME_TAG)) {
                            intent.putExtra(key, AtworkApplicationLike.getLoginUserSync().mUsername);
                        } else {
                            intent.putExtra(key, value);
                        }

                    }
                }

            }


            if (!needCheckAuth) {
                context.startActivity(intent);

            } else {

                startAppIntentCheckAuth(context, auth, intent);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startAppIntentCheckAuth(Context context, String auth, Intent intent) {
        CordovaAsyncNetService.getUserTicket(context, new CordovaAsyncNetService.GetUserTicketListener() {
            @Override
            public void getUserTicketSuccess(String userTicket) {
                if (null == intent) {
                    return;
                }
                try {
                    intent.putExtra("ticket", userTicket);
                    intent.putExtra("KEY_TICKET", userTicket);
                    intent.putExtra("KEY_FROM_PLATFORM", AtworkConfig.ANDROID_PLATFORM);
                    intent.putExtra("KEY_DEVICE_ID", AtworkConfig.getDeviceId());
                    intent.putExtra("KEY_TENANT_ID", AtworkConfig.DOMAIN_ID);
                    intent.putExtra("KEY_API_HOST", AtworkConfig.API_URL);


                    if(WangwangAsyncNetService.AUTH_TAG.equalsIgnoreCase(auth)) {
                        WangwangAsyncNetService.auth(context, result -> {
                            if (!StringUtils.isEmpty(result)) {
                                intent.putExtra("token", result);

                                context.startActivity(intent);
                            } else {
                                AtworkToast.showResToast(R.string.network_not_avaluable);
                            }

                        });
                    } else {
                        context.startActivity(intent);

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    AtworkToast.showResToast(R.string.cannot_start_native_app_intent);
                }
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleError(ErrorHandleUtil.Module.App, errorCode, errorMsg);
            }
        });
    }


    public static void handleLightAppClick(Context context, AppBundles appBundle) {
        LogUtil.e("handleLightAppClick  --> ");
        IntentUtil.openLightApp(context, appBundle, appBundle.getTitleI18n(context));
    }


    /**
     * 打开一个轻应用
     *
     * @param context
     * @param app
     */
    private static void openLightApp(final Context context, final AppBundles app, String title) {
        if(AppManager.getInstance().useOfflinePackageNeedReLoad(app)) {
            loadOfflineData(context, app);


        } else {
            WebViewControlAction webViewControlAction = WebViewControlAction.newAction().setLightApp(app).setTitle(title).setArticleItem(null);
            context.startActivity(WebViewActivity.getIntent(context, webViewControlAction));
        }

    }

    public static void loadOfflineData(final Context context, AppBundles appBundle) {
        AtworkAlertDialog alert = new AtworkAlertDialog(context);
        alert.setContent("检测到应用离线包更新，是否立即更新？")
                .setBrightBtnText("立即更新")
                .setDeadBtnText("不更新")
                .setTitleText(context.getString(R.string.tip))
                .setClickBrightColorListener(dialog -> {
                    LightAppDownloadManager.getInstance().startDownload(context, appBundle);
                    if(context instanceof AtworkBaseActivity && !(context instanceof MainActivity)) {
                        ((AtworkBaseActivity) context).finish();
                    }
                })
                .setCanceledOnTouchOutside(false);
        alert.show();

    }

    /**
     * 打开系统应用
     *
     * @param context
     * @param targetUri
     */
    public static void startSystemApp(Context context, String targetUri) {

        if ("SYSTEM://CALENDAR".equalsIgnoreCase(targetUri)) {
            startSystemCalendar(context);
            return;
        }
        if ("SYSTEM://EMAIL".equalsIgnoreCase(targetUri)) {
            startRegisteredEmail(context, null);

        }
        if ("SYSTEM://QUANSHI".equalsIgnoreCase(targetUri)) {
            startUCCalendar(context);
        }

    }


    public static void startSystemCalendar(Context context) {
        try {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setDataAndType(Uri.parse("content://com.android.calendar/"), "time/epoch");
            context.startActivity(intent);
        } catch (Exception e) {
            AtworkToast.showResToast(R.string.error_system_app_not_found, context.getString(R.string.calendar));
            e.printStackTrace();
        }
    }

    /**
     * 若系统只安装了一个注册到"mailto"的邮箱, 则直接打开; 否则弹出选择框让用户选择进入
     *
     * @param context
     * @param uri
     *
     */
    public static void startRegisteredEmail(Context context, @Nullable Uri uri) {
        try {
            Uri uriAction = uri;
            if (null == uriAction) {
                uriAction = Uri.parse("mailto:");
            }
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uriAction);
            PackageManager pm = context.getPackageManager();

            List<ResolveInfo> allResInfos = pm.queryIntentActivities(emailIntent, 0);
            List<ResolveInfo> resInfos = new ArrayList<>();
            for (ResolveInfo info : allResInfos) {
                if (!"com.fsck.k9.activity.MessageCompose".equalsIgnoreCase(info.activityInfo.name)) {
                    resInfos.add(info);
                }
            }

            if (resInfos.size() > 0) {
                ResolveInfo ri;

                List<LabeledIntent> intentList = new ArrayList<>();
                for (int i = 0; i < resInfos.size(); i++) {
                    ri = resInfos.get(i);
                    String packageName = ri.activityInfo.packageName;
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setComponent(new ComponentName(ri.activityInfo.packageName, ri.activityInfo.name));
                    intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
                }

                Intent openInChooser =
                        Intent.createChooser(intentList.remove(0), "");

                LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);
                openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
                context.startActivity(openInChooser);
            } else {
                AtworkToast.showResToast(R.string.error_system_app_not_found, context.getString(R.string.email));
            }
        } catch (Exception e) {
            AtworkToast.showResToast(R.string.error_system_app_not_found, context.getString(R.string.email));
            e.printStackTrace();
        }
    }


    public static void startUCCalendar(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserRealUserName(context);
//        UCCalendarToken ucCalendarToken = new UCCalendarToken();
//        ucCalendarToken.mIsLogin = true;
//        ucCalendarToken.mUsername = "zhangsan";
//        ucCalendarToken.mToken = "5bca437e-c4ec-4850-9bd6-1f6df8f6e339";

        UCCalendarToken ucCalendarToken = UCCalendarCache.getInstance().getUCCalendarCache(username);
        if (ucCalendarToken != null && ucCalendarToken.mIsLogin) {
            doShowUCCalendar(context, ucCalendarToken.mUsername);
            return;
        }
        UCCalendarManager.getInstance().getUCCalendarToken(context, new UCCalendarPlugin.OnUCCalendarTokenListener() {
            @Override
            public void onUCCalendarTokenSuccess(UCCalendarToken token) {
                ContactQueryHelper.getCurrentContactMobile(mobile -> {

                    if(StringUtils.isEmpty(mobile)) {
                        AtworkToast.showResToast(R.string.qsy_cannot_into_meeting_by_phone);
                    } else {
                        UCCalendarManager.getInstance().setPhoneNumber(mobile);
                    }

                    doLoginUCCalendar(context, token);

                });



            }

            @Override
            public void onUCCalendarTokenFail(int error) {

                ErrorHandleUtil.handleError(ErrorHandleUtil.Module.QsyCalendar, error, StringUtils.EMPTY);
            }
        });
    }


    private static void doLoginUCCalendar(Context context, UCCalendarToken token) {
        UCCalendarManager.getInstance().onLoginUCCalendar(token.mSerialNo, token.mToken, new UCCalendarPlugin.OnUCCalendarLoginListener() {
            @Override
            public void onUCCalendarLoginSuccess() {
                doShowUCCalendar(context, token.mUsername);
            }

            @Override
            public void onUCCalendarLoginFail(String reason) {
                AtworkToast.showToast("login fail:" + reason);
            }
        });
    }

    private static void doShowUCCalendar(Context context, String username) {
        UCCalendarCache.getInstance().updateUCCalendarLoginStatus(username, true);
        UCCalendarManager.getInstance().onShowCalendar(context);
    }


    /**
     * 启动应用的设置
     */
    public static void startAppSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }

    public static void startLocationSetting(Context context){
        Intent settintIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(settintIntent);
    }

    public static void startBLESetting(Context context){
        Intent settintIntent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        context.startActivity(settintIntent);
    }

    public static void installApk(Context context, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            AtworkToast.showToast(BaseApplicationLike.baseContext.getResources().getString(R.string.file_not_exists));
            return;
        }
        Intent intent = new Intent();

        Uri path = UriCompat.getFileUriCompat(context, file);

        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(path, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        FileProviderUtil.grantRWPermission(intent);
        context.startActivity(intent);
    }

    public static void previewIntent(Context context, String filePath, String docType) {
        File file = new File(filePath);
        if (!FileUtil.isExist(filePath)) {
            AtworkToast.showToast(BaseApplicationLike.baseContext.getResources().getString(R.string.file_not_exists));
            return;
        }

        Uri path = UriCompat.getUriForFile(context, file);

        Intent intent = new Intent(Intent.ACTION_VIEW);



        intent.setDataAndType(path, docType);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        FileProviderUtil.grantRWPermission(intent);


        if (isIntentSafe(context, intent)) {
            context.startActivity(intent);
        } else {
            AtworkToast.showToast(context.getResources().getString(R.string.not_valid_intent));
        }

    }

    public static void shareIntent(Context context, String filePath) {
        File file = new File(filePath);
        if (!FileUtil.isExist(filePath)) {
            AtworkToast.showToast(BaseApplicationLike.baseContext.getResources().getString(R.string.file_not_exists));
            return;
        }

        Uri path = UriCompat.getUriForFile(context, file);

        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.putExtra(Intent.EXTRA_STREAM, path);
        intent.setType("*/*");

//        intent.setDataAndType(path, docType);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        FileProviderUtil.grantRWPermission(intent);


        if (isIntentSafe(context, intent)) {
            context.startActivity(intent);
        } else {
            AtworkToast.showToast(context.getResources().getString(R.string.not_valid_intent));
        }

    }

    private static boolean isIntentSafe(final Context context, final Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List activities = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafe = activities.size() > 0;
        return isIntentSafe;
    }

    /**
     * 打电话
     *
     * @param context
     * @param mobile
     */
    public static void callPhoneDirectly(Context context, String mobile) {

        Intent intent = new Intent();
        intent.setAction("android.intent.action.CALL");
        intent.setData(Uri.parse("tel:" + mobile));//mobile为你要拨打的电话号码，模拟器中为模拟器编号也可
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void callPhoneJump(@Nullable Context context, String text) {
        if(null == context) {
            context = AtworkApplicationLike.baseContext;
        }

        Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + text));

        Intent chooser = Intent.createChooser(phoneIntent, "拨打电话");

        if(!(context instanceof Activity)) {
            chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        if (phoneIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(phoneIntent);

        } else {
            AtworkToast.showToast("手机中找不到合适的应用进行此操作:" + "拨打电话");
        }
    }

    /**
     * 发短信
     *
     * @param context
     * @param mobile
     */
    public static void sendSms(Context context, String mobile) {
        sendSms(context, mobile, null);
    }

    public static void sendSms(Context context, String mobile, String content) {
        Uri uri = Uri.parse("smsto:" + mobile);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        if (content != null) {
            intent.putExtra("sms_body", content);
        }
        context.startActivity(intent);
    }


    /**
     * 发邮件
     *
     * @param context
     * @param email
     */
    public static void email(Context context, String email) {
        Intent data = new Intent(Intent.ACTION_SENDTO);
        data.setData(Uri.parse("mailto:" + email));
        if (isIntentSafe(context, data)) {
            context.startActivity(data);
        } else {
            AtworkToast.showToast(context.getResources().getString(R.string.not_valid_intent));
        }
    }

    /**
     * 打开相机拍照
     *
     * @param plugin
     * @param requestCode
     * @return photoPath, 无论成功失败都返回路径
     */
    public static String camera(Context context, CordovaPlugin plugin, int requestCode) {
        String photoPath = AtWorkDirUtils.getInstance().getImageDir(context) + System.currentTimeMillis() + ".jpg";
        Intent intent = getCameraIntent(photoPath);
        plugin.cordova.startActivityForResult(plugin, intent, requestCode);
        return photoPath;
    }

    /**
     * 打开相机拍照
     *
     * @param activity
     * @param requestCode
     * @return
     */
    public static String camera(Activity activity, int requestCode) {

        String photoPath = AtWorkDirUtils.getInstance().getImageDir(activity) + System.currentTimeMillis() + ".jpg";
        Intent intent = getCameraIntent(photoPath);
        activity.startActivityForResult(intent, requestCode);
        return photoPath;
    }

    public static String camera(Fragment fragment, int requestCode) {
        String photoPath = AtWorkDirUtils.getInstance().getImageDir(fragment.getContext()) + System.currentTimeMillis() + ".jpg";
        Intent intent = getCameraIntent(photoPath);
        fragment.startActivityForResult(intent, requestCode);
        return photoPath;
    }

    public static Intent getCameraIntent(String photoPath) {

        File file = new File(photoPath);
        Uri uri = UriCompat.getFileUriCompat(BaseApplicationLike.baseContext, file);
        Log.e("PHOTO:", photoPath);
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("return-data", true);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("PATH:", photoPath);
        FileProviderUtil.grantRWPermission(intent);
        return intent;
    }


    /**
     * 选择 图片
     */
    public static void getPhotoFromLibrary(Fragment context, int requestCode) {
        Intent intent = getPhotoFromLibraryIntent();
        context.startActivityForResult(intent, requestCode);
    }

    public static Intent getPhotoFromLibraryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return intent;
    }

    /**
     * 图片剪裁工具
     *
     * @param photoUri
     * @return
     */
    public static Intent getCropImageIntent(Context context, Uri photoUri) {
        String filePath = AtWorkDirUtils.getInstance().getImageDir(context) + System.currentTimeMillis() + "_avatar.jpg";

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("output", Uri.fromFile(new File(filePath)));
        intent.putExtra("noFaceDetection", true);// 取消人脸识别功能
        intent.putExtra("return-data", true);

        FileProviderUtil.grantRWPermission(intent);

        return intent;
    }

    /**
     * 由于在android4.4以上收到的信息URI格式是：
     * content://com.android.providers.media.documents/document/image:2677
     * 将其改为格式为：
     * content://media/external/images/media/2677
     *
     * @param uri
     * @return
     */ @SuppressLint("NewApi")
    public static Uri changeDocumentsToNormal(Uri uri) {
        String docId = DocumentsContract.getDocumentId(uri);
        String[] split = docId.split(":");
        String type = split[0];
        Uri contentUri = null;
        if ("image".equals(type)) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if ("video".equals(type)) {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else if ("audio".equals(type)) {
            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        String path = contentUri.toString() + "/" + split[1];
        return Uri.parse(path);
    }

    /**
     * 通知第统显示
     *
     * @param context
     */
    public static void setBadge(Context context) {

        if (context == null) {
            context = BaseApplicationLike.baseContext;
        }

        Context finalContext = context;


        int badgeCount = ChatSessionDataWrap.getInstance().getUnreadCount();

        //桌面未读数最多显示99
        doSetBadgeCount(finalContext, badgeCount);


    }

    private static void doSetBadgeCount(Context finalContext, int badgeCount) {
        //桌面未读数最多显示99
        if (99 < badgeCount) {
            badgeCount = 99;
        }
        if (RomUtil.isHuawei()) {
            sendToHuaWei(finalContext, badgeCount);
            return;
        }
        if (!RomUtil.isXiaomi()) {
            sendToOthers(finalContext, badgeCount);
        }
    }


    private static void sendToHuaWei(Context context, int count) {
        try {
            Bundle extra = new Bundle();
            extra.putString("package", AppUtil.getPackageName(context));
            extra.putString("class", SplashActivity.class.getName());
            extra.putInt("badgenumber", count);
            BaseApplicationLike.baseContext.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, extra);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 目前华为桌面并非每个版本都支持该接口,考虑兼容性,可以先判断桌面是否支持 角标,
     * 然后再调用角标接口显示角标。可以依据桌面版本号来判断桌面是否支持角 标,目前桌面支持角标的最低版本号为63029
     *
     * @param context
     * @return
     */
    public static boolean huaWeiBadgeSupport(Context context) {
        boolean isSupportedBade = false;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo("com.huawei.android.launcher",
                    0);
            if (info.versionCode >= 63029) {
                isSupportedBade = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSupportedBade;
    }

    /**
     * miui 系统的桌面 badge 一定要随着notification 的生命周期, 即notifition 消失的时候, 对应的 badge也消息
     */
    public static void setXiaoMiBadge(Notification notification, int count) {
        sendToXiaoMi(notification, count);
    }

    private static void sendToOthers(Context context, int count) {
        try {
            String launcherClassName = getLauncherClassName(context);
            if (launcherClassName == null) {
                return;
            }
            Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            intent.putExtra("badge_count", count);
            intent.putExtra("badge_count_package_name", context.getPackageName());
            intent.putExtra("badge_count_class_name", launcherClassName);
            context.sendBroadcast(intent);
//            changeOPPOBadge(context, count);
            changeVIVOBadge(context, count);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void changeOPPOBadge(Context paramContext, int paramInt)
    {
        Bundle extra = new Bundle();
        extra.putInt("app_badge_count", paramInt);
        Bundle b = null;
        b = paramContext.getContentResolver().call(Uri.parse("content://" + "com.android.badge" + "/" + "badge"),"setAppBadgeCount", null, extra);
        boolean result = false;
        result = b != null;
        return;
    }

    public static void changeVIVOBadge(Context context, int paramInt) {
        Intent localIntent1 = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
        localIntent1.putExtra("packageName", context.getPackageName());
        localIntent1.putExtra("className", SplashActivity.class.getName());
        localIntent1.putExtra("notificationNum", paramInt);
        context.sendBroadcast(localIntent1);

    }



    /**
     * 向小米手机发送未读消息数广播
     *
     * @param count
     */
    public static void sendToXiaoMi(Notification notification, int count) {
        try {

            Field field = notification.getClass().getDeclaredField("extraNotification");

            Object extraNotification = field.get(notification);

            Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);

            method.invoke(extraNotification, count);

        } catch (Exception e) {

            e.printStackTrace();
            count = ChatSessionDataWrap.getInstance().getUnreadCount();
            //桌面未读数最多显示99
            if (99 < count) {
                count = 99;
            }
            sendToXiaoMi(BaseApplicationLike.baseContext, count);

        }
    }

    /**
     * 向小米手机发送未读消息数广播
     *
     * @param count
     */
    private static void sendToXiaoMi(Context context, int count) {
        try {
            Class miuiNotificationClass = Class.forName("android.app.MiuiNotification");
            Object miuiNotification = miuiNotificationClass.newInstance();
            Field field = miuiNotification.getClass().getDeclaredField("messageCount");
            field.setAccessible(true);
            field.set(miuiNotification, String.valueOf(count == 0 ? "" : count));  // 设置信息数-->这种发送必须是miui 6才行
        } catch (Exception e) {
            e.printStackTrace();
            Intent localIntent = new Intent(
                    "android.intent.action.APPLICATION_MESSAGE_UPDATE");
            localIntent.putExtra(
                    "android.intent.extra.update_application_component_name",
                    context.getPackageName() + "/" + getLauncherClassName(context));
            localIntent.putExtra(
                    "android.intent.extra.update_application_message_text", String.valueOf(count == 0 ? "" : count));
            context.sendBroadcast(localIntent);
        }
    }

    public static String getLauncherClassName(Context context) {

        PackageManager pm = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }

    public static boolean isActivityExist(String activityName) {
        boolean result = true;
        Intent intent = new Intent();
        intent.setClassName(AppUtil.getPackageName(BaseApplicationLike.baseContext), activityName);
        if (BaseApplicationLike.baseContext.getPackageManager().resolveActivity(intent, 0) == null) {
            result = false;
        }
        return result;
    }
}
