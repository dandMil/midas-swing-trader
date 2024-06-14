package com.dandmil.midasswingtrader.pojo.yahoo;

import com.dandmil.midasswingtrader.service.ApiResponse;
import lombok.Data;
import yahoofinance.histquotes.HistoricalQuote;

import java.math.BigDecimal;
import java.util.List;

@Data
public class YahooResponse extends ApiResponse {
    private BigDecimal weeklyIncrease;
    private BigDecimal dailyIncrease;
    private List<HistoricalQuote> marketData;
    private String ticker;

}
