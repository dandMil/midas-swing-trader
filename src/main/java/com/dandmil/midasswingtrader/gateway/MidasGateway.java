package com.dandmil.midasswingtrader.gateway;


import com.dandmil.midasswingtrader.entity.Asset;
import com.dandmil.midasswingtrader.pojo.polygon.PolygonResponse;
import org.springframework.integration.annotation.Gateway;
import org.springframework.messaging.Message;

public interface MidasGateway {

    @Gateway(requestChannel = "cronChannel")
    void process (Message<PolygonResponse>message);


    @Gateway(requestChannel = "getSignalChannel")
    Asset getSignal (Message<PolygonResponse>message);
}
