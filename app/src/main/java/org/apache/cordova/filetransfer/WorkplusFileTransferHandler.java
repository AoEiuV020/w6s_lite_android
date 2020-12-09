package org.apache.cordova.filetransfer;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by dasunsy on 2017/8/1.
 */

public interface WorkplusFileTransferHandler {
    OutputStream getOutputStream(OutputStream outputStream) throws IOException;

    void insertRecentDb(String path);
}
