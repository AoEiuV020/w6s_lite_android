package com.foreveross.atwork.infrastructure.utils.encryption;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import androidx.annotation.Nullable;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.file.FileStreamHelper;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dasunsy on 2017/2/28.
 */

public class EncryptCacheDisk {

    private static final Object sFileLock = new Object();

    private static final Object sDoubleCheckLock = new Object();

    private static EncryptCacheDisk sInstance = null;

    //720min
    private final static long MAX_FILE_AGE = 12 * 60 * 60 * 1000;

    //30min
    private final static long KEEP_LIVE_AGE = 30 * 60 * 1000;

    //10min
    private final static long CLEAN_PERIOD = 10 * 60 * 1000;

    private final ConcurrentHashMap<String, Long> mLoadingDates = new ConcurrentHashMap<>();

    private volatile boolean mIsCleaning = false;

    private long mLastCleanTime = -1;

    private final String SUFFIX_DELETING = ".over";

    private boolean mRevertNamePure = false;

    public static EncryptCacheDisk getInstance() {
        if(null == sInstance) {
            synchronized (sDoubleCheckLock) {

                if(null == sInstance) {
                    sInstance = new EncryptCacheDisk();
                }
            }
        }

        return sInstance;
    }

    public void setRevertNamePure(boolean revertNamePure) {
        mRevertNamePure = revertNamePure;
    }

    public boolean revertNamePure() {
        return mRevertNamePure;
    }

    public synchronized void clean() {
        final File cacheTmpDic = new File(AtWorkDirUtils.getInstance().getTmpFilesCachePath());
        long currentTimeMillis = System.currentTimeMillis();

        if(!mIsCleaning && currentTimeMillis - mLastCleanTime > CLEAN_PERIOD) {
            mLastCleanTime = currentTimeMillis;
            mIsCleaning = true;

            cleanInThread(cacheTmpDic);

        }


    }

    @SuppressLint("StaticFieldLeak")
    private void cleanInThread(final File cacheTmpDic) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                synchronized (sFileLock) {
                    long startTime = System.currentTimeMillis();

                    renameDeletingFiles(cacheTmpDic);

                    long endTime = System.currentTimeMillis();

                    LogUtil.e(EncryptHelper.TAG, "rename duration -> " + (endTime - startTime));

                }

                long startTime = System.currentTimeMillis();

                deleteDeletingFiles(cacheTmpDic);

                long endTime = System.currentTimeMillis();

                LogUtil.e(EncryptHelper.TAG, "delelte duration -> " + (endTime - startTime));


                mIsCleaning = false;




                return null;
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    /**
     * 递归重命名准备删除的文件
     * @param file
     * */
    private void renameDeletingFiles(File file) {

        if (file.isFile()) {
            checkDeletingRename(file);
            return;
        }

        if(file.isDirectory()){
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                return;
            }

            for (File childFile : childFiles) {
                renameDeletingFiles(childFile);
            }

        }
    }


    private void checkDeletingRename(File file) {

        if(isOvertime(file)) {
            file.renameTo(new File(file.getAbsoluteFile() + SUFFIX_DELETING));
        }
    }

    /**
     * 是否过期
     * @param file
     * */
    private boolean isOvertime(File file) {
        return isOvertime(file.getAbsolutePath());
    }

    /**
     * 是否过期
     * @param filePath
     * */
    private boolean isOvertime(String filePath) {
        Long cacheTime = mLoadingDates.get(filePath);
        if(null == cacheTime) {
            cacheTime = new File(filePath).lastModified();
            mLoadingDates.put(filePath, cacheTime);
        }

        return System.currentTimeMillis() - cacheTime > MAX_FILE_AGE;
//        return true;
    }

    private void deleteDeletingFile(File file) {
        if(file.getAbsolutePath().contains(SUFFIX_DELETING)) {
            LogUtil.e(EncryptHelper.TAG, "delelting file -> " + file.getAbsolutePath());

            file.delete();


        }


    }

    private void deleteDeletingFiles(File file) {
        if (file.isFile()) {
            deleteDeletingFile(file);
            return;
        }

        if(file.isDirectory()){
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                return;
            }

            for (File childFile : childFiles) {
                deleteDeletingFiles(childFile);
            }

        }

    }


    public void setUsing(String path, boolean isSustain) {
        if(isSustain) {
            mLoadingDates.put(path, -1L);

        } else {
            mLoadingDates.put(path, System.currentTimeMillis() + KEEP_LIVE_AGE);
        }
    }

    public void removeUsing(String path) {
        mLoadingDates.remove(path);
    }

    @Nullable
    public File get(String path, boolean isSustain) {
        File file = null;
        if (FileUtil.isExistHavingContent(path)) {

            if(isOvertime(path) && mIsCleaning) {

                synchronized (sFileLock) {
                    if(FileUtil.isExistHavingContent(path)) {
                        setUsing(path, isSustain);

                        file = new File(path);
                    }
                }

            } else {
                setUsing(path, isSustain);

                file = new File(path);
            }


        }

        return file;

    }

    /**
     * 解密文件到 cache 目录
     *
     * @param path 传入的 path 需要满足 {@link EncryptHelper#checkFileEncrypted(String)}的条件
     * @param isSustain 是否需要持续保护, 该状态下, {@link EncryptCacheDisk#clean()}不会去清理
     *
     * @return 解密完后的路径
     * */
    public static String decrypt(String path, boolean isSustain) {
        String toPath = EncryptHelper.getOriginalPath(path);

        File file = EncryptCacheDisk.getInstance().get(toPath, isSustain);

        if(null == file) {
            boolean success = FileStreamHelper.decrypt(path, toPath, false);
            if(!success) {
                toPath = path;
            }
        }

        return toPath;
    }



    /**
     * 检查路径, 并且返回没有加密处理的源文件路径, 若源文件不存在, 则解密出来临时文件(注意堵塞线程)
     *
     * @param filePath
     * @param isSustain
     *
     * @return 源文件路径
     * */
    public String getOriginalFilePathAndCheck(String filePath, boolean isSustain) {
        String originalPath;


        if(EncryptHelper.checkFileEncrypted(filePath)) {
            originalPath = decrypt(filePath, isSustain);
        } else {
            originalPath = filePath;
        }

//        LogUtil.e(EncryptHelper.TAG, "filePath -> " + filePath + "  file size -> " + new File(filePath).length());
//        LogUtil.e(EncryptHelper.TAG, "originalPath -> " + originalPath + "  file size -> " + new File(originalPath).length());

        return originalPath;

    }



    /**
     * @see #getOriginalFilePathAndCheck(String, boolean)
     * */
    public void  getOriginalFilePathAndCheck(final String filePath, final boolean isSustain, final EncryptHelper.OnCheckFileEncryptedListener onCheckFileEncryptedListener) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                return getOriginalFilePathAndCheck(filePath, isSustain);
            }

            @Override
            protected void onPostExecute(String path) {
                onCheckFileEncryptedListener.onFinish(path);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }


}
