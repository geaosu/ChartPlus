package com.geaosu.chart.utils;

import android.content.Context;

import java.util.Random;

public class ChartPlusUtils {
    /**
     *
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     *
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
//        return (pxValue-0.5f)/scale;
    }

    /**
     * 获取屏幕高度(px)
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕宽度(px)
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * float 类型保留两位小数，且小数点后为0时只保留整数部分
     */
    public static String getFloat2(float value) {
        String str;
        Float f = new Float(value);
        int itemp = Math.round((f - f.intValue()) * 100);
        if (itemp % 100 == 0) {
            str = String.format("%.0f", f);
        } else if (itemp % 10 == 0) {
            str = String.format("%.1f", f);
        } else {
            str = String.format("%.2f", f);
        }
        return str;
    }

    public static String getRandomColor() {
        String red;        //红色
        String green;        //绿色
        String blue;        //蓝色
        Random random = new Random();        //生成随机对象
        red = Integer.toHexString(random.nextInt(256)).toUpperCase();        //生成红色颜色代码
        green = Integer.toHexString(random.nextInt(256)).toUpperCase();        //生成绿色颜色代码
        blue = Integer.toHexString(random.nextInt(256)).toUpperCase();        //生成蓝色颜色代码

        red = red.length() == 1 ? "0" + red : red;        //判断红色代码的位数
        green = green.length() == 1 ? "0" + green : green;        //判断绿色代码的位数
        blue = blue.length() == 1 ? "0" + blue : blue;        //判断蓝色代码的位数
        String color = "#" + red + green + blue;        //生成十六进制颜色值

        return color;
    }
}
