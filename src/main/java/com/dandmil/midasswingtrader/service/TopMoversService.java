package com.dandmil.midasswingtrader.service;

import com.dandmil.midasswingtrader.adapters.PolygonAdapter;
import com.dandmil.midasswingtrader.constants.Constants;
import com.dandmil.midasswingtrader.pojo.polygon.PolygonResponse;
import com.dandmil.midasswingtrader.transformer.ApiResponseTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Service
public class TopMoversService {


    PolygonAdapter polygonAdapter;

    ApiResponseTransformer apiResponseTransformer;

    PythonCaller pythonCaller;

    private static final Logger logger = LoggerFactory.getLogger(TopMoversService.class);


    @Autowired
    public TopMoversService(PolygonAdapter polygonAdapter, PythonCaller pythonCaller){
        this.polygonAdapter = polygonAdapter;
        this.pythonCaller = pythonCaller;
    }

    public Mono<ApiResponse> fetchTopMovers() {
        return polygonAdapter.makeApiCall(null, Constants.FETCH_TOP_MOVERS,0);
    }

    public void loadTopMovers() {
        logger.info("Loading Top Movers");
        Mono<ApiResponse> apiResponseMono = polygonAdapter.makeApiCall(null,Constants.FETCH_TOP_MOVERS,0);

        apiResponseMono.cast(PolygonResponse.class)
                .map(PolygonResponse::getTickerArray)
                .subscribe(tickers -> {
                    try {
                        pythonCaller.yahooSignificantVolume(tickers);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
