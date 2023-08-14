package com.dandmil.midasswingtrader.gateway;


import com.dandmil.midasswingtrader.pojo.PolygonResponse;
import org.springframework.stereotype.Component;

public interface MidasGateway {

    void process (PolygonResponse response);
}
