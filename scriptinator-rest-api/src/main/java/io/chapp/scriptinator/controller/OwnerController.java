/*
 * Copyright © 2018 Thomas Biesaart (thomas.biesaart@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.chapp.scriptinator.controller;

import io.chapp.scriptinator.model.OwnerDtos;
import io.chapp.scriptinator.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class OwnerController {
    private final Logger logger = LoggerFactory.getLogger(OwnerController.class);
    private final UserService userService;

    public OwnerController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/{username}")
    @ResponseBody
    public OwnerDtos ownerDtos(@PathVariable("username") String username) {
        logger.info("requested user information of " + username);
        return OwnerDtos.convert(userService.getByUsername(username));
    }
}
