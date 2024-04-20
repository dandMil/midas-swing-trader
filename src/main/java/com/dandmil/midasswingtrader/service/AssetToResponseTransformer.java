package com.dandmil.midasswingtrader.service;

import com.dandmil.midasswingtrader.pojo.*;
import com.dandmil.midasswingtrader.repository.AssetRepository;
import com.dandmil.midasswingtrader.repository.VolumeWatchlistRepository;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.Transformer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetToResponseTransformer {

    @Autowired
    AssetRepository assetRepository;

    @Autowired
    VolumeWatchlistRepository volumeWatchlistRepository;


    public List<AssetResponse> transformAssetToResponse(List<WatchlistEntry>watchlistEntries){
        List<String>names = new ArrayList<>();

        for (WatchlistEntry entry: watchlistEntries) {
            names.add(entry.getName());
        }

        List<Asset> assets = assetRepository.findAllByNameInOrderByDateDesc(names);

        List<AssetResponse> assetResponses = assets.stream()
                .filter(asset -> {
                    // Check if the asset exists in both repositories
                    return assetRepository.existsByName(asset.getName()) &&
                            volumeWatchlistRepository.existsByName(asset.getName());
                })
                .map(asset -> {
                    // Query the repository for VolumeWatchlistEntry by asset name, ordered by date created
                    List<VolumeWatchlistEntry> volumeWatchlistEntries = volumeWatchlistRepository.findAllByNameOrderByDateCreatedDesc(asset.getName());

                    // Create a new AssetResponse DTO object for each asset
                    AssetResponse assetResponse = new AssetResponse();
                    assetResponse.setName(asset.getName());
                    assetResponse.setType(asset.getType());
                    assetResponse.setSignal(asset.getSignal());
                    assetResponse.setLastUpdated(new Date()); // Set the lastUpdated date, you can set it according to your requirement
                    assetResponse.setMarketPrice(asset.getMarketPrice());

                    // Create a list of Volume objects and set it into the AssetResponse object
                    List<Volume> volumes = volumeWatchlistEntries.stream()
                            .map(entry -> {
                                Volume volume = new Volume();
                                volume.setVolume(entry.getVolume());
                                volume.setDateCreated(entry.getDateCreated());
                                volume.setDailyIncrease(entry.getDailyIncrease());
                                volume.setWeeklyIncrease(entry.getWeeklyIncrease());
                                return volume;
                            })
                            .collect(Collectors.toList());
                    assetResponse.setVolumeHistory(volumes);

                    if (!volumes.isEmpty()) {
                        // Set additional properties from the first volume entry
                        Volume firstVolume = volumes.get(0);
                        assetResponse.setDailyIncrease(firstVolume.getDailyIncrease());
                        assetResponse.setWeeklyIncrease(firstVolume.getWeeklyIncrease());
                        assetResponse.setVolume(firstVolume.getVolume());
                    }

                    return assetResponse;
                })
                .collect(Collectors.toList());

        // Now you have a list of AssetResponse DTO objects
        return assetResponses;
    }
}
