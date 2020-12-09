package com.foreveross.db;

import android.content.Context;

import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by dasunsy on 2017/2/17.
 */

public abstract class WorkplusSQLiteOpenHelper extends SQLiteOpenHelper {
    public WorkplusSQLiteOpenHelper(Context context, String name, android.database.sqlite.SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public SQLiteDatabase getStrategyReadableDatabase(String password) {
        return new SQLiteDatabase(super.getReadableDatabase());
    }

    public SQLiteDatabase getStrategyWritableDatabase(String password) {
        return new SQLiteDatabase(super.getWritableDatabase());
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase sqLiteDatabase) {
        onCreate(new SQLiteDatabase(sqLiteDatabase));
    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        onUpgrade(new SQLiteDatabase(sqLiteDatabase), oldVersion, newVersion);
    }

    public abstract void onCreate(SQLiteDatabase sqLiteDatabase);

    public abstract void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion);
}
