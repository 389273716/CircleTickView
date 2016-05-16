package com.tc.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;


import java.math.BigDecimal;


/**
 * author：   tc
 * date     2016/4/28  12:09
 * version    1.0
 * description  圆形刻度view
 * modify by
 */
public class CircleTickView extends View {
    private static final String TAG = CircleTickView.class.getSimpleName();
    /**
     * 被选中刻度的颜色
     */
    private int mSelectTickColor;
    /**
     * 刻度的粗细
     */
    private int mTickStrokeSize;

    /**
     * 刻度的总数
     */
    private int mTickMaxCount;
    /**
     * 默认刻度的颜色
     */
    private int mDefaultTickColor;
    /**
     * 中间文本颜色
     */
    private int mCenterTextColor;
    /**
     * 中间文本描述
     */
    private String mCenterText;
    /**
     * 中间文本大小
     */
    private float mCenterTextSize;
    /**
     * 底部文本颜色
     */
    private int mBottomTextColor;
    /**
     * 底部文本描述
     */
    private String mBottomText;
    /**
     * 底部文本大小
     */
    private float mBottomTextSize;
    /**
     * 内切大圆半径，
     */
    private int mCircleRadius;
    /**
     * 圆环的paint
     */
    private Paint mCircleRingPaint;
    /**
     * 中间文本的paint
     */
    private Paint mCenterTextPaint;
    /**
     * 底部文本的paint
     */
    private Paint mBottomTextPaint;
    /**
     * 圆心的x坐标
     */
    private int mCircleCenterX;
    /**
     * 角度，单个间隔块所占的角度
     */
    private float mSinglPoint;
    /**
     * 圆环内的半径
     */
    private int mCircleRingRadius;
    private RectF mRecf;
    /**
     * 当前绘制位置的百分比值，比如50
     */
    private double mCurrentProgress;
    /**
     * 圆环背景色
     */
    private int mRoundBackgroundColor;
    /**
     * 间隔条角度大小
     */
    private float mLineWidth = 0.3f;
    /**
     * 中间文字的属性
     */
    private Rect mCenterTextBounds;
    /**
     * 垂直方向的padding
     */
    private int mVerticalPadding;
    /**
     * 控件高
     */
    private int mHeight;
    /**
     * 控件宽
     */
    private int mWidth;
    /**
     * 大圆圆心坐标y
     */
    private int mCircleCenterY;
    /**
     * 刷新界面的动画
     */
    private ViewRefreshAnimation mBarAnimation;

    private static final long DEFAULLT_TIME_LENGTH = 15 * 60 * 1000;

    /**
     * 当前设置的时间
     */
    private long mCurrentTime;
    /**
     * 上一次的设置时间，本字段用于动画效果辅助。
     */
    private long mLastTime;
    /**
     * 设置时间的最大值
     */
    private long mMaxTime = DEFAULLT_TIME_LENGTH;
    /**
     * 选中的刻度总数
     */
    private int mSelectTickCount;
    /**
     * 上一次的刻度选中点
     */
    private int mLastSelectTickCount;
    /**
     * 动画时间
     */
    private int mAnimDuration;

    /**
     * 是否自动倒计时，此时不走正常的触摸渠道，并且屏蔽view触摸事件，只能外界关闭自动倒计时
     */
    private boolean mIsLockTouch;

    /**
     * 刻度盘起始时间
     */
    private long mStartTime;
    /**
     * 是否允许触摸view使刻度归零
     */
    private boolean mIsCanResetZero;

    public interface OnTimeChangeListener {
        void onChange(long time, int tickCount);
    }

    private OnTimeChangeListener mOnTimeChangeListener;

    public void setOnTimeChangeListener(OnTimeChangeListener onTimeChangeListener) {
        mOnTimeChangeListener = onTimeChangeListener;
    }

    public CircleTickView(Context context) {
        this(context, null);
    }

    public CircleTickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleTickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 获取刻度盘起始时间
     *
     * @return 刻度盘起始时间
     */
    public long getStartTime() {
        return mStartTime;
    }

