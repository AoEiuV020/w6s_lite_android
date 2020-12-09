package com.foreveross.db;


import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;

import java.io.File;


/**
 * Created by dasunsy on 2017/2/17.
 */

public class SQLiteDatabase implements SQLiteDatabaseStrategy {

    public static final int CONFLICT_IGNORE = android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE;

    public static final int CONFLICT_REPLACE = android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE;

    public android.database.sqlite.SQLiteDatabase mSQLiteDatabase;

    public SQLiteDatabase(android.database.sqlite.SQLiteDatabase sqLiteDatabase) {
        this.mSQLiteDatabase = sqLiteDatabase;
    }


    @Override
    public void execSQL(String sql) {
        mSQLiteDatabase.execSQL(sql);
    }

    @Override
    public void execSQL(String sql, Object[] bindArgs) {
        mSQLiteDatabase.execSQL(sql, bindArgs);
    }

    @Override
    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return mSQLiteDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    @Override
    public long insert(String table, String nullColumnHack, ContentValues values) {
        return mSQLiteDatabase.insert(table, nullColumnHack, values);
    }

    @Override
    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        return mSQLiteDatabase.update(table, values, whereClause, whereArgs);
    }

    @Override
    public int delete(String table, String whereClause, String[] whereArgs) {
        return mSQLiteDatabase.delete(table, whereClause, whereArgs);
    }

    @Override
    public long replace(String table, String nullColumnHack, ContentValues initialValues) {
        return mSQLiteDatabase.replace(table, nullColumnHack, initialValues);
    }

    @Override
    public long insertWithOnConflict(String table, String nullColumnHack, ContentValues initialValues, int conflictAlgorithm) {
        return mSQLiteDatabase.insertWithOnConflict(table, nullColumnHack, initialValues, conflictAlgorithm);
    }

    @Override
    public int updateWithOnConflict(String table, ContentValues values, String whereClause, String[] whereArgs, int conflictAlgorithm) {
        return mSQLiteDatabase.updateWithOnConflict(table, values, whereClause, whereArgs, conflictAlgorithm);
    }

    @Override
    public Cursor rawQuery(String sql, String[] selectionArgs) {
        return mSQLiteDatabase.rawQuery(sql, selectionArgs);
    }

    @Override
    public void beginTransaction() {
        mSQLiteDatabase.beginTransaction();
    }

    @Override
    public void setTransactionSuccessful() {
        mSQLiteDatabase.setTransactionSuccessful();

    }

    @Override
    public void endTransaction() {
        mSQLiteDatabase.endTransaction();
    }

    @Override
    public void close() {
        mSQLiteDatabase.close();
    }

    @Override
    public int getVersion() {
        return mSQLiteDatabase.getVersion();
    }

    @Override
    public void setVersion(int version) {
        mSQLiteDatabase.setVersion(version);
    }

    @Override
    public SQLiteStatement compileStatement(String sql) {
        return new SQLiteStatement(mSQLiteDatabase.compileStatement(sql));
    }

    public static void loadLibs(Context context) {
//        android.database.sqlite.SQLiteDatabase.loadLibs(context);
    }

    public static SQLiteDatabase openOrCreateDatabase(Context context, String name, String password, android.database.sqlite.SQLiteDatabase.CursorFactory factory) {
        return new SQLiteDatabase(context.openOrCreateDatabase(name, Context.MODE_PRIVATE, factory));
    }

    public static SQLiteDatabase openOrCreateDatabase(File file, String password, android.database.sqlite.SQLiteDatabase.CursorFactory factory) {
        return new SQLiteDatabase(android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(file, factory));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean deleteDatabase(File file) {
        return android.database.sqlite.SQLiteDatabase.deleteDatabase(file);
    }
}