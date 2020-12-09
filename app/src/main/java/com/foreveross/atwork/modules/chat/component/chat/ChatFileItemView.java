package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.file.util.FileMediaTypeUtil;
import com.foreveross.atwork.utils.ChatMessageHelper;


public class ChatFileItemView extends RelativeLayout {

    public RelativeLayout rlContentRoot;

    //文件缩缩略图
    public ImageView ivFileThumbnails;

    //文件名称
    private TextView tvFileName;

    //文件大小
    private TextView tvFileSize;

    //文件传输状态
    private TextView tvFileStatus;

    //上传进度条
    private ProgressBar progressBar;

    private TextView tvProgress;

    private RelativeLayout rlProgressBarArea;

    private LinearLayout mLlSomeStatusInfoWrapperParent;
    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;
    private ImageView mIvSomeStatus;

    private Context mContext;


    private String mNormaTvNameColor = "#000000";
    private String mIllegalTvFileNameColor = "#80000000";

    public ChatFileItemView(Context context) {
        super(context);
        mContext = context;
        initView();

    }

    public ChatFileItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.component_file_transfer, this);

        rlContentRoot = view.findViewById(R.id.rl_content_root);
        ivFileThumbnails = view.findViewById(R.id.chat_file_img);
        tvFileName = view.findViewById(R.id.chat_file_filename);
        tvFileSize = view.findViewById(R.id.chat_file_size);
        tvFileStatus = view.findViewById(R.id.chat_file_status);
        tvProgress = view.findViewById(R.id.chat_file_progress_num);
        progressBar = view.findViewById(R.id.chat_file_progress);
        rlProgressBarArea = view.findViewById(R.id.rl_file_progress_info);
        progressBar.setMax(100);

        mLlSomeStatusInfoWrapperParent = view.findViewById(R.id.ll_some_status_info_wrapper_parent);
        mLlSomeStatusInfo = view.findViewById(R.id.ll_some_status_info);
        mTvTime = view.findViewById(R.id.tv_time);
        mIvSomeStatus = view.findViewById(R.id.iv_some_status);

        mTvTime.setTextColor(ContextCompat.getColor(getContext(), R.color.common_text_color_999));
