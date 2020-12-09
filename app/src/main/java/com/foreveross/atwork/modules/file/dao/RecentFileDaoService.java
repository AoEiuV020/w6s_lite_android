package com.foreveross.atwork.modules.file.dao;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Handler;

import com.foreverht.db.service.BaseDbService;
import com.foreverht.db.service.repository.RecentFileRepository;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.modules.file.fragement.FileSelectFragment;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.foreveross.atwork.modules.downLoad.component.DownloadFileDetailView.MSG_DELETE_RECENT_DOWN_LOAD_FILE_SUCCESS;

/**
 * 文件DAO数据查询服务
 * Created by ReyZhang on 2015/5/12.
 */
public class RecentFileDaoService extends BaseDbService {

    private static final String TAG = RecentFileDaoService.class.getSimpleName();

    private static RecentFileDaoService sFileDaoService = new RecentFileDaoService();

    public static RecentFileDaoService getInstance() {
        synchronized (TAG) {
            if (sFileDaoService == null) {
                sFileDaoService = new RecentFileDaoService();
            }
            return sFileDaoService;
        }
    }

    /**
     * 查询最近文件列表
     *
     * @param handler
     */
    @SuppressLint("StaticFieldLeak")
    public void getRecentFiles(final Handler handler) {
        new AsyncTask<Void, Void, List<FileData>>() {

            @Override
            protected List<FileData> doInBackground(Void... params) {
                RecentFileRepository repository = RecentFileRepository.getInstance();
                List<FileData> recentFlies = repository.queryFileDataList();
                removeSameFileData(recentFlies);

                return recentFlies;
            }

            @Override
            protected void onPostExecute(List<FileData> fileDatas) {
//                if (fileDatas.isEmpty()) {
//                    return;
//                }
                handler.obtainMessage(FileSelectFragment.MSG_GET_RECENT_FILES_SUCCESS, fileDatas).sendToTarget();
            }
        }.executeOnExecutor(mDbExecutor);
    }

    /**
     * 搜索文件列表
     */
    @SuppressLint("StaticFieldLeak")
    public void searchRecentFiles(String searchValue, OnSearchRecentFilesListener listener) {
        new AsyncTask<Void, Void, List<FileData>>() {

            @Override
            protected List<FileData> doInBackground(Void... params) {
                RecentFileRepository repository = RecentFileRepository.getInstance();
                List<FileData> recentFlies = repository.searchFileData(searchValue);
                removeSameFileData(recentFlies);
                return recentFlies;
            }

            @Override
            protected void onPostExecute(List<FileData> fileDatas) {
                if (fileDatas.isEmpty()) {
                    return;
                }
                listener.onSearchRecentFilesResult(fileDatas);
            }
        }.executeOnExecutor(mDbExecutor);
    }
    public interface OnSearchRecentFilesListener {
        void onSearchRecentFilesResult(List<FileData> fileDataList);
    }

    /**
     * Description:根据文件的id删除本地数据库中的某一文件
     * @param handler
     * @param fileId
     */
    @SuppressLint("StaticFieldLeak")
    public void deleteDownloadFileByFileId(final Handler handler, String fileId, String filePath) {
        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... params) {
                //FileUtil.deleteFile(filePath, false);///////暂不清除该文件的本地缓存
                RecentFileRepository repository = RecentFileRepository.getInstance();
                return repository.deleteDownloadFileByFileId(fileId);
            }

            @Override
            protected void onPostExecute(Integer result) {
                handler.obtainMessage(MSG_DELETE_RECENT_DOWN_LOAD_FILE_SUCCESS, result).sendToTarget();
            }
        }.executeOnExecutor(mDbExecutor);
    }
    /**
     * 查询最近文件某一类型（后缀名）的列表
     *
     * @param handler
     */
    @SuppressLint("StaticFieldLeak")
    public void getRecentFilesByType(final Handler handler, String FileType) {
        new AsyncTask<Void, Void, List<FileData>>() {

            @Override
            protected List<FileData> doInBackground(Void... params) {
                RecentFileRepository repository = RecentFileRepository.getInstance();
                List<FileData> recentFlies = repository.queryFileDataListByType(FileType);
                removeSameFileData(recentFlies);

                return recentFlies;
            }

            @Override
            protected void onPostExecute(List<FileData> fileDatas) {
                if (fileDatas.isEmpty()) {
                    return;
                }
                handler.obtainMessage(FileSelectFragment.MSG_GET_RECENT_FILES_SUCCESS, fileDatas).sendToTarget();
            }
        }.executeOnExecutor(mDbExecutor);
    }

    private void removeSameFileData(List<FileData> recentFlies) {
        Set<FileData> fileDataSet = new HashSet<>(recentFlies);
        recentFlies.clear();
        recentFlies.addAll(fileDataSet);

        Collections.sort(recentFlies, (o1, o2) -> {

            if(0 <= o2.date - o1.date) {
                return 1;
            } else {
                return -1;
            }

        });
    }


    public void getRecentDownloadFiles(final Handler handler) {
        new AsyncTask<Void, Void, List<FileData>>() {

            @Override
            protected List<FileData> doInBackground(Void... params) {
                RecentFileRepository repository = RecentFileRepository.getInstance();
                List<FileData> recentFlies = repository.getRecentDownloadFiles();
                return recentFlies;
            }

            @Override
            protected void onPostExecute(List<FileData> fileDatas) {
                if (fileDatas.isEmpty()) {
                    return;
                }
                handler.obtainMessage(FileSelectFragment.MSG_GET_RECENT_FILES_SUCCESS, fileDatas).sendToTarget();
            }
        }.executeOnExecutor(mDbExecutor);
    }
}
