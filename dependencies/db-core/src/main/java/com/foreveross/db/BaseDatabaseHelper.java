package com.foreveross.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseDatabaseHelper extends WorkplusSQLiteOpenHelper {

    private static final String ADD_COLUMN_ON_TABLE_SQL = "alter table %s add column %s %s";


    public BaseDatabaseHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }


    public static void addColumnToTable(final SQLiteDatabase db, String tableName, final String columnName, final String columnType) {
        try {
            String sql = String.format(ADD_COLUMN_ON_TABLE_SQL, tableName, columnName, columnType);
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();

            //本地已存在字段, 捕捉异常, 不抛出
            if(e.getLocalizedMessage().contains("duplicate column name")) {
                return;
            }

            throw e;
        }
    }

    public static void createTable(final SQLiteDatabase db, String createSql) {
        try {
            db.execSQL(createSql);
        } catch (Exception e) {
            e.printStackTrace();

            //本地已存在表, 捕捉异常, 不抛出
            if(e.getLocalizedMessage().contains("already exists")) {
                return;
            }

            throw e;
        }

    }

    /**
     * 获取当前 db 所有的表名
     * @param db
     * */
    public static List<String> getAllTablesName(SQLiteDatabase db){
        ArrayList<String> arrTblNames = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        try {
            if (c.moveToFirst()) {
                while ( !c.isAfterLast() ) {
                    arrTblNames.add( c.getString( c.getColumnIndex("name")) );
                    c.moveToNext();
                }
            }
        } finally {
            if(null != c) {
                c.close();
            }
        }

        return arrTblNames;
    }

}
