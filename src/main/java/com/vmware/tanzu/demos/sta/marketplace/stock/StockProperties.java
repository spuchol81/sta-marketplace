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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "app.stocks")
public class StockProperties {
    private Map<String, BigDecimal> initialValues;
    private Map<String, String> updaters;

    public Map<String, BigDecimal> initialValues() {
        return initialValues;
    }

    public void setInitialValues(Map<String, BigDecimal> initialValues) {
        this.initialValues = Map.copyOf(initialValues);
    }

    public Map<String, String> updaters() {
        return updaters;
    }

    public void setUpdaters(Map<String, String> updaters) {
        this.updaters = updaters;
    }
}
