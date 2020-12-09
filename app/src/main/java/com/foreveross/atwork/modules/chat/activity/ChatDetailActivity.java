package com.foreveross.atwork.modules.chat.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreverht.threadGear.HighPriorityCachedTreadPoolExecutor;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.biometricAuthentication.BiometricAuthenticationProtectItemType;
import com.foreveross.atwork.infrastructure.model.clickStatistics.Type;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.ServiceCompat;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.rom.RomUtil;
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil;
import com.foreveross.atwork.modules.app.manager.AppManager;
import com.foreveross.atwork.manager.listener.BaseQueryListener;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment;
import com.foreveross.atwork.modules.chat.util.BurnModeHelper;
import com.foreveross.atwork.modules.voip.activity.CallActivity;
import com.foreveross.atwork.services.ImSocketService;
import com.foreveross.atwork.support.SingleFragmentActivity;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.meizu.cloud.pushsdk.util.MzSystemUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

import static com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment.APP_BUNDLE_ID;

/**
 * Created by lingen on 15/3/24.
 * Description:聊天界面详情的ChatDetail
 */
public class ChatDetailActivity extends SingleFragmentActivity {


    public static final String IDENTIFIER = "Identifier";
    public static final String TYPE = "type";
    public static final String DISPLAY_NAME = "display_name";
    public static final String DISPLAY_AVATAR = "display_avatar";
    public static final String TO = "to";

    public static final String TO_FIXED_MESSAGE_ID = "to_fixed_message_id";
    public static final String WAITING_FOR_SEND_MESSAGES = "WAITING_FOR_SEND_MESSAGES";

    public static final String ACTION = "ACTION";

    public static final String SESSION_LEGAL_CHECK = "SESSION_LEGAL_CHECK";

    public String sIdentifierOpen;
    public String mAppBundleId;
    public String type;
    public String displayName;
    public String displayAvatar;
    public String to;

    private ChatDetailFragment mChatDetailFragment;

    /**
     * 是否阅后即焚模式模式
     * */
    public static boolean sIsBurnMode = false;

    private String toFixedMessageId;

    private List<ChatPostMessage> chatPostMessageList;

    private boolean isReturnBack;

    private Bundle mBundle;

    private boolean mNeedSessionLegalCheck = true;

    private String mBehaviorLogKeyTag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ImageCacheHelper.checkPool();

        sIdentifierOpen = getIntent().getStringExtra(IDENTIFIER);
        mAppBundleId = getIntent().getStringExtra(APP_BUNDLE_ID);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("workplus_scheme://com.foreverht.workplus.test/notify_params?from=123&to=456"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Logger.e("chat", intent.toUri(Intent.URI_INTENT_SCHEME));
        mBundle = getIntent().getExtras();
        this.chatPostMessageList = (List<ChatPostMessage>) getIntent().getSerializableExtra(WAITING_FOR_SEND_MESSAGES);
        this.toFixedMessageId = getIntent().getStringExtra(TO_FIXED_MESSAGE_ID);
        this.isReturnBack = getIntent().getBooleanExtra(ChatDetailFragment.RETURN_BACK, false);
        this.mNeedSessionLegalCheck = getIntent().getBooleanExtra(SESSION_LEGAL_CHECK, true);
        if (TextUtils.isEmpty(sIdentifierOpen)) {
            decodeUri();

            ServiceCompat.startServiceCompat(this, ImSocketService.class);

        }
        //弥补在语音通话中, 从通知栏进去聊天界面的场景, 此时, 通话界面为关闭状态
        CallActivity.sIsOpening = false;

        super.onCreate(savedInstanceState);

        sIsBurnMode = false;
    }

    private void decodeUri() {
        if (RomUtil.isHuawei() || RomUtil.isOppo()) {
            decodeHuaweiUri();
            return;
        }
        if (MzSystemUtils.isBrandMeizu(this)) {
            decodeMZUri();
            return;
        }
        String from = getIntent().getStringExtra("from");
        to = getIntent().getStringExtra("to");
        type = getIntent().getStringExtra("type");
        sIdentifierOpen = "user".equalsIgnoreCase(type) ? from : to;
        displayName = getIntent().getStringExtra("display_name");
        displayAvatar = getIntent().getStringExtra("display_avatar");
    }

    private void decodeHuaweiUri() {
        Uri uri = getIntent().getData();
        if (uri == null) {
            //todo..to main activity
        }
        String from = uri.getQueryParameter("from");
        to = uri.getQueryParameter("to");
        type = uri.getQueryParameter("type");
        sIdentifierOpen = "user".equalsIgnoreCase(type) ? from : to;
        displayName = uri.getQueryParameter("display_name");
        displayAvatar = uri.getQueryParameter("display_avatar");
    }

