package com.foreveross.atwork.modules.file.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.foreveross.atwork.R;
import com.foreveross.atwork.modules.file.component.LocalFileItem;
import com.foreveross.atwork.modules.file.fragement.LocalFileFragment;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by ReyZhang on 2015/4/22.
 */
public class LocalFileAdapter extends BaseAdapter {

    private static final String TAG = LocalFileFragment.class.getSimpleName();
    private LocalFileType[] mFileTypes = new LocalFileType[]{LocalFileType.Image, LocalFileType.Audio, LocalFileType.Video, LocalFileType.PhoneRam};
    private String[] mFileNames;
    private TypedArray mFileIcons;
    private Context mContext;
    private String mExtSdCardPath;
    private boolean mExtSdCardStatus;
    private int[] mFilesCounts = new int[3];


    public LocalFileAdapter(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("invalid arguments on" + TAG);
        }
        mFileNames = context.getResources().getStringArray(R.array.file_name_array);
        mFileIcons = context.getResources().obtainTypedArray(R.array.file_icon_array);
        mFilesCounts = new int[mFileNames.length];
        mExtSdCardPath = getExtSdcardPath(context);
        mExtSdCardStatus = sdCardStatus();
        mContext = context;
    }

    public void setFileCounts(int[] fileCounts) {
        mFilesCounts = fileCounts;
        notifyDataSetChanged();
    }
    /**
     * 通过映射获取sdcard路径
     *
     * @param context

     * @return
     */
    public static String getExtSdcardPath(Context context) {
        String sdcardPath = "";
        String internalSdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        // 获取sdcard的路径：外置和内置
        try {
            String[] paths = (String[]) sm.getClass().getMethod("getVolumePaths").invoke(sm);
            for (String string : paths) {
                if (TextUtils.isEmpty(string)) {
                    continue;
                }
                if (string.toLowerCase().contains("sdcard0") || string.equals(internalSdPath)) {
                    continue;
                }
                sdcardPath = string;
                break;
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return sdcardPath;
    }

    @Override
    public int getCount() {
        return mFileTypes.length;
    }

    @Override
    public Object getItem(int position) {
        return mFileTypes[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getSelectItemName(int position) {
        return mFileNames[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new LocalFileItem(mContext);
        }
        LocalFileItem item = (LocalFileItem) convertView;
        item.setName(mFileNames[position], mFilesCounts[position]);
        item.setIcon(mFileIcons.getResourceId(position, R.mipmap.icon_image));
        item.needShowTopView(false);
        if (position == 3) {
            item.needShowTopView(true);
        }
        if (position == 5) {
            if (!mExtSdCardStatus || TextUtils.isEmpty(mExtSdCardPath)) {
                item.hideItem();
            }
            File file = new File(mExtSdCardPath);
            if (file.list() == null || file.list().length == 0) {
                item.hideItem();
            }
        }

        return convertView;
    }

    public boolean sdCardStatus() {
        boolean sdCardExist = false;
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            sdCardExist = true;
        }
        return sdCardExist;
    }

    /**
     * 文件类型
     * Image           图片
     * Audio           音频
     * Video           视频
     * DownloadFile    已下载文件
     * PhoneRam        手机内存文件
     * SDCard          SDCard文件
     */
    public enum LocalFileType {
        Image,
        Audio,
        Video,
        PhoneRam,
        SDCard
    }

}
