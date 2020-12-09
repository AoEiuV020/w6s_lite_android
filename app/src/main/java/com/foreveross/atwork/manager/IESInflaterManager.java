package com.foreveross.atwork.manager;

import android.content.Context;

import com.foreveross.atwork.infrastructure.model.IES;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.CustomerHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by reyzhang22 on 15/8/11.
 */
public class IESInflaterManager {

    private static final String TAG = IESInflaterManager.class.getSimpleName();

    private static IESInflaterManager sInstance = new IESInflaterManager();

    public Class mIesClz;

    public static IESInflaterManager getInstance() {
        synchronized (TAG) {
            if (sInstance == null) {
                sInstance = new IESInflaterManager();
            }
            return sInstance;
        }
    }

    /**
     * 初始化IES SDK
     * @param context
     * @return
     */
    public boolean initIES(Context context) {
        if (!AtworkConfig.H3C_CONFIG) {
            return false;
        }
        boolean result = false;
        initIESObject(context);
        if (mIesClz == null) {
            return result;
        }
        try {
            boolean needVpn = !CustomerHelper.isXtbank(context);

            Method method = mIesClz.getMethod("initH3ConfigWithINode", Context.class, boolean.class);
            Object object = method.invoke(mIesClz.newInstance(), context, needVpn);
            result = (boolean)object;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return result;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return result;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return result;
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 拉起Inode登陆
     */
    public int requestInodeLogin(Context context) {
        if (!AtworkConfig.H3C_CONFIG) {
            return -1;
        }
        initIESObject(context);
        if (mIesClz == null) {
            return -1;
        }
        try {
            Method method = mIesClz.getMethod("requestInodeLogin");
            Object object = method.invoke(mIesClz.newInstance());
            return (Integer)object;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return -1;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return -2;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return -3;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return -4;
        } catch (ClassCastException e) {
            e.printStackTrace();
            return -5;
        }
    }

    /**
     * 检查Inode是否在线
     * @return
     */
    public boolean checkVpnOnline(Context context) {
        if (!AtworkConfig.H3C_CONFIG) {
            return false;
        }
        boolean isLogin = false;
        initIESObject(context);
        if (mIesClz == null) {
            return false;
        }
        try {
            Method method = mIesClz.getMethod("checkOnlineStatus");
            Object object = method.invoke(mIesClz.newInstance());
            return (boolean)object;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return isLogin;
    }

    public IES getIESAccount(Context context) {
        if (!AtworkConfig.H3C_CONFIG) {
            return null;
        }
        IES ies = null;
        initIESObject(context);
        if (mIesClz == null) {
            return ies;
        }
        try {
            Method method = mIesClz.getMethod("getIESAccount");
            Object object = method.invoke(mIesClz.newInstance());
            ies = (IES)object;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return ies;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return ies;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return ies;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return ies;
        }
        return ies;
    }

    private void initIESObject(Context context) {
        if (mIesClz != null) {
            return;
        }
        try {
            mIesClz = Class.forName("com.foreveross.com.h3c_ies.IESManager");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
    }


}
