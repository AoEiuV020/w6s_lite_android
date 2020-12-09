package com.foreveross.atwork.modules.chat.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foreverht.cache.BitmapCache;
import com.foreverht.cache.MessageCache;
import com.foreverht.cache.UserCache;
import com.foreverht.cache.WatermarkCache;
import com.foreverht.db.service.repository.DiscussionRepository;
import com.foreverht.db.service.repository.DropboxRepository;
import com.foreverht.db.service.repository.MessageRepository;
import com.foreverht.db.service.repository.WatermarkRepository;
import com.foreverht.workplus.module.chat.activity.ShareLocationActivity;
import com.foreverht.workplus.module.chat.activity.ShowLocationActivity;
import com.foreverht.workplus.module.file_share.FileShareAction;
import com.foreverht.workplus.module.file_share.activity.FileShareActivity;
import com.foreverht.workplus.module.sticker.activity.StickerViewActivity;
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreverht.workplus.ui.component.dialogFragment.CommonPopSelectData;
import com.foreverht.workplus.ui.component.dialogFragment.W6sSelectDialogFragment;
import com.foreverht.workplus.ui.component.recyclerview.layoutManager.RecyclerViewNoBugLinearLayoutManager;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.BuildConfig;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener;
import com.foreveross.atwork.api.sdk.BaseNetWorkListener;
import com.foreveross.atwork.api.sdk.chat.TranslateLanguageResponse;
import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.ConversionConfigSettingItem;
import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.ConversionConfigSettingParticipant;
import com.foreveross.atwork.api.sdk.discussion.DiscussionAsyncNetService;
import com.foreveross.atwork.api.sdk.discussion.responseJson.DiscussionSettingsResponse;
import com.foreveross.atwork.api.sdk.dropbox.DropboxAsyncNetService;
import com.foreveross.atwork.api.sdk.message.MessageAsyncNetService;
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil;
import com.foreveross.atwork.api.sdk.net.model.DownloadFileParamsMaker;
import com.foreveross.atwork.api.sdk.net.model.UploadFileParamsMaker;
import com.foreveross.atwork.api.sdk.sticker.responseJson.StickerData;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.api.sdk.upload.MediaCenterSyncNetService;
import com.foreveross.atwork.api.sdk.upload.model.MediaCompressResponseJson;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.api.sdk.voip.responseJson.CreateOrQueryMeetingResponseJson;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.component.SelectDialogFragment;
import com.foreveross.atwork.component.WorkplusBottomPopDialog;
import com.foreveross.atwork.cordova.plugin.ContactPlugin_New;
import com.foreveross.atwork.db.daoService.DiscussionDaoService;
import com.foreveross.atwork.db.daoService.UserDaoService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.manager.OrganizationSettingsManager;
import com.foreveross.atwork.infrastructure.manager.zoom.ZoomManager;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionTop;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.Watermark;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.ServiceApp;
import com.foreveross.atwork.infrastructure.model.chat.MeetingNoticeChatMessage;
import com.foreveross.atwork.infrastructure.model.chat.SystemChatMessage;
import com.foreveross.atwork.infrastructure.model.chat.VoipChatMessage;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.discussion.template.DiscussionFeature;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.employee.DataSchema;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.model.file.ImageItem;
import com.foreveross.atwork.infrastructure.model.file.MediaItem;
import com.foreveross.atwork.infrastructure.model.file.SelectMediaType;
import com.foreveross.atwork.infrastructure.model.file.VideoItem;
import com.foreveross.atwork.infrastructure.model.location.GetLocationInfo;
import com.foreveross.atwork.infrastructure.model.organizationSetting.OrganizationSettings;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.model.setting.BusinessCase;
import com.foreveross.atwork.infrastructure.model.setting.ConfigSetting;
import com.foreveross.atwork.infrastructure.model.user.SelectedContactList;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.model.voip.CallState;
import com.foreveross.atwork.infrastructure.model.voip.CurrentVoipMeeting;
import com.foreveross.atwork.infrastructure.model.voip.MeetingInfo;
import com.foreveross.atwork.infrastructure.model.voip.MeetingStatus;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingGroup;
import com.foreveross.atwork.infrastructure.model.voip.VoipType;
import com.foreveross.atwork.infrastructure.model.zoom.HandleMeetingInfo;
import com.foreveross.atwork.infrastructure.model.zoom.ZoomSdk;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.ReadStatus;
import com.foreveross.atwork.infrastructure.newmessage.ReceiptMessage;
import com.foreveross.atwork.infrastructure.newmessage.UserTypingMessage;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleItem;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.DocChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.HasFileStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.IAtContactMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageContentInfo;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MultipartChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.StickerChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoFileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.reference.ReferenceMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.UndoEventMessage;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.support.AtworkConstants;
import com.foreveross.atwork.infrastructure.support.VoipSdkType;
import com.foreveross.atwork.infrastructure.utils.BitmapUtil;
import com.foreveross.atwork.infrastructure.utils.CloneUtil;
import com.foreveross.atwork.infrastructure.utils.CommonUtil;
import com.foreveross.atwork.infrastructure.utils.ContactHelper;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.MapUtil;
import com.foreveross.atwork.infrastructure.utils.NetworkStatusUtil;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.UrlHandleHelper;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk;
import com.foreveross.atwork.infrastructure.utils.encryption.MD5Utils;
import com.foreveross.atwork.infrastructure.utils.explosion.ExplosionUtils;
import com.foreveross.atwork.infrastructure.utils.file.FileStreamHelper;
import com.foreveross.atwork.infrastructure.utils.language.LanguageSupport;
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil;
import com.foreveross.atwork.inter.ReSendListener;
import com.foreveross.atwork.layout.KeyboardRelativeLayout;
import com.foreveross.atwork.modules.app.manager.AppManager;
import com.foreveross.atwork.modules.chat.adapter.ChatDetailListAdapterV2;
import com.foreveross.atwork.modules.chat.component.InterceptRecyclerView;
import com.foreveross.atwork.modules.discussion.adapter.DiscussionFeaturesInChatDetailAdapter;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.manager.DropboxManager;
import com.foreveross.atwork.manager.EmployeeManager;
import com.foreveross.atwork.manager.MessageNoticeManager;
import com.foreveross.atwork.manager.OnlineManager;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.manager.VoipManager;
import com.foreveross.atwork.manager.listener.BaseQueryListener;
import com.foreveross.atwork.manager.model.CheckTalkAuthResult;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.app.route.UrlRouteHelper;
import com.foreveross.atwork.modules.chat.activity.BurnMessageDetailActivity;
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity;
import com.foreveross.atwork.modules.chat.activity.ChatInfoActivity;
import com.foreveross.atwork.modules.chat.activity.MsgContentDetailActivity;
import com.foreveross.atwork.modules.chat.activity.MultiPartDetailActivity;
import com.foreveross.atwork.modules.chat.component.ChatDetailInputView;
import com.foreveross.atwork.modules.chat.component.ChatInputType;
import com.foreveross.atwork.modules.chat.component.ChatMoreView;
import com.foreveross.atwork.modules.chat.component.ChatVoiceView;
import com.foreveross.atwork.modules.chat.component.FileStatusView;
import com.foreveross.atwork.modules.chat.component.PhoneEmailPopupWindow;
import com.foreveross.atwork.modules.chat.component.PopChatDetailDataHoldingView;
import com.foreveross.atwork.modules.chat.component.PopChatDetailFunctionAreaView;
import com.foreveross.atwork.modules.chat.component.PopLinkTranslatingView;
import com.foreveross.atwork.modules.chat.component.ServiceMenuView;
import com.foreveross.atwork.modules.chat.component.chat.BasicChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.PopupMicroVideoRecordingDialog;
import com.foreveross.atwork.modules.chat.component.recyclerView.ChatPopUpDialogDupportPack;
import com.foreveross.atwork.modules.chat.dao.ChatDaoService;
import com.foreveross.atwork.modules.chat.dao.ReceiptDaoService;
import com.foreveross.atwork.modules.chat.dao.VoipMeetingDaoService;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.data.SendMessageDataWrap;
import com.foreveross.atwork.modules.chat.inter.ChatDetailInputListener;
import com.foreveross.atwork.modules.chat.inter.ChatItemClickListener;
import com.foreveross.atwork.modules.chat.inter.ChatItemLongClickListener;
import com.foreveross.atwork.modules.chat.inter.ChatModeListener;
import com.foreveross.atwork.modules.chat.model.EntrySessionRequest;
import com.foreveross.atwork.modules.chat.model.HistoryDivider;
import com.foreveross.atwork.modules.chat.model.PlayAudioInChatDetailViewParams;
import com.foreveross.atwork.modules.chat.model.TranslateLanguageType;
import com.foreveross.atwork.modules.chat.service.ChatPermissionService;
import com.foreveross.atwork.modules.chat.service.ChatSendOnMainViewService;
import com.foreveross.atwork.modules.chat.service.ChatService;
import com.foreveross.atwork.modules.chat.service.upload.FileMediaUploadListener;
import com.foreveross.atwork.modules.chat.service.upload.ImageMediaUploadListener;
import com.foreveross.atwork.modules.chat.service.upload.MicroVideoUploadListener;
import com.foreveross.atwork.modules.chat.service.upload.MultipartUploadListener;
import com.foreveross.atwork.modules.chat.service.upload.VoiceUploadListener;
import com.foreveross.atwork.modules.chat.util.AudioFileHelper;
import com.foreveross.atwork.modules.chat.util.AudioRecord;
import com.foreveross.atwork.modules.chat.util.BurnModeHelper;
import com.foreveross.atwork.modules.chat.util.ChatDetailExposeBroadcastSender;
import com.foreveross.atwork.modules.chat.util.DiscussionHelper;
import com.foreveross.atwork.modules.chat.util.EmergencyMessageConfirmHelper;
import com.foreveross.atwork.modules.chat.util.FileDataUtil;
import com.foreveross.atwork.modules.chat.util.HideMessageHelper;
import com.foreveross.atwork.modules.chat.util.KeyboardHeightHelper;
import com.foreveross.atwork.modules.chat.util.LinkTranslatingHelper;
import com.foreveross.atwork.modules.chat.util.MessageChatViewBuild;
import com.foreveross.atwork.modules.chat.util.MessageItemPopUpHelp;
import com.foreveross.atwork.modules.chat.util.MultipartMsgHelper;
import com.foreveross.atwork.modules.chat.util.SessionRefreshHelper;
import com.foreveross.atwork.modules.chat.util.TextMsgHelper;
import com.foreveross.atwork.modules.chat.util.TextTranslateHelper;
import com.foreveross.atwork.modules.chat.util.UndoMessageHelper;
import com.foreveross.atwork.modules.configSettings.manager.ConfigSettingsManager;
import com.foreveross.atwork.modules.contact.activity.PersonalInfoActivity;
import com.foreveross.atwork.modules.contact.data.StarUserListDataWrap;
import com.foreveross.atwork.modules.discussion.manager.extension.DiscussionManagerKt;
import com.foreveross.atwork.modules.dropbox.activity.DropboxBaseActivity;
import com.foreveross.atwork.modules.dropbox.activity.SaveToDropboxActivity;
import com.foreveross.atwork.modules.file.activity.FileSelectActivity;
import com.foreveross.atwork.modules.file.service.FileTransferService;
import com.foreveross.atwork.modules.discussion.activity.DiscussionMemberSelectActivity;
import com.foreveross.atwork.modules.discussion.activity.DiscussionReadUnreadActivity;
import com.foreveross.atwork.modules.group.activity.TransferMessageActivity;
import com.foreveross.atwork.modules.group.activity.UserSelectActivity;
import com.foreveross.atwork.modules.discussion.fragment.DiscussionReadUnreadFragment;
import com.foreveross.atwork.modules.discussion.model.DiscussionMemberSelectControlAction;
import com.foreveross.atwork.modules.group.module.TransferMessageControlAction;
import com.foreveross.atwork.modules.group.module.UserSelectControlAction;
import com.foreveross.atwork.modules.group.service.SelectToHandleActionService;
import com.foreveross.atwork.modules.image.activity.ImageSwitchInChatActivity;
import com.foreveross.atwork.modules.image.activity.MediaPreviewActivity;
import com.foreveross.atwork.modules.image.activity.MediaSelectActivity;
import com.foreveross.atwork.modules.image.fragment.ImageSwitchFragment;
import com.foreveross.atwork.modules.main.activity.MainActivity;
import com.foreveross.atwork.modules.meeting.util.MeetingHelper;
import com.foreveross.atwork.modules.newsSummary.util.NewsSummaryHelper;
import com.foreveross.atwork.modules.test.manager.TestManager;
import com.foreveross.atwork.modules.voip.activity.VoipSelectModeActivity;
import com.foreveross.atwork.modules.voip.manager.ZoomVoipManager;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.AvatarHelper;
import com.foreveross.atwork.utils.ChatMessageHelper;
import com.foreveross.atwork.utils.ContactQueryHelper;
import com.foreveross.atwork.utils.EditTextUtil;
import com.foreveross.atwork.utils.EmployeeHelper;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.atwork.utils.FileHelper;
import com.foreveross.atwork.utils.GifChatHelper;
import com.foreveross.atwork.utils.ImageChatHelper;
import com.foreveross.atwork.utils.IntentUtil;
import com.foreveross.atwork.utils.OfficeHelper;
import com.foreveross.atwork.utils.StringConfigHelper;
import com.foreveross.atwork.utils.TimeViewUtil;
import com.foreveross.atwork.utils.ToastHelper;
import com.foreveross.atwork.utils.watermark.WaterMarkHelper;
import com.foreveross.theme.manager.SkinHelper;
import com.foreveross.theme.manager.SkinMaster;
import com.foreveross.translate.OnResultListener;
import com.foreveross.translate.youdao.YoudaoTranslate;
import com.foreveross.watermark.core.WaterMarkUtil;
import com.foreveross.xunfei.JsonParser;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.w6s.emoji.StickerManager;
import com.w6s.module.MessageTags;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.kvh.media.amr.AmrFileDecoder;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import pl.droidsonroids.gif.GifDrawable;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_FLING;
import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
import static com.foreveross.atwork.modules.chat.util.VoiceTranslateHelper.setTranslating;
import static com.foreveross.atwork.modules.file.activity.OfficeViewActivity.VIEW_FROM_CHAT_LIST;
import static com.foreveross.atwork.modules.file.activity.OfficeViewActivity.VIEW_FROM_FILE_DETAIL;

/**
 * Created by lingen on 15/3/24.
 * Description:
 */
