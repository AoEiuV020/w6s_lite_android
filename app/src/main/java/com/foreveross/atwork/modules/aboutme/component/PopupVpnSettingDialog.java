package com.foreveross.atwork.modules.aboutme.component;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.utils.AtworkToast;

public class PopupVpnSettingDialog extends DialogFragment {

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_vpn_setting_dialog, container);
        initViews(view);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Light);
        getDialog().getWindow().setBackgroundDrawableResource(R.color.white);
        return view;
    }

    private void initViews(View view) {
        EditText accountName = (EditText)view.findViewById(R.id.vpn_account_name);
        EditText password = (EditText)view.findViewById(R.id.vpn_password);
        Button save = (Button)view.findViewById(R.id.save);
        save.setOnClickListener(view1 -> {
            String vpnName = accountName.getText().toString();
            String vpnPwd = password.getText().toString();
            if (TextUtils.isEmpty(vpnName)) {
                AtworkToast.showToast(getString(R.string.vpn_account_empty));
                return;
            }
            if (TextUtils.isEmpty(vpnPwd)) {
                AtworkToast.showToast(getString(R.string.vpn_pwd_empty));
                return;
            }

        });
    }

}