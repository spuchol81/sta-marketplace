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

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;

@Component
@ConditionalOnProperty(name = "app.stocks.updater", havingValue = "penny-stock")
class PennyStockUpdater implements StockUpdater {
    private final Random random = new Random();

    @Override
    public BigDecimal update(String symbol, BigDecimal price) {
        final BigDecimal growthRate;
        if (price.compareTo(BigDecimal.ONE) > 0) {
            final var percent = random.nextInt(25);
            growthRate = BigDecimal.valueOf((100 - percent) / 100d);
        } else {
            final var percent = random.nextInt(5);
            final boolean increaseStock = random.nextBoolean();
            growthRate = BigDecimal.valueOf(increaseStock ? (1 + percent / 100d) : (100 - percent) / 100d);
        }
        return price.multiply(growthRate);
    }

    @Override
    public String toString() {
        return "PENNY_STOCK";
    }
}
