package com.dandmil.midasswingtrader.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PortfolioEntry {

    private String ticker;
    private int shares;
    private TradeRecommendation tradeRecommendation;


}
