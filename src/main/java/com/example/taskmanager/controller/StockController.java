package com.example.taskmanager.controller;

import com.example.taskmanager.entity.StockPrice;
import com.example.taskmanager.service.StockPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/stocks")
public class StockController {

    @Autowired
    private StockPriceService service;

    @GetMapping(value = "/{symbol}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<StockPrice> streamStockPrices(@PathVariable String symbol) {
        return service.getStockPrices(symbol);
    }
}
