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
