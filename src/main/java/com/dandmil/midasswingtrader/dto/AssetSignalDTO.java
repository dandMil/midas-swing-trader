package com.dandmil.midasswingtrader.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
public class AssetSignalDTO {
    private UUID id;
    private BigDecimal marketPrice;
    private BigDecimal macd;
    private BigDecimal priceRateOfChange;
    private BigDecimal relativeStrengthIndex;
    private BigDecimal stochasticOscillator;
    private String indicatorScores;
    private String signal;
    private Date dateCreated;
}
