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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;

@Component
class AroundInitialStockUpdater implements StockUpdater {
    private final StockProperties stockProperties;
    private final Random random = new Random();

    AroundInitialStockUpdater(StockProperties stockProperties) {
        this.stockProperties = stockProperties;
    }

    @Override
    public BigDecimal update(String symbol, BigDecimal price) {
        final BigDecimal initialPrice = stockProperties.initialValues().get(symbol);
        if (initialPrice == null) {
            return price;
        }
        final BigDecimal growthRate;
        final var percent = random.nextInt(10);
        if (price.compareTo(initialPrice) > 0) {
            growthRate = BigDecimal.valueOf((100 - percent) / 100d);
        } else {
            growthRate = BigDecimal.valueOf(1 + percent / 100d);
        }
        return price.multiply(growthRate);
    }

    @Override
    public String id() {
        return "AROUND_INITIAL";
    }

    @Override
    public String toString() {
        return id();
    }
}
