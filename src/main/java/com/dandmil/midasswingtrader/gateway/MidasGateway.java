package com.dandmil.midasswingtrader.gateway;


import com.dandmil.midasswingtrader.pojo.PolygonResponse;
import org.springframework.stereotype.Component;
import org.springframework.messaging.Message;

public interface MidasGateway {

    void process (Message<PolygonResponse>message);
}