    /**
     * 设置刻度盘起始时间
     *
     * @param startTime 刻度盘起始时间
     */
    public void setStartTime(long startTime) {
        mStartTime = startTime;
    }

    /**
     * 是否允许触摸view使刻度归零
     *
     * @return 是否允许触摸view使刻度归零
     */
    public boolean isCanResetZero() {
        return mIsCanResetZero;
    }

    /**
     * 设置是否允许触摸view使刻度归零
     *
     * @param isCanResetZero 是否允许触摸view使刻度归零
     */
    public void setIsCanResetZero(boolean isCanResetZero) {
        mIsCanResetZero = isCanResetZero;
    }

    /**
     * 设置当前百分比的值
     *
     * @param currentProgress 当前百分比，0-100
     */
    public void setCurrentProgress(double currentProgress) {
        this.setCurrentProgress(currentProgress, true);
    }

    /**
     * 设置当前百分比的值
     *
     * @param currentProgress 当前百分比，0-100
     * @param isAnim          是否动画
     */
    public void setCurrentProgress(double currentProgress, boolean isAnim) {
        mCurrentProgress = currentProgress;
        BigDecimal bigDecimal = new BigDecimal(mCurrentProgress / 100 * mTickMaxCount)
                .setScale(0, BigDecimal.ROUND_HALF_UP);
        mSelectTickCount = bigDecimal.intValue();
        setSelectTickCount(mSelectTickCount, isAnim);
    }

    /**
     * 设置当前选择的刻度总数
     *
     * @param selectTickCount 选中的刻度
     * @param isAnim          是否动画
     */
    public void setSelectTickCount(int selectTickCount, boolean isAnim) {
        mLastSelectTickCount = mSelectTickCount;//记录上一次的刻度值，以便触摸动画的起始位置从上一次开始增加或者减少
        mLastTime = mCurrentTime;
        mSelectTickCount = selectTickCount;
        mCurrentTime = (mMaxTime - mStartTime) / mTickMaxCount * mSelectTickCount +
                mStartTime;
        //总时间减去起始时间求出每个刻度的时间，根据当前刻度选中数曾以刻度然后加上起始时间就是当前时间
        if (mBarAnimation != null && isAnim) {
            startAnimation(mBarAnimation);
        } else {
            mAnimTickCount = mSelectTickCount;
            mCenterText = formatTime(mCurrentTime);
            invalidate();
        }
        if (mOnTimeChangeListener != null) {
            mOnTimeChangeListener.onChange(mCurrentTime, mSelectTickCount);
        }
    }

    /**
     * 设置刻度总数
     *
     * @param tickMaxCount 刻度总数
     */
    public void setTickMaxCount(int tickMaxCount) {
        mTickMaxCount = tickMaxCount;
    }

    /**
     * 设置最大的时间长度
     *
     * @param maxTime long类型
     */
    public void setMaxTime(long maxTime) {
        mMaxTime = maxTime;
    }

    /**
     * 设置当前时间
     *
     * @param currentTime long
     * @param isAnim      是否动画
     */
    public void setCurrentTime(long currentTime, boolean isAnim) {
        mCurrentTime = currentTime;
        Log.i(TAG, "当前时间所占百分比" + (getTimePercent()));
        setCurrentProgress(getTimePercent() * 100, isAnim);
    }

    /**
     * 获取当前时间所占百分比
     *
     * @return 当前时间所占百分比
     */
    private double getTimePercent() {
        return (double) (mCurrentTime - mStartTime) /
                (mMaxTime - mStartTime);
    }

    public void setCurrentTime(long currentTime) {
        this.setCurrentTime(currentTime, true);
    }

    /**
     * 当前设置时间
     *
     * @return 当前设置时间
     */
    public long getCurrentTime() {
        return mCurrentTime;
    }

    /**
     * 获取当前选择的刻度总数
     *
     * @return 获取当前选择的刻度总数
     */
    public int getSelectTickCount() {
        return mSelectTickCount;
    }

