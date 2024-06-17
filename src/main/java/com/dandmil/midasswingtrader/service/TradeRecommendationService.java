package com.dandmil.midasswingtrader.service;

import com.dandmil.midasswingtrader.TIUtils;
import com.dandmil.midasswingtrader.adapters.PolygonAdapter;
import com.dandmil.midasswingtrader.pojo.AssetSignalIndicator;
import com.dandmil.midasswingtrader.pojo.TradeRecommendation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;

import static com.dandmil.midasswingtrader.constants.Constants.*;

@Service
public class TradeRecommendationService {

    //TODO make this scale with volatility
    private static final double PROFIT_TARGET_PERCENTAGE = 10.0;
    private static final double STOP_LOSS_PERCENTAGE = 5.0;

    private static final double HIGH_VOLATILITY_THRESHOLD = 1.5; // Example threshold for high volatility
    private static final double LOW_VOLATILITY_THRESHOLD = 0.5; // Example threshold for low volatility

    private static final double PROFIT_TARGET_MULTIPLIER = 3.0; // 3x ATR for profit target
    private static final double STOP_LOSS_MULTIPLIER = 2.0; // 2x ATR for stop loss

    private final TechnicalIndicatorService technicalIndicatorService;


    @Autowired
    public TradeRecommendationService( TechnicalIndicatorService technicalIndicatorService){
            this.technicalIndicatorService = technicalIndicatorService;
    }

    public TradeRecommendation calculateTradeRecommendations(String ticker, double entryPrice) {
        // Fetch technical indicators
        AssetSignalIndicator response = technicalIndicatorService.calculateTechnicalIndicators(ticker, "stock");
        TradeRecommendation tradeRecommendation = new TradeRecommendation();

        if (response != null) {
            // Select strategy based on technical indicators
            String strategy = selectStrategy(response.getAtr(), response.getStochasticOscillator(),
                    response.getRelativeStrengthIndex(), response.getMacd(), response.getPriceRateOfChange());

            tradeRecommendation.setStrategy(strategy);
            tradeRecommendation.setPriceEntry(TIUtils.roundTo3SigFigs(entryPrice));
            tradeRecommendation.setRecommendationDate(new Date());
            double profitTarget = 0.0;
            double stopLoss = 0.0;
            if (PERCENTAGE_BASED.equalsIgnoreCase(strategy)) {
                // Percentage-Based Strategy
                 profitTarget = entryPrice * (1 + PROFIT_TARGET_PERCENTAGE / 100);
                 stopLoss = entryPrice * (1 - STOP_LOSS_PERCENTAGE / 100);
                tradeRecommendation.setTakeProfit(TIUtils.roundTo3SigFigs(profitTarget));
                tradeRecommendation.setStopLoss(TIUtils.roundTo3SigFigs(stopLoss));
            } else if (VOLATILITY_BASED.equalsIgnoreCase(strategy)) {
                // Volatility-Based Strategy
                double atr = response.getAtr();
                 profitTarget = entryPrice + (PROFIT_TARGET_MULTIPLIER * atr);
                 stopLoss = entryPrice - (STOP_LOSS_MULTIPLIER * atr);
                tradeRecommendation.setTakeProfit(TIUtils.roundTo3SigFigs(profitTarget));
                tradeRecommendation.setStopLoss(TIUtils.roundTo3SigFigs(stopLoss));
            }
            double expectedProfit = TIUtils.roundTo3SigFigs(profitTarget - entryPrice);
            double expectedLoss = TIUtils.roundTo3SigFigs(entryPrice - stopLoss);

            tradeRecommendation.setExpectedProfit(expectedProfit);
            tradeRecommendation.setExpectedLoss(expectedLoss);
        }

        return tradeRecommendation;
    }

    private String selectStrategy(double atr, double so, double rsi, double macd, double roc) {
        boolean isVolatilityHigh = atr > HIGH_VOLATILITY_THRESHOLD;
        boolean isMomentumStrong = Math.abs(macd) > 1.0 || Math.abs(roc) > 10.0;
        boolean isOverboughtOversold = rsi > 70 || rsi < 30 || so > 80 || so < 20;

        if (isVolatilityHigh || isMomentumStrong || isOverboughtOversold) {
            return VOLATILITY_BASED;
        } else {
            return PERCENTAGE_BASED;
        }
    }
}
