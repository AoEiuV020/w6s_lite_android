package com.foreveross.atwork.broadcast;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.FloatPos;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.shared.EmpIncomingCallShareInfo;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.NetworkStatusUtil;
import com.foreveross.atwork.manager.EmpIncomingCallManager;
import com.foreveross.atwork.manager.VoipMeetingController;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.w6s.handyfloat.FloatConfig;
import com.w6s.handyfloat.HandyFloat;
import com.w6s.handyfloat.enums.SeamMode;
import com.w6s.handyfloat.enums.ShowMode;
import com.w6s.handyfloat.interfaces.OnFloatCallbacks;
import com.w6s.model.incomingCall.IncomingCaller;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import kotlin.Pair;

/**
 * Created by dasunsy on 2016/10/9.
 */

public class WorkPlusPhoneCallStateReceiver extends PhoneCallStateReceiver {

    public static final String FLOAT_INCOMING_CALL_TAG = "FLOAT_INCOMING_CALL_TAG";

    private static long sIncomingCallTime = -1;
    private static Handler sCheckHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.e("photo_state", "tag = " + String.valueOf(msg.obj));

            HandyFloat.dismissAppFloat(BaseApplicationLike.baseContext, String.valueOf(msg.obj));
        }
    };


    @Override
    protected void onIncomingCallStarted(Context ctx, String number, long start) {

        handleCallStarted(ctx, true);
        if (!TextUtils.isEmpty(number) && EmpIncomingCallShareInfo.getInstance().getEmpIncomingCallAssistantSetting(ctx)) {
            showFloatingView(ctx, number);
        }
    }


    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, long start) {

        handleCallStarted(ctx, true);
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, long start, long end) {
        dismissFloatingView(ctx);
        handleCallStarted(ctx, false);


    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, long start, long end) {

        handleCallStarted(ctx, false);

    }

    @Override
    protected void onMissedCall(Context ctx, String number, long start) {
        dismissFloatingView(ctx);
        handleCallStarted(ctx, false);

    }

    private void handleCallStarted(Context ctx, boolean isMute) {
        if(VoipHelper.isHandlingVoipCall()) {

            boolean networkAvailable = NetworkStatusUtil.isNetworkAvailable(ctx);

            LogUtil.e("photo_state", "networkAvailable -> " + networkAvailable);

            if(networkAvailable) {
                VoipMeetingController.getInstance().muteAll(isMute);

            }
        }
    }

    private void showFloatingView(Context context, String phoneNum) {
        EmpIncomingCallManager.getInstance().asyncQueryEmpIncomingCaller(phoneNum, caller -> show(context, caller));
    }

    private void dismissFloatingView(Context context) {
        Logger.e("photo_state", "dismissing float");
        if (context == null) {
            Logger.e("photo_state", "incoming call end Context is null");
            return;
        }
        HandyFloat.dismissAppFloat(context, FLOAT_INCOMING_CALL_TAG + sIncomingCallTime);
    }

    private void show(Context context, IncomingCaller caller) {
        if (context == null) {
            Logger.e("photo_state", "incoming call start context is null");
            return;
        }
        sIncomingCallTime = System.currentTimeMillis();
        String tag = FLOAT_INCOMING_CALL_TAG + sIncomingCallTime;
        Message message = new Message();
        message.obj = tag;
        sCheckHandler.sendMessageDelayed(message, 30 * 1000);
        FloatConfig config = new FloatConfig();
        config.setLayoutId(R.layout.float_incoming_call);
        config.setSeamMode(SeamMode.Default);
        config.setShowMode(ShowMode.Always);
        config.setFloatTag(tag);
        config.setDragEnable(true);
        config.setGravity(Gravity.CENTER);
        FloatPos pos = CommonShareInfo.getFloatingPos(AtworkApplicationLike.baseContext);
        Pair<Integer, Integer> location = new Pair<>(pos.getPosX(), pos.getPosY());
        config.setLocationPair(location);
        config.setSetContentViewInterface(view -> {
            initViewAndData(view, caller);
        });
        config.setFloatCallbacks(new OnFloatCallbacks() {
            @Override
            public void createdResult(boolean isCreated, @Nullable String msg, @Nullable View view) {}

            @Override
            public void show(@NotNull View view) {}

            @Override
            public void hide(@NotNull View view) {}

            @Override
            public void dismiss() {}

            @Override
            public void touchEvent(@NotNull View view, @NotNull MotionEvent event) {}

            @Override
            public void drag(@NotNull View view, @NotNull MotionEvent event) {}

            @Override
            public void dragEnd(@NotNull View view) {

                FloatPos floatPos = new FloatPos();
                int[] position = new int[2];
                view.getLocationOnScreen(position);
                floatPos.setPosX(position[0]);
                floatPos.setPosY(position[1]-80);
                CommonShareInfo.setFloatingPos(AtworkApplicationLike.baseContext, floatPos);

            }
        });
        HandyFloat.createAppFloat(context, config);
    }

    private void initViewAndData(View view, IncomingCaller caller) {
        View infoLayout = view.findViewById(R.id.info_layout);
        TextView tvName = view.findViewById(R.id.name);
        TextView tvPos = view.findViewById(R.id.position_info);
        TextView tvComp = view.findViewById(R.id.org_info);
        TextView unknown = view.findViewById(R.id.known_contact);

        if (caller == null || TextUtils.isEmpty(caller.getName())) {
            infoLayout.setVisibility(View.INVISIBLE);
            unknown.setVisibility(View.VISIBLE);
            return;
        }

        tvName.setText(caller.getName());
        StringBuilder stringBuilder = new StringBuilder();
        if (!TextUtils.isEmpty(caller.getJobTitle()) && DomainSettingsManager.getInstance().showJobTitleOnPhoneFloating()) {
            stringBuilder.append(caller.getJobTitle()).append("-");
        }
        if (DomainSettingsManager.getInstance().showDirectlyOrgOnPhoneFloating()) {
            stringBuilder.append(caller.getOrgName());
        }
        String posInfo = stringBuilder.toString();
        if (posInfo.endsWith("-")) {
            int length = posInfo.length();
            posInfo = posInfo.substring(0, length - 1);
        }

        tvPos.setText(posInfo);
        if (DomainSettingsManager.getInstance().showDirectlyCorpOnPhoneFloating()) {
            tvComp.setText(caller.getCorpName());
            return;
        }
    }


}
