package com.foreverht.db.service.repository;

import android.database.Cursor;
import android.text.TextUtils;

import com.foreverht.cache.DropboxCache;
import com.foreverht.db.service.R;
import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.dbHelper.DropboxDBHelper;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.db.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

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
 * Created by reyzhang22 on 16/9/7.
 */
public class DropboxRepository extends W6sBaseRepository {

    private static DropboxRepository sInstance = new DropboxRepository();

    public static DropboxRepository getInstance() {
        return sInstance;
    }

    /**
     * 批量插入网盘信息
     * @param list
     * @return
     */
    public boolean batchInsertDropboxes(List<Dropbox> list) {
        boolean insertResult = false;

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.beginTransaction();
        try {
            for (Dropbox dropbox : list) {
                long result = insertOrUpdateDropbox(dropbox);
                LogUtil.e("insert result ->" + result);
            }

            sqLiteDatabase.setTransactionSuccessful();
            insertResult = true;
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                sqLiteDatabase.endTransaction();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return insertResult;
    }

    public long insertOrUpdateDropbox(Dropbox dropbox) {
        DropboxCache.getInstance().setDropboxCache(dropbox);
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.insertWithOnConflict(
                DropboxDBHelper.TABLE_NAME,
                null,
                DropboxDBHelper.getContentValues(dropbox),
                SQLiteDatabase.CONFLICT_REPLACE);
    }
    /**
     * 通过数据源id查找该id下的网盘文件所有信息
     * @param sourceId
     * @return
     */
    public List<Dropbox> getDropboxBySourceId(String sourceId) {
        String sql = "select * from " + DropboxDBHelper.TABLE_NAME + " where " + DropboxDBHelper.DBColumn.SOURCE_ID + " = ? order by "
                + DropboxDBHelper.DBColumn.LAST_MODIFY_TIME + " DESC";
        Cursor cursor = getReadableDatabase().rawQuery(sql, new String[]{sourceId});
        return getDropboxFromCursor(cursor);
    }

    /**
     * 查询所有将在24小时内过期的网盘数据
     * @return
     */
    public List<Dropbox> getIsGoingOverdueDropbox() {
        long time = TimeUtil.getCurrentTimeInMillis() + ( 24 * 60 * 60 * 1000 );
        String sql = "select * from " + DropboxDBHelper.TABLE_NAME + " where " + DropboxDBHelper.DBColumn.EXPIRED_TIME + " < ?  and " + DropboxDBHelper.DBColumn.IS_OVERDUE_REPORT + "=?";
        Cursor cursor = getReadableDatabase().rawQuery(sql, new String[]{String.valueOf(time), "0"});
        return getIsGoingOverdueDropboxFromCursor(cursor);
    }

    public boolean isFileExistInSameParent(String sourceId, String parentId, String fileName) {
        String sql = "select * from " + DropboxDBHelper.TABLE_NAME + " where " + DropboxDBHelper.DBColumn.SOURCE_ID + " = ? and "
                + DropboxDBHelper.DBColumn.PARENT_ID + " = ? and "+ DropboxDBHelper.DBColumn.FILE_NAME + " = ?";
        Cursor cursor = getReadableDatabase().rawQuery(sql, new String[] {sourceId, parentId, fileName});
        List<Dropbox> list = getDropboxFromCursor(cursor);
        return !list.isEmpty();
    }

    /**
     * 在sourceId下根据类型获取网盘列表
     * @param sourceId
     * @param fileType
     * @return
     */
    public List<Dropbox> getDropboxByFileTypeInSourceId(String sourceId, Dropbox.DropboxFileType fileType, String parentId) {
        String sql = "select * from " + DropboxDBHelper.TABLE_NAME + " where " + DropboxDBHelper.DBColumn.SOURCE_ID + " = ? and " +
                DropboxDBHelper.DBColumn.PARENT_ID+" = ? order by " + DropboxDBHelper.DBColumn.LAST_MODIFY_TIME + " DESC";
        Cursor cursor = getReadableDatabase().rawQuery(sql, new String[]{sourceId, parentId});
        return getDropboxFromCursor(cursor);
    }

    public List<Dropbox> sortedDropboxByTime(String sourceId) {
        String sql = "select * from " + DropboxDBHelper.TABLE_NAME + " where " +  DropboxDBHelper.DBColumn.SOURCE_ID + " = ? order by " + DropboxDBHelper.DBColumn.LAST_MODIFY_TIME + " DESC";
        Cursor cursor = getReadableDatabase().rawQuery(sql, new String[]{sourceId});
        return getDropboxFromCursor(cursor);
    }

    public List<Dropbox> sortedDropboxByName(String sourceId) {
        String sql = "select * from " + DropboxDBHelper.TABLE_NAME + " where " +  DropboxDBHelper.DBColumn.SOURCE_ID + " = ? order by " + DropboxDBHelper.DBColumn.FILE_NAME + " DESC";
        Cursor cursor = getReadableDatabase().rawQuery(sql, new String[]{sourceId});
        return getDropboxFromCursor(cursor);
    }

    private List<Dropbox> getDropboxFromCursor(Cursor cursor) {
        List<Dropbox> dropboxes = new ArrayList<>();
        if (cursor == null) {
            return dropboxes;
        }
        try {
            while (cursor.moveToNext()) {
                Dropbox dropbox = DropboxDBHelper.fromCursor(cursor);
                if (dropbox == null) {
                    continue;
                }
                if (dropbox.mExpiredTime - getCurrentTimeInMillis() <= 0) {
                    continue;
                }
                dropboxes.add(dropbox);
            }
        } finally {
            cursor.close();
        }

        return dropboxes;
    }

    private List<Dropbox> getIsGoingOverdueDropboxFromCursor(Cursor cursor) {
        List<Dropbox> dropboxes = new ArrayList<>();
        if (cursor == null) {
            cursor.close();
            return dropboxes;
        }
        try {
            while (cursor.moveToNext()) {
                Dropbox dropbox = DropboxDBHelper.fromCursor(cursor);
                if (dropbox == null || dropbox.mUploadStatus == Dropbox.UploadStatus.Fail) {
                    continue;
                }
                dropbox.mIsOverdueReport = true;
                dropboxes.add(dropbox);
            }
        } finally {
            cursor.close();
        }

        return dropboxes;
    }

    /**
     * 通过dropboxId获取到网盘信息
     * @param fileId
     * @return
     */
    public Dropbox getDropboxByFileId(String fileId) {
        if (TextUtils.isEmpty(fileId)) {
            return null;
        }
        String sql = "select * from " + DropboxDBHelper.TABLE_NAME + " where " + DropboxDBHelper.DBColumn.FILE_ID + " = ?";
        Cursor cursor = getReadableDatabase().rawQuery(sql, new String[]{fileId});
        if (cursor == null) {
            return null;
        }

        Dropbox dropbox = null;
        try {
            if (cursor.moveToFirst()) {
                dropbox = DropboxDBHelper.fromCursor(cursor);

            }
        } finally {
            cursor.close();
        }
        return dropbox;
    }

    /**
     * 获取dropbox所有文件夹
     * @return
     */
    public List<Dropbox> getDropboxDirs(String sourceId) {
        String sql = "select * from " + DropboxDBHelper.TABLE_NAME + " where " + DropboxDBHelper.DBColumn.IS_DIR + " = '1' and " + DropboxDBHelper.DBColumn.SOURCE_ID + " =? order by " + DropboxDBHelper.DBColumn.LAST_MODIFY_TIME + " DESC";
        Cursor cursor = getReadableDatabase().rawQuery(sql, new String[] {sourceId});
        return getDropboxFromCursor(cursor);
    }

    /**
     * 根据文件id删除相关信息
     * @param fileId
     * @return
     */
    public int deleteDropboxByFileId(String fileId) {
        return getWritableDatabase().delete(DropboxDBHelper.TABLE_NAME, DropboxDBHelper.DBColumn.FILE_ID + "=?", new String[]{fileId});
    }

    public void batchDeleteDropboxList(List<String> dropboxes) {

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();
            for (String id : dropboxes) {
                String sql = "delete from " + DropboxDBHelper.TABLE_NAME + " where " +  DropboxDBHelper.DBColumn.FILE_ID + "=?";
                sqLiteDatabase.execSQL(sql, new String[]{id});
            }
            sqLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqLiteDatabase.endTransaction();
        }

    }
}
