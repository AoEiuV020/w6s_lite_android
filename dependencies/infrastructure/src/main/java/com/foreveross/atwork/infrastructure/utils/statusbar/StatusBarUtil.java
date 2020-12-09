package com.foreveross.atwork.infrastructure.utils.statusbar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.ColorInt;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.foreveross.atwork.infrastructure.R;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.DeviceUtil;
import com.foreveross.atwork.infrastructure.utils.rom.RomUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Jaeger on 16/2/14.
 *
 * Email: chjie.jaeger@gmail.com
 * GitHub: https://github.com/laobie
 */
public class StatusBarUtil {

    public static final int FAKE_STATUS_BAR_VIEW_ID = R.id.statusbarutil_fake_status_bar_view;
    public static final int FAKE_TRANSLUCENT_VIEW_ID = R.id.statusbarutil_translucent_view;

    // 大部分状态栏都是25dp
    private final static int STATUS_BAR_DEFAULT_HEIGHT_DP = 25;

    private static int sStatusBarHeight = -1;
    // 在某些机子上存在不同的density值，所以增加两个虚拟值
    public static float sVirtualDensity = -1;
    public static float sVirtualDensityDpi = -1;

    public static boolean setStatusBarMode(Activity activity, boolean darkmode) {
        return setStatusBarMode(activity.getWindow(), darkmode);
    }

    public static boolean setStatusBarMode(Window window, boolean darkmode) {

        if(Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT) {

            if(RomUtil.isMeizu()) {
                return setMeizuStatusBarDarkIcon(window, darkmode);
            }

            if(RomUtil.isXiaomi()) {
                return setMiuiStatusBarDarkMode(window, darkmode);
            }


        }

        return setAndroidStatusBarMode(window, darkmode);

    }



    private static boolean setAndroidStatusBarMode(Window window, boolean darkmode) {
        if (supportAndroidStatusBarMode()) {
            int systemUi = darkmode ? View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR : View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            systemUi = changeStatusBarModeRetainFlag(window, systemUi);

            window.getDecorView().setSystemUiVisibility(systemUi);

            return true;
        }

        return false;
    }


    /**
     * 是否支持设置状态栏字体颜色的方法, 区别于 {@link #supportAndroidStatusBarMode()}, 小米, 魅族的 rom 有提供
     * 6.0以下版本设置状态栏字体颜色的方法
     * */
    public static boolean supportStatusBarMode() {
        if(Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT) {

            if(RomUtil.isMeizu()) {
                return true;
            }

            if(RomUtil.isXiaomi()) {
                return true;
            }


        }

        return supportAndroidStatusBarMode();

    }


    /**
     * 是否支持 android 官方的设置状态栏字体颜色的方法
     * */
    private static boolean supportAndroidStatusBarMode() {
        //这两部机器很神奇, 设置状态栏字体变色无效, 但它们自家应用又可以, 并没有开放接口 see https://github.com/QMUI/QMUI_Android
        //努比亚这个机器状态栏变色在transparent状态栏时, 还是白色字体, 放弃资料它
        if(DeviceUtil.isZTKC2016()
                || DeviceUtil.isZUKZ1()
                || DeviceUtil.isNubiaNX569H()) {
            return false;
        }

        return Build.VERSION_CODES.M <= Build.VERSION.SDK_INT;
    }


    /**
     * 更改状态栏图标、文字颜色的方案是否是MIUI自家的， MIUI9之后用回Android原生实现
     * 见小米开发文档说明：https://dev.mi.com/console/doc/detail?pId=1159
     */
    private static boolean isMIUICustomStatusBarLightModeImpl() {
        return RomUtil.isMIUIV5() || RomUtil.isMIUIV6() ||
                RomUtil.isMIUIV7() || RomUtil.isMIUIV8();
    }

