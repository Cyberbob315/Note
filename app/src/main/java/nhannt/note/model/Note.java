package nhannt.note.model;

import java.io.Serializable;

/**
 * A note model class
 */

public class Note implements Serializable {
    private final int id;
    private final String title;
    private final String content;
    private final int color;
    private final long createdDate;
    private final long notifyDate;

    public Note(int id, String title, String content, int color, long createdDate, long notifyDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.color = color;
        this.createdDate = createdDate;
        this.notifyDate = notifyDate;
    }


    public int getId() {
        return id;
    }

    public int getColor() {
        return color;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public long getNotifyDate() {
        return notifyDate;
    }

}