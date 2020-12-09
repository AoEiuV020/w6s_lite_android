package com.foreveross.atwork.modules.setting.fragment;

import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreverht.cache.MessageCache;
import com.foreverht.db.service.repository.MessageRepository;
import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreverht.threadGear.DbThreadPoolExecutor;
import com.foreverht.workplus.ui.component.dialogFragment.W6sAtworkAlertDialog;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.support.BackHandledFragment;

import java.util.ArrayList;

import static com.foreverht.webview.WebkitSdkUtil.clearCookies;
import static com.foreveross.atwork.infrastructure.utils.FileUtil.deleteFile;
import static com.foreveross.atwork.infrastructure.utils.FileUtil.formatFromSize;
import static com.foreveross.atwork.infrastructure.utils.FileUtil.getFileDicSize;

/**
 * Created by wuzejie on 2019/9/17.
 */

public class StorageSpaceSettingFragment extends BackHandledFragment {

    private ImageView mIvBack;
    private TextView mTvTitle;
    private TextView mTvRightest;
    private TextView mTvClearHistory;
    private TextView mTvComputingHistoryRecord;
    private TextView mTvCleanHistoryRecordTip;
    private TextView mTvClearLocalFile;
    private TextView mTvComputingLocalFile;
    private TextView mTvClearBrowserCache;
    private TextView mTvClearAllCache;

    private boolean finishHistoryCalculation = false;
    private boolean finishLocalCalculation = false;
    private boolean finishHistoryClear = false;
    private boolean finishLocalClear = false;
    private boolean finishBrowerClear = false;
    private int finishallCalculation = 0;
    private boolean mForbiddenHandle = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_storage_space_setting, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();

        registerListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
    }

    @Override
    protected void findViews(View view) {
        mIvBack = view.findViewById(R.id.title_bar_common_back);
        mTvTitle = view.findViewById(R.id.title_bar_common_title);
        mTvRightest = view.findViewById(R.id.title_bar_common_right_text);
        mTvClearHistory = view.findViewById(R.id.clear_history);
        mTvComputingHistoryRecord = view.findViewById(R.id.computing_history_record);
        mTvCleanHistoryRecordTip = view.findViewById(R.id.tv_clean_history_record_tip);
        mTvClearLocalFile = view.findViewById(R.id.clear_local_file);
        mTvComputingLocalFile = view.findViewById(R.id.computing_local_file);
        mTvClearBrowserCache = view.findViewById(R.id.clear_browser_cache);
        mTvClearAllCache = view.findViewById(R.id.clear_all_cache);

    }

    private void initViews() {
        mTvTitle.setText(R.string.storage_space);
        mTvRightest.setVisibility(View.GONE);
        mTvClearBrowserCache.setBackgroundResource(R.drawable.shape_setting_blue);
        mTvCleanHistoryRecordTip.setText(getString(R.string.tip_about_clear_history_messages, AtworkConfig.CHAT_CONFIG.getCleanMessagesThreshold() + ""));
    }

    private void registerListener() {
        mIvBack.setOnClickListener(v -> onBackPressed());

        mTvClearHistory.setOnClickListener(v -> {
            if(finishHistoryCalculation && !finishHistoryClear ){
                cleanMessageThresholdAndHistoryFile();
            }else{
                return;
            }
            //showLocalFile2();
        });

        mTvClearLocalFile.setOnClickListener(v -> {
            if (finishLocalCalculation && !finishLocalClear) {
                AsyncTaskThreadPool.getInstance().execute(() -> {

                    //清理本地缓存
                    boolean result = clearLocalFile();
                    String localData = computingHistoryFile();
                    AtworkApplicationLike.runOnMainThread(() -> {

                        if (result) {
                            mTvComputingLocalFile.setText(R.string.data_is_empty);
                            finishLocalClear = true;
                            mTvClearLocalFile.setBackgroundResource(R.drawable.shape_setting_gray);
                            mTvComputingHistoryRecord.setText(localData);
                            toastOver(R.string.clean_messages_data_successfully);
                        } else {
                            toastOver(R.string.clean_messages_data_unsuccessfully);

                        }
                    });
                });

            } else {
                return;
            }
        });

        mTvClearBrowserCache.setOnClickListener(v -> {
            if(!finishBrowerClear){
                AsyncTaskThreadPool.getInstance().execute(() -> {

                //清理浏览器缓存
                boolean result = clearBrowserCache();
                AtworkApplicationLike.runOnMainThread(() -> {

                        if (result) {
                            finishBrowerClear = true;
                            mTvClearBrowserCache.setBackgroundResource(R.drawable.shape_setting_gray);
                            toastOver(R.string.clean_messages_data_successfully);
                        } else {
                            toastOver(R.string.clean_messages_data_unsuccessfully);

                        }
                    });
                });
            }else {
                return;
            }

        });

        mTvClearAllCache.setOnClickListener(v -> {
            if(2 == finishallCalculation){
                W6sAtworkAlertDialog atworkAlertDialog = new W6sAtworkAlertDialog(getActivity(), W6sAtworkAlertDialog.Type.CLASSIC)
                        .setTitleText(R.string.clean_all_messages_data)
                        .setContent(getStrings(R.string.clean_all_messages_data_tip, AtworkConfig.CHAT_CONFIG.getCleanMessagesThreshold()))
                        .setClickBrightColorListener(dialog -> {
                            clearAllMessageThread();
                        });

                atworkAlertDialog.show();

            }else{
                return;
            }
        });
    }
    /**
     * 通过线程池计算历史文件
     * */
    public void showHistoryFile(){
        AsyncTaskThreadPool.getInstance().execute(() -> {

            String result =computingHistoryFile();
            AtworkApplicationLike.runOnMainThread(() -> {

                if(result!=null&&!result.equals("")) {
                    mTvComputingHistoryRecord.setText(result);
                    mTvComputingHistoryRecord.setTextColor(getResources().getColor(R.color.black));
                    TextPaint tp = mTvComputingHistoryRecord.getPaint();
                    tp.setFakeBoldText(true);
                    mTvClearHistory.setBackgroundResource(R.drawable.shape_setting_blue);
                    finishHistoryCalculation = true;
                    finishallCalculation +=1;
                    if(2 == finishallCalculation){
                        mTvClearAllCache.setTextColor(getResources().getColor(R.color.common_blue_bg));
                    }
                } else {
                    toastOver(R.string.clean_messages_data_unsuccessfully);
                }
            });
        });
    }
    /**
     * 通过线程池计算本地文件
     * */
    public void showLocalFile(){
        AsyncTaskThreadPool.getInstance().execute(() -> {

            String result =computingLocalFile();
            AtworkApplicationLike.runOnMainThread(() -> {

                if(result!=null&&!result.equals("")) {
                    mTvComputingLocalFile.setText(result);
                    mTvComputingLocalFile.setTextColor(getResources().getColor(R.color.black));
                    TextPaint tp = mTvComputingLocalFile.getPaint();
                    tp.setFakeBoldText(true);
                    mTvClearLocalFile.setBackgroundResource(R.drawable.shape_setting_blue);
                    finishLocalCalculation = true;
                    finishallCalculation +=1;
                    if(2 == finishallCalculation){
                        mTvClearAllCache.setTextColor(getResources().getColor(R.color.common_blue_bg));
                    }
                } else {
                    toastOver(R.string.clean_messages_data_unsuccessfully);
                }
            });
        });
    }
    /**
     * 获取消息类文件路径（图片、小视频、文件等数据，清理后，每个会话保留最近1万/20条消息记录）
     * */
    public ArrayList<String> getHistoryFilePath(){
        //路径数组
        ArrayList<String> filePath = new ArrayList<>();
        String tempPath;

        //获取音频路径
        tempPath = AtWorkDirUtils.getInstance().getAUDIO(getContext());
        filePath.add(tempPath);

        //获取小视频接受路径
        tempPath = AtWorkDirUtils.getInstance().getMicroVideoDir(getContext());
        filePath.add(tempPath);

        //获取小视频发送路径
        tempPath = AtWorkDirUtils.getInstance().getMicroVideoHistoryDir(getContext());
        filePath.add(tempPath);

        //获取聊天文件路径
        tempPath = AtWorkDirUtils.getInstance().getChatFiles(getContext());
        filePath.add(tempPath);

        //获取图片路径
        tempPath = AtWorkDirUtils.getInstance().getImageDir(getContext());
        filePath.add(tempPath);

        return filePath;
    }


    /**
     * 获取本地文件路径（网盘、邮箱下载、轻应用离线包数据）以及消息类文件路径（图片、小视频、文件等数据）
     * */
    public ArrayList<String> getLocalFilePath(){

        //路径数组
        ArrayList<String> filePath = new ArrayList<>();
        String tempPath;

        //消息类文件
        filePath = getHistoryFilePath();

        //获取网盘路径
        tempPath = AtWorkDirUtils.getInstance().getDropboxDir(getContext());
        filePath.add(tempPath);
        //获取邮箱路径
        tempPath = AtWorkDirUtils.getInstance().getEmailAttachmentDir(LoginUserInfo.getInstance().getLoginUserUserName(getContext()));
        filePath.add(tempPath);

        //获取离线包数据路径
        tempPath = AtWorkDirUtils.getInstance().getDataRootDir();
        filePath.add(tempPath);


        //获取文件路径
        tempPath = AtWorkDirUtils.getInstance().getFiles(getContext());
        filePath.add(tempPath);

        return filePath;
    }



    /**
     * 计算历史文件
     * */
    public String computingHistoryFile(){
        String result = "";
        //路径数组
        ArrayList<String> filePath = new ArrayList<>();
        filePath = getHistoryFilePath();
        Log.e("路径数组", "computingHistoryFile: "+filePath.toString() );
        //计算本地文件
        result = formatFromSize(getFileDicSize(filePath));
        return result;
    }
    /**
     * 删除历史文件
     * */
    public Boolean clearHistoryFile(){
        ArrayList<String> filePath = new ArrayList<>();
        filePath = getHistoryFilePath();
        Boolean result = deleteFile(filePath,false);
        return result;
    }

    /**
     * 删除1万条之后的消息记录和历史文件
     * */
    private void cleanMessageThresholdAndHistoryFile() {
        ProgressDialogHelper progressDialogHelper = new ProgressDialogHelper(getActivity());
        progressDialogHelper.show(false, 30000);

        DbThreadPoolExecutor.getInstance().execute(() -> {
            boolean result1 = MessageRepository.getInstance().cleanMessages2Threshold();
            boolean result2 = clearHistoryFile();
            if(result1) {
                MessageCache.getInstance().clear();
            }
            String localData = computingLocalFile();
            AtworkApplicationLike.runOnMainThread(() -> {
                progressDialogHelper.dismiss();

                if(result1 && result2) {
                    mTvComputingHistoryRecord.setText(R.string.data_is_empty);
                    finishHistoryClear = true;
                    mTvClearHistory.setBackgroundResource(R.drawable.shape_setting_gray);
                    mTvComputingLocalFile.setText(localData);
                    toastOver(R.string.clean_messages_data_successfully);
                } else {
                    toastOver(R.string.clean_messages_data_unsuccessfully);

                }
            });
        });
    }
    /**
     * 计算本地文件
     * */
    public String computingLocalFile(){
        String result = "";
        //路径数组
        ArrayList<String> filePath = new ArrayList<>();
        filePath = getLocalFilePath();
        Log.e("路径数组", "computingLocalFile: "+filePath.toString() );
        //计算本地文件
        result = formatFromSize(getFileDicSize(filePath));
        return result;
    }
    /**
     * 清理本地文件
     * */
    public Boolean clearLocalFile(){
        ArrayList<String> filePath = new ArrayList<>();
        filePath = getLocalFilePath();
        Boolean result = deleteFile(filePath,false);
        return result;
    }
    /**
     * 清理浏览器缓存
     * */
    public Boolean clearBrowserCache(){

        clearCookies();
        LoginUserInfo loginUserInfo = new LoginUserInfo();
        loginUserInfo.setUserNeedClearWebview(getContext(),true);
        return true;
    }

    /**
     * 通过线程池清理全部缓存
     * */
    private void clearAllMessageThread() {
        ProgressDialogHelper progressDialogHelper = new ProgressDialogHelper(getActivity());
        progressDialogHelper.show(false, 30000);

        AsyncTaskThreadPool.getInstance().execute(() -> {
            //清理全部缓存
            boolean result1 = MessageRepository.getInstance().cleanMessages2Threshold();//清除10000条之后的消息缓存
            boolean result2 = clearHistoryFile();//清除历史文件
            boolean result3 = clearLocalFile();//清除本地文件
            boolean result4 = clearBrowserCache();//清除本地文件

            AtworkApplicationLike.runOnMainThread(() -> {
                progressDialogHelper.dismiss();

                if(result1 && result2 && result3 && result4) {
                    mTvComputingHistoryRecord.setText(R.string.data_is_empty);
                    mTvComputingLocalFile.setText(R.string.data_is_empty);

                    finishHistoryClear = false;
                    finishLocalClear = false;
                    finishBrowerClear = false;
                    mTvClearHistory.setBackgroundResource(R.drawable.shape_setting_gray);
                    mTvClearLocalFile.setBackgroundResource(R.drawable.shape_setting_gray);
                    mTvClearBrowserCache.setBackgroundResource(R.drawable.shape_setting_gray);

                    toastOver(R.string.clean_messages_data_successfully);
                } else {
                    toastOver(R.string.clean_messages_data_unsuccessfully);

                }
            });
        });
    }

    private void initData() {
        showHistoryFile();
        showLocalFile();
    }

    @Override
    protected boolean onBackPressed() {

        if (!mForbiddenHandle) {
            finish();
        }
        return false;
    }
}
