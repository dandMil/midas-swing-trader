package com.dandmil.midasswingtrader.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
public class Volume {
    private Double volume;
    private Date dateCreated;
    private Double weeklyIncrease;
    private Double dailyIncrease;
}
