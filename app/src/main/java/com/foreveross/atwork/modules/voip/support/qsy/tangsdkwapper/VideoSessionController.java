package com.foreveross.atwork.modules.voip.support.qsy.tangsdkwapper;

import android.util.Log;

import com.foreveross.atwork.modules.voip.support.qsy.TangSDKInstance;
import com.tang.gnettangsdk.CGNetTangSessionErrorInfo;
import com.tang.gnettangsdk.CGNetTangVariant;
import com.tang.gnettangsdk.IGNetTangVideoInstance;
import com.tang.gnettangsdk.IGNetTangVideoSession;
import com.tang.gnettangsdk.IGNetTangVideoSessionSink;

public class VideoSessionController extends IGNetTangVideoSessionSink {

    public VideoSessionController(IGNetTangVideoSession videoService){
        m_videoSession = videoService;
        if( m_videoSession != null )
            m_videoSession.setSessionCallback(this);
    }

    public int changeCameraOrientation(int orientation) {
        return m_videoSession.changeCameraOrientation(orientation);
    }

    public int resetRenderWindow(long nUserID, Object pWindow, int nRenderMode) {
        return m_videoSession.resetRenderWindow(nUserID,pWindow,nRenderMode );
    }

    public long getCameraCount() {
        return m_videoSession.getCameraCount();
    }

    public void getCameraName(int nIndex, String sDeviceName) {
        m_videoSession.getCameraName(nIndex, sDeviceName);
    }

    public void startPreview(int nDevicIndex, Object pWindow, int nRenderMode) {
        m_videoSession.startPreview(nDevicIndex, pWindow,nRenderMode);
    }

    public void stopPreview() {
        m_videoSession.stopPreview();
    }

    public void startView(long nUserID, Object pWindow, int nRenderMode) {
        m_videoSession.startView(nUserID, pWindow,nRenderMode);
    }

    public void stopView(long nUserID) {
        m_videoSession.stopView(nUserID);
    }

    public void startShare(long nUserID, int nDevicIndex, long nWidth, long nHeight) {
        m_videoSession.startShare(nUserID, nDevicIndex, nWidth, nHeight);
    }

    public void stopShare(long nUserID) {
        m_videoSession.stopShare(nUserID);
    }

    public void requestResolution(long nUserID, long nWidth, long nHeight) {
        m_videoSession.requestResolution(nUserID, nWidth, nHeight);
    }

    public void changeShareCamera(long nUserID, int nDevicIndex) {
        m_videoSession.changeShareCamera(nUserID, nDevicIndex);
    }

    public long getVideoInstanceCount() {
        return m_videoSession.getVideoInstanceCount();
    }

    public IGNetTangVideoInstance getVideoInstanceByIndex(long nIndex) {
            return m_videoSession.getVideoInstanceByIndex(nIndex);
    }

    public IGNetTangVideoInstance getVideoInstanceByUserID(long nUserID) {
        return m_videoSession.getVideoInstanceByUserID(nUserID);
    }

    public IGNetTangVideoInstance getVideoInstanceMyself() {
        return m_videoSession.getVideoInstanceMyself();
    }

    public int getPropertyValue(String propName, CGNetTangVariant value) {
        return m_videoSession.getPropertyValue(propName, value);
    }

    @Override
    public void onVideoInstanceAdded(IGNetTangVideoInstance pInst) {

        try{
            long videoUserID = 0;
            if (pInst != null) {
                videoUserID = pInst.getVideoUserID();
                Log.e("VOIP", "VideoSession::onVideoInstanceAdded" + videoUserID);

                TangSDKInstance.getInstance().onVideoItemAdded(videoUserID);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onVideoInstanceRemoved(IGNetTangVideoInstance pInst) {
        try{
            long videoUserID = 0;
            if (pInst != null) {
                videoUserID = pInst.getVideoUserID();
                Log.e("VOIP", "VideoSession::onVideoInstanceRemoved " + videoUserID);
                TangSDKInstance.getInstance().onVideoItemDeleted(videoUserID);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onSessionErrorHandle(CGNetTangSessionErrorInfo pErrorInfo) {
        Log.e("VOIP", "VideoSession::onSessionErrorHandle()");
    }

    @Override
    public void onVideoPropertyChanged(String propName, CGNetTangVariant oldPropValue, CGNetTangVariant newPropValue) {
        Log.e("VOIP", "VideoSession::onVideoPropertyChanged()");
    }

    @Override
    public void onVideoInstancePropertyChanged(String propName, CGNetTangVariant oldPropValue, CGNetTangVariant newPropValue, IGNetTangVideoInstance pInst) {
        Log.e("VOIP", "VideoSession::onVideoInstancePropertyChanged()");
        try{
            if(propName.compareTo("showdataready")==0 && newPropValue.getUintVal()!=0 ){
                long videoUserID = pInst.getVideoUserID();
                Log.e("VOIP", "VideoSession::onVideoInstancePropertyChanged  showdataready" + videoUserID);
                TangSDKInstance.getInstance().onVideoItemShowed(videoUserID);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private IGNetTangVideoSession m_videoSession = null;
}