# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/lingen/SOFT/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#因为采用了三方的jar包 不提示警告
-dontwarn com.ies.link.document.**
-dontwarn com.foreveross.atwork.**
-dontwarn org.bytedeco.javacpp.**
-dontwarn org.htmlcleaner.**
-dontwarn com.handmark.pulltorefresh.library.**
-dontwarn com.tang.addressbook.**
-dontwarn com.tencent.bugly.**
-dontwarn org.chromium.**
-dontwarn com.facebook.**
-dontwarn org.xwalk.core.**
-dontwarn javax.annotation.**
-dontwarn java.lang.invoke**
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontwarn dalvik.**
-dontwarn org.joda.**
-dontwarn android.org.apache.**
-dontwarn android.javax.xml.**
-dontwarn com.sun.naming.**

# For EWS and JavaDNS
-dontwarn java.applet.**
-dontwarn javax.servlet.**
-dontwarn javax.activation.**
-dontwarn org.ietf.jgss.**
-dontwarn org.apache.log.**
-dontwarn org.apache.log4j.**
-dontwarn org.apache.avalon.**
-dontwarn sun.net.spi.nameservice.**

-keep class android.org.apache.** { *; }
-keep class com.sun.** {*;}


#腾讯bugly包不混淆
-keep public class com.tencent.bugly.**{*;}

#不混淆反射类
-keep class com.facebook.stetho.Stetho{*;}

# tinker混淆规则
-dontwarn com.tencent.tinker.**
-keep class com.tencent.tinker.** { *; }
-keep class com.foreveross.atwork.AtworkApplicationLike {
     <init>(android.app.Application, int, boolean, long, long, android.content.Intent);
     void attachBaseContext(android.content.Context);
}



#代码迭代优化次数，取值范围0-7 默认5
-optimizationpasses 5

#混淆时不使用大小写混合的方式，止痒混淆后都是小写字母组合
-dontusemixedcaseclassnames

#混淆时不做预检验，用于加快混淆速度
-dontpreverify


#混淆时记录日志，生成映射文件在 build/outputs/mapping/release/workplus_mapping.txt
-verbose
-printmapping build/outputs/mapping/release/workplus_mapping.txt

#混淆时所采用的算法，参数是Google官方推荐的过滤器算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#如果项目中使用到注解，需要保留注解属性
-keepattributes *Annotation*

#不混淆泛型
-keepattributes Signature

#保留代码行号，这在混淆代码运行中抛出异常信息时，有利于代码定位
-keepattributes SourceFile,LineNumberTable

#保持Android SDK相关API类不被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

#保留 R 类
-keep class **.R$* {
    *;
}

#保持 nateive 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

#保持自定义控件不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#保持Activity中参数是View类型的函数，保证在Layout XML文件中配置的Onclick属性的值能够正常调用到
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

#保持枚举 enum 类不被混淆
-keepclassmembers enum * {
    *;
}

#保持 Parcelable 不被混淆
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

#保持 Serializable 序列化相关方法字段不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#这是由于友盟 SDK中的部分代码使用反射来调用构造函数， 如果被混淆掉， 在运行时会提示"NoSuchMethod"错误
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

#保持自定义控件不混淆
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
    *** get*();
}

#保留RecyclerView类不被混淆
-keep class androidx.recyclerview.widget.RecyclerView {*;}

#保留sqlcipher不混淆
-keep  class net.sqlcipher.** {*;}
-keep  class net.sqlcipher.database.** {*;}

#保留深信服不混淆
-keep class com.sangfor.ssl.** {*;}

#保留小视频不混淆
-keep class org.bytedeco.javacpp.** {*;}

#保留声网不混淆
-keep class io.agora.** {*;}

#保留全时云不混淆
-keep class com.quanshi.tang.network.** {*;}
-keep class com.tang.** {*;}
-keep class org.webrtc.** {*;}

#保留 gif sdk 不混淆
-keep public class pl.droidsonroids.gif.GifIOException{*;}
-keep class pl.droidsonroids.gif.GifInfoHandle{*;}

