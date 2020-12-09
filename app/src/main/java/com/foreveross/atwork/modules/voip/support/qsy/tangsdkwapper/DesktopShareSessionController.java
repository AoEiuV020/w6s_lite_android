package com.foreveross.atwork.modules.voip.support.qsy.tangsdkwapper;

import android.util.Log;
import android.widget.ImageView;

import com.foreveross.atwork.modules.voip.support.qsy.TangSDKInstance;
import com.tang.gnettangsdk.CGNetTangSessionErrorInfo;
import com.tang.gnettangsdk.CGNetTangVariant;
import com.tang.gnettangsdk.IGNetTangDesktopSession;
import com.tang.gnettangsdk.IGNetTangDesktopSessionSink;

public class DesktopShareSessionController extends IGNetTangDesktopSessionSink {
	public DesktopShareSessionController(IGNetTangDesktopSession desktopService){
		m_desktopService = desktopService;
		m_desktopService.setSessionCallback(this);
	}
	public void setImageView(ImageView imageView){
		m_pImageView = imageView;
		if(m_pImageView != null){
			m_pImageView.setScaleType(ImageView.ScaleType.MATRIX);
		}
	}
		
	public int startView(ImageView imageView){
		setImageView(imageView);
		return m_desktopService.startView(imageView);
	}
	public int stopView(){
		m_pImageView = null;
		return m_desktopService.stopView();
	}
	
	public void destroy(){
		m_desktopService.setSessionCallback(null);
	}

	public long getSharerUserID() {
		return m_desktopService.getSharerUserID();
	}

	public long getShareDesktopWidth() {
		return m_desktopService.getShareDesktopWidth();
	}

	public long getShareDesktopHeight() {
		return m_desktopService.getShareDesktopHeight();
	}

	public void zoomView(double scale, int nZoomCenterX, int nZoomCenterY) {
		m_desktopService.zoomView(scale, nZoomCenterX, nZoomCenterY);
	}

	public void scroll(int x, int y) {
		m_desktopService.scroll(x, y);
	}

	public double getZoom() {
		return m_desktopService.getZoom();
	}

	public int getScrollPosX() {
		return m_desktopService.getScrollPosX();
	}

	public int getScrollPosY() {
		return m_desktopService.getScrollPosY();
	}


	public int getPropertyValue(String var1, CGNetTangVariant var2) {
		return m_desktopService.getPropertyValue(var1, var2);
	}
	
	@Override
	public void onDesktopShared() {
		Log.e("VOIP", "DesktopShareSession::onDesktopShared()");
		TangSDKInstance.getInstance().onDesktopShared();
	}
	
	@Override
	public void onDesktopShareStoped() {		
		Log.e("VOIP", "DesktopShareSession::onDesktopShareStoped()");
		TangSDKInstance.getInstance().onDesktopShareStoped();
	}
	
	@Override
	public void onDesktopViewerStarted() {
		Log.e("VOIP", "DesktopShareSession::onDesktopViewerStarted()");
		CGNetTangVariant shareUserId = new CGNetTangVariant();
		m_desktopService.getPropertyValue("shareUserID", shareUserId);
		
		Log.e("VOIP", "getPropertyValue, shareUserId=" + shareUserId.getUintVal());
		
		CGNetTangVariant width = new CGNetTangVariant();
		m_desktopService.getPropertyValue("width", width);	
	
		Log.e("VOIP", "getPropertyValue, width=" + width.getUintVal());
		
		CGNetTangVariant height = new CGNetTangVariant();
		m_desktopService.getPropertyValue("height", height);
		Log.e("VOIP", "getPropertyValue, height=" + height.getUintVal());

		TangSDKInstance.getInstance().onDesktopViewerStarted();
	}
	
	@Override
	public void onDesktopViewerStopped() {	
		Log.e("VOIP", "DesktopShareSession::onDesktopViewerStopped()");
		TangSDKInstance.getInstance().onDesktopViewerStopped();
		if(m_pImageView!=null)
		{
			m_pImageView.setImageBitmap(null);
		}
	}
	
	@Override
	public void onSessionErrorHandle(CGNetTangSessionErrorInfo pErrorInfo) {
		Log.e("VOIP", "DesktopShareSession::onSessionErrorHandle, errInfo: " + pErrorInfo);
	}

	private IGNetTangDesktopSession m_desktopService = null;
	private ImageView m_pImageView = null;

}
