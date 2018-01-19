package io.chapp.scriptinator.services;

import io.chapp.scriptinator.model.Project;
import io.chapp.scriptinator.script.Script;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Service;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ScriptExecutor {
    private final ObjectFactory<ScriptEngine> engineProvider;

    public ScriptExecutor(ObjectFactory<ScriptEngine> engineProvider) {
        this.engineProvider = engineProvider;
    }

    public void run(Project project, Path projectPath, String script) throws IOException, ScriptException {
        ScriptEngine engine = buildEngine("/" + script);

        try (Reader reader = Files.newBufferedReader(projectPath.resolve(script))) {
            engine.eval(reader);
        }
    }

    private ScriptEngine buildEngine(String scriptPath) {
        ScriptEngine engine = engineProvider.getObject();

        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        Path simpleScriptFile = Paths.get(scriptPath);

        bindings.put("script", new Script(simpleScriptFile.toString(), simpleScriptFile.getParent().toString()));
        bindings.remove("exit");
        bindings.remove("load");
        bindings.remove("loadWithNewGlobal");
        bindings.remove("quit");

        return engine;
    }
}