#保留 QQ, WECHAT 不混淆
-keep class com.tencent.** {*;}

#保留百度定位 sdk 不混淆
-keep class com.baidu.location.** {*;}

#保留 有道翻译 sdk 不混淆

-keep class com.youdao.sdk.** { *;}

#保留 讯飞 sdk 不混淆
-keep class com.iflytek.**{*;}

#保留 recyclerView 组件库不混淆
-keep class com.foreveross.atwork.component.recyclerview.** {
*;
}

-keep public class * extends com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
-keep public class * extends com.foreveross.atwork.component.recyclerview.BaseViewHolder
-keepclassmembers public class * extends com.foreveross.atwork.component.recyclerview.BaseViewHolder {
           <init>(android.view.View);
}


#透视包 apm sdk混淆规则
-keep class com.cloudwise.agent.app.** {*;}
-dontwarn com.cloudwise.agent.app.**
-keepattributes Exceptions, Signature, InnerClasses

#leakcanary 混淆规则
-dontwarn com.squareup.haha.guava.**
-dontwarn com.squareup.haha.perflib.**
-dontwarn com.squareup.haha.trove.**
-dontwarn com.squareup.leakcanary.**
-keep class com.squareup.haha.** { *; }
-keep class com.squareup.leakcanary.** { *; }

# Marshmallow removed Notification.setLatestEventInfo()
-dontwarn android.app.Notification


#得令 混淆规则
-keep class com.dh.bluelock.** {*;}
-keep class cellcom.com.cn.deling.** {*;}

#okhttputils
-dontwarn com.zhy.http.**
-keep class com.zhy.http.**{*;}


#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}


#greenDao
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

#okio
-dontwarn okio.**
-keep class okio.**{*;}

#zebra 混淆规则
-keep class com.zebra.scannercontrol.** {*;}


#支付宝
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
-keep class com.alipay.sdk.app.H5PayCallback {
    <fields>;
    <methods>;
}
-keep class com.alipay.android.phone.mrpc.core.** { *; }
-keep class com.alipay.apmobilesecuritysdk.** { *; }
-keep class com.alipay.mobile.framework.service.annotation.** { *; }
-keep class com.alipay.mobilesecuritysdk.face.** { *; }
-keep class com.alipay.tscenter.biz.rpc.** { *; }
-keep class org.json.alipay.** { *; }
-keep class com.alipay.tscenter.** { *; }
-keep class com.ta.utdid2.** { *;}
-keep class com.ut.device.** { *;}



#glide 混淆规则
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#阿里语音混淆规则
-keep class com.alibaba.idst.util.*{*;}

# MeiZuFingerprint
-keep class com.fingerprints.service.** { *; }

# SmsungFingerprint
-keep class com.samsung.android.sdk.** { *; }


# 网宿混淆规则
-keep class com.mato.** { *; }
-keep class com.wangsu.muf.crashcatch.**{*;}
-keep class com.sce.sdk.monitor.**{*;}
-dontwarn com.mato.**
-keepattributes Exceptions, Signature, InnerClasses



#阿里人脸识别规则
-keep class com.taobao.securityjni.**{*;}
-keep class com.taobao.wireless.security.**{*;}
-keep class com.ut.secbody.**{*;}
-keep class com.taobao.dp.**{*;}
-keep class com.alibaba.wireless.security.**{*;}
-keep class com.alibaba.security.rp.**{*;}
-keep class com.alibaba.sdk.android.**{*;}
-keep class com.alibaba.security.biometrics.**{*;}
-keep class android.taobao.windvane.**{*;}

# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

# -------------------------------------Gson 混淆配置---------------------------------------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}




##--------------------------------------End: Gson 混淆配置  --------------------------------

##----------------------------------------Start: XWalk------------------------------------------------
# Too many hard code reflections between xwalk wrapper and bridge,so
# keep all xwalk classes.
-keepattributes JNINamespace
-keepattributes CalledByNative
-keepattributes *Annotation*
-keepattributes EnclosingMethod

