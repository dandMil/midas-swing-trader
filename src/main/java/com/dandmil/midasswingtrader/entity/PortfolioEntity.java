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
@Table(name = "PORTFOLIO")
public class PortfolioEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;

    private String type;
    int shares;
    Double price;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

}
