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

import io.chapp.scriptinator.model.OAuthApp;
import io.chapp.scriptinator.model.User;
import io.chapp.scriptinator.security.ApiScope;
import io.chapp.scriptinator.services.OAuthAppService;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserWebController {
    private final OAuthAppService appService;
    private final ObjectFactory<User> currentUserFactory;

    public UserWebController(OAuthAppService appService, ObjectFactory<User> currentUserFactory) {
        this.appService = appService;
        this.currentUserFactory = currentUserFactory;
    }

    @GetMapping("")
    public String showUserProfile() {
        return "pages/view_profile";
    }

    @GetMapping("apps")
    public String listOAuthApps(Model model) {
        model.addAttribute(OAuthApp.LIST_ATTRIBUTE, appService.findAllOwnedByPrincipal(
                PageRequest.of(
                        0,
                        Integer.MAX_VALUE
                )
        ));
        return "pages/view_apps";
    }

    /*===== New app =====*/

    @GetMapping("app")
    public String newOAuthApp(Model model) {
        model.addAttribute(OAuthApp.ATTRIBUTE, new OAuthApp());
        return "pages/new_app";
    }

    @PostMapping("app")
    public String createOAuthApp(@ModelAttribute(OAuthApp.ATTRIBUTE) OAuthApp appInfo) {
        appInfo.setOwner(currentUserFactory.getObject());
        appInfo = appService.create(appInfo);

        return "redirect:/user/apps/" + appInfo.getId();
    }

    /*===== Edit app =====*/

    @GetMapping("apps/{appId}")
    public String editOAuthApp(@PathVariable long appId, Model model) {
        model.addAttribute(OAuthApp.ATTRIBUTE, appService.getOwnedByPrincipal(appId));
        model.addAttribute("allScopes", ApiScope.allScopes());
        return "pages/edit_app";
    }

    @PostMapping("apps/{appId}")
    public String updateOAuthApp(@PathVariable long appId, @ModelAttribute(OAuthApp.ATTRIBUTE) OAuthApp appChanges, Model model) {
        OAuthApp app = appService.getOwnedByPrincipal(appId);
        app.setName(appChanges.getName());
        app.setDescription(appChanges.getDescription());
        app.setScopes(appChanges.getScopes());
        appService.update(app);

        return listOAuthApps(model);
    }

    @DeleteMapping("apps/{appId}")
    public String deleteOAuthApp(@PathVariable long appId, Model model) {
        appService.deleteIfOwnedByPrincipal(appId);
        return listOAuthApps(model);
    }
}
