package com.foreveross.atwork.support;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.manager.OrganizationSettingsManager;
import com.foreveross.atwork.infrastructure.model.user.LoginUserBasic;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.UndoEventMessage;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.CustomerHelper;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil;
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.manager.listener.OnDomainSettingChangeListener;
import com.foreveross.atwork.manager.listener.OnOrgSettingChangeListener;
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment;
import com.foreveross.atwork.modules.chat.util.UndoMessageHelper;
import com.foreveross.atwork.modules.main.activity.MainActivity;
import com.foreveross.atwork.modules.main.service.HandleLoginService;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.theme.interfaces.ISkinUpdate;
import com.foreveross.theme.manager.SkinHelper;
import com.foreveross.theme.manager.SkinMaster;
import com.foreveross.theme.model.Theme;
import com.foreveross.watermark.core.WaterMarkUtil;
import com.takwolf.android.lock9.Lock9View;

import java.util.Locale;

/**
 * Created by lingen on 15/6/10.
 * Description:
 */
public abstract class BackHandledFragment extends Fragment implements ISkinUpdate, OnDomainSettingChangeListener, OnOrgSettingChangeListener {

    public static final int REQUEST_RESULT_BACK = 377;

    private boolean mFinishChainHead = false;

    private Locale mLastLocale;

    private AtworkAlertDialog mAlertDialog;

    public Activity mActivity;

    protected boolean mShouldClearPermission = true;

    private boolean mIsFromLogin = false;

    protected boolean mIsVisible = false;

    protected boolean mIsFragmentVisibleHint = false;

