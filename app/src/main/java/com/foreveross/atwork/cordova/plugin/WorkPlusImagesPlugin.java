package com.foreveross.atwork.cordova.plugin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.foreverht.db.service.daoService.FileDaoService;
import com.foreverht.workplus.ui.component.dialogFragment.CommonPopSelectData;
import com.foreverht.workplus.ui.component.dialogFragment.W6sSelectDialogFragment;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil;
import com.foreveross.atwork.api.sdk.net.model.UploadFileParamsMaker;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.api.sdk.upload.MediaCenterSyncNetService;
import com.foreveross.atwork.api.sdk.upload.model.MediaInfoResponseJson;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.cordova.plugin.model.ChooseMediasRequest;
import com.foreveross.atwork.cordova.plugin.model.MediaSelectedResponseJson;
import com.foreveross.atwork.cordova.plugin.model.LongPressImageRequestJson;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.file.ImageItem;
import com.foreveross.atwork.infrastructure.model.file.MediaItem;
import com.foreveross.atwork.infrastructure.model.file.SelectMediaType;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.BitmapUtil;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.UriCompat;
import com.foreveross.atwork.infrastructure.utils.encryption.Base64Util;
import com.foreveross.atwork.infrastructure.utils.encryption.MD5Utils;
import com.foreveross.atwork.infrastructure.utils.file.FileStreamHelper;
import com.foreveross.atwork.modules.image.activity.MediaPreviewActivity;
import com.foreveross.atwork.modules.image.activity.MediaSelectActivity;
import com.foreveross.atwork.modules.qrcode.service.QrcodeManager;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.qrcode.zxing.decode.BitmapQrcodeDecoder;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.CropHelper;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.foreveross.atwork.utils.IntentUtil;
import com.foreveross.atwork.utils.MediaCenterUtils;
import com.foreveross.watermark.core.DrawWaterMark;
import com.foreveross.watermark.core.WatermarkForPicUtil;
import com.google.zxing.Result;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.foreveross.atwork.modules.image.activity.MediaSelectActivity.DATA_OPEN_FULL_MODE_SELECT;


/**
 * Created by lingen on 15/5/1.
 * 图片插件
 */
public class WorkPlusImagesPlugin extends WorkPlusCordovaPlugin {

    //拍照
    private static final String ACTION_TAKE_PHOTO = "takePhoto";
    //拍照返回并且可编辑
    private static final String ACTION_TAKE_PHOTO_WITH_EDIT = "takePhotoWithEdit";
    //图片单选 可编辑
    public static final String ACTION_SINGLE_SELECT_IMAGE_WITH_CROP = "selectImageWithEdit";
    //图片单选 不可编辑
    public static final String ACTION_SINGLE_SELECT_IMAGE_NO_CROP = "selectImage";
    //图片选择，可多选
    public static final String ACTION_MULTI_SELECT_IMAGE = "selectImages";

    //图片选择，可多选(旧接口)
    public static final String GET_IMAGES_ACTION = "getImages";
    //图片选择，选择图片后带上传，返回mediaId方式
    public static final String  ACTION_CHOOSE_IMAGES= "chooseImages";
    // 拍照后，上传照片，返回mediaId方式
    public static final String ACTION_TAKE_PICTURE="takePicture";
    //拍照后，将图片附上水印
    public static final String ACTION_ADD_WATERMARK_FOR_TAKE_PHOTO = "takePhotoAndAddWaterMark";

    //清除手机图片缓存
    private static final String ACTION_CLEAN_UP_COMPRESS_IMAGE = "cleanCompressImage";
    //图片预览
    public static final String ACTION_SHOW_IMAGE = "showImages";
    //图片保存
    private static final String ACTION_SAVE_IMAGE = "saveImages";
    //长按图片弹出选择框
    private static final String ACTION_LONG_PRESS_IMAGE = "actionForLongPressImage";

    public static final String FROM_CORDOVA_PLUG = "from_cordova_plugin";

    public static final String MULTI_SELECT_LIST = "multi_select_list";

    public static final String DATA_IMAGE_CROP = "data_image_crop";

    public static final String ACTION_POSITION = "action_pos";

    public static final String DATA_CHOOSE_IMAGE_REQUEST = "data_choose_image_request";

    public static final int IMAGES_REQUEST_CODE = 0x102;

    public static final int SINGLE_SELECT_RESULT_CODE = 0x110;

    public static final int SINGLE_SELECT_NO_EDIT_REQUEST_CODE = 0x190;

    private static final int SINGLE_SELECT_REQUEST_CODE = 0x150;

    public static final int MULTI_SELECT_RESULT_CODE = 0x120;

    private static final int MULTI_SELECT_REQUEST_CODE = 0x160;

    private static final int TAKE_PHOTO_REQUEST_CODE = 0x131;

    private static final int TAKE_PHOTO_EDIT_REQUEST_CODE = 0x132;

    private static final int TAKE_PHOTO_ADD_WATERMARK_REQUEST_CODE = 0x133;

    private static final int TAKE_PHOTO_WITH_CORP_REQUEST_CODE = 0x170;

    private static final int TAKE_PHOTO_ADD_WATERMARK_WITH_CORP_REQUEST_CODE = 0X171;

