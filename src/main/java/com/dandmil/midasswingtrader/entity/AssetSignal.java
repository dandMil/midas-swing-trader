package com.dandmil.midasswingtrader.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "ASSET_SIGNALS")
public class AssetSignal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    @JsonBackReference
    private Asset asset;  // Ensure this field exists

    private BigDecimal marketPrice;
    private BigDecimal macd;
    private BigDecimal priceRateOfChange;
    private BigDecimal relativeStrengthIndex;
    private BigDecimal stochasticOscillator;
    private String indicatorScores;
    private String signal;
    private Date dateCreated;
}
