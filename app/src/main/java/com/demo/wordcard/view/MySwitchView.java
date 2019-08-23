package com.demo.wordcard.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;

/**
 * 滑动开关
 */
public class MySwitchView extends View {

    //左圆半径
    private int center;
    //矩形x坐标
    private int rec_x;
    //画笔
    private Paint paint;
    //控件宽
    private int measuredWidth;
    //控件高
    private int measuredHeight;
    //小圆半径
    private int smallCenter;
    //小圆的x坐标
    private float smallCenter_x;
    //小圆画笔
    private Paint smallPaint;
    //按下的x坐标
    private float startx;
    //移动的结束坐标
    private float endx;
    //左圆圆心和右圆圆心中间的坐标
    private int mid_x;

    private int openColor=0xFFFE7325;
    private int closeColor=0xffdddddd;

    public MySwitchView(Context context) {
        this(context, null);
    }

    public MySwitchView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MySwitchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    //回调，点击事件
    private OnMClickListener onClickListener;

    public void setOnMbClickListener(OnMClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnMClickListener {
        void onClick(boolean isRight);
    }


    private void init() {
        //初始化一些数据
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        smallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(openColor);
        smallPaint.setColor(Color.WHITE);
    }

    //往左
    public static final int TO_LEFT = 11;
    //往右
    public static final int TO_RIGHT = 22;

    private boolean isRight = true;

    private boolean isAnimate = false;

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case TO_LEFT:
                    paint.setColor(closeColor);
                    if (smallCenter_x > center) {
                        smallCenter_x -= 5;
                        handler.sendEmptyMessageDelayed(TO_LEFT, 1);
                        isAnimate = true;
                    } else {
                        smallCenter_x = center;
                        //设置滑动不可点击
                        setEnabled(true);
                        isAnimate = false;
                    }
                    break;
                case TO_RIGHT:
                    paint.setColor(openColor);
                    if (smallCenter_x < rec_x) {
                        smallCenter_x += 5;
                        handler.sendEmptyMessageDelayed(TO_RIGHT, 1);
                        isAnimate = true;
                    } else {
                        smallCenter_x = rec_x;
                        setEnabled(true);
                        isAnimate = false;
                    }
                    break;
            }
            //重绘
            invalidate();

            return false;
        }
    });

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measuredHeight = getMeasuredHeight();
        measuredWidth = getMeasuredWidth();
        //得出左圆，长方形，右圆的坐标
        center = measuredHeight / 2;
        //长方形右边的坐标
        rec_x = measuredWidth - center;
        //小圆的半径 = 大圆半径减5
        smallCenter = center - 5;
        //小圆的圆心x坐标一直在变化
        smallCenter_x = rec_x;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //左圆
        canvas.drawCircle(center, center, center, paint);
        //矩形
        canvas.drawRect(center, 0, rec_x, measuredHeight, paint);
        //右圆
        canvas.drawCircle(rec_x, center, center, paint);
        //小圆
        canvas.drawCircle(smallCenter_x, center, smallCenter, smallPaint);
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //开始的x坐标
                startx = event.getX();
                endx = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float distance = event.getX() - endx;
                smallCenter_x += distance;
                //控制范围
                if (smallCenter_x > rec_x) {
                    isRight = true;
                    smallCenter_x = rec_x;
                    paint.setColor(openColor);
                } else if (smallCenter_x < center) {
                    //最左
                    smallCenter_x = center;
                    isRight = false;
                    paint.setColor(closeColor);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                //分2种情况，1.点击 2.没滑过中点
                //1.点击
                float up_x = event.getX();
                //按下和抬起的距离小于2确定是点击了
                if (Math.abs(up_x - Math.abs(startx)) < 2) {
                    //不在动画的时候可以点击
                    if (!isAnimate) {
                        startGO();
                    }
                } else {
                    //2.没滑过中点
                    //滑到中间的x坐标
                    mid_x = (center + (rec_x - center) / 2);
                    if (smallCenter_x < mid_x) {
                        //最左
                        isRight = false;
                        smallCenter_x = center;
                        paint.setColor(closeColor);
                        setEnabled(true);
                        invalidate();
                    } else {
                        //最右
                        isRight = true;
                        smallCenter_x = rec_x;
                        paint.setColor(openColor);
                        setEnabled(true);
                        invalidate();
                    }
                }
                //到了两端都有点击事件
                if (smallCenter_x == rec_x || smallCenter_x == center) {
                    if (onClickListener != null) {
                        onClickListener.onClick(isRight);
                    }
                }
                break;
        }
        return true;
    }

    //提供方法调用
    public void goLeft() {
        isRight = false;
        handler.sendEmptyMessageDelayed(TO_LEFT, 40);
    }

    public void goRight() {
        isRight = true;
        handler.sendEmptyMessageDelayed(TO_RIGHT, 40);
    }

    public void startGO() {
        if (isRight) {
            goLeft();
        } else {
            goRight();
        }
    }

    public boolean getStatus(){
        return isRight;
    }



}
