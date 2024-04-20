package com.dandmil.midasswingtrader.service;
import com.google.common.util.concurrent.RateLimiter;


import com.dandmil.midasswingtrader.gateway.MidasGateway;
import com.dandmil.midasswingtrader.pojo.Asset;
import com.dandmil.midasswingtrader.pojo.PolygonResponse;
import com.dandmil.midasswingtrader.pojo.WatchlistEntry;
import com.dandmil.midasswingtrader.properties.MidasProperties;
import com.dandmil.midasswingtrader.repository.WatchListRepository;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class AssetAdapter {

    private final MidasProperties midasProperties;
    private final WebClient webClient;

    private final MidasGateway midasGateway;

    private final WatchListRepository watchListRepository;

    private final RateLimiter rateLimiter = RateLimiter.create(5.0 / 60.0);

    private static final Logger logger = LoggerFactory.getLogger(AssetAdapter.class);

   @Autowired
    public AssetAdapter(MidasProperties midasProperties, WebClient webClient, MidasGateway midasGateway, WatchListRepository watchListRepository){
       this.midasProperties = midasProperties;
       this.webClient = webClient;
       this.midasGateway = midasGateway;
       this.watchListRepository = watchListRepository;
    }







    // Assuming fetchPolygonData returns a Mono<PolygonResponse>

//    @Scheduled(cron = "0 * 0 * * *") // Run once every hour at the start of the hour
    public void checkWatchListData() {
        logger.info("WatchList cron job has started");
        List<WatchlistEntry> entries = watchListRepository.findAll();
        StringBuilder stringBuilder = new StringBuilder();

        // Maintain a counter to keep track of requests made in the current minute
        AtomicInteger requestsInCurrentMinute = new AtomicInteger(0);

        // Track the time when the last request was made
        final LocalDateTime[] lastRequestTime = {LocalDateTime.now()};

        for (WatchlistEntry entry : entries) {
            try {
                if (entry.getName() != null) {
                    String assetName = entry.getName().toUpperCase();

                    if (entry.getType().equals("crypto")) {
                        stringBuilder.append("X:");
                        stringBuilder.append(entry.getName().toUpperCase());
                        stringBuilder.append("USD");
                        assetName = stringBuilder.toString();
                        stringBuilder.setLength(0);
                    }

                    // Check if the rate limit for the current minute has been reached
                    if (requestsInCurrentMinute.get() < 4) {
                        logger.info("REQUEST COUNT AT {}", requestsInCurrentMinute);

                        // Increment the request counter
                        requestsInCurrentMinute.incrementAndGet();

                        fetchPolygonData(assetName)
                                .subscribe(
                                        response -> {
                                            Message<PolygonResponse> message = MessageBuilder.withPayload(response)
                                                    .setHeader("type", entry.getType()).build();
                                            midasGateway.process(message);
                                        },
                                        error -> {
                                            // Handle the error, for example, log it
                                            logger.error("Error occurred while fetching polygon data: {}", error.getMessage());
                                        },
                                        () -> {
                                            // Update the last request time when the subscription completes
                                            lastRequestTime[0] = LocalDateTime.now();
                                        }
                                );
                    } else {
                        // If the request limit is reached, wait for 1 minute and 30 seconds
                        LocalDateTime currentTime = LocalDateTime.now();
                        LocalDateTime nextAllowedRequestTime = lastRequestTime[0].plusMinutes(1).plusSeconds(30);
                        long waitTimeSeconds = ChronoUnit.SECONDS.between(currentTime, nextAllowedRequestTime);

                        if (waitTimeSeconds > 0) {
                            logger.info("Request limit reached for the current minute. Waiting for {} seconds.", waitTimeSeconds);
                            Thread.sleep(waitTimeSeconds * 1000); // Convert seconds to milliseconds
                        }

                        // Reset the request counter and update the last request time
                        requestsInCurrentMinute.set(0);
                        lastRequestTime[0] = LocalDateTime.now();
                    }
                }
            } catch (Exception e) {
                logger.error("ERROR IN ASSET ADAPTER {}", e);
            }
        }
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
       logger.info("Calling POLYGON for {}",asset);
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
