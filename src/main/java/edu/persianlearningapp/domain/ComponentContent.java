package edu.persianlearningapp.domain;

/** UC-03 instructional text belonging to one configured component. */
public class ComponentContent {
    private int componentId;
    private String persianContent;
    private String urduContent;

    public ComponentContent(int componentId, String persianContent, String urduContent) {
        this.componentId = componentId;
        this.persianContent = persianContent;
        this.urduContent = urduContent;
    }
    public int getComponentId() { return componentId; }
    public void setComponentId(int componentId) { this.componentId = componentId; }
    public String getPersianContent() { return persianContent; }
    public void setPersianContent(String persianContent) { this.persianContent = persianContent; }
    public String getUrduContent() { return urduContent; }
    public void setUrduContent(String urduContent) { this.urduContent = urduContent; }
}
