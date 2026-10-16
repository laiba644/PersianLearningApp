package edu.persianlearningapp.domain;

public class LearningEvent {
    private final String type;
    private final int chapterId;
    public LearningEvent(String type, int chapterId) {
        this.type = type;
        this.chapterId = chapterId;
    }
    public String getType() { return type; }
    public int getChapterId() { return chapterId; }
}
