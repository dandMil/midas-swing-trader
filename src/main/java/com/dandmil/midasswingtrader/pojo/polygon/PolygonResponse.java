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

}
