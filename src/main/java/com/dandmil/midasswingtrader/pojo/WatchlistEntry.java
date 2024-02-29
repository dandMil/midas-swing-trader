package com.dandmil.midasswingtrader.pojo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "watch_list")
public class WatchlistEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String name;
    private String type;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
}
