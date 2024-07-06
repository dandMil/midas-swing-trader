package com.dandmil.midasswingtrader.service;

import com.dandmil.midasswingtrader.TIUtils;
import com.dandmil.midasswingtrader.adapters.PolygonAdapter;
import com.dandmil.midasswingtrader.constants.Constants;
import com.dandmil.midasswingtrader.entity.Asset;
import com.dandmil.midasswingtrader.pojo.AssetSignalIndicator;
import com.dandmil.midasswingtrader.pojo.polygon.PolygonResponse;
import com.dandmil.midasswingtrader.pojo.polygon.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

import static com.dandmil.midasswingtrader.constants.Constants.FETCH_HISTORY;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TechnicalIndicatorService {

    private static final Logger logger = LoggerFactory.getLogger(TechnicalIndicatorService.class);

    private final PolygonAdapter polygonAdapter;


    @Autowired
    public TechnicalIndicatorService(PolygonAdapter polygonAdapter){
        this.polygonAdapter = polygonAdapter;
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


    public AssetSignalIndicator calculateTechnicalIndicators(String ticker, String type) {
        AssetSignalIndicator asset = new AssetSignalIndicator();

        Mono<ApiResponse> apiResponseMono = polygonAdapter.makeApiCall(ticker, FETCH_HISTORY,0);
        ApiResponse response = apiResponseMono.block(); // Block and get the response

        if (response != null) {
            logger.info("Calculate Technical Indicators {}", response.toString());
            double[] closingList = getClosingPrices((PolygonResponse) response);
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
            double[] result = TIUtils.calculateMacd(closingList, 26, 12, 6);
            double macdLine = result[0];
            double signalLine = result[1];
            double macdHistogram = result[2];

            Map<String, Integer> indicatorScores = new HashMap<>();

            if (macdLine > signalLine) {
                indicatorScores.put("MACD", 1);
            } else {
                indicatorScores.put("MACD", -1);
            }

            double stochasticSignal = TIUtils.stochasticOscillator(closingList, 14);

            if (stochasticSignal < 20) {
                indicatorScores.put("SO", 1);
            }

            if (stochasticSignal > 80) {
                indicatorScores.put("SO", -1);
            }

            if (20 < stochasticSignal && 80 > stochasticSignal) {
                indicatorScores.put("SO", 0);
            }

            double rsiSignal = TIUtils.relativeStrengthIndex(closingList, 14);

            if (rsiSignal > 70) {
                indicatorScores.put("RSI", -1);
            }

            if (rsiSignal < 30) {
                indicatorScores.put("RSI", 1);
            }

            if (30 < rsiSignal && 70 > rsiSignal) {
                indicatorScores.put("RSI", 0);
            }

            double prc = TIUtils.priceRateOfChange(closingList, 14);

            if (prc > 0) {
                indicatorScores.put("PRC", 1);
            }

            if (prc < 0) {
                indicatorScores.put("PRC", -1);
            }

            double atr = TIUtils.calculateATR(((PolygonResponse) response).getResults(),14);
            logger.info("Setting Asset Indicator info");
            asset.setTicker(ticker);
            asset.setMarketPrice(closingList[0]);

            // Convert to 2 significant figures
            asset.setMacd(roundToSignificantFigures(macdLine, 2));
            asset.setPriceRateOfChange(roundToSignificantFigures(prc, 2));
            asset.setRelativeStrengthIndex(roundToSignificantFigures(rsiSignal, 2));
            asset.setStochasticOscillator(roundToSignificantFigures(stochasticSignal, 2));

            asset.setIndicatorScores(indicatorScores);
            asset.setSignal(computeSignal(indicatorScores, ticker));
            asset.setPolygonResponse((PolygonResponse) response);
            asset.setAtr(atr);
            logger.info(asset.toString());
        }

        return asset;
    }

    private double roundToSignificantFigures(double num, int n) {
        if (num == 0) {
            return 0;
        }

        final double d = Math.ceil(Math.log10(num < 0 ? -num : num));
        final int power = n - (int) d;

        final double magnitude = Math.pow(10, power);
        final long shifted = Math.round(num * magnitude);
        return shifted / magnitude;
    }

    private String computeSignal(Map<String,Integer> signalMap,String tickerName){
        Map<String, Double> indicatorWeights = new HashMap<>();
        indicatorWeights.put("MACD", 0.5);
        indicatorWeights.put("PRC", 0.3);
        indicatorWeights.put("RSI", 0.2);
        indicatorWeights.put("SO", 0.4);

 // Compute the overall score based on the weighted sum of individual indicator scores
        double overallScore = 0;
        for (String indicator : signalMap.keySet()) {
            overallScore += signalMap.get(indicator) * indicatorWeights.get(indicator);
        }

        // Check if the overall score is positive or negative, and make a trading decision accordingly
        if (overallScore > 0) {
            logger.info("Bullish signal detected for " + tickerName);
            return (Constants.BULLISH);
            // Enter long position
        } else if (overallScore < 0) {
            logger.info("Bearish signal detected for " + tickerName);
            return (Constants.BEARISH);
            // Enter short position
        } else {
            logger.info("Neutral signal detected for " + tickerName);
            return (Constants.NEUTRAL);
            // Stay on the sidelines
        }
    }
}
