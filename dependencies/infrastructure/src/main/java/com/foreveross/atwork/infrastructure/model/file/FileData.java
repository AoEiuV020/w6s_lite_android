package com.foreveross.atwork.infrastructure.model.file;

import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by ReyZhang on 2015/4/22.
 */
public class FileData implements Serializable {

    private static final String KEY_PREFIX_LOCAL_FILE = "uuid_";

    // 通用图片后缀,
    public static final String mImageSuffix[] = {".bmp", ".dib", ".jfif", ".jpe", ".jpeg", ".jpg", ".png", ".tif", ".tiff", ".ico"};
    public static final String mGifSuffix[] = {".gif"};
    // 通用音频后缀， 一维数组为后缀，二维数组为打开方式
    public static final String mAudioSuffix[] = {".wav", ".wma", ".ogg", ".tta", ".mpga", ".mp2", ".mp3", ".m3u", ".m4a", ".m4b", ".m4p", ".ape" , ".aac", ".amr", ".flac"};
    // 通用视频后缀， 一维数组为后缀，二维数组为打开方式
    public static final String mVideoSuffix[] = {".asx", ".asf", ".avi", ".m4v", ".mov", ".mpa", ".mpe", ".mpeg", ".mpg", ".mpg4", ".flv", ".mp4", ".rmvb", ".rm", ".mkv", ".3gp", ".dat", ".wmv", ".wvx"};
    //通用word文档后缀
    public static final String mWordSuffix[] = {".doc", ".docx"};
    //通用excel文档后缀
    public static final String mExcelSuffix[] = {".xls", ".xlsx"};
    //通用PPT文档后缀
    public static final String mPptSuffix[] = {".ppt", ".pptx", ".pps"};
    //通用PDF文档后缀
    public static final String mPdfSuffix[] = {".pdf", ".xps", ".cbz"};
    //通用Txt文档后缀
    public static final String mTxtSuffix[] = {".txt"};
    //通用html文档后缀，包括xml
    public static final String mHtmlSuffix[] = {".html", ".htm", ".xml"};

    public static final String rarSuffix[] = {".rar", ".zip", ".tar", ".gz", ".gzip", ".cab", ".uue", ".arj", ".bz2", ".lzh", ".jar", ".iso", ".ace", ".7z", ".z"};

    public static final String mAppSuffix[] = {".apk", ".ipa", ".exe", ".gpk", ".app", ".msi"};
     //文件数据id
    public String identifier;
    //文件类型，根据后缀判断
    public FileType fileType;
    //文件名，不包括后缀
    public String title;
    //文件所在路径
    public String filePath = "";
    //文件大小
    public long size;
    //文件创建日期
    public long date;
    //文件缩略图地址
    public String thumbnailPath;
    //文件是否被选中
    public boolean isSelect = false;
    //当前是否是目录文件
    public boolean isDir = false;
    //来自
    public String from;
    //发送至
    public String to;
    //是否是下载:默认false
    public int isDownload = 0;
    //文件在服务器上的mediaId
    public String mediaId;

    public FileData() {
        identifier = UUID.randomUUID().toString();
    }

    public String getMediaId() {
        if(!StringUtils.isEmpty(mediaId)) {
            if(!mediaId.startsWith(KEY_PREFIX_LOCAL_FILE)) {
                return mediaId;
            }
        }

        return StringUtils.EMPTY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileData fileData = (FileData) o;

        if(!StringUtils.isEmpty(getMediaId()) && !StringUtils.isEmpty(fileData.getMediaId())) {
            return getMediaId().equals(fileData.getMediaId());
        }

        return filePath.equals(fileData.filePath);
    }

    @Override
    public int hashCode() {
        int result = filePath != null ? filePath.hashCode() : 0;
        result = 31 * result + getMediaId().hashCode();
        return result;
    }

    public static FileData fromPath(String path) {
        FileData fileData = new FileData();
        File file = new File(path);
        String keyId = KEY_PREFIX_LOCAL_FILE + UUID.randomUUID().toString();
        fileData.mediaId = keyId;
        fileData.filePath = path;
        fileData.title = file.getName();
        fileData.size = file.length();
        fileData.fileType = getFileType(path);

        return fileData;
    }

