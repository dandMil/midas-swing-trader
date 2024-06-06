package com.dandmil.midasswingtrader.service;

import com.dandmil.midasswingtrader.adapters.PolygonAdapter;
import com.dandmil.midasswingtrader.constants.Constants;
import com.dandmil.midasswingtrader.transformer.ApiResponseTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
@Service
public class TopMoversService {


    PolygonAdapter polygonAdapter;

    ApiResponseTransformer apiResponseTransformer;


    @Autowired
    public TopMoversService(PolygonAdapter polygonAdapter){
        this.polygonAdapter = polygonAdapter;
    }

    public Mono<ApiResponse> fetchTopMovers() {
        return polygonAdapter.makeApiCall(null, Constants.FETCH_TOP_MOVERS);
    }
}