//        progressBar.setVisibility(GONE);
    }


    public LinearLayout getLlSomeStatusInfoWrapperParent() {
        return mLlSomeStatusInfoWrapperParent;
    }

    public LinearLayout getLlSomeStatusInfo() {
        return mLlSomeStatusInfo;
    }

    public TextView getTvTime() {
        return mTvTime;
    }

    public ImageView getIvSomeStatus() {
        return mIvSomeStatus;
    }

    public void refreshCanChangeFileItem(final FileTransferChatMessage fileTransferChatMessage) {
        tvFileStatus.setText(getStatus(fileTransferChatMessage));
        refreshProgress(fileTransferChatMessage);

    }

    public void refreshFileItem(final FileTransferChatMessage fileTransferChatMessage) {

        tvFileName.setText(fileTransferChatMessage.name);
        tvFileSize.setText(ChatMessageHelper.getMBOrKBString(fileTransferChatMessage.size));
        refreshAvatar(fileTransferChatMessage);
        tvFileStatus.setText(getStatus(fileTransferChatMessage));
        refreshProgress(fileTransferChatMessage);

        if(ChatMessageHelper.isOverdue(fileTransferChatMessage)) {
            tvFileName.setTextColor(Color.parseColor(mIllegalTvFileNameColor));
            ivFileThumbnails.setAlpha(0.5f);
        } else {
            tvFileName.setTextColor(Color.parseColor(mNormaTvNameColor));
            ivFileThumbnails.setAlpha(1f);

        }
    }

    /**
     * 更新缩回图信息
     *
     * @param fileTransferChatMessage
     */
    private void refreshAvatar(final FileTransferChatMessage fileTransferChatMessage) {
        ivFileThumbnails.setImageResource(FileMediaTypeUtil.getFileTypeIcon(fileTransferChatMessage));
    }






    /**
     * 获取文件状态
     *
     * @param fileTransferChatMessage
     * @return
     */
    private String getStatus(FileTransferChatMessage fileTransferChatMessage) {


        if (ChatMessageHelper.isOverdue(fileTransferChatMessage)) {
            return getResources().getString(R.string.overdued);
        }

        FileStatus fileStatus = fileTransferChatMessage.fileStatus;

        if (ChatStatus.Not_Send.equals(fileTransferChatMessage.chatStatus) && FileStatus.SENDING.equals(fileTransferChatMessage.fileStatus)) {
            fileTransferChatMessage.fileStatus = FileStatus.SEND_FAIL;
            return getResources().getString(R.string.file_transfer_status_send_fail);
        }
        if (FileStatus.NOT_SENT.equals(fileStatus)) {
//            return getResources().getString(R.string.file_transfer_status_not_send);
            //暂时需求为未发送也显示"发送失败"
            return getResources().getString(R.string.file_transfer_status_send_fail);
        }

        if (FileStatus.SENDING.equals(fileStatus)) {
            return getResources().getString(R.string.file_transfer_status_sending);
        }

        //文件已经发送成功后, 此时拥有 media, 根据IM 状态来判断显示文字
        if (FileStatus.SENDED.equals(fileStatus)) {

            if(ChatStatus.Sended.equals(fileTransferChatMessage.chatStatus)){
                return getResources().getString(R.string.file_transfer_status_sended);
            } else if(ChatStatus.Sending.equals(fileTransferChatMessage.chatStatus)){
                return getResources().getString(R.string.file_transfer_status_sending);
            }else{
                return getResources().getString(R.string.file_transfer_status_send_fail);
            }
        }

        if (FileStatus.SEND_FAIL.equals(fileStatus)) {
            return getResources().getString(R.string.file_transfer_status_send_fail);
        }

        if (FileStatus.SEND_CANCEL.equals(fileStatus)) {
            return getResources().getString(R.string.file_transfer_status_send_cancel);
        }

        if (FileStatus.NOT_DOWNLOAD.equals(fileStatus)) {
            return getResources().getString(R.string.file_transfer_status_not_download);
        }

        if (FileStatus.DOWNLOADING.equals(fileStatus)) {
            return getResources().getString(R.string.file_transfer_status_downloading);
        }

        if (FileStatus.DOWNLOAD_FAIL.equals(fileStatus)) {
//            return getResources().getString(R.string.file_transfer_status_download_fail);
            //暂时需求为下载失败也显示"未下载"
            String from = fileTransferChatMessage.from;
            if (!TextUtils.isEmpty(from) && from.equalsIgnoreCase(LoginUserInfo.getInstance().getLoginUserId(BaseApplicationLike.baseContext))) {
                return getResources().getString(R.string.file_transfer_status_sended);
            }
            return getResources().getString(R.string.file_transfer_status_not_download);
        }

        if (FileStatus.DOWNLOAD_CANCEL.equals(fileStatus)) {
            return getResources().getString(R.string.file_transfer_status_download_cancel);
        }

        if (FileStatus.DOWNLOADED.equals(fileStatus)) {
            //针对如果发送方是自己，应该是返回已发送状态
            String from = fileTransferChatMessage.from;
            if (!TextUtils.isEmpty(from) && from.equalsIgnoreCase(LoginUserInfo.getInstance().getLoginUserId(BaseApplicationLike.baseContext))) {
                return getResources().getString(R.string.file_transfer_status_sended);
            }
            return getResources().getString(R.string.file_transfer_status_downloaded);
        }

        return StringUtils.EMPTY;
    }

    //刷新PROGRESS
    public void refreshProgress(FileTransferChatMessage fileTransferChatMessage) {
        if ((fileTransferChatMessage.fileStatus.equals(FileStatus.SENDING) && fileTransferChatMessage.chatStatus.equals(ChatStatus.Sending)) || fileTransferChatMessage.fileStatus.equals(FileStatus.DOWNLOADING)) {
            progressBar.setVisibility(VISIBLE);
            tvProgress.setVisibility(VISIBLE);
            progressBar.setProgress(fileTransferChatMessage.progress);
            tvProgress.setText(fileTransferChatMessage.progress + "%");

            rlProgressBarArea.setVisibility(VISIBLE);
        } else {
            progressBar.setVisibility(GONE);
            tvProgress.setVisibility(GONE);
            rlProgressBarArea.setVisibility(GONE);
        }
    }

    public void allTvTextWhite() {
        int whiteColor = ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.white);
        tvFileSize.setTextColor(whiteColor);
        tvFileStatus.setTextColor(whiteColor);
        tvProgress.setTextColor(whiteColor);
    }

    public void setNormaTvColor(String normaTvColor) {
        this.mNormaTvNameColor = normaTvColor;
    }

    public void setIllegalTvColor(String illegalTvColor) {
        this.mIllegalTvFileNameColor = illegalTvColor;
    }

    public TextView getTvFileStatus() {
        return tvFileStatus;
    }
}
