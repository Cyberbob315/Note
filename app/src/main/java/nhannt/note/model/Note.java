package nhannt.note.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by IceMan on 12/30/2016.
 */

public class Note implements Serializable {
    private int id;
    private String title;
    private String content;
    private int color;
    private long createdDate;
    private long notifyDate;
    private ArrayList<String> imagePath;

    public Note(int id, String title, String content, int color, long createdDate, long notifyDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.color = color;
        this.createdDate = createdDate;
        this.notifyDate = notifyDate;
    }

    public Note(int id, String title, String content, int color, long createdDate, long notifyDate, ArrayList<String> imagePath) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.color = color;
        this.createdDate = createdDate;
        this.notifyDate = notifyDate;
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public long getNotifyDate() {
        return notifyDate;
    }

    public void setNotifyDate(long notifyDate) {
        this.notifyDate = notifyDate;
    }

    public ArrayList<String> getImagePath() {
        return imagePath;
    }

    public void setImagePath(ArrayList<String> imagePath) {
        this.imagePath = imagePath;
    }
}