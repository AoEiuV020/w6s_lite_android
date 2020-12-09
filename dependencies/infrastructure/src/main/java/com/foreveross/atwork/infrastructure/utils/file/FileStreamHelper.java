package com.foreveross.atwork.infrastructure.utils.file;

import android.util.Log;

import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptHelper;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.IOUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by dasunsy on 2017/2/20.
 */

public class FileStreamHelper implements FileStreamStrategy {

    public static OutputStream getOutputStream(File file) throws IOException {
        return getOutputStream(file, AtworkConfig.OPEN_DISK_ENCRYPTION);
    }

    public static OutputStream getOutputStream(File file, boolean needEncrypt) throws IOException {

        FileOutputStream fos = new FileOutputStream(file);
        if (needEncrypt) {
            try {
                return new CipherOutputStream(fos, EncryptHelper.getEncryptCipher());
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
                e.printStackTrace();
            }

        }

        return fos;
    }

    public static OutputStream getOutputStream(OutputStream outputStream) throws IOException {

        if (AtworkConfig.OPEN_DISK_ENCRYPTION) {
            try {
                return new CipherOutputStream(outputStream, EncryptHelper.getEncryptCipher());
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
                e.printStackTrace();
            }

        }

        return outputStream;
    }

    public static InputStream getInputStream(File file) throws IOException {
        return getInputStream(file, AtworkConfig.OPEN_DISK_ENCRYPTION);
    }

    public static InputStream getInputStream(File file, boolean needEncrypt) throws IOException {
        if (needEncrypt) {
            String originalPath = EncryptHelper.getOriginalPath(file.getPath());

            if (!FileUtil.isExist(originalPath)) {
                try {
                    return new CipherInputStream(new FileInputStream(file), EncryptHelper.getDecryptCipher());

                } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
                    e.printStackTrace();
                }

            } else {
                return new FileInputStream(originalPath);
            }

        }

        return new FileInputStream(file);
    }

    public static InputStream getInputStream(InputStream inputStream, File file) throws IOException {
        if (AtworkConfig.OPEN_DISK_ENCRYPTION) {
            String originalPath = EncryptHelper.getOriginalPath(file.getPath());

            if (!FileUtil.isExist(originalPath)) {
                try {
                    return new CipherInputStream(inputStream, EncryptHelper.getDecryptCipher());

                } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
                    e.printStackTrace();
                }

            } else {
                return inputStream;
            }

        }

        return inputStream;
    }

    public static void rewrite(String fileFromName, String fileToName) {
        if (AtworkConfig.OPEN_DISK_ENCRYPTION) {
            encrypt(fileFromName, fileToName, false);

        } else {
            File newFile = new File(fileToName);
            File resourceFile = new File(fileFromName);

            resourceFile.renameTo(newFile);
        }
    }


    public static void encrypt(String fileFromName, String fileToName, boolean deleteResource) {
        File toFileTemp = new File(fileToName + ".cip");
        FileInputStream fis = null;
        CipherOutputStream cos = null;
        try {
            FileUtil.createFile(toFileTemp);

            fis = new FileInputStream(fileFromName);

            FileOutputStream fos = new FileOutputStream(toFileTemp);

            cos = new CipherOutputStream(fos, EncryptHelper.getEncryptCipher());

            byte[] buffer = new byte[1024];
            int len;

            while ((len = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, len);
            }

            if (deleteResource) {
                new File(fileFromName).delete();
            }


            File newFile = new File(fileToName);

            toFileTemp.renameTo(newFile);


        } catch (NoSuchPaddingException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();

            toFileTemp.delete();

        } finally {
            IOUtil.release(fis);
            IOUtil.release(cos);
        }
    }

    public static boolean decrypt(String fileFromName, String fileToName, boolean deleteResource) {

//        LogUtil.e(EncryptHelper.TAG, "------- start decrypt -----");
//        LogUtil.e(EncryptHelper.TAG, "fileFromName - > " + fileFromName);

        boolean result = false;

        File toFileTemp = new File(fileToName + ".cip");

        FileInputStream fis = null;
        CipherInputStream cis = null;
        FileOutputStream fos = null;

        try {
            FileUtil.createFile(toFileTemp);

            fis = new FileInputStream(fileFromName);
            fos = new FileOutputStream(toFileTemp);

            cis = new CipherInputStream(fis, EncryptHelper.getDecryptCipher());

            byte[] buffer = new byte[1024];
            int len;

            while ((len = cis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }


            //fail
            if (0 == toFileTemp.length()) {
                toFileTemp.delete();

            } else {

                File newFile = new File(fileToName);
                toFileTemp.renameTo(newFile);

                if (deleteResource) {
                    new File(fileFromName).delete();
                }

                result = true;

            }


        } catch (NoSuchPaddingException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();

            result = false;

            toFileTemp.delete();

        } finally {
            IOUtil.release(fos);
            IOUtil.release(cis);
        }

//        if (result) {
//            LogUtil.e(EncryptHelper.TAG, "decrypt success - > " + fileToName);
//
//        } else {
//            LogUtil.e(EncryptHelper.TAG, "decrypt fail - > " + fileToName);
//
//        }


//        LogUtil.e(EncryptHelper.TAG, "------- end decrypt -----");


        return result;

    }

    /**
     * 读取文件
     *
     * @param path
     * @return
     */
    public static byte[] readFile(String path) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        InputStream inputStream = null;
        try {
            inputStream = FileStreamHelper.getInputStream((new File(path)));
            byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[4096];
            int len = 0;
            while ((len = inputStream.read(bytes)) > 0) {
                byteArrayOutputStream.write(bytes, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();

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
        return saveFile(AtworkConfig.OPEN_DISK_ENCRYPTION, path, content);
    }

    public static boolean saveFile(boolean needEncrypt, String path, byte[] content) {
        long startTime = System.currentTimeMillis();

        boolean result = false;
        if (content != null && content.length == 1) {
            throw new IllegalArgumentException("invalid image content");
        }
        File file = new File(path);
        FileUtil.createFile(file);

        OutputStream outputStream = null;
        try {
            outputStream = getOutputStream(file, needEncrypt);
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


}
