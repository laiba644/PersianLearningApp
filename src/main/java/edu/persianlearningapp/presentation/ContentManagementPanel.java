package edu.persianlearningapp.presentation;

import edu.persianlearningapp.business.ContentBO;
import edu.persianlearningapp.business.ChapterBO;
import edu.persianlearningapp.domain.ChapterComponent;
import edu.persianlearningapp.domain.ComponentContent;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.GridLayout;//components ko top bottom left krt hai 
import javax.swing.JButton;
import javax.swing.JComboBox;//chp or com select krnay ka dropdown
import javax.swing.JLabel;
import javax.swing.JPanel;//gui container
import javax.swing.JScrollPane;//text ziada ho to scroll allow krta hai
import javax.swing.JTextArea;
import edu.persianlearningapp.domain.Chapter;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JOptionPane;


import edu.persianlearningapp.observer.LearningEventSource;
import edu.persianlearningapp.observer.Observable;
import edu.persianlearningapp.observer.Observer;
import javax.swing.SwingUtilities;
public class ContentManagementPanel extends JPanel implements Observer{
    private final ContentBO contentBO;
    private final ChapterBO chapterBO;
    private final JComboBox<String> chapterComboBox = new JComboBox<>();
    private final JComboBox<String> componentComboBox = new JComboBox<>();
    private final JTextArea persianArea = new JTextArea(5, 30);
    private final JTextArea urduArea = new JTextArea(5, 30);
    private final JButton saveButton = new JButton("محفوظ کرنا");
    private final JButton deleteButton = new JButton("مواد ختم کریں");
    private final JButton refreshButton = new JButton("تازہ کریں");
    
    private final List<Chapter> chapters = new ArrayList<>();
    private final List<ChapterComponent> components = new ArrayList<>();
    private boolean loadingChapters = false;
    
    public ContentManagementPanel(ContentBO contentBO, ChapterBO chapterBO,LearningEventSource eventSource) {
        this.contentBO = contentBO;
        this.chapterBO = chapterBO;
        setLayout(new BorderLayout(10, 10));

        //chapter component selection
        JPanel selectionPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        selectionPanel.add(new JLabel("باب منتخب کریں"));
        selectionPanel.add(chapterComboBox);
        selectionPanel.add(new JLabel("حصہ منتخب کریں"));
        selectionPanel.add(componentComboBox);
        add(selectionPanel, BorderLayout.NORTH);
        //persian ya urdu kay content area
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        persianArea.setLineWrap(true);
        persianArea.setWrapStyleWord(true);
        persianArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        urduArea.setLineWrap(true);
        urduArea.setWrapStyleWord(true);
        urduArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JPanel persianPanel = new JPanel(new BorderLayout());
        persianPanel.add(new JLabel("فارسی مواد"), BorderLayout.NORTH);
        persianPanel.add(new JScrollPane(persianArea), BorderLayout.CENTER);
        
        JPanel urduPanel = new JPanel(new BorderLayout());
        urduPanel.add(new JLabel("اردو مواد"), BorderLayout.NORTH);
        urduPanel.add(new JScrollPane(urduArea), BorderLayout.CENTER);
        textPanel.add(persianPanel);
        textPanel.add(urduPanel);
        add(textPanel, BorderLayout.CENTER);

        //buttonss
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
        refreshButton.addActionListener(e -> loadChapters());
        saveButton.addActionListener(e -> saveContent());
        deleteButton.addActionListener(e -> deleteContent());
        chapterComboBox.addActionListener(e -> {
            if (!loadingChapters) {
                loadComponents();
            }
        });
        componentComboBox.addActionListener(e -> loadContent());
        loadChapters();
        applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        eventSource.addObserver(this);
    }

	private void loadChapters() {
		loadingChapters = true;
	    chapterComboBox.removeAllItems();
	    componentComboBox.removeAllItems();
	    chapters.clear();
	    persianArea.setText("");
	    urduArea.setText("");
	    try {
	        List<Chapter> loadedChapters = chapterBO.listChapters();
	
	        for (Chapter chapter : loadedChapters) {
	
	            chapters.add(chapter);
	
	            chapterComboBox.addItem(
	                chapter.getId() + " - " + chapter.getTitle()
	            );
	        }
	
	    } catch (RuntimeException e) {
	        JOptionPane.showMessageDialog(
	            this,
	            "Could not load chapters. Check database connection.",
	            "Error",
	            JOptionPane.ERROR_MESSAGE
	        );
	    }
	    loadingChapters = false;
	    loadComponents();
	}

	private void loadComponents() {	
	    componentComboBox.removeAllItems();
	    components.clear();
	    persianArea.setText("");
	    urduArea.setText("");
	    int index = chapterComboBox.getSelectedIndex();
	    if (index < 0 || index >= chapters.size()) {
	        return;
	    }
	    int chapterId = chapters.get(index).getId();
	    try {
	        List<ChapterComponent> loadedComponents =
	                contentBO.listConfiguredComponents(chapterId);
	        for (ChapterComponent component : loadedComponents) {
	            components.add(component);
	            componentComboBox.addItem(
	                component.getDisplayOrder() + " - "
	                + componentNameInUrdu(component.getComponentType())
	            );
	        }
	    } catch (RuntimeException e) {
	        JOptionPane.showMessageDialog(
	            this,
	            "Could not load components: " + e.getMessage(),
	            "Error",
	            JOptionPane.ERROR_MESSAGE
	        );
	    }
	}

