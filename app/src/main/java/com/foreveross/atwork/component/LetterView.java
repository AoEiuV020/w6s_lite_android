package com.foreveross.atwork.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingen on 15/4/8.
 * Description:字母索引表控件
 */
public class LetterView extends LinearLayout {

    private static final String TAG = LetterView.class.getSimpleName();

    private static final String[] letter = new String[]{
            "letter_1", "letter_A", "letter_B", "letter_C", "letter_D", "letter_E", "letter_F",
            "letter_G", "letter_H", "letter_I", "letter_J", "letter_K", "letter_L", "letter_M",
            "letter_N", "letter_O", "letter_P", "letter_Q", "letter_R", "letter_S", "letter_T",
            "letter_U", "letter_V", "letter_W", "letter_X", "letter_Y", "letter_Z", "letter_Z1"
    };

    private OnLetterSelectListener onLetterSelectListener;
    private List<TextView> letterList = new ArrayList<>();

    public LetterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.component_letter, this);

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float y = event.getY();
                float everyY = (view.getY() + view.getHeight()) / 28;
                int location = (int) (y / everyY);
                if (location > letterList.size() - 1) {
                    location = letterList.size() - 1;
                }
                TextView textView = letterList.get(location);
                if (onLetterSelectListener != null) {
                    onLetterSelectListener.onLetterSelect(textView.getText().toString());
                }

                return true;
            }
        });
        for (String letterView : letter) {
            TextView letter = (TextView) view.findViewWithTag(letterView);
            letterList.add(letter);
        }
    }

    public void setOnLetterSelectListener(OnLetterSelectListener onLetterSelectListener) {
        this.onLetterSelectListener = onLetterSelectListener;
    }

    /**
     * 字母选中事件传递
     */
    public interface OnLetterSelectListener {
        void onLetterSelect(String letter);
    }
}
