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

package com.vmware.tanzu.demos.sta.marketplace.leaderboard;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

record Leaderboard(
        Entry[] entries,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        ZonedDateTime updateTime

) {
    @Override
    public String toString() {
        return Arrays.stream(entries).map(Record::toString).collect(Collectors.joining(", "));
    }

    record Entry(
            String user,
            BigDecimal balance,
            BigDecimal equities
    ) {
        @Override
        public String toString() {
            final NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            return String.format("%s/%s/%s", user, nf.format(balance), nf.format(equities));
        }
    }
}
