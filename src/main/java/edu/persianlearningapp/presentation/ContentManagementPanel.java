package edu.persianlearningapp.presentation;

import edu.persianlearningapp.business.ContentBO;
import javax.swing.JPanel;

/** UC-03 presentation shell; add Unicode text fields and save controls. */
public class ContentManagementPanel extends JPanel {
    private final ContentBO contentBO;
    public ContentManagementPanel(ContentBO contentBO) { this.contentBO = contentBO; }
    public ContentBO getContentBO() { return contentBO; }
}
