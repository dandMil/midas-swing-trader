package com.dandmil.midasswingtrader.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AssetDTO {

    private UUID assetId;
    private String name;
    private String type;
    private List<AssetVolumeDTO> assetVolumes;
    private List<AssetSignalDTO> signals;


}
