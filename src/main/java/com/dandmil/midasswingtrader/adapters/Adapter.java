package com.dandmil.midasswingtrader.adapters;

import com.dandmil.midasswingtrader.service.ApiResponse;
import reactor.core.publisher.Mono;

abstract class Adapter {

    protected void logRequest (String endpoint){
        //Log request
    }

    public abstract Mono<ApiResponse> makeApiCall(String assetName, String command, int timeRange,
                                                  String[]argument);

}
