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
@Table(name = "TRADE_RECOMMENDATION")
public class TradeRecommendationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String strategy;
    private String ticker;
    private double entryPrice;
    private double takeProfit;
    private double stopLoss;
    private double expectedLoss;
    private double expectedProfit;
    private Date recommendationDate;

}
