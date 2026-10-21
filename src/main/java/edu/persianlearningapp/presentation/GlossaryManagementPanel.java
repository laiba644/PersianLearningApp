package edu.persianlearningapp.presentation;

import edu.persianlearningapp.business.BusinessException;
import edu.persianlearningapp.business.GlossaryBO;
import edu.persianlearningapp.domain.GlossaryEntry;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import edu.persianlearningapp.dao.DataAccessException;
import edu.persianlearningapp.domain.Chapter;
public class GlossaryManagementPanel extends JPanel {

    private static final int COL_ENTRY_ID     = 0;  // hidden column
    private static final int COL_PERSIAN_WORD = 1;
    private static final int COL_URDU_MEANING = 2;


    // (Number / ID)
    private static final String HDR_ID =
        "نمبر";

    // (Persian word)
    private static final String HDR_PERSIAN =
        "فارسی لفظ";

    // (Urdu meaning)
    private static final String HDR_URDU =
        "اردو معنی";


    // (Refresh)
    private static final String LBL_REFRESH =
        "تازہ کریں";

    // (Add)
    private static final String LBL_ADD =
        "اضافہ کریں";

    // (Edit)
    private static final String LBL_EDIT =
        "ترمیم کریں";

    // (Delete)
    private static final String LBL_DELETE =
        "حذف کریں";

    // (Chapter number:)
    private static final String LBL_CHAPTER =
        "باب نمبر:";

    // "فارسی لفظ:"  (Persian word:)
    private static final String LBL_PERSIAN_FIELD =
        "فارسی لفظ:";

    // "اردو معنی:"  (Urdu meaning:)
    private static final String LBL_URDU_FIELD =
        "اردو معنی:";

    // "کیا آپ واقعی اس اندراج کو حذف کرنا چاہتے ہیں؟"
    // (Do you really want to delete this entry?)
    private static final String MSG_CONFIRM_DELETE =
        "کیا آپ واقعی اس "
        + "اندراج کو حذف "
        + "کرنا چاہتے ہیں؟";
    private static final String LBL_CANCEL = "منسوخ کریں";

    // "حذف کی تصدیق"  (Confirm deletion)
    private static final String TITLE_CONFIRM_DELETE =
        "حذف کی تصدیق";

    // "براہ کرم پہلے ایک اندراج منتخب کریں۔"
    // (Please select an entry first.)
    private static final String MSG_NO_SELECTION =
        "براہ کرم پہلے ایک "
        + "اندراج منتخب کریں۔";

    // "خرابی"  (Error)
    private static final String TITLE_ERROR =
        "خرابی";
 // "ڈیٹا بیس میں خرابی ہے۔ براہ کرم دوبارہ کوشش کریں۔"
    private static final String MSG_DB_ERROR =
        "ڈیٹا بیس میں خرابی ہے۔ "
        + "براہ کرم دوبارہ کوشش کریں۔";
    
    private final GlossaryBO glossaryBO;

    /** Spinner that holds the current chapter id selected by the admin. */
    /** Drop-down of existing chapters only (no typing allowed). */
    private final JComboBox<Chapter> chapterBox;

    /** True while the chapter list is being reloaded, to avoid extra refreshes. */
    private boolean loadingChapters = false;

    /** Table model that backs the glossary JTable. */
    private final DefaultTableModel tableModel;

    /** The visible JTable showing glossary entries. */
    private final JTable entryTable;

    /** Right-to-left text field for the Persian word input. */
    private final JTextField persianField;

    /** Right-to-left text field for the Urdu meaning input. */
    private final JTextField urduField;

    /** Font used for all Persian and Urdu text (NFR-US-04). */
    private final Font rtlFont;

    public GlossaryManagementPanel(GlossaryBO glossaryBO) {
        this.glossaryBO = glossaryBO;
        this.rtlFont    = resolveRtlFont();
        

        // Table model: 3 columns, no cells editable directly.
        // Column 0 (entry_id) is kept hidden so we have the id when editing/deleting.
        tableModel = new DefaultTableModel(
                new Object[]{HDR_ID, HDR_PERSIAN, HDR_URDU}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;   // users edit via the text fields, not inline
            }
        };

