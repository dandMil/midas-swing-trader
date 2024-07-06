package com.dandmil.midasswingtrader.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class TradeRecommendation {

    private double priceEntry;

    private double stopLoss;

    private double takeProfit;

    private Date recommendationDate;

    private String strategy;

    private double expectedProfit;

    private double expectedLoss;
}
