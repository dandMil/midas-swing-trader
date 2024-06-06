package com.dandmil.midasswingtrader.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "ASSETS")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID assetId;

    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    private String type;

    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<AssetVolume> assetVolume;

    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<AssetSignal> assetSignals;  // Ensure this matches the field name in AssetSignal
}
