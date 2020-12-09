package com.foreveross.atwork.modules.dropbox.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.dropbox.DropboxConfig;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.DropboxManager;
import com.foreveross.atwork.manager.model.SetReadableNameParams;
import com.foreveross.atwork.modules.dropbox.activity.DropboxBaseActivity;
import com.foreveross.atwork.modules.file.util.FileMediaTypeUtil;
import com.foreveross.atwork.utils.ContactShowNameHelper;
import com.foreveross.atwork.utils.TimeViewUtil;

import java.util.Calendar;
import java.util.Set;

import static com.foreveross.atwork.infrastructure.utils.TimeUtil.getCurrentTimeInMillis;

/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 * Created by reyzhang22 on 16/9/8.
 */
public class DropboxFileItem extends RelativeLayout {

    private ImageView mIcon;

    private TextView mTitle;

    private TextView mSize;

    private TextView mModifyDate;

    private TextView mTvFrom;

    public TextView mCancelUpload;

    private ProgressBar mProgressBar;

    private RadioButton mSelectBtn;

    private OnItemIconClickListener mOnIconSelectedListener;

    private ImageView mFileExpandIcon;

    private View mFromView;

    private View mUploadFailLayout;
    private ImageView mReUploadBtn;
    private ImageView mCancelUploadBtn;
    private TextView mTransBreak;
    private TextView mExpiredTime;

    private Context mContext;


    public DropboxFileItem(Context context) {
        super(context);
        mContext = context;
        initViews(context);
    }


    public void setOnItemSelectedListener(OnItemIconClickListener listener) {
        mOnIconSelectedListener = listener;
    }

