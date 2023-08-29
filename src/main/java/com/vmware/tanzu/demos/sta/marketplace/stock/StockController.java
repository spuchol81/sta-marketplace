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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
class StockController {
    private final Logger logger = LoggerFactory.getLogger(StockController.class);
    private final StockService stockService;

    StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping(value = "/api/v1/stocks/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Lookup stock", tags = "stocks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<Stock> getStock(@PathVariable("symbol") String symbol) {
        return stockService.getStock(symbol).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/api/v1/stocks", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get current values for stocks", tags = "stocks")
    List<Stock> getStocks() {
        return stockService.getStockLastValues();
    }

    @GetMapping(value = "/api/v1/stocks/{symbol}/values", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get stock past values", tags = "stocks")
    List<StockHistoricValue> getStockPastValues(@PathVariable("symbol") String symbol) {
        return stockService.getStockValues(symbol);
    }
}
