/*
 * Copyright (c) 2023 VMware, Inc. or its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vmware.tanzu.demos.sta.marketplace.sim;

import com.vmware.tanzu.demos.sta.marketplace.stock.Stock;
import com.vmware.tanzu.demos.sta.marketplace.stock.StockProperties;
import com.vmware.tanzu.demos.sta.marketplace.stock.StockService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Service
class StockMarketplaceSimulator {
    private final Logger logger = LoggerFactory.getLogger(StockMarketplaceSimulator.class);
    private final StockService stockService;
    private final boolean frozen;
    private final StockProperties stockProperties;
    private final StockUpdaterLocator stockUpdaterLocator;

    StockMarketplaceSimulator(StockService stockService, @Value("${app.stocks.frozen}") boolean frozen, StockProperties stockProperties, StockUpdaterLocator stockUpdaterLocator) {
        this.stockService = stockService;
        this.stockProperties = stockProperties;
        this.stockUpdaterLocator = stockUpdaterLocator;
        this.frozen = frozen;
    }

    @Scheduled(fixedDelayString = "${app.stocks.refresh-rate}")
    @Transactional
    public void update() {
        if (!frozen) {
            updateStocks();
        }
    }

    void updateStocks() {
        logger.debug("Updating stocks");

        final var stocks = stockService.getStockLastValues();
        final var newStocks = new ArrayList<StockUpdate>(stocks.size());
        for (final Stock stock : stocks) {
            final BigDecimal newPrice = findStockUpdater(stock.symbol()).update(stock.symbol(), stock.price());
            final StockUpdate up = new StockUpdate(stock.symbol(), stock.price(), newPrice);
            newStocks.add(up);
            stockService.updateStockValue(up.symbol, up.newPrice);
        }

        if (!newStocks.isEmpty()) {
            newStocks.sort(comparing(StockUpdate::symbol));
            final var newStocksStr = newStocks.stream().map(StockUpdate::toString).collect(Collectors.joining(", "));
            logger.info("Stocks updated: {}", newStocksStr);
        }
    }

    private StockUpdater findStockUpdater(String symbol) {
        final var updater = stockUpdaterLocator.getUpdater(symbol);
        if (updater == null) {
            throw new IllegalStateException("Cannot find StockUpdater for symbol: " + symbol);
        }
        return updater;
    }

    record StockUpdate(String symbol, BigDecimal oldPrice, BigDecimal newPrice) {
        @Override
        public String toString() {
            final NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            final String changeSymbol;
            switch (oldPrice.compareTo(newPrice)) {
                case -1 -> changeSymbol = "↑";
                case 1 -> changeSymbol = "↓";
                default -> changeSymbol = "→";
            }
            return String.format("%s (%s) %s->%s", symbol, changeSymbol, nf.format(oldPrice), nf.format(newPrice));
        }
    }
}
