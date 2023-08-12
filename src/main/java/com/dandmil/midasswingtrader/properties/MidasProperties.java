package com.dandmil.midasswingtrader.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConfigurationProperties(prefix = "midas")
@Getter
@Setter
public class MidasProperties {
    private List<String> cryptoAssets;
    private String polygonUrl;
    private String polygonKey;
}
