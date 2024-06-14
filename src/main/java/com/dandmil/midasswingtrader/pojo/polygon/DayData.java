package com.dandmil.midasswingtrader.pojo.polygon;

import lombok.Data;

@Data
public class DayData {
    private double o;
    //Open price for time period
    private double h;
    //lowest price for time period
    private double l;
    //closing price
    private double c;
    //trading volume for the time period
    private long v;
    //trading volume weighted average price
    private double vw;


}

