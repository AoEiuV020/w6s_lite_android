package com.foreveross.atwork.infrastructure.utils;

import android.os.Build;
import android.view.View;
import android.widget.PopupWindow;

/**
 * Created by dasunsy on 2016/12/28.
 */

public class PopupWindowCompat {

    public static void showAsDropDown(PopupWindow popup, View anchor, int xoff, int yoff,
                                      int gravity) {
        androidx.core.widget.PopupWindowCompat.showAsDropDown(popup, anchor, xoff, yoff, gravity);

//        if (24 <= Build.VERSION.SDK_INT) {
//            int[] location = new int[2];
//            anchor.getLocationInWindow(location);
//            popup.showAtLocation(anchor, gravity,  location[0] + xoff, location[1] + anchor.getHeight() + yoff);
//        } else {
//            android.support.v4.widget.PopupWindowCompat.showAsDropDown(popup, anchor, xoff, yoff, gravity);
//        }
    }




    public static void showAsPopUp(PopupWindow popup, View anchor, int xoff, int yoff,
                                   int gravity, int popupHeight) {
        if (24 <= Build.VERSION.SDK_INT) {
            int[] location = new int[2];
            anchor.getLocationInWindow(location);
            popup.showAtLocation(anchor, gravity,  location[0] + xoff, location[1] - popupHeight - yoff);
        } else {
            androidx.core.widget.PopupWindowCompat.showAsDropDown(popup, anchor, xoff, yoff, gravity);
        }
    }
}
