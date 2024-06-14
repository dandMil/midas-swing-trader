package com.dandmil.midasswingtrader.controller;

import com.dandmil.midasswingtrader.adapters.AssetAdapter;
import com.dandmil.midasswingtrader.dto.AssetDTO;
import com.dandmil.midasswingtrader.entity.Asset;
import com.dandmil.midasswingtrader.entity.VolumeWatchlistEntry;
import com.dandmil.midasswingtrader.entity.WatchlistEntry;
import com.dandmil.midasswingtrader.repository.AssetRepository;
import com.dandmil.midasswingtrader.repository.VolumeWatchlistRepository;
import com.dandmil.midasswingtrader.repository.WatchListRepository;
import com.dandmil.midasswingtrader.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
public class MidasController {

    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private AssetAdapter assetAdapter;

    @Autowired
    AssetService assetService;

    @Autowired
    VolumeWatchlistRepository volumeWatchlistRepository;

    @Autowired
    TopMoversService topMoversService;

    @Autowired
    PythonCaller pythonCaller;


    @Autowired
    private WatchListRepository watchListRepository;
    private static final Logger logger = LoggerFactory.getLogger(MidasController.class);

    @GetMapping("/midas/asset/get_signal/{asset}/{type}")
    @CrossOrigin
    public CompletableFuture<Asset> getSignal(@PathVariable("asset")String asset, @PathVariable("type") String type){
        logger.info("REQUEST ASSET {} {}",asset,type);
        if(type.equals("crypto")) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("X:");
            stringBuilder.append(type.toUpperCase());
            stringBuilder.append(("USD"));
            type = stringBuilder.toString();
            stringBuilder.setLength(0);
        }
        return assetAdapter.getAssetData(asset,type);
    }

    @GetMapping("/midas/asset/get_all_assets")
    public List<AssetDTO>getAssets(){
        return assetService.getAllAssetsWithVolumesAndSignals();
    }


    @GetMapping("/midas/asset/top_movers")
    public CompletableFuture<ApiResponse> getTopMovers(){
        return topMoversService.fetchTopMovers().toFuture();
    }

    @GetMapping("/midas/asset/significant_volume")
    public CompletableFuture<ApiResponse>getSignificantVolume() throws IOException {
        topMoversService.loadTopMovers();
        return null;
    }


    @GetMapping("/midas/crypto/get_all")
    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    @PostMapping("/midas/asset/watch_list/")
    public ResponseEntity<String> addToWatchList(@RequestBody WatchlistEntry watchlistEntry) {
        try {
            watchlistEntry.setId(UUID.randomUUID());
            watchlistEntry.setDateCreated(new Date());
            watchListRepository.save(watchlistEntry);
            return ResponseEntity.status(HttpStatus.OK).body("Asset added to watchlist successfully");
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            // Return a 500 status code with an error message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding asset to watchlist");
        }
    }

    @DeleteMapping("midas/asset/watch_list/{asset}")
    public ResponseEntity<String> removeFromWatchList(@PathVariable("asset")String asset){
        try{
            watchListRepository.deleteByName(asset);
            return ResponseEntity.status(HttpStatus.OK).body("Asset Deleted successfully");
        } catch (Exception e){
        e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding asset to watchlist");

        }
    }

    @GetMapping("midas/asset/get_watch_list")
        public List<Asset> getAllFromWatchList(){
        logger.info("Called get_watch_list");
            List<WatchlistEntry> entries = watchListRepository.findAll();
            List<String>names = new ArrayList<>();

            for (WatchlistEntry entry: entries) {
                names.add(entry.getName());
            }
            List<Asset> assets;
//            assets = assetRepository.findAllByNameInOrderByDateCreatedDesc(names);
            assets = assetRepository.findAll();
            return assets;
        }



    @GetMapping("midas/asset/get_volume_watch_list")
    public List<VolumeWatchlistEntry> getAllFromVolumeWatchList(){
        logger.info("Called get_volume_watch_list");

        List<VolumeWatchlistEntry> entries = new ArrayList<>();
        try {
//            List<WatchlistEntry> entries = watchListRepository.findAll();
            entries = volumeWatchlistRepository.findAll();
//            responses = transformer.transformAssetToResponse(entries);
            return entries;
        }catch (Exception e){
            e.printStackTrace();
            return entries;
        }
    }
    }