    private void initViews(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_dropbox_file, this);
        mIcon = (ImageView)view.findViewById(R.id.file_icon);
        mTitle = (TextView)view.findViewById(R.id.file_name);
        mSize = (TextView)view.findViewById(R.id.file_size);
        mModifyDate = (TextView)view.findViewById(R.id.file_modify_time);
        mFromView = view.findViewById(R.id.from_view);
        mTvFrom = (TextView)view.findViewById(R.id.file_from);
        mCancelUpload = (TextView)view.findViewById(R.id.cancel_upload);
        mProgressBar = (ProgressBar)view.findViewById(R.id.file_upload_progress);
        mSelectBtn = (RadioButton)view.findViewById(R.id.file_select);
        mFileExpandIcon = (ImageView)view.findViewById(R.id.file_expand_icon);
        mUploadFailLayout = view.findViewById(R.id.upload_fail_layout);
        mCancelUploadBtn = (ImageView)mUploadFailLayout.findViewById(R.id.cancel_upload_btn);
        mReUploadBtn = (ImageView)mUploadFailLayout.findViewById(R.id.reupload);
        mTransBreak = (TextView)view.findViewById(R.id.trans_break);
        mExpiredTime = view.findViewById(R.id.expired_time);
    }

    public void setDropbox(Dropbox dropbox, DropboxBaseActivity.DisplayMode displayMode, Set<String> list, boolean moveOrCopy, DropboxConfig dropboxConfig) {
        mIcon.setImageResource(dropbox.mIsDir ? R.mipmap.icon_dropbox_item_dir : FileMediaTypeUtil.getFileTypeByExtension(dropbox.mExtension));
        StringBuilder name = new StringBuilder(dropbox.mFileName);
        mTitle.setText(name.toString());
        mSize.setText(FileUtil.formatFromSize(dropbox.mFileSize));
        mModifyDate.setText(TimeViewUtil.getSimpleUserCanViewTime(BaseApplicationLike.baseContext , dropbox.mLastModifyTime));

        SetReadableNameParams setReadableNameParams = SetReadableNameParams.newSetReadableNameParams()
                .setTextView(mTvFrom)
                .setDiscussionId(Dropbox.SourceType.Discussion == dropbox.mSourceType ? dropbox.mSourceId : StringUtils.EMPTY)
                .setUserId(dropbox.mOwnerId)
                .setDomainId(dropbox.mDomainId)
                .setFailName(dropbox.mOwnerName);

        ContactShowNameHelper.setReadableNames(setReadableNameParams);


        if (Dropbox.SourceType.User == dropbox.mSourceType) {
            mFromView.setVisibility(GONE);
        }

        mCancelUpload.setOnClickListener(view1 -> {
            if (mOnIconSelectedListener != null) {
                mOnIconSelectedListener.onCancelClick(dropbox);
            }
        });
        mReUploadBtn.setOnClickListener(view1 -> {
            if (mOnIconSelectedListener != null) {
                mOnIconSelectedListener.onRetryClick(dropbox);
            }
        });
        mCancelUploadBtn.setOnClickListener(view1 -> {
            if (mOnIconSelectedListener != null) {
                mOnIconSelectedListener.onCancelClick(dropbox);
            }
        });

        mFileExpandIcon.setOnClickListener(view2 -> {
            if (mOnIconSelectedListener != null) {
                mOnIconSelectedListener.onExpandIconClick(dropbox);
            }
        });

        refreshProgress(dropbox);
        setSelectMode(displayMode, dropbox, list);
        updateExpandIcon(!moveOrCopy && DropboxManager.hasOpsAuth(dropboxConfig) &&
                !(Dropbox.UploadStatus.Fail.equals(dropbox.mUploadStatus) || Dropbox.UploadStatus.Uploading.equals(dropbox.mUploadStatus)) &&
                !(DropboxBaseActivity.DisplayMode.Send.equals(displayMode) || DropboxBaseActivity.DisplayMode.Select.equals(displayMode)));

        showExpiredTime(dropbox.mExpiredTime);
    }

    public void updateExpandIcon(boolean display) {
        mFileExpandIcon.setVisibility(display? VISIBLE : INVISIBLE);
    }

    public void refreshProgress(Dropbox dropbox) {
        if (Dropbox.UploadStatus.Uploading.equals(dropbox.mUploadStatus)) {
            mProgressBar.setVisibility(VISIBLE);
            mCancelUpload.setVisibility(VISIBLE);
            mModifyDate.setVisibility(GONE);
            mSize.setVisibility(GONE);
            int progress = (int)(new Long(dropbox.mUploadBreakPoint).intValue()/(float)dropbox.mFileSize * 100);
            mProgressBar.setProgress(progress);
            mUploadFailLayout.setVisibility(GONE);
            mTransBreak.setVisibility(GONE);
        }  else if (Dropbox.UploadStatus.Fail.equals(dropbox.mUploadStatus)) {
            mProgressBar.setVisibility(GONE);
            mCancelUpload.setVisibility(GONE);
            mModifyDate.setVisibility(GONE);
            mSize.setVisibility(GONE);
            mTransBreak.setVisibility(VISIBLE);
            mUploadFailLayout.setVisibility(VISIBLE);
        } else {
            mProgressBar.setVisibility(GONE);
            mCancelUpload.setVisibility(GONE);
            mModifyDate.setVisibility(VISIBLE);
            mSize.setVisibility(dropbox.mIsDir ? GONE : VISIBLE);
            mUploadFailLayout.setVisibility(GONE);
            mTransBreak.setVisibility(GONE);
        }
    }

    private void setSelectMode(DropboxBaseActivity.DisplayMode displayMode, Dropbox dropbox, Set<String> selectedSet) {
        mSelectBtn.setVisibility(displayMode.equals(DropboxBaseActivity.DisplayMode.Select) || (!dropbox.mIsDir && displayMode.equals(DropboxBaseActivity.DisplayMode.Send))  ? VISIBLE : GONE);

        if (!(displayMode.equals(DropboxBaseActivity.DisplayMode.Select) || displayMode.equals(DropboxBaseActivity.DisplayMode.Send)) || selectedSet == null) {
            return;
        }
        if (selectedSet.contains(dropbox.mFileId)) {
            mSelectBtn.setBackgroundResource(R.mipmap.icon_dropbox_file_selected);
            return;
        }
        mSelectBtn.setBackgroundResource(R.mipmap.icon_dropbox_file_unselected);
        mSelectBtn.setOnClickListener(view -> {
            if (mOnIconSelectedListener != null) {
                mOnIconSelectedListener.onSelectIconClick(dropbox);
            }
        });
    }

    public void handleLineVisibility(boolean show) {
    }

    private void showExpiredTime(long expiredTime) {
        if (expiredTime <= 0 ||expiredTime >= 9999999999999L) {
            mExpiredTime.setVisibility(View.GONE);
            return;
        }
        mExpiredTime.setVisibility(View.VISIBLE);
        mExpiredTime.setText(getDeadlineText(expiredTime));
    }

    private String getDeadlineText(long expiredTime) {
        long deadlineStamp = expiredTime - getCurrentTimeInMillis() ;
        if (deadlineStamp <= 0) {
            return mContext.getString(R.string.file_expires_tips);
        }
        Calendar currentCal = Calendar.getInstance();
        currentCal.setTimeInMillis(getCurrentTimeInMillis());
        Calendar expiredCal = Calendar.getInstance();
        expiredCal.setTimeInMillis(expiredTime);
        int expiredDays = expiredCal.get(Calendar.DAY_OF_YEAR) - currentCal.get(Calendar.DAY_OF_YEAR) - 1;
        if (expiredDays > 0) {
            return String.format(mContext.getString(R.string.expired_afer_days), expiredDays + "");
        }
        if (isSameDay(currentCal, expiredCal)) {
            String minute = expiredCal.get(Calendar.MINUTE) +"";
            if (minute.length() == 1) {
                minute = "0"+ minute;
            }
            return String.format(mContext.getString(R.string.expired_in_days), expiredCal.get(Calendar.HOUR_OF_DAY) + ":" + minute);
        }
        int expiredHours = 24 + expiredCal.get(Calendar.HOUR_OF_DAY) - currentCal.get(Calendar.HOUR_OF_DAY) - 1;
        return String.format(mContext.getString(R.string.expired_afer_hours), String.valueOf(expiredHours));
    }

    private boolean isSameDay(Calendar currentCal, Calendar expiredCal) {
        return currentCal.get(Calendar.YEAR) == expiredCal.get(Calendar.YEAR) && currentCal.get(Calendar.MONTH) == expiredCal.get(Calendar.MONTH)
                && currentCal.get(Calendar.DAY_OF_YEAR) == expiredCal.get(Calendar.DAY_OF_YEAR);
    }

    public interface OnItemIconClickListener {
        void onSelectIconClick(Dropbox dropbox);
        void onExpandIconClick(Dropbox dropbox);
        void onCancelClick(Dropbox dropbox);
        void onRetryClick(Dropbox dropbox);
    }
}