    /**
     * 自动倒计时模式，此时触摸事件被屏蔽，view不停地绘制自身，每次一秒（由外界传入time控制）。
     *
     * @param time        当前时间
     * @param isLockTouch 是否锁定触摸事件，即禁用触摸
     */
    public void autoCountDown(long time, boolean isLockTouch) {
        mCurrentTime = time;
        mIsLockTouch = isLockTouch;
        mCenterText = formatTime(mCurrentTime);
        BigDecimal bigDecimal = new BigDecimal(getTimePercent() * mTickMaxCount)
                .setScale(0, BigDecimal.ROUND_HALF_UP);
        mAnimTickCount = mSelectTickCount = bigDecimal.intValue();
        Log.i(TAG, "autoCountDown: mAnimTickCount:" + mAnimTickCount + "  mMaxTime:" + mMaxTime
                + "  mCurrentTime:" + mCurrentTime + "  mCenterText:" + mCenterText);
        invalidate();
    }

    /**
     * 初始化属性
     *
     * @param context 上下文
     * @param attrs   属性
     */
    private void init(Context context, AttributeSet attrs) {
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.CircleTickView);

        mSelectTickColor = attr.getColor(R.styleable.CircleTickView_selectTickColor, getResources
                ().getColor(R.color.orange_f6));
        mDefaultTickColor = attr.getColor(R.styleable.CircleTickView_defaultTickColor, getResources
                ().getColor(R.color.purple_82));
        mTickStrokeSize = attr.getDimensionPixelSize(R.styleable.CircleTickView_tickStrokeSize,
                getDpValue(20));
        mTickMaxCount = attr.getInt(R.styleable.CircleTickView_tickMaxCount, 31);
        mCenterTextColor = attr.getColor(R.styleable.CircleTickView_centerTextColor, getResources
                ().getColor(R.color
                .orange_f6));
        mCenterTextSize = attr.getDimension(R.styleable.CircleTickView_centerTextSize,
                getDpValue(50));
        mStartTime = attr.getInt(R.styleable.CircleTickView_startTime, 0);
        mMaxTime = attr.getInt(R.styleable.CircleTickView_maxTime, 20 * 60 * 1000);
        mCenterText = formatTime(mStartTime);
        mBottomTextColor = attr.getColor(R.styleable.CircleTickView_bottomTextColor, getResources
                ().getColor(R.color
                .purple_82));
        mBottomText = attr.getString(R.styleable.CircleTickView_bottomText);
        if (TextUtils.isEmpty(mBottomText)) {
            mBottomText = "";
        }
        mBottomTextSize = attr.getDimension(R.styleable.CircleTickView_bottomTextSize,
                getDpValue(16));
        mRoundBackgroundColor = attr.getColor(R.styleable.CircleTickView_roundBackgroundColor,
                getResources().getColor(R.color
                        .gray_c));
        mAnimDuration = attr.getInt(R.styleable.CircleTickView_animDuration, 500);
        mIsCanResetZero = attr.getBoolean(R.styleable.CircleTickView_isCanResetZero, true);