-adaptresourcefilenames    **.properties,**.gif,**.jpg
-adaptresourcefilecontents **.properties,META-INF/MANIFEST.MF

-keep class org.xwalk.**{ *; }
-keep class org.crosswalk.**{ *; }
-keep interface org.xwalk.**{ *; }
-keep class com.example.extension.**{ *; }
-keep class org.crosswalkproject.**{ *; }

# Rules for org.chromium classes:
# Keep annotations used by chromium to keep members referenced by native code
-keep class org.chromium.base.*Native*
-keep class org.chromium.base.annotations.JNINamespace
-keepclasseswithmembers class org.chromium.** {
    @org.chromium.base.AccessedByNative <fields>;
}
-keepclasseswithmembers class org.chromium.** {
    @org.chromium.base.*Native* <methods>;
}

-keep class org.chromium.** {
    native <methods>;
}

# Keep methods used by reflection and native code
-keep class org.chromium.base.UsedBy*
-keep @org.chromium.base.UsedBy* class *
-keepclassmembers class * {
    @org.chromium.base.UsedBy* *;
}

-keep @org.chromium.base.annotations.JNINamespace* class *
-keepclassmembers class * {
    @org.chromium.base.annotations.CalledByNative* *;
}

# Suppress unnecessary warnings.
-dontnote org.chromium.net.AndroidKeyStore
# Objects of this type are passed around by native code, but the class
# is never used directly by native code. Since the class is not loaded, it does
# not need to be preserved as an entry point.
-dontnote org.chromium.net.UrlRequest$ResponseHeadersMap

# Generate by aapt. may only need for testing, just add them here.
-keep class org.chromium.ui.ColorPickerAdvanced { <init>(...); }
-keep class org.chromium.ui.ColorPickerMoreButton { <init>(...); }
-keep class org.chromium.ui.ColorPickerSimple { <init>(...); }


# Enable proguard with Cordova
-keep class org.apache.cordova.** { *; }
-keep public class * extends org.apache.cordova.CordovaPlugin
##----------------------------------------End: XWalk------------------------------------------------



#不混淆的Model类

-keep class com.foreveross.com.sxf_vpn.SxfVPNManager {
    *;
}

-keep class com.foreveross.atwork.infrastructure.model.Employee {
    *;
}

-keep class * extends com.foreveross.atwork.infrastructure.plugin.WorkplusPlugin {
    public static ** getInstance();
}


#------------------------------tbs--------------------------------
#@proguard_debug_start
# ------------------ Keep LineNumbers and properties ---------------- #
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
-renamesourcefileattribute TbsSdkJava
-keepattributes SourceFile,LineNumberTable
#@proguard_debug_end

# --------------------------------------------------------------------------
# Addidional for x5.sdk classes for apps

-keep class com.tencent.smtt.export.external.**{
    *;
}

-keep class com.tencent.tbs.video.interfaces.IUserStateChangedListener {
	*;
}

-keep class com.tencent.smtt.sdk.CacheManager {
	public *;
}

-keep class com.tencent.smtt.sdk.CookieManager {
	public *;
}

-keep class com.tencent.smtt.sdk.WebHistoryItem {
	public *;
}

-keep class com.tencent.smtt.sdk.WebViewDatabase {
	public *;
}

-keep class com.tencent.smtt.sdk.WebBackForwardList {
	public *;
}