public class ChatDetailFragment extends OnSensorChangedFragment implements ChatModeListener,
        ReSendListener, PopupMicroVideoRecordingDialog.OnMicroVideoTakingListener {

    private static final String TAG = ChatDetailFragment.class.getSimpleName();

    private static final long TYPING_STATUS_SHOWING_THRESHOLD = 5000;

    private ExecutorService mMsgThreadService = Executors.newFixedThreadPool(15);

    public static final String MESSAGE_LEFT_QUOTE_FLAG = "「 ";
    public static final String MESSAGE_RIGHT_QUOTE_FLAG = " 」\n";
    public static final String MESSAGE_QUOTE_DIVER = "- - - - - - - - - - - - - - -";

    //清空消息列表
    public static final String ACTION_CLEAR_MESSAGE_LIST = "ACTION_CLEAR_MESSAGE_LIST";

    //收到消息回执通知
    public static final String REFRESH_MESSAGE_LIST = "REFRESH_MESSAGE_LIST";

    //收到消息
    public static final String CHAT_MESSAGE_RECEIVED = "CHAT_MESSAGE_RECEIVED";

    //收到正在输入的通知
    public static final String CHAT_MESSAGE_USER_TYPING = "CHAT_MESSAGE_USER_TYPING";

    public static final String CHAT_MESSAGE_RECEIVED_SELF_UPDATE = "CHAT_MESSAGE_RECEIVED_SELF_UPDATE";

    public static final String UNDO_MESSAGE_SEND_SUCCESSFULLY = "UNDO_MESSAGE_SEND_SUCCESSFULLY";

    public static final String REMOVE_MESSAGE_SUCCESSFULLY = "REMOVE_MESSAGE_SUCCESSFULLY";

    public static final String ACTION_REFRESH_SEND_BTN_STATUS = "ACTION_REFRESH_SEND_BTN_STATUS";

    public static final String DELETE_MESSAGES = "DELETE_MESSAGES";

    //批量收到消息
    public static final String BATCH_MESSAGES_RECEIVED = "BATCH_MESSAGES_RECEIVED";
    //漫游回来消息
    public static final String ROMAING_MESSAGE_RECEIVED = "ROMAING_MESSAGE_RECEIVED";

    //当前聊天的 user 改变
    public static final String USER_CHANGED = "USER_CHANGED";

    //session改变
    public static final String SESSION_CHANGED = "SESSION_CHANGED";

    //关掉当前聊天界面
    public static final String ACTION_FINISH = "ACTION_FINISH";

    //清除@人数据
    public static final String ACTION_CLEAR_AT_DATA = "ACTION_CLEAR_AT_DATA";

    //禁止关闭界面时检查session
    public static final String ACTION_DO_NOT_CHECK_SESSION = "ACTION_DO_NOT_CHECK_SESSION";

    public static final String ACTION_EMERGENCY_MESSAGE_CONFIRMED = "ACTION_EMERGENCY_MESSAGE_CONFIRMED";

    public static final String DATA_USER = "DATA_USER";

    public static final String DATA_SESSION = "DATA_SESSION";

    public static final String DATA_MSG_ID_LIST = "DATA_MSG_ID_LIST";

    public static final String DATA_MSG_ID = "DATA_MSG_ID";

    public static final String DATA_TIP = "DATA_TIP";

    //是否直接返回上一层
    public static final String RETURN_BACK = "return_back";

    public static final String APP_BUNDLE_ID = "app_bundle_id";

    public static final String STOP_PLAYING_MSG_ID = "stop_playing_voice_content";

    public static final String PLAYING_NEXT_VOICE = "plaing_next_voice";

    public static final String NOTIFY_MSG = "NotifyMsg";

    public static final String ADD_SENDING_LISTENER = "add_sending_listener";

    //目标会话
    public static final String FORWARD_SESSION = "FORWARD_SESSION";


    public static final int INIT_LOAD_MESSAGES_COUNT = 20;

    public static final int MORE_LOAD_MESSAGES_COUNT = 20;
    //点击拍照事件
    private static final int GET_CAMERA = 1;
    //点击图片事件
    private static final int GET_PHOTO = 2;
    //点击从文件中选取事件
    private static final int GET_FILE = 3;
    //@人
    private static final int GET_DISCUSSION_AT = 4;
    //获取待发送名片的列表
    private static final int GET_CARD_USERLIST = 5;
    //群聊选人
    private static final int GET_VOIP_DISCUSSION_MEMBER_SELECT = 6;
    //点击拍照并编辑返回
    private static final int GET_CAMERA_EDIT = 7;

    private static final int GET_DROPBOX_TO_SEND = 8;

    //预约会畅会议
    private static final int GET_ZOOM_MEETING_RESERVATION = 9;

    //即时会畅会议
    private static final int GET_ZOOM_MEETING_INSTANT = 10;

    private static final int SHARE_LOCATION_REQ_CODE = 11;

    public static final int GET_DOC_TO_SEND = 12;

    public static final int GET_FEATURE_ROUTE = 13;


    private static final int NEW_MSG_FROM_OFFLINE = 0;
    private static final int NEW_MSG_FROM_OTHER_USER = 1;
    private static final int NEW_MSG_FROM_OWN_SEND = 2;
    private static final int NEW_MSG_FROM_ROAMING = 3;

    //传送批量详细的intent key
    public static final String INTENT_BATCH_MESSAGES = "INTENT_BATCH_MESSAGES";
    public static String DATA_BUNDLE = "DATA_BUNDLE";
    public static String DATA_NEW_MESSAGE = "DATA_NEW_MESSAGE";
    public static String DATA_UNDO_MESSAGE = "DATA_UNDO_MESSAGE";
    private final String IMAGE_TYPE = "image/*";

    private final String mTagMeetingReservation = AtworkApplicationLike.getResourceString(R.string.meeting_reservation);
    private final String mTagMeetingInstant = AtworkApplicationLike.getResourceString(R.string.meeting_instant);

    private final Object mRefreshListLock = new Object();
    private final Object mRefreshUILock = new Object();


    //消息列表
    private SmartRefreshLayout mSmartRefreshLayout;
    private InterceptRecyclerView mChatListView;
    private ChatDetailListAdapterV2 mChatDetailArrayListAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private KeyboardRelativeLayout mRlRoot;
    //输入功能区
    private ChatDetailInputView mChatDetailInputView;
    private ServiceMenuView mServiceMenuView;
    //右边功能
    private LinearLayout mLlRightArea;
    private ImageView mIvChatInfo;
    private ImageView mIvUserPhone;
    //下方功能(发送图片，文件，拍照等)
    private FrameLayout mFlFunctionArea;
    private ChatMoreView mChatMoreView;
    private ChatVoiceView mChatVoiceView;
    private View mEmojView;
    public EmojiconsFragment emojiconsFragment = new EmojiconsFragment();
    //监听键盘事件
    private KeyboardRelativeLayout mKeyboardRelativeLayout;

    private boolean mKeyboardShowing = false;
    //返回按扭
    private ImageView mIvBack;
    private AudioRecord audioRecord;

    private Session mSession;
    private Vector<ChatPostMessage> mMessageList = new Vector<>();
    private Vector<ChatPostMessage> mMessageImageTypeListLoadSilently = null;
    //只用来临时显示的消息集合
    private Vector<ChatPostMessage> mMessageJustShowList = new Vector<>();
    //附带时间的当前信息的集合
    private ArrayList<ChatPostMessage> mWithTimeMessageList = new ArrayList<>();

    private List<ChatPostMessage> mEmergencyMessageUnconfirmedList = new ArrayList<>();

    //下拉刷新VIEW
    private View mPullDownRefreshView;

    private long mRealFirstTimestamp = -1;

    //本地是否有更多历史消息
    private boolean mLocalHasMore = true;
    //远程服务器是否有更多历史消息
    private boolean mRemoteHasMore = true;

    private View mSelectModelView;
    private View mInputModelView;
    private View mServiceMenuModeView;
    //聊天模式，普通以及删除转发模式
    private ChatModel mChatModel = ChatModel.COMMON;
    private ImageView mFavChatView;
    private ImageView mForwardChatView;
    private ImageView mDeleteChatView;

    //未读消息提醒
    private TextView mViewUnreadMsgTip;
    private TextView mViewUnreadDiscussionNotifyTip;
    private TextView mTvViewNewMessageTip;
    private ImageView mIvJumpAnchor;
    private ChatPostMessage mFirstUnreadChatPostMessage;
    private ChatPostMessage mUnderHistoryChatMessage; //历史分割线下的第一条消息
    private ProgressDialogHelper mProgressDialogHelper;
    private View mVTitleBar;
    private TextView mTvSelectTitle;
    private RelativeLayout mRlCommonTitle;
    private TextView mTitleView;
    private ImageView mIvTitleArrow;

    private TextView mTvDiscussionSize;
    private TextView mTvTipViewNotInitialize;
    private TextView mTvOrgPosition;
    private ImageView mIvTitleBarChatDetailTranslation;
    private TextView mTvPersonalInfo;

    private View mJoinAudioMeeting;

    private View mVMaskLayer;

    private View mVTopLineChatInput;
    private View mVBottomLineChatInput;

    private PopLinkTranslatingView mPopLinkTranslatingView;
    private PopChatDetailFunctionAreaView mPopChatDetailFunctionAreaView;
    private PopChatDetailDataHoldingView mPopChatDetailDataHoldingView;

    private RecyclerView mRvDiscussionEntries;
    private DiscussionFeaturesInChatDetailAdapter mDiscussionFeaturesInChatDetailAdapter;
    private List<DiscussionFeature> mDiscussionFeatureList = new ArrayList<>();

    private TextView mTvTimePrintFloat;


    private int mUnreadTotalCount;
    private int mReadCount;  //用户看到消息的数量(例如初始化20), 此时mReadCount 20条, 下拉20条, 此时 mReadCount 为40条

    private Set<String> mUnreadMapInBeginUsingInFindUnRead = new HashSet<>();  //刚进来聊天界面时的未读消息 id 列表

    private List<ChatPostMessage> mDiscussionNotificationList = new ArrayList<>();  //群通知(指界面上显示的"x 条群通知")列表, 只要出现过,即加入该队列, 防止因各种场景重复提醒
    private List<ChatPostMessage> mUnreadDiscussionNotificationList = new ArrayList<>(); //群通知(指界面上显示的"x 条群通知")未读列表

    private List<String> mUnreadListInBegin = new ArrayList<>();
    private List<UserHandleInfo> mAtContacts = new ArrayList<>();

    private int mFirstVisibleItem = 0;
    private User mLoginUser;
    private Discussion mDiscussion;
    private User mUser;
    private List<Employee> mEmployeeList;
    private App mApp;
    private String mOrgId;

    private List<ServiceApp.ServiceMenu> mServiceMenus;

    private String mToFixedMessageId;


    //底部键盘等切换动画
    private TranslateAnimation mShowAnimation;
    private TranslateAnimation mHideAnimation;

    //记录上一次点击屏幕(整个 listview)的 Y 坐标
    private float mLastClickDownY;

    //记录是否完成了拉取聊天数据的动作
    private boolean hasInitReadChatData = false;

    private boolean mAtAll = false;

    private String mPhotoPath;

    private boolean mNeedHandingKeyboard = false;

    private boolean mIsAtMode = false;


    //设置底层和popupWindow之间的半透明层
    View transparentView;
    public static final String TRANSPARENT_POP = "TRANSPARENT_POP";
    public static final String TRANSPARENT_POP_DATA = "TRANSPARENT_POP_DATA";
    public static final String TRANSPARENT_POP_TYPE = "TRANSPARENT_POP_TYPE";
    private PhoneEmailPopupWindow mPopupWindow;
    //在听筒模式下关闭感应监听器
//    private boolean mIsEarPhone = false;

    private int mKeyboardInputHeight = -1;

    private VoipMeetingGroup mVoipConference;

    private PopupMicroVideoRecordingDialog mRecordDialog;

    private boolean mHasCheckSession = false;

    private boolean mDoNotCheckSession = false;

    private boolean mNeedSessionLegalCheck = true;

    //是否做了清除数据处理, 并且成功清除了
    private boolean mMsgClearedSuccessfullyAction = false;

    private static Bitmap sScreenshot = null;

    private boolean mCallDiscussionBasicInfoRemote = false;

    private boolean mIsFirstStartLifeCircle = true;

    //翻译的语种
    private String mStrTranslationShortName = "";
    //长按时的实时语种
    private String mCurrentLanguage = "";
    //是否重新获取翻译的语种
    private Boolean mIsRegainLanguageShortName = false;

    private String mVoiceTranslateTarget = "zh_ch";//获取当前的翻译语言环境

    // 语音听写对象
    private SpeechRecognizer mIat;


    private boolean mIsLoadUserRefreshFromRemoteResultSuccess = false;
    private boolean mIsLoadEmpListRemoteRequestSuccess = false;
    private boolean mPeerTyped = false;

    private boolean mSelectOriginalMode;

    private int mViewBottomPopLongLive = -1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_detail, container, false);
        mKeyboardRelativeLayout = (KeyboardRelativeLayout) view;

        testBug();
        return view;
    }

    private void testBug() {
        String str = "hello world";
        Log.e("bugly", "bug string length is " + str.length());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData(savedInstanceState);
        registerListener(view);
        registerBroadcast();

    }

    @Override
    public void onStart() {
        super.onStart();
        this.mLoginUser = AtworkApplicationLike.getLoginUserSync();

        MessageNoticeManager.getInstance().clear();
        if (mSession == null) {
            String accountName = getArguments().getString(ChatDetailActivity.IDENTIFIER);
            mSession = ChatSessionDataWrap.getInstance().getSession(accountName, null);
        }

        if (mSession != null) {
            dealWithContactViewHeader();
            dealWithContactInitializeStatusViewTip();
            mSession.visible = true;
            sessionUnreadClear();
        }

        //设置UI显示为true
        setUserVisibleHint(true);


        if (mIsAtMode) {
            mChatDetailInputView.postDelayed(this::showInput, 500);
        }
        //revert back
        mIsAtMode = false;
        mHasCheckSession = false;

        if (!mIsFirstStartLifeCircle) {
            checkSendReadMessageReceipt();
        }

        mIsFirstStartLifeCircle = false;
        //querySessionSettingTranslationLanguage();
    }

    @Override
    public void onResume() {
        super.onResume();
        initTalkGuy();
        if (mIsRegainLanguageShortName) {
            querySessionSettingTranslationLanguage();
            mChatDetailArrayListAdapter.setTranslateLanguage(mStrTranslationShortName);
            refreshListAdapter();
        } else {
            mIsRegainLanguageShortName = true;
        }

    }

    /**
     * onPause 不要做耗时操作
     */
    @Override
    public void onPause() {
        if (mRecordDialog != null && mRecordDialog.isVisible()) {
            mRecordDialog.dismiss();
        }

        updateSessionTypeStatus(true);

        super.onPause();

        if (mSession != null) {
            mSession.visible = false;
            mSession.clearUnread();
            IntentUtil.setBadge(BaseApplicationLike.baseContext);
        }

        AudioRecord.stopPlaying();

        AtworkUtil.hideInput(getActivity(), mChatDetailInputView.getEmojiIconEditText());
    }

    @Override
    public void onStop() {
        super.onStop();
        AudioRecord.stopPlaying();
        checkSessionUpdate();

        //todo 优化避免多余发送 mUnreadListInBegin
        ChatService.clearChatMsgReceipts(BaseApplicationLike.baseContext, mSession, mUnreadListInBegin);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        checkSessionUpdate();

        unregisterBroadcast();

        super.onDestroy();
        AudioRecord.stopPlaying();
        clearBroadcast();
        clearImage();
        releaseScreenshot();

        if (null != mIat) {
            mIat.destroy();
            mIat = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    protected void findViews(View view) {
        mRlRoot = getView().findViewById(R.id.v_root);
        mVTopLineChatInput = getView().findViewById(R.id.v_top_line_chat_input);
        mChatDetailInputView = getView().findViewById(R.id.chat_detail_input_include);
        mVBottomLineChatInput = getView().findViewById(R.id.v_bottom_line_chat_input);

        mSmartRefreshLayout = getView().findViewById(R.id.chat_detail_list_Line);
        mChatListView = getView().findViewById(R.id.chat_detail_list_view);

        mChatDetailInputView = getView().findViewById(R.id.chat_detail_input_include);
        mServiceMenuView = getView().findViewById(R.id.chat_detail_input_service_menu);
        mLlRightArea = getView().findViewById(R.id.ll_right_area);
        mIvChatInfo = getView().findViewById(R.id.title_bar_main_more_btn);
        mIvUserPhone = getView().findViewById(R.id.iv_user_phone);
        mFlFunctionArea = getView().findViewById(R.id.fl_function_area);
        mChatMoreView = getView().findViewById(R.id.chat_detail_chat_more_view);
        mChatVoiceView = getView().findViewById(R.id.chat_detail_chat_voice_view);
        mEmojView = getView().findViewById(R.id.chat_detail_chat_emojicon);
        mEmojView.setVisibility(GONE);


        if (!AtworkConfig.STICKER_CONFIG.isEnable()) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(EmojiconsFragment.SHOW_STICKER, false);
            emojiconsFragment.setArguments(bundle);
        }
        getChildFragmentManager().beginTransaction().add(R.id.chat_detail_chat_emojicon, emojiconsFragment).commit();

        mIvBack = getView().findViewById(R.id.title_bar_chat_detail_back);
        mPullDownRefreshView = getView().findViewById(R.id.chat_detail_pull_down_refresh);

        mSelectModelView = getView().findViewById(R.id.chat_detail_select_mode);
        mInputModelView = getView().findViewById(R.id.chat_detail_input_area);
        mServiceMenuModeView = getView().findViewById(R.id.chat_detail_input_service_menu_mode);

        mFavChatView = getView().findViewById(R.id.chat_detail_fav_chat);

        ViewUtil.setVisible(mFavChatView, false);

        mForwardChatView = getView().findViewById(R.id.chat_detail_forward_chat);
        mDeleteChatView = getView().findViewById(R.id.chat_detail_delete_chat);
        mViewUnreadMsgTip = getView().findViewById(R.id.new_message_tip);
        mViewUnreadDiscussionNotifyTip = getView().findViewById(R.id.tv_new_at_all_tip);
        mTvViewNewMessageTip = getView().findViewById(R.id.tv_rece_new_message_tip);
        mIvJumpAnchor = getView().findViewById(R.id.iv_jump_anchor);
        mVTitleBar = getView().findViewById(R.id.chat_detail_include_title_bar);
        mRlCommonTitle = getView().findViewById(R.id.rl_common_title);
        mTvSelectTitle = getView().findViewById(R.id.tv_select_title);
        mTitleView = getView().findViewById(R.id.title_bar_chat_detail_name);
        mIvTitleArrow = getView().findViewById(R.id.iv_personal_title_arrow);
        mTvDiscussionSize = getView().findViewById(R.id.title_bar_chat_detail_discussion_size);
        mTvTipViewNotInitialize = getView().findViewById(R.id.tv_contact_status_tip);
        mTvOrgPosition = getView().findViewById(R.id.tv_contact_org_position);
        mJoinAudioMeeting = getView().findViewById(R.id.voip_meeting_tip);
        mIvTitleBarChatDetailTranslation = getView().findViewById(R.id.iv_title_bar_chat_detail_translation);
        mTvPersonalInfo = getView().findViewById(R.id.tv_personal_info);

        mVMaskLayer = getView().findViewById(R.id.v_mask_layer);
        mPopLinkTranslatingView = getView().findViewById(R.id.view_pop_translating);
        mPopChatDetailFunctionAreaView = getView().findViewById(R.id.v_chat_detail_pop_function_area);
        mPopChatDetailDataHoldingView = getView().findViewById(R.id.v_pop_chat_detail_text_message_quote_reference);

        mRvDiscussionEntries = getView().findViewById(R.id.rv_discussion_entries);

        mTvTimePrintFloat = getView().findViewById(R.id.tv_time_print_float);
//        mTvTimePrintFloat.setAlpha(0.3f);

        SkinMaster.getInstance().changeBackground(mViewUnreadMsgTip.getBackground(), SkinHelper.parseColorFromTag("c14"));

    }

    public int getRootHeight() {
        return mRlRoot.getScreenHeight();
    }


    private BroadcastReceiver mSideBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (USER_CHANGED.equals(action)) {

                User user = intent.getParcelableExtra(DATA_USER);

                if (null != user && mSession.identifier.equals(user.mUserId)) {
                    setTitle(user);
                    mSession.name = user.getShowName();

                    mUser = user;

                }
            } else if (SESSION_CHANGED.equals(action)) {
                Session session = intent.getParcelableExtra(DATA_SESSION);
                if (mSession.identifier.equals(session.identifier)) {
                    mSession.top = session.top;
                }

            } else if (ACTION_FINISH.equals(action)) {
                String finishSessionId = intent.getStringExtra(ChatDetailActivity.IDENTIFIER);
                if (null != mSession && mSession.identifier.equals(finishSessionId)) {
                    finish(false);
                }

            } else if (ACTION_DO_NOT_CHECK_SESSION.equals(action)) {
                String sessionId = intent.getStringExtra(ChatDetailActivity.IDENTIFIER);
                if (null != mSession && mSession.identifier.equals(sessionId)) {
                    mDoNotCheckSession = true;

                }

            } else if (ACTION_CLEAR_AT_DATA.equals(action)) {
                //清除上一次AT信息
                mAtContacts.clear();

            } else if (ACTION_EMERGENCY_MESSAGE_CONFIRMED.equals(action)) {
                String messageId = intent.getStringExtra(DATA_MSG_ID);
                confirmEmergencyMessage(messageId);

            } else if (SelectToHandleActionService.ACTION_SEND_SUCCESSFULLY.equals(action)) {

                if (ChatModel.SELECT.equals(mChatModel)) {
                    changeModel();
                    return;
                }

            }


        }
    };


    private BroadcastReceiver mSessionInvalidBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            String id = intent.getStringExtra(DiscussionHelper.SESSION_INVALID_ID);
            int type = intent.getIntExtra(DiscussionHelper.SESSION_INVALID_TYPE, -1);
            if (TextUtils.isEmpty(id) || !id.equalsIgnoreCase(mSession.identifier)) {
                return;
            }

            if (DiscussionHelper.SESSION_INVALID_DISCUSSION_KICKED == type
                    || DiscussionHelper.SESSION_INVALID_DISCUSSION_DISMISSED == type) {
                ChatSessionDataWrap.getInstance().removeSessionSafely(id);
                DiscussionDaoService.getInstance().removeDiscussion(id);
            }

            showKickDialog(ChatListFragment.getSessionInvalidContent(type));
            hideAll();
        }
    };


    private BroadcastReceiver mNewMessageBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (CHAT_MESSAGE_RECEIVED.equals(intent.getAction())) {
                final ChatPostMessage message = (ChatPostMessage) intent.getSerializableExtra(DATA_NEW_MESSAGE);
                if (!ChatMessageHelper.getChatUser(message).mUserId.equalsIgnoreCase(mSession.identifier)) {
                    return;
                }

                if (message.mTargetScope == ChatPostMessage.TARGET_SCOPE_NEWS_SUMMARY) {
                    return;
                }

                //update title name
                if (message instanceof SystemChatMessage) {
                    String content = ((SystemChatMessage) message).content;
                    if (content.contains(getStrings(R.string.modify_group_name, "", ""))) {
                        setTitle(mSession);
                        mDiscussion.mName = mSession.name;
                    }
                }

                receiveOneMsg(message);


                return;
            }


            if (CHAT_MESSAGE_RECEIVED_SELF_UPDATE.equals(intent.getAction())) {
                Bundle bundle = intent.getBundleExtra(DATA_BUNDLE);
                final ChatPostMessage message = (ChatPostMessage) bundle.getSerializable(DATA_NEW_MESSAGE);
                int messageIndex = mMessageList.indexOf(message);
                if (-1 != messageIndex) {
                    mMessageList.remove(messageIndex);

                    mMessageList.add(message);

                    MessageCache.getInstance().updateMessageList(mSession.identifier, mMessageList);

                    refreshWithTimeMsgListTotally();
                    mChatDetailArrayListAdapter.setMessages(mWithTimeMessageList);
                    refreshListAdapter();

                    checkLastMessageOnSession();
                    //滚到最后

                    mChatListView.scrollToPosition(mChatDetailArrayListAdapter.getItemCount() - 1);
                }


                return;
            }


            if (CHAT_MESSAGE_USER_TYPING.equals(intent.getAction())) {
                final UserTypingMessage message = (UserTypingMessage) intent.getSerializableExtra(DATA_NEW_MESSAGE);

                if (null == message) {
                    return;
                }

                if (isUserChat() && !isCurrentFileTransfer() && mSession.identifier.equals(message.getSessionChatId())) {

                    showUserTyping();
                }

            }

        }
    };


    private BroadcastReceiver mOfflineMessageBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ROMAING_MESSAGE_RECEIVED.equals(intent.getAction())) {
                final List<ChatPostMessage> messages = (List<ChatPostMessage>) intent.getSerializableExtra(DATA_NEW_MESSAGE);
                batchReceivedMsg(NEW_MSG_FROM_ROAMING, messages);

            } else if (BATCH_MESSAGES_RECEIVED.equals(intent.getAction())) {
                final List<ChatPostMessage> messages = (List<ChatPostMessage>) intent.getSerializableExtra(INTENT_BATCH_MESSAGES);
                batchReceivedMsg(NEW_MSG_FROM_OFFLINE, messages);
            }

        }
    };

    /**
     * 播放下一条语音
     */
    private BroadcastReceiver mPlayingNextBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isPlayNothing;
            if (PLAYING_NEXT_VOICE.equals(intent.getAction())) {
                isPlayNothing = true;
                VoiceChatMessage voiceChatMessage = (VoiceChatMessage) intent.getSerializableExtra(STOP_PLAYING_MSG_ID);
                int position = mWithTimeMessageList.indexOf(voiceChatMessage);
                if (position == -1) {
                    //如果没有要播放的语音消息，则注销距离感应器监听器
                    playAudioEnd(context);
                    return;
                }
                if (mWithTimeMessageList.size() > position + 1) {
                    ChatPostMessage chatPostMessage = mWithTimeMessageList.get(position + 1);
                    if (chatPostMessage instanceof VoiceChatMessage) {
                        final VoiceChatMessage nextVoiceChatMessage = (VoiceChatMessage) chatPostMessage;
                        if (!nextVoiceChatMessage.isUndo() && MessageChatViewBuild.isLeftView(nextVoiceChatMessage) && !nextVoiceChatMessage.play) {

                            isPlayNothing = false;
                            new Handler().postDelayed(() -> playNextAudio(nextVoiceChatMessage), 200);

                        }
                    }
                }
                if (isPlayNothing) {
                    //如果没有要播放的语音消息，则注销距离感应器监听器
                    playAudioEnd(context);

                }
            }
        }
    };

    private BroadcastReceiver mRefreshViewBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (ACTION_CLEAR_MESSAGE_LIST.equals(action)) {
                clearMessageListData();
                mWithTimeMessageList.clear();
                refreshListAdapter();

            } else if (REFRESH_MESSAGE_LIST.equals(action)) {
                refreshListAdapter();

            } else if (UNDO_MESSAGE_SEND_SUCCESSFULLY.equals(action)) {

                UndoEventMessage undoEventMessage = (UndoEventMessage) intent.getSerializableExtra(ChatDetailFragment.DATA_NEW_MESSAGE);
                if (null != undoEventMessage) {
                    undoMessages(undoEventMessage);
                }

                mProgressDialogHelper.dismiss();

            } else if (REMOVE_MESSAGE_SUCCESSFULLY.equals(action)) {
                List<String> removedIdList = intent.getStringArrayListExtra(DATA_MSG_ID_LIST);

                //update msg data
                removeMessageList(removedIdList);
                refreshWithTimeMsgListTotally();
                mChatDetailArrayListAdapter.setMessages(mWithTimeMessageList);
                refreshListAdapter();

                checkLastMessageOnSession();
            } else if (DELETE_MESSAGES.equals(action)) {
                List<ChatPostMessage> messages = (List<ChatPostMessage>) intent.getSerializableExtra(INTENT_BATCH_MESSAGES);
                deleteMessages(messages);

            } else if (ACTION_REFRESH_SEND_BTN_STATUS.equals(action)) {
                mChatDetailInputView.refreshSendBtnStatus();
            }
        }
    };

    private void clearMessageListData() {
        mRealFirstTimestamp = -1;

        mMessageList.clear();
        mMessageJustShowList.clear();
    }


    private BroadcastReceiver mAddSendingListenerBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            FileTransferChatMessage fileTransferChatMessage = (FileTransferChatMessage) intent.getSerializableExtra(FileStatusView.BUNDLE_FILE_MESSAGE);
            addFileSendingListener(fileTransferChatMessage);
        }
    };

    /**
     * 收到contact已经更新的通知
     */
    private BroadcastReceiver mReceiverContactNotifyMsg = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //重新获取contact
            mLoginUser = AtworkApplicationLike.getLoginUserSync();
        }
    };

    /**
     * 收到是否需要显示半透明遮盖层的通知（用于弹出pop）
     */
    private BroadcastReceiver mReceiverPopWindowNotifyMsg = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra(ChatDetailFragment.TRANSPARENT_POP_DATA);
            int type = intent.getIntExtra(ChatDetailFragment.TRANSPARENT_POP_TYPE, PhoneEmailPopupWindow.PHONE);
            hideAll();
            mPopupWindow = new PhoneEmailPopupWindow(context, type, text, new PhoneEmailPopupWindow.ItemClick() {
                @Override
                public void onItemClick(int type) {
                    switch (type) {
                        case PhoneEmailPopupWindow.PHONE_JUMP:
                            IntentUtil.callPhoneJump(context, text);
                            break;
                        case PhoneEmailPopupWindow.COPY:
                            ClipboardManager cmb = (ClipboardManager) BaseApplicationLike.baseContext.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clipData = ClipData.newPlainText("newPlainTextLabel", text);
                            cmb.setPrimaryClip(clipData);
                            toastOver(R.string.copied);
                            break;
                        case PhoneEmailPopupWindow.CANCEL:
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + type);
                    }
                    mPopupWindow.dismiss();
                }
            });
            assert getFragmentManager() != null;
            mPopupWindow.show(getFragmentManager(), "PhoneEmailPopupWindow");
            mChatDetailInputView.showMoreImage();

            //规避长按头像@人需要弹出输入框
            if (!mNeedHandingKeyboard) {
                hideAll();
            }

            tryHidePopChatDetailFunctionAreaView(-1);
        }
    };

    /**
     * 长点击事件
     */
    private ChatItemLongClickListener chatItemLongClickListener = new ChatItemLongClickListener() {
        @Override
        public void fileLongClick(final FileTransferChatMessage fileTransferChatMessage, AnchorInfo anchorInfo) {
            commonPopLongClickDialog(fileTransferChatMessage, anchorInfo);
        }

        @Override
        public void textLongClick(final ChatPostMessage chatPostMessage, AnchorInfo anchorInfo) {

            commonPopLongClickDialog(chatPostMessage, anchorInfo);
        }

        @Override
        public void imageLongClick(final ChatPostMessage imageChatMessage, AnchorInfo anchorInfo) {

            if (imageChatMessage instanceof ImageChatMessage) {
                commonPopLongClickDialog(imageChatMessage, anchorInfo);
            }

        }

        @Override
        public void annoImageLongClick(ChatPostMessage imageChatMessage, AnchorInfo anchorInfo) {
            commonPopLongClickDialog(imageChatMessage, anchorInfo);
        }

        @Override
        public void stickerLongClick(StickerChatMessage stickerChatMessage, AnchorInfo anchorInfo) {
            commonPopLongClickDialog(stickerChatMessage, anchorInfo);
        }

        @Override
        public void voiceLongClick(final VoiceChatMessage voiceChatMessage, AnchorInfo anchorInfo) {
            commonPopLongClickDialog(voiceChatMessage, anchorInfo);

        }

        @Override
        public void shareLongClick(ShareChatMessage shareChatMessage, AnchorInfo anchorInfo) {
            commonPopLongClickDialog(shareChatMessage, anchorInfo);

        }

        @Override
        public void microVideoLongClick(MicroVideoChatMessage microVideoChatMessage, AnchorInfo anchorInfo) {
            commonPopLongClickDialog(microVideoChatMessage, anchorInfo);

        }

        @Override
        public void voipLongClick(VoipChatMessage voipChatMessage, AnchorInfo anchorInfo) {
            hideInput();

            popVoipLongClickDialog(voipChatMessage, anchorInfo);
        }

        @Override
        public void referenceLongClick(ReferenceMessage referenceMessage, AnchorInfo anchorInfo) {
            commonPopLongClickDialog(referenceMessage, anchorInfo);

        }

        @Override
        public void burnLongClick(ChatPostMessage chatPostMessage, AnchorInfo anchorInfo) {
            hideInput();
            popP2PLongClickDialog(chatPostMessage, anchorInfo);

        }

        @Override
        public void multipartLongClick(MultipartChatMessage multipartChatMessage, AnchorInfo anchorInfo) {
            commonPopLongClickDialog(multipartChatMessage, anchorInfo);
        }

        @Override
        public void meetingLongClick(MeetingNoticeChatMessage meetingNoticeChatMessage, AnchorInfo anchorInfo) {
            hideInput();
            String items[] = MessageItemPopUpHelp.getVirtualMsgPopItems();
            commonChatItemPopUp(meetingNoticeChatMessage, items, anchorInfo);
        }

        @Override
        public void showDeleteLongClick(ChatPostMessage chatPostMessage, AnchorInfo info) {
            commonPopLongClickDialog(chatPostMessage, info);
        }


    };

    private void commonPopLongClickDialog(ChatPostMessage chatPostMessage, AnchorInfo anchorInfo) {
        hideInput();

        if (isUserChat()) {
            popP2PLongClickDialog(chatPostMessage, anchorInfo);
            return;
        }
        if (isDiscussionChat()) {
            popDiscussionLongClickDialog(chatPostMessage, anchorInfo);
            return;
        }

        if (isServiceChat()) {
            popMultipartLongClickDialog(chatPostMessage, anchorInfo);
            return;
        }
    }

    private void popMultipartLongClickDialog(final ChatPostMessage chatPostMessage, AnchorInfo info) {
        String items[] = MessageItemPopUpHelp.getMultipartMessagePopupItems();
        commonChatItemPopUp(chatPostMessage, items, info);
    }

    /**
     * 点击事件
     */
    private ChatItemClickListener chatItemClickListener = new ChatItemClickListener() {


        @Override
        public void selectClick(ChatPostMessage message) {
            refreshSelectTitleText();
        }

        @Override
        public void textClick(TextChatMessage textChatMessage, String viewDoubleClickTag) {
            //双击判断
            if (CommonUtil.isFastClick(viewDoubleClickTag, 300)) {
                Intent intent = MsgContentDetailActivity.getIntent(getContext(), textChatMessage);
                getContext().startActivity(intent);
            }
        }

        //
        @Override
        public void imageClick(ChatPostMessage imageChatMessage) {
            if (imageChatMessage instanceof ImageChatMessage) {
                showImageSwitchFragment(imageChatMessage);
                return;
            }

        }

        @Override
        public void stickerClick(ChatPostMessage stickerChatMessage) {
            Intent intent = StickerViewActivity.Companion.getIntent(mActivity, (StickerChatMessage) stickerChatMessage);
            startActivity(intent, false);
            AtworkUtil.hideInput(getActivity(), mChatDetailInputView.getEmojiIconEditText());
        }

        //点击了文件传输
        @Override
        public void fileClick(FileTransferChatMessage fileTransferChatMessage) {
            if (fileTransferChatMessage.fileType.equals(FileData.FileType.File_Image) || fileTransferChatMessage.fileType.equals(FileData.FileType.File_Gif)) {
                showImageSwitchFragment(fileTransferChatMessage);
                return;
            }
            if (shouldPreviewLocal(fileTransferChatMessage)) {
                previewLocal(fileTransferChatMessage, VIEW_FROM_CHAT_LIST);
            } else {
                showFileStatusFragment(fileTransferChatMessage);
            }
        }

        private void showFileStatusFragment(FileTransferChatMessage fileTransferChatMessage) {
            FileStatusFragment fileStatusFragment = new FileStatusFragment();
            fileStatusFragment.initBundle(mSession.identifier, fileTransferChatMessage, null, VIEW_FROM_FILE_DETAIL);
            fileStatusFragment.show(getChildFragmentManager(), "FILE_DIALOG");

            AudioRecord.stopPlaying();

            AtworkUtil.hideInput(getActivity(), mChatDetailInputView.getEmojiIconEditText());
        }

        private void showImageSwitchFragment(ChatPostMessage message) {
            refreshImageChatMessageList();
            int count = ImageSwitchInChatActivity.sImageChatMessageList.indexOf(message);
            Intent intent = new Intent();
            intent.putExtra(ImageSwitchFragment.INDEX_SWITCH_IMAGE, count);
            intent.putExtra(ImageSwitchFragment.DATA_HIDE_INDEX_POS_UI, true);
            intent.setClass(BaseApplicationLike.baseContext, ImageSwitchInChatActivity.class);
            intent.putExtra(ImageSwitchFragment.ARGUMENT_SESSION, mSession);
            startActivity(intent, false);
            AtworkUtil.hideInput(getActivity(), mChatDetailInputView.getEmojiIconEditText());
        }


        @Override
        public void avatarClick(String identifier, String domainId) {
            hideInput();

            UserManager.getInstance().queryUserByUserId(getActivity(), identifier, domainId, new UserAsyncNetService.OnQueryUserListener() {
                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    ErrorHandleUtil.handleError(errorCode, errorMsg);
                }

                @Override
                public void onSuccess(@NonNull User user) {
                    //显示个人详情
                    if (null != getActivity()) {
                        startActivity(PersonalInfoActivity.getIntent(BaseApplicationLike.baseContext, user));

                    }
                }

            });
        }

        @Override
        public void avatarLongClick(String identifier, String domainId) {

            mNeedHandingKeyboard = true;
            //防止长按时触发 listview 的 onTouch, 从而收起键盘, 造成键盘闪烁
            new Handler().postDelayed(() -> mNeedHandingKeyboard = false, 2000);

            ContactQueryHelper.queryContact(getActivity(), mDiscussion.getOrgCodeCompatible(), domainId, identifier, contact -> {
                if (null != contact) {
                    handleAvatarLongClick(contact);
                }
            });

        }

        private void handleAvatarLongClick(@NonNull ShowListItem contact) {
            if (mChatDetailInputView.getChatInputType() == ChatInputType.Voice) {
                mChatDetailInputView.textMode(true);
            }
            mChatDetailInputView.isAppendAtMembersDirectly = true;
            int tagSelection = mChatDetailInputView.getEmojiIconEditText().getSelectionStart();

            StringBuilder sb = new StringBuilder();
            sb.append("@").append(contact.getTitle()).append(" ");
//                        mChatDetailInputView.getEmojiIconEditText().append(sb.toString());
            tagSelection += sb.length();
            SpannableString text = getEditTextString(AtworkUtil.createBitmapByString(mActivity, sb.toString()), sb.toString());
            mChatDetailInputView.appendText(text);
            mAtContacts.add(ContactHelper.toUserHandleInfo(contact));
            //                    AtworkUtil.showInput(new WeakReference<Activity>(getActivity()),mChatDetailInputView.getEmojiIconEditText());
            showInput();
            mChatDetailInputView.isAppendAtMembersDirectly = false;
            mChatDetailInputView.getEmojiIconEditText().setSelection(tagSelection);
        }


        @Override
        public void hideAll() {
            ChatDetailFragment.this.hideAll();
            LogUtil.i("shadow", "hideinput1");
        }

        @Override
        public void microVideoClick(MicroVideoChatMessage microVideoChatMessage) {
            if (VoipHelper.isHandlingVoipCall()) {
                AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);
                return;
            }
            refreshImageChatMessageList();

            int count = ImageSwitchInChatActivity.sImageChatMessageList.indexOf(microVideoChatMessage);
            Intent intent = new Intent();
            intent.putExtra(ImageSwitchFragment.INDEX_SWITCH_IMAGE, count);
            intent.putExtra(ImageSwitchFragment.DATA_HIDE_INDEX_POS_UI, true);
            intent.setClass(BaseApplicationLike.baseContext, ImageSwitchInChatActivity.class);
            startActivity(intent);
            AtworkUtil.hideInput(getActivity(), mChatDetailInputView.getEmojiIconEditText());

        }

        @Override
        public void voipClick(VoipChatMessage voipChatMessage) {
            if (!VoipHelper.isVoipEnable(getActivity())) {
                AtworkToast.showResToast(R.string.alert_have_no_auth_voip);

                return;
            }

            if (AtworkUtil.isSystemCalling()) {
                AtworkToast.showResToast(R.string.alert_is_handling_system_call);
                return;
            }

            if (VoipHelper.isHandlingVoipCall()) {
                AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);
                return;
            }

            if (null == voipChatMessage.mVoipSdkType || VoipSdkType.AGORA == voipChatMessage.mVoipSdkType) {
                startP2pTypeVoipMeeting();
                return;
            }

            if (voipChatMessage.isZoomProduct()) {
                zoomRouteP2pCallInstant(getActivity());
            }

        }

        @Override
        public void referenceClick(ReferenceMessage referenceMessage) {
            jumpTargetMessage(referenceMessage);


        }

        @Override
        public void annoImageClick(AnnoImageChatMessage annoImageChatMessage, ImageContentInfo targetImageContentInfo) {
            refreshImageChatMessageList();
            int count = ImageSwitchInChatActivity.sImageChatMessageList.indexOf(targetImageContentInfo.transformImageChatMessage(annoImageChatMessage));
            Intent intent = new Intent();
            intent.putExtra(ImageSwitchFragment.INDEX_SWITCH_IMAGE, count);
            intent.putExtra(ImageSwitchFragment.DATA_HIDE_INDEX_POS_UI, true);
            intent.setClass(BaseApplicationLike.baseContext, ImageSwitchInChatActivity.class);
            intent.putExtra(ImageSwitchFragment.ARGUMENT_SESSION, mSession);
            startActivity(intent, false);
            AtworkUtil.hideInput(getActivity(), mChatDetailInputView.getEmojiIconEditText());
        }

        @Override
        public void burnClick(ChatPostMessage chatPostMessage) {
            if (chatPostMessage instanceof VoiceChatMessage) {
                if (VoipHelper.isHandlingVoipCall()) {
                    AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);

                    return;
                }
            }

            hideInput();

            if (MessageChatViewBuild.isLeftView(chatPostMessage)) {
                queryBurnMessageAuthWithRequestPermission(chatPostMessage);

            } else {
                if (chatPostMessage.isExpired()) {
                    toastOver(R.string.receiver_burn_msg_expired_tip);
                    deleteMessages(ListUtil.makeSingleList(chatPostMessage));

                } else {
                    goBurnDetailActivity(chatPostMessage);

                }
            }

        }

        @Override
        public void RightMultipartClick(MultipartChatMessage multipartChatMessage) {
            Intent intent = MultiPartDetailActivity.getIntent(getActivity(), multipartChatMessage, mStrTranslationShortName);
            startActivity(intent);
        }

        @Override
        public void LeftMultipartClick(MultipartChatMessage multipartChatMessage) {
            Intent intent = MultiPartDetailActivity.getIntent(getActivity(), multipartChatMessage, mStrTranslationShortName);
            startActivity(intent);
        }

        @Override
        public void meetingClick(MeetingNoticeChatMessage meetingNoticeChatMessage) {
            MeetingHelper.handleClick(getActivity(), meetingNoticeChatMessage);
        }

        @Override
        public void reEditUndoClick(ChatPostMessage chatPostMessage) {
            if (chatPostMessage instanceof TextChatMessage) {
                TextChatMessage textChatMessage = (TextChatMessage) chatPostMessage;
                EditText inputView = mChatDetailInputView.getEmojiIconEditText();
//                StringBuilder textBuilder = new StringBuilder(inputView.getText().toString());
//                textBuilder.append(textChatMessage.text);

                inputView.setText(textChatMessage.text);
                inputView.setSelection(textChatMessage.text.length());

                showInput();
            }
        }

        @Override
        public void locationClick(ShareChatMessage shareChatMessage) {
            requestLocationPermission(new OnLocationPermissionResult() {
                @Override
                public void onGrant() {
                    if (shareChatMessage == null) {
                        return;
                    }
                    Object object = shareChatMessage.getChatBody().get(ShareChatMessage.SHARE_MESSAGE);
                    if (!(object instanceof ShareChatMessage.LocationBody)) {
                        return;
                    }
                    ShareChatMessage.LocationBody locationBody = (ShareChatMessage.LocationBody) object;
                    ShowLocationActivity.Companion.startActivity(mActivity, locationBody);
                }

                @Override
                public void onReject(String permission) {
                    if (isAdded()) {
                        final AtworkAlertDialog alertDialog = AtworkUtil.getAuthSettingAlert(getActivity(), permission);
                        alertDialog.setOnDismissListener(dialog -> {
                        });

                        alertDialog.show();
                    }
                }
            });
        }


    };


    private void requestLocationPermission(OnLocationPermissionResult callback) {
        if (callback == null) {
            return;
        }
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                callback.onGrant();
            }

            @Override
            public void onDenied(String permission) {
                callback.onReject(permission);
            }
        });
    }

    private void queryBurnMessageAuthWithRequestPermission(final ChatPostMessage chatPostMessage) {
        if (chatPostMessage instanceof ImageChatMessage
                || chatPostMessage instanceof FileTransferChatMessage
                || chatPostMessage instanceof MicroVideoChatMessage
                || chatPostMessage instanceof VoiceChatMessage) {
            AndPermission
                    .with(mActivity)
                    .runtime()
                    .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                    .onGranted(data -> {
                        queryBurnMessageAuth(chatPostMessage);
                    })
                    .onDenied(data -> AtworkUtil.popAuthSettingAlert(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    .start();
        } else {
            queryBurnMessageAuth(chatPostMessage);
        }
    }

    private void queryBurnMessageAuth(final ChatPostMessage chatPostMessage) {
        mProgressDialogHelper.show(false);

        MessageAsyncNetService.queryBurnMessageAuth(getActivity(), chatPostMessage.deliveryId, new BaseCallBackNetWorkListener() {
            @Override
            public void onSuccess() {

                if (chatPostMessage instanceof ImageChatMessage) {
                    loadImageOriginal(chatPostMessage);


                } else {
                    mProgressDialogHelper.dismiss();

                    goBurnDetailActivity(chatPostMessage);
                }
            }


            @Override
            public void networkFail(int errorCode, String errorMsg) {
                mProgressDialogHelper.dismiss();
                if (AtworkConstants.MSG_HAD_REMOVED == errorCode) {

                    deleteMessages(ListUtil.makeSingleList(chatPostMessage));
                    if (chatPostMessage.isExpired()) {
                        toastOver(R.string.receiver_burn_msg_expired_tip);

                    } else {
                        toastOver(R.string.read_message_failed);

                    }

                } else {
                    toastOver(R.string.read_message_failed);
                    ErrorHandleUtil.handleTokenError(errorCode, errorMsg);
                }


            }


        });
    }

    private void loadImageOriginal(ChatPostMessage chatPostMessage) {
        //下载原图片
        MediaCenterNetManager mediaCenterNetManager = new MediaCenterNetManager(BaseApplicationLike.baseContext);
        MediaCenterNetManager.addMediaDownloadListener(new MediaCenterNetManager.MediaDownloadListener() {
            @Override
            public String getMsgId() {
                return chatPostMessage.deliveryId;
            }

            @Override
            public void downloadSuccess() {
                mProgressDialogHelper.dismiss();

                if (((ImageChatMessage) chatPostMessage).isGif) {
                    goBurnDetailActivity(chatPostMessage);

                } else {
                    Bitmap bitmap = BitmapUtil.Bytes2Bitmap(ImageShowHelper.getOriginalImage(mActivity, getMsgId()));
                    if (null != bitmap) {
                        BitmapCache.getBitmapCache().addBitmapToMemoryCache(getMsgId() + ImageChatMessage.ORIGINAL_SUFFIX, bitmap);

                        goBurnDetailActivity(chatPostMessage);
                    } else {
                        toastOver(R.string.read_message_failed);

                    }
                }


            }

            @Override
            public void downloadFailed(int errorCode, String errorMsg, boolean doRefreshView) {
                mProgressDialogHelper.dismiss();
                toastOver(R.string.read_message_failed);

            }

            @Override
            public void downloadProgress(double progress, double value) {

            }
        });
        String mediaId = ((ImageChatMessage) chatPostMessage).mediaId;

        mediaCenterNetManager.downloadFile(
                DownloadFileParamsMaker.Companion.newRequest().setDownloadId(chatPostMessage.deliveryId).setMediaId(mediaId)
                        .setDownloadPath(ImageShowHelper.getOriginalPath(mActivity, chatPostMessage.deliveryId))
                        .setDownloadType(MediaCenterHttpURLConnectionUtil.DOWNLOAD_TYPE.ORIGINAL)
        );
    }

    private void goBurnDetailActivity(ChatPostMessage chatPostMessage) {
        hideInput();

        sScreenshot = ExplosionUtils.createBitmapFromView(mRlRoot);
        Intent intent = BurnMessageDetailActivity.getIntent(getActivity(), chatPostMessage, mSession.identifier);
        startActivity(intent);
    }

    private void popDiscussionLongClickDialog(final ChatPostMessage chatPostMessage, AnchorInfo anchorInfo) {
        String items[] = MessageItemPopUpHelp.getDiscussionPopItems(getActivity(), mDiscussion, chatPostMessage, mChatModel);
        commonChatItemPopUp(chatPostMessage, items, anchorInfo);

    }

    private void popP2PLongClickDialog(final ChatPostMessage chatPostMessage, AnchorInfo anchorInfo) {
        ReceiptDaoService.getInstance().queryP2PReceipt(mSession.identifier, chatPostMessage.deliveryId, chatPostMessage.deliveryTime, receiptMessage -> {
            if (isAdded()) {
                String[] items = null;
                items = MessageItemPopUpHelp.getP2PPopItems(getActivity(), chatPostMessage, receiptMessage, mChatModel);
                chatItemPopUp(chatPostMessage, items, receiptMessage, anchorInfo);
            }
        });
    }

    private void popVoipLongClickDialog(final VoipChatMessage voipChatMessage, AnchorInfo anchorInfo) {
        String items[] = MessageItemPopUpHelp.getVirtualMsgPopItems();
        commonChatItemPopUp(voipChatMessage, items, anchorInfo);
    }

    private void commonChatItemPopUp(final ChatPostMessage chatPostMessage, String[] items, AnchorInfo anchorInfo) {

        BasicChatItemView.setMsgIdNeedMask(chatPostMessage.deliveryId);
        refreshListAdapter();


        ChatPopUpDialogDupportPack chatSelectDialog = new ChatPopUpDialogDupportPack();
        Bundle dialogBundle = new Bundle();
        ArrayList<String> itemDialogList = new ArrayList<>();
        itemDialogList.addAll(Arrays.asList(items));
        dialogBundle.putStringArrayList(ChatPopUpDialogDupportPack.DATA_ITEMS_LIST, itemDialogList);
        dialogBundle.putInt(ChatPopUpDialogDupportPack.ANCHOR_HEIGHT, anchorInfo.anchorHeight);
        dialogBundle.putInt(ChatPopUpDialogDupportPack.AREA_HEIGHT, anchorInfo.chatViewHeight);
        dialogBundle.putString(ChatPopUpDialogDupportPack.DELIVERY_ID, chatPostMessage.deliveryId);
        chatSelectDialog.setArguments(dialogBundle);
        chatSelectDialog.setOnclick(value -> {
            chatItemClickEvent(value, chatPostMessage, null);
        });

        chatSelectDialog.setOnDismissingListener(() -> {
            BasicChatItemView.clearMsgIdNeedMask();
            refreshListAdapter();
        });

        if (getActivity() != null) {
            chatSelectDialog.show(getChildFragmentManager(), "TEXT_POP_DIALOG");
        }
    }

    private void forwardMsgPopUp(List<ChatPostMessage> messages) {
        List<ChatPostMessage> msgListCanForward = new ArrayList<>();

        List<String> items = new ArrayList<>();
        items.add(getStrings(R.string.item_by_item_transfer));
        items.add(getStrings(R.string.multipart_transfer));

        ArrayList<String> itemList = new ArrayList<>();
        itemList.addAll(items);

        W6sSelectDialogFragment w6sSelectDialogFragment = new W6sSelectDialogFragment();
        w6sSelectDialogFragment.setData(new CommonPopSelectData(itemList, null))
                .setTextContentCenter(true)
                .setDialogWidth(148)
                .setOnClickItemListener((position, value) -> {
                    if (TextUtils.isEmpty(value)) {
                        return;
                    }
                    boolean isItemByItem = getStrings(R.string.item_by_item_transfer).equals(value);
                    for (ChatPostMessage postMessage : messages) {
                        if (MessageItemPopUpHelp.canForward(postMessage, isItemByItem, mChatModel == ChatModel.SELECT)) {
                            msgListCanForward.add(postMessage);

                        }
                    }
                    onForwardMsgPopUpClick(value, msgListCanForward, messages);
                })
                .show(getFragmentManager(), "TEXT_POP_DIALOG");
    }

    private void onForwardMsgPopUpClick(String value, List<ChatPostMessage> msgListCanForward, List<ChatPostMessage> messages) {

        if (msgListCanForward.size() == messages.size()) {
            forwardMessage(value, msgListCanForward);
            return;
        }

        popAskMsgNotForward(value, msgListCanForward);
    }

    private void popAskMsgNotForward(String value, List<ChatPostMessage> msgListCanForward) {
        boolean isItemByItem = getStrings(R.string.item_by_item_transfer).equals(value);

        AtworkAlertDialog askDialog = new AtworkAlertDialog(getActivity(), AtworkAlertDialog.Type.SIMPLE);
        askDialog.setContent(StringConfigHelper.getMsgNotForward(getActivity(), isItemByItem))
                .setClickBrightColorListener(dialog1 -> {
                    if (0 == msgListCanForward.size()) {
                        changeModel();

                    } else {
                        forwardMessage(value, msgListCanForward);

                    }

                }).setClickDeadColorListener(dialog1 -> {
//                                changeModel();

        }).show();
    }

    private void chatItemClickEvent(String value, final ChatPostMessage chatPostMessage, @Nullable ReceiptMessage receiptMessage) {

        //听筒
        if (MessageItemPopUpHelp.VOICE_PHONE.equals(value)) {
            PersonalShareInfo.getInstance().setAudioPlayMode(getActivity(), false);

            AtworkToast.showResToast(R.string.switch_earphone_mode);
            return;
        }
        //扬声器
        if (MessageItemPopUpHelp.VOICE_SPEAK.equals(value)) {
            PersonalShareInfo.getInstance().setAudioPlayMode(getActivity(), true);

            AtworkToast.showResToast(R.string.switch_speaker_mode);
            return;
        }
        //语音转换成文字
        if (MessageItemPopUpHelp.VOICE_TRANSLATE.equals(value)) {
            VoiceChatMessage voiceChatMessage = (VoiceChatMessage) chatPostMessage;
//            Intent intent = MsgContentDetailActivity.getIntent(getActivity(), voiceChatMessage);
//            startActivity(intent);
//
//            voiceChatMessage.play = true;
//            ChatDaoService.getInstance().updateMessage(voiceChatMessage);
//
//            new Handler().postDelayed(() -> {
//                if (isAdded()) {
//                    refreshListAdapter();
//                }
//            }, 500);
            setTranslating(voiceChatMessage, true);
            //获取到翻译的结果
            doXunfeiVoiceRecognize(voiceChatMessage);//进行讯飞语音识别

            return;
        }
        //语音已转换成的文字收起文字
        if (MessageItemPopUpHelp.VOICE_SHOW_ORIGINAL.equals(value)) {
            VoiceChatMessage voiceChatMessage = (VoiceChatMessage) chatPostMessage;
            //设置隐藏
            voiceChatMessage.mVoiceTranslateStatus.mTranslating = false;
            voiceChatMessage.mVoiceTranslateStatus.mVisible = false;
            //更新
            ChatDaoService.getInstance().insertOrUpdateMessage(BaseApplicationLike.baseContext, voiceChatMessage);
            //发送广播
            ChatDetailExposeBroadcastSender.refreshMessageListViewUI();
            return;
        }

        //复制功能
        if (MessageItemPopUpHelp.COPY_ITEM.equals(value)) {
            ClipboardManager cmb = (ClipboardManager) BaseApplicationLike.baseContext.getSystemService(Context.CLIPBOARD_SERVICE);
            TextChatMessage textChatMessage = (TextChatMessage) chatPostMessage;
            ClipData clipData = ClipData.newPlainText("newPlainTextLabel", TextMsgHelper.getVisibleText(textChatMessage));
            cmb.setPrimaryClip(clipData);

            toastOver(R.string.copied);

            return;
        }

        //更多
        if (MessageItemPopUpHelp.MORE_ITEM.equals(value)) {
            changeModel();
            return;
        }

        //删除
        if (MessageItemPopUpHelp.DELETE_ITEM.equals(value)) {
            List<ChatPostMessage> messages = new ArrayList<>();
            messages.add(chatPostMessage);
            deleteMessages(messages);
            return;
        }
        //转发
        if (MessageItemPopUpHelp.FORWARDING_ITEM.equals(value)) {
            forwardMessage(null, ListUtil.makeSingleList(chatPostMessage));

            return;
        }

        if (MessageItemPopUpHelp.SHARE_DROPBOX.equals(value)) {
            shareDropbox(chatPostMessage);
        }

        if (MessageItemPopUpHelp.TEXT_TRANSLATE.equals(value)) {
            TextChatMessage textChatMessage = (TextChatMessage) chatPostMessage;

            //不仅要求是否有翻译结果，同时还要求翻译的语种是否一致！
            //if (StringUtils.isEmpty(textChatMessage.getTranslatedResult())) {
            if (isTranslate(textChatMessage)) {
                TextTranslateHelper.setTranslating(textChatMessage, true);

                intelligentTranslation(textChatMessage, result -> {
                    if (!StringUtils.isEmpty(result)) {
                        TextTranslateHelper.updateTranslateResultAndUpdateDb(textChatMessage, result, mCurrentLanguage);
                    } else {
                        TextTranslateHelper.setTranslating(textChatMessage, false);

                    }
                });


//                Translator.getInstance().translate(getActivity(), TextMsgHelper.getShowText(textChatMessage), result -> {
//
//                    if (!StringUtils.isEmpty(result)) {
//                        LogUtil.e("translate result - > " + result);
//                        TextTranslateHelper.updateTranslateResultAndUpdateDb(textChatMessage, result);
//
//                    } else {
//                        toastOver(R.string.Translate_common);
//                        TextTranslateHelper.setTranslating(textChatMessage, false);
//
//                    }
//
//
//                });

            } else {
                TextTranslateHelper.showTranslateStatusAndUpdateDb(textChatMessage, true);

            }

            return;
        }

        if (MessageItemPopUpHelp.TEXT_SHOW_ORIGINAL.equals(value)) {
            TextChatMessage textChatMessage = (TextChatMessage) chatPostMessage;
            TextTranslateHelper.showTranslateStatusAndUpdateDb(textChatMessage, false);
            return;
        }

        //撤回
        if (MessageItemPopUpHelp.CHAT_UNDO.equals(value)) {
            if (UndoMessageHelper.isFirstClickUndoMessage()) {
                PersonalShareInfo.getInstance().setFirstClickUndoMessage(mActivity, false);

                AtworkAlertDialog dialog = new AtworkAlertDialog(mActivity, AtworkAlertDialog.Type.SIMPLE)
                        .setContent(getStrings(R.string.first_use_undo_message, TimeViewUtil.getShowDurationHavingText(DomainSettingsManager.getInstance().getMaxUndoTime())))
                        .hideDeadBtn();

                dialog.setOnDismissListener(dialog1 -> clickUndoItem(chatPostMessage));

                dialog.show();
                return;
            }
            clickUndoItem(chatPostMessage);
            return;
        }

        //群聊已读未读
        if (MessageItemPopUpHelp.CHECK_UNREAD_READ.equals(value)) {
            String orgCode = StringUtils.EMPTY;
            String discussionId = StringUtils.EMPTY;
            if (null != mDiscussion) {
                discussionId = mDiscussion.mDiscussionId;
            }

            if (null != mDiscussion && mDiscussion.showEmployeeInfo()) {
                orgCode = mDiscussion.getOrgCodeCompatible();
            }
            startActivity(DiscussionReadUnreadActivity.getIntent(BaseApplicationLike.baseContext, chatPostMessage.deliveryId, DiscussionReadUnreadFragment.ReadOrUnread.Unread, discussionId, orgCode));
            return;
        }

        if (MessageItemPopUpHelp.MESSAGE_REFERENCE.equals(value)) {
            mPopChatDetailDataHoldingView.refreshMessageReference(chatPostMessage);
            mChatDetailInputView.textMode(true);

            //makeQuoteMessage(chatPostMessage);
        }

        //单聊已读
        if (MessageItemPopUpHelp.USER_READ.equals(value)) {
            if (null != receiptMessage) {
//                AtworkToast.showResToast(R.string.toast_read_info, TimeUtil.getStringForMillis(receiptMessage.timestamp, TimeUtil.getTimeFormat2(BaseApplicationLike.baseContext)));
            }
            return;
        }


        if (MessageItemPopUpHelp.DEBUG_TEST_CLONE_MESSAGE.equals(value)) {
            mProgressDialogHelper.show();

            long beginTime = System.currentTimeMillis();

            TestManager.INSTANCE.cloneMessageBatch(ListUtil.makeSingleList(chatPostMessage), () -> {
                mProgressDialogHelper.dismiss();
                toast("成功克隆了10000条消息, 耗时 : " + (System.currentTimeMillis() - beginTime));

                return Unit.INSTANCE;
            });

            return;
        }


        if (MessageItemPopUpHelp.DEBUG_TEST_QUERY_MESSAGE_COUNT.equals(value)) {
            mProgressDialogHelper.show();

            TestManager.INSTANCE.queryCount(mSession.identifier, integer -> {
                mProgressDialogHelper.dismiss();

                toast("一共 " + integer + "条消息");
                return Unit.INSTANCE;

            });

            return;

        }

        if (MessageItemPopUpHelp.RESEND.equals(value)) {

            if (chatPostMessage.getChatType().equals(ChatPostMessage.ChatType.Share)) {

                ShareChatMessage shareChatMessage = (ShareChatMessage) chatPostMessage;
                ShareChatMessage fakeMessage = CloneUtil.cloneTo(shareChatMessage);


                String meUserId = LoginUserInfo.getInstance().getLoginUserId(mActivity);
                String meUserName = LoginUserInfo.getInstance().getLoginUserName(mActivity);
                String meUserAvatar = LoginUserInfo.getInstance().getLoginUserAvatar(mActivity);
                ParticipantType toType = ParticipantType.Discussion;

                fakeMessage.reGenerate(mActivity, meUserId, mSession.getId(), mSession.getDomainId(), ParticipantType.User, toType, fakeMessage.mBodyType, mSession.orgId, mSession, meUserName, meUserAvatar);
                ChatService.sendMessageOnBackground(mSession, fakeMessage);
            }

            return;

        }

    }

    //获取服务器最新的会话配置! 同步会话设置：智能翻译；
    private void syncConversationSettings() {
        if (null == mSession) {
            return;
        }

        ConversionConfigSettingParticipant participant = new ConversionConfigSettingParticipant(mSession.identifier, mSession.mDomainId, mSession.type);
        ConfigSettingsManager.INSTANCE.getConversationSettingRemote(participant, new BaseNetWorkListener<ConversionConfigSettingItem>() {
            @Override
            public void onSuccess(ConversionConfigSettingItem conversionConfigSettingItem) {

                //将获取到的服务器最新的会话配置保存到本地session（保存到缓存中，以及本地数据库中）
                mStrTranslationShortName = conversionConfigSettingItem.getLanguage();
                setSessionTranslationSetting(!StringUtils.isEmpty(conversionConfigSettingItem.getLanguage()), conversionConfigSettingItem.getLanguage());
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleTokenError(errorCode, errorMsg);
            }
        });
    }

    private void setSessionTranslationSetting(Boolean waitToOpen, String shortLanguage) {
        ConfigSetting configSetting = new ConfigSetting();
        configSetting.mSourceId = mSession.identifier;
        configSetting.mSourceType = com.foreveross.atwork.infrastructure.model.setting.SourceType.valueOf(mSession.type);
        configSetting.mBusinessCase = BusinessCase.SESSION_TRANSLATION;

        if (waitToOpen) {
            configSetting.mValue = TranslateLanguageType.TranslateLanguage.getTranslateLanguageValue(shortLanguage);
        } else {
            configSetting.mValue = TranslateLanguageType.TranslateLanguage.NO.getValue();
        }
        //设置到本地Session（保存到缓存中，以及本地数据库中）
        ConfigSettingsManager.INSTANCE.setSessionSettingLocal(configSetting, result -> {
            if (result) {
                if (!waitToOpen) {
                    mStrTranslationShortName = "";
                    mIvTitleBarChatDetailTranslation.setVisibility(View.GONE);
                } else {
                    mStrTranslationShortName = shortLanguage;
                    mIvTitleBarChatDetailTranslation.setVisibility(View.VISIBLE);
                }
                if (null != mChatDetailArrayListAdapter) {
                    mChatDetailArrayListAdapter.setTranslateLanguage(mStrTranslationShortName);
                    refreshListAdapter();
                }
            }
            return null;
        });

    }

    //查询本地session的值，此时获取到的language的值为en
    private void querySessionSettingTranslationLanguage() {

        ConfigSetting configSetting = new ConfigSetting();
        configSetting.mSourceId = mSession.identifier;
        configSetting.mSourceType = com.foreveross.atwork.infrastructure.model.setting.SourceType.valueOf(mSession.type);
        configSetting.mBusinessCase = BusinessCase.SESSION_TRANSLATION;

        //查询本地session的值，此时获取到的language的值为en
        ConfigSettingsManager.INSTANCE.querySessionSetting(configSetting, configSettingReceived -> {
            if (null != configSettingReceived) {
                if (0 != configSettingReceived.mValue) {
                    mStrTranslationShortName = TranslateLanguageType.TranslateLanguage.getTranslateLanguageShortName(configSettingReceived.mValue);
                    mIvTitleBarChatDetailTranslation.setVisibility(View.VISIBLE);
                } else {
                    mStrTranslationShortName = "";
                    mIvTitleBarChatDetailTranslation.setVisibility(View.GONE);
                }
            }

            return null;
        });
    }

    private String getLocalLanguageShortName() {
        String languageShortName = "";
        switch (LanguageUtil.getLanguageSupport(getContext())) {

            case LanguageSupport.SIMPLIFIED_CHINESE:
                languageShortName = "zh-CHS";
                break;

            case LanguageSupport.TRADITIONAL_CHINESE:
                languageShortName = "zh-CHS";

                break;

            default:
                languageShortName = "en";

        }
        return languageShortName;
    }

    private Boolean isTranslate(TextChatMessage textChatMessage) {
        String result = textChatMessage.getTranslatedResult();
        Boolean isEmptyTranslatedResult = StringUtils.isEmpty(result);
        if (isEmptyTranslatedResult) {
            return true;
        }
        String language = (null != textChatMessage.mTextTranslate.mTranslationLanguage ? textChatMessage.mTextTranslate.mTranslationLanguage : "");
        Boolean hasLanguage = !language.equalsIgnoreCase(mStrTranslationShortName);
        if (hasLanguage) {
            return true;
        }
        return false;
    }

    /**
     * Description:调用有道翻译api翻译
     *
     * @param textChatMessage
     * @param listener
     */
    public void intelligentTranslation(TextChatMessage textChatMessage, final OnResultListener listener) {
        //当前长按翻译的语种
        mCurrentLanguage = mStrTranslationShortName;
        if (StringUtils.isEmpty(mStrTranslationShortName)) {
            mCurrentLanguage = getLocalLanguageShortName();
        }

        YoudaoTranslate youdaoTranslate = new YoudaoTranslate();
        String url = youdaoTranslate.translate(textChatMessage.text, mCurrentLanguage);
        youdaoTranslate.getTranlateLanguage(url, new YoudaoTranslate.OnTranslateListener() {
            @Override
            public void networkFail(int errorCode, String errorMsg) {
                LogUtil.e("translate result - > " + "网络失败！");
                toastOver(R.string.Translate_common);
                listener.onResult(null);

            }

            @Override
            public void onSuccess(TranslateLanguageResponse translateLanguageResponse) {
                if (!StringUtils.isEmpty(translateLanguageResponse.translation.get(0))) {
                    listener.onResult(translateLanguageResponse.translation.get(0));

                } else {
                    listener.onResult(null);

                }
            }
        });
    }

    private String makeQuoteMessage(ChatPostMessage chatPostMessage) {
        TextChatMessage textChatMessage = (TextChatMessage) chatPostMessage;
        String displayName = "";
        if (mDiscussion.showEmployeeInfo()) {
            Employee employee = EmployeeManager.getInstance().queryEmpInSync(mActivity, textChatMessage.from, mDiscussion.getOrgCodeCompatible());
            displayName = employee.name;
        } else {
            User user = UserManager.getInstance().queryUserInSyncByUserId(mActivity, textChatMessage.from, textChatMessage.mFromDomain);
            displayName = user.getShowName();
        }
//        StringBuilder stringBuilder = new StringBuilder(mChatDetailInputView.getEmojiIconEditText().getText());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(MESSAGE_LEFT_QUOTE_FLAG).append(displayName).append(":").append(textChatMessage.text).append(MESSAGE_RIGHT_QUOTE_FLAG).append(MESSAGE_QUOTE_DIVER).append("\n");

//        mChatDetailInputView.getEmojiIconEditText().setText(stringBuilder.toString());
//        mChatDetailInputView.getEmojiIconEditText().setSelection(stringBuilder.toString().length());

        return stringBuilder.toString();
    }

    private void clickUndoItem(ChatPostMessage chatPostMessage) {
        if (!NetworkStatusUtil.isNetworkAvailable(mActivity)) {
            AtworkToast.showResToast(R.string.network_error);
            return;
        }
        if (UndoMessageHelper.isHandleUndoLegal(chatPostMessage)) {
            sendUndoMessage(chatPostMessage);
            if (isDiscussionChat() && chatPostMessage instanceof FileTransferChatMessage) {
                FileTransferChatMessage message = (FileTransferChatMessage) MessageRepository.getInstance().queryMessage(mActivity, mSession.identifier, chatPostMessage.deliveryId);
                if (message != null && !TextUtils.isEmpty(message.dropboxFileId)) {
                    List<String> deleteList = new ArrayList<>();
                    deleteList.add(message.dropboxFileId);
                    DropboxManager.getInstance().deleteDropboxFile(mActivity, deleteList, "", mSession.mDomainId, Dropbox.SourceType.Discussion, mSession.identifier, new DropboxAsyncNetService.OnDropboxListener() {
                        @Override
                        public void onDropboxOpsSuccess(List<Dropbox> dropboxes) {
                            DropboxRepository.getInstance().deleteDropboxByFileId(message.dropboxFileId);
                        }

                        @Override
                        public void onDropboxOpsFail(int errorCode) {

                        }
                    });
                }

            }
            mProgressDialogHelper.show();
        } else {
            AtworkToast.showResToast(R.string.undo_message_expire_time, TimeViewUtil.getShowDurationHavingText(DomainSettingsManager.getInstance().getMaxUndoTime()));
        }
    }

    private void chatItemPopUp(final ChatPostMessage chatPostMessage, String[] items, ReceiptMessage receiptMessage, AnchorInfo anchorInfo) {

        BasicChatItemView.setMsgIdNeedMask(chatPostMessage.deliveryId);
        refreshListAdapter();

        ChatPopUpDialogDupportPack chatSelectDialog = new ChatPopUpDialogDupportPack();
        Bundle dialogBundle = new Bundle();
        ArrayList<String> itemDialogList = new ArrayList<>();
        itemDialogList.addAll(Arrays.asList(items));
        dialogBundle.putStringArrayList(ChatPopUpDialogDupportPack.DATA_ITEMS_LIST, itemDialogList);
        dialogBundle.putInt(ChatPopUpDialogDupportPack.ANCHOR_HEIGHT, anchorInfo.anchorHeight);
        dialogBundle.putInt(ChatPopUpDialogDupportPack.AREA_HEIGHT, anchorInfo.chatViewHeight);
        dialogBundle.putString(ChatPopUpDialogDupportPack.DELIVERY_ID, chatPostMessage.deliveryId);
        chatSelectDialog.setArguments(dialogBundle);
        chatSelectDialog.setOnclick(value -> chatItemClickEvent(value, chatPostMessage, receiptMessage));

        chatSelectDialog.setOnDismissingListener(() -> {
            BasicChatItemView.clearMsgIdNeedMask();
            refreshListAdapter();
        });

        if (getActivity() != null) {
            chatSelectDialog.show(getChildFragmentManager(), "TEXT_POP_DIALOG");
        }
    }

    private void forwardMessage(String actionValue, List<ChatPostMessage> messages) {

        if (messages.size() == 1 && messages.get(0) instanceof VoiceChatMessage && mChatModel == ChatModel.COMMON) {
            actionValue = getStrings(R.string.multipart_transfer);
        }
        if (null == actionValue || getStrings(R.string.item_by_item_transfer).equals(actionValue)) {

            TransferMessageControlAction transferMessageControlAction = new TransferMessageControlAction();
            transferMessageControlAction.setSendMessageList(messages);

            Intent intent = TransferMessageActivity.Companion.getIntent(BaseApplicationLike.baseContext, transferMessageControlAction);
            startActivity(intent);


        } else if (getStrings(R.string.multipart_transfer).equals(actionValue)) {


            MultipartChatMessage.Builder builder = new MultipartChatMessage.Builder();
            builder.setContext(BaseApplicationLike.baseContext)
                    .setMsgList(messages);

            MultipartChatMessage multipartChatMessage = builder.build();


            TransferMessageControlAction transferMessageControlAction = new TransferMessageControlAction();
            transferMessageControlAction.setSendMessageList(ListUtil.makeSingleList(multipartChatMessage));

            Intent intent = TransferMessageActivity.Companion.getIntent(BaseApplicationLike.baseContext, transferMessageControlAction);

            startActivity(intent);
//            MultipartMsgHelper.assembleMsg(BaseApplicationLike.baseContext, multipartChatMessage, multipartChatMessageCallback -> {
//
//
//            });


        }


    }


    private void shareDropbox(ChatPostMessage chatPostMessage) {
        FileShareAction action = new FileShareAction();
        action.setDomainId(AtworkConfig.DOMAIN_ID);
        action.setSourceType(Dropbox.SourceType.User);
        action.setOpsId(LoginUserInfo.getInstance().getLoginUserId(mActivity));
        action.setType("file_id");
        String mediaId = "";
        if (chatPostMessage instanceof ImageChatMessage) {
            ImageChatMessage imageChatMessage = (ImageChatMessage) chatPostMessage;
            mediaId = imageChatMessage.mediaId;
        }
        if (chatPostMessage instanceof FileTransferChatMessage) {
            FileTransferChatMessage fileTransferChatMessage = (FileTransferChatMessage) chatPostMessage;
            mediaId = fileTransferChatMessage.mediaId;
        }
        if (chatPostMessage instanceof MicroVideoChatMessage) {
            MicroVideoChatMessage microVideoChatMessage = (MicroVideoChatMessage) chatPostMessage;
            mediaId = microVideoChatMessage.mediaId;
        }
        action.setFileId(mediaId);
        FileShareActivity.Companion.startActivity(mActivity, action);
    }

    private void unregisterBroadcast() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mNewMessageBroadcast);

        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mOfflineMessageBroadcast);

        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mRefreshViewBroadcastReceiver);

        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mPlayingNextBroadcastReceiver);

        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mSessionInvalidBroadcast);

        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mSideBroadcast);

        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mReceiverContactNotifyMsg);

        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mAddSendingListenerBroadcastReceiver);

        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mReceiverPopWindowNotifyMsg);
    }

    private void registerBroadcast() {

        IntentFilter newMessageFilter = new IntentFilter();
        newMessageFilter.addAction(CHAT_MESSAGE_RECEIVED);
        newMessageFilter.addAction(CHAT_MESSAGE_RECEIVED_SELF_UPDATE);
        newMessageFilter.addAction(CHAT_MESSAGE_USER_TYPING);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mNewMessageBroadcast, newMessageFilter);

        IntentFilter offlineIntentFilter = new IntentFilter();
        offlineIntentFilter.addAction(ROMAING_MESSAGE_RECEIVED);
        offlineIntentFilter.addAction(BATCH_MESSAGES_RECEIVED);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mOfflineMessageBroadcast, offlineIntentFilter);


        IntentFilter refreshViewIntentFilter = new IntentFilter();
        refreshViewIntentFilter.addAction(ACTION_CLEAR_MESSAGE_LIST);
        refreshViewIntentFilter.addAction(REFRESH_MESSAGE_LIST);
        refreshViewIntentFilter.addAction(UNDO_MESSAGE_SEND_SUCCESSFULLY);
        refreshViewIntentFilter.addAction(REMOVE_MESSAGE_SUCCESSFULLY);
        refreshViewIntentFilter.addAction(ACTION_REFRESH_SEND_BTN_STATUS);
        refreshViewIntentFilter.addAction(DELETE_MESSAGES);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mRefreshViewBroadcastReceiver, refreshViewIntentFilter);

        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mPlayingNextBroadcastReceiver, new IntentFilter(PLAYING_NEXT_VOICE));

        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mSessionInvalidBroadcast, new IntentFilter(DiscussionHelper.SESSION_INVALID));

        IntentFilter sideIntentFilter = new IntentFilter();
        sideIntentFilter.addAction(USER_CHANGED);
        sideIntentFilter.addAction(SESSION_CHANGED);
        sideIntentFilter.addAction(ACTION_FINISH);
        sideIntentFilter.addAction(ACTION_DO_NOT_CHECK_SESSION);
        sideIntentFilter.addAction(ACTION_CLEAR_AT_DATA);
        sideIntentFilter.addAction(ACTION_EMERGENCY_MESSAGE_CONFIRMED);
        sideIntentFilter.addAction(SelectToHandleActionService.ACTION_SEND_SUCCESSFULLY);

        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mSideBroadcast, sideIntentFilter);

        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mReceiverContactNotifyMsg, new IntentFilter(NOTIFY_MSG));

        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mAddSendingListenerBroadcastReceiver, new IntentFilter(ADD_SENDING_LISTENER));

        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mReceiverPopWindowNotifyMsg, new IntentFilter(TRANSPARENT_POP));
    }


    private void initData(Bundle savedInstanceState) {

        mKeyboardInputHeight = getKeyBoardHeight();

        mProgressDialogHelper = new ProgressDialogHelper(mActivity);

        mToFixedMessageId = getArguments().getString(ChatDetailActivity.TO_FIXED_MESSAGE_ID);

        mNeedSessionLegalCheck = getArguments().getBoolean(ChatDetailActivity.SESSION_LEGAL_CHECK, true);

        String sessionIdentifier = getArguments().getString(ChatDetailActivity.IDENTIFIER);

        mLoginUser = AtworkApplicationLike.getLoginUserSync();

        mSession = ChatSessionDataWrap.getInstance().getSession(sessionIdentifier, null);

        //querySessionSettingTranslationLanguage();
        if (mSession != null) {
            startSession(savedInstanceState);

        } else {
            tryMakeSession(sessionIdentifier);
            if (mSession == null) {
                startActivity(MainActivity.getMainActivityIntent(getActivity(), false));
                getActivity().finish();
                return;
            }
            startSession(savedInstanceState);
        }

        syncConversationSettings();


        mPopChatDetailDataHoldingView.initCurrentSession(mSession);
        mPopChatDetailDataHoldingView.doRefreshDataHolding();

        mDiscussionFeaturesInChatDetailAdapter = new DiscussionFeaturesInChatDetailAdapter(mDiscussionFeatureList);

        mRvDiscussionEntries.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mRvDiscussionEntries.setAdapter(mDiscussionFeaturesInChatDetailAdapter);

        if (canChat()) {

            if (isLegalUserChat()) {

                mIvTitleArrow.setVisibility(VISIBLE);

//                showPopChatDetailFunctionAreaView();


            }

        } else {
            mChatDetailInputView.setVisibility(GONE);
            mIvChatInfo.setVisibility(GONE);
        }

        //如果是服务号聊天，开启服务号菜单模式, 如果是通知类型, 隐藏相关组件
//        if (null != mSession) {
//            if (!SessionType.Service.equals(mSession.type) && !SessionType.LightApp.equals(mSession.type)) {
//
//                if (SessionType.Notice.equals(mSession.type)) {
//                    mChatDetailInputView.setVisibility(View.GONE);
//                    mIvChatInfo.setVisibility(View.GONE);
//                }
//            }
//        }

        mChatDetailInputView.setFragment(this);

        //单聊里检查是否有需要补发的 remove ack
        if (null != mSession) {
            if (isUserChat()) {
                ChatService.checkSendRemovedReceipts(getActivity());
            }
        }


        ChatDaoService.getInstance().queryImageMessagesInLimitAndCheckExpired(mActivity, mSession.identifier, 200, (chatPostMessageList, systemMessageTipList) -> {
            mMessageImageTypeListLoadSilently = new Vector<>(chatPostMessageList);

        });


    }

    private boolean mPopChatDetailFunctionAreaViewHiding = false;

    private void showPopChatDetailFunctionAreaView() {

        mPopChatDetailFunctionAreaView.clearAnimation();

        mPopChatDetailFunctionAreaView.setVisibility(VISIBLE);

        TranslateAnimation animate = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);                // toYDelta

        animate.setDuration(500);
        animate.setFillAfter(true);

        mPopChatDetailFunctionAreaView.startAnimation(animate);


        mIvTitleArrow.clearAnimation();
        mIvTitleArrow.animate().rotation(90).setDuration(500);

    }


    private void hidePopChatDetailFunctionAreaView() {
        if (mPopChatDetailFunctionAreaViewHiding) {
            return;
        }

        mPopChatDetailFunctionAreaViewHiding = true;
        mPopChatDetailFunctionAreaView.clearAnimation();


        TranslateAnimation animate = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f);                // toYDelta

        animate.setDuration(500);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mPopChatDetailFunctionAreaView.clearAnimation();
                mPopChatDetailFunctionAreaView.setVisibility(GONE);

                mPopChatDetailFunctionAreaViewHiding = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mPopChatDetailFunctionAreaView.startAnimation(animate);

        mIvTitleArrow.clearAnimation();
        mIvTitleArrow.animate().rotation(0).setDuration(500);
    }

    private void tryMakeSession(String from) {
        Bundle bundle = getArguments();
        String type = bundle.getString("type");
        String displayName = bundle.getString("display_name");
        String displayAvatar = bundle.getString("display_avatar");
        if (TextUtils.isEmpty(from)) {
            return;
        }
        SessionType sessionType = "discussion".equalsIgnoreCase(type) ? SessionType.Discussion : SessionType.User;
        EntrySessionRequest request = EntrySessionRequest.newRequest().setDomainId(AtworkConfig.DOMAIN_ID).setChatType(sessionType)
                .setName(displayName).setAvatar(displayAvatar)
                .setIdentifier(from);
        mSession = ChatSessionDataWrap.getInstance().entrySession(request);
    }

    private void startSession(Bundle savedInstanceState) {
        mChatDetailArrayListAdapter = new ChatDetailListAdapterV2((ArrayList<ChatPostMessage>) mWithTimeMessageList, savedInstanceState, mSession, mStrTranslationShortName);

        mChatDetailArrayListAdapter.setChatItemClickListener(chatItemClickListener);
        mChatDetailArrayListAdapter.setChatItemLongClickListener(chatItemLongClickListener);
        mChatDetailArrayListAdapter.setChatModeListener(this);
        mChatDetailArrayListAdapter.setReSendListener(this);

        mChatDetailArrayListAdapter.setHasStableIds(false);
//        ((SimpleItemAnimator)mChatListView.getItemAnimator()).setSupportsChangeAnimations(false);

        mLinearLayoutManager = new RecyclerViewNoBugLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        mLinearLayoutManager.setStackFromEnd(true);

        mChatListView.setLayoutManager(mLinearLayoutManager);
        mChatListView.setAdapter(mChatDetailArrayListAdapter);

        if (isAppChat()) {
            mIvChatInfo.setImageResource(R.mipmap.icon_setting_dark);
        } else if (isDiscussionChat()) {
            mIvChatInfo.setImageResource(R.mipmap.icon_discussion);
        } else {
            mIvChatInfo.setImageResource(R.mipmap.icon_chat_single);
        }

        //设置草稿信息
        if (!StringUtils.isEmpty(mSession.draft)) {
            mChatDetailInputView.getEmojiIconEditText().setText(mSession.draft);
            mChatDetailInputView.getEmojiIconEditText().setSelection(mChatDetailInputView.getEmojiIconEditText().getText().length());

            mChatDetailInputView.postDelayed(() -> showInput(), 500);
        }
        mUnreadTotalCount = mSession.getUnread();
        mUnreadMapInBeginUsingInFindUnRead.addAll(mSession.unreadMessageIdSet);
        mUnreadListInBegin.addAll(mSession.unreadMessageIdSet);

        if (StringUtils.isEmpty(mToFixedMessageId)) {
            loadInitMessage();
        } else {
            loadInitMessageForFixedMessageId();
        }

        //重置标题
        setTitle(mSession);

        updateSessionTypeStatus(true);

        mChatMoreView.configChatMoreItem(mSession.type, isCurrentFileTransfer());

        showWatermark();

        mPopChatDetailDataHoldingView.setOnPopVisibleListener(isVisible ->{
            if(!isVisible) {
                checkRvDiscussionEntriesVisible();
                return Unit.INSTANCE;

            }

            hideRvDiscussionEntriesLightLy();

            return Unit.INSTANCE;
        });
    }

    private void initTalkGuy() {
        if (isDiscussionChat()) {
            initDiscussionData();

        } else if (isAppChat()) {
            initAppData();

        } else if (isUserChat()) {
            initUserData();
        }
    }

    private boolean isAppChat() {
        return SessionType.Service.equals(mSession.type) || SessionType.LightApp.equals(mSession.type);
    }

    private boolean isUserChat() {
        return SessionType.User.equals(mSession.type);
    }


    private void initUserData() {

        if (shouldShowLeftConnerFunViewIfUser()) {
            mChatDetailInputView.showLeftConnerFunView();
        }

        refreshContactStarUIInPopAreaView();

        getTalkUser(getActivity(), mSession.identifier, mSession.mDomainId, new UserAsyncNetService.OnQueryUserListener() {
            @Override
            public void onSuccess(@NonNull User user) {

                checkUpdateSessionName(user);


                showWatermark();

                checkOnline(user);

                //检查更新用户信息
                checkUserRefreshFromRemote();

            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleTokenError(errorCode, errorMsg);

                Logger.e("user", "initUserData networkFail");
            }

        });


        getTalkEmployees(mSession.identifier, employeeList -> {

            refreshEmployeeCorrespondingView(employeeList);

            //检查更新雇员信息
            checkEmpRefreshFromRemote();
        });

    }

    private void refreshContactStarUIInPopAreaView() {
        if (StarUserListDataWrap.getInstance().containsKey(mSession.identifier)) {
            mPopChatDetailFunctionAreaView.getStarIvView().setImageResource(R.mipmap.chat_detail_pop_star_active);
        } else {
            mPopChatDetailFunctionAreaView.getStarIvView().setImageResource(R.mipmap.chat_detail_pop_star_inactive);

        }
    }

    private void checkUpdateSessionName(@NonNull User user) {
        if (!user.getShowName().equals(mSession.name)) {
            mSession.name = user.getShowName();
            updateSessionToDB();
        }
    }

    private boolean shouldShowLeftConnerFunViewIfUser() {

        return DomainSettingsManager.getInstance().handleEphemeronSettingsFeature()
                && !isCurrentFileTransfer();
    }


    private boolean shouldShowLeftConnerFunViewIfDiscussion() {

        return isDiscussionChat();
    }

    private void checkOnline(@NonNull User user) {
        if (User.isYou(AtworkApplicationLike.baseContext, user.mUserId)) {
            return;
        }


        OnlineManager.getInstance().checkOnline(mActivity, user.mUserId, onlineList -> {
            if (isAdded()) {
                if (ListUtil.isEmpty(onlineList)
                        && mUser.isStatusInitialized()
                        && !mPeerTyped) {
                    setTitle(user.getShowName() + getString(R.string.tip_not_online));
                }
            }
        });
    }


    private void checkUserRefreshFromRemote() {
        if (!UserManager.getInstance().checkLegalRequestIdCheckCheckUserEmpInfoRemote(mSession.identifier)) {
            return;
        }

        UserManager.getInstance().asyncQueryUserInfoFromRemote(BaseApplicationLike.baseContext, mSession.identifier, mSession.mDomainId, new UserAsyncNetService.OnUserCallBackListener() {

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleTokenError(errorCode, errorMsg);
            }

            @Override
            public void onFetchUserDataSuccess(Object... object) {
                User user = (User) object[0];
                if (null != user) {
                    mUser = user;

                    checkUpdateSessionName(user);

                    UserDaoService.getInstance().insertUser(user);

                    dealWithContactViewHeader();

                    mIsLoadUserRefreshFromRemoteResultSuccess = true;
                    if (mIsLoadEmpListRemoteRequestSuccess && mIsLoadUserRefreshFromRemoteResultSuccess) {
                        UserManager.getInstance().addInterceptRequestIdCheckCheckUserEmpInfoRemote(mSession.identifier);
                    }
                }
            }
        });
    }

    private void checkEmpRefreshFromRemote() {
        if (!UserManager.getInstance().checkLegalRequestIdCheckCheckUserEmpInfoRemote(mSession.identifier)) {
            return;
        }

        EmployeeManager.getInstance().queryOrgAndEmpListRemote(BaseApplicationLike.baseContext, mSession.identifier, new EmployeeManager.QueryOrgAndEmpListListener() {
            @Override
            public void onSuccess(List<Organization> organizationListFromRemote, List<Employee> employeeListFromRemote) {


                mEmployeeList = employeeListFromRemote;
                refreshEmployeeCorrespondingView(employeeListFromRemote);


                mIsLoadEmpListRemoteRequestSuccess = true;

                if (mIsLoadEmpListRemoteRequestSuccess && mIsLoadUserRefreshFromRemoteResultSuccess) {
                    UserManager.getInstance().addInterceptRequestIdCheckCheckUserEmpInfoRemote(mSession.identifier);
                }
            }

            @Override
            public void onFail() {

            }
        });
    }


    private void refreshEmployeeCorrespondingView() {
        refreshEmployeeCorrespondingView(mEmployeeList);
    }

    private void refreshEmployeeCorrespondingView(List<Employee> employeeList) {
        if (null == mEmployeeList) {
            return;
        }

        refreshUserOrgPosition(employeeList);


        refreshUserPhone(employeeList);
    }

    private void refreshUserOrgPosition(List<Employee> employeeList) {

        if (isCurrentFileTransfer()) {
            return;
        }

        if (!AtworkConfig.CHAT_CONFIG.isChatDetailViewNeedOrgPosition()) {
            return;
        }


        if (!ListUtil.isEmpty(employeeList)) {
            Employee employee = employeeList.get(0);
            mTvOrgPosition.setText(employee.getJobTitleWithLast3OrgName());
            mTvOrgPosition.setVisibility(View.VISIBLE);
        }


    }

    private void refreshUserPhone(List<Employee> employeeList) {
        HashMap<String, DataSchema> userEmployeeMobileMap = EmployeeHelper.getMobileDataSchemaStringHashMap(employeeList);
        if (noNeedShowPhoneView(userEmployeeMobileMap)) {
//            mIvUserPhone.setVisibility(View.GONE);
            mPopChatDetailFunctionAreaView.getCallView().setVisibility(GONE);
        } else {
//            mIvUserPhone.setVisibility(View.VISIBLE);
            mPopChatDetailFunctionAreaView.getCallView().setVisibility(VISIBLE);

        }
    }


    private boolean noNeedShowPhoneView(HashMap<String, DataSchema> userEmployeeMobileMap) {
        return isCurrentFileTransfer() || MapUtil.isEmpty(userEmployeeMobileMap);
    }

    private boolean isCurrentFileTransfer() {
        return FileTransferService.INSTANCE.needVariation(mSession);
    }

    private void initDiscussionData() {

        if (shouldShowLeftConnerFunViewIfDiscussion()) {
            mChatDetailInputView.setIvLeftConnerFunIcon(R.mipmap.icon_at);
            mChatDetailInputView.showLeftConnerFunView();
        }

        DiscussionManager.getInstance().queryDiscussion(getActivity(), mSession.identifier, new DiscussionAsyncNetService.OnQueryDiscussionListener() {
            @Override
            public void onSuccess(@NonNull Discussion discussion) {
                if (!discussion.mName.equals(mSession.name)) {
                    mSession.name = discussion.mName;
                    setTitle(discussion.mName);
                    updateSessionToDB();
                }
                mDiscussion = discussion;


                mTvDiscussionSize.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        mTitleView.setMaxWidth(ScreenUtils.getScreenWidth(BaseApplicationLike.baseContext) - mIvBack.getWidth() - mIvChatInfo.getWidth() - ViewUtil.getTextLength(mTvDiscussionSize) - DensityUtil.dip2px(30));
                        mTvDiscussionSize.getViewTreeObserver().removeOnPreDrawListener(this);
                        return false;

                    }
                });

                mTvDiscussionSize.setText("(" + discussion.mMemberList.size() + ")");
                mTvDiscussionSize.setVisibility(View.VISIBLE);

                showWatermark();


                queryDiscussionBasicInfoRemote();
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleTokenError(errorCode, errorMsg);

            }
        });
    }


    private void initAppData() {

        AppManager.getInstance().queryApp(mActivity, mSession.identifier, mSession.orgId, new AppManager.GetAppFromMultiListener() {
            @Override
            public void onSuccess(@NonNull App app) {
                mApp = app;
                mOrgId = app.mOrgId;
                mSession.orgId = mOrgId;

                String appTitleI18n = app.getTitleI18n(BaseApplicationLike.baseContext);
                if (!appTitleI18n.equals(mSession.name)) {
                    mSession.name = appTitleI18n;
                    setTitle(appTitleI18n);
                    updateSessionToDB();
                }


                mServiceMenuView.setApp(mApp);
                mServiceMenuView.setUser(mLoginUser);


                showWatermark();
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleTokenError(errorCode, errorMsg);

                Logger.e("app", "initAppData networkFail");

            }
        });


        AppManager.getInstance().queryServiceMenu(getActivity(), mSession.identifier, serviceMenusLocal -> {
            if (serviceMenusLocal.size() == 0) {
                inputMode();
                return;
            }
            ChatDetailFragment.this.mServiceMenus = serviceMenusLocal;
            mChatDetailInputView.serviceMode();
            mServiceMenuView.refreshMenu(serviceMenusLocal);
            mServiceMenuView.setSession(mSession);
            if (StringUtils.isEmpty(mSession.draft)) {
                serviceMenuMode();
            }


        });
    }

    private void showRvDiscussionEntries(List<DiscussionFeature> discussionFeatures) {
        mViewBottomPopLongLive = R.id.rv_discussion_entries;

        if (10 < discussionFeatures.size()) {
            discussionFeatures = discussionFeatures.subList(0, 10);
            DiscussionFeature moreFeature = new DiscussionFeature();
            moreFeature.setId(DiscussionFeature.ID_MORE);
            moreFeature.setFeature(DiscussionFeature.ENTRY_LIST);
            discussionFeatures.add(moreFeature);
        }


        mDiscussionFeatureList.clear();
        mDiscussionFeatureList.addAll(discussionFeatures);
        mDiscussionFeaturesInChatDetailAdapter.notifyDataSetChanged();
        showRvDiscussionEntriesLightly();
    }

    private void hideRvDiscussionEntries() {
        mDiscussionFeatureList.clear();
        mDiscussionFeaturesInChatDetailAdapter.notifyDataSetChanged();

        hideRvDiscussionEntriesLightLy();
    }

    private void hideRvDiscussionEntriesLightLy() {

        mRvDiscussionEntries.setVisibility(GONE);
    }

    private void showRvDiscussionEntriesLightly() {
        if(View.VISIBLE == mPopChatDetailDataHoldingView.getVisibility()) {
            return;
        }

        mRvDiscussionEntries.setVisibility(VISIBLE);

    }

    private void checkRvDiscussionEntriesVisible() {
        if (-1 == mViewBottomPopLongLive) {
            hideRvDiscussionEntriesLightLy();
            return;
        }

        if (R.id.rv_discussion_entries == mViewBottomPopLongLive) {
            showRvDiscussionEntriesLightly();

        }
    }


    private void receiveOneMsg(ChatPostMessage message) {
        List<ChatPostMessage> messageList = new ArrayList<>();
        messageList.add(message);
        batchReceivedMsg(NEW_MSG_FROM_OTHER_USER, messageList);

//        just for test
//        showUserTyping();
    }

    private void batchReceivedMsg(int newMsgType, List<ChatPostMessage> messageList) {

        synchronized (mRefreshListLock) {
            boolean shouldRefresh = false;
            List<ChatPostMessage> receiptList = new ArrayList<>();
            for (ChatPostMessage message : messageList) {
                if (ChatMessageHelper.getChatUser(message).mUserId.equalsIgnoreCase(mSession.identifier)) {
                    if (!mMessageList.contains(message)) {
                        mMessageList.add(message);

                        if (NEW_MSG_FROM_ROAMING != newMsgType) {
                            shouldRefresh = true;
                            receiptList.add(message);
                            addMsgToWithTimeList(message);


                            if (message.isDiscussionAtAllNeedNotify()) {
                                addDiscussionNotification(message);
                                refreshDiscussionNotifyUnreadTipView();
                            }
                        }

                        if (message.isEmergencyUnconfirmed()) {
                            mEmergencyMessageUnconfirmedList.add(message);
                            refreshEmergencyUnConfirmedTipView();
                        }


                    } else {
                        //重复提醒的群红包, 若已经存在聊天列表, 则只需要更新时间戳, 让其刷新位置
                        int index = mMessageList.indexOf(message);
                        ChatPostMessage chatPostMessage = mMessageList.get(index);
                    }
                }

            }

            if (NEW_MSG_FROM_ROAMING == newMsgType) {
                shouldRefresh = true;
                receiptList.addAll(messageList);

                refreshWithTimeMsgListTotally();
            }

            if (shouldRefresh) {
                if (mIsVisible) {
                    sendReceipt(receiptList);
                }
                refreshView(newMsgType);
            }
        }

    }

    private void addDiscussionNotification(ChatPostMessage message) {
        if (!mDiscussionNotificationList.contains(message)) {
            mUnreadDiscussionNotificationList.add(0, message);
            mDiscussionNotificationList.add(message);
        }

    }

    /**
     * 初始时发送所有已读回执
     */
    private synchronized void checkSendReadMessageReceipt() {
        sendReceipt(mMessageList);
    }


    private void sendReceipt(final List<ChatPostMessage> messageList) {
        mMsgThreadService.submit(() -> ChatService.sendReceiptInSync(mActivity, mSession, messageList));
    }


    public List<ChatPostMessage> getImageTypeMessage() {
        List<ChatPostMessage> msgListUsing = new ArrayList<>();

        msgListUsing.addAll(filterImageTypeMessages(mMessageList));

        if (null != mMessageImageTypeListLoadSilently) {

            List<ChatPostMessage> imageTypeMessagesLoadSilently = filterImageTypeMessages(mMessageImageTypeListLoadSilently);

            for (ChatPostMessage chatPostMessage : imageTypeMessagesLoadSilently) {
                if (!msgListUsing.contains(chatPostMessage)) {
                    msgListUsing.add(chatPostMessage);
                }
            }


        }

        return msgListUsing;

    }

    private List<ChatPostMessage> filterImageTypeMessages(List<ChatPostMessage> messageList) {
        List<ChatPostMessage> msgListUsing = new ArrayList<>();

        for (ChatPostMessage message : messageList) {
            if (message.isBurn() || message.isUndo()) {
                continue;
            }

            if (message instanceof ImageChatMessage || message instanceof MicroVideoChatMessage || message instanceof FileTransferChatMessage || message instanceof AnnoImageChatMessage) {
                if (message instanceof FileTransferChatMessage) {
                    FileTransferChatMessage fileTransferChatMessage = (FileTransferChatMessage) message;
                    if (fileTransferChatMessage.isGifType() || fileTransferChatMessage.isStaticImgType()) {
                        msgListUsing.add(message);
                    }
                    continue;
                }


                if (message instanceof AnnoImageChatMessage) {
                    AnnoImageChatMessage annoImageChatMessage = (AnnoImageChatMessage) message;
                    msgListUsing.addAll((annoImageChatMessage.getImageContentInfoMessages()));
                    continue;
                }

                msgListUsing.add(message);
            }

        }

        return msgListUsing;
    }


    /**
     * 获取消息中的所有图片消息
     */
    private void refreshImageChatMessageList() {
        ImageSwitchInChatActivity.sImageChatMessageList.clear();

        ImageSwitchInChatActivity.sImageChatMessageList.addAll(getImageTypeMessage());

        Collections.sort(ImageSwitchInChatActivity.sImageChatMessageList);
    }


    /**
     * 清除SESSION未读消息数
     */
    private void sessionUnreadClear() {

        //进入后，所有未读消息标记为已读
        mSession.clearUnread();
        IntentUtil.setBadge(mActivity);

        if (mSession.savedToDb) {
            updateSessionToDB();
        }
    }


    /**
     * 是否能够聊天, 若不能, 需要隐藏相关组件
     */
    private boolean canChat() {
        if (null == mSession) {
            return true;
        }

        if (SessionType.Notice.equals(mSession.type)) {
            return false;
        }

        if (SessionType.Service.equals(mSession.type)) {

            if (!AtworkConfig.APP_CONFIG.getCanChatInServiceApp()) {
                return false;
            }

        }

        return true;

    }

    private boolean mNeedLoadingAvatarHolder = true;

    private void dealWithContactViewHeader() {

        if (isCurrentFileTransfer()) {
            return;
        }

        boolean hasSet = false;

        if (isUserChat()) {
            User talkUser;
            if (null != mUser) {
                talkUser = mUser;

            } else {
                talkUser = UserManager.getInstance().queryUserInSyncByUserId(getActivity(), mSession.identifier, mSession.mDomainId);
            }

            if (null == talkUser) {
                return;
            }


            if (!talkUser.isStatusInitialized()) {
                mTvTipViewNotInitialize.setVisibility(View.VISIBLE);

            } else {
                mTvTipViewNotInitialize.setVisibility(GONE);

                if (DomainSettingsManager.getInstance().handleUserOnlineFeature()
                        && !OnlineManager.getInstance().isOnline(mSession.identifier)
                        && !mPeerTyped) {
                    setTitle(mSession.name + getString(R.string.tip_not_online));
                    hasSet = true;
                }

                int dp30 = DensityUtil.dip2px(30);
                ViewUtil.setSize(mIvChatInfo, dp30, dp30);

                AvatarHelper.setUserAvatarByAvaId(talkUser.mAvatar, mIvChatInfo, mNeedLoadingAvatarHolder, true);
                mNeedLoadingAvatarHolder = false;
            }


            setPersonalInfoView(talkUser);

        }

        if (!hasSet) {
            setTitle(mSession);
        }


    }


    private void setPersonalInfoView(User talkUser) {
        if (DomainSettingsManager.getInstance().handlePersonalSignatureEnable() && !StringUtils.isEmpty(talkUser.getSignature())) {
            mTvPersonalInfo.setText(talkUser.getSignature());
            mTvPersonalInfo.setVisibility(View.VISIBLE);
        } else {
            mTvPersonalInfo.setVisibility(GONE);
        }
    }


    private void dealWithContactInitializeStatusViewTip() {
        if (isUserChat()) {
            User talkUser = UserManager.getInstance().queryUserInSyncByUserId(getActivity(), mSession.identifier, mSession.mDomainId);

            if (null == talkUser) {
                return;
            }

            if (!talkUser.isStatusInitialized()) {

                if (!havingInitializeTipChatMsgInList()) {
                    SystemChatMessage systemChatMessage = new SystemChatMessage(getString(R.string.chat_detail_tip_account_is_not_initialized, mSession.name), SystemChatMessage.Type.NOT_INITIALIZE_TIP);
                    //未激活的提示语tip 总在聊天列表的最前面
                    systemChatMessage.deliveryTime = -1;

                    mWithTimeMessageList.add(0, systemChatMessage);
                }

            } else {
                dealWithContactInitializedViewTip();
            }

        }
    }

    private void dealWithContactInitializedViewTip() {
        if (havingInitializeTipChatMsgInList()) {
            ChatPostMessage chatPostMessage = mWithTimeMessageList.get(0);
            mWithTimeMessageList.remove(chatPostMessage);
            refreshListAdapter();
        }
    }

    private boolean havingInitializeTipChatMsgInList() {
        if (ListUtil.isEmpty(mWithTimeMessageList)) {
            return false;
        }

        ChatPostMessage chatPostMessage = mWithTimeMessageList.get(0);
        if (chatPostMessage instanceof SystemChatMessage) {
            SystemChatMessage systemChatMessage = (SystemChatMessage) chatPostMessage;

            return SystemChatMessage.Type.NOT_INITIALIZE_TIP == systemChatMessage.type;
        }

        return false;

    }

    private void handleAudioMeetingUI() {
        VoipMeetingDaoService.getInstance().queryVoipConfById(mSession.identifier, new VoipMeetingDaoService.onVoipConfOptListener() {
            @Override
            public void onOptSuccess(Object... objects) {
                if (objects[0] == null) {
                    return;
                }
                mVoipConference = (VoipMeetingGroup) objects[0];
                mJoinAudioMeeting.setVisibility(View.VISIBLE);
            }

            @Override
            public void onOptFail() {
                mVoipConference = null;
                mJoinAudioMeeting.setVisibility(GONE);
            }
        });
    }

    private void handlerUnreadUi() {
        new Handler().postDelayed(() -> {
            calcuUnread(true);


            checkSendReadMessageReceipt();

            handleEmergencyUnconfirmedTipView();

            handleDiscussionNotifyUnreadTipView();


        }, 500);

    }

    /**
     * 显示出"还有x 条未确认的紧急呼", 暂时此需求不需要
     */
    private void handleEmergencyUnconfirmedTipView() {
        if (Session.ShowType.Emergency == mSession.lastMessageShowType) {
            EmergencyMessageConfirmHelper.queryUnConfirmedList(mSession.identifier, msgList -> {
                if (!ListUtil.isEmpty(msgList)) {
                    mEmergencyMessageUnconfirmedList.addAll(msgList);
                }


                refreshEmergencyUnConfirmedTipView();

            });
        }
    }

    private void refreshEmergencyUnConfirmedTipView() {


        if (!ListUtil.isEmpty(mEmergencyMessageUnconfirmedList)) {
            //暂时需求不需要弹出该提示框
            mViewUnreadMsgTip.setVisibility(GONE);
            mViewUnreadMsgTip.setText(mEmergencyMessageUnconfirmedList.size() + getResources().getString(R.string.emergency_message_unconfirmed_tip));

        } else {
            mViewUnreadMsgTip.setVisibility(GONE);

        }
    }

    private void handlerUnreadOrAtMsgUi() {
        if (Session.ShowType.At.equals(mSession.lastMessageShowType)) {
            new Handler().postDelayed(() -> {
                calcuUnread(false);
//                    mUnreadTotalCount = 0;
                toAtPosition();
                checkSendReadMessageReceipt();
            }, 500);
        } else {
            handlerUnreadUi();
        }

    }


    private boolean isStillHavingUnread() {
        return mUnreadTotalCount > mReadCount;
    }


    private void toAtPosition() {
        for (ChatPostMessage chatPostMessage : mWithTimeMessageList) {
            if (chatPostMessage instanceof IAtContactMessage && ((IAtContactMessage) chatPostMessage).containAtMe(mActivity)
                    && ReadStatus.Unread.equals(chatPostMessage.read)
                    && ((TextChatMessage) chatPostMessage).chatSendType.equals(ChatSendType.RECEIVER)) {
                int position = mWithTimeMessageList.indexOf(chatPostMessage);


                setSelection(position);

                mSession.lastMessageShowType = Session.ShowType.Text;
                updateSessionToDB();
                return;
            }
        }
    }

    private void setSelection(int position) {
        mLinearLayoutManager.scrollToPositionWithOffset(position, 0);
    }

    private void addChatMessage(final ChatPostMessage chatPostMessage) {

        synchronized (mRefreshListLock) {
            mMessageList.add(chatPostMessage);
            addMsgToWithTimeList(chatPostMessage);

        }

    }

    public void refreshViewToPosition(final ChatPostMessage preFirstChatPostMessage) {
        synchronized (mRefreshUILock) {

            mChatDetailArrayListAdapter.setMessages(mWithTimeMessageList);
//            refreshListAdapter();
            int position = mWithTimeMessageList.indexOf(preFirstChatPostMessage);


            View firstChildView = mChatDetailArrayListAdapter.getViewByPosition(0, R.id.ll_root);
            int offset = 0;
            if (null != firstChildView) {
                offset = firstChildView.getHeight();
            }


            mChatDetailArrayListAdapter.notifyItemRangeInserted(0, position - 1);

            ChatPostMessage prepreFirstChatPostMessage = CollectionsKt.getOrNull(mWithTimeMessageList, position - 1);
            if (null != prepreFirstChatPostMessage && prepreFirstChatPostMessage.from.equals(preFirstChatPostMessage.from)) {
                mChatDetailArrayListAdapter.notifyItemChanged(position);
            }

//            mLinearLayoutManager.scrollToPositionWithOffset(position, offset);
//            scrollTo(position);

//        if (mSession.unread > 0) {
//            mTvViewNewMessageTip.setVisibility(View.VISIBLE);
//        }
        }

    }

    /**
     * @see #refreshView(int)
     */
    public void refreshView() {
        refreshView(NEW_MSG_FROM_OWN_SEND);
    }

    /**
     * refresh list view
     *
     * @param newMsgType 当前刷新是否因为来了新消息
     */
    public void refreshView(int newMsgType) {
        synchronized (mRefreshUILock) {
            mChatDetailArrayListAdapter.setMessages(mWithTimeMessageList);

            if (shouldShowNewMsgTip(newMsgType)) {
                //离线时, 即使在最底部也要不滑动到最低, 所以此处在离线的情况下无论如何都要偏移一点,
                //防止自动滚到到最低
//                if (NEW_MSG_FROM_OFFLINE == newMsgType) {
//                    mChatListView.scrollListBy(-1);
//                }
                refreshListAdapter();

                mTvViewNewMessageTip.setVisibility(VISIBLE);
                mIvJumpAnchor.setVisibility(GONE);

                //rollback scroll y
//                if (NEW_MSG_FROM_OFFLINE == newMsgType) {
//                    mChatListView.scrollListBy(1);
//                }

            } else {
                refreshListAdapter();


                setSelection(mChatDetailArrayListAdapter.getItemCount() - 1);

                hideBottomJumpBtnArea();

            }

            //根据新消息 来更新session，只有在单聊里才更新
            shouldRefreshSessionTitle(newMsgType);
        }


    }

    private void hideBottomJumpBtnArea() {
        mTvViewNewMessageTip.setVisibility(GONE);
        mIvJumpAnchor.setVisibility(GONE);
    }

    private boolean shouldShowNewMsgTip(int newMsgType) {
        if (!ListUtil.isEmpty(mWithTimeMessageList)) {
            ChatPostMessage lastReceivedMsg = findLatestLeftMsg();
            String currentUser = LoginUserInfo.getInstance().getLoginUserId(mActivity);

            if (NEW_MSG_FROM_OTHER_USER == newMsgType
                    || NEW_MSG_FROM_OFFLINE == newMsgType) {

                return !currentUser.equals(lastReceivedMsg.from)
                        && null != mChatListView.getChildAt(mChatListView.getChildCount() - 1)
                        && mChatListView.getHeight() < mChatListView.getChildAt(mChatListView.getChildCount() - 1).getBottom();

            }

        }

        return false;

    }

    @Deprecated
    private boolean shouldShowNewMsgTip_v1(int newMsgType) {
        if (!ListUtil.isEmpty(mWithTimeMessageList)) {
            ChatPostMessage lastReceivedMsg = findLatestLeftMsg();
            String currentUser = LoginUserInfo.getInstance().getLoginUserId(mActivity);

            if (NEW_MSG_FROM_OTHER_USER == newMsgType) {
                return !currentUser.equals(lastReceivedMsg.from)
                        && null != mChatListView.getChildAt(mChatListView.getChildCount() - 1)
                        && mChatListView.getHeight() < mChatListView.getChildAt(mChatListView.getChildCount() - 1).getBottom();

            } else if (NEW_MSG_FROM_OFFLINE == newMsgType) {
                return !currentUser.equals(lastReceivedMsg.from);
            }

        }

        return false;

    }

    private void shouldRefreshSessionTitle(int newMsgType) {
        boolean isCameNewMsg = NEW_MSG_FROM_OFFLINE == newMsgType || NEW_MSG_FROM_OTHER_USER == newMsgType;

        if (isCameNewMsg && isUserChat() && !ListUtil.isEmpty(mWithTimeMessageList)) {
            ChatPostMessage latestMsg = mWithTimeMessageList.get(mWithTimeMessageList.size() - 1);

            //排除自己的消息
            if (LoginUserInfo.getInstance().getLoginUserId(mActivity).equals(latestMsg.from)) {
                return;
            }

            User user = UserCache.getInstance().getUserCache(latestMsg.from);
            if (null != user && !mSession.name.equals(user.getShowName())) {
                mSession.name = user.getShowName();
                setTitle(mSession);
                updateSessionToDB();
            }

            mTvTipViewNotInitialize.setVisibility(GONE);
            dealWithContactInitializedViewTip();
        }
    }


    public void refreshListAdapter() {
        if (null != mChatDetailArrayListAdapter) {
            mChatDetailArrayListAdapter.notifyDataSetChanged();

            LogUtil.e("mChatDetailArrayListAdapter.notifyDataSetChanged");
        }

    }


    private void loadInitMessageForFixedMessageId() {
        clearMessageListData();
        ChatDaoService.getInstance().queryFixedMessageAndCheckExpired(mActivity, mSession.identifier, mToFixedMessageId, (chatPostMessageList, systemMessageTipList) -> {
            mMessageList = new Vector<>();
            mMessageJustShowList = new Vector<>();

            loadMessageCompleteWithoutReceipt(chatPostMessageList, systemMessageTipList);
            refreshView();

            sendWaitingForSendMessages();

            for (int i = 0; i < mWithTimeMessageList.size(); i++) {
                if (mWithTimeMessageList.get(i).deliveryId.equals(mToFixedMessageId)) {

                    setSelection(i);
                    break;
                }
            }

            handlerUnreadUi();

        });
    }


    private void loadInitMessage() {
        synchronized (mRefreshListLock) {

            clearMessageListData();
            List<ChatPostMessage> messages = MessageCache.getInstance().getFixSizeMessageCacheInChatView(mSession.identifier, INIT_LOAD_MESSAGES_COUNT);
            if (messages != null) {
                List<ChatPostMessage> systemMessageTipList = BurnModeHelper.checkMsgExpiredAndRemove(messages, true);

                mMessageList.addAll(messages);
                mMessageJustShowList.addAll(systemMessageTipList);

                refreshWithTimeMsgListTotally();
            }

            if (isNoMessageShow()) {

                loadInitMessageFromDb();

            } else {
                afterLoadInitMessage();
                //小余20条, 尝试去数据库补充进而展示
                if (20 > getOriginalMsgListSize()) {
                    loadInitMessageFromDb();
                }
            }

        }

    }

    private void loadInitMessageFromDb() {
        ChatDaoService.getInstance().queryLastMessageInLimitAndCheckExpired(mActivity, mSession.identifier, -1, INIT_LOAD_MESSAGES_COUNT, (chatPostMessageList, systemMessageTipList) -> {
            mMessageList = new Vector<>();
            mMessageJustShowList = new Vector<>();

            loadMessageCompleteWithoutReceipt(chatPostMessageList, systemMessageTipList);

            afterLoadInitMessage();

        });
    }

    private void afterLoadInitMessage() {
        sendWaitingForSendMessages();

        handlerUnreadUi();
        hasInitReadChatData = true;

        mReadCount += INIT_LOAD_MESSAGES_COUNT;

        refreshView();

        ChatService.calibrateExpiredMessages(mSession.identifier, result -> {
            mMsgClearedSuccessfullyAction = result;
        });
    }

    private boolean isNoMessageShow() {
        return ListUtil.isEmpty(mMessageList) && ListUtil.isEmpty(mMessageJustShowList);
    }

    /**
     * 寻找聊天列表里的聊天数据(ChatPostMessage)
     */
    public ChatPostMessage findMessageInChatView(String msgId) {
        if (!ListUtil.isEmpty(mMessageList)) {
            for (ChatPostMessage chatPostMessage : mMessageList) {
                if (msgId.equals(chatPostMessage.deliveryId)) {
                    return chatPostMessage;
                }
            }
        }

        return null;
    }

    private void sendWaitingForSendMessages() {
        List<ChatPostMessage> waitingForSendChatPostMessages = (List<ChatPostMessage>) getArguments().getSerializable(ChatDetailActivity.WAITING_FOR_SEND_MESSAGES);

        if (waitingForSendChatPostMessages != null) {
            sendWaitingForSendMessages(waitingForSendChatPostMessages);
        }
    }

    private void sendWaitingForSendMessages(List<ChatPostMessage> waitingForSendChatPostMessages) {
        for (ChatPostMessage chatPostMessage : waitingForSendChatPostMessages) {
            reSendMessage(chatPostMessage);
        }
    }


    /**
     * 保存获取到的离线消息
     *
     * @param chatPostMessageList
     */
    private void batchSaveHistoryMessages(List<ChatPostMessage> chatPostMessageList) {

        if (ListUtil.isEmpty(chatPostMessageList)) {
            return;
        }

        if (mSession != null && SessionType.Service.equals(mSession.type)) {
            ChatDaoService.getInstance().batchInsertAppMessages(chatPostMessageList, mSession);
        }

        ChatDaoService.getInstance().batchInsertMessages(chatPostMessageList, new ChatDaoService.BatchAddOrRemoveListener() {
            @Override
            public void addOrRemoveFail() {

                toast(R.string.batch_save_history_messages_fail);
            }

            @Override
            public void addOrRemoveSuccess() {
                if (BuildConfig.DEBUG) {
                    toast("批量更新历史消息成功");
                }
            }
        });
    }

    /**
     * 返回列表中接收到的最后条消息
     *
     * @return resultChatPostMessage
     */
    private ChatPostMessage findLatestLeftMsg() {
        int count = mWithTimeMessageList.size();
        ChatPostMessage resultChatPostMessage = null;
        for (int i = count - 1; i >= 0; i--) {
            ChatPostMessage chatPostMessage = mWithTimeMessageList.get(i);

            if (null != chatPostMessage && MessageChatViewBuild.isLeftView(chatPostMessage)) {
                resultChatPostMessage = chatPostMessage;
                break;
            }
        }

        return resultChatPostMessage;
    }


    @Nullable
    private ChatPostMessage findFirstUnreadMsg(int basePositionFound) {


        LogUtil.e(TAG, "  0 left mUnreadMapInBeginUsingInFindUnRead size -> " + mUnreadMapInBeginUsingInFindUnRead.size());


        ChatPostMessage firstUnreadChatPostMessage = null;
        for (int i = 0; i <= basePositionFound; i++) {
            ChatPostMessage chatPostMessage = mWithTimeMessageList.get(i);

            if (null == firstUnreadChatPostMessage
                    && mUnreadMapInBeginUsingInFindUnRead.contains(chatPostMessage.deliveryId)) {
                firstUnreadChatPostMessage = chatPostMessage;
            }


            mUnreadMapInBeginUsingInFindUnRead.remove(chatPostMessage.deliveryId);

        }


        LogUtil.e(TAG, "left mUnreadMapInBeginUsingInFindUnRead size -> " + mUnreadMapInBeginUsingInFindUnRead.size());

        return firstUnreadChatPostMessage;
    }

    @Deprecated
    private ChatPostMessage findFirstUnreadMsg2(int basePositionFound) {
        ChatPostMessage firstUnreadChatPostMessage;
        int readCount = -1;
        //第一次遇到已读的 voip(未成功)的消息的位置
        int readCountFirstMeetingAtVoip = -1;
        boolean hasEarnUnread = false;
        //按倒序找未读
        for (int i = basePositionFound; i >= 0; i--) {
            ChatPostMessage chatPostMessage = mWithTimeMessageList.get(i);

            //如果还没算到有未读的消息, 则只能拿左边的消息来判断
            if (hasEarnUnread || (null != chatPostMessage && MessageChatViewBuild.isLeftView(chatPostMessage))) {

                if (ReadStatus.LocalRead.equals(chatPostMessage.read)
                        || ReadStatus.AbsolutelyRead.equals(chatPostMessage.read)) {

                    //因为 voip 消息可能存在连续未读消息里, 突然插入已读的情况, 所以需要特殊处理
                    //例如自己手动拒绝的聊天会议, 是视为已读的
                    //todo 目前任存在 "这类型消息数量+未读消息数量 > 单次加载消息数量"时, 无法跳转未读精准的问题
                    if (chatPostMessage instanceof VoipChatMessage && MeetingStatus.FAILED == ((VoipChatMessage) chatPostMessage).mStatus && 0 != i) {
                        if (-1 == readCountFirstMeetingAtVoip) {
                            readCountFirstMeetingAtVoip = i;
                        }

                    } else {
                        if (-1 != readCountFirstMeetingAtVoip) {
                            readCount = readCountFirstMeetingAtVoip;

                        } else {
                            readCount = i;

                        }

                        break;
                    }

                } else {
                    readCountFirstMeetingAtVoip = -1;

                    if (chatPostMessage.isUndo()) {
                        continue;
                    }

                    //时间戳不能为未读数的起始值
                    if (chatPostMessage instanceof SystemChatMessage) {
                        if (SystemChatMessage.Type.TIME_SHOW == ((SystemChatMessage) chatPostMessage).type
                                && !hasEarnUnread) {
                            continue;
                        }
                    }


                    hasEarnUnread = true;
                }
            }

        }

        if (readCount == mWithTimeMessageList.size()) {
            return null;
        }

        try {
            firstUnreadChatPostMessage = mWithTimeMessageList.get(readCount + 1);
        } catch (Exception e) {
            firstUnreadChatPostMessage = null;
        }

        return firstUnreadChatPostMessage;
    }

    @Nullable
    private ChatPostMessage findUnderHistoryChatMessage() {
        if (isStillHavingUnread()) {
            return null;
        }

        return mFirstUnreadChatPostMessage;
    }


    private void refreshHistoryItemView() {
        boolean result = addHistoryItemView();
        if (result) {
            refreshListAdapter();
        }

    }

    private boolean addHistoryItemView() {
        if (null == mUnderHistoryChatMessage) {
            return false;
        }

        int underHistoryMsgPos = mWithTimeMessageList.indexOf(mUnderHistoryChatMessage);

        //检查历史分割线前是否含有时间戳, 有的话需要再把分割线挪到时间戳前面去
        int frontMsgPos = underHistoryMsgPos - 1;

        if (-1 < frontMsgPos) {
            ChatPostMessage postMessage = mWithTimeMessageList.get(frontMsgPos);
            if (postMessage instanceof SystemChatMessage) {
                SystemChatMessage timeSystemChatMessage = (SystemChatMessage) postMessage;
                if (SystemChatMessage.Type.TIME_SHOW == timeSystemChatMessage.type) {

                    if (0 < frontMsgPos) {
                        mWithTimeMessageList.add(frontMsgPos, new HistoryDivider());
                        return true;

                    }

                    return false;
                }

            }
        }


        if (0 < underHistoryMsgPos) {

            mWithTimeMessageList.add(underHistoryMsgPos, new HistoryDivider());
            return true;
        }

        return false;
    }

    private void refreshUnreadStatusInLoadMore(int basePositionFound) {
        if (mUnreadTotalCount > 0 && mUnreadTotalCount > getOriginalMsgListSize() - MORE_LOAD_MESSAGES_COUNT) {
            refreshUnreadStatus(basePositionFound);
        }
    }

    private void refreshUnreadStatus(int basePositionFound) {
        ChatPostMessage firstUnreadMsgFound = findFirstUnreadMsg(basePositionFound);
        if (null != firstUnreadMsgFound) {
            mFirstUnreadChatPostMessage = firstUnreadMsgFound;
        }


        if (null == mUnderHistoryChatMessage) {
            int canSeeNum = getCanSeeNum();

            if (mUnreadTotalCount > canSeeNum) {
                mUnderHistoryChatMessage = findUnderHistoryChatMessage();
                refreshHistoryItemView();
            }

        }
    }

    private int getCanSeeNum() {
        return mLinearLayoutManager.findLastVisibleItemPosition() - mLinearLayoutManager.findFirstVisibleItemPosition();
    }


    private void calcuUnread(boolean showUnreadTip) {
        if (ListUtil.isEmpty(mWithTimeMessageList)) {
            return;
        }
        synchronized (mRefreshListLock) {

            refreshUnreadStatus(mWithTimeMessageList.size() - 1);

            if (null == mFirstUnreadChatPostMessage) {
                return;
            }
            int firstUnreadPosition = mWithTimeMessageList.indexOf(mFirstUnreadChatPostMessage);


            mFirstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();


            if (Session.ShowType.Emergency != mSession.lastMessageShowType) {
                handleUnreadTipView(showUnreadTip, firstUnreadPosition);

            }
        }

    }

    private void handleDiscussionNotifyUnreadTipView() {
        if (SessionType.Discussion != mSession.type) {
            return;
        }

        ChatDaoService.getInstance().queryUnreadAtAllMessageList(BaseApplicationLike.baseContext, mSession.identifier, mUnreadListInBegin, chatPostMessageList -> {
//            List<ChatPostMessage> unreadAtAllMessageList = chatPostMessageList;
//            List<ChatPostMessage> canSeenMsgList = getCanSeenMsgList();
//            for(ChatPostMessage canSeeMsg : canSeenMsgList) {
//                unreadAtAllMessageList.remove(canSeeMsg);
//            }

            mUnreadDiscussionNotificationList.addAll(chatPostMessageList);
            mDiscussionNotificationList.addAll(chatPostMessageList);

            refreshDiscussionNotifyUnreadTipView();
        });
    }


    private void refreshDiscussionNotifyUnreadTipView() {
        if (0 < mUnreadDiscussionNotificationList.size()) {
            mViewUnreadDiscussionNotifyTip.setText(mUnreadDiscussionNotificationList.size() + getResources().getString(R.string.unread_discussion_notify_tip));
            mViewUnreadDiscussionNotifyTip.setVisibility(View.VISIBLE);

        } else {
            mViewUnreadDiscussionNotifyTip.setVisibility(GONE);

        }

    }

    private void handleUnreadTipView(boolean showUnreadTip, int firstUnreadPosition) {
        if (isAdded()) {
            Logger.e("chatdetialFragment", "mUnreadTotalCount : " + mUnreadTotalCount + " firstUnreadPosition : " + firstUnreadPosition + "  mFirstItem : " + mFirstVisibleItem);

            if (mUnreadTotalCount > 0 && mFirstVisibleItem > firstUnreadPosition && mUnreadTotalCount > getCanSeeNum()) {
                if (showUnreadTip) {
                    mViewUnreadMsgTip.setVisibility(View.VISIBLE);
                    mViewUnreadMsgTip.setText(mUnreadTotalCount + getResources().getString(R.string.unread_chat_msg_tip));
                }

            } else {
                mViewUnreadMsgTip.setVisibility(GONE);
            }
        }
    }


    /**
     * 点击 {@link #mViewUnreadMsgTip}时调用的方法, 跳转到最早的未读消息的位置(当批未读消息里, 不包括以前的未读)
     */
    private void jumpToHeadUnread() {
        synchronized (mRefreshListLock) {
            //清除全部群通知状态
//            mUnreadDiscussionNotificationList.clear();
            refreshDiscussionNotifyUnreadTipView();

            final int position = mWithTimeMessageList.indexOf(mFirstUnreadChatPostMessage);
            LogUtil.e("click unread view position: " + position);


            if (isStillHavingUnread()) {
                mViewUnreadMsgTip.setVisibility(GONE);

                showLoading();
                mProgressDialogHelper.show();

                mChatListView.postDelayed(() -> {
                    ChatPostMessage chatPostMessage = getFirstRealChatPostMessage();
                    if (null == chatPostMessage) {
                        return;
                    }

                    int limitCount = mUnreadTotalCount - mReadCount;
                    if (0 < limitCount) {

                        ChatDaoService.getInstance().queryMessagesBetween2PointsAndCheckExpired(mActivity, mSession.identifier, new ArrayList<>(mUnreadMapInBeginUsingInFindUnRead), chatPostMessage.deliveryId, chatPostMessage.deliveryTime, (chatPostMessageList, systemMessageTipList) -> {
                            if (isAdded()) {
                                mReadCount += limitCount;

                                mProgressDialogHelper.dismiss();
                                hiddenLoading();
                                loadMessageCompleteAndJumpToHead(chatPostMessageList, systemMessageTipList);
                                sendReceipt(chatPostMessageList);
                            }
                        });

                    }

                }, 0);
            } else {
                mChatListView.post(() -> {
                    if (position > 1) {
                        setSelection(position - 1);

                    } else { // position is 0
                        setSelection(position);

                    }
                    mViewUnreadMsgTip.setVisibility(GONE);
//                    mUnreadTotalCount = 0;
                });
            }
        }

    }

    /**
     * 点击 {@link #mViewUnreadDiscussionNotifyTip}时调用的方法, 跳转到上一个未读的群通知
     */
    private void jumpToHeadDiscussionNotify() {
        if (ListUtil.isEmpty(mUnreadDiscussionNotificationList)) {
            return;
        }


        synchronized (mRefreshListLock) {

            ChatPostMessage lastUnreadDiscussionNotifyMsg = mUnreadDiscussionNotificationList.get(0);
            final int position = mWithTimeMessageList.indexOf(lastUnreadDiscussionNotifyMsg);

            if (0 <= position) {

                mUnreadDiscussionNotificationList.remove(0);

                mChatListView.post(() -> {

                    if (position > 1) {
                        setSelection(position - 1);
                    } else { // position is 0
                        setSelection(position);

                    }

                    refreshDiscussionNotifyUnreadTipView();
                });


            } else {
                ChatPostMessage firstMsg = getFirstRealChatPostMessage();
                if (null == firstMsg) {
                    return;
                }

                showLoading();
                mProgressDialogHelper.show();

                mUnreadDiscussionNotificationList.remove(0);

                ChatDaoService.getInstance().queryMessagesBetween2PointsAndCheckExpired(mActivity, mSession.identifier, ListUtil.makeSingleList(lastUnreadDiscussionNotifyMsg.deliveryId), firstMsg.deliveryId, firstMsg.deliveryTime, (chatPostMessageList, systemMessageTipList) -> {
                    if (isAdded()) {

                        mReadCount += chatPostMessageList.size();

                        mProgressDialogHelper.dismiss();
                        hiddenLoading();
                        loadMessageCompleteAndJumpToHead(chatPostMessageList, systemMessageTipList);
                        sendReceipt(chatPostMessageList);


                        if (!isStillHavingUnread()) {
                            mViewUnreadMsgTip.setVisibility(GONE);
                        }

                        refreshDiscussionNotifyUnreadTipView();

                    }
                });


            }


        }

    }


    /**
     * 点击"引用消息"时调用的方法, 跳转指定被引用的消息
     */
    private void jumpTargetMessage(ReferenceMessage referenceMessage) {
        String sessionId = ChatMessageHelper.getChatUser(referenceMessage.mReferencingMessage).mUserId;
        if (!sessionId.equals(mSession.identifier)) {
            return;
        }


        synchronized (mRefreshListLock) {

            final int position = mWithTimeMessageList.indexOf(referenceMessage.mReferencingMessage);

            if (0 <= position) {


                mChatListView.post(() -> {
                    if (position > 1) {
                        setSelection(position - 1);
                    } else { // position is 0
                        setSelection(position);

                    }


//                    refreshDiscussionNotifyUnreadTipView();
                });
                mChatListView.postDelayed(() -> {
                    BasicChatItemView.addMsgIdNeedBlink(referenceMessage.mReferencingMessage.deliveryId);
                    refreshListAdapter();

                }, 300);


            } else {
                ChatDaoService.getInstance().existMessage(sessionId, referenceMessage.mReferencingMessage.deliveryId, result -> {
                    //本地 db 存在才做跳转
                    if (result) {
                        doJumpTargetMessage(referenceMessage);
                    }
                });


            }


        }


    }

    private void doJumpTargetMessage(ReferenceMessage referenceMessage) {
        ChatPostMessage firstMsg = getFirstRealChatPostMessage();
        if (null == firstMsg) {
            return;
        }

        showLoading();
        mProgressDialogHelper.show();


        ChatDaoService.getInstance().queryMessagesBetween2PointsAndCheckExpired(mActivity, mSession.identifier, referenceMessage.mReferencingMessage.deliveryTime + "", firstMsg.deliveryId, firstMsg.deliveryTime, (chatPostMessageList, systemMessageTipList) -> {
            if (isAdded()) {

                mReadCount += chatPostMessageList.size();

                mProgressDialogHelper.dismiss();
                hiddenLoading();
                loadMessageCompleteAndJumpToHead(chatPostMessageList, systemMessageTipList);
                sendReceipt(chatPostMessageList);


                if (!isStillHavingUnread()) {
                    mViewUnreadMsgTip.setVisibility(GONE);
                }

                mChatListView.postDelayed(() -> {
                    BasicChatItemView.addMsgIdNeedBlink(referenceMessage.mReferencingMessage.deliveryId);
                    refreshListAdapter();

                }, 1300);


//                        refreshDiscussionNotifyUnreadTipView();

            }
        });
    }


    private void jumpToHeadUnConfirmedEmergencyMessage() {
    }

    private void loadMessageCompleteAndJumpToHead(List<ChatPostMessage> chatPostMessageList, List<ChatPostMessage> msgJustShowList) {
        int oldWithTimeMsgSize = mWithTimeMessageList.size(); // for get the right firstUnread

        for (ChatPostMessage chatPostMessage : chatPostMessageList) {
            if (!mMessageList.contains(chatPostMessage)) {
                mMessageList.add(chatPostMessage);
            }
        }

        if (!ListUtil.isEmpty(msgJustShowList)) {
            for (ChatPostMessage chatPostMessage : msgJustShowList) {
                if (!mMessageJustShowList.contains(chatPostMessage)) {
                    mMessageJustShowList.add(chatPostMessage);
                }

            }
        }


        refreshWithTimeMsgListTotally();
        mChatDetailArrayListAdapter.setMessages((ArrayList<ChatPostMessage>) mWithTimeMessageList);

        refreshUnreadStatus(mWithTimeMessageList.size() - oldWithTimeMsgSize - 1);
        refreshListAdapter();


        setSelection(0);
    }


    private void loadMessageCompleteWithoutReceipt(List<ChatPostMessage> msgListLoadMore, @Nullable List<ChatPostMessage> msgJustShowList) {
        synchronized (mRefreshListLock) {

            for (ChatPostMessage chatPostMessage : msgListLoadMore) {
                if (!mMessageList.contains(chatPostMessage)) {
                    mMessageList.add(chatPostMessage);
                }
            }

            if (!ListUtil.isEmpty(msgJustShowList)) {
                for (ChatPostMessage chatPostMessage : msgJustShowList) {
                    if (!mMessageJustShowList.contains(chatPostMessage)) {
                        mMessageJustShowList.add(chatPostMessage);
                    }

                }
            }

            refreshWithTimeMsgListTotally();

//            if (firstChatPostMessage != null) {
//                refreshViewToPosition(firstChatPostMessage);
//            } else {
//                refreshView();
//            }

            MessageCache.getInstance().updateMessageList(mSession.identifier, mMessageList);
        }

    }

    /**
     * 设置标题
     *
     * @param title
     */
    private void setTitle(String title) {
        String title18N = AtworkUtil.getSessionNameI18N(mSession, title);
        mTitleView.setText(title18N);
    }

    private void setTitle(ShowListItem contact) {
        String title = getTitle(contact);
        setTitle(title);
    }

    private String getTitle(ShowListItem contact) {
        String title = FileTransferService.INSTANCE.getVariationName(contact);
        return title;
    }


    private void registerListener(final View view) {

        mVTitleBar.setOnClickListener(view1 -> {

            if (isLegalUserChat()) {

                if (mPopChatDetailFunctionAreaView.isShown()) {
                    hidePopChatDetailFunctionAreaView();
                } else {
                    showPopChatDetailFunctionAreaView();

                }
            }


        });


        mTvViewNewMessageTip.setOnClickListener(v -> {
            setSelection(mWithTimeMessageList.size() - 1);

            hideBottomJumpBtnArea();
        });


        mIvJumpAnchor.setOnClickListener(v -> {
            setSelection(mWithTimeMessageList.size() - 1);

            hideBottomJumpBtnArea();
        });


        //通过分发拦截事件前获取坐标
        mChatListView.setOnInterceptListener(event -> {
            mChatDetailInputView.showMoreImage();
            if (mFirstVisibleItem != 0) {
                return;
            }

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mLastClickDownY = event.getY();
            }
        });


        mSmartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            loadMoreMessages();
        });

        mChatListView.setOnTouchListener(new View.OnTouchListener() {

            long begin;

            long end;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mChatDetailInputView.showMoreImage();

                //规避长按头像@人需要弹出输入框
                if (!mNeedHandingKeyboard) {
                    hideAll();
                }

                tryHidePopChatDetailFunctionAreaView(-1);

                if (mFirstVisibleItem != 0) {
                    return false;
                }

                if (event.getAction() == MotionEvent.ACTION_UP && mLinearLayoutManager.findFirstVisibleItemPosition() <= 5) {

                    end = System.currentTimeMillis();
                    float nowY = event.getY();
                    if (nowY - mLastClickDownY > 60 && end - begin > 200) {

//                        loadMoreMessages();
                    }

                    Log.d("TIME::", (end - begin) + "");
                }

                return false;

            }
        });

        //点击未读消息提醒功能
        mViewUnreadMsgTip.setOnClickListener(v -> jumpToHeadUnread());


        mViewUnreadDiscussionNotifyTip.setOnClickListener(v -> jumpToHeadDiscussionNotify());

        //转发功能
        mForwardChatView.setOnClickListener(v -> {
            final List<ChatPostMessage> messages = getSelectedChat();
            if (messages.size() == 0) {
                AtworkToast.showToast(getResources().getString(R.string.not_select_any_chat_item));
                return;
            }

            forwardMsgPopUp(messages);

        });

        //删除消息事件
        mDeleteChatView.setOnClickListener(v -> {
            final List<ChatPostMessage> messages = getSelectedChat();
            if (messages.size() == 0) {
                AtworkToast.showToast(getResources().getString(R.string.not_select_any_chat_item));
                return;
            } else {

                final SelectDialogFragment selectDialogFragment = new SelectDialogFragment();
                selectDialogFragment.setMessage(getResources().getString(R.string.confirm_delete_messages)).setOnClickListener(() -> {
                    selectDialogFragment.dismiss();
                    deleteMessages(messages);
                    changeModel();
                });

                selectDialogFragment.show(getChildFragmentManager(), "SELECT_DIALOG");

            }
        });


        //表情符号删除事件
