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

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;

@Component
class DefaultStockUpdater implements StockUpdater {
    private final Random random = new Random();

    @Override
    public BigDecimal update(String symbol, BigDecimal price) {
        final var coeff = random.nextDouble(0.95, 1.05);
        return price.multiply(BigDecimal.valueOf(coeff));
    }

    @Override
    public String id() {
        return "DEFAULT";
    }

    @Override
    public String toString() {
        return id();
    }
}
