package com.dandmil.midasswingtrader.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class AssetResponse {
    private String name;
    private String type;
    private List<Volume> volumeHistory;
    private String signal;
    private Date lastUpdated;
    private Double marketPrice;
    private Double weeklyIncrease;
    private Double dailyIncrease;
    private Double volume;
}
