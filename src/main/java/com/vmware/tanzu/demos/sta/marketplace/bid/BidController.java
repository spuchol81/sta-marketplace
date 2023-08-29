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

import com.vmware.tanzu.demos.sta.marketplace.security.ServiceAccount;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@CrossOrigin
@RestController
class BidController {
    private final Logger logger = LoggerFactory.getLogger(BidController.class);
    private final BidService bidService;
    private final boolean frozen;

    BidController(BidService bidService, @Value("${app.stocks.frozen}") boolean frozen) {
        this.bidService = bidService;
        this.frozen = frozen;
    }

    @PostMapping(value = "/api/v1/bids", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Place user bid", tags = "bids")
    @Parameter(name = "acc", hidden = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(hidden = true)), description = "Cannot process bid request")})
    @SecurityRequirement(name = "oauth2", scopes = "bid")
    @PreAuthorize("hasAnyAuthority('SCOPE_bid', 'SCOPE_admin')")
    ResponseEntity<BidResponse> proceedBidRequest(@RequestBody BidRequest bidRequest, ServiceAccount acc) {
        logger.info("Received bid request from {}: {} x {}", bidRequest.user(), bidRequest.shares(), bidRequest.symbol());

        if (frozen) {
            logger.warn("Bidding activities are forbidden");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }

        if (!acc.user().equals(bidRequest.user()) && !acc.isAdmin()) {
            logger.warn("Service account {} cannot place bids for user {}", acc, bidRequest.user());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        final var sharesOwned = bidService.saveUserStockTransaction(bidRequest.user(), bidRequest.symbol(), bidRequest.shares());
        return ResponseEntity.ok(new BidResponse(bidRequest.user(), bidRequest.symbol(), sharesOwned));
    }

    @ExceptionHandler
    ProblemDetail handleBidError(IllegalStateException e) {
        final var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setType(URI.create("urn:sta/errors/bid-request-failed"));
        pd.setTitle(e.getMessage());
        return pd;
    }
}
