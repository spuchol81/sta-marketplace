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

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@CrossOrigin
@RestController
class UserController {
    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/v1/users/{user}")
    @Operation(summary = "Lookup user info", tags = "users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<UserInfo> getUserInfo(@PathVariable("user") String user) {
        return userService.getUserInfo(user).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/api/v1/users/{user}")
    @Operation(summary = "Reset user info", tags = "users")
    @Hidden
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    UserInfo resetUser(@PathVariable("user") String user, @RequestBody ResetUserReq req) {
        return userService.resetUser(user, req.balance());
    }

    @PostMapping("/api/v1/users/{user}")
    @Operation(summary = "Update user info", tags = "users")
    @Hidden
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    ResponseEntity<UserInfo> updateUser(@PathVariable("user") String user, @RequestBody UpdateUserReq req) {
        return userService.updateUser(user, req.balanceDiff()).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/v1/users/{user}")
    @Operation(summary = "Delete user", tags = "users")
    @Hidden
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    void deleteUser(@PathVariable("user") String user) {
        userService.deleteUser(user);
    }

    record ResetUserReq(
            BigDecimal balance
    ) {
    }

    record UpdateUserReq(
            BigDecimal balanceDiff
    ) {
    }
}
