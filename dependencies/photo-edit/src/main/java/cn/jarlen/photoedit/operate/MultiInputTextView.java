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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;

import java.util.ArrayList;
import java.util.List;


public class MultiInputTextView extends View {
    private Context mContext;
    private List<ImageObject> mImgLists = new ArrayList<>();
    private Rect mCanvasLimits;
    private Bitmap bgBmp;
    private Paint mPaint = new Paint();
    //	private Context mContext;
    private float mPicScale = 0.4f;

    private int mColor;


    private boolean mIsMultiAdd;// true 代表可以添加多个水印图片（或文字），false 代表只可添加单个水印图片（或文字）

    private boolean mNeedRotate = false;  //是否需要旋转功能


    public MultiInputTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        mContext = context;
    }

    /**
     * 设置水印图片初始化大小
     *
     * @param picScale
     */
    public void setPicScale(float picScale) {
        this.mPicScale = picScale;
    }

    /**
     * 设置是否可以添加多个图片或者文字对象
     *
     * @param isMultiAdd true 代表可以添加多个水印图片（或文字），false 代表只可添加单个水印图片（或文字）
     */
    public void setMultiAdd(boolean isMultiAdd) {
        this.mIsMultiAdd = isMultiAdd;
    }

    public void setBitmap(Bitmap resizeBmp) {
        bgBmp = resizeBmp;
        int width = bgBmp.getWidth();
        int height = bgBmp.getHeight();
        mCanvasLimits = new Rect(0, 0, width, height);
    }


    public void setColor(int color) {
        mColor = color;
    }

    /**
     * 将图片对象添加到View中
     *
     * @param imgObj 图片对象
     */
    public void addItem(ImageObject imgObj) {
        if (imgObj == null) {
            return;
        }
        if (!mIsMultiAdd && mImgLists != null) {
            mImgLists.clear();
        }
        imgObj.setSelected(true);
        if (!imgObj.isTextObject) {
            imgObj.setScale(mPicScale);
        }
        ImageObject tempImgObj = null;
        for (int i = 0; i < mImgLists.size(); i++) {
            tempImgObj = mImgLists.get(i);
            tempImgObj.setSelected(false);
        }
        mImgLists.add(imgObj);
        invalidate();
    }

    /**
     * 画出容器内所有的图像
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null != mCanvasLimits) {
            int sc = canvas.save();
            canvas.clipRect(mCanvasLimits);
            canvas.drawBitmap(bgBmp, 0, 0, mPaint);

            drawImages(canvas);

            canvas.restoreToCount(sc);

            drawIcons(canvas);
        }
    }

    private void drawIcons(Canvas canvas) {
        for (ImageObject ad : mImgLists) {
            if (null != ad && ad.isNeedShow() && ad.isSelected()) {
                ad.drawIconAndRectLines(canvas);
            }
        }
    }

    public void save() {
        ImageObject io = getSelected();
        if (io != null) {
            io.setSelected(false);
        }
        invalidate();
    }

    /**
     * 根据触控点重绘View
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() == 1) {
            handleSingleTouchManipulateEvent(event);
        } else {
            handleMultiTouchManipulateEvent(event);
        }
        invalidate();

        super.onTouchEvent(event);
        return true;
    }

    private boolean mMovedSinceDown = false;
    private boolean mResizeAndRotateSinceDown = false;
    private boolean mShouldClick = false;
    private float mStartDistance = 0.0f;
    private float mStartScale = 0.0f;
    private float mStartRot = 0.0f;
    private float mPrevRot = 0.0f;
    static public final double ROTATION_STEP = 2.0;
    static public final double ZOOM_STEP = 0.01;
    static public final float CANVAS_SCALE_MIN = 0.25f;
    static public final float CANVAS_SCALE_MAX = 3.0f;
    private Point mPreviousPos = new Point(0, 0); // single touch events
    float diff;
    float rot;

    /**
     * 多点触控操作
     *
     * @param event
     */
    private void handleMultiTouchManipulateEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                float x1 = event.getX(0);
                float x2 = event.getX(1);
                float y1 = event.getY(0);
                float y2 = event.getY(1);
                float delX = (x2 - x1);
                float delY = (y2 - y1);
                diff = (float) Math.sqrt((delX * delX + delY * delY));
                mStartDistance = diff;
                // float q = (delX / delY);
                mPrevRot = (float) Math.toDegrees(Math.atan2(delX, delY));
                for (ImageObject io : mImgLists) {
                    if (io.isSelected()) {
                        mStartScale = io.getScale();
                        mStartRot = io.getRotation();
                        break;
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                //双指操作不能误判为点击事件
                mShouldClick = false;

                x1 = event.getX(0);
                x2 = event.getX(1);
                y1 = event.getY(0);
                y2 = event.getY(1);
                delX = (x2 - x1);
                delY = (y2 - y1);
                diff = (float) Math.sqrt((delX * delX + delY * delY));
                float scale = diff / mStartDistance;
                float newscale = mStartScale * scale;
                rot = (float) Math.toDegrees(Math.atan2(delX, delY));

                float rotdiff = 0;
                if (mNeedRotate) {
                    rotdiff = mPrevRot - rot;
                }

                for (ImageObject io : mImgLists) {
                    if (io.isSelected() && newscale < 10.0f && newscale > 0.1f) {
                        float newrot = Math.round((mStartRot + rotdiff) / 1.0f);
                        if (Math.abs((newscale - io.getScale()) * ROTATION_STEP) > Math
                                .abs(newrot - io.getRotation())) {
                            io.setScale(newscale);
                        } else {
                            io.setRotation(newrot % 360);
                        }
                        break;
                    }
                }

                break;
        }
    }

    /**
     * 获取选中的对象ImageObject
     *
     * @return
     */
    @Nullable
    public ImageObject getSelected() {
        for (ImageObject ibj : mImgLists) {
            if (ibj.isSelected()) {
                return ibj;
            }
        }
        return null;
    }

    public List<ImageObject> getImageObjList() {
        return mImgLists;
    }


    private long selectTime = 0;

    /**
     * 单点触控操作
     *
     * @param event
     */
    private void handleSingleTouchManipulateEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mMovedSinceDown = false;
                mResizeAndRotateSinceDown = false;
                mShouldClick= false;
                int selectedId = -1;

                for (int i = mImgLists.size() - 1; i >= 0; --i) {
                    ImageObject io = mImgLists.get(i);
                    if (io.contains(event.getX(), event.getY())
                            || io.pointOnCorner(event.getX(), event.getY(),
                            OperateConstants.RIGHTBOTTOM)
                            || io.pointOnCorner(event.getX(), event.getY(),
                            OperateConstants.LEFTTOP)) {
                        mImgLists.remove(i);
                        mImgLists.add(io);
                        selectedId = mImgLists.size() - 1;

                        if(!io.isSelected()) {
                            io.setSelected(true);

                        } else {
                            mShouldClick = true;

                        }


                        break;
                    }
                }


                if (-1 != selectedId) {

                    for (int i = 0; i < mImgLists.size(); ++i) {
                        ImageObject io = mImgLists.get(i);
                        if (i != selectedId) {
                            io.setSelected(false);
                        }
                    }
                }

                ImageObject io = getSelected();
                if(-1 != selectedId && null != io) {
                    if (io.pointOnCorner(event.getX(), event.getY(),
                            OperateConstants.LEFTTOP)) {
                        mImgLists.remove(io);

                    } else if (io.pointOnCorner(event.getX(), event.getY(),
                            OperateConstants.RIGHTBOTTOM)) {
                        mResizeAndRotateSinceDown = true;
                        float x = event.getX();
                        float y = event.getY();
                        float delX = x - io.getPoint().x;
                        float delY = y - io.getPoint().y;
                        diff = (float) Math.sqrt((delX * delX + delY * delY));
                        mStartDistance = diff;
                        mPrevRot = (float) Math.toDegrees(Math
                                .atan2(delX, delY));
                        mStartScale = io.getScale();
                        mStartRot = io.getRotation();
                    } else if (io.contains(event.getX(), event.getY())) {
                        mMovedSinceDown = true;
                        mPreviousPos.x = (int) event.getX();
                        mPreviousPos.y = (int) event.getY();
                    }

                } else {

                    if(null != io) {
                        io.setSelected(false);

                    } else {
                        //clear other new TextObject
//                        clearNewTextObjs();

                        newTextObj((int) event.getX(), (int) event.getY());
                    }


                }

                break;

            case MotionEvent.ACTION_UP:

                if(mShouldClick) {
                    if (null != mOnEditTextListener) {
                        if (null != getSelected() && getSelected().isTextObject()) {
                            mOnEditTextListener.onClick((TextObject) getSelected());

                        }
                    }
                }

                mMovedSinceDown = false;
                mResizeAndRotateSinceDown = false;
                mShouldClick = false;

                break;

            case MotionEvent.ACTION_MOVE:

                 Log.i("jarlen"," 移动了");
                // 移动
                if (mMovedSinceDown) {
                    int curX = (int) event.getX();
                    int curY = (int) event.getY();
                    int diffX = curX - mPreviousPos.x;
                    int diffY = curY - mPreviousPos.y;

                    Log.e("test" , "test diffX -> " + diffX);
                    Log.e("test" , "test diffy -> " + diffY);

                    if(8 < Math.abs(diffX) || 8 < Math.abs(diffY)) {
                        mShouldClick = false;

                    } else {
                        //if is click event, then just return
                        break;
                    }


                    mPreviousPos.x = curX;
                    mPreviousPos.y = curY;
                    io = getSelected();
                    Point p = io.getPosition();
                    int x = p.x + diffX;
                    int y = p.y + diffY;
                    if (p.x + diffX >= mCanvasLimits.left
                            && p.x + diffX <= mCanvasLimits.right
                            && p.y + diffY >= mCanvasLimits.top
                            && p.y + diffY <= mCanvasLimits.bottom)
                        io.moveBy(diffX, diffY);
                }

                // 旋转和缩放
                if (mResizeAndRotateSinceDown) {

                    mShouldClick = false;

                    io = getSelected();
                    float x = event.getX();
                    float y = event.getY();
                    float delX = x - io.getPoint().x;
                    float delY = y - io.getPoint().y;
                    diff = (float) Math.sqrt((delX * delX + delY * delY));
                    float scale = diff / mStartDistance;
                    float newscale = mStartScale * scale;
                    rot = (float) Math.toDegrees(Math.atan2(delX, delY));
                    float rotdiff = 0;
                    if (mNeedRotate) {
                        rotdiff = mPrevRot - rot;
                    }
                    if (newscale < 10.0f && newscale > 0.1f) {
                        float newrot = Math.round((mStartRot + rotdiff) / 1.0f);
                        if (Math.abs((newscale - io.getScale()) * ROTATION_STEP) > Math
                                .abs(newrot - io.getRotation())) {
                            io.setScale(newscale);
                        } else {
                            io.setRotation(newrot % 360);
                        }
                    }
                }
                break;
        }

        cancelLongPress();

    }

    public void newTextObj(int x, int y) {
        TextObject textObj = TextObject.getNewTextObject(mContext, x, y);

        if(textObj != null){
            textObj.setColor(mColor);
            textObj.setTextSize(DensityUtil.sp2px(24));
            textObj.commit();
            addItem(textObj);
        }
    }

    /**
     * 循环画图像
     *
     * @param canvas
     */
    private void drawImages(Canvas canvas) {
        for (ImageObject ad : mImgLists) {
            if (null != ad && ad.isNeedShow()) {
                ad.draw(canvas);
            }
        }
    }

    public Bitmap getBitmapByView() {
        clearNewTextObjs();
        clearSelectStatuObjs();

        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        return bitmap;
    }

    public void clearInputText() {
        mImgLists.clear();
    }

    /**
     * 隐藏"请输入文字"的图层
     * */
    public void hideNewTextObjs() {
        for (ImageObject io : mImgLists) {
            if(io instanceof TextObject && !((TextObject)io).hasInputContent()) {
                io.setNeedShow(false);
            }
        }

        invalidate();

    }

    /**
     * 显示所有图层
     * */
    public void showAllTextObjs() {
        for (ImageObject io : mImgLists) {
            io.setNeedShow(true);
        }

        invalidate();
    }


    private void clearSelectStatuObjs() {
        for (ImageObject io : mImgLists) {
            io.setSelected(false);
        }
    }

    private void clearNewTextObjs() {
        List<ImageObject> ioRemovedList = new ArrayList<>();

        for (ImageObject io : mImgLists) {
            if(io instanceof TextObject && !((TextObject)io).hasInputContent()) {
                ioRemovedList.add(io);
            }
        }

        mImgLists.removeAll(ioRemovedList);
    }

    public boolean isModified() {
        return !ListUtil.isEmpty(mImgLists);
    }


    /**
     * 向外部提供双击监听事件（双击弹出自定义对话框编辑文字）
     */
    OnEditTextListener mOnEditTextListener;

    public void setOnEditTextListener(OnEditTextListener onEditTextListener) {
        this.mOnEditTextListener = onEditTextListener;
    }

    public interface OnEditTextListener {
        void onClick(TextObject tObject);
    }
}
