package com.dandmil.midasswingtrader.service;

import com.dandmil.midasswingtrader.controller.MidasController;
import com.dandmil.midasswingtrader.dto.AssetDTO;
import com.dandmil.midasswingtrader.properties.MidasProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class SignalIndicatorService {


    @Autowired
    public SignalIndicatorService(){
    }


    public void computeIndicatorSignal(AssetDTO assetDTO){

    }
}