    private static final int IMAGES_CORP_REQUEST_CODE = 0x140;

    private static final int IMAGES_CORP_SELECT_REQUEST_CODE = 0x191;

    private static final int IMAGES_SHOW_REQUEST_CODE = 0x180;

    public static final String IMAGES_FROM_CORDOVA_CODE = "fromCordova";

    public static final String SHOW_WATERMARK = "showWatermark";

    private String mPhotoPath;

    private String mCropOutputPath;

    private Uri mImagePathUriOriginal;

    private String mWatermarkContent;

    private int mWatermarkFontSize;

    private String mWatermarkColor;

    private String mWatermarkAddValu= "";

    private CallbackContext mCallbackContext;

    private boolean mAutoUpload = false;

    public static final String DATA_SELECT_IMGS = "data_select_imgs";

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean execute(String action, final JSONArray jsonArr,
                           final CallbackContext callbackContext) throws JSONException {

        if(!this.requestCordovaAuth())return false;

        mCallbackContext = callbackContext;
        if (action.equals(GET_IMAGES_ACTION)) {
            try {
                AndPermission
                        .with(cordova.getActivity())
                        .runtime()
                        .permission( Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                        .onGranted(data -> {
                            Intent intent = new Intent();
                            intent.putExtra(FROM_CORDOVA_PLUG, true);
                            intent.setClass(cordova.getActivity(), MediaSelectActivity.class);
                            this.cordova.startActivityForResult(this, intent, IMAGES_REQUEST_CODE);
                        })
                        .onDenied(data -> AtworkUtil.popAuthSettingAlert(cordova.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        .start();

                return true;

            } catch (Exception e) {
                Log.e("error!", e.getMessage(), e);
                return false;
            }
        }

        if (ACTION_TAKE_PHOTO.equalsIgnoreCase(action)) {
            mAutoUpload = false;
            return takePicture(TAKE_PHOTO_REQUEST_CODE);
        }

        if (ACTION_TAKE_PHOTO_WITH_EDIT.equalsIgnoreCase(action)) {
            mAutoUpload = false;
            return takePicture(TAKE_PHOTO_WITH_CORP_REQUEST_CODE);
        }

        if (ACTION_ADD_WATERMARK_FOR_TAKE_PHOTO.equalsIgnoreCase(action)) {
            mAutoUpload = false;
            JSONObject jsonObject = jsonArr.optJSONObject(0);
            if (jsonObject == null) {
                return true;
            }
            mWatermarkColor = jsonObject.optString("color");
            mWatermarkContent = jsonObject.optString("content");
            mWatermarkFontSize = jsonObject.optInt("font_size", 14);
            mWatermarkAddValu = jsonObject.optString("add_value");
            return jsonObject.optBoolean("editable", false) ? takePicture(TAKE_PHOTO_ADD_WATERMARK_WITH_CORP_REQUEST_CODE) : takePicture(TAKE_PHOTO_ADD_WATERMARK_REQUEST_CODE);
        }

        if (ACTION_SINGLE_SELECT_IMAGE_WITH_CROP.equalsIgnoreCase(action)) {
            mAutoUpload = false;
            return startSelectSingleImageActivity(true, SINGLE_SELECT_REQUEST_CODE, null);
        }

        if (ACTION_SINGLE_SELECT_IMAGE_NO_CROP.equalsIgnoreCase(action)) {
            mAutoUpload = false;
            return startSelectSingleImageActivity(false, SINGLE_SELECT_NO_EDIT_REQUEST_CODE, null);
        }

        if (ACTION_MULTI_SELECT_IMAGE.equalsIgnoreCase(action)) {


            mAutoUpload = false;
            ChooseMediasRequest request = NetGsonHelper.fromCordovaJson(jsonArr, ChooseMediasRequest.class);
            if(null == request) {
                request = new ChooseMediasRequest();
            }

            if (!request.checkLegal()) {
                mCallbackContext.error();
                return true;
            }

            request.mMultiple = true;
            request.mFromCordova = true;

            startSelectMultiImagesActivity(jsonArr, request);

            return true;
        }

        if (ACTION_CLEAN_UP_COMPRESS_IMAGE.equalsIgnoreCase(action)) {
            this.cordova.getThreadPool().execute(() -> {
                deleteFile(new File(AtWorkDirUtils.getInstance().getCompressImageDir(LoginUserInfo.getInstance().getLoginUserUserName(cordova.getActivity()))));
                callbackContext.success("success");
            });

            return true;
        }


        if (ACTION_SHOW_IMAGE.equalsIgnoreCase(action)) {
            try {
//                String str="{\"urls\": [\"http://c.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=08108ba5718da9774e7a8e2f8561d42f/c83d70cf3bc79f3dba5caca6b8a1cd11738b29e2.jpg\",\"http://a.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=e385faa7a38b87d65017a31b3238040e/fd039245d688d43fd70dd9647f1ed21b0ff43bfb.jpg\"],\"mediaIds\": [\"\",\"\"]}";
                JSONArray jsonArray = new JSONArray(jsonArr.toString());
                JSONObject jsonObject = jsonArray.optJSONObject(0);
                int pos = jsonObject.optInt("position");
                boolean showWatermark = jsonObject.optBoolean("show_watermark", false);
                JSONArray urls = jsonObject.optJSONArray("urls");
                JSONArray medialds = jsonObject.optJSONArray("mediaIds");
                List<String> imgUrlList = new ArrayList<>();
                if (urls != null) {
                    for (int i = 0; i < urls.length(); i++) {
                        if (urls.getString(i).equals("")) {
                            continue;
                        }
                        imgUrlList.add(urls.getString(i));
                    }
                }

                if (medialds != null) {
                    for (int i = 0; i < medialds.length(); i++) {
                        if (medialds.getString(i).equals("")) {
                            continue;
                        }
                        MediaCenterSyncNetService mediaCenterSyncNetService = MediaCenterSyncNetService.getInstance();
                        imgUrlList.add(mediaCenterSyncNetService.getMediaUrl(BaseApplicationLike.baseContext, medialds.getString(i)));
                    }
                }

                if (imgUrlList.size() == 0) {
                    JSONObject jsonError = new JSONObject();
                    jsonError.put("message", "预览图片为空");
                    callbackContext.error(jsonError);
                    return false;
                }

                List<MediaItem> cordovaImageList = new ArrayList<>();
                for (int i = 0; i < imgUrlList.size(); i++) {
                    MediaItem imageItem = new ImageItem();
                    imageItem.filePath = imgUrlList.get(i);
                    cordovaImageList.add(imageItem);
                }
                AndPermission
                        .with(cordova.getActivity())
                        .runtime()
                        .permission( Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                        .onGranted(data -> {
                            Intent intent = MediaPreviewActivity.getImagePreviewIntent(BaseApplicationLike.baseContext, MediaPreviewActivity.FromAction.IMAGE_SELECT);
                            intent.putExtra(WorkPlusImagesPlugin.IMAGES_FROM_CORDOVA_CODE, true);
                            intent.putExtra(WorkPlusImagesPlugin.ACTION_SHOW_IMAGE, (Serializable) cordovaImageList);
                            intent.putExtra(WorkPlusImagesPlugin.ACTION_POSITION, pos);
                            intent.putExtra(WorkPlusImagesPlugin.SHOW_WATERMARK, showWatermark);
                            this.cordova.startActivityForResult(this, intent, IMAGES_SHOW_REQUEST_CODE);
                        })
                        .onDenied(data -> AtworkUtil.popAuthSettingAlert(cordova.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        .start();

                return true;
            } catch (Exception e) {
                JSONObject jsonError = new JSONObject();
                jsonError.put("message", "预览图片为空");
                callbackContext.error(jsonError);
                return false;
            }

        }

        if (ACTION_SAVE_IMAGE.equalsIgnoreCase(action)) {
//            String str="{\"urls\": \"http://c.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=08108ba5718da9774e7a8e2f8561d42f/c83d70cf3bc79f3dba5caca6b8a1cd11738b29e2.jpg\"}";
            if (jsonArr.length() == 0) {
                JSONObject jsonError = new JSONObject();
                jsonError.put("message", "保存图片为空");
                callbackContext.error(jsonError);
                return false;
            }
            JSONArray jsonArray = new JSONArray(jsonArr.toString());
            JSONObject jsonObject = jsonArray.optJSONObject(0);
            String mediald = jsonObject.optString("mediaId");
            String url = jsonObject.optString("url");
            String imageData = jsonObject.optString("imageData");

            if (!StringUtils.isEmpty(mediald)) {
                ImageCacheHelper.loadImageByMediaId(mediald, new ImageCacheHelper.ImageLoadedListener() {
                    @Override
                    public void onImageLoadedComplete(Bitmap bitmap) {
                        saveIMG(bitmap, callbackContext);
                    }

                    @Override
                    public void onImageLoadedFail() {
                        try {
                            JSONObject jsonError = new JSONObject();
                            jsonError.put("message", "该图片资源不存在");
                            callbackContext.error(jsonError);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

                return true;

            } else if (!StringUtils.isEmpty(url)) {
                ImageCacheHelper.loadImageByUrl(url, new ImageCacheHelper.ImageLoadedListener() {
                    @Override
                    public void onImageLoadedComplete(Bitmap bitmap) {
                        saveIMG(bitmap, callbackContext);
                    }

                    @Override
                    public void onImageLoadedFail() {
                        try {
                            JSONObject jsonError = new JSONObject();
                            jsonError.put("message", "该图片资源不存在");
                            callbackContext.error(jsonError);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                return true;

            } else if(!StringUtils.isEmpty(imageData)) {
                saveImage(cordova.getActivity(), BitmapUtil.strToBitmap(imageData));
                return true;
            }
            return false;
        }


        if (ACTION_LONG_PRESS_IMAGE.equalsIgnoreCase(action)) {
            LongPressImageRequestJson requestJson = NetGsonHelper.fromCordovaJson(jsonArr.toString(), LongPressImageRequestJson.class);
            Activity activity = cordova.getActivity();

            if (null != requestJson) {
                byte[] bmpByte = Base64Util.decode(requestJson.mImageDataBase64);


                new AsyncTask<Void, Void, Result>() {
                    @Override
                    protected Result doInBackground(Void... params) {

                        Result result = null;

                        //转化成合适二维码解码的尺寸
                        byte[] compressBye = ImageShowHelper.compressImageForQrcodeRecognize(bmpByte);
                        Bitmap judgeBitmap = BitmapUtil.Bytes2Bitmap(compressBye);
                        //下面方法尝试去获取是否有二维码result
                        result = new BitmapQrcodeDecoder(activity).getRawResult(judgeBitmap);

                        return result;
                    }

                    @Override
                    protected void onPostExecute(Result result) {
                        ArrayList<String> longClickList = new ArrayList<>();
                        if (null != result) {
                            longClickList.add(activity.getResources().getString(R.string.qrcode_recognition));

                        }

                        if (needDownload()) {
                            longClickList.add(activity.getResources().getString(R.string.save_to_mobile));
                        }

                        if (!ListUtil.isEmpty(longClickList)) {

                            if(cordova.getActivity() instanceof FragmentActivity){
                                FragmentActivity fragmentActivity = (FragmentActivity) cordova.getActivity();
                                FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
                                W6sSelectDialogFragment W6sSelectDialogFragment = new W6sSelectDialogFragment();
                                W6sSelectDialogFragment.setData(new CommonPopSelectData(longClickList, null))
                                        .setDialogWidth(148)
                                        .setTextContentCenter(true)
                                        .setOnClickItemListener((position, item) -> {
                                            handleClickEvent(activity, result, BitmapUtil.Bytes2Bitmap(bmpByte), item);
                                        })
                                        .show(fragmentManager, "VIEW_IMAGE_DIALOG");
                            }else{
                                return;
                            }
                        }

                    }

                    private boolean needDownload() {
                        if(AtworkConfig.ENCRYPT_CONFIG.isImageSaveIgnoringEncrypt()) {
                            return true;
                        }

                        return !AtworkConfig.OPEN_DISK_ENCRYPTION;
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
            return true;
        }
        if (action.equalsIgnoreCase(ACTION_CHOOSE_IMAGES)) {
            return doCommandChooseImages(jsonArr, true);
        }

        if (action.equalsIgnoreCase(ACTION_TAKE_PICTURE)) {
            return doCommandTakePicture(jsonArr);
        }
        return super.execute(action, jsonArr, callbackContext);
    }

    @SuppressLint("StaticFieldLeak")
    private void handleClickEvent(Activity activity, Result result, final Bitmap bitmap, String item) {
            if (activity.getResources().getString(R.string.save_to_mobile).equals(item)) {
                saveImage(activity, bitmap);

            } else if (activity.getResources().getString(R.string.qrcode_recognition).equals(item)) {

                if (null != result) {

                    String resultText = result.getText();

                    final ProgressDialogHelper progressDialog = new ProgressDialogHelper(activity);
                    progressDialog.show(activity.getResources().getString(R.string.loading));

                    new Handler().postDelayed(() -> {

                        progressDialog.dismiss();

                        realHandleQrcodeResult(activity, resultText);

                    }, 1000L);
                }
            }
    }


    @SuppressLint("StaticFieldLeak")
    private void saveImage(Activity activity, Bitmap bitmap) {
        AndPermission
                .with(cordova.getActivity())
                .runtime()
                .permission( Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                .onGranted(data -> {
                    ProgressDialogHelper mProgressDialogHelper = new ProgressDialogHelper(activity);

                    //保存图片
                    new AsyncTask<Void, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Void... params) {
                            mProgressDialogHelper.show();

                            String savedPath = ImageShowHelper.saveImageToGalleryAndGetPath(activity, BitmapUtil.Bitmap2Bytes(bitmap), null, false);
                            if(!StringUtils.isEmpty(savedPath)) {
                                FileDaoService.getInstance().insertRecentFile(savedPath);

                            }
                            return !StringUtils.isEmpty(savedPath);

                        }

                        @Override
                        protected void onPostExecute(Boolean succeed) {

                            mProgressDialogHelper.dismiss();

                            if (succeed) {
                                int galleryIndex = AtWorkDirUtils.getInstance().getGalleryDir(LoginUserInfo.getInstance().getLoginUserUserName(activity)).indexOf(AtworkConfig.APP_FOLDER);
                                AtworkToast.showResToast(R.string.save_image_to_mobile_success, AtWorkDirUtils.getInstance().getGalleryDir(LoginUserInfo.getInstance().getLoginUserUserName(activity)).substring(galleryIndex));
                            } else {
                                AtworkToast.showResToast(R.string.save_image_to_mobile_fail);

                            }

                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                })
                .onDenied(data -> AtworkUtil.popAuthSettingAlert(cordova.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .start();

    }

    private void realHandleQrcodeResult(Activity activity, String resultText) {
        QrcodeManager.getInstance().handleSelfProtocol(activity, resultText);


    }

    /**
     * 保存图片到相册
     */
    private void saveIMG(Bitmap bitmap, CallbackContext callbackContext) {
        try {
            if (bitmap != null) {
                byte[] content = BitmapUtil.Bitmap2Bytes(bitmap);
                if (content != null && content.length != 0) {
                    String savedPath = ImageShowHelper.saveImageToGalleryAndGetPath(BaseApplicationLike.baseContext, content, null, false);
                    boolean result = !StringUtils.isEmpty(savedPath);

                    if(result) {
                        FileDaoService.getInstance().insertRecentFile(savedPath);

                    }

                    if (result) {
                        JSONObject json = new JSONObject();
                        json.put("message", "保存成功");
                        callbackContext.success(json);
                    } else {
                        JSONObject jsonError = new JSONObject();
                        jsonError.put("message", "保存失败");
                        callbackContext.error(jsonError);
                    }
                }
            } else {
                JSONObject jsonError = new JSONObject();
                jsonError.put("message", "保存图片为空");
                callbackContext.error(jsonError);
            }
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == IMAGES_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            final ArrayList<String> pathLists = (ArrayList<String>) intent.getSerializableExtra("imagePaths");
            if (pathLists == null) {
                try {
                    mCallbackContext.error(new JSONObject().put("msg", "image not found"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            JSONArray jsonArray = new JSONArray();
            for (String path : pathLists) {
                JSONObject json = new JSONObject();
                try {
                    json.put("imageURI", path);
                    jsonArray.put(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mCallbackContext.success(jsonArray);
        }


        if (requestCode == MULTI_SELECT_REQUEST_CODE && resultCode == MULTI_SELECT_RESULT_CODE) {
            List<MediaSelectedResponseJson> mediaSelectedList = intent.getParcelableArrayListExtra(DATA_SELECT_IMGS);

            if (mediaSelectedList.isEmpty()) {
                try {
                    mCallbackContext.error(new JSONObject().put("message", "image not found"));
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (mAutoUpload) {
                asyncUploadImage(mediaSelectedList, jsons -> {
                    mCallbackContext.success(jsons);
                });
                return;
            }

            mCallbackContext.success(returnMultipleImage(mediaSelectedList));
        }

        if (requestCode == SINGLE_SELECT_NO_EDIT_REQUEST_CODE && resultCode == SINGLE_SELECT_RESULT_CODE) {
            try {
                MediaSelectedResponseJson responseJson = intent.getParcelableExtra(DATA_SELECT_IMGS);
                if (!TextUtils.isEmpty(responseJson.key)) {
                    if (mAutoUpload) {
                        List<MediaSelectedResponseJson> list = new ArrayList<>();
                        list.add(responseJson);
                        asyncUploadImage(list, json -> mCallbackContext.success(json.get(0)));
                        return;
                    }
                    mCallbackContext.success(responseJson);
                }

            } catch (Exception e) {
                mCallbackContext.error(e.getMessage());
            }
        }


        if (requestCode == SINGLE_SELECT_REQUEST_CODE && resultCode == SINGLE_SELECT_RESULT_CODE) {
            try {
                MediaSelectedResponseJson responseJson = intent.getParcelableExtra(DATA_SELECT_IMGS);
                String path = responseJson.key;
                if (StringUtils.isEmpty(path)) {
                    return;
                }
                File file = new File(path);
                if (file != null) {
                    Uri uri = UriCompat.getFileUriCompat(cordova.getActivity(), file);
                    cropImage(uri, IMAGES_CORP_SELECT_REQUEST_CODE);
                }
            } catch (Exception e) {
                mCallbackContext.error(e.getMessage());
            }
        }


        if (requestCode == TAKE_PHOTO_REQUEST_CODE) {
            if (Activity.RESULT_OK == resultCode) {
                onCameraResult();

            } else {
                mCallbackContext.error();

            }
        }

        if (requestCode == TAKE_PHOTO_ADD_WATERMARK_REQUEST_CODE) {
            if (Activity.RESULT_OK == resultCode) {
                asyncAddWatermarkToPic(mPhotoPath, false, path -> onCropImageResult(path));

            } else {
                mCallbackContext.error();

            }
        }

        if (requestCode == TAKE_PHOTO_EDIT_REQUEST_CODE) {
            if (Activity.RESULT_OK == resultCode) {
                onCameraEditResult(intent);

            } else {
                mCallbackContext.error();

            }
        }

        if ((requestCode == TAKE_PHOTO_WITH_CORP_REQUEST_CODE ||requestCode == TAKE_PHOTO_ADD_WATERMARK_WITH_CORP_REQUEST_CODE)) {

            if (Activity.RESULT_OK == resultCode) {
                onCameraResultWithCorp(intent);

            } else {
                mCallbackContext.error();
            }
        }

        if (requestCode == IMAGES_CORP_REQUEST_CODE) {
            onCorpResult(intent);
        }

        if (requestCode == IMAGES_CORP_SELECT_REQUEST_CODE) {
            onCorpResult(intent);
        }

    }

    private void onCorpResult(Intent intent) {
        if (null == intent) {
            return;
        }

        CropHelper.makeCropIntentCompatible(intent, mCropOutputPath, mImagePathUriOriginal);
        String path = CropHelper.getCropFilePath(cordova.getActivity(), intent);

        if (new File(path).exists()) {
            if (!TextUtils.isEmpty(mWatermarkContent)) {
                asyncAddWatermarkToPic(path, true, path1 -> onCropImageResult(path1));
                return;
            }
            onCropImageResult(path);
        } else {
            JSONObject json = new JSONObject();
            try {
                json.put("error", "cannot get the image");
            } catch (JSONException e) {
                mCallbackContext.error(e.getMessage());
                e.printStackTrace();
            }
            mCallbackContext.error(json);
        }
    }

    private void onCropImageResult(String path) {
        BitmapUtil.ImageInfo imageInfo = BitmapUtil.getImageInfo(path);

        MediaSelectedResponseJson mediaSelectedResponseJson = new MediaSelectedResponseJson();
        mediaSelectedResponseJson.key = path;
        mediaSelectedResponseJson.imageURL = path;
        mediaSelectedResponseJson.imageInfo = new MediaSelectedResponseJson.ImageInfo();
        mediaSelectedResponseJson.imageInfo.height = imageInfo.height;
        mediaSelectedResponseJson.imageInfo.width = imageInfo.width;
        mediaSelectedResponseJson.imageInfo.size = imageInfo.size;
        if (!TextUtils.isEmpty(path)) {
            mediaSelectedResponseJson.name = new File(path).getName();
        }


        if (mAutoUpload) {
            List<MediaSelectedResponseJson> list = new ArrayList<>();
            list.add(mediaSelectedResponseJson);
            asyncUploadImage(list, json -> {
                mCallbackContext.success(json.get(0));
            });
            return;
        }

        mCallbackContext.success(mediaSelectedResponseJson);
    }

    private void onCameraResult() {
        List<MediaItem> cameraImageList = new ArrayList<>();
        MediaItem imageItem = new ImageItem();
        imageItem.filePath = mPhotoPath;
        cameraImageList.add(imageItem);


        Intent intent = MediaPreviewActivity.getImagePreviewIntent(cordova.getActivity(), MediaPreviewActivity.FromAction.CAMERA);
        intent.putExtra(MediaPreviewActivity.INTENT_IMAGE_SELECTED_LIST, (Serializable) cameraImageList);
        this.cordova.startActivityForResult(this, intent, TAKE_PHOTO_EDIT_REQUEST_CODE);
    }

    private void onCameraEditResult(Intent data) {
        try {
//                String photoPath = intent.getStringExtra(PhotoPreviewActivity.PREVIEW_PHOTO_PATH_INTENT);
            String path = data.getStringExtra(MediaPreviewActivity.DATA_IMG_PATH);
            if(StringUtils.isEmpty(path)) {
                path = mPhotoPath;
            }

            File file = new File(path);

            LogUtil.d("take photo done, the path = " + mPhotoPath);
            LogUtil.d("take photo done, the edit path = " + path);
            if (file.exists()) {
                //考虑进行旋转图片
                if (FileUtil.isExist(path)) {
                    Bitmap bitmap = ImageShowHelper.getRotateImageBitmap(path, false);
                    byte[] content = BitmapUtil.Bitmap2Bytes(bitmap);
                    FileStreamHelper.saveFile(path, content);
                }
                String compressPath = ImageShowHelper.imagePluginCompress(cordova.getActivity(), path);
                BitmapUtil.ImageInfo imageInfo = BitmapUtil.getImageInfo(path);

                MediaSelectedResponseJson mediaSelectedResponseJson = new MediaSelectedResponseJson();
                mediaSelectedResponseJson.key = path;
                mediaSelectedResponseJson.imageURL = compressPath;
                mediaSelectedResponseJson.imageInfo = new MediaSelectedResponseJson.ImageInfo();
                mediaSelectedResponseJson.imageInfo.height = imageInfo.height;
                mediaSelectedResponseJson.imageInfo.width = imageInfo.width;
                mediaSelectedResponseJson.imageInfo.size = imageInfo.size;
                if (!TextUtils.isEmpty(path)) {
                    mediaSelectedResponseJson.name = new File(path).getName();
                }


                if (mAutoUpload) {
                    List<MediaSelectedResponseJson> list = new ArrayList<>();
                    list.add(mediaSelectedResponseJson);
                    asyncUploadImage(list, json -> {
                        mCallbackContext.success(json.get(0));
                    });
                    return;
                }
                mCallbackContext.success(mediaSelectedResponseJson);
            }
        } catch (Exception e) {
                mCallbackContext.error(" fail by taking photo");
        }
    }

    private void onCameraResultWithCorp(Intent intent) {
        try {
            File file = new File(mPhotoPath);
            if (file.exists()) {
                Uri uri = UriCompat.getFileUriCompat(cordova.getActivity(), file);
                cropImage(uri, IMAGES_CORP_REQUEST_CODE);
            }
        } catch (Exception e) {
            LogUtil.i("test", e.getMessage());
            mCallbackContext.error(" fail by taking photo");
        }
    }

    private void cropImage(Uri uri, int code) {
        mImagePathUriOriginal = uri;

        Intent cropIntent = IntentUtil.getCropImageIntent(cordova.getActivity(), uri);
        Uri outputUri = cropIntent.getParcelableExtra("output");
        mCropOutputPath = outputUri.getPath();

        this.cordova.startActivityForResult(this, cropIntent, code);
    }

    public void deleteFile(File oldPath) {
        if (!oldPath.isDirectory()) {
            oldPath.delete();
            return;
        }
        File[] files = oldPath.listFiles();
        for (File file : files) {
            deleteFile(file);
        }
    }

    /**
     * 选择图片接口，参数决定单选多选，是否进行剪裁，返回值带有mediaId
     * @param jsonArray
     * @return
     * @throws JSONException
     */
    private boolean doCommandChooseImages(JSONArray jsonArray, boolean autoUpload) throws JSONException {
        mAutoUpload = autoUpload;
        ChooseMediasRequest request = NetGsonHelper.fromCordovaJson(jsonArray, ChooseMediasRequest.class);
        if(null == request) {
            request = new ChooseMediasRequest();
        }

        if (!request.checkLegal()) {
            mCallbackContext.error();
            return true;
        }

        request.mFromCordova = true;

        if (!request.isSingleType()) {
            startSelectMultiImagesActivity(jsonArray, request);
            return true;
        }

        return startSelectSingleImageActivity(request.mEditable, request.mEditable ? SINGLE_SELECT_REQUEST_CODE : SINGLE_SELECT_NO_EDIT_REQUEST_CODE, request);
    }

    /**
     * 拍照接口，参数决定是否进行剪裁，返回值带有mediaId
     * @param jsonArray
     * @return
     */
    private boolean doCommandTakePicture(JSONArray jsonArray) {
        mAutoUpload = true;
        JSONObject jsonObject = jsonArray.optJSONObject(0);
        boolean editable = jsonObject.optBoolean("editable");
        return takePicture(editable ? TAKE_PHOTO_WITH_CORP_REQUEST_CODE : TAKE_PHOTO_REQUEST_CODE);
    }

    private boolean takePicture(int requestCode) {
        if(VoipHelper.isHandingVideoVoipCall()) {
            AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);
            return true;
        }

        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(cordova.getActivity() , new String[]{Manifest.permission.CAMERA}, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                try {
                    mPhotoPath = IntentUtil.camera(cordova.getActivity(), WorkPlusImagesPlugin.this, requestCode);
                    ImageShowHelper.insertImage(cordova.getActivity().getContentResolver(), new File(mPhotoPath), mPhotoPath, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    JSONObject jsonError = new JSONObject();
                    try {
                        jsonError.put("error", e.getMessage());
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }
                    mCallbackContext.error(jsonError);
                }

            }

            @Override
            public void onDenied(String permission) {
                AtworkUtil.popAuthSettingAlert(cordova.getActivity(), permission);
            }
        });
        return true;
    }

    private void startSelectMultiImagesActivity(JSONArray jsonArr, @Nullable ChooseMediasRequest chooseMediasRequest) throws JSONException {
        List<MediaItem> cordovaImageList = getImgKeys(jsonArr);
        AndPermission
                .with(cordova.getActivity())
                .runtime()
                .permission( Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                .onGranted(data -> {
                    Intent intent = MediaSelectActivity.getIntent(BaseApplicationLike.baseContext);
                    intent.putExtra(ACTION_MULTI_SELECT_IMAGE, true);
                    intent.putExtra(DATA_OPEN_FULL_MODE_SELECT, false);
                    if (cordovaImageList.size() != 0) {
                        intent.putExtra(MULTI_SELECT_LIST, (Serializable) cordovaImageList);
                    }

                    if(null != chooseMediasRequest) {
                        intent.putExtra(DATA_CHOOSE_IMAGE_REQUEST, chooseMediasRequest);
                    }

                    if(null != chooseMediasRequest && 1 == chooseMediasRequest.mMedias) {
                        intent.putExtra(MediaSelectActivity.DATA_SELECT_MEDIA_TYPE_ADD, (Serializable) ListUtil.makeSingleList(SelectMediaType.VIDEO));
                    }

                    this.cordova.startActivityForResult(this, intent, MULTI_SELECT_REQUEST_CODE);
                })
                .onDenied(data -> AtworkUtil.popAuthSettingAlert(cordova.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .start();
    }

    @NonNull
    private List<MediaItem> getImgKeys(JSONArray jsonArr) throws JSONException {
        List<String> imgUrlList = new ArrayList<>();
        if (!StringUtils.isEmpty(jsonArr.toString()) && !jsonArr.toString().equals("[]")) {
            JSONArray jsonArray = jsonArr;
            JSONObject jsonObject = jsonArray.optJSONObject(0);
            JSONArray urls = jsonObject.optJSONArray("imageKeys");
            if (urls != null) {
                for (int i = 0; i < urls.length(); i++) {
                    String imgUrl = urls.getString(i);
                    if (!StringUtils.isEmpty(imgUrl) && !imgUrlList.contains(imgUrl)) {
                        imgUrlList.add(imgUrl);
                    }
                }
            }

        }
        List<MediaItem> cordovaImageList = new ArrayList<>();
        if (!ListUtil.isEmpty(imgUrlList)) {
            for (int i = 0; i < imgUrlList.size(); i++) {
                MediaItem imageItem = new ImageItem();
                imageItem.filePath = imgUrlList.get(i);
                cordovaImageList.add(imageItem);
            }
        }
        return cordovaImageList;
    }

    /**
     *
     * @param crop          是否进行裁剪
     * @param requestCode   请求requestCode
     * @param chooseMediasRequest
     * @return
     */
    private boolean startSelectSingleImageActivity(boolean crop, int requestCode, @Nullable ChooseMediasRequest chooseMediasRequest) {
        AndPermission
                .with(cordova.getActivity())
                .runtime()
                .permission( Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                .onGranted(data -> {
                    Intent intent = MediaSelectActivity.getIntent(BaseApplicationLike.baseContext);
                    intent.putExtra(ACTION_SINGLE_SELECT_IMAGE_WITH_CROP, true);
                    intent.putExtra(DATA_OPEN_FULL_MODE_SELECT, false);
                    intent.putExtra(DATA_IMAGE_CROP, crop);
                    if(null != chooseMediasRequest) {
                        intent.putExtra(DATA_CHOOSE_IMAGE_REQUEST, chooseMediasRequest);
                    }

                    if(null != chooseMediasRequest && 1 == chooseMediasRequest.mMedias) {
                        intent.putExtra(MediaSelectActivity.DATA_SELECT_MEDIA_TYPE_ADD, (Serializable) ListUtil.makeSingleList(SelectMediaType.VIDEO));
                    }

                    this.cordova.startActivityForResult(this, intent, requestCode);
                })
                .onDenied(data -> AtworkUtil.popAuthSettingAlert(cordova.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .start();

        return true;
    }

    private void asyncUploadImage(List<MediaSelectedResponseJson> jsons, ImageUploadedListener listener) {
        ProgressDialogHelper dialogHelper = new ProgressDialogHelper(cordova.getActivity());
        dialogHelper.show(false);
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(() -> {
            List<MediaSelectedResponseJson> list = new ArrayList<>();
            for (MediaSelectedResponseJson json : jsons) {


                HttpResult httpResult = syncUploadImage(json.getFilePath(), json.key);
                dialogHelper.dismiss();
                String mediaId = "";
                if (httpResult.isNetSuccess()) {
                    BasicResponseJSON basicResponseJSON = httpResult.resultResponse;
                    mediaId = MediaCenterNetManager.getMediaId(basicResponseJSON);

                }
                json.mediaId = mediaId;
                list.add(json);

            }
            listener.onImageUploaded(list);

        });
    }

    private void asyncAddWatermarkToPic(String path, boolean crop, OnWatermarkAddedListener listener) {
        ProgressDialogHelper dialogHelper = new ProgressDialogHelper(cordova.getActivity());
        dialogHelper.show();
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(() -> {
            File file = new File(path);
            if (!file.exists()) {
                try {
                    mCallbackContext.error(new JSONObject().put("message", "image not found"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return;
            }

            String compressPath = ImageShowHelper.imagePluginCompress(cordova.getActivity(), path);

            int color = -1;
            try {
                color = Color.parseColor(mWatermarkColor);
            } catch (Exception e) {
                e.printStackTrace();
            }
            DrawWaterMark drawWaterMark = new DrawWaterMark(mWatermarkContent, "", -1, color, mWatermarkFontSize, -1, 0, mWatermarkAddValu);
            Bitmap bitmap = WatermarkForPicUtil.drawTextToLeftBottom(cordova.getActivity(), BitmapFactory.decodeFile(compressPath), mWatermarkContent, drawWaterMark, 20, 20);
            if (bitmap == null) {
                mCallbackContext.error("");
                return;
            }
            MediaCenterUtils.saveBitmapToFile(compressPath, bitmap);
            dialogHelper.dismiss();
            listener.onAddWatermarkComplete(compressPath);
        });
    }

    private HttpResult syncUploadImage(String imagePath, String imageId) {
        String checkSum = MD5Utils.getDigest(imagePath);
        HttpResult httpResult = MediaCenterSyncNetService.getInstance().getMediaInfo(cordova.getActivity(), checkSum, "digest");

        //说明服务端有该文件
        if(httpResult.isRequestSuccess()) {
            MediaInfoResponseJson mediaInfoResponseJson = (MediaInfoResponseJson) httpResult.resultResponse;
            if (mediaInfoResponseJson.isLegal()) {

                //+1s 文件
                MediaCenterNetManager.linkMedia(cordova.getActivity(), mediaInfoResponseJson.mediaInfo.id, -1, null);

                return httpResult;


            }

        }

        UploadFileParamsMaker uploadFileParamsMaker = UploadFileParamsMaker.newRequest()
                .setMsgId(imageId)
                .setFileDigest(checkSum)
                .setFilePath(imagePath)
                .setExpireLimit(-1);

        return MediaCenterHttpURLConnectionUtil.getInstance().uploadImageFile(cordova.getActivity(), uploadFileParamsMaker);
    }

    private JSONArray returnMultipleImage(List<MediaSelectedResponseJson> imgSelectedList) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < imgSelectedList.size(); i++) {
            try {
                JSONObject json = new JSONObject(JsonUtil.toJson(imgSelectedList.get(i)));
                jsonArray.put(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    private interface ImageUploadedListener {
        void onImageUploaded(List<MediaSelectedResponseJson> jsons);
    }

    private interface OnWatermarkAddedListener {
        void onAddWatermarkComplete(String path);
    }

}
