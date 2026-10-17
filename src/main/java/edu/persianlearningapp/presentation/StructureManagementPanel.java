
package edu.persianlearningapp.presentation;

import edu.persianlearningapp.business.ChapterBO;
import edu.persianlearningapp.business.StructureBO;
import edu.persianlearningapp.domain.Chapter;
import edu.persianlearningapp.domain.ChapterComponent;
import edu.persianlearningapp.domain.ComponentType;

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

public class StructureManagementPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final StructureBO structureBO;
    private final ChapterBO chapterBO;

    private final JComboBox<String> chapterComboBox =
            new JComboBox<String>();

    private final JComboBox<ComponentType> componentTypeComboBox =
            new JComboBox<ComponentType>(ComponentType.values());

    private final JTextField displayOrderField = new JTextField(10);

    private final DefaultListModel<ChapterComponent> componentModel =
            new DefaultListModel<ChapterComponent>();

    private final JList<ChapterComponent> componentList =
            new JList<ChapterComponent>(componentModel);

    private boolean loadingChapters;

    public StructureManagementPanel(
            StructureBO structureBO,
            ChapterBO chapterBO) {

        this.structureBO = structureBO;
        this.chapterBO = chapterBO;

        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel(
                "ابواب کے اجزاء کا انتظام", JLabel.CENTER);
        add(heading, BorderLayout.NORTH);

        configureRenderers();
        loadChapters();

        chapterComboBox.addActionListener(event -> {
            if (!loadingChapters) {
                loadSelectedStructure();
            }
        });

        add(createMainPanel(), BorderLayout.CENTER);
        add(createButtons(), BorderLayout.SOUTH);

        applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        loadSelectedStructure();
    }

    private void configureRenderers() {
        componentTypeComboBox.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean selected, boolean focused) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, selected, focused);

                if (value instanceof ComponentType) {
                    label.setText(typeLabel((ComponentType) value));
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
                    JList<?> list, Object value, int index,
                    boolean selected, boolean focused) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, selected, focused);

                if (value instanceof ChapterComponent) {
                    ChapterComponent item = (ChapterComponent) value;
                    ComponentType type =
                            ComponentType.valueOf(item.getComponentType());

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

    private String typeLabel(ComponentType type) {
        switch (type) {
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
            case EXERCISES:
                return "مشقیں";
            case GLOSSARY:
                return "لغت";
            default:
                return type.name();
        }
    }

    private void loadChapters() {
        loadingChapters = true;
        chapterComboBox.removeAllItems();
        chapterComboBox.addItem("باب منتخب کریں");

        try {
            List<Chapter> chapters = chapterBO.listChapters();

            for (Chapter chapter : chapters) {
                chapterComboBox.addItem(
                        chapter.getTitle() + "  |  " + chapter.getId());
            }
        } catch (RuntimeException e) {
            showError("ابواب کی فہرست نہیں کھل سکی۔ ڈیٹا بیس چیک کریں۔");
        } finally {
            loadingChapters = false;
        }
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 15));
        JPanel inputPanel = new JPanel(new GridBagLayout());

        inputPanel.setBorder(BorderFactory.createTitledBorder(
                "نئے جزو کی تفصیلات"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_END;

        addInput(inputPanel, gbc, 0,
                "باب منتخب کریں:", chapterComboBox);
        addInput(inputPanel, gbc, 1,
                "باب کا جزو:", componentTypeComboBox);
        addInput(inputPanel, gbc, 2,
                "ترتیبِ نمائش:", displayOrderField);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        JPanel listPanel = new JPanel(new BorderLayout(5, 5));
        listPanel.setBorder(BorderFactory.createTitledBorder(
                "باب کے اجزاء کی فہرست"));

        listPanel.add(
                new JLabel("شامل کیے گئے اجزاء", JLabel.CENTER),
                BorderLayout.NORTH);
        listPanel.add(new JScrollPane(componentList), BorderLayout.CENTER);

        mainPanel.add(listPanel, BorderLayout.CENTER);
        return mainPanel;
    }

    private void addInput(
            JPanel panel, GridBagConstraints gbc, int row,
            String labelText, Component input) {

        gbc.gridy = row;
        gbc.gridx = 1;
        gbc.weightx = 0;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 0;
        gbc.weightx = 1;
        panel.add(input, gbc);
    }

    private JPanel createButtons() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton addButton = new JButton("جزو شامل کریں");
        JButton deleteButton = new JButton("جزو حذف کریں");
        JButton saveButton = new JButton("ساخت محفوظ کریں");
        JButton refreshButton = new JButton("تازہ کریں");
        JButton clearButton = new JButton("خانے صاف کریں");

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
            throw new IllegalArgumentException("پہلے ایک باب منتخب کریں۔");
        }

        String selected = (String) chapterComboBox.getSelectedItem();
        int separator = selected.lastIndexOf('|');

        if (separator < 0) {
            throw new IllegalArgumentException("منتخب باب درست نہیں ہے۔");
        }

        return Integer.parseInt(selected.substring(separator + 1).trim());
    }

    private void loadSelectedStructure() {
        componentModel.clear();

        if (chapterComboBox.getSelectedIndex() <= 0) {
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
            showError("باب کی ساخت لوڈ نہیں ہو سکی: " + e.getMessage());
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
                order = Integer.parseInt(displayOrderField.getText().trim());
            } catch (NumberFormatException e) {
                showError("ترتیبِ نمائش میں درست عدد درج کریں۔");
                return;
            }

            if (order <= 0) {
                showError("ترتیبِ نمائش مثبت عدد ہونی چاہیے۔");
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
        }
    }

    private void deleteComponent() {
        int selectedIndex = componentList.getSelectedIndex();

        if (selectedIndex < 0) {
            showError("پہلے فہرست سے ایک جزو منتخب کریں۔");
            return;
        }

        componentModel.remove(selectedIndex);
        sortComponents();
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
            showError("ساخت محفوظ نہیں ہو سکی: " + e.getMessage());
        }
    }

    private void clearFields() {
        displayOrderField.setText("");
        componentTypeComboBox.setSelectedIndex(0);
        displayOrderField.requestFocusInWindow();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this, message, "خرابی", JOptionPane.ERROR_MESSAGE);
    }
}
