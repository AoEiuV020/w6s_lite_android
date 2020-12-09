package com.foreverht.workplus.ui.component.dialogFragment;

/**
 * Created by dasunsy on 2015/7/6 0006.
 */
public interface AtworkAlertInterface {
    interface OnBrightBtnClickListener{
        void onClick(AtworkAlertInterface dialog);
    }
    interface OnDeadBtnClickListener{
        void onClick(AtworkAlertInterface dialog);
    }
    interface OnPasswordClickListener {
        void onClick(String pwd);
    }
}
