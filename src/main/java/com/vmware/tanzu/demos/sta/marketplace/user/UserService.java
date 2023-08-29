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

package com.vmware.tanzu.demos.sta.marketplace.user;

import com.vmware.tanzu.demos.sta.marketplace.dao.StockValueRepository;
import com.vmware.tanzu.demos.sta.marketplace.dao.UserAccount;
import com.vmware.tanzu.demos.sta.marketplace.dao.UserAccountRepository;
import com.vmware.tanzu.demos.sta.marketplace.dao.UserStockHoldingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserAccountRepository uar;
    private final UserStockHoldingRepository ushr;
    private final StockValueRepository svr;

    public UserService(UserAccountRepository uar, UserStockHoldingRepository ushr, StockValueRepository svr) {
        this.uar = uar;
        this.ushr = ushr;
        this.svr = svr;
    }

    @Transactional
    public List<UserInfo> getUserInfos() {
        final var userAccounts = uar.findAll();
        final List<UserInfo> userInfos = new ArrayList<>(userAccounts.size());
        for (final UserAccount userAccount : userAccounts) {
            getUserInfo(userAccount.getUser()).ifPresent(userInfos::add);
        }
        return userInfos;
    }

    @Transactional
    public Optional<UserInfo> getUserInfo(String user) {
        final var userAccount = uar.findByUser(user);
        if (userAccount == null) {
            return Optional.empty();
        }
        final var userBalance = userAccount.getBalance();

        final var stockHoldings = ushr.findByUser(user);
        var totalEquities = BigDecimal.ZERO;
        final List<UserInfo.StockHolding> resStockHoldings;

        if (stockHoldings == null) {
            resStockHoldings = Collections.emptyList();
        } else {
            resStockHoldings = stockHoldings.stream().map(s -> new UserInfo.StockHolding(s.getSymbol(), s.getSharesOwned())).toList();

            for (final var holding : stockHoldings) {
                final var stockValue = svr.findFirstBySymbolIgnoreCaseOrderByUpdateTimeDesc(holding.getSymbol());
                if (stockValue != null) {
                    totalEquities = totalEquities.add(stockValue.getPrice().multiply(BigDecimal.valueOf(holding.getSharesOwned())));
                }
            }
        }

        return Optional.of(new UserInfo(user, userBalance, totalEquities, resStockHoldings));
    }

    @Transactional
    public UserInfo resetUser(String user, BigDecimal balance) {
        var userAccount = uar.findByUser(user);
        if (userAccount == null) {
            userAccount = new UserAccount();
            userAccount.setUser(user);
        }
        userAccount.setBalance(balance);
        uar.save(userAccount);
        return getUserInfo(user).orElseThrow();
    }

    @Transactional
    public Optional<UserInfo> updateUser(String user, BigDecimal balanceUpdate) {
        var userAccount = uar.findByUser(user);
        if (userAccount == null) {
            return Optional.empty();
        }
        userAccount.setBalance(userAccount.getBalance().add(balanceUpdate));
        uar.save(userAccount);
        return Optional.of(getUserInfo(user).orElseThrow());
    }

    @Transactional
    public void deleteUser(String user) {
        ushr.deleteByUser(user);
        uar.deleteByUser(user);
    }
}
