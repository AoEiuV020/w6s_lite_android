package com.foreveross.db;

/**
 * Created by dasunsy on 2017/2/17.
 */

public interface SQLiteStatementStrategy {
    void bindString(int index, String value);

    void execute();

    void clearBindings();

    void close();
}
