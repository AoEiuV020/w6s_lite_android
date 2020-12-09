package com.foreveross.atwork.broadcast;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;

public abstract class PhoneCallStateReceiver extends BroadcastReceiver {

    //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations

    private static int sLastState = TelephonyManager.CALL_STATE_IDLE;
    private static long sCallStartTime;
    private static boolean sIsIncoming;
    private static String sSavedNumber;  //because the passed incoming is only valid in ringing

    public static boolean sIsCalling = false;

    //init the state
    static {
        TelephonyManager tm = (TelephonyManager) BaseApplicationLike.baseContext.getSystemService(Service.TELEPHONY_SERVICE);

        sIsCalling = TelephonyManager.CALL_STATE_IDLE != tm.getCallState();

        sLastState = tm.getCallState();

    }


    @Override
    public void onReceive(Context context, Intent intent) {

        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        Bundle extras = intent.getExtras();
        if ("android.intent.action.NEW_OUTGOING_CALL".equals(intent.getAction())) {
            if (null != extras) {
                sSavedNumber = extras.getString("android.intent.extra.PHONE_NUMBER");
            }
        }
        else{
            String stateStr = null;
            String number = null;
            if (null != extras) {
                stateStr = extras.getString(TelephonyManager.EXTRA_STATE);
                number = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            }
            int state = 0;
            if(TelephonyManager.EXTRA_STATE_IDLE.equals(stateStr)){
                state = TelephonyManager.CALL_STATE_IDLE;
            }
            else if(TelephonyManager.EXTRA_STATE_OFFHOOK.equals(stateStr)){
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            }
            else if(TelephonyManager.EXTRA_STATE_RINGING.equals(stateStr)){
                state = TelephonyManager.CALL_STATE_RINGING;
            }


            onCallStateChanged(context, state, number);
        }
    }


    //Deals with actual events

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    public void onCallStateChanged(Context context, int state, String number) {
        // android9.0系统需要call log的权限，一旦添加权限，该receiver会接收2次回调，暂时注释
//        if(sLastState == state){
//            //No change, debounce extras
//            return;
//        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                sIsIncoming = true;
                sCallStartTime = System.currentTimeMillis();
                sSavedNumber = number;
                onIncomingCallStarted(context, number, sCallStartTime);
                sIsCalling = true;
                break;

            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if(sLastState != TelephonyManager.CALL_STATE_RINGING){
                    sIsIncoming = false;
                    sCallStartTime = System.currentTimeMillis();
                    onOutgoingCallStarted(context, sSavedNumber, sCallStartTime);
                    sIsCalling = true;

                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if(sLastState == TelephonyManager.CALL_STATE_RINGING){
                    //Ring but no pickup-  a miss
                    onMissedCall(context, sSavedNumber, sCallStartTime);
                }
                else if(sIsIncoming){
                    onIncomingCallEnded(context, sSavedNumber, sCallStartTime, System.currentTimeMillis());
                }
                else{
                    onOutgoingCallEnded(context, sSavedNumber, sCallStartTime, System.currentTimeMillis());
                }

                sIsCalling = false;
                break;
        }
        sLastState = state;
    }

    //Derived classes should override these to respond to specific events of interest
    protected abstract void onIncomingCallStarted(Context ctx, String number, long start);
    protected abstract void onOutgoingCallStarted(Context ctx, String number, long start);
    protected abstract void onIncomingCallEnded(Context ctx, String number, long start, long end);
    protected abstract void onOutgoingCallEnded(Context ctx, String number, long start, long end);
    protected abstract void onMissedCall(Context ctx, String number, long start);
}