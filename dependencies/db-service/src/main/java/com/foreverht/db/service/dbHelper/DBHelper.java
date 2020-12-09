package com.foreverht.db.service.dbHelper;

import com.foreveross.db.SQLiteDatabase;

/**
 * Created by lingen on 15/4/3.
 * Description:
 */
public interface DBHelper {

    String LEFT_BRACKET = " ( ";

    String RIGHT_BRACKET = " ) ";

    String CREATE_TABLE = "create table ";

    String TEXT = " text ";

    String INTEGER = " integer ";

    String BLOB = " blob ";

    String PRIMARY_KEY = " primary key ";

    String COMMA = ",";

    String FOREIGN_KEY = "foreign key";

    String REFERENCES = "references ";

    String AUTO_INCREMENT = "autoincrement";

    String NOT_NULL = " NOT NULL ";

    String UNIQUE = " UNIQUE ";

    void onCreate(SQLiteDatabase db);

    void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
}
