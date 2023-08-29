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

package com.vmware.tanzu.demos.sta.marketplace.security;

import org.springframework.security.core.Authentication;

import java.util.List;

public final class ServiceAccount {
    private final Authentication auth;

    public ServiceAccount(final Authentication auth) {
        this.auth = auth;
    }

    public List<String> scopes() {
        return auth.getAuthorities().stream().map(a -> a.getAuthority().replace("SCOPE_", "")).toList();
    }

    public String user() {
        final var i = auth.getName().indexOf('_');
        if (i == -1) {
            return auth.getName();
        }
        return auth.getName().substring(i + 1);
    }

    public boolean isAdmin() {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("SCOPE_admin"));
    }

    @Override
    public String toString() {
        return user();
    }
}
