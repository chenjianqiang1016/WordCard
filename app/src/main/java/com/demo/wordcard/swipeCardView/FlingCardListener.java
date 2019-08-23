package com.demo.wordcard.swipeCardView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import com.demo.wordcard.Constant;
import com.demo.wordcard.SpUtil;
import com.demo.wordcard.util.DataUtil;

/**
 * Created by dionysis_lorentzos on 5/8/14
 * for package com.lorentzos.swipecards
 * and project Swipe cards.
 * Use with caution dinausaurs might appear!
 */
public class FlingCardListener implements View.OnTouchListener {

    private final float objectX;
    private final float objectY;
    private final int objectH;
    private final int objectW;
    private final int parentWidth;
    private final FlingListener mFlingListener;
    private final Object dataObject;
    private final float halfWidth;
    private final int scaledTouchSlop;
    private float BASE_ROTATION_DEGREES;

    private float aPosX;
    private float aPosY;
    private float aDownTouchX;
    private float aDownTouchY;
    private static final int INVALID_POINTER_ID = -1;

    // The active pointer is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;
    private View frame = null;

    private final int TOUCH_ABOVE = 0;
    private final int TOUCH_BELOW = 1;
    private int touchPosition;
    // private final Object obj = new Object();
    private boolean isAnimationRunning = false;
    private float MAX_COS = (float) Math.cos(Math.toRadians(45));
    // 支持左右滑
    private boolean isNeedSwipe = true;

    private float aTouchUpX;

    private int animDuration = 300;
    private float scale;

    /**
     * every time we touch down,we should stop the {@link #animRun}
     */
    private boolean resetAnimCanceled = false;

    //一卡多词时使用
    private float location_ws;
    private float location_we;

    private float location_1;
    private float location_2;
    private float location_3;
    private float location_4;
    private float location_5;
    private float location_6;

    //一卡一词时使用
    private float h1;
    private float h2;
    private float h3;

    private float w1;
    private float w2;
    private float w3;
    private float w4;


    private Context mContext;

    public FlingCardListener(Context context, View frame, Object itemAtPosition, float rotation_degrees, FlingListener flingListener) {
        super();

        mContext = context;

        this.frame = frame;
        this.objectX = frame.getX();
        this.objectY = frame.getY();
        this.objectW = frame.getWidth();
        this.objectH = frame.getHeight();
        this.halfWidth = objectW / 2f;
        this.dataObject = itemAtPosition;
        this.parentWidth = ((ViewGroup) frame.getParent()).getWidth();
        this.BASE_ROTATION_DEGREES = rotation_degrees;
        this.mFlingListener = flingListener;
        scaledTouchSlop = ViewConfiguration.get(frame.getContext()).getScaledTouchSlop();

        Log.e("FlingCardListener ", " FlingCardListener");


        try {

            boolean plural = SpUtil.INSTANCE.getBooleanValue(Constant.Companion.getWordCardPluralSetStatus(), true);

            if (plural) {
                //一卡多词
                String locations = SpUtil.INSTANCE.getStringValue(Constant.PluralWordLocationData, "");
                if (!TextUtils.isEmpty(locations)) {

                    String[] ls = locations.split("-");

                    location_ws = Float.parseFloat(ls[1]);
                    location_we = Float.parseFloat(ls[2]);

                    location_1 = Float.parseFloat(ls[3]);
                    location_2 = Float.parseFloat(ls[4]);
                    location_3 = Float.parseFloat(ls[5]);
                    location_4 = Float.parseFloat(ls[6]);
                    location_5 = Float.parseFloat(ls[7]);
                    location_6 = Float.parseFloat(ls[8]);
                }
            } else {

            }

        } catch (Exception e) {

        }


    }

    public void setIsNeedSwipe(boolean isNeedSwipe) {
        this.isNeedSwipe = isNeedSwipe;
    }

