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

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Project extends AbstractEntity {
    public static final String ATTRIBUTE = "project";
    public static final String LIST_ATTRIBUTE = "projects";
    @NotEmpty
    @Length(min = 4)
    private String displayName;
    private String description;
    private boolean actionsEnabled = true;
    @ManyToOne(optional = false)
    private User owner;
    @OneToMany(mappedBy = "project", orphanRemoval = true)
    @NotNull
    private List<Script> scripts = new ArrayList<>();
    @OneToMany(mappedBy = "project", orphanRemoval = true)
    private List<Action> actions = new ArrayList<>();

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }


    public boolean isActionsEnabled() {
        return actionsEnabled;
    }

    public void setActionsEnabled(boolean actionsEnabled) {
        this.actionsEnabled = actionsEnabled;
    }

    public List<Script> getScripts() {
        return scripts;
    }

    public void setScripts(List<Script> scripts) {
        this.scripts = scripts;
    }
}
