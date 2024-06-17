package com.dandmil.midasswingtrader;

import java.util.Arrays;
import java.util.List;

import com.dandmil.midasswingtrader.pojo.polygon.Result;
public class TIUtils {

    public static double priceRateOfChange(double[] price, int window) {
        double rateOfChange = (price[price.length - 1] / price[price.length - window]) - 1;
        return rateOfChange;
    }

    public static double relativeStrengthIndex(double[] price, int window) {
        double[] deltas = new double[price.length - 1];
        for (int i = 0; i < price.length - 1; i++) {
            deltas[i] = price[i + 1] - price[i];
        }
        double[] gain = new double[deltas.length];
        double[] loss = new double[deltas.length];
        for (int i = 0; i < deltas.length; i++) {
            gain[i] = deltas[i] > 0 ? deltas[i] : 0;
            loss[i] = deltas[i] < 0 ? -deltas[i] : 0;
        }
        double avgGain = Arrays.stream(gain).skip(gain.length - window).average().getAsDouble();
        double avgLoss = Arrays.stream(loss).skip(loss.length - window).average().getAsDouble();
        double relativeStrength = avgGain / avgLoss;
        double relativeStrengthIndex = 100 - (100 / (1 + relativeStrength));
        return relativeStrengthIndex;
    }

    public static double stochasticOscillator(double[] price, int window) {
        double highestHigh = Arrays.stream(price).skip(price.length - window).max().getAsDouble();
        double lowestLow = Arrays.stream(price).skip(price.length - window).min().getAsDouble();
        double stochasticOscillator = 100 * (price[price.length - 1] - lowestLow) / (highestHigh - lowestLow);
        return stochasticOscillator;
    }

    public static double[] calculateMacd(double[] stockPrices, int days26, int days12, int slidingWindow) {
        double fastMa = Arrays.stream(stockPrices).skip(stockPrices.length - days12).average().getAsDouble();
        double slowMa = Arrays.stream(stockPrices).skip(stockPrices.length - days26).average().getAsDouble();
        double macdLine = fastMa - slowMa;
        double signalLine = Arrays.stream(stockPrices).skip(stockPrices.length - days26 - slidingWindow + 1).limit(slidingWindow).average().getAsDouble() -
                Arrays.stream(stockPrices).skip(stockPrices.length - slidingWindow).limit(slidingWindow).average().getAsDouble();
        double macdHistogram = macdLine - signalLine;
        return new double[]{macdLine, signalLine, macdHistogram};
    }


    public static double calculateATR(List<Result> results, int period) {
        if (results == null || results.size() < period) {
            throw new IllegalArgumentException("Insufficient data to calculate ATR");
        }

        double atr = 0.0;

        // Calculate True Range (TR) for each period
        for (int i = 1; i < period; i++) {
            Result current = results.get(i);
            Result previous = results.get(i - 1);

            double highLow = current.getH() - current.getL();
            double highClose = Math.abs(current.getH() - previous.getC());
            double lowClose = Math.abs(current.getL() - previous.getC());

            double tr = Math.max(highLow, Math.max(highClose, lowClose));
            atr += tr;
        }

        // Average True Range (ATR)
        return atr / period;
    }

    public static double roundTo3SigFigs(double value) {
        if (value == 0) {
            return 0;
        }
        final double scale = Math.pow(10, 3 - Math.ceil(Math.log10(Math.abs(value))));
        return Math.round(value * scale) / scale;
    }
}
