package edu.persianlearningapp.presentation;

import edu.persianlearningapp.business.ChapterBO;
import javax.swing.JPanel;

/** UC-01 presentation shell; wire buttons/forms to ChapterBO in Laiba's branch. */
public class ChapterManagementPanel extends JPanel {
    private final ChapterBO chapterBO;
    public ChapterManagementPanel(ChapterBO chapterBO) { this.chapterBO = chapterBO; }
    public ChapterBO getChapterBO() { return chapterBO; }
}
