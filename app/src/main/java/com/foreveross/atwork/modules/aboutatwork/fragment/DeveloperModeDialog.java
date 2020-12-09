package com.foreveross.atwork.modules.aboutatwork.fragment;/**
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


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.gridpasswordview.GridPasswordView;
import com.foreveross.atwork.utils.AtworkUtil;

/**
 * Created by reyzhang22 on 16/3/22.
 */
public class DeveloperModeDialog extends PopupWindow {

    private GridPasswordView mGridPasswordView;

    private OnPreviewCodeListener mListener;

    private Context mContext;

    private ImageView mClose;

    public void setPreviewCodeListener(OnPreviewCodeListener listener) {
        mListener = listener;
    }

    public DeveloperModeDialog(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.view_developer_mode, null);
        mClose = (ImageView) view.findViewById(R.id.close_dev_dialog);
        mGridPasswordView = (GridPasswordView) view.findViewById(R.id.grid_password_view);
        mGridPasswordView.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
            @Override
            public void onChanged(String psw) {

            }

            @Override
            public void onMaxLength(final String psw) {
                mListener.onPreviewCodeInput(psw);
            }
        });

        mClose.setOnClickListener(v -> {
            DeveloperModeDialog.this.dismiss();
            if (mContext != null) {
                AtworkUtil.hideInput((Activity) mContext);
            }
        });

        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setContentView(view);
//        setBackgroundDrawable(context.getResources().getDrawable(R.color.white));
        setOutsideTouchable(false);
    }


    public View getGridPwdView() {
        return mGridPasswordView;
    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.view_developer_mode, container);
//        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        setCancelable(true);
//        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Light);
//        getDialog().getWindow().setBackgroundDrawableResource(R.color.white);
//        return view;
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        mGridPasswordView = (GridPasswordView) view.findViewById(R.id.grid_password_view);
//        mGridPasswordView.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
//            @Override
//            public void onChanged(String psw) {
//
//            }
//
//            @Override
//            public void onMaxLength(final String psw) {
//                mListener.onPreviewCodeInput(psw);
//            }
//        });
//    }
//
//    @Override
//    public void onDismiss(DialogInterface dialog) {
//        super.onDismiss(dialog);
//        LogUtil.e("developer", getActivity().getCurrentFocus() + "");
//        if (getActivity() != null ) {
//            AtworkUtil.hideInput(getActivity());
//        }
//    }

    public interface OnPreviewCodeListener {
        void onPreviewCodeInput(String code);
    }
}
