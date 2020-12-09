package com.foreveross.atwork.modules.dropbox.loader;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import com.foreverht.cache.WatermarkCache;
import com.foreverht.db.service.repository.DropboxRepository;
import com.foreverht.db.service.repository.WatermarkRepository;
import com.foreveross.atwork.infrastructure.model.Watermark;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;

import java.util.List;

/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 * Created by reyzhang22 on 16/9/14.
 */
public class DropboxLoader extends AsyncTaskLoader<List<Dropbox>> {

    private String mSourceId;

    private boolean mSaveMode;
    public DropboxLoader(Context context, String sourceId, boolean saveMode) {
        super(context);
        mSourceId = sourceId;
        mSaveMode = saveMode;
    }

    @Override
    public List<Dropbox> loadInBackground() {
        Watermark watermark = WatermarkRepository.getInstance().queryWatermark(mSourceId, Watermark.Type.DROPBOX.toInt());
        if (watermark != null) {
            WatermarkCache.getInstance().setWatermarkConfigCache(watermark, true);
        }
        if (mSaveMode) {
            return DropboxRepository.getInstance().getDropboxDirs(mSourceId);
        }
        return DropboxRepository.getInstance().getDropboxBySourceId(mSourceId);
    }
}
