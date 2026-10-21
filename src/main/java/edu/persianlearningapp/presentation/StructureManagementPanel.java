package edu.persianlearningapp.presentation;

import edu.persianlearningapp.business.ChapterBO;
import edu.persianlearningapp.business.StructureBO;
import edu.persianlearningapp.domain.Chapter;
import edu.persianlearningapp.domain.ChapterComponent;
import edu.persianlearningapp.domain.ComponentType;
import edu.persianlearningapp.observer.LearningEventSource;
import edu.persianlearningapp.observer.Observable;
import edu.persianlearningapp.observer.Observer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

public class StructureManagementPanel extends JPanel implements Observer {

    private static final long serialVersionUID = 1L;
    // business layer say existing structure ko access krnay k liye or new add krnay k liye
    private final StructureBO structureBO;
    private final ChapterBO chapterBO;
    // swing 
    private final JComboBox<String> chapterComboBox =
            new JComboBox<String>();

    private final JComboBox<ComponentType> componentTypeComboBox =
            new JComboBox<ComponentType>(ComponentType.values());

    private final JTextField displayOrderField = new JTextField(10);

    private final DefaultListModel<ChapterComponent> componentModel =
            new DefaultListModel<ChapterComponent>();

    private final JList<ChapterComponent> componentList =
            new JList<ChapterComponent>(componentModel);
    // buttons ki list
    private final JButton addButton = new JButton("جزو شامل کریں");
    private final JButton deleteButton = new JButton("جزو حذف کریں");
    private final JButton saveButton = new JButton("ساخت محفوظ کریں");
    private final JButton refreshButton = new JButton("تازہ کریں");
    private final JButton clearButton = new JButton("خانے صاف کریں");
    // data base say chapters k load honay tak k time ko mange krnay k liye
    private boolean loadingChapters;
    // observer pattern + jo start say MainFrame say objects aa rhay hain (Singleton pattern)
    public StructureManagementPanel(
            StructureBO structureBO,
            ChapterBO chapterBO,
            LearningEventSource eventSource) {

        this.structureBO = structureBO;
        this.chapterBO = chapterBO;
        
        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel(
                "ابواب کے اجزاء کا انتظام", JLabel.CENTER);
        add(heading, BorderLayout.NORTH);

        configureRenderers();

        componentList.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        
        loadChapters();

        chapterComboBox.addActionListener(event -> {
            if (!loadingChapters) {
                loadSelectedStructure();
                updateActionButtons();
            }
        });

        componentList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                updateActionButtons();
            }
        });

        add(createMainPanel(), BorderLayout.CENTER);
        add(createButtons(), BorderLayout.SOUTH);

        applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        loadSelectedStructure();
        updateActionButtons();

        // observer pattern - observer ko add kr rhay hain
        eventSource.addObserver(this);
    }

   // update button states matlab content load honay par buttons ko active krna
    private void updateActionButtons() {
        boolean hasChapter = chapterComboBox.getSelectedIndex() > 0;
        boolean hasComponent = componentList.getSelectedValue() != null;

        addButton.setEnabled(hasChapter);
        saveButton.setEnabled(hasChapter);
        refreshButton.setEnabled(hasChapter);
        deleteButton.setEnabled(hasChapter && hasComponent);

        clearButton.setEnabled(true);

        componentTypeComboBox.setEnabled(hasChapter);
        displayOrderField.setEnabled(hasChapter);
    }

    private void configureRenderers() {
        componentTypeComboBox.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focused) {

                JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, selected, focused);

                if (value instanceof ComponentType) {
                    label.setText(
                            typeLabel((ComponentType) value));
                }

                label.setComponentOrientation(
                        ComponentOrientation.RIGHT_TO_LEFT);

                return label;
            }
        });

        componentList.setCellRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean selected,
                    boolean focused) {

                JLabel label = (JLabel)
                        super.getListCellRendererComponent(
                                list, value, index, selected, focused);

                if (value instanceof ChapterComponent) {
                    ChapterComponent item = (ChapterComponent) value;

                    ComponentType type = ComponentType.valueOf(
                            item.getComponentType());

                    label.setText(
                            item.getDisplayOrder() + "  |  "
                            + typeLabel(type));
                }

                label.setComponentOrientation(
                        ComponentOrientation.RIGHT_TO_LEFT);

                return label;
            }
        });
    }

    private String typeLabel(ComponentType typename) {
        switch (typename) {
            case LEARNING_OBJECTIVES:
                return "سیکھنے کے مقاصد";
            case PERSIAN_DIALOGUE:
                return "فارسی مکالمہ";
            case PERSIAN_READING_PASSAGE:
                return "فارسی عبارت";
            case URDU_TRANSLATION_OR_EXPLANATION:
                return "اردو ترجمہ یا وضاحت";
            case GRAMMAR_OR_LANGUAGE_NOTE:
                return "گرامر یا زبان کا نوٹ";
            case EXAMPLES:
                return "مثالیں";
            case LESSON_TEXT:
            	return "سبق کا متن";
            case EXERCISES:
            	return "مشق";
            default:
                return typename.name();
        }
    }

    /**
     * Reloads the chapter dropdown while preserving the selected
     * chapter by ID. This method does not reload the component list
     * when the selected chapter still exists.
     */
    
    // chapters reload krna or load krnay k liye
    private void loadChapters() {
        int selectedChapterId = -1;
        
        if (chapterComboBox.getSelectedIndex() > 0) {
            try {
                selectedChapterId = getSelectedChapterId();
            } catch (IllegalArgumentException e) {
                selectedChapterId = -1;
            }
        }

        try {
            // Fetch first, so a database error does not clear the list.
            List<Chapter> chapters = chapterBO.listChapters();

            loadingChapters = true;

            chapterComboBox.removeAllItems();
            chapterComboBox.addItem("باب منتخب کریں");

            int selectedIndex = 0;

            for (Chapter chapter : chapters) {
                chapterComboBox.addItem(
                        chapter.getTitle() + "  |  " + chapter.getId());

                if (chapter.getId() == selectedChapterId) {
                    selectedIndex = chapterComboBox.getItemCount() - 1;
                }
            }

            chapterComboBox.setSelectedIndex(selectedIndex);

            // The previously selected chapter no longer exists.
            if (selectedChapterId > 0 && selectedIndex == 0) {
                componentModel.clear();
                componentList.clearSelection();
                clearFields();
            }

        } catch (RuntimeException e) {
            showError("ابواب کی فہرست نہیں کھل سکی۔");
        } finally {
            loadingChapters = false;
            updateActionButtons();
        }
    }

    /**
     * Receives notifications from the shared LearningEventSource.
     */
    // oberveable view ko notify/update krnay k liye
    @Override
    public void update(Observable subject) {
        if (!(subject instanceof LearningEventSource)) {
            return;
        }

        if (SwingUtilities.isEventDispatchThread()) {
            loadChapters();
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    loadChapters();
                }
            });
        }
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 15));
        mainPanel.setComponentOrientation(
                ComponentOrientation.RIGHT_TO_LEFT);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setComponentOrientation(
                ComponentOrientation.RIGHT_TO_LEFT);

        inputPanel.setBorder(BorderFactory.createTitledBorder(
                "نئے جزو کی تفصیلات"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // Label on the right, input on its left.
        addInput(inputPanel, gbc, 0,
                "باب منتخب کریں:", chapterComboBox);

        addInput(inputPanel, gbc, 1,
                "باب کا جزو:", componentTypeComboBox);

        addInput(inputPanel, gbc, 2,
                "ترتیبِ نمائش:", displayOrderField);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        JPanel listPanel = new JPanel(new BorderLayout(5, 5));
        listPanel.setComponentOrientation(
                ComponentOrientation.RIGHT_TO_LEFT);

        listPanel.setBorder(BorderFactory.createTitledBorder(
                "باب کے اجزاء کی فہرست"));

        JLabel listHeading = new JLabel(
                "شامل کیے گئے اجزاء", JLabel.CENTER);
        listHeading.setComponentOrientation(
                ComponentOrientation.RIGHT_TO_LEFT);

        listPanel.add(listHeading, BorderLayout.NORTH);
        listPanel.add(new JScrollPane(componentList), BorderLayout.CENTER);

        mainPanel.add(listPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    /**
     * NOTE: The panel is RIGHT_TO_LEFT, so GridBagLayout mirrors the
     * columns: gridx = 0 is the RIGHT-most column and gridx = 1 is to
     * its left. Hence label -> gridx 0 (right), input -> gridx 1 (left).
     */
    private void addInput(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String labelText,
            Component input) {

        JLabel label = new JLabel(labelText, JLabel.RIGHT);
        label.setHorizontalAlignment(JLabel.RIGHT);
        label.setComponentOrientation(
                ComponentOrientation.RIGHT_TO_LEFT);

        input.setComponentOrientation(
                ComponentOrientation.RIGHT_TO_LEFT);

        gbc.gridy = row;
        gbc.weighty = 0;

        // Right column: label (fixed-ish width).
        gbc.gridx = 0;
        gbc.weightx = 0.30;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(label, gbc);

        // Left column: dropdown or input field (takes remaining width).
        gbc.gridx = 1;
        gbc.weightx = 0.70;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(input, gbc);
    }

    private JPanel createButtons() {
        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.CENTER));

        addButton.addActionListener(event -> addComponent());
        deleteButton.addActionListener(event -> deleteComponent());
        saveButton.addActionListener(event -> saveStructure());
        refreshButton.addActionListener(event -> loadSelectedStructure());
        clearButton.addActionListener(event -> clearFields());

        buttons.add(addButton);
        buttons.add(deleteButton);
        buttons.add(saveButton);
        buttons.add(refreshButton);
        buttons.add(clearButton);

        return buttons;
    }

    private int getSelectedChapterId() {
        if (chapterComboBox.getSelectedIndex() <= 0) {
            throw new IllegalArgumentException(
                    "پہلے ایک باب منتخب کریں۔");
        }

        String selected =
                (String) chapterComboBox.getSelectedItem();

        int separator = selected.lastIndexOf('|');

        if (separator < 0) {
            throw new IllegalArgumentException(
                    "منتخب باب درست نہیں ہے۔");
        }

        return Integer.parseInt(
                selected.substring(separator + 1).trim());
    }

    private void loadSelectedStructure() {
        componentModel.clear();

        if (chapterComboBox.getSelectedIndex() <= 0) {
            updateActionButtons();
            return;
        }

        try {
            int chapterId = getSelectedChapterId();

            List<ChapterComponent> components =
                    structureBO.getStructure(chapterId);

            for (ChapterComponent component : components) {
                componentModel.addElement(component);
            }

        } catch (RuntimeException e) {
            showError(
                    "باب کی ساخت لوڈ نہیں ہو سکی: " + e.getMessage());
        } finally {
            updateActionButtons();
        }
    }

    private void addComponent() {
        try {
            int chapterId = getSelectedChapterId();

            ComponentType type =
                    (ComponentType) componentTypeComboBox.getSelectedItem();

            if (type == null) {
                showError("باب کا جزو منتخب کریں۔");
                return;
            }

            int order;

            try {
                order = Integer.parseInt(
                        displayOrderField.getText().trim());
            } catch (NumberFormatException e) {
                showError("ترتیبِ نمائش میں درست عدد درج کریں۔");
                return;
            }

            if (order <= 0) {
                showError(
                        "ترتیبِ نمائش مثبت عدد ہونی چاہیے۔");
                return;
            }

            for (int i = 0; i < componentModel.size(); i++) {
                ChapterComponent current = componentModel.get(i);

                if (current.getComponentType().equals(type.name())) {
                    showError("یہ جزو پہلے ہی شامل ہے۔");
                    return;
                }

                if (current.getDisplayOrder() == order) {
                    showError("یہ ترتیب پہلے ہی استعمال ہو رہی ہے۔");
                    return;
                }
            }

            componentModel.addElement(new ChapterComponent(
                    0, chapterId, type.name(), order));

            sortComponents();
            clearFields();

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } finally {
            updateActionButtons();
        }
    }

    private void deleteComponent() {
        int selectedIndex = componentList.getSelectedIndex();

        if (selectedIndex < 0) {
            updateActionButtons();
            return;
        }

        componentModel.remove(selectedIndex);
        sortComponents();
        updateActionButtons();
    }

    private void sortComponents() {
        List<ChapterComponent> sorted =
                new ArrayList<ChapterComponent>();

        for (int i = 0; i < componentModel.size(); i++) {
            sorted.add(componentModel.get(i));
        }

        sorted.sort(Comparator.comparingInt(
                ChapterComponent::getDisplayOrder));

        componentModel.clear();

        for (ChapterComponent component : sorted) {
            componentModel.addElement(component);
        }

        updateActionButtons();
    }

    private void saveStructure() {
        try {
            int chapterId = getSelectedChapterId();

            List<ChapterComponent> components =
                    new ArrayList<ChapterComponent>();

            for (int i = 0; i < componentModel.size(); i++) {
                components.add(componentModel.get(i));
            }

            structureBO.saveStructure(chapterId, components);
            loadSelectedStructure();

            JOptionPane.showMessageDialog(
                    this,
                    "باب کی ساخت کامیابی سے محفوظ ہو گئی۔",
                    "کامیابی",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (RuntimeException e) {
            showError(
                    "ساخت محفوظ نہیں ہو سکی: " + e.getMessage());
        } finally {
            updateActionButtons();
        }
    }

    private void clearFields() {
        displayOrderField.setText("");
        componentTypeComboBox.setSelectedIndex(0);
        displayOrderField.requestFocusInWindow();

        updateActionButtons();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "خرابی",
                JOptionPane.ERROR_MESSAGE);
    }
}