        initView();
        mBarAnimation = new ViewRefreshAnimation();
        mBarAnimation.setDuration(mAnimDuration);
//        setSelectTickCount(1, false);//设置默认一进入为1格
    }


    private void initView() {


        mCircleRingPaint = new Paint();
        mCircleRingPaint.setColor(mDefaultTickColor);
        mCircleRingPaint.setStyle(Paint.Style.STROKE);
        mCircleRingPaint.setStrokeWidth(mTickStrokeSize);
        mCircleRingPaint.setAntiAlias(true);

        mCenterTextPaint = new Paint();
        mCenterTextPaint.setColor(mCenterTextColor);
        mCenterTextPaint.setTextAlign(Paint.Align.CENTER);
        mCenterTextPaint.setTextSize(mCenterTextSize);
        mCenterTextPaint.setAntiAlias(true);
        mCenterTextBounds = new Rect();
        mCenterTextPaint.getTextBounds(mCenterText, 0, mCenterText.length(), mCenterTextBounds);

        mBottomTextPaint = new Paint();
        mBottomTextPaint.setColor(mBottomTextColor);
        mBottomTextPaint.setTextAlign(Paint.Align.CENTER);
        mBottomTextPaint.setTextSize(mBottomTextSize);
        mBottomTextPaint.setAntiAlias(true);
        //长方形，圆形view在这里作为内切圆
        mRecf = new RectF();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent: ");
        if (mIsLockTouch) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        if ((x - mCircleCenterX) * (x - mCircleCenterX) + (y - mCircleCenterY) *
                (y - mCircleCenterY) <= ((float)
                1 / 2 * mCircleRadius) * ((float) 1 / 2 * mCircleRadius)) {
            // 圆内触摸点在半径的1/2范围内点击无效
            return false;
        }
        Log.i(TAG, "onTouchEvent: x:" + x + "  y:" + y);
        Log.i(TAG, "onTouchEvent: mCircleCenterX:" + mCircleCenterX + "  mCircleCenterY:" +
                mCircleCenterY);
        float result = (y - mCircleCenterY) / (x - mCircleCenterX);
        double i = Math.atan((double) result);//计算点击坐标到圆心的弧度
        double angle = Math.abs(180 * i / Math.PI);//根据弧度转化为角度
        Log.i(TAG, "touch: angle:" + angle);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                judgeQuadrantAndSetCurrentProgress(x, y, angle, true);
                return true;
            case MotionEvent.ACTION_MOVE:
                judgeQuadrantAndSetCurrentProgress(x, y, angle, false);
                return true;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 判断象限，并且计算当前百分比
     *
     * @param x      当前坐标x
     * @param y      当前坐标y
     * @param angle  角度
     * @param isAnim 是否动画
     */
    private void judgeQuadrantAndSetCurrentProgress(float x, float y, double angle, boolean
            isAnim) {
        double percent = 0;//百分比
        int selectCount = mSelectTickCount;
        if (x >= mCircleCenterX && y <= mCircleCenterY) {
            //第一象限
            Log.i(TAG, "onTouchEvent: 第一象限");
            angle = 360 - angle;
            percent = (angle - 135) / 270;
            selectCount = getSelectCount(percent);
        } else if (x >= mCircleCenterX && y >= mCircleCenterY) {
            //第二象限
            Log.i(TAG, "onTouchEvent: 第二象限");
            if (angle <= 55) {//加10度
                percent = (angle + 225) / 270;
                selectCount = getSelectCount(percent);
                if (angle > 45 - (mSinglPoint / 2)) {
                    selectCount = mTickMaxCount;
                }
            }
        } else if (x <= mCircleCenterX && y >= mCircleCenterY) {
            //第三象限
            Log.i(TAG, "onTouchEvent: 第三象限");
            if (angle <= 65) {
                percent = (45 - angle) / 270;
                //由于第三象限的度数是逆时针递增，所以这里特殊处理，结果必须加1.
                // 比如45度，percent是0，但是此时格子应该是1格。
                selectCount = getSelectCount(percent) + 1;
                //下面代码处理，点击第一个附近时都可以选中第一个
                if (angle > 45 - (mSinglPoint / 2)) {
                    selectCount = 1;
                }
            } else if (angle > 65 && angle < 90) {
                if (mIsCanResetZero) {//如果允许点击第三象限的空白区域归零，
                    selectCount = 0;
                } else {
                    selectCount = 1;
                }
            }
        } else if (x <= mCircleCenterX && y <= mCircleCenterY) {
            //第四象限
            Log.i(TAG, "onTouchEvent: 第四象限");
            percent = (angle + 45) / 270;
            selectCount = getSelectCount(percent);
        }
        Log.i(TAG, "onTouchEvent: selectCount:" + selectCount);
        if (selectCount != mSelectTickCount) {
            //只有发生变化时，才重绘界面
            setSelectTickCount(selectCount, isAnim);
        }
    }

    /**
     * 根据百分比，计算当前选择的刻度总数
     *
     * @param percent 百分比
     * @return 选中刻度总数
     */
    private int getSelectCount(double percent) {
        BigDecimal bigDecimal = new BigDecimal(percent * mTickMaxCount).setScale(0, BigDecimal
                .ROUND_HALF_UP);
        return bigDecimal.intValue();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initValues();
        int p;
        float start = 135f;
        //绘制选中刻度
        if (mAnimTickCount < 0) {
            mAnimTickCount = 0;  //避免初始时间不为0时，界面显示异常，所以过滤错误值
        } else if (mAnimTickCount > mTickMaxCount) {
            mAnimTickCount = mTickMaxCount;
        }

        p = mAnimTickCount;
        for (int i = 0; i < p; i++) {
            mCircleRingPaint.setColor(mSelectTickColor);
            canvas.drawArc(mRecf, start - mLineWidth, mLineWidth, false,
                    mCircleRingPaint); // 绘制间隔块
            start = (start + mSinglPoint);

        }
        //绘制全部刻度
        //剩余刻度的起点=start
        p = mTickMaxCount - p;
        for (int i = 0; i < p; i++) {
            mCircleRingPaint.setColor(mDefaultTickColor);
            canvas.drawArc(mRecf, start - mLineWidth, mLineWidth, false,
                    mCircleRingPaint); // 绘制间隔块
            start = (start + mSinglPoint);
        }

        //绘制
        Paint.FontMetricsInt fontMetrics = mCenterTextPaint.getFontMetricsInt();
        int baseline = (mHeight - getPaddingTop() / 2 - fontMetrics.bottom + fontMetrics.top) / 2 -
                fontMetrics.top;
        canvas.drawText(mCenterText, mCircleCenterX,
                baseline,
                mCenterTextPaint);

        float[] coordinatePoint = getCoordinatePoint(mCircleRadius, 45f + mSinglPoint);
//        Log.i(TAG, "onDraw: mCircleCenterX=" + mCircleCenterX);
//        Log.i(TAG, "onDraw: mCircleRadius=" + mCircleRadius);
//        Log.i(TAG, "onDraw:  coordinatePoint[1]=" + coordinatePoint[1] + "  coordinatePoint[0]=" +
//                coordinatePoint[0]);
        canvas.drawText(mBottomText, mCircleCenterX, coordinatePoint[1] + getPaddingTop(),
                mBottomTextPaint);


    }

    /**
     * 初始化各种view的参数
     */
    private void initValues() {
        mWidth = getWidth();//直径
        mHeight = getHeight();
        mCircleCenterX = mWidth / 2;//半径
        mSinglPoint = (float) 270 / (float) (mTickMaxCount - 1);
        Log.i(TAG, "initValues: mSinglPoint:" + mSinglPoint);
        mVerticalPadding = getPaddingTop() + getPaddingBottom();
        int padding = getPaddingTop() > getPaddingBottom() ? getPaddingTop() :
                getPaddingBottom();
        if (mHeight > mWidth) {
            mCircleRadius = mWidth / 2 - padding;
        } else {
            mCircleRadius = mHeight / 2 - padding;
        }
        mCircleRingRadius = mCircleRadius - mTickStrokeSize / 2; // 圆环的半径
        mCircleCenterY = mHeight / 2;

        mRecf.set(mCircleCenterX - mCircleRingRadius, mHeight / 2 - mCircleRadius,
                mCircleCenterX + mCircleRingRadius,
                mHeight / 2 + mCircleRadius);
    }


    /**
     * 依圆心坐标，半径，扇形角度，计算出扇形终射线与圆弧交叉点的xy坐标
     *
     * @param radius   半径
     * @param cirAngle 扇形角度
     * @return 扇形终射线与圆弧交叉点的xy坐标
     */
    public float[] getCoordinatePoint(int radius, float cirAngle) {
        int position = mHeight / 2 - getPaddingBottom() - getPaddingTop();
        float[] point = new float[2];
        double arcAngle = Math.toRadians(cirAngle); //将角度转换为弧度
        if (cirAngle < 90) {
            point[0] = (float) (position + Math.cos(arcAngle) * radius);
            point[1] = (float) (position + Math.sin(arcAngle) * radius);
        } else if (cirAngle == 90) {
            point[0] = position;
            point[1] = position + radius;
        } else if (cirAngle > 90 && cirAngle < 180) {
            arcAngle = Math.PI * (180 - cirAngle) / 180.0;
            point[0] = (float) (position - Math.cos(arcAngle) * radius);
            point[1] = (float) (position + Math.sin(arcAngle) * radius);
        } else if (cirAngle == 180) {
            point[0] = position - radius;
            point[1] = position;
        } else if (cirAngle > 180 && cirAngle < 270) {
            arcAngle = Math.PI * (cirAngle - 180) / 180.0;
            point[0] = (float) (position - Math.cos(arcAngle) * radius);
            point[1] = (float) (position - Math.sin(arcAngle) * radius);
        } else if (cirAngle == 270) {
            point[0] = position;
            point[1] = position - radius;
        } else {
            arcAngle = Math.PI * (360 - cirAngle) / 180.0;
            point[0] = (float) (position + Math.cos(arcAngle) * radius);
            point[1] = (float) (position - Math.sin(arcAngle) * radius);
        }

        return point;
    }

    /**
     * 动画当前的百分比值
     */
    private int mAnimTickCount;

    public class ViewRefreshAnimation extends Animation {
        public ViewRefreshAnimation() {
        }

        protected void applyTransformation(float interpolatedTime, Transformation t) {

            super.applyTransformation(interpolatedTime, t);
            long mAnimTime = mCurrentTime;//动画当前的时间值
            int diffTick;//当前选中刻度与上一次的差值
            long diffTime;//动画当前的时间值上一次的差值
            if (interpolatedTime <= 1.0F) {
                Log.i(TAG, "applyTransformation: interpolatedTime:" + interpolatedTime + " " +
                        "mLastSelectTickCount:" + mLastSelectTickCount);
                if (mLastSelectTickCount < mSelectTickCount) {
                    //增加刻度与时间,从当前位置增加，不从起点
                    diffTick = mSelectTickCount - mLastSelectTickCount;
                    diffTime = mCurrentTime - mLastTime;
                    mAnimTickCount = mLastSelectTickCount + (int) (interpolatedTime * diffTick);
                    mAnimTime = mLastTime + (long) (interpolatedTime * diffTime);
                } else {//从当前位置减少刻度，减少时间
                    diffTick = mLastSelectTickCount - mSelectTickCount;
                    diffTime = mLastTime - mCurrentTime;
                    mAnimTickCount = mLastSelectTickCount - (int) (interpolatedTime * diffTick);
                    mAnimTime = mLastTime - (long) (interpolatedTime * diffTime);
                }
                Log.i(TAG, "applyTransformation: mAnimTickCount:" + mAnimTickCount);
            }
            mCenterText = formatTime(mAnimTime);
            postInvalidate();
        }
    }



    private int getDpValue(int w) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, w, getContext()
                .getResources().getDisplayMetrics());
    }

    /**
     * @param mss 要转换的毫秒数
     * @return 该毫秒数转换为 15:00 的格式
     */
    public String formatTime(long mss) {
        Log.i(TAG, "formatTime: mss:" + mss);
        if (mss > mMaxTime) {
            mss = mMaxTime;
        } else if (mss < 1000) {
            return "00:00";
        }
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        String min = String.valueOf(minutes);
        if (min.length() <= 1) {
            min = "0" + min;
        }
        String sec = String.valueOf(seconds);
        if (sec.length() <= 1) {
            sec = "0" + sec;
        }
        return min + ":" + sec;
    }

}
