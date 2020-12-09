package com.foreverht.db.service.daoService;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.foreverht.db.service.BaseDbService;
import com.foreverht.db.service.repository.RecentFileRepository;
import com.foreveross.atwork.infrastructure.model.file.FileData;


public class FileDaoService extends BaseDbService {

    private static final String TAG = FileDaoService.class.getSimpleName();

    private static FileDaoService sFileDaoService = new FileDaoService();

    public static FileDaoService getInstance() {
        synchronized (TAG) {
            if (sFileDaoService == null) {
                sFileDaoService = new FileDaoService();
            }
            return sFileDaoService;
        }
    }

    public void insertRecentFile(String path) {
        insertRecentFile(FileData.fromPath(path));
    }


    @SuppressLint("StaticFieldLeak")
    public void insertRecentFile(final FileData fileData) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                RecentFileRepository repository = RecentFileRepository.getInstance();
                boolean result = repository.insertFileData(fileData);
                return result;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {

            }
        }.executeOnExecutor(mDbExecutor);
    }

}
