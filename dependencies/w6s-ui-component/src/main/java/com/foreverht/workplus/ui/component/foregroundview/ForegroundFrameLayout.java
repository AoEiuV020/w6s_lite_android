package com.foreverht.workplus.ui.component.foregroundview;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class ForegroundFrameLayout extends FrameLayout implements  IForegroundView{

    public ForegroundFrameLayout(@NonNull Context context) {
        super(context);
    }

    public ForegroundFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
}
