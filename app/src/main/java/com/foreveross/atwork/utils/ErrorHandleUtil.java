package com.foreveross.atwork.utils;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpResultException;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.utils.NetworkStatusUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil;
import com.foreveross.atwork.support.AtworkBaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by lingen on 15/5/31.
 * Description:
 * 错误处理程序
 */
public class ErrorHandleUtil {

    public static final int TOKEN_NOT_FOUND_ERROR = 10011;
    public static final int TOKEN_MISSING_ERROR = 10012;
    public static final int TOKEN_EXPIRED_ERROR = 10013;

    public static final int LICENSE_OVERDUE_ERROR = 203022;
    public static final int LICENSE_NOT_FOUND = 203021;

    public static final int USER_IS_LOCKED = 201008;

    public static final int USER_IS_FORBIDDEN = 201600;
    public static  final int USER_IS_BINDING = 201610;

    public static final int MAINTENANCE_MODE = 201041;
    public static final int MAINTENANCE_MODE2 = 201042;

    public static final int READ_SERVER_MSG_ERROR = 999999;

    private static final String SPLIT = "_";
    private static final String COMMON = "common";
    private static final String NETWORK = "NETWORK";



    public static void handleError(Throwable ex) {
        handleError(null, ex);
    }

    public static void handleError(Module module, Throwable ex) {
        if(ex instanceof HttpResultException) {
            HttpResultException httpResultException = (HttpResultException) ex;
            handleError(module, httpResultException.getErrorCode(), httpResultException.getErrorMsg());
        } else {

            AtworkToast.showToast(BaseApplicationLike.baseContext.getString(R.string.error_happened) + "\n" + ex.getMessage());


        }
    }

    public static void handleTokenError(Throwable ex) {
        if(ex instanceof HttpResultException) {
            HttpResultException httpResultException = (HttpResultException) ex;
            handleTokenError(httpResultException.getErrorCode(), httpResultException.getErrorMsg());
        } else {
            AtworkToast.showToast(BaseApplicationLike.baseContext.getString(R.string.error_happened) + "\n" + ex.getMessage());

        }
    }

    /**
     * 网络错误处理
     * @param errorCode
     * @param errorMsg
     * */
    public static void handleError(int errorCode, String errorMsg) {
        handleError(null, errorCode, errorMsg);
    }

    /**
     * 网络错误处理
     *
     * @param module
     * @param errorCode
     * @param errorMsg
     * */
    public static void handleError(Module module, int errorCode, String errorMsg) {

        if(handleBaseError(errorCode, errorMsg, true)) {
            return;
        }

        Context baseContext = BaseApplicationLike.baseContext;


        if(READ_SERVER_MSG_ERROR == errorCode && !StringUtils.isEmpty(errorMsg)) {
            AtworkToast.showToast(errorMsg);
            return;
        }

        if(null != module) {
            int resId = getErrorResId(baseContext, module, errorCode);

            if (resId != 0) {
                AtworkToast.showToast(baseContext.getResources().getString(resId));
            }


        } else {
            AtworkToast.showToast(baseContext.getString(R.string.network_not_avaluable));

        }

    }

    public static int getErrorResId(Context ctx, Module module, int errorCode) {
        String resName = module.getModuleString() + SPLIT + getStringErrorCode(errorCode);

        int resId = ctx.getResources().getIdentifier(resName, "string", ctx.getPackageName());
        if (resId == 0) {
            resName = module.toString() + SPLIT + COMMON;
            resId = ctx.getResources().getIdentifier(resName, "string", ctx.getPackageName());
        }

        if (resId == 0) {
            resName = COMMON;
            resId = ctx.getResources().getIdentifier(resName, "string", ctx.getPackageName());
        }

        return resId;
    }



    public static boolean handleBaseError(int errorCode, String errorMsg) {
        return handleBaseError(errorCode, errorMsg, true);
    }

    public static boolean handleBaseError(int errorCode, String errorMsg, boolean shouldToast) {
        Context baseContext = BaseApplicationLike.baseContext;
        if (handleTokenError(errorCode, errorMsg)) {
            return true;

        }

        //网络中断情况下
        if (!NetworkStatusUtil.isNetworkAvailable(AtworkApplicationLike.baseContext) && shouldToast) {
            AtworkToast.showToast(baseContext.getString(R.string.network_not_avaluable));
            return true;
        }
        return false;
    }

