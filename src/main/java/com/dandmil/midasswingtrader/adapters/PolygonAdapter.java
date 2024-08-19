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

import static com.dandmil.midasswingtrader.constants.Constants.*;

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
    public Mono<ApiResponse> makeApiCall(String assetName, String fetchCommand,
                                         int timeRange, String[] arguments) {
        logRequest("Polygon endpoint for " + assetName);
        String endpoint = switch (fetchCommand){
            case FETCH_HISTORY -> buildPriceTickerUrl(assetName);
            case FETCH_TOP_MOVERS -> buildTopMoversUrl(arguments[0]);
            case FETCH_BARS -> buildBarsTickerUrl(assetName,timeRange,0);
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


//    https://api.polygon.io/v2/aggs/ticker/AAPL/range/1/day/2023-01-09/
//    // 2023-02-10?adjusted=true&sort=asc&apiKey=q5L0XMSpFfIyyE0q_zJWgQaZ0U8aUqMK

    private String buildBarsTickerUrl(String ticker, int start, int end) {
        return new StringBuilder()
                .append("https://api.polygon.io/v2/aggs/ticker/")
                .append(ticker)
                .append("/range/1/day/")
                .append(nDaysAgo(start))
                .append("/")
                .append(nDaysAgo(end))
                .append("?adjusted=true&sort=asc&")
                .append("apiKey=")
                .append(midasProperties.getPolygonKey())
                .toString();
    }

    private String buildTopMoversUrl(String direction){
        logger.info("Calling Top Movers {}" ,direction);
        return "https://api.polygon.io/v2/snapshot/locale/us/markets/stocks/"+direction+"?"+"apiKey="+midasProperties.getPolygonKey();
    }
    private String nDaysAgo(int n) {
        LocalDate today = LocalDate.now();
        LocalDate daysAgo = today.minusDays(n);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return daysAgo.format(formatter);
    }
}
