package com.geaosu.chart.widget.pie;

/**
 *
 */
public class ChartPlusPieViewBean {

    private String name;
    private float value;
    private int color = 0;

    public ChartPlusPieViewBean() {

    }

    public ChartPlusPieViewBean(String name, float value, int color) {
        this.name = name;
        this.value = value;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }



}
