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

import com.vmware.tanzu.demos.sta.marketplace.stock.StockProperties;
import com.vmware.tanzu.demos.sta.marketplace.stock.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

@Component
class StockMarketplaceInitializer implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger(StockMarketplaceInitializer.class);
    private final StockProperties stockProperties;
    private final StockService stockService;

    StockMarketplaceInitializer(StockProperties stockProperties, StockService stockService) {
        this.stockProperties = stockProperties;
        this.stockService = stockService;
    }

    @Override
    public void run(String... args) {
        final var nf = NumberFormat.getCurrencyInstance(Locale.US);
        for (final Map.Entry<String, BigDecimal> e : stockProperties.initialValues().entrySet()) {
            if (stockService.getStock(e.getKey()).isEmpty()) {
                final var symbol = e.getKey();
                final var price = e.getValue();
                logger.info("Initializing stock value: {}={}", symbol, nf.format(price));
                stockService.updateStockValue(symbol, price);
            }
        }
    }
}