//        emojiconsFragment.setOnEmojiconBackspaceClickedListener(v -> EmojiconsFragment.backspace(mChatDetailInputView.getEmojiIconEditText()));
//
//        //表情符号点击事件
        emojiconsFragment.setOnEmojiconClickedListener(new EmojiconGridFragment.OnEmojiconClickedListener() {
            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                EmojiconsFragment.input(mChatDetailInputView.getEmojiIconEditText(), emojicon);
            }

            @Override
            public void onStickerClicked(String categoryId, StickerData stickerData) {
                if (TextUtils.isEmpty(stickerData.mId)) {
                    return;
                }
                ShowListItem chatItem = getSendChatItem();
                long deletionOnTime = -1;
                int readTime = -1;
                if (BurnModeHelper.isBurnMode()) {
                    deletionOnTime = DomainSettingsManager.getInstance().getDeletionOnTime();
                    readTime = DomainSettingsManager.getInstance().getEmphemeronReadTime(DomainSettingsManager.TextReadTimeWords.ImageRead);
                }
                newSendMessage(StickerChatMessage.Companion.newSendStickerMessage(getActivity(), categoryId, stickerData.mOriginName, stickerData.getShowName(mActivity), 120, 120, mSession.identifier,
                        mSession.mDomainId, mSession.type.getToType(), mOrgId, chatItem, BurnModeHelper.isBurnMode(), readTime, deletionOnTime, null));
            }
        });

        mChatListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int scrollState) {
                mChatDetailInputView.showMoreImage();
                final int currentFirstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
                final int lastVisiblePosition = mLinearLayoutManager.findLastVisibleItemPosition();

                tryHideViewUnreadUnreadMsgTip(currentFirstVisibleItem, lastVisiblePosition);


                if (scrollState == SCROLL_STATE_FLING && !mTvViewNewMessageTip.isShown()) {
                    mIvJumpAnchor.setVisibility(VISIBLE);
                }

//                tryHideViewUnreadDiscussionNotifyTip(currentFirstVisibleItem);


                ChatDetailFragment.this.mFirstVisibleItem = currentFirstVisibleItem;

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                if (6 > mLinearLayoutManager.findFirstVisibleItemPosition()
                        && mChatDetailArrayListAdapter.getItemCount() - 1 != mLinearLayoutManager.findLastVisibleItemPosition()) {
                    loadMoreMessages();
                }

                refreshTimePrintViewFloating(mLinearLayoutManager.findFirstVisibleItemPosition());
            }
        });


        //进入个人或群消息的信息区
        mIvChatInfo.setOnClickListener(v -> {
            hideInput();
            //跳转到聊天详情中
            Intent intent = ChatInfoActivity.getIntent(mSession);
            startActivityRouteBack(intent, true);

        });

        mPopChatDetailFunctionAreaView.getCallView().setOnClickListener(v -> {

            getTalkEmployees(mSession.identifier, employeeList -> {
                EmployeeHelper.callPhone(getActivity(), employeeList);
            });


        });


        mPopChatDetailFunctionAreaView.getMoreView().setOnClickListener(v -> {
            getTalkUser(getActivity(), mSession.identifier, mSession.mDomainId, new UserAsyncNetService.OnQueryUserListener() {
                @Override
                public void onSuccess(@NonNull User user) {
                    //显示个人详情
                    if (null != getActivity()) {
                        hideInput();
                        startActivity(PersonalInfoActivity.getIntent(BaseApplicationLike.baseContext, user));

                    }
                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    ErrorHandleUtil.handleError(errorCode, errorMsg);

                }
            });
        });

        mPopChatDetailFunctionAreaView.getStarView().setOnClickListener(v -> {
            if (StarUserListDataWrap.getInstance().containsKey(mSession.identifier)) {
                removeFavorContact();
                return;
            }

            checkCanChatAndStarContact();
        });

        //进入输入框模式
        mServiceMenuView.setToInputModelListener(() -> inputMode());

        //进入服务号菜单模式
        mChatDetailInputView.setToServiceModeListener(() -> {
            serviceMenuMode();
            hideInput();
        });

        mPopLinkTranslatingView.setOnShareLinkClickListener(articleItem -> {
            ChatPostMessage shareChatMessage = initShareChatMessage(articleItem, mSession.identifier, mSession.mDomainId, ShareChatMessage.ShareType.Link, null);
            newSendMessage(shareChatMessage);
            mPopLinkTranslatingView.nothing();
            mChatDetailInputView.clearEditText();
        });

        mChatDetailInputView.setLinkMatchListener(new LinkTranslatingHelper.LinkMatchListener() {
            @Override
            public void hidePopView() {
                mPopLinkTranslatingView.nothing();
            }

            @Override
            public void matching() {
                mPopLinkTranslatingView.translating();
            }

            @Override
            public void showMatchedResult(ArticleItem articleItem) {
                mPopLinkTranslatingView.showMatchedResult(articleItem);
            }

            @Override
            public void showNotMatch() {
                mPopLinkTranslatingView.showNotMatch();
            }

        });


        //INPUT输入框下的各种事件


        ChatDetailInputListener chatDetailInputListener = new ChatDetailInputListener() {

            @Override
            public void moreFunctionShow() {
                new Handler().postDelayed(() -> {
                    //这个处理会有问题，延迟处理，但是可能activity已经不存在了
                    if (getActivity() == null) {
                        return;
                    }
                    switchChatMoreView();
                }, 100);
            }

            /**
             * 隐藏更多功能框
             */
            @Override
            public void moreFunctionHidden() {
                showEdit();
            }

            @Override
            public void voiceShow() {
                showVoice();
            }

            @Override
            public void emoticonsShow() {
                showEmoji();
            }

            @Override
            public void inputShow() {

                showInput();
            }

            /**
             * 开始录音
             */
            @Override
            public void record() {

                Log.e("Audio", "record");

//                mActivity.getFragmentManager().executePendingTransactions();

//                if (mRecordDialogFragment.isAdded()) {
//                    Log.e("Audio", "dialog dismiss");
//                    mRecordDialogFragment.dismiss();
//                }

//                mChatVoiceView.dismissRecordingDialog();

//                mRecordDialogFragment.show(getFragmentManager(), "RECORD");

                audioRecord = new AudioRecord();


                setSelection(mChatDetailArrayListAdapter.getItemCount() - 1);

                AudioRecord.stopPlaying();
                audioRecord.setRecordListener(new AudioRecord.RecordListener() {
                    @Override
                    public void timeout() {
                        Log.e("Audio", "record time out");

                        mActivity.runOnUiThread(() -> {
//                            mRecordDialogFragment.dismiss();
                            mChatVoiceView.dismissRecordingDialog();
                            AtworkToast.showToast(getResources().getString(R.string.recored_timeout));
//                            ChatDetailInputView.mLastEndTime = TimeUtil.getCurrentTimeInMillis();
                        });


                    }

                    @Override
                    public void recordFinished(final String audioId, final int playtime) {
                        getActivity().runOnUiThread(() -> {
                            Log.e("audio", "send voice");
                            ShowListItem sendItem = getSendChatItem();
                            long deletionOnTime = -1;
                            int readTime = -1;
                            if (BurnModeHelper.isBurnMode()) {
                                deletionOnTime = DomainSettingsManager.getInstance().getDeletionOnTime();
                                readTime = DomainSettingsManager.getInstance().getEmphemeronReadTime(DomainSettingsManager.TextReadTimeWords.VoiceRead);
                            }

                            //发送一个语音消息
                            VoiceChatMessage voiceMessage = VoiceChatMessage.newSendVoiceMessage(mActivity, audioId, playtime, mLoginUser, mSession.identifier,
                                    ParticipantType.User, mSession.type.getToType(), mSession.mDomainId, BodyType.Voice, mOrgId, sendItem, BurnModeHelper.isBurnMode(),
                                    readTime, deletionOnTime);

                            newSendWithProgressMessage(MediaCenterNetManager.COMMON_FILE, VoiceChatMessage.getAudioPath(mActivity, voiceMessage.deliveryId), voiceMessage, false);
                        });

                    }

                    @Override
                    public void tooShort() {
                        LogUtil.e("Audio", "record too short");

                        toastOver(R.string.recording_voice_too_short);

                    }

                    @Override
                    public void recordFail() {
                        LogUtil.e("Audio", "record failed");
                        audioRecord.cancelAuthCheckSchedule();

                        getActivity().runOnUiThread(() -> {
                            String appName = getString(R.string.app_name);
//                            if (mRecordDialogFragment.isAdded()) {
//                                mRecordDialogFragment.dismiss();
//                            }

                            mChatVoiceView.dismissRecordingDialog();

                            audioRecord.cancelRecord();
//                            ChatDetailInputView.mLastEndTime = TimeUtil.getCurrentTimeInMillis();
                            new AtworkAlertDialog(getActivity(), AtworkAlertDialog.Type.SIMPLE)
                                    .setContent(getString(R.string.tip_record_fail_no_auth, appName))
                                    .hideBrightBtn()
                                    .setDeadBtnText(R.string.i_known)
                                    .show();
                        });


                    }

                });
                audioRecord.startRecord();


            }

            /**
             * 录音结束
             */
            @Override
            public void recordEnd() {

                Log.e("Audio", "record End");

                new Handler().postDelayed(() -> {
                    if (null != audioRecord) {
                        audioRecord.stopRecord();
                        mChatVoiceView.dismissRecordingDialog();
//                        if (mRecordDialogFragment.isAdded()) {
//                            mRecordDialogFragment.dismiss();
//
//                        }
                    }
                }, 200);

//                ChatDetailInputView.mLastEndTime = TimeUtil.getCurrentTimeInMillis();


            }

            @Override
            public void recordReadyCancel() {
//                mRecordDialogFragment.switchMode(RecordDialogFragment.Mode.CANCEL);
            }

            @Override
            public void recordNotCancel() {
//                mRecordDialogFragment.switchMode(RecordDialogFragment.Mode.RECORDING);
            }

            @Override
            public void voiceMode() {
                //进入语音模式，隐藏其它输入功能
                hideAll();
            }

            @Override
            public void clickLeftConnerFunView() {


                if (shouldShowLeftConnerFunViewIfDiscussion()) {
                    mChatDetailInputView.isAppendAtMembersDirectly = true;

                    inputAt();

//                    mChatDetailInputView.isAppendAtMembersDirectly = false;


                } else {
                    handleClickBurnMode();

                }
            }

            @Override
            public boolean getRecordViewStatus() {
                return true;
            }

            @Override
            public void recordCancel() {

                LogUtil.e("Audio", "record cancel");

                if (audioRecord == null) {
                    LogUtil.e("Audio", "audioRecord null");
                    return;
                }
                audioRecord.cancelRecord();
                audioRecord.cancelAuthCheckSchedule();
                new Handler().postDelayed(() -> {
//                    if (mRecordDialogFragment.isAdded()) {
//                        mRecordDialogFragment.dismiss();
//                    }

                    mChatVoiceView.dismissRecordingDialog();

                }, 200);
//                ChatDetailInputView.mLastEndTime = TimeUtil.getCurrentTimeInMillis();

/*                new Handler().postDelayed(() -> {
//                    audioRecord.stopRecord(mActivity);
                }, 200); */

            }

            @Override
            public void onSystemCancel() {
                LogUtil.e("Audio", "system cancel");
/*               if (mRecordDialogFragment != null && mRecordDialogFragment.isAdded()) {
                    new Handler().post(()->{});
                }*/
                new Handler().post(() -> {
//                    mRecordDialogFragment.dismissAllowingStateLoss();

                    mChatVoiceView.dismissRecordingDialog();

                });
                if (audioRecord == null) {
                    return;
                }
                audioRecord.cancelRecord();
                audioRecord.cancelAuthCheckSchedule();
            }

            //子线程
            @Override
            public void recordKill() {
                mActivity.runOnUiThread(() -> {
                    audioRecord.cancelRecord();
//                    mRecordDialogFragment.dismiss();
                    mChatVoiceView.dismissRecordingDialog();

//                    if (mRecordDialogFragment.isAdded()) {
//                        ChatDetailInputView.mLastEndTime = TimeUtil.getCurrentTimeInMillis();
//                    }
                });

            }

            @Override
            public void handleForcedSend() {
                if (mPopChatDetailDataHoldingView.fileDataHolding()) {
                    FileData fileDataHolding = mPopChatDetailDataHoldingView.getDataHolding().getFileData().get(0);
                    doSendMultiFiles(ListUtil.makeSingleList(fileDataHolding));

                    mPopChatDetailDataHoldingView.cancelDataHolding();
                    mChatDetailInputView.refreshSendBtnStatus();
                    return;
                }

                if (mPopChatDetailDataHoldingView.mediaDataHolding()) {
                    List<MediaItem> mediaDataHolding = mPopChatDetailDataHoldingView.getDataHolding().getMediaData();
                    mPopChatDetailDataHoldingView.cancelDataHolding();

                    if (null != mediaDataHolding && 1 == mediaDataHolding.size()) {
                        sendImgOnActivityResultForMediaDirectly(mediaDataHolding, mSelectOriginalMode);
                        mChatDetailInputView.refreshSendBtnStatus();
                        //reset
                        mSelectOriginalMode = false;
                        return;
                    }

                    sendImgOnActivityResultForMediaAssemble(null, mediaDataHolding, mSelectOriginalMode, StringUtils.EMPTY);
//                  //reset
                    mSelectOriginalMode = false;
                    return;
                }

            }

            @Override
            public boolean shouldForcedShowSend() {
                return mChatDetailInputView.isEmptyInput()
                        && (mPopChatDetailDataHoldingView.fileDataHolding() || mPopChatDetailDataHoldingView.mediaDataHolding());
            }

            /**
             * 发送一个文本消息
             * @param text
             */
            @Override
            public void sendText(String text) {

                if (mPopChatDetailDataHoldingView.messageReferencing()) {
                    ChatPostMessage messageReferencing = mPopChatDetailDataHoldingView.getDataHolding().getMessage();

                    mPopChatDetailDataHoldingView.cancelDataHolding();

                    //send referencing message
                    sendReferenceMessage(text, messageReferencing);

                    return;
                }

                if (mPopChatDetailDataHoldingView.fileDataHolding()) {
                    FileData fileDataHolding = mPopChatDetailDataHoldingView.getDataHolding().getFileData().get(0);
                    mPopChatDetailDataHoldingView.cancelDataHolding();


                    //send anno file
                    sendAnnoFileMessage(text, fileDataHolding);
                    return;
                }

                if (mPopChatDetailDataHoldingView.mediaDataHolding()) {
                    List<MediaItem> mediaDataHolding = mPopChatDetailDataHoldingView.getDataHolding().getMediaData();
                    mPopChatDetailDataHoldingView.cancelDataHolding();

                    sendImgOnActivityResultForMediaAssemble(null, mediaDataHolding, mSelectOriginalMode, text);
                    //reset
                    mSelectOriginalMode = false;

                    return;
                }


                doSendText(text);
            }

            private void doSendText(String text) {
                Context context = BaseApplicationLike.baseContext;

                ShowListItem sendItem = getSendChatItem();
                long readTime = -1;
                long deletionOnTime = -1;
                if (BurnModeHelper.isBurnMode()) {
                    int length = text.length();
                    if (0 < length && 15 >= length) {
                        readTime = DomainSettingsManager.getInstance().getEmphemeronReadTime(DomainSettingsManager.TextReadTimeWords.Words15);

                    } else if (15 < length && 30 >= length) {
                        readTime = DomainSettingsManager.getInstance().getEmphemeronReadTime(DomainSettingsManager.TextReadTimeWords.Words30);

                    } else if (30 < length && 100 >= length) {
                        readTime = DomainSettingsManager.getInstance().getEmphemeronReadTime(DomainSettingsManager.TextReadTimeWords.Words100);

                    } else {
                        readTime = DomainSettingsManager.getInstance().getEmphemeronReadTime(DomainSettingsManager.TextReadTimeWords.Words140);

                    }

                    deletionOnTime = DomainSettingsManager.getInstance().getDeletionOnTime();
                }

                if (mAtAll) {
                    text = AtworkUtil.getAtAllI18n(text);
                }

                TextChatMessage textMessage = TextChatMessage.newSendTextMessage(context, text, mSession.identifier,
                        mSession.mDomainId, mSession.type.getToType(), mOrgId, sendItem, BurnModeHelper.isBurnMode(), readTime, deletionOnTime, null);

                if (mAtContacts.size() > 0) {
                    textMessage.textType = TextChatMessage.AT;
                    if (mAtAll) {
                        textMessage.setAtAll(true);
                    } else {
                        textMessage.setAtUsers(mAtContacts);
                    }

                }
                newSendMessage(textMessage);
                mAtContacts.clear();
                mChatDetailInputView.clearEditText();
                mAtAll = false;
            }

            @Override
            public void inputAt() {
                if (isDiscussionChat()) {
                    mIsAtMode = true;

                    AtworkApplicationLike.getLoginUser(new UserAsyncNetService.OnQueryUserListener() {
                        @Override
                        public void onSuccess(@NonNull User user) {
                            List<User> singleList = new ArrayList<>();
                            singleList.add(user);

                            DiscussionMemberSelectControlAction discussionMemberSelectControlAction = new DiscussionMemberSelectControlAction();
                            discussionMemberSelectControlAction.setSelectedContacts(singleList);
                            discussionMemberSelectControlAction.setDiscussionId(mSession.identifier);
                            discussionMemberSelectControlAction.setSelectMode(DiscussionMemberSelectActivity.Mode.AT);

                            Intent intent = DiscussionMemberSelectActivity.getIntent(BaseApplicationLike.baseContext, discussionMemberSelectControlAction);
                            startActivityForResult(intent, GET_DISCUSSION_AT);
                        }

                        @Override
                        public void networkFail(int errorCode, String errorMsg) {
                            ErrorHandleUtil.handleBaseError(errorCode, errorMsg);

                        }
                    });


                }
            }

            @Override
            public void onEditTextEmpty() {
                mAtContacts.clear();
            }

            @Override
            public void onTyping() {
                trySendTypingStatus();
            }
        };

        mChatVoiceView.setChatDetailInputListener(chatDetailInputListener);
        mChatDetailInputView.setChatDetailInputListener(chatDetailInputListener);

        //捕获键盘事件
        mKeyboardRelativeLayout.setOnKeyboardChangeListener(state -> {
            if (state == KeyboardRelativeLayout.KEYBOARD_STATE_SHOW) {
                if (!mKeyboardShowing) {
                    showEdit();
                    mKeyboardShowing = true;
                }
            } else if (state == KeyboardRelativeLayout.KEYBOARD_STATE_HIDE) {

                mKeyboardShowing = false;

                mNeedHandingKeyboard = false;

            }

        });

        mKeyboardRelativeLayout.setOnKeyBoardHeightListener(height -> {
            CommonShareInfo.setKeyBoardHeight(getActivity(), height);
//            mKeyboardInputHeight = height;
        });

        //回退事件
        mIvBack.setOnClickListener(v -> backAction());

        //选取照片，拍照，选取文件等事件
        mChatMoreView.setChatMoreViewListener(new ChatMoreView.ChatMoreViewListener() {
            @Override
            public void cameraClick() {
                if (VoipHelper.isHandingVideoVoipCall()) {
                    AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);
                    return;
                }
                AndPermission
                        .with(mActivity)
                        .runtime()
                        .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                        .onGranted(data -> {
                            AndPermission
                                    .with(mActivity)
                                    .runtime()
                                    .permission(Permission.CAMERA)
                                    .onGranted(granted -> {
                                        if (!BaseApplicationLike.baseContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                                            AtworkToast.showToast(getResources().getString(R.string.CAN_NOT_FOUND_CAMERA));
                                            return;
                                        }
                                        mPhotoPath = IntentUtil.camera(ChatDetailFragment.this, GET_CAMERA);
                                        //界面切换效果
                                        getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                                    })
                                    .onDenied(denied -> AtworkUtil.popAuthSettingAlert(mActivity, Manifest.permission.CAMERA))
                                    .start();
                        })
                        .onDenied(data -> AtworkUtil.popAuthSettingAlert(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        .start();

            }

            @Override
            public void photoClick() {
                AndPermission
                        .with(mActivity)
                        .runtime()
                        .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                        .onGranted(data -> {
                            Intent getAlbum = MediaSelectActivity.getIntent(BaseApplicationLike.baseContext);
                            if (!BurnModeHelper.isBurnMode()) {
                                getAlbum.putExtra(MediaSelectActivity.DATA_SELECT_MEDIA_TYPE_ADD, (Serializable) ListUtil.makeSingleList(SelectMediaType.VIDEO));
                            }
                            getAlbum.putExtra(MediaSelectActivity.DATA_OPEN_FULL_MODE_SELECT, !BurnModeHelper.isBurnMode());
                            getAlbum.putExtra(MediaSelectActivity.DATA_FROM_VIEW, MediaSelectActivity.FROM_CHAT_DETAIL);
                            getAlbum.setType(IMAGE_TYPE);
                            startActivityForResult(getAlbum, GET_PHOTO);
                        })
                        .onDenied(data -> AtworkUtil.popAuthSettingAlert(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        .start();

            }

            @Override
            public void fileClick() {
                routeFile();

            }

            @Override
            public void microVideoClick() {
                routeMicroVideo();

            }

            @Override
            public void voipConfClick() {
                String orgCode = PersonalShareInfo.getInstance().getCurrentOrg(BaseApplicationLike.baseContext);
                OrganizationSettings organizationSetting = OrganizationSettingsManager.getInstance().getCurrentUserOrgSetting(orgCode);
                if(organizationSetting == null || !organizationSetting.isAgoraEnabled()){
                    ToastHelper.showShortToast(BaseApplicationLike.baseContext,"此功能未开启");
                    return;
                }
                routeVoip();

            }

            @Override
            public void meetingClick() {
                String orgCode = PersonalShareInfo.getInstance().getCurrentOrg(BaseApplicationLike.baseContext);
                OrganizationSettings organizationSetting = OrganizationSettingsManager.getInstance().getCurrentUserOrgSetting(orgCode);
                if(!organizationSetting.isZoomEnabled()){
                    ToastHelper.showShortToast(BaseApplicationLike.baseContext,"此功能未开启");
                    return;
                }

                routeMeeting();
            }

            @Override
            public void cardClick() {
                if (BurnModeHelper.isBurnMode()) {
                    return;
                }

                SelectedContactList.clear();

                UserSelectControlAction userSelectControlAction = new UserSelectControlAction();
                userSelectControlAction.setSelectMode(UserSelectActivity.SelectMode.SELECT);
                userSelectControlAction.setMax(1);
                userSelectControlAction.setNeedSetNotAllowList(false);

                Intent intent = UserSelectActivity.getIntent(mActivity, userSelectControlAction);
                startActivityForResult(intent, GET_CARD_USERLIST);
            }

            @Override
            public void locationClick() {
                routeLocation();
            }
        });

        mJoinAudioMeeting.setOnClickListener(v -> {
            if (mVoipConference == null) {
                return;
            }
//            ESpaceManager.getInstance().startConf(mActivity, ConferenceManagerActivity.class, String.valueOf(mVoipConference.mEventId),  LoginUserInfo.getInstance().getLoginUser(mActivity).mUsername, false);
        });

        mDiscussionFeaturesInChatDetailAdapter.setOnItemClickListener((adapter, itemView, position) -> {


            DiscussionFeature feature = mDiscussionFeatureList.get(position);
            routeDiscussionFeature(feature);
        });
    }

    private void routeDiscussionFeature(DiscussionFeature feature) {
        if (routeInChatView(feature)) return;

        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }

    }

    private void routeLocation() {
        requestLocationPermission(new OnLocationPermissionResult() {
            @Override
            public void onGrant() {
                startActivityForResult(ShareLocationActivity.Companion.getIntent(mActivity), SHARE_LOCATION_REQ_CODE);
            }

            @Override
            public void onReject(String permission) {
                if (isAdded()) {
                    final AtworkAlertDialog alertDialog = AtworkUtil.getAuthSettingAlert(getActivity(), permission);
                    alertDialog.setOnDismissListener(dialog -> {
                    });

                    alertDialog.show();
                }
            }
        });
    }

    private void routeFile() {
        AndPermission
                .with(mActivity)
                .runtime()
                .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                .onGranted(data -> {
                    if (BurnModeHelper.isBurnMode()) {
                        return;
                    }

                    Intent intent = FileSelectActivity.getIntent(BaseApplicationLike.baseContext, FileSelectActivity.SelectMode.SEND, false, false);
                    startActivityForResult(intent, GET_FILE);
                })
                .onDenied(data -> AtworkUtil.popAuthSettingAlert(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .start();
    }

    private void routeMicroVideo() {
        if (BurnModeHelper.isBurnMode()) {
            return;
        }

        if (VoipHelper.isHandlingVoipCall()) {
            AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);
            return;
        }

        AndPermission
                .with(mActivity)
                .runtime()
                .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                .onGranted(data -> {
                    AndPermission
                            .with(mActivity)
                            .runtime()
                            .permission(Permission.CAMERA, Permission.RECORD_AUDIO)
                            .onGranted(granted -> {
                                mRecordDialog = new PopupMicroVideoRecordingDialog();
                                mRecordDialog.setMicroVideoTakingListener(ChatDetailFragment.this);
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.add(mRecordDialog, "TEXT_POP_DIALOG");
                                transaction.commitAllowingStateLoss();
                                new Handler().postDelayed(() -> mActivity.runOnUiThread(() -> hideAll()), 1500);
                            })
                            .onDenied(denied -> {
                                if (denied != null && denied.size() > 0) {
                                    AtworkUtil.popAuthSettingAlert(mActivity, denied.get(0));
                                }

                            })
                            .start();
                })
                .onDenied(data -> AtworkUtil.popAuthSettingAlert(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .start();
    }

    private void routeVoip() {
        if (BurnModeHelper.isBurnMode()) {
            return;
        }

        if (AtworkUtil.isSystemCalling()) {
            AtworkToast.showResToast(R.string.alert_is_handling_system_call);
            return;
        }


        if (!VoipHelper.isHandlingVoipCall()) {
            if (isUserChat()) { //单聊
                startP2pTypeVoipMeeting();


            } else if (isDiscussionChat()) { //群聊

                SelectedContactList.clear();

                AtworkApplicationLike.getLoginUser(new UserAsyncNetService.OnQueryUserListener() {
                    @Override
                    public void onSuccess(@NonNull User user) {
                        List<User> singleList = new ArrayList<>();
                        singleList.add(user);

                        DiscussionMemberSelectControlAction discussionMemberSelectControlAction = new DiscussionMemberSelectControlAction();
                        discussionMemberSelectControlAction.setSelectedContacts(singleList);
                        discussionMemberSelectControlAction.setDiscussionId(mSession.identifier);
                        discussionMemberSelectControlAction.setSelectMode(DiscussionMemberSelectActivity.Mode.VOIP);

                        Intent intent = DiscussionMemberSelectActivity.getIntent(getActivity(), discussionMemberSelectControlAction);
                        startActivityForResult(intent, GET_VOIP_DISCUSSION_MEMBER_SELECT);
                    }

                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        ErrorHandleUtil.handleBaseError(errorCode, errorMsg);
                    }
                });

            }

        } else {
            AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);
        }
    }

    private boolean routeInChatView(DiscussionFeature feature) {
        switch (feature.getFeature()) {
            case DiscussionFeature.VOIP:
                routeVoip();
                return true;

            case DiscussionFeature.CONFERENCE:
                routeMeeting();
                return true;


            case DiscussionFeature.VIDEO:
                routeMicroVideo();
                return true;

            case DiscussionFeature.FILE:
                routeFile();
                return true;


            case DiscussionFeature.LOCATION:
                routeLocation();
                return true;

            case DiscussionFeature.ENTRY_LIST:
                routeEntryList();
                return true;

        }

        return false;
    }

    private void routeEntryList() {
        Activity activity = getActivity();

        if (null == activity) {
            return;
        }

    }

    private void routeMeeting() {
        if (BurnModeHelper.isBurnMode()) {
            return;
        }

        if (VoipHelper.isHandlingVoipCall()) {
            AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);
            return;
        }

        if (ZoomSdk.BIZCONF == AtworkConfig.ZOOM_CONFIG.getSdk()
                || ZoomSdk.ZOOM == AtworkConfig.ZOOM_CONFIG.getSdk()) {


            Activity activity = getActivity();
            if (null == activity) {
                return;
            }

            if (isUserChat()) {
                //直接进入单人呼叫界面
                zoomRouteP2pCallInstant(activity);
                return;
            }


            String[] meetingActions = new String[]{mTagMeetingReservation, mTagMeetingInstant};

            WorkplusBottomPopDialog workplusBottomPopDialog = new WorkplusBottomPopDialog();
            workplusBottomPopDialog.refreshData(meetingActions);

            workplusBottomPopDialog.setItemTextDrawable(0, R.mipmap.icon_meeting_reservation_new);
            workplusBottomPopDialog.setItemTextDrawable(1, R.mipmap.icon_meeting_instant_new);


//                    workplusBottomPopDialog.setAllItemTextColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.common_blue_bg));

            workplusBottomPopDialog.setItemOnListener(tag -> {

                workplusBottomPopDialog.dismiss();

                if (isDiscussionChat()) {
                    zoomRouteDiscussionMemberSelect(tag);
                    return;
                }


            });

            workplusBottomPopDialog.show(getFragmentManager(), "meetingActionDialog");


            return;
        }


        String url = AtworkConfig.ZOOM_CONFIG.getUrl();
        if (isDiscussionChat()) {
            url += ("?discussionId=" + mDiscussion.mDiscussionId);
        }

        WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                .setUrl(url)
                .setNeedShare(false)
                .setHideTitle(false);
        startActivity(WebViewActivity.getIntent(getActivity(), webViewControlAction));
    }

    private void tryHidePopChatDetailFunctionAreaView(int scrollState) {

        if (SCROLL_STATE_IDLE != scrollState
                && mPopChatDetailFunctionAreaView.isShown()) {
            hidePopChatDetailFunctionAreaView();
        }
    }

    private boolean isLegalUserChat() {
        return isUserChat() && !isCurrentFileTransfer();
    }

    private void showUserTyping() {

        mPeerTyped = true;

        mTvPersonalInfo.setText("对方正在输入…");
        mTvPersonalInfo.setVisibility(View.VISIBLE);

        boolean shouldStartTypingClock = (0 >= mUserTypingShowTimeRemaining);

        mUserTypingShowTimeRemaining = TYPING_STATUS_SHOWING_THRESHOLD;

        if (shouldStartTypingClock) {
            doShowUserTyping();
        }
    }

    private long mUserTypingShowTimeRemaining = 0;

    private void doShowUserTyping() {
        if (0 >= mUserTypingShowTimeRemaining) {
            setPersonalInfoView(mUser);
            return;
        }

        mUserTypingShowTimeRemaining -= 1000;

        mTvPersonalInfo.postDelayed(this::doShowUserTyping, 1000);

    }


    private long mLastSendTypingStatusTime = -1;

    private void trySendTypingStatus() {

        if (isLegalUserChat()) {

            long currentTimeMillis = System.currentTimeMillis();
            if (5000 > currentTimeMillis - mLastSendTypingStatusTime) {
                return;
            }

            UserTypingMessage.UserTypingMessageBuilder builder =
                    UserTypingMessage.UserTypingMessageBuilder.anUserTypingMessage()
                            .withBasicFrom()
                            .withTo(mSession.identifier)
                            .withToDomainId(mSession.mDomainId)
                            .withToType(ParticipantType.User)
                            .withConversationId(mSession.identifier)
                            .withConversationDomainId(mSession.mDomainId)
                            .withConversationType(ParticipantType.User);

            ChatMessageHelper.sendTypingStatus(builder.build());


            mLastSendTypingStatusTime = currentTimeMillis;
        }
    }

    private void zoomRouteP2pCallInstant(Activity activity) {
        MeetingInfo meetingInfo = new MeetingInfo();
        meetingInfo.mType = MeetingInfo.Type.USER;
        meetingInfo.mId = mSession.identifier;
        meetingInfo.mOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext);

        AtworkApplicationLike.getLoginUser(new UserAsyncNetService.OnQueryUserListener() {
            @Override
            public void onSuccess(@NonNull User loginUser) {
                List<ShowListItem> selectContactList = new ArrayList<>();
                selectContactList.add(loginUser);
                selectContactList.add(mUser);

                ZoomVoipManager.INSTANCE.goToCallActivity(activity, null, meetingInfo, VoipType.VIDEO, false, true, selectContactList, null, null);
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleError(errorCode, errorMsg);
            }
        });
    }

    private void zoomRouteDiscussionMemberSelect(String tag) {
        AtworkApplicationLike.getLoginUser(new UserAsyncNetService.OnQueryUserListener() {
            @Override
            public void onSuccess(@NonNull User user) {
                List<User> singleList = new ArrayList<>();
                singleList.add(user);

                DiscussionMemberSelectControlAction discussionMemberSelectControlAction = new DiscussionMemberSelectControlAction();
                discussionMemberSelectControlAction.setSelectedContacts(singleList);
                discussionMemberSelectControlAction.setDiscussionId(mSession.identifier);
                discussionMemberSelectControlAction.setSelectMode(DiscussionMemberSelectActivity.Mode.SELECT);

                Intent intent = DiscussionMemberSelectActivity.getIntent(BaseApplicationLike.baseContext, discussionMemberSelectControlAction);
                if (mTagMeetingReservation.equalsIgnoreCase(tag)) {
                    startActivityForResult(intent, GET_ZOOM_MEETING_RESERVATION);
                    return;

                }

                if (mTagMeetingInstant.equalsIgnoreCase(tag)) {
                    startActivityForResult(intent, GET_ZOOM_MEETING_INSTANT);
                    return;
                }
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleBaseError(errorCode, errorMsg);

            }
        });
    }

    private void bizconfRouteUserSelect(Activity activity, String tag) {
        UserSelectControlAction userSelectControlAction = new UserSelectControlAction();
        userSelectControlAction.setSelectMode(UserSelectActivity.SelectMode.SELECT);
        userSelectControlAction.setNeedCacheForCordova(true);

        Intent intent = UserSelectActivity.getIntent(activity, userSelectControlAction);

        if (mTagMeetingReservation.equalsIgnoreCase(tag)) {
            startActivityForResult(intent, GET_ZOOM_MEETING_RESERVATION);
            return;

        }

        if (mTagMeetingInstant.equalsIgnoreCase(tag)) {
            startActivityForResult(intent, GET_ZOOM_MEETING_INSTANT);
            return;
        }
    }

    private void handleClickBurnMode() {
        ChatDetailActivity.sIsBurnMode = !ChatDetailActivity.sIsBurnMode;

        if (ChatDetailActivity.sIsBurnMode) {
            toastOver(R.string.opened_burn_mode);

        } else {
            toastOver(R.string.closed_burn_mode);

        }

        mChatDetailInputView.refreshBurnUI(ChatDetailActivity.sIsBurnMode);
        mChatMoreView.setBurnMode(ChatDetailActivity.sIsBurnMode);

        if (ChatDetailActivity.sIsBurnMode) {
            EditTextUtil.setEditTextMaxStringLengthInput(mChatDetailInputView.getEmojiIconEditText(), 140, false);
        } else {
            EditTextUtil.clearMaxInput(mChatDetailInputView.getEmojiIconEditText());
        }
        //切换阅后即焚需要清空输入框
//        mChatDetailInputView.getEmojiIconEditText().setText(StringUtils.EMPTY);

        refreshListAdapter();
        refreshBurnUI(ChatDetailActivity.sIsBurnMode);
    }

    private void doDropboxClick() {
        Dropbox dropbox = new Dropbox();
        dropbox.mDomainId = AtworkConfig.DOMAIN_ID;
        dropbox.mSourceId = LoginUserInfo.getInstance().getLoginUserId(mActivity);
        dropbox.mSourceType = Dropbox.SourceType.User;
        Intent intent = SaveToDropboxActivity.getIntent(mActivity, dropbox, DropboxBaseActivity.DisplayMode.Send, false);
        startActivityForResult(intent, GET_DROPBOX_TO_SEND);
    }

    private void callPhone(String value) {
        IntentUtil.callPhoneJump(getActivity(), value);

//        callPhoneDirectly(value);
    }

    private void callPhoneDirectly(String value) {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                IntentUtil.callPhoneDirectly(AtworkApplicationLike.baseContext, value);

            }

            @Override
            public void onDenied(String permission) {
                AtworkUtil.popAuthSettingAlert(getActivity(), permission);
            }
        });
    }

    private void refreshTimePrintViewFloating(int currentFirstVisibleItem) {
        if (ListUtil.isEmpty(mWithTimeMessageList)) {
            mTvTimePrintFloat.setVisibility(GONE);
            return;
        }

        ChatPostMessage messageFirstVisible = CollectionsKt.getOrNull(mWithTimeMessageList, currentFirstVisibleItem);
        if (null == messageFirstVisible) {
            return;
        }

        if (0 >= messageFirstVisible.deliveryTime) {
            mTvTimePrintFloat.setVisibility(GONE);
            return;
        }

        mTvTimePrintFloat.setText(TimeViewUtil.getUserCanViewTimeInChatDetail(getContext(), messageFirstVisible.deliveryTime));
        mTvTimePrintFloat.setVisibility(VISIBLE);

    }

    private void tryHideViewUnreadUnreadMsgTip(int currentFirstVisibleItem, int lastVisiblePosition) {
        if (lastVisiblePosition == mWithTimeMessageList.size() - 1) {
            hideBottomJumpBtnArea();
        }
        if (mViewUnreadMsgTip.isShown() && !isStillHavingUnread()) {
            int position = mWithTimeMessageList.indexOf(mFirstUnreadChatPostMessage);
            if (currentFirstVisibleItem <= position) {
                mViewUnreadMsgTip.setVisibility(GONE);
            }
        }
    }

    private void tryHideViewUnreadDiscussionNotifyTip(int currentFirstVisibleItem) {
        if (mViewUnreadDiscussionNotifyTip.isShown()) {
            List<ChatPostMessage> waitRemoveMsgList = new ArrayList<>();
            for (ChatPostMessage unreadDiscussionNotificationMsg : mUnreadDiscussionNotificationList) {

                int position = mWithTimeMessageList.indexOf(unreadDiscussionNotificationMsg);
                if (currentFirstVisibleItem <= position) {
                    waitRemoveMsgList.add(unreadDiscussionNotificationMsg);
                }

            }

            if (!ListUtil.isEmpty(waitRemoveMsgList)) {
                mUnreadDiscussionNotificationList.removeAll(waitRemoveMsgList);
                refreshDiscussionNotifyUnreadTipView();
            }
        }
    }


    private void refreshCommonBackView() {
        setCommonBackView(ChatDetailActivity.sIsBurnMode);
    }

    private void refreshCancelBackView() {
        setCancelBackView(ChatDetailActivity.sIsBurnMode);
    }

    private void setPopChatDetailFunctionAreaView(boolean isBurn) {
        FragmentActivity activity = getActivity();

        if (isBurn) {

            mPopChatDetailFunctionAreaView.getRootBgView().setBackgroundColor(ContextCompat.getColor(activity, R.color.burn_mode_chat_input_bg));
            mPopChatDetailFunctionAreaView.getMoreTv().setTextColor(ContextCompat.getColor(activity, R.color.common_gray_bg));
            mPopChatDetailFunctionAreaView.getCallTv().setTextColor(ContextCompat.getColor(activity, R.color.common_gray_bg));
            mPopChatDetailFunctionAreaView.getStarTv().setTextColor(ContextCompat.getColor(activity, R.color.common_gray_bg));
        } else {
            mPopChatDetailFunctionAreaView.getRootBgView().setBackgroundColor(Color.WHITE);
            mPopChatDetailFunctionAreaView.getMoreTv().setTextColor(ContextCompat.getColor(activity, R.color.common_text_color));
            mPopChatDetailFunctionAreaView.getCallTv().setTextColor(ContextCompat.getColor(activity, R.color.common_text_color));
            mPopChatDetailFunctionAreaView.getStarTv().setTextColor(ContextCompat.getColor(activity, R.color.common_text_color));
        }
    }

    private void setCommonBackView(boolean isBurn) {
        if (isBurn) {
            mIvBack.setImageResource(R.mipmap.icon_burn_back);

        } else {
            mIvBack.setImageResource(R.mipmap.icon_back);

        }
    }

    private void setCancelBackView(boolean isBurn) {
        if (isBurn) {
            mIvBack.setImageResource(R.mipmap.icon_remove);

        } else {
            mIvBack.setImageResource(R.mipmap.icon_remove_back);

        }
    }

    private void refreshBurnUI(boolean isBurn) {
        FragmentActivity activity = getActivity();
        setCommonBackView(isBurn);
        setPopChatDetailFunctionAreaView(isBurn);

        if (isBurn) {
            mVTopLineChatInput.setBackgroundColor(ContextCompat.getColor(activity, R.color.burn_mode_line));
            mVBottomLineChatInput.setBackgroundColor(ContextCompat.getColor(activity, R.color.burn_mode_line));
            mFlFunctionArea.setBackgroundColor(ContextCompat.getColor(activity, R.color.burn_mode_chat_input_bg));
            mTitleView.setTextColor(ContextCompat.getColor(getActivity(), R.color.common_gray_bg));

            mVTitleBar.setBackgroundColor(ContextCompat.getColor(activity, R.color.burn_mode_chat_input_bg));

//            mIvChatInfo.setImageResource(R.mipmap.icon_burn_chat_single);
            mIvUserPhone.setImageResource(R.mipmap.icon_burn_user_phone);
            mIvBack.setImageResource(R.mipmap.icon_burn_back);

            mFavChatView.setImageResource(R.mipmap.icon_burn_multi_select_fav);
            mForwardChatView.setImageResource(R.mipmap.icon_burn_transfer_in_chat_view);
            mDeleteChatView.setImageResource(R.mipmap.icon_burn_multi_select_delete);
            mSelectModelView.setBackgroundColor(ContextCompat.getColor(activity, R.color.burn_mode_chat_input_bg));

        } else {
            mVTopLineChatInput.setBackgroundColor(Color.parseColor("#D8D8D8"));
            mVBottomLineChatInput.setBackgroundColor(ContextCompat.getColor(activity, R.color.common_line_color));
            mFlFunctionArea.setBackgroundColor(Color.WHITE);
            mTitleView.setTextColor(ContextCompat.getColor(getActivity(), R.color.common_item_black));

            mVTitleBar.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));

