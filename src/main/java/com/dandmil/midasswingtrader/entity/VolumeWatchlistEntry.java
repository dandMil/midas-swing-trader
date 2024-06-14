package com.dandmil.midasswingtrader.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "volume_watchlist")
public class VolumeWatchlistEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
    private Double volume;
    private Double dailyIncrease;
    private Double weeklyIncrease;
    private String type;
    private Double price;
}
