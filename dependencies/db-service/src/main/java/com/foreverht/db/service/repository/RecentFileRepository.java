package com.foreverht.db.service.repository;

import android.database.Cursor;

import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.dbHelper.RecentFileDBHelper;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.utils.LogUtil;

import com.foreveross.db.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * 最近文件reponsitory
 * Created by ReyZhang on 2015/5/12.
 */
public class RecentFileRepository extends W6sBaseRepository {

    public static final String TAG = RecentFileRepository.class.getSimpleName();

    private static RecentFileRepository sRecentFileRepository = new RecentFileRepository();

    /**
     * 查询最近文件表
     */
    private static final String SELECT_RECENT_FILE_SQL = "select * from " + RecentFileDBHelper.TABLE_NAME + " group by "+ RecentFileDBHelper.DBColumn.ID + " order by " + RecentFileDBHelper.DBColumn.RECENT_TIME + " desc";

    private static final String SELECT_RECENT_FILE_BY_TYPE_SQL = "select * from " + RecentFileDBHelper.TABLE_NAME + " where " + RecentFileDBHelper.DBColumn.FILE_TYPE + "='0' order by " + RecentFileDBHelper.DBColumn.RECENT_TIME + " desc";

    private static final String SELECT_RECENT_DOWNLOAD_FILE_SQL = "select * from " + RecentFileDBHelper.TABLE_NAME + " where " + RecentFileDBHelper.DBColumn.DOWNLOAD + "='1' order by " + RecentFileDBHelper.DBColumn.RECENT_TIME + " desc";
    public static RecentFileRepository getInstance() {
        synchronized (RecentFileRepository.class) {
            if (sRecentFileRepository == null) {
                sRecentFileRepository = new RecentFileRepository();
            }
            return sRecentFileRepository;
        }
    }

    /**
     * 查询最近文件列表
     * @return
     */
    public List<FileData> queryFileDataList() {
        List<FileData> list = new ArrayList<FileData>();
        Cursor cursor = null;
        LogUtil.i(TAG, "start search sql :" + SELECT_RECENT_FILE_SQL);

        cursor = getReadableDatabase().rawQuery(SELECT_RECENT_FILE_SQL,new String[]{});
        if (cursor == null) {
            return list;
        }
        while (cursor.moveToNext()) {
            FileData fileData = RecentFileDBHelper.getFileDataFromCursor(cursor);
            if (!list.contains(fileData)) {
                list.add(fileData);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    /**
     * 删除文件
     * @param fileId
     * @return
     */
    public int deleteDownloadFileByFileId(String fileId){
        return getWritableDatabase().delete(RecentFileDBHelper.TABLE_NAME, RecentFileDBHelper.DBColumn.ID + "=?", new String[]{fileId});
    }

    /**
     * 查询最近文件某一类型（后缀名）的列表
     * @return
     */
    public List<FileData> queryFileDataListByType(String fileType) {
        List<FileData> list = new ArrayList<FileData>();
        Cursor cursor = null;
        LogUtil.i(TAG, "start search sql :" + SELECT_RECENT_FILE_BY_TYPE_SQL);

        cursor = getReadableDatabase().rawQuery(SELECT_RECENT_FILE_BY_TYPE_SQL,new String[]{});
        if (cursor == null) {
            return list;
        }
        while (cursor.moveToNext()) {
            FileData fileData = RecentFileDBHelper.getFileDataFromCursor(cursor);
            if (!list.contains(fileData)) {
                list.add(fileData);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    /**
     * 文件名称搜索
     *
     * @param searchValue
     * @return
     */
    public List<FileData> searchFileData(final String searchValue) {
        String sql = "select * from " + RecentFileDBHelper.TABLE_NAME + " where " + RecentFileDBHelper.DBColumn.FILE_NAME + " like ?";
        Cursor cursor = null;
        List<FileData> fileDataList = new ArrayList<FileData>();
        try {
            cursor = getReadableDatabase().rawQuery(sql, new String[]{"%" + searchValue + "%"});
            if (cursor == null) {
                return fileDataList;
            }
            while (cursor.moveToNext()) {
                FileData fileData = RecentFileDBHelper.getFileDataFromCursor(cursor);
                if (!fileDataList.contains(fileData)) {
                    fileDataList.add(fileData);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return fileDataList;
    }

    /**
     * 插入一条数据
     * @param fileData
     * @return
     */
    public boolean insertFileData(FileData fileData) {
        fileData.date = System.currentTimeMillis();
        long result = getWritableDatabase().insertWithOnConflict(RecentFileDBHelper.TABLE_NAME, RecentFileDBHelper.DBColumn.ID, RecentFileDBHelper.getContentValues(fileData), SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }

    public List<FileData> getRecentDownloadFiles() {
        List<FileData> list = new ArrayList<FileData>();
        Cursor cursor = null;
        LogUtil.i(TAG, "start search sql :" + SELECT_RECENT_DOWNLOAD_FILE_SQL);
        cursor = getReadableDatabase().rawQuery(SELECT_RECENT_DOWNLOAD_FILE_SQL, new String[] {});
        if (cursor == null) {
            return list;
        }
        while (cursor.moveToNext()) {
            list.add(RecentFileDBHelper.getFileDataFromCursor(cursor));
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

}
