package com.dandmil.midasswingtrader.service;


import com.dandmil.midasswingtrader.gateway.MidasGateway;
import com.dandmil.midasswingtrader.pojo.Asset;
import com.dandmil.midasswingtrader.pojo.PolygonResponse;
import com.dandmil.midasswingtrader.properties.MidasProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class AssetAdapter {

    private final MidasProperties midasProperties;
    private final WebClient webClient;

    private final MidasGateway midasGateway;

    private static final Logger logger = LoggerFactory.getLogger(AssetAdapter.class);

   @Autowired
    public AssetAdapter(MidasProperties midasProperties, WebClient webClient, MidasGateway midasGateway){
       this.midasProperties = midasProperties;
       this.webClient = webClient;
       this.midasGateway = midasGateway;
    }




//    @Scheduled(fixedRate = 300000)
    public void pullAssetCryptoData() {
        List<String> cryptoAssets = midasProperties.getCryptoAssets();
        String type = "Crypto";
        for (String asset : cryptoAssets) {
            fetchPolygonData(asset).subscribe( response -> {
                Message<PolygonResponse> message = MessageBuilder.withPayload(response)
                        .setHeader("type",type).build();
                midasGateway.process(message);
            });
        }
    }

//    @Scheduled(fixedRate = 300000)
    public void pullAssetStockData() {
        List<String> cryptoAssets = midasProperties.getStockAssets();
        String type = "Stock";
        for (String asset : cryptoAssets) {
            fetchPolygonData(asset).subscribe( response -> {
                logger.info("Response {}",response);
                Message<PolygonResponse> message = MessageBuilder.withPayload(response)
                                .setHeader("type",type).build();
                midasGateway.process(message);
            });
        }
    }


    public CompletableFuture<Asset> getAssetData(String assetName, String type) {
        CompletableFuture<Asset> completableFuture = new CompletableFuture<>();
        fetchPolygonData(assetName).subscribe(response -> {
            Message<PolygonResponse> message = MessageBuilder.withPayload(response)
                    .setHeader("type", type).build();
            try {
                Asset asset = midasGateway.getSignal(message);
                completableFuture.complete(asset);
            } catch (Exception e) {
                completableFuture.completeExceptionally(e);
            }
        }, error -> completableFuture.completeExceptionally(error));
        return completableFuture;
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
