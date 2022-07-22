package com.geaosu.chart.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

import com.geaosu.chart.R;
import com.geaosu.chart.utils.ChartPlusUtils;


/*
 使用方法：
 xml布局
    <com.geaosu.chart.widget.ChartPlusCirclePercentView
        android:id="@+id/mChartPlusCirclePercentView"
        android:layout_width="100dp"
        android:layout_height="100dp"
        app:circleRadius="1dp" />

        circleRadius 圆环的宽度


// 代码控制进度
mChartPlusCirclePercentView.setProgress(90F);// 设置进度

遗留问题：
1.文字是否加粗
2.文字大小设置


 */
/**
 * 圆环进度显示
 */
public class ChartPlusCirclePercentView extends View {
    private final String TAG = "CirclePercentView";
    private float mRadius;
    private float mRadiusDefault = 10F;// 圆环的宽度默认是10dp
    private RectF mRectF;
    private float mProgress;// 进度数字
    private float mProgressDefault = 0F;// 进度数字

    // 文字部分
    private Paint mTextPaint;// 文字画笔
    private int yOffset;
    private int mTextColor;// 文字颜色
    private int mTextColorDefault = Color.parseColor("#07AFEC");   // 默认文字颜色

    // 圆环部分
    private int mProgressBgColor;// 圆环背景颜色
    private int mProgressBgColorDefault = Color.parseColor("#EAE8E8");// 默认圆环背景颜色
    private int mProgressColor; // 进度圆环颜色
    private int mProgressColorDefault = Color.parseColor("#07AFEC"); // 默认进度圆环颜色

    private LinearGradient mGradient;

    private Paint mPaint;// 圆环
    private boolean isGradient = false;// 是否开启渐变：默认不开启
    private int mGradientStartColor;// 开始渐变色
    private int mGradientStartColorDefault = Color.parseColor("#07AFEC");// 默认开始渐变色
    private int mGradientEndColor;// 结束渐变色
    private int mGradientEndColorDefault = Color.parseColor("#FF0000");// 默认结束渐变色
    private boolean isFirstDraw = true;
    private int mTextSize;// 文字大小
    private int mTextSizeDefault = 10;// 默认文字大小
    private boolean mTextStyleIsBold;// 文字加粗
    private boolean mTextStyleIsBoldDefault = false;// 文字默认不加粗

    public ChartPlusCirclePercentView(Context context) {
        super(context);

        mProgress = mProgressDefault;
        mRadius = ChartPlusUtils.dip2px(getContext(), mRadiusDefault);// 这里需要将10dp转为px值

        mTextColor = mTextColorDefault;

        mProgressBgColor = mProgressBgColorDefault;
        mProgressColor = mProgressColorDefault;

        mGradientStartColor = mGradientStartColorDefault;
        mGradientEndColor = mGradientEndColorDefault;

        init(context);
    }

    public ChartPlusCirclePercentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ChartPlusCirclePercentView);
        mProgress = typedArray.getFloat(R.styleable.ChartPlusCirclePercentView_chartPlusCirclePercentViewProgress, mProgressDefault);

        // 这里获取到的dp值
        // R.styleable.CirclePercentView_circleRadius获取到的是dp值，getDimension会将dp值转成px值
        mRadius = typedArray.getDimension(R.styleable.ChartPlusCirclePercentView_chartPlusCirclePercentViewRadius, ChartPlusUtils.dip2px(getContext(), mRadiusDefault));

        mProgressBgColor = typedArray.getColor(R.styleable.ChartPlusCirclePercentView_chartPlusCirclePercentViewProgressBgColor, mProgressBgColorDefault);
        mProgressColor = typedArray.getColor(R.styleable.ChartPlusCirclePercentView_chartPlusCirclePercentViewProgressColor, mProgressColorDefault);

        // 是否开启渐变色
        isGradient = typedArray.getBoolean(R.styleable.ChartPlusCirclePercentView_chartPlusCirclePercentViewIsGradient, false);
        // 渐变色：开始颜色
        mGradientStartColor = typedArray.getColor(R.styleable.ChartPlusCirclePercentView_chartPlusCirclePercentViewGradientStartColor, mGradientStartColorDefault);
        // 渐变色：结束颜色
        mGradientEndColor = typedArray.getColor(R.styleable.ChartPlusCirclePercentView_chartPlusCirclePercentViewGradientEndColor, mGradientEndColorDefault);

        // 文字的颜色
        mTextColor = typedArray.getColor(R.styleable.ChartPlusCirclePercentView_chartPlusCirclePercentViewTextColor, mTextColorDefault);
        // 文字大小
        mTextSize = typedArray.getInt(R.styleable.ChartPlusCirclePercentView_chartPlusCirclePercentViewTextSize, mTextSizeDefault);
        // 文字是否加粗
        mTextStyleIsBold = typedArray.getBoolean(R.styleable.ChartPlusCirclePercentView_chartPlusCirclePercentViewTextStyleIsBold, mTextStyleIsBoldDefault);

        typedArray.recycle();
        init(context);
    }

    public ChartPlusCirclePercentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        //设置笔刷的样式 设置线冒 :圆形
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(ChartPlusUtils.dip2px(context, 18));
        measureText();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mGradient = new LinearGradient(getWidth(), 0, getWidth(), getHeight(), mGradientStartColor, mGradientEndColor, Shader.TileMode.MIRROR);
    }

    // @SuppressWarnings("Duplicates")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 1、绘制背景灰色圆环
        int centerX = getWidth() / 2;// 圆心点X坐标
        int centerY = getHeight() / 2;// 圆心点Y坐标

        float strokeWidth = mRadius;// 这里算的是PX的值
        mPaint.setStrokeWidth(strokeWidth);// px的值
        //必须设置为null，否则背景也会加上渐变色
        mPaint.setShader(null);
        mPaint.setColor(mProgressBgColor);
        canvas.drawCircle(centerX, centerX, centerX - strokeWidth / 2, mPaint);
        // 2、绘制比例弧
        if (mRectF == null) {
            mRectF = new RectF(strokeWidth / 2, strokeWidth / 2, 2 * centerX - strokeWidth / 2, 2 * centerX - strokeWidth / 2);
        }
        //3、是否绘制渐变色
        if (isGradient) {
            //设置线性渐变
            mPaint.setShader(mGradient);
        } else {
            mPaint.setColor(mProgressColor);
        }
        //画比例圆弧

        // 每个扇形的角度（当前值 / 总值 * 360 = 在圆中所占的比重）
