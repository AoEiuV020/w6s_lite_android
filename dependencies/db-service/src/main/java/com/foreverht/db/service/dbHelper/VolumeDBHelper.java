package com.foreverht.db.service.dbHelper;

import com.foreveross.db.SQLiteDatabase;

public class VolumeDBHelper implements DBHelper {

    public static final String TABLE_NAME = "volume_";

    private static final String SQL_EXEC = CREATE_TABLE + TABLE_NAME + LEFT_BRACKET +
            VolumeDBHelper.DBColumn.ID + TEXT + PRIMARY_KEY +  COMMA +
            VolumeDBHelper.DBColumn.VOLUME_TYPE + TEXT + COMMA +
            VolumeDBHelper.DBColumn.OWNER_ID + TEXT + COMMA +
            VolumeDBHelper.DBColumn.OWNER_CODE + TEXT + COMMA +
            DBColumn.NAME + TEXT+ COMMA +
            DBColumn.TOTAL_SIZE + INTEGER + COMMA +
            DBColumn.MAX_SIZE + INTEGER + COMMA +
            DBColumn.CREATE_TIME + INTEGER + COMMA +
            DBColumn.ASSOCIATED + TEXT + COMMA +
            DBColumn.WATERMARK + TEXT + COMMA +
            DBColumn.READONLY + TEXT + COMMA +
            DBColumn.SHAREABLE + TEXT +COMMA +
            DBColumn.PINYIN + TEXT +
            RIGHT_BRACKET;

    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_EXEC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 350) {
            db.execSQL(SQL_EXEC);
            oldVersion = 350;
        }
    }

    public class DBColumn {
        public static final String ID = "id_";

        public static final String VOLUME_TYPE = "volume_id_";

        public static final String OWNER_ID = "owner_id_";

        public static final String OWNER_CODE = "owner_code_";

        public static final String NAME = "name_";

        public static final String TOTAL_SIZE = "total_size_";

        public static final String MAX_SIZE = "max_size_";

        public static final String CREATE_TIME = "create_time_";

        public static final String ASSOCIATED = "associated_";

        public static final String WATERMARK = "watermark_";

        public static final String READONLY = "readonly_";

        public static final String SHAREABLE = "shareable_";

        public static final String PINYIN = "pinyin_";
    }
}
