package com.example.taskmanager.service;

import com.example.taskmanager.entity.StockPrice;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;

@Service
public class StockPriceService {

    private final Random random = new Random();

    public Flux<StockPrice> getStockPrices(String symbol) {
        return Flux.interval(Duration.ofSeconds(2))  // Emit an update every second
                .map(i -> new StockPrice(
                        symbol.toUpperCase(),
                        100 + random.nextDouble() * 20, // Price between 100 and 120
                        System.currentTimeMillis()
                ));
    }
}
