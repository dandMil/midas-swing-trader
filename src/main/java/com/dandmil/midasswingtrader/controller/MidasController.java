package com.dandmil.midasswingtrader.controller;

import com.dandmil.midasswingtrader.gateway.MidasGateway;
import com.dandmil.midasswingtrader.pojo.Asset;
import com.dandmil.midasswingtrader.repository.AssetRepository;
import com.dandmil.midasswingtrader.service.AssetAdapter;
import com.dandmil.midasswingtrader.service.ComputeSignalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
public class MidasController {

    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private AssetAdapter assetAdapter;




    @GetMapping("/midas/asset/get_signal/{asset}/{type}")
    public CompletableFuture<Asset> getSignal(@PathVariable("asset")String asset, @PathVariable("type") String type){
        return assetAdapter.getAssetData(asset,type);
    }
    @GetMapping("/midas/crypto/get_all")
    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }
}

