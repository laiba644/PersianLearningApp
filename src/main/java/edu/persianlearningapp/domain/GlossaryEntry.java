package edu.persianlearningapp.domain;

public class GlossaryEntry {
    private int id;
    private int chapterId;
    private String persianWord;
    private String urduMeaning;

    public GlossaryEntry(int id, int chapterId, String persianWord, String urduMeaning) {
        this.id = id;
        this.chapterId = chapterId;
        this.persianWord = persianWord;
        this.urduMeaning = urduMeaning;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getChapterId() { return chapterId; }
    public void setChapterId(int chapterId) { this.chapterId = chapterId; }
    public String getPersianWord() { return persianWord; }
    public void setPersianWord(String persianWord) { this.persianWord = persianWord; }
    public String getUrduMeaning() { return urduMeaning; }
    public void setUrduMeaning(String urduMeaning) { this.urduMeaning = urduMeaning; }
}
