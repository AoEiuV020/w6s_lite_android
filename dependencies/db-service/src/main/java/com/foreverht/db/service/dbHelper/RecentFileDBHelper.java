package com.foreverht.db.service.dbHelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.file.FileData;

import com.foreveross.db.SQLiteDatabase;

/**
 * 最近文件传输表：记录最近的文件传输记录
 * Created by ReyZhang on 2015/5/12.
 */
public class RecentFileDBHelper implements DBHelper {

    private static final String TAG = RecentFileDBHelper.class.getSimpleName();

    public static final String TABLE_NAME = "recent_files_";

    public class DBColumn {

        /**
         * id 非自增，使用文件的mediaId；
         */
        public static final String ID = "identifier_";

        /**
         * 文件名
         */
        public static final String FILE_NAME = "file_name_";

        /**
         * 文件类型
         */
        public static final String FILE_TYPE = "file_type_";

        /**
         * 文件路径
         */
        public static final String FILE_PATH = "file_path_";

        /**
         * 缩略图路径
         */
        public static final String THUMBNAIL_PATH = "thumbnail_path_";

        /**
         * 来自
         */
        public static final String FROM = "from_";

        /**
         * 发送至
         */
        public static final String TO = "to_";

        /**
         * 最近发送/接收时间
         */
        public static final String RECENT_TIME = "recent_time_";

        /**
         * 大小
         */
        public static final String SIZE = "size_";

        /**
         * 是否是下载的
         */
        public static final String DOWNLOAD = "download_";

    }

    public static final String CREATE_RECENT_FILE_TABLE = "create table " + TABLE_NAME +" (" +
            DBColumn.ID + " text primary key," +
            DBColumn.FILE_NAME + " text, " +
            DBColumn.FILE_TYPE + " integer, " +
            DBColumn.FILE_PATH + " text, " +
            DBColumn.THUMBNAIL_PATH + " text, " +
            DBColumn.FROM + " text, " +
            DBColumn.TO + " text, " +
            DBColumn.RECENT_TIME + " integer, " +
            DBColumn.DOWNLOAD + " integer, " +
            DBColumn.SIZE + " integer)";


    public static ContentValues getContentValues(FileData fileData) {
        ContentValues cv = new ContentValues();
        cv.put(DBColumn.ID, fileData.mediaId);
        cv.put(DBColumn.FILE_NAME, fileData.title);
        cv.put(DBColumn.FILE_PATH, fileData.filePath);
        cv.put(DBColumn.FILE_TYPE, FileData.getFileType2DB(fileData.fileType));
        cv.put(DBColumn.THUMBNAIL_PATH, fileData.thumbnailPath);
        cv.put(DBColumn.FROM, fileData.from);
        cv.put(DBColumn.TO, fileData.to);
        cv.put(DBColumn.RECENT_TIME, fileData.date);
        cv.put(DBColumn.SIZE, fileData.size);
        cv.put(DBColumn.DOWNLOAD, fileData.isDownload);
        return cv;
    }

    public static FileData getFileDataFromCursor(Cursor cursor) {
        FileData fileData = new FileData();
        int index = -1;
        if ((index =cursor.getColumnIndex(DBColumn.FILE_NAME)) != -1) {
            fileData.title = cursor.getString(index);
        }
        if ((index =cursor.getColumnIndex(DBColumn.FILE_PATH)) != -1) {
            fileData.filePath = cursor.getString(index);
        }
        if ((index =cursor.getColumnIndex(DBColumn.FILE_TYPE)) != -1) {
            fileData.fileType = FileData.getFileTypeFromDb(cursor.getInt(index));
        }
        if ((index =cursor.getColumnIndex(DBColumn.FROM)) != -1) {
            fileData.from = cursor.getString(index);
        }
        if ((index =cursor.getColumnIndex(DBColumn.TO)) != -1) {
            fileData.to = cursor.getString(index);
        }
        if ((index =cursor.getColumnIndex(DBColumn.THUMBNAIL_PATH)) != -1) {
            fileData.thumbnailPath = cursor.getString(index);
        }
        if ((index =cursor.getColumnIndex(DBColumn.RECENT_TIME)) != -1) {
            fileData.date = cursor.getLong(index);
        }
        if ((index =cursor.getColumnIndex(DBColumn.SIZE)) != -1) {
            fileData.size = cursor.getInt(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.DOWNLOAD)) != -1) {
            fileData.isDownload = cursor.getInt(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.ID)) != -1) {
            fileData.mediaId = cursor.getString(index);
        }

        return fileData;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RECENT_FILE_TABLE);
        if (BaseApplicationLike.sIsDebug) {
            Log.i(TAG, CREATE_RECENT_FILE_TABLE);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