    private float Rx;
    private float Ry;

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        try {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:

                    // remove the listener because 'onAnimationEnd' will still be called if we cancel the animation.
                    this.frame.animate().setListener(null);
                    this.frame.animate().cancel();

                    resetAnimCanceled = true;

                    Rx = event.getRawX();
                    Ry = event.getRawY();

                    // Save the ID of this pointer
                    mActivePointerId = event.getPointerId(0);
                    final float x = event.getX(mActivePointerId);
                    final float y = event.getY(mActivePointerId);

                    // Remember where we started
                    aDownTouchX = x;
                    aDownTouchY = y;
                    // to prevent an initial jump of the magnifier, aposX and aPosY must
                    // have the values from the magnifier frame
                    aPosX = frame.getX();
                    aPosY = frame.getY();

                    if (y < objectH / 2) {
                        touchPosition = TOUCH_ABOVE;
                    } else {
                        touchPosition = TOUCH_BELOW;
                    }
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    // Extract the index of the pointer that left the touch sensor
                    final int pointerIndex = (event.getAction() &
                            MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    final int pointerId = event.getPointerId(pointerIndex);
                    if (pointerId == mActivePointerId) {
                        // This was our active pointer going up. Choose a new
                        // active pointer and adjust accordingly.
                        final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                        mActivePointerId = event.getPointerId(newPointerIndex);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:

                    // Find the index of the active pointer and fetch its position
                    final int pointerIndexMove = event.findPointerIndex(mActivePointerId);
                    final float xMove = event.getX(pointerIndexMove);
                    final float yMove = event.getY(pointerIndexMove);

                    // from http://android-developers.blogspot.com/2010/06/making-sense-of-multitouch.html
                    // Calculate the distance moved
                    final float dx = xMove - aDownTouchX;
                    final float dy = yMove - aDownTouchY;

//                    LogUtils.errorInfo("滑动距离","dx:"+dx+",dy:"+dy +",scaledTouchSlop:"+scaledTouchSlop);
//	                if (Math.abs(dx)<scaledTouchSlop/2 && Math.abs(dy)<scaledTouchSlop/2 && isClick){
//	                    return false;
//                    }else {
//	                    isClick = false;
//                    }
                    // Move the frame
                    aPosX += dx;
                    aPosY += dy;

//                    aPosX= aPosX * 0.5f;
//                    aPosY=aPosY*0.5f;

                    // calculate the rotation degrees
                    float distObjectX = aPosX - objectX;
                    float rotation = BASE_ROTATION_DEGREES * 2f * distObjectX / parentWidth;
//	                if (touchPosition == TOUCH_BELOW) {
//	                    rotation = -rotation;
//	                }

                    // in this area would be code for doing something with the view as the frame moves.
                    if (isNeedSwipe) {
                        frame.setX(aPosX);
                        frame.setY(aPosY);
                        frame.setRotation(rotation);
                        mFlingListener.onScroll(getScrollProgress(), getScrollXProgressPercent());
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    //mActivePointerId = INVALID_POINTER_ID;
                    int pointerCount = event.getPointerCount();
                    int activePointerId = Math.min(mActivePointerId, pointerCount - 1);
                    aTouchUpX = event.getX(activePointerId);
                    mActivePointerId = INVALID_POINTER_ID;
                    resetCardViewOnStack(event);
                    break;

                default: {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }


    private float getScrollProgress() {
        float dx = aPosX - objectX;
        float dy = aPosY - objectY;
        float dis = Math.abs(dx) + Math.abs(dy);
        return Math.min(dis, 400f) / 400f;
    }

    private float getScrollXProgressPercent() {
        if (movedBeyondLeftBorder()) {
            return -1f;
        } else if (movedBeyondRightBorder()) {
            return 1f;
        } else {
            float zeroToOneValue = (aPosX + halfWidth - leftBorder()) / (rightBorder() - leftBorder());
            return zeroToOneValue * 2f - 1f;
        }
    }

    private boolean resetCardViewOnStack(MotionEvent event) {
        if (isNeedSwipe) {
            final int duration = 200;
            if (movedBeyondLeftBorder()) {
                // Left Swipe
                onSelected(true, getExitPoint(-objectW), duration);
                mFlingListener.onScroll(1f, -1.0f);
            } else if (movedBeyondRightBorder()) {
                // Right Swipe
                onSelected(false, getExitPoint(parentWidth), duration);
                mFlingListener.onScroll(1f, 1.0f);
            } else {
                float absMoveXDistance = Math.abs(aPosX - objectX);
                float absMoveYDistance = Math.abs(aPosY - objectY);
                if (absMoveXDistance < 4 && absMoveYDistance < 4) {

                    mFlingListener.onClick(getWordIndex(Rx, Ry));
                } else {
                    frame.animate()
                            .setDuration(animDuration)
                            .setInterpolator(new OvershootInterpolator(1.5f))
                            .x(objectX)
                            .y(objectY)
                            .rotation(0)
                            .start();
                    scale = getScrollProgress();
                    this.frame.postDelayed(animRun, 0);
                    resetAnimCanceled = false;
                }
                aPosX = 0;
                aPosY = 0;
                aDownTouchX = 0;
                aDownTouchY = 0;
            }
        } else {
            float distanceX = Math.abs(aTouchUpX - aDownTouchX);

            if (distanceX < 4) {
                mFlingListener.onClick(getWordIndex(Rx, Ry));
            }

        }
        return false;
    }

    private int getWordIndex(float x, float y) {


        boolean plural = SpUtil.INSTANCE.getBooleanValue(Constant.Companion.getWordCardPluralSetStatus(), true);

        if (plural) {
            //一卡多词

            //点击偏移
            int clickOffset = DataUtil.Companion.dp2px(mContext, 10);

            if (x > location_ws && x < location_we) {
                //水平方向上，做个限制

                //判断Y轴方向上的区域
                if (y > location_1 + clickOffset && y < location_2 - clickOffset) {
                    return 0;
                } else if (y > location_2 + clickOffset && y < location_3 - clickOffset) {
                    return 1;
                } else if (y > location_3 + clickOffset && y < location_4 - clickOffset) {
                    return 2;
                } else if (y > location_4 + clickOffset && y < location_5 - clickOffset) {
                    return 3;
                } else if (y > location_5 + clickOffset && y < location_6 - clickOffset) {
                    return 4;

                } else {
                    return -1;
                }

            } else {
                return -1;
            }

        } else {
            //一卡一词

            try {

                //一卡一词
                String locations = SpUtil.INSTANCE.getStringValue(Constant.OneWordLocationData+"_0", "");
                if (!TextUtils.isEmpty(locations)) {

                    String[] ls = locations.split("-");

                    h1 = Float.parseFloat(ls[1]);
                    h2 = Float.parseFloat(ls[2]);
                    h3 = Float.parseFloat(ls[3]);

                    w1 = Float.parseFloat(ls[4]);
                    w2 = Float.parseFloat(ls[5]);
                    w3 = Float.parseFloat(ls[6]);
                    w4 = Float.parseFloat(ls[7]);

                    if ((y > h1 && y < h2 && x > w1 && x < w2) || (y > h2 && y < h3 && x > w3 && x < w4)) {
                        return 0;
                    } else {
                        return -1;
                    }


                } else {
                    return -1;
                }

            } catch (Exception e) {

                return -1;
            }

        }

    }

    private Runnable animRun = new Runnable() {
        @Override
        public void run() {
            mFlingListener.onScroll(scale, 0);
            if (scale > 0 && !resetAnimCanceled) {
                scale = scale - 0.1f;
                if (scale < 0)
                    scale = 0;
                frame.postDelayed(this, animDuration / 20);
            }
        }
    };

    private boolean movedBeyondLeftBorder() {
        return aPosX + halfWidth < leftBorder();
    }

    private boolean movedBeyondRightBorder() {
        return aPosX + halfWidth > rightBorder();
    }


    /**
     * 松开手指后，卡片滑出屏幕灵敏度调整规则
     * <p>
     * 1、左滑，增大leftBorder()方法中，"3.8"的值，可以减小灵敏度，让用户滑动更多距离，才会使得卡片移出屏幕
     * <p>
     * 2、右滑，减小rightBorder()方法中，"2.2"的值，可以减小灵敏度，让用户滑动更多距离，才会使得卡片移出屏幕
     */

    public float leftBorder() {
        return parentWidth / 3.8f;
    }

    public float rightBorder() {
        return 2.2f * parentWidth / 3f;
    }


    public void onSelected(final boolean isLeft, float exitY, long duration) {
        isAnimationRunning = true;
        float exitX;
        if (isLeft) {
            exitX = -objectW - getRotationWidthOffset();
        } else {
            exitX = parentWidth + getRotationWidthOffset();
        }

        this.frame.animate()
                .setDuration(duration)
                .setInterpolator(new LinearInterpolator())
                .translationX(exitX)
                .translationY(exitY)
                //.rotation(isLeft ? -BASE_ROTATION_DEGREES:BASE_ROTATION_DEGREES)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (isLeft) {
                            mFlingListener.onCardExited();
                            mFlingListener.leftExit(dataObject);
                        } else {
                            mFlingListener.onCardExited();
                            mFlingListener.rightExit(dataObject);
                        }
                        isAnimationRunning = false;
                    }
                }).start();
    }