//         float sweepAngle = 3.6f * mProgress;
//         float sweepAngle = 3.6f * mProgress * mAnimProgress;
        float sweepAngle = 3.6f * (mAnimProgress * 100);

        Log.d(TAG, "------>> onDraw: " + sweepAngle);
        // canvas.drawArc(mRectF, -90, 3.6f * mProgress, false, mPaint);
        canvas.drawArc(mRectF, -90, sweepAngle, false, mPaint);// 画个圆弧
        //绘制中间的字
//        String text = (mAnimProgress * 100) + "%";
        String text = ChartPlusUtils.getFloat2((mAnimProgress * 100)) + "%";
        canvas.drawText(text, centerX, centerY + yOffset / 2, mTextPaint);
    }

    private AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
    private float mAnimProgress = 0f;

    // 设置进度
    public void setProgress(float progress) {
        isFirstDraw = false;
        startInvaliDataAnim(progress);
    }

    private void startInvaliDataAnim(float progress) {
        float oldValue = getProgress() / 100;
        float newValue = progress / 100;

        ValueAnimator mAnimator = ValueAnimator.ofFloat(oldValue, newValue);
        mAnimator.setDuration(800);
        mAnimator.setInterpolator(interpolator);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimProgress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mAnimator.start();

        this.mProgress = progress;
        measureText();// 绘制文字
        invalidate();// View重新绘制
    }

    // 获取进度
    public float getProgress() {
        return this.mProgress;
    }

    // 绘制文字
    private void measureText() {
        Rect bound = new Rect();
        String text = mProgress + "%";
        mTextPaint.getTextBounds(text, 0, text.length(), bound);
        yOffset = bound.bottom - bound.top;
    }

    /**
     * 设置圆环宽度
     *
     * @param mRadius
     */
    public void setRadius(int mRadius) {
        this.mRadius = mRadius;
        invalidate();// View重新绘制
    }

    /**
     * 设置圆环进度背景颜色
     */
    public void setProgressBgColor(int progressBgColor) {
        this.mProgressBgColor = progressBgColor;
        invalidate();// View重新绘制
    }

    /**
     * 设置圆环进度颜色
     */
    public void setProgressColor(int mProgressColor) {
        this.mProgressColor = mProgressColor;
        invalidate();// View重新绘制
    }

    /**
     * 设置圆环进度渐变开始颜色
     */
    public void setGradientStartColor(int gradientStartColor) {
        this.mGradientStartColor = gradientStartColor;
        invalidate();// View重新绘制
    }

    /**
     * 设置圆环进度渐变结束颜色
     */
    public void setGradientEndColor(int gradientEndColor) {
        this.mGradientEndColor = gradientEndColor;
        invalidate();// View重新绘制
    }

    /**
     * 圆环进度是否开启渐变
     */
    public void setGradient(boolean mGradient) {
        this.isGradient = mGradient;
        invalidate();// View重新绘制
    }
}
