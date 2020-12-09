package com.foreveross.atwork.component;/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 */


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertInterface;
import com.foreveross.atwork.R;
import com.foreveross.atwork.component.gridpasswordview.GridPasswordView;
import com.foreveross.atwork.component.gridpasswordview.PasswordType;

/**
 * Created by reyzhang22 on 16/3/22.
 */
public class CommonPasswordDialog extends Dialog implements AtworkAlertInterface {

    private GridPasswordView mGridPasswordView;

    private Context mContext;

    private GridPasswordView mGridPasswordIndicate;
    private TextView mTvDeadColor;
    private TextView mTvBrightColor;

    public CommonPasswordDialog(Context context) {
        super(context, R.style.app_alert_dialog);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_input_password, null);
        mGridPasswordIndicate = view.findViewById(R.id.grid_password_textview);
        mGridPasswordView = view.findViewById(R.id.grid_password_view);
        mTvDeadColor = view.findViewById(R.id.tv_dead_color);
        mTvBrightColor = view.findViewById(R.id.tv_bright_color);
        mGridPasswordView.setPasswordType(PasswordType.TEXT);
        mGridPasswordIndicate.setPasswordVisibility(true);
        mGridPasswordView.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
            @Override
            public void onChanged(String psw) {
                mGridPasswordIndicate.setPassword(psw);
            }

            @Override
            public void onMaxLength(final String psw) {

            }
        });


        setContentView(view);
    }

    public CommonPasswordDialog setClickDeadColorListener(final AtworkAlertInterface.OnDeadBtnClickListener listener) {
        mTvDeadColor.setOnClickListener(v -> {
            listener.onClick(CommonPasswordDialog.this);
            dismiss();
        });
        return this;
    }

    public CommonPasswordDialog setClickBrightColorListener(final AtworkAlertInterface.OnPasswordClickListener listener) {
        mTvBrightColor.setOnClickListener(v -> {
            listener.onClick(mGridPasswordIndicate.getPassWord().trim());
        });
        return this;
    }
}
