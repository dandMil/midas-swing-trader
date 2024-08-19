package com.dandmil.midasswingtrader.pojo.polygon;

import com.dandmil.midasswingtrader.service.ApiResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class PolygonResponse extends ApiResponse {
    private String ticker;
    //Snapshot
    private Ticker[] tickers;
    private int queryCount;
    private int resultsCount;
    private boolean adjusted;
    private List<Result> results;
    private String status;
    private String request_id;
    private int count;

//    public TickerSummary[] getTickerSummary() {
//        if (tickers == null) {
//            return new TickerSummary[0];
//        }
//        TickerSummary[] tickerSummaries = new TickerSummary[tickers.length];
//        for (int i = 0; i < tickers.length; i++) {
//            tickerSummaries[i].setTicker(tickers[i].getTicker());
//            tickerSummaries[i].setVolume(results[0]);
//        }
//        return tickerSummaries;
//    }

    public String[] getTickerArray() {
        if (tickers == null) {
            return new String[0];
        }
        String[] tickerStrings = new String[tickers.length];
        for (int i = 0; i < tickers.length; i++) {
            tickerStrings[i] = tickers[i].getTicker();
        }
        return tickerStrings;
    }
}

