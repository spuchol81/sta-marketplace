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
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
class StockUpdaterLocator {
    private final List<StockUpdater> allStockUpdaters;
    private final Map<String, StockUpdater> stockUpdaters = new HashMap<>(8);

    StockUpdaterLocator(List<StockUpdater> allStockUpdaters, StockProperties stockProperties) {
        this.allStockUpdaters = allStockUpdaters;
        stockProperties.updaters().forEach((key, value) -> stockUpdaters.put(key.toLowerCase(), findUpdater(value)));
    }

    private StockUpdater findUpdater(String id) {
        return allStockUpdaters.stream().filter(s -> normalizeId(s.id()).equals(id)).findFirst().orElseThrow();
    }

    StockUpdater getUpdater(String symbol) {
        return stockUpdaters.get(symbol.toLowerCase());
    }

    StockUpdater setUpdater(String symbol, String updaterId) {
        final var updater = findUpdater(updaterId);
        stockUpdaters.put(symbol.toLowerCase(), updater);
        return updater;
    }

    private static String normalizeId(String id) {
        return id.toLowerCase().replace('_', '-');
    }
}
