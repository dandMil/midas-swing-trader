package com.dandmil.midasswingtrader.service;

import com.dandmil.midasswingtrader.adapters.PolygonAdapter;
import com.dandmil.midasswingtrader.constants.Constants;
import com.dandmil.midasswingtrader.pojo.AssetBars;
import com.dandmil.midasswingtrader.pojo.polygon.PolygonResponse;
import com.dandmil.midasswingtrader.pojo.polygon.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.dandmil.midasswingtrader.constants.Constants.FETCH_HISTORY;

@Service
public class BarsService {


    private final PolygonAdapter polygonAdapter;


    @Autowired
    public BarsService(PolygonAdapter polygonAdapter){
        this.polygonAdapter = polygonAdapter;

    }

    public AssetBars getBars(String asset, int timeRange){
        String[] arguments = new String[2];
        arguments[0] = "gainers";
        Mono<ApiResponse> apiResponseMono = polygonAdapter.makeApiCall(asset, FETCH_HISTORY,timeRange,arguments);
        PolygonResponse response = (PolygonResponse) apiResponseMono.block(); // Block and get the response
       AssetBars assetBars = new AssetBars();
        if (response != null){
            List<Result> results = response.getResults();
            double min = Integer.MAX_VALUE;
            double max = Integer.MIN_VALUE;

            for (Result result : results){
                min = Math.min(min,result.getL());
                max = Math.max(max,result.getH());
            }
            assetBars.setResistance(max);
            assetBars.setSupport(min);
            assetBars.setPolygonResponse(response);
        }

        return assetBars;
    }
}
