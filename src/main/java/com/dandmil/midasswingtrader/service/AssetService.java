package com.dandmil.midasswingtrader.service;

import com.dandmil.midasswingtrader.dto.AssetDTO;
import com.dandmil.midasswingtrader.dto.AssetSignalDTO;
import com.dandmil.midasswingtrader.dto.AssetVolumeDTO;
import com.dandmil.midasswingtrader.entity.Asset;
import com.dandmil.midasswingtrader.entity.AssetSignal;
import com.dandmil.midasswingtrader.entity.AssetVolume;
import com.dandmil.midasswingtrader.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;


    public List<AssetDTO> getAllAssetsWithVolumesAndSignals() {
        List<Asset> assets = assetRepository.findAllAssets();
        List<UUID> assetIds = assets.stream().map(Asset::getAssetId).collect(Collectors.toList());

        List<AssetVolume> volumes = assetRepository.findAllAssetVolumeByAssetIds(assetIds);
        List<AssetSignal> signals = assetRepository.findAllAssetSignalByAssetIds(assetIds);

        Map<UUID, List<AssetVolume>> volumesMap = volumes.stream().collect(Collectors.groupingBy(volume -> volume.getAsset().getAssetId()));
        Map<UUID, List<AssetSignal>> signalsMap = signals.stream().collect(Collectors.groupingBy(signal -> signal.getAsset().getAssetId()));

        return assets.stream().map(asset -> {
            AssetDTO dto = convertToDTO(asset);
            dto.setAssetVolumes(volumesMap.getOrDefault(asset.getAssetId(), Collections.emptyList()).stream().map(this::convertToVolumeDTO).collect(Collectors.toList()));
            dto.setSignals(signalsMap.getOrDefault(asset.getAssetId(), Collections.emptyList()).stream().map(this::convertToSignalDTO).collect(Collectors.toList()));
            return dto;
        }).collect(Collectors.toList());
    }

    private AssetDTO convertToDTO(Asset asset) {
        AssetDTO dto = new AssetDTO();
        dto.setAssetId(asset.getAssetId());
        dto.setName(asset.getName());
        dto.setType(asset.getType());
        return dto;
    }

    private AssetVolumeDTO convertToVolumeDTO(AssetVolume volume) {
        AssetVolumeDTO volumeDTO = new AssetVolumeDTO();
        volumeDTO.setId(volume.getId());
        volumeDTO.setVolume(volume.getVolume());
        volumeDTO.setWeeklyIncrease(volume.getWeeklyIncrease());
        volumeDTO.setDailyIncrease(volume.getDailyIncrease());
        volumeDTO.setDateCreated(volume.getDateCreated());
        return volumeDTO;
    }

    private AssetSignalDTO convertToSignalDTO(AssetSignal signal) {
        AssetSignalDTO signalDTO = new AssetSignalDTO();
        signalDTO.setId(signal.getId());
        signalDTO.setMarketPrice(signal.getMarketPrice());
        signalDTO.setMacd(signal.getMacd());
        signalDTO.setPriceRateOfChange(signal.getPriceRateOfChange());
        signalDTO.setRelativeStrengthIndex(signal.getRelativeStrengthIndex());
        signalDTO.setStochasticOscillator(signal.getStochasticOscillator());
        signalDTO.setIndicatorScores(signal.getIndicatorScores());
        signalDTO.setSignal(signal.getSignal());
        signalDTO.setDateCreated(signal.getDateCreated());
        return signalDTO;
    }
}