//            mIvChatInfo.setImageResource(R.mipmap.icon_chat_single);
            mIvUserPhone.setImageResource(R.mipmap.icon_user_phone);
            mIvBack.setImageResource(R.mipmap.icon_back);


            mFavChatView.setImageResource(R.mipmap.icon_favorite_dark);
            mForwardChatView.setImageResource(R.mipmap.icon_transfer_in_chat_view);
            mDeleteChatView.setImageResource(R.mipmap.icon_del_hover);
            mSelectModelView.setBackgroundColor(Color.WHITE);


            changeTheme(activity);
        }

        setUserWatermarkBaseOnMode();

        changeStatusBar(activity);
    }

    private void changeTheme(FragmentActivity activity) {
        if (activity instanceof ChatDetailActivity) {
            ChatDetailActivity chatDetailActivity = (ChatDetailActivity) activity;
            chatDetailActivity.changeTheme();
        }
    }

    private void changeStatusBar(FragmentActivity activity) {
        if (activity instanceof ChatDetailActivity) {
            ChatDetailActivity chatDetailActivity = (ChatDetailActivity) activity;
            chatDetailActivity.changeStatusBar();
        }
    }

    public void startP2pTypeVoipMeeting() {
        ArrayList<User> coupleList = new ArrayList<>();
        coupleList.add(mUser);

        AtworkApplicationLike.getLoginUser(new UserAsyncNetService.OnQueryUserListener() {
            @Override
            public void onSuccess(@NonNull User user) {
                coupleList.add(user);

                Intent intent = VoipSelectModeActivity.getIntent(getActivity(), coupleList);
                startActivity(intent);
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleTokenError(errorCode, errorMsg);
            }
        });
    }

    @Nullable
    public ShowListItem getSendChatItem() {
        ShowListItem sendItem = null;
        if (mSession.isAppType()) {
            sendItem = mApp;

        } else if (mSession.isDiscussionType()) {
            sendItem = mDiscussion;

        } else if (mSession.isUserType()) {
            sendItem = mUser;
        }

        return sendItem;
    }


    /**
     * 删除消息
     *
     * @param messages
     */
    private void deleteMessages(final List<ChatPostMessage> messages) {
        ChatDaoService.getInstance().batchRemoveMessages(messages, new ChatDaoService.BatchAddOrRemoveListener() {
            @Override
            public void addOrRemoveFail() {
            }

            @Override
            public void addOrRemoveSuccess() {
                mMessageList.removeAll(messages);
                refreshWithTimeMsgListTotally();
                mChatDetailArrayListAdapter.setMessages(mWithTimeMessageList);
                refreshListAdapter();

                MessageCache.getInstance().updateMessageList(mSession.identifier, mMessageList);
                checkLastMessageOnSession();


                boolean result = mUnreadDiscussionNotificationList.removeAll(messages);
                if (result) {
                    refreshDiscussionNotifyUnreadTipView();
                }

            }
        });
    }

    public void backAction() {
        hideInput();
        //如果是在转发删除模式，退出此模式
        if (ChatModel.SELECT.equals(mChatModel)) {
            changeModel();
            return;
        }

        checkSessionUpdate();

        boolean isReturnBack = getArguments().getBoolean(RETURN_BACK, false);

        if (!isReturnBack) {
            startActivity(MainActivity.getMainActivityIntent(getActivity(), true));
        }
        finish();

    }

    private void checkSessionUpdate() {
        if (mHasCheckSession) {
            return;
        }

        if (mDoNotCheckSession) {
            return;
        }

        if (null == mSession) {
            return;
        }

        mHasCheckSession = true;

        String draft = mChatDetailInputView.getEmojiIconEditText().getText().toString();
        mSession.clearUnread();
        boolean shouldRemove = false;
        if (!StringUtils.isEmpty(draft)) {
            mSession.draft = draft;
        } else {

            shouldRemove = verifySessionOnFinish(true);
            if (!shouldRemove) {
                mSession.draft = StringUtils.EMPTY;

            }
        }

        updateSessionTypeStatus(false);

        if (!shouldRemove) {
            checkLastMessageOnSession();
        }

        SessionRefreshHelper.notifyRefreshSession();
    }


    private void checkLastMessageOnSession() {
        final ChatPostMessage message = getLastLegalChatPostMessage();
        mMsgThreadService.submit(() -> {
                    if (null != message) {
                        ChatSessionDataWrap.getInstance().updateSession(mSession, message, false, false, true);
                    } else {
                        ChatSessionDataWrap.getInstance().emptySessionInMainChatView(mSession);
                    }
                }
        );

    }


    /**
     * 返回真正意义的第一条消息(包括过期变成"系统通知"的消息)
     */
    private ChatPostMessage getFirstRealChatPostMessage() {

        if (!ListUtil.isEmpty(mWithTimeMessageList)) {
            for (int i = 0; i < mWithTimeMessageList.size(); i++) {
                ChatPostMessage chatPostMessage = mWithTimeMessageList.get(i);
                if (mMessageList.contains(chatPostMessage) || mMessageJustShowList.contains(chatPostMessage)) {
                    return chatPostMessage;
                }
            }
        }

        return null;
    }

    /**
     * 返回有存储意义的最后条消息,
     * 例如, "阅后即焚消息超时未读，已被系统删除" 这条通知不用存储起来, 只对当次有效
     */
    @Nullable
    private ChatPostMessage getLastLegalChatPostMessage() {

        if (!ListUtil.isEmpty(mWithTimeMessageList)) {
            for (int i = mWithTimeMessageList.size() - 1; i >= 0; i--) {
                ChatPostMessage chatPostMessage = mWithTimeMessageList.get(i);
                if (mMessageList.contains(chatPostMessage)) {
                    return chatPostMessage;
                }
            }
        }

        return null;
    }

    private int getOriginalMsgListSize() {
        return mMessageList.size() + mMessageJustShowList.size();
    }


    /**
     * 更新session @人状态
     */
    private void updateSessionTypeStatus(boolean updateDb) {


        Session.ShowType lastLastMessageShowType = mSession.lastMessageShowType;

        if (Session.ShowType.At.equals(mSession.lastMessageShowType)) {
            mSession.lastMessageShowType = Session.ShowType.Text;

        } else if (Session.ShowType.RedEnvelope.equals(mSession.lastMessageShowType)) {
            mSession.lastMessageShowType = Session.ShowType.Text;
        }


        if (lastLastMessageShowType == mSession.lastMessageShowType) {
            return;
        }

        if (updateDb) {
            updateSessionToDB();
        }
    }

    /**
     * 若所有@人的消息里都已读, 则更改状态(旧版本的需求)
     */
    private void updateAtStatus_old() {
        if (Session.ShowType.At.equals(mSession.lastMessageShowType)) {
            boolean needUpdate = false;
            for (ChatPostMessage chatPostMessage : mWithTimeMessageList) {
                if (chatPostMessage instanceof TextChatMessage
                        && ((TextChatMessage) chatPostMessage).containAtMe(mActivity)
                        && ((TextChatMessage) chatPostMessage).chatSendType.equals(ChatSendType.RECEIVER)) {

                    if (ReadStatus.LocalRead.equals(chatPostMessage.read)
                            || ReadStatus.AbsolutelyRead.equals(chatPostMessage.read)) {
                        needUpdate = true;
                    } else {
                        needUpdate = false;
                        break;
                    }
                }
            }

            if (needUpdate) {
                mSession.lastMessageShowType = Session.ShowType.Text;
                updateSessionToDB();
            }

        }

    }

    /**
     * 退出对话界面时判读 mSession 是否要清除掉
     *
     * @param doRemove 是否进行 remove 操作
     */
    private boolean verifySessionOnFinish(boolean doRemove) {

        boolean shouldRemove = false;
        if (shouldRemoveSession()) {

            if (doRemove) {
                ChatSessionDataWrap.getInstance().removeSession(mSession.identifier, false);
            }
            shouldRemove = true;
        }
        return shouldRemove;
    }

    private boolean shouldRemoveSession() {
        if (!hasInitReadChatData) {
            return false;
        }

        if (!isEmptyExceptSystemMsg(mMessageList)) {
            return false;
        }

        if (!isEmptyExceptSystemMsg(mWithTimeMessageList)) {
            return false;
        }


        if (!isSessionNoneTop()) {
            return false;
        }


        if (mMsgClearedSuccessfullyAction) {
            return true;
        }

        if (mNeedSessionLegalCheck) {
            return true;
        }


        return false;


    }

    private boolean isSessionNoneTop() {
        return null != mSession && SessionTop.REMOTE_TOP != mSession.top && !ChatSessionDataWrap.getInstance().isTop(mSession.identifier);
    }

    /**
     * 判断是否消息列表为空
     * 除了系统消息以外还是否存在其他消息
     */
    public boolean isEmptyExceptSystemMsg(final List<ChatPostMessage> sourceList) {
        boolean isEmpty = true;

        //判断是否是群主，如果是群主, 只判断是否有消息
        if (null != mDiscussion && null != mDiscussion.mOwner) {
            if (mLoginUser.mUserId.equals(mDiscussion.mOwner.mUserId)) {
                return ListUtil.isEmpty(sourceList);
            }
        }

        //如果不是群主，判断是否有其他消息, 好友接受通知, 群二维码消息, 组织通知消息
        if (!ListUtil.isEmpty(sourceList)) {
            for (ChatPostMessage chatPostMessage : sourceList) {
                if ((chatPostMessage instanceof SystemChatMessage)) {
                    SystemChatMessage systemChatMessage = ((SystemChatMessage) chatPostMessage);
                    if (isSystemMsgFromFriApproved(systemChatMessage) || isSystemMsgFromQrcodeShare(systemChatMessage)
                            || isSystemMsgFromOrgNotice(systemChatMessage) || isSystemMsgFromVoip(systemChatMessage)) {
                        isEmpty = false;
                        break;
                    }
                } else {
                    isEmpty = false;
                    break;
                }
            }
        }
        return isEmpty;
    }

    private boolean isSystemMsgFromQrcodeShare(SystemChatMessage systemChatMessage) {
        return systemChatMessage.content.startsWith(getString(R.string.invite_group_member, getString(R.string.you), ""))
                && systemChatMessage.content.endsWith(getString(R.string.key_tip_invite_group_member));
    }

    private boolean isSystemMsgFromOrgNotice(SystemChatMessage systemChatMessage) {
        return Session.WORKPLUS_SYSTEM.equals(systemChatMessage.from);
    }

    private boolean isSystemMsgFromFriApproved(SystemChatMessage systemChatMessage) {
        return systemChatMessage.content.contains(getString(R.string.me_accept_friend_tip_tail)) || systemChatMessage.content.equals(getString(R.string.other_accept_friend_tip));
    }

    private boolean isSystemMsgFromVoip(SystemChatMessage systemChatMessage) {
        return SystemChatMessage.Type.NOTIFY_VOIP == systemChatMessage.type;
    }


    /**
     * 显示更多功能框
     */
    private void switchChatMoreView() {
//        mChatMoreView.getLayoutParams().height = getKeyBoardHeight();
        //如果更多的功能框显示，更新更多的按钮并关闭更多的功能框
        if (mChatMoreView.isShown()) {
            mChatDetailInputView.showMoreImage();
            mFlFunctionArea.setVisibility(GONE);

        } else {

            tryHidePopChatDetailFunctionAreaViewWhenHidingBottomAreaView();


            mChatDetailInputView.showCloseImage();
            changeMenuView(mChatMoreView);

            hideInputNothingMode();

            scrollToLast();

        }

    }

    private void tryHidePopChatDetailFunctionAreaViewWhenHidingBottomAreaView() {
        if (!mFlFunctionArea.isShown()) {
            mPopChatDetailFunctionAreaView.postDelayed(() -> tryHidePopChatDetailFunctionAreaView(-1), 200);
        }
    }


    private void scrollToLast() {

        //先停止滚动
//        mChatListView.smoothScrollBy(0, 0);
//        mChatListView.postDelayed(() -> mChatListView.setSelection(mChatDetailArrayListAdapter.getCount() - 1), 100);

        mChatListView.scrollToPosition(mChatDetailArrayListAdapter.getItemCount() - 1);

        hideBottomJumpBtnArea();
    }


    /**
     * 显示表情框
     */
    private void showEmoji() {
        mEmojView.getLayoutParams().height = getFunctionAreaShowHeight();
        if (mEmojView.isShown()) {
            return;
        }

        tryHidePopChatDetailFunctionAreaViewWhenHidingBottomAreaView();

        StickerManager.Companion.getInstance().checkRemoteStickerAlbums(mActivity, true);
        hideInputNothingMode();

        changeMenuView(mEmojView);
        scrollToLast();
    }


    private void showVoice() {

        mChatVoiceView.getLayoutParams().height = getFunctionAreaShowHeight();


        if (mChatVoiceView.isShown()) {
            return;
        }

        tryHidePopChatDetailFunctionAreaViewWhenHidingBottomAreaView();


        hideInputNothingMode();

        changeMenuView(mChatVoiceView);

        scrollToLast();


    }

    private void showEdit() {
        scrollToLast();

        mChatMoreView.getLayoutParams().height = getFunctionAreaShowHeight();
        if (!mChatMoreView.isShown()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
        hideFunctionArea();

        mChatDetailInputView.showIvSwitchEmoji();
    }


    private boolean mLoadingMoreMessages = false;

    private void loadMoreMessages() {
        if (mLoadingMoreMessages) {
            return;
        }

        synchronized (mRefreshListLock) {

            mLoadingMoreMessages = true;


            LogUtil.e("  loadMoreMessages   mLoadingMoreMessages  ~~~~~");

            long firstTimestamp;

            if (-1 == mRealFirstTimestamp) {
                firstTimestamp = TimeUtil.getCurrentTimeInMillis();

                ChatPostMessage firstMsg = getFirstRealChatPostMessage();
                if (null != firstMsg) {
                    firstTimestamp = firstMsg.deliveryTime;
                } else {
                    mLocalHasMore = false;
                }

            } else {
                firstTimestamp = mRealFirstTimestamp;

            }


            if (mLocalHasMore) {
                showLoading();
                Log.d("RESULT", "加载本地更多数据");
                loadMoreMessagesFromLocal(firstTimestamp);

            } else if (mRemoteHasMore) {
                showLoading();
                Log.d("RESULT", "加载远程更多");
                loadMoreMessagesFromRemote(firstTimestamp);

            } else {
                AtworkToast.showToast(getResources().getString(R.string.no_more_messages));
                hiddenLoading();
            }
        }

    }

    private void loadMoreMessagesFromRemote(final long firstTimestamp) {
        if (isUserChat() || isDiscussionChat() || SessionType.Service.equals(mSession.type)) {
            String participantDomain = null;
            String participantId;
            ParticipantType participantType = null;

            if (isUserChat()) {
                if (mLoginUser == null) {
                    hiddenLoading();
                    return;
                }
                participantDomain = mLoginUser.mDomainId;
                participantType = ParticipantType.User;

            } else if (isDiscussionChat()) {
                if (mDiscussion == null) {
                    hiddenLoading();
                    return;
                }
                participantDomain = mDiscussion.mDomainId;
                participantType = ParticipantType.Discussion;

            } else if (SessionType.Service.equals(mSession.type)) {
                if (mApp == null) {
                    hiddenLoading();
                    return;
                }
                participantDomain = mApp.mDomainId;
                participantType = ParticipantType.App;
            }

            if (StringUtils.isEmpty(participantDomain)) {
                return;
            }

            participantId = mSession.identifier;


            long fetchInDayTimeDomainSetting = DomainSettingsManager.getInstance().getMessagePullLatestTime();
            if (fetchInDayTimeDomainSetting > firstTimestamp) {
                AtworkToast.showToast(getResources().getString(R.string.no_more_messages));
                hiddenLoading();
                return;
            }


            long begin = -1;
            if (-1 != fetchInDayTimeDomainSetting) {
                begin = fetchInDayTimeDomainSetting;
            }

            ChatService.queryRoamingMessages(mActivity, participantDomain, participantType, participantId, begin, firstTimestamp,
                    AtworkConfig.ROAMING_AND_PULL_SYNC_INCLUDE_TYPE, null, "last_in", MORE_LOAD_MESSAGES_COUNT, false,
                    new MessageAsyncNetService.GetHistoryMessageListener() {
                        @Override
                        public void networkFail(int errorCode, String errorMsg) {
                            hiddenLoading();

                        }

                        @Override
                        public void getHistoryMessageSuccess(List<ChatPostMessage> historyMessages, int realOfflineSize) {
                            if (!ListUtil.isEmpty(historyMessages)) {
                                mRealFirstTimestamp = historyMessages.get(0).deliveryTime;
                            }

                            HideMessageHelper.hideMessageList(historyMessages);
                            NewsSummaryHelper.Companion.filtrateMessageList(historyMessages);
                            List<ChatPostMessage> systemMsgJustShowList = BurnModeHelper.checkMsgExpiredAndRemove(historyMessages, false);

                            for (ChatPostMessage chatPostMessage : historyMessages) {
                                ChatMessageHelper.dealWithChatMessage(mActivity, chatPostMessage, false);
                            }

//                            historyMessages = handleFilterExpiredMsg(historyMessages);

                            hiddenLoading();
                            LogUtil.d("RESULT", "远程数据加载成功:" + realOfflineSize);
                            if (0 == realOfflineSize) {
                                mRemoteHasMore = false;
                                toast(R.string.no_more_messages);
                                return;
                            }

                            if (realOfflineSize < MORE_LOAD_MESSAGES_COUNT) {
                                mRemoteHasMore = false;
                            }

                            if (ListUtil.isEmpty(mMessageList) && !ListUtil.isEmpty(historyMessages)) {
                                ChatPostMessage last = historyMessages.get(historyMessages.size() - 1);
                                updateSession(last);
                            }

                            mReadCount += MORE_LOAD_MESSAGES_COUNT;

                            int oldWithTimeMsgSize = mWithTimeMessageList.size(); // for get the right firstUnread

                            ChatPostMessage preFirstChatPostMessage = getFirstRealChatPostMessage();

                            loadMessageCompleteWithoutReceipt(historyMessages, systemMsgJustShowList);
                            batchSaveHistoryMessages(historyMessages);
                            refreshUnreadStatusInLoadMore(mWithTimeMessageList.size() - oldWithTimeMsgSize - 1);

                            //刷新 view 并且跳转
                            if (preFirstChatPostMessage != null) { //下拉时固定位置
                                refreshViewToPosition(preFirstChatPostMessage);
                            } else {
                                refreshView();
                            }

                            sendReceipt(historyMessages);

                        }

                    });

        } else {
            hiddenLoading();
        }
    }

//    @NonNull
//    private List<ChatPostMessage> handleFilterExpiredMsg(List<ChatPostMessage> historyMessages) {
//        int preSize = historyMessages.size();
//
//        boolean hasReadBreakMsg = hasReadBreakMsg(historyMessages);
//
//        historyMessages = BasicMsgHelper.filterExpiredMsg(historyMessages);
//        int newSize = historyMessages.size();
//
//        if(preSize != newSize && hasReadBreakMsg) {
//            mRemoteHasMore = false;
//        }
//        return historyMessages;
//    }
//
//    private boolean hasReadBreakMsg(List<ChatPostMessage> historyMessages) {
//        return CollectionsKt.any(historyMessages, message -> message.read != ReadStatus.Unread && BasicMsgHelper.isReceiver(message));
//    }


    private void loadMoreMessagesFromLocal(final long firstTimestamp) {
        ChatDaoService.getInstance().queryLastMessageInLimitAndCheckExpired(mActivity, mSession.identifier, firstTimestamp, MORE_LOAD_MESSAGES_COUNT, (chatPostMessageList, systemMessageTipList) -> {

            if (chatPostMessageList.size() < MORE_LOAD_MESSAGES_COUNT) {
                mLocalHasMore = false;
            }

            if (ListUtil.isEmpty(chatPostMessageList) && ListUtil.isEmpty(systemMessageTipList)) {
                loadMoreMessagesFromRemote(firstTimestamp);
                return;
            }

            hiddenLoading();

            mReadCount += MORE_LOAD_MESSAGES_COUNT;

            int oldWithTimeMsgSize = mWithTimeMessageList.size(); // for get the right firstUnread

            ChatPostMessage preFirstChatPostMessage = getFirstRealChatPostMessage();

            loadMessageCompleteWithoutReceipt(chatPostMessageList, systemMessageTipList);
            refreshUnreadStatusInLoadMore(mWithTimeMessageList.size() - oldWithTimeMsgSize - 1);

            //刷新 view 并且跳转
            if (preFirstChatPostMessage != null) { //下拉时固定位置
                refreshViewToPosition(preFirstChatPostMessage);
            } else {
                refreshView();
            }

            sendReceipt(chatPostMessageList);

        });
    }


    private void showLoading() {
//        mPullDownRefreshView.setVisibility(View.VISIBLE);
    }

    private void hiddenLoading() {
//        new Handler().postDelayed(() -> mPullDownRefreshView.setVisibility(GONE), 400);

        mLoadingMoreMessages = false;
        mSmartRefreshLayout.finishRefresh(0);
    }

    private void hideAll() {
        mChatDetailInputView.showIvSwitchEmoji();
        hideInput();
        hideFunctionArea();
    }


    /**
     * 隐藏键盘
     */
    public void hideInput() {
        AtworkUtil.hideInput(getActivity(), mChatDetailInputView.getEmojiIconEditText());
        if (mPopLinkTranslatingView.isShowing()) {
            mPopLinkTranslatingView.nothing();
        }
    }

    private void hideInputNothingMode() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        handleFunctionAreaHeight(getFunctionAreaShowHeight());
        mFlFunctionArea.setVisibility(View.VISIBLE);
        hideInput();
    }

    private void showInput() {
        //如果功能面板已经显示的状态, 则对高度进行调整, 已适应键盘弹出的位置
        if (mFlFunctionArea.isShown()) {
            mChatMoreView.setVisibility(GONE);
            mChatDetailInputView.showMoreImage();
            mEmojView.setVisibility(GONE);

            handleFunctionAreaHeight(mKeyboardInputHeight);
            mFlFunctionArea.requestLayout();
        }
        AtworkUtil.showInput(getActivity(), mChatDetailInputView.getEmojiIconEditText());

        tryHidePopChatDetailFunctionAreaViewWhenHidingBottomAreaView();

        new Handler().postDelayed(() -> {
            if (isAdded()) {

                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                mFlFunctionArea.setVisibility(GONE);
            }
        }, 300);

    }

    private int getFunctionAreaShowHeight() {
        return KeyboardHeightHelper.getFunctionAreaShowHeight(mKeyboardInputHeight);
    }

    private void handleFunctionAreaHeight(int height) {
        mFlFunctionArea.getLayoutParams().height = height;
        mChatMoreView.getLayoutParams().height = height;
        mChatVoiceView.getLayoutParams().height = height;
    }


    private int getKeyBoardHeight() {
        return CommonShareInfo.getKeyBoardHeight(getActivity());
    }

    private void hideFunctionArea() {
        mFlFunctionArea.setVisibility(GONE);
        mChatMoreView.setVisibility(GONE);
        mChatVoiceView.setVisibility(GONE);
        mChatDetailInputView.showMoreImage();
        mEmojView.setVisibility(GONE);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onChangeLanguage() {
        MessageItemPopUpHelp.refreshViewItemText();
    }

    private void clearImage() {
        if (mChatDetailArrayListAdapter != null) {
            ImageSwitchInChatActivity.sImageChatMessageList.clear();

        }

    }


    private void clearBroadcast() {
        mNewMessageBroadcast = null;
        mOfflineMessageBroadcast = null;
        mRefreshViewBroadcastReceiver = null;
        mAddSendingListenerBroadcastReceiver = null;
    }


    public void playNextAudio(VoiceChatMessage voiceChatMessage) {
        if (voiceChatMessage.playing) {
            AudioRecord.stopPlaying();
            voiceChatMessage.playing = false;
        } else {
            PlayAudioInChatDetailViewParams playAudioInChatDetailViewParams = new PlayAudioInChatDetailViewParams();
            playAudioInChatDetailViewParams.setContext(mActivity);
            playAudioInChatDetailViewParams.setVoiceChatMessage(voiceChatMessage);
            playAudioInChatDetailViewParams.setInSuccession(true);

            AudioRecord.playAudioInChatDetailView(playAudioInChatDetailViewParams);

            voiceChatMessage.play = true;
            voiceChatMessage.playing = true;
            ChatDaoService.getInstance().updateMessage(voiceChatMessage);
        }

        refreshListAdapter();
    }


    private void newSendMessage(final ChatPostMessage message) {
        Context context = BaseApplicationLike.baseContext;
        ChatService.wrapParticipant(context, message, mSession, postTypeMessage -> doSendNewMessage((ChatPostMessage) postTypeMessage));

    }


    private void doSendNewMessage(ChatPostMessage message) {
        if (!mMessageList.contains(message)) {
            //更新状态
            addChatMessage(message);
        }
        updateSession(message);

        MessageCache.getInstance().updateMessageList(mSession.identifier, mMessageList);

        //发送消息
        ChatSendOnMainViewService.sendNewMessageToIM(mSession, message);
        updateMessageToDB(message);

        refreshViewAfterSendPureMsg();

    }

    private void refreshViewAfterSendPureMsg() {
        mChatListView.removeCallbacks(mRefreshViewAfterSendPureMsgRunnable);
        mChatListView.postDelayed(mRefreshViewAfterSendPureMsgRunnable, 0);
    }

    private Runnable mRefreshViewAfterSendPureMsgRunnable = () -> {
        refreshView();
        //todo scroll
//        mChatListView.postDelayed(() -> mChatListView.setSelection(mWithTimeMessageList.size() - 1), 300);
//        mChatListView.postDelayed(() -> mChatListView.smoothScrollToPosition(mWithTimeMessageList.size() - 1), 300);

        mChatListView.scrollToPosition(mWithTimeMessageList.size() - 1);
    };

    private void sendUndoMessage(ChatPostMessage message) {
        UndoEventMessage undoEventMessage = UndoEventMessage.newUndoEventMessage(mLoginUser, getSendChatItem(), mSession.identifier, message.deliveryId, mSession.type.getFromType(), mSession.type.getToType(), mSession.mDomainId, BodyType.Event);
        Context context = BaseApplicationLike.baseContext;
        ChatService.wrapParticipant(context, undoEventMessage, mSession, postTypeMessage -> doSendUndoMessage(postTypeMessage));


    }


    private void doSendUndoMessage(PostTypeMessage postTypeMessage) {
        ChatSendOnMainViewService.sendNewMessageToIM(mSession, postTypeMessage);
    }

    /**
     * @see #newSendWithProgressMessage(String, String, ChatPostMessage, boolean, boolean)
     */
    private void newSendWithProgressMessage(final String fileType, final String filePath, final ChatPostMessage message, boolean needCheckSum) {
        newSendWithProgressMessage(fileType, filePath, message, true, needCheckSum);
    }

    private void localFileNotExist(final ChatPostMessage message) {
        AtworkToast.showToast(getResources().getString(R.string.file_not_exists));
        message.chatStatus = ChatStatus.Not_Send;
        if (message instanceof HasFileStatus) {
            ((HasFileStatus) message).setFileStatus(FileStatus.NOT_SENT);
        }
        updateMessageToDB(message);
        refreshView();
    }

    /**
     * 发送带上传进度功能的消息
     *
     * @param message
     */
    private void newSendWithProgressMessage(final String fileType, final String filePath, final ChatPostMessage message, boolean needRefresh, boolean needCheckSum) {
        Context context = BaseApplicationLike.baseContext;
        ChatService.wrapParticipant(context, message, mSession, postTypeMessage -> doNewSendWithProgressMessage(fileType, filePath, (ChatPostMessage) postTypeMessage, needRefresh, needCheckSum));

    }


    private void doNewSendWithProgressMessage(String fileType, String filePath, ChatPostMessage message, boolean needRefresh, boolean needCheckSum) {
        if (TextUtils.isEmpty(filePath)) {
            localFileNotExist(message);
            return;
        }
        if (!new File(filePath).exists()) {
            localFileNotExist(message);
            return;
        }
        doNewSendWithProgressMessageBeforeUploadCheck(message, needRefresh);

        //延时处理，因为此时监控上传下载的UI可能还没有绘制好

        if (MediaCenterNetManager.IMAGE_FULL_FILE.equals(fileType)) {
            MediaCenterNetManager.uploadFullImageFile(getActivity(), message.deliveryId, filePath);

        } else {
//            mediaCenterNetManager.uploadFile(getActivity(), fileType, message.deliveryId, filePath, needCheckSum, (errorCode, errorMsg)
//                    -> ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Media, errorCode, errorMsg));

            UploadFileParamsMaker uploadFileParamsMaker = UploadFileParamsMaker.newRequest()
                    .setType(fileType)
                    .setMsgId(message.deliveryId)
                    .setFilePath(filePath)
                    .setNeedCheckSum(needCheckSum);

            MediaCenterNetManager.uploadFile(getActivity(), uploadFileParamsMaker);
        }


        if (message instanceof ImageChatMessage) {
            addImgSendingListener((ImageChatMessage) message);

        } else if (message instanceof FileTransferChatMessage) {
            addFileSendingListener((FileTransferChatMessage) message);

        } else if (message instanceof MicroVideoChatMessage) {
            addMicroVideoSendingListener((MicroVideoChatMessage) message);

        } else if (message instanceof VoiceChatMessage) {
            addVoiceSendingListener((VoiceChatMessage) message);

        } else if (message instanceof MultipartChatMessage) {
            addMultipartSendingListener((MultipartChatMessage) message);
        }
    }

    private void doNewSendWithProgressMessageBeforeUploadCheck(ChatPostMessage message, boolean needRefresh) {
        SendMessageDataWrap.getInstance().addChatSendingMessage(message);
        if (needRefresh) {

            if (!mMessageList.contains(message)) {
                addChatMessage(message);
            }

            updateSession(message);
            MessageCache.getInstance().updateMessageList(mSession.identifier, mMessageList);

            refreshView();
        }
    }

    private void updateSession(final ChatPostMessage message) {
        mMsgThreadService.submit(() -> {

            ChatService.handleUpdateSessionAndSendStatusInSync(mSession, message);

            mSession.draft = StringUtils.EMPTY;
        });

    }

    private void updateSessionToDB() {
        ChatDaoService.getInstance().sessionRefresh(mSession);
    }

    private void updateMessageToDB(ChatPostMessage message) {
        ChatDaoService.getInstance().insertOrUpdateMessage(BaseApplicationLike.baseContext, message);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_CAMERA) {
            onActivityResultForCameraPreview(resultCode, data);

        } else if (requestCode == GET_CAMERA_EDIT) {
            onActivityResultForCameraEdit(resultCode, data);

        } else if (requestCode == GET_PHOTO) {
            onActivityResultForMedia(resultCode, data);

        } else if (requestCode == GET_FILE) {
            onActivityResultForFile(resultCode, data);

        } else if (requestCode == GET_DISCUSSION_AT) {
            onActivityResultForDiscussionAt(requestCode, data);

        } else if (requestCode == GET_CARD_USERLIST) {
            onActivityResultForCards(requestCode, data);

        } else if (requestCode == GET_VOIP_DISCUSSION_MEMBER_SELECT) {
            onActivityResultForDiscussionVoip(requestCode, data);

        } else if (requestCode == GET_DROPBOX_TO_SEND) {
            onActivityResultForDropbox(resultCode, data);
        }
        else if (requestCode == GET_ZOOM_MEETING_RESERVATION) {
            onActivityResultForGetBizconfMeetingReservation(resultCode, data);

        } else if (requestCode == GET_ZOOM_MEETING_INSTANT) {
            onActivityResultForGetBizconfMeetingInstant(resultCode, data);

        } else if (requestCode == GET_FEATURE_ROUTE) {
            onActivityResultForFeatureRoute(resultCode, data);
        }


        if (requestCode == SHARE_LOCATION_REQ_CODE) {
            onActivityResultForShareLocation(resultCode, data);
        }
    }

    private void onActivityResultForDiscussionAt(int resultCode, Intent data) {
        if (resultCode != 0 && data != null) {
            List<ShowListItem> selectContactList = SelectedContactList.getContactList();
            List<UserHandleInfo> handleInfoList = ContactHelper.transferContactList(selectContactList);

            if (!ListUtil.isEmpty(handleInfoList)) {
                mAtContacts.addAll(handleInfoList);
            }

            //如果@人名称拥有多个
            if (selectContactList.size() > 1) {
                mChatDetailInputView.isMore = true;
            }
            List<String> atText = getAtText(handleInfoList);
            mChatDetailInputView.isBackForAT = true;
            int tagSelection = mChatDetailInputView.getEmojiIconEditText().getSelectionStart();

            //批次生成
            for (String text : atText) {
                tagSelection += text.length();
                //生成编辑框文本
                SpannableString editTextString = getEditTextString(AtworkUtil.createBitmapByString(mActivity, text), text);
                mChatDetailInputView.appendText(editTextString);
            }
            if (mChatDetailInputView.isAppendAtMembersDirectly) {
                mChatDetailInputView.getEmojiIconEditText().setSelection(tagSelection);
            } else {
                mChatDetailInputView.getEmojiIconEditText().setSelection(tagSelection - 1);

            }
        }

        mChatDetailInputView.clearData();


    }


    /**
     * 根据bitmap和字符串生成SpannableString
     * 具有图片状态的字符串
     */
    private SpannableString getEditTextString(Bitmap bitmap, String str) {
        SpannableString spannableString = new SpannableString(str);
        if (bitmap != null) {
            ImageSpan imageSpan = new ImageSpan(mActivity, bitmap);
            //将字符串加上图片的状态
            spannableString.setSpan(imageSpan, 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }


    private List<String> getAtText(List<UserHandleInfo> selectedAtUsers) {
        List<String> atTextList = new ArrayList<>(selectedAtUsers.size());
        StringBuilder sb = new StringBuilder();
        Discussion discussion = DiscussionRepository.getInstance().queryDiscussionDetailInfo(mSession.identifier);
        if (discussion != null) {
            //记得减去自己
            if (discussion.mMemberList.size() - 1 == selectedAtUsers.size()) {
                sb.append("@" + AtworkApplicationLike.getResourceString(R.string.at_all_group));
                sb.append(" ");
                mAtAll = true;
                atTextList.add(sb.toString());
                return atTextList;
            }
        }


        for (int i = 0; i < selectedAtUsers.size(); i++) {
            sb.append("@").append(selectedAtUsers.get(i).mShowName).append(" ");
            atTextList.add(i, sb.toString());
            //清空StringBuffer
            sb.setLength(0);
        }
        return atTextList;
    }


    //处理视频文件返回
    @SuppressLint("StaticFieldLeak")
    @Override
    public void onMicroVideoFile(boolean isNewVideo, final String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        if (!FileUtil.isExist(filePath)) {
            return;
        }


        new AsyncTask<Void, MicroVideoChatMessage, MicroVideoChatMessage>() {

            @Override
            protected MicroVideoChatMessage doInBackground(Void... params) {

                String originalPath;
                if (isNewVideo) {
                    originalPath = filePath;
                } else {
                    originalPath = EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(filePath, false);

                }

                Bitmap thumbBitmap = ThumbnailUtils.createVideoThumbnail(originalPath, MediaStore.Images.Thumbnails.MINI_KIND);
                if (thumbBitmap == null) {
                    return null;
                }
                byte[] thumbByte = BitmapUtil.compressImageForQuality(thumbBitmap, AtworkConfig.CHAT_THUMB_SIZE);

                ShowListItem chatItem = getSendChatItem();
                final MicroVideoChatMessage microVideoChatMessage = MicroVideoChatMessage.newSendMicroVideoMessage(AtworkApplicationLike.baseContext, thumbByte, mLoginUser, mSession.identifier,
                        ParticipantType.User, mSession.type.getToType(),
                        mSession.mDomainId, BodyType.Video, mOrgId, chatItem, FileUtil.getSize(originalPath));


                publishProgress(microVideoChatMessage);


                if (isNewVideo) {
                    File newFile = FileHelper.getMicroVideoFileSendById(mActivity, microVideoChatMessage.deliveryId);
                    FileStreamHelper.rewrite(originalPath, FileHelper.getMicroVideoFileSendPathById(mActivity, microVideoChatMessage.deliveryId));

                    microVideoChatMessage.filePath = newFile.getAbsolutePath();

                } else {
                    microVideoChatMessage.filePath = new File(originalPath).getAbsolutePath();

                }

                ImageShowHelper.saveThumbnailImage(mActivity, microVideoChatMessage.deliveryId, thumbByte);

                if (thumbBitmap != null) {
                    thumbBitmap.recycle();
                    thumbBitmap = null;
                }
                return microVideoChatMessage;
            }

            @Override
            protected void onProgressUpdate(MicroVideoChatMessage... values) {
                if (!mMessageList.contains(values[0])) {
                    addChatMessage(values[0]);
                }

                updateSession(values[0]);
                MessageCache.getInstance().updateMessageList(mSession.identifier, mMessageList);

                refreshView();
            }

            @Override
            protected void onPostExecute(MicroVideoChatMessage microVideoChatMessage) {
                super.onPostExecute(microVideoChatMessage);
                if (microVideoChatMessage == null) {
                    return;
                }
                newSendWithProgressMessage(MediaCenterNetManager.COMMON_FILE, FileHelper.getMicroExistVideoFilePath(mActivity, microVideoChatMessage), microVideoChatMessage, !isNewVideo);
                hideAll();
            }
        }.executeOnExecutor(mMsgThreadService);
    }

    private void onActivityResultForCameraPreview(int resultCode, Intent data) {
        if (0 != resultCode && isAdded()) {
            List<MediaItem> cameraImageList = new ArrayList<>();
            MediaItem imageItem = new ImageItem();
            imageItem.filePath = mPhotoPath;
            cameraImageList.add(imageItem);


            Intent intent = MediaPreviewActivity.getImagePreviewIntent(getActivity(), MediaPreviewActivity.FromAction.CAMERA);
            intent.putExtra(MediaPreviewActivity.INTENT_IMAGE_SELECTED_LIST, (Serializable) cameraImageList);
            startActivityForResult(intent, GET_CAMERA_EDIT);

        }
    }

    /**
     * 处理拍照返回数据
     *
     * @param resultCode
     * @param data
     */
    @SuppressLint("StaticFieldLeak")
    private void onActivityResultForCameraEdit(int resultCode, Intent data) {
        if (resultCode != 0) {

            String orientationPath = data.getStringExtra(MediaPreviewActivity.DATA_IMG_PATH);
            if (StringUtils.isEmpty(orientationPath)) {
                orientationPath = mPhotoPath;
            }

            if (FileUtil.isEmptySize(orientationPath)) {
                return;
            }

            String finalOrientationPath = orientationPath;
            new AsyncTask<Void, ImageChatMessage, ImageChatMessage>() {
                @Override
                protected ImageChatMessage doInBackground(Void... params) {

                    String originalPath = EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(finalOrientationPath, false);

                    //先旋转缩略图, 提前显示在列表中
                    Bitmap thumbBitmap = ImageShowHelper.getRotateImageBitmap(originalPath, true);
                    //旋转后的缩略图会变大, 所以要再次压缩
                    byte[] thumbByte = BitmapUtil.compressImageForQuality(thumbBitmap, AtworkConfig.CHAT_THUMB_SIZE);

                    final ImageChatMessage imageMessage = newSendImageMessage(thumbByte, false);

                    imageMessage.info.height = thumbBitmap.getHeight();
                    imageMessage.info.width = thumbBitmap.getWidth();

                    //接着刷新列表
                    publishProgress(imageMessage);

                    //再旋转大图
                    Bitmap bitmap = ImageShowHelper.getRotateImageBitmap(originalPath, false);
                    byte[] content = BitmapUtil.Bitmap2Bytes(bitmap);

                    byte[] originalContent = ImageShowHelper.compressImageForOriginal(content);
                    String path = ImageShowHelper.saveOriginalImage(mActivity, imageMessage.deliveryId, originalContent);

                    ImageChatHelper.setImageMessageInfo(imageMessage, path);

                    ImageShowHelper.saveThumbnailImage(mActivity, imageMessage.deliveryId, thumbByte);

                    saveImageToGallery(thumbBitmap, bitmap, originalContent);


                    return imageMessage;
                }

                @Override
                protected void onProgressUpdate(ImageChatMessage... values) {
                    super.onProgressUpdate(values);

                    if (mMessageList.contains(values[0]) == false) {
                        addChatMessage(values[0]);
                    }

                    updateSession(values[0]);
                    MessageCache.getInstance().updateMessageList(mSession.identifier, mMessageList);

                    refreshView();
                }

                @Override
                protected void onPostExecute(ImageChatMessage imageChatMessage) {
                    newSendWithProgressMessage(MediaCenterNetManager.IMAGE_FILE, ImageShowHelper.getOriginalPath(mActivity, imageChatMessage.deliveryId), imageChatMessage, false);
                }


                private void saveImageToGallery(Bitmap thumbBitmap, Bitmap bitmap, byte[] originalContent) {
                    //FIXME 拍照后 android 手机 1有些会自动储存到 media 库,并且没有旋转处理 2有些不会存储到 media 库
                    //FIXME 下面操作将清除掉不再使用的原图, 并且从 media 库去除掉,再把经过处理的照片插入进去
                    File cameraFile = new File(finalOrientationPath);
                    cameraFile.delete();
                    bitmap.recycle();
                    bitmap = null;
                    thumbBitmap.recycle();
                    thumbBitmap = null;

                    ContentResolver resolver = mActivity.getContentResolver();
                    resolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            MediaStore.Images.Media.DATA + "=?",
                            new String[]{finalOrientationPath});
                    ImageShowHelper.saveImageToGallery(getActivity(), originalContent, null, false);
                }


            }.execute();
        }
    }

    private ImageChatMessage newSendImageMessage(byte[] thumbByte, boolean isGif) {
        ShowListItem chatItem = getSendChatItem();
        long deletionOnTime = -1;
        int readTime = -1;
        if (BurnModeHelper.isBurnMode()) {
            deletionOnTime = DomainSettingsManager.getInstance().getDeletionOnTime();
            readTime = DomainSettingsManager.getInstance().getEmphemeronReadTime(DomainSettingsManager.TextReadTimeWords.ImageRead);
        }
        return ImageChatMessage.newSendImageMessage(BaseApplicationLike.baseContext, thumbByte, mLoginUser, mSession.identifier,
                mSession.type.getToType(), mSession.mDomainId, isGif, BodyType.Image, mOrgId, chatItem, BurnModeHelper.isBurnMode(), readTime, deletionOnTime, null);
    }

    /**
     * 处理选取文件返回
     *
     * @param resultCode
     * @param data
     */

    private void onActivityResultForFile(int resultCode, Intent data) {
        if (resultCode != 0) {
            List<FileData> selectedFileData = (ArrayList<FileData>) data.getSerializableExtra(FileSelectActivity.RESULT_INTENT);
            sendFiles(selectedFileData);
        }
    }

    private void onActivityResultForDropbox(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            List<ChatPostMessage> messages = (ArrayList<ChatPostMessage>) data.getSerializableExtra(SaveToDropboxActivity.KEY_INTENT_SELECT_DROPBOX_SEND);
            if (ListUtil.isEmpty(messages)) {
                return;
            }

            for (ChatPostMessage message : messages) {
                if (message instanceof FileTransferChatMessage) {
                    FileTransferChatMessage fileTransferChatMessage = (FileTransferChatMessage) message;
                    MediaCenterNetManager.linkMedia(BaseApplicationLike.baseContext, fileTransferChatMessage.mediaId, new MediaCenterNetManager.OnMediaLinkedListener() {
                        @Override
                        public void mediaLinked(String linkedMediaId) {
                            if (TextUtils.isEmpty(linkedMediaId)) {
                                reSendFileMessage(message);
                            } else {
                                fileTransferChatMessage.mediaId = linkedMediaId;
                                reSendFileMessage(fileTransferChatMessage);
                            }
                        }
                    });
                }
            }

            DropboxBaseActivity.mSelectedDropbox.clear();
        }
    }

    private void onActivityResultForGetBizconfMeetingReservation(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            FragmentActivity activity = getActivity();
            if (null == activity) {
                return;
            }

            List<ShowListItem> contacts = SelectedContactList.getContactList();
            ContactPlugin_New.setContactSelectedCache(contacts);

            contacts.clear();

            HashMap<String, String> params = new HashMap<>();
            params.put("w6sContactCache", "1");
            if (null != mDiscussion) {
                params.put("discussionId", mDiscussion.mDiscussionId);
            }


            WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                    .setUrl(UrlHandleHelper.addParams(AtworkConfig.ZOOM_CONFIG.getUrl(), params))
                    .setNeedShare(false)
                    .setHideTitle(false);

            UrlRouteHelper.routeUrl(activity, webViewControlAction);

//            startActivity(WebViewActivity.getIntent(getActivity(), webViewControlAction));
        }
    }


    private void onActivityResultForGetBizconfMeetingInstant(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            //多人群聊呼叫
            createZoomMeetingInstant();


        }
    }

    private void onActivityResultForShareLocation(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            sendShareLocationMessage(data);
        }
    }

    private void createZoomMeetingInstant() {
        ContactQueryHelper.queryLoginContact(mDiscussion.getOrgCodeCompatible(), loginContact -> {
            List<ShowListItem> selectContactList = SelectedContactList.getContactList();

            if (!ListUtil.isEmpty(selectContactList)) {
                selectContactList.add(0, loginContact);


                String discussionOrgCode = StringUtils.EMPTY;
                if (null != mDiscussion && mDiscussion.showEmployeeInfo()) {
                    discussionOrgCode = mDiscussion.getOrgCodeCompatible();
                }


                //群类型的 session info
                MeetingInfo meetingInfo = new MeetingInfo();
                meetingInfo.mType = MeetingInfo.Type.DISCUSSION;
                meetingInfo.mId = mSession.identifier;
                meetingInfo.mOrgCode = discussionOrgCode;

                //todo 后面去除掉, 会畅后台不依赖组织才对
                if (StringUtils.isEmpty(meetingInfo.mOrgCode)) {
                    meetingInfo.mOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext);
                }

                ZoomVoipManager.INSTANCE.setCurrentVoipMeeting(AtworkApplicationLike.baseContext, true, true, selectContactList, null, meetingInfo, VoipType.VIDEO, null, null);

                CurrentVoipMeeting currentVoipMeeting = ZoomVoipManager.INSTANCE.getCurrentVoipMeeting();
                if (null == currentVoipMeeting) {
                    return;
                }

                Activity activity = getActivity();
                if (null == activity) {
                    return;
                }

                ProgressDialogHelper progressDialogHelper = new ProgressDialogHelper(getActivity());
                progressDialogHelper.show(false);

                ZoomVoipManager.INSTANCE.changeCallState(CallState.CallState_StartCall);

                //创建会畅会议, 加入, 并调用sdk
                ZoomVoipManager.INSTANCE.createMeeting(AtworkApplicationLike.baseContext, meetingInfo, VoipType.VIDEO, currentVoipMeeting.mCallParams.getCallMemberList(), new VoipManager.OnCreateAndQueryVoipMeetingListener() {
                    @Override
                    public void onSuccess(CreateOrQueryMeetingResponseJson responseJson) {
                        String workplusVoipMeetingId = responseJson.mResult.mMeetingId;

                        ZoomVoipManager.INSTANCE.joinMeeting(AtworkApplicationLike.baseContext, meetingInfo, workplusVoipMeetingId, VoipType.VIDEO, new VoipManager.OnGetJoinTokenListener() {

                            @Override
                            public void onSuccess(String token) {
                                ZoomVoipManager.INSTANCE.changeCallState(CallState.CallState_Calling);

                                progressDialogHelper.dismiss();
                                LogUtil.e("bizconf token -> " + token);

                                HandleMeetingInfo handleMeetingInfo = new HandleMeetingInfo();
                                handleMeetingInfo.setMeetingUrl(token);
                                ZoomManager.INSTANCE.startMeeting(activity, handleMeetingInfo);

                            }


                            @Override
                            public void networkFail(int errorCode, String errorMsg) {
                                progressDialogHelper.dismiss();

                                ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Voip, errorCode, errorMsg);
                                ZoomVoipManager.INSTANCE.stopCall();
                            }


                        });


                    }

                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        progressDialogHelper.dismiss();

                        ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Voip, errorCode, errorMsg);
                        ZoomVoipManager.INSTANCE.stopCall();

                    }
                });


