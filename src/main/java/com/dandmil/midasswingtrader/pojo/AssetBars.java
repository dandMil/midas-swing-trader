package com.dandmil.midasswingtrader.pojo;

import com.dandmil.midasswingtrader.pojo.polygon.PolygonResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AssetBars {

    //Highest high for time period
    double resistance;

    //Lowest low for tie period
    double support;

    double timeRange;


    PolygonResponse polygonResponse;



}
