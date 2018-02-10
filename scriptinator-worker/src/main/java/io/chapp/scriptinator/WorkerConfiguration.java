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
package io.chapp.scriptinator;

import jdk.nashorn.api.scripting.ClassFilter;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;

@Configuration
@ConfigurationProperties
public class WorkerConfiguration {
    private int workers = Runtime.getRuntime().availableProcessors();

    public int getWorkers() {
        return workers;
    }

    public void setWorkers(int workers) {
        this.workers = workers;
    }

    @Bean
    public NashornScriptEngineFactory engineFactory() {
        return new NashornScriptEngineFactory();
    }

    @Bean
    public ClassFilter classFilter() {
        return clazz -> false;
    }

    @Bean
    @Scope("prototype")
    public ScriptEngine scriptEngine(NashornScriptEngineFactory factory, ClassFilter classFilter) {
        ScriptEngine engine = factory.getScriptEngine(
                classFilter
        );

        // Remove dangerous functions
        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.remove("exit");
        bindings.remove("load");
        bindings.remove("loadWithNewGlobal");
        bindings.remove("quit");
        bindings.remove("$EXEC");
        bindings.remove("print");

        return engine;
    }
}
