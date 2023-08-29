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

package com.vmware.tanzu.demos.sta.marketplace.dao;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "USER_STOCK_TRANSACTION")
public class UserStockTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "username", nullable = false, length = 128)
    private String user;
    @Column(nullable = false, length = 8)
    private String stockSymbol;
    @Column(nullable = false)
    private BigDecimal stockPrice;
    @Column(nullable = false)
    private Integer shares;
    @Column(nullable = false)
    private ZonedDateTime transactionTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public BigDecimal getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(BigDecimal stockPrice) {
        this.stockPrice = stockPrice;
    }

    public Integer getShares() {
        return shares;
    }

    public void setShares(Integer shares) {
        this.shares = shares;
    }

    public ZonedDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(ZonedDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserStockTransaction that)) return false;
        return Objects.equals(getUser(), that.getUser()) && Objects.equals(getStockSymbol(), that.getStockSymbol()) && Objects.equals(getStockPrice(), that.getStockPrice()) && Objects.equals(getShares(), that.getShares()) && Objects.equals(getTransactionTime(), that.getTransactionTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser(), getStockSymbol(), getStockPrice(), getShares(), getTransactionTime());
    }
}
