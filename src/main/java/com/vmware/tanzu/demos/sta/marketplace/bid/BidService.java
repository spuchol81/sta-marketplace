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

package com.vmware.tanzu.demos.sta.marketplace.bid;

import com.vmware.tanzu.demos.sta.marketplace.dao.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Service
class BidService {
    private final StockValueRepository svr;
    private final UserAccountRepository uar;
    private final UserStockTransactionRepository ustr;
    private final UserStockHoldingRepository ushr;

    BidService(StockValueRepository svr, UserAccountRepository uar, UserStockTransactionRepository ustr, UserStockHoldingRepository ushr) {
        this.svr = svr;
        this.uar = uar;
        this.ustr = ustr;
        this.ushr = ushr;
    }

    @Transactional
    public int saveUserStockTransaction(String user, String symbol, int shares) {
        final StockValue stock = svr.findFirstBySymbolIgnoreCaseOrderByUpdateTimeDesc(symbol);
        if (stock == null) {
            throw new IllegalArgumentException("Cannot find stock: " + stock);
        }

        var userAccount = uar.findByUser(user);
        if (userAccount == null) {
            userAccount = new UserAccount();
            userAccount.setUser(user);
            userAccount.setBalance(BigDecimal.ZERO);
        }

        final var tx = new UserStockTransaction();
        tx.setUser(user);
        tx.setStockSymbol(symbol);
        tx.setStockPrice(stock.getPrice());
        tx.setTransactionTime(ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        tx.setShares(shares);
        ustr.save(tx);

        final BigDecimal stockTotal = stock.getPrice().multiply(BigDecimal.valueOf(shares));
        userAccount.setBalance(userAccount.getBalance().subtract(stockTotal));
        uar.save(userAccount);

        var userStockHolding = ushr.findByUserAndSymbol(user, symbol);
        if (userStockHolding == null) {
            userStockHolding = new UserStockHolding();
            userStockHolding.setUser(user);
            userStockHolding.setSymbol(symbol);
            userStockHolding.setSharesOwned(0);
        }

        final var newSharesOwned = userStockHolding.getSharesOwned() + shares;
        if (newSharesOwned < 0) {
            throw new IllegalStateException("User " + user + " cannot hold negative amount of shares for " + symbol);
        }

        userStockHolding.setSharesOwned(newSharesOwned);
        ushr.save(userStockHolding);

        var coeff = BigDecimal.ONE;
        if (shares > 0) {
            coeff = BigDecimal.valueOf(1.1);
        } else if (shares < 0) {
            coeff = BigDecimal.valueOf(0.9);
        }

        final var newPrice = stock.getPrice().multiply(coeff);
        final var sv = new StockValue();
        sv.setSymbol(symbol);
        sv.setPrice(newPrice);
        sv.setUpdateTime(ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        svr.save(sv);

        return newSharesOwned;
    }
}
