package com.foreveross.atwork.modules.voip.support.qsy.tangsdkwapper;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.foreveross.atwork.manager.VoipManager;
import com.foreveross.atwork.modules.voip.support.qsy.TangSDKInstance;
import com.foreveross.atwork.modules.voip.support.qsy.utils.IGnetTangUserHelper;
import com.tang.gnettangsdk.CGNetTangSessionErrorInfo;
import com.tang.gnettangsdk.CGNetTangVariant;
import com.tang.gnettangsdk.GNetTangSDKWrapper;
import com.tang.gnettangsdk.GNetTangSessionType;
import com.tang.gnettangsdk.IGNetTangAudioSession;
import com.tang.gnettangsdk.IGNetTangBaseSession;
import com.tang.gnettangsdk.IGNetTangConference;
import com.tang.gnettangsdk.IGNetTangConferenceSink;
import com.tang.gnettangsdk.IGNetTangDesktopSession;
import com.tang.gnettangsdk.IGNetTangUser;
import com.tang.gnettangsdk.IGNetTangVideoSession;
import com.tang.gnettangsdk.TANG_JOINCONF_STATUS;
import com.tang.gnettangsdk.TANG_LEFTCONF_REASON;
import com.tang.gnettangsdk.TANG_VARENUM;
import com.tang.gnettangsdk.gnettangsdkConstants;


public class VoipMeetingController extends IGNetTangConferenceSink {

    public static final String TAG = "VOIP";

    private static String sLogPath = "";

    private IGNetTangConference mIGNetTangCof = null;
    private AudioSessionController mAudioSessionController = null;
    private VideoSessionController mVideoSessionController = null;
    private DesktopShareSessionController mDesktopSession = null;
    private int mAudioSessionID = 0;
    private int mVideoSessionID = 0;
    private int mDesktopSessionID = 0;

    private static Context sContext = null;

    public VoipMeetingController() {
        mIGNetTangCof = null;
    }

    public static void init(Context context, @NonNull String strLogPath) {
        if (context != null && sContext != context) {
            sContext = context;
            GNetTangSDKWrapper.init(sContext);
        }

        sLogPath = strLogPath;
        GNetTangSDKWrapper.getGNetTangService().setGNetTangConfig(gnettangsdkConstants.GNETTANGCONFIG_SETAPPLOGPATH, new CGNetTangVariant(sLogPath));

    }


    public boolean isValid() {
        return (mIGNetTangCof != null);
    }


    //Join Meeting
    public boolean joinConferenceWithJoinKey(String strJoinKey) {
        if (isValid()) {
            return false;
        }

        mIGNetTangCof = GNetTangSDKWrapper.getGNetTangService().createGNetTangConference(strJoinKey, this);
        if (mIGNetTangCof == null) {
            return false;
        }

        int nRet = mIGNetTangCof.joinConf();
        Log.e(TAG, "IGNetTangConference::joinConf return: " + nRet);
        return true;
    }

    public AudioSessionController getAudioSession() {
        if (!isValid()) {
            return null;
        }

        IGNetTangBaseSession session = null;
        if (this.mAudioSessionID == 0) {
            mAudioSessionID = mIGNetTangCof.createSession(GNetTangSessionType.TMC_SESSIONTYPE_AUDIO);
        }
        if (this.mAudioSessionID != 0) {
            session = mIGNetTangCof.getSession(this.mAudioSessionID);
        }
        if (session == null) {
            return null;
        }
        return mAudioSessionController;
    }

    public VideoSessionController getVideoSession() {
        if (!isValid()) {
            return null;
        }

        IGNetTangBaseSession session = null;
        if (this.mVideoSessionID == 0) {
            mVideoSessionID = mIGNetTangCof.createSession(GNetTangSessionType.TMC_SESSIONTYPE_VIDEO); //fix mantis 0003228
        }
        if (this.mVideoSessionID != 0) {
            session = mIGNetTangCof.getSession(this.mVideoSessionID);
        }
        if (session == null) {
            return null;
        }

        return mVideoSessionController;
    }

