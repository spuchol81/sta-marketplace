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

package com.vmware.tanzu.demos.sta.marketplace.stock;

import com.vmware.tanzu.demos.sta.marketplace.dao.StockValue;
import com.vmware.tanzu.demos.sta.marketplace.dao.StockValueRepository;
import com.vmware.tanzu.demos.sta.marketplace.dao.UserAccountRepository;
import com.vmware.tanzu.demos.sta.marketplace.dao.UserStockTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class StockService {
    private final StockValueRepository svr;
    private final UserAccountRepository uar;
    private final UserStockTransactionRepository ustr;

    public StockService(StockValueRepository svr, UserAccountRepository uar, UserStockTransactionRepository ustr) {
        this.svr = svr;
        this.uar = uar;
        this.ustr = ustr;
    }

    @Transactional
    public Optional<Stock> getStock(String symbol) {
        final var s = svr.findFirstBySymbolIgnoreCaseOrderByUpdateTimeDesc(symbol);
        if (s == null) {
            return Optional.empty();
        }
        return Optional.of(new Stock(s.getSymbol(), s.getPrice(), s.getUpdateTime()));
    }

    @Transactional
    public List<Stock> getStockLastValues() {
        return svr.findSymbols().stream().map(svr::findFirstBySymbolIgnoreCaseOrderByUpdateTimeDesc).map(s -> new Stock(s.getSymbol(), s.getPrice(), s.getUpdateTime())).toList();
    }

    @Transactional
    public List<StockHistoricValue> getStockValues(String symbol) {
        return svr.findTop50BySymbolOrderByUpdateTimeDesc(symbol).stream().map(s -> new StockHistoricValue(s.getUpdateTime(), s.getPrice())).sorted(Comparator.comparing(StockHistoricValue::time)).toList();
    }

    @Transactional
    public void updateStockValue(String symbol, BigDecimal price) {
        final var sv = new StockValue();
        sv.setSymbol(symbol);
        sv.setPrice(price);
        sv.setUpdateTime(ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        svr.save(sv);
    }
}
