package com.dandmil.midasswingtrader.service;


import com.dandmil.midasswingtrader.pojo.PolygonResponse;
import com.dandmil.midasswingtrader.properties.MidasProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TechnicalIndicatorService {

    private final MidasProperties midasProperties;
    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(TechnicalIndicatorService.class);

   @Autowired
    public TechnicalIndicatorService(MidasProperties midasProperties, WebClient webClient){
       this.midasProperties = midasProperties;
       this.webClient = webClient;

    }

    @Scheduled(fixedRate = 30000)
    public void pullAssetData() {
        List<String> cryptoAssets = midasProperties.getCryptoAssets();
        for (String asset : cryptoAssets) {
            fetchPolygonData(asset).subscribe( response -> {
            logger.info("Response {}",response);
            });
        }

    }
    private Mono<PolygonResponse> fetchPolygonData (String asset){
        return  webClient.get()
                .uri(buildUri(asset))
                .retrieve()
                .bodyToMono(PolygonResponse.class);
   }


    private String buildUri(String cryptoTicker){
       StringBuilder sb = new StringBuilder();
       sb.append("https://api.polygon.io/v2/aggs/ticker/");
       sb.append(cryptoTicker);
       sb.append("/");
       sb.append("range");
       sb.append("/");
       sb.append("1");
       sb.append("/");
       sb.append("day");
       sb.append("/");
       sb.append(nDaysAgo(60));
       sb.append("/");
       sb.append(nDaysAgo(0));
       sb.append("?");
       sb.append("apiKey=");
       sb.append(midasProperties.getPolygonKey());

        return sb.toString();
    }

    private String nDaysAgo(int n) {
        LocalDate today = LocalDate.now();
        LocalDate daysAgo = today.minusDays(n);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return daysAgo.format(formatter);
    }
}
