package com.dandmil.midasswingtrader.configuration;

import com.dandmil.midasswingtrader.properties.MidasProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@EnableConfigurationProperties(MidasProperties.class)
public class MidasConfiguration {
}