    private BroadcastReceiver mSettingsChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(DomainSettingsManager.DOMAIN_SETTINGS_CHANGE.equals(action)) {
                onDomainSettingChange();

            } else if(OrganizationSettingsManager.ORG_SETTINGS_CHANGE.equals(action)) {
                onOrgSettingChange();
            }
        }
    };

    private BroadcastReceiver mUndoMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            if (UndoMessageHelper.UNDO_MESSAGE_RECEIVED.equals(intent.getAction())) {
                UndoEventMessage undoEventMessage = (UndoEventMessage) intent.getSerializableExtra(ChatDetailFragment.DATA_NEW_MESSAGE);
                onUndoMsgReceive(undoEventMessage);


            }
        }
    };


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDataInBackHandledFragment();
        SkinMaster.getInstance().attach(this);
        registerCoreBroadcast();
        handleOverallWatermark();
    }

    protected void handleOverallWatermark() {
        if (CustomerHelper.isHighSecurity(mActivity)) {
            addWatermark(mActivity);
        }
    }

    public void addWatermark(Activity activity) {
        final ViewGroup rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        LoginUserBasic meUser = LoginUserInfo.getInstance().getLoginUserBasic(getActivity());
        String token = LoginUserInfo.getInstance().getLoginUserAccessToken(getActivity());
        if (meUser == null || TextUtils.isEmpty(meUser.mUserId) || TextUtils.isEmpty(token)) {
            return;
        }
        User user = UserManager.getInstance().queryUserInSyncByUserId(activity, meUser.mUserId, AtworkConfig.DOMAIN_ID);
        if (user == null) {
            return;
        }
        View framView = WaterMarkUtil.drawTextToBitmap(activity, -1, -1, user, "");
        //可对水印布局进行初始化操作
        rootView.addView(framView);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViews(view);
        handleFakeStatusBar();
    }

    @Override
    public void onStart() {
        super.onStart();

        Locale locale = LanguageUtil.getLocale(BaseApplicationLike.baseContext);
        if (null != mLastLocale && !locale.equals(mLastLocale)) {
            onChangeLanguage();
        }

        mLastLocale = locale;

        mIsVisible = true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        mIsVisible = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mShouldClearPermission) {
            PermissionsManager.getInstance().clear();
        }

        SkinMaster.getInstance().detach(this);
        unregisterCoreBroadcast();

    }

    private void initDataInBackHandledFragment() {
        Bundle bundle = getArguments();
        if(null != bundle) {
            mIsFromLogin = bundle.getBoolean(HandleLoginService.DATA_FROM_LOGIN, false);
        }
    }

    protected abstract void findViews(View view);

    protected void handleFakeStatusBar() {
        View view = getFakeStatusBar();
        if(null != view && StatusBarUtil.supportStatusBarMode()) {
            ViewUtil.setHeight(view, StatusBarUtil.getStatusBarHeight(BaseApplicationLike.baseContext));
            view.setVisibility(View.VISIBLE);
        }
    }

    protected View getFakeStatusBar() {
        return null;
    }


    private void registerCoreBroadcast() {
        IntentFilter settingIntentFilter = new IntentFilter();
        settingIntentFilter.addAction(DomainSettingsManager.DOMAIN_SETTINGS_CHANGE);
        settingIntentFilter.addAction(OrganizationSettingsManager.ORG_SETTINGS_CHANGE);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mSettingsChangeReceiver, settingIntentFilter);


        IntentFilter undoIntentFilter = new IntentFilter();
        undoIntentFilter.addAction(UndoMessageHelper.UNDO_MESSAGE_RECEIVED);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mUndoMessageReceiver, undoIntentFilter);

    }

    private void unregisterCoreBroadcast() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mSettingsChangeReceiver);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mUndoMessageReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (REQUEST_RESULT_BACK == requestCode && !isFinishChainHead()) {

            if (Activity.RESULT_OK == resultCode) {
                getActivity().setResult(Activity.RESULT_OK);
                finish(false);
            }
        }
    }

    protected boolean onBackPressed() {
        finish();
        return false;
    }


    protected String getPageTag() {
        return getClass().getName();
    }

    /**
     * 如果没有使用Viewpager
     * 需要手动调用
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        mIsFragmentVisibleHint = isVisibleToUser;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }


    protected boolean isFragmentVisibleAndHintVisible() {
        return mIsVisible && mIsFragmentVisibleHint;
    }

    protected void showKickDialog(int msg) {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            return;
        }
        mAlertDialog = new AtworkAlertDialog(getActivity(), AtworkAlertDialog.Type.SIMPLE);
        mAlertDialog.hideDeadBtn().setContent(msg);
        mAlertDialog.setCancelable(false);
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.setDismissListener(dialog -> kickToSessionList());

        if (!mAlertDialog.isShowing()) {
            mAlertDialog.show();
        }

    }

    protected void showUndoDialog(Context context, PostTypeMessage message) {
        AtworkAlertDialog dialog = new AtworkAlertDialog(context, AtworkAlertDialog.Type.SIMPLE)
                .setContent(UndoMessageHelper.getUndoContent(context, message))
                .hideDeadBtn()
                .setClickBrightColorListener(dialog1 -> onBackPressed());
        dialog.setCancelable(false);
        dialog.show();
    }


    private void kickToSessionList() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().finish();
    }

    /**
     * 不进行 isAdd() 判断, 即使页面被销毁, 也会显示出 toast
     */
    public void toastOver(@StringRes int id) {
        AtworkToast.showToast(getStrings(id));
    }

    /**
     * 不进行 isAdd() 判断, 即使页面被销毁, 也会显示出 toast
     */
    public void toastOver(@StringRes int resId, Object... formatArgs) {
        AtworkToast.showToast(getStrings(resId, formatArgs));
    }

    /**
     * 不进行 isAdd() 判断, 即使页面被销毁, 也会显示出 toast
     */
    public void toastOver(String content) {
        AtworkToast.showToast(content);
    }

    public void toast(@StringRes int id) {
        if (isAdded()) {
            toast(getString(id));
        }
    }


    public void toast(@StringRes int resId, Object... formatArgs) {
        if (isAdded()) {
            toast(getString(resId, formatArgs));
        }
    }

    public void toast(String content) {
        if (isAdded()) {
            AtworkToast.showToast(content);
        }
    }

    /**
     * 使用{@link com.foreveross.atwork.AtworkApplicationLike#baseContext} 获取 string, 避免 Fragment 里的IllegalStateException bug
     */
    public String getStrings(@StringRes int resId, Object... formatArgs) {
        return getString(BaseApplicationLike.baseContext, resId, formatArgs);
    }

    public String getString(Context context, @StringRes int resId, Object... formatArgs) {
        return context.getString(resId, formatArgs);
    }

    public void finish() {
        finish(true);
    }

    public void finish(boolean needAnim) {
        getActivity().finish();
        if (needAnim) {
            getActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

        }
    }

    protected void initLockView(Lock9View lock9View) {
        BitmapDrawable nodeOnSrc = (BitmapDrawable) lock9View.getNodeOnSrc();
        lock9View.setLineRightColor(SkinHelper.getMainColor());
        Drawable result = new BitmapDrawable(getResources(), SkinMaster.getInstance().transformImmutable(nodeOnSrc.getBitmap(), SkinHelper.getMainColor()));
        lock9View.setNodeOnSrc(result);

    }

    @Override
    public void startActivity(Intent intent) {
        startActivity(intent, true);
    }

    public void startActivity(Intent intent, boolean needAnimation) {
        FragmentActivity activity = getActivity();
        if(null == activity) {
            return;
        }

        if (needAnimation) {
            activity.startActivity(intent);
            //界面切换效果
            activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

        } else {
            if (activity instanceof AtworkBaseActivity) {
                ((AtworkBaseActivity) activity).startActivity(intent, false);

            } else {
                activity.startActivity(intent);

            }
        }

    }

    /**
     * 收到撤回消息通知
     * */
    public void onUndoMsgReceive(UndoEventMessage undoEventMessage) {

    }

    /**
     * 域设置变更
     * */
    @Override
    public void onDomainSettingChange() {

    }

    /**
     * 组织设置变更
     * */
    @Override
    public void onOrgSettingChange() {

    }

    /**
     * 系统语言变化
     */
    public void onChangeLanguage() {

    }

    /**
     * 用于类似聊天界面这种 singleTop 的场景, 例如聊天界面打开其他页面的整个过程假如为 A-> B-> C-> A
     * 这时为了比较好的体验, 是 C 返回 B, 然后 B 再返回到 A
     *
     * @param intent
     * @param isHead 是否是上述描述里面的A
     */
    public void startActivityRouteBack(Intent intent, boolean isHead) {
        startActivityForResult(intent, REQUEST_RESULT_BACK);

        setFinishChainHead(isHead);

        //界面切换效果
        getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    public void setFinishChainHead(boolean isHead) {
        this.mFinishChainHead = isHead;
    }

    public boolean isFinishChainHead() {
        return this.mFinishChainHead;
    }


    public interface OnK9MailClickListener {
        void onK9MailClick(String appId);
    }

    @Override
    public void changeTheme() {

    }

    @Override
    public void onThemeUpdate(Theme theme) {
        //对换肤感兴趣的子类需重写该方法，例如有listview,recyclerview的子类，需要在该方法调用adater.notifyDatasetChanded()
    }

    /**
     * 是否处于登录流程
     * */
    public boolean isInLoginFlow() {
        return mIsFromLogin;
    }


    protected void fullScreen() {
        FragmentActivity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity)activity).fullScreen();
        }
    }

    protected void unfullScreen() {
        FragmentActivity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity)activity).unfullScreen();
        }
    }
}