	private void loadContent() {
	
	    persianArea.setText("");
	    urduArea.setText("");
	    int chapterIndex = chapterComboBox.getSelectedIndex();
	    int componentIndex = componentComboBox.getSelectedIndex();
	    if (chapterIndex < 0 || componentIndex < 0) {
	        return;
	    }
	    if (chapterIndex >= chapters.size() ||
	        componentIndex >= components.size()) {
	        return;
	    }
	    int chapterId = chapters.get(chapterIndex).getId();
	    int componentId = components.get(componentIndex).getId();
	    try {
	        ComponentContent content =
	                contentBO.getContent(chapterId, componentId);
	
	        if (content != null) {
	
	            persianArea.setText(content.getPersianContent());
	            urduArea.setText(content.getUrduContent());
	        }	
	    } catch (RuntimeException e) {
	
	        JOptionPane.showMessageDialog(
	            this,"	مواد لوڈ نہیں ہو سک" + e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE
	        );
	    }
	}


	private void saveContent() {
	
	    int chapterIndex = chapterComboBox.getSelectedIndex();
	    int componentIndex = componentComboBox.getSelectedIndex();
	
	    if (chapterIndex < 0 || componentIndex < 0) {
	        JOptionPane.showMessageDialog(this, "برائے مہربانی ایک باب اور جزو منتخب کریں۔");
	        return;
	    }
	
	    int chapterId = chapters.get(chapterIndex).getId();
	    int componentId = components.get(componentIndex).getId();
	
	    String persian = persianArea.getText();
	    String urdu = urduArea.getText();
	
	    try {
	        contentBO.saveContent(chapterId, componentId, persian, urdu);
	
	        JOptionPane.showMessageDialog(this, "مواد کامیابی سے محفوظ ہو گیا۔");
	
	        loadContent();
	
	    } catch (RuntimeException e) {
	        JOptionPane.showMessageDialog(
	            this,
	            "مواد محفوظ نہیں ہو سکا" + e.getMessage(),
	            "Error",
	            JOptionPane.ERROR_MESSAGE
	        );
	    }
	}

	private void deleteContent() {	
	    int chapterIndex = chapterComboBox.getSelectedIndex();
	    int componentIndex = componentComboBox.getSelectedIndex();
	    if (chapterIndex < 0 || componentIndex < 0) {
	        JOptionPane.showMessageDialog(this, "	برائے مہربانی ایک باب اور جزو منتخب کریں۔");
	        return;
	    }
	    int chapterId = chapters.get(chapterIndex).getId();
	    int componentId = components.get(componentIndex).getId();
	    int choice = JOptionPane.showConfirmDialog(
	        this,"Are you sure you want to delete this content?","	ڈیلیٹ کرنے کی تصدیق کریں",JOptionPane.YES_NO_OPTION);
	    if (choice != JOptionPane.YES_OPTION) {
	        return;
	    }
	    try {
	        contentBO.removeContent(chapterId, componentId);
	        JOptionPane.showMessageDialog(this, "مواد کامیابی سے ڈیلیٹ ہو گیا۔");
	        loadContent();
	    } catch (RuntimeException e) {
	        JOptionPane.showMessageDialog(
	            this,
	            "مواد ڈیلیٹ نہیں ہو سک" + e.getMessage(),
	            "Error",
	            JOptionPane.ERROR_MESSAGE
	        );
	    }
	}
    public ContentBO getContentBO() {
        return contentBO;
    }
    private String componentNameInUrdu(String type) {

        switch (type) {
            case "LEARNING_OBJECTIVES":
                return "سیکھنے کے مقاصد";

            case "PERSIAN_DIALOGUE":
                return "فارسی مکالمہ";

            case "PERSIAN_READING_PASSAGE":
                return "فارسی پڑھنے کا متن";

            case "URDU_TRANSLATION_OR_EXPLANATION":
                return "اردو ترجمہ یا وضاحت";

            case "GRAMMAR_OR_LANGUAGE_NOTE":
                return "قواعد اور زبان کے نکات";

            case "EXAMPLES":
                return "مثالیں";

            case "EXERCISES":
                return "مشقیں";

            case "GLOSSARY":
                return "فرہنگ";

            default:
                return type;
        }
    }

	@Override
	public void update(Observable subject) {
	
	    if (!(subject instanceof LearningEventSource)) {
	        return;
	    }
	
	    if (SwingUtilities.isEventDispatchThread()) {
	        refreshChaptersFromEvent();
	    } else {
	        SwingUtilities.invokeLater(() -> refreshChaptersFromEvent());
	    }
	}

	private void refreshChaptersFromEvent() {
	
	    int oldIndex = chapterComboBox.getSelectedIndex();
	    int oldChapterId = -1;
	
	    if (oldIndex >= 0 && oldIndex < chapters.size()) {
	        oldChapterId = chapters.get(oldIndex).getId();
	    }
	
	    try {
	        List<Chapter> latestChapters = chapterBO.listChapters();
	
	        loadingChapters = true;
	
	        chapterComboBox.removeAllItems();
	        chapters.clear();
	
	        int newIndex = -1;
	
	        for (Chapter chapter : latestChapters) {
	
	            if (chapter.getId() == oldChapterId) {
	                newIndex = chapters.size();
	            }
	
	            chapters.add(chapter);
	            chapterComboBox.addItem(
	                chapter.getId() + " - " + chapter.getTitle()
	            );
	        }
	
	        chapterComboBox.setSelectedIndex(newIndex);
	
	    } catch (RuntimeException e) {
	        JOptionPane.showMessageDialog(this,
	            "فصل‌ها بارگذاری نشدند: " + e.getMessage());
	        return;
	
	    } finally {
	        loadingChapters = false;
	    }
	
	    if (oldChapterId > 0 &&
	        chapterComboBox.getSelectedIndex() == -1) {
	        loadComponents();
	    }
	}



}
