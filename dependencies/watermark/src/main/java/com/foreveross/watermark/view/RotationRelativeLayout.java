package com.foreveross.watermark.view;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

public class RotationRelativeLayout extends RelativeLayout {
    public RotationRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RotationRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RotationRelativeLayout(Context context) {
        super(context);
        init();
    }

    private void init() {
        setStaticTransformationsEnabled(true);
    }

    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t) {
        t.setTransformationType(Transformation.TYPE_MATRIX);
        Matrix m = t.getMatrix();
        m.reset();
        m.postRotate(-17, child.getWidth() / 2.0f, child.getHeight() / 2.0f);
        return true;
    }
}