    public static List<FileData> fromPathList(List<String> pathList) {
        List<FileData> fileDataList = new ArrayList<>();
        for(String path : pathList) {
            String filePath = FileUtil.getInternalUsedPath(path);

            FileData fileData = FileData.fromPath(filePath);
            fileDataList.add(fileData);
        }

        return fileDataList;
    }

    public static FileData fromDropbox(Dropbox dropbox) {
        FileData fileData = new FileData();
        fileData.date = dropbox.mCreateTime;
        fileData.filePath = dropbox.mLocalPath;
        fileData.fileType = getFileTypeByExtension(dropbox.mExtension);
        fileData.from = dropbox.mOwnerName;
        fileData.identifier = dropbox.mFileId;
        fileData.isDir = dropbox.mIsDir;
        fileData.size = dropbox.mFileSize;
        fileData.title = dropbox.mFileName;
        fileData.to = "";
        return  fileData;
    }

    private static boolean contactSuffix(String[] suffixs, String suffix) {
        if(!suffix.startsWith(".")) {
            suffix = "." + suffix;
        }
        for (String value : suffixs) {
            if (value.equals(suffix.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static List<String> toPathList(List<FileData> fileDataList) {
        List<String> pathList = new ArrayList<>();
        for(FileData fileData : fileDataList) {
            pathList.add(fileData.filePath);
        }

        return pathList;
    }

    /**
     * 根据文件后缀返回文件类型
     *
     * @param fileName
     * @return
     */
    public static FileType getFileType(String fileName) {

        FileType fileType = FileType.File_Unknown;
        if (TextUtils.isEmpty(fileName) || fileName.contains(".") == false) {
            return fileType;
        }

        String suffix = fileName.substring(fileName.lastIndexOf("."));
        return getFileTypeByExtension(suffix);

    }

    public static  FileType getFileTypeByExtension(String suffix) {
        FileType fileType = FileType.File_Unknown;
        if (TextUtils.isEmpty(suffix)) {
            return fileType;
        }
        if (contactSuffix(mImageSuffix, suffix)) {
            return FileType.File_Image;
        }

        if (contactSuffix(mAudioSuffix, suffix)) {
            return FileType.File_Audio;
        }

        if(contactSuffix(rarSuffix,suffix)){
            return FileType.File_RAR;
        }

        if (contactSuffix(mVideoSuffix, suffix)) {

            return FileType.File_Video;
        }


        if (contactSuffix(mWordSuffix, suffix)) {
            return FileType.File_Word;
        }

        if (contactSuffix(mExcelSuffix, suffix)) {
            return FileType.File_Excel;
        }

        if (contactSuffix(mPptSuffix, suffix)) {
            return FileType.File_Ppt;
        }

        if (contactSuffix(mPdfSuffix, suffix)) {
            return FileType.File_Pdf;
        }

        if (contactSuffix(mTxtSuffix, suffix)) {
            return FileType.File_Txt;
        }

        if (contactSuffix(mHtmlSuffix, suffix)) {
            return FileType.File_HTML;
        }

        if (contactSuffix(mVideoSuffix, suffix)) {
            return FileType.File_Video;
        }

        if (contactSuffix(mAudioSuffix, suffix)) {
            return FileType.File_Audio;
        }

        if (contactSuffix(mImageSuffix, suffix)) {
            return FileType.File_Image;
        }

        if (contactSuffix(mGifSuffix, suffix)) {
            return FileType.File_Gif;
        }

        if (contactSuffix(mAppSuffix, suffix)) {
            return FileType.File_APK;
        }

        return fileType;
    }
    /**
     *
     * Description:将文件类型分成：文档、图片、视频三类
     */
    public static int getFileType(FileType fileType) {
        int type;
        switch (fileType){
            case File_Excel:
            case File_Pdf:
            case File_Txt:
            case File_Word:
            case File_RAR:
            case File_Ppt:
            case File_HTML:
            case File_APK:
            case File_Audio:
            case File_Unknown:
                type = 1;
                break;
            case File_Image:
            case File_Gif:
                type = 2;
                break;
            case File_Video:
                type = 3;
                break;
            default:
                type = 0;
        }
        return type;
    }

    /**
     * 从数据库中获取出来的的fileType类型进行转换
     *
     * @return
     */
    public static FileType getFileTypeFromDb(int type) {
        FileType fileType = FileType.File_Unknown;
        switch (type) {
            case 0:
                fileType = FileType.File_Image;
                break;

            case 1:
                fileType = FileType.File_Audio;
                break;

            case 2:
                fileType = FileType.File_Video;
                break;

            case 3:
                fileType = FileType.File_Word;
                break;

            case 4:
                fileType = FileType.File_Excel;
                break;

            case 5:
                fileType = FileType.File_Ppt;
                break;

            case 6:
                fileType = FileType.File_Pdf;
                break;

            case 7:
                fileType = FileType.File_Txt;
                break;

            case 8:
                fileType = FileType.File_HTML;
                break;

            case 9:
                fileType = FileType.File_Unknown;
                break;
            case 10:
                fileType=FileType.File_RAR;
                break;
            case 11:
                fileType= FileType.File_Gif;
                break;
        }

        return fileType;
    }

    public static int getFileType2DB(FileType fileType) {
        int type = 9;
        switch (fileType) {
            case File_Image:
                type = 0;
                break;

            case File_Audio:
                type = 1;
                break;

            case File_Video:
                type = 2;
                break;

            case File_Word:
                type = 3;
                break;

            case File_Excel:
                type = 4;

                break;

            case File_Ppt:
                type = 5;
                break;

            case File_Pdf:
                type = 6;
                break;

            case File_Txt:
                type = 7;
                break;

            case File_HTML:
                type = 8;
                break;

            case File_Unknown:
                type = 9;
                break;

            case File_RAR:
                type=10;
                break;

            case File_Gif:
                type = 11;
                break;
        }
        return type;
    }

    /**
     * 文件类型，根据文件后缀名判断
     * File_Image           图片
     * File_Audio           语音
     * File_Video           视频
     * File_Word            word文档
     * File_Excel           Excel表格
     * File_Ppt             PPT文档
     * File_Pdf             PDF文档
     * File_Txt             TXT文档
     * File_HTML            HTML页面
     * File_Unknown         未知类型
     */
    public enum FileType {

        File_RAR{
            @Override
            public String getFileType() {
                return "application/octet-stream";
            }

            @Override
            public String getString() {
                return "rar";
            }
        },

        File_APK {
            @Override
            public String getFileType() {
                return "application/vnd.android.package-archive";
            }

            @Override
            public String getString() {
                return "app";
            }
        },

        File_Image {
            @Override
            public String getFileType() {
                return "image/jpeg";
            }

            @Override
            public String getString() {
                return "image";
            }
        },

        File_Gif {
            @Override
            public String getFileType() {
                return "image/jpeg";
            }

            @Override
            public String getString() {
                return "gif";
            }
        },

        File_Audio {
            @Override
            public String getFileType() {
                return "audio/x-mpeg";
            }

            @Override
            public String getString() {
                return "audio";
            }
        },
        File_Video {
            @Override
            public String getFileType() {
                return "video/mpeg";
            }

            @Override
            public String getString() {
                return "video";
            }
        },
        File_Word {
            @Override
            public String getFileType() {
                return "application/msword";
            }

            @Override
            public String getString() {
                return "office";
            }
        },
        File_Excel {
            @Override
            public String getFileType() {
                return "application/msexcel";
            }

            @Override
            public String getString() {
                return "office";
            }
        },
        File_Ppt {
            @Override
            public String getFileType() {
                return "application/vnd.ms-powerpoint";
            }

            @Override
            public String getString() {
                return "office";
            }
        },
        File_Pdf {
            @Override
            public String getFileType() {
                return "application/pdf";
            }

            @Override
            public String getString() {
                return "pdf";
            }
        },
        File_Txt {
            @Override
            public String getFileType() {
                return "text/plain";
            }

            @Override
            public String getString() {
                return "office";
            }
        },
        File_HTML {
            @Override
            public String getFileType() {
                return "text/html";
            }

            @Override
            public String getString() {
                return "html";
            }
        },
        File_Unknown {
            @Override
            public String getFileType() {
                return "*/*";
            }

            @Override
            public String getString() {
                return "unknown";
            }
        };

        public abstract String getFileType();

        public abstract String getString();
    }

    public static String getFileExtension(String fileName) {

        if (TextUtils.isEmpty(fileName) || !fileName.contains(".")) {
            return "";
        }
        return  fileName.substring(fileName.lastIndexOf(".")+ 1);
    }

}
