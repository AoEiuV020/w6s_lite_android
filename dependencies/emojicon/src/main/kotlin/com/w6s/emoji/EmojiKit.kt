package com.w6s.emoji

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


object EmojiKit {
    private val STICKER_NAME_IN_ASSETS = "sticker"
    private var density: Float = 0.toFloat()
    private var scaleDensity: Float = 0.toFloat()
    private var STICKER_PATH: String? = null//默认路径在 /data/data/包名/files/sticker 下

    private fun getAndSaveParameter(context: Context) {
        val dm = context.getApplicationContext().getResources().getDisplayMetrics()
        density = dm.density
        scaleDensity = dm.scaledDensity
    }

    fun init(context: Context, stickerPath: String) {
        getAndSaveParameter(context)
        STICKER_PATH = stickerPath

        //将asset/sticker目录下默认的贴图复制到STICKER_PATH下
        copyStickerToStickerPath(context, STICKER_NAME_IN_ASSETS)

    }

    fun init(context: Context) {
        init(context, File(context.getFilesDir(), STICKER_NAME_IN_ASSETS).getAbsolutePath())
    }

    private fun copyStickerToStickerPath(context: Context, assetsFolderPath: String) {
        val assetManager = context.getResources().getAssets()
        val srcFile: List<String> = ArrayList()
        try {
            val stickers = assetManager.list(assetsFolderPath)
            if (stickers != null) {
                for (fileName in stickers) {
                    if (!File(getStickerPath(), fileName).exists()) {
                        srcFile.plus(fileName)
                    }
                }
            }
            if (srcFile.size > 0) {
                copyToStickerPath(context, assetsFolderPath, srcFile)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun copyToStickerPath(context: Context, assetsFolderPath: String, srcFile: List<String>) {
        Thread(Runnable {
            val assetManager = context.getResources().getAssets()
            for (fileName in srcFile) {

                if (fileName.contains(".")) {//文件
                    var inputStream: InputStream? = null
                    var fos: FileOutputStream? = null
                    try {
                        inputStream = assetManager.open(assetsFolderPath + File.separator + fileName)
                        val destinationFile: File
                        if (assetsFolderPath.startsWith(STICKER_NAME_IN_ASSETS + File.separator)) {//递归回来的时候assetsFolderPath可能变为"sticker/tsj"
                            destinationFile = File(getStickerPath(), assetsFolderPath.substring(assetsFolderPath.indexOf(File.separator) + 1) + File.separator + fileName)
                        } else {
                            destinationFile = File(getStickerPath(), fileName)
                        }
                        fos = FileOutputStream(destinationFile)
                        val buffer = ByteArray(1024)
                        var len: Int
                        while ((inputStream!!.read(buffer)) != -1) {
                            fos!!.write(buffer, 0, inputStream!!.read(buffer))
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        if (inputStream != null) {
                            try {
                                inputStream!!.close()
                                inputStream = null
                            } catch (e: IOException) {
                                e.printStackTrace()
                                inputStream = null
                            }

                        }
                        if (fos != null) {
                            try {
                                fos!!.close()
                                fos = null
                            } catch (e: IOException) {
                                e.printStackTrace()
                                fos = null
                            }

                        }
                    }
                } else {//文件夹

                    val dir = File(getStickerPath(), fileName)
                    if (!dir.exists()) {
                        dir.mkdirs()
                    }

                    copyStickerToStickerPath(context,assetsFolderPath + File.separator + fileName)
                }
            }
        }).start()
    }


    fun getStickerPath(): String? {
        return STICKER_PATH
    }

    fun dip2px(dipValue: Float): Int {
        return (dipValue * density + 0.5f).toInt()
    }

    fun px2dip(pxValue: Float): Int {
        return (pxValue / density + 0.5f).toInt()
    }

    fun sp2px(spValue: Float): Int {
        return (spValue * scaleDensity + 0.5f).toInt()
    }
}