    public DesktopShareSessionController getDesktopSession() {
        if (!isValid()) {
            return null;
        }

        IGNetTangBaseSession session = null;
        if (this.mDesktopSessionID == 0) {
            mDesktopSessionID = mIGNetTangCof.createSession(GNetTangSessionType.TMC_SESSIONTYPE_DESKTOP);
        }
        if (this.mDesktopSessionID != 0) {
            session = mIGNetTangCof.getSession(this.mDesktopSessionID);
        }
        if (session == null) {
            return null;
        }

        return mDesktopSession;
    }

    public int leaveConf() {
        if (!isValid()) {
            return -1;
        }
        int nRet = mIGNetTangCof.leaveConf();
        if (nRet != 0) {
            Log.e(TAG, "Conference.leaveConf failed nRet = " + nRet + ", force delete the conference instance.");
            TangSDKInstance.getInstance().onConfLeft(TANG_LEFTCONF_REASON.LEFTCONFREASON_SELFLEFT.swigValue());
            releaseConference();
        }
        return nRet;
    }

    public int endConf() {
        if (!isValid()) {
            return -1;
        }
        int nRet = mIGNetTangCof.endConf();
        if (nRet != 0) {
            Log.e(TAG, "Conference.endConf failed nRet = " + nRet + ", force delete the conference instance.");
            TangSDKInstance.getInstance().onConfLeft(TANG_LEFTCONF_REASON.LEFTCONFREASON_SELFLEFT.swigValue());
            releaseConference();

        }
        return nRet;
    }

    public void releaseConference() {
        if (isValid()) {
            GNetTangSDKWrapper.getGNetTangService().deleteGNetTangConference(mIGNetTangCof);
            mIGNetTangCof = null;
        }
    }


    public int reconnectConf() {
        if (!isValid()) {
            return -1;
        }
        return mIGNetTangCof.reconnectConf();
    }

    public long getConfID() {
        if (!isValid()) {
            return -1;
        }
        return mIGNetTangCof.getConfID();
    }

    public int createSession(GNetTangSessionType sessionType) {
        if (!isValid()) {
            return -1;
        }
        return mIGNetTangCof.createSession(sessionType);
    }

    public int closeSession(int sessionID) {
        if (!isValid()) {
            return -1;
        }
        return mIGNetTangCof.closeSession(sessionID);
    }

    public IGNetTangBaseSession getSession(int sessionID) {
        if (!isValid()) {
            return null;
        }

        return mIGNetTangCof.getSession(sessionID);
    }

    public int getPropertyValue(String propName, CGNetTangVariant value) {
        if (!isValid()) {
            return -1;
        }

        return mIGNetTangCof.getPropertyValue(propName, value);
    }

    public long getUserCount() {
        if (!isValid()) {
            return 0;
        }
        return mIGNetTangCof.getUserCount();
    }

    public IGNetTangUser getUserByIndex(int index) {
        if (!isValid()) {
            return null;
        }
        return mIGNetTangCof.getUserByIndex(index);
    }

    public IGNetTangUser getUserByID(long userID) {
        if (!isValid()) {
            return null;
        }

        return mIGNetTangCof.getUserByID(userID);
    }

    public IGNetTangUser getUserByName(String userName) {
        if (!isValid()) {
            return null;
        }

        IGNetTangUser tangUserFind = null;
        long nConfUserCount = mIGNetTangCof.getUserCount();
        for (int i = 0; i < nConfUserCount; i++) {
            IGNetTangUser tangUser = mIGNetTangCof.getUserByIndex(i);
            if (tangUser == null) {
                continue;
            }

            if (tangUser.getUserName().compareTo(userName) == 0) {
                tangUserFind = tangUser;
                break;
            }
        }

        return tangUserFind;
    }

    public IGNetTangUser getMyself() {
        if (!isValid()) {
            return null;
        }

        return mIGNetTangCof.getMyself();
    }


