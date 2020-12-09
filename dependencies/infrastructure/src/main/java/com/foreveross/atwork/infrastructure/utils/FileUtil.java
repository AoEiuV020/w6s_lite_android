package com.foreveross.atwork.infrastructure.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Created by lingen on 15/4/21.
 * Description:
 */
public class FileUtil {


    /**
     * 读取Assert目录下的文件
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String getFromAssets(Context context, String fileName) {
        String result = "";
        InputStreamReader inputReader = null;
        BufferedReader bufReader = null;
        try {
            inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            bufReader = new BufferedReader(inputReader);
            String line = "";

            while ((line = bufReader.readLine()) != null)
                result += line;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                if (bufReader != null) {
                    bufReader.close();
                }
                if (inputReader != null) {
                    inputReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static boolean isExistHavingContent(String path) {
        File file = new File(path);
        return file.exists() && 0 != file.length();
    }

    public static boolean isExist(String path) {
        if(StringUtils.isEmpty(path)) {
            return false;
        }

        return new File(path).exists();
    }

    public static boolean isEmptySize(String path) {
        File file = new File(path);
        return 0 == file.length();
    }

    public static byte[] readFileByRAWay(String file) throws IOException {
        return readFileByRAWay(new File(file));
    }

    public static byte[] readFileByRAWay(File file) throws IOException {
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            long longLength = f.length();
            int length = (int) longLength;
            if (length != longLength)
                throw new IOException("File size >= 2 GB");

            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }


    public static void checkParentExist(File file) {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
    }

    /**
     * @see #rename(File, String)
     * */
    public static String rename(String oldFilePath, String newName) {
        return rename(new File(oldFilePath), newName);
    }

    /**
     * 重命名文件
     * @param file
     * @param newName
     * @return 如果修改成功, 返回新的路径
     * */
    public static String rename(File file, String newName) {
        String newNamePath = file.getParent() + "/" + newName;
        boolean success = file.renameTo(new File(newNamePath));
        if(success) {
            return newNamePath;
        }

        return StringUtils.EMPTY;
    }


    public static boolean createFile(File file) {
        File newFile = file;

        checkParentExist(newFile);

        if (newFile.exists()) {
            newFile.delete();
        }

        try {
            return newFile.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean createFile(String path) {
        return createFile(new File(path));
    }

    /**
     * 读取文件
     *
     * @param path
     * @return
     */
    public static byte[] readFile(String path) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream((new File(path)));
            byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[4096];
            int len = 0;
            while ((len = inputStream.read(bytes)) > 0) {
                byteArrayOutputStream.write(bytes, 0, len);
            }
        } catch (Exception e) {
            Log.d("FILE", path);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (byteArrayOutputStream == null) {
            return new byte[0];
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static boolean saveFile(String path, byte[] content) {
        long startTime = System.currentTimeMillis();

        boolean result = false;
        if (content != null && content.length == 1) {
            throw new IllegalArgumentException("invalid image content");
        }
        File file = new File(path);
        checkParentExist(file);

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(content);
            outputStream.close();
            result = true;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        long endTime = System.currentTimeMillis();
        Log.e("test", "save time : " + (endTime - startTime));
        return result;
    }


    public static void copyFile(String sourceFilePath, String destFilePath) throws IOException {
        File sourceFile = new File(sourceFilePath);
        File destFile = new File(destFilePath);
        copyFile(sourceFile, destFile);
    }


    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }




    public static int getFileCount(String filePath) {

        File file = new File(filePath);
        return getFileCount(file);
    }

    private static int getFileCount(File file) {
        int count = 0;

        if (file.isFile()) {
            count++;
            return count;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles != null && childFiles.length != 0) {
                for (File childFile : childFiles) {
                    count += getFileCount(childFile);
                }
            }


        }

        return count;
    }

    /**
     * 删除多个文件夹
     * */
    public static Boolean deleteFile(ArrayList<String> filePath, boolean needDeleteDic) {
        Boolean flag = false;
        try{
            for (int i = 0; i < filePath.size(); i++){
                deleteFile(filePath.get(i),needDeleteDic);
                flag = true;
            }
        }catch (Exception e) {
             e.getMessage();
        }
        return flag;
    }

    /**
     * @see #deleteFile(File, boolean)
     * @param filePath
     * @param needDeleteDic 是否需要删除文件夹
     * */
    public static void deleteFile(String filePath, boolean needDeleteDic) {
        deleteFile(new File(filePath), needDeleteDic);
    }

    /**
     * 递归删除文件
     *
     * @param file
     * @param needDeleteDic 是否需要删除文件夹
     */
    public static void deleteFile(File file, boolean needDeleteDic) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                if (needDeleteDic) {
                    file.delete();
                }
                return;
            }

