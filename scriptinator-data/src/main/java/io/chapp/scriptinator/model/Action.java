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
package io.chapp.scriptinator.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity
public class Action extends AbstractEntity {
    public static final String ATTRIBUTE = "action";
    public static final String LIST_ATTRIBUTE = "actions";
    @NotNull
    @Pattern(regexp = "[\\w]+")
    private String fullyQualifiedName;

    @NotNull
    private String description;

    @ManyToOne(optional = false)
    @NotNull
    private Project project;

    @ManyToOne(optional = false)
    @NotNull
    private Script script;

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public void setFullyQualifiedName(String fullyQualifiedName) {
        this.fullyQualifiedName = fullyQualifiedName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Script getScript() {
        return script;
    }

    public void setScript(Script script) {
        this.script = script;
    }
}
