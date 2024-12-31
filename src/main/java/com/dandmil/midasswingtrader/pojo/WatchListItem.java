package com.dandmil.midasswingtrader.pojo;

import lombok.Data;
import java.util.Date;

@Data
public class WatchListItem {

    private String ticker;
    private String type;
    private Date dateCreated;
    private String movement;
    private double price;
    private double volume;
    private TradeRecommendation recommendation;
    private AssetSignalIndicator assetSignalIndicator;
}
