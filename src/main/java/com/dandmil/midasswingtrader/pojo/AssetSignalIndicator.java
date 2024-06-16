package com.dandmil.midasswingtrader.pojo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Data
public class AssetSignalIndicator {

    private String ticker;
    private String signal;
    private Double marketPrice;
    private Double macd;
    private Double priceRateOfChange;
    private Double relativeStrengthIndex;
    private Double stochasticOscillator;
    private Map<String,Integer> indicatorScores;
    private double blah = 8.0;


}
