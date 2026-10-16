package edu.persianlearningapp.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Chapter {
    private int id;
    private String title;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private final List<ChapterComponent> components = new ArrayList<ChapterComponent>();
    private final List<GlossaryEntry> glossaryEntries = new ArrayList<GlossaryEntry>();

    public Chapter(int id, String title) {
        this.id = id;
        this.title = title;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    public List<ChapterComponent> getComponents() { return Collections.unmodifiableList(components); }
    public void addComponent(ChapterComponent component) { components.add(component); }
    public List<GlossaryEntry> getGlossaryEntries() { return Collections.unmodifiableList(glossaryEntries); }
    public void addGlossaryEntry(GlossaryEntry entry) { glossaryEntries.add(entry); }
}
