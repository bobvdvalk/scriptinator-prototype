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

import io.chapp.scriptinator.model.ScriptDtos;
import io.chapp.scriptinator.services.ScriptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scripts")
public class ScriptController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ScriptService scriptService;

    public ScriptController(ScriptService scriptService) {
        this.scriptService = scriptService;
    }

    @RequestMapping("/script/{id}/")
    @ResponseBody
    public ScriptDtos getScript(@PathVariable("script-id") int scriptId) {
        logger.info("retrieving script with id: "+ scriptId);
        return ScriptDtos.convert(scriptService.get(scriptId));
    }

    @RequestMapping("/script/{project-id}/{script-name}")
    @ResponseBody
    public ScriptDtos getScriptByProjectIdAndFullyQualifiedName(@PathVariable("project-id") int projectId,
                                                                @PathVariable("script-name") String qualifiedName) {
        logger.info("retrieving script with project-id: " + projectId + " and name: "+ qualifiedName);
        return ScriptDtos.convert(scriptService.get(projectId, qualifiedName));
    }
}
