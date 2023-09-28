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

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
class SimulatorController {
    private final Logger logger = LoggerFactory.getLogger(SimulatorController.class);
    private final StockUpdaterLocator stockUpdaterLocator;

    SimulatorController(StockUpdaterLocator stockUpdaterLocator) {
        this.stockUpdaterLocator = stockUpdaterLocator;
    }

    @PutMapping(value = "/api/v1/stocks/{symbol}/updater", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Update stock updater", tags = "stocks")
    @Hidden
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    String setStockUpdater(@PathVariable("symbol") String symbol, @RequestBody StockUpdaterReq req) {
        logger.info("Setting stock updater: {}->{}", symbol, req.updater);
        return stockUpdaterLocator.setUpdater(symbol, req.updater).id();
    }

    @GetMapping(value = "/api/v1/stocks/{symbol}/updater", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Get stock updater", tags = "stocks")
    @Hidden
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    String getStockUpdater(@PathVariable("symbol") String symbol) {
        return stockUpdaterLocator.getUpdater(symbol).id();
    }

    record StockUpdaterReq(String updater) {
    }
}
