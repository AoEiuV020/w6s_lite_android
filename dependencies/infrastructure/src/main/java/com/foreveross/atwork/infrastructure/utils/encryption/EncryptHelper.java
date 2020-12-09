package com.foreveross.atwork.infrastructure.utils.encryption;

import androidx.annotation.NonNull;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.FileUtil;

import java.io.File;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by dasunsy on 2017/2/13.
 */

public class EncryptHelper {

    public static final String TAG = "ENCRYPT";

    /**
     * workplus加密因子
     */
    public static String getWorkplusEncryptedKey() {
        String resultAes = AES128ECBUtil_V0.encrypt(AtworkConfig.getDeviceId(), LoginUserInfo.getInstance().getLoginUserId(BaseApplicationLike.baseContext));
        return MD5Utils.hex(resultAes);
    }

    public static String get16WorkplusEncryptedKey() {
        return getWorkplusEncryptedKey().substring(0, 16);
    }

    public static Cipher getEncryptCipher() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        return AES128ECBUtil_V0.getCipher(get16WorkplusEncryptedKey(), Cipher.ENCRYPT_MODE);
    }


    public static Cipher getDecryptCipher() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        return AES128ECBUtil_V0.getCipher(get16WorkplusEncryptedKey(), Cipher.DECRYPT_MODE);
    }

    /**
     * 检查文件是否在加密的范畴
     * */
    public static boolean checkFileEncrypted(String path) {
        return AtworkConfig.OPEN_DISK_ENCRYPTION && path.startsWith(AtWorkDirUtils.getInstance().getUserRootDicPath()) && !path.startsWith(AtWorkDirUtils.getInstance().getTmpFilesCachePath());
    }


    /**
     * 未加密处理的源文件是否存在
     *
     * @param path 加密文件路径
     * @return 是否存在
     * */
    public static boolean isOriginalFileExist(String path) {
        String originalPath = getOriginalPath(path);
        return FileUtil.isExist(originalPath);
    }

    /**
     * 获取源文件路径
     * */
    @NonNull
    public static String getOriginalPath(String path) {
        String fileName = path.substring(path.lastIndexOf(File.separator) + 1);

        if (!EncryptCacheDisk.getInstance().revertNamePure()) {
            int indexDot = fileName.lastIndexOf(".");
            if(-1 < indexDot) {
                String prefixName = fileName.substring(0, indexDot);
                String suffix = fileName.substring(indexDot, fileName.length());
                fileName = MD5Utils.hex(prefixName) + suffix;
            }
        }

        return AtWorkDirUtils.getInstance().getTmpFilesCachePath() + fileName ;
    }

    public interface OnCheckFileEncryptedListener {
        void onFinish(String filePath);
    }

}