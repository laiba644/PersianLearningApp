package edu.persianlearningapp.domain;

public class ChapterComponent {
    private int id;
    private int chapterId;
    private String componentType; // Replace with the agreed enum after UC-02 types are finalized.
    private int displayOrder;

    public ChapterComponent(int id, int chapterId, String componentType, int displayOrder) {
        this.id = id;
        this.chapterId = chapterId;
        this.componentType = componentType;
        this.displayOrder = displayOrder;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getChapterId() { return chapterId; }
    public void setChapterId(int chapterId) { this.chapterId = chapterId; }
    public String getComponentType() { return componentType; }
    public void setComponentType(String componentType) { this.componentType = componentType; }
    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }
}
