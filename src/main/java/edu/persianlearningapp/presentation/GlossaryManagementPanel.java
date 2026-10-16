package edu.persianlearningapp.presentation;

import edu.persianlearningapp.business.GlossaryBO;
import javax.swing.JPanel;

/** UC-04 presentation shell; add glossary CRUD controls and learner visibility flow. */
public class GlossaryManagementPanel extends JPanel {
    private final GlossaryBO glossaryBO;
    public GlossaryManagementPanel(GlossaryBO glossaryBO) { this.glossaryBO = glossaryBO; }
    public GlossaryBO getGlossaryBO() { return glossaryBO; }
}
