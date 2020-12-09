package com.foreveross.atwork.infrastructure.utils;


import com.file.zip.ZipEntry;
import com.file.zip.ZipFile;
import com.file.zip.ZipOutputStream;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;


/**
 * Java utils 实现的Zip工具
 *
 * @author once
 */
public class ZipUtil {
    private static final int BUFF_SIZE = 1024;

    /**
     * 解压缩一个文件
     * @param fromPath
     * @param toPath
     * @return
     * @throws IOException 当解压缩过程出错时抛出
     */
    public static boolean upZipFile(String fromPath, String toPath, boolean clearToTarget) throws IOException {

        if(!FileUtil.isExist(fromPath)) {
            return false;
        }

        if(clearToTarget) {
            FileUtil.deleteFile(new File(toPath), true);
        }

        try {
            File toPathDic = new File(toPath);
            if(!toPathDic.exists()) {
                toPathDic.mkdirs();
            }

            ZipFile zipFile = new ZipFile(fromPath, "GBK");//设置压缩文件的编码方式为GBK
            Enumeration<ZipEntry> entris = zipFile.getEntries();
            ZipEntry zipEntry = null;
            File tmpFile = null;
            BufferedOutputStream bos = null;
            InputStream is = null;
            byte[] buf = new byte[BUFF_SIZE];
            int len = 0;
            while (entris.hasMoreElements()) {
                zipEntry = entris.nextElement();
                // 不进行文件夹的处理,些为特殊处理
                tmpFile = new File(toPath + zipEntry.getName());
                if (zipEntry.isDirectory()) {//当前文件为目录
                    if (!tmpFile.exists()) {
                        tmpFile.mkdirs();
                    }
                } else {
                    if (!tmpFile.exists()) {

                        File parentFile = tmpFile.getParentFile();
                        if (null != parentFile && !parentFile.exists()) {
                            tmpFile.getParentFile().mkdirs();
                        }

                        tmpFile.createNewFile();
                    }
                    is = zipFile.getInputStream(zipEntry);
                    bos = new BufferedOutputStream(new FileOutputStream(tmpFile));
                    while ((len = is.read(buf)) > 0) {
                        bos.write(buf, 0, len);
                    }
                    bos.flush();
                    bos.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }

        return true;

    }

    /**
     * 压缩文件和文件夹
     *
     * @param srcFileString 要压缩的文件或文件夹
     * @param zipFileString 压缩完成的Zip路径
     * @throws Exception
     */
    public static void zipFolder(String srcFileString, String zipFileString) throws Exception {
        File zipFile = new File(zipFileString);
        if(!zipFile.exists()) {
            zipFile.getParentFile().mkdirs();
            zipFile.createNewFile();
        }

        //创建ZIP
        ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(zipFileString));
        //创建文件
        File file = new File(srcFileString);
        //压缩
        LogUtil.e("---->"+file.getParent()+"==="+file.getAbsolutePath());
        zipFiles(file.getParent()+ File.separator, file.getName(), outZip);
        //完成和关闭
        outZip.finish();
        outZip.close();
    }

    /**
     * 压缩文件
     *
     * @param folderString
     * @param fileString
     * @param zipOutputSteam
     * @throws Exception
     */
    private static void zipFiles(String folderString, String fileString, ZipOutputStream zipOutputSteam) throws Exception {
        LogUtil.e("folderString:" + folderString + "\n" +
                "fileString:" + fileString + "\n==========================");
        if (zipOutputSteam == null)
            return;
        File file = new File(folderString + fileString);
        if (file.isFile()) {
            ZipEntry zipEntry = new ZipEntry(fileString);
            FileInputStream inputStream = new FileInputStream(file);
            zipOutputSteam.putNextEntry(zipEntry);
            int len;
            byte[] buffer = new byte[BUFF_SIZE];
            while ((len = inputStream.read(buffer)) != -1) {
                zipOutputSteam.write(buffer, 0, len);
            }
            zipOutputSteam.closeEntry();
        } else {
            //文件夹
            String fileList[] = file.list();
            //没有子文件和压缩
            if (fileList.length <= 0) {
                ZipEntry zipEntry = new ZipEntry(fileString + File.separator);
                zipOutputSteam.putNextEntry(zipEntry);
                zipOutputSteam.closeEntry();
            }
            //子文件和递归
            for (int i = 0; i < fileList.length; i++) {
                zipFiles(folderString+fileString+"/", fileList[i], zipOutputSteam);
            }
        }
    }

}