    private void decodeMZUri() {
        if (mBundle == null) {
            AtworkToast.showLongToast("数据解析出错");
            finish();
            return;
        }
        String from = mBundle.getString("from");
        to = mBundle.getString("to");
        type = mBundle.getString("type");
        sIdentifierOpen = "user".equalsIgnoreCase(type) ? from : to;
        displayName = mBundle.getString("display_name");
        displayAvatar = mBundle.getString("display_avatar");
    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sIdentifierOpen = null;
        sIsBurnMode = false;
    }

    public static Intent getIntent(Context context, String identifier) {
        Intent intent = new Intent();
        intent.setClass(context, ChatDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(IDENTIFIER, identifier);
        return intent;
    }

    public static Intent getIntent(Context context, String identifier, String toFixedId) {
        Intent intent = new Intent();
        intent.setClass(context, ChatDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(IDENTIFIER, identifier);
        intent.putExtra(TO_FIXED_MESSAGE_ID, toFixedId);
        return intent;
    }

    /**
     * 进入列表，指定要发送的消息
     *
     * @param context
     * @param identifier
     * @param chatPostMessageList
     * @return
     */
    public static Intent getIntent(Context context, String identifier, List<ChatPostMessage> chatPostMessageList) {
        Intent intent = new Intent();
        intent.setClass(context, ChatDetailActivity.class);
        intent.putExtra(IDENTIFIER, identifier);
        intent.putExtra(WAITING_FOR_SEND_MESSAGES, (java.io.Serializable) chatPostMessageList);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        Bundle bundle = new Bundle();
        bundle.putString(IDENTIFIER, sIdentifierOpen);
        bundle.putSerializable(WAITING_FOR_SEND_MESSAGES, (java.io.Serializable) chatPostMessageList);
        bundle.putString(TO_FIXED_MESSAGE_ID, toFixedMessageId);
        bundle.putBoolean(ChatDetailFragment.RETURN_BACK, this.isReturnBack);
        bundle.putBoolean(SESSION_LEGAL_CHECK, mNeedSessionLegalCheck);
        bundle.putString(TYPE, type);
        bundle.putString(DISPLAY_AVATAR, displayAvatar);
        bundle.putString(DISPLAY_NAME, displayName);
        bundle.putString(TO, to);
        mChatDetailFragment = new ChatDetailFragment();
        mChatDetailFragment.setArguments(bundle);

        return mChatDetailFragment;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //界面回退动画
        this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    @Override
    public void finish() {
        super.finish();
    }


    @Override
    public void changeTheme() {
        if (!BurnModeHelper.isBurnMode()) {
            super.changeTheme();

        }
    }

    @Override
    public void changeStatusBar() {
        if(BurnModeHelper.isBurnMode()) {
            StatusBarUtil.setColorNoTranslucent(this, ContextCompat.getColor(this, R.color.burn_mode_chat_input_bg));
        } else {
            super.changeStatusBar();
        }
    }

    @Override
    public int getRootHeight() {
        return mChatDetailFragment.getRootHeight();
    }

    @SuppressLint("StaticFieldLeak")
    private void getApp(BaseQueryListener<App> listener) {

        new AsyncTask<Void, Void, App>(){

            @Override
            protected App doInBackground(Void... voids) {
                return getLightAppSync();
            }

            @Override
            protected void onPostExecute(App app) {
                listener.onSuccess(app);
            }
        }.executeOnExecutor(HighPriorityCachedTreadPoolExecutor.getInstance());
    }


    @Nullable
    private App getLightAppSync() {


        if(StringUtils.isEmpty(sIdentifierOpen)) {
            return null;
        }


        Session session = ChatSessionDataWrap.getInstance().getSession(sIdentifierOpen, null);
        if(null != session && session.isAppType()) {
            App app = AppManager.getInstance().queryAppSync(BaseApplicationLike.baseContext, sIdentifierOpen, session.orgId);
            return app;
        }


        return null;
    }

    @Nullable
    @Override
    public BiometricAuthenticationProtectItemType getBiometricAuthenticationProtectItemTag() {
        return BiometricAuthenticationProtectItemType.IM;
    }

    @Override
    public boolean commandProtected(@NotNull Function2<? super String, ? super Boolean, Unit> getResult) {
        getApp(app -> {
            if(null != app) {
                getResult.invoke(app.getId(), app.isNeedBioAuthProtect());

            } else {
                getResult.invoke(StringUtils.EMPTY, false);
            }
        });

        return true;
    }
}
