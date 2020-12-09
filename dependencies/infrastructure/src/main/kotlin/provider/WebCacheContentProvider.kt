package com.foreverht.workplus.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils
import java.io.File
import java.io.FileNotFoundException

class WebCacheContentProvider: ContentProvider() {

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        Log.e("WebCacheContentProvider", "fetching: $uri")

        val path = AtWorkDirUtils.getInstance().webCacheDir + "/" + uri.path
        val file = File(path)
        var parcel: ParcelFileDescriptor? = null
        try {
            parcel = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        } catch (e: FileNotFoundException) {
            Log.e("WebCacheContentProvider", "uri $uri", e)
        }
        return parcel
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        return null
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        return -1
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return -1
    }

    override fun getType(uri: Uri): String? {
        return ""
    }
}