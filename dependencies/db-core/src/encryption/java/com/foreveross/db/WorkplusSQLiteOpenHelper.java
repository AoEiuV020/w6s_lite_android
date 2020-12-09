package com.foreveross.db;

import android.content.Context;

import net.sqlcipher.database.SQLiteOpenHelper;


/**
 * Created by dasunsy on 2017/2/17.
 */

public abstract class WorkplusSQLiteOpenHelper extends SQLiteOpenHelper {
    public WorkplusSQLiteOpenHelper(Context context, String name, net.sqlcipher.database.SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public SQLiteDatabase getStrategyReadableDatabase(String password) {
        return new SQLiteDatabase(super.getReadableDatabase(password));
    }

    public SQLiteDatabase getStrategyWritableDatabase(String password) {
        return new SQLiteDatabase(super.getWritableDatabase(password));
    }

    @Override
    public void onCreate(net.sqlcipher.database.SQLiteDatabase sqLiteDatabase) {
        onCreate(new SQLiteDatabase(sqLiteDatabase));
    }

    @Override
    public void onUpgrade(net.sqlcipher.database.SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        onUpgrade(new SQLiteDatabase(sqLiteDatabase), oldVersion, newVersion);
    }

    public abstract void onCreate(SQLiteDatabase sqLiteDatabase);

    public abstract void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion);
}
