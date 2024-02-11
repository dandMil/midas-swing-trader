package com.dandmil.midasswingtrader.service;

import com.dandmil.midasswingtrader.TIUtils;
import com.dandmil.midasswingtrader.pojo.Asset;
import com.dandmil.midasswingtrader.pojo.PolygonResponse;
import com.dandmil.midasswingtrader.pojo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.error.Mark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Asset calculateTechnicalIndicators(PolygonResponse response){
    logger.info("Calculate Technical Indicators {}",response.toString());

        double[] closingList = getClosingPrices(response);

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

        return new Asset(response.getTicker(), closingList[0], macdLine, prc, rsiSignal, stochasticSignal, indicatorScores,""); // Define MarketAsset constructor
    }

}
