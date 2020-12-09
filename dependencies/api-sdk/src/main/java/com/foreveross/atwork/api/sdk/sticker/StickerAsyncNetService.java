package com.foreveross.atwork.api.sdk.sticker;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.ArrayMap;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.api.sdk.NetWorkFailListener;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.sticker.requestJson.CheckStickerRequest;
import com.foreveross.atwork.api.sdk.sticker.responseJson.StickerAlbumData;
import com.foreveross.atwork.api.sdk.sticker.responseJson.StickerAlbumList;
import com.foreveross.atwork.api.sdk.sticker.responseJson.StickerAlbumResult;
import com.foreveross.atwork.api.sdk.sticker.responseJson.StickerData;
import com.foreveross.atwork.api.sdk.util.NetWorkHttpResultHelper;
import com.foreveross.atwork.infrastructure.shared.StickerShareInfo;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class StickerAsyncNetService {

    private static volatile StickerAsyncNetService sInstance = new StickerAsyncNetService();

    public static StickerAsyncNetService getInstance() {
        return sInstance;
    }

    public void checkStickerAlbum(final Context context, final OnStickerAlbumsCheckedListener listener) {
        new AsyncTask<Void, Void, List<StickerAlbumData>>() {
            @Override
            protected List<StickerAlbumData> doInBackground(Void... voids) {
                CheckStickerRequest request = new CheckStickerRequest();
                String localList = StickerShareInfo.getInstance().getStickerData(context);
                List<StickerAlbumData> stickerAlbumList = new Gson().fromJson(localList, new TypeToken<List<StickerAlbumData>>(){}.getType());
                if (stickerAlbumList == null) {
                    stickerAlbumList = new ArrayList<>();
                }
                if (!ListUtil.isEmpty(stickerAlbumList)) {
                    List<String> albumIds = new ArrayList<>();
                    for (StickerAlbumData data : stickerAlbumList) {
                        albumIds.add(data.mId);
                    }
                    request.mAlbumIds = albumIds;
                    request.mRefreshTime = StickerShareInfo.getInstance().getLastRefreshTime(context);
                }
                HttpResult result = StickerNetSyncService.getInstance().checkStickerAlbums(context, new Gson().toJson(request));
                if (result.resultResponse == null) {
                    return stickerAlbumList;
                }
                StickerAlbumResult resultList = (StickerAlbumResult)result.resultResponse;
                if (resultList.mStickerAlbumList == null) {
                    return stickerAlbumList;
                }
                //map用于过滤比较数据
                Map<String, StickerAlbumData> filterMap = new ArrayMap<>();
                for (StickerAlbumData data :  stickerAlbumList) {
                    filterMap.put(data.mId, data);
                }
                List<StickerAlbumData> addList = resultList.mStickerAlbumList.mAddList;
                if (!ListUtil.isEmpty(addList)) {
                    for (StickerAlbumData data :  addList) {
                        filterMap.put(data.mId, data);
                    }
                }
                List<StickerAlbumData> updateList = resultList.mStickerAlbumList.mUpdateList;
                if (!ListUtil.isEmpty(updateList)) {
                    List<String> updateIds = new ArrayList<>();
                    for (StickerAlbumData data :  updateList) {
                        updateIds.add(data.mId);
                        filterMap.put(data.mId, data);
                    }
                    StickerShareInfo.getInstance().setStickerUpdateList(context, new Gson().toJson(updateIds));
                }
                List<String> deleteList = resultList.mStickerAlbumList.mDeletedList;
                if (!ListUtil.isEmpty(deleteList)) {
                    for (String deleteId :  deleteList) {
                        filterMap.remove(deleteId);
                    }
                }
                List<StickerAlbumData> returnList = new ArrayList<>(filterMap.values());
                StickerShareInfo.getInstance().setStickerData(context, new Gson().toJson(returnList));
                StickerShareInfo.getInstance().setLastRefreshTime(context, resultList.mStickerAlbumList.mRefreshTime);
                return returnList;
            }

            @Override
            protected void onPostExecute(List<StickerAlbumData> stickerAlbumList) {
                if (listener == null) {
                    return;
                }
                listener.onChecked(stickerAlbumList);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    public interface OnStickerAlbumsCheckedListener {
        void onChecked(List<StickerAlbumData> stickerAlbumList);
    }
}