        entryTable   = buildTable();
        persianField = buildRtlTextField(20);
        urduField    = buildRtlTextField(30);
        chapterBox = buildChapterBox();
        setLayout(new BorderLayout(6, 6));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildNorthPanel(),  BorderLayout.NORTH);
        add(new JScrollPane(entryTable), BorderLayout.CENTER);
        add(buildSouthPanel(), BorderLayout.SOUTH);
        loadChapters();
    }

    public GlossaryBO getGlossaryBO() {
        return glossaryBO;
    }

    private JPanel buildNorthPanel() {
        JPanel north = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        north.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel chapLabel = new JLabel(LBL_CHAPTER);
        chapLabel.setFont(rtlFont);
        chapLabel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JButton refreshBtn = new JButton(LBL_REFRESH);
        refreshBtn.setFont(rtlFont);
        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTable();
            }
        });

        north.add(refreshBtn);
        north.add(chapterBox);
        north.add(chapLabel);
        JLabel title = new JLabel("لغت کا انتظام", SwingConstants.CENTER);
        title.setFont(rtlFont);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(title, BorderLayout.NORTH);
        wrapper.add(north, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildSouthPanel() {
        // Use a GridBagLayout for a clean label-field alignment.
        JPanel south = new JPanel(new GridBagLayout());
        south.setBorder(BorderFactory.createTitledBorder(""));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill   = GridBagConstraints.HORIZONTAL;

        // Row 0: Persian label + field
        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        JLabel persianLabel = new JLabel(LBL_PERSIAN_FIELD);
        persianLabel.setFont(rtlFont);
        persianLabel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        south.add(persianLabel, c);

        c.gridx = 1; c.weightx = 1;
        south.add(persianField, c);

        // Row 1: Urdu label + field
        c.gridx = 0; c.gridy = 1; c.weightx = 0;
        JLabel urduLabel = new JLabel(LBL_URDU_FIELD);
        urduLabel.setFont(rtlFont);
        urduLabel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        south.add(urduLabel, c);

        c.gridx = 1; c.weightx = 1;
        south.add(urduField, c);

        // Row 2: Add, Edit, Delete buttons (right-aligned)
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        buttonRow.add(buildAddButton());
        buttonRow.add(buildEditButton());
        buttonRow.add(buildDeleteButton());

        c.gridx = 0; c.gridy = 2; c.gridwidth = 2; c.weightx = 1;
        south.add(buttonRow, c);

        return south;
    }
    private JTable buildTable() {
        JTable table = new JTable(tableModel);
        table.setFont(rtlFont);
        table.setRowHeight(45);
        table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        table.getTableHeader().setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        table.getTableHeader().setFont(rtlFont);

        // Hide the entry_id column (index 0) — we need the value but not the display.
        table.getColumnModel().getColumn(COL_ENTRY_ID).setMinWidth(0);
        table.getColumnModel().getColumn(COL_ENTRY_ID).setMaxWidth(0);
        table.getColumnModel().getColumn(COL_ENTRY_ID).setWidth(0);

        // Make the table single-selection so edit/delete are unambiguous.
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // When a row is selected, pre-fill the text fields for easy editing.
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                prefillFieldsFromSelection();
            }
        });

        return table;
    }

    /** Build the Add button and wire its action. */
    private JButton buildAddButton() {
        JButton btn = new JButton(LBL_ADD);
        btn.setFont(rtlFont);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAdd();
            }
        });
        return btn;
    }

    /** Build the Edit button and wire its action. */
    private JButton buildEditButton() {
        JButton btn = new JButton(LBL_EDIT);
        btn.setFont(rtlFont);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleEdit();
            }
        });
        return btn;
    }

    /** Build the Delete button and wire its action. */
    private JButton buildDeleteButton() {
        JButton btn = new JButton(LBL_DELETE);
        btn.setFont(rtlFont);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDelete();
            }
        });
        return btn;
    }
    /** Build the chapter drop-down: choose from the list, no typing. */
    private JComboBox<Chapter> buildChapterBox() {
        JComboBox<Chapter> box = new JComboBox<Chapter>();
        box.setFont(rtlFont);
        box.setEditable(false);
        box.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                if (value instanceof Chapter) {
                    Chapter c = (Chapter) value;
                    label.setText(c.getId() + " - " + c.getTitle());
                }
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                return label;
            }
        });
        // Picking a chapter shows its glossary straight away.
        box.addActionListener(e -> {
            if (!loadingChapters) {
                refreshTable();
            }
        });
        return box;
    }

    /** Reload the chapter list from the BO, keep the selection, then refresh the table. */
    private void loadChapters() {
        int keepId = currentChapterId();
        loadingChapters = true;
        try {
            chapterBox.removeAllItems();
            for (Chapter c : glossaryBO.listChapters()) {
                chapterBox.addItem(c);
            }
            for (int i = 0; i < chapterBox.getItemCount(); i++) {
                if (chapterBox.getItemAt(i).getId() == keepId) {
                    chapterBox.setSelectedIndex(i);
                    break;
                }
            }
        } catch (BusinessException ex) {
            showError(ex.getMessage());
        } catch (DataAccessException ex) {
            showError(MSG_DB_ERROR);
        } finally {
            loadingChapters = false;
        }
        refreshTable();
    }

    private JTextField buildRtlTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(rtlFont);
        field.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        field.setHorizontalAlignment(SwingConstants.RIGHT);
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new NoLatinFilter());
        return field;
    }
    private void handleAdd() {
        int chapterId = currentChapterId();
        try {
            glossaryBO.addEntry(chapterId,
                                persianField.getText(),
                                urduField.getText());
            refreshTable();
            clearFields();
        } catch (BusinessException ex) {
            // Show the Urdu error message; leave the typed text intact (BR-07).
            showError(ex.getMessage());
        } catch (DataAccessException ex) {
            showError(MSG_DB_ERROR);
        }
    }
    private void handleEdit() {
        int selectedRow = entryTable.getSelectedRow();
        if (selectedRow < 0) {
            showError(MSG_NO_SELECTION);
            return;
        }

        int chapterId = currentChapterId();
        int entryId   = (Integer) tableModel.getValueAt(selectedRow, COL_ENTRY_ID);

        try {
            glossaryBO.editEntry(chapterId, entryId,
                                 persianField.getText(),
                                 urduField.getText());
            refreshTable();
            clearFields();
        } catch (BusinessException ex) {
            showError(ex.getMessage());
        } catch (DataAccessException ex) {
            showError(MSG_DB_ERROR);
        }
    }
    private void handleDelete() {
        int selectedRow = entryTable.getSelectedRow();
        if (selectedRow < 0) {
            showError(MSG_NO_SELECTION);
            return;
        }

        int chapterId = currentChapterId();
        int entryId   = (Integer) tableModel.getValueAt(selectedRow, COL_ENTRY_ID);

        // Show the Urdu confirmation dialog (BR-04, NFR-US-05).
        Object[] options = {LBL_DELETE, LBL_CANCEL};
        int choice = JOptionPane.showOptionDialog(
                this,
                MSG_CONFIRM_DELETE,
                TITLE_CONFIRM_DELETE,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[1]);

        boolean confirmed = (choice == 0);
        try {
            glossaryBO.deleteEntry(chapterId, entryId, confirmed);
            if (confirmed) {
                refreshTable();
                clearFields();
            }
        } catch (BusinessException ex) {
            showError(ex.getMessage());
            refreshTable();
        } catch (DataAccessException ex) {
            showError(MSG_DB_ERROR);
        }
    }
    private void refreshTable() {
        int chapterId = currentChapterId();

        // Remove all existing rows.
        tableModel.setRowCount(0);

        try {
            List<GlossaryEntry> entries = glossaryBO.listForAdmin(chapterId);
            for (GlossaryEntry entry : entries) {
                tableModel.addRow(new Object[]{
                    entry.getId(),
                    entry.getPersianWord(),
                    entry.getUrduMeaning()
                });
            }
        } catch (BusinessException ex) {
            showError(ex.getMessage());
        } catch (DataAccessException ex) {
            showError(MSG_DB_ERROR);
        }
    }
    private void prefillFieldsFromSelection() {
        int row = entryTable.getSelectedRow();
        if (row >= 0) {
            Object word    = tableModel.getValueAt(row, COL_PERSIAN_WORD);
            Object meaning = tableModel.getValueAt(row, COL_URDU_MEANING);
            persianField.setText(word    == null ? "" : word.toString());
            urduField.setText(meaning == null ? "" : meaning.toString());
        }
    }
    /** Id of the chapter currently selected by the admin. */
    private int currentChapterId() {
        Chapter c = (Chapter) chapterBox.getSelectedItem();
        if (c == null) {
            return -1;
        } else {
            return c.getId();
        }
    }
    private void clearFields() {
        persianField.setText("");
        urduField.setText("");
    }
    private void showError(String message) {
        // Set dialog text direction to right-to-left so Urdu reads naturally.
        JLabel label = new JLabel(message);
        label.setFont(rtlFont);
        label.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        JOptionPane.showMessageDialog(this, label, TITLE_ERROR,
                                      JOptionPane.ERROR_MESSAGE);
    }
    private static Font resolveRtlFont() {
    	/** Blocks English (Latin) letters, including pasted text. */
    	
        return new Font("Arial", Font.PLAIN, 30);
    }
    /** Blocks English (Latin) letters, including pasted text. */
    private static class NoLatinFilter extends DocumentFilter {
        private static String clean(String s) {
            if (s == null) return null;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                char ch = s.charAt(i);
                if (Character.UnicodeScript.of(ch) != Character.UnicodeScript.LATIN) {
                    sb.append(ch);
                }
            }
            return sb.toString();
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String text,
                                 AttributeSet attr) throws BadLocationException {
            super.insertString(fb, offset, clean(text), attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text,
                            AttributeSet attrs) throws BadLocationException {
            super.replace(fb, offset, length, clean(text), attrs);
        }
    }

}