            for (File childFile : childFiles) {
                deleteFile(childFile, needDeleteDic);
            }

            if (needDeleteDic) {
                file.delete();
            }
        }
    }

    public static boolean delete(String path) {
        File file = new File(path);
        return file.delete();
    }

    public static String formatFromSize(long size) {
        if (size < 0) {
            return "0B";
        }
        String suffix = "B";

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
                if (size >= 1024) {
                    suffix = "GB";
                    size /= 1024;
                }
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null)
            resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    public static String getName(String path) {
        File file = new File(path);
        return file.getName();
    }

    public static long getSize(String path) {
        File file = new File(path);
        return file.length();
    }

    public static int getFileDicSize(ArrayList<String > arrayList){
        int size = 0;
        for (int i = 0; i < arrayList.size(); i++){
           size += getFileDicSize(arrayList.get(i));
        }
        return size;
    }


    public static int getFileDicSize(String filePath) {

        File file = new File(filePath);
        return getFileDicSize(file);
    }

    private static int getFileDicSize(File file) {
        int size = 0;

        if (file.isFile()) {
            size += file.length();
            return size;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles != null && childFiles.length != 0) {
                for (File childFile : childFiles) {
                    size += getFileDicSize(childFile);
                }
            }


        }

        return size;
    }

    public static String getInternalUsedPath(String path) {
        return path.replace("file:///", "/");
    }

    public static String getExternalUsedPath(String path) {
        if(!path.startsWith("file://")) {
            return "file://" + path;
        }

        return path;
    }

    public static FileUtil.FileInfo getFileInfoByChecking(String fileName, String filePathParent) {

        String shouldOriginalName = fileName;
        String shouldFilePath = filePathParent + shouldOriginalName;

        FileUtil.FileInfo fileInfo = new FileUtil.FileInfo(shouldOriginalName, shouldFilePath);


        int index = 1;
        while (FileUtil.isExist(shouldFilePath)) {

            index++;

            String handleName = shouldOriginalName;
            int indexOfDot = handleName.indexOf(".");
            if(-1 != indexOfDot) {
                String prefix = handleName.substring(0, indexOfDot);
                String subfix = handleName.substring(indexOfDot);
                handleName = prefix + ("(" + index + ")") + subfix;

            } else {
                handleName += ("(" + index + ")");
            }
            shouldFilePath = filePathParent + handleName;

            fileInfo.fileName = handleName;
            fileInfo.filePath = shouldFilePath;
        }

        return fileInfo;
    }

    public static void copyAssetsToSdCard(final Context context, final String srcPath, final String dstPath) {
        try {
            String fileNames[] = context.getAssets().list(srcPath);
            if (fileNames.length > 0) {
                File file = new File(dstPath);
                if (!file.exists()){
                    file.mkdir();
                }
                for (String fileName : fileNames) {
                    if (!srcPath.equals("")) { // assets 文件夹下的目录
                        copyAssetsToSdCard(context, srcPath + File.separator + fileName, dstPath + File.separator + fileName);
                    }
                }
            } else {
                File outFile = new File(dstPath);
                if (!outFile.getParentFile().exists()) {
                    outFile.getParentFile().mkdirs();
                }
                InputStream is = context.getAssets().open(srcPath);
                FileOutputStream fos = new FileOutputStream(outFile);
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static class FileInfo {

        public FileInfo(String fileName, String filePath) {
            this.fileName = fileName;
            this.filePath = filePath;
        }

        public String fileName;
        public String filePath;
    }

    public interface OnFileCopyListener {
        void onCopyDone();
    }

}
