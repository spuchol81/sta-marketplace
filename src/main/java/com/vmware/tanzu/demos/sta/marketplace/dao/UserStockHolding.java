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

import java.util.Objects;

@Entity
@Table(name = "USER_STOCK_HOLDING", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username", "symbol"}),
})
public class UserStockHolding {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "username", nullable = false, length = 128)
    private String user;
    @Column(nullable = false, length = 8)
    private String symbol;
    @Column(nullable = false)
    private Integer sharesOwned;

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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Integer getSharesOwned() {
        return sharesOwned;
    }

    public void setSharesOwned(Integer sharesOwned) {
        this.sharesOwned = sharesOwned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserStockHolding that)) return false;
        return Objects.equals(getUser(), that.getUser()) && Objects.equals(getSymbol(), that.getSymbol()) && Objects.equals(getSharesOwned(), that.getSharesOwned());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser(), getSymbol(), getSharesOwned());
    }
}
