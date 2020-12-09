package com.foreveross.atwork.db.daoService;

import android.os.AsyncTask;

import com.foreverht.db.service.BaseDbService;
import com.foreverht.db.service.repository.DataSchemaRepository;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.utils.ListUtil;

import java.util.List;

/**
 * Created by shadow on 2016/5/4.
 */
public class DataSchemaDaoService extends BaseDbService {

    private static DataSchemaDaoService sInstance = new DataSchemaDaoService();

    private DataSchemaDaoService() {

    }
    public static DataSchemaDaoService getInstance() {
        return sInstance;
    }

    /**
     * 批量插入 emp liOst 的 dataSchema
     * */
    public void batchInsertMulEmpsDataSchema(final List<Employee> empList) {
        if (ListUtil.isEmpty(empList)){
            return;
        }
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return DataSchemaRepository.getInstance().batchInsertMulEmpsDataSchema(empList);
            }
        }.executeOnExecutor(mDbExecutor);
    }



}
