package com.dandmil.midasswingtrader.controller;

import com.dandmil.midasswingtrader.pojo.Asset;
import com.dandmil.midasswingtrader.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class MidasController {

    @Autowired
    AssetRepository assetRepository;

//    @GetMapping("/midas/crypto/get_all")
//    public Mono<List<Asset>> getAllAssets() {
//        return Mono.fromCallable(() -> assetRepository.findAll());
//    }

    @GetMapping("/midas/crypto/get_all")
    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }
}

