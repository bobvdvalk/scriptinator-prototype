package io.chapp.scriptinator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@JsonPropertyOrder({"id", "url"})
public class AbstractEntity {
    @Id
    @GeneratedValue
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + name() + ")";
    }

    public Link getUrl() {
        return new Link("/" + getClass().getSimpleName().toLowerCase() + "s/" + getId());
    }

    @JsonIgnore
    public String name() {
        return Integer.toString(getId());
    }

}