-keep public class com.tencent.smtt.sdk.WebView {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.WebView$HitTestResult {
	public static final <fields>;
	public java.lang.String getExtra();
	public int getType();
}

-keep public class com.tencent.smtt.sdk.WebView$WebViewTransport {
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.WebView$PictureListener {
	public <fields>;
	public <methods>;
}


-keepattributes InnerClasses

-keep public enum com.tencent.smtt.sdk.WebSettings$** {
    *;
}

-keep public enum com.tencent.smtt.sdk.QbSdk$** {
    *;
}

-keep public class com.tencent.smtt.sdk.WebSettings {
    public *;
}


-keepattributes Signature
-keep public class com.tencent.smtt.sdk.ValueCallback {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.WebViewClient {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.DownloadListener {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.WebChromeClient {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.WebChromeClient$FileChooserParams {
	public <fields>;
	public <methods>;
}

-keep class com.tencent.smtt.sdk.SystemWebChromeClient{
	public *;
}
# 1. extension interfaces should be apparent
-keep public class com.tencent.smtt.export.external.extension.interfaces.* {
	public protected *;
}

# 2. interfaces should be apparent
-keep public class com.tencent.smtt.export.external.interfaces.* {
	public protected *;
}

-keep public class com.tencent.smtt.sdk.WebViewCallbackClient {
	public protected *;
}

-keep public class com.tencent.smtt.sdk.WebStorage$QuotaUpdater {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.WebIconDatabase {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.WebStorage {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.DownloadListener {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.QbSdk {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.QbSdk$PreInitCallback {
	public <fields>;
	public <methods>;
}
-keep public class com.tencent.smtt.sdk.CookieSyncManager {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.Tbs* {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.utils.LogFileUtils {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.utils.TbsLog {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.utils.TbsLogClient {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.CookieSyncManager {
	public <fields>;
	public <methods>;
}

# Added for game demos
-keep public class com.tencent.smtt.sdk.TBSGamePlayer {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.TBSGamePlayerClient* {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.TBSGamePlayerClientExtension {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.sdk.TBSGamePlayerService* {
	public <fields>;
	public <methods>;
}

-keep public class com.tencent.smtt.utils.Apn {
	public <fields>;
	public <methods>;
}
# end


-keep public class com.tencent.smtt.export.external.extension.proxy.ProxyWebViewClientExtension {
	public <fields>;
	public <methods>;
}

-keep class MTT.ThirdAppInfoNew {
	*;
}

-keep class com.tencent.mtt.MttTraceEvent {
	*;
}

# Game related
-keep public class com.tencent.smtt.gamesdk.* {
	public protected *;
}

-keep public class com.tencent.smtt.sdk.TBSGameBooter {
        public <fields>;
        public <methods>;
}

-keep public class com.tencent.smtt.sdk.TBSGameBaseActivity {
	public protected *;
}

-keep public class com.tencent.smtt.sdk.TBSGameBaseActivityProxy {
	public protected *;
}

-keep public class com.tencent.smtt.gamesdk.internal.TBSGameServiceClient {
	public *;
}
#---------------------------------------------------------------------------

#------------------            bizconf 混淆              -------------------


################提供给app层混淆规则客户端添加################


-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!method/inlining/*
-useuniqueclassmembernames

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference


-dontwarn us.zoom.thirdparty.**
-dontwarn com.google.firebase.**
-dontwarn com.nineoldandroids.**
-dontwarn us.google.protobuf.**

-dontwarn us.zoom.videomeetings.BuildConfig
-dontwarn us.zoom.androidlib.BuildConfig
-dontwarn com.bumptech.glide.**
-dontwarn com.google.gson.**
-dontwarn com.zipow.videobox.ptapp.PTAppProtos.**
-dontwarn com.zipow.videobox.ptapp.PTAppProtos**
-dontwarn org.greenrobot.eventbus.**
-dontwarn com.zipow.videobox.ptapp.MeetingInfoProto**
-dontwarn com.zipow.videobox.ZMFirebaseMessagingService
-dontwarn com.zipow.videobox.util.ConfLocalHelper**
-dontwarn com.zipow.videobox.util.RoundedCornersTransformation**
-dontwarn com.davemorrissey.labs.subscaleview.**
-dontwarn com.zipow.videobox.view.mm.UnshareAlertDialogFragment**
-dontwarn com.zipow.videobox.util.zmurl.avatar.ZMAvatarUrl**
-dontwarn com.android.internal.R$styleable**


-keep class  com.zipow.videobox.share.IDrawingViewListener {*;}
-keep class com.zipow.videobox.share.IColorChangedListener {*;}
-keep class com.zipow.videobox.sdk.SDKShareUnit {*;}
-keep class com.zipow.videobox.kubi.IKubiService {*;}
-keep class us.zoom.androidlib.util.ParamsList{*;}
-keep class com.zipow.videobox.IPTService{*;}
-keep class com.zipow.videobox.util.**{*;}
-keep class com.zipow.videobox.view.video.AbsVideoScene{*;}
-keep class com.zipow.videobox.sdk.SDKVideoUnit{*;}
-keep class us.zoom.androidlib.util.**
-keep class us.zipow.mdm.**{*;}
-keep ,includedescriptorclasses class com.zipow.videobox.sdk.SDKVideoView {*;}

-dontnote us.zoom.androidlib.app.ZMActivity
-dontnote us.zoom.androidlib.util.UIUtil
-dontnote us.zoom.androidlib.util.DnsServersDetector
-dontnote org.apache.**
-dontnote org.json.**
-dontnote com.zipow.videobox.view.mm.**
-dontnote com.zipow.videobox.view.sip.**
-dontnote com.davemorrissey.labs.**

-keep class com.zipow.videobox.view.video.ZPGLTextureView**{
    *;
}
-keep class ZMActivity.** { *;}
-keep class us.zoom.androidlib.util.UIUtil {  *;}


-keep class com.zipow.videobox.util.BuildTarget {
	*;
}

-dontwarn com.facebook.android.**
-dontwarn android.hardware.**
-dontwarn android.media.**
-dontwarn android.widget.**
-dontwarn com.zipow.videobox.**
-dontwarn us.zoom.androidlib.**
-dontwarn us.zoom.thirdparty.**
-dontwarn com.google.firebase.**

-dontwarn com.box.**

-keep class com.box.** {
	*;
}

-dontwarn com.dropbox.**
-keep class com.dropbox.** {
	*;
}

-dontwarn org.apache.**
-keep class org.apache.** {
    *;
}

-keep class org.json.** {
    *;
}

-keepattributes *Annotation*
-keepattributes Signature

-keepnames class com.fasterxml.jackson.** {
	*;
}

-dontwarn com.fasterxml.jackson.databind.**

-dontwarn com.google.android.gms.**
-keep class com.google.android.gms.** {
 	*;
}

-dontwarn com.google.api.client.**
-keep class com.google.api.client.** {
	*;
}

-dontwarn com.microsoft.live.**
-keep class com.microsoft.live.** {
	*;
}

-dontwarn com.baidu.**
-keep class com.baidu.** {
	*;
}

-keep class com.google.api.services.** {
	*;
}

-keep class com.google.android.** {
	*;
}

-keep class sun.misc.Unsafe { *; }

-keep class com.google.gson.** {
    *;
}

-dontwarn androidx.**
-keep class androidx.** {
    *;
}
-keep class us.google.protobuf.** {
    *;
}

-keep class com.bumptech.glide.** {
    *;
}
-dontwarn com.bumptech.glide.**

-keep class us.zoom.androidlib.app.ZMLocalFileListAdapter {
	*;
}

-keep class us.zoom.androidlib.app.ZMFileListEntry{*;}

-keep class us.zoom.androidlib.util.DefaultNameAbbrGenerator {
	*;
}

-keep class com.zipow.videobox.util.ZoomAccountNameValidator {
	*;
}

-keep class com.zipow.videobox.stabilility.NativeCrashHandler {
	*;
}

-dontwarn com.zipow.google_login.GoogleAuthActivity

-keep class us.zoom.net.** {
    *;
}

-keep public class com.zipow.annotate.** {
    *;
}

-keep public class com.zipow.cmmlib.** {
    *;
}

-keep public class com.zipow.nydus.** {
    *;
}

-keep class com.zipow.videobox.util.BuildTarget {
	*;
}

-keep class com.zipow.videobox.util.ZoomAccountNameValidator {
	*;
}

-keep class com.zipow.videobox.stabilility.NativeCrashHandler {
	*;
}

-keep class com.zipow.videobox.ptapp.** {
    *;
}

-keep class com.zipow.videobox.confapp.** {
    *;
}

-keep class com.zipow.videobox.mainboard.** {
    *;
}

-keep class com.zipow.videobox.pdf.** {
    *;
}

-keep class org.webrtc.voiceengine.** {
    *;
}

-keep class us.google.protobuf.** {
    *;
}

-keep class android.support.** {
    *;
}

-dontwarn android.support.**

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}




# 忽略BizConfVideoSDK包名下的所有类
-keep class meeting.confcloud.cn.bizaudiosdk.** {*;}

#################提供给app层混淆规则客户端添加################

#---------------------------------------------------------------------------




#------------------------全时云日历----------------------

-keep class com.QSBox.**{*;}
-keep class com.qs.tvboxremote.**{*;}
-keep class com.gnet.sdk.**{*;}
-keep class com.mtp.**{*;}
-keep class com.gnet.sdk.control.**{*;}
-keep class com.QSBox.RemoteControllerModel.**{*;}
 -dontwarn org.apache.log4j.**
-dontwarn com.google.zxing.**
-dontwarn com.tencent.wxop.**
-dontwarn com.google.gson.**
-dontwarn com.QSShareLibrary.**
-keep class com.chad.library.adapter.** {*;}
-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
-keepclassmembers class **$** extends com.chad.library.adapter.base.BaseViewHolder {
<init>(...);
}
# Retrofit
-dontwarn retrofit2.** -keep class retrofit2.** {*;}
# rxJava
-keep class rx.** {*;}
-dontwarn rx.internal.**

#---------------------------------------------------------------------------

#--------------------HMS-----------------------
-ignorewarnings
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keep class com.hianalytics.android.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}

-keep class com.huawei.gamebox.plugin.gameservice.**{*;}

-keep public class com.huawei.android.hms.agent.** extends android.app.Activity { public *; protected *; }
-keep interface com.huawei.android.hms.agent.common.INoProguard {*;}
-keep class * extends com.huawei.android.hms.agent.common.INoProguard {*;}


# ProGuard configurations for NetworkBench Lens
-keep class com.networkbench.** { *; }
-dontwarn com.networkbench.**
-keepattributes Exceptions, Signature, InnerClasses
# End NetworkBench Lens

#------------------------高德地图 start ----------------------
-dontwarn com.amap.api.col.**

-keep class com.mapbox.mapboxsdk.**{ *; }

#3D 地图 V5.0.0之前：
-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.amap.mapcore.*{*;}
-keep   class com.amap.api.trace.**{*;}

#3D 地图 V5.0.0之后：
-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.**{*;}
-keep   class com.amap.api.trace.**{*;}

#定位
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}

#搜索
-keep   class com.amap.api.services.**{*;}

#2D地图
-keep class com.amap.api.maps2d.**{*;}
-keep class com.amap.api.mapcore2d.**{*;}

#导航
-keep class com.amap.api.navi.**{*;}
-keep class com.autonavi.**{*;}

-keep class com.loc.**{*;}

#------------------------高德地图 end ----------------------

#------------------------推送混淆---------------------
 -keep class com.foreverht.workplus.receiver.XMPushReceiver {*;}
 -keep class com.xiaomi.**{*;}
 -dontwarn com.xiaomi.**
 -keep class * extends com.xiaomi.mipush.sdk.PushMessageReceiver
 -dontwarn com.vivo.push.**
 -keep class com.vivo.push.**{*; }
 -keep class com.vivo.vms.**{*; }
 -keep class com.foreverht.workplus.receiver.VivoPushReceiver{*;}

 -keep public class com.megvii.**{*;}

 -keep class com.ebensz.**{*;}

  -keepclasseswithmembers class com.camerakit.preview.CameraSurfaceView {
      native <methods>;
  }
