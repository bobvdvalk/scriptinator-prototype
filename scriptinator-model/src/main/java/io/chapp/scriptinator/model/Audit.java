package io.chapp.scriptinator.model;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(indexes = @Index(columnList = "time"))
public class Audit extends AbstractEntity {
    private Date time;
    private String subjectLink;
    private String subjectName;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubjectLink() {
        return subjectLink;
    }

    public void setSubjectLink(String subjectLink) {
        this.subjectLink = subjectLink;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