//                    Intent intent = VoipSelectModeActivity.getIntent(mActivity, meetingInfo, (ArrayList<? extends ShowListItem>) selectContactList, discussionOrgCode);
//                    startActivity(intent);
            }

        });
    }

    private void onActivityResultForFeatureRoute(int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            DiscussionFeature discussionFeature = data.getParcelableExtra("DiscussionFeature");
            if (null != discussionFeature) {
                routeDiscussionFeature(discussionFeature);
            }
        }
    }

    private void reSendFileMessage(ChatPostMessage message) {
        message.to = mSession.identifier;
        message.mToType = mSession.type.getToType();
        message.mToDomain = mSession.mDomainId;
        if (getSendChatItem() != null) {
            message.mDisplayAvatar = getSendChatItem().getAvatar();
            message.mDisplayName = getSendChatItem().getTitle();
        } else {
            message.mDisplayAvatar = mSession.avatar;
            message.mDisplayName = mSession.name;
        }

        message.mOrgId = mOrgId;
        reSendMessage(message);
    }

    /**
     * 处理选取媒体(视频, 图片)返回
     *
     * @param resultCode
     * @param data
     */
    @SuppressLint("StaticFieldLeak")
    private void onActivityResultForMedia(int resultCode, final Intent data) {
        hideAll();
        if (resultCode != 0) {
            if (data == null) {
                return;
            }
            final List<MediaItem> selectedList = (List<MediaItem>) data.getSerializableExtra(MediaSelectActivity.RESULT_SELECT_IMAGE_INTENT);
            mSelectOriginalMode = data.getBooleanExtra(MediaSelectActivity.DATA_SELECT_FULL_MODE, false);
            String sendMode = data.getStringExtra(MediaSelectActivity.DATA_SELECT_SEND_MODE);
            if (selectedList.isEmpty()) {
                return;
            }

            sendImgOnActivityResultForMedia(CollectionsKt.filter(selectedList, mediaItem -> mediaItem instanceof ImageItem), mSelectOriginalMode, sendMode);
            sendVideoOnActivityResultForMedia(selectedList);
        }
    }

    private void sendVideoOnActivityResultForMedia(List<MediaItem> selectedList) {
        for (final MediaItem videoItem : selectedList) {
            if (null == videoItem || !(videoItem instanceof VideoItem)) {
                continue;
            }

            VideoItem videoItemSend = (VideoItem) videoItem;

            if (videoItemSend.isLegalInChat()) {
                onMicroVideoFile(false, videoItemSend.filePath);

            } else {
                sendFiles(ListUtil.makeSingleList(FileDataUtil.convertImageItem2FileData(videoItemSend)));
            }

        }
    }

    @SuppressLint("StaticFieldLeak")
    private void sendImgOnActivityResultForMedia(List<MediaItem> selectedList, boolean isOriginalMode, String sendMode) {
        if (getStrings(R.string.image_comment).equals(sendMode)) {
            popAnnoImageCommentView(selectedList);

        } else {
            sendImgOnActivityResultForMediaDirectly(selectedList, isOriginalMode);

        }


    }

    class ProgressAction {
        public AnnoImageChatMessage annoImageChatMessage;
        public int action;


    }

    @SuppressLint("StaticFieldLeak")
    private void sendImgOnActivityResultForMediaAssemble(@Nullable AnnoImageChatMessage annoImageChatMessage, List<MediaItem> selectedList, boolean isOriginalMode, String comment) {

        new AsyncTask<Void, ProgressAction, AnnoImageChatMessage>() {

            @Override
            protected AnnoImageChatMessage doInBackground(Void... voids) {

                long startTime = System.currentTimeMillis();

                List<ImageContentInfo> imageContentInfos = new ArrayList<>();


                AnnoImageChatMessage annoImageChatMessageSend = null;
                if (null == annoImageChatMessage) {
                    for (final MediaItem imageItem : selectedList) {

                        if (null == imageItem || FileUtil.isEmptySize(imageItem.filePath) || !(imageItem instanceof ImageItem)) {
                            continue;
                        }

                        ImageContentInfo imageContentInfo = new ImageContentInfo();


                        String imagePath = imageItem.filePath;
                        imagePath = EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(imagePath, false);
                        Bitmap thumbBitmap = ImageShowHelper.getRotateImageBitmap(imagePath, true);
                        byte[] thumbByte = BitmapUtil.compressImageForQuality(thumbBitmap, AtworkConfig.CHAT_THUMB_SIZE);

                        imageContentInfo.thumbnail = thumbByte;
                        imageContentInfo.filePath = imagePath;


                        imageContentInfos.add(imageContentInfo);


                        thumbBitmap.recycle();
                        thumbBitmap = null;
                    }


                    String commentUsing = comment;
                    if (mAtAll) {
                        commentUsing = AtworkUtil.getAtAllI18n(comment);
                    }


                    AnnoImageChatMessage.Builder builder = AnnoImageChatMessage
                            .newBuilder()
                            .setComment(commentUsing)
                            .setContentInfos(imageContentInfos)
                            .setTo(mSession.identifier)
                            .setToDomainId(mSession.mDomainId)
                            .setToType(mSession.type.getToType());


                    if (null != getSendChatItem()) {
                        builder.setDisplayName(getSendChatItem().getTitle()).setDisplayAvatar(getSendChatItem().getAvatar());

                    }

                    annoImageChatMessageSend = builder.build();

                    if (mAtContacts.size() > 0) {
                        if (mAtAll) {
                            annoImageChatMessageSend.setAtAll(true);
                        } else {
                            annoImageChatMessageSend.setAtUsers(mAtContacts);
                        }

                    }


                } else {
                    annoImageChatMessageSend = annoImageChatMessage;

                    imageContentInfos.addAll(annoImageChatMessageSend.contentInfos);


                }

                ProgressAction cleanInputProgressAction = new ProgressAction();
                cleanInputProgressAction.annoImageChatMessage = annoImageChatMessageSend;
                cleanInputProgressAction.action = 2;

                publishProgress(cleanInputProgressAction);


                //clean all progress
                for (ImageContentInfo imageContentInfo : imageContentInfos) {
                    imageContentInfo.progress = 0;
                }


                LogUtil.e("压缩耗时 : " + (System.currentTimeMillis() - startTime));


                ProgressAction refreshMessageUIProgressAction = new ProgressAction();
                refreshMessageUIProgressAction.annoImageChatMessage = annoImageChatMessageSend;
                refreshMessageUIProgressAction.action = 0;

                //先刷新聊天界面展示出来
                publishProgress(refreshMessageUIProgressAction);


                for (ImageContentInfo imageContentInfo : imageContentInfos) {

                    if (GifChatHelper.isGif(imageContentInfo.filePath)) {
                        imageContentInfo.isGif = true;

                        byte[] bitmapByte = FileUtil.readFile(imageContentInfo.filePath);
                        String originalPath = ImageShowHelper.saveOriginalImage(mActivity, imageContentInfo.deliveryId, bitmapByte);
                        ImageShowHelper.saveThumbnailImage(mActivity, imageContentInfo.deliveryId, imageContentInfo.thumbnail);

                        ImageChatHelper.setImageMessageInfo(imageContentInfo, originalPath);
                        continue;
                    }


                    //旋转原图
                    Bitmap bitmap = ImageShowHelper.getRotateImageBitmap(imageContentInfo.filePath, false);
                    byte[] content = BitmapUtil.Bitmap2Bytes(bitmap);
                    byte[] originalContent = ImageShowHelper.compressImageForOriginal(content);
                    String originalPath = ImageShowHelper.saveOriginalImage(mActivity, imageContentInfo.deliveryId, originalContent);
                    ImageShowHelper.saveThumbnailImage(mActivity, imageContentInfo.deliveryId, imageContentInfo.thumbnail);

                    if (isOriginalMode) {
                        ImageChatHelper.setImageMessageInfo(imageContentInfo, imageContentInfo.filePath);
                        imageContentInfo.fullImgPath = imageContentInfo.filePath;

                    } else {
                        ImageChatHelper.setImageMessageInfo(imageContentInfo, originalPath);

                    }

                    bitmap.recycle();
                    bitmap = null;

                }


                //轮流上传
                checkSendAnnoImageChatMessage(annoImageChatMessageSend, imageContentInfos, isOriginalMode);

                annoImageChatMessageSend.fileStatus = FileStatus.SENDED;

                return annoImageChatMessageSend;
            }

            private void checkSendAnnoImageChatMessage(AnnoImageChatMessage annoImageChatMessage, List<ImageContentInfo> imageContentInfos, boolean isOriginalMode) {
                for (ImageContentInfo imageContentInfo : imageContentInfos) {
                    String thumbnailPath = ImageShowHelper.getThumbnailPath(AtworkApplicationLike.baseContext, imageContentInfo.deliveryId);
                    String thumbnailPathCheck = EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(thumbnailPath, true);


                    if (StringUtils.isEmpty(imageContentInfo.thumbnailMediaId)) {
                        //先上传缩略图
                        UploadFileParamsMaker uploadThumbFileParamsMaker = UploadFileParamsMaker
                                .newRequest()
                                .setType(MediaCenterNetManager.IMAGE_FILE)
                                .setMsgId(imageContentInfo.deliveryId)
                                .setFilePath(thumbnailPathCheck);
//                             .setMediaProgressListener((value, size) -> {
//                                 imageContentInfo.progress = (int) value;
////                                     publishProgress();
//                             });

                        HttpResult httpResult = MediaCenterHttpURLConnectionUtil.getInstance().uploadImageFile(AtworkApplicationLike.baseContext, uploadThumbFileParamsMaker);


                        String thumbMediaId = null;
                        if (httpResult.isRequestSuccess()) {
                            BasicResponseJSON basicResponseJSON = httpResult.resultResponse;
                            thumbMediaId = MediaCenterNetManager.getMediaId(basicResponseJSON);


                        }

                        if (!StringUtils.isEmpty(thumbMediaId)) {
                            imageContentInfo.thumbnailMediaId = thumbMediaId;
                        } else {
                            //失败处理

                            failAnnoImg(annoImageChatMessage);
                            return;
                        }

                        LogUtil.e("上传获取缩略图 id" + httpResult.result);

                    }


                    if (isOriginalMode) {
                        handleUploadFullAnnoImage(annoImageChatMessage, imageContentInfo);

                        return;
                    }


                    handleUploadCommonAnnoImage(annoImageChatMessage, imageContentInfo);


                }
            }

            private void handleUploadFullAnnoImage(AnnoImageChatMessage annoImageChatMessage, ImageContentInfo imageContentInfo) {
                if (StringUtils.isEmpty(imageContentInfo.mediaId) || StringUtils.isEmpty(imageContentInfo.fullMediaId)) {
                    String originalPathCheck = EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(imageContentInfo.filePath, true);
                    String checkSum = MD5Utils.getDigest(originalPathCheck);
                    long expiredTime = DomainSettingsManager.getInstance().getChatFileExpiredTime();

                    HttpResult httpResult = MediaCenterSyncNetService.getInstance().getCompressMediaInfo(AtworkApplicationLike.baseContext, checkSum);

                    String compressMediaIdInstant = null;
                    String fullMediaIdInstant = null;

                    if (httpResult.isRequestSuccess()) {
                        MediaCompressResponseJson compressResponseJson = (MediaCompressResponseJson) httpResult.resultResponse;
                        if (compressResponseJson.isLegal()) {
                            compressMediaIdInstant = compressResponseJson.mMediaCompressInfo.mCompressImg.mMediaId;
                            fullMediaIdInstant = compressResponseJson.mMediaCompressInfo.mOriginalImg.mMediaId;

                        }

                    }


                    if (!StringUtils.isEmpty(compressMediaIdInstant)
                            && !StringUtils.isEmpty(fullMediaIdInstant)) {
                        imageContentInfo.progress = 100;
                        imageContentInfo.mediaId = compressMediaIdInstant;
                        imageContentInfo.fullMediaId = fullMediaIdInstant;

                        ProgressAction progressAction = new ProgressAction();
                        progressAction.annoImageChatMessage = annoImageChatMessage;
                        progressAction.action = 1;

                        publishProgress(progressAction);
                    } else {
                        httpResult = MediaCenterHttpURLConnectionUtil.getInstance().uploadFullImageFile(AtworkApplicationLike.baseContext, imageContentInfo.deliveryId, checkSum, originalPathCheck, new MediaCenterHttpURLConnectionUtil.MediaProgressListener() {
                            @Override
                            public void progress(double value, double size) {
                                imageContentInfo.progress = (int) value;

                                ProgressAction progressAction = new ProgressAction();
                                progressAction.annoImageChatMessage = annoImageChatMessage;
                                progressAction.action = 1;

                                publishProgress(progressAction);
                            }
                        });

                        LogUtil.e("上传原图 " + httpResult.result);


                        String compressMediaId = null;
                        String fullMediaId = null;

                        if (httpResult.isNetSuccess()) {
                            MediaCompressResponseJson compressResponseJson = JsonUtil.fromJson(httpResult.result, MediaCompressResponseJson.class);
                            if (null != compressResponseJson && null != compressResponseJson.mMediaCompressInfo) {
                                fullMediaId = compressResponseJson.mMediaCompressInfo.mOriginalImg.mMediaId;
                                compressMediaId = compressResponseJson.mMediaCompressInfo.mCompressImg.mMediaId;
                            } else {
                                compressMediaId = httpResult.result;
                                fullMediaId = httpResult.result;
                            }
                        }


                        if (!StringUtils.isEmpty(compressMediaId)
                                && !StringUtils.isEmpty(fullMediaId)) {
                            imageContentInfo.progress = 100;
                            imageContentInfo.mediaId = compressMediaId;
                            imageContentInfo.fullMediaId = fullMediaId;

                            ProgressAction progressAction = new ProgressAction();
                            progressAction.annoImageChatMessage = annoImageChatMessage;
                            progressAction.action = 1;

                            publishProgress(progressAction);
                        } else {
                            //失败处理
                            failAnnoImg(annoImageChatMessage);
                        }

                    }

                }
            }

            private void handleUploadCommonAnnoImage(AnnoImageChatMessage annoImageChatMessage, ImageContentInfo imageContentInfo) {
                if (StringUtils.isEmpty(imageContentInfo.mediaId)) {
                    String originalPathCheck = EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(imageContentInfo.filePath, true);

                    String checkSum = MD5Utils.getDigest(originalPathCheck);

                    //step1. 优先检查网络端文件MD5, 如果存在，直接使用返回的MediaId，否则上传文件
                    HttpResult httpResult = MediaCenterSyncNetService.getInstance().getMediaInfo(AtworkApplicationLike.baseContext, checkSum, "digest");

                    LogUtil.e("上传获取md5 " + httpResult.result);


                    String originalMediaIdInstant = null;
                    if (httpResult.isRequestSuccess()) {
                        BasicResponseJSON basicResponseJSON = httpResult.resultResponse;
                        originalMediaIdInstant = MediaCenterNetManager.getMediaId(basicResponseJSON);

                    }

                    //说明服务端有该文件
                    if (!StringUtils.isEmpty(originalMediaIdInstant)) {
                        imageContentInfo.progress = 100;
                        imageContentInfo.mediaId = originalMediaIdInstant;

                        ProgressAction progressAction = new ProgressAction();
                        progressAction.annoImageChatMessage = annoImageChatMessage;
                        progressAction.action = 1;

                        publishProgress(progressAction);

                    } else {

                        //上传压缩高清图
                        UploadFileParamsMaker uploadOriginalParamsMaker = UploadFileParamsMaker
                                .newRequest()
                                .setType(MediaCenterNetManager.IMAGE_FILE)
                                .setMsgId(imageContentInfo.deliveryId)
                                .setFilePath(originalPathCheck)
                                .setMediaProgressListener((value, size) -> {
                                    imageContentInfo.progress = (int) value;

                                    ProgressAction progressAction = new ProgressAction();
                                    progressAction.annoImageChatMessage = annoImageChatMessage;
                                    progressAction.action = 1;

                                    publishProgress(progressAction);
                                });


                        httpResult = MediaCenterHttpURLConnectionUtil.getInstance().uploadImageFile(AtworkApplicationLike.baseContext, uploadOriginalParamsMaker);

                        LogUtil.e("上传压缩高清图 " + httpResult.result);

                        String originalMediaId = null;
                        if (httpResult.isRequestSuccess()) {
                            BasicResponseJSON basicResponseJSON = httpResult.resultResponse;
                            originalMediaId = MediaCenterNetManager.getMediaId(basicResponseJSON);

                        }

                        if (!StringUtils.isEmpty(originalMediaId)) {
                            imageContentInfo.progress = 100;
                            imageContentInfo.mediaId = originalMediaId;
                            ProgressAction progressAction = new ProgressAction();
                            progressAction.annoImageChatMessage = annoImageChatMessage;
                            progressAction.action = 1;

                            publishProgress(progressAction);

                        } else {
                            //失败处理
                            failAnnoImg(annoImageChatMessage);
                        }


                    }


                }
            }

            @Override
            protected void onProgressUpdate(ProgressAction... values) {
                ProgressAction progressAction = values[0];
                AnnoImageChatMessage annoImageChatMessage = progressAction.annoImageChatMessage;
                if (0 == progressAction.action) {
                    if (!mMessageList.contains(annoImageChatMessage)) {
                        addChatMessage(annoImageChatMessage);
                    }
                    updateSession(annoImageChatMessage);
                    MessageCache.getInstance().updateMessageList(mSession.identifier, mMessageList);
                    refreshView();
                    return;
                }

                if (1 == progressAction.action) {
//                    annoImageChatMessage
                    ChatDetailExposeBroadcastSender.refreshMessageListViewUI();
                    return;
                }

                if (2 == progressAction.action) {
                    mAtContacts.clear();
                    mChatDetailInputView.clearEditText();
                    mAtAll = false;
                }


            }


            @Override
            protected void onPostExecute(AnnoImageChatMessage annoImageChatMessage) {
                //数据都准备好了, 发送消息
                newSendMessage(annoImageChatMessage);
            }


        }.executeOnExecutor(mMsgThreadService);


    }

    private void failAnnoImg(AnnoImageChatMessage annoImageChatMessage) {
        annoImageChatMessage.fileStatus = FileStatus.SEND_FAIL;
        annoImageChatMessage.chatStatus = ChatStatus.Not_Send;
        ChatDaoService.getInstance().insertOrUpdateMessage(BaseApplicationLike.baseContext, annoImageChatMessage);
//        MediaCenterNetManager.removeMediaUploadListener(this);
//        ChatSessionDataWrap.getInstance().removeChatInFileStreaming(mSession.identifier, annoImageChatMessage.deliveryId);

        ChatDetailExposeBroadcastSender.refreshMessageListViewUI();
        ChatSessionDataWrap.getInstance().notifyMessageSendFail(annoImageChatMessage.deliveryId);
    }

    private void sendImgOnActivityResultForMediaDirectly(List<MediaItem> selectedList, boolean isOriginalMode) {
        for (final MediaItem imageItem : selectedList) {

            if (null == imageItem || FileUtil.isEmptySize(imageItem.filePath) || !(imageItem instanceof ImageItem)) {
                continue;
            }

            new AsyncTask<Void, ImageChatMessage, ImageChatMessage>() {
                @Override
                protected ImageChatMessage doInBackground(Void... params) {
                    ImageChatMessage imageMessage = null;

                    String imagePath = imageItem.filePath;
                    imagePath = EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(imagePath, false);

                    if (GifChatHelper.isGif(imagePath)) {
                        imageMessage = handleGif(imagePath);
                        return imageMessage;

                    }

                    //如果是三星系统 将图片进行旋转
                    if (FileUtil.isExist(imagePath)) {
                        return handleCompressImgAdjustOrientation(imagePath, isOriginalMode);

                    }

                    imageMessage = handleCompressImg(imagePath, isOriginalMode);

                    return imageMessage;
                }


                @NonNull
                private ImageChatMessage handleCompressImg(String imagePath, boolean isFull) {
                    ImageChatMessage imageMessage;
                    byte[] thumbnails = ImageShowHelper.compressImageForThumbnail(imagePath);
                    byte[] bitmapCompressedByte = ImageShowHelper.compressImageForCamera(imagePath);

                    imageMessage = newSendImageMessage(thumbnails, false);
                    publishProgress(imageMessage);

                    ImageShowHelper.saveThumbnailImage(mActivity, imageMessage.deliveryId, thumbnails);
                    //保存压缩的图片用于显示
                    String originalPath = ImageShowHelper.saveOriginalImage(mActivity, imageMessage.deliveryId, bitmapCompressedByte);

                    if (isFull) {
                        ImageChatHelper.setImageMessageInfo(imageMessage, imagePath);
                        imageMessage.fullImgPath = imagePath;

                    } else {
                        ImageChatHelper.setImageMessageInfo(imageMessage, originalPath);

                    }

                    return imageMessage;
                }


                @NonNull
                private ImageChatMessage handleCompressImgAdjustOrientation(String imagePath, boolean isFull) {
                    ImageChatMessage imageMessage;//先旋转缩略图
                    Bitmap thumbBitmap = ImageShowHelper.getRotateImageBitmap(imagePath, true);
                    byte[] thumbByte = BitmapUtil.compressImageForQuality(thumbBitmap, AtworkConfig.CHAT_THUMB_SIZE);
                    imageMessage = newSendImageMessage(thumbByte, false);
                    //先填充原图的高宽进行优先的 UI 显示
//                                ImageChatHelper.setImageMessageInfo(imageMessage, imageItem.filePath);

                    publishProgress(imageMessage);
                    //旋转原图
                    Bitmap bitmap = ImageShowHelper.getRotateImageBitmap(imagePath, false);
                    byte[] content = BitmapUtil.Bitmap2Bytes(bitmap);
                    byte[] originalContent = ImageShowHelper.compressImageForOriginal(content);
                    String originalPath = ImageShowHelper.saveOriginalImage(mActivity, imageMessage.deliveryId, originalContent);
                    ImageShowHelper.saveThumbnailImage(mActivity, imageMessage.deliveryId, thumbByte);

                    if (isFull) {
                        ImageChatHelper.setImageMessageInfo(imageMessage, imagePath);
                        imageMessage.fullImgPath = imagePath;

                    } else {
                        ImageChatHelper.setImageMessageInfo(imageMessage, originalPath);

                    }

                    bitmap.recycle();
                    bitmap = null;
                    thumbBitmap.recycle();
                    thumbBitmap = null;

                    return imageMessage;
                }

                @NonNull
                private ImageChatMessage handleGif(String imagePath) {
                    byte[] bitmapByte = null;
                    byte[] thumbnails = null;
                    ImageChatMessage imageMessage;

                    try {
                        GifDrawable gif = new GifDrawable(imagePath);
                        //the first frame is the cover
                        Bitmap gifBitmap = gif.seekToFrameAndGet(0);
                        thumbnails = BitmapUtil.compressImageForQuality(gifBitmap, AtworkConfig.CHAT_THUMB_SIZE);

                        gif.recycle();
                        gifBitmap.recycle();
                        gif = null;
                        gifBitmap = null;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    imageMessage = newSendImageMessage(thumbnails, true);
                    publishProgress(imageMessage);
                    bitmapByte = FileUtil.readFile(imagePath);
                    String originalPath = ImageShowHelper.saveOriginalImage(mActivity, imageMessage.deliveryId, bitmapByte);
                    ImageShowHelper.saveThumbnailImage(mActivity, imageMessage.deliveryId, thumbnails);

                    ImageChatHelper.setImageMessageInfo(imageMessage, originalPath);
                    return imageMessage;
                }

                @Override
                protected void onProgressUpdate(ImageChatMessage... values) {
                    super.onProgressUpdate(values);
                    if (!mMessageList.contains(values[0])) {
                        addChatMessage(values[0]);
                    }
                    updateSession(values[0]);
                    MessageCache.getInstance().updateMessageList(mSession.identifier, mMessageList);
                    refreshView();

                }

                @Override
                protected void onPostExecute(ImageChatMessage imageChatMessage) {
                    if (null != imageChatMessage) {

                        String fileType;
                        String path;
                        if (imageChatMessage.isFullMode()) {
                            fileType = MediaCenterNetManager.IMAGE_FULL_FILE;
                            path = imageChatMessage.fullImgPath;

                        } else {
                            fileType = MediaCenterNetManager.IMAGE_FILE;
                            path = ImageShowHelper.getOriginalPath(mActivity, imageChatMessage.deliveryId);

                        }
                        newSendWithProgressMessage(fileType, path, imageChatMessage, false, true);

                    }
                }
            }.executeOnExecutor(mMsgThreadService);
        }
    }

    /**
     * 处理选取名片返回
     *
     * @param resultCode
     * @param data
     */
    private void onActivityResultForCards(int resultCode, Intent data) {
        if (resultCode != 0) {
            List<ShowListItem> contactCards = new ArrayList<>();
            contactCards.addAll(SelectedContactList.getContactList());
            SelectedContactList.getContactList().clear();

            if (!ListUtil.isEmpty(contactCards)) {

                AtworkAlertDialog dialog = new AtworkAlertDialog(getActivity(), AtworkAlertDialog.Type.SIMPLE);
                dialog.setContent(R.string.send_card)
                        .setClickBrightColorListener(dialog1 -> sendCardShareMessage(contactCards))
                        .show();
            }
        }
    }

    private void onActivityResultForDiscussionVoip(int resultCode, Intent data) {
        if (0 != resultCode && null != data) {
            ContactQueryHelper.queryLoginContact(mDiscussion.getOrgCodeCompatible(), loginContact -> {
                List<ShowListItem> selectContactList = SelectedContactList.getContactList();

                if (!ListUtil.isEmpty(selectContactList)) {
                    selectContactList.add(0, loginContact);

                    //群类型的 session info
                    MeetingInfo meetingInfo = new MeetingInfo();
                    meetingInfo.mType = MeetingInfo.Type.DISCUSSION;
                    meetingInfo.mId = mSession.identifier;

                    String discussionOrgCode = StringUtils.EMPTY;
                    if (null != mDiscussion && mDiscussion.showEmployeeInfo()) {
                        discussionOrgCode = mDiscussion.getOrgCodeCompatible();
                    }

                    Intent intent = VoipSelectModeActivity.getIntent(mActivity, meetingInfo, (ArrayList<? extends ShowListItem>) selectContactList, discussionOrgCode);
                    startActivity(intent);
                }

            });

        }
    }

    private ChatPostMessage initShareChatMessage(ArticleItem articleItem, String toUserId, String toDomainId, ShareChatMessage.ShareType shareType, @Nullable Employee selectedEmp) {
        User cardUser = UserManager.getInstance().queryUserInSyncByUserId(getActivity(), toUserId, toDomainId);
        ShowListItem sender = mLoginUser;
        ChatPostMessage shareChatMessage = ShareChatMessage.newSendShareMessage(AtworkApplicationLike.baseContext, articleItem, sender.getId(), sender.getDomainId(), sender.getTitle(), sender.getAvatar(), ParticipantType.User, BodyType.Share, shareType);
        shareChatMessage.to = mSession.identifier;
        shareChatMessage.mToDomain = mSession.mDomainId;
        shareChatMessage.mToType = mSession.type.getToType();
        if (null != cardUser && shareType.equals(ShareChatMessage.ShareType.BusinessCard)) {
            articleItem.setBusinessCardData(cardUser, selectedEmp);
        }

        ShowListItem sendItem = getSendChatItem();

        if (null != sendItem) {
            shareChatMessage.mDisplayAvatar = sendItem.getAvatar();
            shareChatMessage.mDisplayName = sendItem.getTitle();
        }
        if (!TextUtils.isEmpty(mSession.orgId)) {
            shareChatMessage.orgId = mSession.orgId;
            shareChatMessage.mOrgId = mSession.orgId;
        }
        return shareChatMessage;
    }

    @SuppressLint("StaticFieldLeak")
    private void sendCardShareMessage(List<ShowListItem> contactList) {
        for (ShowListItem contact : contactList) {

            new AsyncTask<Void, Void, ChatPostMessage>() {
                @Override
                protected ChatPostMessage doInBackground(Void... params) {
                    ArticleItem articleItem = new ArticleItem();
                    Employee selectedEmp = null;
                    if (contact instanceof Employee) {
                        selectedEmp = (Employee) contact;
                    }
                    return initShareChatMessage(articleItem, contact.getId(), contact.getDomainId(), ShareChatMessage.ShareType.BusinessCard, selectedEmp);
                }

                @Override
                protected void onPostExecute(ChatPostMessage shareChatMessage) {
                    if (null != shareChatMessage) {
                        newSendMessage(shareChatMessage);
                    }
                }
            }.executeOnExecutor(mMsgThreadService);


        }

    }

    private void sendShareLocationMessage(Intent data) {
        if (data == null) {
            return;
        }
        GetLocationInfo locationInfo = data.getParcelableExtra("CURRENT_LOCATION");
        if (locationInfo == null) {
            return;
        }
        ArticleItem articleItem = new ArticleItem();

        articleItem.mAoi = locationInfo.mAoiName;
        StringBuilder address = new StringBuilder();
        if (!TextUtils.isEmpty(locationInfo.mProvince) && !locationInfo.mAddress.startsWith(locationInfo.mProvince)) {
            address.append(locationInfo.mProvince);
            if (!TextUtils.isEmpty(locationInfo.mCity)) {
                address.append(locationInfo.mCity);
            }
            if (!TextUtils.isEmpty(locationInfo.mDistrict)) {
                address.append(locationInfo.mDistrict);
            }
        }
        address.append(locationInfo.mAddress);

        articleItem.mAddress = address.toString();
        articleItem.mLatitude = locationInfo.mLatitude;
        articleItem.mLongitude = locationInfo.mLongitude;
        ShowListItem sender = mLoginUser;
        ChatPostMessage shareChatMessage = ShareChatMessage.newSendShareMessage(AtworkApplicationLike.baseContext,
                articleItem, sender.getId(), sender.getDomainId(), sender.getTitle(), sender.getAvatar(), ParticipantType.User,
                BodyType.Share, ShareChatMessage.ShareType.Loc);
        shareChatMessage.to = mSession.identifier;
        shareChatMessage.mToDomain = mSession.mDomainId;
        shareChatMessage.mToType = mSession.type.getToType();

        ShowListItem sendItem = getSendChatItem();

        if (null != sendItem) {
            shareChatMessage.mDisplayAvatar = sendItem.getAvatar();
            shareChatMessage.mDisplayName = sendItem.getTitle();
        }
        if (!TextUtils.isEmpty(mSession.orgId)) {
            shareChatMessage.orgId = mSession.orgId;
            shareChatMessage.mOrgId = mSession.orgId;
        }
        newSendMessage(shareChatMessage);
    }


    private void sendAnnoFileMessage(String text, FileData fileDataHolding) {

        if (mAtAll) {
            text = AtworkUtil.getAtAllI18n(text);
        }


        AnnoFileTransferChatMessage.Builder builder = AnnoFileTransferChatMessage
                .newBuilder()
                .setComment(text)
                .setFileData(fileDataHolding)
                .setTo(mSession.identifier)
                .setToDomainId(mSession.mDomainId)
                .setToType(mSession.type.getToType());


        if (null != getSendChatItem()) {
            builder.setDisplayName(getSendChatItem().getTitle()).setDisplayAvatar(getSendChatItem().getAvatar());

        }

        AnnoFileTransferChatMessage annoFileTransferChatMessage = builder.build();

        if (mAtContacts.size() > 0) {
            if (mAtAll) {
                annoFileTransferChatMessage.setAtAll(true);
            } else {
                annoFileTransferChatMessage.setAtUsers(mAtContacts);
            }

        }

        mAtContacts.clear();
        mChatDetailInputView.clearEditText();
        mAtAll = false;

        if (annoFileTransferChatMessage != null) {
            if (!TextUtils.isEmpty(annoFileTransferChatMessage.mediaId)) {
                MediaCenterNetManager.linkMedia(BaseApplicationLike.baseContext, annoFileTransferChatMessage.mediaId, new MediaCenterNetManager.OnMediaLinkedListener() {
                    @Override
                    public void mediaLinked(String linkedMediaId) {
                        if (!TextUtils.isEmpty(linkedMediaId)) {
                            annoFileTransferChatMessage.mediaId = linkedMediaId;
                        }
                        reSendFileMessage(annoFileTransferChatMessage);
                    }
                });
                return;
            }
            newSendWithProgressMessage(MediaCenterNetManager.COMMON_FILE, fileDataHolding.filePath, annoFileTransferChatMessage, true);

        }


    }


    private void sendReferenceMessage(String text, ChatPostMessage messageReferencing) {
        if (mAtAll) {
            text = AtworkUtil.getAtAllI18n(text);
        }

        ReferenceMessage.Builder builder = ReferenceMessage
                .newBuilder()
                .setReferencingMessage(messageReferencing)
                .setReply(text)
                .setTo(mSession.identifier)
                .setToDomainId(mSession.mDomainId)
                .setToType(mSession.type.getToType());

        if (null != getSendChatItem()) {
            builder.setDisplayName(getSendChatItem().getTitle()).setDisplayAvatar(getSendChatItem().getAvatar());

        }
        ReferenceMessage referenceMessage = builder.build();


        if (mAtContacts.size() > 0) {
            if (mAtAll) {
                referenceMessage.setAtAll(true);
            } else {
                referenceMessage.setAtUsers(mAtContacts);
            }

        }

        newSendMessage(referenceMessage);
        mAtContacts.clear();
        mChatDetailInputView.clearEditText();
        mAtAll = false;


    }

    private void sendFiles(List<FileData> fileDataArrayList) {
        if (ListUtil.isEmpty(fileDataArrayList)) {
            return;
        }

        if (1 == fileDataArrayList.size()) {
            popAnnoFileCommentView(fileDataArrayList.get(0));
            return;
        }

        doSendMultiFiles(fileDataArrayList);

    }


    private void popAnnoFileCommentView(FileData fileData) {
        mPopChatDetailDataHoldingView.refreshFileData(fileData);
        mChatDetailInputView.textMode(true);
        mChatDetailInputView.refreshSendBtnStatus();

    }

    private void popAnnoImageCommentView(List<MediaItem> mediaItems) {
        mPopChatDetailDataHoldingView.refreshMediaData(mediaItems);
        mChatDetailInputView.textMode(true);
        mChatDetailInputView.refreshSendBtnStatus();
    }


    @SuppressLint("StaticFieldLeak")
    private void doSendMultiFiles(List<FileData> fileDataArrayList) {
        for (final FileData fileData : fileDataArrayList) {

            new AsyncTask<Void, Void, FileTransferChatMessage>() {
                @Override
                protected FileTransferChatMessage doInBackground(Void... params) {
                    long overtime = DomainSettingsManager.getInstance().handleChatFileExpiredFeature() ? TimeUtil.getTimeInMillisDaysAfter(DomainSettingsManager.getInstance().getChatFileExpiredDay()) : -1;
                    FileTransferChatMessage fileTransferChatMessage = FileTransferChatMessage.newFileTransferChatMessage(AtworkApplicationLike.baseContext, fileData, mLoginUser,
                            mSession.identifier, ParticipantType.User, mSession.type.getToType(),
                            mSession.mDomainId, BodyType.File, mOrgId, getSendChatItem(), overtime, null);
                    //FIXME 图片类型文件先按普通来处理
//                        if (fileData.fileType.equals(FileData.FileType.File_Image)) {
//                            byte[] originalThumbnail = ImageChatMessageHelper.compressOriginalImageForFile(fileData.filePath);
//                            byte[] thumbnailContents = ImageChatMessageHelper.compressThumbnailImageForFile(fileData.filePath);
//                            fileTransferChatMessage.thumbnail = thumbnailContents;
//                            ImageChatMessageHelper.saveOriginalImage(fileTransferChatMessage.deliveryId, originalThumbnail);
//                            ImageChatMessageHelper.saveThumbnailImage(fileTransferChatMessage.deliveryId, thumbnailContents);
//                        }
                    return fileTransferChatMessage;
                }

                @Override
                protected void onPostExecute(FileTransferChatMessage fileTransferChatMessage) {
                    if (fileTransferChatMessage != null) {
                        if (!TextUtils.isEmpty(fileTransferChatMessage.mediaId)) {
                            MediaCenterNetManager.linkMedia(BaseApplicationLike.baseContext, fileTransferChatMessage.mediaId, new MediaCenterNetManager.OnMediaLinkedListener() {
                                @Override
                                public void mediaLinked(String linkedMediaId) {
                                    if (!TextUtils.isEmpty(linkedMediaId)) {
                                        fileTransferChatMessage.mediaId = linkedMediaId;
                                    }
                                    reSendFileMessage(fileTransferChatMessage);
                                }
                            });
                            return;
                        }
                        newSendWithProgressMessage(MediaCenterNetManager.COMMON_FILE, fileData.filePath, fileTransferChatMessage, true);

                    }
                }
            }.execute();
        }
    }


    private void serviceMenuMode() {

        if (mServiceMenus != null && mServiceMenus.size() != 0) {
            mSelectModelView.setVisibility(GONE);
            mInputModelView.setVisibility(GONE);

            mServiceMenuModeView.setVisibility(View.VISIBLE);

            if (canChat()) {
                mServiceMenuView.showMenuKeyBoardView();

            } else {
                mServiceMenuView.hideMenuKeyBoardView();
            }
        }

    }

    private void inputMode() {

        refreshCommonBackView();


        mSelectModelView.setVisibility(GONE);
        mServiceMenuModeView.setVisibility(GONE);
        mTvSelectTitle.setVisibility(GONE);

        mRlCommonTitle.setVisibility(View.VISIBLE);
        mInputModelView.setVisibility(View.VISIBLE);
        mLlRightArea.setVisibility(View.VISIBLE);

        if (StringUtils.isEmpty(mStrTranslationShortName)) {
            mIvTitleBarChatDetailTranslation.setVisibility(View.GONE);
        } else {
            mIvTitleBarChatDetailTranslation.setVisibility(View.VISIBLE);
        }


    }

    private void selectMode() {
        //清除上次的选中信息
        for (ChatPostMessage postMessage : mMessageList) {
            postMessage.select = false;
        }

        refreshCancelBackView();
        refreshSelectTitleViewBurnUI();

        mLlRightArea.setVisibility(GONE);
        mInputModelView.setVisibility(GONE);
        mServiceMenuModeView.setVisibility(GONE);
        mRlCommonTitle.setVisibility(GONE);

        mSelectModelView.setVisibility(View.VISIBLE);
        mTvSelectTitle.setVisibility(View.VISIBLE);

        mIvTitleBarChatDetailTranslation.setVisibility(View.GONE);

    }

    private void changeModel() {
        if (this.mChatModel.equals(ChatModel.COMMON)) {
            mChatModel = ChatModel.SELECT;
            selectMode();

        } else if (this.mChatModel.equals(ChatModel.SELECT)) {
            mChatModel = ChatModel.COMMON;
            inputMode();
        }

        refreshListAdapter();
//        refreshView();
    }

    private void refreshSelectTitleViewBurnUI() {
        if (ChatDetailActivity.sIsBurnMode) {
            mTvSelectTitle.setTextColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.common_gray_bg));

        } else {
            mTvSelectTitle.setTextColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.common_item_black));
        }


    }

    private void refreshSelectTitleText() {
        int selectCount = CollectionsKt.count(mMessageList, ChatPostMessage::isSelectLegal);
        if (selectCount > 0) {
            mTvSelectTitle.setText(getStrings(R.string.select) + "(" + (selectCount + ")"));
            return;
        }

        mTvSelectTitle.setText(getStrings(R.string.select));

    }

    @Override
    public ChatModel getChatModel() {
        return mChatModel;
    }

    /**
     * 返回选中的消息
     *
     * @return
     */
    private List<ChatPostMessage> getSelectedChat() {
        List<ChatPostMessage> messages = new ArrayList<>();
        for (ChatPostMessage chatPostMessage : mMessageList) {
            if (chatPostMessage.isSelectLegal()) {
                messages.add(chatPostMessage);
            }
        }
        return messages;
    }

    /**
     * 重新排序并且刷新 {@link #mWithTimeMessageList}
     *
     * @return mWithTimeMessageList
     */
    private List<ChatPostMessage> refreshWithTimeMsgListTotally() {
        synchronized (mRefreshListLock) {

            List<ChatPostMessage> chatPostMessageList = new ArrayList<>();
            chatPostMessageList.addAll(mMessageList);
            chatPostMessageList.addAll(mMessageJustShowList);

            Collections.sort(chatPostMessageList, (lhs, rhs) -> TimeUtil.compareTo(lhs.deliveryTime, rhs.deliveryTime));

//            mWithTimeMessageList = new ArrayList<>();
            mWithTimeMessageList.clear();
            long timeRecord = 0;

            for (ChatPostMessage chatPostMessage : chatPostMessageList) {
                long chatRecord = chatPostMessage.deliveryTime;

                if (isNotSameDay(chatRecord, timeRecord)) {
                    SystemChatMessage systemChatMessage = new SystemChatMessage(TimeViewUtil.getUserCanViewTimeInChatDetail(BaseApplicationLike.baseContext, chatRecord), SystemChatMessage.Type.TIME_SHOW);
                    systemChatMessage.deliveryTime = chatRecord;
                    mWithTimeMessageList.add(systemChatMessage);
                }

                timeRecord = chatRecord;
                mWithTimeMessageList.add(chatPostMessage);

            }
            chatPostMessageList.clear();

            addHistoryItemView();

            dealWithContactInitializeStatusViewTip();
            return mWithTimeMessageList;
        }
    }

    /**
     * 添加新消息到 {@link #mWithTimeMessageList}, 因为{@link #mWithTimeMessageList}已经排序好了, 所以
     * 该方法直接从最后条开始判断消息时间的先后, 并且插队, 以此避免反复调用 {@link #refreshWithTimeMsgListTotally()}
     *
     * @param newChatPostMessage
     */
    private void addMsgToWithTimeList(ChatPostMessage newChatPostMessage) {
        if (ListUtil.isEmpty(mWithTimeMessageList)) {
            refreshWithTimeMsgListTotally();
        } else {
            List<ChatPostMessage> chatPostMessageList = new ArrayList<>();


            boolean hasInserted = false;

            for (int i = mWithTimeMessageList.size() - 1; i >= 0; i--) {
                ChatPostMessage lastWithTimeMsg = mWithTimeMessageList.get(i);
                if (newChatPostMessage.deliveryTime > lastWithTimeMsg.deliveryTime) {

                    if (needInsertTimeTip(newChatPostMessage, lastWithTimeMsg)) {
                        SystemChatMessage systemChatMessage = new SystemChatMessage(TimeViewUtil.getUserCanViewTimeInChatDetail(BaseApplicationLike.baseContext, newChatPostMessage.deliveryTime), SystemChatMessage.Type.TIME_SHOW);
                        systemChatMessage.deliveryTime = newChatPostMessage.deliveryTime;

                        chatPostMessageList.add(systemChatMessage);
                    }

                    chatPostMessageList.add(newChatPostMessage);

                    mWithTimeMessageList.addAll(i + 1, chatPostMessageList); // combine with the withTimeMsgList
                    hasInserted = true;

                    break;
                }

            }

            if (!hasInserted) {
                fixEarliestMessage(newChatPostMessage, chatPostMessageList);


            }
        }

    }

    /**
     * 处理时间戳最早的消息, 让其插在最前的位置
     */
    private void fixEarliestMessage(ChatPostMessage newChatPostMessage, List<ChatPostMessage> chatPostMessageList) {

        //插在最前时需要判断是否处理掉原本排在最前的"时间"viewItem
        if (null != mWithTimeMessageList && 1 < mWithTimeMessageList.size()) {
            ChatPostMessage topMessage = mWithTimeMessageList.get(0);
            if (topMessage instanceof SystemChatMessage && SystemChatMessage.Type.TIME_SHOW == ((SystemChatMessage) topMessage).type) {
                ChatPostMessage chatMessage = mWithTimeMessageList.get(1);

                //比较排在最前面的两条消息
                if (isNotSameDay(newChatPostMessage, chatMessage)) {
                    mWithTimeMessageList.remove(topMessage);
                }
            }
        }

        SystemChatMessage systemChatMessage = new SystemChatMessage(TimeViewUtil.getUserCanViewTimeInChatDetail(BaseApplicationLike.baseContext, newChatPostMessage.deliveryTime), SystemChatMessage.Type.TIME_SHOW);
        systemChatMessage.deliveryTime = newChatPostMessage.deliveryTime;

        chatPostMessageList.add(systemChatMessage);
        chatPostMessageList.add(newChatPostMessage);
        mWithTimeMessageList.addAll(0, chatPostMessageList); // combine with the withTimeMsgList
    }

    private boolean needInsertTimeTip(ChatPostMessage newChatPostMessage, ChatPostMessage lastWithTimeMsg) {

        //在未激活的提示语下, 不用插入时间 tip
        if (-1 == lastWithTimeMsg.deliveryTime && lastWithTimeMsg instanceof SystemChatMessage) {
            if (SystemChatMessage.Type.NOT_INITIALIZE_TIP == ((SystemChatMessage) lastWithTimeMsg).type) {
                return false;
            }
        }

        return isNotSameDay(newChatPostMessage, lastWithTimeMsg);

    }

    private boolean isNotSameDay(ChatPostMessage newChatPostMessage, ChatPostMessage lastWithTimeMsg) {
        return isNotSameDay(newChatPostMessage.deliveryTime, lastWithTimeMsg.deliveryTime);
    }

    private boolean isNotSameDay(long deliveryTime0, long deliveryTime1) {
        return !TimeUtil.isSameDate(deliveryTime0, deliveryTime1);
    }

    private void addImgSendingListener(ImageChatMessage message) {
        if (FileStatus.SENDING.equals(message.fileStatus)) {
            MediaCenterNetManager.MediaUploadListener mediaUploadListener
                    = MediaCenterNetManager.getMediaUploadListenerById(message.deliveryId, MediaCenterNetManager.UploadType.CHAT_IMAGE);

            if (null == mediaUploadListener) {
                mediaUploadListener = new ImageMediaUploadListener(mSession, message);
                MediaCenterNetManager.addMediaUploadListener(mediaUploadListener);

                ChatSessionDataWrap.getInstance().updateChatInFileStreaming(mSession.identifier, message.deliveryId);
            }
        }

    }


    public void addFileSendingListener(FileTransferChatMessage message) {
        if (FileStatus.SENDING.equals(message.fileStatus)) {
            MediaCenterNetManager.MediaUploadListener mediaUploadListener
                    = MediaCenterNetManager.getMediaUploadListenerById(message.deliveryId, MediaCenterNetManager.UploadType.CHAT_FILE);

            if (null == mediaUploadListener) {
                mediaUploadListener = new FileMediaUploadListener(mSession, message);
                MediaCenterNetManager.addMediaUploadListener(mediaUploadListener);

                ChatSessionDataWrap.getInstance().updateChatInFileStreaming(mSession.identifier, message.deliveryId);


            }
        }

    }

    public void addMicroVideoSendingListener(MicroVideoChatMessage message) {
        if (FileStatus.SENDING.equals(message.fileStatus)) {
            MediaCenterNetManager.MediaUploadListener mediaUploadListener
                    = MediaCenterNetManager.getMediaUploadListenerById(message.deliveryId, MediaCenterNetManager.UploadType.MICRO_VIDEO);

            if (null == mediaUploadListener) {
                mediaUploadListener = new MicroVideoUploadListener(mSession, message);
                MediaCenterNetManager.addMediaUploadListener(mediaUploadListener);

                ChatSessionDataWrap.getInstance().updateChatInFileStreaming(mSession.identifier, message.deliveryId);


            }
        }

    }

    public void addVoiceSendingListener(VoiceChatMessage message) {
        if (FileStatus.SENDING.equals(message.fileStatus)) {
            MediaCenterNetManager.MediaUploadListener mediaUploadListener
                    = MediaCenterNetManager.getMediaUploadListenerById(message.deliveryId, MediaCenterNetManager.UploadType.VOICE);

            if (null == mediaUploadListener) {
                mediaUploadListener = new VoiceUploadListener(mSession, message);
                MediaCenterNetManager.addMediaUploadListener(mediaUploadListener);

                ChatSessionDataWrap.getInstance().updateChatInFileStreaming(mSession.identifier, message.deliveryId);

            }
        }

    }

    public void addMultipartSendingListener(MultipartChatMessage message) {
        if (FileStatus.SENDING.equals(message.fileStatus)) {
            MediaCenterNetManager.MediaUploadListener mediaUploadListener
                    = MediaCenterNetManager.getMediaUploadListenerById(message.deliveryId, MediaCenterNetManager.UploadType.VOICE);

            if (null == mediaUploadListener) {
                mediaUploadListener = new MultipartUploadListener(mSession, message);
                MediaCenterNetManager.addMediaUploadListener(mediaUploadListener);

                ChatSessionDataWrap.getInstance().updateChatInFileStreaming(mSession.identifier, message.deliveryId);
            }
        }

    }


    @Override
    public void reSendMessage(ChatPostMessage chatPostMessage) {
        chatPostMessage.to = mSession.identifier;
        chatPostMessage.mToType = mSession.type.getToType();
//        chatPostMessage.mFromType = ParticipantType.User;
//        chatPostMessage.mFromDomain = mLoginUser.mDomainId;
        chatPostMessage.mToDomain = mSession.mDomainId;
        chatPostMessage.deliveryTime = TimeUtil.getCurrentTimeInMillis();
        chatPostMessage.chatStatus = ChatStatus.Sending;
//        chatPostMessage.mMyAvatar = LoginUserInfo.getInstance().getLoginUserAvatar(BaseApplicationLike.baseContext);
//        chatPostMessage.mMyName = LoginUserInfo.getInstance().getLoginUserName(BaseApplicationLike.baseContext);
        chatPostMessage.buildSenderInfo(AtworkApplicationLike.baseContext);
        doReSendMessage(chatPostMessage);

    }

    private void doReSendMessage(ChatPostMessage chatPostMessage) {
        chatPostMessage.mRetries++;
        if (chatPostMessage instanceof TextChatMessage || chatPostMessage instanceof ShareChatMessage ||
                chatPostMessage instanceof ArticleChatMessage ||
                chatPostMessage instanceof StickerChatMessage || chatPostMessage instanceof DocChatMessage) {
            newSendMessage(chatPostMessage);
            return;
        }
        if (chatPostMessage instanceof VoiceChatMessage) {
            VoiceChatMessage voiceChatMessage = (VoiceChatMessage) chatPostMessage;
            voiceChatMessage.fileStatus = FileStatus.SENDING;
            newSendWithProgressMessage(MediaCenterNetManager.COMMON_FILE, VoiceChatMessage.getAudioPath(mActivity, voiceChatMessage.deliveryId), voiceChatMessage, false);
            return;
        }
        if (chatPostMessage instanceof ImageChatMessage) {
            ImageChatMessage imageChatMessage = (ImageChatMessage) chatPostMessage;
            //如果mediaId为空，则重新上传
            if (StringUtils.isEmpty(imageChatMessage.mediaId)) {
                imageChatMessage.fileStatus = FileStatus.SENDING;
                imageChatMessage.progress = 0;
                String imageFile;

                if (imageChatMessage.isFullMode()) {
                    imageFile = MediaCenterNetManager.IMAGE_FULL_FILE;
                } else {
                    imageFile = MediaCenterNetManager.IMAGE_FILE;

                }
                newSendWithProgressMessage(imageFile, ImageShowHelper.getImageChatMsgPath(mActivity, imageChatMessage), imageChatMessage, false);
                return;
            }
            imageChatMessage.fileStatus = FileStatus.SENDED;
            newSendMessage(imageChatMessage);
            return;
        }
        if (chatPostMessage instanceof FileTransferChatMessage) {
            FileTransferChatMessage fileTransferChatMessage = (FileTransferChatMessage) chatPostMessage;
            if (StringUtils.isEmpty(fileTransferChatMessage.mediaId)) {
                fileTransferChatMessage.fileStatus = FileStatus.SENDING;
                fileTransferChatMessage.progress = 0;

                newSendWithProgressMessage(MediaCenterNetManager.COMMON_FILE, fileTransferChatMessage.filePath, fileTransferChatMessage, false);
                return;
            }
            //媒体 ID 不为空时, 可能为转发的情况
            fileTransferChatMessage.fileStatus = FileStatus.SENDED;
            newSendMessage(fileTransferChatMessage);
            return;
        }
        if (chatPostMessage instanceof MicroVideoChatMessage) {
            MicroVideoChatMessage microVideoChatMessage = (MicroVideoChatMessage) chatPostMessage;
            if (StringUtils.isEmpty(microVideoChatMessage.mediaId)) {
                microVideoChatMessage.fileStatus = FileStatus.SENDING;
                microVideoChatMessage.progress = 0;

                newSendWithProgressMessage(MediaCenterNetManager.COMMON_FILE, FileHelper.getMicroExistVideoFilePath(mActivity, microVideoChatMessage), microVideoChatMessage, false);
                return;
            }
            microVideoChatMessage.fileStatus = FileStatus.SENDED;
            newSendMessage(microVideoChatMessage);
            return;
        }

        if (chatPostMessage instanceof MultipartChatMessage) {
            MultipartChatMessage multipartChatMessage = (MultipartChatMessage) chatPostMessage;
            if (StringUtils.isEmpty(multipartChatMessage.mFileId)) {
                multipartChatMessage.fileStatus = FileStatus.SENDING;

                newSendWithProgressMessage(MediaCenterNetManager.COMMON_FILE, MultipartMsgHelper.getMultipartPath(multipartChatMessage), multipartChatMessage, false);
                return;
            }
            multipartChatMessage.fileStatus = FileStatus.SENDED;
            newSendMessage(multipartChatMessage);
            return;
        }


        if (chatPostMessage instanceof AnnoImageChatMessage) {
            sendImgOnActivityResultForMediaAssemble((AnnoImageChatMessage) chatPostMessage, null, false, null);

            return;
        }

        refreshListAdapter();
    }


    @Override
    protected boolean onBackPressed() {
        backAction();
        return false;
    }


    /**
     * 更改底部切换
     *
     * @param showView 要显示的view
     */
    public void changeMenuView(final View showView) {

        List<View> moreBottomViewList = new ArrayList<>();
        moreBottomViewList.add(mChatMoreView);
        moreBottomViewList.add(mEmojView);
        moreBottomViewList.add(mChatVoiceView);


        moreBottomViewList.remove(showView);


        List<View> showingViewList = CollectionsKt.filter(moreBottomViewList, View::isShown);

        //防止部分机型因有渲染出view而触发不了动画
        if (ListUtil.isEmpty(showingViewList)) {
            showMenuView(showView);

        } else {

            View showingView = showingViewList.get(0);

            mHideAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
            mHideAnimation.setDuration(100);
            mHideAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    showingView.setVisibility(GONE);
                    showMenuView(showView);
                }
            });

            showingView.startAnimation(mHideAnimation);

        }
    }

    /**
     * 底部view显示, 触发动画效果
     *
     * @param showView
     */
    public void showMenuView(final View showView) {
        showView.postDelayed(() -> {
            mFlFunctionArea.setVisibility(View.VISIBLE);

            showView.setVisibility(View.VISIBLE);
            mShowAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            mShowAnimation.setDuration(100);
            showView.setAnimation(mShowAnimation);
        }, 100);

    }

    public void getTalkUser(final Context context, final String userId, final String domainId, UserAsyncNetService.OnQueryUserListener listener) {
        if (null == mUser) {
            UserManager.getInstance().queryUserByUserId(context, userId, domainId, new UserAsyncNetService.OnQueryUserListener() {
                @Override
                public void onSuccess(@NonNull User user) {
                    mUser = user;

                    listener.onSuccess(user);

                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    listener.networkFail(errorCode, errorMsg);

                }
            });

            return;
        }


        listener.onSuccess(mUser);

    }


    public void getTalkEmployees(final String userId, BaseQueryListener<List<Employee>> queryListener) {
        if (null == mEmployeeList) {
            EmployeeManager.getInstance().queryUserEmployeeList(userId, employeeList -> {
                mEmployeeList = employeeList;
                queryListener.onSuccess(employeeList);
            });

            return;
        }

        queryListener.onSuccess(mEmployeeList);
    }


    public enum ChatModel {
        //普通界面
        COMMON,

        //转发界面
        SELECT,
    }


    public void hideMaskLayer() {
        super.hideMaskLayer();
        mVMaskLayer.setVisibility(GONE);
    }

    public void showMaskLayer() {
        super.showMaskLayer();
        mVMaskLayer.setVisibility(View.VISIBLE);

    }

    /**
     * 根据聊天类型处理显示水印逻辑
     */
    private void showWatermark() {
        if (!isAdded()) {
            return;
        }

        if (SessionType.NativeApp.equals(mSession.type)) {
            setUserWatermarkBaseOnMode();
            return;
        }

        if (SessionType.LightApp.equals(mSession.type)) {
            setUserWatermarkBaseOnMode();
            return;
        }

        if (SessionType.Service.equals(mSession.type)) {
            getServiceRemoteTags(null);
            setUserWatermarkBaseOnMode();
            return;
        }

        if (isUserChat()) {
            setUserWatermarkBaseOnMode();
            return;
        }
        if (isDiscussionChat()) {
            showDiscussionWatermark();

        }
    }

    private boolean isDiscussionChat() {
        return SessionType.Discussion.equals(mSession.type);
    }

    private boolean isServiceChat() {
        return SessionType.Service.equals(mSession.type);
    }

    private void getServiceRemoteTags(final MessageAsyncNetService.OnMessageTagsListener listener) {
        MessageAsyncNetService.queryMessageTags(mActivity, mSession.orgId, mSession.identifier, new MessageAsyncNetService.OnMessageTagsListener() {
            @Override
            public void getMessageTagsSuccess(List<MessageTags> tagsList) {
                ChatDaoService.getInstance().saveTags(tagsList);
                if (listener == null) {
                    return;
                }
                listener.getMessageTagsSuccess(tagsList);
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
            }
        });
    }

    private void setUserWatermarkBaseOnMode() {
        int bgColor = getChatListBgColor();

        int textColor = getWaterTextColor();

        setUserWatermark(bgColor, textColor);
    }


    private void setUserWatermark(int bgColor, int textColor) {
        if ("show".equalsIgnoreCase(DomainSettingsManager.getInstance().handleUserWatermarkFeature())) {
            WaterMarkUtil.setLoginUserWatermark(mActivity, mChatListView, bgColor, textColor, "");

        } else {
            mChatListView.setBackgroundColor(bgColor);

        }
    }

    private int getChatListBgColor() {
        int bgColor;
        if (BurnModeHelper.isBurnMode()) {
            bgColor = ContextCompat.getColor(getActivity(), R.color.burn_mode_chat_bg);

        } else {
            bgColor = -1;
        }
        return bgColor;
    }

    private int getWaterTextColor() {
        int textColor = -1;
        if (BurnModeHelper.isBurnMode()) {
            textColor = ContextCompat.getColor(getActivity(), R.color.watermark_text_color);
            textColor = ColorUtils.setAlphaComponent(textColor, 76);
        }
        return textColor;
    }


    /**
     * 显示群聊水印
     */
    private void showDiscussionWatermark() {
        if (!isAdded()) {
            return;
        }

        if (mDiscussion == null) {
            return;
        }
        String watermarkFeature = DomainSettingsManager.getInstance().handleDiscussionWatermarkFeature();
        if ("none".equalsIgnoreCase(watermarkFeature)) {
            mChatListView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.chat_detail_bg));
            return;
        }

        boolean showDiscussionWatermark = false;
        if ("show".equalsIgnoreCase(watermarkFeature)) {
            showDiscussionWatermark = true;
        }
        if ("customer".equalsIgnoreCase(watermarkFeature)) {
            showDiscussionWatermark = WatermarkCache.getInstance().getWatermarkConfigCache(new Watermark(mDiscussion.mDiscussionId, Watermark.Type.DISCUSSION));
        }

        if (!showDiscussionWatermark) {
            mChatListView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.chat_detail_bg));
            return;
        }
        if (TextUtils.isEmpty(mDiscussion.getOrgCodeCompatible())) {
            WaterMarkUtil.setLoginUserWatermark(mActivity, mChatListView, "");
            return;
        }
        WaterMarkHelper.setEmployeeWatermarkByOrgId(mActivity, mChatListView, mDiscussion.getOrgCodeCompatible());
    }

    /**
     * 查询远程服务器上群组的基础资料
     */
    private void queryDiscussionBasicInfoRemote() {
        if (mCallDiscussionBasicInfoRemote) {
            return;
        }

        if (mDiscussion == null) {
            return;
        }


        mCallDiscussionBasicInfoRemote = true;


        DiscussionManagerKt.queryDiscussionBasicInfoRemote(DiscussionManager.getInstance(), mActivity, mDiscussion.mDiscussionId, result -> {

            if (result instanceof DiscussionSettingsResponse) {
                DiscussionSettingsResponse response = (DiscussionSettingsResponse) result;
                Watermark watermark = new Watermark();
                watermark.mSourceId = mDiscussion.mDiscussionId;
                watermark.mType = Watermark.Type.DISCUSSION;
                WatermarkRepository repository = WatermarkRepository.getInstance();
                WatermarkCache.getInstance().setWatermarkConfigCache(watermark, response.mWatermarkEnable);
                long resultHandling = response.mWatermarkEnable ? repository.insertOrUpdateWatermark(watermark) : repository.deleteWatermark(watermark);

                showDiscussionWatermark();

                return null;
            }

            if (result instanceof Discussion) {
                Discussion newDiscussion = (Discussion) result;
                if (null != mDiscussion) {
                    newDiscussion.mMemberList = mDiscussion.mMemberList;
                    newDiscussion.mMemberContactList = mDiscussion.mMemberContactList;
                }

                mDiscussion = newDiscussion;
                return null;

            }

            return null;
        });


    }


    private void removeMessageList(List<String> msgIdList) {
        List<ChatPostMessage> removedMsgList = new ArrayList<>();
        for (ChatPostMessage chatPostMessage : mMessageList) {
            if (msgIdList.contains(chatPostMessage.deliveryId)) {
                removedMsgList.add(chatPostMessage);
            }
        }

        mMessageList.removeAll(removedMsgList);
    }

    public static Bitmap getScreenshot() {
        return sScreenshot;
    }

    public static void releaseScreenshot() {
        if (null != sScreenshot) {
            sScreenshot.recycle();
            sScreenshot = null;
        }
    }

    private void confirmEmergencyMessage(String messageId) {
        ChatPostMessage msgRemoved = null;
        for (ChatPostMessage emergencyMessage : mEmergencyMessageUnconfirmedList) {
            if (emergencyMessage.deliveryId.equals(messageId)) {
                msgRemoved = emergencyMessage;
                break;
            }
        }
        if (null != msgRemoved) {
            mEmergencyMessageUnconfirmedList.remove(msgRemoved);

            refreshEmergencyUnConfirmedTipView();
        }
    }


    @Override
    public void onUndoMsgReceive(UndoEventMessage undoEventMessage) {
        if (null != undoEventMessage) {

            undoMessages(undoEventMessage);

            if (null != AudioRecord.sPlayingVoiceMedia && undoEventMessage.isMsgUndo(AudioRecord.sPlayingVoiceMedia.getKeyId())) {
                AudioRecord.stopPlaying();
            }


            boolean result = ChatMessageHelper.remove(mUnreadDiscussionNotificationList, undoEventMessage.mEnvIds);
            if (result) {
                refreshDiscussionNotifyUnreadTipView();
            }
        }
    }


    private void undoMessages(UndoEventMessage undoEventMessage) {
        List<ChatPostMessage> messageUndoList = CollectionsKt.filter(mMessageList, chatPostMessage -> undoEventMessage.isMsgUndo(chatPostMessage.deliveryId));
        CollectionsKt.forEach(messageUndoList, chatPostMessage -> {

            chatPostMessage.setChatStatus(ChatStatus.UnDo);
            chatPostMessage.undoSuccessTime = undoEventMessage.deliveryTime;

            return null;
        });

        if (!ListUtil.isEmpty(messageUndoList)) {
            refreshListAdapter();
        }
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = code -> {
        LogUtil.e(TAG, "SpeechRecognizer init() code = " + code);

        if (code != ErrorCode.SUCCESS) {

        }
    };

    //在获取到翻译结果之后更新一个MESSAGE对象并插入到SQLite数据库
    private void updateVoiceMessage(VoiceChatMessage voiceChatMessage, String voiceResult) {
        voiceChatMessage.setTranslatedResult(voiceResult, mVoiceTranslateTarget);

        ChatPostMessage msgCache = MessageCache.getInstance().queryMessage(voiceChatMessage);
        if (null != msgCache) {
            ((VoiceChatMessage) msgCache).setTranslatedResult(voiceResult, mVoiceTranslateTarget);
        }
        ChatDaoService.getInstance().insertOrUpdateMessage(BaseApplicationLike.baseContext, voiceChatMessage);
    }


    private void showFailedUI(VoiceChatMessage voiceChatMessage) {
        toast(R.string.voice_translate_failed);
        setTranslating(voiceChatMessage, false);
    }

    private void doXunfeiVoiceRecognize(VoiceChatMessage voiceChatMessage) {

        //听写监听器
        RecognizerListener recognizerListener = new RecognizerListener() {
            String voiceResult = StringUtils.EMPTY;

            @Override
            public void onBeginOfSpeech() {
                // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
                LogUtil.e(TAG, "onBeginOfSpeech");
            }

            @Override
            public void onError(SpeechError error) {
                // Tips：
                // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
                // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。

                LogUtil.e(TAG, "error -> " + error.getErrorDescription() + "  error_code -> " + error.getErrorCode());

                showFailedUI(voiceChatMessage);//显示翻译失败的UI
            }

            @Override
            public void onEndOfSpeech() {
                // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
                LogUtil.e(TAG, "onEndOfSpeech");
            }

            @Override
            public void onResult(RecognizerResult results, boolean isLast) {
                LogUtil.e(TAG, "onResult ->" + results.getResultString());
                String result = JsonParser.parseIatResult(results.getResultString());
                LogUtil.e(TAG, "onResult text ->" + result);
                voiceResult += result;
                if (isLast) {//是否翻译到最后了，如果是（true），则将正在翻译的属性设置为false
                    //如果不为空，更新ＵＩ
                    if (!StringUtils.isEmpty(voiceResult)) {
                        updateVoiceMessage(voiceChatMessage, voiceResult);//在获取到翻译结果之后更新一个MESSAGE对象并插入到SQLite数据库
                        //showTranslatedResult(mVoiceResult);//UI显示翻译结果
                        ChatDetailExposeBroadcastSender.refreshMessageListViewUI();//更新消息的UI
                    } else {
                        showFailedUI(voiceChatMessage);//显示翻译失败的UI：这个地方，显示是应该写在元素那边了所以，就是这里就不操作，然后再元素那边，再次通过判断是否为空，再来显示翻译失败的UI样式
                    }
                    // voiceResult = StringUtils.EMPTY;
                }
            }

            @Override
            public void onVolumeChanged(int volume, byte[] data) {
                LogUtil.e(TAG, "返回音频数据：" + data.length);
            }

            @Override
            public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
                // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
                // 若使用本地能力，会话id为null
                //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
                //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
                //		LogUtil.e(TAG, "session id =" + sid);
                //	}
            }
        };

        mIat = SpeechRecognizer.createRecognizer(getActivity(), mInitListener);
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        // 根据文档 10000为最大的间隔
        mIat.setParameter(SpeechConstant.VAD_EOS, "10000");
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        // 根据文档 10000为最大的间隔
        mIat.setParameter(SpeechConstant.VAD_BOS, "10000");
        //禁止静音抑制
//        mIat.setParameter(SpeechConstant.VAD_ENABLE, "0");


        mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
        mIat.setParameter(SpeechConstant.SAMPLE_RATE, "8000");

        mIat.setParameter(SpeechConstant.LANGUAGE, mVoiceTranslateTarget);


        int ret = mIat.startListening(recognizerListener);

        if (ErrorCode.SUCCESS == ret) {
            AudioFileHelper.getVoiceFileOriginalPath(getActivity(), voiceChatMessage, path -> {
                if (FileUtil.isExist(path)) {
                    AmrFileDecoder amrFileDecoder = new AmrFileDecoder();

                    byte[] armResult = amrFileDecoder.amr2Pcm(path);
                    LogUtil.e("armResult -> " + armResult.length);

                    mIat.writeAudio(armResult, 0, armResult.length);

                } else {
                    showFailedUI(voiceChatMessage);
                }

                mIat.stopListening();
            });


        } else {
            LogUtil.e("识别失败,错误码：" + ret);

        }
    }

    /**
     * Description:判断是否直接预览
     *
     * @param fileTransferChatMessage
     * @return
     */
    public boolean shouldPreviewLocal(FileTransferChatMessage fileTransferChatMessage) {
        if (!OfficeHelper.isSupportType(fileTransferChatMessage.filePath)) {
            return false;
        }
        if (!isFileExist(fileTransferChatMessage)) {
            return false;
        }
        return true;
    }

    /**
     * Description：文件是否存在
     *
     * @param fileTransferChatMessage
     * @return
     */
    private boolean isFileExist(FileTransferChatMessage fileTransferChatMessage) {
        if (TextUtils.isEmpty(fileTransferChatMessage.filePath)) {
            return false;
        }
        File file = new File(fileTransferChatMessage.filePath);
        return file.exists();
    }

    /**
     * Description:本地预览
     */
    public void previewLocal(FileTransferChatMessage fileTransferChatMessage, int intentType) {
        EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(fileTransferChatMessage.filePath, false, fileName -> {

            OfficeHelper.previewByX5(getContext(), fileName, mSession.identifier, fileTransferChatMessage, intentType);

        });
    }


    private void checkCanChatAndStarContact() {

        checkCanChatAndAction(checkTalkAuthResult -> {

            mProgressDialogHelper.dismiss();

            if (CheckTalkAuthResult.CAN_TALK == checkTalkAuthResult) {
                addFavorContact();

            }

            return Unit.INSTANCE;
        });


    }


    private void checkCanChatAndAction(Function1<CheckTalkAuthResult, Unit> checkCanChatAndAction) {
        if (ListUtil.isEmpty(mEmployeeList)) {
            if (!mIsLoadEmpListRemoteRequestSuccess) {
                toast(R.string.network_not_avaluable);

                checkEmpRefreshFromRemote();
                return;
            }

        }

        mProgressDialogHelper.show();
        ChatPermissionService.INSTANCE.canChat(mEmployeeList, mSession.identifier, mSession.mDomainId, checkCanChatAndAction);
    }

    /**
     * 取消星标
     */
    private void removeFavorContact() {
        UserManager.getInstance().addOrRemoveStarUser(mActivity, mUser, false, new UserAsyncNetService.OnHandleUserInfoListener() {
            @Override
            public void networkFail(int errorCode, String errorMsg) {
                if (!ErrorHandleUtil.handleBaseError(errorCode, errorMsg)) {
                    AtworkToast.showResToast(R.string.database_error);

                }

            }

            @Override
            public void success() {
                StarUserListDataWrap.getInstance().removeUser(mUser);
                if (getActivity() != null) {
                    AtworkToast.showToast(getResources().getString(R.string.contact_remove_success));
                }
                refreshContactStarUIInPopAreaView();
            }

        });
    }


    /**
     * 星标
     */
    private void addFavorContact() {

        UserManager.getInstance().addOrRemoveStarUser(mActivity, mUser, true, new UserAsyncNetService.OnHandleUserInfoListener() {
            @Override
            public void networkFail(int errorCode, String errorMsg) {
                if (!ErrorHandleUtil.handleBaseError(errorCode, errorMsg)) {
                    AtworkToast.showResToast(R.string.database_error);

                }
            }

            @Override
            public void success() {
                StarUserListDataWrap.getInstance().addUser(mUser);
                AtworkToast.showResToast(R.string.contact_add_success);
                refreshContactStarUIInPopAreaView();
            }

        });

    }

    interface OnLocationPermissionResult {
        void onGrant();

        void onReject(String permission);
    }


}