    public static boolean handleTokenError(int errorCode, String msg) {
        //TOKEN如果不对，则重新对IM进行连接
        switch (errorCode){
            case MAINTENANCE_MODE:
            case MAINTENANCE_MODE2:
                AtworkApplicationLike.clearData();
                Intent intent = new Intent(AtworkBaseActivity.MAINTENANCE_MODE);
                intent.putExtra("MAINTENANCE_MSG", getMaintenanceMsg(msg));
                LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).sendBroadcast(intent);
                return true;

            case TOKEN_NOT_FOUND_ERROR:
            case TOKEN_MISSING_ERROR:
                AtworkApplicationLike.clearData();
                LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(AtworkBaseActivity.TOKEN_EXPIRE));
                return true;

            case TOKEN_EXPIRED_ERROR:
                AtworkApplicationLike.clearData();
                LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(AtworkBaseActivity.TOKEN_EXPIRE));
                return true;

            case LICENSE_OVERDUE_ERROR:
            case LICENSE_NOT_FOUND:
                AtworkApplicationLike.clearData();
                LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(AtworkBaseActivity.LICENSE_OVERDUE));

                return true;

            case USER_IS_LOCKED:
                AtworkApplicationLike.clearData();
                LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(AtworkBaseActivity.ACCOUNT_IS_LOCKED));
                return true;

            case USER_IS_FORBIDDEN:
                AtworkApplicationLike.clearData();
                UserRemoveHelper.clean(BaseApplicationLike.baseContext);

                LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(AtworkBaseActivity.DEVICE_FORBIDDEN));
                return true;

            case USER_IS_BINDING:
                AtworkApplicationLike.clearData();
                LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(AtworkBaseActivity.DEVICE_BINDING));
                return true;

        }
        return false;
    }

    private static String getStringErrorCode(int errorCode) {
        if (errorCode == -1) {
            return NETWORK;
        } else {
            return String.valueOf(errorCode);
        }
    }


    public enum Module {

        Nothing {
            @Override
            public String getModuleString() {
                return StringUtils.EMPTY;
            }
        },

        ChangePwd {
            @Override
            public String getModuleString() {
                return "ChangePwd";
            }
        },

        Login {
            @Override
            public String getModuleString() {
                return "Login";
            }
        },

        AboutMe {
            @Override
            public String getModuleString() {
                return "AboutMe";
            }
        },

        FavorContact {
            @Override
            public String getModuleString() {
                return "FavorContact";
            }
        },

        Group {
            @Override
            public String getModuleString() {
                return "Group";
            }
        },

        Contact {
            @Override
            public String getModuleString() {
                return "Contact";
            }
        },

        Offline {
            @Override
            public String getModuleString() {
                return "Offline";
            }
        },

        IM {
            @Override
            public String getModuleString() {
                return "IM";
            }
        },

        App {
            @Override
            public String getModuleString() {
                return "App";
            }
        },

        Media {
            @Override
            public String getModuleString() {
                return "Media";
            }
        },

        Qrcode {
            @Override
            public String getModuleString() {
                return "Qrcode";
            }
        },

        ModifyEmployee {
            @Override
            public String getModuleString() {
                return "ModifyEmployee";
            }
        },

        Voip {
            @Override
            public String getModuleString() {
                return "Voip";
            }
        },

        WechatSync {
            @Override
            public String getModuleString() {
                return "WechatSync";
            }
        },

        Dropbox {
            @Override
            public String getModuleString() {
                return "Dropbox";
            }
        },

        WalletSetInfo {
            @Override
            public String getModuleString() {
                return "WalletSetInfo";
            }
        },

        WalletModifyPwd {
            @Override
            public String getModuleString() {
                return "WalletModifyPwd";
            }
        },

        WalletPay {
            @Override
            public String getModuleString() {
                return "WalletPay";
            }
        },

        QsyCalendar {
            @Override
            public String getModuleString() {
                return "QsyCalendar";
            }
        },

        FaceBio {
            @Override
            public String getModuleString() {
                return "FaceBio";
            }
        },


        BingPost {
            @Override
            public String getModuleString() {
                return "BingPost";
            }
        };

        public abstract String getModuleString();

    }


    public static String getMaintenanceMsg(String message) {
        String retMsg = "";
        try {
            JSONObject jsonObject = new JSONObject(message);
            Locale locale = LanguageUtil.getCurrentSettingLocale(AtworkApplicationLike.sApp);
            if (Locale.SIMPLIFIED_CHINESE.equals(locale)) {
                return jsonObject.optString("prompt", "");
            }
            if (Locale.TRADITIONAL_CHINESE.equals(locale)) {
                return jsonObject.optString("tw_prompttw_prompt", "");
            }
            return jsonObject.optString("en_prompt", "");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return retMsg;
    }
    /**
     * 判断网络是否异常, 例如被关闭网络等
     * */
    @Deprecated
    public static boolean isNetworkUnreachable(int errorCode, String error) {
        return -1 == errorCode && null != error && error.contains(HttpResult.EXCEPTION_NETWORK_IS_UNREACHABLE);

    }

}
