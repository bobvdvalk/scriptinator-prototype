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
