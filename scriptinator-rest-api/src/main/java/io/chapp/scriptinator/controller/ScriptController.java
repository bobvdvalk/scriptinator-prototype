package io.chapp.scriptinator.controller;

import io.chapp.scriptinator.model.ScriptDtos;
import io.chapp.scriptinator.services.ScriptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scripts")
public class ScriptController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ScriptService scriptService;

    public ScriptController(ScriptService scriptService) {
        this.scriptService = scriptService;
    }

    @RequestMapping("/script")
    @ResponseBody
    public ScriptDtos getScript(@RequestParam("script-id") int scriptId) {
        logger.info("retrieving script with id: "+ scriptId);
        return ScriptDtos.convert(scriptService.get(scriptId));
    }

    @RequestMapping("/script")
    @ResponseBody
    public ScriptDtos getScriptByProjectIdAndFullyQualifiedName(@RequestParam("project-id") int projectId,
                                                                @RequestParam("script-name") String qualifiedName) {
        logger.info("retrieving script with project-id: " + projectId + " and name: "+ qualifiedName);
        return ScriptDtos.convert(scriptService.get(projectId, qualifiedName));
    }
}
