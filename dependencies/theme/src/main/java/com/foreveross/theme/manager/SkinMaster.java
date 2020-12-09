package com.foreveross.theme.manager;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import com.google.android.material.tabs.TabLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.theme.interfaces.ISkinLoader;
import com.foreveross.theme.interfaces.ISkinUpdate;
import com.foreveross.theme.model.Theme;
import com.foreveross.theme.model.ThemeType;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建时间：2016年09月07日 下午4:58
 * 创建人：laizihan
 * 类名：SkinManager
 * 用途：
 */
public class SkinMaster implements ISkinLoader {
    private Context mContext;

    private static SkinMaster sInstance;

    private List<ISkinUpdate> mObserverList;

    private Theme mCurrentTheme;

    public static String sTestMode;


    private SkinMaster() {
    }


    public static SkinMaster getInstance() {
        if (sInstance == null) {
            synchronized (SkinMaster.class) {
                if (sInstance == null) {
                    sInstance = new SkinMaster();
                }
            }
        }
        return sInstance;
    }


    public void init(Context context) {
        mContext = context.getApplicationContext();
    }

    public void setCurrentTheme(Theme theme) {
        this.mCurrentTheme = theme;
    }

    public Theme getCurrentTheme() {
        return mCurrentTheme;
    }


    /**
     * TODO:后期用策略类实现以下逻辑，便于维护
     * <p>
     * 根据tag递归遍历找出所有对应view的颜色标签。设置相应的颜色背景
     * 控件的颜色由具体控件的类型和tag共同决定。
     *
     * @param rootView
     */
    public void changeTheme(ViewGroup rootView) {
        if(!AtworkConfig.SKIN) {
            return;
        }

        int childCount = rootView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = rootView.getChildAt(i);
            if (child instanceof ViewGroup) {

                if (child instanceof TabLayout && isTagValid(child)) {
                    TabLayout tabLayout = (TabLayout) child;
                    tabLayout.setTabTextColors(SkinHelper.getTabInactiveColor(), SkinHelper.getTabActiveColor());
                    tabLayout.setSelectedTabIndicatorColor(Color.parseColor(SkinHelper.getColorFromTag((String) tabLayout.getTag())));

                } else if (isTagValid(child)) {
                    child.setBackgroundColor(Color.parseColor(SkinHelper.getColorFromTag((String) child.getTag())));
                }
                changeTheme((ViewGroup) child);
            } else if (child instanceof TextView) {
                TextView textView = (TextView) child;

                if (textView instanceof Button && isTagValid(textView)) {
                    changeBackground(textView.getBackground(), SkinHelper.parseColorFromTag((String) textView.getTag()));
                    continue;
                }

                //edittext 的tag不起作用，因为edittext的hintcolor和textcolor都是已知
                if (textView instanceof EditText) {
                    EditText editText = (EditText) textView;
                    editText.setHintTextColor(SkinHelper.getSecondaryTextColor());
                    editText.setTextColor(SkinHelper.getPrimaryTextColor());
                    continue;
                }

                if (isTagValid(textView)) {
                    textView.setTextColor(SkinHelper.parseColorFromTag((String) textView.getTag()));
                }


            } else if (child instanceof ImageView && isTagValid(child)) {
                //imageview 一般为线的颜色，背景的颜色
//                child.setBackgroundColor(ColorHelper.parseColorFromTag((String) child.getTag()));

            }
        }
    }


    public boolean isTagValid(View view) {
        return (view.getTag() != null && view.getTag() instanceof String  && SkinHelper.tagBelongsToBaseColor((String) view.getTag()));
    }


    @Override
    public void attach(ISkinUpdate observer) {
        if (mObserverList == null) {
            mObserverList = new ArrayList<>();
        }
        if (!mObserverList.contains(observer)) {
            mObserverList.add(observer);
        }
    }

    @Override
    public void detach(ISkinUpdate observer) {
        if (mObserverList == null) {
            return;
        }
        if (mObserverList.contains(observer)) {
            mObserverList.remove(observer);
        }
    }

    /**
     * 根据不同的drawable改变bg颜色
     *
     * @param drawable
     * @param color
     */

    public void changeBackground(Drawable drawable, int color) {
        if(!AtworkConfig.SKIN) {
            return;
        }

        if (drawable instanceof ShapeDrawable) {
            ShapeDrawable shapeDrawable = (ShapeDrawable) drawable;
            shapeDrawable.getPaint().setColor(color);
        } else if (drawable instanceof GradientDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) drawable;
            gradientDrawable.setColor(color);
        } else if (drawable instanceof ColorDrawable) {
            ColorDrawable colorDrawable = (ColorDrawable) drawable;
            colorDrawable.setColor(color);
        }

    }


    @Override
    public void notifySkinChange(Theme theme) {
        if (mObserverList == null) {
            return;
        }
        for (ISkinUpdate observer : mObserverList) {
            observer.onThemeUpdate(theme);
        }

    }




    public Bitmap transformImmutable(Bitmap sourceBitmap, int color) {

        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
                sourceBitmap.getWidth() - 1, sourceBitmap.getHeight() - 1);
        Paint p = new Paint();
        ColorFilter filter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
        p.setColorFilter(filter);
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);
        return resultBitmap;
    }

    public Drawable transformImmutable(Drawable drawable, int color) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap srcBmp = bitmapDrawable.getBitmap();
            Bitmap result = transformImmutable(srcBmp, color);
            drawable = new BitmapDrawable(mContext.getResources(), result);
        }
        return drawable;

    }

    /**
     * mutable
     * 改变bitmap的颜色，效果等同于{@link SkinMaster#changeBackground(Drawable, int)}
     *
     * @param source
     * @param color
     * @return
     */
    public Bitmap transform(Bitmap source, int color) {
        if (color == 0) {
            return source;
        }
        BitmapDrawable drawable = new BitmapDrawable(Resources.getSystem(), source);
        Bitmap result = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        drawable.draw(canvas);
        drawable.setColorFilter(null);
        drawable.setCallback(null);
        if (result != source) {
            source.recycle();
        }

        return result;
    }

    /**
     * mutable
     *
     * @param drawable
     * @param color
     * @return
     */
    public Drawable transform(Drawable drawable, int color) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap srcBmp = bitmapDrawable.getBitmap();
            Bitmap result = transform(srcBmp, color);
            drawable = new BitmapDrawable(mContext.getResources(), result);
        }
        return drawable;
    }


    public ColorStateList makeHomeTabSelector(int selected, int normal) {
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_selected},
                new int[]{}
        };
        int[] colors = new int[]{selected, normal};
        return new ColorStateList(states, colors);
    }


    public StateListDrawable makeItemSessionViewSelector(int selected, int normal) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(selected));
        stateListDrawable.addState(new int[]{}, new ColorDrawable(normal));
        return stateListDrawable;
    }


    public Theme parseTheme(String json, String name, ThemeType type) {
        Theme theme = JsonUtil.fromJson(json, Theme.class);
        theme.mName = name;
        theme.mType = type;

        return theme;
    }


}
