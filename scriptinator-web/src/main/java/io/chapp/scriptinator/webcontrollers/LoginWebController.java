/*
 * Copyright © 2018 Scriptinator (support@scriptinator.io)
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
package io.chapp.scriptinator.webcontrollers;

import io.chapp.scriptinator.model.User;
import io.chapp.scriptinator.services.UserRegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.net.URI;

@Controller
public class LoginWebController {
    private final UserRegistrationService userRegistrationService;

    public LoginWebController(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }

    @RequestMapping("/login")
    public String showLoginPage(Model model, HttpSession httpSession) {
        if (httpSession.getAttribute("email") != null) {
            model.addAttribute("newUserEmail", httpSession.getAttribute("email"));
            httpSession.removeAttribute("email");
        }
        return "pages/login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("newUser", new User());
        return "pages/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("newUser") User user, Model model, HttpServletRequest request, HttpSession httpSession) {
        if (!userRegistrationService.register(user, URI.create(request.getRequestURL().toString()))) {
            model.addAttribute("error", "A user with that username already exists");
            return "pages/register";
        }
        if (userRegistrationService.isEmailVerificationEnabled()) {
            httpSession.setAttribute("email", user.getEmail());
        }

        return "redirect:/login";
    }

    @GetMapping("/register/activate")
    public String activate(@RequestParam("token") String activationToken) {
        userRegistrationService.activate(activationToken);
        return "pages/activate";
    }
}
