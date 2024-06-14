package com.dandmil.midasswingtrader.adapters;

import com.dandmil.midasswingtrader.gateway.MidasGateway;
import com.dandmil.midasswingtrader.entity.Asset;
import com.dandmil.midasswingtrader.pojo.polygon.PolygonResponse;
import com.dandmil.midasswingtrader.properties.MidasProperties;
import com.dandmil.midasswingtrader.service.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

import static com.dandmil.midasswingtrader.constants.Constants.FETCH_HISTORY;
import static com.dandmil.midasswingtrader.constants.Constants.FETCH_TOP_MOVERS;

@Service
public class PolygonAdapter extends Adapter {

    private static final Logger logger = LoggerFactory.getLogger(PolygonAdapter.class);

    private final WebClient webClient;
    private final MidasProperties midasProperties;
    private final MidasGateway midasGateway;

    @Autowired
    public PolygonAdapter(WebClient webClient, MidasProperties midasProperties, MidasGateway midasGateway) {
        this.webClient = webClient;
        this.midasProperties = midasProperties;
        this.midasGateway = midasGateway;
    }

    @Override
    public Mono<ApiResponse> makeApiCall(String assetName, String fetchCommand) {
        logRequest("Polygon endpoint for " + assetName);
        String endpoint = switch (fetchCommand){
            case FETCH_HISTORY -> buildPriceTickerUrl(assetName);
            case FETCH_TOP_MOVERS -> buildTopMoversUrl();
            default -> throw new IllegalStateException("Unexpected value: " + fetchCommand);
        };
        logRequest("Using command "+fetchCommand);

        return webClient.get()
                .uri(endpoint)
                .retrieve()
                .bodyToMono(PolygonResponse.class)
                .cast(ApiResponse.class);
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

    private Mono<PolygonResponse> fetchPolygonData(String asset) {
        logger.info("Calling POLYGON for {}", asset);
        return webClient.get()
                .uri(buildPriceTickerUrl(asset))
                .retrieve()
                .bodyToMono(PolygonResponse.class);
    }

    private String buildPriceTickerUrl(String cryptoTicker) {
        return new StringBuilder()
                .append("https://api.polygon.io/v2/aggs/ticker/")
                .append(cryptoTicker)
                .append("/range/1/day/")
                .append(nDaysAgo(60))
                .append("/")
                .append(nDaysAgo(0))
                .append("?apiKey=")
                .append(midasProperties.getPolygonKey())
                .toString();
    }

    private String buildTopMoversUrl(){
        return "https://api.polygon.io/v2/snapshot/locale/us/markets/stocks/"+"gainers?"+"apiKey="+midasProperties.getPolygonKey();
    }
    private String nDaysAgo(int n) {
        LocalDate today = LocalDate.now();
        LocalDate daysAgo = today.minusDays(n);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return daysAgo.format(formatter);
    }
}