    @TargetApi(23)
    private static int changeStatusBarModeRetainFlag(Window window, int out) {
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_FULLSCREEN);
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        return out;
    }

    public static int retainSystemUiFlag(Window window, int out, int type) {
        int now = window.getDecorView().getSystemUiVisibility();
        if ((now & type) == type) {
            out |= type;
        }
        return out;
    }


    /**
     * @see #setMiuiStatusBarDarkMode(Window, boolean)
     * */
    public static boolean setMiuiStatusBarDarkMode(Activity activity, boolean darkmode) {
        return setMiuiStatusBarDarkMode(activity.getWindow(), darkmode);
    }

    /**
     * 小米设置状态栏字体跟图标颜色(6.0以上官方系统已经有提供api)
     * */
    public static boolean setMiuiStatusBarDarkMode(Window window, boolean darkmode) {
        if(!isMIUICustomStatusBarLightModeImpl()
                && Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {
            setAndroidStatusBarMode(window, darkmode);
            return true;
        }


        Class<? extends Window> clazz = window.getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(window, darkmode ? darkModeFlag : 0, darkModeFlag);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * @see #setMeizuStatusBarDarkIcon(Window, boolean)
     * */
    public static boolean setMeizuStatusBarDarkIcon(Activity activity, boolean dark) {
        return setMeizuStatusBarDarkIcon(activity.getWindow(), dark);
    }


    /**
     * 魅族设置状态栏字体跟图标颜色(6.0以上官方系统已经有提供api)
     * */
    public static boolean setMeizuStatusBarDarkIcon(Window window, boolean dark) {
        // flyme 在 6.2.0.0A 支持了 Android 官方的实现方案，旧的方案失效
        setAndroidStatusBarMode(window, dark);

        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }



    /**
     * 设置状态栏颜色
     *
     * @param activity       需要设置的activity
     * @param color          状态栏颜色值1
     * @param statusBarAlpha 状态栏透明度(opacity, 不透明度, 越大越透明)
     */

    public static void setColor(Activity activity, @ColorInt int color, int statusBarAlpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().setStatusBarColor(calculateStatusColor(color, statusBarAlpha));

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            View fakeStatusBarView = decorView.findViewById(FAKE_STATUS_BAR_VIEW_ID);
            if (fakeStatusBarView != null) {
                if (fakeStatusBarView.getVisibility() == View.GONE) {
                    fakeStatusBarView.setVisibility(View.VISIBLE);
                }
                fakeStatusBarView.setBackgroundColor(calculateStatusColor(color, statusBarAlpha));
            } else {
                decorView.addView(createStatusBarView(activity, color, statusBarAlpha));
            }
            setRootView(activity);
        }
    }


    /**
     * 设置状态栏纯色 不加半透明效果((用于dialogFragment等))
     *
     * @param viewGroup  对应的 viewGroup
     * @param color    状态栏颜色值
     */
    public static void setColorNoTranslucent(ViewGroup viewGroup, Window window, @ColorInt int color) {
        setColor(viewGroup, window, color, 0);
    }


    /**
     * 设置状态栏颜色(用于dialogFragment等)
     *
     * @param viewGroup  对应的 viewGroup
     * @param window       需要设置的window
     * @param color          状态栏颜色值
     * @param statusBarAlpha 状态栏透明度
     */

    public static void setColor(ViewGroup viewGroup, Window window, @ColorInt int color, int statusBarAlpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(calculateStatusColor(color, statusBarAlpha));

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            View fakeStatusBarView = viewGroup.findViewById(FAKE_STATUS_BAR_VIEW_ID);
            if (fakeStatusBarView != null) {
                if (fakeStatusBarView.getVisibility() == View.GONE) {
                    fakeStatusBarView.setVisibility(View.VISIBLE);
                }
                fakeStatusBarView.setBackgroundColor(calculateStatusColor(color, statusBarAlpha));

            } else {

                viewGroup.addView(createStatusBarView(viewGroup.getContext(), color, statusBarAlpha), 0);
            }
        }
    }



    /**
     * 设置状态栏纯色 不加半透明效果
     *
     * @param activity 需要设置的 activity
     * @param color    状态栏颜色值
     */
    public static void setColorNoTranslucent(Activity activity, @ColorInt int color) {
        setColor(activity, color, 0);
    }







    /**
     * 为头部是 ImageView 的界面设置状态栏全透明
     *
     * @param activity       需要设置的activity
     * @param needOffsetView 需要向下偏移的 View
     */
    public static void setTransparentForImageView(Activity activity, View needOffsetView) {
        setTranslucentForImageView(activity, 0, needOffsetView);
    }


    /**
     * 为头部是 ImageView 的界面设置状态栏透明
     *
     * @param activity       需要设置的activity
     * @param statusBarAlpha 状态栏透明度
     * @param needOffsetView 需要向下偏移的 View
     */
    public static void setTranslucentForImageView(Activity activity, int statusBarAlpha, View needOffsetView) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        setTransparentFullScreen(activity);
        addTranslucentView(activity, statusBarAlpha);
        if (needOffsetView != null) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) needOffsetView.getLayoutParams();
            layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin + getStatusBarHeight(activity),
                    layoutParams.rightMargin, layoutParams.bottomMargin);
        }
    }




    /**
     * 添加半透明矩形条
     *
     * @param activity       需要设置的 activity
     * @param statusBarAlpha 透明值
     */
    private static void addTranslucentView(Activity activity, int statusBarAlpha) {
        ViewGroup contentView = activity.findViewById(android.R.id.content);
        View fakeTranslucentView = contentView.findViewById(FAKE_TRANSLUCENT_VIEW_ID);
        if (fakeTranslucentView != null) {
            if (fakeTranslucentView.getVisibility() == View.GONE) {
                fakeTranslucentView.setVisibility(View.VISIBLE);
            }
            fakeTranslucentView.setBackgroundColor(Color.argb(statusBarAlpha, 0, 0, 0));
        } else {
            contentView.addView(createTranslucentStatusBarView(activity, statusBarAlpha));
        }
    }

    /**
     * 生成一个和状态栏大小相同的彩色矩形条
     *
     * @param activity 需要设置的 activity
     * @param color    状态栏颜色值
     * @return 状态栏矩形条
     */
    private static StatusBarView createStatusBarView(Activity activity, @ColorInt int color) {
        return createStatusBarView(activity, color, 0);
    }

    /**
     * 生成一个和状态栏大小相同的半透明矩形条
     *
     * @param context 需要设置的context
     * @param color    状态栏颜色值
     * @param alpha    透明值
     * @return 状态栏矩形条
     */
    private static StatusBarView createStatusBarView(Context context, @ColorInt int color, int alpha) {
        // 绘制一个和状态栏一样高的矩形
        StatusBarView statusBarView = new StatusBarView(context);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(context));
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(calculateStatusColor(color, alpha));
        statusBarView.setId(FAKE_STATUS_BAR_VIEW_ID);
        return statusBarView;
    }


    /**
     * 设置根布局参数
     */
    private static void setRootView(Activity activity) {
        ViewGroup parent = activity.findViewById(android.R.id.content);
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(true);
                ((ViewGroup) childView).setClipToPadding(true);
            }
        }
    }



    public static void setTransparentFullScreenAndStatusBar(Window window, boolean darkmode) {
        StatusBarUtil.setTransparentFullScreen(window);
        StatusBarUtil.setStatusBarMode(window, darkmode);
    }

    /**
     * 设置statusBar透明, 并且全屏显示
     */
    public static void setTransparentFullScreen(Activity activity) {
        setTransparentFullScreen(activity.getWindow());
    }


    public static void setTransparentFullScreen(Window window) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);


            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }


    }


    public static void setTransparentFullScreen_v2(Activity activity) {
        setTransparentFullScreen_v2(activity.getWindow());
    }


    public static void setTransparentFullScreen_v2(Window window) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);


            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }


    }


    /**
     * 创建半透明矩形 View
     *
     * @param alpha 透明值
     * @return 半透明 View
     */
    private static StatusBarView createTranslucentStatusBarView(Activity activity, int alpha) {
        // 绘制一个和状态栏一样高的矩形
        StatusBarView statusBarView = new StatusBarView(activity);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(Color.argb(alpha, 0, 0, 0));
        statusBarView.setId(FAKE_TRANSLUCENT_VIEW_ID);
        return statusBarView;
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    /**
     * 获取状态栏的高度。
     */
    public static int getStatusBarHeight(Context context) {
        if (-1 == sStatusBarHeight) {
            initStatusBarHeight(context);
        }
        return sStatusBarHeight;
    }

    /**
     * copy from QMUI, see https://github.com/QMUI/QMUI_Android
     * */
    private static void initStatusBarHeight(Context context) {
        Class<?> clazz;
        Object obj = null;
        Field field = null;
        try {
            clazz = Class.forName("com.android.internal.R$dimen");
            obj = clazz.newInstance();
            if (RomUtil.isMeizu()) {
                try {
                    field = clazz.getField("status_bar_height_large");
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            if (field == null) {
                field = clazz.getField("status_bar_height");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        if (field != null && obj != null) {
            try {
                int id = Integer.parseInt(field.get(obj).toString());
                sStatusBarHeight = context.getResources().getDimensionPixelSize(id);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        if (DeviceUtil.isTablet(context)
                && sStatusBarHeight > DensityUtil.dip2px(STATUS_BAR_DEFAULT_HEIGHT_DP)) {
            //状态栏高度大于25dp的平板，状态栏通常在下方
            sStatusBarHeight = 0;
        } else {
            if (sStatusBarHeight <= 0
                    || sStatusBarHeight > DensityUtil.dip2px(STATUS_BAR_DEFAULT_HEIGHT_DP * 2)) {
                //安卓默认状态栏高度为25dp，如果获取的状态高度大于2倍25dp的话，这个数值可能有问题，用回桌面定义的值从新获取。出现这种可能性较低，只有小部分手机出现
                if (sVirtualDensity == -1) {
                    sStatusBarHeight = DensityUtil.dip2px(STATUS_BAR_DEFAULT_HEIGHT_DP);
                } else {
                    sStatusBarHeight = (int) (STATUS_BAR_DEFAULT_HEIGHT_DP * sVirtualDensity + 0.5f);
                }
            }
        }
    }

    /**
     * 计算状态栏颜色
     *
     * @param color color值
     * @param alpha alpha值
     * @return 最终的状态栏颜色
     */
    private static int calculateStatusColor(@ColorInt int color, int alpha) {
        if (alpha == 0) {
            return color;
        }
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }
}
