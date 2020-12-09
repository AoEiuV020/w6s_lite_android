/*
 *          Copyright (C) 2016 jarlen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package cn.jarlen.photoedit.operate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Parcel;

import com.foreveross.atwork.infrastructure.utils.StringUtils;

import cn.jarlen.photoedit.R;


public class TextObject extends ImageObject {

    private int textSize = 90;
    private int color = Color.BLACK;
    private String typeface;
    private String text;
    private boolean bold = false;
    private boolean italic = false;
    private Context context;
    private boolean hasInputContent = false;

    Paint paint = new Paint();

    /**
     * 构造方法
     *
     * @param context  上下文
     * @param text     输入的文字
     * @param x        位置x坐标
     * @param y        位置y坐标
     * @param rotateBm 旋转按钮的图片
     * @param deleteBm 删除按钮的图片
     */
    public TextObject(Context context, String text, int x, int y,
                      Bitmap rotateBm, Bitmap deleteBm) {
        super();
        this.context = context;
        this.text = text;
        mPoint.x = x;
        mPoint.y = y;
        this.rotateBm = rotateBm;
        this.deleteBm = deleteBm;
        regenerateBitmap();
    }

    public TextObject() {
    }

    /**
     * 绘画出字体
     */
    public void regenerateBitmap() {
        paint.setAntiAlias(true);
        paint.setTextSize(textSize);
//		paint.setTypeface(getTypefaceObj());
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setDither(true);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        String showText = getShowText(context);

        String lines[] = showText.split("\n");

        int textWidth = 0;
        for (String str : lines) {
            int temp = (int) paint.measureText(str);
            if (temp > textWidth)
                textWidth = temp;
        }
        if (textWidth < 1)
            textWidth = 1;
        if (srcBm != null)
            srcBm.recycle();

        int offset = 8;
        //特殊高度, 需要增大偏移量
        if(showText.contains("y") || showText.contains("j")
                || showText.contains("p") || showText.contains("q")
                || showText.contains("g")) {

            offset = 16;
        }
        srcBm = Bitmap.createBitmap(textWidth, textSize * (lines.length) + offset,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(srcBm);
        canvas.drawARGB(0, 0, 0, 0);
        for (int i = 1; i <= lines.length; i++) {
            canvas.drawText(lines[i - 1], 0, i * textSize, paint);
        }
        setCenter();
    }

    /**
     * 设置字体样式
     *
     * @return Typeface 默认系统字体，设置属性后变换字体 目前支持本地两种字体 by3500.ttf 、 bygf3500.ttf
     */

    public Typeface getTypefaceObj() {
        Typeface tmptf = Typeface.DEFAULT;
        if (typeface != null) {
            if (OperateConstants.FACE_BY.equals(typeface)
                    || OperateConstants.FACE_BYGF.equals(typeface)) {
                tmptf = Typeface.createFromAsset(context.getAssets(), "fonts/"
                        + typeface + ".ttf");
            }
        }
        if (bold && !italic)
            tmptf = Typeface.create(tmptf, Typeface.BOLD);
        if (italic && !bold)
            tmptf = Typeface.create(tmptf, Typeface.ITALIC);
        if (italic && bold)
            tmptf = Typeface.create(tmptf, Typeface.BOLD_ITALIC);
        return tmptf;
    }

    /**
     * 设置属性值后，提交方法
     */
    public void commit() {
        regenerateBitmap();
    }

    /**
     * 公共的getter和setter方法
     */
    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTypeface() {
        return typeface;
    }

    public void setTypeface(String typeface) {
        this.typeface = typeface;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public int getX() {
        return mPoint.x;
    }

    public void setX(int x) {
        this.mPoint.x = x;
    }

    public int getY() {
        return mPoint.y;
    }

    public void setY(int y) {
        this.mPoint.y = y;
    }

    public String getEditText() {
        if(hasInputContent) {
            return text;

        }

        return StringUtils.EMPTY;
    }


    public String getShowText(Context context) {
        if(hasInputContent) {
            return text;
        }

        return context.getString(R.string.pls_input_text);
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * 添加图片的方法
     * @param x           离边界x坐标
     * @param y           离边界y坐标
     * @return
     */
    public static TextObject getNewTextObject(Context context, int x, int y) {
        TextObject textObj;

        Bitmap rotateBm = BitmapFactory.decodeResource(context.getResources(),
                cn.jarlen.photoedit.R.drawable.scale);
        Bitmap deleteBm = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.delete);
        textObj = new TextObject(context, StringUtils.EMPTY, x, y, rotateBm, deleteBm);
        textObj.setTextObject(true);
        return textObj;
    }

    /**
     * 是否有输入内容
     * */
    public boolean hasInputContent() {
        return hasInputContent;
    }

    public void setInputContent(boolean hasInputContent) {
        this.hasInputContent = hasInputContent;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.textSize);
        dest.writeInt(this.color);
        dest.writeString(this.typeface);
        dest.writeString(this.text);
        dest.writeByte(this.bold ? (byte) 1 : (byte) 0);
        dest.writeByte(this.italic ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.mPoint, flags);
        dest.writeFloat(this.mRotation);
        dest.writeFloat(this.mScale);
        dest.writeByte(this.mSelected ? (byte) 1 : (byte) 0);
        dest.writeByte(this.flipVertical ? (byte) 1 : (byte) 0);
        dest.writeByte(this.flipHorizontal ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isTextObject ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.srcBm, flags);
        dest.writeParcelable(this.rotateBm, flags);
        dest.writeParcelable(this.deleteBm, flags);
        dest.writeInt(this.first);

    }

    protected TextObject(Parcel in) {
        super(in);
        this.textSize = in.readInt();
        this.color = in.readInt();
        this.typeface = in.readString();
        this.text = in.readString();
        this.bold = in.readByte() != 0;
        this.italic = in.readByte() != 0;
        this.mPoint = in.readParcelable(Point.class.getClassLoader());
        this.mRotation = in.readFloat();
        this.mScale = in.readFloat();
        this.mSelected = in.readByte() != 0;
        this.flipVertical = in.readByte() != 0;
        this.flipHorizontal = in.readByte() != 0;
        this.isTextObject = in.readByte() != 0;
        this.srcBm = in.readParcelable(Bitmap.class.getClassLoader());
        this.rotateBm = in.readParcelable(Bitmap.class.getClassLoader());
        this.deleteBm = in.readParcelable(Bitmap.class.getClassLoader());
        this.first = in.readInt();

    }

    public static final Creator<TextObject> CREATOR = new Creator<TextObject>() {
        @Override
        public TextObject createFromParcel(Parcel source) {
            return new TextObject(source);
        }

        @Override
        public TextObject[] newArray(int size) {
            return new TextObject[size];
        }
    };
}
