package edu.persianlearningapp.presentation;

import edu.persianlearningapp.business.StructureBO;
import javax.swing.JPanel;

/** UC-02 presentation shell; wire component selection/order controls to StructureBO. */
public class StructureManagementPanel extends JPanel {
    private final StructureBO structureBO;
    public StructureManagementPanel(StructureBO structureBO) { this.structureBO = structureBO; }
    public StructureBO getStructureBO() { return structureBO; }
}
