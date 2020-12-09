package com.foreveross.atwork.infrastructure.beeworks;/**
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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 */


/**
 * Created by reyzhang22 on 16/3/11.
 */
public class BeeWorksGrid {

    public enum Navigation {
        WEST {
            @Override
            public Navigation fromString(String navi) {
                return WEST;
            }
        },

        NORTH {
            @Override
            public Navigation fromString(String navi) {
                return NORTH;
            }
        };


        abstract public Navigation fromString(String navi);
    }

    /**
     * 图标
     */
    public String mIcon;

    /**
     * 名称
     */
    public String mTitle;

    /**
     * 文字颜色
     */
    public String mFontColor;

    /**
     * 点击动作
     */
    public String mActionType;

    /**
     * 动作值
     */
    public String mValue;

    /**
     * host主机地址
     */
    public String mTipUrl;

    /**
     * 显示模式
     */
    public String mDisplayMode;

    /**
     * 方位
     */
    public Navigation mNavi;

}
