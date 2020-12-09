package com.foreveross.atwork.modules.contact.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class ContactObservableScrollView extends ScrollView {

    private ContactScrollViewListener scrollViewListener = null;

    public ContactObservableScrollView(Context context) {
        super(context);
    }
    public ContactObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public ContactObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public void setScrollViewListener(ContactScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }
}
