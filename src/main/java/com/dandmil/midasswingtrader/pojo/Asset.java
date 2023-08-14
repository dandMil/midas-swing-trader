package com.dandmil.midasswingtrader.pojo;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Asset {
    private String name;
    private double marketPrice;
    private double macd;
    private double priceRateOfChange;
    private double relativeStrengthIndex;
    private double stochasticOscillator;
    private Map<String,Integer> indicatorScores;
    private String signal;

}

