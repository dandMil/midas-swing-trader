package com.dandmil.midasswingtrader.dto;


import lombok.Data;

import java.util.Date;
import java.util.UUID;


@Data
public class AssetVolumeDTO {
    private UUID id;
    private Double volume;
    private Double weeklyIncrease;
    private Double dailyIncrease;
    private Date dateCreated;



}