package com.geaosu.chart.widget.pie;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.core.content.ContextCompat;

import com.geaosu.chart.R;
import com.geaosu.chart.utils.ChartPlusUtils;

import java.util.ArrayList;
import java.util.List;

/*
 使用方法：
 xml布局
    <com.geaosu.chart.widget.pie.PieChartNone
        android:id="@+id/mPieChartNone1"
        android:layout_width="100dp"
        android:layout_height="100dp"
        android:layout_marginTop="12dp" />


// 代码控制进度
mCirclePercentView.setProgress(90F);// 设置进度


遗留问题：
1.文字是否加粗
2.文字大小设置


 */

/**
 * 饼状图表
 */
public class ChartPlusPieView extends View {
    public final int TOUCH_OFFSET = ChartPlusUtils.dip2px(getContext(), 5);
    private int mTotalWidth, mTotalHeight;
    private float mRadius;
    private float mVerticalLineSize = ChartPlusUtils.dip2px(getContext(), 20);
    private float mHorizontalLineSize = ChartPlusUtils.dip2px(getContext(), 40);
    private float mLineOffset = ChartPlusUtils.dip2px(getContext(), 8);
    private float mTextOffset = ChartPlusUtils.dip2px(getContext(), 3);
    private Paint mPaint, mLinePaint, mTextPaint, mMiddlePaint;
    private Rect mTextRect = new Rect();

    private boolean mIsHaveGapDefault = true;// 是否有缺口，默认有缺口
    private boolean mIsHaveGap = true;// 是否有间隙，是否有缺口，开口
    private boolean mIsHollow ;// 是否是空心
    private boolean mIsHollowDefault = false;// 是否是空心，默认不是空心
    private boolean isShowMiddleText = false;// 是否显示中间的文字

    private Path mPath;
    private RectF mRectF;// 扇形的绘制区域

    private List<ChartPlusPieViewBean> mData;// 数据集合
    private float mTotalValue;// 所有的数据加起来的总值
    private List<Region> mRegions = new ArrayList<>();
    private int lastClickedPosition = -1;// 点击某一块之后再次点击回复原状
    private boolean lastPositionClicked = false;

    private int position = -1;// 手点击的部分的position
    private OnItemClickListener mOnItemClickListener;// 点击监听
    private RectF mRectFTouch;// 点击之后的扇形的绘制区域


    /********** 文字部分 **********/
    // 上面的总数文字
    private int mTextColor;// 文字颜色
    private int mTextColorDefault = Color.parseColor("#000000");// 文字颜色，默认黑色
    private int mTextSize;// 文字大小
    private int mTextSizeDefault = ChartPlusUtils.dip2px(getContext(), 14);// 文字大小，默认14
    private boolean mTextStyleIsBold;// 文字是否加粗
    private boolean mTextStyleIsBoldDefault = false;// 文字是否加粗，默认false不加粗
    // 下边的提示文字
    private int mSubTextColor;// 文字颜色
    private int mSubTextColorDefault = Color.parseColor("#000000");// 文字颜色，默认黑色
    private int mSubTextSize;// 文字大小
    private int mSubTextSizeDefault = ChartPlusUtils.dip2px(getContext(), 14);// 文字大小，默认14
    private boolean mSubTextStyleIsBold;// 文字是否加粗
    private boolean mSubTextStyleIsBoldDefault = false;// 文字是否加粗，默认false不加粗

    /********** 背景部分 **********/
    private int mBgColorDefault = Color.parseColor("#F5F5F5");// 圆环背景颜色，默认是灰色
    private int mBgColor;// 圆环背景颜色

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemPieClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public ChartPlusPieView(Context context) {
        this(context, null);
    }

