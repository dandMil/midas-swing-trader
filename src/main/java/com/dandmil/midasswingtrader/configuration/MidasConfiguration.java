package com.dandmil.midasswingtrader.configuration;

import com.dandmil.midasswingtrader.properties.MidasProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MidasProperties.class)
public class MidasConfiguration {
}
