package com.foreveross.atwork.component;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.fragment.app.DialogFragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.utils.StringUtils;

/**
 * Created by lingen on 15/5/27.
 * Description:
 */
public class SelectDialogFragment extends DialogFragment {


    private static final String TITLE = "title";
    private static final String ICON_MEDIAID = "ICON_MEDIAID";
    private static final String ICON_RESID = "ICON_RESID";
    private static final String MESSAGE = "MESSAGE";
    private static final String NO_CANCEL = "no_cancel";
    private OnClickListener onClickListener;
    private SelectDialogItem selectDialogItem;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        selectDialogItem = new SelectDialogItem(getActivity());
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        return selectDialogItem;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerListener();

        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Light);
        getDialog().getWindow().setBackgroundDrawableResource(R.color.white);
    }

    private Bundle getBundle() {
        if (getArguments() == null) {
            Bundle bundle = new Bundle();
            setArguments(bundle);
        }
        return getArguments();
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
    }

    private void initData() {
        if (!StringUtils.isEmpty(getBundle().getString(TITLE))) {
            selectDialogItem.setTitle(getBundle().getString(TITLE));
        }

        String mediaId = getBundle().getString(ICON_MEDIAID);
        if (StringUtils.isEmpty(mediaId) == false) {
            selectDialogItem.setIcon(mediaId);
        }

        int resId = getBundle().getInt(ICON_RESID);
        if (resId != 0) {
            selectDialogItem.setIconResId(resId);
        }

        String message = getBundle().getString(MESSAGE);

        if (!StringUtils.isEmpty(message)) {
            selectDialogItem.setMessage(message);
        }

        boolean noCancel = getBundle().getBoolean(NO_CANCEL, false);
        if (noCancel) {
            selectDialogItem.noCancel();
        }

    }

    public SelectDialogFragment setTitle(String title) {

        getBundle().putString(TITLE, title);
        return this;
    }

    public SelectDialogFragment setIcon(String mediaId) {
        getBundle().putString(ICON_MEDIAID, mediaId);
        return this;
    }

    public SelectDialogFragment setIcon(int resId) {
        getBundle().putInt(ICON_RESID, resId);
        return this;
    }

    public SelectDialogFragment setMessage(String message) {
        getBundle().putString(MESSAGE, message);
        return this;
    }

    public SelectDialogFragment noCancel() {
        getBundle().putBoolean(NO_CANCEL, true);
        return this;
    }

    private void registerListener() {
        selectDialogItem.cancelView.setOnClickListener(v -> SelectDialogFragment.this.dismiss());

        selectDialogItem.okView.setOnClickListener(v -> {
            if (onClickListener != null) {
                onClickListener.click();
            }
        });
    }


    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void click();
    }

}
