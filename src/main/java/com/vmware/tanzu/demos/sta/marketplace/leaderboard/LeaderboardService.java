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

import com.vmware.tanzu.demos.sta.marketplace.user.UserService;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

import static java.util.Comparator.comparing;

@Component
class LeaderboardService {
    private final UserService userService;

    LeaderboardService(UserService userService) {
        this.userService = userService;
    }

    Leaderboard getLeaderboard() {
        final var userInfos = userService.getUserInfos();
        final Leaderboard.Entry[] entries = userInfos.stream().map(userInfo -> new Leaderboard.Entry(userInfo.user(), userInfo.balance(), userInfo.equities())).sorted(comparing(Leaderboard.Entry::balance).reversed()).toArray(Leaderboard.Entry[]::new);
        return new Leaderboard(entries, ZonedDateTime.now());
    }
}
