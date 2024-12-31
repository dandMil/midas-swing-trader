package com.dandmil.midasswingtrader.service;

import com.dandmil.midasswingtrader.adapters.PolygonAdapter;
import com.dandmil.midasswingtrader.constants.Constants;
import com.dandmil.midasswingtrader.entity.WatchlistEntry;
import com.dandmil.midasswingtrader.pojo.WatchListItem;
import com.dandmil.midasswingtrader.pojo.polygon.PolygonResponse;
import com.dandmil.midasswingtrader.repository.WatchListRepository;
import com.dandmil.midasswingtrader.transformer.ApiResponseTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.time.LocalDate;

@Service
public class TopMoversService {

    PolygonAdapter polygonAdapter;

    ApiResponseTransformer apiResponseTransformer;

    PythonCaller pythonCaller;

    WatchListRepository watchListRepository;

    TradeRecommendationService tradeRecommendationService;

    @Value("${top-movers-service.repeated-mover-window}")
    private int repeatedMoverWindow;

    private static final Logger logger = LoggerFactory.getLogger(TopMoversService.class);

    @Autowired
    public TopMoversService(PolygonAdapter polygonAdapter, PythonCaller pythonCaller, WatchListRepository watchListRepository, TradeRecommendationService tradeRecommendationService) {
        this.polygonAdapter = polygonAdapter;
        this.pythonCaller = pythonCaller;
        this.watchListRepository = watchListRepository;
        this.tradeRecommendationService = tradeRecommendationService;
    }

    public Mono<ApiResponse> fetchTopMovers(String mover) {
        String[] arguments = new String[1];
        if (mover.equals("loser")){
            arguments[0] = "losers";
        }
        else{
            arguments[0] = "gainers";
        }

        return polygonAdapter.makeApiCall(null, Constants.FETCH_TOP_MOVERS, 0, arguments);
    }

    public void loadTopMovers() {
        logger.info("Loading Top Movers");
        Mono<ApiResponse> apiResponseMono = polygonAdapter.makeApiCall(null, Constants.FETCH_TOP_MOVERS, 0, null);

        apiResponseMono.cast(PolygonResponse.class)
                .map(PolygonResponse::getTickerArray)
                .subscribe(tickers -> {
                    try {
                        pythonCaller.yahooSignificantVolume(tickers);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    // Scheduled method to fetch and save top movers
    // @Scheduled(cron = "0 0 22 * * MON-FRI")
//     @Scheduled(cron = "0 */1 * * * *")
    public void fetchAndSaveTopMovers() {
        String[] gainerArgs = {"gainers"};
        String[] loserArgs = {"losers"};

        Mono<ApiResponse> apiResponseMonoGainer = polygonAdapter.makeApiCall(null, Constants.FETCH_TOP_MOVERS, 0, gainerArgs);
        Mono<ApiResponse> apiResponseMonoLoser = polygonAdapter.makeApiCall(null, Constants.FETCH_TOP_MOVERS, 0, loserArgs);

        saveTopMovers(apiResponseMonoGainer, gainerArgs)
                .then(saveTopMovers(apiResponseMonoLoser, loserArgs))
                .subscribe();
    }

//    // New method to find repeated movers within the specified window
//    public List<String> findRepeatedMovers() {
//        logger.info("Finding repeated movers in the last {} days", repeatedMoverWindow);
//
//        // Calculate the start date based on the repeatedMoverWindow
//        LocalDateTime startDate = LocalDateTime.now().minusDays(repeatedMoverWindow);
//        String formattedStartDate = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
//
//        // Call repository method to find repeated movers
//        List<String> repeatedMovers = watchListRepository.findNamesWithAtLeastTwoEntriesInPeriod(formattedStartDate);
//
//        logger.info("Found repeated movers: {}", repeatedMovers);
//        return repeatedMovers;
//    }


    public List<WatchListItem> findRepeatedMovers() {
        logger.info("Finding repeated movers in the last {} days", repeatedMoverWindow);

        // Calculate the start date based on the repeatedMoverWindow
        LocalDate startDate = LocalDate.now().minusDays(repeatedMoverWindow); // Use LocalDate for date without timezone

        // Call repository method to find repeated movers
//        List<String> repeatedMovers = watchListRepository.findNamesWithAtLeastTwoEntriesInPeriod(startDate);
        List<WatchlistEntry> repeatedMovers = watchListRepository.findRecordsWithAtLeastTwoEntriesInPeriod(startDate);
        List<WatchListItem> watchListItems = new ArrayList<>();

        for (WatchlistEntry watchlistEntry : repeatedMovers){
            logger.info("Found repeated movers: {}", watchlistEntry.getName());
            WatchListItem watchListItem = new WatchListItem();
            watchListItem.setTicker(watchlistEntry.getName());
            watchListItem.setMovement(watchListItem.getMovement());
            watchListItem.setType(watchListItem.getType());
//            watchListItem.setRecommendation(tradeRecommendationService.calculateTradeRecommendations());
        }
        return watchListItems;
    }

    private Mono<Void> saveTopMovers(Mono<ApiResponse> apiResponseMono, String[] arguments) {
        return apiResponseMono
                .cast(PolygonResponse.class)
                .map(PolygonResponse::getTickers)
                .flatMap(tickers -> {
                    List<WatchlistEntry> watchlistEntries = Stream.of(tickers)
                            .map(ticker -> {
                                WatchlistEntry entry = new WatchlistEntry();
                                entry.setId(UUID.randomUUID());
                                entry.setName(ticker.getTicker());
                                entry.setType("Stock");
                                entry.setDateCreated(new Date());
                                entry.setMovement(arguments[0]);
                                entry.setPriceChange(ticker.getTodaysChange());
                                entry.setVolume(ticker.getDay().getV());
                                entry.setPrice(ticker.getDay().getC());
                                return entry;
                            })
                            .collect(Collectors.toList());
                    return Mono.fromRunnable(() -> watchListRepository.saveAll(watchlistEntries));
                })
                .then();
    }
}