    public ChartPlusPieView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartPlusPieView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ChartPlusPieView);
        // 是否有缺口
        mIsHaveGap = typedArray.getBoolean(R.styleable.ChartPlusPieView_chartPlusPieViewIsHaveGap, mIsHaveGapDefault);
        // 是否有缺口
        mIsHollow = typedArray.getBoolean(R.styleable.ChartPlusPieView_chartPlusPieViewIsHaveGap, mIsHollowDefault);
        // 圆环背景颜色
        mBgColor = typedArray.getColor(R.styleable.ChartPlusPieView_chartPlusPieViewBgColor, mBgColorDefault);




        // 文字的颜色
        mTextColor = typedArray.getColor(R.styleable.ChartPlusPieView_chartPlusPieViewTextColor, mTextColorDefault);
        // 文字大小
        mTextSize = typedArray.getInt(R.styleable.ChartPlusPieView_chartPlusPieViewTextSize, mTextSizeDefault);
        // 文字是否加粗
        mTextStyleIsBold = typedArray.getBoolean(R.styleable.ChartPlusPieView_chartPlusPieViewTextStyleIsBold, mTextStyleIsBoldDefault);

        // 文字的颜色
        mSubTextColor = typedArray.getColor(R.styleable.ChartPlusPieView_chartPlusPieViewSubTextColor, mSubTextColorDefault);
        // 文字大小
        mSubTextSize = typedArray.getInt(R.styleable.ChartPlusPieView_chartPlusPieViewSubTextSize, mSubTextSizeDefault);
        // 文字是否加粗
        mSubTextStyleIsBold = typedArray.getBoolean(R.styleable.ChartPlusPieView_chartPlusPieViewSubTextStyleIsBold, mSubTextStyleIsBoldDefault);


        typedArray.recycle();
        init();
    }

    private void init() {
        mRectF = new RectF();
        mRectFTouch = new RectF();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setStrokeWidth(2);
        mLinePaint.setColor(Color.BLACK);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(ChartPlusUtils.dip2px(getContext(), 8));

        mMiddlePaint = new Paint();
        mMiddlePaint.setAntiAlias(true);
        mMiddlePaint.setStyle(Paint.Style.FILL);
        mMiddlePaint.setTextSize(10);

        mPath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 计算总宽度和总高度（View的宽度减去内边距）
        mTotalWidth = w - getPaddingLeft() - getPaddingRight();// 减去左边和右边的内边距
        mTotalHeight = h - getPaddingTop() - getPaddingBottom();// 减去上边和下边的内边距

        // Math.max(mTotalWidth, mTotalHeight) 根据宽高，取最大值进行绘制
//        mRadius = (float) (Math.min(mTotalWidth, mTotalHeight) / 2 * 0.7);
        // 计算圆饼的半径（半径=View总宽度或者View总高度除以2，因为从View的中心点开始画的，半径就是view的高度或者宽度的一半）
        mRadius = (float) (Math.min(mTotalWidth, mTotalHeight) / 2);//

        mRectF.left = -mRadius;
        mRectF.top = -mRadius;
        mRectF.right = mRadius;
        mRectF.bottom = mRadius;

        mRectFTouch.left = -mRadius - TOUCH_OFFSET;
        mRectFTouch.top = -mRadius - TOUCH_OFFSET;
        mRectFTouch.right = mRadius + TOUCH_OFFSET;
        mRectFTouch.bottom = mRadius + TOUCH_OFFSET;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mData == null)
            return;
        canvas.translate(mTotalWidth >> 1, mTotalHeight >> 1);
        // 绘制饼图的每块区域
        drawPiePath(canvas);
    }

    private float percent = 0f;
    private TimeInterpolator pointInterpolator = new DecelerateInterpolator();

    public void startAnimation(int duration) {
        ValueAnimator mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setDuration(duration);
        mAnimator.setInterpolator(pointInterpolator);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                percent = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mAnimator.start();
    }

    /**
     * 绘制饼图的每块区域 和文本
     *
     * @param canvas
     */
    private void drawPiePath(Canvas canvas) {
        //起始地角度
        float startAngle = 0;
        mRegions.clear();
        for (int i = 0; i < mData.size(); i++) {// mDataList.size() 模块的数量，绘制多少个模块
            ChartPlusPieViewBean data = mData.get(i);

            // 每个扇形的角度（当前值 / 总值 * 360 = 在圆中所占的比重）
//            float sweepAngle;
//            if (mIsHaveGap) {
//                sweepAngle = pieChartViewDataBean.getValue() / mTotalValue * 360 - 1;// 去掉-1就会链接在一起，没有空隙
//            } else {
//                sweepAngle = pieChartViewDataBean.getValue() / mTotalValue * 360;// 去掉-1就会链接在一起，没有空隙
//            }
//            sweepAngle = sweepAngle * percent;

            float sweepAngle = data.getValue() / mTotalValue * 360 - 1;// 去掉-1就会链接在一起，没有空隙
            sweepAngle = sweepAngle * percent;

            // 给画笔设置颜色
            mPaint.setColor(data.getColor());
            mLinePaint.setColor(data.getColor());
            mTextPaint.setColor(data.getColor());

            //*******下面的两种方法选其一就可以 一个是通过画路径来实现 一个是直接绘制扇形***********
            //第一种 绘制path
            mPath.moveTo(0, 0);
            if (position == i) {
                if (lastClickedPosition == position && lastPositionClicked) {
                    // 点击item后绘制的圆形区域
                    mPath.arcTo(mRectFTouch, startAngle, sweepAngle);
                } else {
                    // 正常绘制的区域
                    mPath.arcTo(mRectF, startAngle, sweepAngle);
                }
            } else {
                mPath.arcTo(mRectF, startAngle, sweepAngle);
            }
            RectF r = new RectF();
            mPath.computeBounds(r, true);
            Region region = new Region();
            region.setPath(mPath, new Region((int) r.left, (int) r.top, (int) r.right, (int) r.bottom));
            mRegions.add(region);
            canvas.drawPath(mPath, mPaint);
            mPath.reset();
            Log.i("toRadians", (startAngle + sweepAngle / 2) + "****" + Math.toRadians(startAngle + sweepAngle / 2));
            // 确定直线的起始和结束的点的位置
            startAngle += sweepAngle + 1;


//            float res = mDataList.get(i).getValue() / mTotalValue * 100;
//            String name = mDataList.get(i).getName();
//            //提供精确的小数位四舍五入处理。
//            double resToRound = CalculateUtil.round(res, 2);

        }

        if (mIsHollow) {
            drawHollow(canvas);// 绘制圆形中心的显示内容，这样的话图形就变成了圆环
        }
    }

    private void drawHollow(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        mPaint.setAlpha(255);
        canvas.drawCircle(0, 0, mRadius / 5 * 4, mPaint);// 画圆，内圈里的圆形
        if (isShowMiddleText) {
            // 绘制中间的数字
            mMiddlePaint.setTextSize(ChartPlusUtils.dip2px(getContext(), 16));
            mMiddlePaint.setColor(ContextCompat.getColor(getContext(), R.color.black));
            canvas.drawText(String.valueOf((int) mTotalValue), 0 - mMiddlePaint.measureText(String.valueOf((int) mTotalValue)) / 2, 0, mMiddlePaint);
            // 绘制数字下面的提示问题
            mMiddlePaint.setTextSize(ChartPlusUtils.dip2px(getContext(), 12));
            mMiddlePaint.setColor(ContextCompat.getColor(getContext(), R.color.gray));
            canvas.drawText("未结案总数", 0 - mMiddlePaint.measureText("未结案总数") / 2, ChartPlusUtils.dip2px(getContext(), 16), mMiddlePaint);
        }
    }

    /**
     * 这里使用Region来确定点击的位置
     * 在{@link HollowPieChart } {@link HollowPieNewChart}中使用角度的方式来判断点击的位置
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX() - (mTotalWidth / 2f);
                float y = event.getY() - (mTotalHeight / 2f);
                for (int i = 0; i < mRegions.size(); i++) {
                    Region region = mRegions.get(i);
                    if (region.contains((int) x, (int) y)) {
                        position = i;
                        break;
                    }
                }
                if (lastClickedPosition == position) {
                    lastPositionClicked = !lastPositionClicked;
                } else {
                    lastPositionClicked = true;
                    lastClickedPosition = position;
                }
                invalidate();
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onClick(position);
                }
                break;
            default:
        }
        return super.onTouchEvent(event);
    }

    /**
     * 是否是空心
     */
    public void isHollow(boolean isHollow) {
        this.mIsHollow = isHollow;
    }

    /**
     * 是否有缺口
     */
    public void isHaveGap(boolean isHaveGap) {
        this.mIsHaveGap = isHaveGap;
    }

    /**
     * 是否显示中间的文字
     */
    public void setShowMiddleText(boolean showMiddleText) {
        isShowMiddleText = showMiddleText;
    }

    /**
     * 设置数据
     */
    public void setData(List<ChartPlusPieViewBean> dataList) {
        this.mData = dataList;
        mTotalValue = 0;
        for (ChartPlusPieViewBean pieData : mData) {
            mTotalValue += pieData.getValue();
        }
        // 执行动画
        ValueAnimator mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setDuration(800);
        mAnimator.setInterpolator(pointInterpolator);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                percent = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mAnimator.start();
    }
}
