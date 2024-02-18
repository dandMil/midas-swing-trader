package com.dandmil.midasswingtrader.service;

import com.dandmil.midasswingtrader.Constants;
import com.dandmil.midasswingtrader.pojo.Asset;
import com.dandmil.midasswingtrader.repository.AssetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ComputeSignalService {

    private static final Logger logger = LoggerFactory.getLogger(ComputeSignalService.class);

    private AssetRepository assetRepository;

    @Autowired
    public ComputeSignalService(AssetRepository assetRepository){
    this.assetRepository = assetRepository;
    }

    public void computeSignal(Asset asset){
    logger.info("Compute Signal called for {}",asset);
        // Define weights for each indicator
        Map<String, Double> indicatorWeights = new HashMap<>();
        indicatorWeights.put("MACD", 0.5);
        indicatorWeights.put("PRC", 0.3);
        indicatorWeights.put("RSI", 0.2);
        indicatorWeights.put("SO", 0.4);

        // Compute the overall score based on the weighted sum of individual indicator scores
        double overallScore = 0;
        for (String indicator : asset.getIndicatorScores().keySet()) {
            overallScore += asset.getIndicatorScores().get(indicator) * indicatorWeights.get(indicator);
        }

        // Check if the overall score is positive or negative, and make a trading decision accordingly
        if (overallScore > 0) {
            logger.info("Bullish signal detected for " + asset.getName());
            asset.setSignal(Constants.BULLISH);
            // Enter long position
        } else if (overallScore < 0) {
            logger.info("Bearish signal detected for " + asset.getName());
            asset.setSignal(Constants.BEARISH);
            // Enter short position
        } else {
            logger.info("Neutral signal detected for " + asset.getName());
            asset.setSignal(Constants.NEUTRAL);
            // Stay on the sidelines
        }
        saveAsset(asset);
    }

    private void saveAsset(Asset asset){
        assetRepository.save(asset);
    }
}
