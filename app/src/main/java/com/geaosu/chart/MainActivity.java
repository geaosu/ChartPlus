package com.geaosu.chart;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.geaosu.chart.utils.ChartPlusUtils;
import com.geaosu.chart.widget.ChartPlusCirclePercentView;
import com.geaosu.chart.widget.pie.ChartPlusPieView;
import com.geaosu.chart.widget.pie.ChartPlusPieViewBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private TextView mTextViewRefresh;
    private EditText mEditTextProgress;
    private LinearLayout mLinearLayoutBox;
    private ChartPlusCirclePercentView mChartPlusCirclePercentView;
    private ChartPlusPieView mChartPlusPieView;

    private CountDownTimer mTimer = new CountDownTimer(10000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            float l = (millisUntilFinished / 1000) * 10F;
//            mCirclePercentView1.setProgress(l);
//            mCirclePercentView2.setProgress(l);
        }

        @Override
        public void onFinish() {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewRefresh = findViewById(R.id.mTextViewRefresh);
        mEditTextProgress = findViewById(R.id.mEditTextProgress);
        mLinearLayoutBox = findViewById(R.id.mLinearLayoutBox);
        mChartPlusCirclePercentView = findViewById(R.id.mChartPlusCirclePercentView);
        mChartPlusPieView = findViewById(R.id.mChartPlusPieView);

        mTextViewRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    /***************** mChartPlusCirclePercentView *******************/
//                    String num = mEditTextProgress.getText().toString().trim();
//                    Float progress = Float.valueOf(num);
//                    if (progress < 0) {
//                        return;
//                    }
//                    mCirclePercentView.setProgress(progress);// 设置进度


                    /***************** mChartPlusPieView *******************/
                    Random random = new Random();
                    List<ChartPlusPieViewBean> mPieChartViewList = new ArrayList<>();
                    for (int i = 0; i <= random.nextInt(20); i++) {
                        ChartPlusPieViewBean bean = new ChartPlusPieViewBean();
                        bean.setName("name" + i);
                        bean.setValue(1);
                        bean.setColor(Color.parseColor(ChartPlusUtils.getRandomColor()));
                        mPieChartViewList.add(bean);
                    }
                    mChartPlusPieView.setData(mPieChartViewList);
                    //mChartPlusPieView.startAnimation(800);
                } catch (Exception e) {
                }
            }
        });

        /***************** mChartPlusCirclePercentView *******************/
        mChartPlusCirclePercentView.setProgress(90F);// 设置进度


        /***************** mChartPlusPieView *******************/
        mChartPlusPieView.isHollow(false);
        mChartPlusPieView.isHaveGap(false);
        mChartPlusPieView.setShowMiddleText(true);
//        List<ChartPlusPieViewBean> mPieChartViewList = new ArrayList<>();
//        for (int i = 0; i <= 6; i++) {
//            ChartPlusPieViewBean bean = new ChartPlusPieViewBean();
//            bean.setName("name" + i);
//            bean.setValue(i + 1);
//            bean.setColor(Color.parseColor(ChartPlusUtils.getRandomColor()));
//            mPieChartViewList.add(bean);
//        }
//        mChartPlusPieView.setData(mPieChartViewList);
//        mChartPlusPieView.startAnimation(800);

        mTimer.start();

    }
}