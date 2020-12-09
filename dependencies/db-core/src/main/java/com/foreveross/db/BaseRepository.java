package com.foreveross.db;

import android.database.Cursor;

import com.foreveross.db.util.ListUtil;

import java.util.List;

public abstract class BaseRepository {


    public abstract SQLiteDatabase getWritableDatabaseCore();

    public abstract SQLiteDatabase getReadableDatabaseCore();

    public abstract String getDbNameCore();

    /**
     * 检查表是否存在
     *
     * @param tableName
     * @return
     */
    public boolean tableExists(String tableName) {
        boolean result = false;
        String sql = "select name from sqlite_master where type = 'table' and name = ?";
        Cursor cursor = null;
        try {
            cursor = getReadableDatabaseCore().rawQuery(sql, new String[]{tableName});
            while (cursor.moveToNext()) {
                result = true;
                break;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public static String getInStringParams(String param) {
        return getInStringParams(ListUtil.makeSingleList(param));
    }


    public static String getInStringParams(List<String> paramList) {
        StringBuilder params = new StringBuilder();

        for (int i = 0; i < paramList.size(); i++) {
            if (i == paramList.size() - 1) {
                params.append("'").append(paramList.get(i)).append("'");
            } else {
                params.append("'").append(paramList.get(i)).append("',");
            }
        }
        return params.toString();
    }



    public static String getDetailDBColumn(String tableName, String column) {
        return tableName + "."  + column;
    }




    public boolean removeAll(String tableName) {
        return removeAll(tableName, getWritableDatabaseCore());
    }

    /**
     * 清空指定表里的数据
     * @param tableName
     * */
    public static boolean removeAll(String tableName, SQLiteDatabase sqLiteDatabase) {

        try {
            sqLiteDatabase.beginTransaction();
            String sql = "delete from " + tableName;
            sqLiteDatabase.execSQL(sql);
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
        return true;
    }
}
