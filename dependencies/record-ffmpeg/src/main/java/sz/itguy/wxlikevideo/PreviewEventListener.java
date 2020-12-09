package sz.itguy.wxlikevideo;

/**
 * Created by dasunsy on 16/3/7.
 */
/**
 * 预览画面改变监听器
 *
 * @author Martin
 */
public interface PreviewEventListener {

    /**
     * 预览开始前回调
     */
    void onPrePreviewStart();

    /**
     * 预览成功回调
     */
    void onPreviewStarted();

    /**
     * 预览失败回调
     */
    void onPreviewFailed();

    /**
     * 对焦完成回调
     *
     * @param success
     */
    void onAutoFocusComplete(boolean success);

}
