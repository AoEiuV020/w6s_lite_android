package com.foreverht.db.service.dbHelper;

import android.content.ContentValues;
import android.database.Cursor;

import com.foreverht.db.service.W6sDatabaseHelper;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;

import com.foreveross.db.SQLiteDatabase;

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
 * Created by reyzhang22 on 16/9/7.
 */
public class DropboxDBHelper implements DBHelper  {

    private static final String TAG = DropboxDBHelper.class.getSimpleName();

    public static final String TABLE_NAME = "cloud_disk_file_";

    private static final String SQL_EXEC = CREATE_TABLE + TABLE_NAME + LEFT_BRACKET +
            DBColumn.FILE_ID + TEXT + PRIMARY_KEY + COMMA +
            DBColumn.SOURCE_ID + TEXT + COMMA +
            DBColumn.SOURCE_TYPE + INTEGER + COMMA +
            DBColumn.DOMAIN_ID + INTEGER + COMMA +
            DBColumn.MEDIA_ID + TEXT + COMMA +
            DBColumn.THUMBNAIL_MEDIA_ID + TEXT + COMMA +
            DBColumn.FILE_TYPE + INTEGER + COMMA +
            DBColumn.IS_DIR + TEXT + COMMA +
            DBColumn.ROOT_ID + TEXT + COMMA +
            DBColumn.PARENT_ID + TEXT + COMMA +
            DBColumn.CREATE_TIME + INTEGER + COMMA +
            DBColumn.LAST_MODIFY_TIME + INTEGER + COMMA +
            DBColumn.EXPIRED_TIME + INTEGER + COMMA +
            DBColumn.LOCAL_PATH + TEXT + COMMA +
            DBColumn.FILE_NAME + TEXT + COMMA +
            DBColumn.FILE_SIZE + INTEGER + COMMA +
            DBColumn.OWNER_ID + TEXT + COMMA +
            DBColumn.OWNER_NAME + TEXT + COMMA +
            DBColumn.DOWNLOAD_STATUS + INTEGER + COMMA +
            DBColumn.UPLOAD_STATUS + INTEGER + COMMA +
            DBColumn.DOWNLOAD_BREAK_POINT + INTEGER + COMMA +
            DBColumn.UPLOAD_BREAK_POINT + INTEGER + COMMA +
            DBColumn.EXTENSION + TEXT + COMMA +
            DBColumn.PINYIN + TEXT + COMMA +
            DBColumn.INITIAL + TEXT + COMMA +
            DBColumn.STATE + INTEGER + COMMA +
            DBColumn.OPERATOR_ID + TEXT + COMMA +
            DBColumn.OPERATOR_NAME + TEXT + COMMA +
            DBColumn.IS_OVERDUE_REPORT + TEXT + COMMA +
            DBColumn.EXTENSION1 + TEXT + COMMA +
            DBColumn.EXTENSION2 + TEXT +
            RIGHT_BRACKET;


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_EXEC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 7) {
            db.execSQL(SQL_EXEC);
            oldVersion = 7;
        }

        if (oldVersion < 120) {
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.IS_OVERDUE_REPORT, "text");
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.EXTENSION1, "text");
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.EXTENSION2, "text");
            oldVersion = 120;
        }
    }

    public static ContentValues getContentValues(Dropbox dropbox) {
        ContentValues cv = new ContentValues();
        cv.put(DBColumn.FILE_ID, dropbox.mFileId);
        cv.put(DBColumn.SOURCE_ID, dropbox.mSourceId);
        cv.put(DBColumn.SOURCE_TYPE, dropbox.mSourceType.valueOfInt());
        cv.put(DBColumn.MEDIA_ID, dropbox.mMediaId);
        cv.put(DBColumn.THUMBNAIL_MEDIA_ID, dropbox.mThumbnailMediaId);
        cv.put(DBColumn.FILE_TYPE, dropbox.mFileType.valueOfInt());
        cv.put(DBColumn.IS_DIR, dropbox.mIsDir);
        cv.put(DBColumn.ROOT_ID, dropbox.mRootId);
        cv.put(DBColumn.PARENT_ID, dropbox.mParentId);
        cv.put(DBColumn.CREATE_TIME, dropbox.mCreateTime);
        cv.put(DBColumn.LAST_MODIFY_TIME, dropbox.mLastModifyTime);
        cv.put(DBColumn.EXPIRED_TIME, dropbox.mExpiredTime);
        cv.put(DBColumn.LOCAL_PATH, dropbox.mLocalPath);
        cv.put(DBColumn.FILE_NAME, dropbox.mFileName);
        cv.put(DBColumn.FILE_SIZE, dropbox.mFileSize);
        cv.put(DBColumn.OWNER_ID, dropbox.mOwnerId);
        cv.put(DBColumn.OWNER_NAME, dropbox.mOwnerName);
        cv.put(DBColumn.DOWNLOAD_STATUS, dropbox.mDownloadStatus.valueOfInt());
        cv.put(DBColumn.UPLOAD_STATUS, dropbox.mUploadStatus.valueOfInt());
        cv.put(DBColumn.DOWNLOAD_BREAK_POINT, dropbox.mDownloadBreakPoint);
        cv.put(DBColumn.UPLOAD_BREAK_POINT, dropbox.mUploadBreakPoint);
        cv.put(DBColumn.EXTENSION, dropbox.mExtension);
        cv.put(DBColumn.DOMAIN_ID, dropbox.mDomainId);
        cv.put(DBColumn.PINYIN, dropbox.mPinyin);
        cv.put(DBColumn.INITIAL, dropbox.mInitial);
        cv.put(DBColumn.STATE, dropbox.mState.valueOfInt());
        cv.put(DBColumn.OPERATOR_ID, dropbox.mOperatorId);
        cv.put(DBColumn.OPERATOR_NAME, dropbox.mOperatorName);
        cv.put(DBColumn.IS_OVERDUE_REPORT, dropbox.mIsOverdueReport);
        return cv;
    }

    public static Dropbox fromCursor(Cursor cursor) {
        int index = -1;
        Dropbox dropbox = new Dropbox();
        if ((index = cursor.getColumnIndex(DBColumn.FILE_ID)) != -1) {
            dropbox.mFileId = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.SOURCE_ID)) != -1) {
            dropbox.mSourceId = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.SOURCE_TYPE)) != -1) {
            dropbox.mSourceType = Dropbox.SourceType.valueOf(cursor.getInt(index));
        }
        if ((index = cursor.getColumnIndex(DBColumn.DOMAIN_ID)) != -1) {
            dropbox.mDomainId = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.MEDIA_ID)) != -1) {
            dropbox.mMediaId = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.THUMBNAIL_MEDIA_ID)) != -1) {
            dropbox.mThumbnailMediaId = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.FILE_TYPE)) != -1) {
            dropbox.mFileType = Dropbox.DropboxFileType.valueOf(cursor.getInt(index));
        }
        if ((index = cursor.getColumnIndex(DBColumn.IS_DIR)) != -1) {
            dropbox.mIsDir = "1".equalsIgnoreCase(cursor.getString(index));

        }
        if ((index = cursor.getColumnIndex(DBColumn.ROOT_ID)) != -1) {
            dropbox.mRootId = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.PARENT_ID)) != -1) {
            dropbox.mParentId = cursor.getString(index) == null ? "" : cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.CREATE_TIME)) != -1) {
            dropbox.mCreateTime = cursor.getLong(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.LAST_MODIFY_TIME)) != -1) {
            dropbox.mLastModifyTime = cursor.getLong(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.EXPIRED_TIME)) != -1) {
            dropbox.mExpiredTime = cursor.getLong(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.LOCAL_PATH)) != -1) {
            dropbox.mLocalPath = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.FILE_NAME)) != -1) {
            dropbox.mFileName = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.FILE_SIZE)) != -1) {
            dropbox.mFileSize = cursor.getLong(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.OWNER_ID)) != -1) {
            dropbox.mOwnerId = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.OWNER_NAME)) != -1) {
            dropbox.mOwnerName = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.DOWNLOAD_STATUS)) != -1) {
            dropbox.mDownloadStatus = Dropbox.DownloadStatus.valueOf(cursor.getInt(index));
        }
        if ((index = cursor.getColumnIndex(DBColumn.UPLOAD_STATUS)) != -1) {
            dropbox.mUploadStatus = Dropbox.UploadStatus.valueOf(cursor.getInt(index));
        }
        if ((index = cursor.getColumnIndex(DBColumn.DOWNLOAD_BREAK_POINT)) != -1) {
            dropbox.mDownloadBreakPoint = cursor.getLong(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.UPLOAD_BREAK_POINT)) != -1) {
            dropbox.mUploadBreakPoint = cursor.getLong(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.EXTENSION)) != -1) {
            dropbox.mExtension = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.PINYIN)) != -1) {
            dropbox.mPinyin = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.INITIAL)) != -1) {
            dropbox.mInitial = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.STATE)) != -1) {
            dropbox.mState = Dropbox.State.valueOf(cursor.getInt(index));
        }
        if ((index = cursor.getColumnIndex(DBColumn.OPERATOR_ID)) != -1) {
            dropbox.mOperatorId = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.OPERATOR_NAME)) != -1) {
            dropbox.mOperatorName = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.IS_OVERDUE_REPORT)) != -1) {
            dropbox.mIsOverdueReport = "1".equalsIgnoreCase(cursor.getString(index));
        }
        return dropbox;
    }

    public static class DBColumn {

        public static final String FILE_ID = "file_id_";

        public static final String SOURCE_ID = "source_id_";

        public static final String SOURCE_TYPE = "source_type_";

        public static final String MEDIA_ID = "media_id_";

        public static final String THUMBNAIL_MEDIA_ID = "thumbnail_media_id_";

        public static final String DOMAIN_ID = "domain_id_";

        public static final String FILE_TYPE = "file_type_";

        public static final String IS_DIR = "is_dir_";

        public static final String ROOT_ID = "root_id_";

        public static final String PARENT_ID = "parent_id_";

        public static final String CREATE_TIME = "create_time_";

        public static final String LAST_MODIFY_TIME = "last_modify_time_";

        public static final String EXPIRED_TIME = "expired_time_";

        public static final String LOCAL_PATH = "local_path_";

        public static final String FILE_NAME = "file_name_";

        public static final String FILE_SIZE = "file_size_";

        public static final String OWNER_ID = "owner_id_";

        public static final String OWNER_NAME = "owner_name_";

        public static final String DOWNLOAD_STATUS = "download_status_";

        public static final String UPLOAD_STATUS = "upload_status_";

        public static final String DOWNLOAD_BREAK_POINT = "download_break_point_";

        public static final String UPLOAD_BREAK_POINT = "upload_break_point_";

        public static final String EXTENSION = "extension_";

        public static final String PINYIN = "pinyin_";

        public static final String INITIAL = "initial_";

        public static final String STATE = "state_";

        public static final String OPERATOR_ID = "operator_id_";

        public static final String OPERATOR_NAME = "operator_name_";

        public static final String IS_OVERDUE_REPORT = "is_overdue_report_";

        public static final String EXTENSION1 = "extension1_";

        public static final String EXTENSION2 = "extension2_";

    }
}
