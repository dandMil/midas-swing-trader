package com.dandmil.midasswingtrader.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "midas")
@Getter
@Setter
public class MidasProperties {
    private List<String> cryptoAssets;
    private String polygonUrl;
    private String polygonKey;
    private List<String>stockAssets;
}
