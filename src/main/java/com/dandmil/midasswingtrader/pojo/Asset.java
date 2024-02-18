package com.dandmil.midasswingtrader.pojo;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "assets")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;
    private double marketPrice;
    private double macd;
    private double priceRateOfChange;
    private double relativeStrengthIndex;
    private double stochasticOscillator;

  @Transient
    private Map<String, Integer> indicatorScores;

    private String signal;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private String type;
}