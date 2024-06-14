package com.dandmil.midasswingtrader.pojo.polygon;

import lombok.Data;

@Data
public class Ticker {
    private String ticker;
    private double todaysChangePerc;
    private double todaysChange;
    private long updated;
    private DayData day;
    private MinData min;
    private PrevDayData prevDay;

}

