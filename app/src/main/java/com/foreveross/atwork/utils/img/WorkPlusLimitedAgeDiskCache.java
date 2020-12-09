package com.foreveross.atwork.utils.img;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.core.DefaultConfigurationFactory;
import com.nostra13.universalimageloader.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dasunsy on 2017/2/27.
 */

public class WorkPlusLimitedAgeDiskCache extends WorkPlusBasicDiskCache {
    private final long maxFileAge;

    private final Map<File, Long> loadingDates = Collections.synchronizedMap(new HashMap<File, Long>());

    /**
     * @param cacheDir Directory for file caching
     * @param maxAge   Max file age (in seconds). If file age will exceed this value then it'll be removed on next
     *                 treatment (and therefore be reloaded).
     */
    public WorkPlusLimitedAgeDiskCache(File cacheDir, long maxAge) {
        this(cacheDir, null, DefaultConfigurationFactory.createFileNameGenerator(), maxAge);
    }

    /**
     * @param cacheDir Directory for file caching
     * @param maxAge   Max file age (in seconds). If file age will exceed this value then it'll be removed on next
     *                 treatment (and therefore be reloaded).
     */
    public WorkPlusLimitedAgeDiskCache(File cacheDir, File reserveCacheDir, long maxAge) {
        this(cacheDir, reserveCacheDir, DefaultConfigurationFactory.createFileNameGenerator(), maxAge);
    }

    /**
     * @param cacheDir          Directory for file caching
     * @param reserveCacheDir   null-ok; Reserve directory for file caching. It's used when the primary directory isn't available.
     * @param fileNameGenerator Name generator for cached files
     * @param maxAge            Max file age (in seconds). If file age will exceed this value then it'll be removed on next
     *                          treatment (and therefore be reloaded).
     */
    public WorkPlusLimitedAgeDiskCache(File cacheDir, File reserveCacheDir, FileNameGenerator fileNameGenerator, long maxAge) {
        super(cacheDir, reserveCacheDir, fileNameGenerator);
        this.maxFileAge = maxAge * 1000; // to milliseconds
    }

    @Override
    public File get(String imageUri) {
        File originalFile = super.get(imageUri);

        File diskFile = super.getFile(imageUri);

        if (diskFile != null && diskFile.exists()) {
            boolean cached;
            Long loadingDate = loadingDates.get(diskFile);
            if (loadingDate == null) {
                cached = false;
                loadingDate = diskFile.lastModified();
            } else {
                cached = true;
            }

            if (System.currentTimeMillis() - loadingDate > maxFileAge) {
                diskFile.delete();
                originalFile.delete();

                loadingDates.remove(diskFile);
            } else if (!cached) {
                loadingDates.put(diskFile, loadingDate);
            }
        }
        return originalFile;
    }

    @Override
    public boolean save(String imageUri, InputStream imageStream, IoUtils.CopyListener listener) throws IOException {
        boolean saved = super.save(imageUri, imageStream, listener);
        rememberUsage(imageUri);
        return saved;
    }

    @Override
    public boolean save(String imageUri, Bitmap bitmap) throws IOException {
        boolean saved = super.save(imageUri, bitmap);
        rememberUsage(imageUri);
        return saved;
    }

    @Override
    public boolean remove(String imageUri) {
        loadingDates.remove(getFile(imageUri));
        return super.remove(imageUri);
    }

    @Override
    public void clear() {
        super.clear();
        loadingDates.clear();
    }

    private void rememberUsage(String imageUri) {
        File file = getFile(imageUri);
        long currentTime = System.currentTimeMillis();
        file.setLastModified(currentTime);
        loadingDates.put(file, currentTime);
    }

}
