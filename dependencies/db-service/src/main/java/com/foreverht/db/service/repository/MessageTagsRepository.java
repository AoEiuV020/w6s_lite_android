package com.foreverht.db.service.repository;

import android.database.Cursor;

import com.foreverht.db.service.dbHelper.MessageTagsDBHelper;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.db.BaseRepository;
import com.foreveross.db.SQLiteDatabase;
import com.w6s.module.MessageTags;

import java.util.ArrayList;
import java.util.List;

import static com.foreverht.db.service.W6sBaseRepository.getReadableDatabase;
import static com.foreverht.db.service.W6sBaseRepository.getWritableDatabase;

public class MessageTagsRepository extends BaseRepository {

    public static MessageTagsRepository sInstance = new MessageTagsRepository();

    public static MessageTagsRepository getInstance() {
        return sInstance;
    }

    public boolean batchInsertMessageTags(List<MessageTags> list) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();

            for (MessageTags tag : list) {
                insertMessageTags(tag);
            }
            sqLiteDatabase.setTransactionSuccessful();


        } catch (Exception e) {
            e.printStackTrace();
            return false;

        } finally {
            sqLiteDatabase.endTransaction();
        }

        return true;
    }

    private void insertMessageTags(MessageTags messageTags) {

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.insertWithOnConflict(
                MessageTagsDBHelper.TABLE_NAME,
                null,
                MessageTagsDBHelper.getContentValues(messageTags),
                SQLiteDatabase.CONFLICT_REPLACE);

    }

    public List<MessageTags> getMessageTags(App app) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String sql = "select * from " + MessageTagsDBHelper.TABLE_NAME + " where " + MessageTagsDBHelper.DBColumn.APP_ID + " = ? and "
                + MessageTagsDBHelper.DBColumn.DOMAIN_ID + " = ? and "+ MessageTagsDBHelper.DBColumn.ORG_ID + " = ?";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, new String[] {app.mAppId, app.mDomainId, app.mOrgId});
        List<MessageTags> list = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {
                return list;
            }
            do {
                list.add(MessageTagsDBHelper.fromCursor(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return list;
    }

    public void deleteAllTags() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String sql = "delete * from " + MessageTagsDBHelper.TABLE_NAME;
        sqLiteDatabase.delete(MessageTagsDBHelper.TABLE_NAME, null, null);
    }

    @Override
    public SQLiteDatabase getWritableDatabaseCore() {
        return getWritableDatabase();
    }

    @Override
    public SQLiteDatabase getReadableDatabaseCore() {
        return getReadableDatabase();
    }

    @Override
    public String getDbNameCore() {
        return null;
    }
}
