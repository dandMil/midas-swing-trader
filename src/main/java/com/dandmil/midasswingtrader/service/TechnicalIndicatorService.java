package com.dandmil.midasswingtrader.service;

import com.dandmil.midasswingtrader.TIUtils;
import com.dandmil.midasswingtrader.entity.Asset;
import com.dandmil.midasswingtrader.pojo.polygon.PolygonResponse;
import com.dandmil.midasswingtrader.pojo.polygon.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TechnicalIndicatorService {

    private static final Logger logger = LoggerFactory.getLogger(TechnicalIndicatorService.class);
    @Autowired
    public TechnicalIndicatorService(){

    }

    private double[] getClosingPrices(PolygonResponse response){
        List<Result> resultList = response.getResults();
        double[] resultsArray = new double[resultList.size()];
        for (int i = 0; i < resultList.size(); i++) {
            Result result = resultList.get(i);
            resultsArray[i]= result.getC();
        }
        return resultsArray;
    }
    public Asset calculateTechnicalIndicators(Message<PolygonResponse> message){
        PolygonResponse response = message.getPayload();
        String type = message.getHeaders().get("type", String.class);
        logger.info("Calculate Technical Indicators {}",response.toString());

        double[] closingList = getClosingPrices(response);

        int left = 0;
        int right = closingList.length - 1;
        while (left < right) {
            // Swap elements at left and right indices
            double temp = closingList[left];
            closingList[left] = closingList[right];
            closingList[right] = temp;
            // Move indices towards the center
            left++;
            right--;
        }
        // Assume market_ta is an object containing technical analysis methods
        double[] result = TIUtils.calculateMacd(closingList, 26, 12,6);
        double macdLine = result[0];
        double signalLine = result[1];
        double macdHistogram = result[2];

        Map<String, Integer> indicatorScores = new HashMap<>();

        if (macdLine > signalLine) {
            indicatorScores.put("MACD", 1);
        } else {
            indicatorScores.put("MACD", -1);
        }

        double stochasticSignal = TIUtils.stochasticOscillator(closingList,14);

        if (stochasticSignal < 20) {
            indicatorScores.put("SO", 1);
        }

        if (stochasticSignal > 80) {
            indicatorScores.put("SO", -1);
        }

        if (20 < stochasticSignal && 80 > stochasticSignal) {
            indicatorScores.put("SO", 0);
        }

        double rsiSignal = TIUtils.relativeStrengthIndex(closingList,14);

        if (rsiSignal > 70) {
            indicatorScores.put("RSI", -1);
        }

        if (rsiSignal < 30) {
            indicatorScores.put("RSI", 1);
        }

        if (30 < rsiSignal && 70 > rsiSignal) {
            indicatorScores.put("RSI", 0);
        }

        double prc = TIUtils.priceRateOfChange(closingList,14);

        if (prc > 0) {
            indicatorScores.put("PRC", 1);
        }

        if (prc < 0) {
            indicatorScores.put("PRC", -1);
        }
        Asset asset = new Asset();
//        asset.setId(UUID.randomUUID());
//        asset.setName(response.getTicker());
//        asset.setMarketPrice(closingList[0]);
//        asset.setMacd(macdLine);
//        asset.setPriceRateOfChange(prc);
//        asset.setRelativeStrengthIndex(rsiSignal);
//        asset.setStochasticOscillator(stochasticSignal);
//        asset.setIndicatorScores(indicatorScores);
//        asset.setSignal("");
//        asset.setDate(new Date());
//        asset.setType("");
        return asset;
    }

}
