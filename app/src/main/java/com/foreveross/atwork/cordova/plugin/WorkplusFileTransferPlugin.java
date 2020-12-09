package com.foreveross.atwork.cordova.plugin;

import com.foreverht.db.service.daoService.FileDaoService;
import com.foreveross.atwork.infrastructure.utils.file.FileStreamHelper;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.filetransfer.FileTransfer;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by dasunsy on 2017/7/28.
 */

public class WorkplusFileTransferPlugin extends FileTransfer {

    @Override
    protected void download(String source, String target, JSONArray args, CallbackContext callbackContext) throws JSONException {
        super.download(source, target, args, callbackContext);
    }

    @Override
    public OutputStream getOutputStream(OutputStream outputStream) throws IOException {
        return FileStreamHelper.getOutputStream(outputStream);
    }

    @Override
    public void insertRecentDb(String path) {
        FileDaoService.getInstance().insertRecentFile(path);
    }
}
