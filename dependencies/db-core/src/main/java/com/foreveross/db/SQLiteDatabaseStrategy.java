package com.foreveross.db;


import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by dasunsy on 2017/2/17.
 */

public interface SQLiteDatabaseStrategy {

    void execSQL(String sql);

    void execSQL(String sql, Object[] bindArgs);

    Cursor query(String table, String[] columns, String selection,
                 String[] selectionArgs, String groupBy, String having,
                 String orderBy);

    long insert(String table, String nullColumnHack, ContentValues values);

    int update(String table, ContentValues values, String whereClause, String[] whereArgs);

    int delete(String table, String whereClause, String[] whereArgs);

    long replace(String table, String nullColumnHack, ContentValues initialValues);

    long insertWithOnConflict(String table, String nullColumnHack, ContentValues initialValues, int conflictAlgorithm);

    int updateWithOnConflict(String table, ContentValues values, String whereClause, String[] whereArgs, int conflictAlgorithm);

    Cursor rawQuery(String sql, String[] selectionArgs);

    void beginTransaction();

    void setTransactionSuccessful();

    void endTransaction();

    void close();

    int getVersion();

    void setVersion(int version);

    SQLiteStatement compileStatement(String sql);
}