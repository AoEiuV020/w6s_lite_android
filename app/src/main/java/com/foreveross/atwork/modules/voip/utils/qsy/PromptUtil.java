package com.foreveross.atwork.modules.voip.utils.qsy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.SoundPool;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.modules.voip.adapter.qsy.PopMenuListAdapter;
import com.foreveross.atwork.modules.voip.component.qsy.TangCustomDialog;
import com.foreveross.atwork.modules.voip.support.qsy.interfaces.OnMenuClickListener;

import java.util.List;

/**
 * Created by RocXu on 2015/12/16.
 */
public class PromptUtil {
    private static final String TAG = "PromptUtil";
    private SoundPool mSoundPool;

    public static Dialog showMenu(String title, List<String> menuList, Context context,
                                  OnMenuClickListener listener) {
        if (context == null) {
            Log.e(TAG, "showMsgMenu-> execute error, param of context is null!");
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        builder.setCancelable(true);
        dialog.show();
        // 设置对话框尺寸
        dialog.getWindow()
                .setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // 获取自定义圆形进度布局容器
        View container = inflater
                .inflate(R.layout.tangsdk_menu_dialog, null);
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        ViewGroup.LayoutParams layoutParams = container.getLayoutParams();
        // 容器宽度大于屏幕时，尝试适配屏幕宽度
        if (dm != null && dm.widthPixels > 0
                && container.getWidth() > (dm.widthPixels)) {
            layoutParams.width = dm.widthPixels;
        }
        PopMenuListAdapter chatMsgMenuAdapter = new PopMenuListAdapter(dialog, menuList, listener);
        ListView menuListView = (ListView) container.findViewById(R.id.menu_list);
        menuListView.setAdapter(chatMsgMenuAdapter);
        dialog.setContentView(container);
        return dialog;
    }

    /**
     * @brief 显示一个自定义的Alert对话框[确定、取消按钮的文字可自定义]
     * @param title
     *            对话框标题，null或空字符串则不显示标题
     * @param message
     *            对话框显示内容，必传s
     * @param positiveText
     *            ［确定］按钮文字，传null默认文字"确定"
     * @param negativeText
     *            ［取消］按钮文字，传null默认文字"取消"
     * @param context
     *            对话框显示需要的上下文环境，必传
     * @param posBtnListener
     *            ［确定］按钮点击事件的监听器
     * @param negBtnListener
     *            ［取消］按钮点击事件的监听器，若不传则不显示取消按钮
     * @param cancelable
     *            对话框是否响应自动关闭事件（主要指用户点击对话框外的任何位置） - true：响应关闭事件 - false： 不响应关闭事件
     */
    public static Dialog showCustomAlertMessage(String title, String message,
                                                String positiveText, String negativeText, Context context,
                                                DialogInterface.OnClickListener posBtnListener,
                                                DialogInterface.OnClickListener negBtnListener, boolean cancelable) {

        if (context == null) {
            Log.e(TAG, "showCustomAlertMessage-> param of context is null!");
            return null;
        }
        if (TextUtils.isEmpty(message)) {
            Log.e(TAG, "showCustomAlertMessage-> param of message is null!");
            return null;
        }
        TangCustomDialog.Builder builder = new TangCustomDialog.Builder(context);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        builder.setMessage(message);

        if(TextUtils.isEmpty(positiveText)){
            builder.setPositiveButton(R.string.tangsdk_continue_btn_text,
                    posBtnListener);
        }else {
            builder.setPositiveButton(positiveText, 0,
                    posBtnListener);
        }
        if (negBtnListener != null) {
            String[] otherText = null;
            if (TextUtils.isEmpty(negativeText)) {
                otherText = new String[] { context
                        .getString(R.string.tangsdk_cancel_btn_title) };
            } else {
                otherText = new String[] { negativeText };
            }
            DialogInterface.OnClickListener[] listeners = new DialogInterface.OnClickListener[]{negBtnListener};
            builder.setOtherBtns(otherText, listeners);
        }else {
            builder.setOtherBtns(null, null);
        }
        builder.setCancelable(cancelable);

        TangCustomDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public static Dialog showVideoGuideDialog(final Context context) {
        if (context == null) {
            Log.e(TAG, "showVideoGuideDialog-> execute error, param of context is null!");
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.dialog_translucent);
        final AlertDialog dialog = builder.create();
        dialog.show();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.alpha = 0.6f;
        dialog.getWindow().setAttributes(lp);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View container = inflater.inflate(R.layout.tangsdk_slide_video_guide_layout, null);
        Button confirmBtn = (Button) container.findViewById(R.id.tangsdk_confirm_btn);
        confirmBtn.setOnClickListener(v -> {
            dialog.dismiss();
            // set the flag
//            SharedPrefUtil.putData(context, SharedPrefUtil.KEY_ISGUIDE_SHOWED, true);
            CommonShareInfo.setQsyVideoGuideShowed(context, true);

        });
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(container);
        dialog.setCancelable(false);

        return dialog;
    }

    public static boolean isGuideShowed(Context context){
        return CommonShareInfo.isQsyVideoGuideShowed(context);
    }

    /**
     * @param fDip dip尺寸
     * @return 转换后的px尺寸
     * @brief 将dip尺寸转换为px尺寸
     */
    public static float convertDipToPx(Context context, float fDip) {
        float fPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, fDip,
                context.getResources().getDisplayMetrics());
        return fPx;
    }
}
