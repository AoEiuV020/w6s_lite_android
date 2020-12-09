package com.foreveross.atwork.cordova.plugin;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Nullable;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil;
import com.foreveross.atwork.api.sdk.net.model.UploadFileParamsMaker;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.api.sdk.upload.MediaCenterSyncNetService;
import com.foreveross.atwork.api.sdk.upload.model.MediaInfoResponseJson;
import com.foreveross.atwork.api.sdk.upload.model.MediaUploadResultResponseJson;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.cordova.plugin.model.ChooseFilesRequest;
import com.foreveross.atwork.cordova.plugin.model.ChooseFilesResponse;
import com.foreveross.atwork.cordova.plugin.model.GetUserFilePathRequest;
import com.foreveross.atwork.cordova.plugin.model.IsFileExistRequest;
import com.foreveross.atwork.cordova.plugin.model.IsFileExistResponse;
import com.foreveross.atwork.cordova.plugin.model.OpenFileDetailRequest;
import com.foreveross.atwork.cordova.plugin.model.ViewFileRequest;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.FileProviderUtil;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.UriCompat;
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk;
import com.foreveross.atwork.infrastructure.utils.encryption.MD5Utils;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.file.activity.FileSelectActivity;
import com.foreveross.atwork.modules.file.fragement.CommonFileStatusFragment;
import com.foreveross.atwork.modules.image.activity.ImageSwitchInChatActivity;
import com.foreveross.atwork.modules.image.fragment.ImageSwitchFragment;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.OfficeHelper;
import com.foreveross.atwork.utils.ToastHelper;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SelectFilePlugin extends WorkPlusCordovaPlugin {

    public static final String SELECT_SINGLE_FILE = "selectFile";

    public static final String SELECT_MULTI_FILES = "selectFiles";

    public static final String DATA_CHOOSE_FILES_REQUEST = "data_choose_files_request";

    private static final String CHOOSE_FILES = "chooseFiles";

    private final static String OPEN_EMAIL_ATTACHMENT = "openEmailAttachment";

    private final static String GET_EMAIL_ATTACHMENT_DIR = "getEmailAttachmentDir";

    private final static String GET_USER_FILE_PATH = "getUserFilePath";

    private final static String OPEN_FILE_DETAIL = "showFile";

    private final static String READ_FILE = "readFile";

    private final static String IS_FILE_EXIST = "isFileExist";

    private final static String FILE_PREVIEW_ONLINE = "filePreviewOnline";

    private CallbackContext callbackContext;

    private static final int REQUEST_CODE_SELECT_FILES = 0x102;

    public static final int RESULT_CODE_SELECT_FILES = 0x103;

    private boolean mAutoUpload = false;

    @Override
    public boolean execute(String action, String rawArgs, final CallbackContext callbackContext) throws JSONException {
        if(!requestCordovaAuth()){
            callbackContext.error(CORDOVA_NOT_AUTH);
            return true;
        }

        this.callbackContext = callbackContext;
        this.cordova.setActivityResultCallback(this);

        //选择单文件
        if (SELECT_SINGLE_FILE.equals(action)) {
            mAutoUpload = false;
            return startFileSelectActivity(false);
        }

        //选择多文件
        if (SELECT_MULTI_FILES.equals(action)) {
            mAutoUpload = false;
            return startFileSelectActivity(true);
        }

        if (CHOOSE_FILES.equalsIgnoreCase(action)) {
            mAutoUpload = true;
            handleChooseFilesUpload(rawArgs);
            return true;
        }

        if (OPEN_EMAIL_ATTACHMENT.equals(action)) {
            JSONArray args = new JSONArray(rawArgs);
            JSONObject json = args.getJSONObject(0);
            if (json == null) {
                callbackContext.error(ErrorConstant.ERROR_INVALID_ARGUMENTS);
                return false;
            }
            String url = json.getString("uri");
            String type = json.getString("type");
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = UriCompat.getFileUriCompat(BaseApplicationLike.baseContext, new File(url));
            intent.setDataAndType(uri, type);
            FileProviderUtil.grantRWPermission(intent);

            if (intent.resolveActivity(this.cordova.getActivity().getPackageManager()) != null) {
                this.cordova.getActivity().startActivity(intent);
            } else {
                ToastHelper.showLongToast(this.cordova.getActivity(), "找不到可以打开该文件的程序");
            }
            return true;
        }

        if (GET_EMAIL_ATTACHMENT_DIR.equals(action)) {
            callbackContext.success(AtWorkDirUtils.getInstance().getEmailAttachmentDir(LoginUserInfo.getInstance().getLoginUserId(BaseApplicationLike.baseContext)));
            return true;
        }

        if(GET_USER_FILE_PATH.equals(action)) {
            handleGetUserFilePath(rawArgs, callbackContext);
            return true;
        }


        if(OPEN_FILE_DETAIL.equals(action)) {
            openFileDetail(rawArgs, callbackContext);

            return true;
        }

        if(READ_FILE.equals(action)) {
            handleReadFile(rawArgs, callbackContext);

            return true;
        }


        if(IS_FILE_EXIST.equals(action)) {
            handleIsFileExist(rawArgs, callbackContext);


            return true;
        }

        if(FILE_PREVIEW_ONLINE.equals(action)){
            if(cordova.getActivity() instanceof WebViewActivity) {
                ((WebViewActivity)cordova.getActivity()).filePreviewOnline(callbackContext);
            }
            return true;
        }

        return false;
    }



    private void handleIsFileExist(String rawArgs, CallbackContext callbackContext) {
        IsFileExistRequest isFileExistRequest = NetGsonHelper.fromCordovaJson(rawArgs, IsFileExistRequest.class);
        if(null == isFileExistRequest) {
            callbackContext.error();

        } else {
            IsFileExistResponse isFileExistResponse = new IsFileExistResponse();
            isFileExistResponse.setExist(FileUtil.isExist(isFileExistRequest.getPath()));

            callbackContext.success(isFileExistResponse);

        }
    }

    private void handleReadFile(String rawArgs, CallbackContext callbackContext) {
        ViewFileRequest viewFileRequest = NetGsonHelper.fromCordovaJson(rawArgs, ViewFileRequest.class);
        if(null == viewFileRequest
                || StringUtils.isEmpty(viewFileRequest.getPath())
                || !FileUtil.isExist(viewFileRequest.getPath())) {
            callbackContext.error();

        } else {
            EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(viewFileRequest.getPath(), false, fileName -> {

                OfficeHelper.previewByX5(cordova.getActivity(), fileName);

            });

        }
    }

    private void openFileDetail(String rawArgs, CallbackContext callbackContext) {
        OpenFileDetailRequest fileDetailRequest = NetGsonHelper.fromCordovaJson(rawArgs, OpenFileDetailRequest.class);

        if (null != fileDetailRequest && fileDetailRequest.checkLegal()) {

            new MediaCenterNetManager(BaseApplicationLike.baseContext).brokenDownloadingOrUploading(fileDetailRequest.getKeyId());

            if (fileDetailRequest.isImgType()) {


                //伪造出 imageChatMessage 传到图片界面
                ImageChatMessage imageChatMessage = new ImageChatMessage();
                imageChatMessage.mBodyType = BodyType.Image;
                imageChatMessage.deliveryId = fileDetailRequest.getKeyId();
                imageChatMessage.mediaId = fileDetailRequest.getMediaId();
                imageChatMessage.isGif = fileDetailRequest.isGifType();
                imageChatMessage.filePath = fileDetailRequest.getPath();
                imageChatMessage.mediaId = fileDetailRequest.getMediaId();
                imageChatMessage.fullImgPath = fileDetailRequest.getPath();

                ImageSwitchInChatActivity.sImageChatMessageList.clear();
                ImageSwitchInChatActivity.sImageChatMessageList.add(imageChatMessage);

                Session fakeSession = new Session();
                fakeSession.type = SessionType.User;

                Intent intent = new Intent();

                intent.putExtra(ImageSwitchFragment.INDEX_SWITCH_IMAGE, 0);
                intent.setClass(BaseApplicationLike.baseContext, ImageSwitchInChatActivity.class);
                intent.putExtra(ImageSwitchFragment.ARGUMENT_SESSION, fakeSession);
                cordova.getActivity().startActivity(intent);


            } else {
                String filePath = null;
                if (StringUtils.isEmpty(fileDetailRequest.getPath())) {
                    String filename = fileDetailRequest.getFileName();
                    int lastDotIndex = filename.lastIndexOf(".");
                    if(-1 != lastDotIndex) {
                        String prefix = filename.substring(0, lastDotIndex );
                        String suffix = filename.substring(lastDotIndex);
                        filename = prefix + "_" + fileDetailRequest.getMediaId() + suffix;

                    } else {
                        filename = filename + "_" + fileDetailRequest.getMediaId();
                    }

                    filePath = AtWorkDirUtils.getInstance().getFiles(BaseApplicationLike.baseContext) + filename;
                    fileDetailRequest.setPath(filePath);

                } else {
                    filePath = fileDetailRequest.getPath();
                }

                if(FileUtil.isExist(filePath)) {
                    fileDetailRequest.setFileStatus(FileStatus.DOWNLOADED);
                }

                CommonFileStatusFragment commonFileStatusFragment = new CommonFileStatusFragment();
                commonFileStatusFragment.initBundle(null, fileDetailRequest);
                commonFileStatusFragment.setUpdateFileDataListener(updateFileStatusInfo -> {


                });
                commonFileStatusFragment.show(cordova.getFragment().getChildFragmentManager(), "FILE_DIALOG");
            }



        } else {
            callbackContext.error();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (intent == null) {
            return;
        }

        if (REQUEST_CODE_SELECT_FILES == requestCode) {
            List<FileData> fileDataList = (List<FileData>) intent.getSerializableExtra(FileSelectActivity.SELECTED_FILES_DATA);
            handleFileSelectedResult(fileDataList);
        }
    }

    private void handleChooseFilesUpload(String jsonArr) {
        ChooseFilesRequest chooseFilesRequest = NetGsonHelper.fromCordovaJson(jsonArr, ChooseFilesRequest.class);
        AndPermission
                .with(cordova.getActivity())
                .runtime()
                .permission( Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                .onGranted(data -> {
                    if(null != chooseFilesRequest && chooseFilesRequest.checkLegal()) {
                        chooseFilesRequest.mFromCordova = true;
                        Intent intent = FileSelectActivity.getIntent(cordova.getActivity(), FileSelectActivity.SelectMode.GET, false, false);
                        intent.putExtra(DATA_CHOOSE_FILES_REQUEST, chooseFilesRequest);
                        cordova.startActivityForResult(this, intent, REQUEST_CODE_SELECT_FILES);

                    } else {
                        callbackContext.error();
                    }
                })
                .onDenied(data -> AtworkUtil.popAuthSettingAlert(cordova.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .start();

    }

    private boolean startFileSelectActivity(boolean multiple) {
        AndPermission
                .with(cordova.getActivity())
                .runtime()
                .permission( Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                .onGranted(data -> {
                    Intent intent = FileSelectActivity.getIntent(cordova.getActivity(), FileSelectActivity.SelectMode.GET, false, false);
                    ChooseFilesRequest chooseFilesRequest = new ChooseFilesRequest();
                    chooseFilesRequest.mMultiple = multiple;
                    chooseFilesRequest.mFileLimit = new ChooseFilesRequest.FileLimit();
                    chooseFilesRequest.mFromCordova = true;

                    if(multiple) {
                        chooseFilesRequest.mFileLimit.mMaxSelectCount = 9;

                    } else {
                        chooseFilesRequest.mFileLimit.mMaxSelectCount = 1;

                    }

                    intent.putExtra(DATA_CHOOSE_FILES_REQUEST, chooseFilesRequest);
                    cordova.startActivityForResult(this, intent, REQUEST_CODE_SELECT_FILES);
                })
                .onDenied(data -> AtworkUtil.popAuthSettingAlert(cordova.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .start();

        return true;
    }

    private void handleFileSelectedResult(List<FileData> fileDataList) {
        if (mAutoUpload) {
            asyncUploadFiles(FileData.toPathList(fileDataList), responseList -> {
                callbackContext.success(responseList);
            });
            return;
        }


        List<ChooseFilesResponse> responseList = new ArrayList<>();
        for (FileData fileData : fileDataList) {
            ChooseFilesResponse chooseFilesResponse = new ChooseFilesResponse();

            chooseFilesResponse.mFilePath = fileData.filePath;
            chooseFilesResponse.mName = fileData.title;
            chooseFilesResponse.mSize = fileData.size;
            chooseFilesResponse.mMediaId = fileData.getMediaId();

            responseList.add(chooseFilesResponse);
        }
        callbackContext.success(responseList);
    }


    private void asyncUploadFiles(List<String> pathList, FileListUploadedListener listener) {
        ProgressDialogHelper dialogHelper = new ProgressDialogHelper(cordova.getActivity());
        dialogHelper.show();
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(() -> {
            List<ChooseFilesResponse> responseList = new ArrayList<>();

            for (String path : pathList) {
                ChooseFilesResponse chooseFilesResponse = getChooseFilesResponse(path);
                if(null != chooseFilesResponse) {
                    responseList.add(chooseFilesResponse);
                }
            }
            dialogHelper.dismiss();

            listener.onFileListUploaded(responseList);
        });
    }

    @Nullable
    private ChooseFilesResponse getChooseFilesResponse(String path) {
        String filePath = path.replace("file:///", "/");

        ChooseFilesResponse chooseFilesResponse = null;
        HttpResult httpResult = syncUploadFile(filePath, System.currentTimeMillis() + "");
        if (httpResult == null) {
            return null;
        }
        String mediaId = "";
        if (httpResult.isNetSuccess()) {

            BasicResponseJSON basicResponseJSON = httpResult.resultResponse;

            //get mediaId name
            if (null != basicResponseJSON) {
                chooseFilesResponse = new ChooseFilesResponse();
                chooseFilesResponse.mFilePath = path;

                if (basicResponseJSON instanceof MediaUploadResultResponseJson) {
                    mediaId = ((MediaUploadResultResponseJson) basicResponseJSON).mediaId;
                    File file = new File(filePath);
                    chooseFilesResponse.mName = file.getName();
                    chooseFilesResponse.mSize = file.length();

                } else if (basicResponseJSON instanceof MediaInfoResponseJson) {
                    MediaInfoResponseJson.MediaInfo mediaInfo = ((MediaInfoResponseJson) basicResponseJSON).mediaInfo;
                    mediaId = mediaInfo.id;
                    chooseFilesResponse.mName = mediaInfo.name;
                    chooseFilesResponse.mSize = mediaInfo.size;

                }

                chooseFilesResponse.mMediaId = mediaId;
            }
        }
        return chooseFilesResponse;
    }

    private HttpResult syncUploadFile(String filePath, String fileId) {
        String checkSum = MD5Utils.getDigest(filePath);
        HttpResult httpResult = MediaCenterSyncNetService.getInstance().getMediaInfo(cordova.getActivity(), checkSum, "digest");
        //说明服务端有该文件
        if(httpResult.isRequestSuccess()) {
            MediaInfoResponseJson mediaInfoResponseJson = (MediaInfoResponseJson) httpResult.resultResponse;
            if (mediaInfoResponseJson.isLegal()) {
                //TODO: link question
                MediaCenterNetManager.linkMedia(cordova.getActivity(), mediaInfoResponseJson.mediaInfo.id, -1, null);
                return httpResult;

            }

        }
        UploadFileParamsMaker uploadFileParamsMaker = UploadFileParamsMaker.newRequest()
                .setMsgId(fileId)
                .setFileDigest(checkSum)
                .setFilePath(filePath)
                .setExpireLimit(-1);

        return MediaCenterHttpURLConnectionUtil.getInstance().uploadCommonFile(cordova.getActivity(), uploadFileParamsMaker);
    }

    private void handleGetUserFilePath(String rawArgs, CallbackContext callbackContext) {
        GetUserFilePathRequest getUserFilePathRequest = NetGsonHelper.fromCordovaJson(rawArgs, GetUserFilePathRequest.class);
        if (null != getUserFilePathRequest) {
            String path = StringUtils.EMPTY;

            if(!StringUtils.isEmpty(getUserFilePathRequest.mSystem)) {

                if(GetUserFilePathRequest.FILE.equalsIgnoreCase(getUserFilePathRequest.mSystem)) {
                    path = AtWorkDirUtils.getInstance().getFiles(cordova.getActivity());

                } else if (GetUserFilePathRequest.DROPBOX.equalsIgnoreCase(getUserFilePathRequest.mSystem)) {
                    path = AtWorkDirUtils.getInstance().getDropboxDir(cordova.getActivity());


                } else if(GetUserFilePathRequest.EMAIL_ATTACHMENT.equalsIgnoreCase(getUserFilePathRequest.mSystem)) {
                    path = AtWorkDirUtils.getInstance().getFiles(cordova.getActivity());

                }




            } else if(!StringUtils.isEmpty(getUserFilePathRequest.mCustom)) {
                path = AtWorkDirUtils.getInstance().getLoginUserFilePath(cordova.getActivity(), getUserFilePathRequest.mCustom);
            }

            callbackContext.success(path);
        }
    }


    private interface FileListUploadedListener {
        void onFileListUploaded(List<ChooseFilesResponse> responseList);
    }
}
