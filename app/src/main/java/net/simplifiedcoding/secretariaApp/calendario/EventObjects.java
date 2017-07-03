package net.simplifiedcoding.secretariaApp.calendario;

import java.io.Serializable;
import java.util.Date;

public class EventObjects implements Serializable{
    private int id;
    private String message;
    private Date start;
    private Date end;

    public EventObjects(int id, String message, Date start, Date end) {
        this.start = start;
        this.end = end;
        this.message = message;
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public String getMessage() {
        return message;
    }
    public Date getDateStart() {
        return start;
    }
    public Date getDateEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "EventObjects{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