    /**
     * Starts a default left exit animation.
     */
    public void selectLeft() {
        if (!isAnimationRunning)
            selectLeft(animDuration);
    }

    /**
     * Starts a default left exit animation.
     */
    public void selectLeft(long duration) {
        if (!isAnimationRunning)
            onSelected(true, objectY, duration);
    }

    /**
     * Starts a default right exit animation.
     */
    public void selectRight() {
        if (!isAnimationRunning)
            selectRight(animDuration);
    }

    /**
     * Starts a default right exit animation.
     */
    public void selectRight(long duration) {
        if (!isAnimationRunning)
            onSelected(false, objectY, duration);
    }

    private float getExitPoint(int exitXPoint) {
        float[] x = new float[2];
        x[0] = objectX;
        x[1] = aPosX;

        float[] y = new float[2];
        y[0] = objectY;
        y[1] = aPosY;

        LinearRegression regression = new LinearRegression(x, y);

        //Your typical y = ax+b linear regression
        return (float) regression.slope() * exitXPoint + (float) regression.intercept();
    }

    private float getExitRotation(boolean isLeft) {
        float rotation = BASE_ROTATION_DEGREES * 2f * (parentWidth - objectX) / parentWidth;
        if (touchPosition == TOUCH_BELOW) {
            rotation = -rotation;
        }
        if (isLeft) {
            rotation = -rotation;
        }
        return rotation;
    }

    /**
     * When the object rotates it's width becomes bigger.
     * The maximum width is at 45 degrees.
     * <p>
     * The below method calculates the width offset of the rotation.
     */
    private float getRotationWidthOffset() {
        return objectW / MAX_COS - objectW;
    }


    public void setRotationDegrees(float degrees) {
        this.BASE_ROTATION_DEGREES = degrees;
    }


    protected interface FlingListener {
        void onCardExited();

        void leftExit(Object dataObject);

        void rightExit(Object dataObject);

        void onClick(int index);

        void onScroll(float progress, float scrollXProgress);
    }


}