    @Override
    public void onSessionErrorHandle(CGNetTangSessionErrorInfo pErrorInfo) {
        try {
            Log.e(TAG, "Conference::onSessionErrorHandle");

            TangSDKInstance.getInstance().onSessionErrorHandle(pErrorInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConfJoined(int statusCode) {

        try {
            long nConfId = mIGNetTangCof.getConfID();

            Log.e(TAG, "onConfJoined nConfId: " + nConfId + " statusCode: " + statusCode);

            mAudioSessionID = mIGNetTangCof.createSession(GNetTangSessionType.TMC_SESSIONTYPE_AUDIO);
            mVideoSessionID = mIGNetTangCof.createSession(GNetTangSessionType.TMC_SESSIONTYPE_VIDEO);
            mDesktopSessionID = mIGNetTangCof.createSession(GNetTangSessionType.TMC_SESSIONTYPE_DESKTOP);

            if (TANG_JOINCONF_STATUS.swigToEnum(statusCode) == TANG_JOINCONF_STATUS.JOINCONFSTATUS_NETWORKCONNECTFAILED ||
                    TANG_JOINCONF_STATUS.swigToEnum(statusCode) == TANG_JOINCONF_STATUS.JOINCONFSTATUS_NETWORKAUTHFAILED) {

                releaseConference();
            } else if (TANG_JOINCONF_STATUS.swigToEnum(statusCode) == TANG_JOINCONF_STATUS.JOINCONFSTATUS_GETCONFINFOFAILED ||
                    TANG_JOINCONF_STATUS.swigToEnum(statusCode) == TANG_JOINCONF_STATUS.JOINCONFSTATUS_GETUSERINFOFAILED) {
                mIGNetTangCof.leaveConf();
            }

            TangSDKInstance.getInstance().onConfJoined(statusCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConfLeft(int statusCode) {
        try {
            Log.e(TAG, "onConfLeft statusCode: " + statusCode);

            TangSDKInstance.getInstance().onConfLeft(statusCode);

            if (TANG_LEFTCONF_REASON.swigToEnum(statusCode) == TANG_LEFTCONF_REASON.LEFTCONFREASON_NETWORKDISCONNECT) {
                Log.e(TAG, "onConfLeft reconnectConf");
            } else {
                mAudioSessionController = null;
                mVideoSessionController = null;
                mDesktopSession = null;

                //Before call deleteGNetTangConference interface, must make sure do clear action
                releaseConference();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfEnded(int statusCode) {
        onConfLeft(statusCode);
    }

    @Override
    public void onConfReconnected(int statusCode) {

        Log.e(TAG, "onConfReconnected statusCode: " + statusCode);

        //fix bug [B160114-002]
        TangSDKInstance.getInstance().onConfReconnected();
    }

    @Override
    public void onSessionCreated(GNetTangSessionType sessionType, IGNetTangBaseSession pTangSession, int statusCode) {

        Log.e(TAG, "onServiceStarted: " + sessionType + " pTangSession: " + pTangSession);
        if (pTangSession == null) {
            Log.e(TAG, "onServiceStarted - pTangSession instancce is null.");
            return;
        }
        if (sessionType == GNetTangSessionType.TMC_SESSIONTYPE_AUDIO) {
            mAudioSessionID = pTangSession.getSessionID();//Ranger fix cannot get session issue

            mAudioSessionController = new AudioSessionController((IGNetTangAudioSession) pTangSession);
            //m_AudioSession.startVoip();

            TangSDKInstance.getInstance().onAudioSessionCreated();
        } else if (sessionType == GNetTangSessionType.TMC_SESSIONTYPE_VIDEO) {

            mVideoSessionID = pTangSession.getSessionID();//Ranger fix cannot get session issue

            mVideoSessionController = new VideoSessionController((IGNetTangVideoSession) pTangSession);
        } else if (sessionType == GNetTangSessionType.TMC_SESSIONTYPE_DESKTOP) {

            mDesktopSessionID = pTangSession.getSessionID();//Ranger fix cannot get session issue

            mDesktopSession = new DesktopShareSessionController((IGNetTangDesktopSession) pTangSession);
            TangSDKInstance.getInstance().onDesktopSessionCreated();
        }
    }

    @Override
    public void onSessionClosed(GNetTangSessionType sessionType, int sessionID) {

        //audio service is stopped, so application layer can do some clean action
        if (sessionID == mAudioSessionID) {
            mAudioSessionController = null;
        } else if (sessionID == mVideoSessionID) {
            mVideoSessionController = null;
        } else if (sessionID == mDesktopSessionID) {
            mDesktopSession = null;
        }
        Log.e(TAG, "onSessionClosed: " + " sessionID: " + sessionID + " sessionType: " + sessionType);
    }

    @Override
    public void onConfPropertyChanged(String propName, CGNetTangVariant oldPropValue, CGNetTangVariant newPropValue) {

        try {
            long lVar = 0;
            if (TANG_VARENUM.swigToEnum(oldPropValue.getVt()) == TANG_VARENUM.TANG_VT_UINT) {
                lVar = oldPropValue.getUintVal();
            }

            Log.e(TAG, "onConfPropertyChanged propName: " + propName + " getVt: " + oldPropValue.getVt() + " lVar: " + lVar);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUserAdded(IGNetTangUser pUser) {

        try {
            if (pUser != null) {
                Log.e(TAG, "onUserAdded" + " getuserID: " + pUser.getUserID() + " username: " + pUser.getUserName());

                String userId = IGnetTangUserHelper.getUserId(pUser);
                TangSDKInstance.getInstance().onUserAdded(userId);

                VoipManager.getInstance().getTimeController().cancel(userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUserRemoved(IGNetTangUser pUser) {
        try {
            if (pUser != null) {
                Log.e(TAG, "onUserRemoved" + " getuserID: " + pUser.getUserID() + " username: " + pUser.getUserName());

                if (TangSDKInstance.getInstance().checkUnbindedConfUserExist(pUser)) {
                    TangSDKInstance.getInstance().onUserUpdate(IGnetTangUserHelper.getUserId(pUser));
                } else {
                    TangSDKInstance.getInstance().onUserRemoved(IGnetTangUserHelper.getUserId(pUser));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUserPropertyChanged(String propName, CGNetTangVariant oldPropValue, CGNetTangVariant newPropValue, IGNetTangUser pUser) {

        try {
            if (TANG_VARENUM.TANG_VT_UINT == TANG_VARENUM.swigToEnum(newPropValue.getVt())) {
                Log.e(TAG, "onUserPropertyChanged TANG_VT_UINT: " + newPropValue.getUintVal() + " newPropValue propName: " + propName);
            } else if (TANG_VARENUM.TANG_VT_BSTR == TANG_VARENUM.swigToEnum(newPropValue.getVt())) {
                Log.e(TAG, "onUserPropertyChanged TANG_VT_BSTR: " + newPropValue.getPUtf8Val() + " newPropValue propName: " + propName);
            } else if (TANG_VARENUM.TANG_VT_BOOL == TANG_VARENUM.swigToEnum(newPropValue.getVt())) {
                Log.e(TAG, "onUserPropertyChanged TANG_VT_BOOL: " + newPropValue.getBoolVal() + " newPropValue propName: " + propName);
            } else {
                Log.e(TAG, "onUserPropertyChanged type: " + TANG_VARENUM.swigToEnum(oldPropValue.getVt()));
            }

            if (TANG_VARENUM.TANG_VT_UINT == TANG_VARENUM.swigToEnum(oldPropValue.getVt())) {
                Log.e(TAG, "onUserPropertyChanged TANG_VT_UINT: " + oldPropValue.getUintVal() + " oldPropValue: " + propName);
            } else if (TANG_VARENUM.TANG_VT_BSTR == TANG_VARENUM.swigToEnum(oldPropValue.getVt())) {
                Log.e(TAG, "onUserPropertyChanged TANG_VT_BSTR: " + oldPropValue.getPUtf8Val() + " oldPropValue: " + propName);
            } else if (TANG_VARENUM.TANG_VT_BOOL == TANG_VARENUM.swigToEnum(oldPropValue.getVt())) {
                Log.e(TAG, "onUserPropertyChanged TANG_VT_BOOL: " + oldPropValue.getBoolVal() + " oldPropValue: " + propName);
            } else {
                Log.e(TAG, "onUserPropertyChanged type: " + TANG_VARENUM.swigToEnum(oldPropValue.getVt()));
            }


            if (pUser != null) {
                Log.e(TAG, "onUserPropertyChanged" + " getuserID: " + pUser.getUserID() + " username: " + pUser.getUserName());
                if (propName.compareTo("isSpeaking") == 0) {
                    TangSDKInstance.getInstance().onIsSpeakingChanged(IGnetTangUserHelper.getUserId(pUser), newPropValue.getUintVal() != 0);
                } else if (propName.compareTo("audioStatus") == 0) {
                    TangSDKInstance.getInstance().onAudioStatusChanged(pUser);
                } else {
                    TangSDKInstance.getInstance().onUserUpdate(IGnetTangUserHelper.getUserId(pUser));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

