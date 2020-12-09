package com.foreveross.db;

/**
 * Created by dasunsy on 2017/2/17.
 */

public class SQLiteStatement implements SQLiteStatementStrategy {

    public net.sqlcipher.database.SQLiteStatement mSqLiteStatement;

    public SQLiteStatement(net.sqlcipher.database.SQLiteStatement sqLiteStatement) {
        this.mSqLiteStatement = sqLiteStatement;
    }


    @Override
    public void bindString(int index, String value) {
        mSqLiteStatement.bindString(index, value);
    }

    @Override
    public void execute() {
        mSqLiteStatement.execute();
    }

    @Override
    public void clearBindings() {
        mSqLiteStatement.clearBindings();
    }

    @Override
    public void close() {
        mSqLiteStatement.close();
    }
}
